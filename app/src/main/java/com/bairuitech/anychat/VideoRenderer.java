package com.bairuitech.anychat;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Process;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import com.cmbc.av.utils.LogUtils;

import java.nio.ByteBuffer;

/* compiled from: AnyChatVideoHelper */
class VideoRenderer implements Callback {
    private Bitmap bitmap = null;
    private float dstBottomScale = 1.0f;
    private Rect dstRect = new Rect();
    private float dstRightScale = 1.0f;
    private int mStreamIndex = -1;
    private int mUserid = -1;
    private float max_cut_imgscale = 1.0f/3;
    private Rect srcRect = new Rect();
    private SurfaceHolder surfaceHolder;

    public VideoRenderer(SurfaceHolder holder) {
        if (holder != null) {
            this.mUserid = 0;
            this.surfaceHolder = holder;
            holder.addCallback(this);
        }
    }

    public int GetUserId() {
        return this.mUserid;
    }

    public int GetStreamIndex() {
        return this.mStreamIndex;
    }

    public void SetUserId(int userid) {
        this.mUserid = userid;
    }

    public void SetStreamIndex(int index) {
        this.mStreamIndex = index;
    }

    public void setMaxCutScale(float scale) {
        if (((double) scale) > 1.0d) {
            scale = 1.0f;
        }
        this.max_cut_imgscale = scale;
    }

    private void changeDestRect(int dstWidth, int dstHeight) {
        this.dstRect.right = (int) (((float) this.dstRect.left) + (this.dstRightScale * ((float) dstWidth)));
        this.dstRect.bottom = (int) (((float) this.dstRect.top) + (this.dstBottomScale * ((float) dstHeight)));
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        changeDestRect(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            Rect dst = holder.getSurfaceFrame();
            if (dst != null) {
                changeDestRect(dst.right - dst.left, dst.bottom - dst.top);
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.bitmap = null;
        this.surfaceHolder = null;
        this.mUserid = -1;
    }

    public Bitmap CreateBitmap(int width, int height) {
        if (this.bitmap == null) {
            try {
                Process.setThreadPriority(-4);
            } catch (Exception e) {
            }
        }
        if (!(this.bitmap == null || (this.srcRect.bottom == height && this.srcRect.right == width))) {
            this.bitmap = null;
        }
        if (this.bitmap == null) {
            this.bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
            this.srcRect.left = 0;
            this.srcRect.top = 0;
            this.srcRect.bottom = height;
            this.srcRect.right = width;
        }
        return this.bitmap;
    }

    public void SetCoordinates(float left, float top, float right, float bottom) {
        this.dstRightScale = right;
        this.dstBottomScale = bottom;
    }

    public void DrawByteBuffer(byte[] mPixel, int rotation, int mirror) {
        if (this.bitmap != null && this.surfaceHolder != null) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(mPixel);
            byteBuffer.rewind();
            this.bitmap.copyPixelsFromBuffer(byteBuffer);
            Canvas canvas = this.surfaceHolder.lockCanvas();
            if (canvas != null) {
                float fScalex;
                float fScaley;
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                Matrix matrix = new Matrix();
                float transX = 0.0f;
                float transY = 0.0f;
                int c_w = canvas.getWidth();
                int c_h = canvas.getHeight();
                int b_w = this.bitmap.getWidth();
                int b_h = this.bitmap.getHeight();
                int temp_b_w = b_w;
                int temp_b_h = b_h;
                if (rotation != 0) {
                    matrix.postRotate((float) rotation, ((float) this.bitmap.getWidth()) / 2.0f, ((float) this.bitmap.getHeight()) / 2.0f);
                    if (rotation == 90 || rotation == 270) {
                        temp_b_w = b_h;
                        temp_b_h = b_w;
                        matrix.postTranslate(0.5f * ((float) (b_h - b_w)), 0.5f * ((float) (b_w - b_h)));
                    }
                }
                if (c_h * temp_b_w > c_w * temp_b_h) {
                    float cutX = ((float) temp_b_w) - ((((float) c_w) * ((float) temp_b_h)) / ((float) c_h));
                    if (cutX > ((float) temp_b_w) * this.max_cut_imgscale) {
                        cutX = ((float) temp_b_w) * this.max_cut_imgscale;
                        transY = (((float) c_h) - ((((float) temp_b_h) * ((float) c_w)) / (((float) temp_b_w) - cutX))) / 2.0f;
                    }
                    transX = ((-cutX) * ((float) c_w)) / (2.0f * (((float) temp_b_w) - cutX));
                    fScalex = ((float) c_w) / (((float) temp_b_w) - cutX);
                    fScaley = fScalex;
                } else {
                    float cutY = ((float) temp_b_h) - ((((float) c_h) * ((float) temp_b_w)) / ((float) c_w));
                    if (cutY > ((float) temp_b_h) * this.max_cut_imgscale) {
                        cutY = ((float) temp_b_h) * this.max_cut_imgscale;
                        transX = (((float) c_w) - ((((float) temp_b_w) * ((float) c_h)) / (((float) temp_b_h) - cutY))) / 2.0f;
                    }
                    transY = ((-cutY) * ((float) c_h)) / (2.0f * (((float) temp_b_h) - cutY));
                    fScaley = ((float) c_h) / (((float) temp_b_h) - cutY);
                    fScalex = fScaley;
                }
                if (mirror != 0) {
                    matrix.postScale(-fScalex, fScaley);
                    matrix.postTranslate(((float) temp_b_w) * fScalex, 0.0f);
                } else {
                    matrix.postScale(fScalex, fScaley);
                }
                matrix.postTranslate(transX, transY);
                canvas.drawColor(-16777216);
                canvas.drawBitmap(this.bitmap, matrix, paint);
                this.surfaceHolder.unlockCanvasAndPost(canvas);
                return;
            }
            LogUtils.dTag("ANYCHAT", "Invalid canvas!");
        }
    }
}
