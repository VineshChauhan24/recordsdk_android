package com.cgfay.cainfilter.facetracker;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.cgfay.cainfilter.utils.CameraUtils;
import com.cgfay.cainfilter.utils.Size;
import com.cgfay.cainfilter.utils.facepp.ConUtil;
import com.cgfay.cainfilter.utils.GlUtil;
import com.cgfay.cainfilter.utils.facepp.SensorEventUtil;
//import com.megvii.facepp.sdk.Facepp;
//import com.megvii.licensemanager.sdk.LicenseManager;


/**
 * 人脸关键点检测管理器
 * Created by cain.huang on 2017/11/10.
 */

public class FaceTrackManager {

    private static FaceTrackManager mInstance;

    // 是否开启调试模式
    private boolean isDebug = false;

    // 属性值
    private boolean is3DPose = false;
    private boolean isROIDetect = false;
    private boolean is106Points = true;
    private boolean isBackCamera = false;
    private boolean isFaceProperty = false;
    private boolean isOneFaceTracking = false;

    // 检测模式
//    private int mTrackModel = Facepp.FaceppConfig.DETECTION_MODE_TRACKING;

    // 检测线程
    private HandlerThread mTrackerThread;
    private Handler mTrackerHandler;

    // 人脸检测实体
//    private Facepp facepp;

    // 最小人脸大小
    private int mMinFaceSize = 200;
    // 检测时间检测
    private int mDetectionInterval = 25;

    // 传感器监听器
    private SensorEventUtil mSensorUtil;

    private float roi_ratio = 0.8f;

    private int Angle;

    // 是否处于检测过程中
    private boolean isDetecting = false;

    // 是否存在人脸
    private volatile boolean mHasFace = false;

    // 置信度
    float confidence;
    // 姿态角x轴
    float pitch;
    // 姿态角y轴
    float yaw;
    // 姿态角z轴
    float roll;

    int rotation = Angle;

    // 关键点绘制检测
    private FacePointsDrawer mFacePointsDrawer;

    private final float[] mMVPMatrix = GlUtil.IDENTITY_MATRIX;
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];

    // 预览画面宽高
    private int mViewWidth;
    private int mViewHeight;

    // 是否允许人脸检测
    private boolean canFaceTrack = false;
    // 是否相机倒置
    private boolean mBackReverse = false;
    // 是否允许绘制
    private boolean enableDrawingPoints = false;

    // 预览尺寸
    private Size mPreviewSize = null;
    // 检测回调
    private FaceTrackerCallback mFaceTrackerCallback;

    public static FaceTrackManager getInstance() {
        if (mInstance == null) {
            mInstance = new FaceTrackManager();
        }
        return mInstance;
    }

    /**
     * Face++SDK联网请求
     */
    public void requestFaceNetwork(Context context) {
//        if (Facepp.getSDKAuthType(ConUtil.getFileContent(context, R.raw
//                .megviifacepp_0_4_7_model)) == 2) {// 非联网授权
//            canFaceTrack = true;
//            return;
//        }
//        final LicenseManager licenseManager = new LicenseManager(context);
//        licenseManager.setExpirationMillis(Facepp.getApiExpirationMillis(context,
//                ConUtil.getFileContent(context, R.raw.megviifacepp_0_4_7_model)));
//
//        String uuid = ConUtil.getUUIDString(context);
//        long apiName = Facepp.getApiName();
//
//        licenseManager.setAuthTimeBufferMillis(0);
//
//        licenseManager.takeLicenseFromNetwork(uuid, Util.API_KEY, Util.API_SECRET, apiName,
//                LicenseManager.DURATION_30DAYS, "Landmark", "1", true,
//                new LicenseManager.TakeLicenseCallback() {
//                    @Override
//                    public void onSuccess() {
//                        if (isDebug) {
//                            Log.d("LicenseManager", "success to register license!");
//                        }
//                        canFaceTrack = true;
//                    }
//
//                    @Override
//                    public void onFailed(int i, byte[] bytes) {
//                        if (isDebug) {
//                            Log.d("LicenseManager", "Failed to register license!");
//                        }
//                        canFaceTrack = false;
//                    }
//                });
    }

    /**
     * 开始检测线程
     */
    public void startFaceTrackThread() {
        mTrackerThread = new HandlerThread("FaceTrackThread");
        mTrackerThread.start();
        mTrackerHandler = new Handler(mTrackerThread.getLooper());
    }

    /**
     * 停止检测线程
     */
    public void stopFaceTrackingThread() {
        // 释放检测线程以及Handler回调
        if (mTrackerHandler == null) {
            if (mTrackerThread != null) {
                mTrackerThread.quitSafely();
                try {
                    mTrackerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mTrackerThread = null;
            }
            return;
        }
        mTrackerHandler.removeCallbacksAndMessages(null);
        mTrackerThread.quitSafely();
        try {
            mTrackerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mTrackerThread = null;
        mTrackerHandler = null;
        
    }

    /**
     * 初始化人脸检测
     * 备注：在相机打开之后调用
     */
    public void initFaceTracking(Context context) {
        if (canFaceTrack) {

//            if (facepp != null) {
//                facepp.release();
//            }
//            facepp = new Facepp();

            mSensorUtil = new SensorEventUtil(context);

            // 开启检测线程
            startFaceTrackThread();

            ConUtil.acquireWakeLock(context);

            if (CameraUtils.getCamera() != null) {
                Angle = 360 - CameraUtils.getPreviewOrientation();
                if (isBackCamera) {
                    Angle = CameraUtils.getPreviewOrientation();
                }
                Size size = CameraUtils.getPreviewSize();
                int width = size.getWidth();
                int height = size.getHeight();

                int left = 0;
                int top = 0;
                int right = width;
                int bottom = height;
                if (isROIDetect) {
                    float line = height * roi_ratio;
                    left = (int) ((width - line) / 2.0f);
                    top = (int) ((height - line) / 2.0f);
                    right = width - left;
                    bottom = height - top;
                }

//                String errorCode = facepp.init(context, ConUtil.getFileContent(context,
//                        R.raw.megviifacepp_0_4_7_model));
//                Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
//                faceppConfig.interval = mDetectionInterval;
//                faceppConfig.minFaceSize = mMinFaceSize;
//                faceppConfig.roi_left = left;
//                faceppConfig.roi_top = top;
//                faceppConfig.roi_right = right;
//                faceppConfig.roi_bottom = bottom;
//                if (isOneFaceTracking)
//                    faceppConfig.one_face_tracking = 1;
//                else
//                    faceppConfig.one_face_tracking = 0;
//
//                faceppConfig.detectionMode = mTrackModel;
//
//                facepp.setFaceppConfig(faceppConfig);
            }
        }

        // 初始化关键点绘制器
        if (enableDrawingPoints) {
            mFacePointsDrawer = new FacePointsDrawer();
        }
    }


    /**
     * 视图发生变化时调用
     * @param width
     * @param height
     */
    public void onDisplayChanged(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;

        int ratio = 1;
        // 投影矩阵
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public void reset(Context context) {
        release();
        initFaceTracking(context);
    }

    /**
     *  释放资源
     */
    public void release() {
        // 停止检测线程
        stopFaceTrackingThread();
        // 释放检测实体
//        if (facepp != null) {
//            facepp.release();
//            facepp = null;
//        }
    }

    /**
     * SDK角度配置
     * @param rotation 当前的角度
     */
    private void setConfig(int rotation) {
//        if (facepp == null) {
//            return;
//        }
//        Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
//        if (faceppConfig.rotation != rotation) {
//            faceppConfig.rotation = rotation;
//            facepp.setFaceppConfig(faceppConfig);
//        }
    }

    /**
     * 人脸检测
     * @param data
     */
    public void onFaceTracking(final byte[] data) {
        if (!canFaceTrack) {
            if (mFaceTrackerCallback != null) {
                mFaceTrackerCallback.onTrackingFinish(false);
            }
            return;
        }
        if (isDetecting) {
            return;
        }
        isDetecting = true;
        if (mTrackerHandler != null) {
            mTrackerHandler.post(new Runnable() {
                @Override
                public void run() {
//                    if (facepp == null) {
//                        if (mFaceTrackerCallback != null) {
//                            mFaceTrackerCallback.onTrackingFinish(false);
//                        }
//                        isDetecting = false;
//                        return;
//                    }
                    mPreviewSize = CameraUtils.getPreviewSize();
                    int width = mPreviewSize.getWidth();
                    int height = mPreviewSize.getHeight();

                    // 调整检测监督
                    long faceDetectTime_action = System.currentTimeMillis();
                    int orientation = mSensorUtil.orientation;
                    if (orientation == 0) {
                        rotation = Angle;
                    } else if (orientation == 1) {
                        rotation = 0;
                    } else if (orientation == 2) {
                        rotation = 180;

                    } else if (orientation == 3) {
                        rotation = 360 - Angle;
                    }
                    setConfig(rotation);

                    mHasFace = false;

//                    final Facepp.Face[] faces = facepp.detect(data, width, height, Facepp.IMAGEMODE_NV21);
                    if (isDebug) {
                        final long algorithmTime = System.currentTimeMillis() - faceDetectTime_action;
                        Log.d("onFaceTracking", "track time = " + algorithmTime);
                    }

                    // 清除先前的关键点
                    if (enableDrawingPoints) {
                        mFacePointsDrawer.points.clear();
                    }
                    // 准备人脸关键点管理器
                    FacePointsManager.getInstance().prepareToAddPoints();
//                    if (faces != null) {
//                        ArrayList<ArrayList> facePoints = new ArrayList<ArrayList>();
//                        confidence = 0.0f;
//                        if (faces.length > 0) {
//                            mHasFace = true;
//                            for (int index = 0; index < faces.length; index++) {
//                                if (is106Points)
//                                    facepp.getLandmark(faces[index], Facepp.FPP_GET_LANDMARK106);
//                                else
//                                    facepp.getLandmark(faces[index], Facepp.FPP_GET_LANDMARK81);
//
//                                if (is3DPose) {
//                                    facepp.get3DPose(faces[index]);
//                                }
//
//                                Facepp.Face face = faces[index];
//
//                                if (isFaceProperty) {
//                                    long time_AgeGender_action = System.currentTimeMillis();
//                                    facepp.getAgeGender(faces[index]);
//
//                                    if (isDebug) {
//                                        Log.d("onFaceTracking", "getAgeGenderTime: "
//                                                + (System.currentTimeMillis() - time_AgeGender_action));
//
//                                        String gender = "man";
//                                        if (face.female > face.male)
//                                            gender = "woman";
//                                        Log.d("onFaceTracking", "age: "
//                                                + (int) Math.max(face.age, 1)
//                                                + "\ngender: " + gender);
//                                    }
//                                }
//
//                                // 设置姿态角以及置信度
//                                pitch = faces[index].pitch;
//                                yaw = faces[index].yaw;
//                                roll = faces[index].roll;
//                                confidence = faces[index].confidence;
//
//                                // 添加姿态角
//                                float euler[] = new float[4];
//                                euler[0] = pitch;
//                                euler[1] = yaw;
//                                euler[2] = roll;
//                                FacePointsManager.getInstance().addEulers(euler);
//
//                                // 调整宽高
//                                if (orientation == 1 || orientation == 2) {
//                                    width = mPreviewSize.getHeight();
//                                    height = mPreviewSize.getWidth();
//                                }
//
//                                // 一个人脸的关键点
//                                ArrayList<FloatBuffer> onePoints = new ArrayList<FloatBuffer>();
//                                for (int i = 0; i < faces[index].points.length; i++) {
//                                    float x = (faces[index].points[i].x / height) * 2 - 1;
//                                    float y = 1 - (faces[index].points[i].y / width) * 2;
//                                    if (orientation == 0 || orientation == 3) { // 竖屏状态下
//                                        if (isBackCamera) {
//                                            x = -x;
//                                        }
//                                    }
//                                    float[] pointf = new float[] { x, y, 0.0f };
//
//                                    if (orientation == 1) {
//                                        if (isBackCamera) {
//                                            if (mBackReverse) {
//                                                pointf = new float[] { y, x, 0.0f };
//                                            } else {
//                                                pointf = new float[] { -y, -x, 0.0f };
//                                            }
//                                        } else {
//                                            pointf = new float[] { -y, x, 0.0f };
//                                        }
//                                    }
//
//                                    if (orientation == 2) {
//                                        if (isBackCamera) {
//                                            if (mBackReverse) {
//                                                pointf = new float[]{-y, -x, 0.0f};
//                                            } else {
//                                                pointf = new float[] { y, x, 0.0f };
//                                            }
//                                        } else {
//                                            pointf = new float[] { y, -x, 0.0f };
//                                        }
//                                    }
//
//                                    if (orientation == 3) {
//                                        pointf = new float[] { -x, -y, 0.0f };
//                                    }
//
//                                    // 添加一个关键点
//                                    FacePointsManager.getInstance().addOnePoint(pointf);
//
//                                    // 是否允许绘制关键点
//                                    if (enableDrawingPoints) {
//                                        FloatBuffer fb = GlUtil.createFloatBuffer(pointf);
//                                        onePoints.add(fb);
//                                    }
//                                }
//                                if (enableDrawingPoints) {
//                                    facePoints.add(onePoints);
//                                }
//                                // 添加一个人脸的关键点
//                                FacePointsManager.getInstance().addOneFacePoints();
//                            }
//
//                            if (enableDrawingPoints) {
//                                mFacePointsDrawer.points = facePoints;
//                            }
//                        } else {
//                            pitch = 0.0f;
//                            yaw = 0.0f;
//                            roll = 0.0f;
//                        }
//                    }
                    // 更新关键点
                    FacePointsManager.getInstance().updateFacePoints();
                    isDetecting = false;
                    // 检测回调，用于更新下一帧
                    if (mFaceTrackerCallback != null) {
                        mFaceTrackerCallback.onTrackingFinish(mHasFace);
                    }
                    mPreviewSize = null;
                }
            });
        } else {
            // 重置人脸关键点
            FacePointsManager.getInstance().resetFacePoints();
            // 检测回调，表示当前并没有检测
            if (mFaceTrackerCallback != null) {
                mFaceTrackerCallback.onTrackingFinish(false);
            }
            isDetecting = false;
        }
    }

    /**
     * 绘制关键点
     */
    public void drawTrackPoints() {
        if (!enableDrawingPoints) {
            return;
        }
        // 回执关键点
        Matrix.setLookAtM(mVMatrix, 0,
                0, 0, -3,
                0f, 0f, 0f,
                0f, 1f, 0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        GLES30.glViewport(0, 0, mViewWidth, mViewHeight);
        mFacePointsDrawer.draw(mMVPMatrix);
    }

    /**
     * 设置人脸关键点检测器回调
     * @param callback
     */
    public void setFaceCallback(FaceTrackerCallback callback) {
        mFaceTrackerCallback = callback;
    }


    //--------------------------- setter and getter ----------------------------

    public void enable3DPose(boolean is3DPose) {
        this.is3DPose = is3DPose;
    }

    public void enableROIDetect(boolean ROIDetect) {
        isROIDetect = ROIDetect;
    }

    /**
     * 是否检测106个点
     * @return
     */
    public boolean isIs106Points() {
        return is106Points;
    }

    /**
     * 是否允许106个点
     * @param enable
     */
    public void enable106Points(boolean enable) {
        is106Points = enable;
    }

    /**
     * 是否后置相机
     * @param backCamera
     */
    public void setBackCamera(boolean backCamera) {
        isBackCamera = backCamera;
    }

    /**
     * 是否允许检测人脸属性(男女、年龄等)
     * @param faceProperty
     */
    public void setFaceProperty(boolean faceProperty) {
        isFaceProperty = faceProperty;
    }

    /**
     * 是否只检测一个人脸
     * @param oneFaceTracking
     */
    public void setOneFaceTracking(boolean oneFaceTracking) {
        isOneFaceTracking = oneFaceTracking;
    }

    /**
     * 设置检测模式
     * @param trackModel
     */
    public void setTrackModel(int trackModel) {

    }

    /**
     * 设置最小人脸大小
     * @param minSize
     */
    public void setMinFaceSize(int minSize) {
        mMinFaceSize = minSize;
    }

    /**
     * 设置检测间隔
     * @param interval
     */
    public void setDetectionInterval(int interval) {
        mDetectionInterval = interval;
    }

    /**
     * 设置相机倒置
     * @param backReverse
     */
    public void setBackReverse(boolean backReverse) {
        mBackReverse = backReverse;
    }
}
