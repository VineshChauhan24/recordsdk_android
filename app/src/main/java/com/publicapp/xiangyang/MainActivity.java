package com.publicapp.xiangyang;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cgfay.cainfilter.utils.PermissionUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.ConfigureManagerUtil;
import com.qukan.qkrecordupload.OutPutMessage;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.QkApplication;
import com.qukan.qkrecordupload.bean.VideoEditBean;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;
import com.qukan.qkrecordupload.fileRecord.NewCameraActivity;
import com.qukan.qkrecorduploadsdk.RecordSdk;
import com.qukan.qkrecorduploadsdk.bean.Code;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

import static com.publicapp.xiangyang.FilePicker.FROM_ALBUM;
import static com.qukan.qkrecordupload.PublicUtils.getVideoDuration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button appKeyButton;

    private Button avbutton2;

    private Button btnVideo;

    private EditText etTitel;

    private EditText etUser;

    private boolean appKeyCheck = false;

    // 消息处理句柄
    private Handler msgHandler = new Handler(new MsgHandlerCallback());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // bugly日志
        CrashReport.initCrashReport(getApplicationContext(), "071b16aaad", true);
        //初始化相关配置
        QkApplication.onCreate(getApplicationContext());
        initView();
        boolean edict = RecordSdk.deviceCheckEdict();
        Log.d("设备是否支持短视频", "" + edict);

        //添加sdk初始化监听
        RecordSdk.addListener(msgHandler);

        //申请权限
        mCameraEnable = PermissionUtils.permissionChecking(getApplicationContext(),
                Manifest.permission.CAMERA);
        mRecordSoundEnable = PermissionUtils.permissionChecking(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        mStorageWriteEnable = PermissionUtils.permissionChecking(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //设置字幕logo
        ConfigureManagerUtil.putRecordLogo(this, R.drawable.police_blue);


        //添加视频生成状态的监听
        OutPutMessage.instance().setCallBack(new OutPutMessage.VideoEdictCallBack() {
            @Override
            public void onDraftGet(VideoEditBean bean) {
                if(bean != null){
                    Log.d("MainActivity","生成草稿文件回调onDraftGet");
                }

            }

            @Override
            public void onVideoOutPath(String outPath) {
                if(outPath != null){
                    Log.d("MainActivity","生成短视频文件回调onVideoOutPath：" + outPath);
                }
            }

            @Override
            public void onVideoRecordPath(String outPath) {
                if(outPath != null){
                    Log.d("MainActivity","生成录制文件回调onVideoOutPath：" + outPath);
                }
            }

            @Override
            public void onVideosGet(final OutPutMessage.VideosGetCallback callBack, BaseFragment fragment) {
                FilePicker filePicker = new FilePicker(fragment);
                filePicker.setMimeType(PictureMimeType.ofAll());
                filePicker.start();

                fragment.setActivityResultGet(new BaseFragment.ActivityResultGet() {
                    @Override
                    public void onActivityResultGet(int requestCode, int resultCode, Intent data) {
                        ArrayList<VideoProcessBean> list = results(requestCode,resultCode,data);

                        try{
                            callBack.onVideoList(list);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        appKeyButton = findViewById(R.id.appKeyButton);
        appKeyButton.setOnClickListener(this);

        avbutton2 = findViewById(R.id.button2);
        avbutton2.setOnClickListener(this);

        btnVideo = findViewById(R.id.btn_video);
        btnVideo.setOnClickListener(this);

        etTitel = findViewById(R.id.editText);
        etUser = findViewById(R.id.et_user);
    }

    @Override
    public void onClick(View v) {
        if (v == appKeyButton) {
            String user = etUser.getText().toString();
            String appkey = etTitel.getText().toString();
            if (user.isEmpty() || appkey.isEmpty()) {
                Toast.makeText(this, "用户名或appkey不得为空", Toast.LENGTH_LONG).show();
                return;
            }
            RecordSdk.setAppKey(appkey, user);
        } else if (v == avbutton2) {
            if (appKeyCheck) {

                boolean startAct = true;

                //⑧申请录制音频的动态权限
                if (!mCameraEnable || !mRecordSoundEnable || !mStorageWriteEnable) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
                    startAct = false;
                }
                if (startAct) {
                    Intent intent = new Intent(MainActivity.this, NewCameraActivity.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "请先设置appKey", Toast.LENGTH_LONG).show();
            }
        } else if (v == btnVideo) {
            if (appKeyCheck) {
                //⑧申请存储权限
                if (!mStorageWriteEnable || !mRecordSoundEnable) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_STORAGE);
                } else {
                    FilePicker filePicker = new FilePicker(this);
                    filePicker.setMimeType(PictureMimeType.ofAll());
                    filePicker.start();
                }

            } else {
                Toast.makeText(this, "请先设置appKey", Toast.LENGTH_LONG).show();
            }

        }
    }

    //判断设备权限
    private static final int REQUEST_CAMERA = 0x01;
    private static final int REQUEST_STORAGE = 0x02;
    private static final int REQUEST_RECORD = 0x03;
    // 权限标志
    private boolean mCameraEnable = false;
    private boolean mRecordSoundEnable = false;
    private boolean mStorageWriteEnable = false;

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
                    //⑧申请录制视频的动态权限
                }
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mRecordSoundEnable = true;
                    //⑧申请录制音频的动态权限
                }
                if (grantResults.length > 0
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    mStorageWriteEnable = true;
                    //⑧申请存储权限
                }
                //⑧申请录制音频的动态权限
                if (mCameraEnable && mRecordSoundEnable && mStorageWriteEnable) {
                    Intent intent = new Intent(MainActivity.this, NewCameraActivity.class);
                    startActivity(intent);
                }

                break;
            // 存储权限
            case REQUEST_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStorageWriteEnable = true;
                }
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mRecordSoundEnable = true;
                }
                //⑧申请录制音频的动态权限
                if (mRecordSoundEnable && mStorageWriteEnable) {
                    FilePicker filePicker = new FilePicker(this);
                    filePicker.setMimeType(PictureMimeType.ofAll());
                    filePicker.start();
                }

                break;
        }
    }


    int TARGET = 0;
    public static int DEFAULT_TIME = 3000;//图片默认的播放时间

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<VideoProcessBean> list = results(requestCode,resultCode,data);

        if(list.size() > 0)
        {
            NewVideoProcessActivity.startActivityToCreate(MainActivity.this, list);
        }
    }
    private ArrayList<VideoProcessBean> results(int requestCode, int resultCode, Intent data) {
        ArrayList<VideoProcessBean> list = new ArrayList<>();

        if (requestCode == FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);

                if (selectList != null && selectList.size() > 0) {
                    for (int i = 0; i < selectList.size(); i++) {
                        LocalMedia media = selectList.get(i);
                        String path = media.getPath();
                        VideoProcessBean bean = getProgress(path);

                        if (bean != null) {
                            list.add(bean);
                        }
                    }
                }
                if (list.size() == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.add_video_error), Toast.LENGTH_LONG).show();
                    return list;
                }

            }
        }
        return list;

    }

        // 事件侦听的处理类
    private class MsgHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
//            QLog.i("msg.what=%d,bundle=%s", msg.what, msg.getData());

            // 将已经处理完的文件删除
            if (msg.what == RecordSdk.MSG_APPKEY_CHECK) {
                Bundle bundle = msg.getData();
                if (Code.RESULT_OK.equals(bundle.getString(Code.RESULT_CODE))) {
                    Toast.makeText(getApplicationContext(), "APPKey成功", Toast.LENGTH_SHORT).show();
                    appKeyCheck = true;
                } else {
                    Toast.makeText(getApplicationContext(), "APPKey失败 : " + bundle.getString(Code.RESULT_CODE), Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    }

    private VideoProcessBean getProgress(String path) {

        if (PublicUtils.isVideo(path) || PublicUtils.isImage(path)) {
            VideoProcessBean videoProcessBean = new VideoProcessBean();
            videoProcessBean.setStartTime(0);
            videoProcessBean.setPath(path);

            boolean isAdd = false;
            if (PublicUtils.isVideo(path)) {
                boolean canEdict = RecordSdk.checkEdict(path);
                if (canEdict) {
                    isAdd = true;
                    long time = (long) getVideoDuration(path);
                    videoProcessBean.setEndTime(time);
                    videoProcessBean.setLength(time);
                } else {
                    isAdd = false;
                    Log.e("视频不支持编辑", "视频不支持编辑");
                }

                Log.d("isH264", "isH264:" + canEdict);
            } else if (PublicUtils.isImage(path)) {
                videoProcessBean.setEndTime(DEFAULT_TIME);
                videoProcessBean.setLength(20000);
                isAdd = true;
            }
            if (isAdd) {
                return videoProcessBean;
            }
        }
        return null;
    }


}
