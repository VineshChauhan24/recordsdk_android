package com.qukan.qkrecordupload.fileRecord.record;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.cgfay.cainfilter.camerarender.CaptureFrameCallback;
import com.cgfay.cainfilter.camerarender.ParamsManager;
import com.cgfay.cainfilter.camerarender.RenderStateChangedListener;
import com.cgfay.cainfilter.utils.AspectFrameLayout;
import com.cgfay.cainfilter.utils.BitmapUtils;
import com.cgfay.cainfilter.utils.CainSurfaceView;
import com.cgfay.cainfilter.utils.CameraUtils;
import com.cgfay.cainfilter.utils.PermissionUtils;
import com.cgfay.cainfilter.utils.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qukan.qkrecordupload.OutPutMessage;
import com.qukan.qkrecordupload.fileRecord.NewCameraActivity;
import com.qukan.qkrecordupload.fileRecord.view.RatioImageView;
import com.qukan.qkrecordupload.fileRecord.view.ShutterButton;
import com.qukan.qkrecordupload.ConfigureConstants;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;
import com.qukan.qkrecorduploadsdk.QukanRecord;
import com.qukan.qkrecorduploadsdk.StartRecordListioner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.os.Looper.getMainLooper;
import static com.qukan.qkrecordupload.PublicUtils.getVideoDuration;

/**
 * Created by Administrator on 2018/7/12 0012.
 */

public class VedioFragment extends Fragment implements View.OnClickListener, RatioImageView.RatioChangedListener, RenderStateChangedListener, CaptureFrameCallback, CainSurfaceView.OnClickListener {

    private final long PAGE_TIME = 60 * 1000 * 15;
    private int page = 1;


    private NewCameraActivity activity = null;

    private static final String TAG = "VedioFragment";

    private static final int REQUEST_PREVIEW = 0x200;

    private static final int REQUEST_CAMERA = 0x01;
    private static final int REQUEST_STORAGE = 0x02;
    private static final int REQUEST_RECORD = 0x03;
    private static final int REQUEST_LOCATION = 0x04;

    // 对焦大小
    private static final int FocusSize = 100;
    // 权限使能标志
    private boolean mCameraEnable = false;
    private boolean mStorageWriteEnable = false;
    private boolean mRecordSoundEnable = false;

    // 状态标志
    private boolean mOnPreviewing = false;
    private boolean mOnRecording = false;

    private long clickTime = 0L;
    private Context mContext;

    // 预览部分
    private AspectFrameLayout mAspectLayout;
    private CainSurfaceView mCameraSurfaceView;
    // 左上角返回和切换前后摄像头
    private ImageView iv_back,iv_switch;

    // 右侧imageview
    private ImageView iv_voice;
    private ImageView iv_flash;
    private ImageView iv_shake;
    private ImageView iv_fourk;
    // 闪光灯是否打开
    private boolean flash_on = false;
    // 录制时是否录制声音
    private boolean is_voice = true;
    // 是否打开防抖
    private boolean is_stabilization = false;
    // 预览尺寸切换
    private RatioImageView mRatioView;
    // 文件缩略图
    private ImageView iv_file1,iv_file2,iv_file3;



    // 倒计时
    private TextView mCountDownView;

    // 底部layout 和 Button
    private ShutterButton mBtnShutter;

    private Button mBtnRecordDelete;
    private Button mBtnRecordPreview;



    // 当前长宽比类型，默认16:9
    private AspectRatioType mCurrentRatioType = AspectRatioType.Ratio_16_9;

    // 当前长宽比值
    private float mCurrentRatio;


    // 主线程Handler
    private Handler mMainHandler;

    private String videoPath;
    private String oldVideoPath;

    private DisplayImageOptions options;

    boolean isRecognizing = false;
    boolean isRecrd= false;


    private ImageView img;

    //录制速度选择
    private ImageView iv_speed;
    private LinearLayout ll_speed;
    private TextView tv_speed1, tv_speed2, tv_speed3, tv_speed4, tv_speed5;

    private VedioTimer vedioTimer;
    public ArrayList<VideoProcessBean> list = new ArrayList<>();


    private QukanRecord qkRecord;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vedio_fragment, null);
        if (mCameraEnable && mRecordSoundEnable) {
            initView(view);
        }else {
            getActivity().finish();
            Toast.makeText(getActivity(), "摄像头权限未申请", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (NewCameraActivity) getActivity();
        qkRecord = new QukanRecord(mContext);
        //主线程用于拍照
        mMainHandler = new Handler(getMainLooper());
        //申请权限
        mCameraEnable = PermissionUtils.permissionChecking(mContext,
                Manifest.permission.CAMERA);
        mStorageWriteEnable = PermissionUtils.permissionChecking(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        mRecordSoundEnable = PermissionUtils.permissionChecking(mContext,
                Manifest.permission.RECORD_AUDIO);
        if (mCameraEnable && mRecordSoundEnable) {
            qkRecord.setListener(this,this,new StartRecordListioner() {
                @Override
                public void onFinish() {
                    mNeedToWaitStop = false;
                    mBtnRecordPreview.setVisibility(View.GONE);
                    mBtnRecordDelete.setVisibility(View.GONE);
                    if(OutPutMessage.instance().getCallBack() != null){
                        OutPutMessage.instance().getCallBack().onVideoRecordPath(oldVideoPath);
                    }
                    //视频录制完成
                    addVideo(oldVideoPath);

                    if(!isRecrd){
                        activity.startSensor();
                    }
                }

                @Override
                public void onStart() {
                    mOnRecording = true;
                    // 编码器已经进入录制状态，则快门按钮可用
                    mBtnShutter.setEnableEncoder(true);
                    // 开始倒计时
                    CountDownManager.getInstance().startTimer();
                }

                @Override
                public void onReStart() {
                    if (isRecrd){
                        startRecord();
                    }
                }
            });

        } else {
            Toast.makeText(getActivity(), "权限未申请", Toast.LENGTH_SHORT).show();
        }

        //隔30秒判断一下内存是否足够
        vedioTimer = new VedioTimer(new VedioTimer.RecordListener() {
            @Override
            public void onTimeChange(long duration) {
                if (duration > PAGE_TIME * page){
                    page++;
                    stopRecording();
                }
                else if (duration % (30 * 1000) == 1000){
                    if (!checkFreeSize()){
                        onStopRecord();
                        page = 1;
                        vedioTimer.stop();
                        showSomeView();
                    }
                }
                mCountDownView.setText(StringUtils.generateTime((int) duration));
            }
        });
    }

    private void initView(View view) {
        img = (ImageView) view.findViewById(R.id.img);

        mCurrentRatio = CameraUtils.getCurrentRatio();

        mAspectLayout = (AspectFrameLayout) view.findViewById(R.id.layout_aspect);
        mAspectLayout.setAspectRatio(mCurrentRatio);
        mAspectLayout.setOritation(getOritation());

        if (mCurrentRatio == CameraUtils.Ratio_1_1 && OritationUtil.getOritation() == 1) {
            mAspectLayout.setPadding(0, dip2px(getActivity(), 57), 0, 0);
        } else if (mCurrentRatio == CameraUtils.Ratio_1_1 && OritationUtil.getOritation() == 2){
            mAspectLayout.setPadding(dip2px(getActivity(), 57), 0, 0, 0);
        } else {
            mAspectLayout.setPadding(0, 0, 0, 0);
        }
        //添加录像的surface
        mCameraSurfaceView = new CainSurfaceView(mContext);
        mCameraSurfaceView.getHolder().addCallback(qkRecord);
        mCameraSurfaceView.addClickListener(this);
        mAspectLayout.addView(mCameraSurfaceView);
        mAspectLayout.requestLayout();



        mRatioView = (RatioImageView) view.findViewById(R.id.iv_ratio);
        mRatioView.addRatioChangedListener(this);

        if (mCurrentRatio == CameraUtils.Ratio_1_1){
            mRatioView.setRatioType(AspectRatioType.RATIO_1_1);
        } else if (mCurrentRatio == CameraUtils.Ratio_4_3){
            mRatioView.setRatioType(AspectRatioType.RATIO_4_3);
        }else if (mCurrentRatio == CameraUtils.Ratio_16_9){
            mRatioView.setRatioType(AspectRatioType.Ratio_16_9);
        }

        mCountDownView = (TextView) view.findViewById(R.id.tv_countdown);

        mBtnShutter = (ShutterButton) view.findViewById(R.id.btn_take);
        mBtnShutter.setOnClickListener(this);
        mBtnShutter.setIsRecorder(true);

        mBtnRecordDelete = (Button) view.findViewById(R.id.btn_record_delete);
        mBtnRecordDelete.setOnClickListener(this);
        mBtnRecordPreview = (Button) view.findViewById(R.id.btn_record_done);
        mBtnRecordPreview.setOnClickListener(this);


        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        iv_switch = (ImageView) view.findViewById(R.id.iv_switch);
        iv_switch.setOnClickListener(this);

        iv_flash = (ImageView) view.findViewById(R.id.iv_flash);
        iv_flash.setOnClickListener(this);
        iv_voice = (ImageView) view.findViewById(R.id.iv_voice);
        iv_voice.setOnClickListener(this);
        iv_shake = (ImageView) view.findViewById(R.id.iv_shake);
        iv_shake.setOnClickListener(this);
        iv_fourk = (ImageView) view.findViewById(R.id.iv_fourk);
        iv_fourk.setOnClickListener(this);

        iv_file1 = (ImageView) view.findViewById(R.id.iv_file1);
        iv_file1.setOnClickListener(this);
        iv_file2 = (ImageView) view.findViewById(R.id.iv_file2);
        iv_file2.setOnClickListener(this);
        iv_file3 = (ImageView) view.findViewById(R.id.iv_file3);
        iv_file3.setOnClickListener(this);

        iv_speed = (ImageView) view.findViewById(R.id.iv_speed);
        tv_speed1 = (TextView) view.findViewById(R.id.tv_speed1);
        tv_speed2 = (TextView) view.findViewById(R.id.tv_speed2);
        tv_speed3 = (TextView) view.findViewById(R.id.tv_speed3);
        tv_speed4 = (TextView) view.findViewById(R.id.tv_speed4);
        tv_speed5 = (TextView) view.findViewById(R.id.tv_speed5);
        iv_speed.setOnClickListener(this);
        tv_speed1.setOnClickListener(this);
        tv_speed2.setOnClickListener(this);
        tv_speed3.setOnClickListener(this);
        tv_speed4.setOnClickListener(this);
        tv_speed5.setOnClickListener(this);
        ll_speed = (LinearLayout) view.findViewById(R.id.ll_speed);
        initTvColor();

        if (qkRecord.getSpeed() == 0.5f){
            tv_speed1.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        } else if (qkRecord.getSpeed() == 0.75f){
            tv_speed2.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        } else if (qkRecord.getSpeed() == 1.0f){
            tv_speed3.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        } else if (qkRecord.getSpeed() == 1.5f){
            tv_speed4.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        } else if (qkRecord.getSpeed() == 2.0f){
            tv_speed5.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        }
    }




    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{ Manifest.permission.CAMERA }, REQUEST_CAMERA);
    }

    private void requestStorageWritePermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_STORAGE);
    }

    private void requestRecordPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{ Manifest.permission.RECORD_AUDIO }, REQUEST_RECORD);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // 相机权限
            case REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraEnable = true;
                }
                break;

            // 存储权限
            case REQUEST_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStorageWriteEnable = true;
                }
                break;

            // 录音权限
            case REQUEST_RECORD:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mRecordSoundEnable = true;
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        registerHomeReceiver();
        registerPhoneStateListener();
        if (mCameraEnable) {
            qkRecord.startPreview();
        } else {
            requestCameraPermission();
        }
        // 判断是否允许写入权限
        if (PermissionUtils.permissionChecking(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mStorageWriteEnable = true;
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        unRegisterHomeReceiver();
        //停止录制
        if (mOnRecording) {
            stopRecording();
            // 停止计时
            page = 1;
            isRecrd = false;
            vedioTimer.stop();
            mCountDownView.setText("");
            showSomeView();
        }
    }
    private void stopRecording(){
        oldVideoPath = videoPath;
        qkRecord.stopRecording();
        mOnRecording = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        qkRecord.release();
    }

    private void registerHomeReceiver() {
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        getActivity().registerReceiver(mHomePressReceiver, homeFilter);
    }

    private void unRegisterHomeReceiver() {
        getActivity().unregisterReceiver(mHomePressReceiver);
    }



    /**
     * 监听点击home键
     */
    private BroadcastReceiver mHomePressReceiver = new BroadcastReceiver() {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.isEmpty(reason)) {
                    return;
                }
                //当点击了home键时需要停止预览，防止后台一直持有相机
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    //停止录制
                    if (mOnRecording) {
                        stopRecording();
                        // 停止计时
                        page = 1;
                        vedioTimer.stop();
                        isRecrd = false;
                        showSomeView();
                        mCountDownView.setText("");
                        mBtnShutter.closeButton();
                    }
                }
            }
        }
    };


    @Override
    public void onPreviewing(boolean previewing) {
        mOnPreviewing = previewing;
        mBtnShutter.setEnableOpenned(mOnPreviewing);
    }

    @Override
    public void onSurfaceChanged() {

    }


    @Override
    public void onClick(View v) {
        if(v == iv_back){
            getActivity().finish();
        }else if(v == iv_switch){
            switchCamera();
        }else if(v == mBtnShutter){

            if (!isRecrd){
                if (!qkRecord.canStart()){
                    return;
                }
                isRecrd = true;
                if (startRecord()){
                    vedioTimer.start();
                    hideSomeView();
                }
            } else {
                isRecrd = false;
                onStopRecord();
                page = 1;
                vedioTimer.stop();
                showSomeView();
            }
            if (isRecognizing){
                isRecognizing = false;
            } else {
                isRecognizing = true;
            }

        }else if(v == iv_speed){
            if (ll_speed.getVisibility() == View.GONE){
                ll_speed.setVisibility(View.VISIBLE);
            } else {
                ll_speed.setVisibility(View.GONE);
            }
        }
        else if(v == tv_speed1){
            RecordSpeed.setSpeed(1);
            initTvColor();
            qkRecord.setSpeed(0.5f);
            tv_speed1.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        }else if(v == tv_speed2){
            RecordSpeed.setSpeed(2);
            initTvColor();
            qkRecord.setSpeed(0.75f);
            tv_speed2.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        }else if(v == tv_speed3){
            RecordSpeed.setSpeed(3);
            initTvColor();
            qkRecord.setSpeed(1.0f);
            tv_speed3.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        }else if(v == tv_speed4){
            RecordSpeed.setSpeed(4);
            initTvColor();
            qkRecord.setSpeed(1.5f);
            tv_speed4.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        }else if(v == tv_speed5){
            RecordSpeed.setSpeed(5);
            initTvColor();
            qkRecord.setSpeed(2.0f);
            tv_speed5.setTextColor(ContextCompat.getColor(mContext,R.color.color_quk));
        }else if(v == iv_flash){
            if (CameraUtils.getSupportFlashLight()){
                if (!flash_on) {
                    flash_on = true;
                    iv_flash.setImageResource(R.drawable.flash);
                    qkRecord.setFlashLight(flash_on);
                } else {
                    flash_on = false;
                    iv_flash.setImageResource(R.drawable.flash_close);
                    qkRecord.setFlashLight(flash_on);
                }
            } else {
                Toast.makeText(getActivity(), "当前摄像头不支持闪光灯", Toast.LENGTH_SHORT).show();
            }
        }else if(v == iv_voice){
            if (is_voice){
                is_voice = false;
                qkRecord.setCloseAudio(is_voice);
                iv_voice.setImageResource(R.drawable.voice);
            } else {
                is_voice = true;
                qkRecord.setCloseAudio(is_voice);
                iv_voice.setImageResource(R.drawable.voice_close);
            }
        }else if(v == iv_shake){
            if (is_stabilization){
                is_stabilization = false;
                CameraUtils.setVideoStabilization(is_stabilization);
                iv_shake.setImageResource(R.drawable.shake_close);
            } else {
                is_stabilization = true;
                CameraUtils.setVideoStabilization(is_stabilization);
                iv_shake.setImageResource(R.drawable.shake);
            }
        }
        else if(v == iv_fourk){
            Toast.makeText(mContext, "暂不支持4K",Toast.LENGTH_SHORT).show();
        }else if(v == iv_file1){
            if (isRecrd){
                return;
            }
            if (System.currentTimeMillis() - clickTime < 2000) {
                return;
            }
            clickTime = System.currentTimeMillis();
            NewVideoProcessActivity.startActivityToCreate(getActivity(), list,getVideoOutPath());
        }
    }

    private void initTvColor(){
        tv_speed1.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        tv_speed2.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        tv_speed3.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        tv_speed4.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        tv_speed5.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        ll_speed.setVisibility(View.GONE);
    }

    @Override
    public void ratioChanged(AspectRatioType type) {
        mCurrentRatioType = type;

        if (mCurrentRatioType == AspectRatioType.RATIO_4_3) {
            mCurrentRatio = CameraUtils.Ratio_4_3;
        } else if(mCurrentRatioType ==  AspectRatioType.Ratio_16_9){
            mCurrentRatio = CameraUtils.Ratio_16_9;
        }else{
            mCurrentRatio = CameraUtils.Ratio_1_1;
        }
        Log.d("mCurrentRatio", mCurrentRatio + "" );
        CameraUtils.setCurrentRatio(mCurrentRatio);
        mAspectLayout.setAspectRatio(mCurrentRatio);
        if (mCurrentRatio == CameraUtils.Ratio_1_1 && OritationUtil.getOritation() == 1) {
            mAspectLayout.setPadding(0, dip2px(getActivity(), 57), 0, 0);
        } else if (mCurrentRatio == CameraUtils.Ratio_1_1 && OritationUtil.getOritation() == 2){
            mAspectLayout.setPadding(dip2px(getActivity(), 57), 0, 0, 0);
        } else {
            mAspectLayout.setPadding(0, 0, 0, 0);
        }
        qkRecord.reopenCamera();
    }






    @Override
    public void onClick(float x, float y) {
        surfaceViewClick(x, y);
    }

    @Override
    public void doubleClick(float x, float y) {
    }

    /**
     * 点击SurfaceView
     * @param x x轴坐标
     * @param y y轴坐标
     */
    private void surfaceViewClick(float x, float y) {

        // 设置聚焦区域
        qkRecord.setFocusAres(CameraUtils.getFocusArea((int)x, (int)y,
                mCameraSurfaceView.getWidth(), mCameraSurfaceView.getHeight(), FocusSize));
    }

    /**
     * 查看媒体库中的图片
     */
    private void viewPhoto() {
//        startActivity(new Intent(CameraActivity.this, MediaSelectActivity.class));
    }

    /**
     * 拍照
     */
    private void takePicture() {
        if (!mOnPreviewing) {
            return;
        }
        if (mStorageWriteEnable
                || PermissionUtils.permissionChecking(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            qkRecord.takePicture();
        } else {
            requestStorageWritePermission();
        }
    }

    @Override
    public void onFrameCallback(final ByteBuffer buffer, final int width, final int height) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {

                String date = formatTime2(System.currentTimeMillis());
                String filePath = ParamsManager.StoragePath + "/RecordVideo/" + date + (int)((Math.random() * 9 + 1) * 10000000) + "_0.jpg";

                File file = new File(filePath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    bitmap = BitmapUtils.rotateBitmap(bitmap, 180, true);
                    bitmap = BitmapUtils.flipBitmap(bitmap, true);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                    img.setVisibility(View.VISIBLE);
                    img.setImageBitmap(bitmap);
                    AlpahAnimation(img);

//                    bitmap.recycle();
//                    bitmap = null;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (bos != null) try {
                        bos.close();
                    } catch (IOException e) {
                        // do nothing
                    }
                }
            }
        });
    }


    //拍照后给用户一个反馈的动画
    private void AlpahAnimation(View view) {
        ObjectAnimator translationX = new ObjectAnimator().ofFloat(view, "translationY",0,1000f);
        ObjectAnimator translationY = new ObjectAnimator().ofFloat(view, "translationX",0,0);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);


        AnimatorSet animatorSet = new AnimatorSet();  //组合动画
        animatorSet.playTogether(scaleX,scaleY); //设置动画
        animatorSet.setDuration(1000);  //设置动画时间
        animatorSet.start(); //启动
    }


    public String formatTime2(long dateTime) {
        //yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(dateTime);
        return sdf.format(date);
    }

    public boolean startRecord() {
        if (!checkFreeSize()){
            Toast.makeText(mContext, "内存不足,暂停录制", Toast.LENGTH_SHORT).show();
            isRecrd = false;
            return false;
        }
        activity.stopSensor();

        videoPath = qkRecord.startRecord();

        // 隐藏删除按钮
        mBtnRecordPreview.setVisibility(View.GONE);
        mBtnRecordDelete.setVisibility(View.GONE);

        return true;
    }


    public void onStopRecord() {
        // 停止录制
        stopRecording();

        mCountDownView.setText("");
    }



    // 是否需要等待录制完成再跳转
    private boolean mNeedToWaitStop = false;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PREVIEW) {
            // 时间清0
            mCountDownView.setText(StringUtils.generateMillisTime(0));
            // 复位进度条
            mBtnShutter.setProgress(0);
            // 清除录制按钮分割线
            mBtnShutter.cleanSplitView();
            // 关闭录制按钮
            mBtnShutter.closeButton();
        }
    }

    /**
     * 切换相机
     */
    private void switchCamera() {
        if (!mCameraEnable) {
            requestCameraPermission();
            return;
        }
        if (mCameraSurfaceView != null) {
            flash_on = false;
            iv_flash.setImageResource(R.drawable.flash_close);
            qkRecord.switchCamera();
        }
    }


    private void registerPhoneStateListener() {
        CustomPhoneStateListener customPhoneStateListener = new CustomPhoneStateListener(getActivity());
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }



    //竖屏返回1，横屏返回2
    private int getOritation(){
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        qkRecord.setOritation(ori);
        return ori;
    }

    //录制时隐藏部分view
    private void hideSomeView(){
        iv_switch.setVisibility(View.GONE);
        iv_voice.setVisibility(View.INVISIBLE);
        mRatioView.setVisibility(View.GONE);

        iv_shake.setVisibility(View.INVISIBLE);
        iv_fourk.setVisibility(View.GONE);
        iv_speed.setVisibility(View.GONE);
        ll_speed.setVisibility(View.GONE);
        iv_back.setVisibility(View.GONE);
    }

    //切换部分view
    private void showSomeView(){
        iv_switch.setVisibility(View.VISIBLE);
        iv_voice.setVisibility(View.VISIBLE);
        mRatioView.setVisibility(View.VISIBLE);

        iv_shake.setVisibility(View.VISIBLE);
        iv_fourk.setVisibility(View.VISIBLE);
        iv_speed.setVisibility(View.VISIBLE);
        mBtnRecordPreview.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
    }

    private boolean checkFreeSize(){
        long freeSize = getSDFreeSize();
        Log.i("luyj", "freeSize " + freeSize + "MB");
        return freeSize > 500;
    }

    public long getSDFreeSize(){
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        return (freeBlocks * blockSize)/1024 /1024; //单位MB
    }

    private void addVideo(String path){
        if(path == null || path.length() == 0){
            return;
        }
        File file = new File(path);
        if (file.exists())
        {
            //录像完成后，刷新本地，确保录像可见
            PublicUtils.updateFile(file, getContext());


            VideoProcessBean videoProcessBean = new VideoProcessBean();
            long time = Long.valueOf(getVideoDuration(path));
            videoProcessBean.setStartTime(0);
            videoProcessBean.setPath(path);
            videoProcessBean.setEndTime(time);
            videoProcessBean.setLength(time);
            list.add(videoProcessBean);
            updateFileImage();
        }
    }

    private String getVideoOutPath() {
        String outPath = ConfigureConstants.QUKAN_PATH_PROCESS_VIDEO;
        String time = "short_video" + PublicUtils.getCurrentTime();
        String path = outPath + File.separator + time + ".mp4";
        return path;
    }

    //更新展示文件的三张图
    private void updateFileImage(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //此时已在主线程中，可以更新UI了
                if (list == null || list.size() == 0){
                    return;
                }
                if(options == null){
                    options = new DisplayImageOptions.Builder()
                            .build();
                }
                int i = 0;
                for (VideoProcessBean videoProcessBean : list){

                    i++;
                    if (i == 1){
                        String uri = "file://" + videoProcessBean.getPath();
                        ImageLoader.getInstance().displayImage(uri, iv_file1, options);
                        iv_file1.setVisibility(View.VISIBLE);
                    }
                    if (i == 2){
                        String uri = "file://" + videoProcessBean.getPath();
                        ImageLoader.getInstance().displayImage(uri, iv_file2, options);
                        iv_file2.setVisibility(View.VISIBLE);
                    }
                    if (i == 3){
                        String uri = "file://" + videoProcessBean.getPath();
                        ImageLoader.getInstance().displayImage(uri, iv_file3, options);
                        iv_file3.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}



