package com.laomachaguan.Common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.Adapter.Mine_SC_adapter;
import com.laomachaguan.Model_Order.Order_detail;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 */
public class Activity_ShouCang extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageView back, tip;
    private TextView title;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SharedPreferences sp;
    private Mine_SC_adapter SCadapter;
    private static final String TAG = "Activity_ShouCang";
    List<HashMap<String, String>> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.shoucang_activity);

        initView();
        getData();

    }

    /**
     * 获取数据
     */
    private void getData() {
        if (sp == null) {
            sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        }
//        if (sp.getString("user_id", "").equals("") || sp.getString("uid", "").equals("")) {
//            tip.setText("暂无收藏信息,请点击头像登录");
//            tip.setVisibility(View.VISIBLE);
//            SCadapter.list.clear();
//            SCadapter.notifyDataSetChanged();
//            return;
//        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    js.put("user_id", sp.getString("user_id", ""));
                    js.put("m_id",Constants.M_id);
                    ApisSeUtil.M m=ApisSeUtil.i(js);

                    String data = OkHttpUtils.post(Constants.Zixun_shoucang_list_IP).tag(TAG)
                            .params("key", m.K())
                            .params("msg",m.M())
                            .execute().body().string();
                    if (!data.equals("")) {
                        Log.w(TAG, "run: news_+-=-getData-=-=-=" + data);
                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data, "draft");///资讯
                        if (mlist == null) {

                        } else {
                            if (list != null) list.addAll(mlist);
                        }
                    }
                    JSONObject js1=new JSONObject();
                    js1.put("user_id", sp.getString("user_id", ""));
                    js1.put("m_id",Constants.M_id);
                    ApisSeUtil.M m1=ApisSeUtil.i(js1);
                    String data1 = OkHttpUtils.post(Constants.Order_shoucang_list)
                            .tag(TAG)
                            .params("key", m1.K())
                            .params("msg",m1.M())
                            .execute().body().string();
                    if (!data1.equals("")) {
                        List<HashMap<String, String>> mlist1 = AnalyticalJSON.getList(data1, "orderlist");///商品
                        if (mlist1 == null) {

                        } else {
                            if (list != null) list.addAll(mlist1);
                        }
                    }
//                    String data1 = OkHttpUtils.post(Constants.Activity_Shoucang_list_IP).tag(TAG)   .params("key", ApisSeUtil.getKey())
//                            .params("msg",ApisSeUtil.getMsg(js)) .execute().body().string();
//                    if (!data1.equals("")) {
//                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data1, "activity");//活动
//                        if (mlist == null) {
//
//                        } else {
//                            if (list != null) list.addAll(mlist);
//                        }
//                    }
//                    String data2 = OkHttpUtils.post(Constants.FUNDING_DETAIL_Shoucang_List).tag(TAG)   .params("key", ApisSeUtil.getKey())
//                            .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
//                    if (!data2.equals("")) {
//                        Log.w(TAG, "run: Fund+-=-getData-=-=-=" + data2);
//                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data2, "crowdfunding");//慈善
//                        if (mlist == null) {
//
//                        } else {
//                            if (list != null) list.addAll(mlist);
//                        }
//                    }
//                    String data3 = OkHttpUtils.post(Constants.Shucheng_shoucang_list_Ip).tag(TAG).params("user_id", sp.getString("user_id", "")).params("key", Constants.safeKey)
//                            .execute().body().string();
//                    if (!data3.equals("")) {
//                        Log.w(TAG, "run: 书城+-=-getData-=-=-=" + data3);
//                        List<HashMap<String, String>> mlist = AnalyticalJSON.getList(data3, "books");//图书
//                        if (mlist == null) {
//
//                        } else {
//                            if (list != null) list.addAll(mlist);
//                        }
//                    }

                    tip.post(new Runnable() {
                        @Override
                        public void run() {
                            if (SCadapter == null) {
                                SCadapter = new Mine_SC_adapter(Activity_ShouCang.this);
                            }
                            if (list != null) {
                                if (list.size() == 0) {
                                    if (tip != null && tip.getVisibility() != View.VISIBLE) {
                                        Toast.makeText(Activity_ShouCang.this, "暂无收藏信息", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if (SCadapter != null && listView != null && tip != null) {
                                        List<HashMap<String, String>> list1 = list;
                                        SCadapter.addList(list1);
                                        listView.setAdapter(SCadapter);
                                    }
                                }
                            }

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
         StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        back.setImageResource(R.drawable.back);
        title = (TextView) findViewById(R.id.title_title);
        title.setText("收藏");
        tip = (ImageView) findViewById(R.id.shoucang_tip);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip);
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);
        swipeRefreshLayout.setOnRefreshListener(this);
        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.shoucang_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView view1 = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
                String id1 = view1.getTag().toString();
                Intent intent = new Intent();

                if (view1.getText().toString().equals("圈子")) {
                    intent.setClass(mApplication.getInstance(), Home_Zixun_detail.class);
                } else if (view1.getText().toString().equals("商品")) {
                    intent.setClass(mApplication.getInstance(), Order_detail.class);
                }

                intent.putExtra("id", id1);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
//        getData();
        tip.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        },3000);
    }
}
