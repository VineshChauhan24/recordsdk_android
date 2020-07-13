package com.qukan.qkrecordupload.fileCut.animation;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.RelativeLayout;

import org.droidparts.util.L;

/**
 * Created by yang on 2017/7/19.
 */
public class AnimationFlyFromRight extends BaseAnimation {
    public void setMainThings(RelativeLayout mainView, RelativeLayout contentView, double maxWidth, View v_hiddenInAnimation, int parentWidth, int showTime){
        super.setMainThings(mainView,contentView,maxWidth,v_hiddenInAnimation,parentWidth,showTime);
    }



    @Override
    // 时间为从0开始
    public void startAnimation(long currentTime) {

        //最大偏移量
        double allTrans =videoViewWidth - left;
        double TranslationX = 0;

        if (currentTime <= DURATION_TIME) {
            TranslationX = allTrans - allTrans/DURATION_TIME * currentTime;
        }
        contentView.setTranslationX((float)TranslationX);
    }
    // 获取view的内容bitmap
    public Bitmap getBitmap(int currentTime) {
//        L.d("AnimationFlyFromRight--getBitmap");
        //图片不是每次都重绘制的.
        Bitmap img = super.getBitmap(currentTime);
        return img;
    }

    @Override
    public String getNewAnimationName(int currentTime){
        if(currentTime <= 3000&&currentTime >=0){
            return "AnimationFlyFromRight"+currentTime;
        }
        if(currentTime < 0){
            return "AnimationFlyFromRight-1";
        }
        return "AnimationFlyFromRight10000000";
    }

}
