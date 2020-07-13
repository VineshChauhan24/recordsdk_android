package com.cgfay.cainfilter.qukan;

import android.graphics.Bitmap;

public class YuvData {
    private byte[] yuvBuf;


    private int width;
    private int decodeWidth;
    private int decodeHeight;

    private int height;
    private int orientation;
    private long size;
    private long time;
    private int selectIndex;
    //COLOR_FormatYUV420Planar  GLYUVFilter y uv
    //COLOR_FormatYUV420SemiPlanar  GL420Filter y u v
    private int colorFormat;
    public long pts;        //视频的pts
    public long playerpts;  //视频播放的pts
    private long seekType;
    //0 没有滤镜  1怀旧 2、素描 3清晰 4、恋橙 5、稚颜 6、青柠 7、film 8：微风 9：萤火虫
    private int filterType = 0;
    //0 无转场  1：左侧划入  2:右侧划入  3：淡入淡出  4：闪白  5：闪黑
    private int transFerType = 0;
    private int transFerCount = 0;

    private YuvData transfer = null;
    private GroupFilters groupFilters = new GroupFilters();
    private Bitmap waterBitmap = null;


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getDecodeWidth() {
        return decodeWidth;
    }

    public void setDecodeWidth(int decodeWidth) {
        this.decodeWidth = decodeWidth;
    }

    public int getDecodeHeight() {
        return decodeHeight;
    }

    public void setDecodeHeight(int decodeHeight) {
        this.decodeHeight = decodeHeight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte[] getYuvBuf() {
        return yuvBuf;
    }

    public void setYuvBuf(byte[] yuvBuf) {
        this.yuvBuf = yuvBuf;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public int getColorFormat() {
        return colorFormat;
    }

    public void setColorFormat(int colorFormat) {
        this.colorFormat = colorFormat;
    }

    public long getPts() {
        return pts;
    }

    public void setPts(long pts) {
        this.pts = pts;
    }

    public long getPlayerpts() {
        return playerpts;
    }

    public void setPlayerpts(long playerpts) {
        this.playerpts = playerpts;
    }

    public long getSeekType() {
        return seekType;
    }

    public void setSeekType(long seekType) {
        this.seekType = seekType;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public int getTransFerType() {
        return transFerType;
    }

    public void setTransFerType(int transFerType) {
        this.transFerType = transFerType;
    }

    public YuvData getTransfer() {
        return transfer;
    }

    public void setTransfer(YuvData transfer) {
        this.transfer = transfer;
    }

    public int getTransFerCount() {
        return transFerCount;
    }

    public void setTransFerCount(int transFerCount) {
        this.transFerCount = transFerCount;
    }

    public GroupFilters getGroupFilters() {
        return groupFilters;
    }

    public void setGroupFilters(GroupFilters groupFilters) {
        this.groupFilters = groupFilters;
    }

    public Bitmap getWaterBitmap() {
        return waterBitmap;
    }

    public void setWaterBitmap(Bitmap waterBitmap) {
        this.waterBitmap = waterBitmap;
    }
}
