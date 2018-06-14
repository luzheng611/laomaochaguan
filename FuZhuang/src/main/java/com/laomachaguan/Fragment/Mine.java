package com.laomachaguan.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.laomachaguan.Common.Activity_ShouCang;
import com.laomachaguan.Common.BangZhu;
import com.laomachaguan.Common.MainActivity;
import com.laomachaguan.Common.Mine_JiFen;
import com.laomachaguan.Common.Mine_gerenziliao;
import com.laomachaguan.Common.Mine_pintuan;
import com.laomachaguan.Common.Promo;
import com.laomachaguan.Common.Sign;
import com.laomachaguan.Common.TG_Mine;
import com.laomachaguan.Common.User_List;
import com.laomachaguan.Common.gerenshezhi;
import com.laomachaguan.R;
import com.laomachaguan.Utils.ACache;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PhotoUtil;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

//import com.hyphenate.EMCallBack;
//import com.hyphenate.chat.EMClient;


/**
 * Created by Administrator on 2016/5/31.
 */
public class Mine extends Fragment implements View.OnClickListener {
    private static final String TAG = "Mine";
    private static final int CHOOSEPICTUE = 2;//相册
    private static final int TAKEPICTURE = 1;//相机

    private Uri pictureUri = null;
    private AlertDialog dialog;
    public ImageView head;
    public SharedPreferences sp;
    public ACache aCache;
    private int screenWidth;
    public String path;
    private File Headfile;
    private int screenHeight;


    private TextView petname;

    private boolean needToGetInfo;


    private TextView sign;
    private View view;
    private MineManager mineManager;
    private RecyclerView recyclerView;

    private boolean isAgreeed=false;//是否同意隐私政策
    private BroadcastReceiver userReseiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateInfo();
            Log.w(TAG, "onReceive: 状态改变");
            checkStatus();
            mineManager.initMine();
        }
    };

    private void checkStatus() {
        if (PreferenceUtil.getUserIncetance(getActivity()).getString("role", "").equals("3")) {
            view.findViewById(R.id.role).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.role).setVisibility(View.GONE);
        }
        if (PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", "").equals("")) {
            petname.setText("请登录");
            sign.setText("暂无个性签名");
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mine, container, false);
        IntentFilter intentFilter = new IntentFilter("Mine");
        getActivity().registerReceiver(userReseiver, intentFilter);
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        LogUtil.e("当前用户: =-=-=-=-=-=-=-=-=" + sp.getString("user_id", ""));
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        aCache = ACache.get(mApplication.getInstance());

        isAgreeed=sp.getString("agree_status","").equals("0")||sp.getString("agree_status","").equals("")?false:true;
        head = (ImageView) view.findViewById(R.id.mine_head);
        petname = (TextView) view.findViewById(R.id.mine_petName);


        Glide.with(this).load(R.drawable.mine_bg).override(screenWidth, screenWidth / 2)
                .fitCenter().into((ImageView) view.findViewById(R.id.mine_gerenbeijing));
        ((ImageView) view.findViewById(R.id.mine_gerenbeijing)).setColorFilter(Color.parseColor("#40000000"));
        sign = (TextView) view.findViewById(R.id.mine_sign);
        sign.setOnClickListener(this);
        SetHead();
        if (!sp.getString("pet_name", "").equals("")) {
            petname.setText(sp.getString("pet_name", ""));
        }
        if (!sp.getString("signature", "").equals("")) {
            sign.setText(sp.getString("signature", ""));
        } else {
            sign.setText("暂无个性签名");
        }
        checkStatus();

        head.setOnClickListener(this);

        view.findViewById(R.id.mine_user).setOnClickListener(this);
        view.findViewById(R.id.mine_qiehuanzhanghao).setOnClickListener(this);
        view.findViewById(R.id.mine_jifen).setOnClickListener(this);
        view.findViewById(R.id.mine_dingdan).setOnClickListener(this);
        view.findViewById(R.id.mine_bangzhu).setOnClickListener(this);
        view.findViewById(R.id.mine_gerenshezhi).setOnClickListener(this);
        view.findViewById(R.id.mine_shoucang).setOnClickListener(this);
        view.findViewById(R.id.mine_pintuan).setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        mineManager = new MineManager(getActivity(), recyclerView);
        mineManager.setOnitemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View v, int i) {
                HashMap<String, Object> map = mineManager.getMaps().get(i);
                LogUtil.e("点击：：："+map.get(MineManager.text));
                switch (map.get(MineManager.text).toString()) {

                    case "用户":
                        view.findViewById(R.id.mine_user).performClick();
                        break;
                    case "切换账号":
                        view.findViewById(R.id.mine_qiehuanzhanghao).performClick();
                        break;
                    case "积分":
                        view.findViewById(R.id.mine_jifen).performClick();
                        break;
                    case "账单":
                        view.findViewById(R.id.mine_dingdan).performClick();
                        break;
                    case "帮助":
                        view.findViewById(R.id.mine_bangzhu).performClick();

                        break;
                    case "设置":
                        view.findViewById(R.id.mine_gerenshezhi).performClick();
                        break;
                    case "收藏":
                        view.findViewById(R.id.mine_shoucang).performClick();
                        break;
                    case "拼团":
                        view.findViewById(R.id.mine_pintuan).performClick();
                        break;
                    case "通知":
                        if(LoginUtil.checkLogin(getActivity())){
                            Intent tongzhi=new Intent(getActivity(),TongZhi.class);
                            startActivity(tongzhi);
                        }
                        break;
                    case "消息":
                        if(LoginUtil.checkLogin(getActivity())){
                            Intent tongzhi=new Intent(getActivity(),ChatMessage.class);
                            startActivity(tongzhi);
                        }

                        break;
                    case "免费开店":
                        getProl();//获取用户协议并提交开店申请
                        break;
                    case "我的店铺":
                        if(LoginUtil.checkLogin(getActivity())){
                            Intent intent=new Intent(getActivity(),Mine_Store.class);
                            startActivity(intent);
                        }
                        break;
                    case "好友":
                        if(LoginUtil.checkLogin(getActivity())){
                            Intent tongzhi=new Intent(getActivity(),Fans_List.class);
                            startActivity(tongzhi);
                        }
                        break;
                    case "填写邀请码":
                        if (LoginUtil.checkLogin(getActivity())) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), Promo.class);
                            startActivity(intent);
                        }
                        break;

                }
            }
        });
        needToGetInfo = false;
        updateInfo();

        return view;
    }

    private void getProl() {
        if(!LoginUtil.checkLogin(getActivity())){
            return;
        }
        if(Network.HttpTest(getActivity())){
            JSONObject js=new JSONObject();
            try {
                js.put("m_id",Constants.M_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m=ApisSeUtil.i(js);
            OkHttpUtils.post(Constants.Store_Prol)
                    .params("key",m.K())
                    .params("msg",m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String,String> m=AnalyticalJSON.getHashMap(s);
                            if(m!=null){
                                String prol=m.get("agreements");
                                if (prol != null && !prol.equals("")) {
                                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.web_confirm_dialog, null);
                                    final WebView web = (WebView) view.findViewById(R.id.web);
                                    TextView cancle = (TextView) view.findViewById(R.id.cancle);
                                    cancle.setText(mApplication.ST("点错了"));
                                    final TextView baoming = (TextView) view.findViewById(R.id.baoming);
                                    baoming.setEnabled(false);
                                    final CountDownTimer cdt = new CountDownTimer(10000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            baoming.setText(mApplication.ST("请阅读开店须知(" + millisUntilFinished / 1000 + "秒)"));
                                        }

                                        @Override
                                        public void onFinish() {
                                            baoming.setText(mApplication.ST("同意"));
                                            baoming.setEnabled(true);
                                        }
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setView(view);
                                    final AlertDialog dialog = builder.create();
                                    web.setWebViewClient(new WebViewClient(){
                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            super.onPageFinished(view, url);
                                            dialog.show();
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                    web.loadDataWithBaseURL("", prol
                                            , "text/html", "UTF-8", null);
//                    Api.getUserNeedKnow(this,web);

                                    cancle.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            web.destroy();
                                            cdt.cancel();
                                            dialog.dismiss();
                                        }
                                    });
                                    baoming.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            showKaiDianDialog();

                                        }
                                    });
                                    cdt.start();


                                }else{
                                    ProgressUtil.dismiss();
                                    showKaiDianDialog();
                                }
                            }

                        }

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            ProgressUtil.show(getActivity(),"","请稍等");
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            ToastUtil.showToastShort("获取用户协议失败");
                        }

                        @Override
                        public void onAfter(@Nullable String s, @Nullable Exception e) {
                            super.onAfter(s, e);

                        }
                    });
        }
    }

    private void showKaiDianDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_tixian, null);
        final TextView now = (TextView) view1.findViewById(R.id.credit_now);
        final TextView exp = (TextView) view1.findViewById(R.id.credit_exp);
        final EditText needTixian = (EditText) view1.findViewById(R.id.credit_needtotixian);
        final EditText aliId = (EditText) view1.findViewById(R.id.alipayId);
        final EditText aliId2 = (EditText) view1.findViewById(R.id.alipay_confirm);
        final EditText aliName = (EditText) view1.findViewById(R.id.alipay_name);
        TextView cancle = (TextView) view1.findViewById(R.id.cancle);
        TextView commit = (TextView) view1.findViewById(R.id.commit);
        commit.setText("申请开店");
        ((TextView) view1.findViewById(R.id.title)).setText("开店申请");
        now.setVisibility(View.GONE);
        aliId2.setVisibility(View.GONE);
        aliId.setVisibility(View.GONE);
        needTixian.setInputType(InputType.TYPE_CLASS_TEXT);
        needTixian.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        needTixian.setHint("请输入店铺名称(提交后无法更改)");
        aliName.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        aliName.setHint("给朋友的返现百分比");
        b.setView(view1);

        exp.setText("免费开店后，分享自己的商铺给朋友,朋友通过您分享的链接购买商品后即可得到一定的积分奖励");
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
                    if(needTixian.getText().toString().trim().equals("")){
                        ToastUtil.showToastShort("请输入您的店铺名称");
                        return;
                    }
                    if(aliName.getText().toString().trim().equals("")){
                        ToastUtil.showToastShort("请输入小于100的积分返现百分比");
                        return;
                    }
                    if(aliName.getText().toString().trim()!=null&&Integer.valueOf(aliName.getText().toString().trim())>100){
                        ToastUtil.showToastShort("请输入小于100的积分返现百分比");
                        return;
                    }
                if (Network.HttpTest(getActivity())) {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
                        js.put("title", needTixian.getText().toString().trim());
                        js.put("redeem", aliName.getText().toString().trim());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.e("开店申请："+js);
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    OkHttpUtils.post(Constants.Store_Post_Apply)
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
                                        ToastUtil.showToastShort("恭喜您开店成功");
                                        dialog.dismiss();
                                        SharedPreferences sp=PreferenceUtil.getUserIncetance(getActivity());
                                        SharedPreferences.Editor ed=sp.edit();
                                        ed.putString("role","2");
                                        ed.commit();
                                        mineManager.initMine();
                                    }else if("003".equals(map.get("code"))){
                                        ToastUtil.showToastShort("店铺名称已存在");
                                    }else if("002".equals(map.get("code"))){
                                        ToastUtil.showToastShort("您已开过店铺了");
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
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(userReseiver);
        mineManager.saveMySetting();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && this != null) {
            updateInfo();


//            MediaPlayer mediaPlayer=new MediaPlayer();
//            try {
//                mediaPlayer.setDataSource(getActivity(),Uri.parse("Android.resource://" + getActivity().getPackageName() + "/" +R.raw.dadun)) ;
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_DTMF);
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

    }

    Handler handler = new Handler();

    private void updateInfo() {
        final Intent intent = new Intent();
        if (TextUtils.isEmpty(sp.getString("user_id", ""))) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("user_id", PreferenceUtil.getUserIncetance(getActivity()).getString("user_id", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.User_Info_Ip).tag(TAG)
                            .params("key", m.K()).params("msg", m.M())

                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)&&!data.equals("null")) {
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                        final SharedPreferences.Editor ed = sp.edit();
                        if (handler != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (map != null) {
                                        if (!"".equals(map.get("user_image"))) {
                                            ed.putString("head_url", map.get("user_image"));
                                            Glide.with(Mine.this).load(map.get("user_image")).asBitmap().override(screenWidth / 4, screenWidth / 4).into(new BitmapImageViewTarget(head) {
                                                @Override
                                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    aCache.put("head" + sp.getString("user_id", ""), resource);
                                                    head.setImageBitmap(resource);
                                                }
                                            });
                                        }
                                        if (!"".equals(map.get("sex"))) {
                                            ed.putString("sex", map.get("sex"));
                                        }
                                        if (!"".equals(map.get("role"))) {
                                            ed.putString("role", map.get("role"));
                                        }else{
                                            ed.putString("role","1");
                                        }
                                        LogUtil.e("登录类型：："+map.get("role"));
                                        if (!"".equals(map.get("pet_name"))) {
                                            ed.putString("pet_name", map.get("pet_name"));
                                            petname.setText(map.get("pet_name"));
                                        }

                                        if (!"".equals(map.get("signature"))) {
                                            ed.putString("signature", map.get("signature"));
                                            sign.setText(map.get("signature"));
                                        }

                                        if (needToGetInfo) {
                                            if ("".equals(map.get("pet_name")) || "".equals(map.get("sex"))) {
                                                intent.setClass(getActivity(), Mine_gerenziliao.class);
                                                startActivity(intent);
                                            }
                                        }
                                        if ("0".equals(map.get("agree_status"))) {
                                            isAgreeed = false;
                                            ed.putString("agree_status", "0");//是否同意隐私政策  0未同意  1为同意
                                            ed.commit();
                                        } else {
                                            isAgreeed = true;
                                            ed.putString("agree_status", map.get("agree_status"));//是否同意隐私政策  0未同意  1为同意
                                            ed.commit();
                                        }
                                        if (!isAgreeed) {
                                            getUserProl();
                                        }
                                        needToGetInfo = true;
                                    }
                                    ed.commit();
                                    checkStatus();
                                    mineManager.initMine();
                                    long creat = TimeUtils.dataOne(map.get("creat_time"));
                                    LogUtil.e(TimeUtils.getStrTime(System.currentTimeMillis() + "") + "        " + map.get("creat_time"));

                                    if ("".equals(map.get("promo")) && System.currentTimeMillis() - creat < 3 * 24 * 60 * 60 * 1000) {
                                        LogUtil.e("显示邀请码");
                                        for(HashMap m:mineManager.getMaps()){
                                            if(m.get(MineManager.text).equals("填写邀请码")){
                                                return;
                                            }
                                        }
                                        HashMap<String, Object> m = new HashMap<String, Object>();
                                        m.put(MineManager.text, "填写邀请码");
                                        m.put(MineManager.img, R.drawable.yaoqing_icon);
                                        mineManager.getMaps().add(0,m);
                                        mineManager.notifyDataChanged();
                                    }else{
                                        for(HashMap m:mineManager.getMaps()){
                                            if(m.get(MineManager.text).equals("填写邀请码")){
                                                mineManager.getMaps().remove(m);
                                                mineManager.notifyDataChanged();
                                                return;
                                            }
                                        }
                                    }

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
    public void getUserProl() {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.little_yhxy__IP).tag(TAG).params("key", m.K()).params("msg", m.M())
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
                                final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                getActivity(). runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (map != null) {
                                            View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_confirm_dialog, null);
                                            final WebView web = (WebView) view.findViewById(R.id.web);
                                            TextView cancle = (TextView) view.findViewById(R.id.cancle);
                                            cancle.setText(mApplication.ST("不同意"));
                                            final TextView baoming = (TextView) view.findViewById(R.id.baoming);
                                            baoming.setEnabled(false);
                                            final CountDownTimer cdt = new CountDownTimer(10000, 1000) {
                                                @Override
                                                public void onTick(long millisUntilFinished) {
                                                    baoming.setText(mApplication.ST("请阅读隐私政策(" + millisUntilFinished / 1000 + "秒)"));
                                                }

                                                @Override
                                                public void onFinish() {
                                                    baoming.setText(mApplication.ST("同意"));
                                                    baoming.setEnabled(true);
                                                }
                                            };
                                            web.loadDataWithBaseURL("", map.get("privacy")
                                                    , "text/html", "UTF-8", null);
//
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setView(view);
                                            final AlertDialog dialog = builder.create();
                                            cancle.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    web.destroy();
                                                    cdt.cancel();
                                                    dialog.dismiss();
                                                    if(MainActivity.activity!=null){
                                                        MainActivity.activity.finish();
                                                        MainActivity.activity=null;
                                                    }
                                                    System.exit(0);
                                                }
                                            });
                                            baoming.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    agreeSecret();
                                                }
                                            });
                                            cdt.start();
                                            builder.setCancelable(false);
                                            web.setWebViewClient(new WebViewClient(){
                                                @Override
                                                public void onPageFinished(WebView view, String url) {
                                                    super.onPageFinished(view, url);
                                                    dialog.show();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                });
    }
    //同意隐私政策
    private void agreeSecret() {
        if (Network.HttpTest(getActivity())) {
            if (new LoginUtil().checkLogin(getActivity())) {
                JSONObject js = new JSONObject();
                try {
                    js.put("user_id", PreferenceUtil.getUserId(getActivity()));
                    js.put("m_id", Constants.M_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m = ApisSeUtil.i(js);
                OkHttpUtils.post(Constants.yhxy_agree).tag(TAG)
                        .params("key", m.K())
                        .params("msg", m.M())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putString("agree_status", "0");//是否同意隐私政策  0未同意  1为同意
                                ed.commit();

                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(getActivity(), "", "请稍等");
                            }

                            @Override
                            public void onAfter(String s, Exception e) {
                                super.onAfter(s, e);
                                ProgressUtil.dismiss();
                            }
                        });
            }

        }
    }
    public void SetHead() {
        Bitmap bm = aCache.getAsBitmap("head_" + sp.getString("user_id", ""));
        if (bm != null) {
            Log.w(TAG, "SetHead:  Cache");
            head.setImageBitmap(bm);
            return;
        }
        if (path != null && !("").equals(path)) {
            File file = new File(sp.getString("head_path", ""));
            if (file.exists() && file.isFile()) {
                Log.w(TAG, "SetHead:  file");
                Glide.with(this).load(path).asBitmap().into(new BitmapImageViewTarget(head) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        aCache.put("head_" + sp.getString("user_id", ""), resource);
                        head.setImageBitmap(resource);
                    }
                });
                return;
            }
        }
        if (!sp.getString("head_url", "").equals("")) {
            Glide.with(mApplication.getInstance()).load(sp.getString("head_url", "")).asBitmap()
                    .override(screenWidth / 4, screenWidth / 4).into(new BitmapImageViewTarget(head) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        aCache.put("head" + sp.getString("user_id", ""), resource);
                        head.setImageBitmap(resource);
                    }
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("Mine  回调" + requestCode + "     " + resultCode + "     " + data);
        if (resultCode == 4) {
            sign.setText(data.getStringExtra("sign"));
        } else {
            LogUtil.e("图片工具：：：：");
            PhotoUtil.onActivityResult(TAG, head, this, requestCode, resultCode, data);
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.mine_user:
                if (LoginUtil.checkLogin(getActivity())) {
                    intent.setClass(getActivity(), User_List.class);
                    startActivity(intent);
                }
                break;
            case R.id.mine_pintuan:
                if (LoginUtil.checkLogin(getActivity())) {
                    intent.setClass(getActivity(), Mine_pintuan.class);
                    startActivity(intent);
                }

                break;
            case R.id.mine_jifen:
                if (LoginUtil.checkLogin(getActivity())) {
                    intent.setClass(getActivity(), Mine_JiFen.class);
                    startActivity(intent);
                }

                break;
              /*
            签名点击修改签名
             */
            case R.id.mine_sign:
                if (!LoginUtil.checkLogin(getActivity())) {
                    return;
                }
                intent = new Intent(getActivity(), Sign.class);
                startActivityForResult(intent, 4);
                break;
            case R.id.mine_bangzhu:
                intent.setClass(getActivity(), BangZhu.class);
                startActivity(intent);
                break;
            case R.id.mine_tougao:
                if (LoginUtil.checkLogin(getActivity())) {
                    intent.setClass(getActivity(), TG_Mine.class);
                    startActivity(intent);
                }

                break;
            case R.id.mine_qiehuanzhanghao://退出登录
                LoginUtil.exitLogin(getActivity(), head);
                break;

            case R.id.mine_head:
                if (LoginUtil.checkLogin(getActivity())) {
                    PhotoUtil.choosePic(this, TAG);
                }
                break;
            case R.id.mine_gerenshezhi:
                intent.setClass(getActivity(), gerenshezhi.class);
                startActivity(intent);
                break;
            case R.id.mine_shoucang:
                if (LoginUtil.checkLogin(getActivity())) {
                    intent.setClass(getActivity(), Activity_ShouCang.class);
                    startActivity(intent);
                }
                break;

            case R.id.mine_zhifu:
                if (LoginUtil.checkLogin(getActivity())) {
                    intent.setClass(getActivity(), Mine_GYQD.class);
                    startActivity(intent);
                }

                break;
            case R.id.mine_dingdan:
                if (LoginUtil.checkLogin(getActivity())) {
                    intent.setClass(getActivity(), Mine_GYQD.class);
                    startActivity(intent);
                }
                break;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PhotoUtil.CHOOSEPICTUE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(mApplication.getInstance(), "无相册权限将无法使用该功能", Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKEPICTURE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(mApplication.getInstance(), "无相机权限将无法使用该功能", Toast.LENGTH_SHORT).show();
                }

        }
    }


}
