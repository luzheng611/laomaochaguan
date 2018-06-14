package com.laomachaguan.TouGao.Photo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.laomachaguan.R;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.LogUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/**
 * Created by Administrator on 2016/10/19.
 */
public class PhotoPicker extends AppCompatActivity implements View.OnClickListener, ImageGridAdapter.onSelectedListener ,ListImageDirPopupWindow.OnImageDirSelected
,ImageGridAdapter.onCuptureListener {
    private ImageView back;
    private TextView fasong, chooseFile, totalNum;
    private GridView Grid;//图片展示的view
    private int mScreenHeight;
    private ProgressDialog mProgressDialog;
    private static final String TAG = "PhotoPicker";
    private HashSet<String> mDirPaths;//辅助判断是否扫描同一个文件的文件名集合
    private int totalSize = 0;//扫描的所有图片
    private List<ImageFloder> mImageFloders;
    private int mPicsSize = 0;//选择的文件夹的图片数量
    private File mImgDir;//选择的文件夹
    private List<String> mImgs;//所选择的文件夹的图片名集合
    private ImageGridAdapter mAdapter;//加载图片的适配器
    private static int hasSelectedPicSize;//已选择过的图片数量
    private ArrayList<String> mAbsolutePaths = new ArrayList<>();//点击完成时传递过来的图片绝对路径集合
    private ListImageDirPopupWindow mListImageDirPopupWindow;

    private String cupturePath="";//gridView中拍摄照片的回调地址
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            //为View绑定数据
            data2View();
            //初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw()
    {
        if(mListImageDirPopupWindow==null){
            mListImageDirPopupWindow = new ListImageDirPopupWindow(
                    WindowManager.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                    mImageFloders, LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.list_dir_contentview, null));
        }

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            @Override
            public void onDismiss()
            {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    /**
     * 绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
            Toast.makeText(PhotoPicker.this, "未搜索到图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")
                        ||(filename.endsWith(".JPG")||filename.endsWith("JPEG"))||(filename.endsWith(".PNG")))
                    return true;
                return false;
            }
        }));//获取该文件夹下所有文件的名字
        mAdapter = new ImageGridAdapter(PhotoPicker.this, mImgs, mImgDir.getAbsolutePath(), hasSelectedPicSize);//实例化适配器
        mAdapter.setOnSelectedListener(this);//添加选择事件的回调接口
        mAdapter.setOnCuptureListener(this);//添加图片回调接口
        Grid.setAdapter(mAdapter);
        totalNum.setText("图片 " + totalSize + "张");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.w("onActivityResult: grid中拍摄照片返回的数据"+data+  "返回码："+resultCode);
        if(resultCode==RESULT_OK){
            if(data!=null){
                Uri uri=data.getData();
                if(uri!=null){
                    String path= ImageUtil.getRealPathFromURI(this,uri);
                    mAdapter.mSelectedImages.add(path);
                    hasSelectedPicSize++;
                    mAdapter.setHasSelectedSize(hasSelectedPicSize);
                    mAbsolutePaths.add(path);
                    fasong.setText("完成" + hasSelectedPicSize + "/9");
                }else{
                    Toast.makeText(PhotoPicker.this, "拍摄失败，请重新尝试", Toast.LENGTH_SHORT).show();
                }
            }else{
                Log.w(TAG, "onActivityResult: data_+_+_+_+>"+data );
                if(cupturePath.equals("")){
                    Toast.makeText(PhotoPicker.this, "拍摄失败，请重新尝试", Toast.LENGTH_SHORT).show();
                }else{
                    mAdapter.mSelectedImages.add(cupturePath);
                    mAdapter.setHasSelectedSize(hasSelectedPicSize);
                    mAbsolutePaths=mAdapter.mSelectedImages;
                    fasong.setText("完成" + (hasSelectedPicSize+mAbsolutePaths.size()) + "/9");
                   LogUtil.w("onActivityResult: 拍摄成功，拿到地址：图片文件大小："+new File(cupturePath).length()+"   路径："+cupturePath );
                    showPreImageView();//拍照预览
                }
            }
            }

    }

    /**
     * gridView中拍照后的预览
     */
    private void showPreImageView() {
        int screenWidth=getResources().getDisplayMetrics().widthPixels;
        int screenHeight=getResources().getDisplayMetrics().heightPixels;
        final AlertDialog dilog=new AlertDialog.Builder(this).create();
        View view=LayoutInflater.from(this).inflate(R.layout.layout_grid_cupture_dialog,null);
        final ImageView i= (ImageView) view.findViewById(R.id.grid_pre_img);
        final TextView t1= (TextView) view.findViewById(R.id.grid_pre_text);
//
//        LinearLayout l=new LinearLayout(this);
//        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(screenWidth/4,0,0,0);
//        l.setLayoutParams(lp);
//        l.setOrientation(LinearLayout.VERTICAL);
////
//        ImageView img=new ImageView(this);
//        LinearLayout.LayoutParams lp1=new LinearLayout.LayoutParams(screenWidth*2/3,screenHeight*2/3);
//        img.setLayoutParams(lp1);
//        l.addView(img);
        Glide.with(this).load(cupturePath).asBitmap().override(screenWidth*2/3,screenHeight*2/3)
                .skipMemoryCache(true).centerCrop().placeholder(R.color.e6e6e6).into(new BitmapImageViewTarget(i){
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                super.onResourceReady(resource, glideAnimation);
                LogUtil.w("showPreImageView: tGLide处理过后的图片所占内存： "+resource.getRowBytes()*resource.getHeight()+"       "
                        +resource.getHeight()+"   b.getw -=--=>"+resource.getWidth());
                RoundedBitmapDrawable rb= RoundedBitmapDrawableFactory.create(getResources(),resource);
                rb.setCornerRadius(20);
                i.setImageDrawable(rb);
                t1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dilog.dismiss();
                        if(!resource.isRecycled()){
                            resource.recycle();
                            System.gc();
                            i.setImageDrawable(null);
                        }
                    }
                });
            }
        });

//        TextView t=new TextView(this);
//        t.setLayoutParams(new LinearLayout.LayoutParams(screenWidth*2/3, ViewGroup.LayoutParams.WRAP_CONTENT));
//        t.setPadding(0, DimenUtils.dip2px(this,10),0,DimenUtils.dip2px(this,10));
//        t.setText("完成");
//        t.setGravity(Gravity.CENTER);
//        t.setTextSize(20);
//        t.setTextColor(Color.BLACK);
//        t.setBackgroundResource(R.drawable.back_sel);

//        l.addView(t);

        dilog.setView(view);
        Window window=dilog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        dilog.show();
//        ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_picker);
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        initView();
        getImages();
        initEvent();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>=2){
            getImages();
        }
    }

    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(PhotoPicker.this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        } else {
            //显示进度条
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},0x00);
                    return;
                }
            }
            mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver mContentResolver = getContentResolver();
                    //只查询jpeg和png
                    Cursor cursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

                    while (cursor.moveToNext()) {
                        String firstImage = null;
                        //获取图片的路径
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                        Log.w(TAG, "run: path------->" + path);
                        //拿到第一张图的路径
                        if (firstImage == null) {
                            firstImage = path;
                        }
                        //获取该图片的父目录名
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null) {
                            continue;
                        }
                        String dirPath = parentFile.getAbsolutePath();
                        ImageFloder imagefloder = null;
                        // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                        if (mDirPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mDirPaths.add(dirPath);
                            imagefloder = new ImageFloder();
                            imagefloder.setDir(dirPath);
                            imagefloder.setFirstImagePath(firstImage);
                        }
                        if (parentFile.list() == null) {
                            continue;
                        }
                        int picSize = parentFile.list(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String filename) {
                                if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")
                                ||(filename.endsWith(".JPG")||filename.endsWith("JPEG"))||(filename.endsWith(".PNG"))){
                                    return true;
                                } else
                                    return false;
                            }
                        }).length;
                        totalSize += picSize;
                        imagefloder.setCount(picSize);
                        mImageFloders.add(imagefloder);

                        ///
                        ///
                        //
                        //
                        if (picSize > mPicsSize) {
                            mPicsSize = picSize;
                            mImgDir = parentFile;
                        }

                    }
                    cursor.close();
                    //扫描完成，释放临时hashset
                    mDirPaths = null;
                    mHandler.sendEmptyMessage(0x110);
                }
            }).start();
        }

    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        chooseFile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.dialogWindowAnim);
                mListImageDirPopupWindow.showAsDropDown(chooseFile, 0, 0);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

    }


    private void initView() {
        back = (ImageView) findViewById(R.id.photo_picker_back);
        fasong = (TextView) findViewById(R.id.photo_picker_fasong);
        chooseFile = (TextView) findViewById(R.id.photo_picker_choose_file);
        Grid = (GridView) findViewById(R.id.photo_picker_grid);
        mDirPaths = new HashSet<>();//判断是否扫描同一个文件夹
        mImageFloders = new ArrayList<>();//包含所有文件信息的list
        totalNum = (TextView) findViewById(R.id.photo_picker_totalNum);
        back.setOnClickListener(this);
        fasong.setOnClickListener(this);
        chooseFile.setOnClickListener(this);

        hasSelectedPicSize = getIntent().getIntExtra("num", 0);
        fasong.setText("完成" + hasSelectedPicSize + "/9");
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.photo_picker_back:
                finish();
                break;
            case R.id.photo_picker_choose_file:
                break;
            case R.id.photo_picker_fasong:
                intent.putExtra("array", mAbsolutePaths);
                setResult(111, intent);
                finish();
                break;
        }
    }

    @Override
    public void onSelected(ArrayList<String> mSelectedImages) {
        int size = mSelectedImages.size();
        fasong.setText("完成" + (hasSelectedPicSize + size) + "/9");
        mAbsolutePaths = mSelectedImages;
    }

    /**
     * 得到本地图片文件
     *
     * @param context
     * @return
     */
    public static ArrayList<HashMap<String, String>> getAllPictures(Context context) {
        ArrayList<HashMap<String, String>> picturemaps = new ArrayList<>();
        HashMap<String, String> picturemap;
        ContentResolver cr = context.getContentResolver();
        //先得到缩略图的URL和对应的图片id
        Cursor cursor = cr.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Images.Thumbnails.IMAGE_ID,
                        MediaStore.Images.Thumbnails.DATA
                },
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            do {
                picturemap = new HashMap<>();
                picturemap.put("image_id_path", cursor.getInt(0) + "");
                picturemap.put("thumbnail_path", cursor.getString(1));
                picturemaps.add(picturemap);
            } while (cursor.moveToNext());
            cursor.close();
        }
        //再得到正常图片的path
        for (int i = 0; i < picturemaps.size(); i++) {
            picturemap = picturemaps.get(i);
            String media_id = picturemap.get("image_id_path");
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Media.DATA
                    },
                    MediaStore.Audio.Media._ID + "=" + media_id,
                    null,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    picturemap.put("image_id", cursor.getString(0));
                    picturemaps.set(i, picturemap);
                    if (cursor.getString(0) == null) {
                        picturemaps.remove(i);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            if(cursor!=null&&!cursor.isClosed())cursor.close();
        }
        return picturemaps;
    }

    @Override
    public void selected(ImageFloder floder,int positoin) {
        mImgDir = new File(floder.getDir());
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")
                        ||(filename.endsWith(".JPG")||filename.endsWith("JPEG"))||(filename.endsWith(".PNG")))
                    return true;
                return false;
            }
        }));
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter.setDatas(mImgs);
        mAdapter.setmDirPath(floder.getDir());
        mAdapter.setHasSelectedSize(hasSelectedPicSize);//已选中的图片数量
        mAdapter.notifyDataSetChanged();
        // mAdapter.notifyDataSetChanged();
        totalNum.setText(floder.getCount() + "张");
        chooseFile.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();
    }

    @Override
    public void onCupture(String path) {
        cupturePath=path;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListImageDirPopupWindow=null;
        mAdapter=null;
        mAbsolutePaths=null;
        mImageFloders=null;
        mImgs=null;
    }
}
