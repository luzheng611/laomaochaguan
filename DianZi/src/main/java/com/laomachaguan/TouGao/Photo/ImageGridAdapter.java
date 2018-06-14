package com.laomachaguan.TouGao.Photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.mApplication;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/10/20.
 */
public class ImageGridAdapter extends BaseAdapter {
    private static final int MAX_CHOOSE_NUM=9;
    private static final String TAG = "ImageGridAdapter";
    //用户选择的完整的路径集合
    public ArrayList<String> mSelectedImages = new ArrayList<String>();
    //显示的图片名称集合
    private List<String> mShowImages = new LinkedList<String>();
    //文件夹路径
    private String mDirPath;
    //上下文对象
    private WeakReference<Context> w;//若引用
    private Context context;
    //    是否是加载的图片最多的文件夹
//    private boolean isMostNumDir;
    private LayoutInflater inflater;
    private int mScreenWidth, dp4;
    private onSelectedListener onselectedlistener;
    private int hasSelectedSize;
    private onCuptureListener onCuptureListener;

    public void setOnSelectedListener(onSelectedListener listener) {
        this.onselectedlistener = listener;
    }

    public void setOnCuptureListener(onCuptureListener listener) {
        this.onCuptureListener = listener;
    }

    public ImageGridAdapter(Context context, List<String> mDatas, String dirPath, int hasSelectedSize) {
        super();
        w = new WeakReference<Context>(context);
        mShowImages.add(0, "add");
        this.mShowImages.addAll(mDatas);
        this.mDirPath = dirPath;
        this.context = w.get();
        inflater = LayoutInflater.from(context);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.hasSelectedSize = hasSelectedSize;
    }

    public void setDatas(List<String> datas) {
        mShowImages.clear();
        mShowImages.add(0, "add");
        this.mShowImages.addAll(datas);
    }

    public void setHasSelectedSize(int hasSelectedSize) {
        this.hasSelectedSize = hasSelectedSize;
    }

    public void setmDirPath(String mDirPath) {
        this.mDirPath = mDirPath;
    }

//    public void setIsMostNumDir(boolean flag) {
//        this.isMostNumDir = flag;
//        if (isMostNumDir)
//            mShowImages.add(0, "camera");
//    }

    @Override
    public int getCount() {
        return mShowImages == null ? 0 : mShowImages.size();
    }

    @Override
    public Object getItem(int position) {
        return mShowImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolder0 holder0 = null;
        final String path = mDirPath + "/" + mShowImages.get(position);//图片的绝对路径
        if (view == null) {
            if (getItemViewType(position) == 0) {
                holder0 = new ViewHolder0();
                view = inflater.inflate(R.layout.photo_picker_item_camera, parent, false);
                holder0.imageView = (ImageView) view.findViewById(R.id.photo_picker_item_camera_img);
                holder0.imageView.setMinimumWidth((mScreenWidth - dp4) / 3);
                holder0.imageView.setMinimumHeight((mScreenWidth - dp4) / 3);
                view.setTag(holder0);
            } else {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.photo_picker_grid_item, parent, false);
                holder.imageView = (ImageView) view.findViewById(R.id.photo_picker_item_imageView);
                holder.root = (FrameLayout) view.findViewById(R.id.root);
                holder.check = (ImageView) view.findViewById(R.id.photo_picker_item_checkbox);
                holder.imageView.setMinimumWidth((mScreenWidth - dp4) / 3);
                holder.imageView.setMinimumHeight((mScreenWidth - dp4) / 3);
                view.setTag(holder);
            }
        } else {
            if (getItemViewType(position) == 0) {
                holder0 = (ViewHolder0) view.getTag();
            } else {
                holder = (ViewHolder) view.getTag();
            }
        }

        if (getItemViewType(position) == 0) {
            Glide.with(context).load(R.drawable.camera).override((mScreenWidth - dp4) / 3, (mScreenWidth - dp4) / 3)
                    .into(holder0.imageView);
            holder0.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w(TAG, "onClick: 准备拍照");
                    if (hasSelectedSize + mSelectedImages.size() < MAX_CHOOSE_NUM) {
                        chooseCamera();
                    } else {
                        Toast.makeText(context, "最多能够上传"+MAX_CHOOSE_NUM+"张图片", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            holder.check.setTag(position);
            holder.imageView.setColorFilter(null);
            //防止选中图片错位
            if (mSelectedImages.contains(path)) {
                holder.check.setImageResource(R.drawable.icon_data_select);
                holder.imageView.setColorFilter(Color.parseColor("#70000000"));
            } else {
                holder.check.setImageResource(R.drawable.pick_checkbox_bg);
                holder.imageView.setColorFilter(null);
            }
            //防止选中图片错位
            Glide.with(context).load(path).override((mScreenWidth - dp4) / 3, (mScreenWidth - dp4) / 3)
                    .skipMemoryCache(true).placeholder(R.drawable.place_holder2).centerCrop().into(holder.imageView);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w(TAG, "onClick: 选中图片");

                    ImageView img = (ImageView)v.findViewById(R.id.photo_picker_item_imageView);
                    ImageView check= (ImageView) v.findViewById(R.id.photo_picker_item_checkbox);
                    if (!mSelectedImages.contains(path)) {
                        if (hasSelectedSize + mSelectedImages.size() >= MAX_CHOOSE_NUM) {
                            Toast.makeText(context, "最多能够上传"+MAX_CHOOSE_NUM+"张图片", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mSelectedImages.add(path);
                       check.setImageResource(R.drawable.icon_data_select);
                        img.setColorFilter(Color.parseColor("#70000000"));
                    } else {
                        mSelectedImages.remove(path);
                      check.setImageResource(R.drawable.pick_checkbox_bg);
                        img.setColorFilter(null);
                    }
                    if (onselectedlistener != null) {
                        onselectedlistener.onSelected(mSelectedImages);
                    }
                }
            });
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.w(TAG, "onClick: 进入大图界面");
//                }
//            });
        }

        return view;
    }

    static class ViewHolder {
        ImageView imageView, check;
        FrameLayout root;
    }

    static class ViewHolder0 {
        ImageView imageView;
    }

    public interface onSelectedListener {
        void onSelected(ArrayList<String> mSelectedImages);
    }

    public interface onCuptureListener {
        void onCupture(String path);
    }

    /**
     * 调用相机
     */
    private void chooseCamera() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
                    Locale.CHINA);
            // Standard Intent action that can be sent to have the camera
            // application capture an image and return it.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        ContentValues attrs = new ContentValues();
//        attrs.put(MediaStore.Images.Media.DISPLAY_NAME,
//                dateFormat.format(new Date(System.currentTimeMillis())));// 添加照片名字
//        attrs.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");// 图片类型
//        attrs.put(MediaStore.Images.Media.DESCRIPTION, "");// 图片描述
//        // //插入图片 成功则返回图片所对应的URI 当然我们可以自己指定图片的位置
//        Uri pictureUri = context.getContentResolver().insert(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, attrs);
            File file = new File(Constants.ExternalCacheDir + PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", "") + "/" + "media/image/");
            if (!file.exists()) {
                file.mkdirs();
                Log.w(TAG, "chooseCamera: 路径：" + file);
            }
            file = new File(Constants.ExternalCacheDir + PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", "") + "/" + "media/image/", System.currentTimeMillis() + ".jpg");
            Log.w(TAG, "chooseCamera: 储存地址：" + file.getAbsolutePath());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));// 指定照片的位置
            ((Activity) context).startActivityForResult(takePictureIntent, 11);
            if (onCuptureListener != null) {
                onCuptureListener.onCupture(file.getAbsolutePath());
            }
        } else {
            Toast.makeText(context, "请确认插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

}
