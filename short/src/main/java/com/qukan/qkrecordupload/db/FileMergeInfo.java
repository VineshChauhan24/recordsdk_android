package com.qukan.qkrecordupload.db;

import java.util.ArrayList;

import lombok.Data;

@Data
public class FileMergeInfo {
    private boolean sendMerge;
    private boolean merge;
    private String key;
    public ArrayList<String> fileList = new ArrayList<String>();
    public ArrayList<String> uploadList = new ArrayList<String>();

    public boolean getMerge(){
        return merge;
    }

    public boolean getSendMerge(){
        return sendMerge;
    }
}
