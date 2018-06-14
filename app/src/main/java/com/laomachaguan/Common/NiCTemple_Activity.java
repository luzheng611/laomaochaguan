package com.laomachaguan.Common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NiCTemple_Activity extends AppCompatActivity implements View.OnClickListener{
private TextView mtvtitle;
    private EditText medit;
    private String title;
    private Intent intent;
//    private Spinner mspinner;  //寺庙的下拉列表
    private RelativeLayout mrelativelayout; //昵称的RelativeLayout
   private String word;  //Edittext的值
//    private String templename;  //Spinner选中的寺庙
    private String httpcanshu; //请求的参数
    private String httpjk;  //请求的接口名
    private SharedPreferences sp;
    private List<HashMap<String, String>> names=new ArrayList<>();
    private Hodler hodler;
    private BaseAdapter adapter=new BaseAdapter() {
        @Override
        public int getCount() {
            return names.size() > 0 ? names.size() : 0;
        }
        @Override
        public Object getItem(int i) {
            return names.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null){
                hodler=new Hodler();
                view= LayoutInflater.from(NiCTemple_Activity.this).inflate(R.layout.temple_itme,null);
                hodler.mtvtemplename=(TextView) view.findViewById(R.id.temple_itme_tv);
                view.setTag(hodler);
            }else {
                hodler=(Hodler) view.getTag();
            }
            hodler.mtvtemplename.setText(names.get(i).get("te_name"));
            return view;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.activity_ni_cqian_mhua_t_);
        intent=getIntent();
        sp=getSharedPreferences("user",MODE_PRIVATE);
        mtvtitle=(TextView) findViewById(R.id.ncqmht_title);
        medit=(EditText) findViewById(R.id.ncqmht_edittext);
//        mspinner=(Spinner)findViewById(R.id.spinner_temple);
        mrelativelayout=(RelativeLayout)findViewById(R.id.ncqmht_relativelayout_nc);
        title= getIntent().getStringExtra("title");
        if(title.equals("昵称")){
            httpcanshu="pet_name";
            mrelativelayout.setVisibility(View.VISIBLE);
            httpjk= Constants.User_info_xiugainc;
            medit.setText(PreferenceUtil.getUserIncetance(this).getString("pet_name",""));
        }
//        else if(title.equals("所属寺庙")){
//            httpcanshu="temple";
//            mspinner.setVisibility(View.VISIBLE);
//            httpjk= Constants.User_info_xiugaitemple;
////            gettemplename();
//        }
        mtvtitle.setText(title);
//        mspinner.setAdapter(adapter);
//        mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Spinner spinner=(Spinner) adapterView;
//                String data=spinner.getItemAtPosition(i).toString();
//                HashMap<String,String> hashMap= AnalyticalJSON.getHashMap(data);
//                templename=hashMap.get("te_name");
//                //Toast.makeText(getApplicationContext(), "xxxx"+hashMap.get("id")+templename, Toast.LENGTH_LONG).show();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
    }
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.ncqmht_baochun:  //保存
                if(title.equals("昵称")){
                    if(medit.getText().toString().equals("")){
                        Toast.makeText(NiCTemple_Activity.this,"请输入保存内容",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        word=medit.getText().toString();    //保存的内容
                        modification() ; //修改昵称或寺庙的方法
                    }

                }
                break;
            case R.id.ncqmht_back:
                finish();
                break;
            case R.id.ncqmht_qingchu:  //清除
                medit.setText("");
                Toast.makeText(NiCTemple_Activity.this,"清除成功",Toast.LENGTH_SHORT).show();
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
                        js.put("user_id",sp.getString("user_id",""));
                        js.put("pet_name", word);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = OkHttpUtils.post(httpjk)
                            .params("key", ApisSeUtil.getKey())
                            .params( "msg",ApisSeUtil.getMsg(js))


                            .execute().body().string();
                    Log.d("ggggggggg",data);
                    HashMap<String,String> retur= AnalyticalJSON.getHashMap(data);
                    Log.d("hhhhhhhhhh",retur.toString());
                   if (retur.get("code").equals("000")){
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(NiCTemple_Activity.this,"修改信息成功",Toast.LENGTH_SHORT).show();
                               intent.putExtra("edit",word);
                               NiCTemple_Activity.this.setResult(2,intent);
                               Intent intent=new Intent("Mine");
                               sendBroadcast(intent);
                                finish();
                           }
                       });
                   }else {
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(NiCTemple_Activity.this,"修改信息失败",Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
//    public void gettemplename(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    String data = OkHttpUtils.post(Constants.Temples)
//                            .execute().body().string();
//                    Log.d("1111111111",data);
//                    final ArrayList<HashMap<String, String>> retur= AnalyticalJSON.getList(data,"Temple");
//                    Log.d("2222222222",retur+"");
//                    if (retur.size()>0){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                names.addAll(retur);
//                               // Log.d("寺庙列表长度：",names.size()+"");
//                                adapter.notifyDataSetChanged();
//                            }
//                        });
//                    }else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(NiCTemple_Activity.this,"无寺庙消息",Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
    class Hodler{
        TextView mtvtemplename;
    }
}
