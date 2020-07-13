package com.qukan.qkrecordupload.fileCut.recordCallback;

/**
 * Created by Administrator on 2017/12/26 0026.
 * 录音时要实现这个接口
 */

public interface PlayerPlayCallback {

    void onVideoPlay(long time);

    void onVideoPause();

    void onSeekTo(long time);

}
