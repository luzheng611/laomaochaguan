package com.laomachaguan.Common;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;

import java.io.IOException;
import java.util.HashMap;

public class XiangxiZiLiaoActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mtvnc;
    private TextView mtvid;
//    private TextView mtvphone;
    private TextView mtvdiqu;
    private ImageView mimageviewhead;
    private ImageView mimageviewsex;
    private HashMap<String, String> map;
    private String Id;
    private SharedPreferences sp;
    private String type;
    private static final String TAG = "XiangxiZiLiaoActivity";
    public void init(){
        mtvnc=(TextView)findViewById(R.id.nicheng_tv);
        mtvid=(TextView)findViewById(R.id.idhao_tv);
//        mtvphone=(TextView)findViewById(R.id.phone_tv);
        mtvdiqu=(TextView)findViewById(R.id.diqu_tv);
        mimageviewhead=(ImageView)findViewById(R.id.head_imageview);
        mimageviewsex=(ImageView)findViewById(R.id.sex_imageview);
        sp=getSharedPreferences("user",MODE_PRIVATE);
        Id=getIntent().getStringExtra("id");
        type=getIntent().getStringExtra("type");
//        if(type!=null&&type.equals("chat")){
//            findViewById(R.id.addfd_bnt).setVisibility(View.GONE);
//        }else if(type!=null&&type.equals("guanzhu")){
//            ((Button) findViewById(R.id.guanzufd_bnt)).setText("取消关注");
//        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.firend_xiangxi_zi_liao);
        init();
        getdata();
    }

    @Override
    public void onClick(final View view) {
        final int id=view.getId();
        switch (id){
            case R.id.xiangxiziliao_back:
                finish();
                break;
//            case R.id.guanzufd_bnt:
//                view.setEnabled(false);
//                if (!new LoginUtil().checkLogin(this)) {
//                    view.setEnabled(true);
//                    return;
//                }
//                if (!Network.HttpTest(this)) {
//                    Toast.makeText(mApplication.getInstance(), "请检查网络连接", Toast.LENGTH_SHORT).show();
//                    view.setEnabled(true);
//                    return;
//                }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String data = OkHttpUtils.post(Constants.Guanzhu_IP).tag(TAG).params("user_id", sp.getString("user_id", "")).params("userid", Id).params("key", Constants.safeKey)
//                                    .execute().body().string();
//                            if (!data.equals("")) {
//                                if (AnalyticalJSON.getHashMap(data) != null && AnalyticalJSON.getHashMap(data).get("code") != null) {
//                                    if (AnalyticalJSON.getHashMap(data).get("code").equals("000")) {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(mApplication.getInstance(), "关注成功", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    } else {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(mApplication.getInstance(), "已取消关注", Toast.LENGTH_SHORT).show();
//                                                view.setEnabled(true);
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }catch ( IllegalStateException e){
//
//                        }
//                    }
//                }).start();
//                break;
            case R.id.addfd_bnt:
                view.setEnabled(false);
                new AlertDialog.Builder(XiangxiZiLiaoActivity.this).setTitle("提示")
                        .setMessage("是否添加好友？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(Id.equals(sp.getString("user_id",""))){
                                    Toast.makeText(XiangxiZiLiaoActivity.this, "你与你自己还不是好友吗？", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String data = OkHttpUtils.post(Constants.QQfriend_IP).tag(TAG).params("user_id", sp.getString("user_id", ""))
                                                    .params("user_friend", Id).params("key", Constants.safeKey).execute().body().string();
                                            Log.w(TAG, "run: " + data);
                                            String code = AnalyticalJSON.getHashMap(data).get("code");
                                            if (code != null && code.equals("000")) {

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(XiangxiZiLiaoActivity.this, "好友请求已发送", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                            } else if (code != null && code.equals("001")) {

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(mApplication.getInstance(), "你们已是好友啦", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                            } else {

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(mApplication.getInstance(), "网络波动请稍后重试", Toast.LENGTH_SHORT).show();
                                                            view.setEnabled(true);
                                                        }
                                                    });

                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }catch (IllegalStateException e){

                                        }
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();

                break;
        }
    }
    public void getdata(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    data1 = OkHttpUtils.post(Constants.User_Info_Ip).tag(TAG).params("user_id",Id)
                            .params("key", Constants.safeKey).execute()
                            .body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (data1!=null&&!data1.equals("")){
                    Log.d("个人信息数据为：",data1);
                    map= AnalyticalJSON.getHashMap(data1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("44444444444","1111111111111");
                            mtvnc.setText(map.get("pet_name"));  //昵称
                            //mtvid  ID号
                            //mtvdiqu  地区
//                            if(type!=null&&type.equals("chat")){
////                                mtvphone.setText(map.get("user_phone"));
//                                mtvphone.setText("");
//                            }else{
//                                mtvphone.setText("仅好友可见");
//                            }
                              //电话号码
                            Glide.with(XiangxiZiLiaoActivity.this).load(map.get("user_image"))
                                    .override(DimenUtils.dip2px(mApplication.getInstance(),70),DimenUtils.dip2px(mApplication.getInstance(),70))
                                    .fitCenter()
                                    .into(mimageviewhead);    //头像
                        if(map.get("sex").equals("2")){
                             //  ("女");
                        mimageviewsex.setImageResource(R.mipmap.nv);   //性别头像
                          }else if (map.get("sex").equals("1")){
                            //  ("男");
                            mimageviewsex.setImageResource(R.mipmap.nan);
                         }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }
}
