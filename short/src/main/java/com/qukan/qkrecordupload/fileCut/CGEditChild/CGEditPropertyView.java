package com.qukan.qkrecordupload.fileCut.CGEditChild;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.bean.VideoTitleBean;
import com.qukan.qkrecordupload.bean.VideoTitleUse;
import com.qukan.qkrecordupload.bean.ViewTitleOnePart;
import com.qukan.qkrecordupload.fileCut.customView.VerticalSeekBar;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;
import com.qukan.qkrecordupload.qkCut.MovieClipDataBase;

import java.util.ArrayList;

public class CGEditPropertyView implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView tvSize;

    private VerticalSeekBar vsbSize;

    private TextView tvDuration;

    private VerticalSeekBar vsbDuration;

    private TextView tvAlpha;

    private VerticalSeekBar vsbAlpha;

    private TextView tvTitleTime;
    private TextView tvCG;

    private VerticalSeekBar vsbTitle;

    Context context;
    View inflate;
    ViewTitleOnePart mVideoBean;
    TextView mTextView;
    VideoTitleUse mVideoTitleUse;
    VideoTitleBean videoTitleBean;
    NewVideoProcessActivity videoProcessActivity;

    public CGEditPropertyView(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        inflate = LayoutInflater.from(context).inflate(R.layout.fragment_cgedit_property, null);
//        Injector.inject(inflate, this);
        initView(inflate);
    }

    private void initView(View view) {
        tvSize = (TextView) view.findViewById(R.id.tv_property_size);

        vsbSize = (VerticalSeekBar) view.findViewById(R.id.vsb_size);

        tvDuration = (TextView) view.findViewById(R.id.tv_property_duration);

        vsbDuration = (VerticalSeekBar) view.findViewById(R.id.vsb_duration);

        tvAlpha = (TextView) view.findViewById(R.id.tv_property_alpha);

        vsbAlpha = (VerticalSeekBar) view.findViewById(R.id.vsb_alpha);

        tvTitleTime = (TextView) view.findViewById(R.id.tv_title_time);

        vsbTitle = (VerticalSeekBar) view.findViewById(R.id.vsb_title);
    }

    public View getPageView() {
        return this.inflate;
    }

    @Override
    public void onClick(View v) {

    }

    public void init(VideoTitleUse videoTitleUse, ViewTitleOnePart videoBean, TextView textView, VideoTitleBean videoTitleBean,
                     NewVideoProcessActivity videoProcessActivity, TextView tvCGShow) {

        this.mVideoBean = videoBean;
        this.mTextView = textView;
        this.mVideoTitleUse = videoTitleUse;
        this.videoTitleBean = videoTitleBean;
        this.videoProcessActivity = videoProcessActivity;
        tvCG = tvCGShow;

        // 字幕当前时间-传进来是毫秒，这里处理成秒
        int startTime = (int) mVideoTitleUse.getStartTime();
        int allTime = (int) videoProcessActivity.getAllDuration() / 1000;
        // 字幕出现在最后一秒会无法显示出来，这里减两秒
        if (allTime > 3) {
            allTime = allTime - 2;
        }

        vsbSize.setMax(66);
        vsbDuration.setMax(19);
        vsbAlpha.setMax(100);
        vsbTitle.setMax(allTime);

        vsbSize.setOnSeekBarChangeListener(this);
        vsbDuration.setOnSeekBarChangeListener(this);
        vsbAlpha.setOnSeekBarChangeListener(this);
        vsbTitle.setOnSeekBarChangeListener(this);

        vsbSize.setProgress(videoBean.getFontSize() - 6);
        vsbAlpha.setProgress((int) (videoBean.getAlpha() * 100));
        vsbDuration.setProgress((videoTitleUse.getDurationTime() / 1000) - 1);
        vsbTitle.setProgress(startTime / 1000);


        tvSize.setText(videoBean.getFontSize() + "pt");
        String current = PublicUtils.convertTime(startTime, 1);
        tvTitleTime.setText(current);
        tvDuration.setText(videoTitleUse.getDurationTime() / 1000 + "S");
        tvAlpha.setText(videoBean.getAlpha() * 100 + "%");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == vsbSize) {
            float scale = 1.0f;
            if (videoTitleBean.getParentHeight() > videoTitleBean.getParentWidth()) {
                scale = videoTitleBean.getParentHeight() / 1080.0f;
            } else {
                scale = videoTitleBean.getParentWidth() / 1080.0f;
            }

            progress = progress + 6;
            mVideoBean.setFontSize((int) (progress * scale));
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress * scale);
            tvCG.setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress * scale);
            tvSize.setText(progress + "pt");
        } else if (seekBar == vsbDuration) {
            progress = progress + 1;
            mVideoTitleUse.setDurationTime(progress * 1000);
            videoTitleBean.setMEndTime(videoTitleBean.getMStartTime() + progress * 1000);
            videoTitleBean.changeAnimation();
            tvDuration.setText(progress + "S");
        } else if (seekBar == vsbAlpha) {
            float alpha = progress / 100f;
            mVideoBean.setAlpha(alpha);
            // 0.0-1.0 完全透明-完全可见
            int textColor = mTextView.getCurrentTextColor();
            int a = (int) (255 * alpha);
            int r = Color.red(textColor);
            int g = Color.green(textColor);
            int b = Color.blue(textColor);
            mTextView.setTextColor(Color.argb(a, r, g, b));
            tvCG.setTextColor(Color.argb(a, r, g, b));
            tvAlpha.setText(progress + "%");
        } else if (seekBar == vsbTitle) {
            mVideoTitleUse.setStartTime(progress * 1000);
            long[] info = getVideoFlagAndTimeByCurrentTime(progress * 1000);
            mVideoTitleUse.setVideoFlag(info[0]);
            mVideoTitleUse.setSoftStartTime(info[1]);
            videoTitleBean.setMStartTime(progress * 1000);
            videoTitleBean.setMEndTime(progress * 1000 + mVideoTitleUse.getDurationTime());
            String current = PublicUtils.convertTime(progress * 1000, 1);
            tvTitleTime.setText(current);

        }
    }

    // 根据当前的播放时间获取当前视频块的标记值和在视频块中的时间位置
    private long[] getVideoFlagAndTimeByCurrentTime(int currentTime) {
        long[] result = new long[2];
        long base = 0;
        ArrayList<VideoProcessBean> list = MovieClipDataBase.instance().getMovies();
        for (int i = 0; i < list.size(); i++) {
            // TODO: 2017/7/5 0005 确定字幕位于哪一个视频块上,用同一个flag标记起来,相同的flag即可认为字幕依附于此视频块
            VideoProcessBean bean = list.get(i);
            base = bean.getDurationTime() + base;
            if (base >= currentTime) {
                //视频在总时间轴上的开始时间
                long flag = bean.getFlag();
                long startTime = base - bean.getDurationTime();

                long StartTimeInVideo = (long) ((currentTime - startTime) * bean.getSpeed() + bean.getStartTime());
                result[0] = flag;
                result[1] = StartTimeInVideo;
                return result;
            }
        }
        return null;
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
