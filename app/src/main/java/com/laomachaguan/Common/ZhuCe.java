package com.laomachaguan.Common;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.Verification;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Administrator on 2016/6/15.
 */
public class ZhuCe extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ZhuCe";
    private EditText phonenum, password, Mid,password2;
    private TextView getMid;
    private Button submit;
    private String YZM;
    public static ZhuCe instance;
    private ImageView headImage;
    private SharedPreferences sp;
    private ImageView back;
    private String code = "86";
    private boolean isAgreeed = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuce1);
        StatusBarCompat.compat(this, getResources().getColor(R.color.umeng_socialize_divider));
        instance = this;
        sp = getSharedPreferences("user", MODE_PRIVATE);
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.zhuce_back);
        back.setOnClickListener(this);
        headImage = (ImageView) findViewById(R.id.activity_register_imageview);
        phonenum = (EditText) findViewById(R.id.Zhuce_phonenum);
        password = (EditText) findViewById(R.id.Zhuce_password);
        Mid = (EditText) findViewById(R.id.Zhuce_Mid);
        password2 = (EditText) findViewById(R.id.Zhuce_password2);
        password2.setHint(mApplication.ST("确认密码/Password"));
        getMid = (TextView) findViewById(R.id.Zhuce_getMid);
        getMid.setOnClickListener(this);
        submit = (Button) findViewById(R.id.Zhuce_submit);
        submit.setOnClickListener(this);
        headImage.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.indra));
        (findViewById(R.id.zhuce_main)).setBackgroundDrawable(new GlideBitmapDrawable(getResources(), ImageUtil.readBitMap(this, R.drawable.backgd)));
        TextView t = (TextView) findViewById(R.id.country_code);
        if (PreferenceUtil.getSettingIncetance(this).getString("country", "").equals("")) {
            t.setText(mApplication.ST("中国大陆  +86"));
        } else {
            t.setText(mApplication.ST(PreferenceUtil.getSettingIncetance(this).getString("country", "") + "   +" + PreferenceUtil.getSettingIncetance(this).getString("code", "")));
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
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        OkHttpUtils.getInstance().cancelTag(TAG);
        (findViewById(R.id.zhuce_main)).setBackgroundDrawable(null);
    }

    Handler mZhuceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(ZhuCe.this, "注册成功", Toast.LENGTH_SHORT).show();

                    ProgressUtil.show(ZhuCe.this,"","正在登录...");

                ////////////////////////////////////首次登陆

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data1 = null;
                        try {
                            JSONObject js = new JSONObject();

                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("phone", phonenum.getText().toString());
                                js.put("password", password.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            data1 = OkHttpUtils.post(Constants.Login_Ip).params("key", ApisSeUtil.getKey())
                                    .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (data1 != null) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                            if (map != null && "000".equals(map.get("code"))) {
                                SharedPreferences usersp = getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor ed = usersp.edit();
                                ed.putString("user_id", map.get("user_id"));
                                ed.putString("phone", phonenum.getText().toString());
                                ed.putString("uid", phonenum.getText().toString());
                                ed.apply();
                                PushServiceFactory.getCloudPushService().addAlias(map.get("user_id")
                                        , new CommonCallback() {
                                            @Override
                                            public void onSuccess(String s) {
                                                LogUtil.e("别名绑定成功");
                                            }

                                            @Override
                                            public void onFailed(String s, String s1) {
                                                LogUtil.e("别名绑定失败"+s1);
                                            }
                                        });
                                getUserInfo();

                            } else {
                                mZhuceHandler.sendEmptyMessage(-2);
                            }
                        } else {
                            mZhuceHandler.sendEmptyMessage(-2);
                        }
                    }
                }).start();
            } else if (msg.what == 1) {
                Toast.makeText(ZhuCe.this, "用户信息提交失败", Toast.LENGTH_SHORT).show();
                ProgressUtil.dismiss();
            } else if (msg.what == -1) {
               ProgressUtil.dismiss();
                Intent intent = new Intent(ZhuCe.this, MainActivity.class);
                startActivity(intent);
                if (Login.instance != null) Login.instance.finish();

                Intent intent1 = new Intent("Mine");
                sendBroadcast(intent1);
                Intent intent2 = new Intent("car");
                sendBroadcast(intent2);
                Intent intent3 = new Intent("yaoyue");
                sendBroadcast(intent3);


                finish();

            } else if (msg.what == -2) {
               ProgressUtil.dismiss();
                Toast.makeText(ZhuCe.this, "登陆失败，请输入已注册的账户密码", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ZhuCe.this, Login.class);
                startActivity(intent);
                finish();
            }
        }
    };
    public void getUserInfo() {
        if (!new LoginUtil().checkLogin(this)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("user_id", sp.getString("user_id", ""));
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    data1 = OkHttpUtils.post(Constants.User_Info_Ip).params("key", m.K())
                            .params("msg", m.M()).execute()
                            .body().string();
                    LogUtil.e("个人信息数据为：" + data1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (data1 != null && !data1.equals("")) {
                    final HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences.Editor ed = sp.edit();
                            if (map != null && map.get("id") != null) {
                                if ("0".equals(map.get("agree_status"))) {
                                    isAgreeed = false;
                                    ed.putString("agree_status", "0");//是否同意隐私政策  0未同意  1为同意
                                    ed.commit();
                                } else {
                                    isAgreeed = true;
                                }
                            } else {
                                isAgreeed = true;
                            }
                            ProgressUtil.dismiss();
                            if (!isAgreeed) {
                                getProl();
                            } else {
                                mZhuceHandler.sendEmptyMessage(-1);
                            }
                        }
                    });
                } else {
                    mZhuceHandler.sendEmptyMessage(-1);
                }

            }
        }).start();
    }

    public void getProl() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.little_yhxy__IP).tag(TAG).params("key", m.K()).params("msg", m.M())
                .execute(new AbsCallback<Object>() {


                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        return null;
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        try {
                            String data = response.body().string();
                            if (!data.equals("")) {
                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (map != null) {
                                            View view = LayoutInflater.from(ZhuCe.this).inflate(R.layout.activity_confirm_dialog, null);
                                            final WebView web = (WebView) view.findViewById(R.id.web);
                                            TextView cancle = (TextView) view.findViewById(R.id.cancle);
                                            cancle.setText(mApplication.ST("不同意"));
                                            final TextView baoming = (TextView) view.findViewById(R.id.baoming);
                                            baoming.setEnabled(false);
                                            final CountDownTimer cdt = new CountDownTimer(10000, 1000) {
                                                @Override
                                                public void onTick(long millisUntilFinished) {
                                                    baoming.setText(mApplication.ST("请阅读隐私政策(" + millisUntilFinished / 1000 + "秒)"));
                                                }

                                                @Override
                                                public void onFinish() {
                                                    baoming.setText(mApplication.ST("同意"));
                                                    baoming.setEnabled(true);
                                                }
                                            };
                                            web.loadDataWithBaseURL("", map.get("privacy")
                                                    , "text/html", "UTF-8", null);
//
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ZhuCe.this);
                                            builder.setView(view);
                                            final AlertDialog dialog = builder.create();
                                            cancle.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    web.destroy();
                                                    cdt.cancel();
                                                    dialog.dismiss();
                                                    if(MainActivity.activity!=null){
                                                        MainActivity.activity.finish();
                                                        MainActivity.activity=null;
                                                    }
                                                    System.exit(0);
                                                }
                                            });
                                            baoming.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    agreeSecret();
                                                }
                                            });
                                            cdt.start();
                                            builder.setCancelable(false);
                                            web.setWebViewClient(new WebViewClient(){
                                                @Override
                                                public void onPageFinished(WebView view, String url) {
                                                    super.onPageFinished(view, url);
                                                    dialog.show();
                                                }
                                            });
                                        } else {
                                            mZhuceHandler.sendEmptyMessage(-1);
                                        }
                                    }
                                });
                            } else {
                                mZhuceHandler.sendEmptyMessage(-1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                });
    }

    //同意隐私政策
    private void agreeSecret() {
        if (Network.HttpTest(this)) {
            if (new LoginUtil().checkLogin(this)) {
                JSONObject js = new JSONObject();
                try {
                    js.put("user_id", PreferenceUtil.getUserId(this));
                    js.put("m_id", Constants.M_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m = ApisSeUtil.i(js);
                OkHttpUtils.post(Constants.yhxy_agree).tag(TAG)
                        .params("key", m.K())
                        .params("msg", m.M())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putString("agree_status", "0");//是否同意隐私政策  0未同意  1为同意
                                ed.commit();
                                mZhuceHandler.sendEmptyMessage(-1);
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(ZhuCe.this, "", "请稍等");
                            }

                            @Override
                            public void onAfter(String s, Exception e) {
                                super.onAfter(s, e);
                                ProgressUtil.dismiss();
                            }
                        });
            }

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent=new Intent(this,MainActivity.class);
//        startActivity(intent);
//        intent.putExtra("exit",true);
//        startActivity(intent);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 111) {
            code = data.getStringExtra("code");
            String country = data.getStringExtra("country");
            ((TextView) findViewById(R.id.country_code)).setText(country + "   +" + code);
            PreferenceUtil.getSettingIncetance(this).edit().putString("country", country)
                    .putString("code", code).apply();
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.country_code:
                Intent intent = new Intent(ZhuCe.this, CountryCode.class);
                startActivityForResult(intent, 000);
                break;
            case R.id.zhuce_back:
                onBackPressed();

                break;
            case R.id.Zhuce_getMid://获取验证码
                if (phonenum.getText().toString().equals("")) {
                    Toast.makeText(ZhuCe.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Verification.isMobileNO(phonenum.getText().toString())) {
                    Toast.makeText(ZhuCe.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
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

                            try {   js.put("phone", phonenum.getText().toString());
                                js.put("m_id", Constants.M_id);
                                js.put("region", code);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String data = OkHttpUtils.post(Constants.Mid_IP)

                                    .params("key",ApisSeUtil.getKey()).params("msg",ApisSeUtil.getMsg(js))
                                    .execute().body().string();
                            if (!data.equals("")) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null) {
                                    switch (map.get("code")) {
                                        case "000":
                                            timer.start();
                                            YZM = map.get("yzm");
                                            getMid.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    YZM = "";
                                                    v.setEnabled(true);
                                                }
                                            }, 60000);
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
                                                    getMid.setTextColor(Color.BLACK);
                                                    ToastUtil.showToastShort("验证码请求超过上限", Gravity.CENTER);
                                                }
                                            });

                                            break;
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
            case R.id.Zhuce_submit://///////提交注册
                if ("".equals(phonenum.getText().toString()) || "".equals(password.getText().toString()) || "".equals(Mid.getText().toString())) {
                    Toast.makeText(ZhuCe.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(password2.getText().toString().trim())) {
                    Toast.makeText(ZhuCe.this, mApplication.ST("请再次输入密码"), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (password2.getText().length() < 6) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("请输入6-16位的密码"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (password.getText() != null && password2.getText() != null) {
                    if (!password.getText().toString().equals(password2.getText().toString())) {
                        Toast.makeText(ZhuCe.this, mApplication.ST("两次密码输入不一致，请重新输入"), Toast.LENGTH_SHORT).show();
                        password.setText("");
                        password2.setText("");
                        return;
                    }
                }
                if (!Verification.isMobileNO(phonenum.getText().toString())) {
                    Toast.makeText(ZhuCe.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (YZM == null || !YZM.equals(Mid.getText().toString())) {
                    Toast.makeText(ZhuCe.this, "验证码不匹配", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProgressUtil.show(ZhuCe.this,"","正在提交用户信息");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data1 = null;
                        try {
                            JSONObject js=new JSONObject();
                            js.put("phone", phonenum.getText().toString());
                            js.put("password", password.getText().toString());
                            js.put("m_id", Constants.M_id);
                            data1 = OkHttpUtils.post(Constants.Regist_Ip).tag(TAG)
                                    .params("key", ApisSeUtil.getKey()).params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, "run: data1-------->" + data1);
                        if (data1 != null) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                            if (map != null) {
                                if ("000".equals(map.get("code"))) {
                                    mZhuceHandler.sendEmptyMessage(0);
                                } else if ("003".equals(map.get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ZhuCe.this, "用户名已注册", Toast.LENGTH_SHORT).show();
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                } else {
                                    mZhuceHandler.sendEmptyMessage(1);
                                }
                            }
                        } else {
                            mZhuceHandler.sendEmptyMessage(1);
                        }
                    }
                }).start();


                break;
        }
    }

}
