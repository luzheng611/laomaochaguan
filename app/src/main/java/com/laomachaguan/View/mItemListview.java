package com.laomachaguan.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/1/17.
 */
public class mItemListview extends ListView {
    public boolean isOnMeasure=false;
    public mItemListview(Context context) {
        super(context);

    }

    public mItemListview(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    public mItemListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }


    /**
     * 解决listView和ScrollView嵌套滑动冲突
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        isOnMeasure=true;
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        isOnMeasure=false;
        super.onLayout(changed, l, t, r, b);
    }
}
