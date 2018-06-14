package com.laomachaguan.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;


/**
 * Created by Administrator on 2016/6/24.
 */
public class LoadMoreListView2 extends ListView implements OnScrollListener {
    public View footer;
    public boolean isOnmeasure = false;
    private int totalItem;
    private int lastItem;

    public boolean isLoading;
    public boolean isScorlling;
    private OnLoadMore onLoadMore;
    private Context context;
    private LayoutInflater inflater;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        isOnmeasure = true;
        super.onMeasure(widthMeasureSpec,expandSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        isOnmeasure = false;
        super.onLayout(changed, l, t, r, b);
    }

    public LoadMoreListView2(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreListView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressLint("InflateParams")
    private void init(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.load_more_footer, null, false);
        footer.setVisibility(View.GONE);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
        setFooterDividersEnabled(false);
        setHeaderDividersEnabled(false);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastItem = firstVisibleItem + visibleItemCount;
        this.totalItem = totalItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.totalItem == lastItem && scrollState == SCROLL_STATE_IDLE) {
            Log.w("isLoading", isLoading + "");
            if (!isLoading) {
                isLoading = true;
                footer.setVisibility(View.VISIBLE);
                onLoadMore.loadMore();
            }
        }
        if ( scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//            scrollState == OnScrollListener.SCROLL_STATE_FLING ||
            Glide.with(context).pauseRequests();
            isScorlling = true;
        } else {
            Glide.with(context).resumeRequests();
            isScorlling = false;
        }

    }

    public void setLoadMoreListen(OnLoadMore onLoadMore) {
        this.onLoadMore = onLoadMore;
    }

    /**
     * 加载完成调用此方法
     */
    public void onLoadComplete() {
        footer.setVisibility(View.GONE);
        isLoading = false;

    }

    public interface OnLoadMore {
        void loadMore();
    }

}