package com.qukan.qkrecordupload.fileCut.processFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cgfay.cainfilter.utils.AspectFrameLayout;
import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.RnToast;
import com.qukan.qkrecordupload.bean.AudioInfoBean;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.bean.VideoTitleBean;
import com.qukan.qkrecordupload.qkCut.MovieClipDataBase;
import com.qukan.qkrecorduploadsdk.decoder.VideoPlayFinish;

import org.droidparts.util.L;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * 播放器
 * 对外提供 播放--预览播放，普通播放，暂停，销毁，新建，等接口
 */
public class PlayerFragment extends BaseFragment {
    private static final String TAG = "PlayerFragment";
    private Context mContext;

    private RelativeLayout rlContentRoot;

    @Getter
    public RelativeLayout rlTextRoot;

    public AspectFrameLayout mAspectLayout;

    // 视频中间点击的播放暂停按钮
    private ImageView ivPlayVideo;

    private ImageView ivMaskVideo;

    private ImageView ivMaskVideoText;

    private RelativeLayout rlPauseVideo;

    private LinearLayout llTab;

    @Setter
    @Getter
    int videoLayoutWidth, videoLayoutHeight;

    private Handler drawProgressHandler = new Handler();
    // 绘制进度条的间隔时间
    private long DELAY_MILLIS = 50;

    MovieClipDataBase instance = MovieClipDataBase.instance();

    @Setter
    public int leftSeek, rightSeek;

    @Setter
    public PlayStateListener playStateListener;

    boolean initOk = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostActivityCreated() {
        super.onPostActivityCreated();
        addTab();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_player_new, null);
        initView(view);
        return view;
    }

    private void initView(View view){

        rlContentRoot = (RelativeLayout) view.findViewById(R.id.rl_content_root);

        rlTextRoot = (RelativeLayout) view.findViewById(R.id.rl_text_root);

        mAspectLayout = (AspectFrameLayout) view.findViewById(R.id.layout_aspect);

        ivPlayVideo = (ImageView) view.findViewById(R.id.iv_play_video);
        ivPlayVideo.setOnClickListener(this);

        ivMaskVideo = (ImageView) view.findViewById(R.id.iv_mask_video);

        ivMaskVideoText = (ImageView) view.findViewById(R.id.iv_mask_text);

        rlPauseVideo = (RelativeLayout) view.findViewById(R.id.rl_pause_video);
        rlPauseVideo.setOnClickListener(this);

        llTab = (LinearLayout) view.findViewById(R.id.ll_tab);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;//不会走这个方法
    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        instance.stopPlayer();
        instance.resetThings();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == ivPlayVideo) {
            if (instance != null){
                playVideo();
            }
        } else if (v == rlPauseVideo) {
            if (instance != null){
                pauseVideo();
            }
        }
    }


    /**
     * 设置播放视频
     */
    public void setVideoAndPlay(){
        instance.setContext(getActivity(), mAspectLayout, getActivity());
        instance.setVideoPlayFinish(onCompleteListener);
        startPreview();
    }

    // 暂停，恢复播放
    public void playVideo() {
        if (instance == null) {
            RnToast.showToast(getActivity(), "播放器未初始化");
        }

        if (playStateListener != null) {
            playStateListener.onPlay(instance.isPreView());
        }
        if (!instance.isPreView()) {
            L.d("CurrentTime=%s", instance.getCurrentTime());
            if (instance.getCurrentTime()*instance.getSpeed() >= rightSeek || instance.getCurrentTime()*instance.getSpeed() <= leftSeek) {
                instance.seekToTimeSync(leftSeek/instance.getSpeed() / 1000.0);
            } else if (isChangeRight) {
                instance.seekToTimeSync(leftSeek/instance.getSpeed() / 1000.0);
            }
            isChangeRight = false;
        }
        instance.playIosPlayer();
        drawProgressHandler.post(runnable);
        ivPlayVideo.setVisibility(View.GONE);
    }

    public void pauseVideo(){
        Log.d("PlayerFragment","pauseVideo");
        if (instance == null) {
            RnToast.showToast(getActivity(), "播放器未初始化");
            return;
        }
        drawProgressHandler.removeCallbacksAndMessages(null);
        instance.pauseIosPlayer();
        if (playStateListener != null) {
            playStateListener.onPauseVideo(instance.isPreView());
        }
        ivPlayVideo.setVisibility(View.VISIBLE);
    }

    public VideoPlayFinish onCompleteListener = new VideoPlayFinish() {
        @Override
        public void onGetPicture(Bitmap bitmap) {
            if(bitmap != null){
                Log.d(TAG,"width:" + bitmap.getWidth());
                ivMaskVideo.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onComplete(){
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivPlayVideo.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        @Override
        public void onSurfaceCreated() {
            if (playStateListener != null) {
                playStateListener.onSurfaceChanged();
            }
        }
    };

    public void addVideoToPlayer(VideoProcessBean videoProcessBean) {
        instance.addOnePart(videoProcessBean);
    }

    public int getCurrentTime() {
        if (instance != null) {
            return (int) (instance.getCurrentTime() * 1000);
        } else {
            return 0;
        }
    }

    public void seekStartTime(long time){
        if (instance != null){

        }
    }

    public void showOneVideo(VideoProcessBean bean){
        if (instance != null){
            instance.showOnePart(bean);
            playVideo();
            hideCG();
            showFilter();
        }
    }

    public void  startPreview(){
        if (instance != null){
            drawProgressHandler.post(runnable);
            rlPauseVideo.setVisibility(View.VISIBLE);
            instance.createAllPlayer();
            playVideo();

            if (playStateListener != null) {
                playStateListener.onPlay(instance.isPreView());
            }
            showCG();
            hidFilter();
        }
    }

    public void seek(long time){
        if (instance != null){
            pauseVideo();
            instance.seekToTime(time / 1000.0);
        }
    }

    public void seekSync(long time){
        if (instance != null){
            pauseVideo();
            instance.seekToTimeSync(time / 1000.0);
        }
    }

    AudioInfoBean audioInfoBean = new AudioInfoBean();

    // 隐藏视频界面中间的控制视频播放/暂停的键
//    public void hideCentreControl() {
//        ivPlayVideo.setVisibility(View.GONE);
//    }

//    public void showCentreControl() {
//        ivPlayVideo.setVisibility(View.VISIBLE);
//    }

    // 隐藏字幕
    public void hideCG() {
        rlTextRoot.setVisibility(View.INVISIBLE);
    }

    // 取消隐藏字幕
    public void showCG() {
        rlTextRoot.setVisibility(View.VISIBLE);
    }

    boolean isImage = false;

    private void setRecordAndBGM() {
        if (!instance.isPreView()) {
            return;
        }
        File file = new File(audioInfoBean.getRecordPath());
        if (file.exists() && file.length() > 0 && file.canRead()) {
            setRecordAudio(file.getAbsolutePath());
        }

        File file1 = new File(audioInfoBean.getBackMusic());
        if (file1.exists() && file1.length() > 0 && file1.canRead()) {
            setBackgroundAudio(file1.getAbsolutePath());
        }

        setVideoVolume(audioInfoBean.getOriginVolume());
        setBackVolume(audioInfoBean.getBGMVolume());
        setRecordVolume(audioInfoBean.getRecordVolume());
    }

    boolean isChangeRight;

    public void setChangeRight(boolean changeRight) {
        isChangeRight = changeRight;
    }

    // 裁剪选择区块绘制播放进度条
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            double videoCurrentTime = 0;
            double rangeSeekBarRight = rightSeek / 1000.0;
            videoCurrentTime = instance.getCurrentTime();
//            Log.d(TAG, "videoCurrentTime: " + videoCurrentTime);
            if (playStateListener != null) {
                playStateListener.onPlaying(videoCurrentTime, instance.isPreView());
                if(ivPlayVideo.getVisibility() == View.VISIBLE){
                    ivPlayVideo.setVisibility(View.GONE);
                }
            }
//            L.d("videoCurrentPosition=%s,rangeSeekBarRight=%s", videoCurrentTime, rangeSeekBarRight);

            boolean playEnd = instance.isPlayerEnd();
            if(playEnd){

                pauseVideo();
                if (playStateListener != null) {
                    playStateListener.onPlaying(videoCurrentTime, instance.isPreView());
                }
            }else if (!instance.isPreView() && !isImage) {
                if (videoCurrentTime*instance.getSpeed() >= rangeSeekBarRight) {
                    pauseVideo();
                } else {
                    drawProgressHandler.postDelayed(this, DELAY_MILLIS);
                }
            } else {
                drawProgressHandler.postDelayed(this, DELAY_MILLIS);
            }

        }
    };


    private int getWindowWidth() {
        return PublicUtils.getWindowWidth(getActivity()).widthPixels;
    }

    Bitmap cachBitmap;

    // 设置播放器是否可见
    public void showPlayer() {
        rlContentRoot.setVisibility(View.VISIBLE);
    }

    public void hidePlayer() {
        rlContentRoot.setVisibility(View.INVISIBLE);
    }

    public boolean getIsPause() {
        return instance.getPauseState();
    }

    // 增加字幕view,--是否用于恢复字幕
    public void addText(VideoTitleBean titleBean) {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoLayoutWidth, videoLayoutHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        boolean isPause = getIsPause();
        RelativeLayout view = titleBean.getShowViews(videoLayoutWidth, videoLayoutHeight, getActivity(), isPause);
        rlTextRoot.addView(view, layoutParams);
        Log.d("addText","" + titleBean.getMStartTime());
    }

    public void removeView(VideoTitleBean videoBean) {
        rlTextRoot.removeView(videoBean.getShowViews(videoLayoutWidth, videoLayoutHeight, getActivity(), false));
    }

    // 清空所有字幕
    public void removeAllView() {
        rlTextRoot.removeAllViews();
    }

    // 调节视频原声的音量，0-100
    public void setVideoVolume(int volume) {
        instance.setOrgSoundValue(volume);
    }

    // mp3 BGM背景音
    public void setBackVolume(int volume) {

        instance.setBackSoundValue(volume);
    }

    // 配音大小
    public void setRecordVolume(int volume) {
        instance.setRecordValue(volume);
    }

    // pcm录音文件路径
    public void setRecordAudio(String path) {
        instance.setRecordAudio(path);
    }

    public void setBackgroundAudio(String path) {
        instance.setBackgroundAudio(path);
    }

    public interface PlayStateListener {
        void onPlay(boolean isFromPreview);

        void onPauseVideo(boolean isFromPreview);

        void onCompleted();

        // 正在播放，回调出播放时间，秒
        void onPlaying(double position, boolean isFromPreview);
        // 正在播放，回调出播放时间，秒
        void onSurfaceChanged();
        void onFilterChange(int type);
    }

    @Setter
    PlaySizeOverListener playSizeOverListener;
    public interface PlaySizeOverListener {
        // 播放器尺寸确定，初始化完成
        void onInit();
    }


    public void setMask() {
        // 截图--分别对播放器和字幕layout截图，设置到imageview里面
        // 遮盖住视频处理时的字幕动画效果
        Bitmap tBitmap = getTextBitmap();
        instance.getPicture();
        ivMaskVideo.setVisibility(View.VISIBLE);
        ivMaskVideoText.setVisibility(View.VISIBLE);
        ivMaskVideoText.setImageBitmap(tBitmap);
//        ivMaskVideo.setImageMatrix(videoSurface.getMmatrix());
    }

    public void clearMask() {
        ivMaskVideo.setVisibility(View.GONE);
        ivMaskVideoText.setVisibility(View.GONE);
    }

    // 获取字幕的截图
    public Bitmap getTextBitmap() {
        rlTextRoot.setDrawingCacheEnabled(true);
        rlTextRoot.buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap tBitmap = Bitmap.createBitmap(rlTextRoot.getDrawingCache());// 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        rlTextRoot.setDrawingCacheEnabled(false);
        return tBitmap;
    }

    @Setter
    SeekBarChange seekbarChange;
    public interface SeekBarChange{
        void onSeekChange(int progress, boolean isFromUser);
    }

    private String[] filters = new String[]{"原始", "怀旧", "素描", "清新", "恋橙", "稚颜", "青柠", "film", "微风", "萤火虫"};
    private List<String> filterList = Arrays.asList(filters);
    private List<TextView> tvFilterList = new ArrayList<>();

    private void addTab(){

        for (int i = 0; i < filterList.size(); i++){
            String filter = filterList.get(i);

            View view = View.inflate(mContext, R.layout.item_filter, null);

            TextView textView = view.findViewById(R.id.tv_filter);
            textView.setText(filter);

            tvFilterList.add(textView);

            final int finalI = i;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalI != selectFilter &&!(selectFilter == -1 &&finalI == 0)){
                        if (playStateListener != null) {
                            playStateListener.onFilterChange(finalI);
                        }
                    }
                    setFilter(finalI);
                }
            });

            llTab.addView(view);
        }

        //恢复默认状态，默认选中1
        if(selectFilter != -1){
            int index = selectFilter;
            selectFilter = -1;
            setFilter(index);
        }else{
            setFilter(0);
        }

    }
    private int selectFilter = -1;

    public void setFilter(int type){
        if(type == selectFilter){
            return;
        }
        if(selectFilter != -1){
            TextView old = tvFilterList.get(selectFilter);
            old.setTextColor(ContextCompat.getColor(mContext, R.color.short_color_8A));
        }

        TextView now = tvFilterList.get(type);
        now.setTextColor(Color.WHITE);
        selectFilter = type;

//        String filter = filterList.get(type);
//        Log.d(TAG, "filter= " + filter);
    }

    public void hidFilter(){
        llTab.setVisibility(View.GONE);
    }

    public void showFilter(){
        llTab.setVisibility(View.VISIBLE);
    }



    public void ChangeRotate(int rotate){
        instance.changeUseOrientation(rotate);
    }

    public AspectFrameLayout getmAspectLayout() {
        return mAspectLayout;
    }

    public void setmAspectLayout(AspectFrameLayout mAspectLayout) {
        this.mAspectLayout = mAspectLayout;
    }
}
