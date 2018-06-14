package com.laomachaguan.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/2/28.
 */

public class ShareManager {
    //分享
    private static SHARE_MEDIA[] share_list = new SHARE_MEDIA[]{
            SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
            , SHARE_MEDIA.SINA
//            , SHARE_MEDIA.LINE
//            ,SHARE_MEDIA.FACEBOOK
//            , SHARE_MEDIA.TWITTER
    };
    private static WeakReference<Activity> weakReference;
    private Activity context;
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            LogUtil.e("正在分享。。。。。。。。。。");

            ProgressUtil.show(context, "", "请稍等.....");

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            LogUtil.e("分享完成。。。。。。。。。。");
            if (share_media.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(context," 收藏成功啦", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mApplication.getInstance(), "分享成功", Toast.LENGTH_SHORT).show();
            }
            ProgressUtil.dismiss();


        }

        @Override
        public void onError(final SHARE_MEDIA share_media, final Throwable throwable) {
            LogUtil.e("分享失败。。。。。。。。。。");
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg=throwable.getMessage();
//                        Toast.makeText(context,throwable.getMessage().substring(msg.indexOf("错误信息")+5,msg.indexOf("点击")),Toast.LENGTH_SHORT).show();
                        if(!msg.contains("未知错误")){
                            if(msg.contains("分享失败")){
                                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(share_media==share_media.LINE){
                            Verification.launchAppDetail(context,"jp.naver.line.android","");
                        }
                    }
                });

            ProgressUtil.dismiss();


        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            LogUtil.e("分享取消。。。。。。。。。。");

                ProgressUtil.dismiss();


        }
    };

    public void shareWeb(final UMWeb umObject, Activity activity) {

//        UmengTool.getSignature(activity);
        weakReference=new WeakReference<Activity>(activity);
        context = weakReference.get();
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            return;
        }
        ShareAction action = new ShareAction(context);
        final UMWeb umWeb = (UMWeb) umObject;
        action.setDisplayList(share_list)
                .addButton("umeng_socialize_clip_link", "umeng_socialize_clip_link", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (share_media == null) {
                            if (snsPlatform.mKeyword.equals("umeng_socialize_clip_link")) {
                                android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboardManager.setText(umWeb.toUrl());
                                Toast.makeText(context, "分享链接已复制", Toast.LENGTH_LONG).show();
                            }
                        } else {
//                           if( !UMShareAPI.get(context).isInstall(context, share_media)){
//                               Toast.makeText(context, "未安装该应用", Toast.LENGTH_SHORT).show();
//                               return;
//                           };
                            if (share_media != SHARE_MEDIA.SINA) {
                                new ShareAction(context).setPlatform(share_media).setCallback(umShareListener)
                                        .withMedia(umWeb).share();
                            } else {
//                                if(share_media==share_media.TWITTER){
//                                    new ShareAction(context)
//                                            .withText("Twitter分享")
//                                            .withMedia(umWeb)
//                                            .setPlatform(share_media)
//                                            .setCallback(umShareListener).share();
//                                    return;
//                                }
                                UMWeb umWeb1 = new UMWeb(umWeb.toUrl());
                                umWeb1.setTitle(umWeb.getTitle());
                                umWeb1.setDescription(umWeb.getTitle() + "\n" + umWeb.getDescription());
                                umWeb1.setThumb(umWeb.getThumbImage());
                                new ShareAction(context).setPlatform(share_media).setCallback(umShareListener).withMedia(umWeb1).share();
                            }
                        }

                    }

                });

        action.open();
    }
    /**
     * 分享功能
     *
     * @param context
     *            上下文
     * @param activityTitle
     *            Activity的名字
     * @param msgTitle
     *            消息标题
     * @param msgText
     *            消息内容
     * @param imgPath
     *            图片路径，不分享图片则传null
     */
    public void shareMsg(Context context,String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));

    }


}
