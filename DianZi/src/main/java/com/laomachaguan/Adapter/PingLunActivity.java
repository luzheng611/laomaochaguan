package com.laomachaguan.Adapter;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mPLlistview;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/28.
 */
public class PingLunActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PingLunActivity";
    private ImageView back;
    private TextView title;
    private String page = "1";
    private String endPage = "";
    private ImageView head;
    private ImageView article_head;
    private TextView name, time, dianzanNum, content, article_title;
    private mPLlistview PlListVIew;
    private PL_List_Adapter adapter;
    private ArrayList<HashMap<String, String>> Pllist;
    //    private ImageView tip;
    //无评论时的header
    private TextView tv;
    private InputMethodManager imm;
    private String pLId;
    private EditText PLText;
    private TextView fasong;
    private SharedPreferences sp;
    private Drawable dianzan, dianzan1;

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.zixun_item_fasong:
                if (PLText.getText().toString().trim().equals("")) {
                    Toast.makeText(this, mApplication.ST("请输入回复"), Toast.LENGTH_SHORT).show();
                    return;
                }
                v.setEnabled(false);
                ProgressUtil.show(this, "", mApplication.ST("正在提交"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            final String content = PLText.getText().toString();
                            JSONObject js = new JSONObject();
                            js.put("user_id", sp.getString("user_id", ""));
                            js.put("ct_contents", content);
                            js.put("m_id", Constants.M_id);
                            js.put("ct_id", pLId);
                            final String data = OkHttpUtils.post(Constants.little_zixun_pl_add_IP).params("key", ApisSeUtil.getKey()).params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                            if (!data.equals("")) {
                                Log.i(TAG, "run:      data------>" + data);
                                final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final HashMap<String, String> map = new HashMap<>();
                                            String headurl = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
                                            final String time = TimeUtils.getStrTime(System.currentTimeMillis() + "");
                                            String petname = sp.getString("pet_name", "");
                                            String diazannum = "0";
                                            map.put("user_image", headurl);
                                            map.put("ct_contents", content);
                                            map.put("pet_name", petname);
                                            map.put("ct_ctr", diazannum);
                                            map.put("user_id", sp.getString("user_id", ""));
                                            map.put("ct_time", time);
                                            map.put("id", hashMap.get("id"));
                                            map.put("realname", sp.getString("ident", ""));
                                            PlListVIew.setFocusable(true);
                                            if (adapter.mlist.size() == 0) {
                                                adapter.mlist.add(0, map);
                                                adapter.flagList.add(0, false);
                                                PlListVIew.removeHeaderView(tv);
                                                PlListVIew.setAdapter(adapter);

                                            } else {
                                                adapter.mlist.add(0, map);
                                                adapter.flagList.add(0, false);
                                                adapter.notifyDataSetChanged();

                                            }
                                            PlListVIew.setSelection(0);
                                            v.setEnabled(true);
                                            PLText.setText("");
                                            imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                            Toast.makeText(PingLunActivity.this, mApplication.ST("添加回复成功"), Toast.LENGTH_SHORT).show();
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.setEnabled(true);
                                        Toast.makeText(PingLunActivity.this, mApplication.ST("提交回复失败，请重新尝试"), Toast.LENGTH_SHORT).show();
                                        ProgressUtil.dismiss();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.Pl_item_DianZan_num:
//                if (v.getTag() == null || v.getTag().toString().equals("")) {
//                    Toast.makeText(PingLunActivity.this, "快去给其他人点赞吧", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                v.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js = new JSONObject();
                            js.put("user_id", sp.getString("user_id", ""));
                            js.put("comment_id", pLId);
                            js.put("m_id", Constants.M_id);

                            String data1 = OkHttpUtils.post(Constants.PL_DZ_IP)
                                    .params("key", ApisSeUtil.getKey())
                                    .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();
                            if (!data1.equals("")) {
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                                if (map != null && map.get("code").equals("000")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) v).setCompoundDrawables(null, null, dianzan1, null);
                                            String d = (Integer.valueOf(dianzanNum.getText().toString()) + 1) + "";
                                            dianzanNum.setText(d);
                                            dianzanNum.setTextColor(ContextCompat.getColor(PingLunActivity.this, R.color.main_color));
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) v).setCompoundDrawables(null, null, dianzan1, null);
                                            dianzanNum.setTextColor(ContextCompat.getColor(PingLunActivity.this, R.color.main_color));
                                            Toast.makeText(PingLunActivity.this, mApplication.ST("你已经对该评论点过赞了"), Toast.LENGTH_SHORT).show();
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PLText.postDelayed(new Runnable() {
            @Override
            public void run() {
                imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
            }
        }, 10);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pl_detail_activity);


        initView();
        getData();
    }

    private void initView() {
        PLText = (EditText) findViewById(R.id.zixun_item_input);
        PLText.setHint(mApplication.ST("请输入您的回复"));
        sp = PreferenceUtil.getUserIncetance(this);
        fasong = (TextView) findViewById(R.id.zixun_item_fasong);
        fasong.setText(mApplication.ST("发送"));
        fasong.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        back.setImageResource(R.drawable.back);
        title = (TextView) findViewById(R.id.title_title);
        title.setText(mApplication.ST("回复详情"));
        time = (TextView) findViewById(R.id.PL_item_time);
        time.setText(TimeUtils.getTrueTimeStr(getIntent().getStringExtra("time")));
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        head = (ImageView) findViewById(R.id.PL_item_Head);
        Glide.with(this).load(getIntent().getStringExtra("user_image")).override(DimenUtils.dip2px(this, 50), DimenUtils.dip2px(this, 50))
                .bitmapTransform(new CropCircleTransformation(this))
                .centerCrop().into(head);
        name = (TextView) findViewById(R.id.PL_item_Name);
        name.setText(getIntent().getStringExtra("pet_name"));
        dianzanNum = (TextView) findViewById(R.id.Pl_item_DianZan_num);
        dianzanNum.setText(getIntent().getStringExtra("num"));
        dianzanNum.setOnClickListener(this);
        dianzan = ContextCompat.getDrawable(this, R.drawable.dianzan);
        dianzan1 = ContextCompat.getDrawable(this, R.drawable.dianzan1);
        dianzan.setBounds(0, 0, DimenUtils.dip2px(this, 15), DimenUtils.dip2px(this, 15));
        dianzan1.setBounds(0, 0, DimenUtils.dip2px(this, 15), DimenUtils.dip2px(this, 15));
        if (getIntent().getBooleanExtra("isLike", false)) {
            dianzanNum.setCompoundDrawables(null, null, dianzan1, null);
            dianzanNum.setTextColor(ContextCompat.getColor(this, R.color.main_color));
        } else {
            dianzanNum.setCompoundDrawables(null, null, dianzan, null);
            dianzanNum.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }
        pLId = getIntent().getStringExtra("id");
        content = (TextView) findViewById(R.id.Pl_item_Content);
        content.setText(getIntent().getStringExtra("content"));
        article_head = (ImageView) findViewById(R.id.article_head);
        article_title = (TextView) findViewById(R.id.article_title);
        PlListVIew = (mPLlistview) findViewById(R.id.zixun_item_plListview);
        PlListVIew.setFooterDividersEnabled(false);

        PlListVIew.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlListVIew.footer.setText(mApplication.ST("正在加载"));
                if (!endPage.equals(page)) page = String.valueOf(Integer.valueOf(page) + 1);
                getData();
            }
        });
        Pllist = new ArrayList<>();
        adapter = new PL_List_Adapter(this);
        adapter.setIsHuifu(true);
        PlListVIew.setAdapter(adapter);


    }

    private void getData() {
        if (!Network.HttpTest(this)) {
            Toast.makeText(PingLunActivity.this, mApplication.ST("请检查网络"), Toast.LENGTH_SHORT).show();
            ProgressUtil.dismiss();
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("page", page);
            js.put("comment_id", pLId);
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpUtils.post(Constants.little_pl_huifu__IP)
                .params("key", ApisSeUtil.getKey()).params("msg",ApisSeUtil.getMsg(js))
               .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {


            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                ProgressUtil.show(PingLunActivity.this, "", mApplication.ST("正在加载"));
            }

            @Override
            public ArrayList<HashMap<String, String>> parseNetworkResponse(Response response) throws Exception {

                return AnalyticalJSON.getList_zj(response.body().string());
            }

                   @Override
                   public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                       Pllist = list;
                       if ((Pllist != null)) {
                           PlListVIew.setFocusable(false);
                           if (adapter.mlist.size() == 0 && Pllist.size() == 0) {//没有评论的时候
                               tv = new TextView(PingLunActivity.this);
                               tv.setText(mApplication.ST("还没有回复"));
                               PlListVIew.addHeaderView(tv);
                               PlListVIew.footer.setVisibility(View.GONE);
                               PlListVIew.setAdapter(adapter);

                               return;
                           }
                           if (adapter.mlist.size() == 0) {//添加评论的的时候
                               adapter.addList(Pllist);
                               PlListVIew.setAdapter(adapter);
                               if (Pllist.size() < 10) {
                                   endPage = page;
                                   PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                   PlListVIew.footer.setEnabled(false);
                               } else {
                                   PlListVIew.footer.setText(mApplication.ST("点击加载更多"));
                               }
                           } else {
                               adapter.mlist.addAll(Pllist);
                               boolean flag = false;
                               for (int i = 0; i < Pllist.size(); i++) {
                                   adapter.flagList.add(flag);
                               }
                               adapter.notifyDataSetChanged();
                               if (Pllist.size() < 10) {
                                   endPage = page;
                                   PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                                   PlListVIew.footer.setEnabled(false);
                               } else {
                                   PlListVIew.footer.setText(mApplication.ST("点击加载更多"));
                               }
                           }
                       } else {
                           PlListVIew.footer.setText(mApplication.ST("没有更多数据了"));
                           PlListVIew.footer.setEnabled(false);
                       }
                   }

                   @Override
                   public void onAfter(@Nullable ArrayList<HashMap<String, String>> hashMaps, @Nullable Exception e) {
                       super.onAfter(hashMaps, e);
                       ProgressUtil.dismiss();
                   }



        });
    }
}
