package com.qukan.qkrecordupload.bean;

import lombok.Data;

/**
 * Created by Administrator on 2017/7/25 0025.
 */

@Data
public class PointPath {
    // 后期只使用left和top
    public float marginTop = -0.01f;
    public float marginBottom = -0.01f;
    public float marginLeft = -0.01f;
    public float marginRight = -0.01f;
    public boolean centerHorizontal;
    public boolean centerVertical;

    public boolean rightChanged = false;
    public PointPath copy() {
        PointPath path = new PointPath();
        path.marginTop = this.marginTop;
        path.marginBottom = this.marginBottom;
        path.marginLeft = this.marginLeft;
        path.marginRight = this.marginRight;
        path.centerHorizontal = this.centerHorizontal;
        path.centerVertical = this.centerVertical;
        return path;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(float marginRight) {
        rightChanged = true;
        this.marginRight = marginRight;
    }

    public void setFirstMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }
}
