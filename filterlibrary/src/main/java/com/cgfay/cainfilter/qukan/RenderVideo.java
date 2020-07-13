package com.cgfay.cainfilter.qukan;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;

import com.cgfay.cainfilter.camerarender.CaptureFrameCallback;
import com.cgfay.cainfilter.camerarender.FrameRateMeter;
import com.cgfay.cainfilter.camerarender.RenderStateChangedListener;
import com.cgfay.cainfilter.gles.EglCore;
import com.cgfay.cainfilter.gles.WindowSurface;
import com.cgfay.cainfilter.qukan.record.QKRecordManager;
import com.cgfay.cainfilter.type.GLFilterGroupType;
import com.cgfay.cainfilter.type.GLFilterType;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class RenderVideo extends HandlerThread implements SurfaceTexture.OnFrameAvailableListener
        {

    private static final String TAG = "RenderVideo";

    private boolean isDebug = false;
    // 操作锁
    private final Object mSynOperation = new Object();
    // Looping锁
    private final Object mSyncIsLooping = new Object();

    private boolean isPreviewing = false;   // 是否预览状态
    private boolean isRecording = false;    // 是否录制状态
    private boolean isRecordingPause = false;   // 是否处于暂停录制状态

    // EGL共享上下文
    private EglCore mEglCore;
    // 预览用的EGLSurface
    private WindowSurface mDisplaySurface;


    // 矩阵
    private final float[] mMatrix = new float[16];

    // 视图宽高
    private int mViewWidth, mViewHeight;
    // 预览图片大小
    private int mImageWidth = 0, mImageHeight = 0;

    // 更新帧的锁
    private final Object mSyncFrameNum = new Object();
    private final Object mSyncTexture = new Object();
    // 可用帧
    private int mFrameNum = 0;
    // 拍照
    private boolean isTakePicture = false;
    // 拍照回调
    private CaptureFrameCallback mCaptureFrameCallback;


    // 渲染状态回调
    private RenderStateChangedListener mRenderStateListener;
    private RenderVideoHandler mRenderHandler;

    // 计算帧率
    private FrameRateMeter mFrameRateMeter;
    private WeakReference<Handler> mWeakFpsHandler;

    private Context mContext;
    private RenderVideoManager render = new RenderVideoManager();

    public RenderVideoManager getRender() {
        return render;
    }

    public RenderVideo(Context context, String name) {
        super(name);
        mContext = context;
    }

    public void setRenderHandler(RenderVideoHandler handler) {
        mRenderHandler = handler;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }

    private long time = 0;

    //展示i420数据
    public void showYuvBuffer(YuvData data){

        // 调整图片大小
        calculateImageSize(data.getWidth(),data.getHeight(),data.getOrientation());

        if (mRenderHandler != null) {
            synchronized (mSynOperation) {
                if (isPreviewing || isRecording) {
                    addNewFrame(data);
                }
            }
        }
        if (isDebug) {
            Log.d("onPreviewFrame", "update time = " + (System.currentTimeMillis() - time));
            time = System.currentTimeMillis();
        }
    }

    void surfaceCreated(SurfaceHolder holder) {
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mDisplaySurface = new WindowSurface(mEglCore, holder.getSurface(), false);
        mDisplaySurface.makeCurrent();
        // 渲染初始化
        getRender().init();

        // 禁用深度测试和背面绘制
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        GLES30.glDisable(GLES30.GL_CULL_FACE);

    }

    void surfaceChanged(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        getRender().onDisplaySizeChanged(mViewWidth, mViewHeight);
        // 开始预览
        isPreviewing = true;
        // 渲染状态回调
        if (mRenderStateListener != null) {
            mRenderStateListener.onPreviewing(isPreviewing);
            mRenderStateListener.onSurfaceChanged();
        }
        // 配置检测点的宽高
        //FaceTrackManager.getInstance().onDisplayChanged(mViewWidth, mViewHeight);
    }

    void surfaceDestoryed() {
        isPreviewing = false;
        // 渲染状态回调
        if (mRenderStateListener != null) {
            mRenderStateListener.onPreviewing(isPreviewing);
        }
        if (mWeakFpsHandler != null) {
            mWeakFpsHandler.clear();
            mWeakFpsHandler = null;
        }
        //FaceTrackManager.getInstance().release();

        // 释放Filter(需要在EGLContext释放之前处理，否则会报以下错误：
        // E/libEGL: call to OpenGL ES API with no current context (logged once per thread)
        getRender().release();

        if (mDisplaySurface != null) {
            mDisplaySurface.release();
            mDisplaySurface = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        mImageWidth = 0;
        mImageHeight = 0;
//        Log.d("group","surfaceDestoryed");

    }


    /**
     * 初始化人脸检测工具
     */
    private void initFaceDetection() {
        //FaceTrackManager.getInstance().setBackReverse(ParamsManager.mBackReverse); // 相机是否倒置
        //FaceTrackManager.getInstance().initFaceTracking(mContext);
        //FaceTrackManager.getInstance().setFaceCallback(this);
    }

    /**
     * 预览回调
     * @param data
     */
    void onPreviewCallback(byte[] data) {
        // 如果允许关键点检测，则进入关键点检测阶段，否则立即更新帧
    }




    /**
     * 开始预览
     */
    void startPreview() {
        getRender().updateTextureBuffer();
        isPreviewing = true;
        if (mRenderStateListener != null) {
            mRenderStateListener.onPreviewing(isPreviewing);
        }
    }

    /**
     * 停止预览
     */
    void stopPreview() {
        isPreviewing = false;
        if (mRenderStateListener != null) {
            mRenderStateListener.onPreviewing(isPreviewing);
        }
    }

    /**
     * 计算imageView 的宽高
     */
    private void calculateImageSize(int width,int height,int orientation) {
        int imgWidth = width;
        int imgHeight = height;
        if (orientation == 90 || orientation == 270) {
            imgWidth = height;
            imgHeight = width;
        }
        if(mImageWidth != imgWidth || mImageHeight != imgHeight || getRender().getmTextureWidth() != mImageWidth || getRender().getmTextureHeight() != imgHeight){
//            Log.d("group","calculateImageSize1");
            mImageWidth = imgWidth;
            mImageHeight = imgHeight;
            getRender().onInputSizeChanged(mImageWidth, mImageHeight);
        }
    }


    /**
     * 设置对焦区域
     * @param rect
     */
    void setFocusAres(Rect rect) {

    }

    /**
     * 设置打开闪光灯
     * @param on
     */
    void setFlashLight(boolean on) {
    }

    /**
     * 设置美颜等级(百分比)
     * @param percent 0 ~ 100
     */
    void setBeautifyLevel(int percent) {
        getRender().setBeautifyLevel(percent);
    }

    /**
     * 更新filter
     * @param type Filter类型
     */
    void changeFilter(GLFilterType type) {
        getRender().changeFilter(type);
    }

    /**
     * 切换滤镜组
     * @param type
     */
    void changeFilterGroup(GLFilterGroupType type) {
        synchronized (mSyncIsLooping) {
            getRender().changeFilterGroup(type);
        }
    }

    private long temp = 0;
    /**
     * 绘制帧
     */
    void drawFrame(YuvData data) {
        if(mDisplaySurface == null){
            return;
        }
        temp = System.currentTimeMillis();
        // 如果存在新的帧，则更新帧
        synchronized (mSyncFrameNum) {
            synchronized (mSyncTexture) {
                while (mFrameNum != 0) {
                    --mFrameNum;
                }
            }
        }

        // 切换渲染上下文
        mDisplaySurface.makeCurrent();
        // 绘制
        draw(data);
        // 拍照状态
        if (isTakePicture) {
            isTakePicture = false;
            ByteBuffer buffer = mDisplaySurface.getCurrentFrame();
            mCaptureFrameCallback.onFrameCallback(buffer,
                    mDisplaySurface.getWidth(), mDisplaySurface.getHeight());
        }
        mDisplaySurface.swapBuffers();
        // 是否处于录制状态
        if (isRecording && !isRecordingPause) {
            QKRecordManager.getInstance().frameAvailable();
            int currentTexture = getRender().getCurrentTexture();
            //mCameraTexture.getTimestamp() 添加时间戳
            QKRecordManager.getInstance()
                    .drawRecorderFrame(currentTexture, data.getTime());

        }
        // 调试信息
        if (isDebug) {
            Log.d(TAG, "drawFrame time = " + (System.currentTimeMillis() - temp));
        }

        // 计算绘制帧
        if (mFrameRateMeter != null) {
            mFrameRateMeter.drawFrameCount();
            if (mWeakFpsHandler != null && mWeakFpsHandler.get() != null) {
                mWeakFpsHandler.get().sendMessage(mWeakFpsHandler.get()
                        .obtainMessage(FrameRateMeter.MSG_GAIN_FPS, mFrameRateMeter.getFPS()));
            }
        }
    }



    /**
     * 绘制图像数据到FBO
     */
    private void draw(YuvData data) {



        // 绘制
        getRender().drawFrame(data);

        // 是否绘制关键点
        //FaceTrackManager.getInstance().drawTrackPoints();
    }


    /**
     * 拍照
     */
    void takePicture() {
        isTakePicture = true;
    }

    /**
     * 设置拍照回调
     * @param callback
     */
    void setCaptureFrameCallback(CaptureFrameCallback callback) {
        mCaptureFrameCallback = callback;
    }



    /**
     * 开始录制
     */
    void startRecording() {
        if (mEglCore != null && isPreviewing) {
            // 设置渲染Texture 的宽高
            QKRecordManager.getInstance().setTextureSize(mViewWidth, mViewHeight);//mImageWidth mImageHeight
            // 设置预览大小
            QKRecordManager.getInstance().setDisplaySize(mViewWidth, mViewHeight);
            // 这里将EGLContext传递到录制线程共享。
            // 由于EGLContext是当前线程手动创建，也就是OpenGLES的mainThread
            // 这里需要传自己手动创建的EglContext
            QKRecordManager.getInstance().startRecording(mEglCore.getEGLContext());
        }
        isRecording = true;
    }

    /**
     * 暂停录制
     */
    void pauseRecording() {
        QKRecordManager.getInstance().pauseRecording();
        isRecordingPause = true;
    }

    /**
     * 继续录制
     */
    void continueRecording() {
        QKRecordManager.getInstance().continueRecording();
        isRecordingPause = false;
    }

    /**
     * 停止录制
     */
    void stopRecording() {
        QKRecordManager.getInstance().stopRecording();
        isRecording = false;
    }



    /**
     * 切换相机
     */
    synchronized void switchCamera() {

        // 切换之后需要重置人脸检测管理器
        //FaceTrackManager.getInstance()
        //        .setBackCamera(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK);
        //FaceTrackManager.getInstance().reset(mContext);
    }

    /**
     * 添加新的一帧
     */
    private void addNewFrame(YuvData data) {
        synchronized (mSyncFrameNum) {
            if (isPreviewing) {
                ++mFrameNum;
                if (mRenderHandler != null) {
                    drawFrame(data);
                }
            }
        }
    }

    /**
     * 设置Fps的handler回调
     * @param handler
     */
    void setFpsHandler(Handler handler) {
        mWeakFpsHandler = new WeakReference<Handler>(handler);
        mFrameRateMeter = new FrameRateMeter();
    }

    /**
     * 设置渲染状态回调
     * @param listener
     */
    void setRenderStateChangedListener(RenderStateChangedListener listener) {
        mRenderStateListener = listener;
    }
}
