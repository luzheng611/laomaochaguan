package com.laomachaguan.YaoYue;

import android.app.Activity;
import android.graphics.Color;
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
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.Common.HeadToInfo;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mItemDeraction;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/11.
 */
public class YaoYue_People_list extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {
    private ImageView back;
    private TextView title;
    private RecyclerView listView;
    private int  page = 1;
    private int  endPage = -1;
    private TextView t;//加载数据的底部提示
    private ProgressBar p;//加载数据的底部进度
    private static final String TAG = "Fund_surpport_list";
    private mAdapter adapter;
    private SwipeRefreshLayout swip;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.fund_people_list);
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
                    JSONObject js=new JSONObject();
                    try {
                        js.put("id", getIntent().getStringExtra("id"));
                        js.put("m_id", Constants.M_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    LogUtil.e("js参数：："+js);
                    String data = OkHttpUtils.post(Constants.yaoyue_people_list)
                            .tag(this)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final List<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                        Log.w(TAG, "run: list------>"+list );
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.title_back);
        back.setImageResource(R.drawable.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title_title);
        title.setText(mApplication.ST("邀约参与"));

        swip= (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);

        listView = (RecyclerView) findViewById(R.id.fund_people_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.addItemDecoration(new mItemDeraction(1));
        adapter=new mAdapter(this,new ArrayList<HashMap<String,String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        adapter.openLoadMore(10,true);
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
        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.loadnothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText("暂无人参与邀约\n下拉刷新");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        listView.setAdapter(adapter);
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
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(10,true);
        getData();
    }

    public static class mAdapter extends BaseQuickAdapter<HashMap<String,String>>{

        public List<HashMap<String ,String >>list;
        private Activity context;

        public mAdapter(Activity activity,ArrayList<HashMap<String, String>> data) {
            super(R.layout.fund_list_item, data);
            WeakReference<Activity> w=new WeakReference<Activity>(activity);
            context=w.get();
        }


        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, String> map) {
            Glide.with(context).load(map.get("user_image")).override(DimenUtils.dip2px(mApplication.getInstance(), 50)
                    , DimenUtils.dip2px(mApplication.getInstance(), 50)).into((ImageView) holder.getView(R.id.fund_list_item_head));

            if("".equals(map.get("pet_name"))){
                SpannableString ss=new SpannableString("佚名"+"\n\n"+mApplication.ST(TimeUtils.getTrueTimeStr(map.get("time"))));
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.wordhuise)),2,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(context,12)),2,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.setText(R.id.fund_list_item_name,ss);
            }else{
                SpannableString ss=new SpannableString(mApplication.ST(map.get("pet_name")+"\n\n"+TimeUtils.getTrueTimeStr(map.get("time"))));
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.wordhuise)),map.get("pet_name").length(),ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new AbsoluteSizeSpan(DimenUtils.dip2px(context,12)),map.get("pet_name").length(),ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.setText(R.id.fund_list_item_name,ss);
            }
            holder.setText(R.id.fund_list_item_money,map.get("money")+"元");
//            holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(list.get(position).get("end_time"))));
            holder.convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HeadToInfo.goToUserDetail(context,map.get("user_id"));
                }
            });

        }






        }




}
