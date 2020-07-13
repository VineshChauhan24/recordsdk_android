package com.qukan.qkrecordupload.fileTranscode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qukan.qkrecordupload.R;

public class BitSettingActivity extends AppCompatActivity {

    EditText etWidth;
    EditText etHeight;
    EditText etBitrate;
    Button btnOk;
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bit_setting);

        etWidth=(EditText)findViewById(R.id.et_width);
        etHeight=(EditText)findViewById(R.id.et_height);
        etBitrate=(EditText)findViewById(R.id.et_bitrate);
        btnCancel=(Button)findViewById(R.id.btn_cancel);
        btnOk=(Button)findViewById(R.id.btn_ok);

        etWidth.setText(String.valueOf(TranscodeInfo.instance().getWidth()));
        etHeight.setText(String.valueOf(TranscodeInfo.instance().getHeight()));
        etBitrate.setText(String.valueOf(TranscodeInfo.instance().getBitRate()));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etWidth.getText().toString().isEmpty() && etWidth.getText().toString() != null) {
                    TranscodeInfo.instance().setWidth(Integer.valueOf(etWidth.getText().toString()).intValue());
                }

                if (!etHeight.getText().toString().isEmpty() && etHeight.getText().toString() != null) {
                    TranscodeInfo.instance().setHeight(Integer.valueOf(etHeight.getText().toString()).intValue());
                }

                if (!etBitrate.getText().toString().isEmpty() && etBitrate.getText().toString() != null) {
                    TranscodeInfo.instance().setBitRate(Integer.valueOf(etBitrate.getText().toString()).intValue());
                }

                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
