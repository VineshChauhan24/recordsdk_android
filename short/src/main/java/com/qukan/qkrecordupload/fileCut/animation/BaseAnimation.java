package com.qukan.qkrecordupload.fileCut.animation;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.droidparts.util.L;

import lombok.Setter;

/**
 * Created by yang on 2017/7/19.
 */
public abstract class BaseAnimation{
    double maxWidth;

    // 布局的view用于调整大小
    public RelativeLayout mainView; //主界面
    @Setter
    public LinearLayout llTextContent; //文字界面
    public RelativeLayout contentView; //内容界面
    public View v_hiddenInAnimation;
    @Setter
    public long DURATION_TIME = 500; //动画持续时间
    public int videoViewWidth;

    public int left = 0;
    public int width = 0;
    public Bitmap cacheImg = null;//图片缓存

    // 不算动画的字幕展示时间
    public long mShowTime = 0;

    // 在片头，片尾动画时是不需要关闭图标的
    @Setter
    boolean isNeedCloseIcon=true;

    // 根据动画名称获取对应的动画实现类
    public static BaseAnimation getAnimationByName(String name){
        if(name.equals("AnimationFlyFromRight")){
            return new AnimationFlyFromRight();
        } else if (name.equals("AnimationFlyFromLeft")) {
            return new AnimationFlyFromLeft();
        } else if (name.equals("AnimationFlyFromLeftEndRight")) {
            return new AnimationFlyFromLeftEndRight();
        }
        return new AnimationFlyFromRight();
    }

    // 字幕本身不算动画效果的显示时间
    public void setMainThings(RelativeLayout mainView, RelativeLayout contentView, double maxWidth, View v_hiddenInAnimation, int parentWidth, long showTime){
        this.maxWidth = maxWidth;
        this.v_hiddenInAnimation = v_hiddenInAnimation;
        this.mainView = mainView;
        this.contentView = contentView;
        this.videoViewWidth = parentWidth;
        this.left = contentView.getLeft();
        this.width = contentView.getWidth();
        this.mShowTime = showTime;
        cacheImg = null;
    }
    //当前页面是否显示，是否是暂停状态，如果是暂停状态，那么v_hiddenInAnimation（右上角关闭按钮） 就要显示出来
    public void HiddenViewOrPasue(int isVisible,Boolean isPause){
        mainView.setVisibility(isVisible);

        if(isPause&&isNeedCloseIcon){
            if(this.v_hiddenInAnimation != null){
                this.v_hiddenInAnimation.setVisibility(View.VISIBLE);
            }
        }else{
            if(this.v_hiddenInAnimation != null){
                this.v_hiddenInAnimation.setVisibility(View.INVISIBLE);
            }
        }
    }
    //重新初始化位置
    public void resetFrame(){
        mainView.setVisibility(View.VISIBLE);

        if(this.v_hiddenInAnimation != null&&isNeedCloseIcon){
            this.v_hiddenInAnimation.setVisibility(View.VISIBLE);
        }
        contentView.setTranslationX(0);
        mainView.setAlpha(1);
        contentView.setAlpha(1);
    }

    // 获取view的内容bitmap,currentTime为相对时间，即减去前面的视频开始时间，这里是从0开始的
    public Bitmap getBitmap(long currentTime) {
//        L.d("BaseAnimation--getBitmap");
        if(this.cacheImg != null && currentTime > DURATION_TIME && currentTime<(DURATION_TIME+mShowTime)){
            return this.cacheImg;
        }
        HiddenViewOrPasue(View.VISIBLE, false);

        startAnimation(currentTime);

        mainView.setDrawingCacheEnabled(true);
        Bitmap tBitmap = mainView.getDrawingCache();
        if (tBitmap == null) {
            L.d("tBitmap == null");
            return null;
        }
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = tBitmap.createBitmap(tBitmap);
        mainView.setDrawingCacheEnabled(false);
        if (currentTime > DURATION_TIME) {
            this.cacheImg = tBitmap;
        }
        int width = tBitmap.getWidth();
        int height = tBitmap.getHeight();
        L.d("width：%d height:%d",width,height);
        return tBitmap;
    }

    public abstract void startAnimation(long currentTime) ;

    public abstract String getNewAnimationName(int currentTime);
}
