package com.qukan.qkrecordupload.bean;



import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/10/25 0025..
 * 趣看公共音乐库
 */
@Setter
@Getter
public class MusicPublicBean{
    long id;
    String name="";
    String size="";
    String url=""; // 用url来命名下载的本地文件，可以防止名字相同的文件被覆盖
    int state; // 0未下载 1等待下载中 2下载中 3已下载--未选中 4 已下载--选中
    int progress;// 下载进度
    String path="";//本地保存路径
    long timeFlag; // 时间标记，用来排序，新的歌排在最上面
    int top=0; //0不置顶 1置顶 ，sqlite不支持boolean 是否置顶，如果第一次进入歌曲界面，以及下载的需要置顶，在界面当前进行下载的，下载完成不需要置顶
    int downOk; // 0 正常 1 文件异常-一般是下载异常
    String pcmPath = "";
}
