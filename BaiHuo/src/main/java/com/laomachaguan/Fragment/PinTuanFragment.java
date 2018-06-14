package com.laomachaguan.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.Model_Order.Pintuan_Detail;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.View.mItemDecoration;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 作者：因陀罗网 on 2017/6/11 11:44
 * 公司：成都因陀罗网络科技有限公司
 */

public class PinTuanFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String pingtuan = "1";
    private static final String guanzhu = "2";
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private DingdanAdapter adapter;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private static final String TAG = "PinTuanFragment";
    Bundle b;
    private String url;

    //    BroadcastReceiver receive=new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getBooleanExtra("tuikuan",false)){
//                onRefresh();
//            }
//        }
//    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        b = getArguments();
        if (b == null) {//个人

        } else {
            switch (b.getString("tag")) {
                case pingtuan:
                    url = Constants.MyPinTuan_Normal;
                    break;
                case guanzhu:
                    url = Constants.PinTuan_Guanzhu_List;
                    break;
            }
            ;
        }
//        IntentFilter intentFilter=new IntentFilter("tuikuan");
//        getActivity().registerReceiver(receive,intentFilter);
        View view = inflater.inflate(R.layout.fragment_dingdan, container, false);
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerView.addItemDecoration(new mItemDecoration(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView textView = new TextView(getActivity());
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.loadnothing);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        if (b.getString("tag").equals("1")) {
            textView.setText("暂未参与任何拼团");
        } else {
            textView.setText("暂未收藏任何拼团");
        }

        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 180);
        textView.setLayoutParams(vl);
        adapter = new DingdanAdapter(getActivity(), new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        adapter.openLoadMore(10, true);
        adapter.setEmptyView(textView);
        adapter
                .setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int i) {
                        Intent intent = new Intent(getActivity(), Pintuan_Detail.class);
                        intent.putExtra("id", adapter.getData().get(i).get("id"));
                        startActivity(intent);
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
//        getActivity().unregisterReceiver(receive);
    }

    private void getData() {
        if (!Network.HttpTest(getActivity())) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
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
                                    adapter.setNewData(list);
//                                    if (isRefresh) {
//                                        adapter.setNewData(list);
//                                        isRefresh = false;
//                                        swip.setRefreshing(false);
//                                    } else if (isLoadMore) {
//                                        isLoadMore = false;
//                                        if (list.size() < 10) {
//                                            ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
////                                endPage = page;
//                                            adapter.notifyDataChangedAfterLoadMore(list, false);
//                                        } else {
//                                            adapter.notifyDataChangedAfterLoadMore(list, true);
//                                        }
//                                    }

                                    swip.setRefreshing(false);
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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
//        adapter.openLoadMore(10, true);
        getData();
    }

    public class DingdanAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        private Activity a;

        public DingdanAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
            super(R.layout.item_mine_pintuan, data);
            WeakReference<Activity> w = new WeakReference<Activity>(activity);
            a = w.get();
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {

            holder.setText(R.id.title, map.get("title"))
                    .setText(R.id.price, "拼团价:￥" + map.get("price"))
            .setText(R.id.time, "有效时间:"+TimeUtils.getTrueTimeStr(map.get("start_time"))+" 至 "+TimeUtils.getTrueTimeStr(map.get("end_time")));
            LogUtil.e("状态：：："+map.get("status"));
            if (b.getString("tag").equals("1")) {
                if ("1".equals(map.get("start"))) {

                    holder.setText(R.id.status, "等待拼团");
                } else if ("2".equals(map.get("start"))) {

                    holder.setText(R.id.status, "拼团成功");
                } else if ("3".equals(map.get("start"))) {

                    holder.setText(R.id.status, "已退款");
                }

            } else {
                holder.setText(R.id.status, "关注成功");
            }
            Glide.with(a).
                    load(map.get("image1")).
                    override(DimenUtils.dip2px(a, 80), DimenUtils.dip2px(a, 80)).centerCrop()
                    .into((ImageView) holder
                            .getView(R.id.image1));
        }
    }
}
