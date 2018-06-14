package com.laomachaguan.Common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.Verification;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/6/21.
 */
public class FindPassword extends AppCompatActivity implements View.OnClickListener {
    private EditText phoneNum, newWord, word2, Mid;
    private TextView getMid;
    private Button button;
    private String YZM;
    private ProgressDialog progressDialog;
    private String code="86";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {//找回密码
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_wangjimima);
         StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        initView();

    }

    private void initView() {
        phoneNum = (EditText) findViewById(R.id.login_wangjimima_phonenum);
        newWord = (EditText) findViewById(R.id.login_wangjimima_password);
        word2 = (EditText) findViewById(R.id.login_wangjimima_password2);
        Mid = (EditText) findViewById(R.id.login_wangjimima_Mid);
        getMid = (TextView) findViewById(R.id.login_wangjimima_getMid);
        button = (Button) findViewById(R.id.login_wangjimima_submit);
        findViewById(R.id.findPassWord_back).setOnClickListener(this);
        button.setOnClickListener(this);
        getMid.setOnClickListener(this);
        TextView t= (TextView) findViewById(R.id.country_code);
        if(PreferenceUtil.getSettingIncetance(this).getString("country","").equals("")){
            t.setText(mApplication.ST("中国大陆  +86"));
        }else{
            code=PreferenceUtil.getUserIncetance(this).getString("code","");
            t.setText(mApplication.ST(PreferenceUtil.getSettingIncetance(this).getString("country","")+"   +"+PreferenceUtil.getSettingIncetance(this).getString("code","")));
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==111){
            code=data.getStringExtra("code");
            code=data.getStringExtra("code");
            String country=data.getStringExtra("country");
            ((TextView) findViewById(R.id.country_code)).setText(country+"   +"+code);
            PreferenceUtil.getSettingIncetance(this).edit().putString("country",country)
                    .putString("code",code).apply();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.country_code:
                Intent intent =new Intent(FindPassword.this, CountryCode.class);
                startActivityForResult(intent,000);
                break;
            case R.id.login_wangjimima_getMid:
//                if (!Verification.isMobileNO(phoneNum.getText().toString())) {
//                    Toast.makeText(FindPassword.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (phoneNum.getText().toString().equals("")) {
                    Toast.makeText(FindPassword.this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newWord.getText().toString().trim().equals(word2.getText().toString().trim())){
                    Toast.makeText(this, "两次密码输入不一致，请重新填写", Toast.LENGTH_SHORT).show();
                    newWord.setText("");
                    word2.setText("");
                    return;
                }
                //获取验证码
                v.setEnabled(false);
                getMid.setText(mApplication.ST("正在请求验证码"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            js.put("phone", phoneNum.getText().toString());
                            js.put("m_id", Constants.M_id);
                            js.put("region",code);
                            String data = OkHttpUtils.post(Constants.Mid_IP)
                                    .params("key", ApisSeUtil.getKey())
                                    .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                            if (!data.equals("")) {
                                HashMap<String ,String> map=AnalyticalJSON.getHashMap(data);
                                if(map!=null){
                                    switch (map.get("code")){
                                        case "000":
                                            timer.start();
                                            YZM = map.get("yzm");
                                            getMid.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    YZM="";
                                                    v.setEnabled(true);
                                                }
                                            },60000);
                                            break;
                                        case "222":
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showToastShort(mApplication.ST("验证码请求过于频繁"), Gravity.CENTER);
                                                    v.setEnabled(true);
                                                    getMid.setText(mApplication.ST("获取验证码"));
                                                }
                                            });

                                            break;
                                        case "333":
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    v.setEnabled(true);
                                                    getMid.setText(mApplication.ST("获取验证码"));
                                                    getMid.setTextColor(Color.WHITE);
                                                    ToastUtil.showToastShort("验证码请求超过上限", Gravity.CENTER);
                                                }
                                            });

                                            break;
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.login_wangjimima_submit:
                if (phoneNum.getText().toString().trim().equals("") ||
                        newWord.getText().toString().trim().equals("") ||
                        word2.getText().toString().trim().equals("") ||
                        Mid.getText().toString().trim().equals("")) {
                    Toast.makeText(FindPassword.this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Verification.isMobileNO(phoneNum.getText().toString().trim())) {
                    Toast.makeText(FindPassword.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (YZM == null || !YZM.equals(Mid.getText().toString().trim())) {
                    Toast.makeText(this, "验证码不匹配", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newWord.getText().toString().trim().equals(word2.getText().toString().trim())){
                    Toast.makeText(this, "两次密码输入不一致，请重新填写", Toast.LENGTH_SHORT).show();
                    newWord.setText("");
                    word2.setText("");
                    return;
                }
                //提交信息
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
                            js.put("phone", phoneNum.getText().toString());
                            js.put("m_id", Constants.M_id);
                            js.put("password", newWord.getText().toString());
                            String data = OkHttpUtils.post(Constants.WJMM_IP)
                                    .params("key", ApisSeUtil.getKey())
                                    .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                            if (!data.equals("")) {
                                if (AnalyticalJSON.getHashMap(data) != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(FindPassword.this, "密码更改成功", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            finish();

                                        }
                                    });
                                } else if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(FindPassword.this, "该手机尚未注册", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });

                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(FindPassword.this, "输入信息有误，请重新尝试", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(FindPassword.this, "密码更改失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.findPassWord_back:
                finish();
                break;
        }
    }
}
