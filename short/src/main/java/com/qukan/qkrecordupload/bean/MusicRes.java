package com.qukan.qkrecordupload.bean;


import org.litepal.crud.DataSupport;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/10/25 0025.
 *
 */
@Setter
@Getter
public class MusicRes extends DataSupport {
    String name = "";
    String pcmPath = "";//pcm本地保存路径
    String url = ""; //本地音乐路径
    String musicAuthor = "";
    long timeFlag; // 时间标记，用来排序，新的歌排在最上面
    int state; // 0未下载 1等待下载中 2下载中 3已下载--未选中 4 已下载--选中
    /**
     * 歌曲长度
     */
    public int duration;
    /**
     * 歌曲的大小
     */
    public long size;
    boolean isChoose = false;
}
