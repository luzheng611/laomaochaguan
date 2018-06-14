package com.laomachaguan.View;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.laomachaguan.R;
import com.laomachaguan.Utils.DimenUtils;

import java.lang.ref.WeakReference;

/**
 * 作者：因陀罗网 on 2017/7/19 11:17
 * 公司：成都因陀罗网络科技有限公司
 */

public class ShiPuDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private Activity activity;
    private Paint mPaint;

    public ShiPuDecoration(Activity activity, int space, int colorId) {
        super();
        this.space = space;
        WeakReference<Activity> w = new WeakReference<Activity>(activity);
        this.activity = w.get();
        mPaint = new Paint();
        mPaint.setStrokeWidth(DimenUtils.dip2px(this.activity, space));
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(activity, R.color.light_huise));
        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setDither(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        GridLayoutManager linearLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        int childSize = parent.getChildCount();
//        int other = parent.getChildCount() / linearLayoutManager.getSpanCount() - 1;

        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            if(i<=4){
                if(i==0){
                    c.drawRect(child.getLeft()+space,child.getTop()-space/2,child.getRight()+space/2,child.getBottom()+space,mPaint);
                }else{
                    c.drawLine(child.getLeft(),child.getTop()-space,child.getRight()+space,child.getTop()-space,mPaint);
                    c.drawLine(child.getLeft(),child.getBottom()+space,child.getRight()+space,child.getBottom()+space,mPaint);
                    c.drawLine(child.getRight()+space/2,child.getTop()-space,child.getRight()+space/2,child.getBottom()+space,mPaint);
                }
            }else{
                if(i%4==1){
                    c.drawLine(child.getLeft()+space,child.getTop()-space,child.getLeft()+space,child.getBottom()+space,mPaint);
                    c.drawLine(child.getLeft(),child.getBottom()+space,child.getRight()+space,child.getBottom()+space,mPaint);
                    c.drawLine(child.getRight()+space/2,child.getTop()-space,child.getRight()+space/2,child.getBottom()+space,mPaint);
                }else{
                    c.drawLine(child.getLeft(),child.getBottom()+space,child.getRight()+space,child.getBottom()+space,mPaint);
                    c.drawLine(child.getRight()+space/2,child.getTop()-space,child.getRight()+space/2,child.getBottom()+space,mPaint);
                }
            }
//                    int top=child.getBottom()-child.getTop()+space;
//                    int left=child.getLeft()+space;
//                    int bottom=child.getBottom()+space;
//                    int right=child.getRight()+space;
//            Point lt = new Point(child.getLeft() - space, child.getTop());
//            Point rt = new Point(child.getRight(), child.getTop());
//            Point lb = new Point(child.getLeft() - space, child.getBottom() + space);
//            Point rb = new Point(child.getRight() + space, child.getRight());
//            if (childSize < 4) {
//                c.drawLine(lt.x, lt.y, rt.x, rt.y, mPaint);
//            }
//            c.drawLine(lt.x, lt.y, rt.x, rt.y, mPaint);
//            c.drawLine(lb.x, lb.y, rb.x, rb.y, mPaint);

        }


    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            for(int i=0;i<state.getItemCount();i++){
                if(i<=4){
                    outRect.set(0, space, space, space);
                    if(i%4==1){
                        outRect.set(space,space,space,space);
                    }
                }else{
                    outRect.set(0,0,space,space);
                    if(i%4==1){
                        outRect.set(space,0,space,space);
                    }
                }
            }

        }
    }
}
