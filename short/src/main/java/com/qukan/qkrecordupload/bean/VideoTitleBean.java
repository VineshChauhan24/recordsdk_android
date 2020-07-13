package com.qukan.qkrecordupload.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qukan.qkrecordupload.ConfigureManagerUtil;
import com.qukan.qkrecordupload.MoveRelativeLayout2;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.QkApplication;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.TextUtil;
import com.qukan.qkrecordupload.fileCut.animation.BaseAnimation;

import org.droidparts.util.L;

import lombok.Getter;
import lombok.Setter;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.qukan.qkrecordupload.R.id.rl_content_root;

/**
 * Created by yang on 2017/7/21.
 */
public class VideoTitleBean implements View.OnClickListener, MoveRelativeLayout2.MoveOverListener {
    private int MARGIN_LEFT_RIGHT = 0;

    // 字幕在整个视频的范围内的开始时间和结束时间
    private long mStartTime, mEndTime;

    //title所有用到的数据都在这里面
    @Getter
    private VideoTitleUse titleUse;

    //取代原来的CGSize类，即父布局的宽高，用于放置字幕的区域
    private int parentWidth;
    private int parentHeight;

    private RelativeLayout mainViewLayout;
    private MoveRelativeLayout2 rlContentRoot;
    private RelativeLayout rlContent;

    private RelativeLayout rlImageContent;
    private LinearLayout llTextContent;
    private ImageView ivClose;
    @Setter
    @Getter
    private BaseAnimation animation;
    OnClickListener videoTextListener;
    private Context context;
    private long EVENT_TIME = 100;

    public VideoTitleBean(VideoTitleUse bean) {
        this.titleUse = bean;
        this.animation = BaseAnimation.getAnimationByName(bean.getAnimationName());
        this.mStartTime = bean.getStartTime();
    }

    public VideoTitleBean() {

    }

    public String getTitleBeanJson() {
        String str = JSON.toJSONString(titleUse);
        return str;
    }

    //最终要放到多大的地方去
    public RelativeLayout getShowViews(int width, int height, Context context, boolean isPause) {
        this.context = context;
        if ((parentWidth == 0 || parentHeight == 0) || (width != parentWidth || height != parentHeight)) {
            parentWidth = width;
            parentHeight = height;
        } else {
            if (mainViewLayout != null) {
                return mainViewLayout;
            }
        }
        L.d("changeAnimationTime--getShowViews");
        CreateAllShowView(context);
        // 生成时不可见，但是添加进布局后一定要调用刷新方法--为解决字幕预览时添加会闪现问题
        mainViewLayout.setVisibility(View.INVISIBLE);

        return mainViewLayout;
    }

    public void setVideoTextListener(OnClickListener videoTextListener) {
        this.videoTextListener = videoTextListener;
    }


    float lastX = 0.0f;
    float lastY = 0.0f;
    float lastA = 0.0f;
    int lastVisible = View.INVISIBLE;

    // 取消处理时重置下这些属性，不然再次处理时isChange判断会失效
    public void reset() {
        lastX = 0.0f;
        lastY = 0.0f;
        lastA = 0.0f;
        lastVisible = View.INVISIBLE;
    }

    // 继续优化
    public boolean isChange() {
        boolean isChange;
        float x = rlContentRoot.getTranslationX();
        float y = rlContentRoot.getTranslationY();
        float a = rlContentRoot.getAlpha();
        int v = mainViewLayout.getVisibility();

        if (x != lastX || y != lastY || a != lastA || v != lastVisible) {
            isChange = true;
        } else {
            isChange = false;
        }
        lastX = x;
        lastY = y;
        lastA = a;
        lastVisible = v;
        return isChange;
    }

    //  改变时间和状态---根据时间显示动画，会在拖动进度条和开始合成视频时触发
    //  考虑增加一个返回值---开始动画后是否发生了变化
    public boolean changeAnimationTime(long currentTime, boolean isPause) {
        int showVisible = View.INVISIBLE;
        boolean isHidden = true;
//        L.d("changeAnimationTime2:mStartTime=%s,mEndTime=%s,animation.DURATION_TIME=%s,currentTime=%s", mStartTime, mEndTime, animation.DURATION_TIME, currentTime);
        // currentTime >= mStartTime要加等号，不然刚刚加进去的时候是无法进入这个判断的
        if (currentTime >= mStartTime && currentTime < (mEndTime + this.animation.DURATION_TIME)) {
            showVisible = View.VISIBLE;
            isHidden = false;
//            L.d("changeAnimationTimeX--View.VISIBLE");
        }

        animation.HiddenViewOrPasue(showVisible, isPause);
//        L.d("changeAnimationTime3:isHidden=%s,isPause=%s", isHidden, isPause);

        if (!isHidden && !isPause) {
            long t = currentTime - mStartTime;
//            L.d("changeAnimationTime4=%s", t);
            this.animation.startAnimation(t);//0-3
        } else {
            if (!isHidden) {
//                L.d("changeAnimationTime5");
                this.animation.resetFrame();
            }
        }
        return isChange();
    }

    //获取图片
    public Bitmap getBitmap(long currentTime) {
//        videoFlag == this.mVideoFlag &&
        if (currentTime > mStartTime && currentTime < (mEndTime + animation.DURATION_TIME)) {
            return animation.getBitmap(currentTime - mStartTime);
        }
        return null;
    }
    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    //获取最大宽度
    private double getMaxWidth() {
        return parentWidth - PublicUtils.dip2px(MARGIN_LEFT_RIGHT) * 2;
    }

    //创建，并且初始化元素
    private void CreateAllShowView(Context context) {
        float scale = 1.0f;
        if (parentHeight > parentWidth) {
            scale = parentHeight / 1080.0f;
        } else {
            scale = parentWidth / 1080.0f;
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainViewLayout = (RelativeLayout) inflater.inflate(R.layout.view_title_bean, null);
        rlContentRoot = (MoveRelativeLayout2) mainViewLayout.findViewById(rl_content_root);
        rlContentRoot.setParentWidth(parentWidth);
        rlContentRoot.setParentHeight(parentHeight);
        rlContentRoot.setListener(this);


        {
            rlContent = (RelativeLayout) mainViewLayout.findViewById(R.id.rl_content);
            //计算布局的位置
            RelativeLayout.LayoutParams rlContentParams = new RelativeLayout.LayoutParams(rlContent.getLayoutParams());
            int margin = dip2px(context, 15);
            rlContentParams.setMargins(0, (int) (margin * scale), margin, 0);
            rlContent.setLayoutParams(rlContentParams);
        }


        llTextContent = (LinearLayout) mainViewLayout.findViewById(R.id.ll_text_content);
        rlImageContent = (RelativeLayout) mainViewLayout.findViewById(R.id.rl_image_content);

        ivClose = (ImageView) mainViewLayout.findViewById(R.id.iv_delete);
        ivClose.setOnClickListener(this);
        String backColor = titleUse.getBackgroundColor();
        if (backColor.startsWith("#")) {
            llTextContent.setBackgroundColor(Color.parseColor(backColor));
        } else if (backColor.equals("default_video_end")) {
            llTextContent.setBackgroundResource(R.drawable.video_back);
        }
        // 设置文字布局的排列方向，横向还是竖向
        if (titleUse.isHorizontal()) {
            llTextContent.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            llTextContent.setOrientation(LinearLayout.VERTICAL);
        }

        //计算布局的位置
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(rlContentRoot.getLayoutParams());

        PointPath point;
        if (parentWidth > parentHeight) {
            //横屏
            point = titleUse.getPointLandscape();
        } else {
            //竖屏
            point = titleUse.getPointVer();
        }

        if (point.centerHorizontal && point.centerVertical && point.marginLeft == -0.01f && point.marginTop == -0.01f && point.marginRight == -0.01f && point.marginBottom == -0.01f) {
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (point.centerHorizontal && point.marginLeft == -0.01f && point.marginRight == -0.01f) {
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (point.centerVertical && point.marginTop == -0.01f && point.marginBottom == -0.01f) {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
        }
        if (point.marginLeft >= 0) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }
        if (point.marginTop == 0) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            if (point.marginBottom >= 0) {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
        }
        if (point.marginRight > 0 && point.rightChanged == false) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }


        int left = (int) (parentWidth * point.marginLeft);
        int right = (int) (parentWidth * point.marginRight);
        int top = (int) (parentHeight * point.marginTop);
        int bottom = (int) (parentHeight * point.marginBottom);
        if (point.marginLeft == -0.01f) {
            left = 0;
        }
        if (point.marginRight == -0.01f || point.rightChanged) {
            right = 0;
        }
        if (point.marginTop == -0.01f) {
            top = 0;
        }
        if (point.marginBottom == -0.01f) {
            bottom = 0;
        }

        if (top > 0) {
            top = 0;
        }
        params.setMargins(left, top, right, bottom);
        rlContentRoot.setLayoutParams(params);

        int durationTime = titleUse.getDurationTime();
        // 增加文字
        final VideoTitleUse titleTemp = titleUse;
        // 片头片尾隐藏右上角关闭按钮
        if (titleUse.isTitleOrPw()) {
            ivClose.setVisibility(View.GONE);
            this.animation.setNeedCloseIcon(false);
        } else {
            ivClose.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < titleUse.getTitleOnePartList().size(); i++) {
            final ViewTitleOnePart subPreview = titleUse.getTitleOnePartList().get(i);

            if (subPreview.type == 3) {
                //含有纯色背景的，要进行拓宽
                rlContentRoot.setPadding(6, 6, 6, 6);
                rlContentRoot.setBackgroundColor(Color.BLUE);
            } else if (subPreview.type == 1) {
                //添加图片--片头
                ImageView img = new ImageView(context);
                String localImage = subPreview.getImgName();

                if (localImage.equals("default_logo")) {
                    int logo = ConfigureManagerUtil.getRecordLogo(QkApplication.getContext());
                    if (logo != 0) {
                        img.setImageResource(logo);
                    } else {
                        img.setImageResource(R.mipmap.ic_launcher);
                    }
                } else if (localImage.equals("logo_frame")) {
                    img.setImageResource(R.mipmap.frame);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeFile(localImage);
                    img.setImageBitmap(bitmap);
                }
                img.setClickable(true);
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams((int) (subPreview.getImgWidth() * scale),
                        (int) (subPreview.getImgHeight() * scale));//两个400分别为添加图片的大小
                img.setLayoutParams(imgParams);

                params.width = PublicUtils.dip2px(15) + (int) (subPreview.getImgWidth() * scale);
                params.height = PublicUtils.dip2px(15) + (int) (subPreview.getImgHeight() * scale);
                rlContentRoot.setLayoutParams(params);

                llTextContent.addView(img);
                int padding = subPreview.getPadding();
                llTextContent.setPadding(padding, padding, padding, padding);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (videoTextListener != null) {
                            if (!isClick()) {
                                return;
                            }
                            videoTextListener.onclickImageView(titleTemp, subPreview, (ImageView) v, VideoTitleBean.this);
                        }
                    }
                });
            } else if (subPreview.type == 2) {
                //添加文字
                String content = subPreview.getText();
                TextView textView = new TextView(context);
                float alpha = subPreview.getAlpha();
                String color = subPreview.getTextColor();
                String c = Integer.toHexString((int) (255 * alpha));
                color = color.replace("#", "#" + c);
                textView.setTextColor(Color.parseColor(color));
//                textView.setAlpha(subPreview.getAlpha());
                textView.setText(content, TextView.BufferType.EDITABLE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, subPreview.getFontSize() * scale);
                TextPaint paint = textView.getPaint();
                paint.setFakeBoldText(true);
                Log.d("textView1", "content:" + content);
                if (!TextUtil.isEmpty(content)) {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (videoTextListener != null) {
                                if (!isClick()) {
                                    return;
                                }
                                videoTextListener.onclickTextView(titleTemp, subPreview, (TextView) v, VideoTitleBean.this);
                            }
                        }
                    });
                }
                int width = subPreview.getWidth();
                int height = subPreview.getHeight();
                if (width != 0) {
                    textView.setWidth(width);
                }
                if (height != 0) {
                    textView.setHeight(height);
                }
                int margin = subPreview.getMargin();
                if (margin != 0) {
                    margin = PublicUtils.dip2px(margin);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                    layoutParams.setMargins(margin, margin, margin, margin);
                    textView.setLayoutParams(layoutParams);
                }
                //包含背景的话添加padding值
                if (subPreview.isAddBackground()) {
                    textView.setBackgroundColor(Color.parseColor(subPreview.getBackgroundColor()));
                    int padding = PublicUtils.dip2px(3);
                    textView.setPadding(padding, padding, padding, padding);
                }
                if (subPreview.isSingleLine()) {
                    textView.setSingleLine();
                }
                llTextContent.addView(textView);
            } else if (subPreview.type == 5) {
                String content = subPreview.getText();
                TextView textView = new TextView(context);
                textView.setSingleLine();
                Log.d("textView", "content:" + content);
                if (TextUtil.isEmpty(content)) {
                    textView.setBackgroundColor(context.getResources().getColor(R.color.text_yellow));
                    textView.setText(" ", TextView.BufferType.EDITABLE);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, subPreview.getFontSize() * scale);
                    textView.setHeight(3);
                    textView.setWidth(llTextContent.getWidth());
                } else {
                    // 0-1
                    float alpha = subPreview.getAlpha();
                    String color = subPreview.getTextColor();
                    String c = Integer.toHexString((int) (255 * alpha));
                    color = color.replace("#", "#" + c);
                    textView.setTextColor(Color.parseColor(color));
                    textView.setText(content, TextView.BufferType.EDITABLE);
//                    textView.setAlpha(subPreview.getAlpha());
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, subPreview.getFontSize());
                    textView.setGravity(Gravity.CENTER);
                    // 加粗
                    TextPaint paint = textView.getPaint();
                    paint.setFakeBoldText(true);
                }
                if (!TextUtil.isEmpty(content)) {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (videoTextListener != null) {
                                if (!isClick()) {
                                    return;
                                }
                                videoTextListener.onclickTextView(titleTemp, subPreview, (TextView) v, VideoTitleBean.this);
                                refreshDivide();
                            }
                        }
                    });
                }
                llTextContent.addView(textView);
                refreshDivide();
            }
        }

        mEndTime = mStartTime + durationTime;
        animation.setLlTextContent(llTextContent);
        animation.setMainThings(mainViewLayout, rlContentRoot, getMaxWidth(), ivClose, parentWidth, mEndTime - mStartTime);
    }

    public void changeAnimation() {
        animation.setMainThings(mainViewLayout, rlContentRoot, getMaxWidth(), ivClose, parentWidth, mEndTime - mStartTime);
    }

    // 刷新第十个字幕的中间分隔线长度
    private void refreshDivide() {
        for (int x = 0; x < llTextContent.getChildCount(); x++) {
            TextView textVw = (TextView) llTextContent.getChildAt(x);
            String text = textVw.getText().toString();
            if (TextUtil.isEmpty(text)) {
                textVw.setWidth(llTextContent.getWidth());
            }
        }
    }

    // 滑动结束回调，保存位置参数，在父布局重绘时保证控件固定
    @Override
    public void onMoveOver(float mLeft, float mTop, float mRight, float mBottom) {
        PointPath pointPathVer = titleUse.getPointVer();
//        if (pointPathVer.getMarginLeft() != 0) {
        pointPathVer.setMarginLeft(mLeft);
//        }
//        if (pointPathVer.getMarginTop() != 0) {
        pointPathVer.setMarginTop(mTop);
//        }
//        if (pointPathVer.getMarginRight() != 0) {
        pointPathVer.setMarginRight(mRight);
//        }
//        if (pointPathVer.getMarginBottom() != 0) {
        pointPathVer.setMarginBottom(mBottom);
//        }

        PointPath pointPathLandscape = titleUse.getPointLandscape();
//        if (pointPathLandscape.getMarginLeft() != 0) {
        pointPathLandscape.setMarginLeft(mLeft);
//        }
//        if (pointPathLandscape.getMarginTop() != 0) {
        pointPathLandscape.setMarginTop(mTop);
//        }
//        if (pointPathLandscape.getMarginRight() != 0) {
        pointPathLandscape.setMarginRight(mRight);
//        }
//        if (pointPathLandscape.getMarginBottom() != 0) {

        pointPathLandscape.setMarginBottom(mBottom);
//        }
    }

    @Override
    public void onMoving() {
        if (videoTextListener != null) {
            videoTextListener.onMoving(this);
        }
    }

    public interface OnClickListener {
        void onclickDelete(VideoTitleBean videoBean);

        void onclickTextView(VideoTitleUse bean, ViewTitleOnePart videoBean, TextView view, VideoTitleBean videoTitleBean);

        void onclickImageView(VideoTitleUse bean, ViewTitleOnePart videoBean, ImageView imageView, VideoTitleBean videoTitleBean);

        void onMoving(VideoTitleBean videoBean);
    }

    @Override
    public void onClick(View v) {
        if (!isClick()) {
            return;
        }
        if (v == ivClose) {
            if (videoTextListener != null) {
                videoTextListener.onclickDelete(this);
            }
        } else {
            L.e("error unKnow View");
        }
    }

    // 判断究竟是拖动还是点击事件
    private boolean isClick() {
        long eventTime = rlContentRoot.getTimeEventEnd() - rlContentRoot.getTimeEventStart();
        L.d("eventTime=%s", eventTime);
        //当按键事件超过一定时间判断为滑动事件,屏蔽点击事件
        if (eventTime > EVENT_TIME) {
            return false;
        } else {
            return true;
        }
    }

    public long getMStartTime() {
        return mStartTime;
    }

    public void setMStartTime(long mStartTime) {
        Log.d("setMStartTime", "" + mStartTime);
        this.mStartTime = mStartTime;
    }

    public long getMEndTime() {
        return mEndTime;
    }

    public void setMEndTime(long mEndTime) {
        Log.d("setMEndTime", "" + mEndTime);

        this.mEndTime = mEndTime;
    }

    public int getParentWidth() {
        return parentWidth;
    }

    public void setParentWidth(int parentWidth) {
        this.parentWidth = parentWidth;
    }

    public int getParentHeight() {
        return parentHeight;
    }

    public void setParentHeight(int parentHeight) {
        this.parentHeight = parentHeight;
    }
}


