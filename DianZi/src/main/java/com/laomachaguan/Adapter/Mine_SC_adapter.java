package com.laomachaguan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public class Mine_SC_adapter extends BaseAdapter {
    public List<HashMap<String, String>> list;
    public Context context;
    public LayoutInflater inflater;
    private int screenWidth;

    public Mine_SC_adapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        list=new ArrayList<>();
    }

    public void addList(List<HashMap<String, String>> list) {
        this.list = list;

    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        HashMap<String, String> map = list.get(position);
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.mine_shoucang_item, parent, false);
            holder.image = (ImageView) view.findViewById(R.id.mine_shoucang_item_image);
            holder.title = (TextView) view.findViewById(R.id.mine_shoucang_item_title);
            holder.time = (TextView) view.findViewById(R.id.mine_shoucang_item_time);
            holder.user = (TextView) view.findViewById(R.id.mine_shoucang_item_user);
            holder.type= (TextView) view.findViewById(R.id.mine_shoucang_item_type);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(map.get("draft_comment")!=null){
            holder.time.setText(TimeUtils.getTrueTimeStr(map.get("time")));
            holder.title.setText(map.get("title"));
            holder.user.setText(map.get("pet_name"));
            holder.type.setText("圈子");
            holder.type.setTag(map.get("id"));
            if(!map.get("image1").equals("")){
                Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth*6/ 25).centerCrop().into(holder.image);
            }else{
                Glide.with(context).load(R.color.e6e6e6).fitCenter().into(holder.image);
            }
//        }else if(map.get("cy_people")==null&&map.get("end_time")!=null){
//            holder.type.setText("活动");
//            holder.type.setTag(map.get("id"));
//            holder.user.setText(map.get("author"));
//            holder.title.setText(map.get("title"));
//            Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth*6/ 25).centerCrop().into(holder.image);
//            holder.time.setText("结束时间："+TimeUtils.getTrueTimeStr(map.get("end_time")));
//        }else if(map.get("cy_people")!=null){
//            holder.type.setText("慈善");
//            holder.type.setTag(map.get("id"));
//            holder.user.setText("参与人数："+map.get("cy_people"));
//            holder.title.setText(map.get("title"));
//            Glide.with(context).load(map.get("image")).override(screenWidth * 3 / 10, screenWidth*6/ 25).centerCrop().into(holder.image);
//            holder.time.setText("结束时间："+TimeUtils.getTrueTimeStr(map.get("end_time")));
        }else {
            holder.time.setText(TimeUtils.getTrueTimeStr(map.get("time")));
            holder.title.setText(map.get("title"));
            holder.user.setText(map.get("name"));
            holder.type.setText("商品");
            holder.type.setTag(map.get("id"));
            if(!map.get("image1").equals("")){
                Glide.with(context).load(map.get("image1")).override(screenWidth * 3 / 10, screenWidth*6/ 25).centerCrop().into(holder.image);
            }
        }

        return view;
    }

    static class ViewHolder {
        ImageView image;
        TextView title, user, time,type;
    }
}
