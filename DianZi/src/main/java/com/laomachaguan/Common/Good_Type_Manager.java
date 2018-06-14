package com.laomachaguan.Common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mItemDeraction;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.model.HttpParams;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/6/8 00:43
 * 公司：成都因陀罗网络科技有限公司
 */

public class Good_Type_Manager extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final int RequestUpdateOrAdd = 121;
    private ImageView back;
    private TextView tv_fabu, tv_tags;
    private SwipeRefreshLayout swip;
    private RecyclerView recycle;
    private GoodAdapter adapter;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.list_good_type_manager);
        initView();
    }


    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        tv_fabu = (TextView) findViewById(R.id.fabu);
        tv_fabu.setOnClickListener(this);
        tv_tags = (TextView) findViewById(R.id.tags);
        tv_tags.setOnClickListener(this);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        recycle = (RecyclerView) findViewById(R.id.recycle);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(new mItemDeraction(1));

        adapter = new GoodAdapter(this, new ArrayList<HashMap<String, String>>());
//        adapter.openLoadMore(10, true);
//        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                if (endPage != page) {
//                    isLoadMore = true;
//                    page++;
//                    getData();
//                }
//            }
//        });
        recycle.setAdapter(adapter);
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
        textView.setText("暂未增加属性");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestUpdateOrAdd && resultCode == Good_Handle.ResultCode) {
            onRefresh();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.fabu://ADD  添加   UPDATE 更新
                Intent intent = new Intent(this, Good_Handle.class);
                intent.putExtra("type", "ADD");
                startActivityForResult(intent, RequestUpdateOrAdd);
                break;
            case R.id.tags:
                Intent i = new Intent(this, Good_Type_Handle.class);
                i.putExtra("type","ADD");
                i.putExtra("id",getIntent().getStringExtra("id"));

                startActivityForResult(i, RequestUpdateOrAdd);
                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
//        adapter.openLoadMore(10, true);
        getData();
    }

    private void getData() {
        if (!Network.HttpTest(this)) {
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("goods_id", getIntent().getStringExtra("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        HttpParams http = new HttpParams("key", m.K());
        http.put("msg", m.M());
        OkHttpUtils.post(Constants.Goods_spec).params(http)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null && !s.equals("") && !s.equals("null")) {
                            final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList(s, "spec");
                            LogUtil.e("run: list------>" + list);
                            if (list != null) {
                                if (isRefresh) {
                                    adapter.setNewData(list);
                                    isRefresh = false;
                                    swip.setRefreshing(false);
                                }
//                                else if (isLoadMore) {
//                                    isLoadMore = false;
//                                    if (list.size() < 10) {
//                                        ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
////                                      endPage = page;
//                                        adapter.notifyDataChangedAfterLoadMore(list, false);
//                                    } else {
//                                        adapter.notifyDataChangedAfterLoadMore(list, true);
//                                    }
//                                }


                            }
                        }
                    }

                    @Override
                    public void onAfter(@Nullable String s, @Nullable Exception e) {
                        super.onAfter(s, e);
                        swip.setRefreshing(false);
                    }
                });
    }

    public static class GoodAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        private Activity context;

        public GoodAdapter(Activity ac, ArrayList<HashMap<String, String>> data) {
            super(R.layout.item_good_manager, data);
            WeakReference<Activity> a = new WeakReference<Activity>(ac);
            context = a.get();
        }

        @Override
        protected void convert(final BaseViewHolder holder, final HashMap<String, String> map) {
            Glide.with(context).load(map.get("image")).asBitmap().override(DimenUtils.dip2px(mApplication.getInstance(), 120),
                    DimenUtils.dip2px(mApplication.getInstance(), 90)).centerCrop()
                    .error(R.mipmap.load_nothing)
                    .into(new BitmapImageViewTarget((ImageView) holder.getView(R.id.order_item_image)) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            RoundedBitmapDrawable rb = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            rb.setCornerRadius(10);
                            setDrawable(rb);
                        }
                    });
            if (Double.valueOf(map.get("price")) > 0) {
                holder.getView(R.id.pingtuan).setVisibility(View.VISIBLE);
                holder.getView(R.id.order_item_sales).setVisibility(View.VISIBLE);
            } else {
                holder.getView(R.id.pingtuan).setVisibility(View.GONE);
                holder.getView(R.id.order_item_sales).setVisibility(View.GONE);
            }

            ((TextView) holder.getView(R.id.order_item_false_money)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.setVisible(R.id.order_item_type, false);
            TextView name = holder.getView(R.id.order_item_name);
            name.setTextColor(ContextCompat.getColor(context, R.color.wordhuise));
            name.setTextSize(15);
            TextView stock = holder.getView(R.id.order_item_abstract);
            stock.setTextSize(15);
            TextView pintuan = holder.getView(R.id.order_item_sales);
            pintuan.setTextColor(ContextCompat.getColor(context, R.color.lightpink));
            pintuan.setTextSize(15);
            holder.setText(R.id.order_item_name, map.get("name") + ";" + map.get("spec_name"))
                    .setText(R.id.order_item_abstract, "库存 " + map.get("stock"))
                    .setText(R.id.order_item_sales, "拼团:¥ " + map.get("price"))
                    .setText(R.id.order_item_true_money, "¥ " + map.get("money"))
                    .setText(R.id.handle, "删除");
            holder.setVisible(R.id.order_item_likes, false);

            if (map.get("cost").equals("0") || map.get("cost").equals("") || map.get("cost").equals("null")) {
                holder.getView(R.id.order_item_false_money).setVisibility(View.INVISIBLE);
            } else {
                holder.getView(R.id.order_item_false_money).setVisibility(View.VISIBLE);
                holder.setText(R.id.order_item_false_money, "¥ " + map.get("cost"));
            }
            holder.setVisible(R.id.shangjia, false);
//            if (map.get("start").equals("1")) {
//                holder.setText(R.id.shangjia, "已下架");
//                holder.getView(R.id.shangjia).setSelected(true);
//            } else {
//                holder.setText(R.id.shangjia, "已上架");
//                holder.getView(R.id.shangjia).setSelected(false);
//            }


            holder.setOnClickListener(R.id.content, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(context, Good_Type_Handle.class);
                    intent.putExtra("type", "update");
                    intent.putExtra("id", map.get("id"));
                    intent.putExtra("map",map);
                    context.startActivityForResult(intent, Good_Type_Manager.RequestUpdateOrAdd);
                }
            });

            //属性删除
            holder.setOnClickListener(R.id.handle, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Delete(view);
                }





                private void Delete(final View v) {
                    if (!Network.HttpTest(context)) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            JSONObject js = new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("id", map.get("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m = ApisSeUtil.i(js);
                            HttpParams http = new HttpParams("key", m.K());
                            http.put("msg", m.M());
                            OkHttpUtils.post(Constants.Goods_spec_delete)
                                    .params(http)
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(String s, Call call, Response response) {
                                            if (s != null && !s.equals("") && !s.equals("null")) {
                                                HashMap<String, String> ma = AnalyticalJSON.getHashMap(s);
                                                if (ma != null) {
                                                    if ("000".equals(ma.get("code"))) {
                                                        ToastUtil.showToastShort("删除成功");
                                                        ((SwipeMenuLayout) holder.getConvertView()).smoothClose();
                                                        notifyItemRemoved(getData().indexOf(map));
                                                        getData().remove(getData().indexOf(map));
//                                                        Intent intent = new Intent("car");
//                                                        context.sendBroadcast(intent);

                                                    } else {
                                                        LogUtil.e("删除失败");
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onAfter(@Nullable String s, @Nullable Exception e) {
                                            super.onAfter(s, e);
                                            v.setEnabled(true);
                                        }
                                    });
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setMessage("确认要删除该商品属性吗？").create().show();
                }
            });


        }
    }
}
