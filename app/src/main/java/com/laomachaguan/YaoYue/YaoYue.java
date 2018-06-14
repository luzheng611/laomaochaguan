package com.laomachaguan.YaoYue;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.Common.HeadToInfo;
import com.laomachaguan.Common.MainActivity;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.MD5Utls;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PointMoveHelper;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.ShareManager;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mItemDeraction;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.laomachaguan.R.id.money;

/**
 * 作者：因陀罗网 on 2017/6/5 15:40
 * 公司：成都因陀罗网络科技有限公司
 */

public class YaoYue extends Fragment implements View.OnClickListener, OnRefreshListener {
    private static final int PAGESIZE = 10;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private ImageView iv_tougao;
    private TGAdapter adapter;
    private boolean isLoad = false;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zixun_tougao, container, false);
        getActivity().registerReceiver(receiver, new IntentFilter("yaoyue"));
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        iv_tougao = (ImageView) view.findViewById(R.id.float_tougao);
        iv_tougao.setPadding(0, 0, 0, 0);
        Glide.with(getActivity()).load(R.drawable.yaoyue_fabu).override(DimenUtils.dip2px(getActivity(), 50)
                , DimenUtils.dip2px(getActivity(), 50)).into(iv_tougao);
        iv_tougao.setOnClickListener(this);
        adapter = new TGAdapter(getActivity(), new ArrayList<HashMap<String, String>>());

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new mItemDeraction(1, R.color.black));
        recyclerView.setAdapter(adapter);
        TextView textView = new TextView(getActivity());
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.loadnothing);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        textView.setText("暂无邀约\n下拉刷新");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);


        //自由滑动
        new PointMoveHelper(getActivity(), iv_tougao)
                .setViewUnMoveable(((MainActivity) getActivity()).pager)
                .setHorizontalMargin(10);
        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this != null && isVisibleToUser && !isLoad) {
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    onRefresh();
                }
            });
            isLoad = true;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * 加载数据
     */
    private void getData() {
        if (!Network.HttpTest(getActivity())) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.e("run: 页码：" + page + "最后一页：" + endPage);
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.getYueList)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                        LogUtil.e("run: list------>" + list);
                        if (list != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        adapter.setNewData(list);
                                        if (adapter.currentPositon != -1) {
                                            recyclerView.scrollToPosition(adapter.currentPositon);
                                            adapter.currentPositon = -1;
                                        }
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list.size() < PAGESIZE) {
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
                        } else {
                            if (getActivity() != null)
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        swip.setRefreshing(false);
                                    }
                                });
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.float_tougao:
                if (!LoginUtil.checkLogin(getActivity())) {
                    return;
                }
                if (PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("3")) {
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bottom_good_manager, null);
                    b.setView(view1);
                    final Dialog d = b.create();
                    TextView t1 = (TextView) view1.findViewById(R.id.zhiding);
                    TextView t2 = (TextView) view1.findViewById(R.id.shangjia);
                    view1.findViewById(R.id.delete).setVisibility(View.GONE);
                    t1.setText("邀约发布");
                    t2.setText("邀约设置");
                    t1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent itent = new Intent(getActivity(), YaoYue_Fabu.class);
                            startActivity(itent);
                            d.dismiss();
                        }
                    });
                    t2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent itent = new Intent(getActivity(), YaoYue_Setting.class);
                            startActivity(itent);
                            d.dismiss();
                        }
                    });
                    view1.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                                                                           @Override
                                                                           public void onClick(View view) {
                                                                               d.dismiss();
                                                                           }
                                                                       }
                    );
                    d.setCanceledOnTouchOutside(true);
                    Window window = d.getWindow();

                    window.getDecorView().setPadding(0, 0, 0, 0);
                    window.setBackgroundDrawableResource(android.R.color.transparent);
                    window.setGravity(Gravity.BOTTOM);
                    WindowManager.LayoutParams wl = window.getAttributes();
                    wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    wl.windowAnimations = R.style.dialogWindowAnim;
                    wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(wl);
                    d.show();

                } else {
                    Intent itent = new Intent(getActivity(), YaoYue_Fabu.class);
                    startActivity(itent);
                }

                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(PAGESIZE, true);
        getData();
    }

    public static class TGAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        private Activity context;
        private Drawable d;
        public int currentPositon = -1;

        public TGAdapter(Activity context, List<HashMap<String, String>> data) {
            super(R.layout.item_yaoyue, data);
            WeakReference<Activity> w = new WeakReference<Activity>(context);
            this.context = w.get();
            d = ContextCompat.getDrawable(context, R.drawable.woshou);
            d.setBounds(0, 0, DimenUtils.dip2px(context, 25), DimenUtils.dip2px(context, 25));
        }

        @Override
        protected void convert(BaseViewHolder hoder, final HashMap<String, String> bean) {
            hoder.setText(R.id.title, bean.get("title"))
                    .setText(money, bean.get("money") + "元")
                    .setText(R.id.num, bean.get("num"))
                    .setText(R.id.time, bean.get("end_time"))
                    .setText(R.id.address, bean.get("address"))
                    .setText(R.id.people, bean.get("people") + "人已应邀")
                    .setText(R.id.contents, bean.get("contents"));
            Glide.with(context).load(bean.get("user_image")).override(DimenUtils.dip2px(context, 40), DimenUtils.dip2px(context, 40))
                    .fitCenter().into((ImageView) hoder.getView(R.id.head));
            hoder.setOnClickListener(R.id.head, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HeadToInfo.goToUserDetail(context, bean.get("user_id"));
                }
            });
            ((TextView) hoder.getView(R.id.people)).setCompoundDrawables(d, null, null, null);
            hoder.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, YaoYue_People_list.class);
                    intent.putExtra("id", bean.get("id"));
                    context.startActivity(intent);
                }
            });
            hoder.setOnClickListener(R.id.share, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UMWeb umWeb = new UMWeb(Constants.host_Ip + "/cg.php/Index/pactd/id/" + bean.get("id") + "/m_id/" + MD5Utls.stringToMD5(Constants.M_id));
                    umWeb.setTitle(PreferenceUtil.getUserIncetance(context).getString("pet_name", "") + "邀请:" + bean.get("title") + "  押金:" + bean.get("money") + "元 还差" + (Integer.valueOf(bean.get("num")) - Integer.valueOf(bean.get("people"))) + "人");
                    umWeb.setDescription(bean.get("contents"));
                    umWeb.setThumb(new UMImage(context, R.drawable.indra_jpg));
                    new ShareManager().shareWeb(umWeb, context);
                    LogUtil.e("分享参数：" + umWeb.toString());
                }
            });
            if (PreferenceUtil.getUserIncetance(context).getString("role", "").equals("3")) {
                hoder.getView(R.id.yingyao).setVisibility(View.GONE);
                hoder.getView(R.id.cancle).setVisibility(View.VISIBLE);
                hoder.setOnClickListener(R.id.cancle, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder b = new AlertDialog.Builder(context);
                        b.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                deleteYaoYue();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setMessage("确定要删除该邀约吗？")
                                .create().show();

                    }

                    private void deleteYaoYue() {
                        if (!Network.HttpTest(context)) {
                            return;
                        }
                        JSONObject js = new JSONObject();
                        try {
                            js.put("m_id", Constants.M_id);
                            js.put("id", bean.get("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m = ApisSeUtil.i(js);
                        OkHttpUtils.post(Constants.yaoyue_Delete)
                                .tag(context)
                                .params("key", m.K())
                                .params("msg", m.M()).execute(new StringCallback() {


                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(context, "", "正在删除");
                            }

                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                if (map != null) {
                                    if ("000".equals(map.get("code"))) {
                                        ToastUtil.showToastShort("删除成功");
                                        notifyItemRemoved(getData().indexOf(bean));
                                        getData().remove(bean);
                                    }
                                }
                            }

                            @Override
                            public void onAfter(@Nullable String s, @Nullable Exception e) {
                                super.onAfter(s, e);
                                ProgressUtil.dismiss();
                            }


                        });
                    }
                });
            } else {
                if (bean.get("user_id").equals(PreferenceUtil.getUserIncetance(context).getString("user_id", ""))) {
                    hoder.setVisible(R.id.yingyao, false);
                } else {
                    hoder.setVisible(R.id.yingyao, true);
                }
                hoder.getView(R.id.cancle).setVisibility(View.GONE);
                LogUtil.e("当前时间：：" + System.currentTimeMillis() + "  过期时间:" + TimeUtils.dataOne(bean.get("end_time")));
                if (bean.get("num").equals(bean.get("people")) || System.currentTimeMillis() >= TimeUtils.dataOne(bean.get("end_time"))) {
                    hoder.setText(R.id.yingyao, "已结束");
                    hoder.getView(R.id.yingyao).setEnabled(false);
                    hoder.getView(R.id.share).setVisibility(View.GONE);
                } else {
                    hoder.setText(R.id.yingyao, "应邀");
                    hoder.getView(R.id.yingyao).setEnabled(true);
                    hoder.getView(R.id.share).setVisibility(View.VISIBLE);
                    hoder.getView(R.id.yingyao).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!LoginUtil.checkLogin(context)) {
                                return;
                            }
                            if (!Network.HttpTest(context)) {
                                return;
                            }
                            AlertDialog.Builder b = new AlertDialog.Builder(context);
                            b.setMessage("确定参加应邀吗？需要预付一定的诚意金哦")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            YinYao();
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();

                        }

                        private void YinYao() {
                            JSONObject js = new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("id", bean.get("id"));
                                js.put("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id", ""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m = ApisSeUtil.i(js);
                            OkHttpUtils.post(Constants.yingYao)
                                    .tag(context)
                                    .params("key", m.K())
                                    .params("msg", m.M())
                                    .execute(new StringCallback() {

                                        @Override
                                        public void onBefore(BaseRequest request) {
                                            super.onBefore(request);
                                            ProgressUtil.show(context, "", "请稍等");
                                        }

                                        @Override
                                        public void onSuccess(String s, Call call, Response response) {
                                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                            if (map != null) {
                                                if ("000".equals(map.get("code"))) {
                                                    mApplication.openPayLayout(context, bean.get("money"), bean.get("id")
                                                            , bean.get("title") + "[应邀诚意金]", "1", "", "12", "","");
                                                    currentPositon = getData().indexOf(bean);
                                                } else if ("003".equals(map.get("code"))) {
                                                    ToastUtil.showToastShort("该邀约已不存在");
                                                    Intent intent=new Intent("yaoyue");
                                                    context.sendBroadcast(intent);
                                                } else {
                                                    ToastUtil.showToastShort("您已参与过该邀约");
                                                }
                                            }
                                        }

                                        @Override
                                        public void onAfter(@Nullable String s, @Nullable Exception e) {
                                            super.onAfter(s, e);
                                            ProgressUtil.dismiss();
                                        }


                                    });
                        }
                    });
                }

            }
        }

    }
}
