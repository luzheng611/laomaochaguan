package com.laomachaguan.View;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 作者：因陀罗网 on 2017/6/11 11:48
 * 公司：成都因陀罗网络科技有限公司
 */

public class DefaultItemDeraction extends RecyclerView.ItemDecoration {
    private Paint paint;

    public DefaultItemDeraction() {
        super();
        paint=new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.parseColor("#d6d6d6"));
        paint.setStrokeWidth(1);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom=1;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count=parent.getChildCount();
        int  left=parent.getPaddingLeft();
        int  fright=parent.getWidth()-parent.getPaddingRight();
        for(int i=0;i<count;i++){
            View view=parent.getChildAt(i);
            float bottom=view.getBottom()+1;
            float top=view.getBottom();
            c.drawRect(left,top,fright,bottom,paint);
        }
    }
}
