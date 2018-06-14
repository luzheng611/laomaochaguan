package com.laomachaguan.Common;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.R;
import com.laomachaguan.SideListview.CharacterParser;
import com.laomachaguan.SideListview.ClearEditText;
import com.laomachaguan.SideListview.SideBar;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.JXl;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 */

public class CountryCode extends AppCompatActivity implements View.OnClickListener, JXl.ExcelLoadedListener {

    private RecyclerView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private countryAdapter adapter;
    private ClearEditText mClearEditText;


    private CharacterParser characterParser;
    private List<JXl.CountryModel> SourceDateList;

    private ImageView back;
    private TextView title;

    private JXl jxl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.country_sort);

        initView();
    }

    private void initView() {
        jxl = new JXl(this);
        characterParser = CharacterParser.getInstance();


        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText(mApplication.ST("国家代码"));
        sideBar.setTextView(dialog);

        //�����Ҳഥ������
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //����ĸ�״γ��ֵ�λ��

                for(int i=0;i<adapter.getItemCount();i++){
                    char sortStr =adapter.getData().get(i).getPinYin().charAt(0);
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

        adapter = new countryAdapter(new ArrayList<JXl.CountryModel>());
        sortListView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                JXl.CountryModel s = (JXl.CountryModel) adapter.getItem(i);
                Intent intent = new Intent();
                intent.putExtra("country", s.getChinaName());
                intent.putExtra("code", s.getAreaNumber());
                setResult(111, intent);
                finish();
            }
        });


        jxl.loader.execute("excel.xls");

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
    }

    public static class countryAdapter extends BaseQuickAdapter<JXl.CountryModel> {
        public countryAdapter(List<JXl.CountryModel> data) {
            super(R.layout.country_sort_item, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, JXl.CountryModel countryModel) {
                //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(countryModel);
            JXl.CountryModel selectModel=null;
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现

            for (int i = 0; i < getItemCount(); i++) {
                char sortStr = getData().get(i).getPinYin().charAt(0);
                if (sortStr == section) {
                    selectModel=getData().get(i);
                    break;
                }
            }
            if (countryModel == selectModel) {
                holder.setVisible(R.id.catalog,true)
                        .setText(R.id.catalog,countryModel.getPinYin())
                        .setText(R.id.country,mApplication.ST(countryModel.getChinaName()))
                        .setText(R.id.id,"+"+countryModel.getAreaNumber());

            } else {
                holder.setVisible(R.id.catalog,false)
                        .setText(R.id.country,mApplication.ST(countryModel.getChinaName()))
                        .setText(R.id.id,"+"+countryModel.getAreaNumber());
            }
        }


        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(JXl.CountryModel countryModel) {
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
        List<JXl.CountryModel> filterDateList = new ArrayList<JXl.CountryModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (JXl.CountryModel model : SourceDateList) {
                String name = model.getChinaName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(model);
                }
            }
        }

        // ����a-z��������
        Collections.sort(filterDateList, new Comparator<JXl.CountryModel>() {
            @Override
            public int compare(JXl.CountryModel lhs, JXl.CountryModel rhs) {
                return lhs.getPinYin().compareTo(rhs.getPinYin());
            }
        });
        adapter.setNewData(filterDateList);
    }

    @Override
    public void onDataLoaded(ArrayList<JXl.CountryModel> list) {
        SourceDateList = list;
        Collections.sort(SourceDateList, new Comparator<JXl.CountryModel>() {
            @Override
            public int compare(JXl.CountryModel lhs, JXl.CountryModel rhs) {
                return lhs.getPinYin().compareTo(rhs.getPinYin());
            }
        });
        adapter.setNewData(SourceDateList);
    }
}
