package com.qukan.qkrecordupload.fileCut;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/12/11 0011.
 * 用来录音
 */

public class AudioHelper implements BaseAudioRecord {

    String mPath;

    // 秒
    float mAllTime;

    long mStartTime;

    // 采样频率
    static public int simpleRate = 44100;
    int channelFormat = AudioFormat.CHANNEL_IN_MONO;

    private volatile boolean isRecording = false; //标记

    AudioRecord localAudioRecord = null;

    File recordFile;

    // 录音开始时从什么位置写入音频数据
    long offsetPosition;

    @Getter
    static AudioHelper audioHelperInstance = new AudioHelper();

    // 单位秒
    // 初始化，用来设置一条录音路径
    boolean initOver = false;

    @Override
    public void initAudio(String path, float allTime) {
        recordFile = new File(path);
        //QLog.d("recordFile.exists=%s", recordFile.exists());
        if (!recordFile.exists()) {
            try {
                recordFile.getParentFile().mkdirs();
                recordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            // 写入空数据
            putEmptyData(recordFile, 0, allTime);
        }

        initOver = true;
        mAllTime = allTime;
    }

    int size = 1024 * 1024;
    // 删除录音片段，即把相应的时间片段数据置零
    private void putEmptyData(File file, float mStartTime, float mEndTime) {
        //QLog.d("mStartTime=%s,mEndTime=%s", mStartTime, mEndTime);
        long startPosition = computePosition(mStartTime);
        long endPosition = computePosition(mEndTime);
        byte[] bytes = new byte[size];
        RandomAccessFile rf = null;
        FileChannel channel = null;
        try {
            rf = new RandomAccessFile(file, "rw");
            channel = rf.getChannel();
            // 把写入位点移到相应位置，position是当前的写入点，会随着write方法的调用，不断后移
            channel.position(startPosition);
            while (channel.position() < endPosition) {
                long surplusSize = endPosition - channel.position();
                if (surplusSize > size) {
                    channel.write(ByteBuffer.wrap(bytes));
                } else {
                    channel.write(ByteBuffer.wrap(new byte[(int) surplusSize]));
                }
//                channel.write(ByteBuffer.wrap(bytes));
//                //QLog.d("startPosition=%s,endPosition=%s，channel.position=%s,recordFile.length()=%s", startPosition, endPosition, channel.position(), file.length());
            }
            rf.close();
            channel.close();
        } catch (IOException e) {
            try {
                rf.close();
                channel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return;
        }
    }

    RecordTask recorder;

    @Override
    public boolean startAudio(float startTime) {
        if (isRecording) {
            //QLog.e("recording");
            return false;
        }
        if (startTime < 0) {
            startTime = 0;
        }
        offsetPosition = computePosition(startTime);
        //QLog.d("startAudio-->startTime=%s,offsetPosition=%s", startTime, offsetPosition);
        //这里启动录制任务
        recorder = new RecordTask();
        LinkedBlockingQueue blockingQueue = new LinkedBlockingQueue();
        ExecutorService exec = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, blockingQueue);
        recorder.executeOnExecutor(exec);
        return true;
//        recorder.execute();
    }

    @Override
    public void stopAudio() {
        //QLog.d("stopAudio");
        isRecording = false;
    }

    @Override
    public void changeTime(float newAllTime) {

    }

    @Override
    public void deleteRecord(float startTime, float endTime) {
        if (initOver) {
            putEmptyData(recordFile, startTime, endTime);
        } else {
            //QLog.e("deleteRecord---未初始化");
        }

    }

    //计算需要偏移的数据量 -- 公式：数据量=（采样频率×采样位数×声道数×时间(秒)）/ 8
    private long computePosition(float mAllTime) {
        long allSize = (long) ((simpleRate * 16 * 1 * mAllTime) / 8);
        if (allSize % 2 == 1) {
            allSize = allSize - 1;
        }
        return allSize;
    }

    private class RecordTask extends AsyncTask<Void, Long, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            //QLog.d("doInBackground1");
            isRecording = true;
            try {
                //开通输出流到指定的文件
//              RandomAccessFile只用用这个类获取的FileChannel才能在文件中的任何位置读写
                RandomAccessFile rf = new RandomAccessFile(recordFile, "rw");
                FileChannel channel = rf.getChannel();
                channel.position(offsetPosition);
                channel.force(true);

                // 设置线程优先级,非常高的优先级
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                int audioSource = MediaRecorder.AudioSource.DEFAULT;

                // 录制缓冲大小
                int minBufSize = AudioRecord.getMinBufferSize(simpleRate, channelFormat, AudioFormat.ENCODING_PCM_16BIT);
                localAudioRecord = new AudioRecord(audioSource, simpleRate, channelFormat, AudioFormat.ENCODING_PCM_16BIT, minBufSize * 4);

                //定义缓冲
                byte[] buffer = new byte[2048];
                //开始录制
                localAudioRecord.startRecording();

                long allPosition = computePosition(mAllTime);
                //QLog.d("offsetPosition=%s,allPosition=%s", offsetPosition, allPosition);
                // 定义循环，根据isRecording的值来判断是否继续录制
                while (isRecording) {
                    //QLog.d("isRecording1");
                    if (!initOver) {
                        //QLog.d("init not over");
                        continue;
                    }
                    int bufferReadResult = localAudioRecord.read(buffer, 0, buffer.length);
                    if (bufferReadResult <= 0) {
                        //QLog.e("isRecording-read failed,  bufferReadResult = %d", bufferReadResult);
                        break;
                    }
                    if (channel.position() >= allPosition) {
                        //QLog.d("isRecording-channel.position() >= allPosition--stopRecord");
                        break;
                    }
                    try {
//                        channel.lock();
                        if (isRecording) {
                            publishProgress(channel.position()); //向UI线程报告当前进度integer
                        }

                        channel.write(ByteBuffer.wrap(buffer));
                        //QLog.d("isRecording4");
                    } catch (IOException ex) {
                        //QLog.d("isRecording5");
                        rf.close();
                        channel.close();
                    }
                    //QLog.d("record=%s,minBufSize=%s,recordFile.length=%s", bufferReadResult, minBufSize, recordFile.length());

                }
                //录制结束
                localAudioRecord.stop();
                localAudioRecord.release();
                localAudioRecord = null;
                //QLog.d("The DOS available:%s", "::" + recordFile.length());
                rf.close();
                channel.close();
                // 把标记值改掉
                stopAudio();
                if (progressCallback != null) {
                    progressCallback.onRecordStop();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error","isRecording--error:");
                // TODO: handle exception
            }
            return null;
        }


        //当在上面方法中调用publishProgress时，该方法触发,该方法在UI线程中被执行
        protected void onProgressUpdate(Long... progress) {
            long currentPosition = progress[0];
            if (progressCallback != null) {
                progressCallback.onProgress(currentPosition);
            } else {
                //QLog.e("progressCallback==null");
            }
        }

        protected void onPostExecute(Void result) {

        }

        protected void onPreExecute() {
            isRecording = true;
            //QLog.d("onPreExecute-----1");
            //stateView.setText("正在录制");
        }

    }

    @Setter
    public ProgressCallback progressCallback;

    public interface ProgressCallback {
        void onProgress(long position);

        // 停止的时候发出通知
        void onRecordStop();
    }
}
