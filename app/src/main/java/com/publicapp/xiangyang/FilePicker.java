package com.publicapp.xiangyang;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;


/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class FilePicker {
    public static final int FROM_ALBUM = 20001;
    public static final int TAKE_PICTURE = 20005;

    int selectionMode = PictureConfig.MULTIPLE;
    int mimeType = PictureMimeType.ofAll();
    boolean isCrop = false;
    Activity activity;

    Fragment fragment;

    public FilePicker(Activity activity) {
        this.activity = activity;
    }

    public FilePicker(Fragment fragment) {
        this.fragment = fragment;
    }

    public void start() {
        PictureSelector pictureSelector;
        if (activity != null) {
            pictureSelector = PictureSelector.create(activity);
        } else {
            pictureSelector = PictureSelector.create(fragment);
        }
        pictureSelector.openGallery(mimeType)//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(10)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(selectionMode)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(false)// 是否可预览视频 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .isCamera(false)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .enableCrop(isCrop)// 是否裁剪 true or false
                .circleDimmedLayer(true)
                .hideBottomControls(true)
                .rotateEnabled(true) // 裁剪是否可旋转图片
                .compress(false)// 是否压缩 true or false
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .forResult(FROM_ALBUM);//结果回调onActivityResult code
    }

    // 开启拍照
    public void startPicture() {
        PictureSelector pictureSelector;
        if (activity != null) {
            pictureSelector = PictureSelector.create(activity);
        } else {
            pictureSelector = PictureSelector.create(fragment);
        }

        pictureSelector
                .openCamera(PictureMimeType.ofImage())
                .enableCrop(isCrop)// 是否裁剪 true or false
                .circleDimmedLayer(isCrop)
                .rotateEnabled(true) // 裁剪是否可旋转图片
                .forResult(TAKE_PICTURE);
    }

    public static int getFromAlbum() {
        return FROM_ALBUM;
    }

    public static int getTakePicture() {
        return TAKE_PICTURE;
    }

    public int getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(int selectionMode) {
        this.selectionMode = selectionMode;
    }

    public int getMimeType() {
        return mimeType;
    }

    public void setMimeType(int mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isCrop() {
        return isCrop;
    }

    public void setCrop(boolean crop) {
        isCrop = crop;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
