package com.laomachaguan.Common;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.FileUtils;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.PermissionUtil;
import com.laomachaguan.Utils.PhoneSMSManager;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.Verification;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.callback.FileCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;


public class GanyuActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GanyuActivity";
    private TextView wangzhan, qq, email, gengxin, pingfen, xieyi, zhengce;

    private String appUrl;
    private String jishuSupprot = "";
    private String share = "";
    private String prol = "";//协议
    private String secret = "";//隐私政策
    private boolean haveProl = false;
    private HashMap<String, String> map;//获取的基本信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganyu);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        wangzhan = (TextView) findViewById(R.id.guanfangwangzhan);
        qq = (TextView) findViewById(R.id.QQqun);
        email = (TextView) findViewById(R.id.user_email);
        ((TextView) findViewById(R.id.title)).setText(mApplication.ST("关于"));
        ((TextView) findViewById(R.id.name)).setText(mApplication.ST(getResources().getString(R.string.app_name)));
        ((TextView) findViewById(R.id.guanwang)).setText(mApplication.ST("官方网站"));
        ((TextView) findViewById(R.id.dianhua)).setText(mApplication.ST("热线电话"));
        ((TextView) findViewById(R.id.email)).setText(mApplication.ST("客服邮箱"));
        ((TextView) findViewById(R.id.banquan)).setText(mApplication.ST("Copyright 2016-2017 成都因陀罗网络科技有限公司 版权所有"));
        ((ImageView) findViewById(R.id.logo)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.indra));
        gengxin = (TextView) findViewById(R.id.gengxin);
        pingfen = (TextView) findViewById(R.id.pingfen);
        xieyi = (TextView) findViewById(R.id.xieyi);
        zhengce = (TextView) findViewById(R.id.zhengce);
        gengxin.setText(mApplication.ST("检查更新"));
        pingfen.setText(mApplication.ST("去评分"));
        xieyi.setText(mApplication.ST("用户协议"));
        zhengce.setText(mApplication.ST("隐私政策"));

        pingfen.setOnClickListener(this);
        gengxin.setOnClickListener(this);
        xieyi.setOnClickListener(this);
        zhengce.setOnClickListener(this);
        getData();
        getProl();
    }

    /**
     * 获取数据
     */
    private void getData() {

        ProgressUtil.show(this, "", "正在加载....");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkHttpUtils.post(Constants.AboutUs_Ip).tag(TAG)
                            .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    if (!data.equals("") && !data.equals("null")) {
                        map = AnalyticalJSON.getHashMap(data);
                        Log.w(TAG, "run: " + data);
                        if (map != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (map.get("url") != null && !map.get("url").equals("")) {

                                        wangzhan.setOnClickListener(GanyuActivity.this);
                                        wangzhan.setTextColor(ContextCompat.getColor(GanyuActivity.this, R.color.cornflowerblue));
                                    }
                                    wangzhan.setText(map.get("url").equals("") ? mApplication.ST("即将开放") : map.get("url"));
                                    if (map.get("tel") != null && !map.get("tel").equals("")) {
                                        qq.setOnClickListener(GanyuActivity.this);
                                        qq.setTextColor(ContextCompat.getColor(GanyuActivity.this, R.color.cornflowerblue));
                                    }
                                    qq.setText(map.get("tel").equals("") ? mApplication.ST("即将开放") : map.get("tel"));
                                    email.setText(map.get("email").equals("") ? mApplication.ST("即将开放") : map.get("email"));
                                    ProgressUtil.dismiss();
                                }
                            });
                        }

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GanyuActivity.this, mApplication.ST("加载失败"), Toast.LENGTH_SHORT).show();
                                ProgressUtil.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GanyuActivity.this, mApplication.ST("加载失败"), Toast.LENGTH_SHORT).show();
                            ProgressUtil.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.guanfangwangzhan://跳转网页
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(map.get("url")));
                startActivity(intent);
                break;
            case R.id.QQqun://打电话
                PhoneSMSManager.callPhone1(this, map.get("tel"));
                break;
            case R.id.guanyu_back:
                finish();
                break;
            case R.id.gengxin:
                checkUpdate();
                break;
            case R.id.pingfen:
                Verification.toMarket(this);
                break;
            case R.id.xieyi:
                if (haveProl) {
                    if (new LoginUtil().checkLogin(this)) {
//
                        if (prol != null && !prol.equals("")) {
                            View view = LayoutInflater.from(this).inflate(R.layout.activity_confirm_dialog, null);
                            final WebView web = (WebView) view.findViewById(R.id.web);
                            TextView cancle = (TextView) view.findViewById(R.id.cancle);
                            cancle.setText(mApplication.ST("确定"));
                            final TextView baoming = (TextView) view.findViewById(R.id.baoming);
                            baoming.setEnabled(false);
                            baoming.setVisibility(View.GONE);
//                            final CountDownTimer cdt = new CountDownTimer(10000, 1000) {
//                                @Override
//                                public void onTick(long millisUntilFinished) {
//                                    baoming.setText(mApplication.ST("请阅读报名须知(" + millisUntilFinished / 1000 + "秒)"));
//                                }
//
//                                @Override
//                                public void onFinish() {
//                                    baoming.setText(mApplication.ST("同意"));
//                                    baoming.setEnabled(true);
//                                }
//                            };
                            web.loadDataWithBaseURL("", prol
                                    , "text/html", "UTF-8", null);

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setView(view);

                            final AlertDialog dialog = builder.create();
                            cancle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    web.destroy();
//                                    cdt.cancel();
                                    dialog.dismiss();
                                }
                            });
//                            baoming.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    dialog.dismiss();
//                                    showBaoMingDialog();
//
//                                }
//                            });
//                            cdt.start();
                            builder.setCancelable(false);
                            web.setWebViewClient(new WebViewClient(){
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);
                                    dialog.show();
                                }
                            });


                        } else {
                            ToastUtil.showToastShort("暂无用户协议");
                        }
                    }
                } else {
                    ToastUtil.showToastShort("暂无用户协议");
                }
                break;
            case R.id.zhengce:
                if (haveProl) {
                    if (new LoginUtil().checkLogin(this)) {
//
                        if (secret != null && !secret.equals("")) {
                            View view = LayoutInflater.from(this).inflate(R.layout.activity_confirm_dialog, null);
                            final WebView web = (WebView) view.findViewById(R.id.web);
                            TextView cancle = (TextView) view.findViewById(R.id.cancle);
                            cancle.setText(mApplication.ST("确定"));
                            final TextView baoming = (TextView) view.findViewById(R.id.baoming);
                            baoming.setEnabled(false);
                            baoming.setVisibility(View.GONE);
//                            final CountDownTimer cdt = new CountDownTimer(10000, 1000) {
//                                @Override
//                                public void onTick(long millisUntilFinished) {
//                                    baoming.setText(mApplication.ST("请阅读报名须知(" + millisUntilFinished / 1000 + "秒)"));
//                                }
//
//                                @Override
//                                public void onFinish() {
//                                    baoming.setText(mApplication.ST("同意"));
//                                    baoming.setEnabled(true);
//                                }
//                            };
                            web.loadDataWithBaseURL("", secret
                                    , "text/html", "UTF-8", null);
//
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setView(view);
                            final AlertDialog dialog = builder.create();
                            cancle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    web.destroy();
//                                    cdt.cancel();
                                    dialog.dismiss();
                                }
                            });
//                            baoming.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    dialog.dismiss();
//                                    showBaoMingDialog();
//
//                                }
//                            });
//                            cdt.start();
                            builder.setCancelable(false);
                            web.setWebViewClient(new WebViewClient(){
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);
                                    dialog.show();
                                }
                            });
                        } else {
                            ToastUtil.showToastShort("暂无隐私政策");
                        }
                    }
                } else {
                    ToastUtil.showToastShort("暂无隐私政策");
                }
                break;
        }
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
                                            haveProl = true;
                                            prol = map.get("agreements");
                                            secret = map.get("privacy");
                                        } else {
                                            haveProl = false;
                                        }
                                    }
                                });
                            } else {
                                haveProl = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                });
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroy();
    }

    /*
  检查更新
  */
    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkHttpUtils.post(Constants.Update_Ip)
                            .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                        if (map != null) {
                            String code = map.get("app_code");
                            final String appname = map.get("app_name");
                            appUrl = map.get("app_url");
                            share = map.get("share");
                            jishuSupprot = map.get("support");
                            if (null != code && Verification.getVersionCode(mApplication.getInstance()) < Integer.valueOf(code)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog(appname, appUrl, map.get("app_update"));//更新通知
                                    }
                                });
                                Log.w(TAG, "run: " + appname + "=-=url=-=-=-=" + appUrl);
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToastShort("暂无最新版本");
                                    }
                                });
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent("zixun");

                                    intent.putExtra("a", appUrl);
                                    intent.putExtra("s", share);
                                    intent.putExtra("j", jishuSupprot);
                                    sendBroadcast(intent);

                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showDialog(final String appname, final String appUrl, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.update_alet_layout, null);
        ((TextView) view.findViewById(R.id.version_update_title)).setText("检测到新版本安装包：" + appname.substring(Constants.NAME_CHAR_NUM, appname.length() - 4));
        final TextView textView = (TextView) view.findViewById(R.id.version_update_content);
        textView.setText(content.equals("") ? "是否需要更新？" : content);
        view.findViewById(R.id.version_update_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView) v).getText().equals("后台更新")) {
                    dialog.dismiss();
                    return;
                }
                PermissionUtil.checkPermission(GanyuActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS});

                ((TextView) v).setText("后台更新");
                final ProgressBar updateBar = (ProgressBar) view.findViewById(R.id.version_update_progress);
                updateBar.setVisibility(View.VISIBLE);
                final TextView percent = (TextView) view.findViewById(R.id.version_update_percent);
                percent.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                OkHttpUtils.get(appUrl).tag("download").execute(new FileCallback(Environment.getExternalStorageDirectory().getAbsolutePath(), appname) {


                    @Override
                    public void onSuccess(File file, Call call, Response response) {

                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        if (percent != null) percent.setText((int) (progress * 100) + "%");
                        if (updateBar != null) updateBar.setProgress((int) (progress * 100));
                    }

                    @Override
                    public void onAfter(File file, Exception e) {
                        super.onAfter(file, e);
                        Verification.installApk(getApplicationContext(), appname);
                    }


                });
            }
        });
        view.findViewById(R.id.version_update_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                OkHttpUtils.getInstance().cancelTag("download");
                FileUtils.deleteFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), appname));
            }
        });
        dialog.setView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
