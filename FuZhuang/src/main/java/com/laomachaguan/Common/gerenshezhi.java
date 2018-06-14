package com.laomachaguan.Common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.Model_Order.MyShouHuoAddress;
import com.laomachaguan.R;
import com.laomachaguan.Utils.ACache;
import com.laomachaguan.Utils.CleanMessageUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.mApplication;


public class gerenshezhi extends AppCompatActivity implements View.OnClickListener {
    private TextView mtvhuancz;
    private AlertDialog customDia;
    private View viewDia;
    private ACache aCache;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.activity_gerenshezhi);
        aCache = ACache.get(this);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        mtvhuancz = (TextView) findViewById(R.id.shez_huanczi_tv);
        try {
            mtvhuancz.setText(CleanMessageUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(PreferenceUtil.getUserIncetance(this).getString("role","").equals("3")){
            findViewById(R.id.shez_address).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.shez_address:
                if (!LoginUtil.checkLogin(this)) {
                    return;
                }
                Intent intent = new Intent(this, MyShouHuoAddress.class);
                startActivity(intent);
                break;
            case R.id.shez_back:
                finish();
                break;
            case R.id.shez_geren_back:
                if (!LoginUtil.checkLogin(this)) {
                    return;
                }
                HeadToInfo.goToUserDetail(this, PreferenceUtil.getUserId(this));
                break;
            case R.id.shez_anquan_back:
                if (!LoginUtil.checkLogin(this)) {
                    return;
                }
                Intent intentaq = new Intent(this, AnquanActivity.class);
                intentaq.putExtra("user_id", "");
                startActivity(intentaq);
                break;
            case R.id.shez_guanyu_back:

                Intent intentgy = new Intent(this, GanyuActivity.class);
                intentgy.putExtra("user_id", "");
                startActivity(intentgy);
                break;
            case R.id.shez_huanc_back:
                if (mtvhuancz.getText().toString().equals("0.00M")) {
                    Toast.makeText(mApplication.getInstance(), "当前没有缓存数据", Toast.LENGTH_SHORT).show();
                } else {
                    viewDia = LayoutInflater.from(this).inflate(R.layout.qingchu_dialog, null);
                    customDia = new AlertDialog.Builder(this).setView(viewDia).create();
                    customDia.show();
                }
                break;
            case R.id.qingchu_dialog_no:
                customDia.dismiss();
                break;
            case R.id.qingchu_dialog_yes:
                CleanMessageUtil.clearAllCache(this);
                try {
                    mtvhuancz.setText(CleanMessageUtil.getTotalCacheSize(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                customDia.dismiss();
                Toast.makeText(mApplication.getInstance(), "缓存已清除至" + mtvhuancz.getText().toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.shez_jubao_back:
                if (!LoginUtil.checkLogin(this)) {
                    return;
                }
                Intent intentjb = new Intent(this, JuBaoActivity.class);
                intentjb.putExtra("user_id", "");
                startActivity(intentjb);
                break;
//            case R.id.tuochu_bnt:
//                if(!new LoginUtil().checkLogin(this)){
//                    return;
//                }
//                SharedPreferences liveSet=getSharedPreferences("liveSetting",MODE_PRIVATE);
//                SharedPreferences.Editor ed1=liveSet.edit();
//                ed1.putString("last_title","");
//                ed1.putString("last_info","");
//                ed1.apply();
//                SharedPreferences.Editor ed = sp.edit();
//                ed.putString("uid", "");
//                ed.putString("user_id", "");
//                ed.putString("head_path", "");
//                ed.putString("head_url", "");
//                ed.putString("Live_state", "");
//                ed.putString("sex", "");
//                ed.putString("pet_name", "");
//                aCache.remove("head_" + sp.getString("user_id", ""));
//                ed.apply();
//                Intent intent=new Intent("Mine");
//
        }
    }
}

