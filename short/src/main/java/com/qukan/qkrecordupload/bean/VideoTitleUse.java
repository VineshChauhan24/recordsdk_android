package com.qukan.qkrecordupload.bean;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Created by Administrator on 2017/7/25 0025.
 * 一个字幕的所有属性（可能是多行的）
 */
@Data
public class VideoTitleUse {

    private List<ViewTitleOnePart> titleOnePartList; //存储了字幕的内容有多少个
    private boolean titleOrPw = false;//是否是片头或者片尾
    private PointPath pointLandscape; //横屏时候
    private PointPath pointVer ; //竖屏时候
    private String animationName = "AnimationFlyFromRight"; //动画名称
    // 字幕在整个视频的范围内的开始时间和结束时间
    private long startTime;
    // 字幕在视频中出现的时间点
    private long softStartTime;
    // 一个标记值用于判断这个字幕加载在哪一个视频块上
    private long videoFlag;
    // 背景
    String backgroundColor= "";
    // 字幕持续时间
    int durationTime=3000;
    boolean isHorizontal;
    int type = 0;// 字幕类型，用来标记字幕在进度条上的小红点

    public VideoTitleUse copy() {
        VideoTitleUse videoTitleUse = new VideoTitleUse();
        List<ViewTitleOnePart> titleOnePartList = new ArrayList<>();
        for (ViewTitleOnePart viewTitleOnePart : this.titleOnePartList) {
            ViewTitleOnePart viewTitleOnePart1 = viewTitleOnePart.copy();
            titleOnePartList.add(viewTitleOnePart1);
        }
        videoTitleUse.titleOnePartList = titleOnePartList;
        videoTitleUse.titleOrPw = this.titleOrPw;
        videoTitleUse.pointLandscape = this.pointLandscape.copy();
        videoTitleUse.pointVer = this.pointVer.copy();
        videoTitleUse.animationName = this.animationName;
        videoTitleUse.startTime = this.startTime;
        videoTitleUse.softStartTime = this.softStartTime;
        videoTitleUse.videoFlag = this.videoFlag;
        videoTitleUse.backgroundColor = this.backgroundColor;
        videoTitleUse.durationTime = this.durationTime;
        videoTitleUse.isHorizontal = this.isHorizontal;

        return videoTitleUse;

    }
}
