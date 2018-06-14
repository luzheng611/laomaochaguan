package com.laomachaguan.Utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import java.lang.ref.WeakReference;


/**
 * 作者：因陀罗网 on 2017/6/19 16:33
 * 公司：成都因陀罗网络科技有限公司
 */

public class PointMoveHelper {
    private static final String TAG = "PointMoveHelper";
    private View mView; //需要滑动的View
    private float startX, startY;
    private long downTime;
    private Activity activity;
    private ValueAnimator va;
    private int screenWidth ;
    private ViewGroup unMoveableView;
    private int horizontalMargin=0;//dp
    public PointMoveHelper(Activity activity,View view) {
        super();
        WeakReference<Activity> a=new WeakReference<Activity>(activity);
        this.activity=a.get();
        mView=view;
        init();
    }
    public PointMoveHelper setViewUnMoveable(ViewGroup  unMoveableView){
        this.unMoveableView=unMoveableView;//// TODO: 2016/12/12 禁止拦截事件 ,由子控件处理事件
        return this;
    }
    public PointMoveHelper setHorizontalMargin(int margin){
        horizontalMargin=margin;
        return this;
    }
    private void init() {

        screenWidth=activity.getResources().getDisplayMetrics().widthPixels;
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setSelected(true);
                        downTime = System.currentTimeMillis();
                        startX = event.getRawX() - v.getX();
                        startY = event.getRawY() - v.getY();
                        if(unMoveableView!=null){
                            unMoveableView.requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveFllow(v, x, y, startX, startY, (View) mView.getParent());//// TODO: 2016/12/12 跟随手指滑动
                        break;
                    case MotionEvent.ACTION_UP:
                        if(unMoveableView!=null){
                            unMoveableView.requestDisallowInterceptTouchEvent(false);
                        }
                        checkHozatal(v);//// TODO: 2016/12/12判断左右边距
                        if (System.currentTimeMillis() - downTime <= 300) {
                            v.performClick();
                        }
                        break;
                }
                return true;
            }
        });
    }


    private void checkHozatal(final View v) {
        int left = (int) v.getX();
        Log.w(TAG, "onTouch: " + left);
        va = ValueAnimator.ofInt(60);
        va.setDuration(500);
        va.setInterpolator(new BounceInterpolator());
        if (left <= screenWidth / 2) {
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX(v.getX() - ((int) animation.getAnimatedValue()));
                    if (v.getX() <= DimenUtils.dip2px(activity, horizontalMargin)) {
                        v.setX(DimenUtils.dip2px(activity, horizontalMargin));
                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        } else {
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.setX(v.getX() + ((int) animation.getAnimatedValue()));
                    if (v.getX() >= screenWidth - DimenUtils.dip2px(activity,horizontalMargin) - v.getWidth()) {
                        v.setX(screenWidth - DimenUtils.dip2px(activity, horizontalMargin) - v.getWidth());
                        v.setSelected(false);
                        va.cancel();
                    }
                }
            });
        }
        va.start();
    }

    private void moveFllow(View v, int x, int y, float startX, float startY, View view) {
        if (v.getY() >= view.getY() + DimenUtils.dip2px(activity, 60) && v.getY() <= view.getY() + view.getHeight() - DimenUtils.dip2px(activity, 10) - v.getHeight()) {
            v.setY(y - startY);
            if (v.getY() < view.getY() + DimenUtils.dip2px(activity, 60)) {
                v.setY(view.getY() + DimenUtils.dip2px(activity, 60));
            } else if (v.getY() > view.getY() + view.getHeight() - DimenUtils.dip2px(activity, 10) - v.getHeight()) {
                v.setY(view.getY() + view.getHeight() - DimenUtils.dip2px(activity, 10) - v.getHeight());
            }
        }

        if (v.getX() >= DimenUtils.dip2px(activity, 10) && v.getX() <= (screenWidth - DimenUtils.dip2px(activity, 10) - v.getWidth())) {
            v.setX(x - startX);
            if (v.getX() < DimenUtils.dip2px(activity, 10)) {
                v.setX(DimenUtils.dip2px(activity, 10));
            } else if (v.getX() > (screenWidth - DimenUtils.dip2px(activity, 10) - v.getWidth())) {
                v.setX((screenWidth - DimenUtils.dip2px(activity, 10) - v.getWidth()));
            }
        }
    }

}
