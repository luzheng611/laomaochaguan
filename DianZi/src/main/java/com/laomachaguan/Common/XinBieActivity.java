package com.laomachaguan.Common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.StatusBarCompat;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class XinBieActivity extends AppCompatActivity implements View.OnClickListener{
private RadioGroup mrgroup;
 private String xinbie;
    private Intent intent;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.activity_xin_bie);
        sp=getSharedPreferences("user",MODE_PRIVATE);
        intent=getIntent();
        mrgroup=(RadioGroup) findViewById(R.id.radioGroup);
        mrgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //获取变更后的选中项的ID
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton)XinBieActivity.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
               xinbie=rb.getText().toString();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.xinbie_back:
                finish();
                break;
            case R.id.xinbie_baochun:      //保存
                if(xinbie==null){
                    Toast.makeText(XinBieActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
                }else {
                    modification();  //数据交互修改性别的方法
              }
                break;
        }
    }
    public void modification(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js=new JSONObject();

                    try {
                        js.put("user_id", sp.getString("user_id", ""));
                        js.put("sex",  xinbie.equals("男") ? "1" : "2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkHttpUtils.post(Constants.User_info_xiugaixb)
                            .params("key", ApisSeUtil.getKey())
                            .params("msg",ApisSeUtil.getMsg(js))
                            .execute().body().string();
                    HashMap<String,String> retur= AnalyticalJSON.getHashMap(data);
                    if (retur.get("code").equals("000")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(XinBieActivity.this,"性别修改成功",Toast.LENGTH_SHORT).show();
                                intent.putExtra("xinbie", xinbie);
                                XinBieActivity.this.setResult(1, intent);
                                 finish();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(XinBieActivity.this,"性别修改失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
