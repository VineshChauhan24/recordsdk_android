package com.qukan.qkrecordupload.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

import org.droidparts.bus.EventBus;

/**
 * Created by Administrator on 2015/9/9 0009.
 */
public abstract class BasePageView implements View.OnClickListener
{
    Context context;
    View inflate;
    public BasePageView(Context context)
    {
        this.context = context;
        onPrepare();
        onPostStart();
    }

    public Context getContext()
    {
        return this.context;
    }

    public View getPageView()
    {
        return this.inflate;
    }

    public void onPrepare()
    {
        inflate = LayoutInflater.from(context).inflate(setPageViewR(),null);
//        Injector.inject(inflate, this);
        EventBus.unregisterAnnotatedReceiver(this);
        EventBus.registerAnnotatedReceiver(this);
    }

    Animation shake ;
    public void SetAnimation(View v) {

        //shake = AnimationUtils.loadAnimation(context, R.anim.shark_lr);//加载动画资源文件
        v.startAnimation(shake); //给组件播放动画效果
    }


    @Override
    public void onClick(View v) {
        //SetAnimation(v);
        onHandleClick(v);
    }

    protected abstract void onHandleClick(View v);

    public abstract int setPageViewR();
    public abstract void onPostStart();

}
