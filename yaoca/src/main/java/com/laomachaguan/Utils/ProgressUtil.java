package com.laomachaguan.Utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.laomachaguan.View.mProgressDialog;


/**
 * Created by Administrator on 2016/11/18.
 */
public class ProgressUtil {
    private static final String TAG = "ProgressUtil";
    private static ProgressDialog progressDialog;

    public static void show(Context context, String title, String msg) {
            if(title!=null&&!title.equals("")){
                progressDialog = ProgressDialog.show(context, title, msg);
                progressDialog.setCancelable(true);
                progressDialog.getWindow().setDimAmount(0.1f);
            }else{
                progressDialog=new mProgressDialog(context);
                ((mProgressDialog) progressDialog).setMsg(msg);
                progressDialog.show();
            }





    }
    public static void show(Context context, String title, String msg,float dimamount) {
        if(title!=null&&!title.equals("")){
            progressDialog = ProgressDialog.show(context, title, msg);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setDimAmount(dimamount);
        }else{
            progressDialog=new mProgressDialog(context);
            ((mProgressDialog) progressDialog).setMsg(msg);
            progressDialog.getWindow().setDimAmount(dimamount);
            progressDialog.show();
        }





    }

    public static void canCancelAble(boolean flag) {
        if (progressDialog != null) {
            progressDialog.setCancelable(flag);
        }
    }

    public static void canCancelAbleOutSide(boolean flag) {
        if (progressDialog != null) {
            progressDialog.setCanceledOnTouchOutside(flag);
        }
    }

    public static void dismiss() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog=null;
    }
}
