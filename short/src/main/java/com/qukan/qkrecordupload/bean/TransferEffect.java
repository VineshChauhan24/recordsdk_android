package com.qukan.qkrecordupload.bean;

import lombok.Data;

/**
 * Created by Administrator on 2017/12/26 0026.
 */
@Data
public class TransferEffect {

    // 转场效果名字
    String name;
    // 转场小icon名字
    String imaName;
    // 转场图片名字
    String imgTransfer;

    int type;  //转场动画：0 没有动画  1:左侧划入 2右侧划入 3:淡出

    public TransferEffect copy() {
        TransferEffect transferEffect = new TransferEffect();
        transferEffect.name = this.name;
        transferEffect.imaName = this.imaName;
        transferEffect.imgTransfer = this.imgTransfer;
        transferEffect.type = this.type;
        return transferEffect;
    }

    public static TransferEffect getDefaultTrans(){
        TransferEffect transferEffect = new TransferEffect();
        transferEffect.name = "";
        transferEffect.imaName = "";
        transferEffect.imgTransfer = "";
        transferEffect.type = 0;
        return transferEffect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImaName() {
        return imaName;
    }

    public void setImaName(String imaName) {
        this.imaName = imaName;
    }

    public String getImgTransfer() {
        return imgTransfer;
    }

    public void setImgTransfer(String imgTransfer) {
        this.imgTransfer = imgTransfer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
