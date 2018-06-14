package com.laomachaguan.Model_Order;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mPLlistview;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/1/7.
 */
public class Dingdan_commit extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Dingdan_commit";//, youbian,province,city, phone
    private ImageView back;
    private LinearLayout No_Address;
      private RelativeLayout      Have_address;
    private mPLlistview listview;
    private TextView username, address, youhui,allmoney, buy;
    private SharedPreferences sp;
    private ArrayList<HashMap<String, String>> list;
    private DdAdapter adapter;
    private Double moneyAll = 0d, allcost = 0d,allYouhui=0d;
    private int allNum=0;
    private String ADDRESS_ID;
    private  boolean havaAddress;
    private StringBuilder AllTitle;
    private StringBuilder msg;
    private boolean isPintuan=false;
    private String  spec_id;
    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.dingdan_layout);

        initView();
        initData();
    }

    private void initData() {
        if (sp.getString("address", "").equals("")) {
            No_Address.setVisibility(View.VISIBLE);
            Have_address.setVisibility(View.GONE);
            havaAddress=false;
            LogUtil.e("隐藏地址");
        } else {
            LogUtil.e("显示地址");
            ADDRESS_ID= sp.getString("id","");
            No_Address.setVisibility(View.GONE);
            Have_address.setVisibility(View.VISIBLE);
            havaAddress=true;
            username.setText("收  货  人: "+sp.getString("name","")+"       "+sp.getString("phone",""));
//            phone.setText(sp.getString("phone",""));
            if (sp.getString("province","").contains("北京")
                    || sp.getString("province","").contains("上海")
                    || sp.getString("province","").contains("天津")
                    || sp.getString("province","").contains("重庆")
                    || sp.getString("province","").contains("香港")
                    || sp.getString("province","").contains("澳门")
                    || sp.getString("province","").contains("钓鱼岛")) {
                if(sp.getString("province","").contains("澳门")||sp.getString("province","").contains("香港")){
                    address.setText("地        址: " +  sp.getString("province","")+"特别行政区" + sp.getString("address",""));
                }else{
                    address.setText("地        址: " +  sp.getString("province","")+( sp.getString("province","").endsWith("岛")?"":"市") + sp.getString("address",""));
                }

            } else {
                if(sp.getString("city","").endsWith("区")||sp.getString("city","").endsWith("州")){
                    address.setText("地        址: " + sp.getString("province","")+"省"+sp.getString("city","")+ sp.getString("address",""));
                }else if(sp.getString("city","").contains("其他")){
                    address.setText("地        址: " + sp.getString("province","")+"省"+ sp.getString("address",""));
                }else{
                    address.setText("地        址: " + sp.getString("province","")+"省"+sp.getString("city","")+"市"+ sp.getString("address",""));
                }

            }
//            youbian.setText(sp.getString("youbian",""));
//            city.setText(sp.getString("city",""));
//            province.setText(sp.getString("province",""));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if(mApplication.good_id_list!=null){
            mApplication.good_id_list.clear();
        }
    }

    private void initView() {
        IntentFilter intentFilter=new IntentFilter("DingDan");
        registerReceiver(receiver,intentFilter);
        isPintuan=getIntent().getBooleanExtra("KT",false);
        list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("list");
        Log.w(TAG, "initView: " + getIntent().getStringArrayListExtra("list"));
        sp = getSharedPreferences("address", MODE_PRIVATE);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        No_Address = (LinearLayout) findViewById(R.id.no_address);
        No_Address.setOnClickListener(this);
        Have_address = (RelativeLayout) findViewById(R.id.have_address);
        Have_address.setOnClickListener(this);
        listview = (mPLlistview) findViewById(R.id.dingdan_listview);
        listview.removeFooterView(listview.footer);
        username = (TextView) findViewById(R.id.username);
//        phone = (TextView) findViewById(phone);
        address = (TextView) findViewById(R.id.address);
//        youbian = (TextView) findViewById(youbian);
        youhui = (TextView) findViewById(R.id.youhui);
//        province= (TextView) findViewById(province);
//        city= (TextView) findViewById(city);
        allmoney = (TextView) findViewById(R.id.allmoney);
        buy = (TextView) findViewById(R.id.dingdan_buy);
        buy.setOnClickListener(this);
        adapter = new DdAdapter(this, list);

        listview.setAdapter(adapter);

                msg=new StringBuilder();
                AllTitle=new StringBuilder();
                AllTitle.append(list.get(0).get("title"));
                for (HashMap<String, String> map : list) {
                    int num = Integer.valueOf(map.get("num"));
                    double cost = Double.valueOf(map.get("cost").equals("")?"0":map.get("cost"));
                    double money = Double.valueOf(map.get("money"));
                    if(cost==0){
                        allYouhui+=0;
                    }else{
                        allYouhui+=((cost-money)*num);
                    }
                    allNum+=num;
                    allcost += num * cost;
                    moneyAll += num * money;
                    msg.append(map.get("id")+",");
                    if(mApplication.good_id_list==null){
                        mApplication.good_id_list=new ArrayList<String>();
                    }
                    mApplication.good_id_list.add(map.get("good_id"));
                    LogUtil.e("需要购买的商品id:"+mApplication.good_id_list+"    需付：；"+moneyAll);
                    if(list.indexOf(map)==list.size()-1){
                        if(isPintuan){
                            AllTitle.append("[拼团]");
                        }

                    }

                    spec_id=map.get("spec_id");
                }
                msg.deleteCharAt(msg.length()-1);
                LogUtil.e("原价："+allcost+"拼接字段："+msg.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(allcost!=0){
                            if(isPintuan){
                                youhui.setText("售价:￥"+allcost);
                            }else{
                                youhui.setText("优惠:￥" + String.format("%.2f", allYouhui));
                            }

                        }else{
                            if(isPintuan){
                                youhui.setText("售价:￥0");
                            }else{
                                youhui.setText("优惠:￥0" );
                            }

                        }
                        if(isPintuan){
                            SpannableStringBuilder ssb = new SpannableStringBuilder("拼团价:￥" + String.format("%.2f", moneyAll));
                            ssb.setSpan(new ForegroundColorSpan(Color.RED), 4, ssb.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            allmoney.setText(ssb);
                        }else{
                            SpannableStringBuilder ssb = new SpannableStringBuilder("待支付:￥" + String.format("%.2f", moneyAll));
                            ssb.setSpan(new ForegroundColorSpan(Color.RED), 4, ssb.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            allmoney.setText(ssb);
                        }



                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.dingdan_buy:
                LogUtil.e("地址Id:"+ADDRESS_ID);
                if("".equals(ADDRESS_ID)||ADDRESS_ID==null||ADDRESS_ID.equals("null")){
                    Toast.makeText(Dingdan_commit.this, "请设置并选择收货地址", Toast.LENGTH_SHORT).show();
                    No_Address.performClick();
                    return;
                }

                if(isPintuan){
                    mApplication.openPayLayout(this,String .format("%.2f",moneyAll),msg.toString(),AllTitle.toString(),"1",ADDRESS_ID,"11",getIntent().getLongExtra("number",0)+"",spec_id);
                }else{
                    mApplication.openPayLayout(this,String .format("%.2f",moneyAll),msg.toString(),AllTitle.toString(),allNum+"",ADDRESS_ID,"7","0",spec_id);
                }



                break;
            case R.id.no_address:
                Intent intent=new Intent(this,MyShouHuoAddress.class);
                startActivityForResult(intent,0);
                break;
            case R.id.have_address:
                Intent intent1=new Intent(this,MyShouHuoAddress.class);
                intent1.putExtra("id",sp.getString("id",""));
                startActivityForResult(intent1,0);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0x00) {
            Log.w(TAG, "onActivityResult: 设置新地址" +data);
            username.setTag(data.getStringExtra("id"));
            username.setText("收 货 人: "+sp.getString("name","")+"       "+sp.getString("phone",""));
//            phone.setText(sp.getString("phone",""));
            if (sp.getString("province","").contains("北京")
                    || sp.getString("province","").contains("上海")
                    || sp.getString("province","").contains("天津")
                    || sp.getString("province","").contains("重庆")
                    || sp.getString("province","").contains("香港")
                    || sp.getString("province","").contains("澳门")
                    || sp.getString("province","").contains("钓鱼岛")) {
                if(sp.getString("province","").contains("澳门")||sp.getString("province","").contains("香港")){
                    address.setText("地        址: " +  sp.getString("province","")+"特别行政区" + sp.getString("address",""));
                }else{
                    address.setText("地        址: " +  sp.getString("province","")+( sp.getString("province","").endsWith("岛")?"":"市") + sp.getString("address",""));
                }

            } else {
                if(sp.getString("city","").endsWith("区")||sp.getString("city","").endsWith("州")){
                    address.setText("地        址: " + sp.getString("province","")+"省"+sp.getString("city","")+ sp.getString("address",""));
                }else if(sp.getString("city","").contains("其他")){
                    address.setText("地        址: " + sp.getString("province","")+"省"+ sp.getString("address",""));
                }else{
                    address.setText("地        址: " + sp.getString("province","")+"省"+sp.getString("city","")+"市"+ sp.getString("address",""));
                }

            }
            ADDRESS_ID=data.getStringExtra("id");
//            if (tip.getVisibility() == View.VISIBLE) {
//                tip.setVisibility(View.GONE);
//                user.setVisibility(View.VISIBLE);
//                address.setVisibility(View.VISIBLE);
//                phone.setVisibility(View.VISIBLE);
//                ((ImageView) findViewById(R.id.next)).setVisibility(View.VISIBLE);
//            }
        } else if (resultCode == 100) {
            Log.w(TAG, "onActivityResult: 原地址被删除" );
//            if (tip.getVisibility() == View.GONE) {
//                tip.setVisibility(View.VISIBLE);
//            }
//            user.setVisibility(View.GONE);
            SharedPreferences.Editor editor=sp.edit();
            editor.clear();
            editor.commit();
            username.setTag(null);
            ADDRESS_ID="";
        }else if(resultCode==200){
            Log.w(TAG, "onActivityResult: 正常" );
        }
        initData();
    }
    public static class DdAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, String>> list;
        private Activity context;
        private WeakReference<Activity> w;

        public DdAdapter(Activity a, ArrayList<HashMap<String, String>> list) {
            super();
            w = new WeakReference<Activity>(a);
            context = w.get();
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
        public View getView(int position, View view, ViewGroup parent) {
            Holder holder = null;
            HashMap<String, String> map = list.get(position);
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.dingdan_item, parent, false);
                holder = new Holder();
                holder.cost = (TextView) view.findViewById(R.id.cost);
                holder.name = (TextView) view.findViewById(R.id.username);
                holder.money = (TextView) view.findViewById(R.id.money);
                holder.num = (TextView) view.findViewById(R.id.num);
                holder.tag= (TextView) view.findViewById(R.id.tag);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.name.setText(map.get("title"));
            holder.num.setText("×" + map.get("num"));
            if(map.get("cost").equals("")||map.get("cost").equals("0")){
                holder.cost.setVisibility(View.INVISIBLE);
            }else{
                holder.cost.setVisibility(View.VISIBLE);
                holder.cost.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.cost.setText("￥" + String.format("%.2f", Double.valueOf(map.get("cost"))));
            }

            holder.money.setText("￥" + String.format("%.2f", Double.valueOf(map.get("money"))));
            holder.tag.setText((map.get("name").equals("0")?"无":map.get("name"))+";"+(map.get("spec_name").equals("0")?"无":map.get("name")));

            return view;
        }

        static class Holder {
            TextView name, cost, money, num,tag;
        }
    }
}
