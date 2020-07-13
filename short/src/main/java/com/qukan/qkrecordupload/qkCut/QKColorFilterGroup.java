package com.qukan.qkrecordupload.qkCut;

import org.json.JSONException;
import org.json.JSONObject;

public class QKColorFilterGroup {
    // Brightness ranges from -1.0 to 1.0, with 0.0 as the normal level
    private double brightness; //亮度
    /**
     * Contrast ranges from 0.0 to 4.0 (max contrast), with 1.0 as the normal level
     */
    private double contrast;   //对比度
    /**
     * Saturation ranges from 0.0 (fully desaturated) to 2.0 (max saturation), with 1.0 as the normal level
     */
    private double saturation; //饱和度
    // Sharpness ranges from -4.0 to 4.0, with 0.0 as the normal level
    private double sharpness; //锐度

    /**
     * 设置色调 0 ~ 360
     *
     * @param hue
     */
    private double hue; //色调设置色调 0 ~ 360

    //获取默认参数
    public static QKColorFilterGroup getDefaultFilterGroup() {
        QKColorFilterGroup group = new QKColorFilterGroup();
        group.brightness = 0;
        group.contrast = 1.0;
        group.saturation = 1.0;
        group.sharpness = 0;
        group.hue = 0;
        return group;
    }
    //转json
    public JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("brightness", brightness);
        obj.put("contrast", contrast);
        obj.put("saturation", saturation);
        obj.put("sharpness", sharpness);
        obj.put("hue", hue);
        return obj;
    }
    //从json还原
    public static QKColorFilterGroup getFilterGroupByDic(JSONObject obj) throws JSONException {
        QKColorFilterGroup group = new QKColorFilterGroup();
        group.brightness = obj.getDouble("brightness");
        group.contrast = obj.getDouble("contrast");
        group.saturation = obj.getDouble("saturation");
        group.sharpness = obj.getDouble("sharpness");
        group.hue = obj.getDouble("hue");
        return group;
    }

    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    public double getContrast() {
        return contrast;
    }

    public void setContrast(double contrast) {
        this.contrast = contrast;
    }

    public double getSaturation() {
        return saturation;
    }

    public void setSaturation(double saturation) {
        this.saturation = saturation;
    }

    public double getSharpness() {
        return sharpness;
    }

    public void setSharpness(double sharpness) {
        this.sharpness = sharpness;
    }

    public double getHue() {
        return hue;
    }

    public void setHue(double hue) {
        this.hue = hue;
    }
}