package com.laomachaguan.Common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import java.net.SocketTimeoutException;

/**
 * Created by Administrator on 2016/6/22.
 */
public class Mine_gerenziliao extends AppCompatActivity implements OnClickListener {
    private EditText petName ;
    private RadioButton sex1,sex2;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;
    private Button commit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.mine_gerenziliao);
        initView();

        if(ZhuCe.instance!=null){
            ZhuCe.instance.finish();
        }

    }

    private void initView() {
        sp = getSharedPreferences("user", MODE_PRIVATE);
        findViewById(R.id.mine_gerenziliao_back).setOnClickListener(this);
        commit= (Button) findViewById(R.id.mine_gerenziliao_commit);
        commit.setOnClickListener(this);
        petName = (EditText) findViewById(R.id.mine_gerenziliao_petname);



        sex1= (RadioButton) findViewById(R.id.sex1);
        sex2= (RadioButton) findViewById(R.id.sex2);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    private static final String TAG = "Mine_gerenziliao";
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mine_gerenziliao_back) finish();
        if (v.getId() == R.id.mine_gerenziliao_commit) {
            Log.e(TAG, "onClick: 男："+sex1.isChecked()+"nv："+sex2.isChecked() );
            if ((!sex1.isChecked()&&!sex2.isChecked())
                    || "".equals(petName.getText().toString())) {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                return;
            }
            final ProgressDialog p = new ProgressDialog(this);
            p.isIndeterminate();
            p.setMessage("正在提交用户信息，请稍等");
            p.setCanceledOnTouchOutside(false);
            p.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject js=new JSONObject();
                        js.put("user_id", sp.getString("user_id", ""));
                        js.put("pet_name", petName.getText().toString());
                        js.put("sex",sex1.isChecked()?"1":"2");
                        js.put("m_id",Constants.M_id);
                        String data = OkHttpUtils.post(Constants.Mine_Grzl_IP)
                                .tag(TAG)
                                .params("key", ApisSeUtil.getKey())
                                .params("msg",ApisSeUtil.getMsg(js))
                                .execute().body().string();
                        if (!data.equals("") && AnalyticalJSON.getHashMap(data)!=null&&"000".equals(AnalyticalJSON.getHashMap(data).get("code"))){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    p.dismiss();
                                    Toast.makeText(Mine_gerenziliao.this, "用户信息提交成功", Toast.LENGTH_SHORT).show();
                                    ed = sp.edit();

                                    ed.putString("sex",sex1.isChecked()?"1":"2");
                                    ed.putString("pet_name", petName.getText().toString());
                                    ed.apply();

                                    Log.w(TAG, "run: 性别保存"+sp.getString("sex","") );
                                    Intent intent=new Intent("Mine");


                                    sendBroadcast(intent);


                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Mine_gerenziliao.this, "用户信息提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                                    p.dismiss();
                                }
                            });
                        }
                    } catch (SocketTimeoutException e){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Mine_gerenziliao.this, "连接服务器超时，请检查网络连接", Toast.LENGTH_SHORT).show();
                                p.dismiss();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch ( IllegalStateException e){

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
