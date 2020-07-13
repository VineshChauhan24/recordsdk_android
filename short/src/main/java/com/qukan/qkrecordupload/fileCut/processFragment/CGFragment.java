package com.qukan.qkrecordupload.fileCut.processFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.TextUtil;
import com.qukan.qkrecordupload.bean.PointPath;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.bean.VideoTitleBean;
import com.qukan.qkrecordupload.bean.VideoTitleUse;
import com.qukan.qkrecordupload.bean.ViewTitleOnePart;
import com.qukan.qkrecordupload.fileCut.Callback.CGControlCallback;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;

import org.droidparts.annotation.inject.InjectParentActivity;
import org.droidparts.util.L;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Setter;

/**
 * 字幕控制类--字幕的添加和字幕的点击事件监听都会在这里处理
 * 这边把字幕的点击事件又传递到了上层的activity中，统一去处理事件逻辑
 */
public class CGFragment extends BaseFragment implements VideoTitleBean.OnClickListener {

    private NewVideoProcessActivity activity;

    private RelativeLayout rl_style_1;

    private RelativeLayout rl_style_2;

    private RelativeLayout rl_style_3;

    private RelativeLayout rl_style_4;

    private RelativeLayout rl_style_5;

    private RelativeLayout rl_style_6;

    private RelativeLayout rl_style_7;

    private RelativeLayout rl_style_8;

    private RelativeLayout rl_style_9;

    private TextView tvTimeAndLocation;

    String RED = "#cc0000";
    String WHITE = "#ffffff";
    String YELLOW = "#FFD810";
    String GREEN = "#76cc50";
    String BLUE = "#69a2fb";
    String BLACK = "#000000";

    // 预览样式的字幕持续时长
    int DURATION_SHORT = 3000;
    int DURATION_MIDDLE = 4000;
    int DURATION_LONG = 7500;

    // 保存了所有的字幕信息，类似一个包装类，可以从此类中获取所需要的信息
    List<VideoTitleBean> videoTitleData = new ArrayList<>();

    // 所有字幕信息类的需要用到的属性--界面跳转要恢复字幕的话要维护好这个list
    List<VideoTitleUse> videoTitleUseData;

    @Setter
    public CGControlCallback cgControlCallback;

    // 判断是否需要把设置界面的片头片尾默认的动画加上
    @Setter
    private boolean needVideoTitle, needVideoEnd;

    // 视频类型的片头片尾
    @Setter
    private String logoPath;

    // 在添加视频前设置好这个值
    @Setter
    int currentVideoTime;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NewVideoProcessActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subtitle, null);
        initView(view);
        return view;
    }

    private void initView(View view){
        rl_style_1 = (RelativeLayout) view.findViewById(R.id.rl_style_1);
        rl_style_1.setOnClickListener(this);

        rl_style_2 = (RelativeLayout) view.findViewById(R.id.rl_style_2);
        rl_style_2.setOnClickListener(this);

        rl_style_3 = (RelativeLayout) view.findViewById(R.id.rl_style_3);
        rl_style_3.setOnClickListener(this);

        rl_style_4 = (RelativeLayout) view.findViewById(R.id.rl_style_4);
        rl_style_4.setOnClickListener(this);

        rl_style_5 = (RelativeLayout) view.findViewById(R.id.rl_style_5);
        rl_style_5.setOnClickListener(this);

        rl_style_6 = (RelativeLayout) view.findViewById(R.id.rl_style_6);
        rl_style_6.setOnClickListener(this);

        rl_style_7 = (RelativeLayout) view.findViewById(R.id.rl_style_7);
        rl_style_7.setOnClickListener(this);

        rl_style_8 = (RelativeLayout) view.findViewById(R.id.rl_style_8);
        rl_style_8.setOnClickListener(this);

        rl_style_9 = (RelativeLayout) view.findViewById(R.id.rl_style_9);
        rl_style_9.setOnClickListener(this);

        tvTimeAndLocation = (TextView) view.findViewById(R.id.tv_style_date);
        //tvTimeAndLocation.setOnClickListener(this);
    }

    @Override
    protected void onPostActivityCreated() {
        super.onPostActivityCreated();
    }

    public void init(List<VideoTitleUse> videoTitleUseData) {
        this.videoTitleUseData = videoTitleUseData;
        tvTimeAndLocation.setText(getCurrentData());
    }

    @Override
    public void onClick(View v) {
        if (v == rl_style_1) {
            addStyle_1();
        } else if (v == rl_style_2) {
            addStyle_2();
        } else if (v == rl_style_3) {
            addStyle_3();
        } else if (v == rl_style_4) {
            addStyle_4();
        } else if (v == rl_style_5) {
            addStyle_5();
        } else if (v == rl_style_6) {
            addStyle_6();
        } else if (v == rl_style_7) {
            addStyle_7();
        } else if (v == rl_style_8) {
            addStyle_8();
        } else if (v == rl_style_9) {
            addStyle_9();
        }
    }

    float BOTTOM = 0.02f;

    float LEFT_LITTLE = 0.05f;

    float RIGHT_LITTLE = 0.001f;

    public void addStyle_1() {
        clickAddStart();
        PointPath pointPath = new PointPath();
        pointPath.setMarginLeft(LEFT_LITTLE);
        pointPath.setMarginBottom(BOTTOM);

        ViewTitleOnePart viewTitleOnePart = new ViewTitleOnePart();
        viewTitleOnePart.setText(getString(R.string.company_name));
        viewTitleOnePart.setBackgroundColor(YELLOW);
        viewTitleOnePart.setTextColor(BLACK);
        viewTitleOnePart.setAddBackground(true);
        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(1);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);

        videoTitleUse.setDurationTime(DURATION_SHORT);
        addText(videoTitleUse, false);
    }

    public void addStyle_2() {
        clickAddStart();
        ViewTitleOnePart viewTitleOnePart_1 = new ViewTitleOnePart();
        viewTitleOnePart_1.setText(getString(R.string.company_name));
        viewTitleOnePart_1.setTextColor(WHITE);
        viewTitleOnePart_1.setFontSize(12);

        ViewTitleOnePart viewTitleOnePart_2 = new ViewTitleOnePart();
        viewTitleOnePart_2.setText(getString(R.string.company_function));
        viewTitleOnePart_2.setTextColor(YELLOW);
        viewTitleOnePart_2.setFontSize(12);

        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart_1);
        titleOneParts.add(viewTitleOnePart_2);

        PointPath pointPath = new PointPath();
        pointPath.setMarginBottom(BOTTOM);
        pointPath.setMarginLeft(LEFT_LITTLE);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(2);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);
        videoTitleUse.setDurationTime(DURATION_LONG);
        addText(videoTitleUse, false);
    }


    public void addStyle_3() {
        clickAddStart();
        ViewTitleOnePart viewTitleOnePart_1 = new ViewTitleOnePart();
        viewTitleOnePart_1.setText(getString(R.string.company_name));
        viewTitleOnePart_1.setTextColor(WHITE);
        viewTitleOnePart_1.setFontSize(12);

        ViewTitleOnePart viewTitleOnePart_2 = new ViewTitleOnePart();
        viewTitleOnePart_2.setText(getString(R.string.company_function));
        viewTitleOnePart_2.setTextColor(WHITE);
        viewTitleOnePart_2.setFontSize(12);

        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart_1);
        titleOneParts.add(viewTitleOnePart_2);

        PointPath pointPath = new PointPath();
        pointPath.setMarginBottom(BOTTOM);
        pointPath.setMarginLeft(LEFT_LITTLE);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(3);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);
        videoTitleUse.setDurationTime(DURATION_LONG);
        addText(videoTitleUse, false);
    }

    public void addStyle_4() {
        clickAddStart();
        ViewTitleOnePart viewTitleOnePart_1 = new ViewTitleOnePart();
        viewTitleOnePart_1.setText(getString(R.string.company_name));
        viewTitleOnePart_1.setTextColor(WHITE);
        viewTitleOnePart_1.setFontSize(12);

        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart_1);

        PointPath pointPath = new PointPath();
        pointPath.setMarginBottom(BOTTOM);
        pointPath.setFirstMarginRight(RIGHT_LITTLE);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(4);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);
        videoTitleUse.setDurationTime(DURATION_SHORT);
        addText(videoTitleUse, false);
    }

    public void addStyle_5() {
        clickAddStart();
        ViewTitleOnePart viewTitleOnePart_1 = new ViewTitleOnePart();
        viewTitleOnePart_1.setText(getString(R.string.company_name));
        viewTitleOnePart_1.setTextColor(WHITE);
        viewTitleOnePart_1.setBackgroundColor(BLUE);
        viewTitleOnePart_1.setAddBackground(true);
        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart_1);

        PointPath pointPath = new PointPath();
        pointPath.setMarginLeft(LEFT_LITTLE);
        pointPath.setMarginBottom(BOTTOM);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(5);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);
        videoTitleUse.setDurationTime(DURATION_SHORT);
        addText(videoTitleUse, false);
    }

    public void addStyle_6() {
        clickAddStart();
        ViewTitleOnePart viewTitleOnePart_1 = new ViewTitleOnePart();
        viewTitleOnePart_1.setText(getCurrentData());
        viewTitleOnePart_1.setTextColor(WHITE);
        viewTitleOnePart_1.setBackgroundColor("#7e000000");
        viewTitleOnePart_1.setFontSize(12);
        viewTitleOnePart_1.setAddBackground(true);
        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart_1);

        PointPath pointPath = new PointPath();
        pointPath.setMarginBottom(0.3f);
        pointPath.setMarginLeft(LEFT_LITTLE);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(6);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);
        videoTitleUse.setAnimationName("AnimationFlyFromLeftEndRight");
        videoTitleUse.setDurationTime(DURATION_MIDDLE);
        addText(videoTitleUse, false);
    }

    public void addStyle_7() {
        clickAddStart();
        ViewTitleOnePart viewTitleOnePart_1 = new ViewTitleOnePart();
        viewTitleOnePart_1.setText(getString(R.string.company_name));
        viewTitleOnePart_1.setTextColor(WHITE);

        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart_1);

        PointPath pointPath = new PointPath();
        pointPath.setCenterHorizontal(true);
        pointPath.setMarginBottom(BOTTOM);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(7);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);
        videoTitleUse.setDurationTime(DURATION_SHORT);
        addText(videoTitleUse, false);
    }

    public void addStyle_8() {
        clickAddStart();
        ViewTitleOnePart viewTitleOnePart_1 = new ViewTitleOnePart();
        viewTitleOnePart_1.setText(getString(R.string.company_name));
        viewTitleOnePart_1.setTextColor(WHITE);
        viewTitleOnePart_1.setFontSize(12);

        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart_1);

        PointPath pointPath = new PointPath();
        pointPath.setCenterVertical(true);
        pointPath.setCenterHorizontal(true);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(8);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);
        videoTitleUse.setDurationTime(DURATION_SHORT);
        addText(videoTitleUse, false);
    }

    public void addStyle_9() {
        clickAddStart();
        ViewTitleOnePart viewTitleOnePart_1 = new ViewTitleOnePart();
        viewTitleOnePart_1.setImgName("default_logo");
        viewTitleOnePart_1.setImgWidth(200);
        viewTitleOnePart_1.setImgHeight(200);
        viewTitleOnePart_1.setType(1);
        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart_1);

        PointPath pointPath = new PointPath();
        pointPath.setMarginLeft(LEFT_LITTLE);
        pointPath.setMarginTop(0.1f);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(5);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);
        videoTitleUse.setDurationTime(DURATION_SHORT);
        addText(videoTitleUse, false);
    }

    public void addStyle_10() {
        clickAddStart();
        ViewTitleOnePart viewTitleOnePart_1 = new ViewTitleOnePart();
        viewTitleOnePart_1.setText(getString(R.string.company_name));
        viewTitleOnePart_1.setTextColor(WHITE);
        viewTitleOnePart_1.setFontSize(12);
        viewTitleOnePart_1.setType(5);
        viewTitleOnePart_1.setSingleLine(true);

        ViewTitleOnePart viewTitleOnePart_2 = new ViewTitleOnePart();
        viewTitleOnePart_2.setText("");
        viewTitleOnePart_2.setFontSize(12);
        viewTitleOnePart_2.setType(5);
        viewTitleOnePart_2.setSingleLine(true);

        ViewTitleOnePart viewTitleOnePart_3 = new ViewTitleOnePart();
        viewTitleOnePart_3.setText(getString(R.string.company_name));
        viewTitleOnePart_3.setTextColor(WHITE);
        viewTitleOnePart_3.setFontSize(12);
        viewTitleOnePart_3.setType(5);
        viewTitleOnePart_3.setSingleLine(true);

        List<ViewTitleOnePart> titleOneParts = new ArrayList<>();
        titleOneParts.add(viewTitleOnePart_1);
        titleOneParts.add(viewTitleOnePart_2);
        titleOneParts.add(viewTitleOnePart_3);

        PointPath pointPath = new PointPath();
        pointPath.setMarginLeft(LEFT_LITTLE);
        pointPath.setCenterVertical(true);

        VideoTitleUse videoTitleUse = new VideoTitleUse();
        videoTitleUse.setType(10);
        videoTitleUse.setPointVer(pointPath);
        videoTitleUse.setPointLandscape(pointPath);
        videoTitleUse.setTitleOnePartList(titleOneParts);
        videoTitleUse.setStartTime(currentVideoTime);
        long[] flagAndTime = getVideoFlagAndTimeByCurrentTime();
        videoTitleUse.setVideoFlag(flagAndTime[0]);
        videoTitleUse.setSoftStartTime(flagAndTime[1]);
        videoTitleUse.setAnimationName("AnimationStyle10");
        videoTitleUse.setDurationTime(DURATION_SHORT);
        addText(videoTitleUse, false);
    }




    // 返回的时间是毫秒
    private String getVideoDuration() {
        long time = 0;
        for (VideoProcessBean bean : activity.getVideoDataLists()) {
            time = time + bean.getDurationTime();
        }
        return time + "";
    }

    private void clickAddStart() {
        if (cgControlCallback != null) {
            cgControlCallback.onClickAddStart();
        }
    }

    public String getCurrentData() {
        String date = PublicUtils.getDateTime();
        String location = "";
        if (!TextUtil.isEmpty(location)) {
            location = location.replace(" ", "");
            return date + " " + location;
        } else {
            return date + " 浙江杭州";
        }
    }

    // 刷新预览的字幕状态，是否显示关闭按钮等
    public void refreshTextView(int progress, boolean isPause) {
//        L.d("refreshTextView:progress=%s,pause=%s,videoTitleData.size()=%s,videoTitleUseData.size()=%s", progress, isPause, videoTitleData.size(), videoTitleUseData.size());
        for (VideoTitleBean videoTitleBean : videoTitleData) {
            videoTitleBean.changeAnimationTime(progress, isPause);
        }
    }

    // 根据当前的播放时间获取当前视频块的标记值和在视频块中的时间位置
    private long[] getVideoFlagAndTimeByCurrentTime() {
        long[] result = new long[2];
        long base = 0;
        for (int i = 0; i < activity.getVideoDataLists().size(); i++) {
            // TODO: 2017/7/5 0005 确定字幕位于哪一个视频块上,用同一个flag标记起来,相同的flag即可认为字幕依附于此视频块
            VideoProcessBean bean = activity.getVideoDataLists().get(i);
            base = bean.getDurationTime() + base;
            if (base >= currentVideoTime) {
                //视频在总时间轴上的开始时间
                long flag = activity.getVideoDataLists().get(i).getFlag();
                long startTime = base - bean.getDurationTime();

                long StartTimeInVideo = (long)((currentVideoTime - startTime)*bean.getSpeed() + bean.getStartTime());
                result[0] = flag;
                result[1] = StartTimeInVideo;
                return result;
            }
        }
        return null;
    }

    // 增加字幕view,--是否用于恢复字幕，恢复字幕的话就不要把字幕加到list的里面去了
    // 片头片尾也不需要加到list中，因为会被保存到草稿
    private void addText(VideoTitleUse videoTitleUse, boolean isRecover) {
        L.d("addText 1");
        VideoTitleBean titleBean = new VideoTitleBean(videoTitleUse);
        titleBean.setVideoTextListener(this);
        videoTitleData.add(titleBean);
        if (!isRecover) {
            videoTitleUseData.add(videoTitleUse);
        }
        if (cgControlCallback != null) {
            cgControlCallback.onClickAddCGOver(titleBean);
        }
    }

    // 恢复本地储存的字幕
    public void recoverText() {
        // 恢复字幕要清空videoTitleData，因为播放界面的变化这个要重新根据videoTitleUseData来生成新的字幕view
        videoTitleData.clear();
        // 字幕和视频片段都有一个flag用于两者之间的绑定，恢复就是找到相等的两个flag
        for (int ii = 0; ii < videoTitleUseData.size(); ii++) {
            VideoTitleUse videoTitleUse = videoTitleUseData.get(ii);
            long textFlag = videoTitleUse.getVideoFlag();
            long baseTime = 0;
            // 要为所有的字幕找到对应的视频片段，然后添加
            for (int i = 0; i < activity.getVideoDataLists().size(); i++) {
                VideoProcessBean bean = activity.getVideoDataLists().get(i);
                long videoFlag = bean.getFlag();
                if (textFlag == videoFlag) {
                    long videoClipTime = bean.getStartTime();
                    long startTimeInVideo = videoTitleUse.getSoftStartTime();
                    long offset = startTimeInVideo - videoClipTime;
                    L.d("recoverOk3");
                    if (offset >= 0) {
                        L.d("recoverOk4");
                        long startTimeInAll = baseTime + (long)(offset/bean.getSpeed());
                        videoTitleUse.setStartTime(startTimeInAll);
                        addText(videoTitleUse, true);
                    }
                    break;
                }
                baseTime = baseTime + bean.getDurationTime();
            }
        }
    }

    @Override
    public void onclickDelete(VideoTitleBean videoBean) {

        videoTitleData.remove(videoBean);
        videoTitleUseData.remove(videoBean.getTitleUse());
        if (cgControlCallback != null) {
            cgControlCallback.onClickDeleteCG(videoBean);
        }
    }

    public void deleteCG(VideoProcessBean videoProcessBean) {

        Iterator<VideoTitleUse> it = videoTitleUseData.iterator();
        while (it.hasNext()) {
            // 删除视频片段的时候根据片段中的标记值，删除所有同标记的字幕bean（字幕属性和view）
            VideoTitleUse videoTitleUse = it.next();
            if (videoTitleUse.getVideoFlag() == videoProcessBean.getFlag()) {
                Iterator<VideoTitleBean> vt = videoTitleData.iterator();
                while (vt.hasNext()) {
                    VideoTitleBean videoTitleBean = vt.next();
                    if (videoTitleBean.getTitleUse().getVideoFlag() == videoTitleUse.getVideoFlag()) {
                        vt.remove();
                    }
                }
                it.remove();
            }
        }
    }

    @Override
    public void onclickTextView(VideoTitleUse videoTitleUse, ViewTitleOnePart videoBean, TextView textView, VideoTitleBean videoTitleBean) {
        if (cgControlCallback != null) {
            cgControlCallback.onClickTextView(videoTitleUse, videoBean, textView, videoTitleBean);
        }
    }
    @Override
    public void onclickImageView(VideoTitleUse videoTitleUse, ViewTitleOnePart videoBean, ImageView imageView, VideoTitleBean videoTitleBean) {
        if (cgControlCallback != null) {
            cgControlCallback.onClickImageView(videoTitleUse, videoBean,imageView, videoTitleBean);
        }
    }
    @Override
    public void onMoving(VideoTitleBean videoBean) {
        if (cgControlCallback != null) {
            cgControlCallback.onMoving(videoBean);
        }
    }

    // 重置字幕的变化保存状态
    public void reset() {
        for (VideoTitleBean videoTitleBean : videoTitleData) {
            videoTitleBean.reset();
        }
    }

    public boolean changeCG(int currentTime) {
        boolean isChangeAll = false;

        // 对字幕进行动画移动
        for (VideoTitleBean videoTitleBean : videoTitleData) {
            boolean isChange = videoTitleBean.changeAnimationTime(currentTime, false);

            if (isChange) {
                isChangeAll = true;
            }
        }

        return isChangeAll;
    }

}
