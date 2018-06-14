package com.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.chrisbanes.photoview.PhotoView;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifImageView;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String path = "array";
    private static final String pos = "position";
    private PhotoViewPager viewPager;
    private ImageView back;
    private TextView position, num;
    public ImageView choose;
    private ArrayList<String> paths;
    private photoAdapter adapter;
    private ScreenSwitchUtils instance;//重力感应辅助类
    private int screenWidth, screenHeight;
    private FrameLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        root = (FrameLayout) findViewById(R.id.root);
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        instance = ScreenSwitchUtils.init(this.getApplicationContext());
        viewPager = (PhotoViewPager) findViewById(R.id.viewpager);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        position = (TextView) findViewById(R.id.position);
        num = (TextView) findViewById(R.id.num);
        choose = (ImageView) findViewById(R.id.choose);
        choose.setOnClickListener(this);

        paths = new ArrayList<>();

//        paths.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1497" +
//                "945712190&di=adbf25543b0e425e0d55e4e4fddd947c&img" +
//                "type=0&src=http%3A%2F%2Fscimg.jb51.net%2Fallimg%2F1607" +
//                "26%2F2-160H6204Q2G3.gif");
//        paths.add("http://indrah.cn/public/uploads/user/2017-06-18/59464ea147018.jpg");
//        paths.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=14979536" +
//                "30731&di=c508ccb9f6b02003241e2ec2a51c0ec7&imgtype=" +
//                "0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01d81457117b5c6ac725134387d3a3.gif");
//        paths.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&" +
//                "sec=1497945775499&di=53a8458f8a36d73cfd95c6cf089ef518&imgtype=0&src=http%3A%2F%2" +
//                "Fup.qqya.com%2Fallimg%2F2016-d10%2F16-102201_896.gif");


        initDatas();//初始化数据

        adapter = new photoAdapter(this, paths);
//        viewPager.setPageTransformer(false,new FirstTransFormer(1));
//        viewPager.setOffscreenPageLimit(paths.size());
        viewPager.setAdapter(adapter);


//        View rl_gif = LayoutInflater.from(this).inflate(R.layout.gif_progress_group, null);//这种方式容易导致内存泄漏
//        GifImageView gifImageView = (GifImageView) rl_gif.findViewById(R.id.gif_photo_view);
//        ProgressWheel progressWheel = (ProgressWheel) rl_gif.findViewById(R.id.progress_wheel);
//        TextView tv_progress = (TextView) rl_gif.findViewById(R.id.tv_progress);
//        AlxGifHelper.displayImage(paths.get(3),gifImageView,progressWheel,tv_progress,0);//最后一个参数传0表示不缩放gif的大小，显示原始尺寸
//        root.addView(rl_gif);
    }

    private void initDatas() {
        paths = getIntent().getStringArrayListExtra(path);
        if (paths != null) {
            num.setText("/" + paths.size());
            position.setText(getIntent().getStringExtra(pos));
            viewPager.setCurrentItem(Integer.valueOf(getIntent().getStringExtra(pos)));
        } else {
            num.setText("");
            position.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.back){
            finish();
        }else if(v.getId()==R.id.choose){
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setItems(new String[]{"保存图片"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    saveImage();
                }
            }).create().show();
        }

    }

    /**
     * 速度最快的复制文件方式
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                saveImage();
        }
    }

    private void saveImage() {
        final String TAG="图片下载";
        Log.e("图片下载", "run: " +paths.get(viewPager.getCurrentItem()));
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                Log.e("图片下载", "run: " +paths.get(viewPager.getCurrentItem()));
                return;
            }
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {

                    Log.e("图片下载", "run: " +paths.get(viewPager.getCurrentItem()));
                    Glide.with(PhotoActivity.this).load(paths.get(viewPager.getCurrentItem()))
                            .asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Log.e(TAG, "onResourceReady:内存大小 "+resource.getByteCount() );
                            File file=new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+".jpg");
                            Log.e(TAG,"文件地址 "+file.length());
                            try {
                                FileOutputStream fos=new FileOutputStream(file);
                                resource.compress(Bitmap.CompressFormat.JPEG,100,fos);
                                if(fos!=null){
                                    fos.close();
                                }
                                Toast.makeText(PhotoActivity.this, "文件已下载至"+file.getAbsolutePath()+",共消耗"+(file.length()/1000)+"KB", Toast.LENGTH_SHORT).show();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });

//            }
//        }).start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        instance.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        instance.stop();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("test", "onConfigurationChanged");
        if (instance.isPortrait()) {
            // 切换成竖屏


            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(screenWidth, screenHeight);
            root.setLayoutParams(params1);
            Log.e("test", "竖屏");
        } else {

            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(screenHeight, screenWidth);
            root.setLayoutParams(params1);
            Log.e("test", "横屏");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private static class photoAdapter extends PagerAdapter {
        private ArrayList<String> paths;
        private Activity activity;
        private int screenWidth;
        private ExecutorService es;

        public photoAdapter(Activity activity, ArrayList<String> paths) {
            super();
            this.paths = paths;
            WeakReference<Activity> w = new WeakReference<Activity>(activity);
            this.activity = w.get();
            screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
            es = Executors.newSingleThreadExecutor();
        }

        @Override
        public int getCount() {
            return paths == null ? 0 : paths.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            if (paths.get(position).endsWith("gif")) {
                if (activity != null) {
                    ((PhotoActivity) activity).choose.setVisibility(View.GONE);
                }
                Log.e("AlexGIF", "现在是gif大图");
                View rl_gif = LayoutInflater.from(activity).inflate(R.layout.gif_progress_group, null);//这种方式容易导致内存泄漏
                GifImageView gifImageView = (GifImageView) rl_gif.findViewById(R.id.gif_photo_view);
                ProgressWheel progressWheel = (ProgressWheel) rl_gif.findViewById(R.id.progress_wheel);
                TextView tv_progress = (TextView) rl_gif.findViewById(R.id.tv_progress);
                AlxGifHelper.displayImage(paths.get(position), gifImageView, progressWheel, tv_progress, 0);//最后一个参数传0表示不缩放gif的大小，显示原始尺寸
                try {
                    container.addView(rl_gif);//这里要注意由于container是一个复用的控件，所以频繁的addView会导致多张相同的图片重叠，必须予以处置
                } catch (Exception e) {
                    Log.e("AlexGIF", "父控件重复！！！！，这里出现异常很正常", e);
                }
                return rl_gif;
            } else {
                if (activity != null) {
                    ((PhotoActivity) activity).choose.setVisibility(View.VISIBLE);
                }
                PhotoView photo = new PhotoView(activity);
                Glide.with(activity).load(paths.get(position)).asBitmap().thumbnail(0.5f).into(photo);
                container.addView(photo);
                return photo;
            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);

        }

//        private  static class gifRunnable implements Runnable {
//            private String path;
//            private GifImageView gifImageView;
//            private Activity activity;
//            private String TAG="gifRunanble";
//
//            public gifRunnable(Activity activity, String url, GifImageView gifImageView) {
//                super();
//                this.path = url;
//                this.gifImageView = gifImageView;
//                WeakReference<Activity> w = new WeakReference<Activity>(activity);
//                this.activity = w.get();
//            }
//
//            @Override
//            public void run() {
//
//                try {
//                    URL url = new URL(path);
//                    BufferedInputStream bi=new BufferedInputStream(url.openStream());
//                    final GifDrawable gifDrawable = new GifDrawable(bi);
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(activity,"显示gif",Toast.LENGTH_SHORT).show();
//                            gifImageView.setImageDrawable(gifDrawable);
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                    Log.e(TAG, "run: 加载gif报错"+e.getMessage());
//                }
//
//            }
//        }


    }


    private void quitFullScreen() {
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
