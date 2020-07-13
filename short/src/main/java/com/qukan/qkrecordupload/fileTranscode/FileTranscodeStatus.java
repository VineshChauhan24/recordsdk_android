package com.qukan.qkrecordupload.fileTranscode;

/**
 * Created by Administrator on 2016-05-18.
 */
import lombok.Data;


@Data
public class FileTranscodeStatus {
    private String name;
    private String transcodeName;
    private String status;//init , transcode , end
    private long percent;
}
