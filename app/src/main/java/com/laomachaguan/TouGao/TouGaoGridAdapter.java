package com.laomachaguan.TouGao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.laomachaguan.R;
import com.laomachaguan.TouGao.Photo.PhotoPicker;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.FileUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.ScaleImageUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/20.
 */
public class TouGaoGridAdapter extends BaseAdapter {
    private static final String TAG = "TouGaoGridAdapter";
    //获取从选择器中选择的图片地址
    private ArrayList<String> mImgs = new ArrayList<>();
    private WeakReference<Activity> weakReference;
    private Activity context;
    private LayoutInflater inflater;
    private int dp70;
    private oncCancleListener oncCancleListener;
    private boolean flag;
    private boolean TG = false;

    public void setTG(boolean flag) {
        this.TG = flag;
    }

    public TouGaoGridAdapter(Activity context, ArrayList<String> mImgs, boolean needChange) {
        super();
        weakReference = new WeakReference<Activity>(context);
        this.context = weakReference.get();
        this.mImgs = mImgs;
        inflater = LayoutInflater.from(context);
        dp70 = DimenUtils.dip2px(context, 70);
        this.flag = needChange;
    }

    public void setOncCancleListener(oncCancleListener listener) {
        this.oncCancleListener = listener;
    }

    public void setmImgs(ArrayList<String> mImgs) {
        this.mImgs = mImgs;
    }

    @Override
    public int getCount() {
        return mImgs == null ? 0 : mImgs.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        Viewholder viewholder = null;
        if (view == null) {
            viewholder = new Viewholder();
            view = inflater.inflate(R.layout.tougao_yulan_grid_item, parent, false);
            viewholder.imageView = (ImageView) view.findViewById(R.id.tougao_grid_item_img);
            viewholder.cancle = (ImageView) view.findViewById(R.id.tougao_grid_item_cancle);
            view.setTag(viewholder);
        } else {
            viewholder = (Viewholder) view.getTag();

        }
        LogUtil.e("私信图片~~~~~~~~~~~~~~~~~~~~~~~~~");
        LogUtil.e("预览照片数组："+mImgs+"");
        if (mImgs.get(position).equals("add")) {
            Glide.with(context).load(R.drawable.addimg_with_text).override(DimenUtils.dip2px(context,50),DimenUtils.dip2px(context,50)).fitCenter().into(viewholder.imageView);
//            viewholder.imageView.setImageBitmap(ImageUtil.readBitMap(context, R.drawable.addimg_with_text));
            viewholder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewholder.imageView.setBackgroundResource(R.drawable.person_add_pic_sel);
            viewholder.imageView.setPadding(DimenUtils.dip2px(context, 10), DimenUtils.dip2px(context, 10), DimenUtils.dip2px(context, 10), DimenUtils.dip2px(context, 10));
            viewholder.cancle.setVisibility(View.GONE);
        } else {
            viewholder.imageView.setPadding(0, 0, 0, 0);
            Glide.with(context).load(mImgs.get(position)).override(dp70, dp70).placeholder(R.color.e6e6e6).centerCrop().into(viewholder.imageView);
            if (TG) {
                Glide.with(context).load(mImgs.get(position)).asBitmap().skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.w(TAG, "onResourceReady: 下载好的bitmap" + resource.getByteCount());
                        String t = System.currentTimeMillis() + ".jpg";
                        FileUtils.saveBitmap(resource, t);
                        Log.w(TAG, "onResourceReady: fiele     " + new File(FileUtils.TEMPPAH, t).length());
                        Intent intent = new Intent("TG");
                        intent.putExtra("name", t);
                        intent.putExtra("pos", position + 1);
                        context.sendBroadcast(intent);
                    }
                });
            }

            viewholder.cancle.setVisibility(View.VISIBLE);
            viewholder.cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (oncCancleListener != null) {
                        oncCancleListener.onCancle(position);
                    }
                }
            });
        }

        viewholder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImgs.get(position).equals("add")) {
                    int i = mImgs.size() - 1;//已经选择的图片数量
                    Intent intent = new Intent(context, PhotoPicker.class);
                    intent.putExtra("num", i);
                    context.startActivityForResult(intent, 000);
                } else {
                    //大图模式
                    ScaleImageUtil.openBigIagmeMode(context,mImgs,position);
                }
            }
        });
        if (!flag) {
            viewholder.cancle.setVisibility(View.GONE);
        }
        return view;
    }

    public interface oncCancleListener {
        void onCancle(int positon);
    }

    static class Viewholder {
        ImageView imageView, cancle;
    }
}
