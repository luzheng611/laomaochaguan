package com.laomachaguan.Common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class AnquanActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mtvnc;
    private TextView mxinbie;
    private TextView msimiao;
    private SharedPreferences sp;

    private String nc;
    private String xinbie;
    private String simiao;
    private HashMap<String, String> map;
    private TextView loginState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anquan);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));

        sp = getSharedPreferences("user", MODE_PRIVATE);
        mtvnc = (TextView) findViewById(R.id.anquan_nichen_tv);
        mtvnc.setText(PreferenceUtil.getUserIncetance(this).getString("pet_name",""));
        mxinbie = (TextView) findViewById(R.id.anquan_xinbie_tv);
        if (PreferenceUtil.getUserIncetance(this).getString("sex","").equals("1")) {
            mxinbie.setText("男");
        } else if (PreferenceUtil.getUserIncetance(this).getString("sex","").equals("2")) {
            mxinbie.setText("女");
        }

//        msimiao = (TextView) findViewById(R.id.anquan_simiao_tv);
        loginState= (TextView) findViewById(R.id.zhanghaoyuanquan_loginState);
        getdatafromserve();
        if(!TextUtils.isEmpty(sp.getString("user_id",""))){
            loginState.setText("账号已登录，无安全风险");
        }else{
            loginState.setText("账号未登录");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.anquan_back:
                finish();
                break;
            case R.id.anquan_nichen_back:
                Intent intentnc = new Intent(AnquanActivity.this, NiCTemple_Activity.class);
                intentnc.putExtra("title", "昵称");
                startActivityForResult(intentnc, 1);
                break;
            case R.id.anquan_xinbie_back:
                Intent intentxb = new Intent(AnquanActivity.this, XinBieActivity.class);
                startActivityForResult(intentxb, 2);
                break;
//            case R.id.anquan_simiao_back:
//                Intent intentsimiao = new Intent(AnquanActivity.this, NiCTemple_Activity.class);
//                intentsimiao.putExtra("title", "所属寺庙");
//                startActivityForResult(intentsimiao, 3);
//                break;
            case R.id.anquan_mima_back:
                Intent inten =new Intent(this,FindPassword.class);
                startActivity(inten);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {
                nc = data.getStringExtra("edit");
                if (!nc.equals("")) {
                    mtvnc.setText(nc);
                    SharedPreferences.Editor ed=sp.edit();
                    ed.putString("pet_name",nc);
                    ed.apply();
                }
            } else if (requestCode == 2) {
                xinbie = data.getStringExtra("xinbie");
                if (!xinbie.equals("")) {
                    mxinbie.setText(xinbie);
                }
            } else if (requestCode == 3) {
                simiao = data.getStringExtra("edit");
                if (!simiao.equals("")) {
                    msimiao.setText(simiao);
                }
            }
        }
    }

    private static final String TAG = "AnquanActivity";
    public void getdatafromserve() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String data1 = null;
                try {
                    JSONObject js=new JSONObject();
                    js.put("user_id", sp.getString("user_id", ""));
                    data1 = OkHttpUtils.post(Constants.User_Info_Ip)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg",ApisSeUtil.getMsg(js)).execute().body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (data1 != null && !data1.equals("")) {
                    Log.d("个人信息数据为：", data1);
                    map = AnalyticalJSON.getHashMap(data1);
                    if (map != null && map.get("code") == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mtvnc.setText(map.get("pet_name"));
                                if (map.get("sex").equals("1")) {
                                    mxinbie.setText("男");
                                } else if (map.get("sex").equals("2")) {
                                    mxinbie.setText("女");
                                }
                            }
                        });
                    }
                }else if(map!=null&&map.get("code")!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AnquanActivity.this, "用户信息获取失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
