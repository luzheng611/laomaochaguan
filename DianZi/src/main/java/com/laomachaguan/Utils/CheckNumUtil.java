package com.laomachaguan.Utils;

/**
 * Created by Administrator on 2017/1/3.
 */
public class CheckNumUtil {

    public static  boolean checkNum(String t) {// TODO: 2016/12/17 检测文字正确性
        boolean isTrue=false;
        if (t.startsWith("0")) {
            if (t.lastIndexOf(".") != -1) {
                if (!t.endsWith(".")) {
                    isTrue = true;
                }
            }
        } else {
            if (t.lastIndexOf(".") != -1) {
                if (!t.startsWith(".") && !t.endsWith(".")) {
                    isTrue = true;
                }
            } else {
                isTrue = true;
            }
        }
        return isTrue;
    }


}
