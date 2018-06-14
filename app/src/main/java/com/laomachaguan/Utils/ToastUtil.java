package com.laomachaguan.Utils;

import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/3/7.
 */

public class ToastUtil {

    public static Toast toast;
    public static void showToastShort(String msg){
        if(toast==null){
            toast=Toast.makeText(mApplication.getInstance(),"",Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    public static void showToastShort(String msg,int gravity){
        if(toast==null){
            toast=Toast.makeText(mApplication.getInstance(),"",Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(gravity,0,0);
        toast.show();

    }
    public static void showToastLong(String msg,int gravity){
        if(toast==null){
            toast=Toast.makeText(mApplication.getInstance(),"",Toast.LENGTH_LONG);
        }
        toast.setText(msg);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(gravity,0,0);
        toast.show();
    }
}
