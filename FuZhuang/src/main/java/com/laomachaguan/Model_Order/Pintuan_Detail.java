package com.laomachaguan.Model_Order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.Common.BangZhu;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.MD5Utls;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.ShareManager;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/6/16 16:37
 * 公司：成都因陀罗网络科技有限公司
 */

public class Pintuan_Detail extends AppCompatActivity implements View.OnClickListener {
    private ImageView back, share, shoucang, image1;
    private TextView title, abs, tag, sales, money;
    private AvatarImageView tuanzhang, member;
    private TextView time;
    private TextView commit;
    private String id;
    private ScheduledExecutorService ses;
    private HashMap<String,String> map;
    private UMWeb umWeb;
    private static final String TAG = "groupd";
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           finish();
        }
    };

    private String UId_TuanZhang;
    private String number;
    private boolean hasStock=false;
    private void initView() {
        IntentFilter intentFilter = new IntentFilter("DingDan");
        registerReceiver(receiver, intentFilter);
        hasStock=getIntent().getBooleanExtra("stock",false);
        back = (ImageView) findViewById(R.id.title_back);
        back.setOnClickListener(this);
        share = (ImageView) findViewById(R.id.title_image3);
        share.setOnClickListener(this);
        shoucang = (ImageView) findViewById(R.id.title_image2);
        shoucang.setOnClickListener(this);
        image1 = (ImageView) findViewById(R.id.image1);
        title = (TextView) findViewById(R.id.title);
        abs = (TextView) findViewById(R.id.abs);
        tag = (TextView) findViewById(R.id.tag);
        sales = (TextView) findViewById(R.id.sales_likes);
        money = (TextView) findViewById(R.id.money);
        tuanzhang = (AvatarImageView) findViewById(R.id.tuanzhang);
        member = (AvatarImageView) findViewById(R.id.member);
        time = (TextView) findViewById(R.id.time_num);
        commit = (TextView) findViewById(R.id.cantuan);
        commit.setOnClickListener(this);
        id = getIntent().getStringExtra("id");
        findViewById(R.id.next).setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.next:
                Intent intent1=new Intent(this, BangZhu.class);
                startActivity(intent1);
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.title_image2://收藏
                if (!LoginUtil.checkLogin(this)) {
                    return;
                }
                if (!Network.HttpTest(this)) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js = new JSONObject();
                            js.put("user_id", PreferenceUtil.getUserIncetance(Pintuan_Detail.this)
                                    .getString("user_id", ""));
                            js.put("group_id", id);
                            String data = OkHttpUtils.post(Constants.PinTuan_Shoucang)
                                    .params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js))
                                    .execute().body().string();
                            if (!data.equals("")) {
                                if (AnalyticalJSON.getHashMap(data) != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToastShort("拼团关注成功");
                                            view.setSelected(true);


                                        }
                                    });
                                } else if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToastShort("已取消关注");
                                            view.setSelected(false);


                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Pintuan_Detail.this, "服务器异常", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.title_image3://分享
                if(umWeb!=null){
                    new ShareManager().shareWeb(umWeb,this);
                }
                break;
            case R.id.cantuan:
                if(!LoginUtil.checkLogin(this)){
                    return;
                }
                // TODO: 2017/6/9 参团
                if (PreferenceUtil.getUserIncetance(Pintuan_Detail.this).getString("user_id", "").equals(UId_TuanZhang)) {
                    ToastUtil.showToastShort("不能参与自己的拼团~");
                    return;
                }
                ArrayList<HashMap<String, String>> list = new ArrayList<>();
                HashMap<String, String> m = new HashMap<>();
                m.put("title", map.get("title"));
                m.put("num", "1");
                m.put("money", map.get("price"));
                m.put("cost", map.get("money"));
                m.put("id", map.get("id"));
                list.add(m);
                Intent intent = new Intent(Pintuan_Detail.this, Dingdan_commit.class);
                intent.putExtra("number", Long.valueOf(number));
                intent.putExtra("list", list);
                intent.putExtra("KT", true);
                startActivity(intent);
                break;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.detail_pintuan);
        initView();
        getData();
    }

    private void getData() {
        if (!Network.HttpTest(this)) {
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.Detail_PinTuan).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {


                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(Pintuan_Detail.this, "", "正在加载");
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/" + id+ "/m_id/" + MD5Utls.stringToMD5(Constants.M_id));
                            umWeb.setTitle(map.get("title"));
                            umWeb.setDescription(map.get("abstract").equals("")?"快来"+R.string.app_name+"拼团吧":map.get("abstract"));
                            umWeb.setThumb(new UMImage(Pintuan_Detail.this, map.get("image1")));
                            title.setText(map.get("title"));
                            Glide.with(Pintuan_Detail.this).load(map.get("image1"))
                                    .override(DimenUtils.dip2px(Pintuan_Detail.this, 70), DimenUtils.dip2px(Pintuan_Detail.this, 70))
                                    .centerCrop()
                                    .into(image1);
                            abs.setText(map.get("abstract"));
                            money.setText("￥"+map.get("money"));
                            tag.setText(map.get("name"));
                            sales.setText("库存"+map.get("stock")+"   销量:" + map.get("sales") + "  点赞:" + map.get("likes"));
                            final ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(map.get("group"));

                            Glide.with(Pintuan_Detail.this).load(list.get(0).get("user_image"))
                                    .override(DimenUtils.dip2px(Pintuan_Detail.this, 50), DimenUtils.dip2px(Pintuan_Detail.this, 50))
                                    .into(tuanzhang);
                            UId_TuanZhang=list.get(0).get("user_id");
                            number=list.get(0).get("number");
                            findViewById(R.id.tuanzhang_tip).setVisibility(View.VISIBLE);
                            if (list.size() ==2) {
                                Glide.with(Pintuan_Detail.this).load(list.get(1).get("user_image"))
                                        .override(DimenUtils.dip2px(Pintuan_Detail.this, 50), DimenUtils.dip2px(Pintuan_Detail.this, 50))
                                        .fitCenter()
                                        .into(member);
                            } else if(list.size()>2){
                                member.setImageBitmap(ImageUtil.readBitMap(Pintuan_Detail.this, R.drawable.many_people));
                            }  else{
                                member.setImageBitmap(ImageUtil.readBitMap(Pintuan_Detail.this, R.drawable.group_no));
                            }

                            long offset = TimeUtils.dataOne(list.get(0).get("end_time")) - System.currentTimeMillis();
                            if (list.size() <= 1) {
                                if (offset > 0) {
                                    if(list.get(0).get("user_id").equals(PreferenceUtil.getUserId(Pintuan_Detail.this))){
                                        commit.setVisibility(View.GONE);
                                    }else{
                                        commit.setText("(￥" + map.get("price") + ")一键参团");
                                        commit.setEnabled(true);
                                    }
                                    ses = Executors.newScheduledThreadPool(1);
                                    ses.scheduleAtFixedRate(new Runnable() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    time.setText("仅剩" + (2 - list.size() + "个名额 , " + TimeUtils.formatTimeShort(TimeUtils.dataOne(list.get(0).get("end_time")) - System.currentTimeMillis(), false) + "后结束"));
                                                }
                                            });

                                        }
                                    }, 0, 1, TimeUnit.SECONDS);
                                } else {
                                    if(ses!=null){
                                        ses.shutdownNow();
                                    }
                                    commit.setText("该拼团项目已过期");
                                    commit.setEnabled(false);
                                }
                            }else{
                                if(ses!=null){
                                    ses.shutdownNow();
                                }
                                time.setText("");
                                commit.setEnabled(false);
                                commit.setTextColor(Color.parseColor("#e6e6e6"));
                                commit.setText("拼团已完成");
                                commit.setEnabled(false);
                                share.setVisibility(View.GONE);
                                shoucang.setVisibility(View.GONE);
                            }
                            if("0".equals(map.get("stock"))){
                                if(ses!=null){
                                    ses.shutdownNow();
                                }
                                time.setVisibility(View.GONE);
                                share.setVisibility(View.GONE);
                                shoucang.setVisibility(View.GONE);
                                commit.setText("已售罄");
                                commit.setEnabled(false);
                            }

                        }else{
                            if(s.equals("null")){
                                ToastUtil.showToastShort("该商品不存在");
                                finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ses!=null){
            ses.shutdownNow();
        }
        unregisterReceiver(receiver);
    }
}
