package com.laomachaguan.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/6/16 20:05
 * 公司：成都因陀罗网络科技有限公司
 */

public class Store_Setting extends AppCompatActivity implements View.OnClickListener {
    private ImageView back;
    private EditText edt_money,edt_address;
    private TextView commit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yaoyue_setting);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        back= (ImageView) findViewById(R.id.title_back);
        back.setOnClickListener(this);
        edt_money= (EditText) findViewById(R.id.edt_money);
        edt_address= (EditText) findViewById(R.id.edt_place);
        commit= (TextView) findViewById(R.id.tv_commit);
        commit.setOnClickListener(this);
        getSetting();
    }

    private void getSetting() {
        if(Network.HttpTest(this)){
            JSONObject js=new JSONObject();
            try {
                js.put("m_id",Constants.M_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m=ApisSeUtil.i(js);
            OkHttpUtils.post(Constants.getYueSetting)
                    .params("key",m.K())
                    .params("msg",m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String,String> map=AnalyticalJSON.getHashMap(s);
                            if(map!=null){
                                edt_address.setText(map.get("address"));
                                edt_money.setText(map.get("money"));
                            }
                        }


                    });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.tv_commit:

                if (edt_money.getText().toString().startsWith(".") || edt_money.getText().toString().endsWith(".") ||
                        (edt_money.getText().toString().startsWith("0") && !edt_money.getText().toString().contains("."))
                        || (edt_money.getText().toString().equals("0.0"))
                        || (edt_money.getText().toString().equals("0.00"))
                        ) {
                    ToastUtil.showToastShort("请输入正确格式的押金");
                    return;
                }
                if(edt_money.getText().toString().trim().contains(".")){
                    int lastnum=edt_money.getText().toString().trim().substring(edt_money.getText().toString().trim().lastIndexOf("."))
                            .length()-1;
                    LogUtil.e("小数点后位数:"+lastnum);
                    if(lastnum>2){
                        ToastUtil.showToastShort("押金小数点不能超过两位");
                        return;
                    }
                }
                if(edt_address.getText().toString().trim().equals("")){
                    ToastUtil.showToastShort("请输入邀约地址");
                    return;
                }
                if(Network.HttpTest(this)){
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id",Constants.M_id);
                        js.put("address",edt_address.getText().toString().trim());
                        js.put("money",edt_money.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    OkHttpUtils.post(Constants.Pact_Update)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute(new StringCallback() {


                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    ProgressUtil.show(Store_Setting.this,"","正在提交");
                                }

                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String ,String> map= AnalyticalJSON.getHashMap(s);
                                    if(map!=null){
                                        if("000".equals(map.get("code"))){
                                            finish();
                                            ToastUtil.showToastShort("邀约信息修改成功");
                                        }
                                    }
                                }

                                @Override
                                public void onAfter(@Nullable String s, @Nullable Exception e) {
                                    super.onAfter(s, e);
                                    ProgressUtil.dismiss();
                                }


                            });
                }
                break;
        }
    }
}
