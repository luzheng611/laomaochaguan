package com.laomachaguan.View;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.Model_Order.Dingdan_commit;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

/**
 * 作者：因陀罗网 on 2017/9/8 10:21
 * 公司：成都因陀罗网络科技有限公司
 */

public class TagDialog {
    public static final String NORMAl = "normal";
    public static final String PINGYUAN = "pingtuan";
    private String type = NORMAl;

    public void setType(String type) {//设置是普通购物车还是直接拼团
        this.type = type;
    }


    private HashMap<String, String> data;
    private Activity activity;
    private ArrayList<String> Name1;
    private ArrayList<String> Name2;
    private ArrayList<HashMap<String, String>> Types;
    private int curPos1 = -1;
    int curPos2 = -1;
    private AlertDialog dialog;
    private View rootView;

    private SpannableStringBuilder ss;
    private String defaultString = "";//未选择任何标签时的价格区间等文字
    private int stock = 0;//总库存

    private int Num = 1;//默认数量
    private View view, pushCar;//加入购物车按钮
    private String id;//商品id;
    private TextView display;//详情页显示属性的textview

    private onTagDialogDismissListener onTagDialogDismissListener;

    public TagDialog(Activity activity, HashMap<String, String> map,TextView display) {
        super();
        WeakReference<Activity> w = new WeakReference<Activity>(activity);
        this.activity = w.get();
        this.data = map;
        this.display=display;
        initDatas(map);

    }
    public  void setOnTagDialogDismissListener(onTagDialogDismissListener listener){
        this.onTagDialogDismissListener=listener;
    }
    public  interface  onTagDialogDismissListener{
        void onDialogDissmiss(String one, String two, int num);
    }
    private void initDatas(final HashMap<String, String> map) {
        if (map != null && map.get("name1") != null && map.get("name2") != null && map.get("spec") != null) {
            Name1 = new ArrayList<>();//颜色
            ArrayList<HashMap<String, String>> tl1 = AnalyticalJSON.getList_zj(map.get("name1"));
            if (tl1 != null) {
                for (HashMap<String, String> mp : tl1) {
                    if (!mp.get("name").equals("0")) {
                        Name1.add(mp.get("name"));
                    }

                }
            }
            Name2 = new ArrayList<>();//尺寸
            ArrayList<HashMap<String, String>> tl2 = AnalyticalJSON.getList_zj(map.get("name2"));
            if (tl2 != null) {
                for (HashMap<String, String> mp : tl2) {
                    if (!mp.get("spec_name").equals("0")) {
                        Name2.add(mp.get("spec_name"));
                    }
                }
            }
            Types = AnalyticalJSON.getList_zj(map.get("spec"));
            ss = new SpannableStringBuilder();
            final ArrayList<Double> list = new ArrayList<>();//价格排序
            final ArrayList<Double> list2 = new ArrayList<>();//拼团价格排序
            for (HashMap<String, String> type : Types) {
                if (type.get("money") != null && !type.get("money").equals("")) {
                    list.add(Double.valueOf(type.get("money")));
                }
                if (type.get("price") != null && !type.get("price").equals("")) {
                    list2.add(Double.valueOf(type.get("price")));
                }
                if (type.get("stock") != null && !type.get("stock").equals("")) {
                    stock += Integer.valueOf(type.get("stock"));
                }
            }
            Collections.sort(list, new Comparator<Double>() {
                @Override
                public int compare(Double aDouble, Double t1) {
                    if (aDouble > t1) {
                        return 1;
                    } else if (aDouble == t1) {
                        return 0;
                    } else {
                        return -1;
                    }

                }
            });
            LogUtil.e("价格排序：：：" + list);
            Collections.sort(list2, new Comparator<Double>() {
                @Override
                public int compare(Double aDouble, Double t1) {
                    if (aDouble > t1) {
                        return 1;
                    } else if (aDouble == t1) {
                        return 0;
                    } else {
                        return -1;
                    }

                }
            });
            LogUtil.e("拼团价格排序：：：" + list2);
            if (list.size() != 0 && list2.size() != 0) {
                defaultString = "￥" + list.get(0) + "-" + list.get(list.size() - 1) + "\n"
                        + "拼团:" + list2.get(0) + "-" + list2.get(list.size() - 1) + "\n" +
                        "库存:" + stock + "\n"
                        + "请选择" + (curPos1 == -1 ? " 尺码" : "") + (curPos2 == -1 ? " 颜色" : "");
                ss.append(defaultString);
                ss.setSpan(new ForegroundColorSpan(Color.RED), 0, defaultString.indexOf("库存"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                defaultString = "暂无货源";
                ss.append(defaultString);
            }
            AlertDialog.Builder b = new AlertDialog.Builder(activity);
            rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_order_tag, null);
            ImageView image = (ImageView) rootView.findViewById(R.id.image);
            final TextView info = (TextView) rootView.findViewById(R.id.info);
            info.setText(ss);
            final TagFlowLayout name1 = (TagFlowLayout) rootView.findViewById(R.id.name1_layout);

            final TagFlowLayout name2 = (TagFlowLayout) rootView.findViewById(R.id.name2_layout);
            TextView jian = (TextView) rootView.findViewById(R.id.jian);
            TextView jia = (TextView) rootView.findViewById(R.id.jia);
            final EditText num = (EditText) rootView.findViewById(R.id.num);
            TextView commit = (TextView) rootView.findViewById(R.id.commit);
            Glide.with(activity).load(map.get("image1")).override(DimenUtils.dip2px(activity, 120), DimenUtils.dip2px(activity, 120))
                    .centerCrop().into(image);
            dialog = b.setView(rootView).create();
            final int dp5 = DimenUtils.dip2px(activity, 5);
            final int dp8 = DimenUtils.dip2px(activity, 8);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(R.color.transparent);

            WindowManager.LayoutParams wl = window.getAttributes();
            wl.windowAnimations = R.style.dialogWindowAnim;
            wl.width = activity.getResources().getDisplayMetrics().widthPixels;
            wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wl);

            rootView.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                        if(onTagDialogDismissListener!=null){
                            onTagDialogDismissListener.onDialogDissmiss(curPos1!=-1?Name2.get(curPos1):"无",curPos2!=-1?Name1.get(curPos2):"无",Num);
                        }
                }
            });
            jian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Num == 1) {
                        ToastUtil.showToastShort("商品数量最小为1");
                        return;
                    }
                    if (Num > 1) {
                        Num--;
                        num.setText(String.valueOf(Num));
                    } else {
                        num.setText("1");
                    }
                }
            });
            jia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Num++;
                    num.setText(String.valueOf(Num));
                }
            });
            num.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable != null && editable.length() != 0) {
                        LogUtil.e("最后的数量：" + editable.toString());
                        if (Integer.valueOf(editable.toString()) == 0) {
                            Num = 0;
                        } else {
                            Num = Integer.valueOf(editable.toString());
                        }

                    } else {
                        Num = 0;
                    }
                }
            });
            commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/9/8 加入购物车或者拼团直接进入订单
                    if(curPos2==-1){
                        ToastUtil.showToastShort("请选择颜色");
                        return;
                    }
                    if(curPos1==-1){
                        ToastUtil.showToastShort("请选择尺寸");
                        return;
                    }
                    if (type.equals(NORMAl)) {//加入购物车
                        LogUtil.e("加入购物车");
                        if (Network.HttpTest(activity)) {
                            if (LoginUtil.checkLogin(activity)) {
                                final LinearLayout car = (LinearLayout) view;
                                car.setEnabled(false);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String money = "";
                                        try {
                                            JSONObject js = new JSONObject();
                                            js.put("user_id", PreferenceUtil.getUserIncetance(activity)
                                                    .getString("user_id", ""));
                                            js.put("order_id", id);
                                            js.put("type", "1");
                                            for (HashMap<String, String> map : Types) {
                                                if (Name1.get(curPos2).equals(map.get("name")) && Name2.get(curPos1).equals(map.get("spec_name"))) {
                                                    js.put("spec_id", map.get("id"));

                                                    break;
                                                }
                                            }
                                            js.put("limited", "0");
                                            js.put("m_id", Constants.M_id);
                                            String data = OkHttpUtils.post(Constants.Order_add_car)
                                                    .tag("main").params("key", ApisSeUtil.getKey())
                                                    .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                            if (!TextUtils.isEmpty(data)) {
                                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);

                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (map != null) {
                                                            if ("000".equals(map.get("code")) || "002".equals(map.get("code"))) {

                                                                car.findViewById(R.id.order_detail_car_img).setVisibility(View.GONE);
                                                                ((TextView) car.findViewById(R.id.order_detail_car_text)).setText("已放入茶壶");
                                                                ((TextView) pushCar).setText("已放入茶壶");
                                                                pushCar.setEnabled(false);
                                                                Intent intent = new Intent("car");
                                                                activity.sendBroadcast(intent);
                                                            }
                                                        } else {

                                                            car.setEnabled(true);
                                                            Toast.makeText(activity, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {

                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        car.setEnabled(true);
                                                        Toast.makeText(activity, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        } catch (Exception e) {

                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    car.setEnabled(true);
                                                }
                                            });

                                        }
                                    }
                                }).start();
                            }
                        }

                    } else if (type.equals(PINGYUAN)) {//拼团进入订单
                        LogUtil.e("拼团订单");

                        Intent intent = new Intent(activity, Dingdan_commit.class);
                        ArrayList<HashMap<String, String>> list = new ArrayList<>();
                        for (HashMap<String, String> ma : Types) {
                            if (ma.get("name").equals(Name1.get(curPos2)) && ma.get("spec_name").equals(Name2.get(curPos1))) {
                                HashMap<String, String> m = new HashMap<>();
                                m.put("title", map.get("title"));
                                m.put("spec",AnalyticalJSON.hashMapToJson(ma));
                                m.put("num", String.valueOf(Num));
                                m.put("money", ma.get("price"));
                                m.put("cost", ma.get("money"));
                                m.put("good_id", ma.get("goods_id"));
                                m.put("name", ma.get("name"));
                                m.put("spec_name", ma.get("spec_name"));
                                m.put("spec_id", ma.get("id"));
                                list.add(m);
                                LogUtil.e("颜色：：" + ma.get("name") + "  尺码:" + ma.get("spec_name"));
                                intent.putExtra("list", list);
                                intent.putExtra("KT", true);
                                activity.startActivity(intent);
                                break;
                            }
                        }

                    }
                    dialog.dismiss();
                }
            });
            name1.setAdapter(new TagAdapter<String>(Name2) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    final TextView textView = (TextView) LayoutInflater.from(activity).inflate(R.layout.order_tag_tv, name1, false);
                    textView.setPadding(dp8, dp5, dp8, dp5);
                    textView.setText(s);
                    return textView;
                }


            });
            name1.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                @Override
                public void onSelected(Set<Integer> selectPosSet) {
                    if (selectPosSet.iterator().hasNext()) {
                        curPos1 = selectPosSet.iterator().next();
                        LogUtil.e("尺码选择++" + Name2.get(curPos1) + "   当前位置：：；" + curPos1);
                    } else {
                        curPos1 = -1;
                        LogUtil.e("取消尺码选择  " + curPos1);
                    }
                    changeInfo(info, list, list2, name1, name2, name1.toString());
                }


            });
//            if (Name2.size() != 0) {
//                name1.getAdapter().setSelectedList(0);
//                curPos1 = 0;
//            }默认选择

            name2.setAdapter(new TagAdapter<String>(Name1) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    final TextView textView = (TextView) LayoutInflater.from(activity).inflate(R.layout.order_tag_tv, name2, false);
                    textView.setPadding(dp8, dp5, dp8, dp5);
                    textView.setText(s);
                    return textView;
                }


            });
            name2.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                @Override
                public void onSelected(Set<Integer> selectPosSet) {
                    if (selectPosSet.iterator().hasNext()) {
                        curPos2 = selectPosSet.iterator().next();
                        LogUtil.e("颜色选择++" + Name1.get(curPos2) + "  当前位置：；" + curPos2);
                    } else {
                        curPos2 = -1;
                        LogUtil.e("取消颜色选择  " + curPos2);
                    }
                    changeInfo(info, list, list2, name1, name2, name2.toString());

                }
            });
//            if (Name1.size() != 0) {
//                name2.getAdapter().setSelectedList(0);
//                curPos2 = 0;
//            }默认选择
        } else {
            LogUtil.e("数据未加载完成");
            display.setVisibility(View.GONE);
        }

    }

    private void changeInfo(TextView info, ArrayList<Double> list, ArrayList<Double> list2, TagFlowLayout name1, TagFlowLayout name2, String oneOrtwo) {

        String string = "";
        if (curPos1 != -1 && curPos2 != -1) {
            LogUtil.e("同时选择");
            int stock = 0;
            ArrayList<String> huisePos = new ArrayList<>();
            ArrayList<String> huisePos2 = new ArrayList<>();
            huisePos.addAll(Name1);
            huisePos2.addAll(Name2);
            for (int i = 0; i < Types.size(); i++) {
                HashMap<String, String> type = Types.get(i);
//
                if(name1.toString().equals(oneOrtwo)){
                    if (type.get("spec_name").equals(Name2.get(curPos1))) {
                        String name = type.get("name");
                        if (!name.equals("0")) {
                            if (type.get("stock").equals("0")) {
                                ((TextView) name2.getChildAt(Name1.indexOf(name)).findViewById(R.id.text)).setTextColor(Color.parseColor("#bbbbbb"));
                                ((TagView) name2.getChildAt(Name1.indexOf(name))).setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        return true;
                                    }
                                });
                            } else {
                                ((TextView) name2.getChildAt(Name1.indexOf(name)).findViewById(R.id.text)).setTextColor(Color.BLACK);
                                ((TagView) name2.getChildAt(Name1.indexOf(name))).setOnTouchListener(null);
                                for (int j = 0; j < Name1.size(); j++) {
                                    if (Name1.contains(name)) {
                                        huisePos.remove(name);
                                        break;
                                    }
                                }
                                LogUtil.e(Name1 + "\n需要置灰的：" + huisePos);
                                for (String s : huisePos) {
                                    int l = Name1.indexOf(s);
                                    ((TextView) name2.getChildAt(l).findViewById(R.id.text)).setTextColor(Color.parseColor("#bbbbbb"));
                                    ((TagView) name2.getChildAt(l)).setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View view, MotionEvent motionEvent) {
                                            return true;
                                        }
                                    });
                                }

                            }
                        }


                    }
                }else if(name2.toString().equals(oneOrtwo)){
                    if (type.get("name").equals(Name1.get(curPos2))) {
                        String name = type.get("spec_name");
                        if (!name.equals("0")) {
                            if (type.get("stock").equals("0")) {
                                ((TextView) name1.getChildAt(Name2.indexOf(name)).findViewById(R.id.text)).setTextColor(Color.parseColor("#bbbbbb"));
                                ((TagView) name1.getChildAt(Name2.indexOf(name))).setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                        return true;
                                    }
                                });
                            } else {
                                ((TextView) name1.getChildAt(Name2.indexOf(name)).findViewById(R.id.text)).setTextColor(Color.BLACK);
                                ((TagView) name1.getChildAt(Name2.indexOf(name))).setOnTouchListener(null);

                                for (int j = 0; j < Name2.size(); j++) {
                                    if (Name2.contains(name)) {
                                        huisePos2.remove(name);
                                        break;
                                    }
                                }
                                LogUtil.e(Name2 + "\n需要置灰的：" + huisePos2);
                                for (String s : huisePos2) {
                                    int l = Name2.indexOf(s);
                                    ((TextView) name1.getChildAt(l).findViewById(R.id.text)).setTextColor(Color.parseColor("#bbbbbb"));
                                    ((TagView) name1.getChildAt(l)).setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View view, MotionEvent motionEvent) {
                                            return true;
                                        }
                                    });
                                }
                            }
                        }

                    }

            }


                if (curPos2 != -1 && curPos1 != -1) {
                    //改变基本信息
                    if (type.get("name").equals(Name1.get(curPos2)) && type.get("spec_name").equals(Name2.get(curPos1))) {
                        LogUtil.e("改变基本信息");
                        stock = Integer.valueOf(type.get("stock"));
                        string = "￥" + type.get("money") + "\n"
                                + "拼团:" + type.get("price") + "\n" +
                                "库存:" + stock + "\n"
                                + "已选择" + " \"" + Name2.get(curPos1) + "\" \"" + Name1.get(curPos2) + "\"";
                        SpannableString ss = new SpannableString(string);
                        ss.setSpan(new ForegroundColorSpan(Color.RED), 0, string.indexOf("库存"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        info.setText(ss);
                    }
                }

//                }

            }


        } else {
            defaultString = "￥" + list.get(0) + "-" + list.get(list.size() - 1) + "\n"
                    + "拼团:" + list2.get(0) + "-" + list2.get(list.size() - 1) + "\n" +
                    "库存:" + stock + "\n"
                    + "请选择" + (curPos1 == -1 ? " 尺码" : "") + (curPos2 == -1 ? " 颜色" : "");
            SpannableString ss = new SpannableString(defaultString);
            ss.setSpan(new ForegroundColorSpan(Color.RED), 0, defaultString.indexOf("库存"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            info.setText(ss);
            if (curPos1 != -1 && curPos2 == -1) {//只选择了尺码，暂未选择颜色
                LogUtil.e("只选择了尺码，暂未选择颜色");
                ArrayList<String> huisePos = new ArrayList<>();
                huisePos.addAll(Name1);
                for (int i = 0; i < Types.size(); i++) {
                    HashMap<String, String> type = Types.get(i);
                    if (type.get("spec_name") != null) {
                        if (name1.toString().equals(oneOrtwo)) {//操作尺码
                            if (type.get("spec_name").equals(Name2.get(curPos1))) {
                                String name = type.get("name");
                                if (!name.equals("0")) {
                                    if (type.get("stock").equals("0")) {
                                        ((TextView) name2.getChildAt(Name1.indexOf(name)).findViewById(R.id.text)).setTextColor(Color.parseColor("#bbbbbb"));
                                        ((TagView) name2.getChildAt(Name1.indexOf(name))).setOnTouchListener(new View.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                                return true;
                                            }
                                        });
                                    } else {
                                        ((TextView) name2.getChildAt(Name1.indexOf(name)).findViewById(R.id.text)).setTextColor(Color.BLACK);
                                        ((TagView) name2.getChildAt(Name1.indexOf(name))).setOnTouchListener(null);
                                        for (int j = 0; j < Name1.size(); j++) {
                                            if (Name1.contains(name)) {
                                                huisePos.remove(name);
                                                break;
                                            }
                                        }
                                        LogUtil.e(Name1 + "\n需要置灰的：" + huisePos);
                                        for (String s : huisePos) {
                                            int l = Name1.indexOf(s);
                                            ((TextView) name2.getChildAt(l).findViewById(R.id.text)).setTextColor(Color.parseColor("#bbbbbb"));
                                            ((TagView) name2.getChildAt(l)).setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                                    return true;
                                                }
                                            });
                                        }

                                    }
                                }


                            }
                        } else if (name2.toString().equals(oneOrtwo)) {//操作颜色
                                    for(String s:Name2){
                                        if(!Name2.get(curPos1).equals(s)){
                                            ((TextView) name1.getChildAt(Name2.indexOf(s)).findViewById(R.id.text)).setTextColor(Color.BLACK);
                                            ((TagView) name1.getChildAt(Name2.indexOf(s))).setOnTouchListener(null);
                                        }
                                    }
                        }

                    }

                }
            } else if (curPos1 == -1 && curPos2 != -1) {//只选择了颜色，暂未选择尺码
                LogUtil.e("只选择了颜色，暂未选择尺码");
                ArrayList<String> huisePos = new ArrayList<>();
                huisePos.addAll(Name2);
                for (int i = 0; i < Types.size(); i++) {
                    HashMap<String, String> type = Types.get(i);
                    if (type.get("name") != null) {
                        if(name1.toString().equals(oneOrtwo)){
                            for(String s:Name1){
                                if(!Name1.get(curPos2).equals(s)){
                                    int pos=Name1.indexOf(s);
                                    LogUtil.e("s:::"+s+"    位置："+pos+"  \nName1::"+Name1);
                                    TagView view= (TagView) name2.getChildAt(pos);
                                    TextView t= (TextView) view.findViewById(R.id.text);
                                    t.setTextColor(Color.BLACK);
                                    view.setOnTouchListener(null);
                                }
                            }
                        }else if(name2.toString().equals(oneOrtwo)){
                            if (type.get("name").equals(Name1.get(curPos2))) {
                                String name = type.get("spec_name");
                                if (!name.equals("0")) {
                                    if (type.get("stock").equals("0")) {
                                        ((TextView) name1.getChildAt(Name2.indexOf(name)).findViewById(R.id.text)).setTextColor(Color.parseColor("#bbbbbb"));
                                        ((TagView) name1.getChildAt(Name2.indexOf(name))).setOnTouchListener(new View.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                                return true;
                                            }
                                        });
                                    } else {
                                        ((TextView) name1.getChildAt(Name2.indexOf(name)).findViewById(R.id.text)).setTextColor(Color.BLACK);
                                        ((TagView) name1.getChildAt(Name2.indexOf(name))).setOnTouchListener(null);

                                        for (int j = 0; j < Name2.size(); j++) {
                                            if (Name2.contains(name)) {
                                                huisePos.remove(name);
                                                break;
                                            }
                                        }
                                        LogUtil.e(Name2 + "\n需要置灰的：" + huisePos);
                                        for (String s : huisePos) {
                                            int l = Name2.indexOf(s);
                                            ((TextView) name1.getChildAt(l).findViewById(R.id.text)).setTextColor(Color.parseColor("#bbbbbb"));
                                            ((TagView) name1.getChildAt(l)).setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                                    return true;
                                                }
                                            });
                                        }
                                    }
                                }

                            }
                        }

                    }
                }
            } else if (curPos2 == -1 && curPos1 == -1) {
                for (int i = 0; i < Name1.size(); i++) {
                    ((TextView) name2.getChildAt(i).findViewById(R.id.text)).setTextColor(Color.BLACK);
                    ((TagView) name2.getChildAt(i)).setOnTouchListener(null);
                }
                for (int i = 0; i < Name2.size(); i++) {
                    ((TextView) name1.getChildAt(i).findViewById(R.id.text)).setTextColor(Color.BLACK);
                    ((TagView) name1.getChildAt(i)).setOnTouchListener(null);
                }
            }
        }
    }

    public void show(String type) {

        if (dialog != null && Name1 != null && Name2 != null && Types != null) {
            LogUtil.e("打开标签选择窗口");
            this.type = type;
            dialog.show();
        } else {
            HashMap<String, String> m=data;
                if(m!=null){
                    Intent intent = new Intent(activity, Dingdan_commit.class);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("title", m.get("title"));
                    map.put("num", "1");
                    map.put("money", m.get("price"));
                    map.put("cost", m.get("money"));
                    map.put("good_id", m.get("id"));
                    list.add(map);
                    intent.putExtra("list", list);
                    intent.putExtra("KT", true);
                    activity.startActivity(intent);
                }else{
                    ToastUtil.showToastShort("数据加载中，请稍等");
                }
        }
    }

    public void show(String type, View view, final String id, final View pushCar) {
        this.view = view;
        this.id = id;
        this.pushCar = pushCar;
        if (dialog != null && Name1 != null && Name2 != null && Types != null) {
            LogUtil.e("打开标签选择窗口");
            this.type = type;
            dialog.show();
        } else {
            LogUtil.e("加入购物车");
            if (Network.HttpTest(activity)) {
                if (LoginUtil.checkLogin(activity)) {
                    final LinearLayout car = (LinearLayout) view;
                    car.setEnabled(false);
                    pushCar.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String money = "";
                            try {
                                JSONObject js = new JSONObject();
                                js.put("user_id", PreferenceUtil.getUserIncetance(activity)
                                        .getString("user_id", ""));
                                js.put("order_id", id);
                                js.put("type", "1");

                                js.put("limited", "0");
                                js.put("m_id", Constants.M_id);
                                String data = OkHttpUtils.post(Constants.Order_add_car)
                                        .tag("main").params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                                if (!TextUtils.isEmpty(data)) {
                                    final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);

                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (map != null) {
                                                if ("000".equals(map.get("code")) || "002".equals(map.get("code"))) {

                                                    car.findViewById(R.id.order_detail_car_img).setVisibility(View.GONE);
                                                    ((TextView) car.findViewById(R.id.order_detail_car_text)).setText("已放入茶壶");
                                                    ((TextView) pushCar).setText("已放入茶壶");
                                                    pushCar.setEnabled(false);
                                                    Intent intent = new Intent("car");
                                                    activity.sendBroadcast(intent);
                                                }
                                            } else {

                                                car.setEnabled(true);
                                                pushCar.setEnabled(true);
                                                Toast.makeText(activity, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {

                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            car.setEnabled(true);
                                            pushCar.setEnabled(true);
                                            Toast.makeText(activity, "系统繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            } catch (Exception e) {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        car.setEnabled(true);
                                    }
                                });

                            }
                        }
                    }).start();
                }
            }
        }
    }
}
