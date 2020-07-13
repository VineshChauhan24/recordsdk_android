package com.qukan.qkrecordupload.fileCut.musicChild;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.AudioInfoBean;
import com.qukan.qkrecordupload.fileCut.customView.VerticalSeekBar;

import org.droidparts.Injector;

import lombok.Setter;

// 调音
public class MusicAdjustView implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private VerticalSeekBar vsb_origin;

    private VerticalSeekBar vsb_music;

    private VerticalSeekBar vsb_record;

    Context context;
    View inflate;
    AudioInfoBean audioInfo;

    @Setter
    MusicAdjustCallback musicAdjustCallback;

    public MusicAdjustView(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        inflate = LayoutInflater.from(context).inflate(R.layout.fragment_music_adjust_new, null);
        initView(inflate);
        Injector.inject(inflate, this);
    }

    private void initView(View view) {
        vsb_origin = view.findViewById(R.id.vsb_origin);

        vsb_record = view.findViewById(R.id.vsb_record);

        vsb_music = view.findViewById(R.id.vsb_music);
    }

    public void init(AudioInfoBean audioInfoBean) {
        this.audioInfo = audioInfoBean;
        vsb_origin.setMax(100);
        vsb_music.setMax(100);
        vsb_record.setMax(100);

        vsb_origin.setProgress(audioInfoBean.getOriginVolume());
        vsb_music.setProgress(audioInfoBean.getBGMVolume());
        vsb_record.setProgress(audioInfoBean.getRecordVolume());

        vsb_origin.setOnSeekBarChangeListener(this);
        vsb_music.setOnSeekBarChangeListener(this);
        vsb_record.setOnSeekBarChangeListener(this);
    }

    public View getPageView() {
        return this.inflate;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == vsb_origin) {
            audioInfo.setOriginVolume(progress);
            if (musicAdjustCallback != null) {
                musicAdjustCallback.originVolume(progress);
            }
        } else if (seekBar == vsb_music) {
            audioInfo.setBGMVolume(progress);
            if (musicAdjustCallback != null) {
                musicAdjustCallback.bgmVolume(progress);
            }
        } else if (seekBar == vsb_record) {
            audioInfo.setRecordVolume(progress);
            if (musicAdjustCallback != null) {
                musicAdjustCallback.recordVolume(progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface MusicAdjustCallback {

        void originVolume(int progress);

        void bgmVolume(int progress);

        void recordVolume(int progress);

    }

}
