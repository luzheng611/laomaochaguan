package com.laomachaguan.Model_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.Adapter.PL_List_Adapter;
import com.laomachaguan.Common.Login;
import com.laomachaguan.Common.Mine_gerenziliao;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.ShareManager;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mPLlistview;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Administrator on 2016/10/7.
 */
public class activity_Detail extends AppCompatActivity implements View.OnClickListener {
    private ImageView back, rightImg, shoucang, dianzanImg;
    private TextView dianzanText, title, fabuTime, faqidanwei, huodongdidian, huodongTime, peopleNum, Tobaoming, content, FaSong;
    private EditText pingLun;
    private LinearLayout dianzan;
    private LinearLayout PointLayou;//轮播图圆点layout
    private mPLlistview PlListVIew;
    private String page = "1";
    private String endPage = "";
    private Thread thread;//线程
    private static final String TAG = "activityd";
    private String Id;//活动id
    private List<String> imageList;
    private ViewPager viewPager;//轮播
    private int screeWidth, dp10, dp180, dp7;
    private PL_List_Adapter adapter;
    private SharedPreferences sp;
    private InputMethodManager imm;
    private TextView tv;//评论的头部
    private boolean needTochange;//判断是否需要通知其他页面改变
    private SHARE_MEDIA[] share_list;
    private ShareAction action;
    private ProgressDialog progressDialog;
    private UMWeb umWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initView();
        LoadData();
    }

    /**
     * 初始化数据
     */
    private void initView() {
        dianzanImg = (ImageView) findViewById(R.id.activity_detail_dianzan_img);
        dianzanText = (TextView) findViewById(R.id.activity_detail_dianzan_text);
         StatusBarCompat.compat(this,ContextCompat.getColor(this,R.color.main_color));
        sp = getSharedPreferences("user", MODE_PRIVATE);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        screeWidth = getResources().getDisplayMetrics().widthPixels;
        dp10 = DimenUtils.dip2px(this, 10);
        adapter = new PL_List_Adapter(this);
        dp180 = DimenUtils.dip2px(this, 180);
        dp7 = DimenUtils.dip2px(this, 7);
        //图片地址数组
        imageList = new ArrayList<>();
        //轮播图圆点layout
        PointLayou = (LinearLayout) findViewById(R.id.activity_detail_circlePoint_layout);
        //活动Id
        Id = getIntent().getStringExtra("id");
        //返回按钮
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        //页面标题
        ((TextView) findViewById(R.id.title_title)).setText("活动详情");

        //分享按钮
        rightImg = (ImageView) findViewById(R.id.title_image2);
        rightImg.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.fenxiang).override(DimenUtils.dip2px(this, 35), DimenUtils.dip2px(this, 35)).into(rightImg);
//        rightImg.setOnClickListener(this);
        rightImg.setFocusable(false);
        //标题，发布时间
        title = (TextView) findViewById(R.id.activity_detail_title);
        fabuTime = (TextView) findViewById(R.id.activity_detail_time);
        //轮播
        viewPager = (ViewPager) findViewById(R.id.activity_detail_viewPager);
        //发起单位，活动地点，活动时间，已报名人数
        faqidanwei = (TextView) findViewById(R.id.activity_detail_faxidanwei);
        huodongdidian = (TextView) findViewById(R.id.activity_detail_huodongdidian);
        huodongTime = (TextView) findViewById(R.id.activity_detail_huodongshijian);
        peopleNum = (TextView) findViewById(R.id.activity_detail_peopleNum);
        //报名入口
        Tobaoming = (TextView) findViewById(R.id.activity_detail_baoming);
//        Tobaoming.setOnClickListener(this);
        //点赞
        dianzan = (LinearLayout) findViewById(R.id.activity_detail_dianzan);
//        dianzan.setOnClickListener(this);
        //活动详情
        content = (TextView) findViewById(R.id.activity_detail_info);
        //评论列表
        PlListVIew = (mPLlistview) findViewById(R.id.activity_detail_listview);
        PlListVIew.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlListVIew.footer.setText("正在加载");
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                getPLData();
            }
        });
        //评论框底部
        pingLun = (EditText) findViewById(R.id.activity_detail_apply_edt);
        FaSong = (TextView) findViewById(R.id.activity_detail_fasong);
        shoucang = (ImageView) findViewById(R.id.activity_detail_shoucang);
//        FaSong.setOnClickListener(this);
//        shoucang.setOnClickListener(this);
        //分享
        share_list = new SHARE_MEDIA[]{
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 退出页面的设置
     */
    @Override
    protected void onDestroy() {
        if (thread.isAlive()) thread.interrupt();
        OkHttpUtils.getInstance().cancelTag(TAG);
        if (needTochange) {
            Intent intent = new Intent("Mine_SC");
            sendBroadcast(intent);
        }
        super.onDestroy();
    }

    /**
     * 获取评论数据
     */
    private void getPLData() {
        new Thread(new Runnable() {
            ArrayList<HashMap<String, String>> Pllist;

            @Override
            public void run() {
                try {
                    String data = OkHttpUtils.post(Constants.Activity_pinglun_IP).params("act_id", Id).params("page", page)
                            .execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        Log.w(TAG, "run:      PLdata------>" + data);
                        Pllist = AnalyticalJSON.getList(data, "comment");
                        if ((Pllist != null)) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    PlListVIew.setFocusable(false);
                                    if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                                        tv = new TextView(activity_Detail.this);
                                        tv.setText("还没有评论嘞");
                                        PlListVIew.addHeaderView(tv);
                                        PlListVIew.footer.setVisibility(View.GONE);
                                        PlListVIew.setAdapter(adapter);

                                        return;
                                    }
                                    if (adapter.mlist.size() == 0) {//添加评论的的时候
                                        adapter.addList(Pllist);
                                        PlListVIew.setAdapter(adapter);
                                        if (Pllist.size() < 20) {
                                            endPage = page;
                                            PlListVIew.footer.setText("没有更多数据了");
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText("点击加载更多");
                                        }
                                    } else {
                                        adapter.mlist.addAll(Pllist);
                                        boolean flag = false;
                                        for (int i = 0; i < Pllist.size(); i++) {
                                            adapter.flagList.add(flag);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (Pllist.size() < 20) {
                                            endPage = page;
                                            PlListVIew.footer.setText("没有更多数据了");
                                            PlListVIew.footer.setEnabled(false);
                                        } else {
                                            PlListVIew.footer.setText("点击加载更多");
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

    /**
     * 加载详情页数据（不包括评论）
     */
    private void LoadData() {
        if(progressDialog==null){
            progressDialog=ProgressDialog.show(this,null,"正在加载....");
            progressDialog.setCanceledOnTouchOutside(true);
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = OkHttpUtils.post(Constants.Activity_detail_IP).tag(TAG).params("key", Constants.safeKey)
                            .params("act_id", Id).execute().body().string();
                    if (!TextUtils.isEmpty(data)) {
                        Log.e(TAG, "run: " + data + "   id-=-=>" + Id);
                        HashMap<String, String> totalMap = AnalyticalJSON.getHashMap(data);
                        if (totalMap != null) {
                            final HashMap<String, String> map = AnalyticalJSON.getHashMap(totalMap.get("activity"));
                            if (map != null) {//加载数据成功
                                totalMap = null;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dianzan.setOnClickListener(activity_Detail.this);
                                        Tobaoming.setOnClickListener(activity_Detail.this);
                                        rightImg.setOnClickListener(activity_Detail.this);
                                        FaSong.setOnClickListener(activity_Detail.this);
                                        shoucang.setOnClickListener(activity_Detail.this);
                                        title.setText(map.get("title"));//标题
                                        fabuTime.append(": " + TimeUtils.getTrueTimeStr(map.get("time")));//发布时间
                                        setUrlToImage(map);//保存图片地址并加载,显示小圆点
                                        faqidanwei.append(":" + map.get("author"));//发布单位
                                        huodongdidian.append(":" + map.get("address"));//活动地点
                                        String startTime = map.get("act_time");
                                        if ((TimeUtils.dataOne(startTime) - System.currentTimeMillis()) <= 0) {
                                            huodongTime.append(":活动已开始");
                                        } else {
                                            huodongTime.append(":" + TimeUtils.getTrueTimeStr(startTime));//活动时间
                                        }
                                        String endTime = map.get("end_time");
                                        if ((TimeUtils.dataOne(endTime) - System.currentTimeMillis()) <= 0) {
                                            Tobaoming.setText("报名已结束");
                                            Tobaoming.setEnabled(false);
                                            Tobaoming.setBackgroundColor(Color.DKGRAY);
                                        }
                                        peopleNum.append(":" + map.get("enrollment"));//已报名人数
                                        dianzanText.setText(map.get("likes"));
                                        content.setText("    " + map.get("abstract"));//简介
                                        //使用的是测试分享地址
                                        umWeb = new UMWeb(Constants.FX_host_Ip + TAG + "/id/" + Id );
                                        LogUtil.e("链接：：：：" + Constants.FX_host_Ip + TAG + "/id/"+ Id);
                                        umWeb.setTitle(mApplication.ST(map.get("title")));
                                        umWeb.setDescription(mApplication.ST(map.get("abstract")));
                                        umWeb.setThumb(new UMImage(activity_Detail.this, map.get("image1")));
                                        //使用的是测试分享地址
                                        getPLData();
                                        if(progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
                                    }
                                });
                            }
                        } else {//加载失败
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
                                    Toast.makeText(activity_Detail.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(progressDialog!=null&&progressDialog.isShowing())progressDialog.dismiss();
                                    Toast.makeText(activity_Detail.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
        thread.start();
    }



    /**
     * 添加评论
     *
     * @param v 发送按钮
     */
    private void addPL(final View v) {
        if (pingLun.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请输入评论", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sp.getString("user_id", "").equals("") || sp.getString("uid", "").equals("")) {
            Toast.makeText(activity_Detail.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            return;
        }
        if(sp.getString("pet_name","").trim().equals("")){
            Toast.makeText(this, "请完善信息", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,Mine_gerenziliao.class);
            startActivity(intent);
            return;
        }
        v.setEnabled(false);
        final ProgressDialog p = new ProgressDialog(this);
        p.setMessage("正在提交评论");
        p.isIndeterminate();
        if (!p.isShowing()) p.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String content = pingLun.getText().toString();
                    final String data = OkHttpUtils.post(Constants.Activity_pinglun_add_IP).params("user_id", sp.getString("user_id", "")).params("act_id", Id)
                            .params("ct_contents", content).params("key", Constants.safeKey).params("m_id", Constants.M_id).execute().body().string();
                    if (!data.equals("")) {
                        Log.i(TAG, "run:      data------>" + data);
                        final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                        if (hashMap != null && "000".equals(hashMap.get("code"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final HashMap<String, String> map = new HashMap<>();
                                    String headurl = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
                                    final String time = "刚刚";
                                    String petname = sp.getString("pet_name", "");
                                    String diazannum = "0";
                                    map.put("user_image", headurl);
                                    map.put("ct_contents", content);
                                    map.put("pet_name", petname);
                                    map.put("ct_ctr", diazannum);
                                    map.put("ct_time", time);
                                    map.put("id", hashMap.get("id"));
                                    PlListVIew.setFocusable(true);
                                    if (adapter.mlist.size() == 0) {
                                        adapter.mlist.add(0, map);
                                        adapter.flagList.add(0, false);
                                        if (tv != null) PlListVIew.removeHeaderView(tv);
                                        PlListVIew.setAdapter(adapter);

                                    } else {
                                        adapter.mlist.add(0, map);
                                        adapter.flagList.add(0, false);
                                        adapter.notifyDataSetChanged();

                                    }
                                    PlListVIew.setSelection(0);
                                    v.setEnabled(true);
                                    pingLun.setText("");
                                    imm.hideSoftInputFromWindow(pingLun.getWindowToken(), 0);

                                    Toast.makeText(activity_Detail.this, "添加评论成功", Toast.LENGTH_SHORT).show();
                                    p.dismiss();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity_Detail.this, "上传评论失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                    p.dismiss();
                                    v.setEnabled(true);
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity_Detail.this, "上传评论失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                p.dismiss();
                                v.setEnabled(true);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 加载轮播图片并加载小圆点
     *
     * @param map
     */
    private void setUrlToImage(final HashMap<String, String> map) {
        String image1 = map.get("image1");
        String image2 = map.get("image2");
        String image3 = map.get("image3");
        if (!TextUtils.isEmpty(image1)) {
            imageList.add(image1);
        }
        if (!TextUtils.isEmpty(image2)) {
            imageList.add(image2);
        }
        if (!TextUtils.isEmpty(image3)) {
            imageList.add(image3);
        }
        viewPager.setOffscreenPageLimit(imageList.size());
        final LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(dp7 + 5, dp7 + 5);
        ll.setMargins(DimenUtils.dip2px(this, 5), 0, 0, 0);
        viewPager.setAdapter(new PagerAdapter() {//加载图片
            @Override
            public int getCount() {
                return imageList == null ? 0 : imageList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageview = new ImageView(activity_Detail.this);
                imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(activity_Detail.this).load(map.get("image" + (position + 1))).override(screeWidth - (dp10 * 2), dp180)
                        .centerCrop().into(imageview);
                ImageView p = new ImageView(activity_Detail.this);
                p.setBackgroundResource(R.drawable.point_sel);
                p.setLayoutParams(ll);
                PointLayou.addView(p);
                container.addView(imageview);
                return imageview;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        if (PointLayou.getChildAt(0) != null)
            PointLayou.getChildAt(0).setSelected(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetPointColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 重置圆点颜色
     */
    private void resetPointColor(int position) {
        for (int i = 0; i < imageList.size(); i++) {
            PointLayou.getChildAt(i).setSelected(false);
        }
        PointLayou.getChildAt(position).setSelected(true);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.activity_detail_fasong:
                addPL(v);//添加评论
                break;
            case R.id.activity_detail_dianzan://点赞
                if (sp.getString("user_id", "").equals("") && sp.getString("uid", "").equals("")) {
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                    Toast.makeText(activity_Detail.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String data = OkHttpUtils.post(Constants.Activity_DZ_IP).params("act_id", Id).params("user_id", sp.getString("user_id", ""))
                                        .params("key", Constants.safeKey).execute().body().string();
                                if (!data.equals("")) {
                                    HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                    if (map != null && map.get("code").equals("000")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzanText.setText((Integer.valueOf(dianzanText.getText().toString()) + 1) + "");
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzan.setEnabled(false);
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dianzanText.setTextColor(getResources().getColor(R.color.main_color));
                                                dianzanImg.setImageResource(R.drawable.dianzan1);
                                                dianzan.setEnabled(false);
                                                Toast.makeText(activity_Detail.this, "已点过赞啦", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
                break;
            case R.id.activity_detail_baoming://报名页面
//                if(new LoginUtil().checkLogin(activity_Detail.this)){
//                    if (sp.getString("pet_name", "").trim().equals("")) {
//                        Toast.makeText(activity_Detail.this, "请完善信息", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(activity_Detail.this, Mine_gerenziliao.class);
//                        startActivity(intent);
//                        return;
//                    }
//                    Intent intent = new Intent(activity_Detail.this, Enrollment.class);
//                    intent.putExtra("id", Id);
//                    startActivity(intent);
//                }
                OkHttpUtils.post(Constants.Activity_BaoMing).tag(TAG).params("act_id",Id).params("key",Constants.safeKey)
                        .params("user_id",sp.getString("user_id","")).execute(new AbsCallback<Object>() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        return null;
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        try {
                            String data=response.body().string();
                            if(!data.equals("")){
                                final HashMap<String  ,String >map=AnalyticalJSON.getHashMap(data);
                                if(map!=null){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            View view= LayoutInflater.from(activity_Detail.this).inflate(R.layout.baoming_alert,null);
                                            AlertDialog.Builder b=new AlertDialog.Builder(activity_Detail.this).
                                                    setView(view);
                                            final AlertDialog d=b.create();
                                            d.getWindow().setDimAmount(0.2f);
                                            view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    d.dismiss();
                                                }
                                            });
                                            if("000".equals(map.get("code"))) {
                                                ((TextView) view.findViewById(R.id.result_msg)).setText("您已成功报名");
                                                view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#40d976"));
                                            }else{
                                                ((TextView) view.findViewById(R.id.result_msg)).setText("您已经报名过了哟~");
                                                view.findViewById(R.id.commit).setBackgroundColor(Color.parseColor("#e75e5e"));
                                            }
                                            SpannableString ss=new SpannableString("请保持电话 "+sp.getString("phone","")+" 畅通");
                                            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity_Detail.this,R.color.main_color)),6,18, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                            ((TextView) view.findViewById(R.id.phone)).setText(ss);

                                            d.show();
                                        }
                                    });

                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(activity_Detail.this,"","正在报名，请稍等");
                    }

                    @Override
                    public void onAfter(@Nullable Object o, @Nullable Exception e) {
                        super.onAfter(o, e);
                        ProgressUtil.dismiss();
                    }


                });


                break;
            case R.id.title_back://返回
                finish();
                break;
            case R.id.title_image2://分享
               new ShareManager().shareWeb(umWeb,this);
                break;
            case R.id.activity_detail_shoucang:
                if (!LoginUtil.checkLogin(this)) {
                    return;
                }
                if (!Network.HttpTest(this)) {
                    Toast.makeText(activity_Detail.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String data = OkHttpUtils.post(Constants.Activity_Shoucang_IP).params("key", Constants.safeKey).params("act_id", Id)
                                    .params("user_id", sp.getString("user_id", "")).execute().body().string();
                            if (!data.equals("")) {
                                if (AnalyticalJSON.getHashMap(data) != null && "000".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity_Detail.this, "添加收藏成功", Toast.LENGTH_SHORT).show();
                                            v.setSelected(true);
                                            needTochange = true;

                                        }
                                    });
                                } else if (AnalyticalJSON.getHashMap(data) != null && "002".equals(AnalyticalJSON.getHashMap(data).get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity_Detail.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                                            v.setSelected(false);
                                            needTochange = true;

                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity_Detail.this, "服务器异常", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }
}
