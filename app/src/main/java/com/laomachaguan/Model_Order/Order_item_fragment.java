package com.laomachaguan.Model_Order;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import com.laomachaguan.Common.MainActivity;
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
import com.laomachaguan.View.LoadMoreListView;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */
public class Order_item_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreListView.OnLoadMore, View.OnClickListener {
    private View view;
    private static final String TAG = "Order_item_fragment";
    private boolean isLoaded = false;
    private Bundle b;
    private static final String TYPE = "type";//获取数据的标识
    private String page = "1";
    private String endPage = "";
    private String Id, url, data;

    private SwipeRefreshLayout swip;
    private LoadMoreListView listView;
    private boolean isRefresh = false;
    private TextView tip;
    private OrderAdapter adapter;
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_list, container, false);
        getActivity().registerReceiver(receiver,new IntentFilter("car"));
        b = getArguments();
        if (!isLoaded&&b==null ) {
            initView(view);
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    getData();
                }
            });

            isLoaded = true;
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoaded && b != null) {
            initView(view);
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    getData();
                }
            });
            isLoaded = true;
        }
    }

    /**
     * 获取数据
     */
    private void getData() {

        if (b == null) {
            LogUtil.e("所有商品刷新     ");
            url = Constants.Order_total;
        } else {
            url = Constants.Order_special;
            Id = b.getString(TYPE);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    js.put("m_id", Constants.M_id);
                    js.put("page", page);
                    if (!TextUtils.isEmpty(Id)) {
                        js.put("type", Id);
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    data = OkHttpUtils.post(url)
                            .params("key", m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                        if (list != null) {
                            if (listView != null) {
                                listView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (list.size() != 10) {
                                            endPage = page;
                                        }
                                        if (adapter == null) {
                                            adapter = new OrderAdapter(getActivity(), list);
                                            listView.setAdapter(adapter);
                                            if (swip != null && swip.isRefreshing())
                                                swip.setRefreshing(false);
                                            if (listView.isLoading) listView.onLoadComplete();
                                        } else {
                                            if (isRefresh) {
                                                isRefresh = false;
                                                adapter.addList(list);
                                                adapter.notifyDataSetChanged();
                                                endPage = "";
                                                if (swip != null && swip.isRefreshing())
                                                    swip.setRefreshing(false);
                                                if (t.getText().toString().equals("没有更多数据了")) {
                                                    listView.onLoadComplete();
                                                    t.setText("正在加载....");
                                                    p.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                adapter.list.addAll(list);
                                                for (HashMap map : list) {
                                                    if(((Order_Tab) ((MainActivity) getActivity()).list.get(1)).carIdlist==null){
                                                        adapter.flaglist.add(false);
                                                    }else{
                                                        if(((Order_Tab) ((MainActivity) getActivity()).list.get(1)).carIdlist.contains(map.get("id"))){
                                                            adapter.flaglist.add(true);
                                                        }else{
                                                           adapter. flaglist.add(false);
                                                        }
                                                    }
                                                }
                                                adapter.notifyDataSetChanged();
                                                if (endPage.equals(page)) {
                                                    t.setText("没有更多数据了");
                                                    p.setVisibility(View.GONE);
                                                    return;
                                                } else {
                                                    t.setText("正在加载....");
                                                }
                                            }
                                            listView.onLoadComplete();
                                        }
                                    }
                                });
                            } else {
                                if (tip != null) {
                                    tip.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tip.setVisibility(View.VISIBLE);
                                            if (swip != null && swip.isRefreshing())
                                                swip.setRefreshing(false);
                                            if (listView.isLoading) listView.onLoadComplete();
                                        }
                                    });
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    if (tip != null) {
                        tip.post(new Runnable() {
                            @Override
                            public void run() {
                                tip.setVisibility(View.VISIBLE);
                                if (swip != null && swip.isRefreshing()) swip.setRefreshing(false);
                                if (listView.isLoading) listView.onLoadComplete();
                            }
                        });
                    }
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

        public interface onCarNumNeedChangeListener{
           void  onCarNumNeedChange(String id, View v);
        }
        private   onCarNumNeedChangeListener onCarNumNeedChangeListener;
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
                if(((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist==null){
                    flaglist.add(false);
                }else{
                    if(((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist.contains(map.get("id"))){
                        flaglist.add(true);
                    }else{
                        flaglist.add(false);
                    }
                }
            }
            LogUtil.w("id数组："+((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist);
            setOnCarNumNeedChangeListener((OrderAdapter.onCarNumNeedChangeListener) ((MainActivity) context).list.get(1));// TODO: 2016/12/15  设置购物车回调
        }

        public void addList(List<HashMap<String, String>> list1) {
            this.list = list1;
            flaglist.clear();
            for (HashMap map : list1) {
                if(((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist==null){
                    flaglist.add(false);
                }else{
                    if(((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist.contains(map.get("id"))){
                        flaglist.add(true);
                    }else{
                        flaglist.add(false);
                    }
                }
            }
            LogUtil.w("id数组："+((Order_Tab) ((MainActivity) context).list.get(1)).carIdlist);
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
                holder.root= (FrameLayout) view.findViewById(R.id.order_item_root);
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
                holder.type= (TextView) view.findViewById(R.id.order_item_type);
                view.setTag(holder);
            } else {
                holder = (Viewholder) view.getTag();
            }
            if(map.get("open").equals("2")){
                holder.pingtuan.setVisibility(View.VISIBLE);
            }else{
                holder.pingtuan.setVisibility(View.GONE);
            }
            if(PreferenceUtil.getUserIncetance(context).getString("role","").equals("3")){
                holder.car.setVisibility(View.GONE);
            }else{
                holder.car.setVisibility(View.VISIBLE);
            }
            holder.car_img.setImageBitmap(ImageUtil.readBitMap(context, R.mipmap.shopcar));
            Glide.with(context).load(map.get("image1")).asBitmap().override(dp120, dp120/4*3).centerCrop()
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
            holder.score.setText("每"+map.get("convert")+"积分可抵用1元");
            holder.sale.setText("库存"+map.get("stock")+"  总销量" + map.get("sales"));
            holder.trueM.setText("¥ " + map.get("money"));
            holder.like.setText("赞" + map.get("likes"));
            if(map.get("cost").equals("0")||map.get("cost").equals("")){
                holder.falseM.setVisibility(View.INVISIBLE);
            }else{
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
                holder.car.setVisibility(View.GONE);
            } else {
                holder.car_img.setVisibility(View.VISIBLE);
                holder.car_text.setText("加入茶壶");
                holder.car.setEnabled(true);
                holder.car.setVisibility(View.GONE);
            }
            if(map.get("stock").equals("0")){
                holder.prs.setVisibility(View.GONE);
                holder.car_img.setVisibility(View.GONE);
                holder.car_text.setText("已售罄");
                holder.car.setEnabled(false);
            }
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (position != parent.getChildCount() - 1) {
                        LinearLayout car= (LinearLayout) v.findViewById(R.id.order_item_car);
                        String Id= (String) car.getTag();
                        Intent intent=new Intent(context,Order_detail.class);
                        intent.putExtra("id",Id);

                        if(car.isEnabled()){
                            intent.putExtra("flag",false);
                        }else{
                            intent.putExtra("flag",true);
                        }
                        intent.putExtra("stock",map.get("stock"));//库存数量

                        intent.putExtra("open",map.get("open").equals("2")?true:false);//2为拼团商品   其他为不拼团
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
                                        JSONObject js=new JSONObject();
                                        js.put("user_id", PreferenceUtil.getUserIncetance(context)
                                                .getString("user_id", ""));
                                        js.put("order_id", id);
                                        js.put("type", "1");
                                        js.put("limited", "0");
                                        js.put("m_id", Constants.M_id);
                                        String data = OkHttpUtils.post(Constants.Order_add_car)
                                                .tag("main").params("key",ApisSeUtil.getKey())
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
                                                                onCarNumNeedChangeListener.onCarNumNeedChange(id,v);

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
            ImageView image, car_img,pingtuan;
            TextView name, abs, score, sale, like, trueM, falseM, car_text,type;
            LinearLayout car;
            FrameLayout root;
            ProgressBar prs;
        }
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(View view) {
        swip = (SwipeRefreshLayout) view.findViewById(R.id.zixun_refresh);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        listView = (LoadMoreListView) view.findViewById(R.id.zixun_list);
        listView.setLoadMoreListen(this);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position != parent.getCount() - 1) {
//                    LinearLayout car= (LinearLayout) view.findViewById(R.id.order_item_car);
//                    String Id= (String) car.getTag();
//                    Intent intent=new Intent(getActivity(),Order_detail.class);
//                    intent.putExtra("id",Id);
//
//                    startActivity(intent);
//                }
//            }
//        });
        tip = (TextView) view.findViewById(R.id.zixun_list_tip);
        tip.setOnClickListener(this);
        t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));

    }


    @Override
    public void loadMore() {
        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            final ProgressBar p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
            final TextView t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
            p.setVisibility(View.GONE);
            t.setText("没有更多数据了");
            return;
        }
        getData();
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        page = "1";
        endPage = "";
        getData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zixun_list_tip:
                getData();
                tip.setVisibility(View.GONE);
                break;
        }
    }
}
