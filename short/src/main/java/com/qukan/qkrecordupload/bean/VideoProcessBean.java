package com.qukan.qkrecordupload.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qukan.qkrecordupload.ConfigureConstants;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecorduploadsdk.RecordContext;
import com.qukan.qkrecorduploadsdk.bean.PlayInfo;

import org.droidparts.util.IOUtils;
import org.droidparts.util.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Created by Administrator on 2017/6/15 0015.
 */
@Data
public class VideoProcessBean implements Serializable{
    private static long flagCount = 0;

    public VideoProcessBean() {
        flagCount++;
        flag = flagCount;
    }
    //视频长度
    private long length;
    private String path;
    private String yuvPath;

    private int width;
    private int height;
    private int bitRate;
    //视频片段在当前这个片段被裁剪时的开始时间和结束时间(毫秒)--一段30s的视频在5-10秒处被裁剪，即为5000和10000
    private long startTime;
    private long endTime;

    //在总时间轴上的开始时间和结束时间
    private long recordStartTime = -1;
    private long recordEndTime;

    //视频裁剪区域的时间
    private long durationTime;

    //转场效果属性对象
    TransferEffect transferEffect = new TransferEffect();

    private List<AudioPartBean> listAudios = new ArrayList<>();

    public long getDurationTime() {
        if(speed <= 0 ){
            speed = 1;
        }
        return (long)((endTime - startTime)/speed);
    }

    public boolean isVideo() {
        return PublicUtils.isVideo(path);
    }

    public boolean isImage() {
        return PublicUtils.isImage(path);
    }


    private long flag;

    /*
    速度
    */
    private double speed = 1;
    private int filterType = 0;
    //主动旋转角度 0 不旋转 1：90度  2：180度 3：270度
    private int useOrientation = 0;


    public static long getNewFlag(){
        flagCount++;
        long nowFlag = flagCount;
        return nowFlag;
    }

    public long getFlag() {
        return flag;
    }

    public void setFlag(long flag) {
        this.flag = flag;
        if (flagCount <= flag) {
            flagCount = flag + 1;
        }
    }

    public VideoProcessBean copyVideoProcessBean() {
        VideoProcessBean newBean = new VideoProcessBean();
        newBean.path = this.path;
        newBean.yuvPath = this.yuvPath;
        newBean.width = this.width;
        newBean.height = this.height;
        newBean.bitRate = this.bitRate;
        newBean.length = this.length;
        newBean.startTime = this.startTime;
        newBean.endTime = this.endTime;
        newBean.recordStartTime = this.recordStartTime;
        newBean.recordEndTime = this.recordEndTime;
        newBean.durationTime = this.durationTime;
        newBean.transferEffect = this.transferEffect.copy();
        List<AudioPartBean> newListAudios = new ArrayList<>();
        for (AudioPartBean listAudio : this.listAudios) {
            newListAudios.add(listAudio.copy());
        }
        newBean.listAudios = newListAudios;
        newBean.flag = this.flag;

        newBean.speed = this.speed;
        newBean.filterType = this.filterType;
        newBean.useOrientation = this.useOrientation;
        return newBean;
    }

    // 添加一个新的音频片段,音频在总时间轴的开始时间和音频在总时间轴的结束时间
    public void setAudioNewPart(long newAudioStartTime, long newAudioEndTime) {
        // 如果这次录音跨越了这个片段的时候
        long partBeginTime = newAudioStartTime;
        long partEndtime = newAudioEndTime;
        if (partBeginTime < recordEndTime && partEndtime > recordStartTime) {
            long partRecordStartTime = 0;
            long partRecordEndTime = 0;
            if (partBeginTime > recordStartTime) {
                partRecordStartTime = partBeginTime;
            } else {
                partRecordStartTime = recordStartTime;
            }

            if (partEndtime < recordEndTime) {
                partRecordEndTime = partEndtime;
            } else {
                partRecordEndTime = recordEndTime;
            }


//            [part setNewRecordPart:partRecordStartTime endTime:partRecordEndTime];


            if (getListAudios().size() == 0) {
                AudioPartBean bean = getAudioPart(partRecordStartTime, partRecordEndTime);
                getListAudios().add(bean);
                return;
            }
            List<AudioPartBean> newAudioList = new ArrayList<>();
            for (AudioPartBean part : getListAudios()) {
                //排除无交集的音频
                if (part.getStartTime() > partRecordEndTime) {
                    newAudioList.add(part);
                    continue;
                }
                if (part.getEndTime() < partRecordStartTime) {
                    newAudioList.add(part);
                    continue;
                }

                //新录音的文件在中间的时候
                if (part.getStartTime() < partRecordStartTime && part.getEndTime() > partRecordEndTime) {
                    AudioPartBean part1 = getAudioPart(part.getStartTime(), partRecordStartTime);
                    AudioPartBean part2 = getAudioPart(partRecordEndTime, part.getEndTime());

                    newAudioList.add(part1);
                    newAudioList.add(part2);

                }
                if (part.getStartTime() >= partRecordStartTime && part.getEndTime() > partRecordEndTime) {
                    AudioPartBean part1 = getAudioPart(partRecordEndTime, part.getEndTime());
                    newAudioList.add(part1);
                }
                if (part.getStartTime() < partRecordStartTime && part.getEndTime() <= partRecordEndTime) {
                    AudioPartBean part1 = getAudioPart(part.getStartTime(), partRecordStartTime);
                    newAudioList.add(part1);
                }
            }
            AudioPartBean part = getAudioPart(partRecordStartTime, partRecordEndTime);
            newAudioList.add(part);
            setListAudios(newAudioList);
        }
    }

    //修改视频片段发生了改变
    public List<AudioChange> changeNewVideoProgress(VideoProcessBean newProgress) {
        List<AudioChange> list = new ArrayList<>();
        if (getListAudios().size() == 0 || newProgress.flag != getFlag()) {
            return list;
        }
        for (AudioPartBean partAudio : getListAudios()) {
            //这个录音与新的片段存在重合部分，那么就复制这个文件
            if (partAudio.getRecordStartTime() < newProgress.getEndTime() && partAudio.getRecordEndTime() > newProgress.getStartTime()) {

                long startTime = partAudio.getStartTime() + (long)((Max(partAudio.getRecordStartTime(), newProgress.getStartTime()) - partAudio.getRecordStartTime())/newProgress.getSpeed());

                long endTime = partAudio.getEndTime() + (long)((Min(partAudio.getRecordEndTime(), newProgress.getEndTime()) - partAudio.getRecordEndTime())/newProgress.getSpeed());

                long newStartTime = 0;
                long newEndTime = 0;

                if (partAudio.getRecordStartTime() > newProgress.getStartTime()) {
                    newStartTime = newProgress.getRecordStartTime() + (long)((partAudio.getRecordStartTime() - newProgress.getStartTime())/newProgress.getSpeed());
                } else {
                    newStartTime = newProgress.getRecordStartTime();
                }
                newEndTime = newStartTime + endTime - startTime;
                newProgress.setAudioNewPart(newStartTime, newEndTime);

                AudioChange audioChg = new AudioChange();
                audioChg.setOldStartTime(startTime);
                audioChg.setOldEndTime(endTime);
                // 这里改了
                audioChg.setStartTime(newStartTime);
                audioChg.setEndTime(newEndTime);
                list.add(audioChg);
            }
        }
        return list;
    }

    //删除片段
    public void deleteAudioChange(long startTime, long endTime) {
        if (getRecordStartTime() < endTime && getRecordEndTime() > startTime) {
            List<AudioPartBean> list = getListAudios();
            for (AudioPartBean partAudio : list) {
                //这个录音与新的片段存在重合部分，那么久复制这个文件
                if (partAudio.getStartTime() == startTime && partAudio.getEndTime() == endTime) {
                    list.remove(partAudio);
                    L.d("删除了片段");
                    return;
                }
            }
        }

    }

    private long Max(long value1, long value2) {
        if (value1 > value2) {
            return value1;
        }
        return value2;
    }

    private long Min(long value1, long value2) {
        if (value1 > value2) {
            return value2;
        }
        return value1;
    }

    private AudioPartBean getAudioPart(long audioPartStartTime, long audioPartEndTime) {
        AudioPartBean bean = new AudioPartBean();
        long recordVideoStartTime = (long)((audioPartStartTime - recordStartTime)*getSpeed() + startTime);
        long recordVideoEndTime = (long)(recordVideoStartTime + (audioPartEndTime - audioPartStartTime)*getSpeed());
        bean.setStartTime(audioPartStartTime);
        bean.setEndTime(audioPartEndTime);
        bean.setRecordStartTime(recordVideoStartTime);
        bean.setRecordEndTime(recordVideoEndTime);
        return bean;
    }

    public static long getFlagCount() {
        return flagCount;
    }

    public static void setFlagCount(long flagCount) {
        VideoProcessBean.flagCount = flagCount;
    }

    public long getLength() {
        if(isImage()){
            return 20000;
        }
        if(length == 0 && endTime > 0){
            length = endTime;
        }
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if(PublicUtils.isImage(path)){
            String yuvPath = getImgPath();
            Bitmap bitmap = compressImageFromFile(path,1920.0f,1080.0f);

            Log.d("isImage","width:" +bitmap.getWidth() + "  height:" + bitmap.getHeight());



            setWidth(bitmap.getWidth());
            setHeight(bitmap.getHeight());

            writeFileData(yuvPath,bitmap);
            this.path = path;
            this.yuvPath = yuvPath;
        }else{
            this.path = path;
            this.yuvPath = "";
        }

    }
    public String getVideoPath() {
        if(isImage()){
            return yuvPath;
        }else{
            return path;
        }
    }

    private String getImgPath() {
        String outPath = ConfigureConstants.QUKAN_CACHE_IMAGE;
        makeRootDirectory(outPath);
        String time = "img" + PublicUtils.getCurrentTime() + flag;
        String path = outPath + File.separator + time + ".yuv";
        return path;
    }


    /**
     * 将图片从本地读到内存时,进行压缩 ,即图片从File形式变为Bitmap形式
     * 特点: 通过设置采样率, 减少图片的像素, 达到对内存中的Bitmap进行压缩
     * @param srcPath
     * @return
     */
    public static Bitmap compressImageFromFile(String srcPath, float pixWidth, float pixHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);

        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        if(w < h && pixWidth > pixHeight){
            float between = pixWidth;
            pixWidth = pixHeight;
            pixHeight = between;
        }else if(w > h && pixWidth < pixHeight){
            float between = pixWidth;
            pixWidth = pixHeight;
            pixHeight = between;
        }
        Log.d("compressImage","w:" + w + "  h:" + h);
        int scale = 1;
        if (w > h && w > pixWidth) {
            scale = (int) (options.outWidth / pixWidth);
        } else if (w < h && h > pixHeight) {
            scale = (int) (options.outHeight / pixHeight);
        }
        if (scale <= 0)
            scale = 1;
        options.inSampleSize = scale;// 设置采样率

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;// 该模式是默认的,可不设
        options.inPurgeable = true;// 同时设置才会有效
        options.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, options);
        if(bitmap.getWidth() %16 != 0 || bitmap.getHeight()%16 != 0){
            bitmap = ScaleBitmap(bitmap);
        }
        // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        // 其实是无效的,大家尽管尝试
        return bitmap;
    }
    public static Bitmap ScaleBitmap(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(height%16 != 0){
            int six = height / 16;
            height = six * 16;
        }

        if(width%16 != 0){
            int six = width / 16;
            width = six * 16;
        }
        QKLiveHelper.getInstance().init(width,height);

        return QKLiveHelper.getInstance().ScaleBitmap(bitmap);
    }

    //向指定的文件中写入指定的数据
    public void writeFileData(String filename, Bitmap bitmap){

        try {
            // bmp数据
            ByteBuffer dst = ByteBuffer.allocate(bitmap.getByteCount());
            bitmap.copyPixelsToBuffer(dst);
            // 将dst中的提出来
            dst.position(0);
            dst.limit(bitmap.getByteCount());
            byte[] acBuffer = new byte[bitmap.getByteCount()];
            dst.get(acBuffer, 0, bitmap.getByteCount());

            makeFilePath(filename);
            File file = new File(filename);
            FileOutputStream fos = new FileOutputStream(file);//获得FileOutputStream

            //将要写入的字符串转换为byte数组

            byte[]  bytes = acBuffer;
            Log.d("writeFileData","bitmap.getByteCount():" + bitmap.getByteCount() + "  bytes:" + bytes.length);
            fos.write(bytes,0,bitmap.getByteCount());//将byte数组写入文件

            fos.close();//关闭文件输出流

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 生成文件
    public File makeFilePath( String fileName) {
        File file = null;
        try {
            file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }
    public int getWidth() {

        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {

        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getRecordStartTime() {
        return recordStartTime;
    }

    public void setRecordStartTime(long recordStartTime) {
        this.recordStartTime = recordStartTime;
    }

    public long getRecordEndTime() {
        return recordEndTime;
    }

    public void setRecordEndTime(long recordEndTime) {
        this.recordEndTime = recordEndTime;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }

    public TransferEffect getTransferEffect() {
        return transferEffect;
    }

    public void setTransferEffect(TransferEffect transferEffect) {
        this.transferEffect = transferEffect;
    }

    public List<AudioPartBean> getListAudios() {
        return listAudios;
    }

    public void setListAudios(List<AudioPartBean> listAudios) {
        this.listAudios = listAudios;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if(speed <= 0){
            Log.e("error","速度不能小于或等于0");
            speed = 1;
        }
        if(speed > 2){
            Log.e("提醒","Android建议速度在2以内");
        }
        this.speed = speed;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public int getUseOrientation() {
        return useOrientation;
    }

    public void setUseOrientation(int useOrientation) {
        this.useOrientation = useOrientation;
    }
}
