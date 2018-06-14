package com.laomachaguan.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.MD5Utls;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.ShareManager;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/9/14.
 */
public class Mine_Store extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<Fragment> list;
    private SharedPreferences sp;
    private static final String TAG = "dishes";
    private ViewPager viewPager;
    private DDPagerAdapter adapter;
    private ImageView jinbi;
    public String title="";//店铺名称
    public String redeem="";//店铺当前返现比例
    //    private TextView pintuan,fahuo,tuikuan;
    //    private DecimalFormat df;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.mine_store);

        sp = getSharedPreferences("user", Context.MODE_PRIVATE);

        list = new ArrayList<Fragment>();
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        jinbi = (ImageView) findViewById(R.id.jinbi);
        jinbi.setOnClickListener(this);

        findViewById(R.id.title).setVisibility(View.VISIBLE);
        DingDanFragment d = new DingDanFragment();
        Bundle b = new Bundle();
        b.putString("role", "2");
        d.setArguments(b);
        list.add(d);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new DDPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setOffscreenPageLimit(list.size());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getStoreInfo();
    }

    private void getStoreInfo() {
        if(Network.HttpTest(this)){
            JSONObject js=new JSONObject();
            try {
                js.put("m_id",Constants.M_id);
                js.put("user_id",PreferenceUtil.getUserId(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m=ApisSeUtil.i(js);
            LogUtil.e("店铺详情：；"+js);
            OkHttpUtils.post(Constants.Mine_store_Detail).tag(this)
                    .params("key",m.K())
                    .params("msg",m.M())
                    .execute(new AbsCallback<HashMap<String,String>>() {
                        @Override
                        public HashMap<String, String> parseNetworkResponse(Response response) throws Exception {
                            return AnalyticalJSON.getHashMap(response.body().string());
                        }

                        @Override
                        public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                            if(map!=null){
                                ((TextView) findViewById(R.id.title)).setText(map.get("title"));
                                title=map.get("title");
                                redeem=map.get("redeem");
                            }

                        }
                    });
        }
    }

    private void showStoreSettingDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_tixian_admin, null);
        b.setView(view);
        final AlertDialog dialog = b.create();
        final EditText percent = (EditText) view.findViewById(R.id.credit_percent);
        ((TextView) view.findViewById(R.id.title)).setText("返现比例");
        ((TextView) view.findViewById(R.id.credit_exp)).setVisibility(View.GONE);
        percent.setText(redeem);
        percent.setSelection(redeem.length());
        TextView setting = (TextView) view.findViewById(R.id.commit);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (percent.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请输入返现比例");
                    return;
                }
                if (Integer.valueOf(percent.getText().toString().trim()) >= 100) {
                    ToastUtil.showToastShort("请输入正确的返现比例，最大100");
                    return;
                }
                JSONObject js = new JSONObject();
                try {
                    js.put("m_id", Constants.M_id);
                    js.put("user_id", PreferenceUtil.getUserId(Mine_Store.this));
                    js.put("redeem", percent.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m = ApisSeUtil.i(js);
                OkHttpUtils.post(Constants.Mine_store_Setting).tag(this)
                        .params("key", m.K())
                        .params("msg", m.M())
                        .execute(new AbsCallback<HashMap<String, String>>() {
                            @Override
                            public HashMap<String, String> parseNetworkResponse(Response response) throws Exception {
                                return AnalyticalJSON.getHashMap(response.body().string());
                            }

                            @Override
                            public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                                if ("000".equals(map.get("code"))) {
                                    ToastUtil.showToastShort("返现比例设置成功");
                                    dialog.dismiss();
                                } else if ("003".equals(map.get("code"))) {
                                    ToastUtil.showToastShort("没有权限或账号不存在");
                                }
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(Mine_Store.this,"","请稍等");
                            }

                            @Override
                            public void onAfter(@Nullable HashMap<String, String> stringStringHashMap, @Nullable Exception e) {
                                super.onAfter(stringStringHashMap, e);
                                ProgressUtil.dismiss();
                            }
                        });
            }
        });
        Window w = dialog.getWindow();
        w.setGravity(Gravity.BOTTOM);
        w.getDecorView().setPadding(0, 0, 0, DimenUtils.dip2px(this, 10));
        WindowManager.LayoutParams wl = w.getAttributes();
        wl.windowAnimations = R.style.dialogWindowAnim;
        w.setBackgroundDrawableResource(R.color.transparent);
        wl.width = getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(Mine_Store.this, 40);
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setAttributes(wl);

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.jinbi:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_bottom_good_manager, null);
                builder.setView(view);
                builder.setCancelable(true);
                final Dialog dialog = builder.create();
                TextView fenxiang = (TextView) view.findViewById(R.id.zhiding);
                fenxiang.setText("分享店铺");
                fenxiang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        UMWeb  umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/" + PreferenceUtil.getUserId(Mine_Store.this)+ "/m_id/" + MD5Utls.stringToMD5(Constants.M_id));
                        umWeb.setTitle(PreferenceUtil.getUserIncetance(Mine_Store.this).getString("pet_name","")+"的店铺");
                        umWeb.setDescription("这是我的茶叶铺子，快来选购吧");
                        umWeb.setThumb(new UMImage(Mine_Store.this, PreferenceUtil.getUserIncetance(Mine_Store.this).getString("head_url","")));
                        new ShareManager().shareWeb(umWeb,Mine_Store.this);
                    }
                });
                TextView setting = (TextView) view.findViewById(R.id.shangjia);
                setting.setText("比例设置");
                setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        showStoreSettingDialog();
                    }
                });
                TextView bangzhu = (TextView) view.findViewById(R.id.delete);
                bangzhu.setText("店铺指南");
                bangzhu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        getProl();
                    }
                });
                view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setWindowAnimations(R.style.dialogWindowAnim);
                window.setBackgroundDrawableResource(R.color.vifrification);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.width = getResources().getDisplayMetrics().widthPixels;
                wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(wl);
                dialog.show();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    private void getProl() {
        if (!LoginUtil.checkLogin(this)) {
            return;
        }
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("m_id", Constants.M_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            OkHttpUtils.post(Constants.Store_Prol)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
                            if (m != null) {
                                String prol = m.get("agreements");
                                if (prol != null && !prol.equals("")) {
                                    View view = LayoutInflater.from(Mine_Store.this).inflate(R.layout.web_confirm_dialog, null);
                                    final WebView web = (WebView) view.findViewById(R.id.web);
                                    TextView cancle = (TextView) view.findViewById(R.id.cancle);
                                    cancle.setText(mApplication.ST("确认"));
                                    view.findViewById(R.id.line).setVisibility(View.GONE);
                                    final TextView baoming = (TextView) view.findViewById(R.id.baoming);
                                    baoming.setEnabled(false);
                                    baoming.setVisibility(View.GONE);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(Mine_Store.this);
                                    builder.setView(view);
                                    final AlertDialog dialog = builder.create();
                                    web.setWebViewClient(new WebViewClient() {
                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            super.onPageFinished(view, url);
                                            dialog.show();
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                    web.loadDataWithBaseURL("", prol
                                            , "text/html", "UTF-8", null);
//                    Api.getUserNeedKnow(this,web);

                                    cancle.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            web.removeAllViews();
                                            web.destroy();
                                            dialog.dismiss();
                                        }
                                    });
                                }else{
                                    ProgressUtil.dismiss();
                                    ToastUtil.showToastShort("暂无店铺指南");
                                }

                            }

                        }

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            ProgressUtil.show(Mine_Store.this, "", "请稍等");
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            ToastUtil.showToastShort("获取用户协议失败");
                        }

                        @Override
                        public void onAfter(@Nullable String s, @Nullable Exception e) {
                            super.onAfter(s, e);

                        }
                    });
        }
    }

    private static class DDPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> list;

        public DDPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }


        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }
    }


}
