package com.laomachaguan.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 */
public class myShuchengPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment>list;
    private Context context;
    private ArrayList<String >titles;
    public myShuchengPagerAdapter(Context context, FragmentManager fm, List<Fragment> list, ArrayList<String >titles) {
        super(fm);
        this.list=list;
        this.titles=titles;
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
        return titles.get(position);
    }

//    public View getTabView(int position) {
//        View view = LayoutInflater.from(context).inflate(R.layout.home_tab_customview, null);
//        view.setMinimumWidth(1000);
//        ImageView img = (ImageView) view.findViewById(R.id.home_tab_image);
//        TextView text = (TextView) view.findViewById(R.id.home_tab_text);
//        text.setText(getPageTitle(position));
//        img.setImageResource(images[position]);
//        return view;
//    }
}
