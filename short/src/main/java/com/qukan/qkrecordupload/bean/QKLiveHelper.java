package com.qukan.qkrecordupload.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qukan.qkrecordupload.QkApplication;
import com.qukan.qkrecordupload.R;


import org.droidparts.util.L;

import java.nio.ByteBuffer;


public class QKLiveHelper {
    // 缓存logoInfo对象

    int srcWidth, srcHeight;

    private static QKLiveHelper instance;

    private Paint textPaint;
    private Paint photoPaint;
    private Bitmap saveBitmap;

    public static QKLiveHelper getInstance() {
        if (instance == null) {
            instance = new QKLiveHelper();
        }
        return instance;
    }


    public void init(int srcWidth, int srcHeight) {
        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;


        photoPaint = new Paint(); // 建立画笔
        photoPaint.setDither(true); // 获取清晰的图像采样
        photoPaint.setFilterBitmap(true);// 过滤一些

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStrokeWidth(1);
        textPaint.setColor(QkApplication.getContext().getResources().getColor(R.color.white));
        textPaint.setStyle(Paint.Style.FILL);
    }


    Bitmap logo;
    Canvas canvas;

    public Bitmap ScaleBitmap(Bitmap bitmap) {
        logo = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(logo);// 初始化画布绘制的图像到icon上
        if (bitmap != null) {
            // 部分包含虚拟按键的手机需要计算虚拟按键的高度，绘制cg时作为偏移量
            float offsetX = (srcWidth - bitmap.getWidth())/2f;
            float offsetY = (srcHeight - bitmap.getHeight())/2f;
            canvas.drawBitmap(bitmap,offsetX, offsetY, photoPaint);
        }


        L.d("logo_canvas.getHeight()=%s,canvas.getWidth()=%s,logoWidth=%s,logoHeight=%s", canvas.getHeight(), canvas.getWidth(), logo.getWidth(), logo.getHeight());
        System.gc();
        return logo;
    }



    public Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWight = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            final Matrix matrix = new Matrix();
            // 如果需要完全不按原图比例缩放可以去掉这个步骤
            float originalScale = Math.max(scaleWight, scaleHeight);
            matrix.setScale(originalScale, originalScale);
            Bitmap res = Bitmap
                    .createBitmap(bitmap, 0, 0, width, height, matrix, false);
            return res;

        } else {
            return null;
        }
    }

}
