package com.qukan.qkrecordupload.compop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qukan.qkrecordupload.R;

/**
 * Created by Administrator on 2017/1/12 0012.
 */
public class CommonSelectFile extends BasePopupWindow {


    private TextView tvChoose;

    private TextView tvCancel;

    private RelativeLayout rl_popup_file_select;

    private ChooseFileCallback callback;

    public CommonSelectFile(Context context, ChooseFileCallback chooseFileCallback) {
        super(context);
        callback = chooseFileCallback;
    }

    @Override
    public View setPopview() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_common_file_select, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);

        tvChoose = view.findViewById(R.id.tv_choose_file);
        tvChoose.setOnClickListener(this);

        rl_popup_file_select = view.findViewById(R.id.rl_popup_file_select);
        rl_popup_file_select.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tvChoose) {
            // 选择手机相册文件
            callback.chooseFile();
            dismiss();
        } else if (v == tvCancel) {
            dismiss();
        } else {
            dismiss();
        }

    }
}
