package com.laomachaguan.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

public class Good_Manager extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final int RequestUpdateOrAdd = 121;
    private ImageView back;
    private TextView tv_fabu;
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
        setContentView(R.layout.list_shang_manager);
        initView();
    }


    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        tv_fabu = (TextView) findViewById(R.id.fabu);
        tv_fabu.setOnClickListener(this);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        recycle = (RecyclerView) findViewById(R.id.recycle);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(new mItemDeraction(1));

        adapter = new GoodAdapter(this, new ArrayList<HashMap<String, String>>());
        adapter.openLoadMore(10, true);
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
        recycle.setAdapter(adapter);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
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
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(10, true);
        getData();
    }

    private void getData() {
        if (!Network.HttpTest(this)) {
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        HttpParams http = new HttpParams("key", m.K());
        http.put("msg", m.M());
        OkHttpUtils.post(Constants.getGoodTotal).params(http)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null && !s.equals("") && !s.equals("null")) {
                            final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(s);
                            LogUtil.e("run: list------>" + list);
                            if (list != null) {
                                if (isRefresh) {
                                    adapter.setNewData(list);
                                    isRefresh = false;
                                    swip.setRefreshing(false);
                                } else if (isLoadMore) {
                                    isLoadMore = false;
                                    if (list.size() < 10) {
                                        ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
//                                      endPage = page;
                                        adapter.notifyDataChangedAfterLoadMore(list, false);
                                    } else {
                                        adapter.notifyDataChangedAfterLoadMore(list, true);
                                    }
                                }


                            }
                        }
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
            Glide.with(context).load(map.get("image1")).asBitmap().override(DimenUtils.dip2px(mApplication.getInstance(), 120),
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
            if(map.get("open").equals("2")){
                holder.getView(R.id.pingtuan).setVisibility(View.VISIBLE);
            }else{
                holder.getView(R.id.pingtuan).setVisibility(View.GONE);
            }
            ((TextView) holder.getView(R.id.order_item_false_money)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.setText(R.id.order_item_type, map.get("name"))
                    .setText(R.id.order_item_name, map.get("title"))
                    .setText(R.id.order_item_abstract, map.get("abstract"))
                    .setText(R.id.order_item_sales, "总销量" + map.get("sales"))
                    .setText(R.id.order_item_likes, "赞" + map.get("likes"))
                    .setText(R.id.order_item_true_money, "¥ " + map.get("money"));


            if (map.get("cost").equals("0")||map.get("cost").equals("")||map.get("cost").equals("null")) {
                holder.getView(R.id.order_item_false_money).setVisibility(View.INVISIBLE);
            } else {
                holder.getView(R.id.order_item_false_money).setVisibility(View.VISIBLE);
                holder.setText(R.id.order_item_false_money, "¥ " + map.get("cost"));
            }
            if (map.get("start").equals("1")) {
                holder.setText(R.id.shangjia, "已下架");
                holder.getView(R.id.shangjia).setSelected(true);
            } else {
                holder.setText(R.id.shangjia, "已上架");
                holder.getView(R.id.shangjia).setSelected(false);
            }
            holder.setOnClickListener(R.id.content, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(context, Good_Handle.class);
                    intent.putExtra("type", "update");
                    intent.putExtra("id", map.get("id"));
                    context.startActivityForResult(intent,Good_Manager.RequestUpdateOrAdd);
                }
            });

            holder.setOnClickListener(R.id.handle, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_good_manager, null);

                    TextView zhiding = (TextView) view1.findViewById(R.id.zhiding);
                    TextView shangjia = (TextView) view1.findViewById(R.id.shangjia);
                    TextView delete = (TextView) view1.findViewById(R.id.delete);
//                    TextView update = (TextView) view1.findViewById(R.id.update);
                    TextView cancle = (TextView) view1.findViewById(R.id.cancle);
                    if (map.get("status").equals("2")) {
                        zhiding.setText("取消置顶");
                    } else {
                        zhiding.setText("置顶");
                    }
                    if (map.get("start").equals("2")) {
                        shangjia.setText("下架");
                    } else {
                        shangjia.setText("上架");
                    }
                    builder.setView(view1);
                    final Dialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    Window window = dialog.getWindow();

                    window.getDecorView().setPadding(0, 0, 0, 0);
                    window.setBackgroundDrawableResource(android.R.color.transparent);
                    window.setGravity(Gravity.BOTTOM);
                    WindowManager.LayoutParams wl = window.getAttributes();
                    wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    wl.windowAnimations = R.style.dialogWindowAnim;
                    wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(wl);
                    dialog.show();

                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            view.setEnabled(false);
                            switch (view.getId()) {
                                case R.id.zhiding:
                                    ZhiDing(view,dialog);
                                    break;
                                case R.id.shangjia:
                                    ShangJia(view,dialog);
                                    break;
                                case R.id.delete:
                                    Delete(view,dialog);
                                    break;
                                case R.id.cancle:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
//                    update.setOnClickListener(onClickListener);
                    zhiding.setOnClickListener(onClickListener);
                    shangjia.setOnClickListener(onClickListener);
                    delete.setOnClickListener(onClickListener);
                    cancle.setOnClickListener(onClickListener);
                }

                private void ZhiDing(final View v, final Dialog dialog) {
                    if (!Network.HttpTest(context)) {
                        return;
                    }
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("id", map.get("id"));
                        js.put("status", map.get("status").equals("2") ? "1" : "2");
                        LogUtil.e("当前状态:::" + (map.get("status").equals("1") ? "未置顶" : "已置顶"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    HttpParams http = new HttpParams("key", m.K());
                    http.put("msg", m.M());
                    OkHttpUtils.post(Constants.GoodsZhiding)
                            .params(http).execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
                            if (m != null) {
                                if ("000".equals(m.get("code"))) {
                                    dialog.dismiss();
                                    ((SwipeMenuLayout) holder.convertView).quickClose();
                                    ((Good_Manager) context).onRefresh();
                                    Intent iten = new Intent("car");
                                    context.sendBroadcast(iten);
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

                private void ShangJia(final View v,final Dialog dialog) {
                    if (!Network.HttpTest(context)) {
                        return;
                    }
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("id", map.get("id"));
                        js.put("start", map.get("start").equals("2") ? "1" : "2");
                        LogUtil.e("当前状态:::" + (map.get("start").equals("1") ? "已下架" : "已上架"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    HttpParams http = new HttpParams("key", m.K());
                    http.put("msg", m.M());
                    OkHttpUtils.post(Constants.GoodsShangjia)
                            .params(http)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    if (s != null && !s.equals("") && !s.equals("null")) {
                                        HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
                                        if (m != null) {
                                            if ("000".equals(m.get("code"))) {
                                                TextView s1 = holder.getView(R.id.shangjia);
                                                dialog.dismiss();
                                                ToastUtil.showToastShort(s1.isSelected() ? "该商品已上架" : "该商品已下架");
                                                s1.setSelected(!s1.isSelected());
                                                s1.setText(s1.isSelected() ? "已下架" : "已上架");
                                                map.put("start", s1.isSelected() ? "1" : "2");
                                                ((SwipeMenuLayout) holder.convertView).smoothClose();
                                                LogUtil.e("变更后状态:::" + (map.get("start").equals("1") ? "已下架" : "已上架"));
                                                Intent intent = new Intent("car");
                                                context.sendBroadcast(intent);
                                            } else {
                                                ToastUtil.showToastShort("操作提交失败，请稍后重试");
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

                private void Delete(final View v,final Dialog dialog) {
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
                            OkHttpUtils.post(Constants.GoodsDelete)
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
                                                        Intent intent = new Intent("car");
                                                        context.sendBroadcast(intent);
                                                        dialog.dismiss();
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
                    }).setMessage("确认要删除该商品吗？").create().show();
                }
            });


        }
    }
}
