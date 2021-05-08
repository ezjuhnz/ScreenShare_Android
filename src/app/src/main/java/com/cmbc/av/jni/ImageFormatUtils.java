package com.cmbc.av.jni;


/**
 * Time: 2021/3/25
 * Author: zhongjunhong
 * LastEdit: 2021/3/25
 * Description:图片数据格式转换
 */
public class ImageFormatUtils {

    static
    {
        System.loadLibrary("CImageUtil");
    }


    /**
     * @param src_frame  原始图片rgba数据
     * @param src_stride 原始图片row stride: 等于 宽度 + 一行的填充字节/4
     * @param width      原始图片宽度
     * @param height     原始图片高度
     * @param yuvbuffer  输出参数,NV21格式数据
     * @desc 用于将RGBA的图片格式转换成NV21格式
     */
    public native static void RGBAToNV21(byte[] src_frame, int src_stride,
                                         int width, int height, byte[] yuvbuffer);

    /**
     *
     * @param srcData   rgba原始数据
     * @param srcWidth  rgba图片宽
     * @param srcHeight rgba图片高
     * @param nv21Data   nv21接收数据  nv21图片高*nv21图片宽*3/2
     * @param nv21Width  nv21图片宽
     * @param nv21Height nv21图片高
     */
    public static native int rgbaToNV21(byte[] srcData,int srcWidth,int srcHeight,byte[] nv21Data,int nv21Width,int nv21Height);


    /**
     *
     * @param srcData       原始I420数据
     * @param srcWidth      原始图片宽度
     * @param srcHeight     原始图片高度
     * @param dstData       目标I420数据,输出参数
     * @param dstWidth      目标宽度
     * @param dstHeight     目标高度
     * @return
     */
    public static native int scaleI420(byte[] srcData, int srcWidth, int srcHeight, byte[] dstData, int dstWidth, int dstHeight, int rgbColor);

    /**
     *RGBA转I420
     * @param srcData
     * @param srcStride
     * @param srcWidth
     * @param srcHeight
     * @param dstData
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    public static native int rgbaToI420(byte[] srcData, int srcStride, int srcWidth, int srcHeight,byte[] dstData, int dstWidth, int dstHeight);

    /**
     * I420转NV21
     * @param i420Data
     * @param width
     * @param height
     * @param nv21Data
     * @return
     */
    public static native int i420ToNV21(byte[] i420Data, int width, int height, byte[] nv21Data);

    /**
     * 图像旋转
     * @param srcData
     * @param width
     * @param height
     * @param dstData
     * @param rotateMode
     * @return
     */
    public static native int rotateI420(byte[] srcData, int width, int height, byte[] dstData,int rotateMode);

    /**
     * 镜像功能
     * @param srcData   原始I420数据
     * @param width     宽度
     * @param height    高度
     * @param dstData   转换后的I420数据
     * @return
     */
    public static native int i420Mirror(byte[] srcData, int width, int height, byte[] dstData);
}
