package com.laomachaguan.Model_Order;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.Adapter.myShuchengPagerAdapter;
import com.laomachaguan.Common.Good_Manager;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.BadgeView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.AbsCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/18.
 */
public class Order_Tab extends Fragment implements View.OnClickListener, Order_item_fragment.OrderAdapter.onCarNumNeedChangeListener {
    private static final String TAG = "ZiXun_Tab";
    private View view;
    private ArrayList<String> titles;
    //    private ArrayList<String >Ids;
    private List<Fragment> list;
    private myShuchengPagerAdapter adpter;
    private TabLayout tab;
    private ViewPager pager;
    private boolean isFirstIn = true;
    private ImageView tip;
    private FragmentManager fm;
    private FrameLayout shopcar;

    private int screenHeight, screenWidth;
    private ValueAnimator va;
    private FrameLayout animLayout;
    public static ArrayList<String> carIdlist;
    private BadgeView b;
    private float startX, startY;
    private long downTime;
    private int currentNum;
    public static List<String> typeIdList = new ArrayList<>();
    public static List<String> typeNameList = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", "").equals("")) {
                carIdlist.clear();
            }
            String id_remove = intent.getStringExtra("good_id");
            if(id_remove!=null){
                if(carIdlist!=null){
                    if (carIdlist.contains(id_remove)) {
                        carIdlist.remove(id_remove);
                        LogUtil.e("移除购物车商品Id！！！！！！！" + id_remove + "   购物车ID；：：" + carIdlist);
                    }
                }
            }
            if (mApplication.good_id_list != null) {
                if (carIdlist != null) {
                    for (String id : mApplication.good_id_list) {
                        if (carIdlist.contains(id)) {
                            carIdlist.remove(id);
                            LogUtil.e("支付成功后移除购物车商品Id！！！！！！！" + id + "   购物车ID；：：" + carIdlist);
                        }
                    }
                }
            }
            getShopCarSize();


            if (PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("3")) {
                b.setVisibility(View.GONE);
                Glide.with(getActivity()).load(R.drawable.shopcar_manager)
                        .override(DimenUtils.dip2px(getActivity(), 50)
                                , DimenUtils.dip2px(getActivity(), 50))
                        .fitCenter().into((ImageView) shopcar.findViewById(R.id.shopcar_home_img));
            } else {
                b.setVisibility(View.VISIBLE);
                if (PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", "").equals("")) {
                    b.setBadgeCount(0);
                }
                b.setTargetView(view.findViewById(R.id.shopcar_home_img));
                Glide.with(getActivity()).load(R.drawable.shopcar_float).override(DimenUtils.dip2px(getActivity(), 50)
                        , DimenUtils.dip2px(getActivity(), 50))
                        .fitCenter().into((ImageView) shopcar.findViewById(R.id.shopcar_home_img));
//            TintUtil.tintDrawableView((ImageView) view.findViewById(R.id.shopcar_home_img),R.drawable.shopcar_float,R.color.main_color);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.zixun_tab, container, false);
        // TODO: 2017/1/7 获取购物车数据
        carIdlist = new ArrayList<>();
        initView(view);
        getShopCarSize();
        IntentFilter intentFilter = new IntentFilter("car");

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isFirstIn && isVisibleToUser && this != null) {
            getData();
            isFirstIn = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void getShopCarSize() {
        if (!PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", "").equals("")) {
            JSONObject js = new JSONObject();
            try {
                js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpUtils.post(Constants.Shopcar_list).tag(TAG)
                    .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js)).execute(new AbsCallback<Object>() {
                @Override
                public Object parseNetworkResponse(Response response) throws Exception {
                    return null;
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("3")) {
                                    return;
                                }
                                if (list != null) {

                                    currentNum = list.size();
                                    LogUtil.e("购物车数量：" + currentNum);
                                    if (b != null) {
                                        b.setBadgeCount(currentNum);
                                    } else {
                                        b = new BadgeView(getActivity());
                                        b.setTextSize(10);
                                        b.setBadgeGravity(Gravity.RIGHT);
                                        b.setBackground(100, Color.RED);
                                        b.setBadgeCount(currentNum);
                                    }
                                    for (HashMap<String, String> map : list) {
                                        String id = map.get("good_id");
                                        if (!carIdlist.contains(id))
                                            carIdlist.add(id);
                                    }

                                } else {
                                    b.setBadgeCount(0);
                                    carIdlist.clear();
                                }

                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                b.setBadgeCount(0);
                                carIdlist.clear();

                            }
                        });

                    }
                }


            });
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        ProgressUtil.show(getActivity(), "", "正在加载");


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
                    final String data = OkHttpUtils.post(Constants.Order_type)
                            .params("key", ApisSeUtil.getKey())
                            .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                            .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                    if (view != null) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {

                                if (!TextUtils.isEmpty(data)) {
                                    Log.e(TAG, "run: data_______>" + data);
                                    List<HashMap<String, String>> list1 = AnalyticalJSON.getList_zj(data);
                                    if (list1 != null) {
                                        ProgressUtil.dismiss();

                                        titles.add("所有商品");
                                        list.add(new Order_item_fragment());
                                        for (HashMap<String, String> map : list1) {
                                            if (!typeNameList.contains(map.get("name"))) {
                                                typeNameList.add(map.get("name"));
                                                typeIdList.add(map.get("id"));
                                            }
                                            titles.add(map.get("name"));
                                            Bundle b = new Bundle();
                                            b.putString("type", map.get("id"));
                                            Order_item_fragment fragment = new Order_item_fragment();
                                            fragment.setArguments(b);
                                            list.add(fragment);///添加fragment
                                        }
                                        if (list.size() > 4) {
                                            tab.setTabMode(TabLayout.MODE_SCROLLABLE);
                                        } else {
                                            tab.setTabMode(TabLayout.MODE_FIXED);
                                        }
                                        adpter = new myShuchengPagerAdapter(mApplication.getInstance(), fm, list, titles);
                                        pager.setAdapter(adpter);
                                        pager.setOffscreenPageLimit(list.size());
                                        tab.setupWithViewPager(pager);
                                    } else {
                                        ProgressUtil.dismiss();
                                        tip.setVisibility(View.VISIBLE);
                                        view.findViewById(R.id.line).setVisibility(View.GONE);
                                    }

                                } else {
                                    ProgressUtil.dismiss();
                                    tip.setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.line).setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    if (view != null) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();
                                tip.setVisibility(View.VISIBLE);
                                view.findViewById(R.id.line).setVisibility(View.GONE);
                            }
                        });
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(final View view) {

        b = new BadgeView(getActivity());
        b.setTextSize(10);
        b.setBadgeGravity(Gravity.RIGHT);
        b.setBackground(100, Color.RED);
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int fragemntHeight = view.getHeight() - DimenUtils.dip2px(getActivity(), 35);
        fm = getChildFragmentManager();
        tip = (ImageView) view.findViewById(R.id.zixun_tab_tip);
        tip.setOnClickListener(this);
        tab = (TabLayout) view.findViewById(R.id.zixun_tab_tablayout);
        pager = (ViewPager) view.findViewById(R.id.zixun_tab_viewPager);
        titles = new ArrayList<>();
        list = new ArrayList<>();
        view.findViewById(R.id.line).setVisibility(View.VISIBLE);
        shopcar = (FrameLayout) view.findViewById(R.id.shopcar_home);
//        TintUtil.tintDrawableView((ImageView) view.findViewById(R.id.shopcar_home_img),R.drawable.shopcar_float,R.color.main_color);


        shopcar.setOnClickListener(this);
        if (PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("3")) {
            Glide.with(getActivity()).load(R.drawable.shopcar_manager)
                    .override(DimenUtils.dip2px(getActivity(), 50)
                            , DimenUtils.dip2px(getActivity(), 50))
                    .fitCenter().into((ImageView) shopcar.findViewById(R.id.shopcar_home_img));
        } else {
            b.setTargetView(view.findViewById(R.id.shopcar_home_img));
            Glide.with(getActivity()).load(R.drawable.shopcar_float).override(DimenUtils.dip2px(getActivity(), 50)
                    , DimenUtils.dip2px(getActivity(), 50))
                    .fitCenter().into((ImageView) shopcar.findViewById(R.id.shopcar_home_img));
//            TintUtil.tintDrawableView((ImageView) view.findViewById(R.id.shopcar_home_img),R.drawable.shopcar_float,R.color.main_color);
        }

        shopcar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setSelected(true);
                        downTime = System.currentTimeMillis();
                        startX = event.getRawX() - v.getX();
                        startY = event.getRawY() - v.getY();
                        View view1 = (View) view.getParent();
                        if (view1 instanceof ViewPager) {
                            ((ViewPager) view1).requestDisallowInterceptTouchEvent(true);//// TODO: 2016/12/12 禁止拦截事件 ,由子控件处理事件
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveFllow(v, x, y, startX, startY, view);//// TODO: 2016/12/12 跟随手指滑动
                        break;
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);//// TODO: 2016/12/12 允许拦截事件
                        checkHozatal(v);//// TODO: 2016/12/12判断左右边距
                        if (System.currentTimeMillis() - downTime <= 500) {
                            v.performClick();
                        }
                        break;
                }
                return true;
            }
        });


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
                    if (v.getX() <= DimenUtils.dip2px(getActivity(), 10)) {
                        v.setX(DimenUtils.dip2px(getActivity(), 10));
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
                    if (v.getX() >= screenWidth - DimenUtils.dip2px(getActivity(), 10) - v.getWidth()) {
                        v.setX(screenWidth - DimenUtils.dip2px(getActivity(), 10) - v.getWidth());
                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        }
        va.start();
    }

    private void moveFllow(View v, int x, int y, float startX, float startY, View view) {
        if (v.getY() >= view.getY() + DimenUtils.dip2px(getActivity(), 60) && v.getY() <= view.getY() + view.getHeight() - DimenUtils.dip2px(getActivity(), 10) - v.getHeight()) {
            v.setY(y - startY);
            if (v.getY() < view.getY() + DimenUtils.dip2px(getActivity(), 60)) {
                v.setY(view.getY() + DimenUtils.dip2px(getActivity(), 60));
            } else if (v.getY() > view.getY() + view.getHeight() - DimenUtils.dip2px(getActivity(), 10) - v.getHeight()) {
                v.setY(view.getY() + view.getHeight() - DimenUtils.dip2px(getActivity(), 10) - v.getHeight());
            }
        }

        if (v.getX() >= DimenUtils.dip2px(getActivity(), 10) && v.getX() <= (screenWidth - DimenUtils.dip2px(getActivity(), 10) - v.getWidth())) {
            v.setX(x - startX);
            if (v.getX() < DimenUtils.dip2px(getActivity(), 10)) {
                v.setX(DimenUtils.dip2px(getActivity(), 10));
            } else if (v.getX() > (screenWidth - DimenUtils.dip2px(getActivity(), 10) - v.getWidth())) {
                v.setX((screenWidth - DimenUtils.dip2px(getActivity(), 10) - v.getWidth()));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zixun_tab_tip:
                getData();
                tip.setVisibility(View.GONE);
                break;
            case R.id.shopcar_home:
                if (new LoginUtil().checkLogin(getActivity())) {
                    if (PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("3")) {
                        // TODO: 2017/6/7 商城管理
                        Intent intent = new Intent(getActivity(), Good_Manager.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), ShopCar.class);
                        startActivity(intent);
                    }

                }
                break;
        }
    }

    @Override
    public void onCarNumNeedChange(String id, View v) {
        if (carIdlist.contains(id)) {
            Toast.makeText(getActivity(), "该商品已经躺在购物车啦，快去看看吧", Toast.LENGTH_SHORT).show();
            return;
        } else {
            carIdlist.add(id);
            LogUtil.w("id数组：" + carIdlist);
        }
        int size = 15;
        int[] l = new int[2];
        int[] l1 = new int[2];
        shopcar.getLocationOnScreen(l1);
        v.getLocationOnScreen(l);
        Log.w(TAG, "onCarNumNeedChange: 商品id:" + id + " 控件" + l[0] + "  Y值：" + l[1]);
        Log.w(TAG, "onCarNumNeedChange: 购物车的坐标" + l1[0] + "  y:  " + l1[1]);
        final View view = new View(getActivity());
        view.setBackgroundResource(R.drawable.shopcar_redpoint);
        if (animLayout == null) {
            creatAnimLayout();// TODO: 2016/12/12 创建动画层
        }
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(DimenUtils.dip2px(getActivity(), size), DimenUtils.dip2px(getActivity(), size));
        int targtY = l1[1];
        int targtX = l1[0] + shopcar.getWidth() - DimenUtils.dip2px(getActivity(), 15);
        int startX = l[0] - DimenUtils.dip2px(getActivity(), size);
        int startY = l[1] - DimenUtils.dip2px(getActivity(), size);
        view.setX(startX);
        view.setY(startY);
        animLayout.addView(view, fl);
        ViewPropertyAnimator viewPropertyAnimator = view.animate().scaleXBy(1.5f).scaleYBy(1.5f).scaleX(0.4f).scaleY(0.4f).alphaBy(1).alpha(0.4f)
                .x(targtX).y(targtY)
                .setStartDelay(200).setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animLayout.removeView(view);
                b.incrementBadgeCount(1);
                AnimatorSet anim = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.shopcar_scale);
                anim.setTarget(shopcar);
                anim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        viewPropertyAnimator.start();
    }

    private void creatAnimLayout() {
        animLayout = new FrameLayout(getActivity());
        ViewGroup root = (ViewGroup) getActivity().getWindow().getDecorView();
        animLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        animLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
        root.addView(animLayout);
    }
}
