package com.demo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 作者：因陀罗网 on 2017/6/20 00:27
 * 公司：成都因陀罗网络科技有限公司
 */

public class PhotoViewPager extends ViewPager {

        public PhotoViewPager(Context context) {
            super(context);
        }

        public PhotoViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        }

}
