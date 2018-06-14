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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.laomachaguan.R;
import com.laomachaguan.Utils.ACache;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
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
 * Created by Administrator on 2016/6/3.
 */
public class Login extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "Login";
//    private UMShareAPI mshareApi;
//    private LinearLayout weChatLogin;
//    private LinearLayout QQLogin;
//    private SHARE_MEDIA platform = null;
    private TextView zhuce;
    private EditText username, password;
    private Button Login;
//    private ImageView back;
    private TextView wangjimima;
    private String headurl;
    private int screenWidth;
    private ACache aCache;
    private SharedPreferences sp;
    public static Login instance;
    private boolean isAgreeed = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login1);
         StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        instance = this;
        initView();

    }

    private void initView() {
        Glide.with(this).load(R.drawable.indra).override(DimenUtils.dip2px(this,100),DimenUtils.dip2px(this,100)).into((ImageView) findViewById(R.id.head));
        aCache = ACache.get(this);
//        mshareApi = UMShareAPI.get(this);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
//        weChatLogin = (LinearLayout) findViewById(R.id.weChat);
//        weChatLogin.setOnClickListener(this);
//        QQLogin = (LinearLayout) findViewById(R.id.QQ);
//        QQLogin.setOnClickListener(this);
        zhuce = (TextView) findViewById(R.id.Login_to_zhuce);
        zhuce.setOnClickListener(this);
        username = (EditText) findViewById(R.id.userName_edt);
        password = (EditText) findViewById(R.id.passWord_edt);
        Login = (Button) findViewById(R.id.Login_login);
        Login.setOnClickListener(this);
//        back = (ImageView) findViewById(R.id.login_back);
//        back.setOnClickListener(this);
        wangjimima = (TextView) findViewById(R.id.Login_wangjimima);
        wangjimima.setOnClickListener(this);
        findViewById(R.id.login_bg).setBackgroundDrawable(new GlideBitmapDrawable(getResources(), ImageUtil.readBitMap(this,R.drawable.backgd)));
//        ((ImageView) findViewById(R.id.login_qq)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.logo_qq));
//        ((ImageView) findViewById(R.id.login_wx)).setImageBitmap(ImageUtil.readBitMap(this,R.drawable.logo_weixin));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.weChat:
//                platform = SHARE_MEDIA.WEIXIN;
//                mshareApi.doOauthVerify(this, platform, umAuthListener);
//                if (progressDialog == null) {
//                    progressDialog = new ProgressDialog(Login.this);
//                    progressDialog.isIndeterminate();
//                    progressDialog.setCanceledOnTouchOutside(false);
//                    progressDialog.setMessage("正在登录，请稍等");
//                    progressDialog.show();
//                }
//                break;
//            case R.id.QQ:
//                platform = SHARE_MEDIA.QQ;
//                mshareApi.doOauthVerify(this, platform, umAuthListener);
//                if (progressDialog == null) {
//                    progressDialog = new ProgressDialog(Login.this);
//                    progressDialog.isIndeterminate();
//                    progressDialog.setCanceledOnTouchOutside(false);
//                    progressDialog.setMessage("正在登录，请稍等");
//                    progressDialog.show();
//                }
//                break;
            case R.id.Login_wangjimima:
                Intent intent4 = new Intent(this, FindPassword.class);
                startActivity(intent4);
                break;
//            case R.id.login_back:
//                finish();
//                break;
            case R.id.Login_to_zhuce:
                Intent intent = new Intent(this, ZhuCe.class);
                startActivity(intent);
//                finish();
                break;
            case R.id.Login_login://登录

                if ("".equals(username.getText().toString().trim()) || "".equals(password.getText().toString().trim())) {
                    Toast.makeText(Login.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Verification.isMobileNO(username.getText().toString().trim())) {
                    Toast.makeText(Login.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProgressUtil.show(this,"","正在登录...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            JSONObject js=new JSONObject();
                            js.put("phone", username.getText().toString());
                            js.put("m_id", Constants.M_id);
                            js.put("password", password.getText().toString());
                            response = OkHttpUtils.post(Constants.Login_Ip)
                                    .tag(TAG)
                                    .params("key", ApisSeUtil.getKey())
                                    .params("msg",ApisSeUtil.getMsg(js)).execute();
                            String data1 = response.body().string();
                            if (!data1.equals("")) {
                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);//解析登录返回信息
                                if (map != null && "000".equals(map.get("code"))) {
                                    SharedPreferences usersp = getSharedPreferences("user", MODE_PRIVATE);
                                    SharedPreferences.Editor ed = usersp.edit();
                                    ed.putString("user_id", map.get("user_id"));//用户名
                                    ed.putString("uid", username.getText().toString());//用户uid
                                    ed.putString("phone", username.getText().toString());//电话号码
                                    ed.putString("status", map.get("status"));
                                    ed.putString("role",map.get("role"));
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

                                } else if ("003".equals(map.get("code"))) {
                                    mLoginHandler.sendEmptyMessage(-3);
                                } else {
                                    mLoginHandler.sendEmptyMessage(-2);
                                }
                            } else {
                                mLoginHandler.sendEmptyMessage(-2);
                            }

                        }catch (Exception e){
                            mLoginHandler.sendEmptyMessage(-2);
                        }


                    }
                }).start();


                break;
        }
    }
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
                                mLoginHandler.sendEmptyMessage(-1);
                            }
                        }
                    });
                } else {
                    mLoginHandler.sendEmptyMessage(-1);
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
                                            View view = LayoutInflater.from(Login.this).inflate(R.layout.activity_confirm_dialog, null);
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
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                            builder.setView(view);
                                            final AlertDialog dialog = builder.create();
                                            cancle.setOnClickListener(new OnClickListener() {
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
                                            baoming.setOnClickListener(new OnClickListener() {
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
                                            mLoginHandler.sendEmptyMessage(-1);
                                        }
                                    }
                                });
                            } else {
                                mLoginHandler.sendEmptyMessage(-1);
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
                                mLoginHandler.sendEmptyMessage(-1);
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(Login.this, "", "请稍等");
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
//    @Override
//    public void onBackPressed() {
//
//        Intent intent=new Intent(this,MainActivity.class);
//        startActivity(intent);
//        intent.putExtra("exit",true);
//        startActivity(intent);
//
//        finish();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
        ( findViewById(R.id.login_bg)).setBackgroundDrawable(null);
    }

    Handler mLoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:

                    Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                    ProgressUtil.dismiss();
                    Intent intent4=new Intent(Login.this,MainActivity.class);
                    startActivity(intent4);
                    Intent intent1 = new Intent("Mine");
                    sendBroadcast(intent1);
                    Intent intent2 = new Intent("car");
                    sendBroadcast(intent2);
                    Intent intent3 = new Intent("yaoyue");
                    sendBroadcast(intent3);

                    finish();
                    break;
                case -2:
                ProgressUtil.dismiss();
                    Toast.makeText(Login.this, "连接超时,请重新尝试", Toast.LENGTH_SHORT).show();
                    break;
                case -3:
                   ProgressUtil.dismiss();
                    Toast.makeText(Login.this, "用户名或密码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        mshareApi.onActivityResult(requestCode, resultCode, data);
    }

//    private UMAuthListener umAuthListener = new UMAuthListener() {
//        String uid;
//        String accessToken;
//
//        @Override
//        public void onComplete(final SHARE_MEDIA platform, int action, final Map<String, String> data) {
//            if (progressDialog == null) {
//                progressDialog = new ProgressDialog(Login.this);
//                progressDialog.isIndeterminate();
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setMessage("正在登录，请稍等");
//            }
//            if (!progressDialog.isShowing()) progressDialog.show();
//            if (action == 0) {
//                mshareApi.getPlatformInfo(Login.this, platform, umAuthListener);
//                uid = data.get("openid");
//                accessToken = data.get("access_token");
//            } else if (action == 2) {
//                Log.w(TAG, "onComplete: data=-=-=-=-=-=-=-=-=23434324" + data);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String data1 = OkHttpUtils.post(Constants.Login_3_Ip).tag(TAG).params("uid", uid).params("password", accessToken)
//                                    .params("pet_name", platform.equals(SHARE_MEDIA.QQ) ? data.get("screen_name") : data.get("nickname"))
//                                    .params("sex", platform.equals(SHARE_MEDIA.QQ) ? (data.get("gender").equals("男") ? "1" : "2") : data.get("sex"))
//                                    .params("key", Constants.safeKey)
//                                    .params("m_id", Constants.M_id)
//                                    .params("type", platform.equals(SHARE_MEDIA.QQ) ? "2" : "3")
//                                    .params("head", platform.equals(SHARE_MEDIA.QQ) ? data.get("profile_image_url") : data.get("headimgurl")).execute().body().string();
//                            if (!data1.equals("")) {
//                                Log.w(TAG, "run: data1_+_+_+_+_+_" + data1);
//                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
//                                if (null != map && map.get("code").equals("000")) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            SharedPreferences.Editor ed = sp.edit();
//                                            ed.putString("user_id", map.get("user_id"));
//                                            ed.putString("uid", uid);
//                                            ed.putString("phone", map.get("phone"));
//                                            ed.putString("pet_name", (platform.equals(SHARE_MEDIA.QQ) ? data.get("screen_name") : data.get("nickname")).equals("") ? map.get("pet_name") :
//                                                    platform.equals(SHARE_MEDIA.QQ) ? data.get("screen_name") : data.get("nickname"));
//                                            ed.putString("sex", platform.equals(SHARE_MEDIA.QQ) ? data.get("gender") : data.get("sex"));
//                                            if (!(platform.equals(SHARE_MEDIA.QQ) ? data.get("profile_image_url") : data.get("headimgurl")).equals("")) {
//                                                ed.putString("head_url", platform.equals(SHARE_MEDIA.QQ) ? data.get("profile_image_url") : data.get("headimgurl"));
//                                            }
//                                            ed.apply();
//                                            if (progressDialog.isShowing() && progressDialog != null) {
//                                                progressDialog.dismiss();
//                                            }
//                                            if (TextUtils.isEmpty(map.get("phone"))) {
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Intent intent = new Intent(Login.this, PhoneCheck.class);
//                                                        startActivity(intent);
//
//                                                    }
//                                                });
//                                            }
//                                            mLoginHandler.sendEmptyMessage(-1);
//
//
//
//
//                                        }
//                                    });
//                                } else {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                           if(progressDialog!=null&&progressDialog.isShowing()) progressDialog.dismiss();
//                                            Toast.makeText(Login.this, "登录异常，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(progressDialog!=null&&progressDialog.isShowing()) progressDialog.dismiss();
//                                    Toast.makeText(Login.this, "登录异常，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//                }).start();
//            }
//
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//            Toast.makeText(getApplicationContext(), "未知错误" + t, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//
//        public void onCancel(SHARE_MEDIA platform, int action) {
//        }
//    };

}
