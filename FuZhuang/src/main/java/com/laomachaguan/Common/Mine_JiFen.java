package com.laomachaguan.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.R;
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
import com.laomachaguan.Utils.Verification;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mItemDeraction;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/6/16 12:53
 * 公司：成都因陀罗网络科技有限公司
 */

public class Mine_JiFen extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int PAGESIZE = 10;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private ImageView back;
    private AvatarImageView head;
    private TextView username, yaoqingma, jifen;
    private SwipeRefreshLayout swip;
    private RecyclerView recycle;
    private JFAdapter adapter;
    private boolean isAdmin = false;
    private EditText msg;
    private TextView search;
    private TextView tixian;
    private TextView history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.mine_jifen);

        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.title_back);
        back.setOnClickListener(this);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        head = (AvatarImageView) findViewById(R.id.head);
        username = (TextView) findViewById(R.id.username);
        yaoqingma = (TextView) findViewById(R.id.yaoqingma);
        jifen = (TextView) findViewById(R.id.jifen);

        tixian = (TextView) findViewById(R.id.tixian);
        tixian.setOnClickListener(this);
        tixian.setText(mApplication.ST("提现"));
        tixian.setEnabled(false);

        history = (TextView) findViewById(R.id.jifen_history);
        history.setOnClickListener(this);

        recycle = (RecyclerView) findViewById(R.id.recycle);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(new mItemDeraction(1));
        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.loadnothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setGravity(Gravity.CENTER);
        textView.setText("未获取到用户\n下拉刷新");
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 150);
        textView.setLayoutParams(vl);
        adapter = new JFAdapter(this, new ArrayList<HashMap<String, String>>());
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.openLoadMore(PAGESIZE, true);
        adapter.setEmptyView(textView);
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
        msg = (EditText) findViewById(R.id.search_msg);
        search = (TextView) findViewById(R.id.search);
        search.setOnClickListener(this);
        if (PreferenceUtil.getUserIncetance(this).getString("role", "").equals("3")) {
            ((TextView) findViewById(R.id.title_title)).setText("积分管理");
            history.setText("提现设置");
            isAdmin = true;
            findViewById(R.id.head_layout).setVisibility(View.GONE);
            findViewById(R.id.line).setVisibility(View.GONE);
            findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams mmp = (ViewGroup.MarginLayoutParams) swip.getLayoutParams();
            mmp.topMargin = DimenUtils.dip2px(this, 100);
            swip.setLayoutParams(mmp);

        } else {
            history.setText("提现记录");
            ((TextView) findViewById(R.id.title_title)).setText("积分管理");
            isAdmin = false;
        }
        recycle.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int i) {
                if (PreferenceUtil.getUserIncetance(Mine_JiFen.this).getString("role", "").equals("3")) {//管理员
                    final String name = adapter.getData().get(i).get("pet_name");
//                    final int num = Integer.valueOf(adapter.getData().get(i).get("credit"));
                    AlertDialog.Builder b = new AlertDialog.Builder(Mine_JiFen.this);
                    View v = LayoutInflater.from(Mine_JiFen.this).inflate(R.layout.input_dialog, null);
                    TextView title = (TextView) v.findViewById(R.id.title);
                    final EditText input = (EditText) v.findViewById(R.id.input);
                    TextView cancle = (TextView) v.findViewById(R.id.cancle);
                    TextView commit = (TextView) v.findViewById(R.id.commit);
                    title.setText("您确定要修改[" + name + "]的积分吗？");
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setText(adapter.getData().get(i).get("integral"));
                    b.setView(v);
                    final Dialog d = b.create();
                    cancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.dismiss();
                        }
                    });
                    commit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Network.HttpTest(Mine_JiFen.this)) {
                                if (input.getText().toString().trim().equals("")) {
                                    ToastUtil.showToastShort("请输入积分");
                                    return;
                                }
                                JSONObject js = new JSONObject();
                                try {
                                    js.put("m_id", Constants.M_id);
                                    js.put("user_id", adapter.getData().get(i).get("id"));
                                    js.put("credit", input.getText().toString().trim());
                                    js.put("admin_id", PreferenceUtil.getUserId(Mine_JiFen.this));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                OkHttpUtils.post(Constants.AdminJIFen_update)
                                        .tag(this)
                                        .params("key", m.K())
                                        .params("msg", m.M())
                                        .execute(new StringCallback() {


                                            @Override
                                            public void onBefore(BaseRequest request) {
                                                super.onBefore(request);
                                                ProgressUtil.show(Mine_JiFen.this, "", "请稍等");
                                            }

                                            @Override
                                            public void onSuccess(String s, Call call, Response response) {
                                                HashMap<String, String> m = AnalyticalJSON.getHashMap(s);
                                                if (m != null) {
                                                    if ("000".equals(m.get("code"))) {
                                                        onRefresh();
                                                        ToastUtil.showToastShort("修改成功");
                                                        d.dismiss();
                                                    } else {
                                                        ToastUtil.showToastShort("修改失败，请稍后重试");
                                                    }
                                                } else {
                                                    ToastUtil.showToastShort("修改失败，请稍后重试");
                                                }

                                            }

                                            @Override
                                            public void onAfter(@Nullable String s, @Nullable Exception e) {
                                                super.onAfter(s, e);
                                                ProgressUtil.dismiss();
                                            }


                                        });

                            }
                        }
                    });
                    d.show();
                }
            }
        });

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    private void getTiXianRule() {
        if (Network.HttpTest(this)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("m_id", Constants.M_id);
                jsonObject.put("user_id", PreferenceUtil.getUserIncetance(this).getString("user_id", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(jsonObject);
            OkHttpUtils.post(Constants.Credit).tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new AbsCallback<HashMap<String, String>>() {
                        @Override
                        public HashMap<String, String> parseNetworkResponse(Response response) throws Exception {
                            return AnalyticalJSON.getHashMap(response.body().string());
                        }

                        @Override
                        public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                            showTixianDialog(map);
                        }

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            ProgressUtil.show(Mine_JiFen.this, "", "请稍等");
                        }

                        @Override
                        public void onAfter(@Nullable HashMap<String, String> stringStringHashMap, @Nullable Exception e) {
                            super.onAfter(stringStringHashMap, e);
                            ProgressUtil.dismiss();
                        }
                    });

        }
    }

    private void getData() {
        if (!Network.HttpTest(this)) {
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
                        js.put("user_id", PreferenceUtil.getUserIncetance(Mine_JiFen.this).getString("user_id", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String url = Constants.JIFen;
                    if (isAdmin) {
                        url = Constants.AdminJIFen;
                    } else {
                        url = Constants.JIFen;
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    final String data = OkHttpUtils.post(url)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        final ArrayList<HashMap<String, String>> list = isAdmin ? AnalyticalJSON.getList_zj(data) : AnalyticalJSON.getList_zj(new JSONObject(data).getString("user"));
                        LogUtil.e("list:::" + list);
                        if (list != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setNormalHeader(data);
                                    if (isRefresh) {
                                        adapter.setNewData(list);
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
                                    tixian.setEnabled(true);
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

    private void setNormalHeader(String data) {
        if (!isAdmin) {
            if (!PreferenceUtil.getUserIncetance(Mine_JiFen.this).getString("head_url", "").equals("")) {
                Glide.with(Mine_JiFen.this)
                        .load(PreferenceUtil.getUserIncetance(Mine_JiFen.this).getString("head_url", ""))
                        .override(DimenUtils.dip2px(Mine_JiFen.this, 100), DimenUtils.dip2px(Mine_JiFen.this, 100))
                        .fitCenter()
                        .into(head);
            }
            JSONObject j = null;
            try {
                j = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SpannableString ss = null;
            try {
                ss = new SpannableString("邀请码: " + j.get("phone"));
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.limegreen)), 4, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString ss1 = new SpannableString("我的积分: " + j.get("credit"));
                ss1.setSpan(new ForegroundColorSpan(Color.RED), 5, ss1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                username.setText(PreferenceUtil.getUserIncetance(Mine_JiFen.this).getString("pet_name", ""));
                yaoqingma.setText(ss);
                jifen.setText(ss1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jifen_history:
                if(PreferenceUtil.getUserIncetance(this).getString("role","").equals("3")){
                    showAdminSettingDialog();
                }else{
                    Intent intent = new Intent(this, Tixian_History.class);
                    startActivity(intent);
                }

                break;
            case R.id.tixian:
                getTiXianRule();
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.search:
                if (!msg.getText().toString().trim().equals("")) {
                    if (!Verification.isMobileNO(msg.getText().toString().trim())) {
                        ToastUtil.showToastShort("请输入正确的手机号码");
                        return;
                    }
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("type", "1");
                        js.put("cxmsg", msg.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    OkHttpUtils.post(Constants.AdminSearch_GetID)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .execute(new StringCallback() {


                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    ProgressUtil.show(Mine_JiFen.this, "", "正在搜索");
                                }

                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                                    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                                    if (map != null) {
                                        if ("000".equals(map.get("code"))) {
                                            list.add(map);
                                            adapter.setNewData(list);
                                        } else if ("002".equals(map.get("code"))) {
                                            adapter.setNewData(list);
                                            ToastUtil.showToastShort("未搜索到用户");
                                        }
                                    } else {
                                        adapter.setNewData(list);
                                        ToastUtil.showToastShort("未搜索到用户");
                                    }
                                }

                                @Override
                                public void onAfter(@Nullable String s, @Nullable Exception e) {
                                    super.onAfter(s, e);
                                    ProgressUtil.dismiss();
                                }


                            });
                }
                break;
        }
    }

    private void showAdminSettingDialog() {
        AlertDialog.Builder b=new AlertDialog.Builder(this);
        View view=LayoutInflater.from(this).inflate(R.layout.dialog_tixian_admin,null);
        b.setView(view);
        final AlertDialog dialog= b.create();
        final EditText percent= (EditText) view.findViewById(R.id.credit_percent);
        TextView setting= (TextView) view.findViewById(R.id.commit);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(percent.getText().toString().trim().equals("")){
                    ToastUtil.showToastShort("请输入提现比例");
                    return;
                }
                if(Integer.valueOf(percent.getText().toString().trim())>=100){
                    ToastUtil.showToastShort("请输入正确的提现比例，最大100");
                    return;
                }
                JSONObject js=new JSONObject();
                try {
                    js.put("m_id",Constants.M_id);
                    js.put("user_id",PreferenceUtil.getUserId(Mine_JiFen.this));
                    js.put("redeem",percent.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m=ApisSeUtil.i(js);
                OkHttpUtils.post(Constants.Tixian_Setting).tag(this)
                        .params("key",m.K())
                        .params("msg",m.M())
                        .execute(new AbsCallback<HashMap<String,String>>() {
                            @Override
                            public HashMap<String, String> parseNetworkResponse(Response response) throws Exception {
                                return AnalyticalJSON.getHashMap(response.body().string());
                            }

                            @Override
                            public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                                if("000".equals(map.get("code"))){
                                    ToastUtil.showToastShort("提现比例设置成功");
                                    dialog.dismiss();
                                }else if("003".equals(map.get("code"))){
                                    ToastUtil.showToastShort("没有权限或账号不存在");
                                }
                            }
                        });
            }
        });
        Window w=dialog.getWindow();
        w.setGravity(Gravity.BOTTOM);
        w.getDecorView().setPadding(0,0,0,DimenUtils.dip2px(this,10));
        WindowManager.LayoutParams wl=w.getAttributes();
        wl.windowAnimations=R.style.dialogWindowAnim;
        w.setBackgroundDrawableResource(R.color.transparent);
        wl.width=getResources().getDisplayMetrics().widthPixels-DimenUtils.dip2px(Mine_JiFen.this,40);
        wl.height=WindowManager.LayoutParams.WRAP_CONTENT;
        w.setAttributes(wl);

        dialog.show();
    }

    private void showTixianDialog(HashMap<String, String> map) {
        if(Double.valueOf(map.get("credit"))<1000){
            ToastUtil.showToastShort("当前积分太少啦，拥有1000积分以上方可提现");
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_tixian, null);
        final TextView now = (TextView) view1.findViewById(R.id.credit_now);
        final TextView exp = (TextView) view1.findViewById(R.id.credit_exp);
        final EditText needTixian = (EditText) view1.findViewById(R.id.credit_needtotixian);
        final EditText aliId = (EditText) view1.findViewById(R.id.alipayId);
        final EditText aliId2 = (EditText) view1.findViewById(R.id.alipay_confirm);
        final EditText aliName = (EditText) view1.findViewById(R.id.alipay_name);
        TextView cancle = (TextView) view1.findViewById(R.id.cancle);
        TextView commit = (TextView) view1.findViewById(R.id.commit);
        b.setView(view1);

        final double now_credit=Double.valueOf(map.get("credit"))-Double.valueOf((map.get("nowcredit").equals("null")?"0":map.get("nowcredit")));
        now.setText(mApplication.ST("拥有积分: " + map.get("credit") + "\n" +
                "冻结积分: "+(map.get("nowcredit").equals("null")?"0":map.get("nowcredit"))+"\n可提现: "+now_credit+
                "\n提现比例: " + map.get("redeem") + "%"));
        int money = (int) (1000 * Integer.valueOf(map.get("redeem")) / 100);
        exp.setText(mApplication.ST("例如:1000积分" + ",可收到" + money + "元，且至少1000积分才可提现"));
        final AlertDialog dialog = b.create();
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (needTixian.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请输入需要提现的积分数量");
                    return;
                }
                if (Integer.valueOf(needTixian.getText().toString().trim()) >now_credit) {
                    ToastUtil.showToastShort("现有积分不足");
                    return;
                }
                if (Integer.valueOf(needTixian.getText().toString().trim()) < 1000) {
                    ToastUtil.showToastShort("提现积分至少为1000积分");
                    return;
                }
                if (aliId.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请输入支付宝账号");
                    return;
                }
                if (!aliId.getText().toString().trim().equals(aliId2.getText().toString().trim())) {
                    ToastUtil.showToastShort("账号输入不一致");
                    return;
                }
                if (aliName.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请输入真实姓名");
                    return;
                }

                if (Network.HttpTest(Mine_JiFen.this)) {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(Mine_JiFen.this).getString("user_id", ""));
                        js.put("phone", aliId.getText().toString().trim());
                        js.put("name", aliName.getText().toString().trim());
                        js.put("credit", needTixian.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    OkHttpUtils.post(Constants.Commit_Tixian)
                            .params("key", m.K())
                            .params("msg", m.M())
                            .execute(new AbsCallback<HashMap<String, String>>() {
                                @Override
                                public HashMap<String, String> parseNetworkResponse(Response response) throws Exception {
                                    return AnalyticalJSON.getHashMap(response.body().string());
                                }

                                @Override
                                public void onSuccess(HashMap<String, String> map, Call call, Response response) {
                                    if ("000".equals(map.get("code"))) {
                                        ToastUtil.showToastShort("提现申请已提交");
                                        dialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
        Window windo = dialog.getWindow();
        windo.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wl = windo.getAttributes();
        windo.setWindowAnimations(R.style.dialogWindowAnim);
        windo.setBackgroundDrawableResource(R.color.transparent);
        wl.width = getResources().getDisplayMetrics().widthPixels * 3 / 5;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windo.setAttributes(wl);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(PAGESIZE, true);
        getData();
    }

    public static class JFAdapter extends BaseQuickAdapter<HashMap<String, String>> {

        private Activity a;

        public JFAdapter(Activity ac, List<HashMap<String, String>> data) {
            super(R.layout.item_mine_jifen, data);
            WeakReference<Activity> w = new WeakReference<Activity>(ac);
            a = w.get();

        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            if (PreferenceUtil.getUserIncetance(a).getString("role", "").equals("3")) {
                holder.setText(R.id.gongxian, "积分:" + map.get("credit"));
            } else {
                holder.setText(R.id.gongxian, "贡献:" + map.get("integral"));
            }
            holder.setText(R.id.username, map.get("pet_name"))
                    .setText(R.id.yaoqingma, "邀请码:" + map.get("phone"));
            Glide.with(a).load(map.get("user_image"))
                    .override(DimenUtils.dip2px(a, 50), DimenUtils.dip2px(a, 50))
                    .fitCenter().into((ImageView) holder.getView(R.id.head));


        }
    }
}
