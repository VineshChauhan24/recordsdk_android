package com.cgfay.cainfilter.qukan;

import android.media.MediaCodecInfo;
import android.util.Log;

import com.cgfay.cainfilter.camerarender.FilterManager;
import com.cgfay.cainfilter.glfilter.base.GLImageFilter;
import com.cgfay.cainfilter.glfilter.base.GLImageFilterGroup;
import com.cgfay.cainfilter.type.GLFilterIndex;
import com.cgfay.cainfilter.type.GLFilterType;

import java.util.ArrayList;
import java.util.List;

public class GLTransferFilterGroup  extends GLImageFilterGroup {
    // 颜色层
    private static final int yuvIndex = 0;

    //动画组合
    private static final int transferIndex = 1;


    public GLTransferFilterGroup() {
        mFilters = initTransferFilters();
    }

    private GLTransferFilterGroup(List<GLImageFilter> filters) {
        mFilters = filters;
    }

    private List<GLImageFilter> initTransferFilters() {
        List<GLImageFilter> filters = new ArrayList<GLImageFilter>();
        filters.add(yuvIndex, FilterManager.getFilter(GLFilterType.YUV));
        filters.add(transferIndex, FilterManager.getFilter(GLFilterType.NONE));
        return filters;
    }

    @Override
    public void setBeautifyLevel(float percent) {
//        ((GLRealtimeBeautyFilter)mFilters.get(BeautyfyIndex)).setSmoothOpacity(percent);
    }

    @Override
    public void changeFilter(GLFilterType type) {
        GLFilterIndex index = FilterManager.getIndex(type);
        if (index == GLFilterIndex.BeautyIndex) {
            changeBeautyFilter(type);
        } else if (index == GLFilterIndex.YUVIndex) {
//            changeColorFilter(type);
        }
    }

    @Override
    public void changeQKFilter(int filterType) {

    }
    @Override
    public void changeYuvFilter(int format) {
        GLImageFilter filter =  mFilters.get(yuvIndex);
        if(format == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar&& filter.getClass() == GLYUV420Filter.class){
            return;
        }
        if(format == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar&& filter.getClass() == GLYUVFilter.class){
            return;
        }
        filter.release();

        GLImageFilter newFilter = null;
        if(format == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar){
            newFilter = FilterManager.getFilter(GLFilterType.YUV420);
        }else{
            newFilter = FilterManager.getFilter(GLFilterType.YUV);
        }
        mFilters.set(yuvIndex, newFilter);
        // 设置宽高
        mFilters.get(yuvIndex).onInputSizeChanged(mImageWidth, mImageHeight);
        mFilters.get(yuvIndex).onDisplayChanged(mDisplayWidth, mDisplayHeight);
    }
    @Override
    public void changeTransfer(int transferType) {
        if (mFilters != null) {
//            Log.d("changeTransfer:",transferType + "");
            GLImageFilter filter = mFilters.get(transferIndex);
            if (filter.getFilterType() == transferType) {
                return;
            }
            filter.release();
            GLImageFilter newFilter = null;
            //转场动画：0 没有动画  1:左侧划入 2右侧划入 3:淡出 4：闪白 5：叠黑
            switch (transferType){
                case 0:
                    newFilter = FilterManager.getFilter(GLFilterType.NONE);
                    break;
                case 1:
                    newFilter = FilterManager.getFilter(GLFilterType.QKLeft);
                    break;
                case 2:
                    newFilter = FilterManager.getFilter(GLFilterType.QKRight);
                    break;
                case 3:
                    newFilter = FilterManager.getFilter(GLFilterType.QKOut);
                    break;
                case 4:
                    newFilter = FilterManager.getFilter(GLFilterType.QKWhite);
                    break;
                case 5:
                    newFilter = FilterManager.getFilter(GLFilterType.QKBlack);
                    break;
            }
            Log.d("changeTransfer:",transferType + "created");

            newFilter.setFilterType(transferType);
            mFilters.set(transferIndex, newFilter);
            // 设置宽高
            mFilters.get(transferIndex).onInputSizeChanged(mImageWidth, mImageHeight);
            mFilters.get(transferIndex).onDisplayChanged(mDisplayWidth, mDisplayHeight);
        }
    }

    /**
     * 切换美颜滤镜
     *
     * @param type
     */
    private void changeBeautyFilter(GLFilterType type) {
//        if (mFilters != null) {
//            mFilters.get(BeautyfyIndex).release();
//            mFilters.set(BeautyfyIndex, FilterManager.getFilter(type));
//            // 设置宽高
//            mFilters.get(BeautyfyIndex).onInputSizeChanged(mImageWidth, mImageHeight);
//            mFilters.get(BeautyfyIndex).onDisplayChanged(mDisplayWidth, mDisplayHeight);
//        }
    }

    /**
     * 切换颜色滤镜
     * @param type
     */
//    private void changeColorFilter(GLFilterType type) {
//        if (mFilters != null) {
//            mFilters.get(ColorIndex).release();
//            mFilters.set(ColorIndex, FilterManager.getFilter(type));
//            // 设置宽高
//            mFilters.get(ColorIndex).onInputSizeChanged(mImageWidth, mImageHeight);
//            mFilters.get(ColorIndex).onDisplayChanged(mDisplayWidth, mDisplayHeight);
//        }
//    }
//
//    /**
//     * 切换瘦脸大眼滤镜
//     * @param type
//     */
//    private void changeFaceStretchFilter(GLFilterType type) {
//        if (mFilters != null) {
//            mFilters.get(FaceStretchIndex).release();
//            mFilters.set(FaceStretchIndex, FilterManager.getFilter(type));
//            // 设置宽高
//            mFilters.get(FaceStretchIndex).onInputSizeChanged(mImageWidth, mImageHeight);
//            mFilters.get(FaceStretchIndex).onDisplayChanged(mDisplayWidth, mDisplayHeight);
//        }
//    }
//
//    /**
//     * 切换贴纸滤镜
//     * @param type
//     */
//    private void changeStickerFilter(GLFilterType type) {
//        if (mFilters != null) {
//            mFilters.get(StickersIndex).release();
//            mFilters.set(StickersIndex, FilterManager.getFilter(type));
//            // 设置宽高
//            mFilters.get(StickersIndex).onInputSizeChanged(mImageWidth, mImageHeight);
//            mFilters.get(StickersIndex).onDisplayChanged(mDisplayWidth, mDisplayHeight);
//        }
//    }

    /**
     * 切换彩妆滤镜
     *
     * @param type
     */
    private void changeMakeupFilter(GLFilterType type) {
        // Do nothing, 彩妆滤镜放在彩妆滤镜组里面
    }
}