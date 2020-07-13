package com.cgfay.cainfilter.qukan;

import android.opengl.GLES30;

import com.cgfay.cainfilter.glfilter.base.GLImageFilter;

public class GLRGBBressFilter  extends GLImageFilter {

    private static final String GLRGBBressFilter_SHADER_2D =
            "precision mediump float;\n" +
            "varying mediump vec2 textureCoordinate;\n" +
            "uniform sampler2D inputTexture;\n" +
            "uniform highp float redAdjustment;\n" +
            "uniform highp float greenAdjustment;\n" +
            "uniform highp float blueAdjustment;\n" +

            //亮度
            "uniform lowp float brightness;\n" +
            //对比度
            "uniform lowp float contrast;\n" +
            //饱和度
            "uniform lowp float saturation;\n" +

            // Values from "Graphics Shaders: Theory and Practice" by Bailey and Cunningham
            "const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +



            "void main(){\n" +
                "highp vec4 textureColor = texture2D(inputTexture, textureCoordinate);\n" +
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
                "gl_FragColor = vec4(textureColor.r * redAdjustment, textureColor.g * greenAdjustment, textureColor.b * blueAdjustment, textureColor.a);\n" +
            "}";


    private int redUniform;
    private int greenUniform;
    private int blueUniform;

    private float red;
    private float green;
    private float blue;
    //亮度
    private int brightnessUniform;
    //对比度
    private int contrastUniform;
    //饱和度
    private int saturationUniform;

    private float brightness = 0.0f; //亮度
    private float contrast = 1.0f;   //对比度
    private float saturation = 1.0f; //饱和度


    public GLRGBBressFilter() {
        this(VERTEX_SHADER, GLRGBBressFilter_SHADER_2D);
    }

    public GLRGBBressFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        //亮度
        redUniform = GLES30.glGetUniformLocation(mProgramHandle,"redAdjustment");
        greenUniform = GLES30.glGetUniformLocation(mProgramHandle,"greenAdjustment");
        blueUniform = GLES30.glGetUniformLocation(mProgramHandle,"blueAdjustment");

        //亮度
        brightnessUniform = GLES30.glGetUniformLocation(mProgramHandle,"brightness");

        //对比度
        contrastUniform = GLES30.glGetUniformLocation(mProgramHandle,"contrast");
        //饱和度
        saturationUniform = GLES30.glGetUniformLocation(mProgramHandle,"saturation");

        setRed(1.0f);
        setGreen(1.0f);
        setBlue(1.0f);

        setBrightness(0.0f);
        setContrast(1.0f);
        setSaturation(1.0f);
    }
    public void setRed(float red){
        this.red = red;
        setFloat(redUniform,red);
    }

    public void setGreen(float green){
        this.green = green;
        setFloat(greenUniform,green);
    }

    public void setBlue(float blue){
        this.blue = blue;
        setFloat(blueUniform,blue);
    }

    //亮度
    public void setBrightness(float newValue)
    {
        this.brightness = newValue;

        setFloat(brightnessUniform,this.brightness);
    }
    //对比度
    public void setContrast(float newValue)
    {
        this.contrast = newValue;
        setFloat(contrastUniform,this.contrast);
    }


    //饱和度
    public void setSaturation(float newValue)
    {
        this.saturation = newValue;

        setFloat(saturationUniform,this.saturation);
    }

}
