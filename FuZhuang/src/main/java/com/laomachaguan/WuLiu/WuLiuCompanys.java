package com.laomachaguan.WuLiu;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.R;
import com.laomachaguan.SideListview.CharacterParser;
import com.laomachaguan.SideListview.ClearEditText;
import com.laomachaguan.SideListview.SideBar;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.FileUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mItemDecoration;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/12.
 */

public class WuLiuCompanys extends AppCompatActivity implements View.OnClickListener {
    public static final String COMPANYS = "companys";
    private RecyclerView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private countryAdapter adapter;
    private ClearEditText mClearEditText;


    private CharacterParser characterParser;
    private List<CompanyModel> SourceDateList;

    private ImageView back;
    private TextView title;
    private LinearLayout head, tags;
    private ArrayList<CompanyModel> cacheList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.wuliu_companys);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    private void initView() {
        head = (LinearLayout) findViewById(R.id.headLayout);
        tags = (LinearLayout) findViewById(R.id.tagsLayout);
        characterParser = CharacterParser.getInstance();
        cacheList = FileUtils.getStorageEntities(this, WuLiuCompanys.COMPANYS);
        LogUtil.e("cachelist:::" + cacheList);
        if (cacheList != null && cacheList.size() > 0) {
            head.setVisibility(View.VISIBLE);
            addTags();
        } else {
            head.setVisibility(View.GONE);
        }
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText(mApplication.ST("选择物流公司"));
        sideBar.setTextView(dialog);

        //�����Ҳഥ������
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //����ĸ�״γ��ֵ�λ��

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    char sortStr = adapter.getData().get(i).getPinYin().charAt(0);
                    if (sortStr == s.charAt(0)) {
//                        int pos=sortListView.getLayoutManager().getPosition(sortListView.getChildAt(0));
//                        if(i<=pos){
                        sortListView.scrollToPosition(i);
//                        }else{
//                            sortListView.scrollToPosition(i+sortListView.getChildCount()-1);
//                        }

                        break;
                    }
                }


            }
        });

        sortListView = (RecyclerView) findViewById(R.id.country_lvcountry);
        sortListView.setLayoutManager(new LinearLayoutManager(this));
        sortListView.addItemDecoration(new mItemDecoration(this));

        adapter = new countryAdapter(new ArrayList<CompanyModel>());
        sortListView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                if (cacheList == null) {
                    cacheList = new ArrayList<CompanyModel>();
                }
                CompanyModel c = adapter.getData().get(i);
                if (!cacheList.contains(c)) {
                    cacheList.add(0, c);
                }
                if (cacheList.size() > 3) {
                    for (int i1 = 3; i1 < cacheList.size(); i1++) {
                        cacheList.remove(i1);
                    }
                }

                FileUtils.saveStorage2SDCard(WuLiuCompanys.this, cacheList, COMPANYS);

                Intent inte = new Intent();
                inte.putExtra("name", c.getChinaName());
                inte.putExtra("code", c.getCode());
                setResult(0, inte);
                finish();
            }
        });


        // ����a-z��������Դ����


        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        mClearEditText.setHint(mApplication.ST("搜索"));
        Drawable d = ContextCompat.getDrawable(this, R.drawable.search_gray);
        d.setBounds(0, 0, DimenUtils.dip2px(this, 25), DimenUtils.dip2px(this, 25));
        mClearEditText.setCompoundDrawables(d, null, null, null);
        //�������������ֵ�ĸı�����������
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        getCompanys();
    }

    /*
    添加常用物流
     */

    private void addTags() {
        int dp10 = DimenUtils.dip2px(this, 10);
        for (int i = 0; i <cacheList.size(); i++) {
            final CompanyModel c = cacheList.get(i);
            TextView text = new TextView(this);
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.setMargins(dp10, dp10, dp10, dp10);
            ll.weight = 1;
            text.setLayoutParams(ll);
            if(cacheList.size()<3){
                text.setTextSize(18);
            }else{
                text.setTextSize(15);
            }
            text.setTextColor(Color.BLACK);
            text.setBackgroundResource(R.drawable.edittext_background);
            text.setText(c.getChinaName());
            text.setGravity(Gravity.CENTER);
            text.setPadding(dp10*2,dp10,dp10*2,dp10);
            tags.addView(text);
            final int finaI = i;
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!cacheList.contains(c)) {
                        cacheList.add(0, c);
                    }

                    if (cacheList.size() > 3) {
                        for (int i1 = 3; i1 < cacheList.size(); i1++) {
                            cacheList.remove(i1);
                        }
                    }
                    FileUtils.saveStorage2SDCard(WuLiuCompanys.this, cacheList, COMPANYS);
                    Intent inte = new Intent();
                    inte.putExtra("name", c.getChinaName());
                    inte.putExtra("code", c.getCode());
                    setResult(0, inte);
                    finish();
                }
            });
        }

    }

    private void getCompanys() {
        if (Network.HttpTest(this)) {
            JSONObject js = new JSONObject();
            try {
                js.put("m_id", Constants.M_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApisSeUtil.M m = ApisSeUtil.i(js);
            OkHttpUtils.post(Constants.ExpressesList).tag(this)
                    .params("key", m.K())
                    .params("msg", m.M())
                    .execute(new AbsCallback<ArrayList<CompanyModel>>() {

                        @Override
                        public ArrayList<CompanyModel> parseNetworkResponse(Response response) throws Exception {
                            ArrayList<CompanyModel> list = new ArrayList<CompanyModel>();
                            JSONArray js = new JSONArray(response.body().string());
                            if (js != null) {
                                for (int i = 0; i < js.length(); i++) {
                                    JSONObject j = js.getJSONObject(i);
                                    CompanyModel c = new CompanyModel();
                                    c.setChinaName(j.getString("express"));
                                    c.setCode(j.getString("code"));
                                    c.setItemType(j.getString("id"));
                                    c.setPinYin(characterParser.getSelling(j.getString("express")).toUpperCase().substring(0, 1));
                                    list.add(c);
                                }
                            }
                            SourceDateList = list;

                            return list;
                        }

                        @Override
                        public void onSuccess(ArrayList<CompanyModel> companyModels, Call call, Response response) {
                            LogUtil.e("查询物流公司成功");
                            adapter.setNewData(companyModels);
                        }

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            ProgressUtil.show(WuLiuCompanys.this, "", "请稍等");
                        }

                        @Override
                        public void onAfter(@Nullable ArrayList<CompanyModel> companyModels, @Nullable Exception e) {
                            super.onAfter(companyModels, e);
                            ProgressUtil.dismiss();
                        }
                    });
        }
    }

    public static class countryAdapter extends BaseQuickAdapter<CompanyModel> {
        public countryAdapter(List<CompanyModel> data) {
            super(R.layout.country_sort_item, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, CompanyModel countryModel) {
            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(countryModel);
            CompanyModel selectModel = null;
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现

            for (int i = 0; i < getItemCount(); i++) {
                char sortStr = getData().get(i).getPinYin().charAt(0);
                if (sortStr == section) {
                    selectModel = getData().get(i);
                    break;
                }
            }
            if (countryModel == selectModel) {
                holder.setVisible(R.id.catalog, true)
                        .setText(R.id.catalog, countryModel.getPinYin())
                        .setText(R.id.country, mApplication.ST(countryModel.getChinaName()))
                        .setText(R.id.id, "");


            } else {
                holder.setVisible(R.id.catalog, false)
                        .setText(R.id.country, mApplication.ST(countryModel.getChinaName())).setText(R.id.id, "");

            }
        }


        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(CompanyModel countryModel) {
            return countryModel.getPinYin().charAt(0);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }


    /**
     * ����������е�ֵ���������ݲ�����ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<CompanyModel> filterDateList = new ArrayList<CompanyModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (CompanyModel model : SourceDateList) {
                String name = model.getChinaName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(model);
                }
            }
        }

        // ����a-z��������
        Collections.sort(filterDateList, new Comparator<CompanyModel>() {
            @Override
            public int compare(CompanyModel lhs, CompanyModel rhs) {
                return lhs.getPinYin().compareTo(rhs.getPinYin());
            }
        });
        adapter.setNewData(filterDateList);
    }


    public static class CompanyModel implements Serializable {
        private String chinaName;
        private String code;
        private String itemType;
        private String pinYin;

        public String getPinYin() {
            return pinYin;
        }

        public void setPinYin(String pinYin) {
            this.pinYin = pinYin;
        }

        public String getChinaName() {
            return chinaName;
        }

        public void setChinaName(String chinaName) {
            this.chinaName = chinaName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        @Override
        public String toString() {
            return chinaName+"   " ;
//                    + "   公司code： " + code + "  大写首字母： " + pinYin;
        }
    }
}
