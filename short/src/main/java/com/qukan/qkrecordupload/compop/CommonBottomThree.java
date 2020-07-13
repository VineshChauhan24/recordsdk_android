package com.qukan.qkrecordupload.compop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qukan.qkrecordupload.R;

/**
 * 一个通用的底部弹出窗
 */
public class CommonBottomThree extends BasePopupWindow {

    TextView tvButton1;

    TextView tvButton2;

    TextView tvCancel;

    RelativeLayout rl_popup;

    OnClickListenerBottomPup onClickListenerBottomPup;

    public CommonBottomThree(Context context) {
        super(context);
    }

    @Override
    public View setPopview() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_common_bottom_three, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvButton1 = view.findViewById(R.id.tv_button1);
        tvButton1.setOnClickListener(this);
        tvButton2 = view.findViewById(R.id.tv_button2);
        tvButton2.setOnClickListener(this);

        tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);

        rl_popup = view.findViewById(R.id.rl_popup);
        rl_popup.setOnClickListener(this);

    }

    public void setButtonCancelColor(int color) {
        tvCancel.setTextColor(color);
    }

    public void setButtonOneColor(int color) {
        tvButton1.setTextColor(color);
    }

    public void setTvButton1(String text) {
        tvButton1.setText(text);
    }

    public void setTvButton2(String text) {
        tvButton2.setText(text);
    }

    public void setListener(OnClickListenerBottomPup onClickListenerBottomPup) {
        this.onClickListenerBottomPup = onClickListenerBottomPup;
    }

    public void setClickCancleListener(OnClickCancelLinstener onClickCancelLinstener) {
        this.onClickCancelLinstener = onClickCancelLinstener;
    }

    public interface OnClickListenerBottomPup {
        public void onButton1Click(CommonBottomThree commonBottomThree);

        public void onButton2Click(CommonBottomThree commonBottomThree);

        public void onButton3Click(CommonBottomThree commonBottomThree);
    }

    private OnClickCancelLinstener onClickCancelLinstener;

    public interface OnClickCancelLinstener {
        public void onClickCancel(CommonBottomThree commonBottomThree);
    }

    @Override
    public void onClick(View v) {
        if (v == tvButton1) {
            if (onClickListenerBottomPup != null) {
                onClickListenerBottomPup.onButton1Click(this);
            }
        } else if (v == tvButton2) {
            if (onClickListenerBottomPup != null) {
                onClickListenerBottomPup.onButton2Click(this);
            }
        } else if (v == tvCancel) {
            if (onClickCancelLinstener != null) {
                onClickCancelLinstener.onClickCancel(this);
            }
            dismiss();
        } else {
            if (onClickCancelLinstener != null) {
                onClickCancelLinstener.onClickCancel(this);
            }
            dismiss();
        }
    }
}
