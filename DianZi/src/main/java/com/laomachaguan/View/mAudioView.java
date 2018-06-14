package com.laomachaguan.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.R;
import com.laomachaguan.Utils.ImageUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/11/6.
 */
public class mAudioView extends FrameLayout {
    private ImageView mImage;
    private ImageView mAnim;
    private TextView mTime;
    private Context context;
    private boolean isPlaying = false;
    private int totalTime = -1,tempTime=-1;
    private onImageClickListener onImageClickListener;
    private Timer timer;
    private int ANIM = 0;

    public boolean isPlaying() {
        // TODO: 2016/11/7 是否正在播放
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        // TODO: 2016/11/7 设置播放状态
        this.isPlaying = isPlaying;
    }

    public mAudioView(Context context) {
        this(context, null);
    }

    public interface onImageClickListener {
        void onImageClick(mAudioView v);
    }

    public void setOnImageClickListener(onImageClickListener listener) {
        this.onImageClickListener = listener;
    }

    public void beginAnim() {
        // TODO: 2016/11/7 开始播放动画
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isPlaying=true;
                mImage.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (ANIM % 3) {
                            case 0:
                                mAnim.setImageBitmap(ImageUtil.readBitMap(context, R.drawable.sound1));
                                break;
                            case 1:
                                mAnim.setImageBitmap(ImageUtil.readBitMap(context, R.drawable.sound2));
                                break;
                            case 2:
                                mAnim.setImageBitmap(ImageUtil.readBitMap(context, R.drawable.sound3));
                                break;
                        }
                        if (tempTime > 0) {
                            tempTime--;
                            mTime.setText(tempTime + "'");
                        } else {
                            resetAnim();
                        }
                        ANIM++;
                        invalidate();
                    }
                });
            }
        }, 0, 1000);
    }

    public void resetAnim() {
        isPlaying=false;
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        ANIM=0;
        tempTime=totalTime;
        if (totalTime> 0) {
            mTime.setText( totalTime+ "'");
        }
        mAnim.setImageBitmap(ImageUtil.readBitMap(context, R.drawable.sound3));
    }

    public mAudioView(Context context, AttributeSet attrs) {//布局加载时使用的构造方法
        super(context, attrs);
        this.context = context;
//        setBackgroundColor(Color.WHITE);

        View view=null;
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.mAudioView);
        if(a.getBoolean(R.styleable.mAudioView_m_gravity,true)){
            view = LayoutInflater.from(context).inflate(R.layout.audio_coustom, this, true);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.audio_coustom_180, this, true);
        };
        mImage = (ImageView) view.findViewById(R.id.audio_image);
        mTime = (TextView) view.findViewById(R.id.audio_time);
        mAnim = (ImageView) view.findViewById(R.id.audio_anim);
        mAnim.setImageBitmap(ImageUtil.readBitMap(context,R.drawable.sound3));
        a.recycle();
//        mTime.setText(a.getText(R.styleable.mAudioView_timeText).equals("")?"0'":a.getText(R.styleable.mAudioView_timeText));
//        mTime.setTextColor(a.getColor(R.styleable.mAudioView_timeTextColor,Color.GRAY));
//        mTime.setTextSize(a.getDimensionPixelSize(R.styleable.mAudioView_timeTextSize,14));
//        mAnim.setColorFilter(a.getColor(R.styleable.mAudioView_animImageColor,Color.WHITE));
//        mImage.setColorFilter(a.getColor(R.styleable.mAudioView_imageColor, ContextCompat.getColor(context,R.color.main_color)));
    }

    public void setTime(int i) {
        totalTime = i;
        tempTime=i;
        if(i!=0){
            mTime.setText(i + "'");
        }else{
            mTime.setText("");
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                onImageClickListener.onImageClick(this);
//                if (!isPlaying) {
//                    beginAnim();
//                } else {
//                    resetAnim();
//                }

                break;
        }
        return true;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getMode(widthMeasureSpec);
        int w1 = MeasureSpec.getSize(widthMeasureSpec);
        w1 = dip2px(context, 240);
        super.onMeasure(MeasureSpec.makeMeasureSpec(w1, w), heightMeasureSpec);
//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
