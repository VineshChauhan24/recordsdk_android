package com.qukan.qkrecordupload.fileCut;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;

import com.qukan.qkrecordupload.QkApplication;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecorduploadsdk.RecordContext;

import org.droidparts.util.L;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2016-06-17.
 */
public class RecordHelper {
    private Bitmap[] leftLogoInfo = new Bitmap[4];
    private Bitmap[] rightLogoInfo = new Bitmap[4];
    private Bitmap[] leftBottomLogoInfo = new Bitmap[4];
    private Bitmap[] rightBottomLogoInfo = new Bitmap[4];
    int srcWidth, srcHeight, windowWidth, windowHeight;
    int size;

    private static RecordHelper instance;
    public Handler handler = new Handler();
    private Paint textPaint;
    private Paint photoPaint;

    public static RecordHelper getInstance() {
        if (instance == null) {
            instance = new RecordHelper();
        }
        return instance;
    }

    // 进入活动直播时清空缓存
    public void clear() {
        leftLogoInfo = null;
        rightLogoInfo = null;
        leftBottomLogoInfo = null;
        rightBottomLogoInfo = null;

        leftLogoInfo = new Bitmap[4];
        rightLogoInfo = new Bitmap[4];
        leftBottomLogoInfo = new Bitmap[4];
        rightBottomLogoInfo = new Bitmap[4];
        handler.removeCallbacksAndMessages(null);
    }

    public void init(int srcWidth, int srcHeight, int windowWidth, int windowHeight) {
        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        photoPaint = new Paint(); // 建立画笔
        photoPaint.setDither(true); // 获取清晰的图像采样
        photoPaint.setFilterBitmap(true);// 过滤一些

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStrokeWidth(1);
        textPaint.setColor(QkApplication.getContext().getResources().getColor(R.color.white));
        textPaint.setStyle(Paint.Style.FILL);

        //
        leftLogoInfo[0] = setupBmpLogo(R.raw.user_logo_0);
        leftLogoInfo[1] = setupBmpLogo(R.raw.user_logo_1);
        leftLogoInfo[2] = setupBmpLogo(R.raw.user_logo_2);

        rightLogoInfo[0] = setupBmpLogo(R.raw.osd_logo_0);
        rightLogoInfo[1] = setupBmpLogo( R.raw.osd_logo_1);
        rightLogoInfo[2] = setupBmpLogo(R.raw.osd_logo_2);

        leftBottomLogoInfo[0] = setupBmpLogo(  R.raw.user_logo_0);
        leftBottomLogoInfo[1] = setupBmpLogo(  R.raw.user_logo_1);
        leftBottomLogoInfo[2] = setupBmpLogo(  R.raw.user_logo_2);

        rightBottomLogoInfo[0] = setupBmpLogo( R.raw.osd_logo_0);
        rightBottomLogoInfo[1] = setupBmpLogo( R.raw.osd_logo_1);
        rightBottomLogoInfo[2] = setupBmpLogo( R.raw.osd_logo_2);

    }

    Bitmap logo;
    Canvas canvas;

    public void drawLogoBitmap() {
        logo = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(logo);// 初始化画布绘制的图像到icon上

        boolean isShowLogo = true;
        boolean isLandscape = false;

        if (isLandscape) {
            size = srcWidth;
        } else {
            size = srcHeight;
        }

        // 绘制上端的LOGO
        if (leftLogoInfo != null && isShowLogo) {
            L.d("logo_bitmapsLeftLogo:srcWidth=%s", srcWidth);
            if (size >= 1280 && leftLogoInfo[2] != null) {
                canvas.drawBitmap(leftLogoInfo[2], 40, 40, photoPaint);
            } else if (size >= 1024 && leftLogoInfo[2] != null) {
                canvas.drawBitmap(leftLogoInfo[2], 24, 24, photoPaint);
            } else if (size >= 640 && leftLogoInfo[1] != null) {
                canvas.drawBitmap(leftLogoInfo[1], 15, 15, photoPaint);
            } else if (leftLogoInfo[0] != null) {
                canvas.drawBitmap(leftLogoInfo[0], 10, 10, photoPaint);
            }
            L.d("logo_bitmapsLeftLogoOver");
        } else {
            L.i("logo_bitmapsLeftLogo ==null");
        }

        if (rightLogoInfo != null && isShowLogo) {
            if (size >= 1280 && rightLogoInfo[2] != null) {
                canvas.drawBitmap(rightLogoInfo[2], srcWidth - rightLogoInfo[2].getWidth() - 40, 40, photoPaint);
            } else if (size >= 1024 && rightLogoInfo[2] != null) {
                canvas.drawBitmap(rightLogoInfo[2], srcWidth - rightLogoInfo[2].getWidth() - 24, 24, photoPaint);
            } else if (size >= 640 && rightLogoInfo[1] != null) {
                canvas.drawBitmap(rightLogoInfo[1], srcWidth - rightLogoInfo[1].getWidth() - 15, 15, photoPaint);
            } else if (rightLogoInfo[0] != null) {
                canvas.drawBitmap(rightLogoInfo[0], srcWidth - rightLogoInfo[0].getWidth() - 10, 10, photoPaint);
            }
            L.d("logo_bitmapsRightLogo over");
        } else {
            L.i("logo_bitmapsRightLogo ==null");
        }

        if (leftBottomLogoInfo != null && isShowLogo) {
            if (size >= 1280 && leftBottomLogoInfo[2] != null) {
                canvas.drawBitmap(leftBottomLogoInfo[2], 40, srcHeight - leftBottomLogoInfo[2].getHeight() - 40, photoPaint);
            } else if (size >= 1024 && leftBottomLogoInfo[2] != null) {
                canvas.drawBitmap(leftBottomLogoInfo[2], 24, srcHeight - leftBottomLogoInfo[2].getHeight() - 24, photoPaint);
            } else if (size >= 640 && leftBottomLogoInfo[1] != null) {
                canvas.drawBitmap(leftBottomLogoInfo[1], 15, srcHeight - leftBottomLogoInfo[1].getHeight() - 15, photoPaint);
            } else if (leftBottomLogoInfo[0] != null) {
                canvas.drawBitmap(leftBottomLogoInfo[0], 10, srcHeight - leftBottomLogoInfo[0].getHeight() - 10, photoPaint);
            }
            L.d("logo_bitmapsRightLogo over");
        } else {
            L.i("logo_bitmapsRightLogo ==null");
        }

        if (rightBottomLogoInfo != null && isShowLogo) {
            if (size >= 1280 && rightBottomLogoInfo[2] != null) {
                canvas.drawBitmap(rightBottomLogoInfo[2], srcWidth - rightBottomLogoInfo[2].getWidth() - 40, srcHeight - rightBottomLogoInfo[2].getHeight() - 40, photoPaint);
            } else if (size >= 1024 && rightBottomLogoInfo[2] != null) {
                canvas.drawBitmap(rightBottomLogoInfo[2], srcWidth - rightBottomLogoInfo[2].getWidth() - 24, srcHeight - rightBottomLogoInfo[2].getHeight() - 24, photoPaint);
            } else if (size >= 640 && rightBottomLogoInfo[1] != null) {
                canvas.drawBitmap(rightBottomLogoInfo[1], srcWidth - rightBottomLogoInfo[1].getWidth() - 15, srcHeight - rightBottomLogoInfo[1].getHeight() - 15, photoPaint);
            } else if (rightBottomLogoInfo[0] != null) {
                canvas.drawBitmap(rightBottomLogoInfo[0], srcWidth - rightBottomLogoInfo[0].getWidth() - 10, srcHeight - rightBottomLogoInfo[0].getHeight() - 10, photoPaint);
            }
            L.d("logo_bitmapsRightLogo over");
        } else {
            L.i("logo_bitmapsRightLogo ==null");
        }

//        setBmpLogo(RecordContext.LEFT_LOGO_SMALL, logo);
//        setBmpLogo(RecordContext.LEFT_LOGO_MIDDLE, logo);
        setBmpLogo(RecordContext.LEFT_LOGO_LARGE, logo);
        L.d("logo_canvas.getHeight()=%s,canvas.getWidth()=%s,logoWidth=%s,logoHeight=%s", canvas.getHeight(), canvas.getWidth(), logo.getWidth(), logo.getHeight());
        System.gc();
    }


    public Bitmap setupBmpLogo(int resId)   // 1,left 3,right
    {
        InputStream inputStream = QkApplication.getContext().getResources().openRawResource(resId);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        return bitmap;

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


    public void clearBmpLogo() {
        clearLeftBmpLogo();
        clearRightBmpLogo();
        clearLeftBottomBmpLogo();
        clearRightBottomBmpLogo();
    }

    public void clearLeftBmpLogo() {
        // 保存bmp数据
        byte[] abBuffer = {0};
        RecordContext.setLogoInfo(RecordContext.LEFT_LOGO_SMALL, 0, 0, abBuffer, 0, 0); // 保存bmp数据
        RecordContext.setLogoInfo(RecordContext.LEFT_LOGO_MIDDLE, 0, 0, abBuffer, 0, 0); // 保存bmp数
        RecordContext.setLogoInfo(RecordContext.LEFT_LOGO_LARGE, 0, 0, abBuffer, 0, 0);
    }

    public void clearRightBmpLogo() {
        // 保存bmp数据
        byte[] abBuffer = {0};
        RecordContext.setLogoInfo(RecordContext.RIGHT_LOGO_SMALL, 0, 0, abBuffer, 0, 0); // 保存bmp数据
        RecordContext.setLogoInfo(RecordContext.RIGHT_LOGO_MIDDLE, 0, 0, abBuffer, 0, 0); // 保存bmp数
        RecordContext.setLogoInfo(RecordContext.RIGHT_LOGO_LARGE, 0, 0, abBuffer, 0, 0);
    }

    public void clearLeftBottomBmpLogo() {
        // 保存bmp数据
        byte[] abBuffer = {0};
        RecordContext.setLogoInfo(RecordContext.LEFT_BOTTOM_LOGO_SMALL, 0, 0, abBuffer, 0, 0); // 保存bmp数据
        RecordContext.setLogoInfo(RecordContext.LEFT_BOTTOM_LOGO_MIDDLE, 0, 0, abBuffer, 0, 0); // 保存bmp数
        RecordContext.setLogoInfo(RecordContext.LEFT_BOTTOM_LOGO_LARGE, 0, 0, abBuffer, 0, 0);
    }

    public void clearRightBottomBmpLogo() {
        // 保存bmp数据
        byte[] abBuffer = {0};
        RecordContext.setLogoInfo(RecordContext.RIGHT_BOTTOM_LOGO_SMALL, 0, 0, abBuffer, 0, 0); // 保存bmp数据
        RecordContext.setLogoInfo(RecordContext.RIGHT_BOTTOM_LOGO_MIDDLE, 0, 0, abBuffer, 0, 0); // 保存bmp数
        RecordContext.setLogoInfo(RecordContext.RIGHT_BOTTOM_LOGO_LARGE, 0, 0, abBuffer, 0, 0);
    }

    public void setBmpLogo(int index, Bitmap bitmap) {
        try {
            // bmp数据
            ByteBuffer dst = ByteBuffer.allocate(bitmap.getByteCount());
            bitmap.copyPixelsToBuffer(dst);
            // 将dst中的提出来
            dst.position(0);
            dst.limit(bitmap.getByteCount());
            byte[] acBuffer = new byte[bitmap.getByteCount()];
            dst.get(acBuffer, 0, bitmap.getByteCount());
            // 保存bmp数据
            RecordContext.setYuv420LogoInfo(2, bitmap.getWidth(), bitmap.getHeight(), acBuffer, 0, bitmap.getByteCount());
            L.i(index + " logo设置成功");
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            L.w(e);
        }
    }
}