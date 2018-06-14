package com.laomachaguan.Common;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

public class GerenxinxiActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mtvxinbie;     //性别
    private TextView mtvnc;        //昵称
//    private TextView mtvphone;   //手机号
//    private TextView mtvid;     //身份证号
//    private TextView mtvsimiao;  //所属寺庙
    private TextView mtvyonghuleixing;   //用户类型
//    private TextView mtvtime;      //注册时间
 private AvatarImageView mcircleview; //RoundedImageView头像
    private SharedPreferences sp;
    private String xb;
    private HashMap<String, String> map;
    private int screenWidth;
  public void init(){
    mtvxinbie = (TextView) findViewById(R.id.gerenxinxi_xingbie_tv);
    mtvnc=(TextView) findViewById(R.id.gerenxinxi_nichengz_tv);
//    mtvphone=(TextView) findViewById(R.id.gerenxinxi_phone_tv);
//    mtvid=(TextView) findViewById(R.id.gerenxinxi_shenfenz_tv);
//    mtvsimiao=(TextView) findViewById(R.id.gerenxinxi_shimiao_tv);
//    mtvyonghuleixing=(TextView) findViewById(R.id.gerenxinxi_yonghuleixing_tv);
//    mtvtime=(TextView) findViewById(R.id.gerenxinxi_time_tv);
    mcircleview=(AvatarImageView) findViewById(R.id.gerenxinxi_touxiang_cicleimageview);
    sp=getSharedPreferences("user",MODE_PRIVATE);
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.activity_gerenxinxi);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        getdata();  //数据交互方法
        screenWidth=this.getResources().getDisplayMetrics().widthPixels;
         init();
        //String id = "3232 1820000101 0010";  完整身份证
        //String show_id = id.substring(0, 4) + "**********" + id.substring(14); 身份证号的5到14位用*
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.gerenxinxi_back:
                finish();
                break;

        }
    }

    private static final String TAG = "GerenxinxiActivity";
    public void getdata(){
        if(!new LoginUtil().checkLogin(this)){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null ;
                try {
                    JSONObject js=new JSONObject();
                    js.put("user_id",sp.getString("user_id",""));
                    data1 = OkHttpUtils.post(Constants.User_Info_Ip)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg",ApisSeUtil.getMsg(js)).execute()
                            .body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (data1!=null&&!data1.equals("")){

                    map= AnalyticalJSON.getHashMap(data1);
                    if(map!=null&&map.get("code")==null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mtvnc.setText(map.get("pet_name"));
//                                mtvtime.setText(map.get("user_time"));
//                                mtvsimiao.setText(map.get("user_temple"));
//                                mtvid.setText(map.get("user_cid"));
//                                mtvphone.setText(map.get("user_phone"));
                                Glide.with(GerenxinxiActivity.this).load(map.get("user_image")).into(mcircleview);
                                if(map.get("sex").equals("1")){
                                    mtvxinbie.setText("男");
                                }else if (map.get("sex").equals("2")){
                                    mtvxinbie.setText("女");
                                }

                            }
                        });
                    }else if(map!=null&&map.get("code")!=null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GerenxinxiActivity.this, "用户信息获取失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

            }
        }).start();
    }


}

