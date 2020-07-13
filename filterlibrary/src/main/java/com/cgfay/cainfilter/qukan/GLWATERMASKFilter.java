package com.cgfay.cainfilter.qukan;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.Environment;

import com.cgfay.cainfilter.glfilter.base.GLImageFilter;
import com.cgfay.cainfilter.utils.GlUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

public class GLWATERMASKFilter  extends GLImageFilter {
    protected static final String GLWATERMASKFilter_SHADER =
            "uniform mat4 uMVPMatrix;                                   \n" +
                    "attribute vec4 aPosition;                                  \n" +
                    "attribute vec4 aTextureCoord;                              \n" +
                    "attribute vec4 aTransTextureCoord;                              \n" +
                    "attribute vec4 waterCoord;                              \n" +
                    "varying vec2 textureCoordinate;                            \n" +
                    "varying vec2 waterCoordinate;                            \n" +
                    "void main() {                                              \n" +
                    "    gl_Position = uMVPMatrix * aPosition;                  \n" +
                    "    textureCoordinate = aTextureCoord.xy;                  \n" +
                    "    waterCoordinate = waterCoord.xy;                  \n" +
                    "}                                                          \n";

    protected static final String GLWATERMASKFilter_SHADER_OES =
            "precision mediump float;                                   \n" +
                    "varying vec2 textureCoordinate;                            \n" +
                    "varying vec2 waterCoordinate;                            \n" +
                    "uniform sampler2D inputTexture;                                \n" +
                    "uniform sampler2D transTexture;                                \n" +
                                       "void main() {                                              \n" +
                    "lowp vec4 otherColor = texture2D(inputTexture, textureCoordinate);\n" +
                    "lowp vec4 transColor = texture2D(transTexture, waterCoordinate);\n" +
                    "highp float marginLeft = 1.0 - transColor.a;\n" +
                    "gl_FragColor = vec4(transColor.rgb*transColor.a + otherColor.rgb*marginLeft, 1.0);\n" +

                    "}                                                          \n";

//             "lowp vec4 input = texture2D(inputTexture, textureCoordinate);\n" +
//                     "lowp vec4 trans = texture2D(transTexture, textureCoordinate);\n" +
//                     "gl_FragColor = vec4(input.rgb*offset + trans.rgb*(1.0 -offset), 1.0);\n" +

    int mTransTextureLoc;
    int mWaterCoordLoc;
    public GLWATERMASKFilter() {
        this(GLWATERMASKFilter_SHADER, GLWATERMASKFilter_SHADER_OES);
    }

    public GLWATERMASKFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        mTransTextureLoc = GLES30.glGetUniformLocation(mProgramHandle,"transTexture");
        mWaterCoordLoc = GLES30.glGetAttribLocation(mProgramHandle, "waterCoord");
    }



    /**
     * 绘制Frame
     * @param textureId 主界面
     * @param bitmap 副界面
     */
    public boolean drawFrame(int textureId,Bitmap bitmap)
    {

        if(bitmap == null){
            return false;
        }

        int waterId = GlUtil.createTexture(bitmap);

        GLES30.glUseProgram(mProgramHandle);


        runPendingOnDrawTasks();
        // 绑定数据
        bindValue(textureId,waterId);
        onDrawArraysBegin();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, mVertexCount);
        GLES30.glFinish();
        onDrawArraysAfter();
        unBindValue();
        GLES30.glUseProgram(0);
        int[] waterIds = {waterId};
        GLES20.glDeleteTextures(1, waterIds, 0);

        return true;
    }


    private static final String SD_PATH = "/sdcard/dskqxt/pic/";
    private static final String IN_PATH = "/dskqxt/pic/";

    /**
     * 随机生产文件名
     *
     * @return
     */
    static int i = 0;
    private static String generateFileName() {
        i++;
        return i+"";
    }
    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    /**
     * 绘制Frame
     * @param textureId 主界面
     * @param waterId 副界面
     */
    private void bindValue(int textureId,int waterId) {

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




        float water_Jx[] = {
                0.0f, 1.0f,     // 2 top left
                1.0f, 1.0f,      // 3 top right
                0.0f, 0.0f,     // 0 bottom left
                1.0f, 0.0f     // 1 bottom right

        };
        FloatBuffer waterVertices = GlUtil.createFloatBuffer(water_Jx);
        waterVertices.position(0);
        GLES30.glVertexAttribPointer(mWaterCoordLoc, 2,
                GLES30.GL_FLOAT, false, 0, waterVertices);
        GLES30.glEnableVertexAttribArray(mWaterCoordLoc);





        GLES30.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMVPMatrix, 0);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(getTextureType(), textureId);
        GLES30.glUniform1i(mInputTextureLoc, 0);


        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(getTextureType(), waterId);
        GLES30.glUniform1i(mTransTextureLoc, 1);

    }

}