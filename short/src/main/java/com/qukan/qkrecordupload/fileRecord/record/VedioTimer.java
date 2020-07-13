package com.qukan.qkrecordupload.fileRecord.record;

import android.os.Handler;

/**
 * Created by Administrator on 2018/7/26 0026.
 */

public class VedioTimer {

    private boolean isRecord = false;
    private long currentSecond = 0;//当前毫秒数
    private Handler mhandle = new Handler();
    private RecordListener listener;

    public VedioTimer(RecordListener listener){
        this.listener = listener;
    }

    public void start(){
        isRecord = true;
        timeRunable.run();
    }

    public void stop(){
        isRecord = false;
        currentSecond = 0;
    }

    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            if (isRecord) {
                //递归调用本runable对象，实现每隔一秒一次执行任务
                mhandle.postDelayed(this, 1000);
                currentSecond = currentSecond + 1000;
                listener.onTimeChange(currentSecond);
            }
        }
    };

    public interface RecordListener{
        /**
         * 时间改变回调
         * @param duration
         */
        void onTimeChange(long duration);
    }
}
