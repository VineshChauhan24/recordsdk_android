package com.qukan.qkrecordupload.qkCut;

import com.qukan.qkrecordupload.bean.AudioInfoBean;

import lombok.Data;

@Data
public class RecordInfoController {
    private AudioInfoBean infoBean = AudioInfoBean.getDefaultInfo();
}
