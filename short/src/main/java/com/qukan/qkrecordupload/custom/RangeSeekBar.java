package com.qukan.qkrecordupload.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.qukan.qkrecordupload.QkApplication;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.RnToast;

import org.droidparts.util.L;


/**
 * 控件首先检测触摸位置是否在滑块之内,
 * 然后算出百分比,根据百分比进行滑块的移动绘制
 */
public class RangeSeekBar extends View {
    private Paint paint = new Paint();
    private Paint paintRect = new Paint();

    private int lineTop, lineBottom, lineLeft, lineRight;
    //    private float initOffset;
    private int lineCorners;
    private int lineWidth;
    private Rect line = new Rect();

    private int colorLineSelected;
    private int colorLineEdge;

    private SeekBar leftSeekBar = new SeekBar();
    private SeekBar rightSeekBar = new SeekBar();
    private SeekBar currTouch;

    private OnRangeChangedListener callback;
    private OnRangeLeftChangedListener callbackLeft;
    private OnRangeRightChangedListener callbackRight;

    private int seekBarResId;
    private int seekBarResId2;
    private float offsetValue;
    private float maxValue, minValue;
    private int cellsCount = 1;
    private float cellsPercent;
    private float reserveValue;
    private int reserveCount;
    private float reservePercent;
    private Bitmap backBitmap;
    Bitmap currentSeekbmp;
    Bitmap secondSeekbmp;
    private float mPlayCurrPercent = 1;
    public boolean isPlay;
    public static final int DRAG = 5000;

    private class SeekBar {
        RadialGradient shadowGradient;
        Paint defaultPaint;
        int widthSize, heightSize;
        float currPercent;
        int left, right, top, bottom;
        Bitmap bmp;


        float material = 0;
        ValueAnimator anim;
        final TypeEvaluator<Integer> te = new TypeEvaluator<Integer>() {
            @Override
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                int alpha = (int) (Color.alpha(startValue) + fraction * (Color.alpha(endValue) - Color.alpha(startValue)));
                int red = (int) (Color.red(startValue) + fraction * (Color.red(endValue) - Color.red(startValue)));
                int green = (int) (Color.green(startValue) + fraction * (Color.green(endValue) - Color.green(startValue)));
                int blue = (int) (Color.blue(startValue) + fraction * (Color.blue(endValue) - Color.blue(startValue)));
                return Color.argb(alpha, red, green, blue);
            }
        };


        void onSizeChanged(int w, int h, boolean cellsMode, Bitmap original, Context context) {
            heightSize = h;
            // 滑块的图片
//            Bitmap original = BitmapFactory.decodeResource(context.getResources(), bmpResId);
//            Matrix matrix = new Matrix();
//            float scaleHeight = ((float) heightSize) / original.getHeight();
//            matrix.postScale(scaleHeight, scaleHeight);
//            bmp = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
            bmp = original;

            int bitmapWidth = bmp.getWidth();
            int bitmapHeight = bmp.getHeight();

            lineLeft = bitmapWidth / 2;
            lineRight = w - bitmapWidth / 2;
            lineTop = 0;
            lineBottom = h - (int) (bitmapHeight / 2.65);
            // 背景条块的宽
            lineWidth = lineRight - lineLeft;
            // 背景区域
            line.set(lineLeft, lineTop, lineRight, lineBottom);

            // 滑块宽
            widthSize = bmp.getWidth();
            left = 0;
            right = bitmapWidth;
            top = 0;
            bottom = bitmapHeight;

            rightSeekBar.currPercent = 1f;
        }

        boolean collide(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            int offset = (int) (lineWidth * currPercent);
            return x > left + offset && x < right + offset && y > top && y < bottom;
        }

        // 判断手指是否触碰到了滑块
        boolean collideRight(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            int offset = (int) (lineWidth * currPercent);
            return x > offset && x < right / 2 + offset && y > top && y < bottom;
        }

        void slide(float percent) {
            if (percent < 0) percent = 0;
            else if (percent > 1) percent = 1;
            currPercent = percent;
        }


        void draw(Canvas canvas) {
            int offset = (int) ((lineWidth) * currPercent);
            canvas.save();
            canvas.translate(offset, 0);
            if (bmp != null) {
                canvas.drawBitmap(bmp, 0, 0, null);
            } else {

            }
            canvas.restore();
        }

        private void drawDefault(Canvas canvas) {
            int centerX = widthSize / 2;
            int centerY = heightSize / 2;
            int radius = (int) (widthSize * 0.5f);
            // draw shadow
            defaultPaint.setStyle(Paint.Style.FILL);
            canvas.save();
            canvas.translate(0, radius * 0.25f);
            canvas.scale(1 + (0.1f * material), 1 + (0.1f * material), centerX, centerY);
            defaultPaint.setShader(shadowGradient);
            canvas.drawCircle(centerX, centerY, radius, defaultPaint);
            defaultPaint.setShader(null);
            canvas.restore();
            // draw body
            defaultPaint.setStyle(Paint.Style.FILL);
            defaultPaint.setColor(te.evaluate(material, 0xFFFFFFFF, 0xFFE7E7E7));
            canvas.drawCircle(centerX, centerY, radius, defaultPaint);
            // draw border
            defaultPaint.setStyle(Paint.Style.STROKE);
            defaultPaint.setColor(0xFFD7D7D7);
            canvas.drawCircle(centerX, centerY, radius, defaultPaint);
        }

        private void materialRestore() {
            if (anim != null) anim.cancel();
            anim = ValueAnimator.ofFloat(material, 0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    material = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    material = 0;
                    invalidate();
                }
            });
            anim.start();
        }
    }

    public interface OnRangeChangedListener {
        void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser);
    }

    public interface OnRangeLeftChangedListener {
        void onRangeLeftChanged(RangeSeekBar view, float min, boolean isFromUser);
    }

    public interface OnRangeRightChangedListener {
        void onRangeRightChanged(RangeSeekBar view, float max, boolean isFromUser);
    }


    public RangeSeekBar(Context context) {
        this(context, null);
    }

    public RangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar);
        seekBarResId = t.getResourceId(R.styleable.RangeSeekBar_seekBarResId, 0);
        seekBarResId2 = t.getResourceId(R.styleable.RangeSeekBar_seekBarResId2, 0);
        colorLineSelected = t.getColor(R.styleable.RangeSeekBar_lineColorSelected, 0xFF4BD962);
        colorLineEdge = t.getColor(R.styleable.RangeSeekBar_lineColorEdge, 0xFFD7D7D7);
        float min = t.getFloat(R.styleable.RangeSeekBar_min, 0);
        float max = t.getFloat(R.styleable.RangeSeekBar_max, 1);
        float reserve = t.getFloat(R.styleable.RangeSeekBar_reserve, 0);
        int cells = t.getInt(R.styleable.RangeSeekBar_cells, 1);
        setRules(min, max, reserve, cells);
        t.recycle();
    }

    public void setOnRangeChangedListener(OnRangeChangedListener listener) {
        callback = listener;
    }

    public void setOnRangeLeftChangedListener(OnRangeLeftChangedListener listener) {
        callbackLeft = listener;
    }

    public void setOnRangeRightChangedListener(OnRangeRightChangedListener listener) {
        callbackRight = listener;
    }

    public void setValue(float min, float max) {
        min = min + offsetValue;
        max = max + offsetValue;

        if (min < minValue) {
            throw new IllegalArgumentException("setValue() min < (preset min - offsetValue) . #min:" + min + " #preset min:" + minValue + " #offsetValue:" + offsetValue);
        }
        if (max > maxValue) {
            throw new IllegalArgumentException("setValue() max > (preset max - offsetValue) . #max:" + max + " #preset max:" + maxValue + " #offsetValue:" + offsetValue);
        }

        if (reserveCount > 1) {
            if ((min - minValue) % reserveCount != 0) {
                throw new IllegalArgumentException("setValue() (min - preset min) % reserveCount != 0 . #min:" + min + " #preset min:" + minValue + "#reserveCount:" + reserveCount + "#reserve:" + reserveValue);
            }
            if ((max - minValue) % reserveCount != 0) {
                throw new IllegalArgumentException("setValue() (max - preset min) % reserveCount != 0 . #max:" + max + " #preset min:" + minValue + "#reserveCount:" + reserveCount + "#reserve:" + reserveValue);
            }
            leftSeekBar.currPercent = (min - minValue) / reserveCount * cellsPercent;
            rightSeekBar.currPercent = (max - minValue) / reserveCount * cellsPercent;
        } else {
            leftSeekBar.currPercent = (min - minValue) / (maxValue - minValue);
            rightSeekBar.currPercent = (max - minValue) / (maxValue - minValue);
        }

        invalidate();
    }

    public void setRules(float min, float max) {
        setRules(min, max, reserveCount, cellsCount);
    }

    // 设置一个最小的区间比例，临近这个区间则滑块无法滑动
    public void setReservePercent(float mReservePercent) {
        reservePercent = mReservePercent;
    }

    public void setRules(float min, float max, float reserve, int cells) {
        if (max <= min) {
            throw new IllegalArgumentException("setRules() max must be greater than min ! #max:" + max + " #min:" + min);
        }
        if (min < 0) {
            offsetValue = 0 - min;
            min = min + offsetValue;
            max = max + offsetValue;
        }
        minValue = min;
        maxValue = max;

        if (reserve < 0) {
            throw new IllegalArgumentException("setRules() reserve must be greater than zero ! #reserve:" + reserve);
        }
        if (reserve >= max - min) {
            throw new IllegalArgumentException("setRules() reserve must be less than (max - min) ! #reserve:" + reserve + " #max - min:" + (max - min));
        }
        if (cells < 1) {
            throw new IllegalArgumentException("setRules() cells must be greater than 1 ! #cells:" + cells);
        }
        cellsCount = cells;
        cellsPercent = 1f / cellsCount;
        reserveValue = reserve;
        reservePercent = reserve / (max - min);
        reserveCount = (int) (reservePercent / cellsPercent + (reservePercent % cellsPercent != 0 ? 1 : 0));
        if (cellsCount > 1) {
            if (leftSeekBar.currPercent + cellsPercent * reserveCount <= 1 && leftSeekBar.currPercent + cellsPercent * reserveCount > rightSeekBar.currPercent) {
                rightSeekBar.currPercent = leftSeekBar.currPercent + cellsPercent * reserveCount;
            } else if (rightSeekBar.currPercent - cellsPercent * reserveCount >= 0 && rightSeekBar.currPercent - cellsPercent * reserveCount < leftSeekBar.currPercent) {
                leftSeekBar.currPercent = rightSeekBar.currPercent - cellsPercent * reserveCount;
            }
        } else {
            if (leftSeekBar.currPercent + reservePercent <= 1 && leftSeekBar.currPercent + reservePercent > rightSeekBar.currPercent) {
                rightSeekBar.currPercent = leftSeekBar.currPercent + reservePercent;
            } else if (rightSeekBar.currPercent - reservePercent >= 0 && rightSeekBar.currPercent - reservePercent < leftSeekBar.currPercent) {
                leftSeekBar.currPercent = rightSeekBar.currPercent - reservePercent;
            }
        }
        invalidate();
    }

    public float[] getCurrentRange() {
        float range = maxValue - minValue;
        return new float[]{-offsetValue + minValue + range * leftSeekBar.currPercent,
                -offsetValue + minValue + range * rightSeekBar.currPercent};
    }

    public float getMax() {
        return maxValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSize * 1.8f > widthSize) {
            setMeasuredDimension(widthSize, (int) (widthSize / 1.8f));
            L.d("onMeasure:true");
        } else {
            L.d("onMeasure:false");
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        L.d(" :w=%s,h=%s", w, h);


        Bitmap original_current = BitmapFactory.decodeResource(getResources(), seekBarResId);
        Bitmap original_second = BitmapFactory.decodeResource(getResources(), seekBarResId2);
        Matrix matrix = new Matrix();
        float scaleHeight = ((float) h) / original_current.getHeight();
        matrix.postScale(scaleHeight, scaleHeight);
        currentSeekbmp = Bitmap.createBitmap(original_current, 0, 0, original_current.getWidth(), original_current.getHeight(), matrix, true);
        secondSeekbmp = Bitmap.createBitmap(original_second, 0, 0, original_second.getWidth(), original_second.getHeight(), matrix, true);

        leftSeekBar.onSizeChanged(w, h, cellsCount > 1, currentSeekbmp, getContext());
        rightSeekBar.onSizeChanged(w, h, cellsCount > 1, secondSeekbmp, getContext());

        if (cellsCount == 1) {
            rightSeekBar.left += leftSeekBar.widthSize;
            rightSeekBar.right += leftSeekBar.widthSize;
        }
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paintRect.setStyle(Paint.Style.FILL);
        paintRect.setColor(0x86000000);
        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(colorLineEdge);
        if (cellsPercent > 0) {
            paint.setStrokeWidth(lineCorners * 0.2f);
            for (int i = 1; i < cellsCount; i++) {
                canvas.drawLine(lineLeft + i * cellsPercent * lineWidth, lineTop - lineCorners,
                        lineLeft + i * cellsPercent * lineWidth, lineBottom + lineCorners, paint);
            }
        }

//        canvas.drawBitmap(backBitmap,lineLeft,0, paint);
        if (backBitmap != null) {
            canvas.drawBitmap(backBitmap, line, line, paint);
            // 播放进度条只有在两个滑块中间时才会显示
//            if (isPlay && leftSeekBar.currPercent < mPlayCurrPercent && mPlayCurrPercent < rightSeekBar.currPercent) {
                float offset = lineWidth * mPlayCurrPercent + leftSeekBar.bmp.getWidth() / 2;
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(5f);
                canvas.drawLine(offset, 0, offset, backBitmap.getHeight(), paint);
//            }

            float offsetLeft = lineWidth * leftSeekBar.currPercent + leftSeekBar.bmp.getWidth() / 2;
            float offsetRight = (lineWidth * rightSeekBar.currPercent + rightSeekBar.bmp.getWidth() / 2);

            canvas.drawRect(lineLeft, 0, offsetLeft, lineBottom, paintRect);
            canvas.drawRect(offsetRight, 0, lineWidth + rightSeekBar.bmp.getWidth() / 2, lineBottom, paintRect);

        }

        leftSeekBar.draw(canvas);
        rightSeekBar.draw(canvas);
    }

    public int getCurrentSeekBar() {
        if (currentSeekbmp == leftSeekBar.bmp) {
            return 1;
        } else if (currentSeekbmp == rightSeekBar.bmp) {
            return 2;
        } else {
            return 0;
        }
    }




    // parm:当前播放时间/视频总时间
    public void drawPlay(float playCurrPercent) {
        isPlay = true;
        this.mPlayCurrPercent = playCurrPercent;
        invalidate();
    }

    public void stopDrawPlay() {
        invalidate();
        isPlay = false;
    }

    public void setBack(Bitmap bitmap) {
        backBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        invalidate();
    }

    // 设置左右滑块的位置,用百分比当参数
    public void setSeekValue(float left, float right) {
        leftSeekBar.slide(left);
        rightSeekBar.slide(right);

        float[] result = getCurrentRange();
        if (callbackLeft != null) {
            callbackLeft.onRangeLeftChanged(this, result[0], false);
        }
        if (callbackRight != null) {
            callbackRight.onRangeRightChanged(this, result[1], false);
        }
        if (callback != null) {
            callback.onRangeChanged(this, result[0], result[1], false);
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean touchResult = false;
                if (rightSeekBar.currPercent >= 1 && leftSeekBar.collide(event)) {
                    currTouch = leftSeekBar;
                    touchResult = true;
                } else if (rightSeekBar.collideRight(event)) {
                    currTouch = rightSeekBar;
                    touchResult = true;
                } else if (leftSeekBar.collide(event)) {
                    currTouch = leftSeekBar;
                    touchResult = true;
                }
                return touchResult;
            case MotionEvent.ACTION_MOVE:
                float percent;
                float x = event.getX();
                L.d("ACTION_MOVE");
                currTouch.material = currTouch.material >= 1 ? 1 : currTouch.material + 0.1f;

                if (currTouch == leftSeekBar) {
                    if (cellsCount > 1) {
                        if (x < lineLeft) {
                            percent = 0;
                        } else {
                            percent = (x - lineLeft) * 1f / (lineWidth);
                        }
                        int touchLeftCellsValue = Math.round(percent / cellsPercent);
                        int currRightCellsValue = Math.round(rightSeekBar.currPercent / cellsPercent);
                        percent = touchLeftCellsValue * cellsPercent;

                        while (touchLeftCellsValue > currRightCellsValue - reserveCount) {
                            touchLeftCellsValue--;
                            if (touchLeftCellsValue < 0) break;
                            percent = touchLeftCellsValue * cellsPercent;
                        }
                    } else {
                        if (x < lineLeft) {
                            percent = 0;
                        } else {
                            percent = (x - lineLeft) * 1f / lineWidth;
                        }

                        if (percent > rightSeekBar.currPercent - reservePercent) {
                            RnToast.showToast(QkApplication.getContext(), "裁剪的视频片段时长不能小于3秒");
                            percent = rightSeekBar.currPercent - reservePercent;
                        }
                    }
                    leftSeekBar.bmp = currentSeekbmp;
                    rightSeekBar.bmp = secondSeekbmp;
                    leftSeekBar.slide(percent);
                    if (callbackLeft != null) {
                        float[] result = getCurrentRange();
                        callbackLeft.onRangeLeftChanged(this, result[0], true);
                    }

                } else if (currTouch == rightSeekBar) {
                    if (cellsCount > 1) {
                        if (x > lineRight) {
                            percent = 1;
                        } else {
                            percent = (x + lineLeft) * 1f / (lineWidth);
                        }
                        int touchRightCellsValue = Math.round(percent / cellsPercent);
                        int currLeftCellsValue = Math.round(leftSeekBar.currPercent / cellsPercent);
                        percent = touchRightCellsValue * cellsPercent;

                        while (touchRightCellsValue < currLeftCellsValue + reserveCount) {
                            touchRightCellsValue++;
                            if (touchRightCellsValue > maxValue - minValue) break;
                            percent = touchRightCellsValue * cellsPercent;
                        }
                    } else {

                        if (x > lineRight) {

                            percent = 1;
                        } else {
                            percent = (x - lineLeft) * 1f / lineWidth;
                        }
                        if (percent < leftSeekBar.currPercent + reservePercent) {
                            percent = leftSeekBar.currPercent + reservePercent;
                            RnToast.showToast(QkApplication.getContext(), "裁剪的视频片段时长不能小于3秒");
                        }
                    }
                    rightSeekBar.slide(percent);
                    if (callbackRight != null) {
                        float[] result = getCurrentRange();
                        callbackRight.onRangeRightChanged(this, result[1], true);
                    }
                    leftSeekBar.bmp = secondSeekbmp;
                    rightSeekBar.bmp = currentSeekbmp;
                }

                if (callback != null) {
                    float[] result = getCurrentRange();
                    callback.onRangeChanged(this, result[0], result[1], true);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                currTouch.materialRestore();

                if (callback != null) {
                    float[] result = getCurrentRange();
                    callback.onRangeChanged(this, result[0], result[1], true);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.minValue = minValue - offsetValue;
        ss.maxValue = maxValue - offsetValue;
        ss.reserveValue = reserveValue;
        ss.cellsCount = cellsCount;
        float[] results = getCurrentRange();
        ss.currSelectedMin = results[0];
        ss.currSelectedMax = results[1];
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        float min = ss.minValue;
        float max = ss.maxValue;
        float reserve = ss.reserveValue;
        int cells = ss.cellsCount;
        setRules(min, max, reserve, cells);
        float currSelectedMin = ss.currSelectedMin;
        float currSelectedMax = ss.currSelectedMax;
        setValue(currSelectedMin, currSelectedMax);
    }

    private class SavedState extends BaseSavedState {
        private float minValue;
        private float maxValue;
        private float reserveValue;
        private int cellsCount;
        private float currSelectedMin;
        private float currSelectedMax;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            minValue = in.readFloat();
            maxValue = in.readFloat();
            reserveValue = in.readFloat();
            cellsCount = in.readInt();
            currSelectedMin = in.readFloat();
            currSelectedMax = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(minValue);
            out.writeFloat(maxValue);
            out.writeFloat(reserveValue);
            out.writeInt(cellsCount);
            out.writeFloat(currSelectedMin);
            out.writeFloat(currSelectedMax);
        }
    }

    public void reset() {
        leftSeekBar.currPercent = 0;
        rightSeekBar.currPercent = 1;
        if (callbackRight != null) {
            float[] result = getCurrentRange();
            callbackRight.onRangeRightChanged(this, result[1], false);
        }
        if (callbackLeft != null) {
            float[] result = getCurrentRange();
            callbackLeft.onRangeLeftChanged(this, result[0], false);
        }
        if (callback != null) {
            float[] result = getCurrentRange();
            callback.onRangeChanged(this, result[0], result[1], false);
        }

        invalidate();
    }
}
