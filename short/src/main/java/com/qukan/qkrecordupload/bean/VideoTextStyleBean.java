package com.qukan.qkrecordupload.bean;

import android.view.Gravity;

import com.qukan.qkrecordupload.R;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by Administrator on 2015/7/30 0030.
 */
@Data
public class VideoTextStyleBean implements Serializable
{
   private String mainText="杭州趣看科技报道";
   private String secondText="";
   // 默认白色
   private int mainTextColor= R.color.white;
   private int secondTextColor= R.color.white;
   private int mainBackgroundColor= R.color.transparent;
   private int secondBackgroundColor= R.color.transparent;
   private int gravity= Gravity.LEFT;
   private int gravityTwo= Gravity.LEFT;
   private int layout_gravity= Gravity.LEFT;
   private int layout_gravityTwo= Gravity.LEFT;
   private int offsetX;
   private int offsetY;
   private long startTimeInAll;
   private long endTimeInAll;
   // 字幕在视频块中出现的时间点，是绝对时间以视频的0秒开始计算
   private long startTimeInVideo;
   private long endTimeInVideo;
   private long flag=-1;

}
