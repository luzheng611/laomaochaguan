package com.laomachaguan.Common;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.laomachaguan.Adapter.PL_List_Adapter;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.MD5Utls;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.ScaleImageUtil;
import com.laomachaguan.Utils.ShareManager;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.JCVideoPlayerStandardShowShareButtonAfterFullscreen;
import com.laomachaguan.View.mAudioManager;
import com.laomachaguan.View.mAudioView;
import com.laomachaguan.View.mPLlistview;
import com.lzy.okhttputils.OkHttpUtils;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Created by Administrator on 2016/11/7.
 */
public class Home_Zixun_detail extends AppCompatActivity implements View.OnClickListener, PL_List_Adapter.onHuifuListener {
    private ImageView back, dianzanImg;
    private static final String TAG = "Home_Zixun_detail";
    private TextView title, time, user, fasong, plNum;
    private TextView dianzan;
    private TextView content;
    private FrameLayout options;
    private mPLlistview PlListVIew;
    private EditText PLText;
    private String id;
    private String page = "1";
    private String endPage = "";
    private ArrayList<HashMap<String, String>> Pllist;
    //    private AvatarImageView head;
    private PL_List_Adapter adapter;
    private ImageView shoucang;
    //    private ProgressDialog progressDialog;
    private SharedPreferences sp;
    //第一次加载的评论数量
    private int firstNum = 0;
    //第一次加载的评论map
    private HashMap<String, String> FirstMap;
    //无评论时的header
    private TextView tv;
    private InputMethodManager imm;
//    private ListView listView;
    private ValueAnimator va;
    JCVideoPlayerStandardShowShareButtonAfterFullscreen player;
    private boolean needTochange = false;
    private LinearLayout dianzanLayout;
    private int matchWidth;
    private imgAdapter adapter1;
    private LinearLayout bootomLayout, FirstLayout;
    private boolean isPLing = false;
    private int currentPosition;
    private String currentId = "";
    private LinearLayout currentLayout;
    private LinearLayout pinglun_bottom;
    private TextView dianzan_bottom;
    private ImageView zhifu;
    private int screenHeight, screenWidth;
    private ZFAdapter zfAdapter;
    private Dialog dialog;
    private long downtime;
    float _x, _y;
    private String User_id;
    private FrameLayout overlay;
    private UMWeb umWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        initView();
        LoadData();
    }

    private void initView() {
        overlay = (FrameLayout) findViewById(R.id.overlay);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PLText.setHint(mApplication.ST("请输入您的评论"));
                isPLing = false;
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                v.setVisibility(View.GONE);
                FirstLayout.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.fenxiangb).setOnClickListener(this);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        zhifu = (ImageView) findViewById(R.id.zhifu);
        matchWidth = getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(this, 10);
//        listView = (ListView) findViewById(R.id.zixun_item_iamges);
        bootomLayout = (LinearLayout) findViewById(R.id.zixun_bottomLayout);
        FirstLayout = (LinearLayout) findViewById(R.id.firstLayout);
        options = (FrameLayout) findViewById(R.id.zixun_item_option);
        shoucang = (ImageView) findViewById(R.id.zixun_item_shoucang);
        shoucang.setOnClickListener(this);
        Pllist = new ArrayList<>();
        adapter = new PL_List_Adapter(this);
        adapter.setOnHuifuListener(this);
        PLText = (EditText) findViewById(R.id.zixun_item_input);
        sp = PreferenceUtil.getUserIncetance(this);
        fasong = (TextView) findViewById(R.id.zixun_item_fasong);

        back = (ImageView) this.findViewById(R.id.zixun_item_back);
        title = (TextView) findViewById(R.id.zixun_item_title);
        time = (TextView) findViewById(R.id.zixun_item_time);
        user = (TextView) findViewById(R.id.zixun_item_name);
        content = (TextView) findViewById(R.id.zixun_item_content);

        PlListVIew = (mPLlistview) findViewById(R.id.zixun_item_plListview);
        PlListVIew.setFooterDividersEnabled(false);
        PlListVIew.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlListVIew.footer.setText("正在加载");
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                getPLandSet(FirstMap);
            }
        });
        plNum = (TextView) findViewById(R.id.zixun_item_pinlunNum);
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        back.setOnClickListener(this);
        fasong.setOnClickListener(this);
        shoucang.setOnClickListener(this);
        dianzanLayout = (LinearLayout) findViewById(R.id.zixun_item_dianzan);
        dianzanImg = (ImageView) findViewById(R.id.zixun_item_dianzan_img);
        dianzan = (TextView) findViewById(R.id.zixun_item_dianzan_text);
        pinglun_bottom = (LinearLayout) findViewById(R.id.pinglun);
        dianzan_bottom = (TextView) findViewById(R.id.jubao);
        pinglun_bottom.setOnClickListener(this);
        dianzan_bottom.setOnClickListener(this);
        if ("1".equals(getIntent().getStringExtra("type"))) {
            shoucang.setSelected(true);
        }
        if (getIntent().getStringExtra("name") != null) {
            user.setText(getIntent().getStringExtra("name"));
        }
        zhifu.setOnClickListener(this);
        zhifu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        _x = (event.getRawX() - v.getX());
                        _y = (event.getRawY() - v.getY());
                        downtime = System.currentTimeMillis();
                        v.setSelected(true);
                        View view1 = (View) v.getParent();
                        if (view1 instanceof ViewPager) {
                            ((ViewPager) view1).requestDisallowInterceptTouchEvent(true);//// TODO: 2016/12/12 禁止拦截事件 ,由子控件处理事件
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveFllow(v, x, y, _x, _y);//// TODO: 2016/12/12 跟随手指滑动
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - downtime < 300) {
                            zhifu.performClick();
                        }
                        v.getParent().requestDisallowInterceptTouchEvent(false);//// TODO: 2016/12/12 允许拦截事件
                        checkHozatal(v);//// TODO: 2016/12/12判断左右边距

                        break;
                }
                return true;
            }
        });
        if (PreferenceUtil.getUserIncetance(this).getString("role", "").equals("3")) {
            findViewById(R.id.jubao).setVisibility(View.INVISIBLE);
        }
    }

    private void checkHozatal(final View v) {
        int left = (int) v.getX();
        Log.w(TAG, "onTouch: " + left);
        va = ValueAnimator.ofInt(60);
        va.setDuration(500);
        va.setInterpolator(new BounceInterpolator());
        if (left <= screenWidth / 2) {
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX(v.getX() - ((int) animation.getAnimatedValue()));
                    if (v.getX() <= 0) {
                        v.setX(0);
                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        } else {
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX(v.getX() + ((int) animation.getAnimatedValue()));
                    if (v.getX() >= screenWidth - v.getWidth()) {
                        v.setX(screenWidth - v.getWidth());
                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        }
        va.start();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

//            case R.id.zhifu:
//                View view = null;
//                getPay();
//                AlertDialog.Builder b = new AlertDialog.Builder(this);
//                view = LayoutInflater.from(this).inflate(R.layout.erweima_dialog, null);
//                ListView listView1 = (ListView) view.findViewById(R.id.zhifu_listview);
//                ProgressBar p = (ProgressBar) view.findViewById(R.id.zhifu_progress);
//                listView1.setEmptyView(p);
//                if (zfAdapter == null)
//                    zfAdapter = new ZFAdapter();
//                listView1.setAdapter(zfAdapter);
//                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Intent intent = new Intent(Home_Zixun_detail.this, QRActivity.class);
//                        intent.putExtra("url", zfAdapter.list.get(position).get("pay"));
//                        intent.putExtra("name", zfAdapter.list.get(position).get("name"));
//                        Toast.makeText(Home_Zixun_detail.this, "长按图片进行操作", Toast.LENGTH_SHORT).show();
//                        startActivity(intent);
//                    }
//                });
//                b.setView(view);
//                dialog = b.create();
//                view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                Window window = dialog.getWindow();
//                window.setWindowAnimations(R.style.dialogWindowAnim);
//                window.setBackgroundDrawableResource(R.color.vifrification);
//                window.setDimAmount(0.4f);
//                window.setGravity(Gravity.BOTTOM);
//                dialog.show();
//                WindowManager.LayoutParams wl = window.getAttributes();
//                wl.width = getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(Home_Zixun_detail.this, 20);
//                wl.height = getResources().getDisplayMetrics().heightPixels * 3 / 5;
//                window.setAttributes(wl);
//                break;
            case R.id.fenxiangb:
                findViewById(R.id.zixun_item_share).performClick();
                break;
            case R.id.zixun_item_share:
                if (umWeb != null) {
                    new ShareManager().shareWeb(umWeb, Home_Zixun_detail.this);
                }
                break;
            case R.id.jubao:
                if (LoginUtil.checkLogin(this)) {
                    Intent intent = new Intent(this, JuBaoActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.pinglun:
                if (LoginUtil.checkLogin(this)) {
                    overlay.setVisibility(View.VISIBLE);
                    PLText.setHint("请输入您的评论");
                    PLText.requestFocus();
                    FirstLayout.setVisibility(View.GONE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
            case R.id.zixun_item_shoucang://收藏
                if (!LoginUtil.checkLogin(this)) {
                    return;

                }
                if (!Network.HttpTest(this)) {
                    ToastUtil.showToastShort("请检查网络连接");
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js = new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("draft_id", id);
                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m = ApisSeUtil.i(js);
                            String data = OkHttpUtils.post(Constants.Zixun_shoucang_cancle_IP)
                                    .params("key", m.K())
                                    .params("msg", m.M()).execute().body().string();
                            if (!data.equals("")) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToastShort("添加收藏成功");
                                            v.setSelected(true);
                                            needTochange = true;

                                        }
                                    });
                                } else if (map != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToastShort("已取消收藏");
                                            v.setSelected(false);
                                            needTochange = true;

                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToastShort("服务器异常");
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


            case R.id.zixun_item_back://返回
                finish();
                break;

            case R.id.zixun_item_fasong://发送提交评论
                if (!LoginUtil.checkLogin(this)) {
                    return;
                }
                if (PLText.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请输入评论");
                    return;
                }
                v.setEnabled(false);
                ProgressUtil.show(this, "", "正在提交");
                if (!isPLing) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String content = PLText.getText().toString();
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("m_id", Constants.M_id);
                                    js.put("draft_id", id);
                                    js.put("ct_contents", content);
                                    js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                final String data = OkHttpUtils.post(Constants.Zixun_commitPL_IP)
                                        .params("key", m.K())
                                        .params("msg", m.M()).execute().body().string();
                                if (!data.equals("")) {
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
                                                map.put("user_id", sp.getString("user_id", ""));
                                                map.put("ct_time", time);
                                                map.put("id", hashMap.get("id"));
                                                map.put("reply", new JSONArray().toString());
//                                                map.put("realname",sp.getString("ident","1"));
                                                PlListVIew.setFocusable(true);
                                                if (adapter.mlist.size() == 0) {
                                                    adapter.mlist.add(0, map);
                                                    adapter.flagList.add(0, false);
                                                    PlListVIew.removeHeaderView(tv);
                                                    PlListVIew.setAdapter(adapter);
                                                    PlListVIew.footer.setText("没有更多数据了");
                                                    PlListVIew.setEnabled(false);
                                                } else {
                                                    adapter.mlist.add(0, map);
                                                    adapter.flagList.add(0, false);
                                                    adapter.notifyDataSetChanged();
                                                    PlListVIew.setEnabled(true);
                                                }
                                                PlListVIew.setSelection(0);
                                                v.setEnabled(true);
                                                firstNum += 1;
                                                plNum.setText("评论 " + firstNum);
                                                PLText.setText("");
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                                overlay.setVisibility(View.GONE);
                                                FirstLayout.setVisibility(View.VISIBLE);
                                                ToastUtil.showToastShort("添加评论成功");
                                                ProgressUtil.dismiss();
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.setEnabled(true);
                                            ToastUtil.showToastShort("提交评论失败，请重新尝试");
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
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("m_id", Constants.M_id);
                                    js.put("ct_id", currentId);
                                    js.put("ct_contents", content);
                                    js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                final String data = OkHttpUtils.post(Constants.little_zixun_pl_add_IP)
                                        .params("key", m.K())
                                        .params("msg", m.M()).execute().body().string();
                                if (!data.equals("")) {
                                    final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                    if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (currentLayout.getVisibility() == View.GONE) {
                                                    currentLayout.setVisibility(View.VISIBLE);
                                                }
                                                TextView textView = new TextView(Home_Zixun_detail.this);
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                layoutParams.setMargins(0, DimenUtils.dip2px(Home_Zixun_detail.this, 5), 0, DimenUtils.dip2px(Home_Zixun_detail.this, 5));
                                                textView.setLayoutParams(layoutParams);
                                                String pet_name = sp.getString("pet_name", "");
                                                SpannableStringBuilder ssb = new SpannableStringBuilder(pet_name + ":" + content);
//                                                ssb.setSpan(new ClickableSpan() {
//                                                    @Override
//                                                    public void onClick(View widget) {
//                                                        Intent intent = new Intent(Home_Zixun_detail.this, User_Detail.class);
//                                                        startActivity(intent);
//                                                    }
//                                                }, 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                textView.setText(ssb);
                                                currentLayout.addView(textView);
                                                overlay.setVisibility(View.GONE);
                                                FirstLayout.setVisibility(View.VISIBLE);
                                                PLText.setText("");
                                                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
//                                                listView.setSelection(currentPosition);
                                                fasong.setEnabled(true);
                                                isPLing = false;
                                                try {
                                                    JSONArray jsonArray = new JSONArray(adapter.mlist.get(currentPosition).get("reply"));
                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("id", hashMap.get("id"));
                                                    jsonObject.put("pet_name", pet_name);
                                                    jsonObject.put("ct_contents", content);
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
                                            ToastUtil.showToastShort("回复提交失败，请重新尝试");
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

            case R.id.zixun_item_dianzan:
                if (!LoginUtil.checkLogin(this)) {
                    return;
                }
                dianzanLayout.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js = new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("draft_id", id);

                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m = ApisSeUtil.i(js);
                            String data = OkHttpUtils.post(Constants.Zixun_dianzan_IP)
                                    .params("key", m.K())
                                    .params("msg", m.M()).execute().body().string();
                            if (!data.equals("")) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null && map.get("code").equals("000")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dianzan.setText((Integer.valueOf(dianzan.getText().toString()) + 1) + "");
                                            dianzanLayout.setSelected(true);

                                        }
                                    });

                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            ToastUtil.showToastShort("已点过赞啦");
                                            dianzanLayout.setSelected(true);

                                        }
                                    });

                                }
                            }
                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dianzanLayout.setEnabled(true);
                                }
                            });
                            e.printStackTrace();
                        }

                    }
                }).start();
                break;
        }
    }

    /**
     * 检查网络状态
     */
    private void checkNetwork() {
        if (!Network.HttpTest(this)) {
            ToastUtil.showToastShort("网络连接失败，请检查网络");
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    public void LoadData() {//加载详情数据
        checkNetwork();
        id = getIntent().getStringExtra("id");

        ProgressUtil.show(this, "", "正在加载");
        if (!id.equals("") && id != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String data1 = null;
                    try {
                        JSONObject js = new JSONObject();
                        try {
                            js.put("m_id", Constants.M_id);
                            js.put("id", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m = ApisSeUtil.i(js);
                        data1 = OkHttpUtils.post(Constants.Zixun_Detail_IP).tag(TAG)
                                .params("key", m.K())
                                .params("msg", m.M()).execute().body().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "run: data1-------->" + data1);
                    if (data1 != null && !data1.equals("")) {
                        FirstMap = AnalyticalJSON.getHashMap(data1);
                        if (FirstMap != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setContentView(R.layout.home_zixun_detail);
                                    initView();
//                                    if(FirstMap.get("realname").equals("1")){
//                                        zhifu.setVisibility(View.GONE);
//                                    }

                                    User_id = FirstMap.get("user_id");
                                    title.setText(FirstMap.get("title"));
                                    time.setText(TimeUtils.getTrueTimeStr(FirstMap.get("time")));
                                    user.setText(FirstMap.get("pet_name"));
                                    content.setText(FirstMap.get("contents"));
                                    dianzanLayout.setVisibility(View.VISIBLE);
                                    dianzan.setText(FirstMap.get("likes"));
                                    plNum.setVisibility(View.VISIBLE);
                                    plNum.setText("评论 " + FirstMap.get("draft_comment"));
                                    if (FirstMap.get("draft_comment") != null && !FirstMap.get("draft_comment").equals("null")) {
                                        firstNum = Integer.valueOf(FirstMap.get("draft_comment"));
                                    }
                                    if (FirstMap.get("options").endsWith(".mp4")) {
                                        //视频
                                        Log.w(TAG, "onBindViewHolder: 显示视频");
                                        JCVideoPlayerStandardShowShareButtonAfterFullscreen j = new JCVideoPlayerStandardShowShareButtonAfterFullscreen(Home_Zixun_detail.this);
                                        FrameLayout.LayoutParams l = new FrameLayout.LayoutParams(matchWidth, matchWidth*9/16);
                                        options.addView(j);
                                        j.setUp(FirstMap.get("options"), "  ");
                                        j.setLayoutParams(l);
                                        if(!"".equals(FirstMap.get("image1"))){
                                            Glide.with(Home_Zixun_detail.this).load(FirstMap.get("image1")).override(matchWidth, matchWidth*16/9).centerCrop().into(j.thumbImageView);
                                        }else{
                                            Glide.with(Home_Zixun_detail.this).load(R.drawable.player_bg)
                                                    .centerCrop().into(j.thumbImageView);
                                        }

                                    } else if (FirstMap.get("options").endsWith(".mp3")) {
                                        //音频
                                        Log.w(TAG, "onBindViewHolder: 显示音频");
                                        mAudioManager.release();
                                        final mAudioView mAudioView = new mAudioView(Home_Zixun_detail.this);
                                        mAudioView.setOnImageClickListener(new mAudioView.onImageClickListener() {
                                            @Override
                                            public void onImageClick(final mAudioView v) {
                                                if (mAudioManager.getAudioView() != null && mAudioManager.getAudioView().isPlaying()) {
                                                    mAudioManager.release();
                                                    mAudioManager.getAudioView().setPlaying(false);
                                                    mAudioManager.getAudioView().resetAnim();
                                                    if (v == mAudioManager.getAudioView()) {
                                                        return;
                                                    }
                                                }
                                                if (!mAudioView.isPlaying()) {
                                                    Log.w(TAG, "onImageClick: 开始播放");
                                                    mAudioManager.playSound(v, FirstMap.get("options"), new MediaPlayer.OnCompletionListener() {
                                                        @Override
                                                        public void onCompletion(MediaPlayer mp) {
                                                            mAudioView.resetAnim();
                                                        }
                                                    }, new MediaPlayer.OnPreparedListener() {
                                                        @Override
                                                        public void onPrepared(MediaPlayer mp) {
                                                            mAudioView.setTime(mAudioManager.mMediaplayer.getDuration() / 1000);
                                                        }
                                                    });

                                                } else {
                                                    Log.w(TAG, "onImageClick: 停止播放");
                                                    mAudioManager.release();
                                                }

                                            }
                                        });
                                        options.addView(mAudioView);

                                    }


                                    ArrayList<HashMap<String, String>> l1 = AnalyticalJSON.getList_zj(FirstMap.get("image"));
                                    final ArrayList<String> l = new ArrayList<String>();
                                    if (l1 != null) {
                                        for (HashMap<String, String> m : l1) {
                                            l.add(m.get("url"));
                                        }
                                    }
                                    umWeb = new UMWeb(Constants.host_Ip + "/cg.php/Index/newsd/id/" + id + "/m_id/" + MD5Utls.stringToMD5(Constants.M_id));
                                    LogUtil.e("MD5::::" + MD5Utls.stringToMD5(Constants.M_id));
                                    if (!FirstMap.get("image1").equals("")) {
                                        umWeb.setThumb(new UMImage(Home_Zixun_detail.this, FirstMap.get("image1")));
                                    } else if (!l.isEmpty()) {
                                        umWeb.setThumb(new UMImage(Home_Zixun_detail.this, l.get(0)));
                                    } else {
                                        umWeb.setThumb(new UMImage(Home_Zixun_detail.this, R.drawable.indra_jpg));
                                    }
                                    umWeb.setDescription(FirstMap.get("contents"));
                                    umWeb.setTitle(FirstMap.get("title"));


                                    for(int i=0;i<l.size();i++){
                                        final ImageView imageView=new ImageView(Home_Zixun_detail.this);
                                        final int screenWidth=getResources().getDisplayMetrics().widthPixels-DimenUtils.dip2px(Home_Zixun_detail.this,10);
                                        final LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        if(i==0){
                                            ll.topMargin=DimenUtils.dip2px(Home_Zixun_detail.this,5);
                                            ll.bottomMargin=DimenUtils.dip2px(Home_Zixun_detail.this,5);
                                        }else {
                                            ll.bottomMargin=DimenUtils.dip2px(Home_Zixun_detail.this,5);
                                        }
                                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        imageView.setLayoutParams(ll);
                                        Glide.with(Home_Zixun_detail.this).load(l.get(i)).asBitmap().into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                int imageHeight= (int) (screenWidth*(resource.getHeight()*1f/resource.getWidth()*1f));
                                                ll.height=imageHeight;
                                                imageView.setImageBitmap(resource);
                                            }

                                        });
                                        ((LinearLayout) findViewById(R.id.imageLayout)).addView(imageView);
                                        final int finalI = i;
                                        imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ScaleImageUtil.openBigIagmeMode(Home_Zixun_detail.this,l, finalI);
                                            }
                                        });
                                    }

//                                    if (l != null && l.size() != 0) {
//                                        if (adapter1 == null) {
//                                            adapter1 = new imgAdapter(Home_Zixun_detail.this, l);
//                                            listView.setAdapter(adapter1);
//                                        } else {
//                                            adapter1.list.clear();
//                                            adapter1.list.addAll(l);
//                                        }
//
//                                    } else {
//                                        listView.setVisibility(View.GONE);
//                                    }

                                    ProgressUtil.dismiss();


                                }
                            });
                            getPLandSet(FirstMap);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    ToastUtil.showToastShort("服务器异常，请稍后重试");
                                }
                            });
                        }

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort("服务器异常，请稍后重试");
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void getPLandSet(final HashMap<String, String> map) {//加载评论并设置
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("page", page);
                        js.put("draft_id", id.equals("") ? getIntent().getStringExtra("id") : id);
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.Zixun_PL_IP).tag(TAG)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!data.equals("")) {
                        Pllist = AnalyticalJSON.getList(data, "comment");
                        if ((Pllist != null)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PlListVIew.setFocusable(false);
                                    if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                                        tv = new TextView(Home_Zixun_detail.this);
                                        tv.setText("还没有评论嘞");
                                        PlListVIew.addHeaderView(tv);
                                        PlListVIew.footer.setVisibility(View.GONE);
                                        PlListVIew.setAdapter(adapter);

                                        return;
                                    }
                                    if (adapter.mlist.size() == 0) {//添加评论的的时候
                                        adapter.addList(Pllist);
                                        PlListVIew.setAdapter(adapter);
                                        if (Pllist.size() < 10) {
                                            endPage = page;
                                            PlListVIew.footer.setText("没有更多数据了");
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText("点击加载更多");
                                        }
                                    } else {
                                        adapter.mlist.addAll(Pllist);
                                        boolean flag = false;

                                        for (int i = 0; i < Pllist.size(); i++) {
                                            adapter.flagList.add(flag);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (Pllist.size() < 10) {
                                            endPage = page;
                                            PlListVIew.footer.setText("没有更多评论了");
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText("点击加载更多");
                                        }
                                    }
                                    plNum.setText("评论 " + adapter.mlist.size());
                                    firstNum = adapter.mlist.size();
                                }
                            });


                        } else {
                            PlListVIew.footer.setEnabled(false);
                            PlListVIew.footer.setText("没有更多评论了");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void moveFllow(View v, float x, float y, float startX, float startY) {


        if (v.getY() >= 0 && v.getY() <= screenHeight - v.getHeight() - DimenUtils.dip2px(this, 115)) {
            v.setY(y - startY);
            if (v.getY() < 0) {
                v.setY(0);
            } else if (v.getY() > screenHeight - v.getHeight() - DimenUtils.dip2px(this, 115)) {
                v.setY(screenHeight - v.getHeight() - DimenUtils.dip2px(this, 115));
            }
        }

        if (v.getX() >= 0 && v.getX() <= (screenWidth - v.getWidth())) {
            v.setX(x - startX);
            if (v.getX() < DimenUtils.dip2px(this, 10)) {
                v.setX(DimenUtils.dip2px(this, 10));
            } else if (v.getX() > (screenWidth - v.getWidth())) {
                v.setX((screenWidth - v.getWidth()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
        JCVideoPlayer.releaseAllVideos();
        mAudioManager.release();
    }

    //    private void getPay() {
//        checkNetwork();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String data = null;
//                try {
//                    JSONObject js = new JSONObject();
//                    try {
//                        js.put("m_id", Constants.M_id);
//
//                        js.put("user_id",User_id );
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    ApisSeUtil.M m=ApisSeUtil.i(js);
//                    data = OkHttpUtils.post(Constants.little_erweima_get__IP)
//                            .params("key",m.K())
//                            .params("msg",m.M())
//                            .execute().body().string();
//                    if (!data.equals("")) {
//                        final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (list != null) {
//                                    if (list.size() == 0) {
//                                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
//                                        ;
//                                        Toast.makeText(Home_Zixun_detail.this, "对方暂时还未设置付款方式", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        zfAdapter.setList(list);
//                                        zfAdapter.notifyDataSetChanged();
//                                    }
//                                } else {
//                                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
//                                    Toast.makeText(Home_Zixun_detail.this, "对方暂时还未设置付款方式", Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        });
//
//                    }
//                } catch (Exception e) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(Home_Zixun_detail.this, "加载超时，请检查网络或重新尝试", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
    @Override
    public void onHuifuClicked(String id, int p, View v, String pet_name) {
        // TODO: 2016/12/27 评论回复接口
        FirstLayout.setVisibility(View.GONE);
        overlay.setVisibility(View.VISIBLE);
        isPLing = true;
        currentLayout = (LinearLayout) v;
        currentPosition = p;
        currentId = id;
        SpannableString ss = new SpannableString("回复 " + pet_name + " :");
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 3, pet_name.length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        PLText.setHint(ss);
        PLText.requestFocus();
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private static class imgAdapter extends BaseAdapter {
        private ArrayList<String> list;
        private int imageWidth;
        private Activity a;
        private WeakReference w;

        public imgAdapter(Activity a1, ArrayList<String> list) {
            super();
            this.list = list;
            w = new WeakReference<Activity>(a1);
            this.a = (Activity) w.get();
//            a=a1;
            imageWidth = this.a.getResources().getDisplayMetrics().widthPixels - DimenUtils.dip2px(a, 10);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            if(view==null){
                view= new ImageView(a);
            }

//            Glide.with(a).load(getItem(position)).asBitmap().fitCenter().placeholder(R.drawable.place_holder2)
//                    .error(R.drawable.place_holder2).into(new BitmapImageViewTarget(imageView) {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    super.onResourceReady(resource, glideAnimation);
//                    float imageHeight = (imageWidth * (resource.getHeight() * 1f / resource.getWidth() * 1f));
//                    LogUtil.e("宽度：："+imageWidth+"  高度：；"+imageHeight);
//                    imageView.setLayoutParams(new AbsListView.LayoutParams(imageWidth, ((int) imageHeight)));
//
//                }
//
//                @Override
//                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                    super.onLoadFailed(e, errorDrawable);
//                    LogUtil.e("宽度：："+imageWidth);
//                }
//
//                @Override
//                public void onLoadStarted(Drawable placeholder) {
//                    super.onLoadStarted(placeholder);
//                    LogUtil.e("宽度：："+placeholder);
//                }
//
//            });
            LogUtil.e("图文详情图片；"+getItem(position));
//            imageView.setLayoutParams(new AbsListView.LayoutParams(imageWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            Glide.with(a).load("http://yintolo.net/public/uploads/draft/2017-06-06/1496713596_1460942899.png").override(imageWidth,imageWidth).into((ImageView) view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScaleImageUtil.openBigIagmeMode(a, list, position);
                }
            });
            return view;
        }
    }

    public class ZFAdapter extends BaseAdapter {
        public ArrayList<HashMap<String, String>> list;

        public void setList(ArrayList<HashMap<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            viewHolder holder;
            if (view == null) {
                holder = new viewHolder();
                holder.text = new TextView(Home_Zixun_detail.this);
                view = holder.text;
                holder.text.setTextSize(16);
                holder.text.setTextColor(getResources().getColor(R.color.main_color));
                holder.text.setGravity(Gravity.CENTER);
                holder.text.setPadding(0, DimenUtils.dip2px(Home_Zixun_detail.this, 8), 0, DimenUtils.dip2px(Home_Zixun_detail.this, 8));
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }
            holder.text.setText(list.get(position).get("name"));
            return view;
        }

        class viewHolder {
            TextView text;
        }
    }
}
