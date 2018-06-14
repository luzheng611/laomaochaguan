package com.laomachaguan.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.Fragment.PinTuanFragment;
import com.laomachaguan.R;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;

import java.util.ArrayList;

/**
 * 作者：因陀罗网 on 2017/6/16 19:22
 * 公司：成都因陀罗网络科技有限公司
 */

public class Mine_pintuan extends AppCompatActivity implements View.OnClickListener {


    private ArrayList<Fragment> list;
    private TextView pintuan, guanzhu;
    private SharedPreferences sp;
    private ViewPager viewPager;
    private DDPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.main_color));
        setContentView(R.layout.mine_pintuan);

        list = new ArrayList<Fragment>();
        ((ImageView) findViewById(R.id.back)).setImageBitmap(ImageUtil.readBitMap(this, R.drawable.back));
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        pintuan = (TextView) findViewById(R.id.pintuan);
        pintuan.setOnClickListener(this);
        guanzhu = (TextView) findViewById(R.id.guanzhu);
        guanzhu.setOnClickListener(this);

        findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
        for (int i = 0; i < 2; i++) {
            PinTuanFragment d = new PinTuanFragment();
            Bundle b = new Bundle();
            b.putString("tag", String.valueOf(i + 1));
            d.setArguments(b);
            list.add(d);
        }
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        adapter =new DDPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setOffscreenPageLimit(list.size());
        viewPager.setAdapter(adapter);
        pintuan.performClick();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        pintuan.performClick();
                        break;
                    case 1:
                        guanzhu.performClick();
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pintuan:
                view.setSelected(true);
                pintuan.setTextColor(Color.BLACK);


                guanzhu.setSelected(false);
                guanzhu.setTextColor(Color.WHITE);
                viewPager.setCurrentItem(Integer.valueOf(view.getTag().toString()));
                break;
            case R.id.guanzhu:
                view.setSelected(true);
                guanzhu.setTextColor(Color.BLACK);


                pintuan.setSelected(false);
                pintuan.setTextColor(Color.WHITE);
                viewPager.setCurrentItem(Integer.valueOf(view.getTag().toString()));
                break;
        }
    }

    private static  class DDPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> list;
        public DDPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list=list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }


        @Override
        public int getCount() {
            return list==null?0:list.size();
        }
    }
}
