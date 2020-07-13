package com.qukan.qkrecordupload.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import lombok.Setter;

/**
 * Created by Administrator on 2018/1/26 0026.
 */

public class CustomViewPager extends ViewPager {

    //是否可以进行滑动
    @Setter
    public boolean isSlide = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSlide;
    }
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return isSlide;
    }

}
