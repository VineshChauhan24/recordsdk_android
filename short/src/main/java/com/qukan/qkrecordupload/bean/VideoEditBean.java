package com.qukan.qkrecordupload.bean;

import com.qukan.qkrecordupload.TextUtil;
import com.qukan.qkrecordupload.qkCut.QKColorFilterGroup;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/6/23 0023.
 * 一次短视频处理中--所有的属性，草稿箱中可使用
 */
@Setter
@Getter
public class VideoEditBean implements Serializable {
    //  DrawType1x1 = 0; DrawType4x3 = 1; DrawType3x4 = 2; DrawType16x9 = 3; DrawType9x16 = 4;
    int ratio = 0;
    // 短视频编辑日期
    long date;
    // 短视频总时长
    String duration;
    // 短视频显示名称
    String shortVideoName="";

    //是否是草稿

    boolean fromDraft = false;

    // 待处理的所有视频块--源视频--18年开始的短视频新版只要这个list保存处理视频
    ArrayList<VideoProcessBean> sourceVideos = new ArrayList<>();
    //所有字幕的信息
    List<VideoTitleUse> textStyleBeen = new ArrayList<>();
    QKColorFilterGroup group = QKColorFilterGroup.getDefaultFilterGroup();



    // 音乐模块
    AudioInfoBean audioInfoBean = new AudioInfoBean();

    // 设定一个版本值，用来在草稿箱中判断是否要失效，旧版本的草稿是无法在新版本的编辑界面中恢复的
    // 0为原来的裁剪字幕分开编辑的版本 1 为增加了配音功能的版本
    int version = 1;
    String coverPath="";

    public void deleteRecordPath(){
        String recordPath = getAudioInfoBean().getRecordPath();
        if (!TextUtil.isEmpty(recordPath)) {
            File file = new File(recordPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
