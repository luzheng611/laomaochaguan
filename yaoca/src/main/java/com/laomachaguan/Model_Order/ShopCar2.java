package com.laomachaguan.Model_Order;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.View.NoScrollViewPager;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/30.
 */
public class ShopCar2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, OnChangeListener {
    private SwipeRefreshLayout swip;
    private ListView listView;
    private TextView toatalNum, buy, tip;
    private boolean isLoaded = false;
    private static final String TAG = "ShopCar";
    private carAdapter adpter;
    private CheckBox totalBox;
    private double totalnum = 0;
    private int chooseNum = 0;
    private PopupWindow p;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            swip.post(new Runnable() {
                @Override
                public void run() {
                    swip.setRefreshing(true);
                    getData();
                }
            });
        }
    };
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_shopcar,container,false);
        initView();
        IntentFilter intentFilter = new IntentFilter("car");
        getActivity().registerReceiver(receiver, intentFilter);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                getData();
            }
        });
        return view;

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser&&view!=null){
            LogUtil.e("禁止滑动");
            ((NoScrollViewPager) view.getParent()).setNoScroll(true);
        }else if(!isVisibleToUser&&view!=null){
            LogUtil.e("开启滑动");
            ((NoScrollViewPager) view.getParent()).setNoScroll(false);
        }
    }

    /**
     * 获取购物车数据
     */
    private void getData() {
        if (PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", "").equals("")) {
            tip.setVisibility(View.VISIBLE);
            tip.setText("点击登录");
            Drawable drawable = getResources().getDrawable(R.mipmap.load_nothing);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            tip.setCompoundDrawables(null, drawable, null, null);
            return;
        }
        if (Network.HttpTest(getActivity())) {
            totalnum = 0;
            toatalNum.setText("¥ " + totalnum);
            buy.setText("去结算");
            totalBox.setChecked(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject js=new JSONObject();
                        try {
                            js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String data = OkHttpUtils.post(Constants.Shopcar_list).tag(TAG)
                                .params("key", ApisSeUtil.getKey()).params("msg",
                               ApisSeUtil.getMsg(js)).execute().body().string();
                        if (!TextUtils.isEmpty(data)) {
                            final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(data);
                           getActivity(). runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (list != null) {
                                        if (list.size() == 0) {
                                            tip.setVisibility(View.VISIBLE);
                                            tip.setText("");
                                            Drawable drawable = getResources().getDrawable(R.mipmap.load_nothing);
                                            drawable.setBounds(0, 0, 200, 200 * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
                                            tip.setCompoundDrawables(null, drawable, null, null);
                                        } else {
                                            tip.setVisibility(View.GONE);
                                        }
                                        if (adpter == null) {
                                            adpter = new carAdapter(getActivity(), list);
                                            adpter.setOnChangeListener(ShopCar2.this);
                                            listView.setAdapter(adpter);
                                        } else {

                                            adpter.addList(list);
                                            adpter.notifyDataSetChanged();
                                        }
                                        if (swip != null && swip.isRefreshing())
                                            swip.setRefreshing(false);
                                        isLoaded = true;
                                    } else {
                                        if (swip != null && swip.isRefreshing())
                                            swip.setRefreshing(false);
                                        tip.setVisibility(View.VISIBLE);
                                        tip.setText("");
                                        Drawable drawable = getResources().getDrawable(R.mipmap.load_fail);
                                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                                        tip.setCompoundDrawables(null, drawable, null, null);
                                        Toast.makeText(getActivity(), "系统繁忙，请检查网络连接或是下拉重新加载", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (swip != null && swip.isRefreshing())
                                        swip.setRefreshing(false);
                                    tip.setVisibility(View.VISIBLE);
                                    tip.setText("");
                                    Drawable drawable = getResources().getDrawable(R.mipmap.load_fail);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                                    tip.setCompoundDrawables(null, drawable, null, null);
                                    Toast.makeText(getActivity(), "系统繁忙，请检查网络连接或是下拉重新加载", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tip.setVisibility(View.VISIBLE);
                                tip.setText("");
                                Drawable drawable = getResources().getDrawable(R.mipmap.load_fail);
                                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                                tip.setCompoundDrawables(null, drawable, null, null);
                                if (swip != null && swip.isRefreshing()) swip.setRefreshing(false);
                                Toast.makeText(getActivity(), "系统繁忙，请检查网络连接或是下拉重新加载", Toast.LENGTH_SHORT).show();
                            }
                        });
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            tip.setVisibility(View.VISIBLE);
            tip.setText("");
            Drawable drawable = getResources().getDrawable(R.mipmap.load_fail);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            tip.setCompoundDrawables(null, drawable, null, null);
            Toast.makeText(getActivity(), "网络连接失败，请检查网络连接，", Toast.LENGTH_SHORT).show();
            if (swip != null && swip.isRefreshing()) swip.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkHttpUtils.getInstance().cancelTag(TAG);
        getActivity().unregisterReceiver(receiver);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (adpter != null && adpter.Changed != null) {
            for (final String s : adpter.Changed) {
                final String str=s;
                if(Integer.valueOf(s)>=adpter.Nums.size()){
                    break;
                }
                JSONObject js=new JSONObject();
                try {
                    js.put("shopcar_id", adpter.list.get(Integer.valueOf(s)).get("id"));
                    js.put("num", adpter.Nums.get(Integer.valueOf(s)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OkHttpUtils.post(Constants.Shopcar_save)
                        .params("key", ApisSeUtil.getKey())
                        .params("msg",ApisSeUtil.getMsg(js))
                      .execute(new AbsCallback<Object>() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        return null;
                    }

                          @Override
                          public void onSuccess(Object o, Call call, Response response) {
                              try {
                                  String data = response.body().string();
                                  if (!TextUtils.isEmpty(data)) {
                                      final HashMap<String, String> m = AnalyticalJSON.getHashMap(data);
                                      if ("000".equals(m.get("code"))) {
                                          LogUtil.e("购物车数据保存成功：" + str);
                                      } else {
                                          LogUtil.e("购物车数据未保存"+str);
                                      }

                                  } else {
                                      LogUtil.e("购物车数据未保存"+str);
                                  }
                              } catch (Exception e) {
                                  LogUtil.e("有部分购物车数据未保存");
                              }
                          }



                });


            }
        }
    }


    /**
     * 初始化控件
     */

    private void initView() {
        swip = (SwipeRefreshLayout) view.findViewById(R.id.shopcar_swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        view. findViewById(R.id.back).setOnClickListener(this);
        listView = (ListView)view. findViewById(R.id.shopcar_listview);
        tip = (TextView)view. findViewById(R.id.shopcar_tip);
        tip.setOnClickListener(this);
        toatalNum = (TextView) view.findViewById(R.id.shopcar_total);
        buy = (TextView) view.findViewById(R.id.shopcar_buy);
        buy.setOnClickListener(this);
        totalBox = (CheckBox) view.findViewById(R.id.car_box_total);
        toatalNum.setText("¥ " + totalnum);
        buy.setText("去结算(0)");
        totalBox.setOnClickListener(this);
    }

    //// TODO: 2016/12/2 适配器监听
    @Override
    public void OnNumChanged(double money, boolean flag) {
        if (flag) {
            totalnum += money;
        } else {
            totalnum -= money;
        }
        if (totalnum < 0) totalnum = 0;
        toatalNum.setText("¥ " + String.format("%.2f", totalnum));
    }

    @Override
    public void OnChooseChanged(double choosedNum, boolean flag) {
        if (flag) {
            totalnum = totalnum + choosedNum;
            buy.setText("去结算(" + (++chooseNum) + ")");
            if (chooseNum == adpter.list.size()) {
                totalBox.setChecked(true);
            }
        } else {
            totalnum -= choosedNum;
            buy.setText("去结算(" + (--chooseNum) + ")");
            if (chooseNum != adpter.list.size()) {
                totalBox.setChecked(false);
            }
        }
        if (totalnum < 0) totalnum = 0;
        toatalNum.setText("¥ " + String.format("%.2f", totalnum));

    }

    @Override
    public void OnDeleteChoosed(double needToCutMoney) {


        totalnum -= needToCutMoney;

        if (totalnum < 0) totalnum = 0;
        toatalNum.setText("¥ " + String.format("%.2f", totalnum));
        buy.setText("去结算(" + (--chooseNum) + ")");
        if (chooseNum <= 0) {
            totalBox.setChecked(false);
            buy.setText("去结算(0)");
        }
        if (adpter.list.size() == 0) {
            tip.setVisibility(View.VISIBLE);
            tip.setText("");
            Drawable drawable = getResources().getDrawable(R.mipmap.load_nothing);
            drawable.setBounds(0, 0, 200, 200 * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
            tip.setCompoundDrawables(null, drawable, null, null);
        }
    }


    public static class carAdapter extends BaseAdapter {
        //// TODO: 2016/12/2 购物车适配器
        public ArrayList<HashMap<String, String>> list;
        private WeakReference<Activity> w;
        private Activity context;
        private LayoutInflater inflater;
        //        private ArrayList<Boolean> isOpen = new ArrayList<>();
        private ArrayList<Boolean> isChoose = new ArrayList<>();
        public ArrayList<String> Nums = new ArrayList<>();
        private OnChangeListener onChangeListener;
        private ArrayList<String> Changed = new ArrayList<>();
        private boolean isDeleted = false;

        public carAdapter(Activity a, ArrayList<HashMap<String, String>> l) {
            super();
            this.list = l;
            w = new WeakReference<Activity>(a);
            context = w.get();
            inflater = context.getLayoutInflater();
            for (int i = 0; i < list.size(); i++) {
                HashMap<String, String> map = list.get(i);
//                isOpen.add(false);
                isChoose.add(false);
                Nums.add(map.get("num"));
            }


        }

        public void setOnChangeListener(OnChangeListener onChangeListener) {
            this.onChangeListener = onChangeListener;
        }

        public void addList(ArrayList<HashMap<String, String>> l) {
            this.list.clear();
//            isOpen.clear();
            isChoose.clear();
            Nums.clear();
            this.list.addAll(l);
            for (int i = 0; i < list.size(); i++) {
                HashMap<String, String> map = list.get(i);
//                isOpen.add(false);
                isChoose.add(false);
                Nums.add(map.get("num"));
            }

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
            return 0;
        }

        @Override
        public View getView( int position, View view, ViewGroup parent) {
            ViewHolder holder = null;
            final HashMap<String, String> map = list.get(position);
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.shopcar_item2, parent, false);
                holder.l = (RelativeLayout) view.findViewById(R.id.content);
                holder.box = (ImageView) view.findViewById(R.id.checkbox);
//                holder.setting = (TextView) view.findViewById(R.id.car_setting);
                holder.image = (ImageView) view.findViewById(R.id.good_img);
                holder.title = (TextView) view.findViewById(R.id.good_name);
//                holder.abs = (TextView) view.findViewById(R.id.car_item_abs);
                holder.money = (TextView) view.findViewById(R.id.good_price);
//                holder.cost = (TextView) view.findViewById(R.id.car_item_cost);
                holder.num = (EditText) view.findViewById(R.id.good_num);
//                holder.num2 = (TextView) view.findViewById(R.id.car_item_num2);
                holder.cut = (ImageView) view.findViewById(R.id.good_jian);
                holder.add = (ImageView) view.findViewById(R.id.good_jia);
                holder.tags= (TextView) view.findViewById(R.id.good_tag);
                holder.delete = (TextView) view.findViewById(R.id.delete);
//                holder.type = (TextView) view.findViewById(R.id.car_item_type);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (isChoose.get(position)) {
                holder.box.setSelected(true);
            } else {
                holder.box.setSelected(false);
            }
            Glide.with(context).load(map.get("image")).override(DimenUtils.dip2px(context, 100), DimenUtils.dip2px(context, 75))
                    .placeholder(R.drawable.place_holder2).error(R.mipmap.load_nothing)
                    .centerCrop().into(holder.image);
            holder.title.setText(map.get("title"));
//            holder.title.setTag(map.get("id"));
            holder.money.setText("¥ " + map.get("money") + "元");
//            holder.cost.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//            holder.cost.setText("¥ " + map.get("cost"));
            holder.num.setText(Nums.get(position));
//            holder.num2.setTag(map.get("id"));
//            holder.setting.setTag(holder.l);
            holder.l.setTag(position);
            holder.box.setTag(holder.l);
//            holder.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
//                    LinearLayout l = (LinearLayout) v.getTag();
//                    final int pos = (int) l.getTag();
//                    final TextView num = (TextView) l.findViewById(R.id.car_item_num);
//                    final TextView num2 = (TextView) num.getTag();
//                    Log.w(TAG, "onClick: 单价:" + list.get(pos).get("money"));
//                    Log.w(TAG, "onClick: 数量：" + Integer.valueOf(num2.getText().toString()));
//
//                    double d = Double.valueOf(String.format("%.2f", Double.valueOf(list.get(pos).get("money")) * Integer.valueOf(num2.getText().toString())));
//
//                    if (onChangeListener != null) {
//                        onChangeListener.OnChooseChanged(d, isChecked);
//                    }
//                    isChoose.set(pos, isChecked);
//                }
//            });
            if(map.get("spec")!=null&&!map.get("spec").equals("")){
                HashMap<String,String > tag=AnalyticalJSON.getHashMap(map.get("spec"));
                holder.tags.setText(tag.get("name")+";"+tag.get("spec_name"));
            }else{
                holder.tags.setText("");
            }
            holder.l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int pos = (int) v.getTag();
                    if(list.get(pos).get("stock").equals("0")){
                        ToastUtil.showToastShort("该商品已售罄");
//                        Intent intent=new Intent("car");
//                        context.sendBroadcast(intent);
                        return;
                    }
                    final TextView num = (TextView) v.findViewById(R.id.good_num);
                    boolean flag = isChoose.get(pos);
                    Log.w(TAG, "onClick: 选中状态：" + flag);
                    v.setSelected(!flag);
                    isChoose.set(pos, !flag);

                    double d = Double.valueOf(String.format("%.2f", Double.valueOf(list.get(pos).get("money")) * Integer.valueOf(num.getText().toString())));
                    if (onChangeListener != null) {
                        onChangeListener.OnChooseChanged(d, !flag);
                    }

                }

            });
            LogUtil.e(position+"   位置");
            holder.num.addTextChangedListener(new mTextWatcher(position));


            holder.cut.setTag(holder.l);
            if (Integer.valueOf(Nums.get(position)) > 1) {
                holder.cut.setEnabled(true);
            } else {
                holder.cut.setEnabled(false);
            }
            holder.cut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout l = (RelativeLayout) v.getTag();
                    final int pos = (int) l.getTag();
                    if (!Changed.contains(pos + "")) {
                        Changed.add(pos + "");
                    }
                    final TextView num1 = (TextView) l.findViewById(R.id.good_num);
                    int num = Integer.valueOf(num1.getText().toString());
                    if (num > 1) {
                        num = num - 1;
                        if (num == 1) {
                            v.setEnabled(false);
                        }
                        Nums.set(pos, num + "");
                        num1.setText(num + "");

                    } else {
                        Toast.makeText(context, "受不了了，商品不能再减少了噢", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.add.setTag(holder.l);
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout l = (RelativeLayout) v.getTag();
                    ImageView jian = (ImageView) l.findViewById(R.id.good_jian);
                    final int pos = (int) l.getTag();

                    final TextView num1 = (TextView) l.findViewById(R.id.good_num);
                    int num = Integer.valueOf(num1.getText().toString());
                    if(num==99){
                        ToastUtil.showToastShort("单个商品最多选择99份");
                        return;
                    }

                    num++;
                    if (!Changed.contains(pos + "")) {
                        Changed.add(pos + "");
                    }
                    if (num > 1) {
                        jian.setEnabled(true);
                    }

                    Nums.set(pos, num + "");
                    LogUtil.w("onClick: 数量："+num+"  位置："+pos );
                    num1.setText(num + "");

                }
            });
            holder.delete.setTag(holder.l);
            holder.delete.setTag(R.id.id_one, map.get("id"));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                //// TODO: 2016/12/2 删除商品
                @Override
                public void onClick(View v) {
                    RelativeLayout l = ((RelativeLayout) v.getTag());
                    final int position = (int) l.getTag();
                    final TextView num1 = (TextView) l.findViewById(R.id.good_num);
                    final String id = (String) v.getTag(R.id.id_one);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("确认要删除该商品吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressUtil.show(context, "", "正在告诉服务器您不喜欢该商品");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject js=new JSONObject();
                                        try {
                                            js.put("shopcar_id", id);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ApisSeUtil.M m1=ApisSeUtil.i(js);
                                        String data = OkHttpUtils.post(Constants.Shopcar_delete)
                                                .params("key",m1.K()).params("msg",m1.M())
                                                .execute().body().string();
                                        if (!TextUtils.isEmpty(data)) {
                                            final HashMap<String, String> m = AnalyticalJSON.getHashMap(data);
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if ("000".equals(m.get("code"))) {
                                                        double d = Double.valueOf(list.get(position).get("money"));
                                                        String goodid=list.get(position).get("good_id");
                                                        list.remove(position);

                                                        Nums.remove(position);
//                                                        isOpen.remove(position);
                                                        if (isChoose.get(position)) {
                                                            if (onChangeListener != null) {
//                                                                double d = Double.valueOf(decimalFormat.format(Double.valueOf(list.get(position).get("money"))*Integer.valueOf(num2.getText().toString()))) ;
                                                                Log.w(TAG, "run: 删除的数量：" + Integer.valueOf(num1.getText().toString()) + "  单价：" + d);
                                                                onChangeListener.OnDeleteChoosed(Double.valueOf(String.format("%.2f", d * Integer.valueOf(num1.getText().toString()))))
                                                                ;
                                                            }
                                                        }
                                                        isChoose.remove(position);
                                                        isDeleted = true;
                                                        if (Changed.contains(position + ""))
                                                            Changed.remove(position + "");
                                                        notifyDataSetChanged();
                                                        Intent intent=new Intent("car");
                                                        intent.putExtra("good_id",goodid);
                                                        context.sendBroadcast(intent);

                                                    } else {
                                                        Toast.makeText(context, "一股未知力量阻止我通知服务器，希望您能检查网络并稍后重试", Toast.LENGTH_SHORT).show();
                                                    }
                                                    ProgressUtil.dismiss();
                                                }
                                            });
                                        } else {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ProgressUtil.dismiss();
                                                    Toast.makeText(context, "一股未知力量阻止我通知服务器，希望您能检查网络并稍后重试", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    } catch (Exception e) {
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ProgressUtil.dismiss();
                                                Toast.makeText(context, "一股未知力量阻止我通知服务器，希望您能检查网络并稍后重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }).setNegativeButton("不好意思，点错了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
            if (position == list.size() - 1) {
                isDeleted = false;
            }
            return view;
        }
        private  class mTextWatcher implements TextWatcher{
            private int position;
            public mTextWatcher(int pos) {
                super();
                this.position=pos;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() != 0) {
                    Nums.set(position, s.toString());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    s.append("1");
                }
                int oldnum = Integer.valueOf(Nums.get(position));
                int newnum = Integer.valueOf(s.toString());
                if(newnum>99){
                    s.replace(0,s.length(),"99");
                    ToastUtil.showToastShort("单个商品最多选择99份");
                    return;
                }
                if (!s.toString().equals("")) {
                    if (isChoose.get(position)) {
//                            double d = Double.valueOf(decimalFormat.format(Double.valueOf(list.get(pos).get("money"))));
                        if (onChangeListener != null) {
                            if (!isDeleted) {
                                LogUtil.w("afterTextChanged: 老数：" + oldnum + "  新数：" + newnum);
                                if (oldnum > newnum) {
                                    double d = (oldnum - newnum) * Double.valueOf(String.format("%.2f", Double.valueOf(list.get(position).get("money"))));
                                    onChangeListener.OnNumChanged(d, false);
                                } else {
                                    double d = (newnum - oldnum) * Double.valueOf(String.format("%.2f", Double.valueOf(list.get(position).get("money"))));
                                    onChangeListener.OnNumChanged(d, true);
                                }

                            }
                        }
                    }
                }
                Nums.set(position, newnum + "");
                list.get(position).put("num",newnum+"");
            }
        }
        class ViewHolder {
            ImageView box;
            TextView title, money, delete,tags;
            ImageView image, add, cut;
            EditText num;
            RelativeLayout l;


        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopcar_buy:

                if (LoginUtil.checkLogin(getActivity())) {
                    if (chooseNum <= 0) {
                        Toast.makeText(getActivity(), "请选择需要购买的商品", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    v.setEnabled(false);
                    ((TextView) v).setTextColor(Color.GRAY);
                    Intent intent=new Intent(getActivity(),Dingdan_commit.class);
                    Bundle bundle=new Bundle();
                    if(totalBox.isChecked()){
                        bundle.putSerializable("list", ((Serializable) adpter.list));
                    }else{
                        ArrayList<HashMap<String ,String >> list=new ArrayList<>();
                        for (int i=0;i<adpter.isChoose.size();i++){
                            if(adpter.isChoose.get(i)){
                                list.add(adpter.list.get(i));
                            }
                        }
                        bundle.putSerializable("list", ((Serializable) list));
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                    v.setEnabled(true);
                    ((TextView) v).setTextColor(Color.WHITE);
                }
                break;
            case R.id.car_box_total:
                int s = adpter.isChoose.size();
                adpter.isChoose.clear();

                for (int i = 0; i < s; i++) {
                    if (totalBox.isChecked()) {
                        chooseNum = adpter.list.size();
                        totalnum += Double.valueOf(Integer.valueOf(adpter.Nums.get(i)) * Double.valueOf(adpter.list.get(i).get("money")));
                    } else {
                        chooseNum = 0;
                        totalnum = 0;
                    }
                    buy.setText("去结算(" + chooseNum + ")");
                    toatalNum.setText("￥" + String.format("%.2f", totalnum));
                    adpter.isChoose.add(totalBox.isChecked());
                }
                adpter.notifyDataSetChanged();
                break;

        }
    }

    @Override
    public void onRefresh() {
        getData();
    }

}
