package com.laomachaguan.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.Managers.BaseActivity;
import com.laomachaguan.R;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.FileUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.MD5Utls;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/29.
 */

public class Search extends BaseActivity implements View.OnClickListener{
    private static  final String CacheName= MD5Utls.stringToMD5("SearchCache");
    private EditText input ;
    private TextView back;
    private RecyclerView history;
    private TextView clearEmptyView;
    private InputMethodManager imm;
    private int searchType=1;

    private historyAdapter adapter;
    private ArrayList<String > list;
    @Override
    public int getLayoutId() {
        return R.layout.search_activity;
    }

    @Override
    public void initView() {
        input= (EditText) findViewById(R.id.search_input);
        back= (TextView) findViewById(R.id.back);
        history= (RecyclerView) findViewById(R.id.search_result);
        RecyclerView.LayoutParams r=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clearEmptyView=new TextView(this);
        clearEmptyView.setLayoutParams(r);
        clearEmptyView.setTextColor(Color.BLACK);
        clearEmptyView.setTextSize(17);
        clearEmptyView.setPadding(0, DimenUtils.dip2px(this,15),0,DimenUtils.dip2px(this,15));
        clearEmptyView.setGravity(Gravity.CENTER);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        list= FileUtils.getStorageEntities(this,CacheName);
        if(list==null||list.size()==0){
            clearEmptyView.setText(mApplication.ST("暂无搜索记录"));
            list=new ArrayList<>();
        }else{
            clearEmptyView.setText(mApplication.ST("清空历史记录"));
        }
        adapter=new historyAdapter(R.layout.search_history_item,list);
        adapter.addFooterView(clearEmptyView);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        RecyclerView.LayoutManager rl=new LinearLayoutManager(this);
        history.setLayoutManager(rl);
        history.addItemDecoration(new mItemDecoration(this));
        history.setAdapter(adapter);

        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                performSearch(adapter.getItem(i));
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                String content=input.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_SEARCH ){
                    performSearch(content);
                    return true;
                }
                return false;
            }
        });

        back.setOnClickListener(this);
        clearEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clearEmptyView.getText().toString().equals(mApplication.ST("清空历史记录"))){
                    list.clear();
                    adapter.notifyDataSetChanged();
                    FileUtils.saveStorage2SDCard(Search.this,list,CacheName);
                    clearEmptyView.setText(mApplication.ST("暂无搜索记录"));
                }
            }
        });
    }


    private void performSearch(String content) {
        imm.hideSoftInputFromWindow(input.getWindowToken(),0);
        Intent intent=new Intent(this,Search_Result.class);
        intent.putExtra("content",content);
        startActivity(intent);
        if(list==null){
            list=new ArrayList<>();
        }
        if(list.contains(content)){
            LogUtil.e(list+"");
            int i=list.indexOf(content);
            adapter.notifyItemMoved(i,0);
            list.add(0,content);
            list.remove(i+1);
            LogUtil.e(list+"");
        }else{
            list.add(0,content);
            adapter.notifyItemInserted(0);
        }
        FileUtils.saveStorage2SDCard(Search.this,list,CacheName);
        clearEmptyView.setText(mApplication.ST("清空历史记录"));
    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public boolean isMainColor() {
        return true;
    }

    @Override
    public void doThings() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.back:
                finish();
                break;
        }
    }

    private  class historyAdapter extends BaseQuickAdapter<String > {
        public historyAdapter(int layoutResId, List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, String s) {
//            Glide.with(Search.this).load(R.drawable.time).override(DimenUtils.dip2px(Search.this,30),DimenUtils.dip2px(Search.this,30))
//                    .into((ImageView) baseViewHolder.getView(R.id.image));
            baseViewHolder.setText(R.id.text,s);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        list=FileUtils.getStorageEntities(this,CacheName);
        if(list==null||list.size()==0){
            clearEmptyView.setText(mApplication.ST("暂无搜索记录"));
            list=new ArrayList<>();
        }else{
            clearEmptyView.setText(mApplication.ST("请空历史记录"));
        }
        adapter.setNewData(list);
    }
}
