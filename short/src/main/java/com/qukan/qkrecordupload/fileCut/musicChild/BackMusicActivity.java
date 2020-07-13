package com.qukan.qkrecordupload.fileCut.musicChild;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qukan.qkrecordupload.BaseActivity;
import com.qukan.qkrecordupload.ConfigureConstants;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.MusicRes;
import com.qukan.qkrecordupload.custom.MyLoadingDialog;

import org.droidparts.util.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackMusicActivity extends BaseActivity {

    private TextView tvFinish;
    private ImageView ivBack;
    private LocalMusicAdapter adapter;
    private List<MusicRes> list = new ArrayList<>();
    private int mChoosePos = -1;
    private MyLoadingDialog myLoadingDialog;

    public static final int TO_GET_MP3 = 10001;

    @Override
    protected void onPostCreate() {
        setContentView(R.layout.activity_back_music);
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        tvFinish = findViewById(R.id.tv_choose);
        tvFinish.setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.rv_list);
        adapter = new LocalMusicAdapter(list);
        adapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LocalMusicAdapter adapter, View view, int position) {
                if (!list.get(position).getName().endsWith(".mp3")) {
                    Toast.makeText(BackMusicActivity.this, "须选择mp3格式的文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mChoosePos != -1) {
                    list.get(mChoosePos).setChoose(false);
                    adapter.notifyItemChanged(mChoosePos);
                }
                list.get(position).setChoose(true);
                adapter.notifyItemChanged(position);
                mChoosePos = position;
            }
        });
        recyclerView.setAdapter(adapter);
        getLocalMusicList();
    }

    private void getLocalMusicList() {
        list.clear();
        list.addAll(MusicUtils.getMusicData(this));
        adapter.notifyDataSetChanged();
    }

    private void coverToPcm(final MusicRes musicRes) {
        String srcPath = musicRes.getUrl();
        File file = new File(srcPath);
        if (file.exists()) {
            String originName = getFileNameByAllPath(srcPath);
            originName = originName.substring(0, originName.lastIndexOf("."));
            final String outPath = ConfigureConstants.QUKAN_PCM_TEMP + File.separator + originName + PublicUtils.random() + ".pcm";
            MP3Process mp3Process = new MP3Process(srcPath, outPath);
            mp3Process.setProcessListener(new MP3Process.OnProcessListener() {
                @Override
                public void onProcessStart() {

                }

                @Override
                public void onProcessOver() {
                    hideLoading();
                    musicRes.setPcmPath(outPath);
                    musicRes.setState(3);
                    musicRes.setTimeFlag(System.currentTimeMillis());
                    musicRes.save();
                    // mp3转换完成才是一次完整的下载结束
                    L.d("Mp3toPcm--onSuccess");
                    Intent intent = new Intent();
                    intent.putExtra("path", musicRes.getUrl());
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFail() {
                    hideLoading();
                    Toast.makeText(BackMusicActivity.this, "添加音乐失败", Toast.LENGTH_SHORT).show();
                    L.d("Mp3toPcm--onFail");
                }
            });
            mp3Process.startProcess();
        }
    }

    // 根据完整路径获取文件的名称（包括后缀名）
    public static String getFileNameByAllPath(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    public void showLoading() {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(this);
        }
        myLoadingDialog.show();
    }

    private void hideLoading() {
        if (myLoadingDialog != null) {
            myLoadingDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        } else if (v == tvFinish) {
            if (mChoosePos == -1) {
                Toast.makeText(this, "请选择音乐", Toast.LENGTH_SHORT).show();
                return;
            }
            String pcmPath = list.get(mChoosePos).getPcmPath();
            if (new File(pcmPath).exists()){
                Intent intent = new Intent();
                intent.putExtra("path", list.get(mChoosePos).getUrl());
                setResult(RESULT_OK, intent);
                finish();
            } else {
                showLoading();
                coverToPcm(list.get(mChoosePos));
            }
        }
    }
}
