package com.qukan.qkrecordupload.fileRecord.record;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qukan.qkrecordupload.R;

public class VideoFrag extends Fragment {
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vedio_fragment, null);
        Log.d("NewCameraActivity","initView(View view)");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("VideoFrag","this:"+this  + "  mContext:" + mContext);
    }
    //设置mBtnShutter为录制视频模式
    public void setVedioMode(boolean b){
        // 录制视频状态
    }
}
