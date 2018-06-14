package com.laomachaguan.Common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.TouGao.TouGao;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.LoadMoreListView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/2.
 */
public class TG_Mine extends AppCompatActivity implements LoadMoreListView.OnLoadMore, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ImageView back;
    private TextView title;
    private LoadMoreListView listView;
    private String page = "1";
    private String endPage = "";
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private mAdapter adapter;
    private static final String TAG = "TG_List";
    private boolean isLoadmore = false;
    private boolean isRefesh = false;
    private SwipeRefreshLayout swip;

    private ArrayList<String> choosed;
    private TextView delete;
    private EditText search;
    private Drawable d;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.tougao_list);
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
            t.setText("没有更多图文了");
            return;
        }
        isLoadmore = true;
        isRefesh = false;
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
                    String url = Constants.Tougao_Mine_list_IP;

                    JSONObject js = new JSONObject();
                    try {
                        if (PreferenceUtil.getUserIncetance(TG_Mine.this).getString("role", "").equals("3")) {
                            url = Constants.Zixun_List_IP;
                        } else {
                            js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                        }
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(url)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)&&!"null".equals(data)) {
                        final List<HashMap<String, String>> list1 = AnalyticalJSON.getList_zj(data);
                        Log.w(TAG, "run: list------>" + list1);
                        if (list1 != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (list1.size() == 0) {
                                        TG_Mine.this.p.setVisibility(View.GONE);
                                        t.setText("没有更多图文了");
                                        swip.setRefreshing(false);
                                        Toast.makeText(TG_Mine.this, "暂无稿件", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (list1.size() < 10) {
                                        endPage = page;
                                        TG_Mine.this.p.setVisibility(View.GONE);
                                        t.setText("没有更多图文了");
                                    }
                                    if (isRefesh) {
                                        if (adapter == null) {
                                            adapter = new mAdapter(TG_Mine.this, list1);
                                            for (int i = 0; i < list1.size(); i++) {
                                                adapter.flags.add(false);
                                            }
                                            listView.setAdapter(adapter);
                                        } else {
                                            adapter.list.clear();
                                            choosed.clear();
                                            adapter.flags.clear();
                                            for (int i = 0; i < list1.size(); i++) {
                                                adapter.flags.add(false);
                                            }
                                            adapter.list.addAll(list1);
                                            adapter.notifyDataSetChanged();
                                        }
                                        isRefesh = false;
                                    } else if (isLoadmore) {
                                        isLoadmore = false;
                                        adapter.list.addAll(list1);
                                        for (int i = 0; i < list1.size(); i++) {
                                            adapter.flags.add(false);
                                        }
                                        adapter.notifyDataSetChanged();
                                        listView.onLoadComplete();
                                    }
                                    swip.setRefreshing(false);


                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.list.clear();
                                    adapter.notifyDataSetChanged();
                                    swip.setRefreshing(false);
                                    Toast.makeText(TG_Mine.this, "暂无稿件", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(adapter!=null&&adapter.list!=null){
                                    adapter.list.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                swip.setRefreshing(false);
                                Toast.makeText(TG_Mine.this, "暂无稿件", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } catch (final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swip.setRefreshing(false);
                            Toast.makeText(TG_Mine.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        search = (EditText) findViewById(R.id.search);
        d = ContextCompat.getDrawable(this, R.drawable.search_gray);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 20), DimenUtils.dip2px(this, 20));
        search.setCompoundDrawables(d, null, null, null);
        delete = (TextView) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        choosed = new ArrayList<>();
        back = (ImageView) findViewById(R.id.title_back);
        back.setImageResource(R.drawable.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        listView = (LoadMoreListView) findViewById(R.id.fund_people_list);
        listView.setLoadMoreListen(this);
        t = (TextView) (listView.footer.findViewById(R.id.load_more_text));
        p = (ProgressBar) (listView.footer.findViewById(R.id.load_more_bar));

        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);

        TextView fabu = (TextView) findViewById(R.id.fabu);
        fabu.setOnClickListener(this);
        if (PreferenceUtil.getUserIncetance(this).getString("role", "").equals("3")) {
            delete.setVisibility(View.VISIBLE);
            findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
            findViewById(R.id.search_layout).setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams  vm= (ViewGroup.MarginLayoutParams) swip.getLayoutParams();
            vm.topMargin=DimenUtils.dip2px(this,40);
            swip.setLayoutParams(vm);
        }
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                String content = search.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(content);
                    return true;
                }
                return false;
            }
        });
    }

    /*
    搜索
     */
    private void performSearch(String content) {
        if (content.equals("")) {
            ToastUtil.showToastShort("请输入关键字");
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("msg", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.Zixun_Search)
                .params("key", m.K())
                .params("msg", m.M())
                .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {
                    @Override
                    public ArrayList<HashMap<String, String>> parseNetworkResponse(Response response) throws Exception {
                        return AnalyticalJSON.getList(response.body().string(), "draft");
                    }

                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                        if(adapter!=null){
                            adapter.list = list;
                            adapter.notifyDataSetChanged();
                            if (list.size() == 0) {
                                ToastUtil.showToastShort("未搜索到匹配图文,下拉刷新");
                            }
                        }else{
                            ToastUtil.showToastShort("未搜索到匹配图文,请下拉刷新");
                        }


                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.fabu:
                Intent intent = new Intent(this, TouGao.class);

                startActivityForResult(intent, 0);
                break;
            case R.id.delete:
                if(choosed==null||choosed.size()==0){
                    ToastUtil.showToastShort("请选择需要删除的图文");
                    return;
                }
                ProgressUtil.show(this, "", "正在删除,请稍等");
                StringBuilder ids = new StringBuilder();
                for (int i = 0; i < choosed.size(); i++) {
                    if (i != choosed.size() - 1) {
                        ids.append(choosed.get(i) + ",");
                    } else {
                        ids.append(choosed.get(i));
                    }
                    LogUtil.e("所有的：：" + choosed + "选中项：：" + choosed.get(i));
                }

                JSONObject js = new JSONObject();
                try {
                    js.put("m_id", Constants.M_id);
                    js.put("id", ids.toString());
                    js.put("type", "2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.e("js::::"+js);
                ApisSeUtil.M m = ApisSeUtil.i(js);
                OkHttpUtils.post(Constants.Archmage_delete_zixun_IP)
                        .params("key", m.K())
                        .params("msg", m.M())
                        .execute(new AbsCallback<Object>() {
                            @Override
                            public Object parseNetworkResponse(Response response) throws Exception {
                                return null;
                            }

                            @Override
                            public void onSuccess(Object o, Call call, Response response) {
                                if (response != null) {
                                    try {
                                        String data = response.body().string();
                                        HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                        if (map != null && "000".equals(map.get("code"))) {
                                            Toast.makeText(TG_Mine.this, "删除成功", Toast.LENGTH_SHORT).show();
                                            onRefresh();
                                            Intent intent = new Intent("main");
                                            sendBroadcast(intent);
                                        } else {
                                            Toast.makeText(TG_Mine.this, "删除失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException e) {

                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onAfter(@Nullable Object o, @Nullable Exception e) {
                                super.onAfter(o, e);
                                ProgressUtil.dismiss();
                            }


                        });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        isRefesh = true;
        isLoadmore = false;
        page = "1";
        endPage = "";
        search.setText("");
        getData();
    }

    public class mAdapter extends BaseAdapter {

        public List<HashMap<String, String>> list;
        private Context context;
        private ArrayList<Boolean> flags;

        public mAdapter(Context context, List<HashMap<String, String>> list) {
            super();
            this.context = context;
            this.list = list;
            flags = new ArrayList<>();
        }

        public void addList(List<HashMap<String, String>> list) {
            this.list = list;
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
        public View getView(final int position, View view, ViewGroup parent) {
            viewHolder holder = null;
            if (view == null) {
                holder = new viewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.tougao_list_item, parent, false);
                holder.head = (ImageView) view.findViewById(R.id.head);
                holder.name = (TextView) view.findViewById(R.id.abs);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.time = (TextView) view.findViewById(R.id.time);
                holder.check = (ImageView) view.findViewById(R.id.check);
                holder.root = (RelativeLayout) view.findViewById(R.id.content);
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }
            if (PreferenceUtil.getUserIncetance(context).getString("role", "").equals("3")) {
                holder.check.setVisibility(View.VISIBLE);
                holder.check.setSelected(flags.get(position));
                holder.root.setSelected(flags.get(position));
                Glide.with(context).load(list.get(position).get("image1")).override(DimenUtils.dip2px(mApplication.getInstance(), 100)
                        , DimenUtils.dip2px(mApplication.getInstance(), 75)).into(holder.head);
                holder.name.setText(list.get(position).get("abstract"));
                holder.name.setTag(list.get(position).get("id"));
                holder.title.setText(list.get(position).get("title"));
                holder.time.setText(list.get(position).get("time"));


                holder.check.setTag(position);
                holder.root.setTag(holder.check);
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView check = (ImageView) v.getTag();
                        int pos = (int) check.getTag();
                        flags.set(pos, !v.isSelected());
                        v.setSelected(flags.get(pos));
                        check.setSelected(flags.get(pos));
                        if (flags.get(pos)) {
                            if (!choosed.contains(list.get(pos).get("id"))) {
                                choosed.add(list.get(pos).get("id"));
                            }
                        } else {
                            if (choosed.contains(list.get(pos).get("id"))) {
                                choosed.remove(list.get(pos).get("id"));
                            }
                        }
                        if (choosed.size() != 0) {
                            title.setText("选中(" + choosed.size() + ")");
                        } else {
                            title.setText("图文管理");
                        }
                        LogUtil.e(choosed + "");
                    }
                });
            } else {
                holder.check.setVisibility(View.GONE);
                holder.root.setTag(list.get(position).get("id"));
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, Home_Zixun_detail.class);
                        intent.putExtra("id", view.getTag().toString());
                        startActivity(intent);
                    }
                });
                Glide.with(context).load(list.get(position).get("image1")).override(DimenUtils.dip2px(mApplication.getInstance(), 100)
                        , DimenUtils.dip2px(mApplication.getInstance(), 75))
                        .into(holder.head);
                holder.name.setText(list.get(position).get("pet_name"));
                holder.title.setText(list.get(position).get("title"));
                holder.time.setText(list.get(position).get("time"));
            }
            return view;
        }

        class viewHolder {
            RelativeLayout root;
            ImageView head, check;
            TextView name, title, time;
        }
    }
}
