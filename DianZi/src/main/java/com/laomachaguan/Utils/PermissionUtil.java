package com.laomachaguan.Utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/12/10.
 */
public class PermissionUtil {
    private static  int PERMISSION_REQUEST_CODE=0x00;
    public static boolean checkPermission(Activity context, String[] permissons) {
        boolean flag=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i <permissons.length; i++) {
                int p = ContextCompat.checkSelfPermission(context.getApplicationContext(), permissons[i]);
                if(p!= PackageManager.PERMISSION_GRANTED){
                    context.requestPermissions(permissons,PERMISSION_REQUEST_CODE);
                    flag=false;
                    break;
                }
            }
        }
        if(!flag){
            Toast.makeText(context, "请获取相关权限后再次进行操作", Toast.LENGTH_SHORT).show();
        }
        LogUtil.e("是否拥有权限：：；"+flag);
        return  flag;
    }
}


