package com.cgfay.cainfilter.qukan.record;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.cgfay.cainfilter.camerarender.CaptureFrameCallback;
import com.cgfay.cainfilter.camerarender.ParamsManager;
import com.cgfay.cainfilter.camerarender.RecordManager;
import com.cgfay.cainfilter.camerarender.RenderManager;
import com.cgfay.cainfilter.camerarender.RenderStateChangedListener;
import com.cgfay.cainfilter.multimedia.MediaEncoder;
import com.cgfay.cainfilter.qukan.AudioData;
import com.cgfay.cainfilter.qukan.RenderVideo;
import com.cgfay.cainfilter.qukan.RenderVideoHandler;
import com.cgfay.cainfilter.qukan.YuvData;
import com.cgfay.cainfilter.type.GLFilterGroupType;
import com.cgfay.cainfilter.type.GLFilterType;
import com.cgfay.cainfilter.type.GalleryType;

public class CompleteVideoManager {
    private static final String TAG = "CompleteVideoManager";


    private CompleteRenderVideoHandler mRenderHandler;
    private CompleteRenderVideo mRenderThread;

    // 操作锁
    private final Object mSynOperation = new Object();


    public CompleteVideoManager() {

    }


//    public void setOutPutPath(String path){
//        QKRecordManager.getInstance().setOutputPath(path);
//    }

    public void initRecord(int width, int height, String path, MediaEncoder.MediaEncoderListener
            mEncoderListener) {
        Log.d(TAG, "initRecord1");
        // 初始化录制线程
        QKRecordManager.getInstance().initThread();
        // 设置输出路径
//        String path = ParamsManager.VideoPath
//                + "CainCamera_" + System.currentTimeMillis() + ".mp4";
        QKRecordManager.getInstance().setOutputPath(path);
        // 是否允许录音，只有录制视频才有音频
        QKRecordManager.getInstance().setEnableAudioRecording(true);
        // 是否允许高清录制
        QKRecordManager.getInstance().enableHighDefinition(true);
        // 初始化录制器
        QKRecordManager.getInstance().initRecorder(width,
                height, mEncoderListener);
    }


    //设置显示的界面的宽高
    public void disPlayScreen(int width, int height) {
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(CompleteRenderVideoHandler.MSG_SURFACE_CHANGED, width, height));
        }
        startPreview();
    }

    public void drawYUVData(YuvData data) {
        if (mRenderHandler != null) {

            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(CompleteRenderVideoHandler.MSG_FRAME, data));
        }
        startPreview();
    }

    public void drawAudioData(AudioData data) {
        if (mRenderHandler != null) {
//            Log.d(TAG,"drawAudioData");
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(CompleteRenderVideoHandler.MSG_AUDIODATA, data));
        }
    }


    /**
     * 创建HandlerThread和Handler
     */
    synchronized public void createRenderThread(Context context) {
        mRenderThread = new CompleteRenderVideo(context, "CompleteRenderVideo");
        mRenderThread.start();
        mRenderHandler = new CompleteRenderVideoHandler(mRenderThread.getLooper(), mRenderThread);
        // 绑定Handler
        mRenderThread.setRenderHandler(mRenderHandler);
    }


    /**
     * 销毁当前持有的Looper 和 Handler
     */
    synchronized public void destoryTrhead() {
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
        mRenderHandler.sendEmptyMessage(CompleteRenderVideoHandler.MSG_DESTROY);
        mRenderThread.quitSafely();
        try {
            mRenderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRenderThread = null;
        mRenderHandler = null;
    }


    public void surfaceCreated(SurfaceHolder holder) {
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(CompleteRenderVideoHandler.MSG_SURFACE_CREATED, holder));
        }
    }

    public void surfacrChanged(int width, int height) {
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(CompleteRenderVideoHandler.MSG_SURFACE_CHANGED, width, height));
        }
        startPreview();
    }


    public void surfaceDestroyed() {
        stopPreview();
        if (mRenderHandler != null) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(CompleteRenderVideoHandler.MSG_SURFACE_DESTROYED));
        }
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
                    .obtainMessage(CompleteRenderVideoHandler.MSG_START_PREVIEW));
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
                    .obtainMessage(CompleteRenderVideoHandler.MSG_STOP_PREVIEW));
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
                    .obtainMessage(CompleteRenderVideoHandler.MSG_FILTER_TYPE, type));
        }
    }

    /**
     * 改变滤镜组类型
     *
     * @param type
     */
    public void changeFilterGroup(GLFilterGroupType type) {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(CompleteRenderVideoHandler.MSG_FILTER_GROUP, type));
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
                    .obtainMessage(CompleteRenderVideoHandler.MSG_START_RECORDING));
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
                    .obtainMessage(CompleteRenderVideoHandler.MSG_PAUSE_RECORDING));
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
                    .obtainMessage(CompleteRenderVideoHandler.MSG_CONTINUE_RECORDING));
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
            mRenderHandler.sendEmptyMessage(CompleteRenderVideoHandler.MSG_STOP_RECORDING);
        }
    }


    public void addRenderStateChangedListener(RenderStateChangedListener listener) {
        if (mRenderHandler == null) {
            return;
        }
        synchronized (mSynOperation) {
            // 发送渲染状态监听
            mRenderHandler.sendMessage(mRenderHandler
                    .obtainMessage(CompleteRenderVideoHandler.MSG_SET_RENDER_STATE_CHANGED_LISTENER, listener));
        }
    }
}
