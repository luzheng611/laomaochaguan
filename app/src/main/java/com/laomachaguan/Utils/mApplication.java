package com.laomachaguan.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.laomachaguan.R;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpHeaders;
import com.lzy.okhttputils.model.HttpParams;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/9/12.
 */
public class mApplication extends Application {
    private int caheM;
    private static final String TAG = "mApplication";
    private static Application context;
    public static ArrayList<String> good_id_list = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //Glide
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        caheM = maxMemory / 8;
        GlideBuilder gb = new GlideBuilder(this);
        gb.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
        gb.setMemoryCache(new LruResourceCache(caheM));
        gb.setBitmapPool(new LruBitmapPool(caheM));
        //Glide


        HttpHeaders headers = new HttpHeaders();
        //所有的 header 都 不支持 中文

        HttpParams params = new HttpParams();
        //所有的 params 都 支持 中文


        //必须调用初始化
        OkHttpUtils.init(this);
        //以下都不是必须的，根据需要自行选择
        OkHttpUtils.getInstance()//
                .debug("OkHttpUtils")                                              //是否打开调试
                .setConnectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS)               //全局的连接超时时间
                .setReadTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                  //全局的读取超时时间
                .setWriteTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                 //全局的写入超时时间
                //.setCookieStore(new MemoryCookieStore())                           //cookie使用内存缓存（app退出后，cookie消失）
//                .setCookieStore(new PersistentCookieStore())                    //cookie持久化存储，如果cookie不过期，则一直有效
                .addCommonHeaders(headers)                                         //设置全局公共头
                .addCommonParams(params);

        Config.DEBUG = true;

        PlatformConfig.setWeixin(Constants.WXPay_APPID, "e97c913528b4712d56f8657569bdc06e");
        PlatformConfig.setQQZone("1106090777", "l8BdcBT8J10T9HOk");
        PlatformConfig.setTwitter("qCFlac3c6rDGttm76x3tyRAMI", "F6dPgiaU7fXvSeSor6n10rWSBoQliRZmv3xWOLQeE7S2OeFidx");
        PlatformConfig.setSinaWeibo("2573561000", "4f01d9ace07b769e2ee21cf9e6fe51c7", "https://api.weibo.com/oauth2/default.html");

        //初始化阿里云推送
        initCloudChannel(this);


        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(this, "2882303761517587273", "5921758751273");
        // 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(this);
    }

    public static Application getInstance() {
        return context;
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.e(TAG, "初始化阿里云推送成功,设备Id:" + pushService.getDeviceId());
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "初始化阿里云推送失败 -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }

    public static byte[] PK() {
        return Base64.decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzj1J20jfuAU3CQDPElbOuASC" +
                "1Qase0eyA1j+bvp64foNnrJ7O5ggM2zJDP3jmEMPrm9BywTIKou30jA0fZh62dRl" +
                "3DslBLJKLlq9xnpecLaawMe0xT3AM54fYMYZdVzKXK8s9OKSYt61V+yDIo+AMgw/" +
                "P60irfotxeRNZNNhHQIDAQAB");
    }

    /**
     * 简繁互换
     *
     * @return
     */
    public static String ST(String s) {
//        try {
//            return isChina ? JChineseConvertor.getInstance().t2s(s):JChineseConvertor.getInstance().s2t(s);
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtil.e("简繁转换错误");
//        }
        return s;
    }

    public static void openPayLayout(final Activity context, final String allmoney, final String attachId, final String title, final String num, final String adress, final String type, final String number, final String spec_id) {// TODO: 2016/12/20 打开支付窗口
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = b.create();
        View view = LayoutInflater.from(context).inflate(R.layout.pay_bottom_layout, null);
        Drawable drawable = context.getResources().getDrawable(R.drawable.pay_wc);
        Drawable drawable3 = context.getResources().getDrawable(R.drawable.pay_ali);
        Drawable drawable1 = context.getResources().getDrawable(R.drawable.pay_qq);
        Drawable drawable2 = context.getResources().getDrawable(R.drawable.pay_up);
        drawable.setBounds(0, 0, DimenUtils.dip2px(context, 35), DimenUtils.dip2px(context, 35));
        drawable1.setBounds(0, 0, DimenUtils.dip2px(context, 35), DimenUtils.dip2px(context, 35));
        drawable2.setBounds(0, 0, DimenUtils.dip2px(context, 35), DimenUtils.dip2px(context, 35));
        drawable3.setBounds(0, 0, DimenUtils.dip2px(context, 35), DimenUtils.dip2px(context, 35));
        ((TextView) view.findViewById(R.id.tv_pay_wx)).setCompoundDrawables(drawable, null, null, null);
        ((TextView) view.findViewById(R.id.tv_pay_qq)).setCompoundDrawables(drawable1, null, null, null);
        ((TextView) view.findViewById(R.id.tv_pay_up)).setCompoundDrawables(drawable2, null, null, null);
        ((TextView) view.findViewById(R.id.tv_pay_ali)).setCompoundDrawables(drawable3, null, null, null);
        view.findViewById(R.id.tv_pay_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXPayUtils.openWXPay(context, allmoney, attachId, title, num,adress, type, number, spec_id);
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_pay_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QpayUtil.openQQPay(context, attachId, context.getResources().getString(R.string.app_name) + "-" + title, allmoney, num, type, spec_id);
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_pay_ali).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AliPayUtil_Old.openAliPay(context, allmoney, attachId, title, num,adress, type, number, spec_id);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
        alertDialog.getWindow().setDimAmount(0.3f);
        alertDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wl = alertDialog.getWindow().getAttributes();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(wl);
    }
}
