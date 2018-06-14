package com.laomachaguan.Common;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/3.
 */
public class User_Detail2 extends AppCompatActivity implements View.OnClickListener {
    public static  final int IS_SELF=3;
    public static final  int NO_SELF=-1;
    private ProgressDialog progresDialog;
    private AlertDialog dialog;
    private ImageView head;
    private RelativeLayout headlayout;
    private static final String TAG = "User_Detail2";
    private EditText beizhu;
    private TextView name, sign, sex, phone, address, job, change_beizhu;
    private String ID;
    private boolean is1 = false, is2 = false;
    private boolean isDeleted = false;
    private HashMap<String, String> map;
    private TextView zhifu;
    private TextView sixin, guanzhu;
    private int screenWidth, screenHeight;
    private ValueAnimator va;
    private ZFAdapter zfAdapter;
    private long downtime;
//    private SlidingPaneLayout parent;
    private ImageView banner;
    private TextView job_tv;
    private TextView guanzhu_right;
    private LinearLayout beizhulayout;
    private int type;//1   入口：粉丝列表    2   入口：关注列表 3 rukou :个人信息
    private boolean isSelf=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.user_xiangxi_zi_liao2);
        initView();
        ID = getIntent().getStringExtra("id");
        isSelf=PreferenceUtil.getUserId(this).equals(ID)?true:false;
        type = getIntent().getIntExtra("self", 0);

//        if (ID == null && getIntent().getStringExtra("concern_id") != null) {
//            ID = getIntent().getStringExtra("concern_id");
//            Log.w(TAG, "onCreate: id:::::::::::::::::::::::" + ID);
//            zhifu.setVisibility(View.GONE);
//            guanzhu_right.setVisibility(View.GONE);
//            sixin.setVisibility(View.GONE);
//            beizhulayout.setVisibility(View.GONE);
//            if (!ID.equals(PreferenceUtil.getUserIncetance(this).getString("user_id", ""))) {
//                ID = getIntent().getStringExtra("concern_id");
//                guanzhu.setVisibility(View.VISIBLE);
//
//            } else {
//                guanzhu.setVisibility(View.GONE);
//                zhifu.setVisibility(View.GONE);
//                sixin.setVisibility(View.GONE);
//            }
//        }

        checkNetwork();
        getData();
        if(!getIntent().getBooleanExtra("add",true)||isSelf){
            // TODO: 2017/8/7 自己
            guanzhu.setVisibility(View.GONE);
        }else{
            // TODO: 2017/8/7 别人


        }
    }


    @Override
    public void onBackPressed() {
        findViewById(R.id.xiangxiziliao_back).performClick();
    }

    /**
     * 检查网络状态
     */
    private void checkNetwork() {
        if (!Network.HttpTest(this)) {
            Toast.makeText(this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT);
            return;
        }
    }

    /**
     * 获取详细信息
     */
    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.User_Info_Ip)
                            .tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute().body().string();
                    if (!data.equals("")) {
                        Log.w(TAG, "run: 获取的用户资料" + data);
                        map = AnalyticalJSON.getHashMap(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (map != null) {
                                    Glide.with(User_Detail2.this).load(map.get("user_image")).override(DimenUtils.dip2px(User_Detail2.this, 70), DimenUtils.dip2px(User_Detail2.this, 70))
                                            .centerCrop().placeholder(R.drawable.indra).into(head);
                                    name.setText(map.get("pet_name"));
                                    sign.setText(map.get("signature").equals("") ? "暂无个性签名" : map.get("signature"));
                                    phone.setText("".equals(map.get("phone")) ? "该用户尚未绑定手机号码" : map.get("phone"));
                                    sex.setText("1".equals(map.get("sex")) ? "男" : "女");


                                    if (!map.get("user_back").equals("")) {
                                        Glide.with(User_Detail2.this).load(map.get("user_back")).override(screenWidth, DimenUtils.dip2px(User_Detail2.this, 200))
                                                .centerCrop()
                                                .crossFade(1500)
                                                .into(banner);
                                        banner.setColorFilter(Color.parseColor("#60000000"));
                                    }

                                }
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化
     */
    private void initView() {

        beizhulayout = (LinearLayout) findViewById(R.id.beizhu_layout);
        beizhu = (EditText) findViewById(R.id.beizhu_tv);
        if (getIntent().getStringExtra("bz_name") != null&&!getIntent().getStringExtra("bz_name").equals("null")&&
        !getIntent().getStringExtra("bz_name").equals("")) {
            beizhu.setText(getIntent().getStringExtra("bz_name"));
        }
        change_beizhu = (TextView) findViewById(R.id.change);
        change_beizhu.setOnClickListener(this);
        guanzhu_right = (TextView) findViewById(R.id.guanzhu_right_top);
        guanzhu_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guanzhu.performClick();
            }
        });
        job_tv = (TextView) findViewById(R.id.job_tv);
        banner = (ImageView) findViewById(R.id.banner_detail);
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        sex = (TextView) findViewById(R.id.sex_tv);
        head = (ImageView) findViewById(R.id.head_imageview);
        job = (TextView) findViewById(R.id.job_tip);
        name = (TextView) findViewById(R.id.username);
        sign = (TextView) findViewById(R.id.sign);
        headlayout = (RelativeLayout) findViewById(R.id.head_layout);
        headlayout.setOnClickListener(this);
        phone = (TextView) findViewById(R.id.phone_tv);
        zhifu = (TextView) findViewById(R.id.zhifu_bnt);
//        parent = (SlidingPaneLayout) zhifu.getParent().getParent().getParent().getParent().getParent().getParent();
        sixin = (TextView) findViewById(R.id.sixin_bnt);
        guanzhu = (TextView) findViewById(R.id.guanzhu_bnt);

        zhifu.setOnClickListener(this);
        guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginUtil.checkLogin(User_Detail2.this)) {
                    return;
                }
                AlertDialog.Builder b = new AlertDialog.Builder(User_Detail2.this);
                final View view = LayoutInflater.from(User_Detail2.this).inflate(R.layout.guanzhu_dialog, null);
                b.setView(view);
                ((TextView) view.findViewById(R.id.tip)).setText("您需要发送验证申请，等待对方同意");
                final Dialog dialog = b.create();
                final EditText ed = (EditText) view.findViewById(R.id.guanzhu_msg);
                ed.setText("我是" + PreferenceUtil.getUserIncetance(User_Detail2.this).getString("pet_name", "") + ",请同意");
                view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    }
                });
                view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View vi) {
                        if (((EditText) view.findViewById(R.id.guanzhu_msg)).getText().toString().trim().equals("")) {
                            Toast.makeText(User_Detail2.this, "请填写认证信息", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject js = new JSONObject();
                        try {
                            js.put("user_friend", ID);
                            js.put("user_id", PreferenceUtil.getUserIncetance(User_Detail2.this).getString("user_id", ""));
                            js.put("m_id", Constants.M_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m=ApisSeUtil.i(js);
                        OkHttpUtils.post(Constants.ADD_Friend_Ip)
                                .tag(this)
                                .params("key",m.K())
                                .params("msg",m.M())

                                .execute(new AbsCallback<Object>() {
                                    @Override
                                    public Object parseNetworkResponse(Response response) throws Exception {
                                        return null;
                                    }

                                    @Override
                                    public void onSuccess(Object o, Call call, Response response) {
                                        try {
                                            String data = response.body().string();
                                            if (!data.equals("")) {
                                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                                if (map != null) {
                                                    if ("000".equals(map.get("code"))) {
                                                        Toast.makeText(User_Detail2.this, "好友请求发送成功，请等待对方同意", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }else if("001".equals(map.get("code"))){
                                                        ToastUtil.showToastShort("你们已经是好友啦");
                                                        dialog.dismiss();
                                                    }else {
                                                        dialog.dismiss();
                                                        Toast.makeText(User_Detail2.this, "您已对该用户发送过好友请求了", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(User_Detail2.this, "好友请求失败，请稍后尝试", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(User_Detail2.this, "好友请求失败，请稍后尝试", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(User_Detail2.this, "好友请求失败，请稍后尝试", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }


                                });
                    }
                });
                dialog.show();
                dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
                WindowManager.LayoutParams wl = dialog.getWindow().getAttributes();
                wl.width = User_Detail2.this.getResources().getDisplayMetrics().widthPixels;
                dialog.getWindow().setAttributes(wl);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    private void moveFllow(View v, int x, int y, float startX, float startY) {

        if (v.getY() >= DimenUtils.dip2px(this, 45) && v.getY() <= screenHeight - v.getHeight()) {
            v.setY(startY + y);
            if (v.getY() < DimenUtils.dip2px(this, 45)) {
                v.setY(DimenUtils.dip2px(this, 45));
            } else if (v.getY() > screenHeight - v.getHeight()) {
                v.setY(screenHeight - v.getHeight());
            }
        }

        if (v.getX() >= 0 && v.getX() <= (screenWidth - v.getWidth())) {
            v.setX(startX + x);
            if (v.getX() < DimenUtils.dip2px(this, 10)) {
                v.setX(DimenUtils.dip2px(this, 10));
            } else if (v.getX() > (screenWidth - v.getWidth())) {
                v.setX((screenWidth - v.getWidth()));
            }
        }
    }

    private void checkHozatal(final View v) {
        int left = (int) v.getX();
        Log.w(TAG, "onTouch: " + left);
        va = ValueAnimator.ofInt(60);
        va.setDuration(500);
        va.setInterpolator(new BounceInterpolator());
        if (left <= screenWidth / 2) {
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX(v.getX() - ((int) animation.getAnimatedValue()));
                    if (v.getX() <= 0) {
                        v.setX(0);
                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        } else {
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX(v.getX() + ((int) animation.getAnimatedValue()));
                    if (v.getX() >= screenWidth - v.getWidth()) {
                        v.setX(screenWidth - v.getWidth());
                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        }
        va.start();
    }

    /**
     * 修改昵称和签名
     */
    private void uploadNameandInfo() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.change_name_info, null);
        final EditText name1 = (EditText) view.findViewById(R.id.changeTitle);
        name1.setHint("请输入昵称");
        final EditText info = (EditText) view.findViewById(R.id.changeInfo);
        info.setHint("请输入签名");
        TextView commit = (TextView) view.findViewById(R.id.change_name_commit);
        ((TextView) view.findViewById(R.id.title)).setText("昵称");
        ((TextView) view.findViewById(R.id.info)).setText("签名");
        name1.setText(name.getText().toString());
        info.setText(sign.getText().toString());
        b.setView(view);
        dialog = b.create();
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNetwork();
                if (progresDialog == null) {
                    progresDialog = ProgressDialog.show(User_Detail2.this, null, "正在提交修改.....");
                } else {
                    progresDialog.setMessage("正在提交修改.....");
                    progresDialog.show();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject js = new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("pet_name", name1.getText().toString());
                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            String data = OkHttpUtils.post(Constants.User_info_xiugainc)
                                    .params("key",m.K())
                                    .params("msg",m.M()).execute().body().string();
                            if (!data.equals("")) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null && "000".equals(map.get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            name.setText(name1.getText().toString());
                                            SharedPreferences sp = PreferenceUtil.getUserIncetance(getApplicationContext());
                                            SharedPreferences.Editor ed = sp.edit();
                                            ed.putString("pet_name", name1.getText().toString());
                                            is1 = true;
                                            if (is2) {
                                                progresDialog.dismiss();
                                                dialog.dismiss();
                                                is1 = false;
                                                is2 = false;
                                            }
                                        }
                                    });
                                }
                            }
                            JSONObject js1 = new JSONObject();
                            try {
                                js1.put("m_id", Constants.M_id);
                                js1.put("signature", info.getText().toString());
                                js1.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m1=ApisSeUtil.i(js1);
                            String data1 = OkHttpUtils.post(Constants.SignChange)
                                    .params("key",m1.K())
                                    .params("msg",m1.M()).execute().body().string();
                            if (!data1.equals("")) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                                if (map != null && "000".equals(map.get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sign.setText(info.getText().toString());
                                            SharedPreferences sp = PreferenceUtil.getUserIncetance(getApplicationContext());
                                            SharedPreferences.Editor ed = sp.edit();
                                            ed.putString("sign", info.getText().toString());
                                            is2 = true;
                                            if (is1 && is2) {
                                                progresDialog.dismiss();
                                                dialog.dismiss();
                                                is1 = false;
                                                is2 = false;
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progresDialog.dismiss();
                                    Toast.makeText(User_Detail2.this, "修改失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        dialog.show();
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.xiangxiziliao_back:
                finish();
                break;
            case R.id.head_layout:
//            uploadNameandInfo();
                break;
            case R.id.sixin_bnt:
//                Intent intent1 = new Intent(this, SiXin.class);
//                intent1.putExtra("id", map.get("id"));
//                startActivity(intent1);
                break;

//            case R.id.zixun_bnt:
//                Intent intent2 = new Intent(User_Detail2.this, ZiXun_List.class);
//                intent2.putExtra("id", ID);
//                intent2.putExtra("isMe", false);
//                startActivity(intent2);

//                break;


        }
    }

    public class ZFAdapter extends BaseAdapter {
        public ArrayList<HashMap<String, String>> list;

        public void setList(ArrayList<HashMap<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            viewHolder holder;
            if (view == null) {
                holder = new viewHolder();
                holder.text = new TextView(User_Detail2.this);
                view = holder.text;
                holder.text.setTextSize(16);
                holder.text.setTextColor(getResources().getColor(R.color.main_color));
                holder.text.setGravity(Gravity.CENTER);
                holder.text.setPadding(0, DimenUtils.dip2px(User_Detail2.this, 8), 0, DimenUtils.dip2px(User_Detail2.this, 8));
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }
            holder.text.setText(list.get(position).get("name"));
            return view;
        }

        class viewHolder {
            TextView text;
        }
    }


}
