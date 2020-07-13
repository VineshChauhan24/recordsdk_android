package com.qukan.qkrecordupload.custom;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.TextUtil;

import org.droidparts.Injector;
import org.droidparts.bus.EventBus;

import static com.qukan.qkrecordupload.R.id.tv_msg;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class MyLoadingPop extends PopupWindow {

    private Context context;
    private RotateAnimation mAnim;
    private ImageView ivWait;
    private TextView tvMsg;

    public MyLoadingPop(Context context)
    {
        super(context);
        this.context = context;
        onInit();
    }

    private void onInit()
    {
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        View inflate = setPopview();
        setContentView(inflate);
        setBackgroundDrawable(new BitmapDrawable());
        Injector.inject(getContentView(), this);
        EventBus.unregisterAnnotatedReceiver(this);
        EventBus.registerAnnotatedReceiver(this);

        ivWait =(ImageView) inflate.findViewById(R.id.iv_wait);
        tvMsg=(TextView)inflate.findViewById(tv_msg);
        mAnim = new RotateAnimation(0, 360, Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
        mAnim.setDuration(1000);
        mAnim.setRepeatCount(Animation.INFINITE);
        mAnim.setRepeatMode(Animation.RESTART);
        mAnim.setStartTime(Animation.START_ON_FIRST_FRAME);
        // 旋转的动画效果，线性均匀旋转
//        mAnim.setInterpolator(new LinearInterpolator());
    }

    public void setMsg(String msg) {
        if (!TextUtil.isEmpty(msg)) {
            tvMsg.setText(msg);
        }
    }

    public void hideMsg(){
        tvMsg.setVisibility(View.GONE);
    }

    public View setPopview() {
        return LayoutInflater.from(context).inflate(R.layout.my_loading, null);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        ivWait.startAnimation(mAnim);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
