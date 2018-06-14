package com.laomachaguan.Model_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.LoadMoreListView;
import com.lzy.okhttputils.OkHttpUtils;

import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2016/10/5.
 */
public class activity_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreListView.OnLoadMore {
    private View view;
    private SwipeRefreshLayout swip;//下拉刷新控件
    private LoadMoreListView listView;
    private ImageView tip;//加载失败的提示
    private boolean isFirstIn = true;
    private String page = "1";
    private String endPage = "";
    private activity_adapter adpter;
    private boolean isRefresh = false;//是否属于下拉刷新操作
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private static final String TAG = "activity_fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment, container, false);
        return view;
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(View view) {
        swip = (SwipeRefreshLayout) view.findViewById(R.id.activity_swip);
        listView = (LoadMoreListView) view.findViewById(R.id.activity_listview);
        swip.setOnRefreshListener(this);
        listView.setLoadMoreListen(this);
        listView.setOnItemClickListener(onItemClickListener);
        t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));
        tip = (ImageView) view.findViewById(R.id.activity_tip);
        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                swip.post(new Runnable() {
                    @Override
                    public void run() {
                        swip.setRefreshing(true);
                        getData();
                    }
                });
            }
        });
        swip.setColorSchemeResources(R.color.main_color);
        swip.post(new Runnable() {
            @Override
            public void run() {
                if (isFirstIn) {
                    swip.setRefreshing(true);
                    getData();
                }
                isFirstIn = false;
            }
        });
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(mApplication.getInstance(), activity_Detail.class);
            String Id = "";
            if (position == 0) {
                Id = ((TextView) view.findViewById(R.id.activity_title)).getTag().toString();
            } else {
                Id = ((TextView) view.findViewById(R.id.activity_item_title)).getTag().toString();
            }
            if (!TextUtils.isEmpty(Id)) {
                intent.putExtra("id", Id);
                startActivity(intent);
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {//首次可见时加载数据
            initView(view);
        }
    }

    /**
     * 退出页面时的设置
     */
    @Override
    public void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroy();
    }

    /**
     * 从网络获取数据
     */
    private void getData() {
        if (!Network.HttpTest(mApplication.getInstance())) {
            Toast.makeText(mApplication.getInstance(), "网络连接失败，请下拉刷新", Toast.LENGTH_SHORT).show();
            if (null != swip && swip.isShown()) swip.setRefreshing(false);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = OkHttpUtils.post(Constants.Activity_list_IP).tag(TAG).params("page", page).params("m_id", Constants.M_id)
                            .params("key", Constants.safeKey).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list = AnalyticalJSON.getList(data, "activity");
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (list != null) {//数据解析成功
                                    if (list.size() != 10) {
                                        endPage = page;
                                    }
                                    if (adpter == null) {//第一次加载
                                        adpter = new activity_adapter(mApplication.getInstance(), list);
                                        listView.setAdapter(adpter);
                                        if (swip.isRefreshing()) swip.setRefreshing(false);
                                        if (listView.isLoading) listView.onLoadComplete();
                                        tip.setVisibility(View.GONE);
                                    } else {
                                        if (isRefresh) {
                                            isRefresh = false;
                                            adpter.setList(list);
                                            adpter.notifyDataSetChanged();
                                            endPage = "";
                                            if (swip.isRefreshing()) swip.setRefreshing(false);
                                            if (t.getText().toString().equals("没有更多数据了")) {
                                                listView.onLoadComplete();
                                                t.setText("正在加载....");
                                                p.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            adpter.list.addAll(list);
                                            if (endPage.equals(page)) {
                                                t.setText("没有更多数据了");
                                                p.setVisibility(View.GONE);
                                                return;
                                            } else {
                                                t.setText("正在加载....");
                                            }
                                        }
                                        listView.onLoadComplete();
                                        tip.setVisibility(View.GONE);
                                    }
                                } else {//数据解析失败
//                                    if(Network.HttpTest(getActivity())){
//                                        Glide.with(getActivity()).load(R.drawable.load_nothing).override(DimenUtils.dip2px(getActivity(),150),
//                                                DimenUtils.dip2px(getActivity(),150)).fitCenter().into(tip);
//                                    }else{
//                                        Glide.with(getActivity()).load(R.drawable.load_neterror).override(DimenUtils.dip2px(getActivity(),150),
//                                                DimenUtils.dip2px(getActivity(),150)).fitCenter().into(tip);
//                                    }
                                    tip.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tip.setVisibility(View.VISIBLE);
//                            if(Network.HttpTest(getActivity())){
//                                Glide.with(getActivity()).load(R.drawable.load_nothing).override(DimenUtils.dip2px(getActivity(),150),
//                                        DimenUtils.dip2px(getActivity(),150)).fitCenter().into(tip);
//                            }else{
//                                Glide.with(getActivity()).load(R.drawable.load_neterror).override(DimenUtils.dip2px(getActivity(),150),
//                                        DimenUtils.dip2px(getActivity(),150)).fitCenter().into(tip);
//                            }
                        }
                    });
                    e.printStackTrace();
                } finally {
                    if (listView != null) {
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (swip != null && swip.isRefreshing()) swip.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    /**
     * 上拉加载
     */
    @Override
    public void loadMore() {
        if (!endPage.equals(page)) {
            page = String.valueOf(Integer.parseInt(page) + 1);
        } else {
            p.setVisibility(View.GONE);
            t.setText("没有更多数据了");
            return;
        }
        getData();
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        page = "1";
        isRefresh = true;
        endPage = "";
        getData();
    }
}
