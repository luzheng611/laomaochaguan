package com.laomachaguan.Common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.Model_activity.activity_Detail;
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
 * Created by Administrator on 2016/10/10.
 */
public class Search extends AppCompatActivity implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back://返回
                finish();
                break;
            case R.id.search_sousuo://搜索
                getData();
                break;
            case R.id.search_zixun://资讯分类
                resetStatus();
                zixun.setSelected(true);
                type = "news";
                break;
            case R.id.search_huodong://活动分类
                resetStatus();
                huodong.setSelected(true);
                type = "activity";
                break;
            case R.id.search_gongyang://供养分类
                resetStatus();
                gongyang.setSelected(true);
                type = "shop";
                break;
            case R.id.search_cishan://慈善分类
                resetStatus();
                zhongchou.setSelected(true);
                type = "cfg";
                break;
        }


    }

    /**
     * 搜索获取数据并保存搜索记录
     */
    private void getData() {

        if (type.equals("news")) {
            url = Constants.News_Search_Ip;
        }
//        else if(type.equals("activity")){
//            url=Constants.Activity_Search_Ip;
//        }else if(type.equals("shop")){
//            url=Constants.GY_Search_Ip;
//        }else if(type.equals("cfg")){
//                url=Constants.CFG_Search_Ip;
//        }
        if (url.equals("")) {
            Toast.makeText(Search.this, "请选择分类标签", Toast.LENGTH_SHORT).show();
            return;
        }
        if (input.getText().toString().equals("")) {
            Toast.makeText(Search.this, "请输入关键字", Toast.LENGTH_SHORT).show();
        }
        p.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();

                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("msg", input.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String data = OkHttpUtils.post(url)
                            .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!data.equals("")) {
                                listList = AnalyticalJSON.getList(data, type);
                                if (listList != null) {
                                    if (listList.size() == 0) {
                                        Toast.makeText(Search.this, "未搜索到相关信息", Toast.LENGTH_SHORT).show();
                                    }
                                    if (adapter.getList().size() == 0) {
                                        adapter.addList(listList);
                                        listView.setAdapter(adapter);
                                        p.setVisibility(View.GONE);
                                    } else {
                                        adapter.addList(listList);
                                        adapter.notifyDataSetChanged();
                                        p.setVisibility(View.GONE);
                                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(), 0);
                                    }
                                } else {
                                    Toast.makeText(Search.this, "未搜索到相关信息", Toast.LENGTH_SHORT).show();
                                    if (p != null) p.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(Search.this, "未搜索到相关信息", Toast.LENGTH_SHORT).show();
                                if (p != null) p.setVisibility(View.GONE);
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private listAdapter adapter;
    private static final String TAG = "Search";
    private String url;//搜索的接口地址
    private ImageView back;
    private EditText input;
    private TextView sousuo, zixun, huodong, gongyang, zhongchou, removeAll;
    private ListView listView;
    private GridView grid;
    private List<String> gridList;
    private List<HashMap<String, String>> listList;
    private String type;//搜索类别标示
    private ProgressBar p;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        initView();
//        loadHistroy();
    }

    /**
     * 重置类别
     */
    private void resetStatus() {
        zixun.setSelected(false);
        huodong.setSelected(false);
        gongyang.setSelected(false);
        zhongchou.setSelected(false);
    }

    /**
     * 初始化数据
     */
    private void initView() {
        listList = new ArrayList<>();
        adapter = new listAdapter(this, listList);
        back = (ImageView) findViewById(R.id.search_back);
        back.setOnClickListener(this);
        p = (ProgressBar) findViewById(R.id.search_loading);
        input = (EditText) findViewById(R.id.search_edit);//搜索输入框
        sousuo = (TextView) findViewById(R.id.search_sousuo);//搜索按钮
        zixun = (TextView) findViewById(R.id.search_zixun);
        huodong = (TextView) findViewById(R.id.search_huodong);
        gongyang = (TextView) findViewById(R.id.search_gongyang);
        zhongchou = (TextView) findViewById(R.id.search_cishan);
//        removeAll= (TextView) findViewById(R.id.search_removeHistory);//清空记录
        listView = (ListView) findViewById(R.id.search_listview);//搜索结果展示listview
//        grid= (GridView) findViewById(R.id.search_grid);//搜索历史gridView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView view1 = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
                String id1 = view1.getTag().toString();
                Intent intent = new Intent();

                if (view1.getText().toString().equals("图文")) {
                    intent.setClass(mApplication.getInstance(), ZiXun_Detail.class);
                } else if (view1.getText().toString().equals("活动")) {
                    intent.setClass(mApplication.getInstance(), activity_Detail.class);
                }
                intent.putExtra("id", id1);
                startActivity(intent);
            }
        });

        sousuo.setOnClickListener(this);
        zixun.setOnClickListener(this);
        huodong.setOnClickListener(this);
        gongyang.setOnClickListener(this);
        zhongchou.setOnClickListener(this);
//        removeAll.setOnClickListener(this);
        zixun.performClick();
    }

//    private void loadHistroy(){
//        gridList= (List<String>) IOUtil.getData(this,TAG,"history",gridList);
//        if(gridList==null){
//            gridList=new ArrayList<>();
//            findViewById(R.id.search_tip).setVisibility(View.VISIBLE);
//        }else{
//                gridAdapter gridadatpter=new gridAdapter(this,gridList);
//                grid.setAdapter(gridadatpter);
//
//
//        }
//    }

    /**
     * 搜索历史适配器
     */
    static class gridAdapter extends BaseAdapter {
        private List<String> list1;
        private Context context;

        public gridAdapter(Context context, List<String> list) {
            super();
            this.context = context;
            this.list1 = list;

        }

        @Override
        public int getCount() {
            return list1 == null ? 0 : list1.size();
        }

        @Override
        public Object getItem(int position) {
            return list1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(context);
            textView.setTextSize(14);
            textView.setMaxLines(1);
            textView.setTextColor(Color.BLACK);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(list1.get(position));
            return textView;
        }
    }

    /**
     * 搜索结果适配器
     */
    static class listAdapter extends BaseAdapter {
        public List<HashMap<String, String>> list;
        private Context context;

        private LayoutInflater inflater;
        private int screenWidth;

        public listAdapter(Context context, List<HashMap<String, String>> list1) {
            super();
            this.context = context;
            this.list = list1;
            inflater = LayoutInflater.from(context);
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        }

        public List<HashMap<String, String>> getList() {
            return list;
        }

        public void addList(List<HashMap<String, String>> list1) {
            this.list = list1;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;
            HashMap<String, String> map = list.get(position);
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.mine_shoucang_item, parent, false);
                holder.image = (ImageView) view.findViewById(R.id.mine_shoucang_item_image);
                holder.title = (TextView) view.findViewById(R.id.mine_shoucang_item_title);
                holder.time = (TextView) view.findViewById(R.id.mine_shoucang_item_time);
                holder.user = (TextView) view.findViewById(R.id.mine_shoucang_item_user);
                holder.type = (TextView) view.findViewById(R.id.mine_shoucang_item_type);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (map.get("end_time") == null && map.get("product") == null && map.get("time") != null) {
                holder.time.setText(map.get("issue_time"));
                holder.title.setText(map.get("title"));
                holder.user.setText(map.get("issuer"));
                holder.type.setText("图文");
                holder.type.setTag(map.get("id"));
                Glide.with(context).load(map.get("image")).override(screenWidth * 3 / 10, screenWidth / 5).centerCrop().into(holder.image);
            }
//            else if (map.get("cy_people") == null && map.get("end_time") != null) {
//                holder.type.setText("活动");
//                holder.type.setTag(map.get("id"));
//                holder.user.setText(map.get("author"));
//                holder.title.setText(map.get("title"));
//                Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth / 5).centerCrop().into(holder.image);
//                holder.time.setText("开始时间：" + map.get("time") + "\n结束时间：" + map.get("end_time"));
//            } else if (map.get("cy_people") != null) {
//                holder.type.setText("慈善");
//                holder.type.setTag(map.get("id"));
//                holder.user.setText("参与人数：" + map.get("cy_people"));
//                holder.title.setText(map.get("title"));
//                Glide.with(context).load(map.get("image")).override(screenWidth * 3 / 10, screenWidth / 5).centerCrop().into(holder.image);
//                holder.time.setText("开始时间：" + map.get("start_time") + "\n结束时间：" + map.get("end_time"));
//            } else if (map.get("product") != null) {
//                holder.type.setText("供养");
//                holder.type.setTag(map.get("id"));
//                Glide.with(context).load(map.get("image")).override(screenWidth / 5, screenWidth / 5).centerCrop().into(holder.image);
//                holder.user.setText(map.get("product"));
//                holder.title.setText(map.get("type1"));
//                holder.time.setText("￥" + map.get("money1"));
//            }

            return view;
        }

        static class ViewHolder {
            ImageView image;
            TextView title, user, time, type;
        }
    }

    @Override
    protected void onDestroy() {
//        removeDuplicate(gridList);
//        IOUtil.setData(this,TAG,"history",gridList);
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(), 0);
        super.onDestroy();

    }

//    public   static   void  removeDuplicate(List list)   {
//        HashSet h  =   new  HashSet(list);
//        list.clear();
//        list.addAll(h);
//        Log.e(TAG, "removeDuplicate: "+list );
//    }
}
