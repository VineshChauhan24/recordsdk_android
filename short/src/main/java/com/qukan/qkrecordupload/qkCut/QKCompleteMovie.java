package com.qukan.qkrecordupload.qkCut;

import android.content.Context;
import android.util.Log;

import com.qukan.qkrecordupload.bean.AudioInfoBean;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecorduploadsdk.bean.PlayInfo;
import com.qukan.qkrecorduploadsdk.decoder.CompleteControl;
import com.qukan.qkrecorduploadsdk.decoder.VideoComplete;

import java.util.ArrayList;

public class QKCompleteMovie {
    private CompleteControl completeControl;

    VideoComplete complete;
    private QKColorFilterGroup group;
    public QKCompleteMovie( Context context, int width, int height, String outpath, ArrayList<VideoProcessBean> array){
        complete = new VideoComplete(context,width,height,outpath);
        double seekToTime = 0;
        ArrayList<PlayInfo> array_playInfos = new ArrayList<PlayInfo>();
        double startTime = 0;
        for(int i =0;i<array.size();i++){
            VideoProcessBean moviePart = array.get(i);
            PlayInfo qk = new PlayInfo();
            qk.setSoftStartTime(moviePart.getStartTime()/ 1000.0);
            qk.setSoftEndTime(moviePart.getEndTime()/ 1000.0);
            qk.setSpeed(moviePart.getSpeed());
            qk.setFilterType(moviePart.getFilterType());
            qk.setUseOrientation(moviePart.getUseOrientation());

            qk.setStartTime(startTime);
            double endTime = startTime + (qk.getSoftEndTime() - qk.getSoftStartTime())/moviePart.getSpeed();
            qk.setEndTime(endTime);
            startTime = endTime;

            qk.setPath(moviePart.getVideoPath());
            qk.setType(moviePart.getTransferEffect().getType());
            qk.setImage(moviePart.isImage());
            qk.setWidth(moviePart.getWidth());
            qk.setHeight(moviePart.getHeight());
            array_playInfos.add(qk);
            Log.d("QKCompleteMovie","start:" + qk.getStartTime() + "  end:" + qk.getEndTime());
        }
        complete.setLocalFiles(array_playInfos);
    }

    public CompleteControl getCompleteControl() {
        return completeControl;
    }

    public void setCompleteControl(CompleteControl completeControl) {
        this.completeControl = completeControl;
        if(complete != null){
            complete.setCompleteControl(completeControl);
        }
    }
    //取消生成视频，你需要等onStop回调通知到以后才能关闭取消的界面
    public void stopEncode(){
        if(complete != null){
            complete.stopEncodeFile();
        }
    }

    //按home键传false  回来 传true
    public void setHomeKey(boolean homeKey){
        if(complete != null){
            complete.setHomeKey(homeKey);
        }
    }

    public VideoComplete getComplete() {
//        Log.d("getComplete:","getComplete:");

        return complete;
    }

    public void setComplete(VideoComplete complete) {
        this.complete = complete;
    }



    //添加所有音频
    public void addAllAudio(AudioInfoBean audio){
        if(audio != null){
            Log.d("audio:","OriginVolume():"+ audio.getOriginVolume());
            Log.d("audio:","getBGMVolume():"+ audio.getBGMVolume());
            Log.d("audio:","getRecordVolume():"+ audio.getRecordVolume());
            //设置背景音乐等
            complete.setOrgSoundValue(audio.getOriginVolume()/100.0f);
            complete.setBackSoundValue(audio.getBGMVolume()/100.0f);
            complete.setRecordValue(audio.getRecordVolume()/100.0f);
            complete.setBackgroundAudio(audio.getBackMusic());
            complete.setRecordAudio(audio.getRecordPath());
        }
    }



    public void setGroup(QKColorFilterGroup group){
        this.group = group;
        addGroupInfo();
    }

    public QKColorFilterGroup getGroup(){
        if(group == null){
            group = QKColorFilterGroup.getDefaultFilterGroup();
        }
        return group;
    }
    public void addGroupInfo(){
        if(this.group != null){
            changeBrightness(this.group.getBrightness());
            changeSaturation(this.group.getSaturation());
            changeSharpness(this.group.getSharpness());
            changeContrast(this.group.getContrast());
            changeHue(this.group.getHue());
        }
    }
    //亮度-1~1 0是正常色
    public void changeBrightness(double brightness)
    {
        this.group.setBrightness(brightness);
        complete.changeBrightness((float)brightness);
    }
    //对比度 0.0 to 4.0 1.0是正常色
    public void changeContrast(double contrast)
    {
        this.group.setContrast(contrast);
        complete.changeContrast((float)contrast);
    }
    //饱和度  0.0  to 2.0 1是正常色
    public void changeSaturation(double saturation)
    {
        this.group.setSaturation(saturation);
        complete.changeSaturation((float)saturation);
    }
    //锐度 -4.0 to 4.0, with 0.0 是正常色
    public void changeSharpness(double sharpness)
    {
        this.group.setSharpness(sharpness);
        complete.changeSharpness((float)sharpness);
    }

    //设置色调 0 ~ 360 0.0 是正常色
    public void changeHue(double hue)
    {
        this.group.setHue(hue);
        complete.changeHue((float)hue);
    }

}
