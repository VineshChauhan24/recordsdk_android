package com.qukan.qkrecordupload.bean;

import lombok.Data;

/**
 * Created by yang on 2017/12/26.
 */
@Data
public class AudioChange {
    // 旧的时间
    private long oldStartTime;
    private long oldEndTime;
    // 新的时间
    private long startTime;
    private long endTime;
}
