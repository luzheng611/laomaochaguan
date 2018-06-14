package com.laomachaguan.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/11/1.
 */
public  class PreferenceUtil {

    /**
     * 用户数据缓存文件
     * @param c
     * @return
     */
    public static SharedPreferences getUserIncetance(Context c){
        return  c.getSharedPreferences("user",Context.MODE_PRIVATE);
    }
    /**
     * 用户数据缓存文件
     * @param c
     * @return
     */
    public static String getUserId(Context c){
        return  c.getSharedPreferences("user",Context.MODE_PRIVATE).getString("user_id","");
    }
    /**
     * 用户设置缓存文件
     * @param c
     * @return
     */
    public static SharedPreferences getSettingIncetance(Context c){
        return  c.getSharedPreferences("setting",Context.MODE_PRIVATE);
    }
    public static void setChina(Context c,boolean flag){
        getSettingIncetance(c).edit().putBoolean("isChina",flag).apply();
    }
    /**
     * 用户设置缓存文件
     * @param c
     * @return
     */
    public static SharedPreferences getYaoYueIncetance(Context c){
        return  c.getSharedPreferences("YaoYue",Context.MODE_PRIVATE);
    }

}
