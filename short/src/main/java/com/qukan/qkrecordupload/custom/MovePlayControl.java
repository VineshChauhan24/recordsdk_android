package com.qukan.qkrecordupload.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import lombok.Setter;

/**
 * Created by Administrator on 2018/1/3 0003.
 * 使用前要先设置mWidth滑块自身宽，parentWidth父布局的宽(这个是指在滑块下面的这张图片的宽，用于计算滑块中心位置在其中的百分比)和leftBound 移动的左边界
 */

public class MovePlayControl extends ImageView {

    private int lastX;

    // 滑块图片的宽度，直接getwidth会在移动时出现变化
    @Setter
    private int mWidth;

    // 滑块下面的这张图片的宽
    @Setter
    public int parentWidth;

    // 设置左边界，字幕编辑左边滑动不能超过起始点--百分比
    @Setter
    public float leftBound;

    private final int mixDive = 10;

    public MovePlayControl(Context context) {
        super(context);
        init();
    }

    public MovePlayControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovePlayControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取当前触摸的绝对坐标
        int rawX = (int) event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 上一次离开时的坐标
                lastX = rawX;
                break;
            case MotionEvent.ACTION_MOVE:
                // 两次的偏移量
                int offsetX = rawX - lastX;
                moveView(offsetX, true);
                // 不断修改上次移动完成后坐标
                lastX = rawX;
                break;
            default:
                break;
        }
        return true;
//        return super.onTouchEvent(event);
    }

    public void setPosition(float percent) {
        float leftMargin = (getParentWidth() * percent);
        move(leftMargin, false);
        invalidate();
    }

    private int getParentWidth() {
        return parentWidth;
    }

    public float getPosition() {
        float percent = (getLeft()) / (float) getParentWidth();
        return percent;
    }

    private void moveView(int offsetX, boolean isFromUser) {

        int left = getLeft();
        int leftMargin = left + offsetX;
        move(leftMargin, isFromUser);
    }

    private void move(float leftMargin, boolean isFromUser) {

        if (leftMargin < 0) {
            leftMargin = 0;
        }

        if (leftMargin > getParentWidth()) {
            leftMargin = getParentWidth();
        }
        float left = leftBound * getParentWidth();

        // mixDive是设置左边界值时，设置一个最小值，不然往左滑会矩形会完全消失
        if (left != 0 && leftMargin < left + mixDive) {
            leftMargin = (int) left + mixDive;
        }

        float percent = (leftMargin) / (float) getParentWidth();

        if (onChangeListener != null) {
            onChangeListener.onPositionChange(this, percent, isFromUser);
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.leftMargin = (int)leftMargin;
        setLayoutParams(layoutParams);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public OnChangeListener onChangeListener;

    public interface OnChangeListener {

        void onPositionChange(MovePlayControl movePlayControl, float percent, boolean isFromUser);

    }

}
