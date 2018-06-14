package com.laomachaguan.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.lzy.okhttputils.OkHttpUtils;
import com.yalantis.ucrop.UCrop;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;
import com.zxy.tiny.callback.FileCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * 作者：因陀罗网 on 2017/6/18 14:12
 * 照片照相
 * 公司：成都因陀罗网络科技有限公司
 */

public class PhotoUtil {
    public static final int TAKEPICTURE = 1;
    public static final int CHOOSEPICTUE = 2;

    private static final int CUT = 3;// 裁剪
    private static File file;

    /*
    选择照片或照相
        dirName  图片保存目录名称   区分各个图片功能
     */
    public static void choosePic(final Activity context, final String dirName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
        builder.setView(view);
        builder.setCancelable(true);
        final Dialog dialog = builder.create();
        view.findViewById(R.id.photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    if (permission1 != PackageManager.PERMISSION_GRANTED) {

                        context.requestPermissions(new String[]{Manifest.permission.CAMERA}, TAKEPICTURE);

                    } else {
                        takePhoto(context, dirName);
                        dialog.dismiss();
                    }
                } else {
                    takePhoto(context, dirName);
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCheck = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int permissionCheck1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
                    int permissionCheck2 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED
                            && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {

                        context.requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                                        , Manifest.permission.READ_EXTERNAL_STORAGE},
                                CHOOSEPICTUE);


                    } else {
                        choosePhotoAlbum(context);
                        dialog.dismiss();
                    }
                } else {
                    choosePhotoAlbum(context);
                    dialog.dismiss();
                }

            }
        });

        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = context.getResources().getDisplayMetrics().widthPixels;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wl);
        dialog.show();
    }

    /*
  选择照片或照相
      dirName  图片保存目录名称   区分各个图片功能
      fragment 实现
   */
    public static void choosePic(final Fragment context, final String dirName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context.getActivity());
        View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
        builder.setView(view);
        builder.setCancelable(true);
        final Dialog dialog = builder.create();
        view.findViewById(R.id.photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    if (permission1 != PackageManager.PERMISSION_GRANTED) {

                        context.requestPermissions(new String[]{Manifest.permission.CAMERA}, TAKEPICTURE);

                    } else {
                        takePhoto(context, dirName);
                        dialog.dismiss();
                    }
                } else {
                    takePhoto(context, dirName);
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCheck = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int permissionCheck1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
                    int permissionCheck2 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED
                            && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {

                        context.requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                                        , Manifest.permission.READ_EXTERNAL_STORAGE},
                                CHOOSEPICTUE);


                    } else {
                        choosePhotoAlbum(context);
                        dialog.dismiss();
                    }
                } else {
                    choosePhotoAlbum(context);
                    dialog.dismiss();
                }

            }
        });

        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = context.getResources().getDisplayMetrics().widthPixels;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wl);
        dialog.show();
    }

    public static String getPath(Activity activity, String dirName) {

        return getAppFile(activity, Constants.NAME_LOW + "_" + dirName + "/") + PreferenceUtil.getUserIncetance(activity).getString("user_id", "") + ".jpg";
    }

    public static String getPath(Fragment activity, String dirName) {

        return getAppFile(activity.getActivity(), Constants.NAME_LOW + "_" + dirName + "/") + PreferenceUtil.getUserIncetance(activity.getActivity()).getString("user_id", "") + ".jpg";
    }

    /**
     * 拍照
     */
    private static void takePhoto(Activity context, String dirName) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = new File(getAppFile(context.getApplicationContext(), Constants.NAME_LOW + "_" + dirName));
            File mPhotoFile = new File(getAppFile(context.getApplicationContext(), Constants.NAME_LOW + "_" + dirName + "/") + PreferenceUtil.getUserIncetance(context.getApplicationContext()).getString("user_id", "") + ".jpg");
            if (!file.exists()) {
                file.mkdirs();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileProvider", mPhotoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            }
            context.startActivityForResult(intent, TAKEPICTURE);

        } catch (Exception e) {
            LogUtil.e("照相报错");
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     */
    private static void takePhoto(Fragment context, String dirName) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = new File(getAppFile(context.getActivity(), Constants.NAME_LOW + "_" + dirName));
            File mPhotoFile = new File(getAppFile(context.getActivity(), Constants.NAME_LOW + "_" + dirName + "/") + PreferenceUtil.getUserIncetance(context.getActivity()).getString("user_id", "") + ".jpg");
            LogUtil.e("父目录：：：" + file + "    照片文件;:;" + mPhotoFile);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context.getActivity(), context.getActivity().getPackageName() + ".fileProvider", mPhotoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            }
            context.startActivityForResult(intent, TAKEPICTURE);

        } catch (Exception e) {
            LogUtil.e("照相报错");
            e.printStackTrace();
        }
    }

    private static File FilecreateImageFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
            file = File.createTempFile(imageFileName, ".jpg",
                    Environment.getExternalStorageDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return file;
    }

    /**
     * 相册
     */
    private static void choosePhotoAlbum(Activity context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        context.startActivityForResult(intent, CHOOSEPICTUE);

    }

    /**
     * 相册
     */
    private static void choosePhotoAlbum(Fragment context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        context.startActivityForResult(intent, CHOOSEPICTUE);
    }

    /**
     * 跳转到系统裁剪图片页面
     *
     * @param imagePath 需要裁剪的图片路径
     */
    private static void cropPic(Activity context, Object imagePath) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (imagePath instanceof Uri) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(((Uri) imagePath), "image/*");
            } else {
                intent.setDataAndType(((Uri) imagePath), "image/*");
            }

        } else if (imagePath instanceof String) {
            File file = new File(((String) imagePath));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
                intent.setDataAndType(contentUri, "image/*");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "image/*");
            }
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", context.getResources().getDisplayMetrics().widthPixels);
        intent.putExtra("outputY", context.getResources().getDisplayMetrics().widthPixels);
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
        LogUtil.e("准备裁剪");
        context.startActivityForResult(intent, CUT);
    }

    /**
     * 跳转到系统裁剪图片页面
     *
     * @param imagePath 需要裁剪的图片路径
     */
    private static void cropPic(Fragment context, Object imagePath) {

//        Intent intent = new Intent("com.android.camera.action.CROP");
//        if(imagePath instanceof Uri){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.setDataAndType(((Uri) imagePath), "image/*");
////                intent.putExtra("output",Uri.fromFile(FilecreateImageFile()));
//            } else {
//                intent.setDataAndType(((Uri) imagePath), "image/*");
////                intent.putExtra("output",Uri.fromFile(FilecreateImageFile()));
//            }
//
//        }else if(imagePath instanceof String){
//            File file = new File(((String) imagePath));
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Uri contentUri = FileProvider.getUriForFile(context.getActivity(), context.getActivity().getPackageName() + ".fileProvider", file);
//                intent.setDataAndType(contentUri, "image/*");
////                intent.putExtra("output",Uri.fromFile(FilecreateImageFile()));
//            } else {
//                intent.setDataAndType(Uri.fromFile(file), "image/*");
////                intent.putExtra("output",Uri.fromFile(FilecreateImageFile()));
//            }
//        }
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", context.getResources().getDisplayMetrics().widthPixels);
//        intent.putExtra("outputY", context.getResources().getDisplayMetrics().widthPixels);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
//        intent.putExtra("noFaceDetection", true);// 取消人脸识别
//        intent.putExtra("return-data", false);
//        intent.putExtra("scale", true);
//        LogUtil.e("准备裁剪");
//        context.startActivityForResult(intent, CUT);
        File file = new File(getPath(context, "crop"));
        Uri contentUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            contentUri = FileProvider.getUriForFile(context.getActivity(), context.getActivity().getPackageName() + ".fileProvider", file);
        } else {
            contentUri = Uri.fromFile(file);
        }
        UCrop.of(((Uri) imagePath), contentUri)
                .withAspectRatio(16, 9)
                .withMaxResultSize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().widthPixels)
                .start(context.getActivity());
    }

    /**
     * 获取本应用在系统的存储目录
     */
    public static String getAppFile(Context context, String uniqueName) {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getParent();
        } else {
            cachePath = context.getCacheDir().getParent();
        }
        return cachePath + File.separator + uniqueName;
    }
     /*
     判断sdcard是否被挂载
      */

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static void onActivityResult(String dirName, final ImageView imageView, final Fragment context, final int requestCode, int resultCode, Intent data) {
        Uri u = null;
//        LogUtil.e("返回码：："+resultCode+"   请求码："+requestCode+"   UCROP::;"+UCrop.REQUEST_CROP);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSEPICTUE:
                   u= data.getData();// 选择照片的Uri 可能为null
//                    cropPic(context, uri);
                    LogUtil.e("相册回调URI："+u);
                    Glide.with(context).load(u).override( context.getResources().getDisplayMetrics().widthPixels / 4,
                            context.getResources().getDisplayMetrics().widthPixels / 4).into(imageView);
//                    displayImage(imageView, context, uri);
                    Tiny.getInstance().init(context.getActivity().getApplication());
                    Tiny.BitmapCompressOptions tb = new Tiny.BitmapCompressOptions();
                    tb.width = context.getResources().getDisplayMetrics().widthPixels / 4;
                    tb.height = context.getResources().getDisplayMetrics().widthPixels / 4;
                    tb.config = Bitmap.Config.RGB_565;
                    Tiny.FileCompressOptions tf = new Tiny.FileCompressOptions();
                    tf.size = 80;
                    tf.isKeepSampling = true;
                    String path=ImageUtil.getImageAbsolutePath(context.getActivity(),u);
                    Tiny.getInstance().source(path)
                            .asFile()
                            .withOptions(tf)
                            .compress(new FileCallback() {
                                @Override
                                public void callback(boolean isSuccess, String outfile) {
                                    if (isSuccess) {
                                        File file = new File(outfile);
                                        LogUtil.e("压缩文件大小：：" + file.length());
                                        uploadHead(context, file);
                                    }
                                }
                            });

                    break;
                case TAKEPICTURE:
                    LogUtil.e("拍照");
                    if (data != null) {
                        u = data.getData();// 选择照片的Uri 可能为null
//                        cropPic(context, u);
                        displayImage(imageView, context, u);
                    } else {
//                        cropPic(context, Uri.fromFile(new File(getPath(context, dirName))));
                        displayImage(imageView, context,  Uri.fromFile(new File(getPath(context, dirName))));
                    }

                    break;
                case UCrop.REQUEST_CROP:
                    u = UCrop.getOutput(data);

//                case CUT:
//                    if (data != null) {
//
//                        u = data.getData();
//
                        LogUtil.e("裁剪数据;:;" + data + "    " + u);
                    displayImage(imageView, context, u);

            }

//            }
        }
    }

    private static void displayImage(final ImageView imageView, final Fragment context, Uri u) {
        if (u != null) {
            Tiny.getInstance().init(context.getActivity().getApplication());
            Tiny.BitmapCompressOptions tb = new Tiny.BitmapCompressOptions();
            tb.width = context.getResources().getDisplayMetrics().widthPixels / 4;
            tb.height = context.getResources().getDisplayMetrics().widthPixels / 4;
            tb.config = Bitmap.Config.RGB_565;
            Tiny.FileCompressOptions tf = new Tiny.FileCompressOptions();
            tf.size = 80;
            tf.isKeepSampling = true;
            Tiny.getInstance().source(u)
                    .asFile()
                    .withOptions(tf)
                    .compress(new FileCallback() {
                        @Override
                        public void callback(boolean isSuccess, String outfile) {
                            if (isSuccess) {
                                File file = new File(outfile);
                                LogUtil.e("压缩文件大小：：" + file.length());
                                uploadHead(context, file);
                            }
                        }
                    });
            Tiny.getInstance().source(u).asBitmap().withOptions(tb).compress(new BitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap) {
                    if (isSuccess) {
                        LogUtil.e("图片宽：：" + bitmap.getWidth() + "   图片高：：" + bitmap.getHeight()
                                + "图片内存：：：" + bitmap.getByteCount());
                        imageView.setImageBitmap(bitmap);
                    }
                }
            });
        }
    }

    public static void uploadHead(final Fragment context, final File file) {
        final String uid = PreferenceUtil.getUserIncetance(context.getActivity()).getString("user_id", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("user_id", uid);
                    Response response = OkHttpUtils.post(Constants.uploadHead_IP).tag(context)
                            .params("head", file).params("key", ApisSeUtil.getKey())
                            .params("msg", ApisSeUtil.getMsg(js)).execute();
                    String data1 = response.body().string();
                    if (!data1.equals("")) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                        if (map != null && null != map.get("code")) {
                            if ("000".equals(map.get("code"))) {
                                SharedPreferences.Editor ed = PreferenceUtil.getUserIncetance(context.getActivity()).edit();
                                String url = map.get("head");
                                if (url != null) {
                                    ed.putString("head_url", url);
                                    ed.apply();
                                }
                                context.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mApplication.getInstance(), "头像更改成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                context.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mApplication.getInstance(), "头像更改失败", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        } else {
                            context.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mApplication.getInstance(), "服务器异常", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    context.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mApplication.getInstance(), "服务器异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }

            }
        }).start();

    }

}
