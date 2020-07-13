package com.qukan.qkrecordupload.fileRecord;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.fileCut.RecordHelper;
import com.qukan.qkrecorduploadsdk.RecordContext;
import com.qukan.qkrecorduploadsdk.RecordSdk;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectSystemService;
import org.droidparts.util.L;

import java.text.SimpleDateFormat;
import java.util.Date;

public class fileRecordActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback, SensorEventListener {

    private Button btnStart;

    private Button btnPause;

    private Button btnStop;

    private Button btnFlash;

    private Button btnCamera;

    private Button btnFocus;

    private Button btnPicture;

    private Button btnAudio;

    private FrameLayout ffaceview;// 显示视频的控件

    @InjectSystemService(value = Context.STORAGE_SERVICE)
    private StorageManager sm;

    private int screen;

    private boolean flash;
    private int swichCamera;

    private boolean autoFocus;

    private SurfaceView svLive;
    private SurfaceHolder surfaceHolder;

    // 定义加速度传感器
    private SensorManager sensorManager;

    private boolean autoFocusing = false;  // 正在自动中
    private boolean mInitialized = false;
    private float mLastX, mLastY, mLastZ;

    private volatile boolean isActive;
    private volatile boolean isRecord;
    private volatile boolean isStart;
    private volatile boolean isAudio;

    // 消息处理句柄
    private Handler msgHandler = new Handler(new MsgHandlerCallback());

    //手动聚焦显示大小
    private DisplayMetrics dm;

    @Override
    public void onPreInject() {
        setContentView(R.layout.activity_file_record);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        screen = getIntent().getExtras().getInt("screenOrientation");

        if (screen == RecordContext.SCREEN_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        swichCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
        flash = false;
        autoFocus = true;

        //获得传感器管理器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //获取显示大小
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //
        btnPause.setClickable(false);
        btnStop.setClickable(false);

        btnStart.setTextColor(Color.RED);
        btnPause.setTextColor(Color.GRAY);
        btnStop.setTextColor(Color.GRAY);

        isActive = true;
        isRecord = false;
        isStart = false;
        isAudio = true;
    }

    private void initView(){
        btnStart = (Button) findViewById(R.id.start);
        btnStart.setOnClickListener(this);

        btnPause = (Button) findViewById(R.id.pause);
        btnPause.setOnClickListener(this);

        btnStop = (Button) findViewById(R.id.stop);
        btnStop.setOnClickListener(this);

        btnFlash = (Button) findViewById(R.id.flash);
        btnFlash.setOnClickListener(this);

        btnCamera = (Button) findViewById(R.id.camera);
        btnCamera.setOnClickListener(this);

        btnFocus = (Button) findViewById(R.id.focus);
        btnFocus.setOnClickListener(this);

        btnPicture = (Button) findViewById(R.id.picture);
        btnPicture.setOnClickListener(this);

        btnAudio = (Button) findViewById(R.id.audio);
        btnAudio.setOnClickListener(this);

        ffaceview = (FrameLayout) findViewById(R.id.surfaceview);
    }

    private String getVelume() {
        try {
            Class<?>[] paramTypes = {};
            Object[] params = {};
            String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", paramTypes).invoke(sm, params);
            boolean bTrue = false;
            int index = 0;
            for (int i = 0; i < paths.length; i++) {
                String status = (String) sm.getClass().getMethod("getVolumeState", String.class).invoke(sm, paths[i]);
                if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
                    return paths[i];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "/";
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecordSdk.addListener(msgHandler);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);

        initSurfaceView();
    }

    private void initSurfaceView() {
        if (svLive != null) {
            ffaceview.removeView(svLive);
        }
        //svLive = (SurfaceView) findViewById(R.id.video_view);

        svLive = new SurfaceView(this);
        surfaceHolder = svLive.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

        // 添加预览使用的surface view
        ffaceview.addView(svLive, 0);
    }

    @Override
    protected void onPause() {
        btnStart.setClickable(true);
        btnPause.setClickable(false);
        btnStop.setClickable(false);

        btnStart.setTextColor(Color.RED);
        btnPause.setTextColor(Color.GRAY);
        btnStop.setTextColor(Color.GRAY);

        RecordContext.stopCamera();

        RecordSdk.removeListener(msgHandler);
        sensorManager.unregisterListener(this);
        super.onPause();

        isStart = false;
    }

    @Override
    public void onClick(View v) {
        if (v == btnStart) {
            String volume = getVelume();
            boolean succ = RecordContext.startRecord(isAudio, volume, "qukantool/record");

            Toast.makeText(getApplicationContext(), "开始文件录像" + String.valueOf(succ), Toast.LENGTH_SHORT).show();

            if (succ) {
                btnStart.setTextColor(Color.GRAY);
                btnPause.setTextColor(Color.RED);
                btnStop.setTextColor(Color.RED);
                btnStart.setClickable(false);
                btnPause.setClickable(true);
                btnStop.setClickable(true);

                isRecord = true;
            }
        } else if (v == btnPause) {
            RecordContext.pauseRecord();

            btnStart.setClickable(true);
            btnPause.setClickable(false);
            btnStop.setClickable(true);

            btnStart.setTextColor(Color.RED);
            btnPause.setTextColor(Color.GRAY);
            btnStop.setTextColor(Color.RED);

            isRecord = false;
        } else if (v == btnStop) {
            RecordContext.stopRecord();

            Toast.makeText(getApplicationContext(), "结束文件录像", Toast.LENGTH_SHORT).show();

            btnStart.setClickable(true);
            btnPause.setClickable(false);
            btnStop.setClickable(false);

            btnStart.setTextColor(Color.RED);
            btnPause.setTextColor(Color.GRAY);
            btnStop.setTextColor(Color.GRAY);

            isRecord = false;
        } else if (v == btnFlash) {
            flash = !flash;
            boolean succ = RecordContext.switchFlash(flash);
        } else if (v == btnCamera) {
            if (swichCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
                swichCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                swichCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
            }

            RecordContext.switchCamera(swichCamera);

            Toast.makeText(getApplicationContext(), "摄像头切换", Toast.LENGTH_SHORT).show();
        } else if (v == btnPicture) {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qukan_image/" + formatTime2(System.currentTimeMillis()) + ".jpg";
            boolean succ = RecordContext.takePicture(filePath);

            Toast.makeText(getApplicationContext(), "抓取图片：" + filePath + " , succ :" + String.valueOf(succ), Toast.LENGTH_SHORT).show();
        } else if (v == btnFocus) {
            autoFocus = !autoFocus;
            RecordContext.setAutoFocus(autoFocus, autoFocusCallback);

            if (autoFocus) {
                btnFocus.setText("手动聚焦");
                Toast.makeText(getApplicationContext(), "自动聚焦", Toast.LENGTH_SHORT).show();
            } else {
                btnFocus.setText("自动聚焦");
                Toast.makeText(getApplicationContext(), "手动聚焦", Toast.LENGTH_SHORT).show();
            }
        } else if (v == btnAudio) {
            isAudio = !isAudio;
            RecordContext.switchAudio(isAudio);

            if (isAudio) {
                btnAudio.setText("关闭声音");
            } else {
                btnAudio.setText("打开声音");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:

                if (autoFocus) {
                    return true;
                }
                float x = event.getX();
                float y = event.getY();

                // 这个callback可以设置,也可以不设置
                RecordContext.manulFocus(x, y, dm.widthPixels, dm.heightPixels, autoFocusCallback);
                break;
        }
        return true;
    }

    public String formatTime2(long dateTime) {
        //yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(dateTime);
        return sdf.format(date);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;

        if (isStart) {
            return;
        }

        isStart = RecordContext.startCamera(surfaceHolder, CameraInfo.instance().getCameraSizeType(), CameraInfo.instance().getVideoDataRate(), swichCamera, screen, false);


        // 设置预览的画面大小
        {
            // 获取屏幕的尺寸
            Pair<Integer, Integer> res = ScreenResolution.getResolution(this);
            int windowWidth = res.first.intValue();
            int windowHeight = res.second.intValue();
            float windowRatio = windowWidth / (float) windowHeight;

            // 获取视频的尺寸
            int videoWidth = RecordContext.getMediaInfo().videoDstWidth;
            int videoHeight = RecordContext.getMediaInfo().videoDstHeight;
            float videoRatio = ((float) (videoWidth)) / videoHeight;

            // 显示的尺寸,使用zoom的方案
            int displayWidth = windowRatio > videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
            int displayHeight = windowRatio < videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(displayWidth, displayHeight, Gravity.CENTER);

            surfaceHolder.setFixedSize(videoWidth, videoHeight);
            svLive.setLayoutParams(lp);

            L.i("windowWidth=%d,windowHeight=%d,v                                                                                                                                                                                                                                  ideoWidth=%d,videoHeight=%d,lp.width=%d,lp.height=%d",
                    windowWidth, windowHeight, videoWidth, videoHeight, lp.width, lp.height);

            RecordHelper.getInstance().init(RecordContext.getMediaInfo().videoDstWidth, RecordContext.getMediaInfo().videoDstHeight, windowWidth, windowHeight);
            RecordHelper.getInstance().clearBmpLogo();
            //处于性能考虑，1080P的分辨率最好不要设置logo
            if(RecordContext.getMediaInfo().videoDstWidth <= 1280 && RecordContext.getMediaInfo().videoDstHeight <= 1280){
                RecordHelper.getInstance().drawLogoBitmap();
            }

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        svLive = null;
        surfaceHolder = null;
    }

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusing = false;

            if (success) {
                Toast.makeText(getApplicationContext(), "auto focus succ", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!autoFocus) {
            return;
        }

        // 如果正在聚焦,那么返回
        if (autoFocusing) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }
        float deltaX = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if (deltaX > .5 || deltaY > .5 || deltaZ > .5) {
            autoFocusing = true;
            RecordContext.setAutoFocus(autoFocus, null);
        }

        mLastX = x;
        mLastY = y;
        mLastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 事件侦听的处理类
    private class MsgHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            return true;
        }
    }
}