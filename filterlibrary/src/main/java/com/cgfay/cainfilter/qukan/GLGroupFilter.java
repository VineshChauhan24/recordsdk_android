package com.cgfay.cainfilter.qukan;

import android.opengl.GLES30;
import android.util.Log;

import com.cgfay.cainfilter.glfilter.base.GLImageFilter;
import com.cgfay.cainfilter.utils.GlUtil;

import java.nio.FloatBuffer;

public class GLGroupFilter extends GLImageFilter {
    protected static final String GLGroupFilter_SHADER =
            "uniform mat4 uMVPMatrix;                                   \n" +
                    "attribute vec4 aPosition;                                  \n" +
                    "attribute vec4 aTextureCoord;                              \n" +
                    "varying vec2 textureCoordinate;                            \n" +

                    "uniform float imageWidthFactor;\n" +
                    "uniform float imageHeightFactor;\n" +
                    "uniform float sharpness;\n" +

                    "varying vec2 leftTextureCoordinate;\n" +
                    "varying vec2 rightTextureCoordinate;\n" +
                    "varying vec2 topTextureCoordinate;\n" +
                    "varying vec2 bottomTextureCoordinate;\n" +

                    "varying float centerMultiplier;\n" +
                    "varying float edgeMultiplier;\n" +

                    "void main() {                                              \n" +
                    "    gl_Position = uMVPMatrix * aPosition;                  \n" +
                    "    textureCoordinate = aTextureCoord.xy;                  \n" +
                    "vec2 widthStep = vec2(imageWidthFactor, 0.0);\n" +
                    "vec2 heightStep = vec2(0.0, imageHeightFactor);\n" +
                    "leftTextureCoordinate = aTextureCoord.xy - widthStep;\n" +
                    "rightTextureCoordinate = aTextureCoord.xy + widthStep;\n" +
                    "topTextureCoordinate = aTextureCoord.xy + heightStep;\n" +
                    "bottomTextureCoordinate = aTextureCoord.xy - heightStep;\n" +
                    "centerMultiplier = 1.0 + 4.0 * sharpness;\n" +
                    "edgeMultiplier = sharpness;\n" +

                    "}                                                          \n";

    private static final String GLGroupFilter_SHADER_2D =
            "precision mediump float;\n" +
                    "\n" +
                    "varying mediump vec2 textureCoordinate;\n" +
                    "uniform sampler2D inputTexture;\n" +
                    //亮度
                    "uniform lowp float brightness;\n" +
                    //对比度
                    "uniform lowp float contrast;\n" +
                    //饱和度
                    "uniform lowp float saturation;\n" +

                    // Values from "Graphics Shaders: Theory and Practice" by Bailey and Cunningham
                    "const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +

                    //锐度
                    "precision highp float;\n" +
                    "varying highp vec2 leftTextureCoordinate;\n" +
                    "varying highp vec2 rightTextureCoordinate;\n" +
                    "varying highp vec2 topTextureCoordinate;\n" +
                    "varying highp vec2 bottomTextureCoordinate;\n" +

                    "varying highp float centerMultiplier;\n" +
                    "varying highp float edgeMultiplier;\n" +

                    //曝光
                    //色度
                    "uniform mediump float hueAdjust;\n" +
                    "const highp  vec4  kRGBToYPrime = vec4 (0.299, 0.587, 0.114, 0.0);\n" +
                    "const highp  vec4  kRGBToI     = vec4 (0.595716, -0.274453, -0.321263, 0.0);\n" +
                    "const highp  vec4  kRGBToQ     = vec4 (0.211456, -0.522591, 0.31135, 0.0);\n" +

                    "const highp  vec4  kYIQToR   = vec4 (1.0, 0.9563, 0.6210, 0.0);\n" +
                    "const highp  vec4  kYIQToG   = vec4 (1.0, -0.2721, -0.6474, 0.0);\n" +
                    "const highp  vec4  kYIQToB   = vec4 (1.0, -1.1070, 1.7046, 0.0);\n" +

                    "void main(){\n" +
                        "highp vec4 textureColor = texture2D(inputTexture, textureCoordinate);\n" +

                        //锐度
                        "if(edgeMultiplier != 0.0){\n" +
                            //锐度
                            "mediump vec3 vtextureColor = textureColor.rgb;\n" +
                            "mediump vec3 leftTextureColor = texture2D(inputTexture, leftTextureCoordinate).rgb;\n" +
                            "mediump vec3 rightTextureColor = texture2D(inputTexture, rightTextureCoordinate).rgb;\n" +
                            "mediump vec3 topTextureColor = texture2D(inputTexture, topTextureCoordinate).rgb;\n" +
                            "mediump vec3 bottomTextureColor = texture2D(inputTexture, bottomTextureCoordinate).rgb;\n" +

                            "textureColor = vec4((vtextureColor * centerMultiplier - (leftTextureColor * edgeMultiplier + rightTextureColor * edgeMultiplier + topTextureColor * edgeMultiplier + bottomTextureColor * edgeMultiplier)), texture2D(inputTexture, bottomTextureCoordinate).w);\n" +
                        "}\n" +

                        //亮度
                        "if(brightness != 0.0){\n" +
                            "textureColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);\n" +
                        "}\n" +
                        //对比度
                        "if(contrast != 1.0){\n" +
                            "textureColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);\n" +
                        "}\n" +
                        //饱和度
                        "if(saturation != 1.0){\n" +
                        //饱和度
                            "lowp float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
                            "lowp vec3 greyScaleColor = vec3(luminance);\n" +

                            "textureColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);\n" +
                        "}\n" +


                        //色度
                        // Convert to YIQ
                        "highp float   YPrime  = dot (textureColor, kRGBToYPrime);\n" +
                        "highp float   I      = dot (textureColor, kRGBToI);\n" +
                        "highp float   Q      = dot (textureColor, kRGBToQ);\n" +

                        // Calculate the hue and chroma
                        "highp float   hue     = atan (Q, I);\n" +
                        "highp float   chroma  = sqrt (I * I + Q * Q);\n" +
                        // Make the user's adjustments
                        "hue += (-hueAdjust); //why negative rotation?\n" +

                        // Convert back to YIQ
                        "Q = chroma * sin (hue);\n" +
                        "I = chroma * cos (hue);\n" +

                        // Convert back to RGB
                        "highp vec4    yIQ   = vec4 (YPrime, I, Q, 0.0);\n" +
                        "textureColor.r = dot (yIQ, kYIQToR);\n" +
                        "textureColor.g = dot (yIQ, kYIQToG);\n" +
                        "textureColor.b = dot (yIQ, kYIQToB);\n" +

                        // Save the result
                        "gl_FragColor = textureColor;\n" +
                    "}";

    //亮度
    private int brightnessUniform;
    //对比度
    private int contrastUniform;
    //饱和度
    private int saturationUniform;
    //锐度
    private int sharpnessUniform;

    private int imageWidthFactorUniform;
    private int imageHeightFactorUniform;
    //色调设置色调 0 ~ 360
    private int hueAdjustUniform;

    private float brightness = -10.0f; //亮度
    private float contrast = -10.0f;   //对比度
    private float saturation = -10.0f; //饱和度
    private float sharpness = -10.0f; //锐度
    private float hue = -10.0f; //色调设置色调 0 ~ 360

    public GLGroupFilter() {
        this(GLGroupFilter_SHADER, GLGroupFilter_SHADER_2D);
    }

    public GLGroupFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        //亮度
        brightnessUniform = GLES30.glGetUniformLocation(mProgramHandle,"brightness");

        //对比度
        contrastUniform = GLES30.glGetUniformLocation(mProgramHandle,"contrast");
        //饱和度
        saturationUniform = GLES30.glGetUniformLocation(mProgramHandle,"saturation");

        //锐度
        sharpnessUniform = GLES30.glGetUniformLocation(mProgramHandle,"sharpness");
        imageWidthFactorUniform = GLES30.glGetUniformLocation(mProgramHandle,"imageWidthFactor");
        imageHeightFactorUniform = GLES30.glGetUniformLocation(mProgramHandle,"imageHeightFactor");

        //色调设置色调 0 ~ 360
        hueAdjustUniform = GLES30.glGetUniformLocation(mProgramHandle,"hueAdjust");

        Log.d("GLGroupFilter","brightnessUniform:" + brightnessUniform+ " contrastUniform:"+ contrastUniform+ " saturationUniform:"+ saturationUniform+ " sharpnessUniform: "+ sharpnessUniform+ " hueAdjustUniform:" + hueAdjustUniform);
        setBrightness(0.0f);
        setContrast(1.0f);
        setSaturation(1.0f);
        setSharpness(0.0f);
        setHue(0.0f);
    }

    @Override
    public void onInputSizeChanged(int width, int height) {
        super.onInputSizeChanged(width, height);
        setFloat(imageWidthFactorUniform,(float)(1.0/width));
        setFloat(imageHeightFactorUniform,(float)(1.0/height));
    }

    //亮度
    public void setBrightness(float newValue)
    {
        if(this.brightness != newValue){
            this.brightness = newValue;

            setFloat(brightnessUniform,this.brightness);
        }
    }
    //对比度
    public void setContrast(float newValue)
    {
        if(this.contrast != newValue){
            this.contrast = newValue;
            setFloat(contrastUniform,this.contrast);
        }
    }


    //饱和度
    public void setSaturation(float newValue)
    {
        if(this.saturation != newValue){
            this.saturation = newValue;

            setFloat(saturationUniform,this.saturation);
        }
    }


    //锐度
    public void setSharpness(float newValue)
    {
        if(this.sharpness != newValue){
            this.sharpness = newValue;

            setFloat(sharpnessUniform,this.sharpness);
        }
    }

    public void setHue(float newHue)
    {
        // Convert degrees to radians for hue rotation
        float mod = (float) (newHue%360.0);
        float m_pi = 3.1415926f;
        float hue = mod * m_pi/180;
        if(this.hue != hue){
            this.hue = hue;
            setFloat(hueAdjustUniform,hue);
        }
    }
}