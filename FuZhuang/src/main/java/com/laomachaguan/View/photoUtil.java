package com.laomachaguan.View;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.mApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/12/22.
 */
public class photoUtil {
    public static final int CHOOSEPICTUE = 2;//相册
    public static final int TAKEPICTURE = 1;//相机
    public static Uri uri;
    //选择照片或照相
    public static void choosePic(final Activity context, final int requestId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
        builder.setView(view);
        builder.setCancelable(true);
        final Dialog dialog = builder.create();
        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    if (permission1 != PackageManager.PERMISSION_GRANTED) {
                        context.requestPermissions(new String[]{Manifest.permission.CAMERA}, requestId);
                    } else {
                        chooseCamera(context,uri,requestId);
                        dialog.dismiss();
                    }
                } else {
                    chooseCamera(context,uri,requestId);
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
                    if (permissionCheck
                            != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED
                            && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
//
                        context.requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                                        , Manifest.permission.READ_EXTERNAL_STORAGE},
                                requestId);
                    } else {
                        choosePhotoAlbum(context,requestId);
                        dialog.dismiss();
                    }
                } else {
                    choosePhotoAlbum(context,requestId);
                    dialog.dismiss();
                }

            }
        });
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        dialog.show();
    }

    /**
     * 调用相机
     */
    public static  void chooseCamera(Activity context, Uri pictureUri,int requestId) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss",
                    Locale.CHINA);
            // Standard Intent action that can be sent to have the camera
            // application capture an image and return it.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ContentValues attrs = new ContentValues();
            attrs.put(MediaStore.Images.Media.DISPLAY_NAME,
                    dateFormat.format(new Date(System.currentTimeMillis())));// 添加照片名字
            attrs.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");// 图片类型
            attrs.put(MediaStore.Images.Media.DESCRIPTION, "");// 图片描述
            // //插入图片 成功则返回图片所对应的URI 当然我们可以自己指定图片的位置
            uri = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, attrs);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);// 指定照片的位置
            context.startActivityForResult(takePictureIntent, requestId);
        } else {
            Toast.makeText(context, "请确认插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 相册
     */
    public static void choosePhotoAlbum(Activity activity,int requestId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, requestId);
    }



}
