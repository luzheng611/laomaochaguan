package com.laomachaguan.Utils;

/**
 * Created by Administrator on 2016/12/17.
 */
public class NumUtils {

    public static String getNumStr(String num) {
        if (num == null || num.equals("")) {
            return "0";
        }
        double n = Double.valueOf(num);
        if (n < 10000) {
            return num;
        } else {
            if(n>=100000000){
                double d=Double.valueOf(num)/100000000;
                return String .format("%.2f",d)+"亿";
            }else{
                double d = Double.valueOf(num) / 10000;
                return String.format("%.2f", d)+"万";
            }

        }



    }
}
