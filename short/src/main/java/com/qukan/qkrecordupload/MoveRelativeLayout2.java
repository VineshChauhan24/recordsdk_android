package com.qukan.qkrecordupload;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/3/1 0001.
 * 用于视频字幕编辑界面的移动布局，修复了原来布局内控件增删时控件复位的问题
 */
public class MoveRelativeLayout2 extends RelativeLayout {
    @Setter
    int parentWidth, parentHeight;// 可移动的区域

    public MoveRelativeLayout2(Context context) {
        super(context);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        parentWidth = dm.widthPixels;
        parentHeight = dm.heightPixels;
    }

    public MoveRelativeLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        parentWidth = dm.widthPixels;
        parentHeight = dm.heightPixels;
    }

    public MoveRelativeLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        parentWidth = dm.widthPixels;
        parentHeight = dm.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Getter
    long timeEventStart = 0, timeEventEnd = 0;
    /**
     * 按下时的位置控件相对屏幕左上角的位置X
     */
    private int startDownX;
    /**
     * 按下时的位置控件距离屏幕左上角的位置Y
     */
    private int startDownY;
    /**
     * 控件相对屏幕左上角移动的位置X
     */
    private int lastMoveX;
    /**
     * 控件相对屏幕左上角移动的位置Y
     */
    private int lastMoveY;

    //拦截touch事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        View v = this;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                timeEventStart = System.currentTimeMillis();
                startDownX = lastMoveX = (int) event.getRawX();
                startDownY = lastMoveY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (listener != null) {
                    listener.onMoving();
                }
                int dx = (int) event.getRawX() - lastMoveX;
                int dy = (int) event.getRawY() - lastMoveY;

                int left = v.getLeft() + dx;
                int top = v.getTop() + dy;
                int right = v.getRight() + dx;
                int bottom = v.getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }
                if (right > parentWidth) {
                    right = parentWidth;
                    left = right - v.getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + v.getHeight();
                }
                if (bottom > parentHeight) {
                    bottom = parentHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
                lastMoveX = (int) event.getRawX();
                lastMoveY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                // 每次移动都要设置其layout，不然父布局刷新时移动的view会回到原来的位置
                RelativeLayout.LayoutParams lpFeedback = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lpFeedback.leftMargin = v.getLeft();
                lpFeedback.topMargin = v.getTop();
                lpFeedback.setMargins(v.getLeft(), v.getTop(), 0, 0);
                v.setLayoutParams(lpFeedback);
                timeEventEnd = System.currentTimeMillis();
                if (listener != null) {
                    listener.onMoveOver(v.getLeft() / (float) parentWidth, v.getTop() / (float) parentHeight, (parentWidth - v.getRight()) / (float) parentWidth, (parentHeight - v.getBottom()) / (float) parentHeight);
                }
                break;
        }
        return false;
    }

    @Setter
    MoveOverListener listener;

    public interface MoveOverListener {
        void onMoveOver(float mLeft, float mTop, float mRight, float mBottom);

        void onMoving();
    }

}
