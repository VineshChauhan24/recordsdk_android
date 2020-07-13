package com.qukan.qkrecordupload.fileCut.Callback;


/**
 * Created by Administrator on 2017/12/29 0029.
 * 裁剪界面各个功能键监听
 */

public interface OnCropCallback {
    void onSeekLeft(long value, Boolean isFromUser);

    void onSeekRight(long value, Boolean isFromUser);

    void onDelete();

    void onSureDelete();
    void onChangeRotate(int rotate);
    void onSpeedChange(double speed);

}
