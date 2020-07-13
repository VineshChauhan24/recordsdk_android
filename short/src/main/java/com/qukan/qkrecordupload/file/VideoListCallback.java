package com.qukan.qkrecordupload.file;


import com.qukan.qkrecordupload.bean.VideoProcessBean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/29 0029.
 */

public interface VideoListCallback {

    void onClickTransIcon(int position);

    void onClickVideo(int position);

    void onAddVideo(List<VideoProcessBean> list);

    void OnVideoMove();

}
