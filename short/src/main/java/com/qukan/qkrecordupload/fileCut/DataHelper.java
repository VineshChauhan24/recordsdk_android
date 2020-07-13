package com.qukan.qkrecordupload.fileCut;

import com.qukan.qkrecordupload.ConfigureConstants;
import com.qukan.qkrecordupload.bean.AudioChange;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecorduploadsdk.utils.QLog;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

import static com.qukan.qkrecordupload.ConfigureConstants.QUKAN_PCM;

/**
 * Created by Administrator on 2017/12/28 0028.
 */

public class DataHelper {

    @Getter
    static DataHelper dataHelperInstance = new DataHelper();

    @Getter
    List<VideoProcessBean> videoLists = new ArrayList<>();

    public void initMusicData(List<VideoProcessBean> list) {

        videoLists.clear();
        for (VideoProcessBean videoProcessBean : list) {
            videoLists.add(videoProcessBean.copyVideoProcessBean());
        }

        long allTime = 0;
        for (VideoProcessBean newProcess : videoLists) {
            newProcess.setRecordStartTime(allTime);
            allTime += newProcess.getDurationTime();
            newProcess.setRecordEndTime(allTime);
        }
    }

    public void changeVideoList(List<VideoProcessBean> list) {
        if (list == null) {
            return;
        }
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
        String recordPath = ConfigureConstants.QUKAN_PCM_AUDIO;
        File recordFile = new File(recordPath);
        if (!recordFile.exists()) {
            QLog.e("本地没有配音文件=%s", recordPath);
            return;
        }
        try {
//            ByteBuffer bytes = ByteBuffer.allocate(2048);
            ByteBuffer buf = ByteBuffer.allocate(2048);
            ByteBuffer buf2 = ByteBuffer.allocate(2048);

            byte[] bytes = new byte[2048];
            RandomAccessFile tempFileRf = new RandomAccessFile(tempFile, "rw");
//            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            FileChannel writeChannel = tempFileRf.getChannel();
            RandomAccessFile recordFileRf = new RandomAccessFile(recordFile, "rw");
//            FileInputStream fileInputStream = new FileInputStream(recordFile);
            FileChannel readChannel = recordFileRf.getChannel();

            {
                //给文件注入空数据
                long allSize = computePosition(allTime / (float) 1000);
                writeChannel.position(0);
                while (writeChannel.position() < allSize) {
                    long surplusSize = allSize - writeChannel.position();
                    if (surplusSize > 2048) {
                        //channel.write(ByteBuffer.wrap(bytes));
                        writeChannel.write(ByteBuffer.wrap(bytes));
                    } else {
                        ByteBuffer surplusBytes = ByteBuffer.allocate((int) surplusSize);
                        writeChannel.write(ByteBuffer.wrap(new byte[(int) surplusSize]));
                    }
                    QLog.d("writeChannel.position()=%s,allSize=%s", writeChannel.position(), allSize);
                }
            }

            List<VideoProcessBean> newAddList = new ArrayList<>();

            for (VideoProcessBean newProcess : list) {
                VideoProcessBean copyProcess = newProcess.copyVideoProcessBean();
                for (VideoProcessBean oldProcess : videoLists) {
                    //计算需要写入临时文件的音频
                    List<AudioChange> listAudioChange = oldProcess.changeNewVideoProgress(copyProcess);
                    if (listAudioChange != null && listAudioChange.size() > 0) {
                        for (AudioChange audioChange : listAudioChange) {

                            //历史的开始和结束为止
                            long readOffset = computePosition(audioChange.getOldStartTime() / (float) 1000);
                            long readEnd = computePosition(audioChange.getOldEndTime() / (float) 1000);
                            readChannel.position(readOffset);

                            //新的开始和结束的位置
                            long writeOffset = computePosition(audioChange.getOldStartTime() / (float) 1000);
                            writeChannel.position(writeOffset);

                            //根据视频片段的长度写入文件
                            while (readChannel.position() < readEnd) {
                                //剩余的大小
                                long surplusSize = readEnd - readChannel.position();
                                if (surplusSize % 2 == 1) {
                                    surplusSize = surplusSize + 1;
                                }
                                if (surplusSize > 2048) {

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
                                    QLog.d("readSize=%s", Arrays.toString(buf.array()));
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
                }
                newAddList.add(copyProcess);
            }
            videoLists = newAddList;
            for (VideoProcessBean videoList : videoLists) {
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
        QLog.d("x=%s,name=%s", x, tempFile.getName());
        return;
    }

    //计算需要偏移的数据量 -- 公式：数据量=（采样频率×采样位数×声道数×时间(秒)）/ 8
    private long computePosition(float mAllTime) {
        long allSize = (long) ((AudioHelper.simpleRate * 16 * 1 * mAllTime) / 8);
        if (allSize % 2 == 1) {
            allSize = allSize - 1;
        }
        return allSize;
    }

}
