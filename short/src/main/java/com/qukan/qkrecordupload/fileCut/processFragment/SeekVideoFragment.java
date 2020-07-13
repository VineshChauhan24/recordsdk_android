package com.qukan.qkrecordupload.fileCut.processFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.AudioChange;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.bean.VideoTitleUse;
import com.qukan.qkrecordupload.custom.MovePlayControl;
import com.qukan.qkrecordupload.custom.TextMarkLayout;
import com.qukan.qkrecordupload.fileCut.Callback.SeekVideoCallback;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static android.graphics.Bitmap.createBitmap;
import static com.qukan.qkrecordupload.PublicUtils.convertTime;

public class SeekVideoFragment extends BaseFragment {
    // 一个带有视频背景缩略图的seekbar

    private RelativeLayout rlDrawVideo;

    private MovePlayControl ivPlayControl;

    private RelativeLayout rlDrawRect;

    private RelativeLayout rl_root;

    private LinearLayout ll_root;

    private TextMarkLayout rlTextMark;

    private int BASE_SIZE;

    // handler 用于在子线程中去生成带缩略图的seekbar背景
    Handler threadHandler, handler;
    HandlerThread handlerThread;

    @Setter
    SeekVideoCallback seekVideoCallback;

    private long allTime;

    Bitmap rectBitmapBack, videoBitmapBack;
    Drawable rectDrawable, videoDrawable;
    Canvas rectCanvas, videoCanvas;
    Paint paint, mBitPaint;

    // seek区域的宽高，用来绘制背景图片的时候确定范围
    int mHeight;
    int mWeight;
    // 一共多少张视频缩略图
    final int nubBitmap = 8;
    // 每张图片的宽
    int eachBackBitmapWeight;

    @Getter
    AudioChange mAudioChange;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_seek_video_new, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rlDrawVideo = (RelativeLayout) view.findViewById(R.id.rl_draw_back);
        rlDrawVideo.setOnClickListener(this);

        ivPlayControl = (MovePlayControl) view.findViewById(R.id.iv_play_control);

        rlDrawRect = (RelativeLayout) view.findViewById(R.id.rl_draw_rect);

        rl_root = (RelativeLayout) view.findViewById(R.id.rl_root);
        rl_root.setOnClickListener(this);

        ll_root = (LinearLayout) view.findViewById(R.id.ll_root);
        ll_root.setOnClickListener(this);

        rlTextMark = (TextMarkLayout) view.findViewById(R.id.rl_text_mark);
    }

    @Override
    protected void onPostActivityCreated() {
        super.onPostActivityCreated();
        handler = new Handler();
        BASE_SIZE = this.getResources().getDimensionPixelSize(R.dimen.cg_seek_height);

        handlerThread = new HandlerThread("drawThumb");
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper());

        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.seekbar_mask));
        paint.setStyle(Paint.Style.FILL);
        mAudioChange = new AudioChange();
        initRect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (threadHandler != null) {
            threadHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 线程退出后不能再次启用，要保证一个创建 一个退出
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

    private void initRect() {
        // 根据布局文件的设置这里写死了宽高
        mHeight = BASE_SIZE;
        int margin = this.getResources().getDimensionPixelSize(R.dimen.seek_view_margin);
        mWeight = getWindowWidth() - 2 * margin;
        ivPlayControl.setParentWidth(mWeight);
        // 滑块自身的宽
        int dragWidth = this.getResources().getDimensionPixelSize(R.dimen.seek_video_thumb);
        ivPlayControl.setMWidth(dragWidth);
        rectBitmapBack = createBitmap(mWeight, mHeight, Bitmap.Config.ARGB_4444);
        videoBitmapBack = createBitmap(mWeight, mHeight, Bitmap.Config.ARGB_4444);
        rectDrawable = new BitmapDrawable(rectBitmapBack);
        videoDrawable = new BitmapDrawable(videoBitmapBack);
        rectCanvas = new Canvas(rectBitmapBack);
        videoCanvas = new Canvas(videoBitmapBack);
        rlDrawRect.setBackground(rectDrawable);
        rlDrawVideo.setBackground(videoDrawable);

        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);
    }

    List<VideoProcessBean> stateLists = new ArrayList<>();

    // parms: 视频总时长--单位毫秒
    public void init(List<VideoProcessBean> list) {
        this.stateLists = list;
        if (stateLists.isEmpty()) {
            return;
        }
        allTime = getAllDuration();
        ivPlayControl.setOnChangeListener(new MovePlayControl.OnChangeListener() {
            @Override
            public void onPositionChange(MovePlayControl movePlayControl, float percent, boolean isFromUser) {
                // 如果是由用户拖动触发
                int current = (int) (allTime * percent);
                String time = convertTime(current, 0);
//                QLog.d("current=%s,time=%s,allTime=%s,percent=%s", current, time, allTime, percent);
                if (seekVideoCallback != null) {
                    seekVideoCallback.onChangeSeek(current, isFromUser);
                }
            }
        });
        setSeekBarBack();
    }

    // 毫秒
    public void setPosition(long time) {
        float percent = time / (float) allTime;
        ivPlayControl.setPosition(percent);
//        QLog.d("Position-set time1=%s,percent=%s,allTime=%s", time, percent, allTime);
    }

    // 毫秒
    public long getPosition() {
        float percent = ivPlayControl.getPosition();
        long position = (long) (allTime * percent);
//        QLog.d("Position-get time2=%s", position);
        return position;
    }

    // 获取视频总时长-毫秒
    private long getAllDuration() {
        long baseTime = 0;
        for (VideoProcessBean bean : stateLists) {
            baseTime += bean.getDurationTime();
        }
        return baseTime;
    }

    public void clearRect() {
        rectCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mAudioChange.setStartTime(0);
        mAudioChange.setEndTime(0);
        rlDrawRect.removeAllViews();
        rlDrawRect.invalidate();
    }

    // 录音时绘制矩形--毫秒单位
    public void drawRect(final long startTime, final long endTime) {
        mAudioChange.setStartTime(0);
        mAudioChange.setEndTime(0);
        for (int i = 0; i < rlDrawRect.getChildCount(); i++) {
            ImageView view = (ImageView) rlDrawRect.getChildAt(i);
            view.setBackground(getActivity().getResources().getDrawable(R.drawable.record_rect_back));
        }
        float percentLeft = startTime / (float) allTime;
        float percentRight = endTime / (float) allTime;
        int w = rlDrawRect.getWidth();
        int h = rlDrawRect.getHeight();
        float right = w * percentRight;
        float left = w * percentLeft;

        rectCanvas.drawRect(left, 0, right, h, paint);
        rlDrawRect.invalidate();
    }

    // 如果界面没绘制完成rlDrawRect的宽高一直会是0,所有延迟绘制矩形
    public void drawRectAll(final long startTime, final long endTime) {
        int w = rlDrawRect.getWidth();
        if (w == 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int w = rlDrawRect.getWidth();
                    if (w == 0) {
                        handler.postDelayed(this, 100);
                    } else {
                        drawRecord(startTime, endTime);
                    }
                }
            }, 100);
        } else {
            drawRecord(startTime, endTime);
        }
    }

    public void drawRecord(long startTime, long endTime) {
        float percentLeft = startTime / (float) allTime;
        float percentRight = endTime / (float) allTime;
        int w = rlDrawRect.getWidth();
        int h = rlDrawRect.getHeight();

        float right = w * percentRight;
        float left = w * percentLeft;

        ImageView imageView = new ImageView(getActivity());
        AudioChange audioChange = new AudioChange();
        audioChange.setStartTime(startTime);
        audioChange.setEndTime(endTime);
        imageView.setTag(audioChange);
        imageView.setBackground(getActivity().getResources().getDrawable(R.drawable.record_rect_back));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (right - left), RelativeLayout.LayoutParams.MATCH_PARENT);
        params.setMargins((int) left, 0, 0, 0);
        imageView.setLayoutParams(params);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < rlDrawRect.getChildCount(); i++) {
                    ImageView view = (ImageView) rlDrawRect.getChildAt(i);
                    view.setBackground(getActivity().getResources().getDrawable(R.drawable.record_rect_back));
                }
                v.setBackgroundColor(getActivity().getResources().getColor(R.color.seekbar_selected));
                AudioChange audioChange = (AudioChange) v.getTag();
                mAudioChange.setStartTime(audioChange.getStartTime());
                mAudioChange.setEndTime(audioChange.getEndTime());
                rlDrawRect.invalidate();
            }
        });
        rlDrawRect.addView(imageView);
    }

    private int getWindowWidth() {
        return PublicUtils.getWindowWidth(getActivity()).widthPixels;
    }

    volatile boolean isRunning;

    public void setSeekBarBack() {
        isRunning = false;
        // 绘图区域的尺寸
        // 每张截图的宽
        eachBackBitmapWeight = mWeight / nubBitmap;
        long duration = getAllDuration();
        // 注意时间单位--根据所需的总图片算出截图之间的间隔时间
        final float average = duration * 1f / 1000 / nubBitmap;
        videoCanvas.drawColor(Color.BLACK);
        rlDrawVideo.invalidate();
        threadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                for (int i = 0; i < nubBitmap; i++) {
                    String[] videoInfo = getVideoByTime(average * i);
                    if (videoInfo == null) {
                        continue;
                    }
                    videoCanvas.drawBitmap(getVideoThumb(videoInfo[0], Long.valueOf(videoInfo[1])), eachBackBitmapWeight * i, 0, mBitPaint);
                    if (!isRunning) {
                        videoCanvas.drawColor(Color.BLACK);
                        return;
                    }
                    android.app.Activity activity = getActivity();
                    // 封面界面如果缩略图未绘制完成就关闭界面的话，这边会为null
                    if (activity == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rlDrawVideo.invalidate();
                        }
                    });
                }
            }
        }, 300);
    }

    //根据当前时间算出在lists中对应的视频块，和这个视频块中视频的真正对应时间（单位 毫秒）
    private String[] getVideoByTime(float time) {
        long newTime = (long) (time * 1000);
        long baseTime = 0;
        for (VideoProcessBean videoProcessBean : stateLists) {
            baseTime += videoProcessBean.getDurationTime();
            if (baseTime >= newTime) {
                long realTime = (long) ((newTime - (baseTime - videoProcessBean.getDurationTime()) + videoProcessBean.getStartTime()) * videoProcessBean.getSpeed());
                String path = videoProcessBean.getPath();
                return new String[]{path, realTime + ""};
            }
        }
        return null;
    }

    // 根据时间在视频块lists里面获取截图,输出原比例的宽高固定的，截取原图中间部分的图片
    // 这里time的单位是豪秒
    public Bitmap getVideoThumb(String path, long time) {
        Bitmap bitmap;
        if (PublicUtils.isImage(path)) {
            bitmap = BitmapFactory.decodeFile(path);
        } else {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            bitmap = mmr.getFrameAtTime(time * 1000, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.BLACK);
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int offsetX = 0, offsetY = 0;
        // 横屏图片
        float scaleA = (float) BASE_SIZE / height;
        float scaleB = (float) eachBackBitmapWeight / width;
        float scale = Math.max(scaleA, scaleB);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 先把图片缩放到一定大小
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        if (height > width) {
            // 竖屏
            offsetY = (newBitmap.getHeight() - BASE_SIZE) / 2;

        } else {
            offsetX = (newBitmap.getWidth() - eachBackBitmapWeight) / 2;
        }

        return Bitmap.createBitmap(newBitmap, offsetX, offsetY, eachBackBitmapWeight, BASE_SIZE);
    }

    public void setCGMark(List<VideoTitleUse> videoTitleUseData, long allTime) {
        rlTextMark.drawMark(videoTitleUseData, allTime);
    }

    @Override
    public void onClick(View v) {

    }

}
