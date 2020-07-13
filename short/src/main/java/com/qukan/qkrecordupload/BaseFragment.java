package com.qukan.qkrecordupload;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View.OnClickListener;

import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecorduploadsdk.utils.QLog;

import java.util.List;


public abstract class BaseFragment extends Fragment implements OnClickListener
{
    private ActivityResultGet activityResultGet = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(this.getClass().getName(), "onActivityCreated()");

        onPostActivityCreated();
    }

    // 活动创建后执行
    protected void onPostActivityCreated()
    {

    }

    @Override
    public void onResume() {
        super.onResume();
        QLog.d(this.getClass().getName(), "onResume");
    }

    public ActivityResultGet getActivityResultGet() {
        return activityResultGet;
    }

    public void setActivityResultGet(ActivityResultGet activityResultGet) {
        this.activityResultGet = activityResultGet;
    }

    public interface ActivityResultGet {

        /*
        获取视频或者图片
         */
        void onActivityResultGet(int requestCode, int resultCode, Intent data);

    }
}
