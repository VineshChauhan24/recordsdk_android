package com.qukan.qkrecordupload;

import android.support.v4.app.Fragment;
import com.qukan.qkrecordupload.bean.VideoEditBean;
import com.qukan.qkrecordupload.bean.VideoProcessBean;

import java.util.List;

public class OutPutMessage {
    public static OutPutMessage outPutMessage = null;
    private static final Object mLock = new Object();

    public static OutPutMessage instance()
    {
        synchronized (mLock)
        {
            if (outPutMessage == null)
            {
                outPutMessage = new OutPutMessage();
            }
            return outPutMessage;
        }
    }

    private VideoEdictCallBack callBack = null;

    public VideoEdictCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(VideoEdictCallBack callBack) {
        this.callBack = callBack;
    }

    public interface VideoEdictCallBack {
        /*
        获取草稿
         */
        void onDraftGet(VideoEditBean bean);
        /*
        获取短视频生成后的地址
         */
        void onVideoOutPath(String outPath);
        /*
        获取视频录制的地址
         */
        void onVideoRecordPath(String outPath);

        /*
        获取视频或者图片
         */
        void onVideosGet(VideosGetCallback callBack,BaseFragment fragment);

    }

    public interface VideosGetCallback {

        /*
        获取视频或者图片
         */
        void onVideoList(List<VideoProcessBean> list);

    }
}
