package com.qukan.qkrecordupload.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.qukan.qkrecordupload.R;


/**
 *
 *
 */
public class MyLoadingDialog extends Dialog {
    private RotateAnimation mAnim;
    private ImageView ivWait;

    public MyLoadingDialog(Context context) {
        super(context, R.style.Dialog_bocop);
        init();
    }

    //    忽略编译警告
    @SuppressWarnings("ResourceType")
    private void init() {
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        View contentView = View.inflate(getContext(), R.layout.dialog_loading, null);
        setContentView(contentView);

        ivWait =(ImageView) contentView.findViewById(R.id.iv_wait);
        initAnim();
        getWindow().setWindowAnimations(R.anim.alpha_in);
    }

    private void initAnim() {
        mAnim = new RotateAnimation(0, 360, Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
        mAnim.setDuration(1000);
        mAnim.setRepeatCount(Animation.INFINITE);
        mAnim.setRepeatMode(Animation.RESTART);
        mAnim.setStartTime(Animation.START_ON_FIRST_FRAME);
    }

    public void show() {
        super.show();
        ivWait.startAnimation(mAnim);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getString(titleId));
    }
}
