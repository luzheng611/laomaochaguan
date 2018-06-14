package com.laomachaguan.Utils;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

/**
 * 作者：因陀罗网 on 2017/5/22 15:56
 * 公司：成都因陀罗网络科技有限公司
 */

public class TintUtil {

    public static void tintDrawableView(ImageView img, int imgRes, int colId) {
        Drawable drawable = ContextCompat.getDrawable(mApplication.getInstance(), imgRes);
        Drawable.ConstantState state = drawable.getConstantState();
        Drawable drawable1 = DrawableCompat.wrap(state == null ? drawable : state.newDrawable().mutate());
        drawable1.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable, ContextCompat.getColor(mApplication.getInstance(), colId));
        img.setImageDrawable(drawable);
    }

    public static Drawable tintDrawable(int imgRes,int corId,int dpValue){
        Drawable drawable = ContextCompat.getDrawable(mApplication.getInstance(), imgRes);
        drawable=DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(mApplication.getInstance(), corId));
        LogUtil.e("渲染后的图像:"+drawable);
        drawable.setBounds(0, 0, DimenUtils.dip2px(mApplication.getInstance(), dpValue), DimenUtils.dip2px(mApplication.getInstance(), dpValue));
        return drawable;
    }

}
