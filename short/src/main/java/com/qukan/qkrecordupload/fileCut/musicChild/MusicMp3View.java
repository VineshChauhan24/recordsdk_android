package com.qukan.qkrecordupload.fileCut.musicChild;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.TextUtil;
import com.qukan.qkrecordupload.bean.AudioInfoBean;
import com.qukan.qkrecordupload.bean.MusicRes;
import com.qukan.qkrecordupload.fileCut.adapter.MusicViewAdapter;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;

import org.droidparts.Injector;

import lombok.Setter;

import static com.qukan.qkrecordupload.fileCut.musicChild.BackMusicActivity.TO_GET_MP3;

// 音乐
public class MusicMp3View implements View.OnClickListener, AdapterView.OnItemClickListener {


    private NewVideoProcessActivity activity;
    private Fragment fragment;
    private View inflate;
    private GridView gvMusic;

    private MusicViewAdapter musicViewAdapter;


    public MusicMp3View(NewVideoProcessActivity activity, Fragment fragment) {

        this.activity = activity;
        this.fragment = fragment;
        init();
    }

    private void init() {
        inflate = LayoutInflater.from(activity).inflate(R.layout.fragment_music_mp3_new, null);
        Injector.inject(inflate, this);

        gvMusic = inflate.findViewById(R.id.gv_music);
        musicViewAdapter = new MusicViewAdapter(activity);
        gvMusic.setAdapter(musicViewAdapter);
        gvMusic.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            Intent intent = new Intent(activity, BackMusicActivity.class);
            fragment.startActivityForResult(intent, TO_GET_MP3);
        } else {
            MusicRes musicRes = musicViewAdapter.getSelected();
            if (musicRes != null) {
                // 如果刚好点击的不是当前选中的这个，那么选中
                if (musicViewAdapter.getSelPosition() != position) {
                    musicViewAdapter.setSelected(position);
                } else {
                    musicViewAdapter.setSelected(-1);
                }
            } else {
                musicViewAdapter.setSelected(position);
            }
            musicViewAdapter.notifyDataSetChanged();
            if (onClickMusicListener != null) {
                onClickMusicListener.onClickMusic(musicViewAdapter.getSelected());
            }
        }
    }

    // 从音乐选择界面回来后-回调
    public void onActivityResult(Intent data) {
        // 要先去刷新数据再去选中，有可能选中原先不存在新添加的数据
        musicViewAdapter.refresh();
        String mp3Url = data.getStringExtra("path");
        if (TextUtil.isEmpty(mp3Url)) {
            musicViewAdapter.setSelected(-1);
        } else {
            musicViewAdapter.setSelectedByUrl(mp3Url);
        }
        musicViewAdapter.refresh();
        if (onClickMusicListener != null) {
            onClickMusicListener.onClickMusic(musicViewAdapter.getSelected());
        }
    }


    public View getPageView() {
        return this.inflate;
    }

    @Override
    public void onClick(View v) {

    }

    public void init(AudioInfoBean audioInfo) {
        musicViewAdapter.refresh();
        musicViewAdapter.setSelectedByPcmPath(audioInfo.getBackMusic());
    }

    @Setter
    OnClickMusicListener onClickMusicListener;

    public interface OnClickMusicListener {
        void onClickMusic(MusicRes musicRes);
    }

}
