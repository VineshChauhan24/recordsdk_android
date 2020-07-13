package com.qukan.qkrecordupload.fileCut.Callback;

import android.widget.ImageView;
import android.widget.TextView;

import com.qukan.qkrecordupload.bean.VideoTitleBean;
import com.qukan.qkrecordupload.bean.VideoTitleUse;
import com.qukan.qkrecordupload.bean.ViewTitleOnePart;


/**
 * Created by Administrator on 2018/1/2 0002.
 */

public interface CGControlCallback {

    void onClickAddStart();

    void onClickAddCGOver(VideoTitleBean titleBea);

    void onClickDeleteCG(VideoTitleBean videoBean);

    void onClickTextView(VideoTitleUse videoTitleUse, ViewTitleOnePart videoBean, TextView textView, VideoTitleBean videoTitleBean);
    void onClickImageView(VideoTitleUse videoTitleUse, ViewTitleOnePart videoBean, ImageView imageView, VideoTitleBean videoTitleBean);

    void onMoving(VideoTitleBean videoBean);

}
