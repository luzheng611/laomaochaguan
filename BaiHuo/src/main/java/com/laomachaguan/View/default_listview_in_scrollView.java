package com.laomachaguan.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/12/26.
 */
public class default_listview_in_scrollView extends ListView {

    public default_listview_in_scrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
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


        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
