package com.laomachaguan.TouGao;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/10/20.
 */
public class mGridView extends GridView {
    public mGridView(Context context) {
        super(context);
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

    public mGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public mGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
