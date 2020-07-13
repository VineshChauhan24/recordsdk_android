package com.qukan.qkrecordupload.fileCut;

/**
 * Created by Administrator on 2017/12/11 0011.
 * 针对录音，提供几个方法用来管理本次视频处理的配音
 */

public interface BaseAudioRecord {
    // 录音总时长，根据视频的长度来
    void initAudio(String path, float allTime);

    // 秒
    boolean startAudio(float startTime);

    void stopAudio();

    void changeTime(float newAllTime);

    void deleteRecord(float startTime, float endTime);
}
