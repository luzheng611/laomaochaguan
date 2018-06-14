package com.laomachaguan.Common;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.Editor.RichEditorActivity;
import com.laomachaguan.Model_Order.Order_Tab;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.ScaleImageUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.View.mRecyclerView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.request.BaseRequest;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Response;

import static android.view.View.GONE;

/**
 * 作者：因陀罗网 on 2017/6/8 02:32
 * 公司：成都因陀罗网络科技有限公司
 */

public class Good_Handle extends AppCompatActivity implements View.OnClickListener {
    public static final int ResultCode = 1111;
    private static final int MaxSize = 3;
    private int pintuan = 2;//2开启  1关闭
    private String tag = "";//分类名称
    public static final int REQUEST_CODE_CHOOSE = 111;
    private mRecyclerView recycle;
    private EditText edt_name, edt_cost, edt_money, edt_abs, edt_content, edt_pintuan_money;
    private TextView tv_commit, tv_tag, tv_open, tv_close;
    private ImageView iv_back;
    private NinePreViewAdapter adapter;
    private ArrayList<Uri> uriList;
    private boolean isUpdate = false;
    private HashMap<String, String> oldMap;
    private String html="";

    private TextView tags_total, tags_manage_spec,bianji;
    private WebView web;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.good_fabu_update);

        initView();
    }

    private void initView() {
        bianji= (TextView) findViewById(R.id.bianji);
        bianji.setOnClickListener(this);
        tags_total= (TextView) findViewById(R.id.tags);
        tags_total.setOnClickListener(this);

        tags_manage_spec= (TextView) findViewById(R.id.tags_manage);
        tags_manage_spec.setOnClickListener(this);


        edt_abs = (EditText) findViewById(R.id.abs);
        recycle = (mRecyclerView) findViewById(R.id.recycle);
        recycle.setLayoutManager(new GridLayoutManager(this, 3));
        edt_content = (EditText) findViewById(R.id.content);
        edt_name = (EditText) findViewById(R.id.name);
        edt_cost = (EditText) findViewById(R.id.cost);
        edt_money = (EditText) findViewById(R.id.money);
        tv_tag = (TextView) findViewById(R.id.tag);
        tv_commit = (TextView) findViewById(R.id.button);
        tv_tag.setOnClickListener(this);
        tv_commit.setOnClickListener(this);
        tv_open = (TextView) findViewById(R.id.open);
        tv_open.setOnClickListener(this);

        edt_pintuan_money = (EditText) findViewById(R.id.pintuan_money);
        tv_close = (TextView) findViewById(R.id.close);
        tv_close.setOnClickListener(this);
        iv_back = (ImageView) findViewById(R.id.back);
        iv_back.setOnClickListener(this);
        ArrayList<String> a = new ArrayList<>();
        a.add("add");
        adapter = new NinePreViewAdapter(this, a);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recycle.setAdapter(adapter);

        tv_open.performClick();
        Tiny.getInstance().init(getApplication());

        if (getIntent().getStringExtra("type").equals("update")) {
            isUpdate = true;
            JSONObject js = new JSONObject();
            try {
                js.put("order_id", getIntent().getStringExtra("id"));
                js.put("m_id", Constants.M_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            OkHttpUtils.post(Constants.Order_detail)
                    .params("key", m.K())
                    .params("msg", m.M()).execute(new StringCallback() {


                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    ProgressUtil.show(Good_Handle.this, "", "正在加载");
                }

                @Override
                public void onSuccess(String s, Call call, Response response) {
                    oldMap = AnalyticalJSON.getHashMap(s);
                    if (oldMap != null) {
                        LogUtil.e(oldMap.toString());
                        edt_name.setText(oldMap.get("title"));
                        ArrayList<String> l = new ArrayList<String>();
                        if (!oldMap.get("image1").equals("")) {
                            l.add(oldMap.get("image1"));
                        }
                        if (!oldMap.get("image2").equals("")) {
                            l.add(oldMap.get("image2"));
                        }
                        if (!oldMap.get("image3").equals("")) {
                            l.add(oldMap.get("image3"));
                        }
                        adapter.getData().addAll(adapter.getData().indexOf("add"), l);
                        if(adapter.getData().size()>3){
                            adapter.getData().remove("add");
                        }
                        adapter.notifyDataSetChanged();
                        edt_cost.setText(oldMap.get("cost"));
                        edt_money.setText(oldMap.get("money"));
                        edt_abs.setText(oldMap.get("abstract"));
                        tv_commit.setText("提交更新");
                        ((TextView) findViewById(R.id.title)).setText("商品修改");
                        tv_tag.setText(oldMap.get("name"));
                        tv_tag.setTag(Order_Tab.typeIdList.get(Order_Tab.typeNameList.indexOf(oldMap.get("name"))));
                        if (oldMap.get("open").equals("2")) {
                            tv_open.performClick();
                        } else {
                            tv_close.performClick();
                        }
                        ((EditText) findViewById(R.id.stock)).setText(oldMap.get("stock").equals("")?"0":oldMap.get("stock"));
                        edt_pintuan_money.setText(oldMap.get("price"));
//                        if(oldMap.get("contents1")!=null&&!oldMap.get("contents1").equals("")){
//                            edt_content.setText(oldMap.get("contents1"));
//                            LogUtil.e("手机操作：："+oldMap.get("contents1"));
//                        }
                        if(oldMap.get("contents")!=null){
                            LogUtil.e(" HTML ：："+oldMap.get("contents"));
//                            ((TextView) findViewById(R.id.tv_shangpin)).setText("商品详情(请在PC端后台修改)");
                            findViewById(R.id.inputlayout).setVisibility(GONE);
                            html=oldMap.get("contents");

                        }
//

                    }
                }

                @Override
                public void onAfter(@Nullable String s, @Nullable Exception e) {
                    super.onAfter(s, e);
                    ProgressUtil.dismiss();
                }


            });
        }else{
            tags_manage_spec.setVisibility(GONE);
        }
        if(Build.VERSION.SDK_INT>=23){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    ||checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},123);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            ArrayList<Uri> l = (ArrayList<Uri>) Matisse.obtainResult(data);
            ArrayList<String> list = new ArrayList<>();
            for (Uri uri : l) {
                list.add(uri.toString());
            }
            if (adapter.getData().contains("add")) {
                int i = adapter.getData().indexOf("add");

                adapter.getData().addAll(i, list);
                LogUtil.e(adapter.getData().size() + "");
                if (adapter.getData().size() > MaxSize) {
                    LogUtil.e(adapter.getData().size() + "  add     ");
                    adapter.getData().remove("add");
                }
                adapter.notifyDataSetChanged();
            } else {
                LogUtil.e("都没有添加图片了怎么进来");
            }
        }else if(requestCode==666){//编辑器回调
            if(data!=null&&data.getStringExtra("html")!=null&&!data.getStringExtra("html").equals("")){
                LogUtil.e("是否相同：：；"+html.equals(data.getStringExtra("html")));
                LogUtil.e(html);
                LogUtil.e("html：：；"+data.getStringExtra("html"));
                html=data.getStringExtra("html");
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bianji://进入详情编辑页
                Intent intent=new Intent(this, RichEditorActivity.class);
                intent.putExtra("html",html);
                startActivityForResult(intent,666);
                break;
            case R.id.tags:
                Intent i=new Intent(this,Add_Tags.class);
                startActivity(i);
                break;
            case R.id.tags_manage:

                Intent i1=new Intent(this,Good_Type_Manager.class);
                i1.putExtra("id",getIntent().getStringExtra("id"));
                startActivity(i1);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.button:
                if (!Network.HttpTest(this)) {
                    return;
                }
                if (edt_name.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请填写商品名称");
                    return;
                }
                if (edt_money.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请填写售价");
                    return;
                }

                if (tv_tag.getText().toString().trim().equals("选择分类")) {
                    ToastUtil.showToastShort("请选择分类");
                    return;
                }
                String cost=edt_cost.getText().toString();
                LogUtil.e("原价小数点后位数：："+(cost.length()-cost.indexOf(".")-1));
                if (cost.startsWith(".") || cost.endsWith(".") ||
                        (cost.startsWith("0") && !cost.contains("."))|| (cost.equals("0.0"))||(cost.contains(".")&&(cost.length()-cost.indexOf(".")-1)>2)) {
                    ToastUtil.showToastShort("请输入正确格式的原价,最多两位小数");
                    return;//||cost.split(".")[1].length()>2
                }
                String edtmoney=edt_money.getText().toString();
                LogUtil.e("售价小数点后位数：："+(edtmoney.length()-edtmoney.indexOf(".")-1));
                if (edtmoney.startsWith(".") || edtmoney.endsWith(".") ||
                        (edtmoney.startsWith("0") && !edtmoney.contains("."))
                        || (edtmoney.equals("0.0"))||(edtmoney.contains(".")&&(edtmoney.length()-edtmoney.indexOf(".")-1)>2)) {

                    ToastUtil.showToastShort("请输入正确格式的售价,最多两位小数");
                    return;
                }

                String ptmoney=edt_pintuan_money.getText().toString();
                LogUtil.e("拼团价小数点后位数：："+(ptmoney.length()-ptmoney.indexOf(".")-1));
                if(pintuan==2){
                    if (ptmoney.startsWith(".") || ptmoney.endsWith(".") ||
                            (ptmoney.startsWith("0") && !ptmoney.contains("."))
                            || (ptmoney.equals("0.0"))||(ptmoney.contains(".")&&(ptmoney.length()-ptmoney.indexOf(".")-1)>2)) {
                        ToastUtil.showToastShort("请输入正确格式的拼团价,最多两位小数");
                        return;//||ptmoney.split(".")[1].length()>2
                    }
                }
                if (pintuan == 2 && edt_pintuan_money.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请输入拼团价或者关闭拼团");
                    return;
                }
                if (!edt_cost.getText().toString().equals("") && Double.valueOf(edt_cost.getText().toString()) <= Double.valueOf(edt_money.getText().toString())) {
                    ToastUtil.showToastShort("售价必须小于原价");
                    return;
                }
                if (pintuan == 2 && !edt_pintuan_money.getText().toString().equals("") && Double.valueOf(edt_pintuan_money.getText().toString()) >= Double.valueOf(edt_money.getText().toString())) {
                    ToastUtil.showToastShort("拼团价必须小于售价");
                    return;
                }
                final JSONObject js = new JSONObject();
                try {
                    if(isUpdate){
                        if (((EditText) findViewById(R.id.stock)).getText().toString().trim().equals("")
                                ) {
                            ToastUtil.showToastShort("请填写库存数量");
                            return;
                        }
                        js.put("id",getIntent().getStringExtra("id"));
                    }else{
                        if (((EditText) findViewById(R.id.stock)).getText().toString().trim().equals("")
                                ||Integer.valueOf(((EditText) findViewById(R.id.stock)).getText().toString().trim())<1) {
                            ToastUtil.showToastShort("库存数量必须填写并且超过1件");
                            return;
                        }
                    }
                    js.put("stock",((EditText) findViewById(R.id.stock)).getText().toString().trim());
                    js.put("m_id", Constants.M_id);
                    js.put("title", edt_name.getText().toString().trim());
                    js.put("abstract", edt_abs.getText().toString().trim());
                    js.put("contents", html);
                    js.put("cost", edt_cost.getText().toString().trim().equals("") ? "0" : edt_cost.getText().toString().trim());
                    js.put("money",edt_money.getText().toString().trim());
                    js.put("type", tv_tag.getTag().toString());
                    js.put("open",pintuan);
                    js.put("price",edt_pintuan_money.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                final HttpParams httpParams = new HttpParams();
                if (uriList == null) {
                    uriList = new ArrayList<>();
                } else {
                    uriList.clear();
                }
                if (adapter.getData().size() > 1) {
                    for (String s : adapter.getData()) {
                        if (s.equals("add")) {
                            continue;
                        }
                        uriList.add(Uri.parse(s));
                        LogUtil.e(Uri.parse(s).toString());
                    }
                    LogUtil.e("图片数组：："+uriList.size());
                    Tiny.getInstance().source(uriList.toArray(new Uri[uriList.size()]))
                            .batchAsFile().batchCompress(new FileBatchCallback() {
                        @Override
                        public void callback(boolean isSuccess, String[] outfile) {
                            if (isSuccess) {
                                if(outfile.length==1){
                                    httpParams.put("image1", new File(outfile[0]));
                                    httpParams.put("image2", "");
                                    httpParams.put("image3", "");
                                }else if(outfile.length==2){
                                    httpParams.put("image1", new File(outfile[0]));
                                    httpParams.put("image2",  new File(outfile[1]));
                                    httpParams.put("image3", "");
                                }else if(outfile.length==3){
                                    httpParams.put("image1", new File(outfile[0]));
                                    httpParams.put("image2",  new File(outfile[1]));
                                    httpParams.put("image3", new File(outfile[2]));
                                }

                                if(isUpdate){
                                    try {
                                        js.put("image_1", oldMap.get("image1"));
                                        js.put("image_2", oldMap.get("image2"));
                                        js.put("image_3", oldMap.get("image3"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                LogUtil.e("有图片时的Json:"+js);
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                httpParams.put("key", m.K());
                                httpParams.put("msg", m.M());
                                pushData(httpParams);
                            }
                        }
                    });
                } else {

                    if(isUpdate){
                        try {
                            js.put("image_1", oldMap.get("image1"));
                            js.put("image_2", oldMap.get("image2"));
                            js.put("image_3", oldMap.get("image3"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    LogUtil.e("json字段：：："+js);
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    httpParams.put("key", m.K());
                    httpParams.put("msg", m.M());
                    pushData(httpParams);
                }


                break;
            case R.id.tag:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(Order_Tab.typeNameList.toArray(new CharSequence[Order_Tab.typeNameList.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tv_tag.setText(Order_Tab.typeNameList.get(i));
                        tv_tag.setTag(Order_Tab.typeIdList.get(i));
                    }
                }).setCancelable(true).create().show();
                break;
            case R.id.open:
                view.setSelected(true);
                tv_close.setSelected(false);
                tv_open.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_close.setTextColor(ContextCompat.getColor(this, R.color.hotpink));
                pintuan = 2;
                break;
            case R.id.close:
                tv_close.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_open.setTextColor(ContextCompat.getColor(this, R.color.hotpink));
                tv_open.setSelected(false);
                view.setSelected(true);
                pintuan = 1;
                break;
        }
    }

    private void pushData(HttpParams httpParams) {
        String url = "";
        if (isUpdate) {
            url = Constants.GoodsUpdate;
        } else {
            url = Constants.GoodsAdd;
        }
        LogUtil.e("商品：：："+httpParams);
        OkHttpUtils.post(url).params(httpParams)
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(Good_Handle.this, "", isUpdate ? "正在提交更新" : "正在添加...");
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null && !s.equals("") && !s.equals("null")) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                            if (map != null) {
                                if ("000".equals(map.get("code"))) {
                                    ToastUtil.showToastShort(isUpdate ? "商品更新成功" : "商品发布成功");
                                    Intent intent = new Intent();
                                    setResult(ResultCode, intent);
                                    finish();
                                } else {
                                    ToastUtil.showToastShort(isUpdate ? "商品更新失败，请稍后重试" : "商品发布失败，请稍后重试");
                                }
                            } else {
                                ToastUtil.showToastShort("网络连接失败，请稍后重试");
                            }
                        } else {
                            ToastUtil.showToastShort("网络连接失败，请稍后重试");
                        }
                    }

                    @Override
                    public void onAfter(@Nullable String s, @Nullable Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }


                });
    }

    public static class NinePreViewAdapter extends BaseQuickAdapter<String> {
        private Activity context;
        public NinePreViewAdapter(Activity a, ArrayList data) {
            super(R.layout.tougao_yulan_grid_item, data);
            WeakReference<Activity> w = new WeakReference<Activity>(a);
            context = w.get();
        }

        @Override
        protected void convert(BaseViewHolder holder, final String s) {
            if (s.equals("add")) {
                int dp15 = DimenUtils.dip2px(context, 20);
                holder.getView(R.id.tougao_grid_item_cancle).setVisibility(GONE);
                holder.getView(R.id.tougao_grid_item_img).setBackgroundResource(R.drawable.tougao_addtileimg_bg);
                holder.getView(R.id.tougao_grid_item_img).setPadding(dp15, dp15, dp15, dp15);
                Glide.with(context).load(R.drawable.addimg_with_text)
                        .override(DimenUtils.dip2px(context, 90), DimenUtils.dip2px(context, 90))
                        .fitCenter().into((ImageView) holder.getView(R.id.tougao_grid_item_img));
                holder.getView(R.id.tougao_grid_item_img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int size = 0;
                        if (getData().contains("add")) {
                            size = getData().size() - 1;
                        } else {
                            size = getData().size();
                        }
                        if (MaxSize - size > 0) {

                            Matisse.from(context)
                                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                                    .theme(R.style.Matisse_Dracula)
                                    .captureStrategy(new CaptureStrategy(true, "com.laomachaguan.fileProvider"))
                                    .capture(true)
                                    .spanCount(3)
                                    .thumbnailScale(1.0f)
                                    .countable(true)
                                    .maxSelectable(MaxSize - size)
                                    .imageEngine(new GlideEngine())
                                    .forResult(REQUEST_CODE_CHOOSE);
                        }

                    }
                });


            } else {
                holder.getView(R.id.tougao_grid_item_cancle).setVisibility(View.VISIBLE);
                holder.setOnClickListener(R.id.tougao_grid_item_img, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ScaleImageUtil.openBigIagmeMode(context, (ArrayList<String>) getData(),getData().indexOf(s));
                        LogUtil.e("点击已移除");
                    }
                });
                holder.getView(R.id.tougao_grid_item_img).setPadding(0, 0, 0, 0);
                Glide.with(context).load(s)
                        .override(DimenUtils.dip2px(context, 90), DimenUtils.dip2px(context, 90))
                        .bitmapTransform(new RoundedCornersTransformation(context, DimenUtils.dip2px(context, 5), 0))
                        .fitCenter().into((ImageView) holder.getView(R.id.tougao_grid_item_img));
                holder.setOnClickListener(R.id.tougao_grid_item_cancle, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getData().remove(s);

                        if (getData().size() < MaxSize && !getData().contains("add")) {
                            getData().add("add");
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
