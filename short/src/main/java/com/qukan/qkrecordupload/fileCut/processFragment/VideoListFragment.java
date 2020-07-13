package com.qukan.qkrecordupload.fileCut.processFragment;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.OutPutMessage;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.TextUtil;
import com.qukan.qkrecordupload.bean.CommonEvent;
import com.qukan.qkrecordupload.bean.TransferEffect;
import com.qukan.qkrecordupload.bean.VideoProcessBean;
import com.qukan.qkrecordupload.compop.ChooseFileCallback;
import com.qukan.qkrecordupload.compop.CommonSelectFile;
import com.qukan.qkrecordupload.custom.ObservableScrollView;
import com.qukan.qkrecordupload.file.VideoListCallback;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;
import com.qukan.qkrecorduploadsdk.RecordSdk;


import org.droidparts.activity.Activity;
import org.droidparts.util.L;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static com.qukan.qkrecordupload.PublicUtils.getVideoDuration;


public class VideoListFragment extends BaseFragment {
    private static final String TAG = "VideoListFragment";

    private NewVideoProcessActivity activity;

    private GestureDetector mGestureDetector;

    private LinearLayout llVideoList;

    private ObservableScrollView hsvVideo;


    private scrollxCallBack scrollxCallBack;

    private boolean isCoverPick = false;
    public void setScrollxCallBack(VideoListFragment.scrollxCallBack scrollxCallBack) {
        this.scrollxCallBack = scrollxCallBack;
    }

    private int BASE_SIZE;

    // 当前选中的item
    @Getter
    public int selectedItem = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NewVideoProcessActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(activity, new DragGestureListener());
        BASE_SIZE = PublicUtils.dip2px(45);
    }

    @Override
    protected void onPostActivityCreated() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.new_fragment_video_list, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        llVideoList = view.findViewById(R.id.ll_video_list);

        hsvVideo = view.findViewById(R.id.hsv_video);
    }


    @Override
    public void onClick(View v) {

    }

    CommonSelectFile commonSelectFile;

    private void initScrollView() {
        hsvVideo.setOnScollChangedListener(new ObservableScrollView.OnScollChangedListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
//                Log.d(TAG, "scorllView " + " scrollX: " + x + " scrollY: " + y + " oldScrollX: " + oldx +" oldScrollY: " + oldy);
                if (scrollxCallBack != null) {
                    if (x < PublicUtils.dip2px(66)) {
                        double p = (x / (double) PublicUtils.dip2px(66));
//                        Log.d(TAG, "scrollxCallBack index: 0 " + "percent: " + p);
                        scrollxCallBack.onScrollx(0, p);
                    } else {
                        int newX = x - PublicUtils.dip2px(66);
                        if (newX > PublicUtils.dip2px(20)) {
                            newX = newX - PublicUtils.dip2px(20);
                            int index = newX / PublicUtils.dip2px(86) + 1;
                            double p = (newX % PublicUtils.dip2px(86)) / (double) PublicUtils.dip2px(66);
//                            Log.d(TAG, "scrollxCallBack index: " + index + " percent: " + p);
                            scrollxCallBack.onScrollx(index, p);
                        } else {
                            scrollxCallBack.onScrollx(1, 0);
                        }

                    }
                }
            }
        });
        hsvVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v == hsvVideo) {
//                    Log.d(TAG, "hsvVideo ontouch");
                    activity.pauseVideo();
                }
                return false;
            }
        });
    }

    public void setCanScroll(boolean scroll) {
        hsvVideo.setEnabled(scroll);
    }

    public interface scrollxCallBack {
        void onScrollx(int index, double percent);
    }

    public void setIndexAndPercent(int index, double percent) {
//        Log.d(TAG, "index: " + index + " percent: " + percent);
        int scrollX = (int) (PublicUtils.dip2px(86) * index + PublicUtils.dip2px(66) * percent);
        setScrollX(scrollX);
    }

    /**
     * 设置滚动的偏移量
     *
     * @param x
     */
    public void setScrollX(int x) {
//        Log.d(TAG, "setScrollX: " + x);
        hsvVideo.setScrollX(x);
    }

    private void initAddButton() {
        int width = PublicUtils.getWindowWidth(getActivity()).widthPixels;
        View view = new View(getActivity());
        llVideoList.addView(view);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (width / 2 - PublicUtils.dip2px(20));
        view.setLayoutParams(params);

        // 添上增加按钮
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addView = layoutInflater.inflate(R.layout.video_item_add, null);
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commonSelectFile == null) {
                    commonSelectFile = new CommonSelectFile(activity, new ChooseFileCallback() {
                        @Override
                        public void chooseFile() {
                            OutPutMessage.instance().getCallBack().onVideosGet(new OutPutMessage.VideosGetCallback() {
                                @Override
                                public void onVideoList(List<VideoProcessBean> list) {
                                    if (list.size() == 0) {
                                        Toast.makeText(activity, getString(R.string.add_video_error), Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    addItem(list, false);
                                    if (videoListCallback != null) {
                                        videoListCallback.onAddVideo(list);
                                    }
                                }
                            },VideoListFragment.this);
                            isCoverPick = false;

                        }
                    });
                }
                commonSelectFile.showAtLocation(activity.getRootView(), Gravity.CENTER, 0, 0);

                activity.pauseVideo();
            }
        });
        llVideoList.addView(addView, 1);

        View rightView = new View(getActivity());
        llVideoList.addView(rightView, 2);

        ViewGroup.LayoutParams params2 = rightView.getLayoutParams();
        params2.width = (width / 2 - PublicUtils.dip2px(91)); // 加号宽度不一样
        rightView.setLayoutParams(params2);
    }

    List<VideoProcessBean> videoDataLists;

    public void init(List<VideoProcessBean> videoDataLists) {
        this.videoDataLists = videoDataLists;
        initScrollView();
        initAddButton();
        addItem(videoDataLists, true);
        setSelected(0);
    }


    public void addCover(){
        OutPutMessage.instance().getCallBack().onVideosGet(new OutPutMessage.VideosGetCallback() {
            @Override
            public void onVideoList(List<VideoProcessBean> list) {
                if (list.size() == 0) {
                    Toast.makeText(activity, getString(R.string.add_video_error), Toast.LENGTH_LONG).show();
                    return;
                }
                addItem(list, false);
                if (videoListCallback != null) {
                    videoListCallback.onAddVideo(list);
                }
            }
        },VideoListFragment.this);
        isCoverPick = true;

    }
    // 设置当前选中的video item
    private void setSelected(int selectedItem) {
        this.selectedItem = selectedItem;
        refresh();
    }

    // 取消所有的选中
    public void unSelected() {
        selectedItem = -1;
        refresh();
    }

    // 当前是否选中了待处理的文件
    public boolean isSelect() {
        if (selectedItem >= 0) {
            return true;
        } else {
            return false;
        }
    }

    // 加入视频片段
    private void addItem(List<VideoProcessBean> lists, boolean isInit) {
        if (lists == null || lists.isEmpty()) {
            return;
        }
        if (!isInit) {
            if(isCoverPick){
                videoDataLists.addAll(0,lists);

            }else{
                videoDataLists.addAll(lists);
            }
        }
        int count = llVideoList.getChildCount();
        for (int i = 0; i < lists.size(); i++) {
            if(isCoverPick){
                addView(lists.get(i), 0 + i + 1);
            }else{
                addView(lists.get(i), count + i - 2);
            }
        }
        refresh();
    }

    public void deleteItem(int selectedItem) {
        llVideoList.removeViewAt(selectedItem + 1);
        refresh();
    }

    // 当前长按的item
    private View mDragView;
    // 当前移动的item在整个llVideoList中的位置
    private int currentDragPosition;

    // 长按item时触发
    private class DragGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            ClipData data = ClipData.newPlainText("", "");
            MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(
                    mDragView);
            mDragView.startDrag(data, shadowBuilder, mDragView, 0);

            for (int i = 0, j = llVideoList.getChildCount(); i < j; i++) {
                if (llVideoList.getChildAt(i) == mDragView) {
                    currentDragPosition = i;
                    L.d("listLocation=%s", currentDragPosition);
                }
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mDragView = v;
            if (mGestureDetector.onTouchEvent(event)) {
                return false;
            }
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        }
    };


    // 长按时item拖动的虚影效果
    private class MyDragShadowBuilder extends View.DragShadowBuilder {

        private final WeakReference<View> mView;

        public MyDragShadowBuilder(View view) {
            super(view);
            mView = new WeakReference<View>(view);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            canvas.scale(1.5F, 1.5F);
            super.onDrawShadow(canvas);
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            final View view = mView.get();
            if (view != null) {
                shadowSize.set((int) (view.getWidth() * 1.5F),
                        (int) (view.getHeight() * 1.5F));
                shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
            } else {

            }
        }
    }

    // 长按后各个阶段时的回调
    private View.OnDragListener mOnDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    L.d("ACTION_DRAG_STARTED:x=%s", event.getX());
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    int pointX = (int) v.getX();
                    DisplayMetrics dm = PublicUtils.getWindowWidth(activity);
                    int windowWidth = dm.widthPixels;
                    int viewWidth = PublicUtils.dip2px(100);
                    L.d("ACTION_DRAG_ENTERED：pointX=%s,windowWidth=%s,viewWidth=%s,getScrollX()=%s", pointX, windowWidth, viewWidth, hsvVideo.getScrollX());
                    if (pointX > (hsvVideo.getScrollX() + windowWidth - viewWidth)) {
                        hsvVideo.smoothScrollBy(100, 0);
                    } else if (pointX < hsvVideo.getScrollX() + viewWidth) {
                        hsvVideo.smoothScrollBy(-100, 0);
                    }
                    v.setAlpha(0.5F);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    L.d("ACTION_DRAG_EXITED:x=%s", event.getX());
                    v.setAlpha(1F);
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
                case DragEvent.ACTION_DROP:
                    L.d("ACTION_DROP:x=%s", event.getX());
                    View view = (View) event.getLocalState();
                    for (int i = 0, j = llVideoList.getChildCount() - 1; i < j; i++) {
                        if (llVideoList.getChildAt(i) == v) {
                            // 当前位置
                            llVideoList.removeView(view);
                            llVideoList.addView(view, i);
                            VideoProcessBean bean = videoDataLists.remove(currentDragPosition - 1);
                            videoDataLists.add(i - 1, bean);
                            // 如果拖动的是选中状态的item则更新选中item的位置
//                            if (currentDragPosition == selectedItem) {
                            selectedItem = i;
//                            }
                            break;
                        }
                    }
                    refresh();
                    if (videoListCallback != null) {
                        videoListCallback.OnVideoMove();
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    L.d("ACTION_DRAG_ENDED:x=%s", event.getX());
                    v.setAlpha(1F);
                default:
                    break;
            }
            return true;
        }
    };
    public static int DEFAULT_TIME = 3000;//图片默认的播放时间

    // 选择视频文件后的回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(getActivityResultGet() != null){
            getActivityResultGet().onActivityResultGet(requestCode,resultCode,data);
        }
    }

    // 检查新加进来的文件是否已经包含在了原来的list里
    private boolean isContain(String path) {
        if (TextUtil.isEmpty(path) || videoDataLists == null) {
            return false;
        }
        for (VideoProcessBean videoProcessBean : videoDataLists) {
            if (videoProcessBean.getPath().equals(path)) {
                return true;
            }
        }
        return false;

    }

    // 刷新一下视频块上面的时间,背景选中块,转场图标等
    public void refresh() {
        for (int i = 0; i < videoDataLists.size(); i++) {
            // 只有一个加号按钮的时候就不需要更新了,还有前后两个空白，一共三个
            if (llVideoList.getChildCount() == 3) {
                return;
            }
            View x = llVideoList.getChildAt(i + 1);
            RelativeLayout relativeLayout = x.findViewById(R.id.rl_list_item);
            TextView tvTime = (TextView) x.findViewById(R.id.tv_time);
            ImageView ivTransfer = x.findViewById(R.id.iv_transfer);
            VideoProcessBean videoProcessBean = videoDataLists.get(i);
            if (i == 0) {
                ivTransfer.setVisibility(View.GONE);
            } else {
                ivTransfer.setVisibility(View.VISIBLE);
                TransferEffect transferEffect = videoProcessBean.getTransferEffect();
                setTransIconByType(ivTransfer, transferEffect.getType());
            }
            String time = PublicUtils.convertTime2(videoProcessBean.getDurationTime());
            tvTime.setText(time);
            if (selectedItem == i) {
//                relativeLayout.setBackgroundResource(R.color.orange);
            } else {
//                relativeLayout.setBackgroundResource(R.color.transparent);
            }
        }
    }


    // 设置转场的小星星图标
    private void setTransIconByType(ImageView imageView, int type) {
        switch (type) {
            case 0:
                imageView.setImageResource(R.drawable.transfer_icon1);
                break;
            case 1:
                imageView.setImageResource(R.drawable.short_ic_transfer_2);
                break;
            case 2:
                imageView.setImageResource(R.drawable.short_ic_transfer_3);
                break;
            case 3:
                imageView.setImageResource(R.drawable.short_ic_transfer_4);
                break;
            case 4:
                imageView.setImageResource(R.drawable.short_ic_transfer_5);
                break;
            case 5:
                imageView.setImageResource(R.drawable.short_ic_transfer_6);
                break;
            case 6:
                imageView.setImageResource(R.drawable.transfer_icon7);
                break;
        }
    }

    @Setter
    VideoListCallback videoListCallback;

    // 增加一个片段到视频列表中
    private void addView(VideoProcessBean processBean, int position) {

        View itemView = activity.getLayoutInflater().inflate(R.layout.video_item, null);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_list_item);
        TextView textView = (TextView) itemView.findViewById(R.id.tv_time);
        ImageView ivTransfer = (ImageView) itemView.findViewById(R.id.iv_transfer);

//        Bitmap thumbnail = getVideoPic(processBean.getPath(), 0);
        ImageLoader.getInstance().displayImage("file://" + processBean.getPath(), imageView);
//        imageView.setImageBitmap(thumbnail);
        String time = PublicUtils.convertTime2(processBean.getDurationTime());
        textView.setText(time);
        itemView.setOnTouchListener(mOnTouchListener);
        itemView.setOnDragListener(mOnDragListener);
        ivTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < llVideoList.getChildCount() - 1; i++) {
                    View x = llVideoList.getChildAt(i);
                    ImageView ivTransfer = (ImageView) x.findViewById(R.id.iv_transfer);
                    if (v == ivTransfer) {
                        if (videoListCallback != null) {
                            videoListCallback.onClickTransIcon(i);
                        }

                    }
                }
            }
        });
        // 视频块块点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < llVideoList.getChildCount() - 1; i++) {
                    View view = llVideoList.getChildAt(i);
                    RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_list_item);
                    if (v == view) {
//                        relativeLayout.setBackgroundResource(R.color.orange);
                        selectedItem = i;
                        if (videoListCallback != null) {
                            videoListCallback.onClickVideo(i);
                        }
                    }
                }
            }
        });

        llVideoList.addView(itemView, position);
    }

    // 原比例获取视频图片
    private Bitmap getVideoPic(String path, float time) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bitmap = mmr.getFrameAtTime((long) (time * 1000) * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        int newWidth;
        int newHeight;
        float scale;
        if (width > height) {
            newHeight = BASE_SIZE;
            scale = (float) BASE_SIZE / height;
            newWidth = (int) (width * scale);
        } else {
            newWidth = BASE_SIZE;
            scale = (float) BASE_SIZE / width;
            newHeight = (int) (height * scale);
        }
        // 如果视频帧的尺寸比basezie还要小的话，就直接返回原视频帧，不过这种情况基本不会有
        if (scale > 1) {
            return bitmap;
        }
        Bitmap newBitmap;
        newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        return newBitmap;
    }

}
