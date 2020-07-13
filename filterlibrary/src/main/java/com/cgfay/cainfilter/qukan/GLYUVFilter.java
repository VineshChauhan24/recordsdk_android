package com.cgfay.cainfilter.qukan;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import com.cgfay.cainfilter.glfilter.base.GLImageFilter;
import com.cgfay.cainfilter.qukan.YuvData;
import com.cgfay.cainfilter.utils.GlUtil;
import com.cgfay.cainfilter.utils.TextureRotationUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
//显示 Y UV 类型的数据
public class GLYUVFilter  extends GLImageFilter {


    // fragment shader (NV12 to ARGB conversion)
    private static final String FRAGMENT_SHADER_OES =

            "#extension GL_OES_EGL_image_external : require         \n" +
                    "precision mediump float;  \n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "uniform sampler2D inputTextureY;\n" +
                    "uniform sampler2D inputTextureUV;\n" +

                    "void main() {\n" +
                    "  mediump vec3 yuv;\n" +
                    "  lowp vec3 rgb;\n" +
                    " yuv.x = texture2D(inputTextureY, textureCoordinate).r;\n" +
                    " yuv.yz = texture2D(inputTextureUV, textureCoordinate).ra - 0.5;\n" +
                    " rgb = mat3(1.0,          1.0,      1.0,\n" +
                    "        0.0,     -0.39465,  2.03211,\n" +
                    "        1.13983, -0.58060,      0.0) * yuv;\n" +
                    " gl_FragColor = vec4(rgb, 1.0);\n" +
//                    " gl_FragColor = texture2D(inputTextureUV, textureCoordinate); \n" +

                    "}\n";


    private FloatBuffer textureVertices = GlUtil.createFloatBuffer(TextureRotationUtils.TextureVertices_180Jx);


    private int _yhandle = -1, _uvhandle = -1;
    private ByteBuffer y = null;
    private ByteBuffer uv = null;
//    private int _ytid = -1, _uvtid = -1;
    private int[] textureId_yuv = null;

    public GLYUVFilter() {
        this(VERTEX_SHADER, FRAGMENT_SHADER_OES);
    }

    public GLYUVFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        if(textureId_yuv == null){
            textureId_yuv = new int[2];
            //创建3个纹理
            GLES30.glGenTextures(2, textureId_yuv, 0);

            //绑定纹理
            for (int id : textureId_yuv) {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id);
                //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            }
        }

//        mInputTextureLoc = GLES30.glGetUniformLocation(mProgramHandle, "inputTexture");
        _yhandle = GLES30.glGetUniformLocation(mProgramHandle, "inputTextureY");
        _uvhandle = GLES30.glGetUniformLocation(mProgramHandle, "inputTextureUV");
        Log.d("GLYUVFilter:","create");
    }

    @Override
    public void onInputSizeChanged(int width, int height) {
        super.onInputSizeChanged(width, height);
    }



    /**
     * 绑定数据
     * @param
     * @param vertexBuffer
     * @param textureBuffer
     */
    public void bindValue(YuvData data,FloatBuffer vertexBuffer,
                             FloatBuffer textureBuffer) {
        int deoodeWidth = data.getDecodeWidth();
        int deoodeHeight = data.getDecodeHeight();
        int verWidth = deoodeWidth;
        int verHeight = deoodeHeight;
        float scaleX = 1;
        float scaleY = 1;


        if(deoodeWidth != data.getWidth() || deoodeHeight != data.getHeight()){
            if(deoodeWidth < data.getWidth()){
                scaleX = (float)(deoodeWidth*1.0/ data.getWidth() - 0.001);
            }
            if(deoodeHeight  < data.getHeight()){
                scaleY = (float)(deoodeHeight*1.0/ data.getHeight() - 0.001);
            }
        }

        if(data.getOrientation() == 0){
            float TextureVertices_Jx[] = {
                    0.0f, scaleY,
                    scaleX, scaleY,
                    0.0f, 0.0f,
                    scaleX, 0.0f,
            };
            textureVertices = GlUtil.createFloatBuffer(TextureVertices_Jx);
        }else if(data.getOrientation() == 1){
            float TextureVertices_Jx[] = {
                    scaleX, scaleY,
                    scaleX, 0.0f,
                    0.0f, scaleY,
                    0.0f, 0.0f,
            };
            textureVertices = GlUtil.createFloatBuffer(TextureVertices_Jx);
            verWidth = deoodeHeight;
            verHeight = deoodeWidth;

        }else if(data.getOrientation() == 3){
            float TextureVertices_Jx[] = {
                    0.0f, 0.0f,
                    0.0f, scaleY,
                    scaleX, 0.0f,
                    scaleX, scaleY,
            };
            textureVertices = GlUtil.createFloatBuffer(TextureVertices_Jx);
            verWidth = deoodeHeight;
            verHeight = deoodeWidth;
        }else{
            float TextureVertices_Jx[] = {
                    scaleX, 0.0f,
                    0.0f, 0.0f,
                    scaleX, scaleY,
                    0.0f, scaleY,
            };
            textureVertices = GlUtil.createFloatBuffer(TextureVertices_Jx);
        }

//        float TextureVertices_Jx[] = {
//                scaleX, scaleY,
//                scaleX, 0.5f,
//                0.0f, scaleY,
//                0.0f, 0.5f,
//        };
//        textureVertices = GlUtil.createFloatBuffer(TextureVertices_Jx);
//
//
        float verx = 1.0f;
        float very= 1.0f;
        float displayXy = (float)(mDisplayHeight*1.0/mDisplayWidth);
        float verXy = (float)(verHeight*1.0/verWidth);
        //给个界面波动区间
        if(displayXy > verXy +0.03 ||displayXy < verXy - 0.03){
            if(displayXy > verXy){
                very = verXy/displayXy;
            }else{
                verx = displayXy/verXy;
            }
        }
        float CubeVertices1[] = {
                -verx, -very,  // 0 bottom left
                verx,  -very,  // 1 bottom right
                -verx,  very,  // 2 top left
                verx,   very,  // 3 top right
        };

        FloatBuffer FULL_RECTANGLE_BUF1 =
                GlUtil.createFloatBuffer(CubeVertices1);
//        Log.d("GLYUVFilter","mDisplayWidth:" + mDisplayWidth + "  mDisplayHeight:" + mDisplayHeight);


//        Log.d("bindValue","deoodeWidth:" + deoodeWidth + "deoodeHeight:" + deoodeHeight );
//        Log.d("bindValue","data.getWidth():" + data.getWidth()  + "data.getHeight():"+data.getHeight() + "scaleX:" + scaleX +"scaleY:" + scaleY);
        FULL_RECTANGLE_BUF1.position(0);
        GLES30.glVertexAttribPointer(maPositionLoc, mCoordsPerVertex,
                GLES30.GL_FLOAT, false, 0, FULL_RECTANGLE_BUF1);
        GLES30.glEnableVertexAttribArray(maPositionLoc);

        textureVertices.position(0);
        GLES30.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES30.GL_FLOAT, false, 0, textureVertices);
        GLES30.glEnableVertexAttribArray(maTextureCoordLoc);

        GLES30.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMVPMatrix, 0);


        int yarraySize = (int)(data.getWidth()*data.getHeight());
        int uvarraySize = yarraySize/2;

       if((y == null || uv == null)||y.capacity() != yarraySize){
            // 初始化容器
            synchronized (this) {
                y = ByteBuffer.allocate(yarraySize);
                uv = ByteBuffer.allocate(uvarraySize);
            }
        }

        y.clear();
        uv.clear();
        y.position(0);
        uv.position(0);
        byte[] yuv = data.getYuvBuf();
        y.put(yuv, 0, yarraySize);
        uv.put(yuv, yarraySize, uvarraySize);
        y.position(0);
        uv.position(0);


        //激活纹理0来绑定y数据
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId_yuv[0]);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE, data.getWidth(), data.getHeight(), 0,
                GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, y);

        //激活纹理1来绑定u数据
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId_yuv[1]);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE_ALPHA, data.getWidth() / 2, data.getHeight() / 2, 0,
                GLES30.GL_LUMINANCE_ALPHA, GLES30.GL_UNSIGNED_BYTE, uv);

        
        //给fragment_shader里面yuv变量设置值   0 1 2 标识纹理x
        GLES30.glUniform1i(_yhandle, 0);
        GLES30.glUniform1i(_uvhandle, 1);
//        GLES30.glUniform1i(sampler_v, 2);


//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
//        GLES30.glBindTexture(getTextureType(), textureId);
//        GLES30.glUniform1i(mInputTextureLoc, 0);
        
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _ytid);
//        GLES30.glUniform1i(_yhandle, 0);
//
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _uvtid);
//        GLES30.glUniform1i(_uvhandle, 1);
    }

    /**
     * 绘制Frame
     * @param
     */
    public boolean drawFrame(YuvData data) {

        return drawFrame(data,mVertexArray, textureVertices);
    }

    /**
     * 绘制Frame
     * @param
     * @param vertexBuffer
     * @param textureBuffer
     */
    public boolean drawFrame(YuvData data,FloatBuffer vertexBuffer,
                             FloatBuffer textureBuffer) {

        GLES30.glUseProgram(mProgramHandle);


        runPendingOnDrawTasks();
        // 绑定数据
        bindValue(data,vertexBuffer, textureBuffer);
        onDrawArraysBegin();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, mVertexCount);
        GLES30.glFinish();
        onDrawArraysAfter();
        unBindValue();
        GLES30.glUseProgram(0);
        return true;
    }


    @Override
    public void release(){
        GLES20.glDeleteTextures(2, textureId_yuv, 0);
        super.release();
    }
}
