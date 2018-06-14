package com.laomachaguan.Common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/3.
 */
public class User_Detail extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog progresDialog;
    private AlertDialog dialog;
    private ImageView head,sex;
    private RelativeLayout headlayout;
    private static final String TAG = "User_Detail";
    private TextView name,sign,phone,time;
    private Button delete;
    private String ID;
    private boolean is1=false,is2=false;
    private boolean isDeleted=false;
    private HashMap<String ,String >map;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.user_xiangxi_zi_liao);
        ID=getIntent().getStringExtra("id");
        initView();
        checkNetwork();
        getData();
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
                    String data= OkHttpUtils.post(Constants.User_Info_Ip)
                            .params("key",m.K())
                            .params("msg",m.M()).execute().body().string();
                    if(!data.equals("")){
                        Log.w(TAG, "run: 获取的用户资料"+data );
                        map= AnalyticalJSON.getHashMap(data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(map!=null){
                                    if(!map.get("role").equals("3")){
                                        delete.setVisibility(View.VISIBLE);
                                    }
                                    Glide.with(User_Detail.this).load(map.get("user_image")).override(DimenUtils.dip2px(User_Detail.this,70),DimenUtils.dip2px(User_Detail.this,70))
                                            .centerCrop().into(head);
                                    name.setText(map.get("pet_name"));
                                    sign.setText(map.get("signature").equals("")?"这个人很懒，还没有个人签名噢":map.get("signature"));
                                    phone.setText(map.get("phone").equals("")?"该用户尚未绑定手机号码":map.get("phone"));
                                    time.setText(map.get("creat_time").equals("")?"创建时间获取失败":map.get("creat_time"));

                                }
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
     * 初始化
     */
    private void initView() {

        time= (TextView) findViewById(R.id.time_tv);
        head= (ImageView) findViewById(R.id.head_imageview);
        sex= (ImageView) findViewById(R.id.sex_imageview);
        name= (TextView) findViewById(R.id.nicheng_tv);
        sign= (TextView) findViewById(R.id.sign_tv);
        headlayout= (RelativeLayout) findViewById(R.id.head_layout);
        headlayout.setOnClickListener(this);
        phone= (TextView) findViewById(R.id.phone_tv);

        delete= (Button) findViewById(R.id.delete_bnt);


    }

    /**
     * 修改昵称和签名
     */
    private void uploadNameandInfo() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.change_name_info, null);
        final EditText name1 = (EditText) view.findViewById(R.id.changeTitle);
        name1.setHint("请输入昵称");
        final  EditText info = (EditText) view.findViewById(R.id.changeInfo);
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
                if(progresDialog==null){
                    progresDialog = ProgressDialog.show(User_Detail.this, null, "正在提交修改.....");
                }else{
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
                                js.put("pet_name",name1.getText().toString());
                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m=ApisSeUtil.i(js);
                            String data= OkHttpUtils.post(Constants.User_info_xiugainc)
                                    .params("key",m.K())
                                    .params("msg",m.M()).execute().body().string();
                            if(!data.equals("")){
                                HashMap<String ,String > map= AnalyticalJSON.getHashMap(data);
                                if(map!=null&&"000".equals(map.get("code"))){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            name.setText(name1.getText().toString());
                                            SharedPreferences sp=PreferenceUtil.getUserIncetance(getApplicationContext());
                                            SharedPreferences.Editor ed=sp.edit();
                                            ed.putString("pet_name",name1.getText().toString());
                                            is1=true;
                                            if(is2){
                                                progresDialog.dismiss();
                                                dialog.dismiss();
                                                is1=false;
                                                is2=false;
                                            }
                                        }
                                    });
                                }
                            }
                            JSONObject js1 = new JSONObject();
                            try {
                                js1.put("m_id", Constants.M_id);
                                js1.put("signature",info.getText().toString());
                                js1.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApisSeUtil.M m1=ApisSeUtil.i(js1);
                            String data1= OkHttpUtils.post(Constants.SignChange)
                                    .params("key",m1.K())
                                    .params("msg",m1.M()).execute().body().string();
                            if(!data1.equals("")){
                                HashMap<String ,String > map= AnalyticalJSON.getHashMap(data1);
                                if(map!=null&&"000".equals(map.get("code"))){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sign.setText(info.getText().toString());
                                            SharedPreferences sp=PreferenceUtil.getUserIncetance(getApplicationContext());
                                            SharedPreferences.Editor ed=sp.edit();
                                            ed.putString("sign",info.getText().toString());
                                            is2=true;
                                            if(is1&&is2){
                                                progresDialog.dismiss();
                                                dialog.dismiss();
                                                is1=false;
                                                is2=false;
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
                                    Toast.makeText(User_Detail.this, "修改失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
        switch (v.getId()){
            case R.id.head_layout:
//            uploadNameandInfo();
                break;
            case R.id.xiangxiziliao_back:
                Intent data=new Intent();
                data.putExtra("name",name.getText().toString());
                data.putExtra("sign",sign.getText().toString());
                setResult(0x00,data);
                finish();
                break;
            case R.id.delete_bnt://删除用户
                checkNetwork();
                v.setEnabled(false);
                JSONObject js=new JSONObject();
                try {
                    js.put("user_id",ID);
                    js.put("m_id",Constants.M_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final ApisSeUtil.M m=ApisSeUtil.i(js);
                ProgressUtil.show(this,"","请稍等");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String data= OkHttpUtils.post(Constants.User_Delete_IP)
                                    .params("key",m.K()).params("msg",m.M())
                                    .execute().body().string();
                            if(!data.equals("")){
                                final HashMap<String ,String > m= AnalyticalJSON.getHashMap(data);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(m!=null&&"000".equals(m.get("code"))){
                                            Toast.makeText(User_Detail.this, "该用户已被删除成功", Toast.LENGTH_SHORT).show();
                                            Intent data=new Intent();
                                            isDeleted=true;
                                            data.putExtra("isDeleted",isDeleted);
                                            setResult(0x00,data);
                                            ProgressUtil.dismiss();
                                            finish();
                                        }else{
                                            ProgressUtil.dismiss();
                                            Toast.makeText(User_Detail.this, "用户删除失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                            v.setEnabled(true);
                                        }
                                    }
                                });

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
