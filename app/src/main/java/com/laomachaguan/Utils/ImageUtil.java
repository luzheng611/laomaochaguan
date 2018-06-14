package com.laomachaguan.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtil {
    public  static final  int mHeight=1920;
    public static  final int mWidth=1080;

    /**
     * 2.  * 以最省内存的方式读取本地资源的图片
     * 3.  * @param context
     * 4.  * @param resId
     * 5.  * @return
     * 6.  6Mb降低到1.4Mb  6/1.4~~4   时间平均缩短20ms   但生成的bitmap不能用于构建convas
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;//使用索引位图  indexed bitmap  每像素1字节  是正常argb-8888的4分之1
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap getImageThumbnail(String imagePath, int width,
                                           int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        bitmap = BitmapFactory.decodeFile(imagePath, options);

        options.inJustDecodeBounds = false;

        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(imagePath, options);


//        try {
//            bitmap=BitmapFactory.decodeStream(new FileInputStream(new File(imagePath)),null,options);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static void compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;//个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap scaleToScreen(Activity context, Uri uri) {
        Bitmap bitmap = null;
        Point screenSize = new Point();

        context.getWindowManager().getDefaultDisplay().getSize(screenSize);
        final int width = screenSize.x;
        final int height = screenSize.y - 120;
        try {
            FileInputStream fis = new FileInputStream(getRealPathFromURI(context, uri));
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeStream(fis, null, opts);
            if (opts.outWidth / width >= 2) {
                opts.inSampleSize = opts.outWidth / width;
            }
            opts.inJustDecodeBounds = false;
            fis.close();
            fis = new FileInputStream(getRealPathFromURI(context, uri));

            bitmap = BitmapFactory.decodeStream(fis, null, opts);

//
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static String getRealPathFromURI(Activity context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }


    public static byte[] Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 90, bos);
        byte[] bytes = bos.toByteArray();

        return bytes;
//        "data:image/jpeg;base64,"+(Base64.encodeToString(bytes, Base64.DEFAULT))
    }

    /**
     * ���Uri��ȡͼƬ���·�������Android4.4���ϰ汾Uriת��
     */
    @TargetApi(22)
    public static String getImageAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public void showImageByThread(final ImageView imageView, final String picurl) {
        mImage = imageView;
        murl = picurl;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getImageFromUrl(picurl);
                Message ms = Message.obtain();
                ms.obj = bitmap;
                handler.sendMessage(ms);
            }
        }.start();

    }

    private ImageView mImage;
    private String murl;
    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImage.getTag().equals(murl)) {
                mImage.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public Bitmap getImageFromUrl(final String url) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            URL murl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) murl.openConnection();
            httpURLConnection.setUseCaches(false);
            is = new BufferedInputStream(httpURLConnection.getInputStream());

            bitmap = BitmapFactory.decodeStream(is);
            httpURLConnection.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public void showImageByAsynTask(LruCache<String, Bitmap> lruCache, ImageView imageView, String url) {

        new mAsynTask(lruCache, imageView, url).execute(url);
    }

    public class mAsynTask extends AsyncTask<String, Void, Bitmap> {

        String url;
        ImageView imageView;
        LruCache<String, Bitmap> lruCache;

        public mAsynTask(LruCache<String, Bitmap> lruCache, ImageView imageView, String url) {
            this.url = url;
            this.imageView = imageView;
            this.lruCache = lruCache;

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getImageFromUrl(params[0]);
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            int pheight = imageView.getHeight();
            int pwidth = imageView.getWidth();
            if (pheight > 0 && pwidth > 0) {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, pheight, pwidth);
                Log.v("this", bitmap.getHeight() + "  " + bitmap.getWidth() + "");
            }
            lruCache.put(url, bitmap);

            if (imageView.getTag() != null && imageView.getTag().equals(url)) {
                imageView.setImageBitmap(bitmap);
            }

        }
    }
}