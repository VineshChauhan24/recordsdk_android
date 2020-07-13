package com.qukan.qkrecordupload.file;


import android.graphics.BitmapFactory;



import java.io.File;
import java.io.Serializable;

import lombok.Data;

//录像文件以及文件状态信息
@Data
public class FileInfoStatus implements Serializable {
    public static final double GB = 1024.0 * 1024 * 1024;
    public static final double MB = 1024.0 * 1024;
    public static final double KB = 1024.0;

    private int fileId;//数据库主键id

    private String fileName;
    // 实际的文件路径
    private String filePath;
    private String fileDisplayName;
    // 用于直播和录播文件在发送合并请求时,向数据库查询同一场活动的分片文件
    private String fileGroup;
    private int height;
    private int width;

    // 文件的大小
    private String fileLength;

    // 文件上传
    private String uploadName;
    private String uploadPath;

    private int pause;

    // 上传开始时间和结束时间
    private String start_time;
    private String end_time;

    public int getPause() {
        return DBHelper.instance().getFileInfoPause(this);
    }

    public void setPause(int pause) {
        DBHelper.instance().updatePause(pause,getFileId());
    }

    public String getUploadPath(){

//        String str_uploadPathaddress = FileOssClient.instance().getOssRes().getOssUploadRecordPath();
//        if(DBHelper.db_liveVideo.equals(getFileType())){
//            str_uploadPathaddress = FileOssClient.instance().getOssRes().getOssUploadLivePath();;
//        }else if(DBHelper.db_takePicture.equals(getFileType())){
//            str_uploadPathaddress = FileOssClient.instance().getOssRes().getOssUploadPicturePath();;
//        }
//
//        return  str_uploadPathaddress + getUploadName();
        return "";
    }

    // 文件类型，2：直播视频；3：录播视频
    private String fileType;

    private String userId;
    private String timeDate;
    private String timeLength;
    private boolean isGetLength;

    private long  liveId;//直播活动的id
    private boolean bSelect;

    private int   status;// 0 未上传(未同步) ， 1 上传中(同步中) ， 2 上传结束(本地已同步) ，3 提交中 ，4 提交结束 ， 5 合并完成
    private double percent;

    private  long fileTrueSize;

    public FileInfoStatus()
    {
        liveId = 0;
        status = 0;
        percent = 0;
        pause = 0;
        isGetLength = false;
    }
    public String getTimeLength(){

//        if(isGetLength){
//            return
//        }
//        if("00:00:00".equals(timeLength)||timeLength == null||timeLength.isEmpty()){
//
//        }
//        isGetLength = true;
        return timeLength;
    }
    public long getFileTrueSize() {
        File temp = new File(filePath);
        if (!temp.isFile())
        {
            return 0;
        }
        // 获取文件的大小
        return temp.length();
    }

    public double getPercent() {
        return percent;
    }

    public String getFileLength(){
        if("0".endsWith(fileLength)||"0B".endsWith(fileLength)){
            File temp = new File(filePath);

            long fileSize = temp.length();
            if (fileSize > GB)
            {
                fileLength =  String.format("%.2fGB", fileSize / GB);
            }
            else if (fileSize > MB)
            {
                fileLength = String.format("%.2fMB", fileSize / MB);
            }
            else if (fileSize > KB)
            {
                fileLength = String.format("%.2fKB", fileSize / KB);
            }
            else
            {
                fileLength = String.format("%dB", fileSize);
            }
            if(!("0".endsWith(fileLength)||"0B".endsWith(fileLength))){
                DBHelper.instance().update(this);
            }
        }
        return fileLength;
    }

    public int getWidth() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options); // 此时返回的bitmap为null

        return  options.outWidth;
    }
    public int getHeight() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options); // 此时返回的bitmap为null

        return  options.outHeight;
    }

}
