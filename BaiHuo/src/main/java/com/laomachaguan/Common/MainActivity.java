package com.laomachaguan.Common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.laomachaguan.Adapter.myHomePagerAdapter;
import com.laomachaguan.Fragment.FenLei;
import com.laomachaguan.Fragment.Mine;
import com.laomachaguan.Fragment.Zixun_Tougao;
import com.laomachaguan.Model_Order.Order_item_fragment_main;
import com.laomachaguan.Model_Order.ShopCar2;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.FileUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.MD5Utls;
import com.laomachaguan.Utils.PermissionUtil;
import com.laomachaguan.Utils.PhoneSMSManager;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.ShareManager;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.Verification;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.BadgeView;
import com.laomachaguan.View.NoScrollViewPager;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.callback.FileCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Response;

import static com.laomachaguan.Utils.mApplication.good_id_list;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView back;
    private TextView title;
    public NoScrollViewPager pager;
    private TabLayout tabLayout;
    private myHomePagerAdapter adapter;
    private SharedPreferences sp;
    public List<Fragment> list;
    private ImageView add;
    private PopupWindow pp;//加号弹出窗口
    private SHARE_MEDIA[] share_list;
    private ShareAction action;
    private String appUrl;
    private AlertDialog backDialog;
    private String jishuSupprot="";
    private String share="";
    public  static Activity activity;
    private String SMS="";
    private  Uri contactData;
    private UMWeb umWeb;
    private BadgeView b;
    private int currentNum;//购物车数量
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String good_id=intent.getStringExtra("good_id");
            if(good_id_list.contains(good_id)){
                good_id_list.remove(good_id);
            }
            getShopCarSize();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
         StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.activity_main);
        activity=this;
        IntentFilter intentFilter=new IntentFilter("car");
        registerReceiver(receiver,intentFilter);
        initView();
        getShopCarSize();
        checkUpdate();
        checkUserinfo();
        getSmsInviteCode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }
    private void getShopCarSize() {
        if (!PreferenceUtil.getUserIncetance(this).getString("user_id", "").equals("")) {
            JSONObject js = new JSONObject();
            try {
                js.put("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpUtils.post(Constants.Shopcar_list).tag(this)
                    .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js)).execute(new AbsCallback<Object>() {
                @Override
                public Object parseNetworkResponse(Response response) throws Exception {
                    return null;
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    if(b!=null){
                        b.setBadgeCount(0);
                    }
                }

                @Override
                public void onSuccess(Object o, Call call, Response response) {
                    String data = null;
                    try {
                        data = response.body().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (data != null && !data.equals("")) {
                        final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (list != null) {
                                    currentNum = list.size();
                                    LogUtil.e("购物车数量：" + currentNum);
                                    if (b != null) {
                                        b.setBadgeCount(currentNum);

                                    } else {
                                        b = new BadgeView(MainActivity.this);
                                        b.setTextSize(10);
                                        b.setBadgeGravity(Gravity.RIGHT);
                                        b.setBackground(100, Color.RED);
                                        b.setBadgeCount(currentNum);
                                    }
                                    for (HashMap<String, String> map : list) {
                                        String id = map.get("good_id");
                                        if (!good_id_list.contains(id))
                                            good_id_list.add(id);
                                    }
                                } else {
                                    b.setBadgeCount(0);
                                    good_id_list.clear();
                                }
                                b.setTargetView(tabLayout.getTabAt(3).getCustomView().findViewById(R.id.home_tab_image));
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                b.setBadgeCount(0);
                                good_id_list.clear();

                            }
                        });

                    }
                }


            });
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ToastUtil.showToastShort("onConfigurationChanged");
    }

    /**
     * 获取短信邀请文本
     */
    private void getSmsInviteCode() {
        JSONObject js=new JSONObject();
        try {
            js.put("m_id",Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.little_sms_get__IP).tag(TAG)
                .params("key", m.K()).params("msg",m.M())
                .execute(new AbsCallback<Object>() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        return null;
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        try {
                            String data=response.body().string();
                            if(!data.equals("")){
                                final HashMap<String ,String > map= AnalyticalJSON.getHashMap(data);
                                if(map!=null)
                                    SMS=map.get("sms");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }




                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        LogUtil.e("到达首页");
        if(resultCode==RESULT_OK){
            if(requestCode==PhoneSMSManager.REQUEST_CODE_CONTENT){
                if (Build.VERSION.SDK_INT >= 23) {

                    //判断有没有拨打电话权限
                    if (PermissionChecker.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                            ||PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        //请求拨打电话权限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_CONTACTS}, 222);
                        return;
                    }

                }
                contactData= data.getData();
                LogUtil.w( "onActivityResult: "+data +"    contactData:"+data.getData());
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                LogUtil.w( "onActivityResult: "+cursor.moveToFirst());
//                String num = PhoneSMSManager.getContactPhone(this,cursor);
                //打开短信app
                if (cursor.moveToFirst()) {
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)) ;
                    String phonenum = "此联系人暂未输入电话号码";
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"="+contactId, null, null);

                    if (phones.moveToFirst()) {
                        phonenum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    LogUtil.w("联系人："+name + "\n电话：" + phonenum);
                    if (Build.VERSION.SDK_INT <14) {
                        phones.close();
                    }
                    Uri uri = Uri.parse("smsto:" +  phonenum);
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                    sendIntent.putExtra("sms_body",("".equals(SMS)?sp.getString("sms",""):SMS));
                    startActivity(sendIntent);
                }
                if (Build.VERSION.SDK_INT <14) {//不添加的话Android4.0以上系统运行会报错
                    cursor.close();
                }


            }

        }

    }

    private void checkUserinfo() {
        if (!sp.getString("user_id", "").equals("")) {
//            if (sp.getString("pet_name", "").equals("") || sp.getString("sex", "").equals("")) {
//                Intent intent = new Intent(this, Mine_gerenziliao.class);
//                startActivity(intent);
//            }
            if (sp.getString("phone", "").equals("")) {
                Intent intent = new Intent(this, PhoneCheck.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        JCVideoPlayer.releaseAllVideos();
        ProgressUtil.dismiss();
    }

    /*
     检查更新
     */
    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.Update_Ip)
                            .params("key", m.K()).params("msg",m.M())
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                        if (map != null) {
                            String code = map.get("app_code");
                            final String appname = map.get("app_name");
                            appUrl = map.get("app_url");
                            share=map.get("share");
                            jishuSupprot=map.get("support");
                            if (null != code && Verification.getVersionCode(mApplication.getInstance()) < Integer.valueOf(code)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog(appname, appUrl,map.get("app_update"));//更新通知
                                    }
                                });
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        b=new BadgeView(this);
        b.setGravity(Gravity.RIGHT);
        b.setBadgeCount(0);
        b.setBackground(100,Color.RED);
        b.setTextSize(10);
        //分享
        share_list = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
        };
        add = (ImageView) findViewById(R.id.title_image3);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.home_add_layout, null);
                if (pp == null) {
                    pp = new PopupWindow(view);
                    pp.setFocusable(true);
                    pp.setOutsideTouchable(true);
                    pp.setTouchable(true);
//                    ColorDrawable c = new ColorDrawable(getResources().getColor(R.color.main_color));
                    pp.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.titlebar_bg));
                    pp.setWidth(DimenUtils.dip2px(mApplication.getInstance(), 150));
                    pp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    pp.showAsDropDown(add, -50, 0);
                } else {
                    if (pp.isShowing()) pp.dismiss();
                    pp.showAsDropDown(add, -50, 0);
                }
                initPopupWindow(view);
            }
        });
        sp = getSharedPreferences("user", MODE_PRIVATE);
        back = (ImageView) findViewById(R.id.title_back);
        title = (TextView) findViewById(R.id.title_title);
        pager = (NoScrollViewPager) findViewById(R.id.home_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.home_bottom_tablayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//
        //初始化fragment
        list = new ArrayList<>();

        Fragment f = new Zixun_Tougao();
        Fragment f1 = new Order_item_fragment_main();
//        Fragment f3 = new YaoYue();
        Fragment fenlei=new FenLei();
        Fragment shopcar=new ShopCar2();
        Fragment f4 = new Mine();
        list.add(f);
        list.add(f1);
        list.add(fenlei);
        list.add(shopcar);
        list.add(f4);


        //加载到adapter
        adapter = new myHomePagerAdapter(this, getSupportFragmentManager(), list);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(list.size());

        //关联tablayout
        tabLayout.setupWithViewPager(pager);
        //设置自定义tab
        for (int i = 0; i < list.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null){
                tab.setCustomView(adapter.getTabView(i));
            }
        }

        //为tab注册选中事件
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.home_tab_text)).setTextColor(getResources().getColor(R.color.main_color));
                pager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        title.setText("圈子");
                        break;
                    case 1:
                        title.setText("商城");
                        break;
                    case 2:
                        title.setText("分类");
                        break;
                    case 3:
                        title.setText("购物车");
                        break;
                    case 4:
                        title.setText("我的");
                        break;


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.home_tab_text)).setTextColor(getResources().getColor(R.color.wordblack));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        pager.setCurrentItem(0);
        title.setText("圈子");
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
        ((TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.home_tab_text)).setTextColor(getResources().getColor(R.color.main_color));
    }

    //更新进度框
    private void showDialog(final String appname, final String appUrl,String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.update_alet_layout, null);
        ((TextView) view.findViewById(R.id.version_update_title)).setText("检测到新版本：" + appname.substring(Constants.NAME_CHAR_NUM, appname.length() - 4));
        final TextView textView= (TextView) view.findViewById(R.id.version_update_content);
        textView.setText(content.equals("")?"是否需要更新？":content);
        view.findViewById(R.id.version_update_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView) v).getText().equals("后台更新")) {
                    dialog.dismiss();
                    return;
                }
               if(!PermissionUtil.checkPermission(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})){
                   return;
               };

                ((TextView) v).setText("后台更新");
                final ProgressBar updateBar = (ProgressBar) view.findViewById(R.id.version_update_progress);
                updateBar.setVisibility(View.VISIBLE);
                final TextView percent = (TextView) view.findViewById(R.id.version_update_percent);
                percent.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                OkHttpUtils.get(appUrl).tag("download").execute(new FileCallback(Environment.getExternalStorageDirectory().getAbsolutePath(), appname) {

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        LogUtil.e("新安装包下载中——"+progress);
                        percent.setText((int) (progress * 100) + "%");
                        updateBar.setProgress((int) (progress * 100));
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ToastUtil.showToastShort("正在下载最新安装包，请稍等");
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        LogUtil.e("下载完成");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        LogUtil.e("下载异常");
                        e.printStackTrace();
                    }

                    @Override
                    public void onAfter(@Nullable File file, @Nullable Exception e) {
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
                FileUtils.deleteFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath(),appname));
            }
        });
        dialog.setView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void initPopupWindow(View v) {
        LinearLayout layout1 = (LinearLayout) v.findViewById(R.id.layout1);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri content_url;
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                if(sp.getString("user_id","").equals("")){
                    content_url = Uri.parse(Constants.host_Ip+"/"+Constants.NAME_LOW+".php");
                    intent.setData(content_url);
                    startActivity(intent);
                    pp.dismiss();
                }else{
                    JSONObject js=new JSONObject();
                    try {
                        js.put("id",sp.getString("user_id","")+MD5Utls.stringToMD5(MD5Utls.stringToMD5(Constants.M_id)));
                        content_url = Uri.parse(Constants.host_Ip+"/"+Constants.NAME_LOW+".php/Index/index/login/"+ android.util.Base64.encodeToString(js.toString().getBytes(), android.util.Base64.DEFAULT));
                        Log.w(TAG, "onClick: 加密地址"+content_url);
                        intent.setData(content_url);
                        startActivity(intent);
                        pp.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        LinearLayout layout2 = (LinearLayout) v.findViewById(R.id.layout2);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                QpayUtil.openQQPay(MainActivity.this,"52","fdsfljdk","100","1","4");
                createShortCut();
                pp.dismiss();
            }
        });
        LinearLayout layout3 = (LinearLayout) v.findViewById(R.id.layout3);
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri content_url;
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                if(jishuSupprot.equals("")){
                    content_url = Uri.parse("http://www.indranet.cn");
                }else{
                    content_url= Uri.parse(jishuSupprot);
                }
                intent.setData(content_url);
                startActivity(intent);
                pp.dismiss();
            }
        });
        LinearLayout layout4 = (LinearLayout) v.findViewById(R.id.layout4);
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umWeb = new UMWeb("".equals(share) ? appUrl : share);
                umWeb.setThumb(new UMImage(mApplication.getInstance(), R.drawable.indra));
                umWeb.setTitle(getResources().getString(R.string.app_name) + "App");
                umWeb.setDescription("".equals(share) ? appUrl : share);
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, MainActivity.this);
                }

                pp.dismiss();
            }
        });
        LinearLayout layout5= (LinearLayout) v.findViewById(R.id.layout5);
        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneSMSManager.openPhoneDetail(MainActivity.this);
            }
        });
    }

    public void createShortCut() {
        //创建快捷方式的Intent
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        //需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name) + "WEB");
        //快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.indra);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(Constants.host_Ip + "/" + Constants.NAME_LOW + ".php");
        intent.setData(content_url);
        //点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        //发送广播。OK
        sendBroadcast(shortcutintent);
    }

    @Override
    public void onBackPressed() {
        if (pp != null && pp.isShowing()) {
            pp.dismiss();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (backDialog == null) {
                backDialog = new AlertDialog.Builder(this).setMessage("确认要退出吗").setNegativeButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        backDialog.dismiss();
                    }
                }).create();
                backDialog.setCanceledOnTouchOutside(false);
            }
            if (backDialog.isShowing())
                return false;
            backDialog.show();

        }
        return super.onKeyDown(keyCode, event);
    }

    private static final String TAG_EXIT = "exit";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra(TAG_EXIT, false);
            if (isExit) {
                this.finish();
            }
        }
    }

}
