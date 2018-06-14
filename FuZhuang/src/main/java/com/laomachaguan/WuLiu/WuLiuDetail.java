package com.laomachaguan.WuLiu;

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
import android.text.style.AbsoluteSizeSpan;
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
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.View.mWuLiuDecoration;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/8/18 15:51
 * 公司：成都因陀罗网络科技有限公司
 */

public class WuLiuDetail extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private ImageView back;
    private TextView status, company, code;
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private wuliuAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.activity_wuliu_detail);
        back = (ImageView) findViewById(R.id.title_back);
        status = (TextView) findViewById(R.id.status);
        company = (TextView) findViewById(R.id.company);
        code = (TextView) findViewById(R.id.WlCode);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new mWuLiuDecoration(this));
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        back.setOnClickListener(this);

        adapter = new wuliuAdapter(new ArrayList<HashMap<String, String>>());
        TextView textView = new TextView(this);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.loadnothing);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 150), DimenUtils.dip2px(this, 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(this, 10));
        textView.setText("暂无物流信息");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(this, 150);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        recyclerView.setAdapter(adapter);

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });


    }

    @Override
    public void onRefresh() {

        getData();
    }

    private void getData() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("m_id", Constants.M_id);
                js.put("exp_code", getIntent().getStringExtra("exp_code"));
                js.put("express", getIntent().getStringExtra("express"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("查看物流：：" + js);
            OkHttpUtils.post(Constants.Expresses)
                    .tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {
                        @Override
                        public ArrayList<HashMap<String, String>> parseNetworkResponse(Response response) throws Exception {
                            ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                            final HashMap<String, String> m = AnalyticalJSON.getHashMap(response.body().string());
                            if (m != null) {
                                final HashMap<String, String> m1 = AnalyticalJSON.getHashMap(m.get("result"));
                                if(m1!=null){
                                    if (m1.get("list") != null) {
                                        list = AnalyticalJSON.getList_zj(m1.get("list"));
                                        LogUtil.e("list:::" + list);
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        String s = "";
                                        if (m1 != null) {
                                            if (m1.get("deliverystatus") != null) {
                                                switch (m1.get("deliverystatus")) {
                                                    case "1":
                                                        s = "派送中";

                                                        break;
                                                    case "2":
                                                        s = "派件中";
                                                        break;
                                                    case "3":
                                                        s = "已签收";
                                                        break;
                                                    case "4":
                                                        s = "派送失败";
                                                        break;
                                                }
                                                SpannableString s1 = new SpannableString("物流状态    " + s);
                                                s1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(WuLiuDetail.this, R.color.green)), 8, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                status.setText(s1);
                                                code.setText("运单编号:   " + m1.get("number"));
                                                SpannableString ss = new SpannableString("承运来源:   " + getIntent().getStringExtra("exp_name"));
                                                company.setText(ss);

                                            }
                                        }else{
                                            ToastUtil.showToastShort(m.get("msg"));
                                        }


                                    }
                                });


                            }
                            return list;
                        }

                        @Override
                        public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                            adapter.setNewData(list);
                        }

                        @Override
                        public void onAfter(@Nullable ArrayList<HashMap<String, String>> list, @Nullable Exception e) {
                            super.onAfter(list, e);
                            swip.setRefreshing(false);
                        }
                    });

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    private class wuliuAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        public wuliuAdapter(List<HashMap<String, String>> data) {
            super(R.layout.item_wuliu, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            SpannableString ss = new SpannableString(map.get("status") + "\n\n" + map.get("time"));
            if (getData().indexOf(map) == 0) {
                holder.getView(R.id.circle).setSelected(true);
                holder.setTextColor(R.id.content, ContextCompat.getColor(WuLiuDetail.this,R.color.green));

            } else {
                holder.getView(R.id.circle).setSelected(false);
                holder.setTextColor(R.id.content, Color.BLACK);
            }
            ss.setSpan(new AbsoluteSizeSpan(13, true), ss.length() - map.get("time").length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.setText(R.id.content, ss);
        }
    }
}
