package com.laomachaguan.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.Adapter.expandAdapter;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LocalGroupSearch;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.LoadMoreListViewExpand;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/2.
 */
public class Fans_List extends AppCompatActivity implements LoadMoreListViewExpand.OnLoadMore, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ImageView back;
    private TextView title;
    private LoadMoreListViewExpand listView;
    private String page = "1";
    private String endPage = "";
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private expandAdapter adapter;
    private static final String TAG = "Fans_List";
    //    private int currentPos=-1;

    private SwipeRefreshLayout swip;
    private boolean isRefresh = false;
    private EditText search;
    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    onRefresh();
                }
            });
        }
    }    ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.fans_focus_list);
        initView();
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
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
            t.setText("没有更多好友了");
            return;
        }
        getData();
    }

    @Override
    public void onRefresh() {
        page = "1";
        endPage = "";
        isRefresh = true;
        getData();
    }

    /**
     * 加载数据
     */
    private void getData() {
        if (!Network.HttpTest(this)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);
                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.FridendList).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final ArrayList<HashMap<String, String>> list1 = AnalyticalJSON.getList_zj(data);
                        Log.w(TAG, "run: list------>" + list1);
                        if (list1 != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (list1.size() == 0) {
                                        Fans_List.this.p.setVisibility(View.GONE);
                                        t.setText("没有更多好友了");
                                        Toast.makeText(Fans_List.this, "暂无好友用户", Toast.LENGTH_SHORT).show();

                                        if (swip != null && swip.isRefreshing())
                                            swip.setRefreshing(false);
                                        return;
                                    }

                                    if (list1.size() < 10) {
                                        endPage = page;
                                        p.setVisibility(View.GONE);
                                        t.setText("没有更多好友了");
                                    }
                                    if (adapter == null) {
                                        adapter = new expandAdapter(Fans_List.this, list1, expandAdapter.Fans);
                                        listView.setAdapter(adapter);
                                    } else {
                                        if (isRefresh) {
                                            isRefresh = false;
                                            adapter.addList(list1);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            adapter.list.addAll(list1);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                    if (swip != null && swip.isRefreshing())
                                        swip.setRefreshing(false);
                                    listView.onLoadComplete();

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (swip != null && swip.isRefreshing())
                                        swip.setRefreshing(false);

                                    Toast.makeText(Fans_List.this, "暂无好友用户", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                  runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (swip != null && swip.isRefreshing()) swip.setRefreshing(false);
                            Toast.makeText(Fans_List.this, "加载异常，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      unregisterReceiver(receiver);
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

 

    private void initView() {
        IntentFilter intentFilter=new IntentFilter("bz");
        registerReceiver(receiver,intentFilter);
        back= (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title= (TextView) findViewById(R.id.title_title);
        title.setText("好友列表");
        search= (EditText)findViewById(R.id.search);
        search.addTextChangedListener(textWatcher);
        swip = (SwipeRefreshLayout) findViewById(R.id.ask_swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);


        listView = (LoadMoreListViewExpand)findViewById(R.id.fund_people_list);
        listView.setLoadMoreListen(this);
        listView.setGroupIndicator(null);
        listView.setChildIndicator(null);

        t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));

    }
    /**
     * 搜索框监听
     */
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            if (s != null && s.length() > 0&&adapter!=null&&adapter.groups!=null) {
                ArrayList<HashMap<String  ,String >> listG = LocalGroupSearch.searchGroup(s, adapter.groups);
                if (listG.size() > 0) {

                } else {
                    ToastUtil.showToastShort("未搜索到用户");
                }
                adapter.addList(listG);
                LogUtil.w("groups++++==="+adapter.groups.size()+"   listg"+listG.size());
                adapter.notifyDataSetChanged();
//                searchUtils.showListView(dAdapter, null);
            } else {
//                FLAG_SHOW_TITLE = false;
//                TitledListView.FLAG_VIEW = false;
//                searchUtils.hideListView();
                if(adapter!=null&&adapter.groups!=null){
                    adapter.addList(adapter.groups);
                    adapter.notifyDataSetChanged();
                }

            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fans_tip:
                onRefresh();
                break;
            case R.id.title_back:
                finish();
                break;
        }
    }




}
