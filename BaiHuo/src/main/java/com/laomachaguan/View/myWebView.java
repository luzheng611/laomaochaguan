package com.laomachaguan.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by Administrator on 2016/12/6.
 */
public class myWebView extends WebView implements View.OnLongClickListener{
    public onLongClickListener onLongClickListener;
    public  void setOnLongClickListener(onLongClickListener onLongClickListener){
        this.onLongClickListener=onLongClickListener;
    }

    public myWebView(Context context) {
        super(context);
        setOnLongClickListener(this);
    }

    public myWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnLongClickListener(this);
    }

    public myWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        // 长按事件监听（注意：需要实现LongClickCallBack接口并传入对象）
        final HitTestResult htr = getHitTestResult();//获取所点击的内容
        if (htr.getType() == WebView.HitTestResult.IMAGE_TYPE) {//判断被点击的类型为图片
            onLongClickListener.onLongClcik(htr.getExtra());
        }
        return true;
    }

    public interface  onLongClickListener{
        void  onLongClcik(String imgUrl);
    }
}
