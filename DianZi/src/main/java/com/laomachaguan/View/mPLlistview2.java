package com.laomachaguan.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/6/7.
 */
public class mPLlistview2 extends ListView {
    public boolean isOnMeasure=false;
    public TextView footer;
    private static final String TAG = "mPLlistview";
    public mPLlistview2(Context context) {
        super(context);
        init(context);
    }

    public mPLlistview2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public mPLlistview2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public void init(Context context){
        setFooterDividersEnabled(false);
        footer=new TextView(context);
        footer.setText("点击加载更多");
        LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        footer.setLayoutParams(lp);
        footer.setTextSize(14);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(20,20,20,20);
        this.addFooterView(footer);
    }
    /**
     * 解决listView和ScrollView嵌套滑动冲突
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        isOnMeasure=true;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        isOnMeasure=false;

        super.onLayout(changed, l, t, r, b);
    }
}
