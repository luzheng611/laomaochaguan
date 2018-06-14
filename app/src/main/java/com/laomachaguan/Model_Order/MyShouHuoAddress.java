package com.laomachaguan.Model_Order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/15.
 */
public class MyShouHuoAddress extends AppCompatActivity implements View.OnClickListener ,SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "MyShouHuoAddress";
    private ListView listView;
    private ImageView tip;
    private ImageView back, add;
    private SharedPreferences sp;
    private List<HashMap<String, String>> list;
    private mAdapter adapter;
    private boolean hasDeleteCurrentId = false;
    private String cureentID;
    private boolean updateCurrentId = false;
    private int updatePosition = -1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isFirstLoad=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shouhuo_list);
        initView();
        getData();
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);

        super.onDestroy();
    }

    /**
     * 获取数据
     */
    private void getData() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();
                    js.put("user_id", sp.getString("user_id", ""));
                    String data = OkHttpUtils.post(Constants.Shucheng_shouhuo_list_Ip).
                            tag(TAG).params("key", ApisSeUtil.getKey()).params("msg",ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        Log.w(TAG, "run: -=-=-" + data);
                        list = AnalyticalJSON.getList(data, "address");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (list != null) {
                                    if (adapter == null) {
                                        adapter = new mAdapter();
                                    }
                                    listView.setAdapter(adapter);
                                    if (list.size() == 0) {
                                        if(isFirstLoad){
                                            Intent intent=new Intent(MyShouHuoAddress.this,add_address.class);
                                            startActivityForResult(intent,0);
                                        }
                                        isFirstLoad=false;
                                        Toast.makeText(MyShouHuoAddress.this, "暂无收货地址,请点击右上角添加", Toast.LENGTH_SHORT).show();
                                    }
                                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                                        swipeRefreshLayout.setRefreshing(false);
                                } else {
                                    tip.setVisibility(View.VISIBLE);
                                    tip.setOnClickListener(MyShouHuoAddress.this);
                                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                                        swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                                    swipeRefreshLayout.setRefreshing(false);
                                tip.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tip.setVisibility(View.VISIBLE);
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 初始化控件
     */
    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip);
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);
        swipeRefreshLayout.setOnRefreshListener(this);
        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.mshouhuo_listview);
         StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        sp = getSharedPreferences("user", MODE_PRIVATE);
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setImageResource(R.drawable.back);
        back.setOnClickListener(this);
        ((TextView) findViewById(R.id.title_title)).setText("我的收货地址");

        add = (ImageView) findViewById(R.id.title_image2);
        add.setVisibility(View.VISIBLE);
        add.setImageResource(R.drawable.home_add_more);
        add.setOnClickListener(this);

        tip = (ImageView) findViewById(R.id.shucheng_tip);
        tip.setOnClickListener(this);
        cureentID = getIntent().getStringExtra("id");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
//                intent.putExtra("name", ((TextView) view.findViewById(R.id.commit_dingdan_user)).getText().toString());
//                intent.putExtra("address", ((TextView) view.findViewById(R.id.commit_dingdan_address)).getText().toString());
//                intent.putExtra("phone", ((TextView) view.findViewById(R.id.commit_dingdan_phone)).getText().toString());
//                intent.putExtra("youbian", ((TextView) view.findViewById(R.id.commit_dingdan_address)).getTag().toString());
//                intent.putExtra("id", ((TextView) view.findViewById(R.id.commit_dingdan_user)).getTag().toString());

                intent.putExtra("name", list.get(position).get("receiver"));
                intent.putExtra("address", list.get(position).get("address"));
                intent.putExtra("phone", list.get(position).get("phone"));
                intent.putExtra("youbian", list.get(position).get("zipcode"));
                intent.putExtra("province", list.get(position).get("province"));
                intent.putExtra("city", list.get(position).get("city"));
                intent.putExtra("id", list.get(position).get("id"));
                SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("address", list.get(position).get("address"));
                ed.putString("phone",list.get(position).get("phone"));
                ed.putString("name", list.get(position).get("receiver"));
                ed.putString("youbian", list.get(position).get("zipcode"));
                ed.putString("province", list.get(position).get("province"));
                ed.putString("city", list.get(position).get("city"));
                ed.putString("id", list.get(position).get("id"));
                ed.commit();
                setResult(0x00, intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                if (hasDeleteCurrentId) {
                    setResult(100);
                    LogUtil.e("返回100");
                } else {
                    setResult(200);
                    LogUtil.e("返回200");
                }
                if (updateCurrentId && updatePosition != -1) {
                    Intent intent = new Intent();
                    intent.putExtra("name", list.get(updatePosition).get("receiver"));
                    intent.putExtra("address", list.get(updatePosition).get("address"));
                    intent.putExtra("phone", list.get(updatePosition).get("phone"));
                    intent.putExtra("youbian", list.get(updatePosition).get("zipcode"));
                    intent.putExtra("province", list.get(updatePosition).get("province"));
                    intent.putExtra("city", list.get(updatePosition).get("city"));
                    intent.putExtra("id", cureentID);
                    setResult(0x00, intent);
                    LogUtil.e("返回0x00");
                }
                finish();
                break;
            case R.id.title_image2:
                Intent intent = new Intent(this, add_address.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.shucheng_tip:
                getData();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        back.performClick();
        super.onBackPressed();
//        if (hasDeleteCurrentId) {
//            setResult(100);
//        } else {
//            setResult(200);
//        }
//        if (updateCurrentId && updatePosition != -1) {
//            Intent intent = new Intent();
//            intent.putExtra("name", "收货人:" + list.get(updatePosition).get("receiver"));
//            intent.putExtra("address", "详细地址:" + list.get(updatePosition).get("address"));
//            intent.putExtra("phone", "联系方式:" + list.get(updatePosition).get("phone"));
//            setResult(0x00, intent);
//        }
//        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            getData();
        }
    }

    @Override
    public void onRefresh() {
        list.clear();
        getData();
    }


    class mAdapter extends BaseAdapter {

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
            Holder holder = null;
            final HashMap<String, String> map = list.get(position);
            if (view == null) {
                holder = new Holder();
                view = LayoutInflater.from(MyShouHuoAddress.this).inflate(R.layout.shouhuo_list_item, parent, false);
                holder.user = (TextView) view.findViewById(R.id.commit_dingdan_user);
                holder.phone = (TextView) view.findViewById(R.id.commit_dingdan_phone);
                holder.address = (TextView) view.findViewById(R.id.commit_dingdan_address);
                holder.update = (ImageView) view.findViewById(R.id.commit_dingdan_update);
                holder.delete = (ImageView) view.findViewById(R.id.commit_dingdan_delete);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            SpannableString ss=new SpannableString("收  货  人：" + map.get("receiver"));
            SpannableString ss1=new SpannableString("联系方式：" + map.get("phone"));
            SpannableString ss2=new SpannableString("详细地址：" + map.get("address"));
            ForegroundColorSpan f=new ForegroundColorSpan(Color.parseColor("#999999"));
            ss.setSpan(f,8,ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss1.setSpan(f,5,ss1.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss2.setSpan(f,5,ss2.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.user.setText(ss);
            holder.phone.setText(ss1);
            holder.address.setText(ss2);
            holder.address.setTag(map.get("zipcode"));
            holder.user.setTag(map.get("id"));
            holder.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyShouHuoAddress.this, Update.class);
                    intent.putExtra("name", map.get("receiver"));
                    intent.putExtra("phone", map.get("phone"));
                    intent.putExtra("address", map.get("address"));
                    intent.putExtra("zipcode", map.get("zipcode"));
                    intent.putExtra("id", map.get("id"));
                    startActivityForResult(intent, 0);
                    if (map.get("id").equals(cureentID))
                        updateCurrentId = true;
                    updatePosition = position;
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    v.setEnabled(false);
                    //删除地址
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js=new JSONObject();
                                js.put("address_id", map.get("id"));
                                String data = OkHttpUtils.post(Constants.Shucheng_shouhuo_delete_Ip)

                                        .params("key",ApisSeUtil.getKey())
                                        .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                                if (!TextUtils.isEmpty(data)) {
                                    final HashMap<String, String> map1 = AnalyticalJSON.getHashMap(data);
                                    if (map1 != null) {
                                        if ("000".equals(map1.get("code"))) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    list.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                    Log.w(TAG, "run: 已设置的Id:" + cureentID + "  后台获取的删除的id:" + map.get("id"));
                                                    if (map.get("id").equals(cureentID)) {
                                                        hasDeleteCurrentId = true;
                                                        updateCurrentId = false;

                                                    }
                                                    v.setEnabled(true);
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
            });
            return view;
        }

        class Holder {
            TextView user, phone, address;
            ImageView update, delete;
        }
    }
}
