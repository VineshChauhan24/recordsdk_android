package com.cgfay.cainfilter.qukan.record;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;
import android.view.SurfaceHolder;

import com.cgfay.cainfilter.camerarender.FilterManager;
import com.cgfay.cainfilter.glfilter.base.GLImageFilter;
import com.cgfay.cainfilter.glfilter.base.GLImageFilterGroup;
import com.cgfay.cainfilter.qukan.GLTransferFilterGroup;
import com.cgfay.cainfilter.qukan.GLVideoDefaultFilterGroup;
import com.cgfay.cainfilter.qukan.RenderVideo;
import com.cgfay.cainfilter.qukan.RenderVideoHandler;
import com.cgfay.cainfilter.qukan.YuvData;
import com.cgfay.cainfilter.type.GLFilterGroupType;
import com.cgfay.cainfilter.type.GLFilterType;
import com.cgfay.cainfilter.type.ScaleType;
import com.cgfay.cainfilter.utils.TextureRotationUtils;

public class CompleteRenderVideoManager {

    private static final String TAG = "CompleteManager";


    private Object mSyncObject = new Object();



    // 实时滤镜组
    private GLImageFilterGroup mRealTimeFilter;
    // 实时滤镜组
    private GLImageFilterGroup mTransferFilter;

    // 显示输出
    private GLImageFilter mDisplayFilter;

    // 当前的TextureId
    private int mCurrentTextureId;

    // 输入流大小
    private int mTextureWidth;
    private int mTextureHeight;
    // 显示大小
    public int mDisplayWidth;
    public int mDisplayHeight;

    private ScaleType mScaleType = ScaleType.CENTER_CROP;



    public CompleteRenderVideoManager() {

    }



    /**
     * 初始化
     */
    public void init() {
        // 释放之前的滤镜
        releaseFilters();
        // 初始化滤镜
        initFilters();
    }


    /**
     * 初始化滤镜
     */
    private void initFilters() {
        // 相机输入流
//        glyuvFilter = new GLYUVFilter();
//        gl420Filter = new GLI420Filter();
        // 渲染滤镜组
        mRealTimeFilter = new GLVideoDefaultFilterGroup(true);
        mTransferFilter = new GLTransferFilterGroup();
        // 显示输出
//        mDisplayFilter = FilterManager.getFilter(GLFilterType.NONE);
      }

    /**
     * 调整由于surface的大小与SurfaceView大小不一致带来的显示问题
     */
    public void adjustViewSize() {
        float[] textureCoords = null;
        float[] vertexCoords = null;
        float[] textureVertices = TextureRotationUtils.getTextureVertices();
        float[] vertexVertices = TextureRotationUtils.CubeVertices;
        float ratioMax = Math.max((float) mDisplayWidth / mTextureWidth,
                (float) mDisplayHeight / mTextureHeight);
        // 新的宽高
        int imageWidth = Math.round(mTextureWidth * ratioMax);
        int imageHeight = Math.round(mTextureHeight * ratioMax);
        // 获取视图跟texture的宽高比
        float ratioWidth = (float) imageWidth / (float) mDisplayWidth;
        float ratioHeight = (float) imageHeight / (float) mDisplayHeight;
        if (mScaleType == ScaleType.CENTER_INSIDE) {
            vertexCoords = new float[] {
                    vertexVertices[0] / ratioHeight, vertexVertices[1] / ratioWidth, vertexVertices[2],
                    vertexVertices[3] / ratioHeight, vertexVertices[4] / ratioWidth, vertexVertices[5],
                    vertexVertices[6] / ratioHeight, vertexVertices[7] / ratioWidth, vertexVertices[8],
                    vertexVertices[9] / ratioHeight, vertexVertices[10] / ratioWidth, vertexVertices[11],
            };
        } else if (mScaleType == ScaleType.CENTER_CROP) {
            float distHorizontal = (1 - 1 / ratioHeight) / 2;
            float distVertical = (1 - 1 / ratioWidth) / 2;
            textureCoords = new float[] {
                    addDistance(textureVertices[0], distVertical), addDistance(textureVertices[1], distHorizontal),
                    addDistance(textureVertices[2], distVertical), addDistance(textureVertices[3], distHorizontal),
                    addDistance(textureVertices[4], distVertical), addDistance(textureVertices[5], distHorizontal),
                    addDistance(textureVertices[6], distVertical), addDistance(textureVertices[7], distHorizontal),
            };
        }
        if (vertexCoords == null) {
            vertexCoords = vertexVertices;
        }
        if (textureCoords == null) {
            textureCoords = textureVertices;
        }
    }

    /**
     * 计算距离
     * @param coordinate
     * @param distance
     * @return
     */
    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    /**
     * 释放资源
     */
    public void release() {
        releaseFilters();
    }

    /**
     * 释放Filters资源
     */
    private void releaseFilters() {
        if (mRealTimeFilter != null) {
            mRealTimeFilter.release();
            mRealTimeFilter = null;
        }
        if(mTransferFilter != null){
            mTransferFilter.release();;
            mTransferFilter = null;
        }
        if(mDisplayFilter != null) {
            mDisplayFilter.release();
            mDisplayFilter = null;
        }
        mTextureWidth = 0;
        mTextureHeight = 0;
    }




    /**
     * 更新TextureBuffer
     */
    public void updateTextureBuffer() {
        //22222222

    }

    /**
     * 渲染Texture的大小
     * @param width
     * @param height
     */
    public void onInputSizeChanged(int width, int height) {
        Log.d(TAG,"onInputSizeChanged");
        //44444

        if(mRealTimeFilter != null &&mTransferFilter != null){
            //44444
            mTextureWidth = width;
            mTextureHeight = height;
        }
        if (mRealTimeFilter != null) {
            mRealTimeFilter.onInputSizeChanged(width, height);
        }
        if (mTransferFilter != null) {
            mTransferFilter.onInputSizeChanged(width, height);
        }
        if (mDisplayFilter != null) {
            mDisplayFilter.onInputSizeChanged(width, height);
        }
    }

    /**
     * Surface显示的大小
     * @param width
     * @param height
     */
    public void onDisplaySizeChanged(int width, int height) {
        //33333333
        mDisplayWidth = width;
        mDisplayHeight = height;

        if (mRealTimeFilter != null) {
            mRealTimeFilter.onDisplayChanged(mDisplayWidth, mDisplayHeight);
        }
        if (mTransferFilter != null) {
            mTransferFilter.onDisplayChanged(mDisplayWidth, mDisplayHeight);
        }
        if (mDisplayFilter != null) {
            mDisplayFilter.onDisplayChanged(mDisplayWidth, mDisplayHeight);
        }
    }

    /**
     * 设置美颜等级（百分比）
     * @param percent 0 ~ 100
     */
    public void setBeautifyLevel(int percent) {
        if (mRealTimeFilter != null) {
            mRealTimeFilter.setBeautifyLevel(percent / 100.0f);
        }
    }

    /**
     * 更新filter
     * @param type Filter类型
     */
    public void changeFilter(GLFilterType type) {
        if (mRealTimeFilter != null) {
            mRealTimeFilter.changeFilter(type);
        }
    }

    /**
     * 切换滤镜组
     * @param type
     */
    public void changeFilterGroup(GLFilterGroupType type) {
        synchronized (mSyncObject) {
            if (mRealTimeFilter != null) {
                mRealTimeFilter.release();
            }
            mRealTimeFilter = FilterManager.getFilterGroup(type);
            mRealTimeFilter.onInputSizeChanged(mTextureWidth, mTextureHeight);
            mRealTimeFilter.onDisplayChanged(mDisplayWidth, mDisplayHeight);
        }
    }

    /**
     * 绘制渲染
     * @param
     * @param
     */
    public void drawFrame(YuvData data) {

        // 如果存在滤镜，则绘制滤镜
        if (mRealTimeFilter != null) {
            mRealTimeFilter.changeYuvFilter(data.getColorFormat());

            mRealTimeFilter.changeQKFilter(data.getFilterType());
            mCurrentTextureId = mRealTimeFilter.drawFrameBuffer(data);
        }
        YuvData transfer = data.getTransfer();
        if(transfer != null){
//            Log.d(TAG,""+transfer.getTransFerType());
            mTransferFilter.changeYuvFilter(data.getColorFormat());
            mTransferFilter.changeTransfer(transfer.getTransFerType());
            mCurrentTextureId = mTransferFilter.drawFrameBuffer(transfer,mCurrentTextureId);
        }

        // 显示输出，需要调整视口大小
        if (mDisplayFilter != null) {
            GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
            // 显示输出
            mDisplayFilter.drawFrame(mCurrentTextureId);
        }
    }

    /**
     * 获取当前渲染的Texture
     * @return
     */
    public int getCurrentTexture() {
        return mCurrentTextureId;
    }

    public int getmTextureWidth() {
        return mTextureWidth;
    }

    public void setmTextureWidth(int mTextureWidth) {
        this.mTextureWidth = mTextureWidth;
    }

    public int getmTextureHeight() {
        return mTextureHeight;
    }

    public void setmTextureHeight(int mTextureHeight) {
        this.mTextureHeight = mTextureHeight;
    }
}
