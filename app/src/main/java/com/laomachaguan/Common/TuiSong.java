package com.laomachaguan.Common;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/9/30 18:32
 * 公司：成都因陀罗网络科技有限公司
 */

public class TuiSong extends AppCompatActivity {
        private ImageView img_back;
    private EditText edt_title,edt_content;
    private TextView commit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.activity_tuisong);
        img_back= (ImageView) findViewById(R.id.back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edt_title= (EditText) findViewById(R.id.title);
        edt_title.setText(getIntent().getStringExtra("title"));
        edt_content= (EditText) findViewById(R.id.edittext);
        edt_content.setText(getIntent().getStringExtra("abs"));
        commit= (TextView) findViewById(R.id.push);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_title.getText().toString().trim().equals("")){
                    ToastUtil.showToastShort("请输入推送标题");
                    return;
                }
                if(edt_content.getText().toString().trim().equals("")){
                    ToastUtil.showToastShort("请输入推送内容");
                    return;
                }
                if(Network.HttpTest(TuiSong.this)){
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id",Constants.M_id);
                        js.put("id",getIntent().getStringExtra("id"));
                        js.put("type",getIntent().getStringExtra("type"));
                        js.put("title",edt_title.getText().toString().trim());
                        js.put("abstract",edt_content.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    LogUtil.e("推送：："+js);
                    OkHttpUtils.post(Constants.AdminPush)
                            .tag(this)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String,String > map= AnalyticalJSON.getHashMap(s);
                                    if(map!=null){
                                        if("000".equals(map.get("code"))){
                                            ToastUtil.showToastShort("推送成功");
                                            finish();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
}
