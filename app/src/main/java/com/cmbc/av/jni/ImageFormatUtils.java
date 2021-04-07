package com.cmbc.av.jni;


/**
 * Time: 2021/3/25
 * Author: zhongjunhong
 * LastEdit: 2021/3/25
 * Description:
 */
public class ImageFormatUtils {

    //public  static native byte[] fetchNV21(Bitmap bitmap) ;
    public native int testAdd(int i, int j);

    public static native byte[] getNV21(int width, int height, int[] pixels);

    public static native int rgbaToYuv(byte[] rgbabyte, byte[] yuvbyte, int width, int height);

    //RGBA to ARGB
    public static native void RGBAToARGB(byte[] src_rgba, int src_stride, byte[] dest_argb, int dest_stride,
                                         int width, int height);

    //ARGB to NV21
    public native static void ARGBToNV21(byte[] src_frame,int src_stride,
                                         int width, int height, byte[] yBuffer, byte[] uvBuffer);

    public native static void ARGBIntToNV21(int[] src_frame,int src_stride,
                                         int width, int height, byte[] yBuffer, byte[] uvBuffer);

    //I420 to RGBA
    public static native int I420ToRgba(int type,byte[] yuv,byte[] rgba,int width,int height);

    public static native void rgbToYuv(byte[] rgba,  int width,int height, byte[] yuv);

    public static native void rgbaToI420(byte[] rgba, int src_stride, int width, int height, int dst_stride, byte[] ybuf, byte[] ubuf,byte[] vbuf);

    public static native int RgbaToI420(int type,byte[] rgba,byte[] yuv,int width,int height);

}
