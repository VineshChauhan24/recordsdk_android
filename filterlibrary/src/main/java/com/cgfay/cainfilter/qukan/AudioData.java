package com.cgfay.cainfilter.qukan;

public class AudioData {
    private byte[] data = null;
    private long pts = 0;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        byte[] newData = new byte[data.length];
        System.arraycopy(data, 0, newData, 0, data.length);
        this.data = newData;
    }

    public long getPts() {
        return pts;
    }

    public void setPts(long pts) {
        this.pts = pts;
    }
}
