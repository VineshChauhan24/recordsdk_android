package com.cgfay.cainfilter.qukan;

public class GroupFilters {

    //亮度-1~1 0是正常色
    private float brightness = 0.0f;
    //对比度 0.0 to 4.0 1.0是正常色
    private float contrast = 1.0f;
    //饱和度  0.0  to 2.0 1是正常色
    private float saturation = 1.0f;
    //锐度 -4.0 to 4.0, with 0.0 是正常色
    private float sharpness = 0;
    //设置色调 0 ~ 360 0.0 是正常色
    private float hue = 0;

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getSharpness() {
        return sharpness;
    }

    public void setSharpness(float sharpness) {
        this.sharpness = sharpness;
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }
}
