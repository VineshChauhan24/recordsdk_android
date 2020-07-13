package com.qukan.qkrecordupload.fileCut.processActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qukan.qkrecordupload.BaseActivity;
import com.qukan.qkrecordupload.ConfigureConstants;
import com.qukan.qkrecordupload.OutPutMessage;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.RnToast;
import com.qukan.qkrecordupload.ThreadPool;
import com.qukan.qkrecordupload.bean.AudioChange;
import com.qukan.qkrecordupload.bean.AudioInfoBean;
import com.qukan.qkrecordupload.bean.AudioPartBean;
import com.qukan.qkrecordupload.bean.MusicRes;
import com.qukan.qkrecordupload.bean.TransferEffect;
import com.qukan.qkrecordupload.bean.VideoEditBean;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.bean.VideoTitleBean;
import com.qukan.qkrecordupload.bean.VideoTitleUse;
import com.qukan.qkrecordupload.bean.ViewTitleOnePart;
import com.qukan.qkrecordupload.compop.CommonBottomThree;
import com.qukan.qkrecordupload.compop.VideoProcessPop;
import com.qukan.qkrecordupload.custom.MyLoadingDialog;
import com.qukan.qkrecordupload.custom.MyLoadingPop;
import com.qukan.qkrecordupload.file.VideoListCallback;
import com.qukan.qkrecordupload.fileCut.AudioHelper;
import com.qukan.qkrecordupload.fileCut.Callback.CGControlCallback;
import com.qukan.qkrecordupload.fileCut.Callback.MusicCallBack;
import com.qukan.qkrecordupload.fileCut.Callback.OnCropCallback;
import com.qukan.qkrecordupload.fileCut.Callback.SeekVideoCallback;
import com.qukan.qkrecordupload.fileCut.VideoTextHelper;
import com.qukan.qkrecordupload.fileCut.processFragment.CGEditFragment;
import com.qukan.qkrecordupload.fileCut.processFragment.CGFragment;
import com.qukan.qkrecordupload.fileCut.processFragment.CGImageFragment;
import com.qukan.qkrecordupload.fileCut.processFragment.ColorFragment;
import com.qukan.qkrecordupload.fileCut.processFragment.CropFragment;
import com.qukan.qkrecordupload.fileCut.processFragment.MusicFragment;
import com.qukan.qkrecordupload.fileCut.processFragment.PlayerFragment;
import com.qukan.qkrecordupload.fileCut.processFragment.SeekVideoFragment;
import com.qukan.qkrecordupload.fileCut.processFragment.TransferEditFragment;
import com.qukan.qkrecordupload.fileCut.processFragment.VideoListFragment;
import com.qukan.qkrecordupload.qkCut.MovieClipDataBase;
import com.qukan.qkrecordupload.qkCut.QKColorFilterGroup;
import com.qukan.qkrecordupload.qkCut.QKCompleteMovie;
import com.qukan.qkrecorduploadsdk.RecordSdk;
import com.qukan.qkrecorduploadsdk.decoder.CompleteControl;

import org.droidparts.util.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


import lombok.Getter;

import static com.qukan.qkrecordupload.ConfigureConstants.QUKAN_PCM;
import static com.qukan.qkrecordupload.PublicUtils.getVideoDuration;


/**
 * Created by Administrator on 2018/9/10 0010.
 */
public class NewVideoProcessActivity extends BaseActivity {

    private static final String TAG = "NewVideoProcessActivity";
    private LinearLayout llTop;

    private RelativeLayout rlTab;

    private LinearLayout llBack;

    private LinearLayout llRatio;

    private ImageView imgatio;
    private TextView tvRatio;

    //播放器外面的view, 用于计算换比例时最大的宽高
    private RelativeLayout rlPlayer;

    private RelativeLayout rlPlayerRoot;

    private RelativeLayout rlCaption;

    private RelativeLayout rlDub;

    private RelativeLayout rlColor;

    private FrameLayout flCg;

    private FrameLayout flCrop;

    private FrameLayout flMusic;

    private FrameLayout flColor;

    private FrameLayout flList;

    private FrameLayout flBar;

    private RelativeLayout rlTransform;

    private RelativeLayout rlControl;

    private TextView tvModel;

    private TextView tvDo;

    private CropFragment fmCrop;

    private CGFragment fmCGFragment;

    private MusicFragment fmMusic;

    private ColorFragment fmColor;

    private PlayerFragment fmPlayer;

    private CGEditFragment fmCGEdit;

    private CGImageFragment fmImgCGEdit;

    private VideoListFragment fmVideoList;

    private TransferEditFragment fmTransferEdit;

    private SeekVideoFragment fmSeekBar;

    private TextView tvCover;

    private TextView tvSave;

    private RelativeLayout rlLockTop;

    private RelativeLayout rlLockBottom;
    public static final int videoResultFinish = 110011;


    // 所有的视频数据资料都在这个list中，视频处理通过维护这个list来保存数据
    @Getter
    ArrayList<VideoProcessBean> videoDataLists = new ArrayList<>();


    private CommonBottomThree videoGeneratePop;
    private VideoProcessPop videoProcessStatePop;
    private QKCompleteMovie complete;


    // 封面图片名称
    String coverPath = "";

    private String newPath;
    private String recordPath;
    private String outputPath = null;
    private int draftId = -1;


    private VideoProcessBean titleBean;
    private VideoProcessBean endBean;

    public static final int RATIO_16X9 = 0;
    public static final int RATIO_4X3 = 1;
    public static final int RATIO_1X1 = 2;
    public static final int RATIO_3X4 = 3;
    public static final int RATIO_9X16 = 4;
    private Boolean startRatioThread = false;

    private Thread ratioThread = null;
    //0 没有选择 ，1 字幕页面 2配音 3调色 4 裁剪
    private int selectType = 0;


    private void initView(View view) {
        llTop = (LinearLayout) view.findViewById(R.id.title);

        rlTab = (RelativeLayout) view.findViewById(R.id.rl_tab);

        llBack = (LinearLayout) view.findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);

        llRatio = (LinearLayout) view.findViewById(R.id.ll_ratio);
        llRatio.setOnClickListener(this);

        imgatio = view.findViewById(R.id.img_ratio);
        tvRatio = view.findViewById(R.id.tv_ratio);

        rlPlayer = (RelativeLayout) view.findViewById(R.id.rl_player);

        rlPlayerRoot = view.findViewById(R.id.rl_player_root);
        rlTransform = view.findViewById(R.id.rl_transform);

        rlCaption = (RelativeLayout) view.findViewById(R.id.rl_caption);
        rlCaption.setOnClickListener(this);

        rlDub = (RelativeLayout) view.findViewById(R.id.rl_dub);
        rlDub.setOnClickListener(this);

        rlColor = (RelativeLayout) view.findViewById(R.id.rl_color);
        rlColor.setOnClickListener(this);

        flCg = (FrameLayout) view.findViewById(R.id.fl_cg);

        flCrop = (FrameLayout) view.findViewById(R.id.fl_crop);

        flMusic = (FrameLayout) view.findViewById(R.id.fl_music);

        flColor = (FrameLayout) view.findViewById(R.id.fl_color);

        flList = (FrameLayout) view.findViewById(R.id.fl_video_list);

        flBar = (FrameLayout) view.findViewById(R.id.fl_video_seekbar);

        rlControl = view.findViewById(R.id.rl_control);

        tvModel = view.findViewById(R.id.tv_model);

        tvDo = view.findViewById(R.id.tv_do);
        tvDo.setOnClickListener(this);

        fmCrop = (CropFragment) getSupportFragmentManager().findFragmentById(R.id.fm_crop);

        fmCGFragment = (CGFragment) getSupportFragmentManager().findFragmentById(R.id.fm_subtitle);

        fmMusic = (MusicFragment) getSupportFragmentManager().findFragmentById(R.id.fm_music);

        fmColor = (ColorFragment) getSupportFragmentManager().findFragmentById(R.id.fm_color);

        fmPlayer = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.fm_player);

        fmCGEdit = (CGEditFragment) getSupportFragmentManager().findFragmentById(R.id.fm_subtitle_edit);

        fmImgCGEdit = (CGImageFragment) getSupportFragmentManager().findFragmentById(R.id.fm_subtitle_imgedit);

        fmVideoList = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.fm_video_list);

        fmTransferEdit = (TransferEditFragment) getSupportFragmentManager().findFragmentById(R.id.fm_transfer_edit);

        fmSeekBar = (SeekVideoFragment) getSupportFragmentManager().findFragmentById(R.id.fm_video_seekbar);


        tvCover = (TextView) view.findViewById(R.id.tv_cover);
        tvCover.setOnClickListener(this);

        tvSave = (TextView) view.findViewById(R.id.tv_save);
        tvSave.setOnClickListener(this);

        rlLockTop = (RelativeLayout) findViewById(R.id.rl_lock_top);
        rlLockTop.setOnClickListener(this);

        rlLockBottom = (RelativeLayout) findViewById(R.id.rl_lock_bottom);
        rlLockBottom.setOnClickListener(this);
    }

    @Override
    protected void onPostCreate() {
        setContentView(R.layout.activity_video_process_new);
        initView(getRootView());
        String coverName = System.currentTimeMillis() + PublicUtils.random();
        coverPath = ConfigureConstants.QUKAN_PATH_TEMP + File.separator + coverName + ".jpg";
        //setFragmentVisible(true, fmCrop);
        initData();
        setListener();
        hideAllFragment();
    }

    @Override
    protected void onPause() {
        fmCGEdit.closeInput();

        if (complete != null) {
            complete.setHomeKey(true);
        }
        fmPlayer.pauseVideo();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        startRatioThread = false;
        // 删除配音数据
        if (isSaved) {
            if (isFromDraft) {
                new File(recordPath).delete();
            }
        } else {
            if (isFromDraft) {
                new File(newPath).delete();
            } else {
                new File(recordPath).delete();
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (complete != null) {
            complete.setHomeKey(false);
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Log.d(TAG, "onWindowFocusChanged");
            if (startRatioThread == false) {
                ratioChange(MovieClipDataBase.instance().getRatio());
                startRatioThread();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == rlCaption) {
            selectType = 1;
            showCGTab();
            fmSeekBar.setPosition(0);
        } else if (v == rlDub) {
            selectType = 2;
            showMusicTab();
            fmSeekBar.setPosition(0);
        } else if (v == rlColor) {
            selectType = 3;
            showColorTab();
            fmSeekBar.setPosition(0);
        } else if (v == tvDo) {
            if (fmMusic.isRecord){
                RnToast.showToast(this, "正在录音中");
                return;
            }
            isShowCGTab = false;
            hidTypeViews();
            if (selectType == 4) {
                Log.d(TAG, "fmCrop.isChanged:" + fmCrop.isChanged());
                if (fmCrop.isChanged()) {
                    fmCrop.sureClipTime();
                    videoChange();
                    refreshVideoList();
                } else {
                    startPreview();
                }
            }
            fmSeekBar.setPosition(0);
            selectType = 0;
        } else if (v == llRatio) {
            int ratio = MovieClipDataBase.instance().getRatio();
            ratio++;
            MovieClipDataBase.instance().setRatio(ratio);
//            Log.d(TAG, "ratio: " + ratio);
            ratioChange(ratio);
//            MovieClipDataBase.instance().draw();
        } else if (v == tvSave) {
            // 点击了生成
            fmCGEdit.closeInput();
            pauseVideo();
            if (videoDataLists == null || videoDataLists.isEmpty()) {
                RnToast.showToast(this, "请添加视频");
                return;
            }
            // 生成时进行一次预览会触发字幕重新添加，这样生成视频的字幕才准，可能出现视频片段变化后，字幕没有重置加载导致生成视频字幕不准
            videoGeneratePop.showAtLocation(getRootView(), Gravity.CENTER, 0, 0);

        } else if (v == tvCover) {
            fmVideoList.addCover();
        } else if (v == llBack) {
            finish();
        } else if (v == rlLockTop || v == rlLockBottom) {
            RnToast.showToast(this, getString(R.string.on_recording));
        }
    }


    private void hidTypeViews() {
        flColor.setVisibility(View.GONE);
        flCg.setVisibility(View.GONE);
        flMusic.setVisibility(View.GONE);
        llTop.setVisibility(View.VISIBLE);
        flList.setVisibility(View.VISIBLE);
        flBar.setVisibility(View.GONE);
        rlTransform.setVisibility(View.GONE);
        rlControl.setVisibility(View.GONE);
        rlTab.setVisibility(View.VISIBLE);
        flCrop.setVisibility(View.GONE);

    }

    private boolean isFromDraft = false;
    private boolean isSaved = false;

    private String saveToDraft() {
        isSaved = true;
        saveToRect();
        VideoEditBean bean = new VideoEditBean();
        //添加保存的时间
        bean.setDate(System.currentTimeMillis());
        //添加视频总时长
        bean.setDuration(getAllTime());
        //添加视频画幅比
        bean.setRatio(MovieClipDataBase.instance().getRatio());
        //添加视频
        bean.setSourceVideos(MovieClipDataBase.instance().getMovies());
        //添加字幕
        bean.setTextStyleBeen(MovieClipDataBase.instance().getTextStyleBeen());
        //添加色调
        bean.setGroup(MovieClipDataBase.instance().getGroup());
        //添加视频名
        bean.setShortVideoName(getName());
        bean.setFromDraft(true);
        bean.setAudioInfoBean(MovieClipDataBase.instance().getAudioInfoBean());
        if (OutPutMessage.instance().getCallBack() != null) {
            OutPutMessage.instance().getCallBack().onDraftGet(bean);
        }
        String draft = JSON.toJSONString(bean);
        return draft;
    }

    // 获取所有视频块的共计时间
    private String getAllTime() {
        long time = 0;
        for (VideoProcessBean bean : videoDataLists) {
            time = time + bean.getDurationTime();
        }
        return PublicUtils.convertTime(time, 1);
    }

    private void showCompile(VideoProcessBean bean) {
        hideAllFragment();
        selectType = 4;

        tvModel.setText("编剪");
        flColor.setVisibility(View.GONE);
        flList.setVisibility(View.GONE);
        flBar.setVisibility(View.GONE);
        rlControl.setVisibility(View.VISIBLE);
        flCrop.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.GONE);
        rlTab.setVisibility(View.GONE);

        // 重绘seekBar
        drawAllRect();
        setFragmentVisible(true, fmCrop);
    }

    private boolean isShowCGTab = false;

    //    int scrollX = 0;
    private void showCGTab() {
        isShowCGTab = true;

//        scrollX += 10;
//        fmVideoList.setScrollX(scrollX);
        if (videoDataLists.isEmpty()) {
            RnToast.showToast(this, "请添加一个视频");
            return;
        }
        hideAllFragment();

        tvModel.setText("字幕");
        flMusic.setVisibility(View.GONE);
        flColor.setVisibility(View.GONE);
        rlTab.setVisibility(View.GONE);

        flCg.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.GONE);
        flList.setVisibility(View.GONE);
        flBar.setVisibility(View.VISIBLE);
        rlControl.setVisibility(View.VISIBLE);

        setFragmentVisible(true, fmCGFragment, fmSeekBar);
        startPreview();
        fmSeekBar.init(videoDataLists);
        // 重绘seekBar
        drawAllRect();
    }

    private void showMusicTab() {

        isShowCGTab = false;
        if (videoDataLists.isEmpty()) {
            RnToast.showToast(this, "请添加一个视频");
            return;
        }
        hideAllFragment();

        tvModel.setText("配音");
        flCg.setVisibility(View.GONE);
        llTop.setVisibility(View.GONE);
        flList.setVisibility(View.GONE);
        flColor.setVisibility(View.GONE);
        rlTab.setVisibility(View.GONE);

        flMusic.setVisibility(View.VISIBLE);
        flBar.setVisibility(View.VISIBLE);
        rlControl.setVisibility(View.VISIBLE);

        setFragmentVisible(true, fmMusic, fmSeekBar);
        fmSeekBar.init(videoDataLists);
        startPreview();

        fmMusic.init(MovieClipDataBase.instance().getAudioInfoBean());
        // 重绘seekBar
        drawAllRect();
    }


    private void showColorTab() {
        isShowCGTab = false;
        if (videoDataLists.isEmpty()) {
            RnToast.showToast(this, "请添加一个视频");
            return;
        }
        hideAllFragment();

        tvModel.setText("调色");
        flCg.setVisibility(View.GONE);
        llTop.setVisibility(View.GONE);
        flList.setVisibility(View.GONE);
        flMusic.setVisibility(View.GONE);
        rlTab.setVisibility(View.GONE);

        flColor.setVisibility(View.VISIBLE);
        flBar.setVisibility(View.VISIBLE);
        rlControl.setVisibility(View.VISIBLE);

        setFragmentVisible(true, fmColor, fmSeekBar);
        fmSeekBar.init(videoDataLists);
        startPreview();

        fmColor.init(MovieClipDataBase.instance().getGroup());
        // 重绘seekBar
        drawAllRect();
    }

    public void drawAllRect() {
        Log.d(TAG, "drawAllRect");
        List<VideoProcessBean> videoLists = MovieClipDataBase.instance().getMovies();
        fmSeekBar.clearRect();
        for (VideoProcessBean videoList : videoLists) {
            List<AudioPartBean> list = videoList.getListAudios();
            if (list == null || list.isEmpty()) {
                Log.d(TAG, "drawAllRect continue");
                continue;
            }
            for (AudioPartBean audioPartBean : list) {
                long recordStartTime = audioPartBean.getRecordStartTime();
                long recordEndTime = audioPartBean.getRecordEndTime();
                long startTime = audioPartBean.getStartTime();
                long endTime = audioPartBean.getEndTime();
                fmSeekBar.drawRectAll(startTime, endTime);
            }
        }
    }

    // 把录音片段数据保存到草稿的list
    private void saveToRect() {
        List<VideoProcessBean> videoLists = MovieClipDataBase.instance().getMovies();
        // 说明没有同步，很可能是没有配音文件，但添加了视频片段
        if (videoLists.size() != videoDataLists.size()) {
            return;
        }
        for (int i = 0; i < videoDataLists.size(); i++) {
            List<AudioPartBean> listAudios = videoLists.get(i).getListAudios();
            List<AudioPartBean> listAudios2 = videoDataLists.get(i).getListAudios();
            // 先清空，再添加
            listAudios2.clear();
            if (listAudios.isEmpty()) {
                continue;
            }
            for (int i1 = 0; i1 < listAudios.size(); i1++) {
                AudioPartBean audioPartBean1 = listAudios.get(i1).copy();
                listAudios2.add(audioPartBean1);
            }
        }
    }

    int oldRatioType = -1;

    public void ratioChange(int ratioType) {
        synchronized (this) {
            ratioType = ratioType % 5;
            int height = rlPlayer.getMeasuredHeight();
            int width = rlPlayer.getMeasuredWidth();
            if (oldRatioType == -1) {
                oldRatioType = ratioType;
            } else if (oldWidth == width && oldHeight == height && oldRatioType == ratioType) {
                Log.d(TAG, "oldWidth == width && oldHeight == height && oldRatioType == ratioType");
                return;
            }
            oldRatioType = ratioType;
            Log.d(TAG, "rlPlayer height=" + height + " ,width=" + width);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlPlayerRoot.getLayoutParams();
            double scale = 1;
            if (ratioType == RATIO_1X1) {
                if (height > width) {
                    params.height = width;
                    params.width = width;
                } else {
                    params.height = height;
                    params.width = height;
                }
                imgatio.setImageResource(R.drawable.short_ic_ratio1_1);
                tvRatio.setText("1:1");
            } else if (ratioType == RATIO_3X4) {
                scale = 3.0 / 4;
                imgatio.setImageResource(R.drawable.short_ic_ratio3_4);
                tvRatio.setText("3:4");
            } else if (ratioType == RATIO_4X3) {
                scale = 4.0 / 3;
                imgatio.setImageResource(R.drawable.short_ic_ratio_4_3);
                tvRatio.setText("4:3");
            } else if (ratioType == RATIO_16X9) {
                scale = 16.0 / 9;
                imgatio.setImageResource(R.drawable.short_ic_ratio16_9);
                tvRatio.setText("16:9");
            } else if (ratioType == RATIO_9X16) {
                scale = 9.0 / 16;
                imgatio.setImageResource(R.drawable.short_ic_ratio9_16);
                tvRatio.setText("9:16");
            }

            Log.d(TAG, "ratioType:" + ratioType + "  scale:" + scale);
            if (scale != 1.0) {
                Log.d(TAG, "scale != 1.0");
                double scaleRl = width * 1.0 / height;
                if (scaleRl > scale) {
                    params.height = height;
                    params.width = (int) (height * scale);
                } else if (scaleRl == scale) {
                    params.height = height;
                    params.width = width;
                } else {
                    params.width = width;
                    params.height = (int) (width / scale);
                    ;
                }
            }
            Log.d(TAG, "params.width:" + params.width + "  params.height:" + params.height);

            rlPlayerRoot.setLayoutParams(params);
            rlPlayerRoot.requestLayout();
            fmPlayer.setVideoLayoutWidth(params.width);
            fmPlayer.setVideoLayoutHeight(params.height);

            oldWidth = width;
            oldHeight = height;
//            fmPlayer.pauseVideo();
            fmPlayer.removeAllView();
            fmCGFragment.recoverText();
            fmCGFragment.refreshTextView(fmPlayer.getCurrentTime(), fmPlayer.getIsPause());
        }
    }

    private int oldWidth = -1;
    private int oldHeight = -1;

    private void startRatioThread() {
        startRatioThread = true;
        ratioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startRatioThread) {
                    int height = rlPlayer.getMeasuredHeight();
                    int width = rlPlayer.getMeasuredWidth();
                    if ((oldWidth != width || oldHeight != height)) {
                        Message message = Message.obtain(); //获取消息的载体
                        message.what = 0;
                        Bundle bundle = new Bundle();
                        message.setData(bundle);
                        ratioHandler.sendMessage(message);
                    }
                    SystemClock.sleep(500);
                }
                Log.d(TAG, "startRatioThread stoped");
            }
        });
        ratioThread.start();
    }

    // 消息处理句柄--用于接收sdk中发出的消息
    private Handler ratioHandler = new Handler(new ratioChangeCallBack());

    // 事件侦听的处理类
    private class ratioChangeCallBack implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            // 短视频处理中
            if (msg.what == 0) {

                ratioChange(MovieClipDataBase.instance().getRatio());
            }
            return true;
        }
    }

    public int getAudioSize() {
        List<VideoProcessBean> videoLists = MovieClipDataBase.instance().getMovies();
        int all = 0;
        for (VideoProcessBean videoList : videoLists) {
            List<AudioPartBean> list = videoList.getListAudios();
            if (list == null || list.isEmpty()) {
                continue;
            }
            all = all + list.size();
        }
        return all;
    }

    // 根据总视频的当前时间获得要删除的录音片段开始时间和结束时间
    public long[] getInfoByPosition(long position) {
        List<VideoProcessBean> videoLists = MovieClipDataBase.instance().getMovies();
        for (VideoProcessBean videoList : videoLists) {
            List<AudioPartBean> list = videoList.getListAudios();
            if (list == null) {
                continue;
            }
            for (AudioPartBean audioPartBean : list) {
                long startTime = audioPartBean.getStartTime();
                long endTime = audioPartBean.getEndTime();
                if (position > startTime && position < endTime) {
                    return new long[]{startTime, endTime};
                }
            }
        }
        return null;
    }

    public static int DEFAULT_TIME = 3000;//图片默认的播放时间

    // 视频发生了变化，如裁剪 删除 增加 换位置，要通知录音模块进行更新
    // 比较耗时，加线程，加弹框，
    MyLoadingPop myLoadingPop;

    public void videoChange() {
        if (myLoadingPop == null) {
            myLoadingPop = new MyLoadingPop(this);
        }
        long allTime = 0;
        for (VideoProcessBean newProcess : videoDataLists) {
            newProcess.setRecordStartTime(allTime);
            allTime = (long) (allTime + (newProcess.getEndTime() - newProcess.getStartTime()) / newProcess.getSpeed());
            newProcess.setRecordEndTime(allTime);
        }
        myLoadingPop.showAtLocation(getRootView(), Gravity.CENTER, 0, 0);

        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                MovieClipDataBase.instance().changeVideoList(videoDataLists);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (NewVideoProcessActivity.this.isDestroyed() || NewVideoProcessActivity.this.isFinishing()) {
                            return;
                        }
                        fmCrop.updateButton();
                        myLoadingPop.dismiss();
                        RnToast.showToast(NewVideoProcessActivity.this, "处理完成");
                        startPreview();
                    }
                });
            }
        });
    }

    // 打开此activity的入口方法，创建短视频
    public static void startActivityToCreate(Activity context, ArrayList<VideoProcessBean> pathList, String outputPath) {
        Intent intent = new Intent(context, NewVideoProcessActivity.class);
        VideoEditBean videoEditBean = new VideoEditBean();
        videoEditBean.setSourceVideos(pathList);
        String data = JSON.toJSONString(videoEditBean);
        intent.putExtra("data", data);
        intent.putExtra("outputPath", outputPath);
        intent.putExtra("draftId", -1);

        context.startActivityForResult(intent, 1);
    }

    // 打开此activity的入口方法，创建短视频
    public static void startActivityToCreate(Activity context, ArrayList<VideoProcessBean> pathList) {
        Intent intent = new Intent(context, NewVideoProcessActivity.class);
        VideoEditBean videoEditBean = new VideoEditBean();
        videoEditBean.setSourceVideos(pathList);
        String data = JSON.toJSONString(videoEditBean);
        intent.putExtra("data", data);
        intent.putExtra("draftId", -1);

        context.startActivityForResult(intent, 1);
    }


    // 打开此activity的入口方法，创建短视频
    public static void startActivityToCreate(Activity context, VideoEditBean videoEditBean, int draftId) {
        Intent intent = new Intent(context, NewVideoProcessActivity.class);
        String data = JSON.toJSONString(videoEditBean);
        intent.putExtra("data", data);
        intent.putExtra("draftId", draftId);
        context.startActivityForResult(intent, 1);
    }


    // 解析传进来的数据
    private void initData() {
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        String outputPath = intent.getStringExtra("outputPath");
        draftId = intent.getIntExtra("draftId", 0);
        this.outputPath = outputPath;
        // 所有属性数据
        VideoEditBean videoEditBean = JSON.parseObject(data, VideoEditBean.class);

        isFromDraft = videoEditBean.isFromDraft();

        videoDataLists = videoEditBean.getSourceVideos();
        MovieClipDataBase.instance().setGroup(videoEditBean.getGroup());
        MovieClipDataBase.instance().setTextStyleBeen(videoEditBean.getTextStyleBeen());
        MovieClipDataBase.instance().setRatio(videoEditBean.getRatio());

        if (videoDataLists != null && !videoDataLists.isEmpty() && !isFromDraft) {
            for (VideoProcessBean videoProcessBean : videoDataLists) {
                String path = videoProcessBean.getPath();
                if (PublicUtils.isVideo(path)) {
                    long time = Long.valueOf(getVideoDuration(path));
                    videoProcessBean.setEndTime(time);
                    videoProcessBean.setLength(time);
                } else {
                    videoProcessBean.setEndTime(DEFAULT_TIME);
                    videoProcessBean.setLength(20000);
                }
            }
        }

        AudioInfoBean audioInfoBean = videoEditBean.getAudioInfoBean();
        // 创建录音文件--每次进来判断是否带有录音路径，有可能是草稿恢复来的
        // 草稿恢复的话，录音pcm文件要备份一份，点击返回要删掉，保存时替换
        // 从草稿恢复的 录音文件一定是存在的，草稿恢复那边会做判断
        recordPath = audioInfoBean.getRecordPath();
        if (!isFromDraft) {
            recordPath = getRandomPcmPath();
            audioInfoBean.setRecordPath(recordPath);
        } else {
            newPath = getRandomPcmPath();
            try {
                new File(newPath).createNewFile();
                copyFile(recordPath, newPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            audioInfoBean.setRecordPath(newPath);
        }


        // 初始化各个fragment的界面--给他们填充上数据
        MovieClipDataBase.instance().setAudioInfoBean(audioInfoBean);
        MovieClipDataBase.instance().changeArrayMovies(videoDataLists);
        // 初始化各个fragment的界面--给他们填充上数据
        fmCrop.create();
        fmVideoList.init(videoDataLists);
        fmCGFragment.init(MovieClipDataBase.instance().getTextStyleBeen());


        showLoading();

        final String path = audioInfoBean.getRecordPath();
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                AudioHelper.getAudioHelperInstance().initAudio(path, getAllDuration() / (float) 1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                    }
                });
            }
        });
        initPop();
        fmPlayer.setVideoAndPlay();
        if (isFromDraft) {
            setFragmentVisible(true, fmCGFragment);
        }
    }


    MyLoadingDialog myLoadingDialog;

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

    @NonNull
    private String getRandomPcmPath() {
        String recordPath;
        String name = System.currentTimeMillis() + "" + PublicUtils.random() + ".pcm";
        recordPath = QUKAN_PCM + File.separator + name;
        return recordPath;
    }

    private void setListener() {
        fmVideoList.setVideoListCallback(new VideoListCallback() {
            @Override
            public void onClickTransIcon(int position) {
                position = position - 1;
                VideoProcessBean videoProcessBean = videoDataLists.get(position);
                setFragmentVisible(true, fmTransferEdit);
                tvModel.setText("转场");
                rlTransform.setVisibility(View.VISIBLE);
                rlControl.setVisibility(View.VISIBLE);
                flList.setVisibility(View.GONE);
                rlTab.setVisibility(View.GONE);

                TransferEffect transferEffect = videoProcessBean.getTransferEffect();
                fmTransferEdit.fullData(transferEffect, position);
            }

            @Override
            public void onClickVideo(int position) {
                position = position - 1;
                setFragmentVisible(false, fmTransferEdit);
                VideoProcessBean bean = videoDataLists.get(position);
                showCompile(bean);
                playVideoOne(bean);
            }

            @Override
            public void onAddVideo(List<VideoProcessBean> list) {
                videoChange();
            }

            @Override
            public void OnVideoMove() {
                videoChange();
            }
        });

        fmVideoList.setScrollxCallBack(new VideoListFragment.scrollxCallBack() {
            @Override
            public void onScrollx(int index, double percent) {
                if (!MovieClipDataBase.instance().getPauseState()) {
                    return;
                }
                long currentTime = 0;
                for (int i = 0; i < index; i++) {
                    currentTime += videoDataLists.get(i).getDurationTime();
                }
                if (index >= videoDataLists.size()) {
                    return;
                }
                currentTime += videoDataLists.get(index).getDurationTime() * percent;
                if (index == videoDataLists.size() - 1 && percent > 0.98 && MovieClipDataBase.instance().isPlayerEnd()) {
                    return;
                }
                fmPlayer.seek(currentTime);
            }
        });

        fmSeekBar.setSeekVideoCallback(new SeekVideoCallback() {
            @Override
            public void onChangeSeek(int position, boolean fromUser) {
                if (fromUser) {
                    fmPlayer.seek(position);
                    if (!fmPlayer.getIsPause()) {
                        fmPlayer.pauseVideo();
                    }
                }
                // 播放时刷新字幕动画
                fmCGFragment.refreshTextView(position, fmPlayer.getIsPause());
            }
        });

        fmPlayer.setPlayStateListener(new PlayerFragment.PlayStateListener() {
            @Override
            public void onPlay(boolean isFromPreview) {
                // TODO: 2018/01/02 0002 给播放器设置个left和right
                if (fmCrop.isVisible()) {
                    if (!isFromPreview) {
                        fmPlayer.setLeftSeek((int) fmCrop.getNowLeftSeek());
                        fmPlayer.setRightSeek((int) fmCrop.getNowRightSeek());
                        fmCrop.unLockRangeBar();
                    }
                } else if (fmSeekBar.isVisible()) {

                }
            }

            @Override
            public void onPauseVideo(boolean isFromPreview) {
                if (!isFromPreview) {
                    return;
                }

                if (fmCrop.isVisible()) {
                    fmCGFragment.refreshTextView(fmPlayer.getCurrentTime(), true);
                } else if (fmCGFragment.isVisible()) {
                    int time = (int) fmPlayer.getCurrentTime();
                    fmCGFragment.refreshTextView(time, true);
                } else if (fmMusic.isVisible()) {
                    int time = (int) fmPlayer.getCurrentTime();
                    fmCGFragment.refreshTextView(time, true);
                }
            }

            @Override
            public void onCompleted() {
                if (fmCrop.isVisible()) {
                    // 播放结束把这个进度条刷到左边进度条
                    if (!MovieClipDataBase.instance().isPreView()) {
                        VideoProcessBean bean = getCurrentSelected();
                        if (bean != null) {
                            if (bean.isVideo()) {
                                float duration = getVideoDuration(bean.getPath());
                                fmCrop.drawPlay(fmCrop.getNowLeftSeek() / duration);
                                fmPlayer.seekSync((long) fmCrop.getNowLeftSeek());
                            }
                        }

                    }

                }

            }

            @Override
            public void onPlaying(double videoCurrentTime, boolean isFromPreview) {
                int currentTime = (int) (videoCurrentTime * 1000);
                int durationTime = 0;
                int singleCurrentTmime;
                double percent = 0;
                int index = 0;
                for (int i = 0; i < videoDataLists.size(); i++) {
                    VideoProcessBean bean = videoDataLists.get(i);
                    singleCurrentTmime = currentTime - durationTime;
                    durationTime += bean.getDurationTime();
                    if (durationTime + 1 >= currentTime) {
                        index = i;
                        percent = (singleCurrentTmime / (double) bean.getDurationTime());
                        break;
                    }
                }

                if(MovieClipDataBase.instance().isPreView()){
                    fmVideoList.setIndexAndPercent(index, percent);
                }

                if (fmCrop.isVisible()) {
                    if (!isFromPreview) {
                        VideoProcessBean bean = getCurrentSelected();
                        if (bean != null) {
                            if (bean.isVideo()) {
                                float duration = (float) (getVideoDuration(bean.getPath()) / 1000.0);
                                if(MovieClipDataBase.instance().isPlayerEnd()){
                                    videoCurrentTime = duration;
                                }
                                fmCrop.drawPlay((float) (videoCurrentTime * MovieClipDataBase.instance().getSpeed() / duration));
                            }
                        }
                    } else {
                        fmCGFragment.refreshTextView((int) (videoCurrentTime * 1000), false);
                    }
                } else if (fmSeekBar.isVisible()) {
                    if (!fmMusic.isRecord) {
                        fmSeekBar.setPosition((long) (videoCurrentTime * 1000));
                    }
                }
            }

            @Override
            public void onSurfaceChanged() {
                ratioChange(MovieClipDataBase.instance().getRatio());
            }

            @Override
            public void onFilterChange(int type) {
                // 点击了确定删除的按钮
                if (isSelectedVideo()) {
                    int position = getCurrentSelectedPosition();
                    VideoProcessBean bean = videoDataLists.get(position);
                    bean.setFilterType(type);
                    VideoProcessBean bean2 = MovieClipDataBase.instance().getMovies().get(position);
                    bean2.setFilterType(type);
                    MovieClipDataBase.instance().changeFilter(type);
                } else {
                    RnToast.showToast(NewVideoProcessActivity.this, "请选择视频");
                }
            }
        });

        fmTransferEdit.setTransferCallback(new TransferEditFragment.TransferCallback() {
            @Override
            public void onSelected(TransferEffect transfer, int position) {
                TransferEffect transferEffect = transfer.copy();
                Log.d(TAG, "onSelected:" + position + " transferEffect:" + transferEffect.getType());
                VideoProcessBean videoProcessBean = videoDataLists.get(position);
                videoProcessBean.setTransferEffect(transferEffect);
                fmVideoList.refresh();
                MovieClipDataBase.instance().changeOneTransfer(transferEffect, videoProcessBean.getFlag());
                if (position > 0) {
                    long seekTime = videoProcessBean.getRecordStartTime() - 1500;
                    if (seekTime < 0) {
                        seekTime = 0;
                    }
                    fmPlayer.seekSync(seekTime);
                    fmPlayer.playVideo();
                }

            }

            @Override
            public void onUseAll(TransferEffect transfer) {
                TransferEffect transferEffect = transfer.copy();

                for (VideoProcessBean videoDataList : videoDataLists) {
                    // 不能用同一个transferEffect，不然会指向同一个内存地址，后续会无法进行单个修改
                    TransferEffect transferEffect1 = new TransferEffect();
                    transferEffect1.setType(transferEffect.getType());
                    videoDataList.setTransferEffect(transferEffect1);
                }
                fmVideoList.refresh();
                MovieClipDataBase.instance().changeAllTransfer(transferEffect);
                fmPlayer.seekSync(0);
                fmPlayer.playVideo();
            }
        });

        // 裁剪界面功能键监听
        fmCrop.setOnCropCallback(new OnCropCallback() {
            @Override
            public void onSeekLeft(long value, Boolean isFromUser) {
                if (!MovieClipDataBase.instance().isPreView()) {
                    seekAsync(value);
                }
            }

            @Override
            public void onSeekRight(long value, Boolean isFromUser) {
                if (!MovieClipDataBase.instance().isPreView()) {
                    fmPlayer.setChangeRight(true);
                    seekAsync(value);
                }
            }


            // 点击了删除按钮
            @Override
            public void onDelete() {
                pauseVideo();
            }

            @Override
            public void onSureDelete() {
                // 点击了确定删除的按钮
                if (isSelectedVideo()) {
                    if (videoDataLists.size() == 1) {

                        RnToast.showToast(NewVideoProcessActivity.this, "请保留至少一个片段");
                        return;
                    }
                    int position = getCurrentSelectedPosition();
                    fmCrop.init(null);
                    clearList();
                    fmCGFragment.deleteCG(videoDataLists.get(position));
                    videoDataLists.remove(position);
                    deleteItemVideo(position);
                    unSelected();
                    // 刷新界面状态
                    fmCrop.updateButton();
                    hidTypeViews();
                    videoChange();
                } else {
                    RnToast.showToast(NewVideoProcessActivity.this, "请选择视频");
                }
            }

            @Override
            public void onChangeRotate(int rotate) {
                fmPlayer.ChangeRotate(rotate);
            }

            @Override
            public void onSpeedChange(double speed) {
                // 修改了速度
                if (isSelectedVideo()) {
                    pauseVideo();
                    fmPlayer.seek(0);
                    int position = getCurrentSelectedPosition();
                    VideoProcessBean bean = videoDataLists.get(position);
                    bean.setSpeed(speed);
                    MovieClipDataBase.instance().changeSpeed(speed);
                } else {
                    RnToast.showToast(NewVideoProcessActivity.this, "请选择视频");
                }
            }

        });


        fmCGFragment.setCgControlCallback(new CGControlCallback() {

            @Override
            public void onClickAddStart() {
                // 开始添加cg的时候设置当前时间
                pauseVideo();
                fmCGFragment.setCurrentVideoTime((int) fmPlayer.getCurrentTime());
            }

            @Override
            public void onClickAddCGOver(VideoTitleBean titleBea) {
                fmPlayer.addText(titleBea);
                fmSeekBar.setCGMark(MovieClipDataBase.instance().getTextStyleBeen(), getAllDuration());
                // 加完字幕后必须刷新一下，因为字幕拿过来是不可见的，要刷新下到底是否可见
                // 如果拿过来就可见预览时字幕会一闪而过
                if (fmCrop.isVisible()) {
                    fmCGFragment.refreshTextView(fmPlayer.getCurrentTime(), fmPlayer.getIsPause());
                } else {
                    fmCGFragment.refreshTextView((int) fmPlayer.getCurrentTime(), fmPlayer.getIsPause());
                }
            }

            @Override
            public void onClickDeleteCG(VideoTitleBean videoBean) {
                fmPlayer.removeView(videoBean);
                fmSeekBar.setCGMark(MovieClipDataBase.instance().getTextStyleBeen(), getAllDuration());
            }

            @Override
            public void onClickTextView(VideoTitleUse videoTitleUse, ViewTitleOnePart videoBean, TextView textView, VideoTitleBean videoTitleBean) {
                // 弹出字幕编辑界面，把videoBean的属性修改保存即可
                setFragmentVisible(true, fmCGEdit);
                setFragmentVisible(false, fmCGFragment);
                fmCGEdit.init(videoTitleUse, videoBean, textView, videoTitleBean);
                fmPlayer.pauseVideo();
                int time;
                if (fmCrop.isVisible()) {
                    time = fmPlayer.getCurrentTime();
                } else {
                    time = fmPlayer.getCurrentTime();
                }

//                QLog.d("onClick-TextView--time=%s", time);
                fmCGFragment.refreshTextView(time, true);
            }

            @Override
            public void onClickImageView(VideoTitleUse videoTitleUse, ViewTitleOnePart videoBean, ImageView imageView, VideoTitleBean videoTitleBean) {
                // 弹出字幕编辑界面，把videoBean的属性修改保存即可
                setFragmentVisible(true, fmImgCGEdit);
                setFragmentVisible(false, fmCGFragment);
                fmImgCGEdit.init(videoTitleUse, videoBean, imageView, videoTitleBean);
                fmPlayer.pauseVideo();
                int time;
                if (fmCrop.isVisible()) {
                    time = fmPlayer.getCurrentTime();
                } else {
                    time = fmPlayer.getCurrentTime();
                }

//                QLog.d("onClick-TextView--time=%s", time);
                fmCGFragment.refreshTextView(time, true);
            }

            @Override
            public void onMoving(VideoTitleBean videoBean) {
                fmPlayer.pauseVideo();
                int time = (int) fmPlayer.getCurrentTime();
                fmCGFragment.refreshTextView(time, true);
            }
        });

        fmMusic.setMusicCallBack(new MusicCallBack() {
            @Override
            public void onVolumeChanged(AudioInfoBean audioInfoBean) {
                Log.d(TAG, "onVolumeChanged：" + audioInfoBean.getRecordVolume());
                fmPlayer.setRecordVolume(audioInfoBean.getRecordVolume());
                fmPlayer.setVideoVolume(audioInfoBean.getOriginVolume());
                fmPlayer.setBackVolume(audioInfoBean.getBGMVolume());
            }

            @Override
            public void onClickSwitchVoice(boolean isOpen) {
                Log.d(TAG, "onVolumeChanged");

                if (isOpen) {
                    fmPlayer.setVideoVolume(MovieClipDataBase.instance().getAudioInfoBean().getRecordVolume());
                } else {
                    fmPlayer.setVideoVolume(0);
                }
            }

            @Override
            public void onSelectedMusic(MusicRes musicRes) {
                AudioInfoBean audioInfoBean = MovieClipDataBase.instance().getAudioInfoBean();
                audioInfoBean.setBackMusic(musicRes.getPcmPath());
                MovieClipDataBase.instance().setBackgroundAudio(musicRes.getPcmPath());
            }

            @Override
            public void onPrepare() {
                lockLayout();
                fmMusic.setStartTime(fmSeekBar.getPosition());
                pauseVideo();
            }

            @Override
            public void onRecordStart(long startTime) {
                // 开始录音时都设置为0，结束录音时恢复
                fmPlayer.setBackVolume(0);
                fmPlayer.setRecordVolume(0);
                fmPlayer.playVideo();
            }

            @Override
            public void onRecordStop() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        unLockLayout();
                        fmPlayer.pauseVideo();
                        AudioInfoBean audioInfoBean = MovieClipDataBase.instance().getAudioInfoBean();
                        // 恢复音量设置
                        fmPlayer.setBackVolume(audioInfoBean.getBGMVolume());
                        fmPlayer.setRecordVolume(audioInfoBean.getRecordVolume());
                        fmPlayer.setVideoVolume(audioInfoBean.getOriginVolume());
                        drawAllRect();
                    }
                });

            }

            @Override
            public void onRecording(long oldTime, long nowTime) {
                fmSeekBar.drawRect(oldTime, nowTime);
                fmSeekBar.setPosition(nowTime);
            }

            @Override
            public void onClickDelete() {
                List<VideoProcessBean> videoLists = MovieClipDataBase.instance().getMovies();
                AudioChange audioChange = fmSeekBar.getMAudioChange();
                long startTime = audioChange.getStartTime();
                long endTime = audioChange.getEndTime();
                if (startTime != 0 || endTime != 0) {
                    float start = startTime / (float) 1000;
                    float end = endTime / (float) 1000;
                    AudioHelper.getAudioHelperInstance().deleteRecord(start, end);
                    for (VideoProcessBean videoList : videoLists) {
                        videoList.deleteAudioChange(startTime, endTime);
                    }
                    drawAllRect();
                } else {
                    RnToast.showToast(NewVideoProcessActivity.this, "请选择要删除的录音片段");
                }
            }
        });

        fmColor.setCallback(new ColorFragment.ColorChangeCallback() {
            @Override
            public void onColorChange(QKColorFilterGroup filterGroup) {
                MovieClipDataBase.instance().setGroup(filterGroup);
            }
        });
        fmCGEdit.setCgEditCallback(new CGEditFragment.CGEditCallback() {
            @Override
            public void onClose() {
                if (fmSeekBar.isVisible()) {
                    fmSeekBar.setCGMark(MovieClipDataBase.instance().getTextStyleBeen(), getAllDuration());
                }
                if (isShowCGTab) {
                    showCGTab();
                }
            }
        });
        fmImgCGEdit.setCgEditCallback(new CGImageFragment.CGEditCallback() {
            @Override
            public void onClose() {
                if (fmSeekBar.isVisible()) {
                    fmSeekBar.setCGMark(MovieClipDataBase.instance().getTextStyleBeen(), getAllDuration());
                }
                if (isShowCGTab) {
                    showCGTab();
                }
            }
        });
    }

    public void playVideoOne(VideoProcessBean bean) {
        fmPlayer.setFilter(bean.getFilterType());
        fmCrop.init(bean);
        addVideoAndPlay(bean);
        fmCrop.updateButton();
        updateCropState();
    }

    // 全部删除
    public void deleteAll() {
        List<VideoProcessBean> videoLists = MovieClipDataBase.instance().getMovies();
        for (VideoProcessBean videoList : videoLists) {
            List<AudioPartBean> list = videoList.getListAudios();
            if (list == null) {
                continue;
            }
            for (AudioPartBean audioPartBean : list) {
                long startTime = audioPartBean.getStartTime();
                long endTime = audioPartBean.getEndTime();
                if (startTime != 0 || endTime != 0) {
                    float start = startTime / (float) 1000;
                    float end = endTime / (float) 1000;
                    AudioHelper.getAudioHelperInstance().deleteRecord(start, end);
                }
            }
            videoList.getListAudios().clear();
        }
        drawAllRect();
    }

    public void hideAllFragment() {
        setFragmentVisible(false, fmCrop, fmCGFragment, fmMusic, fmColor, fmCGEdit, fmImgCGEdit, fmSeekBar, fmTransferEdit);
    }

    // 获取视频总时长
    public long getAllDuration() {
        long baseTime = 0;
        for (VideoProcessBean bean : videoDataLists) {
            baseTime += bean.getDurationTime();
        }
        return baseTime;
    }

    public void seekAsync(long time) {
        fmPlayer.seek((int) time);
    }


    public void videoDestroy() {
//        fmPlayer.videoDestroy();
    }

    // 新建一个播放器并播放 // TODO: 2017/12/15 0015 是否来自预览呢
    public void addVideoAndPlay(VideoProcessBean videoProcessBean) {
        fmPlayer.showOneVideo(videoProcessBean);
    }

    // 开始预览
    public void startPreview() {
        fmPlayer.removeAllView();
        fmCGFragment.recoverText();
        fmPlayer.startPreview();
    }

    public void playVideo() {
        fmPlayer.playVideo();
    }

    public void pauseVideo() {
        fmPlayer.pauseVideo();
    }

    // 刷新裁剪 预览 删除等按钮的可用状态
    public void updateCropState() {

    }

    // 获取当前选中的视频item
    public VideoProcessBean getCurrentSelected() {
        int po = fmVideoList.getSelectedItem() - 1;
        if (po < 0) {
            return null;
        }
        return videoDataLists.get(po);
    }

    public int getCurrentSelectedPosition() {
        int po = fmVideoList.getSelectedItem() - 1;
        return po;
    }

    public void refreshVideoList() {
        fmVideoList.refresh();
    }

    //是否选中了待处理的视频块
    public boolean isSelectedVideo() {
        return fmVideoList.isSelect();
    }

    // 取消所有的选中
    public void unSelected() {
        fmVideoList.unSelected();
    }

    // 清空播放列表
    public void clearList() {
//        fmPlayer.clearList();
    }

    // 删除指定的视频块
    public void deleteItemVideo(int index) {
        fmVideoList.deleteItem(index);
    }

    public boolean getIsSelect() {
        return fmVideoList.isSelect();
    }


    // 是否正在处理中
    private boolean processing;
    private float totalTime;// 总计完成后的时长
    long startTime;
    long endTime;
    int PreTime = -1;
    private final int DRAW_DELAY = 15;//绘制动画的间隔时间
    // 用户选择的宽高
    private int targetWidth = 100, targetHeight = 100;

    // 四舍五入保留一位小数
    private float getNub(float x) {
        return Math.round(x * 10) / 10f;
    }

    private float getNub(double x) {
        return Math.round(x * 10) / 10f;
    }


    private String getName() {
        return "短视频_" + PublicUtils.getLocationTime();
    }

    // 录音时锁住其他布局，不允许点击
    private void lockLayout() {
        rlLockTop.setVisibility(View.VISIBLE);
        rlLockBottom.setVisibility(View.VISIBLE);
        fmMusic.lockLayout();

    }

    private void unLockLayout() {
        rlLockTop.setVisibility(View.INVISIBLE);
        rlLockBottom.setVisibility(View.INVISIBLE);
        fmMusic.unLockLayout();
    }

    //视频生成的模块
    // 开始高清/ 低清的短视频处理
    public void startCG(int width, int height) {
        if (complete != null) {
            Log.d(TAG, "complete != null");
            return;
        }
        // 截图--分别对播放器和字幕layout截图，设置到imageview里面
        // 遮盖住视频处理时的字幕动画效果
        fmPlayer.setMask();
        // 清空视频贴图数据

        final String outPath = getVideoOutPath();
        Log.d(TAG, "outPath:" + outPath);
        complete = new QKCompleteMovie(this, width, height, outPath, MovieClipDataBase.instance().getMovies());
        complete.setGroup(MovieClipDataBase.instance().getGroup());
        complete.addAllAudio(MovieClipDataBase.instance().getAudioInfoBean());
        complete.setCompleteControl(new CompleteControl() {
            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此时已在主线程中，更新UI
                        fmPlayer.getmAspectLayout().removeView(complete.getComplete().getVideoSurface());
                        RnToast.showToast(getApplicationContext(), "视频已经生成");
                        processing = false;
                        videoProcessStatePop.dismiss();
                        startPreview();
                        fmPlayer.clearMask();
                        // 因为视频处理过程中在不断变化字幕，所有取消处理后要重置字幕的变化保存状态
                        fmCGFragment.reset();
                        PreTime = -1;
                        L.d("onComplete");
                        complete = null;
                        File file = new File(outPath);
                        PublicUtils.updateFile(file, getApplicationContext());
                        //数据是使用Intent返回
                        Intent intent = new Intent();
                        //把返回数据存入Intent
                        String draft = saveToDraft();
                        intent.putExtra("path", outPath);
                        intent.putExtra("draft", draft);
                        intent.putExtra("draftId", draftId);

                        if (OutPutMessage.instance().getCallBack() != null) {
                            OutPutMessage.instance().getCallBack().onVideoOutPath(outPath);
                        }
                        //设置返回数据
                        NewVideoProcessActivity.this.setResult(videoResultFinish, intent);
                        //关闭Activity
                        NewVideoProcessActivity.this.finish();
                    }
                });
            }

            @Override
            public void progress(double progress) {
//                Log.d(TAG, "progress:" + progress);
                final int process100 = (int) (progress * 100);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此时已在主线程中，更新UI
                        videoProcessStatePop.setTvMsg(process100 + "%");
                    }
                });
            }

            @Override
            public void onStop() {
                Log.d(TAG, "onStoponStoponStoponStoponStop");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此时已在主线程中，更新UI
                        fmPlayer.getmAspectLayout().removeView(complete.getComplete().getVideoSurface());
                        RnToast.showToast(getApplicationContext(), "已取消");
                        processing = false;
                        videoProcessStatePop.dismiss();
                        startPreview();
                        fmPlayer.clearMask();
                        // 因为视频处理过程中在不断变化字幕，所有取消处理后要重置字幕的变化保存状态
                        fmCGFragment.reset();
                        PreTime = -1;
                        complete = null;
                    }
                });

            }

            @Override
            public void getBitmap(long time) {
//                Log.d("getBitmap", "getBitmap:" + time);
                Message message = Message.obtain(); //获取消息的载体

                message.what = RecordSdk.MSG_SHORT_VIDEO_PROGRESS;
                Bundle bundle = new Bundle();
                bundle.putInt("currentTime", (int) time);
                message.setData(bundle);
                msgHandler.sendMessage(message);
            }
        });
        fmPlayer.getmAspectLayout().addView(complete.getComplete().getVideoSurface(), 0);
        fmPlayer.getmAspectLayout().requestLayout();
        videoProcessStatePop.showAtLocation(getRootView(), Gravity.CENTER, 0, 0);
        videoProcessStatePop.setTvMsg("处理中。。。");
        // 开启解码和编码线程，分开两个方法便于排查问题


        processing = true;
        startTime = System.currentTimeMillis();
    }

    Bitmap resultBitmap = null;


    // 消息处理句柄--用于接收sdk中发出的消息
    private Handler msgHandler = new Handler(new MsgHandlerCallback());

    // 事件侦听的处理类
    private class MsgHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            // 短视频处理中
            if (msg.what == RecordSdk.MSG_SHORT_VIDEO_PROGRESS) {

                Bundle bundle = msg.getData();
                int currentTime = bundle.getInt("currentTime");
                Bitmap newMap = resultBitmap;
                if (currentTime - PreTime >= DRAW_DELAY || PreTime == 0) {

                    // 避免过于频繁调用 processCG
                    fmCGFragment.changeCG(currentTime);
                    PreTime = currentTime;

                    newMap = VideoTextHelper.getInstance().drawNewText(fmPlayer.getTextBitmap(), fmPlayer.getVideoLayoutWidth(), fmPlayer.getVideoLayoutHeight(), targetWidth, targetHeight);
                    resultBitmap = newMap;

                }

                if (complete != null) {
                    complete.getComplete().setWaterBitmap(newMap, currentTime);
                } else {
                    Log.d(TAG, "complete == null");
                }

            }
            return true;
        }
    }


    private String getVideoOutPath() {
        if (outputPath != null) {
            return outputPath;
        }
        String outPath = ConfigureConstants.QUKAN_PATH_PROCESS_VIDEO;
        String time = "short_video" + PublicUtils.getCurrentTime();
        String path = outPath + File.separator + time + ".mp4";
        return path;
    }

    // 点击生成按钮弹出的选择对话框和视频处理中对话框
    private void initPop() {
        videoGeneratePop = new CommonBottomThree(this);
        videoGeneratePop.setTvButton1("超清");// 1080*1920
        videoGeneratePop.setTvButton2("高清");// 720*1080

        videoGeneratePop.setListener(new CommonBottomThree.OnClickListenerBottomPup() {
            @Override
            public void onButton1Click(CommonBottomThree commonBottomThree) {
                //高清
                commonBottomThree.dismiss();
                int ratio = MovieClipDataBase.instance().getRatio();
                int width = 1920;
                int height = 1080;
                if (ratio == RATIO_1X1) {
                    width = 1080;
                    height = 1080;
                } else if (ratio == RATIO_3X4) {
                    width = 1080;
                    height = 1440;
                } else if (ratio == RATIO_4X3) {
                    width = 1440;
                    height = 1080;
                } else if (ratio == RATIO_16X9) {
                    width = 1920;
                    height = 1080;
                } else if (ratio == RATIO_9X16) {
                    width = 1080;
                    height = 1920;
                }
                Log.d(TAG, "RATIO:" + ratio + "targetWidth:" + targetWidth + " targetHeight:" + targetHeight);
                targetWidth = width;
                targetHeight = height;
                startCG(width, height);
            }

            @Override
            public void onButton2Click(CommonBottomThree commonBottomThree) {
                //低清
                //高清
                commonBottomThree.dismiss();
                int width = 1280;
                int height = 720;
                int ratio = MovieClipDataBase.instance().getRatio();
                if (ratio == RATIO_1X1) {
                    width = 720;
                    height = 720;
                } else if (ratio == RATIO_3X4) {
                    width = 720;
                    height = 960;
                } else if (ratio == RATIO_4X3) {
                    width = 960;
                    height = 720;
                } else if (ratio == RATIO_16X9) {
                    width = 1280;
                    height = 720;
                } else if (ratio == RATIO_9X16) {
                    width = 720;
                    height = 1280;
                }
                targetWidth = width;
                targetHeight = height;
                startCG(width, height);
            }

            @Override
            public void onButton3Click(CommonBottomThree commonBottomThree) {

            }
        });

        videoProcessStatePop = new VideoProcessPop(this);
        videoProcessStatePop.setListener(new VideoProcessPop.Listener() {
            @Override
            public void onClickCancel(VideoProcessPop videoProcessPop) {
                complete.stopEncode();
            }

            @Override
            public void onPause(VideoProcessPop videoProcessPop) {

            }

            @Override
            public void onResume(VideoProcessPop videoProcessPop) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        if (complete != null) {
            complete.stopEncode();
        } else {
            finish();
        }
    }

    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    public static void copyFile(String pathFrom, String pathTo) throws IOException {
        if (pathFrom.equalsIgnoreCase(pathTo)) {
            return;
        }

        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            inputChannel = new FileInputStream(new File(pathFrom)).getChannel();
            outputChannel = new FileOutputStream(new File(pathTo)).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

}
