package com.qukan.qkrecordupload.fileCut.Callback;


import com.qukan.qkrecordupload.bean.AudioInfoBean;
import com.qukan.qkrecordupload.bean.MusicRes;

/**
 * Created by Administrator on 2018/1/2 0002.
 */

public interface MusicCallBack {
    // TODO: 2018/1/2 0002  在音乐界面回调出操作数据
    void onVolumeChanged(AudioInfoBean audioInfoBean);

    // 点击了配音时监听原声
    void onClickSwitchVoice(boolean isOpen);

    void onSelectedMusic(MusicRes path);

    // 倒计时开始
    void onPrepare();

    void onRecordStart(long startTime);

    void onRecordStop();

    void onRecording(long oldTime, long nowTime);

    void onClickDelete();

}
