package com.bairuitech.anychat;

import android.view.SurfaceHolder;

public class AnyChatVideoHelper {
    private int MAX_VIDEO_NUM = 10;
    VideoRenderer[] render = new VideoRenderer[this.MAX_VIDEO_NUM];

    public int bindVideo(SurfaceHolder holder) {
        int i = 0;
        while (i < this.MAX_VIDEO_NUM) {
            if (this.render[i] != null && this.render[i].GetUserId() == -1) {
                this.render[i] = null;
            }
            i++;
        }
        for (i = 0; i < this.MAX_VIDEO_NUM; i++) {
            if (this.render[i] == null) {
                this.render[i] = new VideoRenderer(holder);
                return i;
            }
        }
        return -1;
    }

    public void UnBindVideo(int index) {
        SetVideoUserEx(index, -1, -1);
    }

    public void SetVideoUser(int index, int userid) {
        if (index >= 0 && index < this.MAX_VIDEO_NUM && this.render[index] != null) {
            this.render[index].SetUserId(userid);
            this.render[index].SetStreamIndex(0);
        }
    }

    public void SetVideoUserEx(int index, int userid, int streamindex) {
        if (index >= 0 && index < this.MAX_VIDEO_NUM && this.render[index] != null) {
            this.render[index].SetUserId(userid);
            this.render[index].SetStreamIndex(streamindex);
        }
    }

    public int SetVideoFmt(int userid, int streamindex, int width, int height) {
        VideoRenderer r = GetRenderByUserId(userid, streamindex);
        if (r == null) {
            return -1;
        }
        try {
            r.CreateBitmap(width, height);
        } catch (Exception e) {
        }
        return 0;
    }

    public void setMaxCutScale(int userId, float scale) {
        GetRenderByUserId(userId, 0).setMaxCutScale(scale);
    }

    public void setMaxCutScaleEx(int userId, int streamindex, float scale) {
        GetRenderByUserId(userId, streamindex).setMaxCutScale(scale);
    }

    public void ShowVideo(int userid, int streamindex, byte[] mPixel, int rotation, int mirror) {
        VideoRenderer r = GetRenderByUserId(userid, streamindex);
        if (r != null) {
            r.DrawByteBuffer(mPixel, rotation, mirror);
        }
    }

    private VideoRenderer GetRenderByUserId(int userid, int streamindex) {
        int i = 0;
        while (i < this.MAX_VIDEO_NUM) {
            if (this.render[i] != null && this.render[i].GetUserId() == userid && this.render[i].GetStreamIndex() == streamindex) {
                return this.render[i];
            }
            i++;
        }
        return null;
    }
}
