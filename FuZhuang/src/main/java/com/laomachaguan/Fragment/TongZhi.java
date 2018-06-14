package com.laomachaguan.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.R;
import com.laomachaguan.Utils.StatusBarCompat;

/**
 * Created by Administrator on 2016/12/26.
 */
public class TongZhi extends AppCompatActivity implements View.OnClickListener {
    private ImageView back;
    //    private ArrayList<Fragment> fragments;
    private static final String TAG = "TongZhi";
    //    private SlidingPaneLayout parent;
    private TextView push, pull;
    private String type = "push";
    private static final String pushStr = "push";
    private static final String pullStr = "pull";
    private FrameLayout frag;
    private FragmentTransaction ft;
    private TongZhi_send tongZhi_send;
    private TongZhi_get tongZhi_get;
    private String id;
    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tongzhi);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        back = (ImageView) findViewById(R.id.title_back);
        back.setImageResource(R.drawable.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        push = (TextView) findViewById(R.id.sixin_push);
        pull = (TextView) findViewById(R.id.sixin_pull);
        push.setSelected(true);
        push.setOnClickListener(this);
        pull.setOnClickListener(this);

        type = pushStr;
        id = getIntent().getStringExtra("id");
        pos = getIntent().getIntExtra("pos", 0);
        frag = (FrameLayout) findViewById(R.id.fragment_root);
        tongZhi_get = new TongZhi_get();
        tongZhi_send = new TongZhi_send();

        FragmentManager fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.fragment_root, tongZhi_send);
        ft.add(R.id.fragment_root, tongZhi_get);
        ft.commit();



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (0 == pos) {
            push.performClick();
        } else {
            pull.performClick();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.sixin_push:
                v.setSelected(true);
                pull.setSelected(false);
                ft = getSupportFragmentManager().beginTransaction();
                ft.show(tongZhi_send).hide(tongZhi_get).commit();
                tongZhi_send.setUserVisibleHint(true);
                break;
            case R.id.sixin_pull:
                v.setSelected(true);
                push.setSelected(false);
                ft = getSupportFragmentManager().beginTransaction();
                ft.show(tongZhi_get).hide(tongZhi_send).commit();
                tongZhi_get.setUserVisibleHint(true);
                break;
        }
    }


}
