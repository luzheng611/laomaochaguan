package com.laomachaguan.Model_Order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.View.mItemListview;
import com.laomachaguan.WuLiu.WuLiuDetail;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;

import static com.laomachaguan.R.id.address;

/**
 * Created by Administrator on 2017/1/19.
 */
public class MyZhiFuDetail extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private String snr;
    private ImageView Back;
    private mItemListview listview;
    private TextView Title, User, Address, Phone, Time, Status, Snr, YouHui, Money_Total, Pay;
    public TextView Total_Cost;
    private itemAdapter adapter;
    private SwipeRefreshLayout swip;
    public double total_cost = 0d;
    private boolean isPintuan = false;
    private ScheduledExecutorService ses;
    private String id;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.my_zhifu_detail);
        Back = (ImageView) findViewById(R.id.title_back);
        Back.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));
        Back.setVisibility(View.VISIBLE);
        Back.setOnClickListener(this);
        Title = (TextView) findViewById(R.id.title_title);
        Title.setText("订单详情");
        initView();
        snr = getIntent().getStringExtra("snr");
        id = getIntent().getStringExtra("id");
        LogUtil.e("订单Id:;"+id+"    订单号：；"+snr+"    是否是开团：："+(isPintuan?"是":"否"));
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                getData();
            }
        });
        findViewById(R.id.wuliu).setOnClickListener(this);

    }

    /**
     * 初始化数据
     */
    private void initView() {

        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        User = (TextView) findViewById(R.id.username);
        Address = (TextView) findViewById(address);
        Phone = (TextView) findViewById(R.id.phone);
        Time = (TextView) findViewById(R.id.time);
        Status = (TextView) findViewById(R.id.status);
        Snr = (TextView) findViewById(R.id.snr);
        Total_Cost = (TextView) findViewById(R.id.total);
        YouHui = (TextView) findViewById(R.id.youhui);
        Money_Total = (TextView) findViewById(R.id.money_total);
        Pay = (TextView) findViewById(R.id.pay);
        listview = (mItemListview) findViewById(R.id.listview);
        adapter = new itemAdapter(this, new ArrayList<HashMap<String, String>>());
        listview.setAdapter(adapter);
        isPintuan = getIntent().getBooleanExtra("KT", false);
        if (isPintuan) {
            findViewById(R.id.putong).setVisibility(View.GONE);
            findViewById(R.id.pintuan).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.putong).setVisibility(View.VISIBLE);
            findViewById(R.id.pintuan).setVisibility(View.GONE);
        }
    }

    private static Handler handler = new Handler();

    /**
     * 获取订单详情
     */
    private void getData() {
        String url = "";

        JSONObject js = new JSONObject();
        try {
            if (PreferenceUtil.getUserIncetance(this).getString("role", "").equals("3")) {//管理员订单详情
                js.put("id", id);
                url = Constants.GoodsAdminDdxq;
            } else {//个人订单详情
                url = Constants.MyOrders_detail;
            }
            js.put("m_id",Constants.M_id);
            js.put("user_id", getIntent().getBooleanExtra("isAgent",false)?getIntent().getStringExtra("user_id"):PreferenceUtil.getUserIncetance(this).getString("user_id", ""));
            js.put("snr", snr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        LogUtil.e("获取订单详情：：：：："+js);
        OkHttpUtils.post(url).tag(this).params("key", m.K())
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
                                final String data = response.body().string();
                                if (!data.isEmpty()) {
                                    final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (map != null) {

                                                HashMap<String, String> address = null;
                                                if (map.get("address") != null && !map.get("address").equals("null")) {
                                                    address = AnalyticalJSON.getHashMap(map.get("address"));
                                                }
                                                if (address != null) {
                                                    User.setText("收  货  人: " + address.get("receiver") + "    " + address.get("phone"));
                                                    if (address.get("province").contains("北京")
                                                            || address.get("province").contains("上海")
                                                            || address.get("province").contains("天津")
                                                            || address.get("province").contains("重庆")
                                                            || address.get("province").contains("香港")
                                                            || address.get("province").contains("澳门")
                                                            || address.get("province").contains("钓鱼岛")) {
                                                        if(address.get("province").contains("澳门")||address.get("province").contains("香港")){
                                                            Address.setText("地        址: " + address.get("province")+"特别行政区" + address.get("address"));
                                                        }else{
                                                            Address.setText("地        址: " + address.get("province")+(address.get("province").endsWith("岛")?"":"市") + address.get("address"));
                                                        }

                                                    } else {
                                                        if(address.get("city").endsWith("区")||address.get("city").endsWith("州")){
                                                            Address.setText("地        址: " + address.get("province")+"省"+address.get("city")+address.get("address"));
                                                        }else if(address.get("city").contains("其他")){
                                                            Address.setText("地        址: " + address.get("province")+"省"+address.get("address"));
                                                        }else{
                                                            Address.setText("地        址: " + address.get("province")+"省"+address.get("city")+"市"+address.get("address"));
                                                        }

                                                    }

                                                }
                                                if (!isPintuan) {
                                                    ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList(data, "order");
                                                    Snr.setText(map.get("snr"));
                                                    Money_Total.setText("￥" + map.get("money"));
                                                    Pay.setText("￥" + map.get("money"));
                                                    Status.setText("已支付");
                                                    Time.setText(map.get("end_time"));
                                                    adapter.Update(list);
                                                    for (HashMap<String, String> map : list) {
                                                        total_cost += (Integer.valueOf(map.get("num")) * Double.valueOf(map.get("cost")));
                                                    }
                                                    if (total_cost > 0) {
                                                        YouHui.setText("￥" + String.format("%.2f", total_cost - Double.valueOf(map.get("money"))));
                                                        Total_Cost.setText("￥" + String.format("%.2f", total_cost));
                                                    } else {
                                                        YouHui.setText("￥" + String.format("%.2f", total_cost));
                                                        Total_Cost.setText("￥" + String.format("%.2f", Double.valueOf(map.get("money"))));
                                                    }

                                                } else {
                                                    ((TextView) findViewById(R.id.time_snr)).setText("订单时间:" + map.get("end_time") + "\n" +
                                                            "订单编号:" + map.get("snr"));
                                                    ((TextView) findViewById(R.id.sut_title)).setText(map.get("sut_title"));
                                                    SpannableString ss = new SpannableString("实付款:￥" + map.get("money"));
                                                    ss.setSpan(new ForegroundColorSpan(Color.RED), 4, ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                    ((TextView) findViewById(R.id.sut_money)).setText(ss);



                                                }
                                                String status = getIntent().getStringExtra("status");
                                                TextView rstime = (TextView) findViewById(R.id.rest_time);
                                                switch (status) {
                                                    case "等待发货":
                                                    case "发货":
                                                        rstime.setText("等待发货");
                                                        break;
                                                    case "退款":
                                                        rstime.setText("拼团失败");
                                                        break;
                                                    case "完成拼团":
                                                    case "等待拼团":
                                                        DownTime(map);
                                                        break;
                                                    case "已申请退款":
                                                        rstime.setText("已申请退款");
                                                        break;
                                                    case "已发货":
                                                        rstime.setText("已发货");
                                                        findViewById(R.id.wuliu).setVisibility(View.VISIBLE);
                                                        findViewById(R.id.wuliu).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                Intent intent=new Intent(MyZhiFuDetail.this, WuLiuDetail.class);
                                                                intent.putExtra("exp_code",map.get("exp_code"));
                                                                intent.putExtra("express",map.get("express"));
                                                                intent.putExtra("exp_name",map.get("exp_name"));
                                                                startActivity(intent);
                                                            }
                                                        });
                                                        break;
                                                    case "已退款":
                                                        rstime.setText("已退款");
                                                        break;

                                                }

                                            }
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            swip.setRefreshing(false);
                                            Toast.makeText(MyZhiFuDetail.this, "加载失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        swip.setRefreshing(false);
                                        Toast.makeText(MyZhiFuDetail.this, "加载失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            } finally {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (swip.isRefreshing()) swip.setRefreshing(false);
                                    }
                                });
                            }
                        }else{
                            swip.setRefreshing(false);
                        }
                    }




                });
    }

    private void DownTime(final HashMap<String, String> map) {
        ses = Executors.newScheduledThreadPool(1);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long begin = TimeUtils.dataOne(map.get("end_time"));
                        long over = begin + (24 * 60 * 60 * 1000 * 2L);
                        long offset = over - System.currentTimeMillis();
                        LogUtil.e(begin + "   " + over + "    " + System.currentTimeMillis());
                        ((TextView) findViewById(R.id.rest_time)).setText("申请退款还需等待:" + TimeUtils.formatTimeShort(offset, true));
                    }
                });

            }
        };
        ses.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
        if (ses != null) {
            ses.shutdownNow();
        }


    }

    @Override
    public void onRefresh() {
        getData();
    }


    private static class itemAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> list;


        public itemAdapter(Context context, ArrayList<HashMap<String, String>> list) {
            super();
            this.context = context;
            this.list = list;
        }

        public void Update(ArrayList<HashMap<String, String>> list) {

            this.list = list;
            notifyDataSetChanged();

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
        public View getView(int position, View view, ViewGroup parent) {
            HashMap<String, String> map = list.get(position);
            LogUtil.e("getView！！@！@！@！");

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.myzhifudetail_item, parent, false);
            }
            if (((mItemListview) parent).isOnMeasure) {
                return view;
            }
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView cost = (TextView) view.findViewById(R.id.cost);
            TextView money = (TextView) view.findViewById(R.id.money);
            TextView num = (TextView) view.findViewById(R.id.num);
            name.setText(map.get("title"));
            cost.setText("￥" + map.get("cost"));
            cost.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            money.setText("￥" + map.get("money"));
            num.setText("×" + map.get("num"));


            return view;
        }
    }
}
