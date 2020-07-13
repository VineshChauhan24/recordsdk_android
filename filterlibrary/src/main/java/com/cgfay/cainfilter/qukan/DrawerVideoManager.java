package com.cgfay.cainfilter.qukan;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.cgfay.cainfilter.camerarender.CaptureFrameCallback;
import com.cgfay.cainfilter.camerarender.DrawerManager;
import com.cgfay.cainfilter.camerarender.RenderStateChangedListener;
import com.cgfay.cainfilter.type.GLFilterGroupType;
import com.cgfay.cainfilter.type.GLFilterType;

public class DrawerVideoManager {

    private static final String TAG = "DrawerVideoManager";


    private RenderVideoHandler mRenderHandler;
    private RenderVideo mRenderThread;

    // 操作锁
    private final Object mSynOperation = new Object();

    private boolean mSetFpsHandler = false;


    public DrawerVideoManager() {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_SURFACE_CREATED, holder));
        }
    }

    public void surfacrChanged(int width, int height) {
        Log.d(TAG,"surfacrChanged:" + width + "  height:" + height);
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_SURFACE_CHANGED, width, height));
        }
        startPreview();
    }

    public void drawYUVData(YuvData data) {
        if (mRenderHandler != null) {

            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_FRAME, data));
        }
        startPreview();
    }


    public void surfaceDestroyed() {
        stopPreview();
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_SURFACE_DESTROYED));
        }
    }

    /**
     * 创建HandlerThread和Handler
     */
    synchronized public void createRenderThread(Context context) {
        mRenderThread = new RenderVideo(context, "RenderThread");
        mRenderThread.start();
        mRenderHandler = new RenderVideoHandler(mRenderThread.getLooper(), mRenderThread);
        // 绑定Handler
        mRenderThread.setRenderHandler(mRenderHandler);
    }


    /**
     * 销毁当前持有的Looper 和 Handler
     */
    synchronized public void destoryTrhead() {

        mSetFpsHandler = false;
        // Handler不存在时，需要销毁当前线程，否则可能会出现重新打开不了的情况
        if (mRenderHandler == null) {
            if (mRenderThread != null) {
                mRenderThread.quitSafely();
                try {
                    mRenderThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mRenderThread = null;
            }
            return;
        }
        mRenderHandler.sendEmptyMessage(RenderVideoHandler.MSG_DESTROY);
        mRenderThread.quitSafely();
        try {
            mRenderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRenderThread = null;
        mRenderHandler = null;
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_START_PREVIEW));
        }
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_STOP_PREVIEW));
        }
    }



    /**
     * 设置美颜等级 0 ~ 100
     * @param percent
     */
    public void setBeautifyLevel(int percent) {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_SET_BEAUTIFY_LEVEL, percent));
        }
    }

    /**
     * 改变Filter类型
     */
    public void changeFilterType(GLFilterType type) {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_FILTER_TYPE, type));
        }
    }

    /**
     * 改变滤镜组类型
     * @param type
     */
    public void changeFilterGroup(GLFilterGroupType type) {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_FILTER_GROUP, type));
        }
    }



    /**
     * 开始录制
     */
    public void startRecording() {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_START_RECORDING));
        }
    }

    /**
     * 暂停录制
     */
    public void pauseRecording() {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_PAUSE_RECORDING));
        }
    }

    /**
     * 继续录制
     */
    public void continueRecording() {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_CONTINUE_RECORDING));
        }
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendEmptyMessage(RenderVideoHandler.MSG_STOP_RECORDING);
        }
    }

    /**
     * 拍照
     */
    public void takePicture() {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            // 发送拍照命令
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_TAKE_PICTURE));
        }
    }

    /**
     * 设置拍照回调
     * @param callback
     */
    public void setCaptureFrameCallback(CaptureFrameCallback callback) {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            // 发送拍照命令
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_SET_CAPTURE_FRAME_CALLBACK, callback));
        }
    }

    /**
     * 设置Fps Handler回调
     * @param handler
     */
    public void setFpsHandler(Handler handler) {
        if (mRenderHandler == null) {
            mSetFpsHandler = false;
            return;
        }
        synchronized (mSynOperation) {
            // 发送Fps回调handler
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_SET_FPSHANDLER, handler));
        }
        mSetFpsHandler = true;
    }

    /**
     * 是否设置了Fps Handler
     * @return
     */
    public boolean hasSetFpsHandle() {
        return mSetFpsHandler;
    }

    public void addRenderStateChangedListener(RenderStateChangedListener listener) {
        if (mRenderHandler == null) {
            mSetFpsHandler = false;
            return;
        }
        synchronized (mSynOperation) {
            // 发送渲染状态监听
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(RenderVideoHandler.MSG_SET_RENDER_STATE_CHANGED_LISTENER, listener));
        }
    }
}