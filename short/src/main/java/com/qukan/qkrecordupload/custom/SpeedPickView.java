package com.qukan.qkrecordupload.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.qukan.qkrecordupload.R;


/**
 * Created by Administrator on 2018/9/14 0014.
 */

public class SpeedPickView extends View {
    private Context mContext;

    private Paint paint;
    private Paint mBitmapPaint;

    private int width, height;

    //几条竖线
    private int items;
    private int itemWidth;
    private int itemColor;

    private int currentItem = 3;
    private int maxCurrentIndex = 7;

    private Bitmap mDragBitmap;
    boolean isAre = false;

    public void setItemChangeLintener(OnItemChange onItemChange) {
        this.onItemChangeLintener = onItemChange;
    }

    private OnItemChange onItemChangeLintener;


    public SpeedPickView(Context context) {
        this(context, null);
    }

    public SpeedPickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedPickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initCustomAttrs(context, attrs);
    }

    /**
     获取自定义属性
     */
    private void initCustomAttrs(Context context, AttributeSet attrs) {
        //获取自定义属性。
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpeedPickView);
        //获取文字颜色，默认颜色是BLUE
        itemColor = ta.getColor(R.styleable.SpeedPickView_item_color, Color.BLACK);
        //获取竖线的宽度
        itemWidth = (int)ta.getDimension(R.styleable.SpeedPickView_item_wdith, 1f);
        //获取分段
        items = ta.getInt(R.styleable.SpeedPickView_items, 3);

        ta.recycle();

        paint = new Paint();
        paint.setColor(itemColor);
        paint.setStrokeWidth(DensityUtils.dip2px(context, itemWidth));

        mBitmapPaint = new Paint();
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setAntiAlias(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isImage){
            Log.d("SpeedPickView","图片不能调整倍速");
            return true;
        }
        int temp = currentItem;
        float x = event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isTouchArc(x)){
                    isAre = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isAre){
                    updateRing(x);
                }
                break;
            case MotionEvent.ACTION_UP:
                isAre = false;

                break;
        }
        invalidate();
        if (temp != currentItem){
            if (onItemChangeLintener != null){
                onItemChangeLintener.onChange(currentItem);
            }
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画竖线
        int unitW = width / items / 2;
        for (int i = 0; i < items; i++) {
            canvas.drawLine(unitW + unitW * 2 * i, height / 10, unitW + unitW * 2 * i, height - height / 5, paint);
        }

        drawBitmap(canvas);
    }

    private void drawBitmap(Canvas canvas){
        int unitW = width / items / 2;

        mDragBitmap = BitmapUtils.getBitmap(mContext, R.drawable.short_ic_choose_speed);
        mDragBitmap = BitmapUtils.conversionBitmap(mDragBitmap, height, height);

        canvas.drawBitmap(mDragBitmap, unitW + (unitW * 2 * currentItem) - height / 2, 0, mBitmapPaint);
    }

    private void updateRing(float x){
        if (x < 0){
            x = 0;
        }
        if (x >= width){
            x = width - 1;//item从0开始，不减currentItem会超
        }
        int unitW = width / items;
        currentItem = (int) (x / unitW);
        if(currentItem >= maxCurrentIndex){
            currentItem = maxCurrentIndex - 1;
        }
    }

    /**
     * 按下时判断按下的点是否按在圆点范围内
     *
     * @param x x坐标点
     */
    private boolean isTouchArc(float x) {
        int unitW = width / items;
        return (x > currentItem * unitW) && (x < (currentItem + 1) * unitW);
    }

    public interface OnItemChange{
        void onChange(int index);
    }



    public void setCurrentIndex(int index) {
        currentItem = index;

        invalidate();

    }
    private boolean isImage = false;
    public void setImage(boolean image){
        isImage = image;
    }

}
