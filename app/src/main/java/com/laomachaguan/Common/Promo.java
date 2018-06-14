package com.laomachaguan.Common;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/6/7 20:00
 * 公司：成都因陀罗网络科技有限公司
 */

public class Promo extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_commit,tv_skip;
    private EditText edt_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.promo);
        tv_commit= (TextView) findViewById(R.id.commit);
        tv_skip=(TextView) findViewById(R.id.skip);
        edt_code= (EditText) findViewById(R.id.id);


    }

    @Override
    public void onClick(View view) {
        final Intent intent=new Intent();
        switch (view.getId()){
            case R.id.skip:
                intent.setClass(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.commit:
                if(!Network.HttpTest(this)){
                    ToastUtil.showToastShort("请检查网络");
                    return;
                }
                if(edt_code.getText().toString().trim().equals("")){
                    ToastUtil.showToastShort("请输入邀请码");
                    return;
                }
                if(PreferenceUtil.getUserIncetance(this).getString("phone","").equals(edt_code.getText().toString().trim())){
                    ToastUtil.showToastShort("不能输入自己的邀请码");
                    edt_code.setText("");
                    return;
                }
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("m_id", Constants.M_id);
                    jsonObject.put("promo",edt_code.getText().toString().trim());
                    jsonObject.put("user_id",PreferenceUtil.getUserIncetance(this).getString("user_id",""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m=ApisSeUtil.i(jsonObject);
                LogUtil.e("js:::"+jsonObject);
                HttpParams httpParams=new HttpParams("key",m.K());
                httpParams.put("msg",m.M());
                OkHttpUtils.post(Constants.PushPromoCode)
                        .params(httpParams)
                        .execute(new StringCallback() {


                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(Promo.this,"","正在验证");
                            }

                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                if(s!=null){
                                    HashMap<String,String > map= AnalyticalJSON.getHashMap(s);
                                    if(map!=null){
                                        if("000".equals(map.get("code"))){
                                            ToastUtil.showToastShort("您的邀请码已验证成功");
                                            intent.setClass(Promo.this,MainActivity.class);
                                            startActivity(intent);
                                            Intent i=new Intent("Mine");
                                            sendBroadcast(i);
                                        }else if("002".equals(map.get("code"))){
                                            ToastUtil.showToastShort("该邀请码不存在，请重新填写");
                                            edt_code.setText("");
                                        }
                                    }else{
                                        ToastUtil.showToastShort("网络请求失败，请检查网络后重新尝试");
                                    }
                                }else{
                                    ToastUtil.showToastShort("网络请求失败，请检查网络后重新尝试");
                                }
                            }

                            @Override
                            public void onAfter(@Nullable String s, @Nullable Exception e) {
                                super.onAfter(s, e);
                                ProgressUtil.dismiss();
                            }


                        });
                break;
        }
    }
}
