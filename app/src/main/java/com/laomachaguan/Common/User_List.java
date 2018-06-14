package com.laomachaguan.Common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mItemDecoration;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;

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
public class User_List extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ImageView back;
    private RecyclerView recyclerView;
    private static final int PAGESIZE = 10;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;

    private mAdapter adapter;
    private static final String TAG = "User_List";
    private int currentPos = -1;
    private SwipeRefreshLayout swip;
    private EditText editText;
    private TextView search;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.user_list);
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
                        js.put("page", page);
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.User_List_IP)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                        if (list != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        adapter.setNewData(list);
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list.size() < PAGESIZE) {
                                            ToastUtil.showToastShort("用户查询完毕", Gravity.CENTER);
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
                } catch (final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(User_List.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.input);
        search = (TextView) findViewById(R.id.search);
        search.setOnClickListener(this);
        search.setOnClickListener(this);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        back = (ImageView) findViewById(R.id.title_back);
        back.setImageResource(R.drawable.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.fund_people_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mItemDecoration(this));
        adapter = new mAdapter(this, new ArrayList<HashMap<String, String>>());
        adapter.openLoadMore(PAGESIZE, true);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
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
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                String id = adapter.getData().get(i).get("id");
                Intent intent = new Intent(getApplicationContext(), User_Detail.class);
                intent.putExtra("id", id);
                currentPos = i;
                Log.w(TAG, "onItemClick: 位置： " + i);
                startActivityForResult(intent, 0);
            }

        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0x00) {
            boolean b = false;

            if (data != null) {
                b = data.getBooleanExtra("isDeleted", false);
            }
            if (b) {
                adapter.notifyItemRemoved(currentPos);
                adapter.getData().remove(currentPos);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.search:
                searchUser();
                break;
        }
    }

    private void searchUser() {
        if(editText.getText().toString().trim().equals("")){
            ToastUtil.showToastShort("请输入关键字");
            return;
        }
        JSONObject js=new JSONObject();
        try {
            js.put("type","2");
            js.put("cxmsg",editText.getText().toString().trim());
            js.put("m_id",Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.search_User_Ip)
                .params("key",m.K())
                .params("msg",m.M())
                .execute(new AbsCallback<ArrayList<HashMap<String,String>>>() {
                    @Override
                    public ArrayList<HashMap<String, String>> parseNetworkResponse(Response response) throws Exception {
                        return AnalyticalJSON.getList_zj(response.body().string());
                    }

                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                        adapter.setNewData(list);
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        swip.setRefreshing(true);
                    }

                    @Override
                    public void onAfter(@Nullable ArrayList<HashMap<String, String>> list, @Nullable Exception e) {
                        super.onAfter(list, e);
                        swip.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(PAGESIZE, true);
        editText.setText("");
        getData();
    }

    public static class mAdapter extends BaseQuickAdapter<HashMap<String, String>> {

        public List<HashMap<String, String>> list;
        private Context context;

        public mAdapter(Context context, ArrayList<HashMap<String, String>> data) {
            super(R.layout.user_list_item, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            Glide.with(context).load(map.get("user_image")).override(DimenUtils.dip2px(mApplication.getInstance(), 60)
                    , DimenUtils.dip2px(mApplication.getInstance(), 60)).into((ImageView) holder.getView(R.id.user_list_item_head));
            holder.setText(R.id.user_list_item_name, map.get("pet_name").equals("")?"暂无昵称":map.get("pet_name"));
            holder.getView(R.id.user_list_item_name).setTag(map.get("id"));
            holder.setText(R.id.user_list_item_sign, map.get("signature").equals("") ? "暂无个性签名" : map.get("signature"));
        }


    }
}
