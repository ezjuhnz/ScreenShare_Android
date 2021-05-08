package com.cmbc.av.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

import static androidx.core.math.MathUtils.clamp;

//import android.support.annotation.FloatRange;
//import android.support.annotation.NonNull;
//import android.support.v4.math.MathUtils;

//import static android.support.v4.math.MathUtils.clamp;

public class ImageUtils {
    public static byte[] fetchNV21(@NonNull Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int size = w * h;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        byte[] nv21 = new byte[size * 3 / 2];

        // Make w and h are all even.
        w &= ~1;
        h &= ~1;

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int yIndex = i * w + j;

                int argb = pixels[yIndex];
                int a = (argb >> 24) & 0xff;  // unused
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;

                int y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                y = clamp(y, 16, 255);
                nv21[yIndex] = (byte) y;

                if (i % 2 == 0 && j % 2 == 0) {
                    int u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                    int v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;

                    u = MathUtils.clamp(u, 0, 255);
                    v = MathUtils.clamp(v, 0, 255);

                    nv21[size + i / 2 * w + j] = (byte) v;
                    nv21[size + i / 2 * w + j + 1] = (byte) u;
                }
            }
        }
        return nv21;
    }

    public static byte[] colorconvertRGB_YUV_NV21(int[] aRGB, int width, int height) {
        final int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        byte[] yuv = new byte[width * height * 3 / 2];

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                //a = (aRGB[index] & 0xff000000) >> 24; //not using it right now
                R = (aRGB[index] & 0xff0000) >> 16;
                G = (aRGB[index] & 0xff00) >> 8;
                B = (aRGB[index] & 0xff) >> 0;

                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                yuv[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));

                if (j % 2 == 0 && index % 2 == 0) {
                    yuv[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }
                index++;
            }
        }
        return yuv;
    }

    //等比例放大缩小bitmap
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (width == bitmap.getWidth() && height == bitmap.getHeight()){
            return bitmap;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = (float) width / (float) w;
        float scaleHeight = (float) height / (float) h;
        if (scaleHeight < scaleWidth) {
            scaleWidth = scaleHeight;
        }
        matrix.postScale(scaleWidth, scaleWidth);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }


    public static Bitmap zoomBitmap4Color(int color, Bitmap bt, int width, int height) {
        if (bt.getWidth() == width && bt.getHeight() == height){
            return bt;
        }
        Bitmap zoomBitmap = zoomBitmap(bt, width, height);
        int zoomBitmapHeight = zoomBitmap.getHeight();
        int zoomBitmapWidth = zoomBitmap.getWidth();
        int marTop = (height - zoomBitmapHeight) / 2;
        int marLeft = (width - zoomBitmapWidth) / 2;
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(width,
                height, zoomBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, width, height, paint);
        canvas.drawBitmap(zoomBitmap, marLeft, marTop, paint);
        return bitmap;

    }


    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height, Context context) {
        RenderScript rs;
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
        Type.Builder yuvType, rgbaType;
        Allocation in, out;
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
        in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(nv21);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);
        //remember to destroy renderScript context, else the number of thread and graphic memory rise continuously
        rs.destroy();
        return bmpout;

    }

    /**
     * 通过降低图片的质量来压缩图片
     *
     * @param bitmap
     *            要压缩的图片
     * @param percentage
     *            压缩比例
     * @return 压缩后的图片
     */
    public static Bitmap compressByQuality(Bitmap bitmap, @FloatRange(from=0d,to = 1d) float percentage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        System.out.println("图片压缩前大小：" + baos.toByteArray().length + "byte");
        int targetSize = (int) (baos.toByteArray().length*percentage);
        while (baos.toByteArray().length / 1024 > targetSize/1024) {
            quality -= 10;
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            System.out.println("质量压缩到原来的" + quality + "%时大小为："
                    + baos.toByteArray().length + "byte");
        }
        System.out.println("图片压缩后大小：" + baos.toByteArray().length + "byte");
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
                baos.toByteArray().length);
        return bitmap;
    }
}
