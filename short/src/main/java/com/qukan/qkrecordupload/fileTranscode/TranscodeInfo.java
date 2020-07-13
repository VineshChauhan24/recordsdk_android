package com.qukan.qkrecordupload.fileTranscode;

/**
 * Created by Administrator on 2016-04-07.
 */
public class TranscodeInfo {

    private int width = 0;
    private int height = 0;
    private int bitRate = 0;

    private TranscodeInfo()
    {
        width = 640;
        height = 360;
        bitRate = 500000;
    }

    // 文件同步的管理类
    private static TranscodeInfo _instance = new TranscodeInfo();

    // 返回单例
    public static TranscodeInfo instance()
    {
        return _instance;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }
}
