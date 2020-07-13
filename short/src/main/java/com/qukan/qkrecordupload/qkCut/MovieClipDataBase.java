package com.qukan.qkrecordupload.qkCut;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.cgfay.cainfilter.utils.AspectFrameLayout;
import com.qukan.qkrecordupload.bean.AudioChange;
import com.qukan.qkrecordupload.bean.AudioInfoBean;
import com.qukan.qkrecordupload.bean.TransferEffect;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.bean.VideoTitleUse;
import com.qukan.qkrecordupload.fileCut.AudioHelper;
import com.qukan.qkrecorduploadsdk.bean.PlayInfo;
import com.qukan.qkrecorduploadsdk.decoder.VideoPlayFinish;
import com.qukan.qkrecorduploadsdk.decoder.VideoPlayer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static com.qukan.qkrecordupload.ConfigureConstants.QUKAN_PCM;

public class MovieClipDataBase {
    private static final Object mLock = new Object();

    private final static int DrawType16x9 = 0;
    private final static int DrawType4x3 = 1;
    private final static int DrawType1x1 = 2;
    private final static int DrawType3x4 = 3;
    private final static int DrawType9x16 = 4;


    private boolean preView = false; //预览还是单个视频选择
    private int ratio = DrawType16x9;
    private double allLength = 0.0; //当前播放的视频的总长度
    private QKColorFilterGroup group = QKColorFilterGroup.getDefaultFilterGroup(); //当前色度等控制类
    private AudioInfoBean infoBean;  //配音、背景音乐等的控制类
    //所有字幕的信息
    List<VideoTitleUse> textStyleBeen = new ArrayList<>();


    private static MovieClipDataBase movieClipDataBase;
    private VideoPlayFinish videoPlayFinish;

    @Getter
    @Setter
    private VideoPlayer videoPlayer = null;
    private AspectFrameLayout mAspectLayout;
    private ArrayList<VideoProcessBean> array_movies = null;
    //当前选中的part
    private VideoProcessBean nowMoviePart;
    private Context context;
    private Activity activity;


    public static MovieClipDataBase instance() {
        synchronized (mLock) {
            if (movieClipDataBase == null) {
                movieClipDataBase = new MovieClipDataBase();
                movieClipDataBase.resetThings();
            }
            return movieClipDataBase;
        }
    }

    public void setContext(Context context, AspectFrameLayout mAspectLayout, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.mAspectLayout = mAspectLayout;
    }

    //清空所有数据
    public void resetThings() {
        stopPlayer();
        ratio = DrawType16x9;
        this.group = QKColorFilterGroup.getDefaultFilterGroup();
        this.nowMoviePart = null;
        this.infoBean = null;
        this.mAspectLayout = null;
        this.context = null;
        this.activity = null;
        textStyleBeen = new ArrayList<>();
    }

    public ArrayList<VideoProcessBean> getMovies() {
        return array_movies;
    }

    //这里会有一个copy的操作，要额外注意
    public void changeArrayMovies(ArrayList<VideoProcessBean> nowArray) {
        ArrayList<VideoProcessBean> array = new ArrayList<VideoProcessBean>();
        long time = 0;
        if (nowArray.size() > 0) {
            for (int i = 0; i < nowArray.size(); i++) {
                VideoProcessBean old = nowArray.get(i);
                old.setRecordStartTime(time);
                VideoProcessBean data = old.copyVideoProcessBean();
                time = (long) (time + (data.getEndTime() - data.getStartTime()) / data.getSpeed());
                data.setRecordEndTime(time);
                array.add(data);
            }
        }
        this.array_movies = array;
    }

    //设置音频文件
    public void setAudioInfoBean(AudioInfoBean bean) {
        infoBean = bean;
    }

    //设置音频文件
    public AudioInfoBean getAudioInfoBean() {
        return infoBean;
    }

    public void changeVideoList(List<VideoProcessBean> list) {
        if (list == null) {
            return;
        }
        int size = 1024 * 1024;
        long allTime = 0;
        for (VideoProcessBean newProcess : list) {
            allTime += newProcess.getEndTime() - newProcess.getStartTime();
        }
        //临时文件
        long tempInt = (long) ((Math.random() * 9 + 1) * 1000000);
        File tempFile = new File(QUKAN_PCM, "temp_" + tempInt + ".pcm");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        try {
            tempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (infoBean == null) {
            return;
        }
        String recordPath = infoBean.getRecordPath();
        File recordFile = new File(recordPath);
        if (!recordFile.exists()) {
            return;
        }
        try {
//            ByteBuffer bytes = ByteBuffer.allocate(2048);
            ByteBuffer buf = ByteBuffer.allocate(size);
            ByteBuffer buf2 = ByteBuffer.allocate(size);

            byte[] bytes = new byte[size];
            RandomAccessFile tempFileRf = new RandomAccessFile(tempFile, "rw");
//            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            FileChannel writeChannel = tempFileRf.getChannel();


            RandomAccessFile recordFileRf = new RandomAccessFile(recordFile, "rw");
//            FileInputStream fileInputStream = new FileInputStream(recordFile);
            FileChannel readChannel = recordFileRf.getChannel();

            {
                //  给临时文件注入空数据
                long allSize = computePosition(allTime / (float) 1000);
                writeChannel.position(0);
                while (writeChannel.position() < allSize) {
                    long surplusSize = allSize - writeChannel.position();
                    if (surplusSize > size) {
                        //channel.write(ByteBuffer.wrap(bytes));
                        writeChannel.write(ByteBuffer.wrap(bytes));
                    } else {
                        ByteBuffer surplusBytes = ByteBuffer.allocate((int) surplusSize);
                        writeChannel.write(ByteBuffer.wrap(new byte[(int) surplusSize]));
                    }
                }
            }

            ArrayList<VideoProcessBean> newAddList = new ArrayList<>();
            long time = 0;
            for (VideoProcessBean newProcess : list) {
                newProcess.setRecordStartTime(time);
                VideoProcessBean copyProcess = newProcess.copyVideoProcessBean();
                time = (long) (time + (copyProcess.getEndTime() - copyProcess.getStartTime()) / copyProcess.getSpeed());
                copyProcess.setRecordEndTime(time);

                for (VideoProcessBean oldProcess : array_movies) {
                    if (oldProcess.getSpeed() == copyProcess.getSpeed()) {
                        //计算需要写入临时文件的音频
                        List<AudioChange> listAudioChange = oldProcess.changeNewVideoProgress(copyProcess);
                        if (listAudioChange.size() > 0) {
                            for (AudioChange audioChange : listAudioChange) {

                                //历史的开始和结束为止
                                long readOffset = computePosition(audioChange.getOldStartTime() / (float) 1000);
                                long readEnd = computePosition(audioChange.getOldEndTime() / (float) 1000);
                                readChannel.position(readOffset);

                                // 新的开始和结束的位置
                                // 这里改了
                                long writeOffset = computePosition(audioChange.getStartTime() / (float) 1000);
                                writeChannel.position(writeOffset);
                                //根据视频片段的长度写入文件
                                while (readChannel.position() < readEnd) {
                                    //剩余的大小
                                    long surplusSize = readEnd - readChannel.position();
                                    // 必须是2的整数倍，16位音频数据，2个字节保存，否则会有杂音
                                    if (surplusSize % 2 == 1) {
                                        surplusSize = surplusSize + 1;
                                    }
                                    if (surplusSize > size) {

                                        buf.flip();
                                        buf.clear();
                                        int readSize = readChannel.read(buf);
                                        buf2.clear();
                                        buf2.put(buf.array());
                                        buf2.flip();
                                        writeChannel.write(buf2);

//                                    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
//                                    int readSize = readChannel.read(byteBuffer);
//                                    writeChannel.write(byteBuffer);
                                    } else {

                                        ByteBuffer bufN = ByteBuffer.allocate((int) surplusSize);
                                        ByteBuffer bufN2 = ByteBuffer.allocate((int) surplusSize);
                                        bufN.flip();
                                        bufN.clear();
                                        int readSize = readChannel.read(bufN);
                                        bufN2.clear();
                                        bufN2.put(bufN.array());
                                        bufN2.flip();
                                        writeChannel.write(bufN2);
//                                    ByteBuffer surplusBytes = ByteBuffer.allocate((int) surplusSize);
//                                    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[(int) surplusSize]);
//                                    int readSize = readChannel.read(byteBuffer);
//                                    writeChannel.write(byteBuffer);
                                    }
                                }
                            }
                        }
                    } else {
                        Log.d("speedChanged", "oldProcess.getSpeed() != copyProcess.getSpeed()");
                    }
                }
                newAddList.add(copyProcess);
            }
            array_movies = newAddList;
            for (VideoProcessBean videoList : array_movies) {
                videoList.getListAudios();
            }
            tempFileRf.close();
            recordFileRf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //删除源文件，将新文件改名--确保要在同一个目录下
        boolean x = recordFile.delete();
        boolean y = tempFile.renameTo(recordFile);   //改名
        return;
    }

    // -改变转场效果
    public void changeOneTransfer(TransferEffect transfer, long flag) {
        int count = 0;
        for (VideoProcessBean part : array_movies) {
            if (count == 0) {
                part.setTransferEffect(TransferEffect.getDefaultTrans());
            } else if (part.getFlag() == flag) {
                part.setTransferEffect(transfer);
            }
            count++;
        }
        changeTransfers();
    }

    // -改变转场效果
    public void changeAllTransfer(TransferEffect transfer) {
        int count = 0;
        for (VideoProcessBean part : array_movies) {
            if (count == 0) {
                part.setTransferEffect(TransferEffect.getDefaultTrans());
            } else {
                part.setTransferEffect(transfer);
            }
            count++;
        }
        changeTransfers();
    }

    public void changeTransfers() {
        if (videoPlayer != null && isPreView()) {
            ArrayList<VideoProcessBean> array = this.array_movies;
            ArrayList<PlayInfo> array_playInfos = new ArrayList<PlayInfo>();
            double startTime = 0;
            for (int i = 0; i < array.size(); i++) {
                VideoProcessBean moviePart = array.get(i);
                PlayInfo qk = new PlayInfo();

                qk.setSoftStartTime(moviePart.getStartTime() / 1000.0);
                qk.setSoftEndTime(moviePart.getEndTime() / 1000.0);

                qk.setSpeed(moviePart.getSpeed());
                qk.setFilterType(moviePart.getFilterType());
                qk.setUseOrientation(moviePart.getUseOrientation());

                qk.setStartTime(startTime);
                double endTime = startTime + (qk.getSoftEndTime() - qk.getSoftStartTime()) / moviePart.getSpeed();
                qk.setEndTime(endTime);
                startTime = endTime;

                qk.setPath(moviePart.getVideoPath());
                qk.setType(moviePart.getTransferEffect().getType());
                qk.setImage(moviePart.isImage());
                qk.setWidth(moviePart.getWidth());
                qk.setHeight(moviePart.getHeight());
                array_playInfos.add(qk);
                Log.d("changeTransfers", "moviePart.getTransferEffect().getType():" + moviePart.getTransferEffect().getType());
            }
            videoPlayer.setChangeMovieTransfer(array_playInfos);
        }
    }


    public void setRatio(int type) {
        type = type % 5;
        this.ratio = type;
    }

    public int getRatio() {
        return this.ratio;
    }

    //添加了一个视频
    public void addOnePart(VideoProcessBean part) {
        array_movies.add(part);
    }

    /*
     **分割一个视频
     */
    public VideoProcessBean setCutPart(VideoProcessBean part, double softTime) {
        VideoProcessBean partCut = part.copyVideoProcessBean();
        partCut.setFlag(VideoProcessBean.getNewFlag());
        partCut.setStartTime((long) (softTime * 1000));

        part.setEndTime((long) (softTime * 1000));

        for (int i = 0; i < array_movies.size(); i++) {
            VideoProcessBean nowPart = array_movies.get(i);
            if (nowPart == part) {
                array_movies.add(i + 1, partCut);
                break;
            }
        }
        return partCut;
    }


    /*
     **删除当前视频
     */
    public void removeNowPart() {
        if (nowMoviePart == null) {
            return;
        }
        array_movies.remove(nowMoviePart);
        nowMoviePart = null;
    }

    /*
     **创建预览的视频
     */
    public void createAllPlayer() {
        preView = true;
        createPlayer(array_movies, 1);
        nowMoviePart = null;
        addAllAudio();
    }

    public void getPicture() {
        if (videoPlayer != null) {
            videoPlayer.getPicture();
        }
    }

    //显示单个视频
    public void showOnePart(VideoProcessBean moviePart) {
        ArrayList<VideoProcessBean> array = new ArrayList<VideoProcessBean>();
        array.add(moviePart);
        preView = false;
        nowMoviePart = moviePart;
        createPlayer(array, 0);
    }

    //type( 0 显示单个完整的视频,1 显示裁剪后的视频
    public void createPlayer(ArrayList<VideoProcessBean> array, int type) {
        if (mAspectLayout != null) {
            mAspectLayout.removeAllViews();
        }

        stopPlayer();
        double seekToTime = 0;
        ArrayList<PlayInfo> array_playInfos = new ArrayList<PlayInfo>();
        double startTime = 0;
        for (int i = 0; i < array.size(); i++) {
            VideoProcessBean moviePart = array.get(i);
            PlayInfo qk = new PlayInfo();

            if (type == 0 && array.size() == 1) {
                qk.setSoftStartTime(0);
                qk.setSoftEndTime(moviePart.getLength() / 1000.0);
                seekToTime = moviePart.getStartTime() / 1000.0;
            } else {
                qk.setSoftStartTime(moviePart.getStartTime() / 1000.0);
                qk.setSoftEndTime(moviePart.getEndTime() / 1000.0);
            }
            qk.setSpeed(moviePart.getSpeed());
            qk.setFilterType(moviePart.getFilterType());
            qk.setUseOrientation(moviePart.getUseOrientation());

            qk.setStartTime(startTime);
            double endTime = startTime + (qk.getSoftEndTime() - qk.getSoftStartTime()) / moviePart.getSpeed();
            qk.setEndTime(endTime);
            startTime = endTime;

            qk.setPath(moviePart.getVideoPath());
            qk.setType(moviePart.getTransferEffect().getType());
            qk.setImage(moviePart.isImage());
            qk.setWidth(moviePart.getWidth());
            qk.setHeight(moviePart.getHeight());
            array_playInfos.add(qk);
//            Log.d("createPlayer","startTime:" + qk.getStartTime() + "  endTime:" + qk.getEndTime());
        }
        videoPlayer = new VideoPlayer(this.context);

        videoPlayer.setLocalFiles(array_playInfos);
        videoPlayer.startPlayerWithTime(seekToTime);
        if (videoPlayFinish != null) {
            videoPlayer.setVideoFinish(videoPlayFinish);
        }
        allLength = startTime;
        addGroupInfo();

        //更新UI
        if (mAspectLayout != null) {
            Log.d("mAspectLayout", "mAspectLayout.addView");
            mAspectLayout.addView(videoPlayer.getVideoSurface());
            mAspectLayout.requestLayout();
        }

    }

    //结束并释放播放器
    public void stopPlayer() {
        if (videoPlayer != null) {
            VideoPlayer videoPlayer = this.videoPlayer;
            this.videoPlayer = null;
            videoPlayer.setVideoFinish(null);
            videoPlayer.stopPlayer();
        }
    }


    //音频
    //添加所有音频
    public void addAllAudio() {
        Log.d("addAllAudio", "11");
        if (this.infoBean != null) {
            //设置背景音乐等
            AudioInfoBean audio = this.infoBean;
            if (videoPlayer == null) {
                return;
            }
            videoPlayer.setOrgSoundValue(audio.getOriginVolume() / 100.0f);
            videoPlayer.setBackSoundValue(audio.getBGMVolume() / 100.0f);
            videoPlayer.setRecordValue(audio.getRecordVolume() / 100.0f);
            videoPlayer.setBackgroundAudio(audio.getBackMusic());
            videoPlayer.setRecordAudio(audio.getRecordPath());
            Log.d("addAllAudio", "audio.getBackMusic():" + audio.getBackMusic() + " getRecordPath:" + audio.getRecordPath());
        }
    }


    //播放器的操作
    public void pauseIosPlayer() {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.pause();
    }

    //开始播放
    public void playIosPlayer() {
        boolean playEnd = isPlayerEnd();
        if (playEnd == true) {
            seekToTimeSync(0);
        }
        boolean pauseState = getPauseState();
        if (pauseState) {
            if (videoPlayer == null) {
                return;
            }
            videoPlayer.play();
        }
    }

    public double getCurrentTime() {
        return videoPlayer.getCurrentTime();
    }

    public double getSpeed() {
        if (isPreView() || nowMoviePart == null) {
            Log.e("getSpeed", "预览的时候不需要获取speed");
            return 1;
        }

        return nowMoviePart.getSpeed();
    }

    public boolean isPlayerEnd() {
        return videoPlayer.isPlayerEnd();
    }

    //从指定时间开始
    public void startPlayerWithTime(double time) {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.startPlayerWithTime(time);

    }

    //获取当前暂停状态
    public boolean getPauseState() {
        return videoPlayer.getPauseState();
    }

    //调整时间
    public void seekToTime(double time) {
        Log.d("seekToTime", "" + time);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.seekToTime(time);
    }

    public void seekToTimeSync(double time) {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.seekToTimeSync(time);
    }


    //修改原始声音大小，建议设置值 0～1之间。可以超过1，超过即放大音量
    public void setOrgSoundValue(float value) {
        Log.d("setOrgSoundValue", "" + value);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.setOrgSoundValue(value / 100.0f);
    }

    //修改背景声音大小，建议设置值 0～1之间。可以超过1，超过即放大音量
    public void setBackSoundValue(float value) {
        Log.d("setBackSoundValue", "" + value);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.setBackSoundValue(value / 100.0f);
    }

    //修改配音声音大小，建议设置值 0～1之间。可以超过1，超过即放大音量
    public void setRecordValue(float value) {
        Log.d("setRecordValue", "" + value);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.setRecordValue(value / 100.0f);
    }

    //设置背景音地址
    public void setBackgroundAudio(String path) {
        infoBean.setBackMusic(path);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.setBackgroundAudio(path);
    }

    //关闭背景音
    public void closeBackgroundAudio() {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.closeBackgroundAudio();
    }

    //设置配音地址
    public void setRecordAudio(String path) {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.setRecordAudio(path);
    }

    //取消配音地址
    public void closeRecordAudio() {
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.closeRecordAudio();
    }


    public void changeFilter(int type) {
        if (type < 0) {
            return;
        }
        if (this.nowMoviePart != null) {
            this.nowMoviePart.setFilterType(type);
        }
        if (!preView) {
            if (videoPlayer == null) {
                return;
            }
            videoPlayer.changeFilter(type);
        }
    }

    public void changeSpeed(double speed) {
        if (!preView) {
            if (videoPlayer == null) {
                return;
            }
            videoPlayer.changeSpeed(speed);
        }
    }

    //0 不旋转 1 90，2 180，3 270
    public void changeUseOrientation(int useOrientation) {
        if (!preView) {
            if (videoPlayer == null) {
                return;
            }
            videoPlayer.changeUseOrientation(useOrientation);
        }
    }


    public void setGroup(QKColorFilterGroup group) {
        this.group = group;
        addGroupInfo();
    }

    public QKColorFilterGroup getGroup() {
        if (group == null) {
            group = QKColorFilterGroup.getDefaultFilterGroup();
        }
        return group;
    }

    public void addGroupInfo() {
        if (this.group != null) {
            changeBrightness(this.group.getBrightness());
            changeSaturation(this.group.getSaturation());
            changeSharpness(this.group.getSharpness());
            changeContrast(this.group.getContrast());
            changeHue(this.group.getHue());
        }
    }

    //亮度-1~1 0是正常色
    public void changeBrightness(double brightness) {
        this.group.setBrightness(brightness);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.changeBrightness((float) brightness);
    }

    //对比度 0.0 to 4.0 1.0是正常色
    public void changeContrast(double contrast) {
        this.group.setContrast(contrast);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.changeContrast((float) contrast);
    }

    //饱和度  0.0  to 2.0 1是正常色
    public void changeSaturation(double saturation) {
        this.group.setSaturation(saturation);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.changeSaturation((float) saturation);
    }

    //锐度 -4.0 to 4.0, with 0.0 是正常色
    public void changeSharpness(double sharpness) {
        this.group.setSharpness(sharpness);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.changeSharpness((float) sharpness);
    }

    //设置色调 0 ~ 360 0.0 是正常色
    public void changeHue(double hue) {
        this.group.setHue(hue);
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.changeHue((float) hue);
    }

    public VideoPlayFinish getVideoPlayFinish() {
        return videoPlayFinish;
    }

    public void setVideoPlayFinish(VideoPlayFinish videoPlayFinish) {
        this.videoPlayFinish = videoPlayFinish;
    }

    public void draw() {
        videoPlayer.drawTest();
    }

    public boolean isPreView() {
        return preView;
    }

    public void setPreView(boolean preView) {
        this.preView = preView;
    }

    //计算需要偏移的数据量 -- 公式：数据量=（采样频率×采样位数×声道数×时间(秒)）/ 8
    private long computePosition(float mAllTime) {
        long allSize = (long) ((AudioHelper.simpleRate * 16 * 1 * mAllTime) / 8);
        if (allSize % 2 == 1) {
            allSize = allSize - 1;
        }
        return allSize;
    }

    public List<VideoTitleUse> getTextStyleBeen() {
        return textStyleBeen;
    }

    public void setTextStyleBeen(List<VideoTitleUse> textStyleBeen) {
        if (textStyleBeen == null) {
            return;
        }
        this.textStyleBeen = textStyleBeen;
    }
}
