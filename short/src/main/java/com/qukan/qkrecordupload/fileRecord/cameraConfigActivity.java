package com.qukan.qkrecordupload.fileRecord;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecorduploadsdk.RecordContext;


public class cameraConfigActivity extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    private Button btnOk;

    private Button btnCannel;

    private EditText bitrate;

    private RadioGroup group;

    private RadioButton rb1;

    private RadioButton rb2;

    private RadioButton rb3;

    private int isCheckedId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_camera_config);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        init();

        switch (CameraInfo.instance().getCameraSizeType())
        {
            case RecordContext.CAMERA_SIZE_640x480:
                rb1.setChecked(true);
                isCheckedId = rb1.getId();
                break;

            case RecordContext.CAMERA_SIZE_1280x720:
                rb2.setChecked(true);
                isCheckedId = rb2.getId();
                break;

            case RecordContext.CAMERA_SIZE_1920x1080:
                rb3.setChecked(true);
                isCheckedId = rb3.getId();
                break;
        }

        bitrate.setText(String.valueOf(CameraInfo.instance().getVideoDataRate()));

        group.setOnCheckedChangeListener(this);
    }

    private void initView(){
        btnOk = (Button) findViewById(R.id.button4);
        btnOk.setOnClickListener(this);

        btnCannel = (Button) findViewById(R.id.button5);

        bitrate = (EditText) findViewById(R.id.editText2);

        group = (RadioGroup) findViewById(R.id.group);

        rb1 = (RadioButton) findViewById(R.id.radioButton1);

        rb2 = (RadioButton) findViewById(R.id.radioButton2);

        rb3 = (RadioButton) findViewById(R.id.radioButton3);
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        isCheckedId = checkedId;
    }

    @Override
    public void onClick(View v) {
        if (v == btnOk)
        {
            check();
            CameraInfo.instance().setVideoDataRate(Integer.valueOf(bitrate.getText().toString()));
            finish();
        }
        else if(v == btnCannel)
        {
            finish();
        }
    }

    private void check()
    {

        if (isCheckedId == rb1.getId())
        {
            CameraInfo.instance().setCameraSizeType(2);
        }
        else if (isCheckedId ==rb2.getId())
        {
            CameraInfo.instance().setCameraSizeType(8);
        }
        else if (isCheckedId ==rb3.getId())
        {
            CameraInfo.instance().setCameraSizeType(9);
        }
    }

    private void init()
    {
        rb1.setChecked(true);
        rb2.setChecked(false);
        rb3.setChecked(false);
    }
}
