package com.qukan.qkrecordupload;

import android.os.Environment;

import java.io.File;

public class ConfigureConstants
{
    // 趣看根目录
    public static final String QUKAN_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qukantool";

    // 录像的路径
    public static final String QUKAN_PATH_RECORD = QUKAN_ROOT + "/record";

    // CahceImage
    public static final String QUKAN_CACHE_IMAGE = QUKAN_ROOT + "/image";

    // 非直播录像的路径        mp4
    public static final String QUKAN_PATH_NOLIVE_RECORD = QUKAN_ROOT + "/RecordVideo";

    // 裁剪视频文件保存路径
    public static final String QUKAN_PATH_CUT_RECORD  = QUKAN_ROOT + "/CutVideo";

    // 合并视频文件保存路径
    public static final String QUKAN_PATH_CONCAT_RECORD  = QUKAN_ROOT + "/ConcatVideo";

    // 合并视频文件保存路径
    public static final String QUKAN_PATH_TEMP  = QUKAN_ROOT + "/TEMP";

    // 配音文件保存路径
    public static final String QUKAN_PCM  = QUKAN_ROOT + "/recordPCM";

    public static final String QUKAN_PCM_AUDIO  = QUKAN_ROOT + "/recordPCM"+ File.separator + "luyin.pcm";

    // 短视频处理后存放的视频路径
    public static final String QUKAN_PATH_PROCESS_VIDEO = QUKAN_ROOT + "/ProcessVideo";
    // 背景音
    public static final String QUKAN_PATH_PROCESS_MP3 = QUKAN_ROOT + "/mp3";
    public static final String QUKAN_PCM_TEMP = QUKAN_ROOT + "/TempPCM";


    static
    {
        File file = new File(QUKAN_PATH_CUT_RECORD);
        file.mkdirs();

        File file2 = new File(QUKAN_PCM);
        file2.mkdirs();
    }
}
