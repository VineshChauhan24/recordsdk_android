package com.qukan.qkrecordupload.fileCut;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import org.droidparts.util.L;

/**
 * Created by Administrator on 2017/6/24 0024.
 */
public class VideoTextHelper {

    private static VideoTextHelper videoTextHelper = new VideoTextHelper();

    Canvas canvas;
    Bitmap bitmapNew;
    Paint photoPaint;

    private VideoTextHelper() {
        super();
    }

    public static VideoTextHelper getInstance() {
        return videoTextHelper;
    }

    public void initPaint() {
        photoPaint = new Paint(); // 建立画笔
        photoPaint.setDither(true); // 获取清晰的图像采样
        photoPaint.setFilterBitmap(true);// 过滤一些
    }

    public void clear() {
        canvas = null;
        bitmapNew = null;
        photoPaint = null;
    }


    public Bitmap drawText(Bitmap bitmap, int videoLayoutWidth, int videoLayoutHeight, int width, int height) {
        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();

        int offsetX = (originWidth - videoLayoutWidth) / 2;
        int offsetY = (originHeight - videoLayoutHeight) / 2;

        Bitmap bitmapTemp = Bitmap.createBitmap(bitmap, offsetX, offsetY, videoLayoutWidth, videoLayoutHeight);
        L.d("offsetX=%s, offsetY=%s,videoLayoutWidth=%s,videoLayoutHeight=%s,bitmapTemp.getWidth()=%s,bitmapTemp.getHeight()=%s", offsetX, offsetY, videoLayoutWidth, videoLayoutHeight, bitmapTemp.getWidth(), bitmapTemp.getHeight());

        if (bitmapNew == null || canvas == null) {
            bitmapNew = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmapNew);// 初始化画布
        }
        if (photoPaint == null) {
            initPaint();
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Matrix matrix = new Matrix();
        float sx = width / (float) videoLayoutWidth;
        float sy = height / (float) videoLayoutHeight;
        matrix.postScale(sx, sy);

        canvas.drawBitmap(bitmapTemp, matrix, photoPaint);
        return bitmapNew;
    }

    public Bitmap drawNewText(Bitmap bitmap, int videoLayoutWidth, int videoLayoutHeight, int width, int height) {
        Bitmap newBitmap = null;
        Canvas newCanvas = null;
        Paint newPhotoPaint = null;
        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();

        int offsetX = (originWidth - videoLayoutWidth) / 2;
        int offsetY = (originHeight - videoLayoutHeight) / 2;

        Bitmap bitmapTemp = Bitmap.createBitmap(bitmap, offsetX, offsetY, videoLayoutWidth, videoLayoutHeight);

        if (newBitmap == null || canvas == null) {
            newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            newCanvas = new Canvas(newBitmap);// 初始化画布
        }
        if (newPhotoPaint == null) {
            newPhotoPaint = new Paint(); // 建立画笔
            newPhotoPaint.setDither(true); // 获取清晰的图像采样
            newPhotoPaint.setFilterBitmap(true);// 过滤一些
        }

        newCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Matrix matrix = new Matrix();
        float sx = width / (float) videoLayoutWidth;
        float sy = height / (float) videoLayoutHeight;
        matrix.postScale(sx, sy);

        newCanvas.drawBitmap(bitmapTemp, matrix, newPhotoPaint);
        return newBitmap;
    }

    // 是否需要重新绘制
    private void isRedraw() {

    }

}
