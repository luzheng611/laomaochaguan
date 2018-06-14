package com.laomachaguan.Common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.Model_Order.Order_item_fragment;
import com.laomachaguan.R;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.StatusBarCompat;

/**
 * 作者：因陀罗网 on 2017/9/15 15:59
 * 公司：成都因陀罗网络科技有限公司
 */

public class Search_Result extends AppCompatActivity implements View.OnClickListener {

    private String content;//搜索结果
    private String url = "";//需要调用的接口
    private String id;//分类查询id；

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        ImageView back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        back.setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));

        TextView title = (TextView) findViewById(R.id.title_title);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Order_item_fragment order_item_fragment = new Order_item_fragment();

        if ((getIntent().getStringExtra("content"))!=null) {//搜索
            content = getIntent().getStringExtra("content");
            title.setText(content);
            url = Constants.Good_Search;
            Bundle b=new Bundle();
            b.putString("content",content);
            order_item_fragment.setArguments(b);
        } else if(getIntent().getStringExtra("id")!=null){//分类商品
            id = getIntent().getStringExtra("id");
            title.setText(getIntent().getStringExtra("name"));
            url = Constants.Order_special;
            Bundle b=new Bundle();
            b.putString("type",id);
            order_item_fragment.setArguments(b);
        }else{//所有商品
            url=Constants.Order_total;
            title.setText("所有商品");
        }

        ft.add(R.id.fragment, order_item_fragment);
        ft.show(order_item_fragment);
        ft.commit();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
        }
    }
}
