package com.qukan.qkrecordupload.fileUpload;

import com.qukan.qkrecorduploadsdk.bean.FileInfo;

import lombok.Data;

//录像文件以及文件状态信息
@Data
public class FileInfoStatus {
    private FileInfo info;
    private String   status;// init 未上传 ， syncing 上传中 ， synced 上传结束 ，uploading 提交中 ，uploaded 提交结束 ， merge 合并中 ， end 结束
    private long percent;
    private boolean pasuse;

    public boolean getPasuse(){
        return pasuse;
    }

    public FileInfoStatus()
    {
        info = new FileInfo();
        percent = 0;
        pasuse = false;
    }

    public FileInfo getInfo() {
        return info;
    }

    public void setInfo(FileInfo info) {
        this.info = info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getPercent() {
        return percent;
    }

    public void setPercent(long percent) {
        this.percent = percent;
    }

    public boolean isPasuse() {
        return pasuse;
    }

    public void setPasuse(boolean pasuse) {
        this.pasuse = pasuse;
    }
}
