#ifndef YL_YUVHELPER_H
#define YL_YUVHELPER_H

#define YL_API
#define IN
#define OUT

//定义数据类型别名
typedef unsigned int uint32_t;
typedef int int32;
typedef unsigned short uint16_t;  // NOLINT
typedef short int16;            // NOLINT
typedef unsigned char uint8_t;
typedef signed char int8;

#define FOURCAL(a, b, c, d)                                     \
  (((uint32_t)(a)) | ((uint32_t)(b) << 8) |       /* NOLINT */ \
   ((uint32_t)(c) << 16) | ((uint32_t)(d) << 24)) /* NOLINT */

enum FourCal{
    YUV_I420 = FOURCAL('I', '4', '2', '0'),
    YUV_NV21 = FOURCAL('N', 'V', '2', '1'),
    YUV_NV12 = FOURCAL('N', 'V', '1', '2'),
    H264 = FOURCAL('H', '2', '6', '4'),
};

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @method cutI420
 * 裁剪原始数据,输出裁剪后的I420字节; 原始数据类型支持NV12, NV21, I420, H264
 * @param srcData       原始字节数据
 * @param srcWidth      原始宽度(可能比实际图片宽度大)
 * @param srcHeight     原始高度
 * @param dstData       裁剪后的I420字节数据
 * @param xStart        行坐标开始位置
 * @param yStart        纵坐标开始位置
 * @param xEnd          行坐标结束位置
 * @param yEnd          纵坐标结束位置
 * @param srcType         原始数据的类型
 * @return 0 on success
 */
YL_API int cutI420(uint8_t* srcData, int srcWidth, int srcHeight, uint8_t* dstData, int xStart,
                   int yStart, int xEnd, int yEnd,int srcType);

/**
 * @method ConvertRGBAToNV21
 * RGBA转NV21,该方法可以直接将有row padding的rgba转换成不带 row padding的NV21, 不需要经过裁剪
 * @param srcRgba          原始rgba字节数据
 * @param srcStride        原始rgba的宽度(含padding)
 * @param dstY             指向NV21数据 Y plane起始位置的指针
 * @param dstStrideY       NV21 Y stride
 * @param dstUV            指向NV21数据 NV plane起始位置的指针
 * @param dstStrideUV      NV21 UV stride
 * @param width            图片宽度(不含padding)
 * @param height           图片高度
 * @return 0 on success
 */
YL_API int convertRGBAToNV21(uint8_t* srcRgba, int srcStride, uint8_t* dstY,
                             int dstStrideY, uint8_t* dstUV, int dstStrideUV, int width, int height);


/**
 * @method rgbaToNV21
 * 将RGBA转成NV21,注意:因为RGBA有line_stride 导致每行会多出一定的长度(如宽度720,line_stride为768)
 * 所以需要裁剪掉多余的部分,得到无padding的I420后再将I420转成NV21
 * @param rgbaData    rgba原始数据
 * @param rgbaWidth   rgba图片宽度(有padding)
 * @param rgbaHeight  rgba图片高度
 * @param srcI420     I420原始数据
 * @param croppedI420 裁剪后的I420(将padding移除了)
 * @param nv21Data    NV21数据
 * @param nv21Width   NV21图片宽度(无padding)
 * @param nv21Height  NV21图片高度
 * @return 0 on success
 */
YL_API int rgbaToNV21(uint8_t* rgbaData, int rgbaWidth, int rgbaHeight, uint8_t* srcI420, uint8_t* croppedI420,
                      uint8_t* nv21Data, int nv21Width, int nv21Height);

/**
 * @method scaleAndFillColor
 * 将I420数据按比例缩放后再填充,使之满足给定分辨率
 * @param srcData   原始I420字节数据
 * @param srcWidth  原始宽度
 * @param srcHeight 原始高度
 * @param dstData   缩放填充后I420字节数据
 * @param dstWidth  目标宽度
 * @param dstHeight 目标高度
 * @param rgbColor  缩放后要填充的颜色
 * @return 0 on success
 */
YL_API int scaleAndFillColor( uint8_t* IN srcData, int srcWidth, int srcHeight,
                              uint8_t* OUT dstData, int dstWidth, int dstHeight, int rgbColor);

/**
 * @method convertRGBAToI420
 * rgba转I420,该方法能去除row padding,不需额外裁剪
 * @param srcData      rgba原始字节数据
 * @param srcStride    rgba line_stride
 * @param dstData      I420字节数据
 * @param dstWidth     I420宽度
 * @param dstHeight    I420高度
 * @return 0 on success
 */
YL_API int convertRGBAToI420(IN uint8_t* srcData, int srcStride,
                             OUT uint8_t* dstData, int dstWidth, int dstHeight);


/**
 * @method convertI420ToNV21
 * I420转NV21
 * @param srcData   I420原始字节数据
 * @param width     I420宽度
 * @param height    I420高度
 * @param dstData   NV21字节数据
 * @return 0 on success
 */
YL_API int convertI420ToNV21(uint8_t* srcData, int width, int height, uint8_t* dstData);

/**
 * @method rotateI420
 * 旋转I420数据
 * @param srcData       原始I420字节数据
 * @param width         宽度
 * @param height        高度
 * @param dstData       NV21字节数据
 * @param rotateMode    旋转角度
 * @return 0 on success
 */
YL_API int rotateI420(uint8_t* srcData, int width, int height, uint8_t* dstData, int rotateMode);

/**
 * @method mirrorI420
 * I420数据镜像功能
 * @param srcData   原始I420字节数据
 * @param width     宽度
 * @param height    高度
 * @param dstData   镜像I420字节数据
 * @return 0 on success
 */
YL_API int mirrorI420(uint8_t* srcData, int width, int height, uint8_t* dstData);

/**
 * @method convertNV12ToI420
 * NV12转I420
 * @param srcData   原始NV21字节数据
 * @param width     宽度
 * @param height    高度
 * @param dstData   I420字节数据
 * @return 0 on success
 */
YL_API int convertNV12ToI420(uint8_t* srcData, int width, int height, uint8_t* dstData);

/**
 * @method convertI420ToNV12
 * I420转NV12
 * @param srcData   原始I420字节数据
 * @param width     宽度
 * @param height    高度
 * @param dstData   NV12字节数据
 * @return 0 on success
 */
YL_API int convertI420ToNV12(uint8_t* srcData, int width, int height, uint8_t* dstData);

#ifdef __cplusplus
}
#endif

#endif //YL_YUVHELPER_H
