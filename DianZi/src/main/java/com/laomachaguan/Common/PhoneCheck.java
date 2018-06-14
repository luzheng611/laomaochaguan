package com.laomachaguan.Common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.Verification;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/13.
 */
public class PhoneCheck extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PhoneCheck";
    private ImageView back;
    private TextView getMid, commit;
    private EditText phone, MID,password;
    private String YZM;
    private ProgressDialog progressDialog;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.phone_check);
        initView();
    }

    private void initView() {
        sp = getSharedPreferences("user", MODE_PRIVATE);
        ((TextView) findViewById(R.id.title_title)).setText("手机号绑定");
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        getMid = (TextView) findViewById(R.id.Zhuce_getMid);
        commit = (TextView) findViewById(R.id.zhuce_commit);
        phone = (EditText) findViewById(R.id.Zhuce_phonenum);
        MID = (EditText) findViewById(R.id.Zhuce_Mid);
        getMid.setOnClickListener(this);
        commit.setOnClickListener(this);
        password= (EditText) findViewById(R.id.Zhuce_password);
    }

    CountDownTimer timer = new CountDownTimer(60000, 1000) {//验证码倒计时
        @Override
        public void onTick(long millisUntilFinished) {
            getMid.setText(millisUntilFinished / 1000 + "秒后可重新获取");
            getMid.setTextColor(Color.parseColor("#9b9b9b"));
            getMid.setEnabled(false);
        }

        @Override
        public void onFinish() {
            if (getMid != null) {
                getMid.setText("请重新发送");
                getMid.setTextColor(Color.parseColor("#000000"));
                getMid.setEnabled(true);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.zhuce_commit:
                if ("".equals(phone.getText().toString().trim()) || "".equals(MID.getText().toString().trim())||"".equals(password.getText().toString().trim())) {
                    Toast.makeText(mApplication.getInstance(), "请输入完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Verification.isMobileNO(phone.getText().toString().trim())) {
                    Toast.makeText(mApplication.getInstance(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (YZM == null || !YZM.equals(MID.getText().toString().trim())) {
                    Toast.makeText(mApplication.getInstance(), "验证码不匹配", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.isIndeterminate();
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("正在提交用户信息");
                }
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("id",sp.getString("old_id",""));
                                js.put("m_id", Constants.M_id);
                                js.put("phone", phone.getText().toString());
                                js.put("password",password.getText().toString().trim());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String data = OkHttpUtils.post(Constants.Phone_Commit_Ip)  .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                            if (!TextUtils.isEmpty(data)) {
                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null && "000".equals(map.get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(PhoneCheck.this, "信息提交成功", Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor ed = sp.edit();
                                            ed.putString("phone", phone.getText().toString().trim());
                                            ed.putString("user_id",map.get("user_id"));
                                            ed.apply();
                                            if (progressDialog != null && progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            Intent intent=new Intent("Mine");
                                            Intent intent2=new Intent("Mine_SC");
                                            sendBroadcast(intent);
                                            sendBroadcast(intent2);

                                            finish();
                                        }
                                    });
                                } else if (map != null && "003".equals(map.get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(PhoneCheck.this, "该手机已注册绑定，请更换手机号或找回密码", Toast.LENGTH_SHORT).show();
                                            YZM=null;
                                            if (progressDialog != null && progressDialog.isShowing())
                                                progressDialog.dismiss();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(PhoneCheck.this, "用户信息提交失败，请稍后尝试", Toast.LENGTH_SHORT).show();
                                            YZM=null;
                                            if (progressDialog != null && progressDialog.isShowing())
                                                progressDialog.dismiss();
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


                break;
            case R.id.Zhuce_getMid:
                if (phone.getText().toString().equals("")) {
                    Toast.makeText(mApplication.getInstance(), "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Verification.isMobileNO(phone.getText().toString())) {
                    Toast.makeText(mApplication.getInstance(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                timer.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String data1 = null;
                        try {
                            data1 = OkHttpUtils.post(Constants.Mid_IP).tag(TAG).params("phone", phone.getText().toString()).params("key", Constants.safeKey)
                                    .params("m_id",Constants.M_id)
                                    .execute().body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, "run: ------->" + data1);
                        if (data1 != null) {
                            final Map<String, String> map = AnalyticalJSON.getHashMap(data1);
                            if (map!=null&&"000".equals(map.get("code"))) {
                                YZM = map.get("yzm");
                            }
                        }
                    }
                }).start();
                break;
        }
    }
}
