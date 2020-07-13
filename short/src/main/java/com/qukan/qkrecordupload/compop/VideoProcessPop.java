package com.qukan.qkrecordupload.compop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qukan.qkrecordupload.R;

import org.droidparts.annotation.inject.InjectView;

import lombok.Setter;
/**
 * Created by Administrator on 2017/2/14 0014.
 * 视频处理弹出的状态显示框
 */
public class VideoProcessPop extends BasePopupWindow {

    TextView tvMsg;

    TextView btnCancel;

//    @InjectView(id = R.id.btn_pause, click = true)
//    Button btn_pause;
//
//    @InjectView(id = R.id.btn_resume, click = true)
//    Button btn_resume;

    @Setter
    Listener listener;

    public VideoProcessPop(Context context) {
        super(context);
    }

    @Override
    public View setPopview() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_video_process, null);
        initView(view);
        return view;
    }

    private void initView(View v){
        tvMsg = v.findViewById(R.id.tv_msg);

        btnCancel = v.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
    }


    public void setTvMsg(String msg) {
        tvMsg.setText(msg);
    }

    public void setButtonShow() {
        btnCancel.setVisibility(View.VISIBLE);
    }

    public interface Listener {
        void onClickCancel(VideoProcessPop videoProcessPop);

        void onPause(VideoProcessPop videoProcessPop);

        void onResume(VideoProcessPop videoProcessPop);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            if (listener != null) {
                listener.onClickCancel(this);
            }
        }
//        if (v == btn_pause) {
//            if (listener != null) {
//                listener.onPause(this);
//            }
//        }
//        if (v == btn_resume) {
//            if (listener != null) {
//                listener.onResume(this);
//            }
//        }
    }
}
