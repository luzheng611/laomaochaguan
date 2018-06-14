package com.laomachaguan.Model_Order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.Common.AD;
import com.laomachaguan.Common.Good_Manager;
import com.laomachaguan.Common.MainActivity;
import com.laomachaguan.Common.ZiXun_Detail;
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
import com.laomachaguan.Utils.ScaleImageUtil;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.View.ShiPuDecoration;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.laomachaguan.R.mipmap.shopcar;
import static com.laomachaguan.Utils.mApplication.good_id_list;

/**
 * Created by Administrator on 2016/10/14.
 */
public class Order_item_fragment_main extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private View view;
    private static final String TAG = "Order_item_fragment";
    private boolean isLoaded = false;

    private static final String TYPE = "type";//获取数据的标识
    private int  page = 1;
    private int  endPage = 0;
    private String url, data;

    private SwipeRefreshLayout swip;

    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private int currentNum;
    private OrderAdapter adapter1;
    private titleAdapter titleAdapter;

    private ArrayList<String> images;
    private ArrayList<HashMap<String, String>> imgInfos;//图片参数
    public static ArrayList<String> carIdlist;
    private Banner banner;
    private ArrayList<HashMap<String, String>> titles;
    private RecyclerView titleView;
    private RecyclerView goodView;
    private goodAdapter adapter;
    private String id="all";
//    private FrameLayout shopcarLayout;
//    private ImageView  shopcar;
//    private BadgeView b;
// TODO: 2017/5/2 banner 点击检测
private static final int AD = 1;
    private static final int Good = 3;
    private static final int HuoDong = 4;
    private static final int KeCheng = 5;
    private static final int ZIXUN = 2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_list_main, container, false);

        initView(view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoaded) {
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    onRefresh();
                }
            });

        }
    }

    // TODO: 2017/5/2 获取轮播图
    private void getBanner() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.getBanner)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {
                    @Override
                    public ArrayList<HashMap<String, String>> parseNetworkResponse(Response response) throws Exception {
                        return AnalyticalJSON.getList_zj(response.body().string());
                    }

                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                        imgInfos = list;
                        if (imgInfos != null) {
                            if (images == null) {
                                images = new ArrayList<String>();
                            } else {
                                images.clear();
                            }
                            for (HashMap<String, String> map : imgInfos) {
                                images.add(map.get("image"));
                            }
                            banner.setImages(images);
                            banner.start();
                            getTitles();
                            isLoaded = true;
                        } else {
                            getBanner();
                        }
                    }


                });
    }

    /**
     * 获取数据
     */
    private void getTitles() {
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
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    final String data = OkHttpUtils.post(Constants.Order_type)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (view != null) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(data)) {
                                    LogUtil.e("run: data_______>" + data);
                                    List<HashMap<String, String>> list1 = AnalyticalJSON.getList_zj(data);
                                    if (list1 != null) {
                                        titles.clear();
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("name", "所有商品");
                                        map.put("id", "all");
                                        titles.add(map);
                                        titles.addAll(list1);
                                        titleAdapter.setNewData(titles);
                                        getData(id);
                                    }

                                }

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取数据
     */
    private void getData(final String id) {

        if (id.equals("all")) {
            LogUtil.e("所有商品刷新     ");
            url = Constants.Order_total;
        } else {
            url = Constants.Order_special;

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("m_id", Constants.M_id);
                    js.put("page", page);
                    if (!id.equals("all")) {
                        js.put("type", id);
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    data = OkHttpUtils.post(url)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                        if (list != null) {
                            if (view != null) {
                                view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isRefresh) {
                                            adapter.setNewData(list);
                                            isRefresh = false;
                                            swip.setRefreshing(false);
                                        } else if (isLoadMore) {
                                            isLoadMore = false;
                                            if (list.size() < 10) {
                                                ToastUtil.showToastShort("商品加载完毕", Gravity.CENTER);
//                                endPage = page;
                                                adapter.notifyDataChangedAfterLoadMore(list, false);
                                            } else {
                                                adapter.notifyDataChangedAfterLoadMore(list, true);
                                            }
                                        }

                                        swip.setRefreshing(false);
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    static class OrderAdapter extends BaseAdapter {
        public List<HashMap<String, String>> list;
        private WeakReference<Activity> w;
        private Activity context;
        private LayoutInflater inflater;
        private int dp120;
        private SharedPreferences sp;
        private ArrayList<Boolean> flaglist;

        public void setOnCarNumNeedChangeListener(OrderAdapter.onCarNumNeedChangeListener onCarNumNeedChangeListener) {
            this.onCarNumNeedChangeListener = onCarNumNeedChangeListener;
        }

        public interface onCarNumNeedChangeListener {
            void onCarNumNeedChange(String id, View v);
        }

        private onCarNumNeedChangeListener onCarNumNeedChangeListener;

        public OrderAdapter(Activity a, List<HashMap<String, String>> list1) {
            super();
            this.list = list1;
            w = new WeakReference<Activity>(a);
            this.context = w.get();
            inflater = context.getLayoutInflater();
            dp120 = DimenUtils.dip2px(context, 120);
            sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
            flaglist = new ArrayList<>();

            for (HashMap map : list1) {
                if (((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist == null) {
                    flaglist.add(false);
                } else {
                    if (((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist.contains(map.get("id"))) {
                        flaglist.add(true);
                    } else {
                        flaglist.add(false);
                    }
                }
            }
            LogUtil.w("id数组：" + ((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist);
            setOnCarNumNeedChangeListener((OrderAdapter.onCarNumNeedChangeListener) ((MainActivity) context).list.get(1));// TODO: 2016/12/15  设置购物车回调
        }

        public void addList(List<HashMap<String, String>> list1) {
            this.list = list1;
            flaglist.clear();
            for (HashMap map : list1) {
                if (((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist == null) {
                    flaglist.add(false);
                } else {
                    if (((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist.contains(map.get("id"))) {
                        flaglist.add(true);
                    } else {
                        flaglist.add(false);
                    }
                }
            }
            LogUtil.w("id数组：" + ((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist);
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
        public View getView(final int position, View view, final ViewGroup parent) {
            Viewholder holder = null;
            final HashMap<String, String> map = list.get(position);
            if (view == null) {
                holder = new Viewholder();
                view = inflater.inflate(R.layout.order_item, parent, false);
                holder.root = (FrameLayout) view.findViewById(R.id.order_item_root);
                holder.image = (ImageView) view.findViewById(R.id.order_item_image);
                holder.pingtuan = (ImageView) view.findViewById(R.id.pingtuan);
                holder.name = (TextView) view.findViewById(R.id.order_item_name);
                holder.abs = (TextView) view.findViewById(R.id.order_item_abstract);
                holder.score = (TextView) view.findViewById(R.id.order_item_score);
                holder.sale = (TextView) view.findViewById(R.id.order_item_sales);
                holder.like = (TextView) view.findViewById(R.id.order_item_likes);
                holder.trueM = (TextView) view.findViewById(R.id.order_item_true_money);
                holder.falseM = (TextView) view.findViewById(R.id.order_item_false_money);
                Paint paint = holder.falseM.getPaint();
                paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.car = (LinearLayout) view.findViewById(R.id.order_item_car);
                holder.car_img = (ImageView) view.findViewById(R.id.order_item_car_img);
                holder.car_text = (TextView) view.findViewById(R.id.order_item_car_text);
                holder.prs = (ProgressBar) view.findViewById(R.id.order_item_progress);
                holder.type = (TextView) view.findViewById(R.id.order_item_type);
                view.setTag(holder);
            } else {
                holder = (Viewholder) view.getTag();
            }
            if (map.get("open").equals("2")) {
                holder.pingtuan.setVisibility(View.VISIBLE);
            } else {
                holder.pingtuan.setVisibility(View.GONE);
            }
            if (PreferenceUtil.getUserIncetance(context).getString("role", "").equals("3")) {
                holder.car.setVisibility(View.GONE);
            } else {
                holder.car.setVisibility(View.VISIBLE);
            }
            holder.car_img.setImageBitmap(ImageUtil.readBitMap(context, shopcar));
            Glide.with(context).load(map.get("image1")).asBitmap().override(dp120, dp120 / 4 * 3).centerCrop()
                    .error(R.mipmap.load_nothing)
                    .into(new BitmapImageViewTarget(holder.image) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            RoundedBitmapDrawable rb = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            rb.setCornerRadius(10);
                            setDrawable(rb);
                        }
                    });
            holder.type.setText(map.get("name"));
            holder.name.setText(map.get("title"));
            holder.car.setTag(map.get("id"));
            holder.abs.setText(map.get("abstract"));
            holder.score.setText("每" + map.get("convert") + "积分可抵用1元");
            holder.sale.setText("库存" + map.get("stock") + "  总销量" + map.get("sales"));
            holder.trueM.setText("¥ " + map.get("money"));
            holder.like.setText("赞" + map.get("likes"));
            if (map.get("cost").equals("0") || map.get("cost").equals("")) {
                holder.falseM.setVisibility(View.INVISIBLE);
            } else {
                holder.falseM.setVisibility(View.VISIBLE);
                holder.falseM.setText("¥ " + map.get("cost"));
            }

            holder.car_img.setTag(holder.prs);
            holder.car_text.setTag(position);
            if (flaglist.get(position)) {
                holder.prs.setVisibility(View.GONE);
                holder.car_img.setVisibility(View.GONE);
                holder.car_text.setText("已放入茶壶");
                holder.car.setEnabled(false);
            } else {
                holder.car_img.setVisibility(View.VISIBLE);
                holder.car_text.setText("加入茶壶");
                holder.car.setEnabled(true);
            }
            if (map.get("stock").equals("0")) {
                holder.prs.setVisibility(View.GONE);
                holder.car_img.setVisibility(View.GONE);
                holder.car_text.setText("已售罄");
                holder.car.setEnabled(false);
            }
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (position != parent.getChildCount() - 1) {
                    LinearLayout car = (LinearLayout) v.findViewById(R.id.order_item_car);
                    String Id = (String) car.getTag();
                    Intent intent = new Intent(context, Order_detail.class);
                    intent.putExtra("id", Id);

                    if (car.isEnabled()) {
                        intent.putExtra("flag", false);
                    } else {
                        intent.putExtra("flag", true);
                    }
                    intent.putExtra("stock", map.get("stock"));//库存数量

                    intent.putExtra("open", map.get("open").equals("2") ? true : false);//2为拼团商品   其他为不拼团
                    context.startActivity(intent);
//                    }
                }
            });
            //// TODO: 2016/11/30 加入购物车
            holder.car.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final String id = (String) v.getTag();
                    final ImageView img = (ImageView) v.findViewById(R.id.order_item_car_img);
                    final ProgressBar p = (ProgressBar) img.getTag();
                    final TextView t = (TextView) v.findViewById(R.id.order_item_car_text);
                    final int pos = (int) t.getTag();
                    if (Network.HttpTest(context)) {
                        if (LoginUtil.checkLogin(context)) {
                            p.setVisibility(View.VISIBLE);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject js = new JSONObject();
                                        js.put("user_id", PreferenceUtil.getUserIncetance(context)
                                                .getString("user_id", ""));
                                        js.put("order_id", id);
                                        js.put("type", "1");
                                        js.put("limited", "0");
                                        js.put("m_id", Constants.M_id);
                                        String data = OkHttpUtils.post(Constants.Order_add_car)
                                                .tag("main").params("key", ApisSeUtil.getKey())
                                                .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                        if (!TextUtils.isEmpty(data)) {
                                            final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                            if (context != null) {
                                                context.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (map != null) {
                                                            if ("000".equals(map.get("code")) || "002".equals(map.get("code"))) {
                                                                p.setVisibility(View.GONE);
                                                                img.setVisibility(View.GONE);
                                                                t.setText("已放入茶壶");
                                                                flaglist.set(pos, true);
                                                                v.setEnabled(false);
                                                                onCarNumNeedChangeListener.onCarNumNeedChange(id, v);

                                                            }
                                                        } else {
                                                            p.setVisibility(View.GONE);
                                                            Toast.makeText(context, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            if (context != null) {
                                                context.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        p.setVisibility(View.GONE);
                                                        v.setEnabled(true);
                                                        Toast.makeText(context, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    } catch (Exception e) {
                                        if (context != null) {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    p.setVisibility(View.GONE);
                                                    v.setEnabled(true);
                                                }
                                            });
                                        }
                                    }
                                }
                            }).start();
                        }
                    }
                }
            });


            return view;
        }

        class Viewholder {
            ImageView image, car_img, pingtuan;
            TextView name, abs, score, sale, like, trueM, falseM, car_text, type;
            LinearLayout car;
            FrameLayout root;
            ProgressBar prs;
        }
    }
//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", "").equals("")) {
//                carIdlist.clear();
//            }
//            String id_remove = intent.getStringExtra("good_id");
//            if(id_remove!=null){
//                if(carIdlist!=null){
//                    if (carIdlist.contains(id_remove)) {
//                        carIdlist.remove(id_remove);
//                        LogUtil.e("移除购物车商品Id！！！！！！！" + id_remove + "   购物车ID；：：" + carIdlist);
//                    }
//                }
//            }
//            if (mApplication.good_id_list != null) {
//                if (carIdlist != null) {
//                    for (String id : mApplication.good_id_list) {
//                        if (carIdlist.contains(id)) {
//                            carIdlist.remove(id);
//                            LogUtil.e("支付成功后移除购物车商品Id！！！！！！！" + id + "   购物车ID；：：" + carIdlist);
//                        }
//                    }
//                }
//            }
//            getShopCarSize();
//
//
//            if (PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("3")) {
//                b.setVisibility(View.GONE);
//                Glide.with(getActivity()).load(R.drawable.shopcar_manager)
//                        .override(DimenUtils.dip2px(getActivity(), 50)
//                                , DimenUtils.dip2px(getActivity(), 50))
//                        .fitCenter().into((ImageView) shopcar.findViewById(R.id.shopcar_home_img));
//            } else {
//                b.setVisibility(View.VISIBLE);
//                if (PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", "").equals("")) {
//                    b.setBadgeCount(0);
//                }
//                b.setTargetView(view.findViewById(R.id.shopcar_home_img));
//                Glide.with(getActivity()).load(R.drawable.shopcar_float).override(DimenUtils.dip2px(getActivity(), 50)
//                        , DimenUtils.dip2px(getActivity(), 50))
//                        .fitCenter().into((ImageView) shopcar.findViewById(R.id.shopcar_home_img));
////            TintUtil.tintDrawableView((ImageView) view.findViewById(R.id.shopcar_home_img),R.drawable.shopcar_float,R.color.main_color);
//            }
//        }
//    };

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(View view) {
        View head=LayoutInflater.from(getActivity()).inflate(R.layout.good_head,null);
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        titles = new ArrayList<>();
        titleView = (RecyclerView) head.findViewById(R.id.recycle_titles);
        goodView = (RecyclerView) view.findViewById(R.id.recycle);

        goodView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        goodView.addItemDecoration(new mItemDeraction(DimenUtils.dip2px(getActivity(), 5)));
        adapter = new goodAdapter(new ArrayList<HashMap<String, String>>());
        goodView.setAdapter(adapter);
        titleView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        titleView.addItemDecoration(new ShiPuDecoration(getActivity(),1, R.color.white));
        titleAdapter = new titleAdapter(new ArrayList<HashMap<String, String>>());
        titleView.setAdapter(titleAdapter);
        titleAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                view.setSelected(true);
                id=titleAdapter.getData().get(i).get("id");
                titleAdapter.notifyDataSetChanged();
                swip.post(new Runnable() {
                    @Override
                    public void run() {
                        swip.setRefreshing(true);
                        page=1;
                        endPage=0;
                        isRefresh=true;
                        adapter.openLoadMore(10,true);
                        getData(id);
                    }
                });

            }
        });

        banner = (Banner) head.findViewById(R.id.order_main_banner);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setDelayTime(3000);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                HashMap<String, String> map = imgInfos.get(position);
                LogUtil.e(map + "    " + position);
                if (map != null) {
                    Intent intent = new Intent();
                    switch (Integer.valueOf(map.get("type"))) {
                        case AD:
                            if(map.get("url").equals("")){
                                ScaleImageUtil.openBigIagmeMode(getActivity(),images.get(position));
                            }else{
                                intent.setClass(getActivity(), AD.class);
                                intent.putExtra("url", map.get("url"));
                                startActivity(intent);
                            }

                            break;
                        case Good:
                            intent.setClass(getActivity(), Order_detail.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;
                        case HuoDong:
                            intent.setClass(getActivity(), activity_Detail.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;
                        case KeCheng:
                            intent.setClass(getActivity(), activity_Detail.class);
                            intent.putExtra("kecheng",true);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;
                        case ZIXUN:
                            intent.setClass(getActivity(), ZiXun_Detail.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;

                    }
                }
            }
        });
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load(path).override(getResources().getDisplayMetrics().widthPixels, DimenUtils.dip2px(getActivity(),200))
                        .centerCrop().into(imageView);
            }
        });

        adapter.addHeaderView(head);
        TextView textView = new TextView(getActivity());
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.loadnothing);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 50);
        textView.setLayoutParams(vl);
        textView.setText("没有检索到商品");
        adapter.setEmptyView(false,true,textView);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {

                String Id= adapter.getData().get(i).get("id");
                Intent intent=new Intent(getActivity(),Order_detail.class);
                intent.putExtra("id",Id);
                if(good_id_list!=null){
                    if(good_id_list.contains(Id)){
                        intent.putExtra("flag",true);
                    }else{
                        intent.putExtra("flag",false);
                    }
                }

                intent.putExtra("stock",adapter.getData().get(i).get("stock"));//库存数量

                intent.putExtra("open",adapter.getData().get(i).get("open").equals("2")?true:false);//2为拼团商品   其他为不拼团
                startActivity(intent);
            }
        });
        adapter.openLoadMore(10,true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getData(id);
                }
            }
        });
//        //自由滑动
//        new PointMoveHelper(getActivity(), shopcarLayout)
//                .setViewUnMoveable(((MainActivity) getActivity()).pager)
//                .setHorizontalMargin(10);

    }

    public class titleAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        public titleAdapter(List<HashMap<String, String>> data) {
            super(R.layout.item_grid, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            if(map.get("id").equals("all")){
                Glide.with(getActivity()).load(R.drawable.all)
                        .override(DimenUtils.dip2px(getActivity(),35),DimenUtils.dip2px(getActivity(),35))
                        .fitCenter().into((ImageView) holder.getView(R.id.image));
            }else{
                Glide.with(getActivity()).load(map.get("image"))
                        .override(DimenUtils.dip2px(getActivity(),35),DimenUtils.dip2px(getActivity(),35))
                        .fitCenter().into((ImageView) holder.getView(R.id.image));
            }

            holder.setText(R.id.text, map.get("name"));
            if(id.equals(map.get("id"))){
                holder.convertView.setSelected(true);
            }else{
                holder.convertView.setSelected(false);
            }
        }
    }

    public class goodAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        int width;
        public goodAdapter(List<HashMap<String, String>> data) {
            super(R.layout.good_item, data);
            width=(getResources().getDisplayMetrics().widthPixels-DimenUtils.dip2px(getActivity(),20))/2;
        }

        @Override
        protected void convert(final BaseViewHolder holder, HashMap<String, String> map) {

            Glide.with(getActivity())
                    .load(map.get("image1"))
                    .asBitmap()
                    .override(width,width)
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            rbd.setCornerRadius(DimenUtils.dip2px(getActivity(), 3));

                            ((ImageView) holder.getView(R.id.image)).setImageDrawable(rbd);
                        }
                    });
            holder.setText(R.id.title, map.get("title"))
                    .setText(R.id.money, "￥" + map.get("money"));
            if (map.get("open").equals("2")) {
                holder.setVisible(R.id.tag,true);
            } else {
                holder.setVisible(R.id.tag,false);
            }

        }
    }

    @Override
    public void onRefresh() {
        id="all";
        isRefresh = true;
        page = 1;
        endPage = 0;
        adapter.openLoadMore(10,true);
        getBanner();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopcar_home:
                if (LoginUtil.checkLogin(getActivity())) {
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
}
