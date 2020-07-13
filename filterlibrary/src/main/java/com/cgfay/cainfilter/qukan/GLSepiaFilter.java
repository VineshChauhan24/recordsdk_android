package com.cgfay.cainfilter.qukan;

import android.opengl.GLES30;
import android.util.Log;

import com.cgfay.cainfilter.glfilter.base.GLImageFilter;

public class GLSepiaFilter  extends GLImageFilter {

    private static final String GLSepiaFilter_SHADER_2D =
            "precision mediump float;\n" +
                    "\n" +
                    "varying mediump vec2 textureCoordinate;\n" +
                    "uniform sampler2D inputTexture;\n" +
                    "uniform lowp mat4 colorMatrix;\n" +
                    "uniform lowp float intensity;\n" +

                    "void main(){\n" +
                        "lowp vec4 textureColor = texture2D(inputTexture, textureCoordinate);\n" +
                        "lowp vec4 outputColor = textureColor * colorMatrix;\n" +

                        "gl_FragColor = (intensity * outputColor) + ((1.0 - intensity) * textureColor);\n" +
                    "}";




    //亮度
    private int colorMatrixUniform;
    //对比度
    private int intensityUniform;

    private float[] colorMatrix = {0.3588f, 0.7044f, 0.1368f, 0.0f,0.2990f, 0.5870f, 0.1140f, 0.0f,0.2392f, 0.4696f, 0.0912f ,0.0f,0.0f,0.0f,0.0f,1.0f};


    private  float intensity = 1.0f;

    public GLSepiaFilter() {
        this(VERTEX_SHADER, GLSepiaFilter_SHADER_2D);
    }

    public GLSepiaFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        //亮度
        colorMatrixUniform = GLES30.glGetUniformLocation(mProgramHandle,"colorMatrix");

        //对比度
        intensityUniform = GLES30.glGetUniformLocation(mProgramHandle,"intensity");
        setFloat(intensityUniform,intensity);
        setUniformMatrix4f(colorMatrixUniform,colorMatrix);
    }
}
