package com.laomachaguan.Model_Order;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.Result;
import com.laomachaguan.Common.ViewPagerActivity;
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
import com.laomachaguan.Utils.QrUtils;
import com.laomachaguan.Utils.ScaleImageUtil;
import com.laomachaguan.Utils.ShareManager;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.View.TagDialog;
import com.laomachaguan.View.myWebView;
import com.lzy.okhttputils.OkHttpUtils;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by Administrator on 2016/11/30.
 */
public class Order_detail extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "dishesd";
    private Banner banner;
    private LinearLayout car, root;
    private ImageView fenxiang;
    private ImageView back, shoucang, car_img, dianzan, dianzan_cai;
    private TextView title_title, title, type, sales, money, cost, car_text, msg, percent, dianzan_text, hate_text;
    private ProgressBar progress, progressBar_h;
    private String id;
    private ArrayList<String> list;
    private boolean like = false;
    private boolean hate = false;
    private myWebView webView;
    private ArrayList<String> arrayList;
    private LinearLayout bottomLayout;
    private TextView push_car, pintuan;
    private LinearLayout groupLayout;

    private boolean isPushed = false;
    private boolean isPinTuan = false;
    private boolean hasStock=true;
    //    private List<Runnable> runnables;
    private ScheduledExecutorService ses;
    private UMWeb umWeb;

    private TextView  Choossed;//已选择的规格属性展示
    private TagDialog tagDialog;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.order_detail);
        IntentFilter intentFilter = new IntentFilter("DingDan");
        registerReceiver(receiver, intentFilter);
        root = (LinearLayout) findViewById(R.id.order_detail_root);
        root.setVisibility(View.INVISIBLE);
        id = getIntent().getStringExtra("id");
        initView();
        getData();

    }



    /**
     * 获取详情
     */
    private void getData() {
        if (!Network.HttpTest(this)) {
            return;
        }
        ProgressUtil.show(this, "", "正在加载数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("goods_id", id);
                    js.put("m_id", Constants.M_id);
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.Good_detail)
                            .tag(TAG).params("key", m.K()).params("msg", m.M()).
                                    execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.e("商品详情数据::::"+map);
                                if (map != null) {
                                    if (isPinTuan) {
                                        ArrayList<HashMap<String, String>> groups = AnalyticalJSON.getList_zj(map.get("group"));
                                        if (groups != null) {
                                            if (ses != null) {
                                                ses.shutdownNow();
                                            }
                                            ses = Executors.newScheduledThreadPool(groups.size());
                                        }
                                        groupLayout.removeAllViews();
                                        for (final HashMap<String, String> map1 : groups) {
//                                            if(map1.get("user_id").equals(PreferenceUtil.getUserIncetance(Order_detail.this).getString("user_id",""))){
//                                                continue;
//                                            }
                                            final String endtime = map1.get("end_time");
                                           long offset = TimeUtils.dataOne(endtime) - System.currentTimeMillis();

                                           if(offset>0){
                                                final View view = LayoutInflater.from(Order_detail.this).inflate(R.layout.pintuan_view, null);
                                                Glide.with(Order_detail.this).load(map1.get("user_image")).override(DimenUtils.dip2px(Order_detail.this, 40)
                                                        , DimenUtils.dip2px(Order_detail.this, 40)).centerCrop().into((ImageView) view.findViewById(R.id.pintuan_view_head));
                                                ((TextView) view.findViewById(R.id.pintuan_view_name)).setText(map1.get("pet_name"));
                                                view.findViewById(R.id.pintuan_view_time_num).setTag(map1.get("end_time"));
                                                groupLayout.addView(view);
                                                Runnable runnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                ((TextView) view.findViewById(R.id.pintuan_view_time_num)).setText("还差一人，剩余" + TimeUtils.formatTimeShort( TimeUtils.dataOne(endtime) - System.currentTimeMillis(), false));
                                                            }
                                                        });
                                                    }
                                                };
                                                if (ses != null) {
                                                    ses.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
                                                }
                                                if (PreferenceUtil.getUserIncetance(Order_detail.this).getString("role", "").equals("3")) {
                                                    view.findViewById(R.id.toCantuan).setVisibility(View.GONE);
                                                } else {
                                                    view.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent=new Intent(Order_detail.this,Pintuan_Detail.class);
                                                            intent.putExtra("id",map1.get("id"));
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    view.findViewById(R.id.toCantuan).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
//                                                            // TODO: 2017/6/9 参团
//
                                                            Intent intent=new Intent(Order_detail.this,Pintuan_Detail.class);
                                                            intent.putExtra("id",map1.get("id"));
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        if (groupLayout.getChildCount() != 0) {
                                            findViewById(R.id.haveGroup).setVisibility(View.VISIBLE);//显示参团头部
                                            groupLayout.setVisibility(View.VISIBLE);
                                            View view = new View(Order_detail.this);
                                            View view1 = new View(Order_detail.this);
                                            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                                            view.setBackgroundColor(Color.parseColor("#f0f0f0"));
                                            view.setLayoutParams(ll);
                                            view1.setBackgroundColor(Color.parseColor("#f0f0f0"));
                                            view1.setLayoutParams(ll);
                                            groupLayout.addView(view, 0);
                                            groupLayout.addView(view1);
                                        }
                                    }

                                    setImages(map);
                                    tagDialog=new TagDialog(Order_detail.this,map,Choossed);
                                    tagDialog.setOnTagDialogDismissListener(new TagDialog.onTagDialogDismissListener() {
                                        @Override
                                        public void onDialogDissmiss(String one, String two, int num) {
                                            LogUtil.e("显示已选");
                                            if(two==null||one==null||one.equals("")||(one.equals("无")&&two.equals("无"))){
                                                Choossed.setText("请选择商品属性");
                                            }else{
                                                Choossed.setText("已选:"+(one.equals("无")?"":("\""+one+"\""))+" "+(two.equals("无")?"":("\""+two+"\""))+" \"数量:"+num+"\"");
                                            }
                                        }
                                    });
                                    title.setText(map.get("title"));
                                    type.setText(map.get("name"));
                                    type.setVisibility(View.VISIBLE);
                                    sales.setText("库存"+map.get("stock")+"   总销量:" + map.get("sales"));
                                    if(map.get("stock").equals("0")){
                                        hasStock=false;
                                    }
                                    ((TextView) findViewById(R.id.order_detail_abs)).setText(map.get("abstract"));
                                    if (!map.get("contents1").equals("")) {
                                        msg.setVisibility(View.VISIBLE);
                                        msg.setText(map.get("contents1"));
                                    } else {
                                        msg.setVisibility(View.GONE);
                                    }

                                    money.setText("¥ " + map.get("money"));
                                    push_car.setText("¥ " + map.get("money") + "\n" +
//                                            + (isPushed ? "已放入茶壶" :
                                            "加入茶壶");
                                    if (isPushed) {
//                                        push_car.setEnabled(false);
                                    }
                                    pintuan.setText("¥ " + map.get("price") + "\n" + "一键开团");
                                    pintuan.setTag(map);
                                    if(!hasStock){
                                        car.setEnabled(false);
                                        car_img.setVisibility(View.GONE);
                                        car_text.setText("已售罄");
                                        push_car.setText("已售罄");
                                        pintuan.setBackgroundColor(Color.parseColor("#bbbbbb"));
                                        pintuan.setEnabled(false);
                                    }
                                    if (map.get("cost").equals("0") || map.get("cost").equals("")) {
                                        cost.setVisibility(View.INVISIBLE);
                                    } else {
                                        cost.setVisibility(View.VISIBLE);
                                        cost.setText("¥ " + map.get("cost"));
                                        cost.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                    }
                                    if (!map.get("contents").equals("")) {
                                        webView.setVisibility(View.VISIBLE);
                                        webView.loadDataWithBaseURL("", map.get("contents")
                                                , "text/html", "UTF-8", null);
                                    } else {
                                        webView.setVisibility(View.GONE);
                                    }

                                    int d1 = 0, d2 = 0;
                                    if (null != map.get("likes")) {
                                        dianzan_text.setText(map.get("likes"));
                                        d1 = Integer.valueOf(map.get("likes"));
                                    } else {
                                        dianzan_text.setText("0");
                                    }
                                    if (null != map.get("tread")) {
                                        hate_text.setText(map.get("tread"));
                                        d2 = Integer.valueOf(map.get("tread"));
                                    } else {
                                        hate_text.setText("0");
                                    }
                                    if ((d1 + d2) != 0) {
                                        progressBar_h.setMax(100);
                                        percent.setText(d1 * 100 / (d1 + d2) + "%");
                                        final int p = d1 * 100 / (d1 + d2);
                                        ValueAnimator value = ValueAnimator.ofInt(0, p);
                                        value.setDuration(2000);
                                        value.setInterpolator(new AnticipateOvershootInterpolator());
                                        ValueAnimator.AnimatorUpdateListener an = new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                progressBar_h.setProgress((Integer) animation.getAnimatedValue());

                                            }
                                        };
                                        value.addUpdateListener(an);
                                        value.start();
                                    }
                                    ProgressUtil.dismiss();
                                    root.setVisibility(View.VISIBLE);
                                } else {
                                    ProgressUtil.dismiss();
                                    Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressUtil.dismiss();
                            Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void setImages(HashMap<String, String> map) {
        if (list.isEmpty()) {
            if (!map.get("image1").equals("")) {

                list.add(map.get("image1"));
            }
            if (!map.get("image2").equals("")) {

                list.add(map.get("image2"));
            }
            if (!map.get("image3").equals("")) {

                list.add(map.get("image3"));
            }
        }
        banner.setImages(list);
        banner.start();

        umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/" + id+ "/m_id/" + MD5Utls.stringToMD5(Constants.M_id));
        umWeb.setTitle(map.get("title"));
        umWeb.setDescription(map.get("abstract"));
        umWeb.setThumb(new UMImage(Order_detail.this, map.get("image1")));
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    private void initView() {
        isPushed = getIntent().getBooleanExtra("flag", false);
        isPinTuan = getIntent().getBooleanExtra("open", false);

        list = new ArrayList<>();
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));

        title_title = (TextView) findViewById(R.id.title_title);
        title_title.setText("商品详情");

        shoucang = (ImageView) findViewById(R.id.title_image2);
        shoucang.setVisibility(View.VISIBLE);
        shoucang.setOnClickListener(this);

        fenxiang = (ImageView) findViewById(R.id.title_image3);
        fenxiang.setOnClickListener(this);
        banner = (Banner) findViewById(R.id.banner);
//        ViewGroup.LayoutParams vl=banner.getLayoutParams();
//        vl.height=getResources().getDisplayMetrics().widthPixels*3/4;
//        vl.width=getResources().getDisplayMetrics().widthPixels;
//        banner.setLayoutParams(vl);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ScaleImageUtil.openBigIagmeMode(Order_detail.this, list, position);
            }
        });
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load(path).override(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().widthPixels*3/4)
                        .centerCrop().into(imageView);
            }
        });
        banner.setDelayTime(3000);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR);
        banner.setImages(list);
        car = (LinearLayout) findViewById(R.id.order_detail_car);
        car_img = (ImageView) findViewById(R.id.order_detail_car_img);
        car_img.setImageBitmap(ImageUtil.readBitMap(this, R.mipmap.shopcar));
        car_text = (TextView) findViewById(R.id.order_detail_car_text);
        progress = (ProgressBar) findViewById(R.id.order_detail_progress);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        groupLayout = (LinearLayout) findViewById(R.id.group_layout);
        push_car = (TextView) findViewById(R.id.push_car);
        push_car.setOnClickListener(this);
        pintuan = (TextView) findViewById(R.id.pintuan);
        pintuan.setOnClickListener(this);
        car.setOnClickListener(this);
        if (PreferenceUtil.getUserIncetance(this).getString("role", "").equals("3")) {
            car.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams vm = (ViewGroup.MarginLayoutParams) findViewById(R.id.scroll).getLayoutParams();
            vm.bottomMargin = 0;
            findViewById(R.id.scroll).setLayoutParams(vm);
        } else {
            if (isPinTuan) {
                bottomLayout.setVisibility(View.VISIBLE);
                car.setVisibility(View.VISIBLE);
            } else {
                bottomLayout.setVisibility(View.GONE);
                ViewGroup.MarginLayoutParams vm = (ViewGroup.MarginLayoutParams) findViewById(R.id.scroll).getLayoutParams();
                vm.bottomMargin = 0;
                findViewById(R.id.scroll).setLayoutParams(vm);
            }
        }
        title = (TextView) findViewById(R.id.order_detail_title);
        type = (TextView) findViewById(R.id.order_detail_type);
        sales = (TextView) findViewById(R.id.order_detail_sales);
        money = (TextView) findViewById(R.id.order_detail_true_money);
        cost = (TextView) findViewById(R.id.order_detail_false_money);

        msg = (TextView) findViewById(R.id.order_detail_msg);
        percent = (TextView) findViewById(R.id.order_detail_percent);
        dianzan = (ImageView) findViewById(R.id.order_detail_like_img);
        dianzan.setOnClickListener(this);
        dianzan_text = (TextView) findViewById(R.id.order_detail_like_text);
        dianzan_cai = (ImageView) findViewById(R.id.order_detail_hate_img);
        dianzan_cai.setOnClickListener(this);
        hate_text = (TextView) findViewById(R.id.order_detail_hate_text);
        tintDrawable(dianzan_cai, R.drawable.cai1);
        tintDrawable(dianzan,R.drawable.zan1);
        progressBar_h = (ProgressBar) findViewById(R.id.order_detail_progress_H);
        if(Order_Tab.carIdlist!=null&&Order_Tab.carIdlist.contains(id)){
            isPushed=true;
        }
        if (isPushed) {
//            car_img.setVisibility(View.GONE);
//            car_text.setText("已放入茶壶");
//            car.setEnabled(false);

        }
        Choossed=(TextView) findViewById(R.id.yixuan);
        Choossed.setOnClickListener(this);
        webView = (myWebView) findViewById(R.id.order_web);

        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }
                imgReset();
                addImageClickListner();


            }

        });
        JavascriptInterface js = new JavascriptInterface(this);
        webView.addJavascriptInterface(js, "addUrl");
        webView.addJavascriptInterface(js, "imagelistener");
        webView.setOnLongClickListener(new myWebView.onLongClickListener() {
            @Override
            public void onLongClcik(String imgUrl) {
                Glide.with(Order_detail.this).load(imgUrl).asBitmap().skipMemoryCache(true).override(400, 400).into(new SimpleTarget<Bitmap>() {
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
                                Toast.makeText(Order_detail.this, "无法识别,请确认当前页面是否有二维码图片", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });

    }

    private void tintDrawable(ImageView img, int imgRes) {
//        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), imgRes);
//        Drawable.ConstantState state = drawable.getConstantState();
//        Drawable drawable1 = DrawableCompat.wrap(state == null ? drawable : state.newDrawable().mutate());
//        drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        DrawableCompat.setTint(drawable, ContextCompat.getColor(getApplicationContext(), resId));
//        img.setImageDrawable(drawable);
        img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), imgRes));
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.yixuan:
                if(isPinTuan){
                    pintuan.performClick();
                }else{
                    car.performClick();
                }

                break;
            case R.id.pintuan:
                if(!LoginUtil.checkLogin(this)){
                    return;
                }
                tagDialog.show(TagDialog.PINGYUAN);
//                HashMap<String, String> m=((HashMap<String, String>) v.getTag());
//                if(m!=null){
//                    Intent intent = new Intent(this, Dingdan_commit.class);
//                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("title", m.get("title"));
//                    map.put("num", "1");
//                    map.put("money", m.get("price"));
//                    map.put("cost", m.get("money"));
//                    map.put("id", id);
//                    list.add(map);
//                    intent.putExtra("list", list);
//                    intent.putExtra("KT", true);
//                    startActivity(intent);
//                }else{
//                    ToastUtil.showToastShort("数据加载中，请稍等");
//                }

                break;
            case R.id.push_car:
                car.performClick();
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.title_image3:
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, Order_detail.this);
                }
                break;
            case R.id.title_image2:
                if(!LoginUtil.checkLogin(this)){
                    return;
                }
                if (!Network.HttpTest(this)) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js = new JSONObject();
                            js.put("user_id", PreferenceUtil.getUserIncetance(Order_detail.this)
                                    .getString("user_id", ""));
                            js.put("order_id", id);
                            String data = OkHttpUtils.post(Constants.Order_shoucang)
                                    .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js))
                                    .execute().body().string();
                            if (!data.equals("")) {
                                if (AnalyticalJSON.getHashMap(data) != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Order_detail.this, "添加收藏成功", Toast.LENGTH_SHORT).show();
                                            v.setSelected(true);


                                        }
                                    });
                                } else if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Order_detail.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                                            v.setSelected(false);


                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Order_detail.this, "服务器异常", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.order_detail_car://加入购物车
                tagDialog.show(TagDialog.NORMAl,car,id,push_car);

                break;
            case R.id.order_detail_like_img:
                if (!like && !hate) {
                    dianzan.setEnabled(false);
                    if (!LoginUtil.checkLogin(Order_detail.this)) {
                        return;
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = new JSONObject();
                                    js.put("user_id", PreferenceUtil.getUserIncetance(Order_detail.this)
                                            .getString("user_id", ""));
                                    js.put("order_id", id);
                                    js.put("type", "1");
                                    js.put("m_id", Constants.M_id);
                                    String data = OkHttpUtils.post(Constants.Order_like)
                                            .params("key", ApisSeUtil.getKey())
                                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                    if (!data.equals("")) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                        if ((map != null && ("000").equals(map.get("code")))
                                                ) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tintDrawable(dianzan, R.drawable.zan2);
                                                    dianzan_text.setText((Integer.valueOf(dianzan_text.getText().toString()) + 1) + "");
                                                    dianzan_text.setTextColor(getResources().getColor(R.color.main_color));

                                                    int d1 = Integer.valueOf(dianzan_text.getText().toString());
                                                    int d2 = Integer.valueOf(hate_text.getText().toString());
                                                    percent.setText(((d1 * 100) / (d1 + d2)) + "%");
                                                    progressBar_h.setMax((d1 + d2) * 100);
                                                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, d1 * 100);
                                                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                        @Override
                                                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                            int v = (int) valueAnimator.getAnimatedValue();
                                                            progressBar_h.setProgress(v);
                                                        }
                                                    });
                                                    valueAnimator.setDuration(1000);
                                                    valueAnimator.start();
                                                    like = true;
                                                }
                                            });

                                        } else if ((map != null && "002".equals(map.get("code")))) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    like = true;
                                                    tintDrawable(dianzan, R.drawable.zan2);
                                                    dianzan_text.setTextColor(getResources().getColor(R.color.main_color));
                                                    Toast.makeText(Order_detail.this, "您已对该商品评价过了", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzan.setEnabled(true);
                                                Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dianzan.setEnabled(true);
                                            Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                }
                break;
            case R.id.order_detail_hate_img:
                if (!like && !hate) {
                    dianzan_cai.setEnabled(false);
                    if (!LoginUtil.checkLogin(Order_detail.this)) {
                        return;
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = new JSONObject();
                                    js.put("user_id", PreferenceUtil.getUserIncetance(Order_detail.this)
                                            .getString("user_id", ""));
                                    js.put("order_id", id);
                                    js.put("type", "2");
                                    js.put("m_id", Constants.M_id);
                                    String data = OkHttpUtils.post(Constants.Order_like)
                                            .params("key", ApisSeUtil.getKey())
                                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                    if (!data.equals("")) {
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                        if ((map != null && ("000").equals(map.get("code")))
                                                ) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tintDrawable(dianzan_cai, R.drawable.cai2);
                                                    hate_text.setText((Integer.valueOf(hate_text.getText().toString()) + 1) + "");
                                                    hate_text.setTextColor(getResources().getColor(R.color.main_color));
                                                    int d1 = Integer.valueOf(dianzan_text.getText().toString());
                                                    int d2 = Integer.valueOf(hate_text.getText().toString());
                                                    percent.setText(((d1 * 100) / (d1 + d2)) + "%");
                                                    progressBar_h.setMax((d1 + d2) * 100);
                                                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, d1 * 100);
                                                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                        @Override
                                                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                            int v = (int) valueAnimator.getAnimatedValue();
                                                            progressBar_h.setProgress(v);
                                                        }
                                                    });
                                                    valueAnimator.setDuration(2000);
                                                    valueAnimator.start();
                                                    hate = true;
                                                }
                                            });

                                        } else if ((map != null && "002".equals(map.get("code")))) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    hate = true;

                                                    tintDrawable(dianzan_cai, R.drawable.cai2);
                                                    hate_text.setTextColor(getResources().getColor(R.color.main_color));
                                                    Toast.makeText(Order_detail.this, "您已对该商品评价过了", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzan_cai.setEnabled(true);
                                                Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dianzan_cai.setEnabled(true);
                                            Toast.makeText(Order_detail.this, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
        if (ses != null) {
            ses.shutdownNow();
        }
        unregisterReceiver(receiver);

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
                LogUtil.w("openImage: 网页图片地址" + img + "页码：" + arrayList.indexOf(img));
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
        webView.loadUrl("javascript:(function(){" +
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

    private void imgReset() {
        webView.loadUrl("javascript:(function(){" +
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
}
