package com.qukan.qkrecordupload.compop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qukan.qkrecordupload.R;

import lombok.Setter;

/**
 * Created by Administrator on 2017/2/14 0014.
 * 一个自定义的可复用的弹出对话框
 */
public class CommonPop extends BasePopupWindow {

    TextView tvOk;

    TextView tvCancel;

    TextView tvTitle;

    TextView tvMsg;

    TextView tvDivide;

    @Setter
    OnClickListenerPup onClickListenerPup;

    public CommonPop(Context context)
    {
        super(context);
    }

    @Override
    public View setPopview()
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_common, null);
        initView(view);
        return view;
    }

    private void initView(View view){
        tvOk = (TextView) view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(this);

        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);

        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        tvMsg = (TextView) view.findViewById(R.id.tv_msg);

        tvDivide = (TextView) view.findViewById(R.id.tv_divide);
    }

    public void setTvMsg(String msg) {
        tvMsg.setText(msg);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTvCancel(String cancel) {
        tvCancel.setText(cancel);
    }

    public void setTvSure(String sure) {
        tvOk.setText( sure);
    }

    public void showSureButton(boolean isShow) {
        if (isShow) {
            tvOk.setVisibility(View.VISIBLE);
            tvDivide.setVisibility(View.VISIBLE);
        } else {
            tvOk.setVisibility(View.GONE);
            tvDivide.setVisibility(View.GONE);
        }
    }

    public void showCancelButton(boolean isShow) {
        if (isShow) {
            tvCancel.setVisibility(View.VISIBLE);
            tvDivide.setVisibility(View.VISIBLE);
        } else {
            tvCancel.setVisibility(View.GONE);
            tvDivide.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == tvCancel)
        {
            if (onClickListenerPup != null) {
                onClickListenerPup.onCancelClick();
            }
            dismiss();
        } else if (v == tvOk) {
            if (onClickListenerPup != null) {
                onClickListenerPup.onSureClick();
            }
            dismiss();
        }
    }
}
