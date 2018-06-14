package com.demo;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 作者：因陀罗网 on 2017/6/20 01:02
 * 公司：成都因陀罗网络科技有限公司
 */

public class FirstTransFormer implements ViewPager.PageTransformer {
    private float baseRotate=90.0f;
    private static final int ThreeD=1;
    private static  final int Transation=2;
    private int type=1;
    public FirstTransFormer(int type) {
        super();
        this.type=type;
    }

    @Override
    public void transformPage(View page, float position) {
        switch (type){
            case 1:
                if(position<=0){
                    ViewCompat.setPivotX(page,page.getMeasuredWidth());
                    ViewCompat.setPivotY(page,page.getMeasuredHeight()*0.5f);
                    ViewCompat.setRotationY(page,baseRotate*position);
                }else if(position<-1){
                    ViewCompat.setPivotX(page,page.getMeasuredWidth());
                    ViewCompat.setPivotY(page,page.getMeasuredHeight()*0.5f);
                    ViewCompat.setRotationY(page,0);
                }else if(position<=1){
                    ViewCompat.setPivotX(page,0);
                    ViewCompat.setPivotY(page,page.getMeasuredHeight()*0.5f);
                    ViewCompat.setRotationY(page,baseRotate*position);
                }else{
                    ViewCompat.setPivotX(page,page.getMeasuredWidth());
                    ViewCompat.setPivotY(page,page.getMeasuredHeight()*0.5f);
                    ViewCompat.setRotationY(page,0);
                }
                break;
            case 2:
                if(position<=0){
                    ViewCompat.setTranslationX(page,page.getMeasuredWidth()*position);
                }else if(position<=1){
                    ViewCompat.setTranslationX(page,page.getMeasuredWidth()*position);
                }else if(position<-1){
                    ViewCompat.setTranslationX(page,page.getMeasuredWidth()*position);
                }else{
                    ViewCompat.setTranslationX(page,page.getMeasuredWidth()*position);
                }
                break;
        }


    }
}
