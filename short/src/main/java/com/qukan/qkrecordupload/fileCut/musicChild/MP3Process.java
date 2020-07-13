package com.qukan.qkrecordupload.fileCut.musicChild;


import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.ThreadPool;
import com.qukan.qkrecorduploadsdk.jni.QukanLiveJni;

import org.droidparts.util.L;

import java.io.File;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/10/20 0020.
 * 用于处理mp3音频，处理成pcm数据
 */

public class MP3Process {
    @Getter
    private String srcPath, dstPath; //mp3来源路径--mp3转码成pcm时临时的存放路径

    @Setter
    OnProcessListener processListener;
    private int mp3DurationTime;

    public MP3Process(String sourcePath, String outputPath) {
        this.srcPath = sourcePath;
        this.dstPath = outputPath;
        File file = new File(outputPath);
        File parentFile = file.getParentFile();
        L.d("parentFile.getAbsolutePath()=%s", parentFile.getAbsolutePath());
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        mp3DurationTime = PublicUtils.getVideoDuration(srcPath);
    }

    public void startProcess() {
        if (processListener != null) {
            processListener.onProcessStart();
        }
        File file = new File(dstPath);
        if (file.exists()) {
            try {
                file.delete();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                L.d("startProcess--begin");
                try {
                    QukanLiveJni.QKMediaFilePlayer(srcPath, dstPath);
//                    Thread.sleep(1000*6);
                } catch (Exception e) {
                    if (processListener != null) {
                        processListener.onFail();
                    }
                    e.printStackTrace();
                    return;
                }finally {

                }
                if (processListener != null) {
                    processListener.onProcessOver();
                }
                L.d("startProcess--over");
            }
        });
    }

    public int getDuration() {
        return mp3DurationTime;
    }

    //根据公式：数据量=（采样频率×采样位数×声道数×时间）/8
    // playTime = Integer.parseInt(String.valueOf((tempSize * 8 * 1000) / (32000 * 16 * 1)));
    // 毫秒
    private long getSizeByTime(long time) {
        time = time / 1000;
        long size = (44100 * 16 * 1 * time) / 8;
        return size;
    }

    public interface OnProcessListener {
        void onProcessStart();

        void onProcessOver();

        void onFail();
    }

}
