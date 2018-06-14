package com.laomachaguan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.laomachaguan.Utils.DimenUtils;

/**
 * Created by Administrator on 2017/4/26.
 */

public class mWuLiuDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private Paint paint;
    private Paint leftPaint;

    public mWuLiuDecoration(Context context) {
        super();
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.parseColor("#d6d6d6"));
        leftPaint = new Paint();
        leftPaint.setColor(Color.parseColor("#b6b6b6"));
        leftPaint.setStrokeWidth(DimenUtils.dip2px(context, 1));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft() + DimenUtils.dip2px(context, 40);
        int right = parent.getWidth() - parent.getPaddingRight();


        for (int i = 0; i < childCount; i++) {
//            if(i==0){
//                continue;
//            }

            View view = parent.getChildAt(i);
            if(childCount==1&&parent.getChildAt(0) instanceof TextView){
                break;
            }
            float top = view.getBottom();
            float bottom = view.getBottom() + 2;
            paint.setColor(Color.WHITE);
            c.drawRect(parent.getPaddingLeft(), top, left, bottom, paint);
            if (i != childCount - 1) {
                paint.setColor(Color.parseColor("#d6d6d6"));
                c.drawRect(left, top, right, bottom, paint);
            }
            c.drawLine(DimenUtils.dip2px(context, 20.5f), 0, DimenUtils.dip2px(context, 20.5f), view.getBottom() + 2, leftPaint);
        }

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);


    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = 2;
//        outRect.left=DimenUtils.dip2px(context,60);
    }
}
