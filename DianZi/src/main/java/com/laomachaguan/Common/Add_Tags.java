package com.laomachaguan.Common;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.laomachaguan.R.id.recycle;

/**
 * 作者：因陀罗网 on 2017/9/28 15:29
 * 公司：成都因陀罗网络科技有限公司
 */

public class Add_Tags extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageView img_back;
    private TextView tv_choose, tv_commit;
    private EditText edt_input;
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private TagAdapter adapter ;
    private int chooseType=-1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.add_tags);
        initView();

        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                getData();
            }
        });

    }

    private void getData() {//获取标签列表
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("m_id",Constants.M_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            LogUtil.e("全局标签列表加载" + js);

            OkHttpUtils.post(Constants.SpecList).tag(this).params("key", m.K())
                    .params("msg", m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                            ArrayList<HashMap<String,String >> list=new ArrayList<HashMap<String, String>>();
                            if (map != null) {

                                HashMap<String,String > m1=new HashMap<String, String>();
                                m1.put("name","类型一");
                                m1.put("spec",map.get("spec"));
                                HashMap<String,String > m2=new HashMap<String, String>();
                                m2.put("name","类型二");
                                m2.put("spec",map.get("spec_info"));
                                list.add(m1);
                                list.add(m2);
                                adapter.setNewData(list);
                            }
                        }

                        @Override
                        public void onAfter(@Nullable String s, @Nullable Exception e) {
                            super.onAfter(s, e);
                            swip.setRefreshing(false);
                        }
                    });
        }
    }

    private void initView() {
        img_back = (ImageView) findViewById(R.id.back);
        img_back.setOnClickListener(this);

        tv_choose = (TextView) findViewById(R.id.choose_type);
        tv_choose.setOnClickListener(this);

        tv_commit = (TextView) findViewById(R.id.commit);
        tv_commit.setOnClickListener(this);

        edt_input = (EditText) findViewById(R.id.edit_tag);//标签输入

        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);

        recyclerView = (RecyclerView) findViewById(recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new TagAdapter(new ArrayList<HashMap<String, String>>());
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void onClick(final View view) {
        int id = view.getId();
        if (id == R.id.back) {
            finish();
        }else if(id==R.id.choose_type){
            AlertDialog.Builder b=new AlertDialog.Builder(this);
            b.setItems(new String[]{"类型一","类型二"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if(i==0){
                        ((TextView) view).setText("类型一");
                        chooseType=1;
                    }else{
                        ((TextView) view).setText("类型二");
                        chooseType=2;
                    }
                }
            }).create().show();
        }else if(id==R.id.commit){
                if(chooseType==-1){
                    ToastUtil.showToastShort("请选择标签类型");
                    return;
                }
                if(edt_input.getText().toString().trim().equals("")){
                    ToastUtil.showToastShort("请输入标签名称");
                    return;
                }
                String url=chooseType==1?Constants.SpecAdd:Constants.Spec2Add;

                if(Network.HttpTest(this)){
                    JSONObject js=new JSONObject();
                    try {
                        js.put("m_id",Constants.M_id);
                        js.put("name",edt_input.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    LogUtil.e("标签增加：："+js);
                    OkHttpUtils.post(url).tag(this)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String,String> map=AnalyticalJSON.getHashMap(s);
                                    if(map!=null){
                                        if("000".equals(map.get("code"))){
                                            LogUtil.e("标签增加成功");
                                            edt_input.setText("");
                                            onRefresh();
                                        }
                                    }

                                }

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    ProgressUtil.show(Add_Tags.this,"","正在添加标签");
                                }

                                @Override
                                public void onAfter(@Nullable String s, @Nullable Exception e) {
                                    super.onAfter(s, e);
                                    ProgressUtil.dismiss();
                                }
                            });
                }
        }
    }

    @Override
    public void onRefresh() {
        getData();
    }

    private class TagAdapter extends BaseQuickAdapter<HashMap<String, String>> {
        public TagAdapter(List<HashMap<String, String>> data) {
            super(R.layout.tag_main_item, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final HashMap<String, String> map) {
            final int i = getData().indexOf(map);

                holder.setText(R.id.name, map.get("name"));



            final ArrayList<HashMap<String,String >> list=AnalyticalJSON.getList_zj(map.get("spec"));
            final TagFlowLayout flow = holder.getView(R.id.tags);
            final int dp8= DimenUtils.dip2px(Add_Tags.this,8);
            final int dp5= DimenUtils.dip2px(Add_Tags.this,5);

            flow.setAdapter(new com.zhy.view.flowlayout.TagAdapter<HashMap<String,String >>(list) {
                @Override
                public View getView(FlowLayout parent, int position, HashMap<String, String> map) {
                    final TextView textView = (TextView) LayoutInflater.from(Add_Tags.this).inflate(R.layout.order_tag_tv2, flow, false);
                    textView.setPadding(dp8, dp5, dp8, dp5);
                    if(i==0){
                        textView.setTextColor(ContextCompat.getColor(Add_Tags.this,R.color.mediumorchid));
                        textView.setBackgroundResource(R.drawable.tag_type2_shape);
                        textView.setText(map.get("name"));
                    }else{
                        textView.setTextColor(ContextCompat.getColor(Add_Tags.this,R.color.mediumorchid));
                        textView.setBackgroundResource(R.drawable.tag_type2_shape);
                        textView.setText(map.get("spec_name"));
                    }

                    return textView;
                }
            });
            flow.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, final int position, FlowLayout parent) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(Add_Tags.this);
                    String msg="确认删除{"+map.get("name")+"}["+list.get(position).get((i==0?"name":"spec_name"))+"]吗?";
                    builder.setMessage(msg)
                            .setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int l) {
                                    dialogInterface.dismiss();
                                    deleteTag(i,list,position);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
                    return true;
                }
            });

        }

        private void deleteTag(int i,ArrayList<HashMap<String,String>>list,int pos) {
            if(Network.HttpTest(Add_Tags.this)){
                JSONObject js=new JSONObject();
                try {
                    js.put("m_id",Constants.M_id);
                    js.put("id",list.get(pos).get("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApisSeUtil.M m=ApisSeUtil.i(js);
                LogUtil.e("标签删除：："+js+"  类型：："+i);
                String url=(i==0?Constants.SpecDelete:Constants.Spec2Delete);
                OkHttpUtils.post(url)
                        .tag(this).params("key",m.K())
                        .params("msg",m.M())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                    HashMap<String,String> map=AnalyticalJSON.getHashMap(s);
                                if(map!=null){
                                    if("000".equals(map.get("code"))){
                                        LogUtil.e("标签删除成功");
                                        onRefresh();
                                    }
                                }
                            }

                            @Override
                            public void onBefore(BaseRequest request) {
                                super.onBefore(request);
                                ProgressUtil.show(Add_Tags.this,"","正在删除标签");
                            }

                            @Override
                            public void onAfter(@Nullable String s, @Nullable Exception e) {
                                super.onAfter(s, e);
                                ProgressUtil.dismiss();
                            }
                        });
            }
        }
    }
}
