package com.laomachaguan.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.Model_Order.MyZhiFuDetail;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.View.mItemDecoration;
import com.laomachaguan.View.mRecyclerView;
import com.laomachaguan.WuLiu.Fahuo;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.laomachaguan.R.id.num;

/**
 * 作者：因陀罗网 on 2017/6/11 11:44
 * 公司：成都因陀罗网络科技有限公司
 */

public class DingDanFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String pingtuan = "1";
    private static final String fahuo = "2";
    private static final String tuikuan = "3";
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private DingdanAdapter adapter;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private static final String TAG = "DingDanFragment";
    private boolean isAgent = false;
    Bundle b;

    BroadcastReceiver receive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            onRefresh();

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        b = getArguments();
        if (b == null) {//个人
            LogUtil.e("普通用户订单");
        } else {
            if (b.getString("role") != null) {//店铺订单
                isAgent = true;
                LogUtil.e("店铺订单");
            } else {
                LogUtil.e("管理员订单");
                switch (b.getString("tag")) {
                    case pingtuan:
                        break;
                    case fahuo:
                        break;
                    case tuikuan:
                        break;
                }
                ;
            }

        }
        IntentFilter intentFilter = new IntentFilter("tuikuan");
        getActivity().registerReceiver(receive, intentFilter);
        View view = inflater.inflate(R.layout.fragment_dingdan, container, false);
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView textView = new TextView(getActivity());
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.loadnothing);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        textView.setText("暂无订单记录");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 180);
        textView.setLayoutParams(vl);
        adapter = new DingdanAdapter(getActivity(), new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        adapter.openLoadMore(10, true);
        adapter.setEmptyView(textView);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getData();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });

        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receive);
    }

    private void getData() {
        if (!Network.HttpTest(getActivity())) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Constants.GoodsAdminGroup;
                    JSONObject js = new JSONObject();
                    if (!PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("3")) {
                        if (PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("2") && isAgent) {//店铺订单
                            LogUtil.e("店铺订单");
                            url = Constants.Mine_store;
                            js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
                        } else {//个人
                            js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
                            url = Constants.GoodsDingdan;
                        }
                    } else {
                        //管理员
                        url = Constants.GoodsAdminGroup;
                        js.put("type", b.getString("tag"));


                    }
                    js.put("page", page);
                    js.put("m_id", Constants.M_id);
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(url).tag(TAG).params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        Log.w(TAG, "run: " + data);
                        final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                        if (list != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        adapter.setNewData(list);
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list.size() < 10) {
                                            ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(10, true);
        getData();
    }

    public class DingdanAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        private Activity a;

        // TODO: 2017/6/22 item适配器
        public class itemAdapter extends BaseQuickAdapter<HashMap<String, String>> {
            //type 1:黑色  2.  红色   3  邀约
            private int type;

            public itemAdapter(int type, List<HashMap<String, String>> data) {
                super(R.layout.item_dingdan_item_text_text, data);
                this.type = type;
            }

            @Override
            protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
                TextView title = holder.getView(R.id.title);
                if (type == 1) {
                    title.setText(map.get("title"));
                    title.setTextColor(Color.BLACK);
                } else if (type == 2) {
                    title.setText(map.get("sut_title"));
                    title.setTextColor(Color.parseColor("#F37885"));
                } else if (type == 3) {
                    title.setText(map.get("sut_title"));
                    title.setTextColor(Color.parseColor("#FF7F00"));
                }
                ((TextView) holder.getView(num)).setText("×" + map.get("num"));
            }
        }

        public DingdanAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
            super(R.layout.my_orders_item, data);
            WeakReference<Activity> w = new WeakReference<Activity>(activity);
            a = w.get();
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            holder.setText(R.id.time, TimeUtils.getTrueTimeStr(map.get("end_time")));


            if (map.get("sut_type").equals("7")) {
                PuTong(holder, map);
                LogUtil.e("普通订单");
            } else {
                holder.setText(R.id.status, "已支付");
                if (map.get("sut_type").equals("12")) {//应邀金
                    YingYaoJin(holder, map);
                    LogUtil.e("应邀金订单");
                } else {
                    pintuan(holder, map);
                    LogUtil.e("拼团订单");
                }
            }
        }

        private void pintuan(final BaseViewHolder holder, final HashMap<String, String> map) {
            holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
            holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
            HashMap<String, String> m1 = new HashMap<>();
            m1.put("sut_title", map.get("sut_title"));
            m1.put("num", map.get("num"));
            m1.put("snr", map.get("snr"));
            ArrayList<HashMap<String, String>> l = new ArrayList<>();
            l.add(m1);
            ((mRecyclerView) holder.getView(R.id.listview)).setLayoutManager(new LinearLayoutManager(a));
            ((mRecyclerView) holder.getView(R.id.listview)).addItemDecoration(new mItemDecoration(a));
            ((mRecyclerView) holder.getView(R.id.listview)).setAdapter
                    (new itemAdapter(2, l));
            int num = 0;
            for (HashMap<String, String> m : l) {
                num += Integer.valueOf(m.get("num"));
            }
            if (isAgent) {//店铺订单
                String credit = String.valueOf(Double.valueOf(map.get("money")).intValue());
                String allow = "";
                String get = String.valueOf(Double.valueOf(Double.valueOf(map.get("money")) * (100 - Integer.valueOf(map.get("ratio"))) / 100).intValue());
                if (map.get("status").equals("2") && map.get("delivery").equals("2")) {
                    allow = " (总共:" + credit + "积分 返现:" + map.get("ratio") + "% 获得:" + get + "积分)";
                }
                SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money") + allow);
                holder.setText(R.id.numMoney, ss);
            } else if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {
                SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money") + ((map.get("store") != null && !map.get("store").equals("")) ? ("  来自" + map.get("store")) : ""));
                ss.setSpan(new ForegroundColorSpan(Color.BLACK), (num + "").length() + 7, (num + "").length() + 7 + map.get("money").length() + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                if (map.get("store") != null && !map.get("store").equals("")) {
                    ss.setSpan(new ForegroundColorSpan(Color.RED), ss.length() - map.get("store").length(), ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }
                holder.setText(R.id.numMoney, ss);
            } else {//普通用户
                String get = String.valueOf(Double.valueOf(Double.valueOf(map.get("money")) * (Integer.valueOf(map.get("ratio"))) / 100).intValue());
                String allow = "";
                if (map.get("status").equals("2") && map.get("delivery").equals("2")) {
                    allow = " (" + " 获得:" + get + "积分)";
                }

                SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money") + allow + ((map.get("store") != null && !map.get("store").equals("")) ? ("  来自" + map.get("store")) : ""));
                ss.setSpan(new ForegroundColorSpan(Color.RED), ss.length() - ((map.get("store") != null && !map.get("store").equals("")) ? map.get("store").length() : 0), ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.setText(R.id.numMoney, ss);

            }


            ((itemAdapter) ((mRecyclerView) holder.getView(R.id.listview)).getAdapter()).setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                    String snr = map.get("snr");
                    Intent intent = new Intent(a, MyZhiFuDetail.class);
//                    if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {
                        intent.putExtra("id", map.get("id"));
//                    }
                    intent.putExtra("user_id",map.get("user_id"));
                    intent.putExtra("isAgent",isAgent);
                    intent.putExtra("status", ((TextView) holder.getView(R.id.pintuan)).getText().toString());
                    intent.putExtra("snr", snr);
                    intent.putExtra("KT", map.get("number").equals("") ? false : true);
                    a.startActivity(intent);
                }
            });
            if (isAgent) {
                if (map.get("status").equals("1")) {
                    long endtime = TimeUtils.dataOne(map.get("end_time"));
                    if (System.currentTimeMillis() - endtime >= (24 * 60 * 60 * 2 * 1000)) {
                        holder.setText(R.id.status, "拼团失败");
                        ((TextView) holder.getView(R.id.pintuan)).setText("等待退款");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    } else {
                        ((TextView) holder.getView(R.id.pintuan)).setText("等待拼团");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    }
                } else if (map.get("status").equals("2")) {
                    if (map.get("delivery").equals("1")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("等待发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    } else if (map.get("delivery").equals("2")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("已发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    }

                } else if (map.get("status").equals("3")) {
                    ((TextView) holder.getView(R.id.pintuan)).setText("已退款");
                    ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                }
            } else if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {

                if (map.get("status").equals("1")) {//等待拼团
                    long endtime = TimeUtils.dataOne(map.get("end_time"));
                    if (System.currentTimeMillis() - endtime >= (24 * 60 * 60 * 2 * 1000)) {
                        holder.setText(R.id.status, "拼团失败");
                        ((TextView) holder.getView(R.id.pintuan)).setText("退款");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(true);
                        holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    } else {
                        ((TextView) holder.getView(R.id.pintuan)).setText("完成拼团");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(true);
                        holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    }

                } else if (map.get("status").equals("2")) {//已完成拼团
                    if (map.get("delivery").equals("1")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(true);
                        holder.getView(R.id.tuikuan).setVisibility(View.VISIBLE);
                        holder.setText(R.id.tuikuan, "退款");
                        holder.getView(R.id.tuikuan).setEnabled(true);
                    } else if (map.get("delivery").equals("2")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("已发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                        holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    }
                } else if (map.get("status").equals("3")) {//已申请退款
                    ((TextView) holder.getView(R.id.pintuan)).setText("已退款");
                    ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                }

            } else {
                if (map.get("status").equals("1")) {
                    long endtime = TimeUtils.dataOne(map.get("end_time"));
                    if (System.currentTimeMillis() - endtime >= (24 * 60 * 60 * 2 * 1000)) {
                        holder.setText(R.id.status, "拼团失败");
                        ((TextView) holder.getView(R.id.pintuan)).setText("退款");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(true);
                    } else {
                        ((TextView) holder.getView(R.id.pintuan)).setText("等待拼团");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    }
                } else if (map.get("status").equals("2")) {
                    if (map.get("delivery").equals("1")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("等待发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    } else if (map.get("delivery").equals("2")) {
                        ((TextView) holder.getView(R.id.pintuan)).setText("已发货");
                        ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                    }

                } else if (map.get("status").equals("3")) {
                    ((TextView) holder.getView(R.id.pintuan)).setText("已退款");
                    ((TextView) holder.getView(R.id.pintuan)).setEnabled(false);
                }

            }


            holder.setOnClickListener(R.id.pintuan, new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (!Network.HttpTest(a)) {
                        return;
                    }
                    if (map.get("status").equals("1")) {//等待拼团

                        if (((TextView) view).getText().toString().equals("退款")) {//拼团时间已过
                            AlertDialog.Builder builder = new AlertDialog.Builder(a);
                            builder.setPositiveButton("退款", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    TuiKuan(view);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setMessage("需要立即退款吗？退款成功后会在0-7个工作日内原路返回").create().show();

                        } else {//管理员凑团
                            JSONObject js = new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("number", map.get("number"));
                                js.put("snr", map.get("snr"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m = ApisSeUtil.i(js);
                            OkHttpUtils.post(Constants.GoodsAdminSave).params("key", m.K())
                                    .params("msg", m.M()).execute(new StringCallback() {

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    ProgressUtil.show(a, "", "正在凑单，请稍后");
                                }

                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                    if (map != null) {
                                        if ("000".equals(map.get("code"))) {
                                            ToastUtil.showToastShort("凑单成功");
                                            ((TextView) view).setText("发货");
                                            map.put("status", "2");
                                            Intent intent = new Intent("tuikuan");
                                            a.sendBroadcast(intent);
                                        } else {
                                            ToastUtil.showToastShort("凑单失败，请稍后重试");
                                        }
                                    } else {
                                        ToastUtil.showToastShort("凑单失败，请稍后重试");
                                    }
                                }

                                @Override
                                public void onAfter(@Nullable String s, @Nullable Exception e) {
                                    super.onAfter(s, e);
                                    ProgressUtil.dismiss();
                                }


                            });
                        }

                    } else if (map.get("status").equals("2") && map.get("delivery").equals("1")) {//发货

                        Intent intent = new Intent(a, Fahuo.class);
                        intent.putExtra("id", map.get("id"));
                        intent.putExtra("snr", map.get("snr"));
                        intent.putExtra("money",map.get("money"));
                        intent.putExtra("user_id",map.get("user_id"));
                        startActivity(intent);
                    }
                }

                private void TuiKuan(final View view) {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3") ?
                                map.get("user_id") : PreferenceUtil.getUserIncetance(a).getString("user_id", ""));
                        js.put("shop_id", map.get("shop_id"));
                        js.put("number", map.get("number"));
                        js.put("id", map.get("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    OkHttpUtils.post(Constants.PTtuikuan)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .execute(new StringCallback() {

                                @Override
                                public void onAfter(@Nullable String s, @Nullable Exception e) {
                                    super.onAfter(s, e);
                                    ProgressUtil.dismiss();
                                }


                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    ProgressUtil.show(a, "", "正在提交退款申请，请稍等");
                                }

                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String, String> map1 = AnalyticalJSON.getHashMap(s);
                                    if (map1 != null) {
                                        if ("000".equals(map1.get("code"))) {
                                            ToastUtil.showToastShort("退款申请已提交，请耐心等待");
                                            map.put("status", "3");
                                            ((TextView) view).setText("已退款");
                                            view.setEnabled(false);
                                            Intent intent = new Intent("tuikuan");
                                            a.sendBroadcast(intent);
                                        } else if ("444".equals(map1.get("code"))) {
                                            ToastUtil.showToastShort("退款申请失败，请稍后重试或联系客服");
                                        }
                                    }
                                }
                            });
                }
            });
        }

        private void YingYaoJin(BaseViewHolder holder, HashMap<String, String> map) {
            holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
            holder.getView(R.id.pintuan).setVisibility(View.INVISIBLE);
            HashMap<String, String> m1 = new HashMap<>();
            m1.put("sut_title", map.get("sut_title"));
            m1.put("num", map.get("num"));
            m1.put("snr", map.get("snr"));
            ArrayList<HashMap<String, String>> l = new ArrayList<>();
            l.add(m1);
            ((mRecyclerView) holder.getView(R.id.listview)).setLayoutManager(new LinearLayoutManager(a));
            ((mRecyclerView) holder.getView(R.id.listview)).addItemDecoration(new mItemDecoration(a));
            ((mRecyclerView) holder.getView(R.id.listview)).setAdapter
                    (new itemAdapter(3, l));
            int num = 0;
            for (HashMap<String, String> m : l) {
                num += Integer.valueOf(m.get("num"));
            }
            SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money"));
            ss.setSpan(new ForegroundColorSpan(Color.BLACK), (num + "").length() + 7, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.setText(R.id.numMoney, ss);
            ((itemAdapter) ((mRecyclerView) holder.getView(R.id.listview)).getAdapter()).setOnRecyclerViewItemClickListener(null);
        }

        private void PuTong(final BaseViewHolder holder, final HashMap<String, String> map) {
            holder.setText(R.id.status, "已支付");
            ArrayList<HashMap<String, String>> l = AnalyticalJSON.getList_zj(map.get("order"));

            if (l != null) {
                if (l.size() == 0) {
                    HashMap<String, String> map1 = new HashMap<>();
                    map1.put("title", map.get("sut_title"));
                    map1.put("num", map.get("num"));
                    l.add(map1);
                }
                ((mRecyclerView) holder.getView(R.id.listview)).setLayoutManager(new LinearLayoutManager(a));
                ((mRecyclerView) holder.getView(R.id.listview)).addItemDecoration(new mItemDecoration(a));
                ((mRecyclerView) holder.getView(R.id.listview)).setAdapter
                        (new itemAdapter(1, l));
                int num = 0;
                for (HashMap<String, String> m : l) {
                    num += Integer.valueOf(m.get("num"));
                }
                if (isAgent) {//店铺订单
                    String credit = String.valueOf(Double.valueOf(map.get("money")).intValue());
                    String allow = "";
                    String get = String.valueOf(Double.valueOf(Double.valueOf(map.get("money")) * (100 - Integer.valueOf(map.get("ratio"))) / 100).intValue());
                    if (map.get("status").equals("2") && map.get("delivery").equals("2")) {
                        allow = " (总共:" + credit + "积分 返现:" + map.get("ratio") + "% 获得:" + get + "积分)";
                    }
                    SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money") + allow);
                    holder.setText(R.id.numMoney, ss);
                } else if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {
                    SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money") + ((map.get("store") != null && !map.get("store").equals("")) ? ("  来自" + map.get("store")) : ""));
                    ss.setSpan(new ForegroundColorSpan(Color.BLACK), (num + "").length() + 7, (num + "").length() + 7 + map.get("money").length() + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    if (map.get("store") != null && !map.get("store").equals("")) {
                        ss.setSpan(new ForegroundColorSpan(Color.RED), ss.length() - map.get("store").length(), ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    holder.setText(R.id.numMoney, ss);
                } else {//普通用户
                    String get = "";


                    get = String.valueOf(Double.valueOf(Double.valueOf(map.get("money").equals("")?"0":map.get("money")) * (Integer.valueOf(map.get("ratio").equals("") ? "0" : map.get("ratio"))) / 100).intValue());


                    String allow = "";
                    if (map.get("status").equals("2") && map.get("delivery").equals("2") && !get.equals("")) {
                        allow = " (" + " 获得:" + get + "积分)";
                    }

                    SpannableString ss = new SpannableString("共" + num + "件商品,实付￥" + map.get("money") + allow + ((map.get("store") != null && !map.get("store").equals("")) ? ("  来自" + map.get("store")) : ""));
                    ss.setSpan(new ForegroundColorSpan(Color.RED), ss.length() - ((map.get("store") != null && !map.get("store").equals("")) ? map.get("store").length() : 0), ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    holder.setText(R.id.numMoney, ss);

                }

                ((itemAdapter) ((mRecyclerView) holder.getView(R.id.listview)).getAdapter()).setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int i) {
                        String snr = map.get("snr");
                        Intent intent = new Intent(a, MyZhiFuDetail.class);
//                        if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {
                            intent.putExtra("id", map.get("id"));
//                        }
                        intent.putExtra("user_id",map.get("user_id"));
                        intent.putExtra("isAgent",isAgent);
                        intent.putExtra("status", ((TextView) holder.getView(R.id.pintuan)).getText().toString());
                        intent.putExtra("snr", snr);
                        intent.putExtra("KT", map.get("number").equals("") ? false : true);
                        a.startActivity(intent);
                    }
                });

            }
            if (isAgent) {
                if (map.get("status").equals("3")) {
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "已退款");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                } else if (map.get("delivery").equals("2")) {
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "已发货");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                } else if (map.get("delivery").equals("1")) {
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "等待发货");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                }
                holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
            } else if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {
                if (map.get("status").equals("3")) {
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "已退款");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                } else {
                    if (map.get("delivery").equals("1")) {
                        holder.setText(R.id.pintuan, "发货");
                        holder.getView(R.id.tuikuan).setVisibility(View.VISIBLE);
                        holder.setText(R.id.tuikuan, "退款");
                        holder.getView(R.id.tuikuan).setEnabled(true);
                        holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                        holder.getView(R.id.pintuan).setEnabled(true);
                        holder.setOnClickListener(R.id.tuikuan, new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("id", map.get("id"));
                                    js.put("user_id", map.get("user_id"));
                                    js.put("shop_id", map.get("shop_id"));
                                    js.put("m_id", Constants.M_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                OkHttpUtils.post(Constants.TuiKuan_Normal).params("key", m.K())
                                        .params("msg", m.M()).execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(String s, Call call, Response response) {
                                        HashMap<String, String> map1 = AnalyticalJSON.getHashMap(s);
                                        if (map1 != null) {
                                            if ("000".equals(map1.get("code"))) {
                                                ToastUtil.showToastShort("退款申请已提交，请耐心等待");
                                                map.put("status", "3");
                                                ((TextView) view).setText("已退款");
                                                view.setEnabled(false);
                                                Intent intent = new Intent("tuikuan");
                                                a.sendBroadcast(intent);
                                            } else if ("444".equals(map1.get("code"))) {
                                                ToastUtil.showToastShort("退款申请失败，请稍后重试或联系客服");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onBefore(BaseRequest request) {
                                        super.onBefore(request);
                                        ProgressUtil.show(a, "", "请稍等");
                                    }

                                    @Override
                                    public void onAfter(@Nullable String s, @Nullable Exception e) {
                                        super.onAfter(s, e);
                                        ProgressUtil.dismiss();
                                    }
                                });
                            }
                        });
                        holder.setOnClickListener(R.id.pintuan, new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                Intent intent = new Intent(a, Fahuo.class);
                                intent.putExtra("id", map.get("id"));
                                intent.putExtra("snr", map.get("snr"));
                                intent.putExtra("money",map.get("money"));
                                intent.putExtra("user_id",map.get("user_id"));
                                startActivity(intent);
                            }
                        });
                    } else {
                        holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                        holder.setText(R.id.pintuan, "已发货");
                        holder.getView(R.id.pintuan).setEnabled(false);
                        holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    }
                }

            } else {
                if (map.get("status").equals("3")) {
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "已退款");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                } else if (map.get("delivery").equals("2")) {
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "已发货");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                } else if (map.get("delivery").equals("1")) {
                    holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.pintuan).setEnabled(false);
                    holder.setText(R.id.pintuan, "等待发货");
                    holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                }
                holder.getView(R.id.pintuan).setVisibility(View.VISIBLE);
                holder.getView(R.id.tuikuan).setVisibility(View.INVISIBLE);
            }
        }
    }
}
