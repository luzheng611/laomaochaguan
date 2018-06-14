package com.laomachaguan.Common;

import android.app.Activity;
import android.content.Intent;

import com.laomachaguan.Utils.mApplication;

/**
 * 作者：因陀罗网 on 2017/8/7 14:30
 * 公司：成都因陀罗网络科技有限公司
 */

public class HeadToInfo {

    public static void goToUserDetail(Activity activity,String user_id){
        Intent intent=new Intent(mApplication.getInstance(),User_Detail2.class);
        intent.putExtra("id",user_id);
//        intent.putExtra("self",isSelf?User_Detail2.IS_SELF:User_Detail2.NO_SELF);
        activity.startActivity(intent);
    }
}
