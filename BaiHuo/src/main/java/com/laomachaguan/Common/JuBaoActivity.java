package com.laomachaguan.Common;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


public class JuBaoActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText medittext;
    private TextView mtextview;
    private EditText title;
    private static final String TAG = "JuBaoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.activity_ju_bao);
        medittext = (EditText) findViewById(R.id.jubao_edittext);
        mtextview = (TextView) findViewById(R.id.jubao_textview);
        title = (EditText) findViewById(R.id.jubao_title);
        medittext.addTextChangedListener(new TextWatcher() {   //文本框监听事件
            private int editStart;
            private int editEnd;
            private CharSequence tem;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tem = charSequence;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mtextview.setText(String.valueOf(150 - (tem.length())));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editStart = medittext.getSelectionStart();
                editEnd = medittext.getSelectionEnd();
                if (tem.length() > 150) {
                    Toast.makeText(JuBaoActivity.this, "输入的字数在150以内", Toast.LENGTH_SHORT).show();
                    editable.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    medittext.setText(editable);
                    medittext.setSelection(tempSelection);
                }
            }
        });
    }

    @Override
    public void onClick(final View view) {
        int id = view.getId();
        switch (id) {
            case R.id.jubao_back:
                finish();
                break;
            case R.id.jubao_tijiao:
                final String t = title.getText().toString();
                final String tijiao = medittext.getText().toString();
                if (t.equals("") || tijiao.equals("")) {
                    Toast.makeText(JuBaoActivity.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                view.setEnabled(false);
                ProgressUtil.show(this, "", "正在提交");
                if (LoginUtil.checkLogin(this)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject js = new JSONObject();
                                js.put("user_id", PreferenceUtil.getUserIncetance(JuBaoActivity.this)
                                        .getString("user_id", ""));
                                js.put("m_id",Constants.M_id);
                                js.put("title", t);
                                js.put("contents", tijiao);
                                String data = OkHttpUtils.post(Constants.Suggest_Ip).tag(TAG)
                                        .params("key", ApisSeUtil.getKey())
                                        .params("msg", ApisSeUtil.getMsg(js)).execute().body().string();

                                if (!data.equals("")) {
                                    Log.w(TAG, "run: " + data);
                                    final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                    if (map != null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if ("000".equals(map.get("code"))) {
                                                    Toast.makeText(JuBaoActivity.this, "信息已提交，感谢您的建议", Toast.LENGTH_SHORT).show();
                                                    ProgressUtil.dismiss();
                                                    finish();
                                                } else {
                                                    Toast.makeText(JuBaoActivity.this, "信息提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                                    ProgressUtil.dismiss();
                                                    view.setEnabled(true);
                                                }
                                            }
                                        });

                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroy();

    }
}
