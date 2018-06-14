package com.laomachaguan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.NumUtils;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.mApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2016/6/1.
 */
public class ziXun_List_Adapter extends BaseAdapter {
    public List<HashMap<String, String>> mlist;
    private static final String TAG = "ziXun_List_Adapter";
    private int screenwidth;
    private Context context;

    //    app:layout_widthPercent="30%w"     资讯列表图片宽高
//    app:layout_heightPercent="20%w"
    public ziXun_List_Adapter(Context context) {
        this.context = context;
        mlist = new ArrayList<>();
        screenwidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void addList(List<HashMap<String, String>> mlist) {
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return mlist.size() > 0 ? mlist.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.valueOf(mlist.get(position).get("id"));
    }

    @Override
    public int getItemViewType(int position) {
        if (mlist.get(position).get("videourl").equals("")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        final HashMap<String, String> bean = mlist.get(position);
        if (getItemViewType(position) == 0) {
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.hot_two_item, parent, false);
                holder.img = (ImageView) view.findViewById(R.id.hot_two_item_image);
                holder.title = (TextView) view.findViewById(R.id.hot_two_item_title);
                holder.time = (TextView) view.findViewById(R.id.hot_two_item_time);
                holder.user = (TextView) view.findViewById(R.id.hot_two_item_Plnum);
                holder.type= (TextView) view.findViewById(R.id.hot_two_item_type);
                holder.ctr = (TextView) view.findViewById(R.id.hot_two_item_ctr);
                holder.abs= (TextView) view.findViewById(R.id.hot_two_item_abs);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Glide.with(context).load(bean.get("image"))
                    .override(screenwidth * 3 / 10, screenwidth / 5)
                    .into(holder.img);
            holder.title.setText(mApplication.ST(bean.get("title")));
            String time=bean.get("time");
            holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(time)));
            holder.user.setText(mApplication.ST(bean.get("issuer")));
            holder.type.setText(mApplication.ST(bean.get("tag")));
            holder.ctr.setText(mApplication.ST("阅读:" + NumUtils.getNumStr(bean.get("ctr"))));
            holder.user.setTag(bean.get("active"));
            holder.abs.setText(mApplication.ST(bean.get("abstract")));

        } else if (getItemViewType(position) == 1) {
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.zixun_video_item, parent, false);
//                holder.player = (JCVideoPlayerStandard) view.findViewById(R.id.zixun_video_player);
                holder.stub = (FrameLayout) view.findViewById(R.id.zixun_video_stub);
                holder.title = (TextView) view.findViewById(R.id.zixun_video_title);
                holder.time = (TextView) view.findViewById(R.id.zixun_video_time);
                holder.user = (TextView) view.findViewById(R.id.zixun_video_user);
                holder.ctr = (TextView) view.findViewById(R.id.zixun_video_Num);
                holder.type= (TextView) view.findViewById(R.id.zixun_video_tag);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if(!bean.get("videourl").endsWith("mp3")){
                JCVideoPlayer.releaseAllVideos();
                if(holder.stub.getChildAt(0) instanceof JCVideoPlayerStandard){
                    JCVideoPlayerStandard jc0= (JCVideoPlayerStandard) holder.stub.getChildAt(0);
                    Glide.with(context).load(bean.get("image")).override(screenwidth- DimenUtils.dip2px(context,10),(screenwidth- DimenUtils.dip2px(context,10))/2)
                            .centerCrop().into(jc0.thumbImageView);
                    jc0.setUp(bean.get("videourl"),bean.get("title"));
                    jc0.titleTextView.setVisibility(View.GONE);
                    jc0.fullscreenButton.setVisibility(View.GONE);
                }else{
                    holder.stub.removeAllViews();
                    JCVideoPlayerStandard jc=new JCVideoPlayerStandard(context);
                    Glide.with(context).load(bean.get("image")).override(screenwidth- DimenUtils.dip2px(context,30),(screenwidth- DimenUtils.dip2px(context,30))/2)
                            .centerCrop().into(jc.thumbImageView);
                    jc.setUp(bean.get("videourl"),bean.get("title"));
                    jc.titleTextView.setVisibility(View.GONE);
                    jc.fullscreenButton.setVisibility(View.GONE);
                    holder.stub.addView(jc);
                }


            }
//            Glide.with(context).load(bean.get("image")).override(screenwidth- DimenUtils.dip2px(context,10),(screenwidth- DimenUtils.dip2px(context,10))/2)
//                    .centerCrop().into(holder.player.thumbImageView);
//            holder.player.setUp(bean.get("videourl").trim(),bean.get("title"));
//            holder.player.titleTextView.setVisibility(View.GONE);
//            holder.player.fullscreenButton.setVisibility(View.GONE);
            holder.title.setText(mApplication.ST(bean.get("title")));
            holder.title.setTag(bean.get("videourl"));
            holder.user.setTag(bean.get("active"));
            holder.ctr.setTag(bean.get("image"));
            holder.type.setText(mApplication.ST(bean.get("tag")));
            holder.user.setText(mApplication.ST(bean.get("issuer")));
            holder.ctr.setText(mApplication.ST("阅读:" + NumUtils.getNumStr(bean.get("ctr"))));
            holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(bean.get("time"))));
        }


        return view;
    }

    static class ViewHolder {
        ImageView img;
        TextView title;
        TextView time;
        TextView user;
        TextView ctr;
        TextView type;
        TextView abs;
//        JCVideoPlayerStandard player;
        FrameLayout stub;
    }

}
