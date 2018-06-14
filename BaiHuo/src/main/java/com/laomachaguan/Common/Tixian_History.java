package com.laomachaguan.Common;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.View.mItemDeraction;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/7/2 17:06
 * 公司：成都因陀罗网络科技有限公司
 */

public class Tixian_History extends AppCompatActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{
    private ImageView back;
    private static final int PAGESIZE = 10;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private TX_Adapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tixian_history);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        swip = (SwipeRefreshLayout)findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        back= (ImageView) findViewById(R.id.title_back);
        back.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);

        adapter = new TX_Adapter(this, new ArrayList<HashMap<String,String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.openLoadMore(PAGESIZE, true);
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

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new mItemDeraction(1, R.color.black));
        recyclerView.setAdapter(adapter);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.loadnothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText("暂无提现记录");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
    }


    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(PAGESIZE,true);
        getData();
    }

    private void getData() {
        if(Network.HttpTest(this)){
            JSONObject js=new JSONObject();
            try {
                js.put("m_id",Constants.M_id);
                js.put("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id",""));
                js.put("page",page);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m=ApisSeUtil.i(js);
            OkHttpUtils.post(Constants.Tixian_List).tag(this)
                    .params("key",m.K())
                    .params("msg",m.M())
                    .execute(new AbsCallback<ArrayList<HashMap<String,String>>>() {
                        @Override
                        public ArrayList<HashMap<String, String>> parseNetworkResponse(Response response) throws Exception {
                            return AnalyticalJSON.getList_zj(response.body().string());
                        }

                        @Override
                        public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                            if (isRefresh) {
                                adapter.setNewData(list);
                                isRefresh = false;
                                swip.setRefreshing(false);
                            } else if (isLoadMore) {
                                isLoadMore = false;
                                if (list.size() < PAGESIZE) {
                                    ToastUtil.showToastShort("已经没有更多记录啦", Gravity.CENTER);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.title_back){
            finish();
        }
    }

    private static  class TX_Adapter  extends BaseQuickAdapter<HashMap<String,String>>{
        private Activity activity;
        public TX_Adapter(Activity activity,List<HashMap<String, String>> data) {
            super(R.layout.tixian_item, data);
            WeakReference<Activity> w=new WeakReference<Activity>(activity);
            this.activity=w.get();
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {

            SpannableString ss=new SpannableString("提现:"+map.get("money")+"元, 扣除:"+map.get("credit")+"积分");
            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity,R.color.limegreen)),3,map.get("money").length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity,R.color.tomato)),map.get("money").length()+9,ss.length()-2   , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.setText(R.id.title,ss)
                    .setText(R.id.Id,"提现账号:"+map.get("phone"))
                    .setText(R.id.name,"账号姓名:"+map.get("name"))
                    .setText(R.id.time,"申请时间:"+map.get("time"))
                    .setText(R.id.time2,"处理时间:"+(map.get("status").equals("1")?"等待审核处理":map.get("end_time")))
            ;
            if(map.get("status").equals("1")){
                holder.setText(R.id.status,"等待处理");
                holder.setVisible(R.id.mark,false);
            }else if(map.get("status").equals("2")){
                holder.setText(R.id.status,"已提现");
                holder.setVisible(R.id.mark,false);
            } else if(map.get("status").equals("3")){
                holder.setText(R.id.status,"提现失败");
                holder.setVisible(R.id.mark,true);
                holder.setText(R.id.mark,"处理结果:\n"+map.get("mark"));
            }

        }
    }
}
