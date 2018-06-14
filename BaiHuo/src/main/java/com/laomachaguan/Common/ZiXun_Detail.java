package com.laomachaguan.Common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.Result;
import com.laomachaguan.Adapter.PL_List_Adapter;
import com.laomachaguan.Model_activity.activity_Detail;
import com.laomachaguan.R;
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
import com.laomachaguan.Utils.QrUtils;
import com.laomachaguan.Utils.ShareManager;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.TintUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mPLlistview;
import com.laomachaguan.View.myWebView;
import com.lzy.okhttputils.OkHttpUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2016/6/17.
 */
public class ZiXun_Detail extends AppCompatActivity implements OnClickListener, PL_List_Adapter.onHuifuListener {
    private static final String TAG = "Newsd";
    private ImageView back;
    private TextView title, time, user, fasong, plNum;
    private TextView tip;
    private LinearLayout dianzan;
    private myWebView content;
    private mPLlistview PlListVIew;
    private EditText PLText;
    private ImageView dianzanImg;
    private TextView dianzanText;
    private int screenWidth;
    private String id;
    private String page = "1";
    private String endPage = "";
    private ArrayList<HashMap<String, String>> Pllist;
    private String var;
    private PL_List_Adapter adapter;
    private ImageView shoucang, fenxiang2;
    private SharedPreferences sp;
    //第一次加载的评论数量
    private int firstNum = 0;
    //第一次加载的评论map
    private HashMap<String, String> FirstMap;
    //无评论时的header
    private TextView tv;
    private InputMethodManager imm;
    private SHARE_MEDIA[] share_list;
    private ShareAction action;
    private ViewStub video;
    JCVideoPlayerStandard player;
    private boolean needTochange = false;
    private ArrayList<String> arrayList;
    private LinearLayout currentLayout;
    private int currentPosition;
    private String currentId;
    private boolean isPLing = false;
    /*
    活动按钮
     */
    private TextView tv_activity;

    private LinearLayout pinglun, fenxiangb;
    private FrameLayout overlay;
    private UMWeb umWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("咨询详情页启动");
        setContentView(R.layout.zixun_detail);
         StatusBarCompat.compat(this,ContextCompat.getColor(this,R.color.main_color));
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        initView();
        LoadData();
    }

    private void imgReset() {
        content.loadUrl("javascript:(function(){" +
                "var table=document.getElementsByTagName('table');" +
                "for(var i=0;i<table.length;i++){" +
                "var t=table[i];" +
                "t.style.width='100%';" +
                "t.style.margin='auto';" +
                "t.style.display='block';" +
                "}" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "img.style.maxWidth = '100%'; " +
                "var w=img.style.width;" +
                "if(w > '50%') {" +
                "img.style.width='100%';}" +
                "img.style.height = 'auto'; " +
                "img.style.marginBottom=10;" +
                "img.style.marginTop=10;" +
                "img.style.marginLeft='auto';" +
                "img.style.marginRight='auto';" +
                "img.style.display='block';" +
                "}" +
                "var obj1=document.getElementsByTagName('section');" +
                "for(var i=0;i<obj1.length;i++)  " +
                "{"
                + "var sec = obj1[i];  " +
                "sec.style.maxWidth = '100%'; " +
                "var w1=sec.style.width;" +
                "if(w1>'50%'){" +
                "w1='100%';" +
                "}" +
                "sec.style.height = 'auto';" +
                "}" +
                "})()"
        );
    }

    private void initView() {
        overlay = (FrameLayout) findViewById(R.id.frame);
        overlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                isPLing = false;
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                v.setVisibility(View.GONE);
            }
        });
        pinglun = (LinearLayout) findViewById(R.id.pinglun);
        pinglun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.VISIBLE);
                PLText.requestFocus();
                isPLing = false;
                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
//                        imm. showSoftInput(PLText,2);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });
        fenxiangb = (LinearLayout) findViewById(R.id.fenxiangb);
        fenxiangb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(umWeb!=null){
                    new ShareManager().shareWeb(umWeb,ZiXun_Detail.this);
                }

            }
        });

        ((TextView) findViewById(R.id.pltv)).setText(mApplication.ST("评论"));
        ((TextView) findViewById(R.id.fxtv)).setText(mApplication.ST("分享"));

        dianzanImg = (ImageView) findViewById(R.id.zixun_detail_dianzan_img);
        dianzanText = (TextView) findViewById(R.id.zixun_detail_dianzan_text);
        shoucang = (ImageView) findViewById(R.id.zixun_detail_shoucang);
        video = (ViewStub) findViewById(R.id.zixun_detail_viewstub);
        fenxiang2 = (ImageView) findViewById(R.id.zixun_detail_fenxiang2);
        TintUtil.tintDrawableView(fenxiang2,R.drawable.share_small,R.color.main_color);
        Pllist = new ArrayList<>();
        adapter = new PL_List_Adapter(this);
        adapter.setOnHuifuListener(this);
        PLText = (EditText) findViewById(R.id.zixun_detail_apply_edt);
        PLText.setHint(mApplication.ST("写入你的评论(300字以内)"));
        sp = getSharedPreferences("user", MODE_PRIVATE);
        plNum = (TextView) findViewById(R.id.zixun_Detail_appendPLNum);
        fasong = (TextView) findViewById(R.id.zixun_detail_fasong);
        fasong.setText(mApplication.ST("发送"));
        tip = (TextView) findViewById(R.id.zixun_detail_tip);
        tip.setText(mApplication.ST("看完了，点个赞吧"));
        dianzan = (LinearLayout) findViewById(R.id.zixun_detail_dianzan);
        tip.setVisibility(View.GONE);
        dianzan.setVisibility(View.GONE);
        back = (ImageView) this.findViewById(R.id.zixun_detail_leftImg);
        title = (TextView) findViewById(R.id.zixun_detail_title);
        time = (TextView) findViewById(R.id.zixun_detail_time);
        user = (TextView) findViewById(R.id.zixun_detail_user);
        content = (myWebView) findViewById(R.id.zixun_detail_content);
        content.setVisibility(View.GONE);
        WebSettings webSettings = content.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setUseWideViewPort(true);//关键点
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setAppCacheEnabled(true);
//        //提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
//        webSettings.setBlockNetworkImage(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }
//                view.getSettings().setBlockNetworkImage(false);
                imgReset();
                addImageClickListner();
                ProgressUtil.dismiss();
                content.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, ImageUtil.readBitMap(ZiXun_Detail.this, R.drawable.indra));
            }
            //            @Override
//            public void onScaleChanged(WebView view, float oldScale, float newScale) {
//                super.onScaleChanged(view, oldScale, newScale);
//                view.requestFocus();
//                view.requestFocusFromTouch();
//            }
        });
        JavascriptInterface js = new JavascriptInterface(this);
        content.addJavascriptInterface(js, "addUrl");
        content.addJavascriptInterface(js, "imagelistener");
        content.setOnLongClickListener(new myWebView.onLongClickListener() {
            @Override
            public void onLongClcik(String imgUrl) {
                Glide.with(ZiXun_Detail.this).load(imgUrl).asBitmap().skipMemoryCache(true).override(400, 400).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Result result = QrUtils.handleQRCodeFormBitmap(resource);
                        if (result == null) {
                            LogUtil.w("onResourceReady: 不是二维码   " + result);
                        } else {
                            LogUtil.w("onResourceReady: 是二维码   " + result);
                            if (result.getText().toString().startsWith("http")) {
                                Uri uri = Uri.parse(result.getText().toString());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ZiXun_Detail.this, mApplication.ST("无法识别,请确认当前页面是否有二维码图片"), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
        PlListVIew = (mPLlistview) findViewById(R.id.zixun_Detail_PL_listview);
        PlListVIew.footer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlListVIew.footer.setText(mApplication.ST("正在加载"));
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                getPLandSet(FirstMap);
            }
        });
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);


        back.setOnClickListener(this);
        plNum.setOnClickListener(this);
        //分享
        share_list = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
        };
        if (null != (getIntent().getStringExtra("video_url"))) {
            if (!getIntent().getStringExtra("video_url").endsWith("mp3")) {
                video.setLayoutResource(R.layout.video_stub);
                View view = video.inflate();
                player = (JCVideoPlayerStandard) view.findViewById(R.id.zixun_detail_player);
                Log.w(TAG, "initView: player-=-" + player + "  getIntent().getStringExtra(\"video_url\")" + getIntent().getStringExtra("video_url")
                        + "   " + getIntent().getStringExtra("title"));
                player.setUp(getIntent().getStringExtra("video_url"),mApplication.ST(getIntent().getStringExtra("title")) );
                player.titleTextView.setVisibility(View.GONE);
                Glide.with(this).load(getIntent().getStringExtra("image"))
                        .override(screenWidth - DimenUtils.dip2px(this, 10), (screenWidth - DimenUtils.dip2px(this, 10)) / 2)
                        .centerCrop()
                        .into(player.thumbImageView);
            }
//            else {
//                //音频
//                Log.w(TAG, "onBindViewHolder: 显示音频");
//                mAudioManager.release();
//                video.setLayoutResource(R.layout.zixun_detail_audio);
//                final mAudioView mAudioView = (mAudioView) video.inflate();
//                mAudioView.setOnImageClickListener(new mAudioView.onImageClickListener() {
//                    @Override
//                    public void onImageClick(final mAudioView v) {
//                        if (!Network.HttpTest(ZiXun_Detail.this)) {
//                            return;
//                        }
//                        if (mAudioManager.getAudioView() != null && mAudioManager.getAudioView().isPlaying()) {
//                            mAudioManager.release();
//                            mAudioManager.getAudioView().setPlaying(false);
//                            mAudioManager.getAudioView().resetAnim();
//                            if (v == mAudioManager.getAudioView()) {
//                                return;
//                            }
//                        }
//                        if (!mAudioView.isPlaying()) {
//                            Log.w(TAG, "onImageClick: 开始播放");
//                            mAudioManager.playSound(ZiXun_Detail.this, v, getIntent().getStringExtra("video_url"), new MediaPlayer.OnCompletionListener() {
//                                @Override
//                                public void onCompletion(MediaPlayer mp) {
//                                    mAudioView.resetAnim();
//                                }
//                            }, new MediaPlayer.OnPreparedListener() {
//                                @Override
//                                public void onPrepared(MediaPlayer mp) {
//                                    mAudioView.setTime(mAudioManager.mMediaplayer.getDuration() / 1000);
//                                    ProgressUtil.dismiss();
//                                }
//                            });
//
//                        } else {
//                            Log.w(TAG, "onImageClick: 停止播放");
//                            mAudioManager.release();
//                        }
//
//                    }
//                });
//            }

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
//        mAudioManager.release();
        content.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        content.onResume();
    }

    @Override
    public void onHuifuClicked(String id, int p, View v, String name) {
        // TODO: 2016/12/27 评论回复接口
        overlay.setVisibility(View.VISIBLE);
        isPLing = true;
        currentLayout = (LinearLayout) v;
        currentPosition = p;
        currentId = id;
        SpannableString ss = new SpannableString(mApplication.ST("回复 ") + name + " :");
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 3, name.length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        PLText.setHint(ss);
        PLText.requestFocus();


        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

//
    }

    public class JavascriptInterface {
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            if (arrayList != null) {
                Intent intent = new Intent();
                intent.putExtra("array", arrayList);
                intent.putExtra("position", arrayList.indexOf(img));
                intent.setClass(context, ViewPagerActivity.class);
                context.startActivity(intent);
                Log.w(TAG, "openImage: 网页图片地址" + img + "页码：" + arrayList.indexOf(img));
            }

        }

        @android.webkit.JavascriptInterface
        public void addUrlToList(String img) {
            if (arrayList == null) {
                arrayList = new ArrayList<String>();
            }
            arrayList.add(img);
        }
    }


    /**
     * 添加图片监听
     */
    private void addImageClickListner() {
        //
        content.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{window.addUrl.addUrlToList(objs[i].src);"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistener.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.zixun_detail_shoucang://收藏
                if (!new LoginUtil().checkLogin(this)) {
                    return;
                }
                if (!Network.HttpTest(this)) {
                    Toast.makeText(ZiXun_Detail.this, mApplication.ST("请检查网络连接"), Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();

                            try {
                                js.put("newsid", id);
                                js.put("user_id", sp.getString("user_id", ""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String data = OkHttpUtils.post(Constants.News_SC_Ip)
                                    .params("key", ApisSeUtil.getKey()).params("msg",ApisSeUtil.getMsg(js))
                                    .execute().body().string();
                            if (!data.equals("")) {
                                if (AnalyticalJSON.getHashMap(data).get("code").equals("000")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ZiXun_Detail.this,mApplication.ST("添加收藏成功") , Toast.LENGTH_SHORT).show();
                                            v.setSelected(true);
                                            needTochange = true;

                                        }
                                    });
                                } else if (AnalyticalJSON.getHashMap(data).get("code").equals("002")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ZiXun_Detail.this, mApplication.ST("已取消收藏"), Toast.LENGTH_SHORT).show();
                                            v.setSelected(false);
                                            needTochange = true;

                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ZiXun_Detail.this, mApplication.ST("服务器异常"), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case R.id.zixun_detail_fenxiang2://底部分享
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
               fenxiangb.performClick();
                break;
            case R.id.zixun_detail_leftImg://返回
                finish();
                break;
            case R.id.zixun_detail_dianzan://资讯点赞
                if (sp.getString("user_id", "").equals("") && sp.getString("uid", "").equals("")) {
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                    Toast.makeText(ZiXun_Detail.this, mApplication.ST("请先登录"), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js=new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("news_id", id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String data = OkHttpUtils.post(Constants.ZX_DZ_IP).params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js))
                                        .execute().body().string();
                                if (!data.equals("")) {
                                    final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                    if (map != null && map.get("code").equals("000")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzanText.setText((Integer.valueOf(dianzanText.getText().toString()) + 1) + "");
//                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                TintUtil.tintDrawableView(dianzanImg,R.drawable.dianzan1,R.color.main_color);
                                                dianzan.setEnabled(false);
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
//                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                TintUtil.tintDrawableView(dianzanImg,R.drawable.dianzan1,R.color.main_color);
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzan.setEnabled(false);
                                                Toast.makeText(ZiXun_Detail.this, mApplication.ST("已点过赞啦"), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }


                break;
            case R.id.zixun_detail_fasong://发送提交评论
                if(!new LoginUtil().checkLogin(ZiXun_Detail.this)){
                    return;
                }
                if (PLText.getText().toString().trim().equals("")) {
                    Toast.makeText(this, mApplication.ST("请输入评论"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sp.getString("pet_name", "").trim().equals("")) {
                    Toast.makeText(this, mApplication.ST("请完善信息"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, Mine_gerenziliao.class);
                    startActivity(intent);
                    return;
                }
                v.setEnabled(false);
                ProgressUtil.show(this, "", mApplication.ST("正在提交"));
                if (!isPLing) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String content = PLText.getText().toString();
                                JSONObject js=new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("ct_contents", content);
                                    js.put("news_id", id);
                                    js.put("m_id", Constants.M_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String data = OkHttpUtils.post(Constants.News_PL_add_IP)
                                        .params("key", ApisSeUtil.getKey())
                                        .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                                if (data != null & !data.equals("")) {
                                    Log.i(TAG, "run:      data------>" + data);
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                final HashMap<String, String> map = new HashMap<>();
                                                String headurl = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
                                                final String time = TimeUtils.getStrTime(System.currentTimeMillis() + "");
                                                String petname = sp.getString("pet_name", "");
                                                String diazannum = "0";
                                                map.put("user_image", headurl);
                                                map.put("ct_contents", content);
                                                map.put("pet_name", petname);
                                                map.put("ct_ctr", diazannum);
                                                map.put("level",sp.getString("level","0"));
                                                map.put("ct_time", time);
                                                map.put("id", hashMap.get("id"));
                                                map.put("reply", new JSONArray().toString());
                                                PlListVIew.setFocusable(true);
                                                if (adapter.mlist.size() == 0) {
                                                    adapter.mlist.add(0, map);
                                                    adapter.flagList.add(0, false);
                                                    PlListVIew.removeHeaderView(tv);
                                                    PlListVIew.setAdapter(adapter);

                                                } else {
                                                    adapter.mlist.add(0, map);
                                                    adapter.flagList.add(0, false);
                                                    adapter.notifyDataSetChanged();

                                                }
                                                PlListVIew.setSelection(0);
                                                v.setEnabled(true);
                                                firstNum += 1;
                                                plNum.setText(mApplication.ST("评论 " + firstNum));
                                                PLText.setText("");
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                overlay.setVisibility(View.GONE);
                                                Toast.makeText(ZiXun_Detail.this, mApplication.ST("添加评论成功"), Toast.LENGTH_SHORT).show();
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                            Toast.makeText(ZiXun_Detail.this, mApplication.ST("上传评论失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String content = PLText.getText().toString();
                                JSONObject js=new JSONObject();
                                try {
                                    js.put("user_id", sp.getString("user_id", ""));
                                    js.put("ct_contents", content);
                                    js.put("ct_id", currentId);
                                    js.put("m_id", Constants.M_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String data = OkHttpUtils.post(Constants.little_zixun_pl_add_IP)
                                        .params("key", ApisSeUtil.getKey())
                                        .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                                if (!data.equals("")) {
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (currentLayout.getVisibility() == View.GONE) {
                                                    currentLayout.setVisibility(View.VISIBLE);
                                                }
                                                TextView textView = new TextView(ZiXun_Detail.this);
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                layoutParams.setMargins(0, DimenUtils.dip2px(ZiXun_Detail.this, 5), 0, DimenUtils.dip2px(ZiXun_Detail.this, 5));
                                                textView.setLayoutParams(layoutParams);
                                                String pet_name = sp.getString("pet_name", "");
                                                SpannableStringBuilder ssb = new SpannableStringBuilder(pet_name + ":" + content);
                                                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ZiXun_Detail.this, R.color.main_color)), 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                textView.setText(ssb);
                                                currentLayout.addView(textView);
                                                PLText.setText("");
                                                PLText.setHint(mApplication.ST("写入您的评论（300字以内）"));
                                                overlay.setVisibility(View.GONE);
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                PlListVIew.setSelection(currentPosition);
                                                fasong.setEnabled(true);
                                                isPLing = false;
                                                try {
                                                    JSONArray jsonArray = new JSONArray(adapter.mlist.get(currentPosition).get("reply"));
                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("id", hashMap.get("id"));
                                                    jsonObject.put("pet_name", mApplication.ST(pet_name));
                                                    jsonObject.put("ct_contents", mApplication.ST(content));
                                                    jsonObject.put("user_id", sp.getString("user_id", ""));
                                                    jsonArray.put(jsonObject);
                                                    adapter.mlist.get(currentPosition).put("reply", jsonArray.toString());
                                                    adapter.notifyDataSetChanged();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                            Toast.makeText(ZiXun_Detail.this, mApplication.ST("回复提交失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
        }
    }


    public void LoadData() {//加载详情数据
        id = getIntent().getStringExtra("id");
        ProgressUtil.show(this, null, mApplication.ST("正在加载...."));
        if (!id.equals("") && id != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String data1 = null;
                    try {
                        JSONObject js=new JSONObject();
                        js.put("news_id",id);
                        data1 = OkHttpUtils.post(Constants.ZiXun_detail_Ip).tag(TAG)
                                .params("key", ApisSeUtil.getKey())
                                .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ZiXun_Detail.this, mApplication.ST("数据加载失败，请检查网络连接"), Toast.LENGTH_SHORT).show();
                                ProgressUtil.dismiss();
                                return;
                            }
                        });
                    }
                    if (data1 != null && !data1.equals("")) {
                        if ("null".equals(data1)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ZiXun_Detail.this, mApplication.ST("该资讯已下架"), Toast.LENGTH_SHORT).show();
                                    ProgressUtil.dismiss();
                                    finish();
                                }
                            });
                            return;
                        }
                        FirstMap = AnalyticalJSON.getHashMap(data1);
                        if (FirstMap != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fasong.setOnClickListener(ZiXun_Detail.this);
                                    shoucang.setOnClickListener(ZiXun_Detail.this);
                                    fenxiang2.setOnClickListener(ZiXun_Detail.this);
                                    title.setText(mApplication.ST(FirstMap.get("title")));
                                    time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(FirstMap.get("time"))));
                                    user.setText(mApplication.ST("发布人:" + FirstMap.get("issuer")));
                                    content.loadDataWithBaseURL(Constants.IMGDIR + TAG + "/" + TimeUtils.getStrTime(System.currentTimeMillis() / 1000 + "") + ".jpg",mApplication.ST(FirstMap.get("contents"))
                                            , "text/html", "UTF-8", null);
                                    // TODO: 2017/2/21 检测活动外链
                                    initActivity();
                                }
                            });
//

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    content.setText(var1);
                                    tip.setVisibility(View.VISIBLE);
                                    dianzan.setVisibility(View.VISIBLE);
                                    dianzan.setOnClickListener(ZiXun_Detail.this);
                                    Log.w(TAG, "run: like s-=-=-=-=-=" + FirstMap.get("likes"));
                                    dianzanText.setText(FirstMap.get("likes"));
                                    plNum.setVisibility(View.VISIBLE);
                                    plNum.setText(mApplication.ST("评论 " + FirstMap.get("news_comment")));
                                    firstNum = Integer.valueOf(FirstMap.get("news_comment"));
//                                    if (loading.isShown()) {
//                                        loading.clearAnimation();
//                                        loading.setVisibility(GONE);
//                                    }
                                    //使用的是测试分享地址

                                    umWeb=new UMWeb(Constants.FX_host_Ip + TAG + "/id/"+ getIntent().getStringExtra("id"));
                                    umWeb.setTitle(mApplication.ST(FirstMap.get("title")));
                                    umWeb.setThumb(new UMImage(ZiXun_Detail.this,FirstMap.get("image")));
                                    if (!FirstMap.get("abstract").equals("")) {
                                        umWeb.setDescription(mApplication.ST(FirstMap.get("abstract"))) ;
                                    }else{
                                        umWeb.setDescription(mApplication.ST(FirstMap.get("title")));
                                    }
//


                                }
                            });

                            getPLandSet(FirstMap);
                        }
                        ProgressUtil.dismiss();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();
                                Toast.makeText(ZiXun_Detail.this,mApplication.ST("服务器异常，请稍后重试") , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void initActivity() {
        if (!"".equals(FirstMap.get("active"))) {
            tv_activity.setVisibility(View.VISIBLE);
            String active = FirstMap.get("active");
            int index = active.lastIndexOf("?");
            String arg = active.substring(index + 1, active.length());
            LogUtil.e("截取后的参数字段：" + arg);
            String[] args = arg.split("&");
            final String id = args[0].substring(args[0].lastIndexOf("=") + 1);
            final String type = args[1].substring(args[1].lastIndexOf("=") + 1);
            LogUtil.e("字段信息：  id::" + id + "  type::" + type);
            if ("".equals(type)) {
                tv_activity.setVisibility(View.GONE);
                return;
            }
            tv_activity.setText(mApplication.ST(type + "入口"));
            tv_activity.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    switch (type) {
                        case "活动":
                            intent.setClass(ZiXun_Detail.this, activity_Detail.class);
                            intent.putExtra("id", id);
                            break;
                        case "供养":
                            intent.setClass(ZiXun_Detail.this, MainActivity.class);
                            intent.putExtra("pos",2);
                            finish();
                            break;

                    }

                    startActivity(intent);
                }
            });
        }
    }



    private void getPLandSet(final HashMap<String, String> map) {//加载评论并设置
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id",Constants.M_id);
                        js.put("page", page);
                        js.put("news_id", id.equals("") ? getIntent().getStringExtra("id") : id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkHttpUtils.post(Constants.ZiXun_detail_PL_Ip).tag(TAG)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                    if (!data.equals("")) {
                        Pllist = AnalyticalJSON.getList(data, "comment");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ((Pllist != null)) {
//                                    PlListVIew.setFocusable(false);
                                    if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                                        tv = new TextView(ZiXun_Detail.this);
                                        tv.setText(mApplication.ST("还没有评论嘞"));
                                        PlListVIew.addHeaderView(tv);
                                        PlListVIew.footer.setVisibility(View.GONE);
                                        PlListVIew.setAdapter(adapter);

                                        return;
                                    }
                                    if (adapter.mlist.size() == 0) {//添加评论的的时候
                                        adapter.addList(Pllist);
                                        PlListVIew.setAdapter(adapter);
                                        plNum.setText(mApplication.ST("评论 " + map.get("news_comment")));
                                        if (Pllist.size() < 10) {
                                            endPage = page;
                                            PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText(mApplication.ST("点击加载更多"));
                                        }
                                    } else {
                                        adapter.mlist.addAll(Pllist);
                                        boolean flag = false;
                                        for (int i = 0; i < Pllist.size(); i++) {
                                            adapter.flagList.add(flag);
                                        }
                                        adapter.notifyDataSetChanged();
                                        plNum.setText(mApplication.ST("评论 " + map.get("news_comment")));
                                        if (Pllist.size() < 10) {
                                            endPage = page;
                                            PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText(mApplication.ST("点击加载更多"));
                                        }
                                    }
                                } else {
                                    PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                    PlListVIew.footer.setEnabled(false);
                                }

//                                ScrollView scroll= (ScrollView) findViewById(R.id.scroll);
//                                ToastUtil.showToastShort(ZiXun_Detail.this,"getPLandSet评论高度："+String .valueOf(plNum.getY()+"     "+String .valueOf(scroll.getHeight())+"     "+scroll.getMeasuredHeight()), Gravity.TOP);
//                               scroll.scrollTo(0, (int) (plNum.getY()+scroll.getHeight()-plNum.getHeight()));
//                               scroll.smoothScrollTo(0, scroll);
//                                scroll.setScrollY((int) (plNum.getY()+scroll.getHeight()-plNum.getHeight()));
//                                Toast.makeText(ZiXun_Detail.this, ""+scroll.getScrollY(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    @Override
    public void onBackPressed() {
        if (PreferenceUtil.getUserIncetance(this).getString("user_id", "").equals("")) {

        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
        JCVideoPlayer.releaseAllVideos();
        UMShareAPI.get(this).release();
//        ShareManager.release();
    }
}
