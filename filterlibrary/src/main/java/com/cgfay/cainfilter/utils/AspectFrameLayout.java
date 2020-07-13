package com.cgfay.cainfilter.utils;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by cain.huang on 2017/7/20.
 */

public class AspectFrameLayout extends FrameLayout {


    //横屏还是竖屏 竖1横2
    public int oritation = 1;
    // 宽高比
    private double mTargetAspect = -1.0;

    public AspectFrameLayout(@NonNull Context context) {
        super(context);
    }

    public AspectFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(double aspectRatio) {
        if (aspectRatio < 0) {
            throw  new IllegalArgumentException("ratio < 0");
        }
        if (mTargetAspect != aspectRatio) {
            mTargetAspect = aspectRatio;
            requestLayout();
        }
    }

    public void setOritation(int oritation){
        this.oritation = oritation;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mTargetAspect > 0) {

            int initialWidth = MeasureSpec.getSize(widthMeasureSpec);
            int initialHeight = MeasureSpec.getSize(heightMeasureSpec);

            int horizPadding = getPaddingLeft() + getPaddingRight();
            int vertPadding = getPaddingTop() + getPaddingBottom();

            initialWidth -= horizPadding;
            initialHeight -= vertPadding;

            double viewAspectRatio = (double) initialWidth / initialHeight;
            double aspectDiff = mTargetAspect / viewAspectRatio - 1;

            if (Math.abs(aspectDiff) >= 0.01) {
                if (oritation == 1){
                    initialHeight = (int)(initialWidth / mTargetAspect);
                } else if (oritation == 2){
                    initialWidth = (int)(initialHeight / mTargetAspect);
                }
            }
            initialWidth += horizPadding;
            initialHeight += vertPadding;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(initialWidth, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
