package com.laomachaguan.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

/**
 * Created by Administrator on 2016/10/10.
 */
public class IOUtil {

    /**
     * 存储数据
     *
     * @param mContext
     *            上下文
     * @param tempName
     *            存储名称
     * @param tempList
     *            数据集合
     */
    public static void setData(Context mContext,String prefName, String tempName, List<?> tempList) {
        SharedPreferences sps = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(tempList);
            // 将字节流编码成base64的字符串
            String tempBase64 = new String(com.umeng.socialize.net.utils.Base64.encodeBase64(baos.toByteArray(),false));
            SharedPreferences.Editor editor = sps.edit();
            editor.putString(tempName, tempBase64);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * @param mContext
     * @param tempName
     * @param tempList
     * @return
     * 获取数据
     */
    public static List<?> getData(Context mContext,String prefName, String tempName, List<?> tempList) {
        SharedPreferences sps = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String tempBase64 = sps.getString(tempName, "");// 初值空
//        if (StringUtil.isBlank(tempBase64)) {
//            return tempList;
//        }
        if(TextUtils.isEmpty(tempBase64)){
            return null;
        }
        // 读取字节
        byte[] base64 = com.umeng.socialize.net.utils.Base64.decodeBase64(tempBase64);
        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            // 再次封装
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 读取对象
            tempList = (List<?>) ois.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tempList;

    }
    /**
     * encodeBase64File:(将文件转成base64 字符串). <br/>
     * @param path 文件路径
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer,Base64.DEFAULT);
    }
    /**
     * decoderBase64File:(将base64字符解码保存文件).
     * @param base64Code 编码后的字串
     * @param savePath  文件保存路径
     */
    public static void decoderBase64File(String base64Code,String savePath) throws Exception {
//byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        byte[] buffer =Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }
}
