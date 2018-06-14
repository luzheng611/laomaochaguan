package com.laomachaguan.Model_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.Verification;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;



/**
 * Created by Administrator on 2016/10/7.
 */
public class Enrollment extends AppCompatActivity implements View.OnClickListener {
    private EditText name, sex, phone, zhiye, c_id, address, another_phone;
    private TextView camera, commit;
    private ImageView photo;
    private Uri pictureUri = null;
    private AlertDialog dialog;
    private static final int CHOOSEPICTUE = 2;//相册
    private static final int TAKEPICTURE = 1;//相机
    private static final String TAG = "Enrollment";
    private int screenWidth;
    private File Headfile;
    private int screenHeight;
    private int dp60;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enrollment);
        setTitleLayout();
        initView();
    }

    /**
     * 初始化其他控件
     */
    private void initView() {

        dp60= DimenUtils.dip2px(this,60);
        screenHeight=getResources().getDisplayMetrics().heightPixels;
        screenWidth=getResources().getDisplayMetrics().widthPixels;
        name = (EditText) findViewById(R.id.baoming_name);
        sex = (EditText) findViewById(R.id.baoming_xingbie);
        phone = (EditText) findViewById(R.id.baoming_phone);
        photo= (ImageView) findViewById(R.id.baoming_shenfengzheng_displayImg);
        zhiye = (EditText) findViewById(R.id.baoming_zhiye);
        c_id = (EditText) findViewById(R.id.baoming_shenfengzheng);
        address = (EditText) findViewById(R.id.baoming_address);
        another_phone = (EditText) findViewById(R.id.baoming_another_phone);
        camera = (TextView) findViewById(R.id.baoming_camera);
        commit = (TextView) findViewById(R.id.baoming_commit);
        camera.setOnClickListener(this);
        commit.setOnClickListener(this);
    }

    /**
     * 初始化头部layout
     */
    private void setTitleLayout() {
         StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        ImageView back = (ImageView) findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title_title);
        title.setText("填写报名信息");
        ImageView fenxiang = (ImageView) findViewById(R.id.title_image2);
//        fenxiang.setVisibility(View.VISIBLE);
        fenxiang.setOnClickListener(this);
        fenxiang.setImageResource(R.drawable.fenxiang);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.baoming_camera://获取身份证照片
                choosePic();
                break;
            case R.id.baoming_commit://提交信息

                if(name.getText().toString().equals("")
                        ||sex.getText().toString().equals("")
                        ||phone.getText().toString().equals("")
                        ||zhiye.getText().toString().equals("")
                        ||c_id.getText().toString().equals("")
                        ||address.getText().toString().equals("")
                        ||another_phone.getText().toString().equals("")
                        ||Headfile==null
                        ||Headfile.length()==0){
                    Toast.makeText(Enrollment.this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Verification.isMobileNO(phone.getText().toString())||!Verification.isMobileNO(another_phone.getText().toString())){
                    Toast.makeText(Enrollment.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                v.setEnabled(false);
                final ProgressDialog p=new ProgressDialog(this);
                p.isIndeterminate();
                p.setMessage("正在提交信息");
                p.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String data= OkHttpUtils.post(Constants.Activity_BaoMing_IP)
                                    .params("key", Constants.safeKey)
                                    .params("name",name.getText().toString())
                                    .params("act_id",getIntent().getStringExtra("id"))
                                    .params("sex",(sex.getText().toString().equals("男")?1:2)+"")
                                    .params("phone",phone.getText().toString())
                                    .params("work",zhiye.getText().toString())
                                    .params("cid",c_id.getText().toString())
                                    .params("address",address.getText().toString())
                                    .params("tel",another_phone.getText().toString())
                                    .params("cidimage",Headfile).execute().body().string();
                            if(!TextUtils.isEmpty(data)){
                                final HashMap<String ,String >map= AnalyticalJSON.getHashMap(data);
                                if(map!=null){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if("000".equals(map.get("code"))){
                                                Toast.makeText(Enrollment.this, "信息提交成功", Toast.LENGTH_SHORT).show();
                                                p.dismiss();
                                                finish();
                                            }else if("002".equals(map.get("code"))){
                                                Toast.makeText(Enrollment.this, "身份证照片不存在", Toast.LENGTH_SHORT).show();
                                                v.setEnabled(true);
                                            }else if("003".equals(map.get("code"))){
                                                p.dismiss();
                                                Toast.makeText(Enrollment.this, "您已经报过名了", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                                   
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Enrollment.this, "无法连接服务器，请检查网络", Toast.LENGTH_SHORT).show();
                                            p.dismiss();
                                            v.setEnabled(true);
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.title_back://返回
                finish();
                break;
            case R.id.title_image2://分享

                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSEPICTUE:
                    Bitmap bm = null;
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    pictureUri = data.getData();// 选择照片的Uri 可能为null
                    if (pictureUri != null) {
                        //上传头像
                        String path = ImageUtil.getImageAbsolutePath(mApplication.getInstance(), pictureUri);
                        Log.w(TAG, "onActivityResult:  path++++++++-========" + path);
                        bm = ImageUtil.getImageThumbnail(path, (screenWidth-dp60)*3/5, dp60*2);
//                        bm = ImageUtil.getImageThumbnail(path, screenWidth*2, dp60*4);
                        photo.setImageBitmap(bm);
                        FileOutputStream faos = null;
                        try {
                            faos = new FileOutputStream(path);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (faos != null) {
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, faos);
                                Log.w(TAG, "onActivityResult: size-=-=-=" + bm.getByteCount());
                                faos.flush();
                                Headfile = new File(path);
                                faos.close();
                            } else {
                                Toast.makeText(mApplication.getInstance(), "上传失败,请重新尝试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.w(TAG, "onActivityResult: ___________." + new File(path).length());
                    } else {
                        Toast.makeText(mApplication.getInstance(), "上传失败,请重新尝试", Toast.LENGTH_SHORT).show();
                    }


                    break;
                case TAKEPICTURE:
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    if (pictureUri != null) {
                        //上传头像
                        String path = ImageUtil.getRealPathFromURI(this, pictureUri);
                        bm = ImageUtil.getImageThumbnail(path, (screenWidth-dp60)*3/5, dp60*2);
                        photo.setImageBitmap(bm);
                        FileOutputStream faos = null;
                        try {
                            faos = new FileOutputStream(path);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (faos != null) {
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, faos);
                                faos.flush();
                                Headfile = new File(path);
                                faos.close();
                            } else {
                                Toast.makeText(mApplication.getInstance(), "图片获取失败,请重新尝试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.w(TAG, "onActivityResult: ___________." + new File(path).length());
                    } else {
                        Toast.makeText(mApplication.getInstance(), "图片获取失败,请重新尝试", Toast.LENGTH_SHORT).show();
                    }


                    break;
            }
        }

    }
    //选择照片或照相
    private void choosePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.dialog_album_camera, null);
        view.findViewById(R.id.photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission1 = ContextCompat.checkSelfPermission(mApplication.getInstance(), Manifest.permission.CAMERA);
                    if (permission1 != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, TAKEPICTURE);
                    } else {
                        chooseCamera();
                        dialog.dismiss();
                    }
                } else {
                    chooseCamera();
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
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                                        , Manifest.permission.READ_EXTERNAL_STORAGE},
                                CHOOSEPICTUE);
                    } else {
                        choosePhotoAlbum();
                        dialog.dismiss();
                    }
                } else {
                    choosePhotoAlbum();
                    dialog.dismiss();
                }

            }
        });
        builder.setView(view);
        dialog = builder.create();
        view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setBackgroundDrawableResource(R.color.vifrification);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = getResources().getDisplayMetrics().widthPixels;
        wl.y =getResources().getDisplayMetrics().heightPixels;
        window.setAttributes(wl);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CHOOSEPICTUE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoAlbum();
                } else {
                    Toast.makeText(mApplication.getInstance(), "无相册权限将无法使用该功能", Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKEPICTURE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(mApplication.getInstance(), "无相机权限将无法使用该功能", Toast.LENGTH_SHORT).show();
                }

        }
    }

    /**
     * 调用相机
     */
    private void chooseCamera() {
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
        pictureUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, attrs);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);// 指定照片的位置
        startActivityForResult(takePictureIntent, TAKEPICTURE);

    }

    /**
     * 相册
     */
    private void choosePhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CHOOSEPICTUE);

    }
}
