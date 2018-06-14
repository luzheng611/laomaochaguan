package com.laomachaguan.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 */
public class myHomePagerAdapter extends FragmentPagerAdapter {
    private List<Fragment>list;
    private Context context;
    private int images[]={R.drawable.shouye_sel,R.drawable.simiao_sel,R.drawable.sel_majiang,R.drawable.sel_car,R.drawable.mine_sel};
    public myHomePagerAdapter(Context context,FragmentManager fm,List<Fragment> list) {
        super(fm);
        this.list=list;
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String titles[]=context.getResources().getStringArray(R.array.bottom_title);
        return titles[position];
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_tab_customview, null);
        ImageView img = (ImageView) view.findViewById(R.id.home_tab_image);

        TextView text = (TextView) view.findViewById(R.id.home_tab_text);
        text.setText(getPageTitle(position));
        img.setImageResource(images[position]);
        return view;
    }
}
