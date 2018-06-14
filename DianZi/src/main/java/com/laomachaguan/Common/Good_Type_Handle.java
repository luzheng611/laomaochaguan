package com.laomachaguan.Common;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
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
import com.laomachaguan.View.mItemDecoration;
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
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Response;

import static android.view.View.GONE;

/**
 * 作者：因陀罗网 on 2017/6/8 02:32
 * 公司：成都因陀罗网络科技有限公司
 */

public class Good_Type_Handle extends AppCompatActivity implements View.OnClickListener {
    public static final int ResultCode = 1111;
    private static final int MaxSize = 1;
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
    private String html = "";

    private TextView tags_total, tags_manage_spec;

    private TextView tag1, tag2;//选择类型一，类型二

    private ArrayList<HashMap<String,String >> list1,list2;//类型一，类型二的列表
    private typeAdapter type_adapter ;
    private int chooseType=-1;
    private String name="",spec_name="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.good_type_fabu_update);
        initView();
        getData();

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
                            if (map != null) {
                                list1=AnalyticalJSON.getList_zj(map.get("spec"));
                                list2=AnalyticalJSON.getList_zj(map.get("spec_info"));
                            }
                        }

                        @Override
                        public void onAfter(@Nullable String s, @Nullable Exception e) {
                            super.onAfter(s, e);

                        }
                    });
        }
    }

    private void initView() {

        type_adapter=new typeAdapter(new ArrayList<HashMap<String, String>>());

        tags_total = (TextView) findViewById(R.id.tags);
        tags_total.setOnClickListener(this);

        tags_manage_spec = (TextView) findViewById(R.id.tags_manage);
        tags_manage_spec.setOnClickListener(this);

        tag1 = (TextView) findViewById(R.id.tag1);
        tag2 = (TextView) findViewById(R.id.tag2);
        tag1.setOnClickListener(this);
        tag2.setOnClickListener(this);


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
            ((TextView) findViewById(R.id.title)).setText("修改属性");
            tv_commit.setText("修改属性");
            isUpdate = true;
            oldMap= (HashMap<String, String>) getIntent().getSerializableExtra("map");
            if (oldMap != null) {
                LogUtil.e(oldMap.toString());
                ArrayList<String> l = new ArrayList<String>();
                if (!oldMap.get("image").equals("")) {
                    l.add(oldMap.get("image"));
                }
                if(l.size()>=1){
                    if(adapter.getData().contains("add")){
                        adapter.getData().remove("add");
                    }
                }
                adapter.getData().addAll(l);

                adapter.notifyDataSetChanged();
                edt_cost.setText(oldMap.get("cost"));
                edt_money.setText(oldMap.get("money"));
                name=oldMap.get("name");
                spec_name=oldMap.get("spec_name");
                tag1.setText(name.equals("")?"请选择类型一":name);
                tag2.setText(spec_name.equals("")?"请选择类型二":spec_name);

                tag1.setEnabled(false);
                tag2.setEnabled(false);


                ((EditText) findViewById(R.id.stock)).setText(oldMap.get("stock").equals("")?"0":oldMap.get("stock"));
                edt_pintuan_money.setText(oldMap.get("price"));

            }




        } else {
            ((TextView) findViewById(R.id.title)).setText("新增属性");
            tv_commit.setText("新增属性");
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 123);
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
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tag1:
                if(list1!=null&&list2!=null){
                    showTypeDialog(1);
                    chooseType=1;
                }else{
                    ToastUtil.showToastShort("数据加载中，请稍后重试");
                    getData();
                }
                break;
            case R.id.tag2:
                if(list1!=null&&list2!=null){
                    showTypeDialog(2);
                    chooseType=2;
                }else{
                    ToastUtil.showToastShort("数据加载中，请稍后重试");
                    getData();
                }
                break;
            case R.id.tags:
                Intent i = new Intent(this, Add_Tags.class);
                startActivity(i);
                break;
            case R.id.tags_manage:

                Intent i1 = new Intent(this, Good_Type_Manager.class);
                i1.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(i1);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.button:
                if (!Network.HttpTest(this)) {
                    return;
                }

                if (edt_money.getText().toString().trim().equals("")) {
                    ToastUtil.showToastShort("请填写售价");
                    return;
                }


                String cost = edt_cost.getText().toString();
                LogUtil.e("原价小数点后位数：：" + (cost.length() - cost.indexOf(".") - 1));
                if (cost.startsWith(".") || cost.endsWith(".") ||
                        (cost.startsWith("0") && !cost.contains(".")) || (cost.equals("0.0")) || (cost.contains(".") && (cost.length() - cost.indexOf(".") - 1) > 2)) {
                    ToastUtil.showToastShort("请输入正确格式的原价,最多两位小数");
                    return;//||cost.split(".")[1].length()>2
                }
                String edtmoney = edt_money.getText().toString();
                LogUtil.e("售价小数点后位数：：" + (edtmoney.length() - edtmoney.indexOf(".") - 1));
                if (edtmoney.startsWith(".") || edtmoney.endsWith(".") ||
                        (edtmoney.startsWith("0") && !edtmoney.contains("."))
                        || (edtmoney.equals("0.0")) || (edtmoney.contains(".") && (edtmoney.length() - edtmoney.indexOf(".") - 1) > 2)) {

                    ToastUtil.showToastShort("请输入正确格式的售价,最多两位小数");
                    return;
                }

                String ptmoney = edt_pintuan_money.getText().toString();
                LogUtil.e("拼团价小数点后位数：：" + (ptmoney.length() - ptmoney.indexOf(".") - 1));
                if (pintuan == 2) {
                    if (ptmoney.startsWith(".") || ptmoney.endsWith(".") ||
                            (ptmoney.startsWith("0") && !ptmoney.contains("."))
                            || (ptmoney.equals("0.0")) || (ptmoney.contains(".") && (ptmoney.length() - ptmoney.indexOf(".") - 1) > 2)) {
                        ToastUtil.showToastShort("请输入正确格式的拼团价,最多两位小数");
                        return;//||ptmoney.split(".")[1].length()>2
                    }
                }

                if (!edt_cost.getText().toString().equals("") && Double.valueOf(edt_cost.getText().toString()) <= Double.valueOf(edt_money.getText().toString())) {
                    ToastUtil.showToastShort("售价必须小于原价");
                    return;
                }
                if (pintuan == 2 && !edt_pintuan_money.getText().toString().equals("") && Double.valueOf(edt_pintuan_money.getText().toString()) >= Double.valueOf(edt_money.getText().toString())) {
                    ToastUtil.showToastShort("拼团价必须小于售价");
                    return;
                }
                if(name.equals("")&&spec_name.equals("")){
                    ToastUtil.showToastShort("请至少选择一种属性");
                    return;
                }
                final JSONObject js = new JSONObject();
                try {
                    if (isUpdate) {
                        if (((EditText) findViewById(R.id.stock)).getText().toString().trim().equals("")
                                ) {
                            ToastUtil.showToastShort("请填写库存数量");
                            return;
                        }

                        js.put("id", oldMap.get("id"));
                        js.put("goods_id", oldMap.get("goods_id"));
                    } else {
                        if (((EditText) findViewById(R.id.stock)).getText().toString().trim().equals("")
                                || Integer.valueOf(((EditText) findViewById(R.id.stock)).getText().toString().trim()) < 1) {
                            ToastUtil.showToastShort("库存数量必须填写并且超过1件");
                            return;
                        }
                        js.put("goods_id", getIntent().getStringExtra("id"));
                    }

                    js.put("stock", ((EditText) findViewById(R.id.stock)).getText().toString().trim());
                    js.put("m_id", Constants.M_id);
                    js.put("name", name);
                    js.put("spec_name", spec_name);
                    js.put("cost", edt_cost.getText().toString().trim().equals("") ? "0" : edt_cost.getText().toString().trim());
                    js.put("money", edt_money.getText().toString().trim());
                    js.put("price", edt_pintuan_money.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                final HttpParams httpParams = new HttpParams();
                if (uriList == null) {
                    uriList = new ArrayList<>();
                } else {
                    uriList.clear();
                }

                    for (String s : adapter.getData()) {
                        if (s.equals("add")) {
                            continue;
                        }
                        uriList.add(Uri.parse(s));
                        LogUtil.e(Uri.parse(s).toString());
                    }
                    LogUtil.e("图片数组：：" + uriList.size());
                    Tiny.getInstance().source(uriList.toArray(new Uri[uriList.size()]))
                            .batchAsFile().batchCompress(new FileBatchCallback() {
                        @Override
                        public void callback(boolean isSuccess, String[] outfile) {
                            if (isSuccess) {
                                if (outfile.length == 1) {
                                    httpParams.put("image", new File(outfile[0]));
                                }
//                                if (isUpdate) {
//                                    try {
//                                        js.put("image_1", oldMap.get("image1"));
//                                        js.put("image_2", oldMap.get("image2"));
//                                        js.put("image_3", oldMap.get("image3"));
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
                                LogUtil.e("有图片时的Json:" + js);
                                ApisSeUtil.M m = ApisSeUtil.i(js);
                                httpParams.put("msg", m.M());
                                httpParams.put("key", m.K());
                                pushData(httpParams);
                            }
                        }
                    });



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

    private  class typeAdapter extends BaseQuickAdapter<HashMap<String,String >> {
        public typeAdapter(List<HashMap<String, String>> data) {
            super(R.layout.dialog_recycle_text_item, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, HashMap<String, String> map) {
            if(chooseType==1){
                holder.setText(R.id.text,map.get("name"));
            }else if(chooseType==2){
                holder.setText(R.id.text,map.get("spec_name"));
            }

        }
    }

    private void showTypeDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_recycle_type_bottom, null);
        builder.setView(view1);
        RecyclerView recycle= (RecyclerView) view1.findViewById(R.id.recycle);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(new mItemDecoration(this));
        recycle.setAdapter(type_adapter);
        type_adapter.setNewData((i==1?list1:list2));
        final Dialog dialog=builder.create();
        type_adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if(i==1){
                    tag1.setText(list1.get(pos).get("name"));
                    name=list1.get(pos).get("name");
                }else if(i==2){
                    tag2.setText(list2.get(pos).get("spec_name"));
                    spec_name=list2.get(pos).get("spec_name");
                }
                dialog.dismiss();
            }
        });

        view1.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();

        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.windowAnimations = R.style.dialogWindowAnim;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wl);
        dialog.show();
    }

    private void pushData(HttpParams httpParams) {
        String url = "";
        if (isUpdate) {
            url = Constants.Goods_spec_update;
        } else {
            url = Constants.Goods_spec_add;
        }
        LogUtil.e("商品：：：" + httpParams);
        OkHttpUtils.post(url).params(httpParams)
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(Good_Type_Handle.this, "", isUpdate ? "正在提交更新" : "正在添加...");
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null && !s.equals("") && !s.equals("null")) {
                            HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                            if (map != null) {
                                if ("000".equals(map.get("code"))) {
                                    ToastUtil.showToastShort(isUpdate ? "修改成功" : "新增成功");
                                    Intent intent = new Intent();
                                    setResult(ResultCode, intent);
                                    finish();
                                } else {
                                    ToastUtil.showToastShort(isUpdate ? "修改失败，请稍后重试" : "该属性已存在");
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
                        ScaleImageUtil.openBigIagmeMode(context, (ArrayList<String>) getData(), getData().indexOf(s));
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
