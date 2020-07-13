package com.cgfay.cainfilter.qukan;

import android.media.MediaCodecInfo;
import android.util.Log;

import com.cgfay.cainfilter.camerarender.FilterManager;
import com.cgfay.cainfilter.glfilter.base.GLImageFilter;
import com.cgfay.cainfilter.glfilter.base.GLImageFilterGroup;
import com.cgfay.cainfilter.glfilter.beauty.GLRealtimeBeautyFilter;
import com.cgfay.cainfilter.type.GLFilterIndex;
import com.cgfay.cainfilter.type.GLFilterType;

import java.util.ArrayList;
import java.util.List;

public class GLVideoDefaultFilterGroup  extends GLImageFilterGroup {
    // 颜色层
    private static final int yuvIndex = 0;

    // 曝光亮度对比度等集合
    private static final int groupIndex = 1;
    //滤镜组filter
    private static final int filterIndex = 2;
    //图片结合
    private static final int imgIndex = 3;

    public GLVideoDefaultFilterGroup() {
        mFilters = initVideoDefaultFilters(false);
    }
    public GLVideoDefaultFilterGroup(boolean addImage) {
        mFilters = initVideoDefaultFilters(addImage);
    }

    private GLVideoDefaultFilterGroup(List<GLImageFilter> filters) {
        mFilters = filters;
    }

    private List<GLImageFilter> initVideoDefaultFilters(boolean addImage) {
        List<GLImageFilter> filters = new ArrayList<GLImageFilter>();
        filters.add(yuvIndex, FilterManager.getFilter(GLFilterType.YUV));
        filters.add(groupIndex, FilterManager.getFilter(GLFilterType.Group));
        filters.add(filterIndex, FilterManager.getFilter(GLFilterType.NONE));
        if(addImage){
            filters.add(imgIndex, FilterManager.getFilter(GLFilterType.QKWATERMASK));
        }
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
        if (mFilters != null) {
            GLImageFilter filter =  mFilters.get(filterIndex);
            if(filter.getFilterType() == filterType){
                return;
            }
            filter.release();
            GLImageFilter newFilter = null;
            //0 没有滤镜  1怀旧 2、素描 3清晰 4、恋橙 5、稚颜 6、青柠 7、film 8：微风 9：萤火虫
            switch (filterType){
                case 0:
                    newFilter = FilterManager.getFilter(GLFilterType.NONE);
                    break;
                case 1:
                    newFilter = FilterManager.getFilter(GLFilterType.GLSepia);
                    break;
                case 2:
                    newFilter = FilterManager.getFilter(GLFilterType.SKETCH);
                    break;
                case 3:
                {
                    GLRGBBressFilter glrgbBressFilter = (GLRGBBressFilter)FilterManager.getFilter(GLFilterType.RGBBress);
                    glrgbBressFilter.setRed(1.41f);
                    glrgbBressFilter.setGreen(1.41f);
                    glrgbBressFilter.setBlue(1.96f);
                    glrgbBressFilter.setSaturation(1.25f);
                    glrgbBressFilter.setBrightness(0.28f);
                    glrgbBressFilter.setContrast(1.55f);
                    newFilter = glrgbBressFilter;
                }

                    break;
                case 4:
                {
                    GLRGBBressFilter glrgbBressFilter = (GLRGBBressFilter)FilterManager.getFilter(GLFilterType.RGBBress);
                    glrgbBressFilter.setRed(1.18f);
                    glrgbBressFilter.setGreen(1.31f);
                    glrgbBressFilter.setBlue(1.29f);
                    glrgbBressFilter.setSaturation(0.98f);
                    glrgbBressFilter.setBrightness(0.07f);
                    glrgbBressFilter.setContrast(0.63f);
                    newFilter = glrgbBressFilter;
                }
                    break;
                case 5:
                {
                    GLRGBBressFilter glrgbBressFilter = (GLRGBBressFilter)FilterManager.getFilter(GLFilterType.RGBBress);
                    glrgbBressFilter.setRed(1.06f);
                    glrgbBressFilter.setGreen(1.31f);
                    glrgbBressFilter.setBlue(1.29f);
                    glrgbBressFilter.setSaturation(1.84f);
                    glrgbBressFilter.setBrightness(0.07f);
                    glrgbBressFilter.setContrast(0.92f);
                    newFilter = glrgbBressFilter;
                }
                break;
                case 6:
                {
                    GLRGBBressFilter glrgbBressFilter = (GLRGBBressFilter)FilterManager.getFilter(GLFilterType.RGBBress);
                    glrgbBressFilter.setRed(1.20f);
                    glrgbBressFilter.setGreen(1.18f);
                    glrgbBressFilter.setBlue(2.06f);
                    glrgbBressFilter.setSaturation(0.00f);
                    glrgbBressFilter.setBrightness(0.11f);
                    glrgbBressFilter.setContrast(0.67f);
                    newFilter = glrgbBressFilter;
                }
                break;
                case 7:
                {
                    GLRGBBressFilter glrgbBressFilter = (GLRGBBressFilter)FilterManager.getFilter(GLFilterType.RGBBress);
                    glrgbBressFilter.setRed(1.25f);
                    glrgbBressFilter.setGreen(0.82f);
                    glrgbBressFilter.setBlue(0.88f);
                    glrgbBressFilter.setSaturation(0.63f);
                    glrgbBressFilter.setBrightness(0.07f);
                    glrgbBressFilter.setContrast(0.75f);
                    newFilter = glrgbBressFilter;
                }
                break;
                case 8:
                {
                    GLRGBBressFilter glrgbBressFilter = (GLRGBBressFilter)FilterManager.getFilter(GLFilterType.RGBBress);
                    glrgbBressFilter.setRed(1.25f);
                    glrgbBressFilter.setGreen(0.82f);
                    glrgbBressFilter.setBlue(1.33f);
                    glrgbBressFilter.setSaturation(1.36f);
                    glrgbBressFilter.setBrightness(0.28f);
                    glrgbBressFilter.setContrast(0.96f);
                    newFilter = glrgbBressFilter;
                }
                break;
                case 9:
                {
                    GLRGBBressFilter glrgbBressFilter = (GLRGBBressFilter)FilterManager.getFilter(GLFilterType.RGBBress);
                    glrgbBressFilter.setRed(1.25f);
                    glrgbBressFilter.setGreen(0.82f);
                    glrgbBressFilter.setBlue(1.35f);
                    glrgbBressFilter.setSaturation(1.17f);
                    glrgbBressFilter.setBrightness(-0.03f);
                    glrgbBressFilter.setContrast(1.63f);
                    newFilter = glrgbBressFilter;
                }
                break;
            }
            newFilter.setFilterType(filterType);
            mFilters.set(filterIndex, newFilter);
            // 设置宽高
            mFilters.get(filterIndex).onInputSizeChanged(mImageWidth, mImageHeight);
            mFilters.get(filterIndex).onDisplayChanged(mDisplayWidth, mDisplayHeight);
        }
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

    }

    /**
     * 切换美颜滤镜
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
     * @param type
     */
    private void changeMakeupFilter(GLFilterType type) {
        // Do nothing, 彩妆滤镜放在彩妆滤镜组里面
    }
}
