package com.qukan.qkrecordupload.bean;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Created by Administrator on 2017/12/27 0027.
 * 录音时需要传递进去的信息
 */
@Data
public class AudioInfoBean {
    // 录音文件的保存路径
    String recordPath="";
      // 选中的背景音乐
    String backMusic="";
    // 原声 音乐 配音
    int originVolume = 100;
    int BGMVolume = 100;
    int recordVolume = 100;
    public static AudioInfoBean getDefaultInfo(){
        return new AudioInfoBean();
    }
}
