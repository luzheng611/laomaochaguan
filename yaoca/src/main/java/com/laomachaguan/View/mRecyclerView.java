package com.laomachaguan.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/4/26.
 */

public class mRecyclerView extends RecyclerView{


    public mRecyclerView(Context context) {
        super(context);
    }

    public mRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public mRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);

    }
}
