package com.laomachaguan.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by Administrator on 2016/12/5.
 */
public class QrUtils {
    private static final String TAG = "QrUtils";
    //这种方法状态栏是空白，显示不了状态栏的信息
    public static void saveCurrentImage(final Activity activity, final File file) {
        //获取当前屏幕的大小
        int width = activity.getWindow().getDecorView().getRootView().getWidth();
        int height = activity.getWindow().getDecorView().getRootView().getHeight();
        //生成相同大小的图片
        Bitmap temBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //找到当前页面的根布局
        View view = activity.getWindow().getDecorView().getRootView();
        //设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片,创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        temBitmap = view.getDrawingCache();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                temBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Log.w(TAG, "onLongClick: "+file.getAbsolutePath() );
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Result result = parseQRcodeBitmap(file.getAbsolutePath(),600);
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Log.w(TAG, "run: 扫描二位码后的结果："+result );
                            if (null != result&&result.getText().toString().startsWith("http")) {
                                Uri uri = Uri.parse(result.getText());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                activity.startActivity(intent);
                            } else {
                                Toast.makeText(activity, "无法识别,请确认当前页面是否有二维码图片", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).start();
            //禁用DrawingCahce否则会影响性能 ,而且不禁止会导致每次截图到保存的是第一次截图缓存的位图
            view.setDrawingCacheEnabled(false);
        }
    }

    //解析二维码图片,返回结果封装在Result对象中
    public static com.google.zxing.Result parseQRcodeBitmap(String bitmapPath,int width) {
        //解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //获取到待解析的图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        //如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path, Options opt)
        //并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
        options.inJustDecodeBounds = true;
        //此时的bitmap是null，这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        //我们现在想取出来的图片的边长（二维码图片是正方形的）设置为400像素
        //以上这种做法，虽然把bitmap限定到了我们要的大小，但是并没有节约内存，如果要节约内存，我们还需要使用inSimpleSize这个属性
        options.inSampleSize = options.outHeight / width;
        if (options.inSampleSize <= 0) {
            options.inSampleSize = 1; //防止其值小于或等于0
        }
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        Log.w(TAG, "parseQRcodeBitmap: 从截图读取的图片"+bitmap );
        //新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        BitmapLuminanceSource rgbLuminanceSource = new BitmapLuminanceSource(bitmap);
        //将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        //初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        //开始解析
        Result result = null;
        try {
            Log.w(TAG, "parseQRcodeBitmap: 二进制图片——："+binaryBitmap.toString()+"   解析转换类型："+hints );
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
        }
        return result;
    }

    public static Result handleQRCodeFormBitmap(Bitmap bitmap) {
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType,String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

        BitmapLuminanceSource rgbluminanceSouce=new BitmapLuminanceSource(bitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(rgbluminanceSouce));
        QRCodeReader reader2= new QRCodeReader();
        Log.w(TAG, "handleQRCodeFormBitmap: 图片"+bitmap+  "  bitmap1    "+"   不知名对象 "+rgbluminanceSouce);
        Result result = null;
        try {
            try {
                result = reader2.decode(bitmap1,hints);
            } catch (ChecksumException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
