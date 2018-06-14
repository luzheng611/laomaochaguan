package com.laomachaguan.View;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.laomachaguan.R;
import com.laomachaguan.Utils.DimenUtils;


/**
 * Created by Administrator on 2017/4/17.
 */

public class mProgressDialog extends ProgressDialog {
    private ImageView progress;
    private TextView Msg;
    public mProgressDialog(Context context) {
        super(context);
        init();
    }

    public mProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Toast.makeText(getContext(), "初始化", Toast.LENGTH_SHORT).show();

    }

    /*
        初始化
         */
    private void init() {
        setContentView(R.layout.custom_progress_dialog);
        progress= (ImageView)findViewById(R.id.progress);
        Msg= (TextView)findViewById(R.id.msg);
        RotateAnimation rotate=new RotateAnimation(0,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(-1);
        rotate.setInterpolator(new LinearInterpolator());
        progress.startAnimation(rotate);
        WindowManager.LayoutParams wl=getWindow().getAttributes();
        wl.width= DimenUtils.dip2px(getContext(),120);
        wl.height=WindowManager.LayoutParams.WRAP_CONTENT;
        wl.dimAmount=0.1f;
        getWindow().setAttributes(wl);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setCancelable(true);
    }
    /*
    设置内容
     */
    public void setMsg(final String s){
        Msg.setText(s);
    }



    @Override
    public View onCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);

    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }
}
