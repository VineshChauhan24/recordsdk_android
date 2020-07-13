package com.qukan.qkrecordupload.fileCut.processFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.RnToast;
import com.qukan.qkrecordupload.TextUtil;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.compop.CommonPop;
import com.qukan.qkrecordupload.compop.OnClickListenerPup;
import com.qukan.qkrecordupload.custom.RangeSeekBar;
import com.qukan.qkrecordupload.custom.SpeedPickView;
import com.qukan.qkrecordupload.fileCut.Callback.OnCropCallback;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;

import org.droidparts.util.L;

import lombok.Getter;
import lombok.Setter;

import static com.qukan.qkrecordupload.PublicUtils.convertTime;
import static com.qukan.qkrecordupload.PublicUtils.getVideoDuration;


public class CropFragment extends BaseFragment {

    // 可裁剪时间不少于3000毫秒
    private static final long RESERVE_TIME = 3000;

    private NewVideoProcessActivity activity;


    private ImageView imgRotate;

    private ImageView imgDelete;


    private RangeSeekBar range_bar;

    private View vwMask;

    private TextView tvStartTime;

    private TextView tvCutTime;

    private TextView tvEndTime;

    private LinearLayout llRangBack;

    private LinearLayout llRang;

    private boolean changed = false;


    private SpeedPickView spView;

    private TextView txtSpeed;


    // 图片时长编辑
    private LinearLayout llImageRange;

    private ImageView ivReduceDuration;

    private EditText etTextDuration;

    private ImageView ivAddDuration;

    private SeekBar sbTextDuration;

    CommonPop deletePop;
    // 绘制缩略图背景
    Handler DrawThumHandler, handler;
    // 绘制背景线程
    HandlerThread mCheckMsgThread;
    // 视频背景缩略图的高
    private int BASE_SIZE;

    // 一共多少张视频缩略图
    final int nubBitmap = 8;
    // 每张图片的宽
    int eachBackBitmapWeight;

    // 用来标记右边的seek滑块是否调节过，如果调节过了那么点击播放时要从左边的滑块开始播放
    public static boolean isChangeRight;

    // 图片的持续时间
    double imageDuration;

    // 是否处于预览状态
    @Setter
    @Getter
    public boolean isPreview;

    @Setter
    OnCropCallback onCropCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NewVideoProcessActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop_new, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        imgRotate = (ImageView) view.findViewById(R.id.img_rotate);
        imgRotate.setOnClickListener(this);

        imgDelete = (ImageView) view.findViewById(R.id.img_delete);
        imgDelete.setOnClickListener(this);

        range_bar = (RangeSeekBar) view.findViewById(R.id.range_bar);

        vwMask = view.findViewById(R.id.vw_mask);
        vwMask.setOnClickListener(this);

        tvStartTime = (TextView) view.findViewById(R.id.tv_start_time);

        tvCutTime = (TextView) view.findViewById(R.id.tv_cut_time);

        tvEndTime = (TextView) view.findViewById(R.id.tv_end_time);

        llRangBack = (LinearLayout) view.findViewById(R.id.ll_rang_back);

        llRang = (LinearLayout) view.findViewById(R.id.ll_rang);

        spView = (SpeedPickView) view.findViewById(R.id.sp_view);

        txtSpeed = (TextView) view.findViewById(R.id.txt_speed);

        llImageRange = (LinearLayout) view.findViewById(R.id.ll_image_range);

        ivReduceDuration = (ImageView) view.findViewById(R.id.iv_reduce_duration);
        ivReduceDuration.setOnClickListener(this);

        etTextDuration = (EditText) view.findViewById(R.id.et_text_duration);

        ivAddDuration = (ImageView) view.findViewById(R.id.iv_add_duration);
        ivAddDuration.setOnClickListener(this);

        sbTextDuration = (SeekBar) view.findViewById(R.id.sb_text_duration);
    }

    @Override
    protected void onPostActivityCreated() {
        super.onPostActivityCreated();
        spView.setItemChangeLintener(new SpeedPickView.OnItemChange() {
            @Override
            public void onChange(int index) {
                if (onCropCallback != null) {
                    double speed = 1.0;
                    switch (index) {
                        case 0:
                            txtSpeed.setText("慢放2倍");
                            speed = 0.5;
                            break;
                        case 1:
                            txtSpeed.setText("慢放1.5倍");
                            speed = 0.667;
                            break;
                        case 2:
                            txtSpeed.setText("慢放1.25倍");
                            speed = 0.8;
                            break;
                        case 3:
                            txtSpeed.setText("原速");
                            speed = 1.0;
                            break;
                        case 4:
                            txtSpeed.setText("快放1.25倍");
                            speed = 1.25;
                            break;
                        case 5:
                            txtSpeed.setText("快放1.5倍");
                            speed = 1.5;
                            break;
                        case 6:
                            txtSpeed.setText("快放1.75倍");
                            speed = 1.75;
                            break;
                    }
                    onCropCallback.onSpeedChange(speed);
                    changed = true;
                    curVideoProcessBean.setSpeed(speed);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
//        if (v == llPreview) {
//            if (onCropCallback != null) {
//                onCropCallback.onClickPreview();
//            }
//            isPreview = true;
//            lockRangeBar();
//            updateButton();
//        } else if (v == llCrop) {
//            if (curVideoProcessBean == null) {
//                RnToast.showToast(getActivity(), "请选择要剪裁的视频");
//                return;
//            }
//            unLockRangeBar();
//            if (curVideoProcessBean.isVideo()) {
//                curVideoProcessBean.setStartTime((long) nowLeftSeek);
//                curVideoProcessBean.setEndTime((long) nowRightSeek);
//            } else {
//                int duration = imageDuration * 1000;
//                if (duration == 0) {
//                    RnToast.showToast(getActivity(), "时长不得为0s");
//                    return;
//                }
//                curVideoProcessBean.setEndTime(duration);
//            }
//            if (onCropCallback != null) {
//                onCropCallback.onClickCrop(curVideoProcessBean);
//            }
        } else if (v == imgDelete) {
            if (onCropCallback != null) {
                onCropCallback.onDelete();
            }
            if (deletePop == null) {
                deletePop = new CommonPop(activity);
                deletePop.setTvSure("确认");
                deletePop.setTvMsg("是否删除这个视频");
                deletePop.setOnClickListenerPup(new OnClickListenerPup() {
                    @Override
                    public void onSureClick() {
                        isPreview = false;
                        if (onCropCallback != null) {
                            onCropCallback.onSureDelete();
                        }
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
            }
            deletePop.showAtLocation(activity.getRootView(), Gravity.CENTER, 0, 0);
        } else if (v == imgRotate) {
            //主动旋转角度 0 不旋转 1：90度  2：180度 3：270度
            int useOrientation = curVideoProcessBean.getUseOrientation();
            useOrientation = (useOrientation + 1) % 4;
            curVideoProcessBean.setUseOrientation(useOrientation);
            if (onCropCallback != null) {
                onCropCallback.onChangeRotate(useOrientation);
            }
            changed = true;
        } else if (v == ivReduceDuration) {
            if (imageDuration > 0) {
                if(imageDuration > 1){
                    imageDuration = imageDuration - 1;
                }
                String text = ((int) (imageDuration * 10)) / 10.0 + "";
                etTextDuration.setText(text);
                sbTextDuration.setProgress((int) (imageDuration * 10));
                nowRightSeek = (int) (imageDuration * 1000);

            }
            changed = true;

        } else if (v == ivAddDuration) {
            if (imageDuration >= 0) {
                imageDuration = imageDuration + 1;
                String text = ((int) (imageDuration * 10)) / 10.0 + "";

                etTextDuration.setText(text);
                sbTextDuration.setProgress((int) (imageDuration * 10));
                nowRightSeek = (int) (imageDuration * 1000);

            }
            changed = true;
        }
    }

    public void lockRangeBar() {
        vwMask.setVisibility(View.VISIBLE);
    }

    // range恢复可拖动滑块
    public void unLockRangeBar() {
        vwMask.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCheckMsgThread != null) {
            mCheckMsgThread.quit();
        }
    }

    VideoProcessBean curVideoProcessBean;

    // 根据传进来的对象，初始化裁剪界面
    public void create() {
        mCheckMsgThread = new HandlerThread("drawBack");
        mCheckMsgThread.start();
        DrawThumHandler = new Handler(mCheckMsgThread.getLooper());
        handler = new Handler();
        BASE_SIZE = PublicUtils.dip2px(45);
        initRangSeekBar();

        tvStartTime.setText(convertTime(0, 0));


        sbTextDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if(progress <= 1){
                        progress = 1;
                    }
                    etTextDuration.setText(progress / 10.0 + "");
                    imageDuration = progress / 10.0;
                    changed = true;
                    nowRightSeek = (int) (imageDuration * 1000);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        etTextDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtil.isEmpty(s.toString())) {
                    etTextDuration.setText("0");
                    nowRightSeek = 0;

                    return;
                }
                double oldImgDuration = imageDuration;
                imageDuration = Double.valueOf(s + "");
                sbTextDuration.setProgress((int) (imageDuration * 10));
                L.d("etTextDuration--onTextChanged ");
                if (oldImgDuration != imageDuration) {
                    changed = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                L.d("etTextDuration--afterTextChanged");
            }
        });
    }

    // 图片或视频作为视频源时他要显示不同的调节界面
    private void showImageAdjust() {
        llImageRange.setVisibility(View.VISIBLE);
        unLockRangeBar();
        llRang.setVisibility(View.GONE);
    }

    private void showVideoClip() {
        unLockRangeBar();
        llImageRange.setVisibility(View.GONE);
        llRang.setVisibility(View.VISIBLE);
    }

    private void initRangSeekBar() {
        range_bar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                tvStartTime.setText(convertTime(min, 0));
                tvEndTime.setText(convertTime(max, 0));
                tvCutTime.setText(convertTime(max - min, 0));
                L.d("onRangeChanged--->currentLeft=%s,currentRight=%s,isFromUser=%s", min, max, isFromUser);
            }
        });

        range_bar.setOnRangeLeftChangedListener(new RangeSeekBar.OnRangeLeftChangedListener() {
            @Override
            public void onRangeLeftChanged(RangeSeekBar view, float min, boolean isFromUser) {
                nowLeftSeek = (min);

                if (onCropCallback != null) {
                    onCropCallback.onSeekLeft((long) (min / curVideoProcessBean.getSpeed()), isFromUser);
                }
                if (isFromUser) {
                    changed = true;
                }
                L.d("onRangeLeftChanged--->nowleftSeek=%s,isFromUser=%s", min, isFromUser);
            }
        });

        range_bar.setOnRangeRightChangedListener(new RangeSeekBar.OnRangeRightChangedListener() {
            @Override
            public void onRangeRightChanged(RangeSeekBar view, float max, boolean isFromUser) {
                nowRightSeek = (max);
                if (onCropCallback != null) {
                    onCropCallback.onSeekRight((long) (max / curVideoProcessBean.getSpeed()), isFromUser);
                }
                // 加一个判断，是否是手动触发的--reset的时候也会触发这个方法
                if (isFromUser) {
                    isChangeRight = true;
                }
                if (isFromUser) {
                    changed = true;
                }
                L.d("onRangeRightChanged--->nowrightSeek=%s,isFromUser=%s", max, isFromUser);
            }
        });
    }

    // 当前左右两边的裁剪的进度条值
    @Getter
    public float nowRightSeek, nowLeftSeek;

    public void drawPlay(float playCurrPercent) {
        range_bar.drawPlay(playCurrPercent);
    }

    public void init(VideoProcessBean videoProcessBean) {
        int index = 3;
        if (videoProcessBean != null) {
            if (videoProcessBean.getSpeed() == 0.5) {
                txtSpeed.setText("慢放2倍");
                index = 0;
            } else if (videoProcessBean.getSpeed() == 0.667) {
                txtSpeed.setText("慢放1.5倍");
                index = 1;
            } else if (videoProcessBean.getSpeed() == 0.8) {
                txtSpeed.setText("慢放1.25倍");
                index = 2;
            } else if (videoProcessBean.getSpeed() == 1.0) {
                txtSpeed.setText("原速");
                index = 3;
            } else if (videoProcessBean.getSpeed() == 1.25) {
                txtSpeed.setText("快放1.25倍");
                index = 4;
            } else if (videoProcessBean.getSpeed() == 1.5) {
                txtSpeed.setText("快放1.5倍");
                index = 5;
            } else if (videoProcessBean.getSpeed() == 1.75) {
                txtSpeed.setText("快放1.75倍");
                index = 6;
            } else {
                txtSpeed.setText("原速");
                videoProcessBean.setSpeed(1.0);
                index = 3;
            }
            spView.setImage(videoProcessBean.isImage());
        }

        spView.setCurrentIndex(index);
        changed = false;
        this.curVideoProcessBean = videoProcessBean;
        resetRangBar(videoProcessBean);

    }

    // 重置rangBar
    public void resetRangBar(VideoProcessBean videoProcessBean) {
        DrawThumHandler.removeCallbacksAndMessages(null);
        if (videoProcessBean == null) {
            llRang.setVisibility(View.GONE);
            llRangBack.setVisibility(View.VISIBLE);
            llImageRange.setVisibility(View.GONE);
            return;
        } else {
            llRangBack.setVisibility(View.GONE);
            if (videoProcessBean.isImage()) {
                llRang.setVisibility(View.GONE);
                llImageRange.setVisibility(View.VISIBLE);
                double duration = videoProcessBean.getEndTime() / 1000.0;
                sbTextDuration.setProgress((int) (duration * 10));

                String text = ((int) (duration * 10)) / 10.0 + "";

                etTextDuration.setText(text);
                imageDuration = duration;
                nowRightSeek = (int) (imageDuration * 1000);

                return;
            }
            llRang.setVisibility(View.VISIBLE);
            llImageRange.setVisibility(View.GONE);
//            tvCrop.setText("剪裁");
//            ivCrop.setImageResource(R.mipmap.clip);
        }
        long durationTime = (long) (getVideoDuration(videoProcessBean.getPath()));

        setRangeSeekBarBack(videoProcessBean.getPath(), 0, durationTime);
        float startTime = (float) (videoProcessBean.getStartTime());
        float endTime = (float) (videoProcessBean.getEndTime());
        range_bar.setRules(0, durationTime);
        float mReservePercent = RESERVE_TIME / (float) durationTime;
        range_bar.setReservePercent(mReservePercent);
        range_bar.reset();
        // 重新定位左右滑块位置，和设置时间显示
        range_bar.setSeekValue(startTime / (float) durationTime, endTime / (float) durationTime);
        tvStartTime.setText(convertTime(startTime, 0));
        tvEndTime.setText(convertTime(endTime, 0));
        tvCutTime.setText(convertTime(endTime - startTime, 0));
    }


    private void setRangeSeekBarBack(final String path, long startTime, long endTime) {
        final Paint mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);
        final Bitmap resultBitmap = Bitmap.createBitmap(getWindowWidth(),
                BASE_SIZE, Bitmap.Config.ARGB_4444);
        // 使用空白图片生成canvas
        final Canvas canvas = new Canvas(resultBitmap);
        // 注意时间单位
        final long average = (endTime - startTime) / nubBitmap;
        // 提取视频区域缩略图时要加上开始时间这个偏移量，不然会一直从头开始取预览图片
        final long offsetTime = startTime;
        DrawThumHandler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < nubBitmap; i++) {
                    canvas.drawBitmap(getVideoThumb(path, offsetTime + average * i), eachBackBitmapWeight * i, 0, mBitPaint);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            range_bar.setBack(resultBitmap);
                        }
                    });
//                    L.d("getVideoThumb=%s", average * i);
                }
            }
        });
    }

    public Bitmap getVideoThumb(String path, long time) {
        eachBackBitmapWeight = getWindowWidth() / nubBitmap;
        Bitmap bitmap = null;
        if (PublicUtils.isImage(path)) {
            bitmap = VideoProcessBean.compressImageFromFile(path, eachBackBitmapWeight, BASE_SIZE);
        } else {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            bitmap = mmr.getFrameAtTime(time * 1000);
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

    private int getWindowWidth() {
        return PublicUtils.getWindowWidth(activity).widthPixels;
    }

    // 更新预览 裁剪 删除按钮状态是否是可用状态
    public void updateButton() {
        if (!isAdded()) {
            return;
        }
        if (getActivity() == null) {
            return;
        }
        // 更新删除按钮
        if (activity.getIsSelect()) {
//            llDelete.setEnabled(true);
//            ivDelete.setImageResource(R.mipmap.delete_clip_selected);
//            tvDelete.setTextColor(getResources().getColor(R.color.clip_icon));
        } else {
//            llDelete.setEnabled(false);
//            ivDelete.setImageResource(R.mipmap.delete_clip);
//            tvDelete.setTextColor(getResources().getColor(R.color.clip_text));
        }
        // 更新预览按钮
        if (activity.getVideoDataLists().isEmpty()) {
//            llPreview.setEnabled(false);
//            ivPreview.setImageResource(R.mipmap.clip_preview);
//            tvPreview.setTextColor(getResources().getColor(R.color.clip_icon));
        } else {
//            llPreview.setEnabled(true);
//            ivPreview.setImageResource(R.mipmap.clip_preview_selected);
//            tvPreview.setTextColor(getResources().getColor(R.color.clip_icon));
        }

        // 更新裁剪按钮
        if (activity.getIsSelect()) {
//            llCrop.setEnabled(true);
//            llCrop.setBackgroundResource(R.drawable.circle_crop_bak);
//            ivClipMask.setVisibility(View.GONE);
        } else {
//            llCrop.setEnabled(false);
//            llCrop.setBackgroundResource(R.drawable.circle_crop_bak_invalid);
//            ivClipMask.setVisibility(View.VISIBLE);
        }
    }


    public void sureClipTime() {
        unLockRangeBar();
        if (curVideoProcessBean.isVideo()) {
            curVideoProcessBean.setStartTime((long) nowLeftSeek);
            curVideoProcessBean.setEndTime((long) nowRightSeek);
        } else {
            int duration = (int) (imageDuration * 1000);
            if (duration == 0) {
                RnToast.showToast(getActivity(), "图片时长不得为0秒,已修改为0.5秒");
                duration = (int) (0.5 * 1000);
            }
            Log.d("sureClipTime", "duration:" + duration);

            curVideoProcessBean.setEndTime(duration);
        }
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
