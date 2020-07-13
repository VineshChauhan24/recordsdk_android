package com.qukan.qkrecordupload.fileRecord;

/**
 * Created by Administrator on 2016-04-20.

*/
 import com.qukan.qkrecorduploadsdk.RecordContext;

 import lombok.Data;

 @Data

 public class CameraInfo {

     private int cameraSizeType = RecordContext.CAMERA_SIZE_640x480;
     private int videoDataRate = 500;

    private static CameraInfo _instance = new CameraInfo();
    // 返回单例
    public static CameraInfo instance()
    {
        return _instance;
    }


}
