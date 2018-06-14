package com.laomachaguan.TouGao.Photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.DimenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 */
public class List_Dir_Adapter extends BaseAdapter {
    private List<ImageFloder> list = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private int checked;
    public List_Dir_Adapter(Context context, List<ImageFloder> l,int checked) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = l;
        this.checked=checked;
    }
    public void setChecked(int checked){
        this.checked=checked;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        viewHolder holder = null;
       ImageFloder imf=list.get(position);
        if (view == null) {
            holder = new viewHolder();
            view = inflater.inflate(R.layout.list_dir_item, parent, false);
            holder.name = (TextView) view.findViewById(R.id.tv_list_dir_item_name);
            holder.num = (TextView) view.findViewById(R.id.tv_list_dir_item_num);
            holder.fImage = (ImageView) view.findViewById(R.id.image_list_dir_item_fimg);
            holder.check = (ImageView) view.findViewById(R.id.img_list_dir_item_check);
            view.setTag(holder);
        } else {
            holder = (viewHolder) view.getTag();
        }
        holder.name.setText(imf.getName());
        holder.num.setText(imf.getCount() + "张");
        Glide.with(context).load(imf.getFirstImagePath()).override(DimenUtils.dip2px(context, 90), DimenUtils.dip2px(context, 90))
                .centerCrop().placeholder(R.drawable.place_holder2).into(holder.fImage);
        holder.check.setVisibility(View.GONE);
        if(position==checked){
            holder.check.setVisibility(View.VISIBLE);
            Glide.with(context).load(R.drawable.icon_data_select).override(DimenUtils.dip2px(context,25),DimenUtils.dip2px(context,25))
                    .into(holder.check);
        }
        return view;
    }

    private static final String TAG = "List_Dir_Adapter";
    static class viewHolder {
        TextView name, num;
        ImageView fImage, check;

    }
}
