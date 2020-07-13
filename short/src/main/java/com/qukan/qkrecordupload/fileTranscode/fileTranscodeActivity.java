package com.qukan.qkrecordupload.fileTranscode;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.RnToast;
import com.qukan.qkrecordupload.SdkUtils;
import com.qukan.qkrecorduploadsdk.RecordSdk;
import com.qukan.qkrecorduploadsdk.bean.Code;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectView;

import java.util.ArrayList;

public class fileTranscodeActivity extends Activity implements View.OnClickListener {

    private Button btnVideo;

    private RecyclerView rcFileList;

    //button7
    private Button button7;

    private AdapterTranscodeList adapterFileList;

    // 消息处理句柄
    private Handler msgHandler = new Handler(new MsgHandlerCallback());

    public final int VIDEO_TRANSCODE_REQUEST = 1;

    @Override
    public void onPreInject() {
        setContentView(R.layout.activity_file_transcode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        adapterFileList = new AdapterTranscodeList(this);
        rcFileList.setAdapter(adapterFileList);
        rcFileList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initView() {
        btnVideo = (Button) findViewById(R.id.btn_video1);
        btnVideo.setOnClickListener(this);

        rcFileList = (RecyclerView) findViewById(R.id.rc_file_list1);
        rcFileList.setOnClickListener(this);

        button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecordSdk.addListener(msgHandler);
    }

    @Override
    protected void onPause() {
        RecordSdk.removeListener(msgHandler);

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v == btnVideo) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/**");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "选择一个文件转码"), VIDEO_TRANSCODE_REQUEST);
        } else if (v == button7) {
            Intent intent = new Intent(fileTranscodeActivity.this, BitSettingActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 视频选择完成回调方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == VIDEO_TRANSCODE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
//
                String path = SdkUtils.getRealPathFromURI(uri);
                ArrayList<String> paths = new ArrayList<>();
                paths.add(path);
                adapterFileList.refreshData(paths);
            }
        }
    }

    // 事件侦听的处理类
    private class MsgHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what != RecordSdk.MSG_TRANSCODE_PROGRESS)

                //上传进度
                if (msg.what == RecordSdk.MSG_TRANSCODE_PROGRESS) {
                    Bundle bundle = msg.getData();
                    if (Code.RESULT_OK.equals(bundle.getString(Code.RESULT_CODE))) {
                        int Progress = bundle.getInt("Progress");
                        String fileName = bundle.getString("inFilePath");

                        //
                        adapterFileList.refreshPercent(fileName, Progress);
                    }
                }

            if (msg.what == RecordSdk.MSG_TRANSCODE_COMPLETE) {
                Bundle bundle = msg.getData();
                String fileName = bundle.getString("inFilePath");
                RnToast.showToast(getApplicationContext(), "文件转码完成 ： " + fileName + " : " + bundle.getString(Code.RESULT_CODE));

                adapterFileList.refreshEnd(fileName);
            }

            if (msg.what == RecordSdk.MSG_TRANSCODE_CANCEL) {
                Bundle bundle = msg.getData();
                if (Code.RESULT_OK.equals(bundle.getString(Code.RESULT_CODE))) {
                    String fileName = bundle.getString("inFilePath");
                    RnToast.showToast(getApplicationContext(),"文件转码取消 ： " + fileName);
                }
            }

            return true;
        }
    }
}
