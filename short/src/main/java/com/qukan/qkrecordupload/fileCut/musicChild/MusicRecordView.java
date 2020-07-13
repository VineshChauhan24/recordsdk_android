package com.qukan.qkrecordupload.fileCut.musicChild;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.qukan.qkrecordupload.BaseActivity;
import com.qukan.qkrecordupload.QkApplication;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.RnToast;
import com.qukan.qkrecordupload.fileCut.AudioHelper;
import com.qukan.qkrecordupload.qkCut.MovieClipDataBase;

import org.droidparts.Injector;

import org.droidparts.util.L;

import lombok.Setter;


// 配音
public class MusicRecordView implements View.OnClickListener, AudioHelper.ProgressCallback {

    private ImageView ivRecord;

    private ImageView ivDelete;

    private CheckBox checkOriginBox;

    @Setter
    RecordCallback recordCallback;

    @Setter
    RecordStateCallback recordStateCallback;

    private int count = 3;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (count == 3) {
                ivRecord.setImageResource(R.drawable.short_ic_number_three);
                handler.postDelayed(this, 1000);
            } else if (count == 2) {
                ivRecord.setImageResource(R.drawable.short_ic_number_two);
                handler.postDelayed(this, 1000);
            } else if (count == 1) {
                ivRecord.setImageResource(R.drawable.short_ic_number_one);
                handler.postDelayed(this, 1000);
            } else {
                L.d("开始录音");
                ivRecord.setImageResource(R.drawable.short_ic_record_pause);
                startRecordAudio();
            }
            count--;
        }
    };
    private Context context;
    private View inflate;

    public MusicRecordView(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        inflate = LayoutInflater.from(context).inflate(R.layout.fragment_music_record_new, null);
        initView(inflate);
        Injector.inject(inflate, this);
        AudioHelper.getAudioHelperInstance().setProgressCallback(this);
        checkOriginBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (recordCallback != null) {
                    recordCallback.onClickSwitchVoice(isChecked);
                }
                if (isChecked) {
                    RnToast.showToast(context,"录音时监听原声");
                } else {
                    RnToast.showToast(context,"录音时关闭原声");
                }
            }
        });
    }

    private void initView(View view){
        ivRecord = view.findViewById(R.id.iv_record);
        ivRecord.setOnClickListener(this);

        ivDelete = view.findViewById(R.id.iv_delete);
        ivDelete.setOnClickListener(this);

        checkOriginBox = view.findViewById(R.id.cb_origin);
    }

    public void startRecordAudio() {
        state = ON_RECORDING;
        if (recordCallback != null) {
            boolean result = recordCallback.onStartRecord();
            if (result) {
                recordCallback.onClickSwitchVoice(checkOriginBox.isChecked());
            }
        }
    }

    public void stopRecordAudio() {
        state = ON_STOP;
        removeTimer();
        ivRecord.setImageResource(R.drawable.short_ic_record_btn_bg);
    }

    private void setTimer() {
        count = 3;
        handler.post(runnable);
    }

    public void removeTimer() {
        handler.removeCallbacksAndMessages(null);
        ivRecord.setImageResource(R.drawable.short_ic_record_btn_bg);
    }

    public View getPageView() {
        return this.inflate;
    }

    private int state = 0;
    private int ON_STOP = 0;
    private int ON_PREPARE = 1;
    private int ON_RECORDING = 2;

    @Override
    public void onClick(View v) {
        if (v == ivRecord) {
            // 开始倒计时录音
            if (state == ON_STOP) {
                if(MovieClipDataBase.instance().isPlayerEnd()){
                    RnToast.showToast(QkApplication.getContext(), "请调整录音开始时间");
                    return;
                }
                state = ON_PREPARE;
                setTimer();
                if (recordCallback != null) {
                    recordCallback.onClickPrepare();
                }
                ivRecord.setImageResource(R.color.short_color_14);
            } else if (state == ON_RECORDING) {
                // 停止录音
                stopRecordAudio();
                if (recordCallback != null) {
                    recordCallback.onClickStopRecord();
                }
                ivRecord.setImageResource(R.drawable.short_ic_record_btn_bg);
            } else {
                // 3.2.1进行中
                stopRecordAudio();
                if (recordCallback != null) {
                    recordCallback.onClickStopRecord();
                }
                ivRecord.setImageResource(R.drawable.short_ic_record_btn_bg);
            }

        } else if (v == ivDelete) {
            if (state == ON_STOP) {
                if (recordCallback != null) {
                    recordCallback.onClickDelete();
                }
            } else {
                RnToast.showToast(QkApplication.getContext(), "正在录音中");
            }

        }
    }

    @Override
    public void onProgress(long position) {
        if (recordStateCallback != null) {
            recordStateCallback.onProgress(position);
        }
    }

    // 不一定要点击停止按钮才发出这个通知，按了home键也会收到
    // 321时按停止按钮，不会收到，注意这个是子线程发出的消息，要在主线程更新ui
    // 录音工具发出通知录音停止了
    @Override
    public void onRecordStop() {
        BaseActivity activity = (BaseActivity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopRecordAudio();
            }
        });

        if (recordStateCallback != null) {
            recordStateCallback.onRecordStop();
        }
        ivRecord.setImageResource(R.drawable.short_ic_record_btn_bg);
    }

    public interface RecordCallback {
        // 倒计时开始时
        void onClickPrepare();

        // 倒计时结束开始录音
        boolean onStartRecord();

        void onClickStopRecord();

        void onClickDelete();

        // 点击了配音时监听原声
        void onClickSwitchVoice(boolean isOpen);

    }


    public interface RecordStateCallback {
        void onProgress(long position);

        // 停止的时候发出通知
        void onRecordStop();
    }

}
