package com.qukan.qkrecordupload.bean;

import lombok.Data;

/**
 * Created by yang on 2017/7/21.
 */
@Data
public class ViewTitleOnePart {
//    {"titleOnePartList":[{"text":"杭州趣看科技有限公司","fontSize":16,"textColor":"#FFFFFF","backgroundColor":"#000000","addBackground":true,"align":1,"padding":0,"type":2}]}
String text = "杭州趣看科技有限公司";
    String fontName = "";
    String imgName = "";
    int fontSize = 16;
    // 字体透明度
    float alpha = 1.0f;
    // 默认文字白色
    String textColor= "#ffffff";
    // 默认背景透明
    String backgroundColor= "#00000000";

    int margin = 0;
    int padding = 0;
    int gravity;//跟下一个元素对比，所在的位置  0:占一行 1、左边 2、右边 3、居中
    int type = 2; //1:图片 2、文字 3、纯色背景，4、图片背景 目前图片背景没有用到
    String path;
    boolean addBackground = false;//备选字段表示有没有背景色
    boolean isSingleLine=false;// 是否单行
    int width;
    int height;

    double scaleImg = 1.0;
    int imgWidth = -1;
    int imgHeight = -1;


    public ViewTitleOnePart copy() {
        ViewTitleOnePart viewTitleOnePart = new ViewTitleOnePart();
        viewTitleOnePart.text = this.text;
        viewTitleOnePart.fontName = this.fontName;
        viewTitleOnePart.imgName = this.imgName;
        viewTitleOnePart.fontSize = this.fontSize;
        viewTitleOnePart.alpha = this.alpha;
        viewTitleOnePart.textColor = this.textColor;
        viewTitleOnePart.backgroundColor = this.backgroundColor;
        viewTitleOnePart.margin = this.margin;
        viewTitleOnePart.padding = this.padding;
        viewTitleOnePart.gravity = this.gravity;
        viewTitleOnePart.type = this.type;
        viewTitleOnePart.path = this.path;
        viewTitleOnePart.addBackground = this.addBackground;
        viewTitleOnePart.isSingleLine = this.isSingleLine;
        viewTitleOnePart.width = this.width;
        viewTitleOnePart.height = this.height;
        return viewTitleOnePart;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAddBackground() {
        return addBackground;
    }

    public void setAddBackground(boolean addBackground) {
        this.addBackground = addBackground;
    }

    public boolean isSingleLine() {
        return isSingleLine;
    }

    public void setSingleLine(boolean singleLine) {
        isSingleLine = singleLine;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getScaleImg() {
        return scaleImg;
    }

    public void setScaleImg(double scaleImg) {
        this.scaleImg = scaleImg;
    }

    public int getImgWidth() {
        return (int)(imgWidth*scaleImg);
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return (int)(imgHeight*scaleImg);
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }
}
