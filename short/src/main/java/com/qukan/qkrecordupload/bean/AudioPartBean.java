package com.qukan.qkrecordupload.bean;

import lombok.Data;

/**
 * Created by yang on 2017/12/26.
 */
@Data
public class AudioPartBean {
    // 总时间轴上的录音开始时间和结束时间(毫秒)
    private long startTime;
    private long endTime;

    // 在未裁剪的视频块（可能已经裁剪了，但这边的时间意思是没裁剪的话）上 -- 开始录音时间和结束时间
    private long recordStartTime;
    private long recordEndTime;

    public AudioPartBean copy() {
        AudioPartBean audioPartBean = new AudioPartBean();
        audioPartBean.startTime = this.startTime;
        audioPartBean.endTime = this.endTime;
        audioPartBean.recordStartTime = this.recordStartTime;
        audioPartBean.recordEndTime = this.recordEndTime;
        return audioPartBean;
    }

}
