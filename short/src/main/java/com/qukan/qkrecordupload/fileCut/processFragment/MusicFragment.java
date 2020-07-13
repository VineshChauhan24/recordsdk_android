package com.qukan.qkrecordupload.fileCut.processFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.RnToast;
import com.qukan.qkrecordupload.bean.AudioInfoBean;
import com.qukan.qkrecordupload.bean.MusicRes;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.custom.CustomViewPager;
import com.qukan.qkrecordupload.fileCut.AudioHelper;
import com.qukan.qkrecordupload.fileCut.Callback.MusicCallBack;
import com.qukan.qkrecordupload.fileCut.musicChild.BackMusicActivity;
import com.qukan.qkrecordupload.fileCut.musicChild.MusicAdjustView;
import com.qukan.qkrecordupload.fileCut.musicChild.MusicMp3View;
import com.qukan.qkrecordupload.fileCut.musicChild.MusicRecordView;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;
import com.qukan.qkrecordupload.qkCut.MovieClipDataBase;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static android.app.Activity.RESULT_OK;

public class MusicFragment extends BaseFragment implements ViewPager.OnPageChangeListener, MusicRecordView.RecordCallback, MusicRecordView.RecordStateCallback, MusicAdjustView.MusicAdjustCallback, MusicMp3View.OnClickMusicListener {

    private NewVideoProcessActivity activity;

    private CustomViewPager vpMusic;

    private TabLayout tabLayout;

//    @InjectView(id = R.id.cb_origin)
//    private CheckBox checkOriginBox;

    private RelativeLayout rlLock;

    MusicAdjustView musicAdjustView;
    MusicMp3View musicMp3View;
    MusicRecordView musicRecordView;

    @Setter
    MusicCallBack musicCallBack;

    AudioInfoBean audioInfo;

    @Getter
    public boolean isRecord = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NewVideoProcessActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_new, null);
        initView(view);
        return view;
    }

    private void initView(View view){
        vpMusic = view.findViewById(R.id.vp_music);
        tabLayout = view.findViewById(R.id.tab_layout);
        rlLock = view.findViewById(R.id.rl_lock);
        rlLock.setOnClickListener(this);
    }

    @Override
    protected void onPostActivityCreated() {
        super.onPostActivityCreated();
        vpMusic.setAdapter(new ViewPagerAdapter());
        vpMusic.addOnPageChangeListener(this);

        musicAdjustView = new MusicAdjustView(activity);
        musicMp3View = new MusicMp3View(activity, this);
        musicRecordView = new MusicRecordView(activity);

        vpMusic.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(vpMusic);

        musicRecordView.setRecordCallback(this);
        musicAdjustView.setMusicAdjustCallback(this);
        musicMp3View.setOnClickMusicListener(this);
    }

    // home键时停止录音
    @Override
    public void onPause() {
        super.onPause();
        AudioHelper.getAudioHelperInstance().stopAudio();
        musicRecordView.stopRecordAudio();
        isRecord = false;
        if (musicCallBack != null) {
            musicCallBack.onRecordStop();
        }
    }

    // 视频列表
    public void init(final AudioInfoBean audioInfo) {
        this.audioInfo = audioInfo;
        List<VideoProcessBean> videoLists = MovieClipDataBase.instance().getMovies();
        long allTime = 0;
        for (VideoProcessBean newProcess : videoLists) {
            allTime += newProcess.getDurationTime();
        }
        AudioHelper.getAudioHelperInstance().initAudio(audioInfo.getRecordPath(), allTime / (float) 1000);

        musicRecordView.setRecordStateCallback(this);
        musicAdjustView.init(audioInfo);
        musicMp3View.init(audioInfo);

    }

    // 录音开始时间和结束时间
    @Setter
    long startTime = 0;
    long endTime = 0;

    // 根据数据量计算时间(秒)
    private float computeTime(long position) {
        return position * 8 / (float) (AudioHelper.simpleRate * 16);
    }

    // 获取各种信息--录音地址，各种配音大小等
    public AudioInfoBean getAudioInfo() {
        return audioInfo;
    }

    @Override
    public void onClick(View v) {
        if (v == rlLock) {
            RnToast.showToast(getActivity(), getString(R.string.on_recording));
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // 倒计时开始
    @Override
    public void onClickPrepare() {
        // 这边需要在回调中让外面传一个开始时间进来
        isRecord = true;
        if (musicCallBack != null) {
            musicCallBack.onPrepare();
        }
        oldTime = startTime;
        endTime = 0;

    }

    // 点击了音频按钮321后开始录音
    @Override
    public boolean onStartRecord() {
        boolean result = AudioHelper.getAudioHelperInstance().startAudio(startTime / (float) 1000);
        if (musicCallBack != null && result) {
            musicCallBack.onRecordStart(startTime);
        } else {
            musicRecordView.stopRecordAudio();
        }
        return result;
    }

    // 点击了停止录制按钮
    @Override
    public void onClickStopRecord() {
        AudioHelper.getAudioHelperInstance().stopAudio();
        if (musicCallBack != null) {
            musicCallBack.onRecordStop();
        }
    }

    // 点击了删除
    @Override
    public void onClickDelete() {
        if (musicCallBack != null) {
            musicCallBack.onClickDelete();
        }
    }

    @Override
    public void onClickSwitchVoice(boolean isOpen) {
        if (isRecord) {
            musicCallBack.onClickSwitchVoice(isOpen);
        }
    }

    long oldTime = startTime;

    // 录音工具发出正在录音中消息
    @Override
    public void onProgress(long position) {
        long nowTime = (long) (computeTime(position) * 1000);
        if (musicCallBack != null) {
            musicCallBack.onRecording(oldTime, nowTime);
        }
        oldTime = nowTime;
        endTime = nowTime;
    }

    // 录音工具发出通知录音停止了
    @Override
    public void onRecordStop() {
        isRecord = false;
        List<VideoProcessBean> videoLists = MovieClipDataBase.instance().getMovies();
        for (VideoProcessBean newList : videoLists) {
            if (startTime > endTime) {
                continue;
            }
            newList.setAudioNewPart(startTime, endTime);
        }
        if (musicCallBack != null) {
            musicCallBack.onRecordStop();
        }
    }

    @Override
    public void originVolume(int progress) {
        if (musicCallBack != null) {
            musicCallBack.onVolumeChanged(audioInfo);
        }
    }

    @Override
    public void bgmVolume(int progress) {
        if (musicCallBack != null) {
            musicCallBack.onVolumeChanged(audioInfo);
        }
    }

    @Override
    public void recordVolume(int progress) {
        if (musicCallBack != null) {
            musicCallBack.onVolumeChanged(audioInfo);
        }
    }

    @Override
    public void onClickMusic(MusicRes musicRes) {
        if (musicRes == null) {
            audioInfo.setBackMusic("");
        } else {
            audioInfo.setBackMusic(musicRes.getPcmPath());
        }
        if (musicCallBack != null && musicRes != null) {
            // 通知播放器重启
            musicCallBack.onSelectedMusic(musicRes);
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        String[] titles = new String[]{"调音", "BGM", "配音"};

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                container.addView(musicAdjustView.getPageView());
                return musicAdjustView.getPageView();
            } else if (position == 1) {
                container.addView(musicMp3View.getPageView());
                return musicMp3View.getPageView();
            } else {
                container.addView(musicRecordView.getPageView());
                return musicRecordView.getPageView();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BackMusicActivity.TO_GET_MP3:
                    musicMp3View.onActivityResult(data);
                    break;
            }
        }
    }

    public void lockLayout() {
        rlLock.setVisibility(View.VISIBLE);
    }

    public void unLockLayout() {
        rlLock.setVisibility(View.INVISIBLE);
    }

}
