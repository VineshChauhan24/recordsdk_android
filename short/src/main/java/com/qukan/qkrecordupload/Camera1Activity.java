package com.qukan.qkrecordupload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cgfay.cainfilter.camerarender.ParamsManager;
import com.cgfay.cainfilter.qukan.YuvData;
import com.cgfay.cainfilter.utils.TextureRotationUtils;
import com.cgfay.cainfilter.utils.AspectFrameLayout;
import com.qukan.qkrecordupload.bean.TransferEffect;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.qkCut.QKCompleteMovie;
import com.qukan.qkrecorduploadsdk.decoder.VideoPlayer;

import org.droidparts.activity.Activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;

public class Camera1Activity extends Activity implements View.OnClickListener{

    private static final String TAG = "Camera1Activity";
    private static final boolean VERBOSE = true;
    private static final int REQUEST_PREVIEW = 0x200;

    private static final int REQUEST_CAMERA = 0x01;
    private static final int REQUEST_STORAGE = 0x02;
    private static final int REQUEST_RECORD = 0x03;
    private static final int REQUEST_LOCATION = 0x04;

    private VideoPlayer videoPlayer;
    private Button showImg;
    private Button play;

    // 预览部分
    private AspectFrameLayout mAspectLayout;

    // 主线程Handler
    private Handler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 持有当前上下文
        ParamsManager.context = this;
        String phoneName = Build.MODEL;
        if (phoneName.toLowerCase().contains("bullhead")
                || phoneName.toLowerCase().contains("nexus 5x")) {
            TextureRotationUtils.setBackReverse(true);
            ParamsManager.mBackReverse = true;
        }

        mMainHandler = new Handler(getMainLooper());
        initView();
    }
    @Override
    public void onPreInject() {
        Log.d(TAG, "onPreInject");
        setContentView(R.layout.activity_camera);
    }
    private void initView() {
        mAspectLayout = findViewById(R.id.layout_aspect);

//        String path = "/mnt/sdcard/1.mp4";
//        videoPlayer = new VideoPlayer(this);
//        PlayInfo info = new PlayInfo();
//        info.setPath(path);
//        info.setSoftStartTime(0);
//        info.setSoftEndTime(4);
//        info.setStartTime(0);
//        info.setEndTime(4);
////        info.setSpeed(1);
////        info.setSpeed(2);
//        info.setUseOrientation(1);
//
//        PlayInfo info2 = new PlayInfo();
//        info2.setPath(path);
//        info2.setSoftStartTime(0);
//        info2.setSoftEndTime(4);
//        info2.setStartTime(4);
//        info2.setEndTime(8);
//        info2.setType(5);
//        info2.setUseOrientation(2);
//
//
//        PlayInfo info3 = new PlayInfo();
//        info3.setPath(path);
//        info3.setSoftStartTime(0);
//        info3.setSoftEndTime(4);
//        info3.setStartTime(8);
//        info3.setEndTime(12);
//        info3.setType(5);
//        info3.setUseOrientation(2);
//        ArrayList<PlayInfo> list = new ArrayList<>();
//        list.add(info);
//        list.add(info2);
//        list.add(info3);
//        videoPlayer.setLocalFiles(list);
//        videoPlayer.startPlayerWithTime(0);
//        mAspectLayout.addView(videoPlayer.getVideoSurface());
//        mAspectLayout.requestLayout();

        showImg = findViewById(R.id.btn_setting);
        showImg.setOnClickListener(this);
        play = findViewById(R.id.btn_setting2);
        play.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        registerHomeReceiver();
        Log.d(TAG,"onResume");
        if(complete != null){
            complete.setHomeKey(false);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterHomeReceiver();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onDestroy() {
        if(videoPlayer != null){
            videoPlayer.stopPlayer();
        }
        // 在停止时需要释放上下文，防止内存泄漏
        ParamsManager.context = null;
        super.onDestroy();
    }

    private void registerHomeReceiver() {
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomePressReceiver, homeFilter);
    }

    private void unRegisterHomeReceiver() {
        unregisterReceiver(mHomePressReceiver);
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
                // 当点击了home键时需要停止预览，防止后台一直持有相机
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    // 停止录制
                    Log.d(TAG,"当点击了home键时");
                    if(complete != null) {
                        complete.setHomeKey(true);
                    }
                }
            }
        }
    };




    @Override
    public void onClick(View v) {
        if (v == showImg){
//            showYuvBuffer();
            startRecording();
//            videoPlayer.play();
        } else if (v == play){
//            videoPlayer.pause();
        }
    }
    QKCompleteMovie complete;
    public void startRecording(){
        String path = "/mnt/sdcard/1.mp4";
        String outpath = "/mnt/sdcard/34.mp4";
        VideoProcessBean info = new VideoProcessBean();
        info.setPath(path);
        info.setRecordStartTime(0);
        info.setRecordEndTime(4);
        info.setStartTime(0);
        info.setEndTime(4);
//        info.setSpeed(1);
        info.setUseOrientation(1);
        info.setTransferEffect(TransferEffect.getDefaultTrans());
        ArrayList<VideoProcessBean> list = new ArrayList<>();
        list.add(info);
        complete = new QKCompleteMovie(this,1280,720,outpath,list);
        mAspectLayout.addView(complete.getComplete().getVideoSurface());
        mAspectLayout.requestLayout();
//        complete.setCompleteControl(new CompleteControl() {
//            @Override
//            public void onComplete() {
//                Log.d(TAG,"onComplete");
//            }
//
//            @Override
//            public void progress(double progress) {
//                Log.d(TAG,"progress:" + progress);
//            }
//
//            @Override
//            public void onStop() {
//                Log.d(TAG,"onStoponStoponStoponStoponStop");
//            }
//
//            @Override
//            public void getBitmap(long time) {
//                return ;
//            }
//        });
        complete.setCompleteControl(null);

//        String path = "/mnt/sdcard/1.mp4";
//
//        PlayInfo info = new PlayInfo();
//        info.setPath(path);
//        info.setSoftStartTime(0);
//        info.setSoftEndTime(4);
//        info.setStartTime(0);
//        info.setEndTime(4);
////        info.setSpeed(1);
//        info.setUseOrientation(1);
//
//        PlayInfo info2 = new PlayInfo();
//        info2.setPath(path);
//        info2.setSoftStartTime(0);
//        info2.setSoftEndTime(4);
//        info2.setStartTime(4);
//        info2.setEndTime(8);
//        info2.setType(5);
//        info2.setUseOrientation(2);
//        ArrayList<PlayInfo> list = new ArrayList<>();
//        list.add(info);
//        list.add(info2);
//        complete.setLocalFiles(list);
//        complete.startPlayerWithTime(0);

    }
    public void showYuvBuffer(){
        byte[] yData = getBufferInPath("y.yuv");
        byte[] uvData = getBufferInPath("uv.yuv");
//        byte[] uData = new byte[uvData.length/2];
//        byte[] vData = new byte[uvData.length/2];
//        for(int i = 0;i<uvData.length;i = i + 2){
//            uData[i/2] = uvData[i+1];
//            vData[i/2] = uvData[i];
//        }
        byte[]yuv = new byte[yData.length + uvData.length];
        System.arraycopy(yData,0,yuv,0,yData.length);
        System.arraycopy(uvData,0,yuv,yData.length,uvData.length);
        YuvData data = new YuvData();

        data.setOrientation(0);
        data.setSize(uvData.length + yData.length);
        data.setTime(0);
        data.setPlayerpts(0);
        data.setPts(0);
        data.setYuvBuf(yuv);


        data.setDecodeWidth(1280);

        data.setWidth(1280);
        data.setHeight(720);
//        data.setFormat(COLOR_FormatYUV420SemiPlanar); //GL420Filter
        data.setColorFormat(COLOR_FormatYUV420Planar);  //GLYUVFilter
        videoPlayer.draw(data);

    }


    private byte[] getBufferInPath(String name){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        int readSize = 0;
        byte[] data = null;
        try {
            inputStream = getAssets().open(name);
            byte[] bytes = new byte[1024 * 8];

            while ((readSize = inputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0, readSize);
            }
            byteArrayOutputStream.flush();
            data = byteArrayOutputStream.toByteArray();


            byteArrayOutputStream.reset();
            inputStream.close();
            inputStream = null;

            SystemClock.sleep(100);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
}
