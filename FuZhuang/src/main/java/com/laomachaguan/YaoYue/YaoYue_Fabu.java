package com.laomachaguan.YaoYue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/6/13 15:57
 * 公司：成都因陀罗网络科技有限公司
 */

public class YaoYue_Fabu extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_xuqiu, edt_peolle, edt_time, edt_content, edt_money, edt_place;
    private TextView tv_commit;
    private Date currentDate;
    private String money, address;
    private boolean canCommit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.yue);
        ImageView back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        ((TextView) findViewById(R.id.title_title)).setText("发布邀约");
        initView();
        getSetting();
    }


    private void initView() {
        edt_content = (EditText) findViewById(R.id.edt_content);
        edt_money = (EditText) findViewById(R.id.edt_money);
        edt_peolle = (EditText) findViewById(R.id.edt_people_num);
        edt_place = (EditText) findViewById(R.id.edt_place);
        edt_time = (EditText) findViewById(R.id.edt_time);
        edt_xuqiu = (EditText) findViewById(R.id.edt_xuqiu);

        tv_commit = (TextView) findViewById(R.id.tv_commit);
        tv_commit.setOnClickListener(this);
        edt_time.setOnClickListener(this);
        edt_time.setFocusable(false);

        edt_content.setText(PreferenceUtil.getYaoYueIncetance(this).getString("contents", ""));
        edt_money.setText(PreferenceUtil.getYaoYueIncetance(this).getString("money", ""));
        edt_time.setText(PreferenceUtil.getYaoYueIncetance(this).getString("end_time", ""));
        edt_peolle.setText(PreferenceUtil.getYaoYueIncetance(this).getString("num", ""));
        edt_xuqiu.setText(PreferenceUtil.getYaoYueIncetance(this).getString("title", ""));

        edt_place.setFocusable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.tv_commit:
                commitYaoYue();
                break;
            case R.id.edt_time:
                TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        long st=date.getTime();
                        if(st<System.currentTimeMillis()){
                            ToastUtil.showToastShort("有效期不能早于当前时间");
                        }else{
                            currentDate = date;
                            edt_time.setText(TimeUtils.getStrTime(String.valueOf(date.getTime())));
                        }

                    }
                })
                        .setTitleText("请选择时间")
                        .isCenterLabel(true)
                        .isCyclic(true)
                        .setContentSize(20)
                        .setLabel("", "月", "日", "时", "分", "秒")
                        .setLineSpacingMultiplier(1.5f)
                        .build();
                if (currentDate == null) {
                    pvTime.setDate(Calendar.getInstance());
                } else {
                    Calendar calender = Calendar.getInstance();
                    calender.setTime(currentDate);
                    pvTime.setDate(calender);
                }

                pvTime.show();
                break;
        }

    }

    private void getSetting() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("m_id", Constants.M_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            OkHttpUtils.post(Constants.getYueSetting)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                            if (map != null) {
                                address = map.get("address");
                                money = map.get("money");
                                edt_place.setText(address);
                                edt_money.setText(money);
                                edt_money.setHint("金额不能小于" + money + "元");
                                canCommit = true;
                            } else {
                                getSetting();
                            }
                        }


                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor ed = PreferenceUtil.getYaoYueIncetance(this).edit();
        ed.putString("title", edt_xuqiu.getText().toString().trim());
        ed.putString("num", edt_peolle.getText().toString().trim());
        ed.putString("contents", edt_content.getText().toString().trim());
//        ed.putString("money", edt_money.getText().toString().trim());
        ed.putString("end_time", edt_time.getText().toString().trim());
//        ed.putString("address", edt_place.getText().toString().trim());
        ed.apply();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    private void commitYaoYue() {//发布邀约
        if (!Network.HttpTest(this)) {
            return;
        }
        if (edt_content.getText().toString().trim().equals("")
                || edt_xuqiu.getText().toString().trim().equals("")
                || edt_money.getText().toString().trim().equals("")
                || edt_peolle.getText().toString().trim().equals("")
                || edt_time.getText().toString().trim().equals("")
                || edt_place.getText().toString().trim().equals("")
                ) {
            ToastUtil.showToastShort("请填写所有邀约信息");
            return;
        }
        if (edt_money.getText().toString().startsWith(".") || edt_money.getText().toString().endsWith(".") ||
                (edt_money.getText().toString().startsWith("0") && !edt_money.getText().toString().contains("."))
                || (edt_money.getText().toString().equals("0.0"))
                || (edt_money.getText().toString().equals("0.00"))
                ) {
            ToastUtil.showToastShort("请输入正确格式的押金");
            return;
        }
        if (edt_money.getText().toString().trim().contains(".")) {
            int lastnum = edt_money.getText().toString().trim().substring(edt_money.getText().toString().trim().lastIndexOf("."))
                    .length() - 1;
            LogUtil.e("小数点后位数:" + lastnum);
            if (lastnum > 2) {
                ToastUtil.showToastShort("押金小数点不能超过两位");
                return;
            }
        }
        if (Double.valueOf(edt_money.getText().toString().trim()) < Double.valueOf(money)) {
            ToastUtil.showToastShort("押金不能小于初始值");
            return;
        }
        if (!canCommit) {
            ToastUtil.showToastShort("数据获取失败，请稍后重试");
            finish();
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserIncetance(YaoYue_Fabu.this).getString("user_id", ""));
            js.put("title", edt_xuqiu.getText().toString().trim());
            js.put("num", edt_peolle.getText().toString().trim());
            js.put("contents", edt_content.getText().toString().trim());
            js.put("money", edt_money.getText().toString().trim());
            js.put("end_time", edt_time.getText().toString().trim());
            js.put("address", edt_place.getText().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.getYueFirst).tag(this)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {


                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(YaoYue_Fabu.this, "", "正在提交邀约信息");
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if ("000".equals(map.get("code"))) {
                                ToastUtil.showToastShort("邀约发布成功");
                                finish();
                                Intent inte
                                        = new Intent("yaoyue");
                                sendBroadcast(inte);
                            } else {
                                ToastUtil.showToastShort("邀约发布失败，请稍后重试");
                            }
                        } else {
                            ToastUtil.showToastShort("邀约发布失败，请稍后重试");
                        }
                    }

                    @Override
                    public void onAfter(@Nullable String s, @Nullable Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }


                });
    }
}
