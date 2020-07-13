package com.qukan.qkrecordupload.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.VideoTitleUse;

import java.util.List;

/**
 * Created by Administrator on 2018/2/5 0005
 */

public class TextMarkLayout extends RelativeLayout {
    Context mContext;
    int offset;

    public TextMarkLayout(Context context) {
        super(context);
        init(context);
    }

    public TextMarkLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextMarkLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        offset = PublicUtils.dip2px(15);
    }

    public void drawMark(List<VideoTitleUse> videoTitleUseData, long allTime) {

        if (videoTitleUseData == null) {
            return;
        }
        removeAllViews();
        for (VideoTitleUse videoTitleUse : videoTitleUseData) {
            float startPercent = videoTitleUse.getStartTime() / (float) allTime;
            // 500是开始字幕进入时的动画时间
//            float endPercent = (videoTitleUse.getStartTime() + videoTitleUse.getDurationTime() + 500) / (float) allTime;
//            if (endPercent > 1.0) {
//                endPercent = 1.0f;
//            }
            int type = videoTitleUse.getType() - 1;
            ImageView imageViewS = getImageView(startPercent, type);
//            ImageView imageViewE = getImageView(endPercent, type);
            addView(imageViewS);
//            addView(imageViewE);
        }

    }

    int[] resId = {R.drawable.text_style_1, R.drawable.text_style_2, R.drawable.text_style_3, R.drawable.text_style_4, R.drawable.text_style_5, R.drawable.text_style_6,
            R.drawable.text_style_7, R.drawable.text_style_8, R.drawable.text_style_9, R.drawable.text_style_10};

    public ImageView getImageView(float percent, int type) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text_style_10);
        int bitmapW = bitmap.getWidth();
        int w = getWidth() - offset * 2;
        int margeL = (int) (percent * w) - bitmapW / 2 + offset;
        ImageView imageView = new ImageView(mContext);
        int res = resId[type];
        imageView.setImageResource(res);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(margeL, 0, 0, 0);
        imageView.setLayoutParams(params);
        return imageView;
    }


}
