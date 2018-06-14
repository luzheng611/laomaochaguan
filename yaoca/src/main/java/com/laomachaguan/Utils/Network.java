package com.laomachaguan.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {
    public static boolean HttpTest(Context context) {
        boolean http = true;
        if (context!=null) {
            ConnectivityManager con = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = con.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isAvailable()) {
                // 无网络
                ToastUtil.showToastShort("请检查网络");
                http = false;
            }
            boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();
            if (!wifi) {
                // WIFI 不可用
            }
        }
        return http;
    }

    public static boolean is3g2g(Context context) {
        boolean http = false;
        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = con.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            // 无网络
        } else {
            boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();
            if (!wifi) {
                // WIFI 不可用
                http = true;
            }
        }
        return http;
    }
}