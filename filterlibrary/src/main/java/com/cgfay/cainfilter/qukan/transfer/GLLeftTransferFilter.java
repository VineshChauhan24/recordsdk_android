package com.cgfay.cainfilter.qukan.transfer;

import android.opengl.GLES30;
import android.util.Log;

import com.cgfay.cainfilter.glfilter.base.GLImageFilter;
import com.cgfay.cainfilter.qukan.YuvData;
import com.cgfay.cainfilter.utils.GlUtil;
import com.cgfay.cainfilter.utils.TextureRotationUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class GLLeftTransferFilter  extends GLImageFilter {

    protected static final String GLLeftTransferFilter_SHADER =
            "uniform mat4 uMVPMatrix;                                   \n" +
            "attribute vec4 aPosition;                                  \n" +
            "attribute vec4 aTextureCoord;                              \n" +
            "attribute vec4 aTransTextureCoord;                              \n" +
            "varying vec2 textureCoordinate;                            \n" +
            "void main() {                                              \n" +
            "    gl_Position = uMVPMatrix * aPosition;                  \n" +
            "    textureCoordinate = aTextureCoord.xy;                  \n" +
            "}                                                          \n";

    protected static final String GLLeftTransferFilter_SHADER_OES =
            "precision mediump float;                                   \n" +
            "varying vec2 textureCoordinate;                            \n" +
            "uniform sampler2D inputTexture;                                \n" +
            "uniform sampler2D transTexture;                                \n" +
            //饱和度
             "uniform lowp float offset;\n" +
             "void main() {                                              \n" +
                "if(textureCoordinate.x <= offset){\n" +
                    "highp float marginLeft = 1.0 - offset;\n" +
                    "highp vec2  newTexture   = vec2 (textureCoordinate.x + marginLeft, textureCoordinate.y);\n" +
                    "gl_FragColor = texture2D(inputTexture, newTexture); \n" +
                "}else{\n" +
                    "highp vec2  newTexture   = vec2 (textureCoordinate.x - offset, textureCoordinate.y);\n" +

                    "gl_FragColor = texture2D(transTexture, newTexture); \n" +
                "}\n" +
            "}                                                          \n";



    int mTransTextureLoc;
    int mOffsetLoc;
    private float offset = 1.0f;
    public GLLeftTransferFilter() {
        this(GLLeftTransferFilter_SHADER, GLLeftTransferFilter_SHADER_OES);
    }

    public GLLeftTransferFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        mTransTextureLoc = GLES30.glGetUniformLocation(mProgramHandle,"transTexture");
        //饱和度
        mOffsetLoc = GLES30.glGetUniformLocation(mProgramHandle,"offset");
        setFloat(mOffsetLoc,offset);
    }



    /**
     * 绘制Frame
     * @param textureId 主界面
     * @param currentId 副界面
     * @param count 动画进行到哪一步了
     */
    public boolean drawFrame(int textureId,int currentId,int count)
    {
        offset = (float) (count/10.0);
        if(offset > 1.0){
            offset = 1.0f;
        }
        setFloat(mOffsetLoc,offset);

        GLES30.glUseProgram(mProgramHandle);


        runPendingOnDrawTasks();
        // 绑定数据
        bindValue(textureId,currentId, count);
        onDrawArraysBegin();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, mVertexCount);
        GLES30.glFinish();
        onDrawArraysAfter();
        unBindValue();
        GLES30.glUseProgram(0);
        return true;
    }

    /**
     * 绘制Frame
     * @param textureId 主界面
     * @param currentId 副界面
     * @param count 动画进行到哪一步了
     */
    private void bindValue(int textureId,int currentId,int count ) {

        Log.d("transfer","bindValue");
        float CubeVertices1[] = {
                -1.0f, -1.0f,  // 0 bottom left
                1.0f,  -1.0f,  // 1 bottom right
                -1.0f,  1.0f,  // 2 top left
                1.0f,   1.0f,  // 3 top right
        };

        FloatBuffer Vertices =
                GlUtil.createFloatBuffer(CubeVertices1);



        Vertices.position(0);
        GLES30.glVertexAttribPointer(maPositionLoc, mCoordsPerVertex,
    GLES30.GL_FLOAT, false, 0, Vertices);
        GLES30.glEnableVertexAttribArray(maPositionLoc);



        float TextureVertices_Jx[] = {
                0.0f, 0.0f,     // 0 bottom left
                1.0f, 0.0f,     // 1 bottom right
                0.0f, 1.0f,     // 2 top left
                1.0f, 1.0f      // 3 top right
        };
        FloatBuffer textureVertices = GlUtil.createFloatBuffer(TextureVertices_Jx);
        textureVertices.position(0);
        GLES30.glVertexAttribPointer(maTextureCoordLoc, 2,
    GLES30.GL_FLOAT, false, 0, textureVertices);
        GLES30.glEnableVertexAttribArray(maTextureCoordLoc);


        GLES30.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMVPMatrix, 0);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(getTextureType(), textureId);
        GLES30.glUniform1i(mInputTextureLoc, 0);


        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(getTextureType(), currentId);
        GLES30.glUniform1i(mTransTextureLoc, 1);


//

//
//
//

//        textureVertices.position(0);
//        GLES30.glVertexAttribPointer(maTextureCoordLoc, 2,
//                GLES30.GL_FLOAT, false, 0, textureVertices);
//        GLES30.glEnableVertexAttribArray(maTextureCoordLoc);
//
//        textureVertices.position(0);
//        GLES30.glVertexAttribPointer(maTransTextureCoordLoc, 2,
//                GLES30.GL_FLOAT, false, 0, textureVertices);
//        GLES30.glEnableVertexAttribArray(maTransTextureCoordLoc);
//
//
//
//        GLES30.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMVPMatrix, 0);
//
//
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
//        GLES30.glBindTexture(getTextureType(), textureId);
//        GLES30.glUniform1i(mInputTextureLoc, 0);
//
////        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
////        GLES30.glBindTexture(getTextureType(), currentId);
////        GLES30.glUniform1i(mTransTextureLoc, 1);
    }


}
