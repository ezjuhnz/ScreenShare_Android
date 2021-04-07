package com.cmbc.av.bean;

import java.io.Serializable;

/**
 * Created by win10-JYcainiao on 2017/8/8.
 */

public class AnychatVideoParams implements Serializable {
    /**
     * 帧率
     */
    private final int fps    = 15;
    /**
     * 关键帧
     */
    private final int gop    = 45;
    /**
     * 视频质量
     */
    private final int spzl   = 2;
    /**
     * 视频预设参数
     */
    private final int spyscs = 2;
    private String id;
    /**
     * 高度
     */
    private       int width;
    /**
     * 宽度
     */
    private       int height;
    /**
     * 码率
     */
    private       int kbs;
    public AnychatVideoParams(String id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.kbs = width * height * fps / 8;
    }
    /**
     * @param width
     * @param height
     * @param kbs
     */
    public AnychatVideoParams(String id, int width, int height, int kbs) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.kbs = kbs;
    }

    public int getGop() {
        return gop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getFps() {
        return fps;
    }

    public int getKbs() {
        return kbs;
    }

    public void setKbs(int kbs) {
        this.kbs = kbs;
    }

    public int getSpzl() {
        return spzl;
    }

    public int getSpyscs() {
        return spyscs;
    }

    @Override
    public String toString() {
        return "AnychatVideoParams{" + "id='" + id + '\'' + ", width=" + width + ", height=" + height + ", fps=" + fps
                + ", gop=" + gop + ", kbs=" + kbs + ", spzl=" + spzl + ", spyscs=" + spyscs + '}';
    }
}
