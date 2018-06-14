package com.laomachaguan.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bumptech.glide.Glide;
import com.laomachaguan.Common.Login;
import com.laomachaguan.Fragment.MineManager;
import com.laomachaguan.R;


/**
 * Created by Administrator on 2016/7/14.
 */
public class LoginUtil {

    public static boolean checkLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        if (sp.getString("user_id", "").equals("") || sp.getString("uid", "").equals("")) {
            Intent intent = new Intent(context, Login.class);
            context.startActivity(intent);
            Toast.makeText(context, "请登录", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static void exitLogin(Activity activity, ImageView headView) {
        PushServiceFactory.getCloudPushService().removeAlias(PreferenceUtil.getUserIncetance(activity).getString("user_id","")
                , new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        LogUtil.e("别名解除成功");
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        LogUtil.e("别名解除失败"+s1);
                    }
                });
        activity.getSharedPreferences("address", Context.MODE_PRIVATE).edit().clear().apply();
        SharedPreferences.Editor ed = PreferenceUtil.getUserIncetance(activity).edit();
        ed.putString("uid", "");
        ed.putString("user_id", "");
        ed.putString("head_path", "");
        ed.putString("head_url", "");
        ed.putString("phone", "");
        ed.putString("sex", "");
        ed.putString("promo", "");
        ed.putString("role", "1");
        ed.putString("pet_name", "");
        ed.putString("signature", "");
        ACache aCache = ACache.get(mApplication.getInstance());
        aCache.remove("head_" + PreferenceUtil.getUserIncetance(activity).getString("user_id", ""));
        ed.commit();
        MineManager.clear(mApplication.getInstance());
        Glide.with(activity).load(R.drawable.default_head).into(headView);
        Intent intent = new Intent();
        intent.setClass(mApplication.getInstance(), Login.class);
        activity.startActivity(intent);
        Intent intent1 = new Intent("Mine");
        activity.sendBroadcast(intent1);
        Intent intent2 = new Intent("car");
        activity.sendBroadcast(intent2);
        Intent intent3 = new Intent("yaoyue");
        activity.sendBroadcast(intent3);

//       getActivity().finish();
    }


}
