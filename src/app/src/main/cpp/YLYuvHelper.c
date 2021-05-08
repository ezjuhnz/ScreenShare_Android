#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <stdarg.h>
#include <time.h>
#include "libyuv.h"
#include "YLYuvHelper.h"

#ifndef Y
#define Y(r,g,b)  (0.299*r + 0.587*g + 0.114 *b)
#endif

#ifndef U
#define U(r,g,b) ( -0.169 * r - 0.331*g + 0.499*b + 128)
#endif

#ifndef V
#define V(r,g,b) (0.499*r - 0.418*g - 0.0813*b + 128)
#endif

int mY = 0;
int mU = 0;
int mV = 0;


#define LOGC(type, format, ...) logC(__func__, __FILE__, __LINE__, type, format, ##__VA_ARGS__)

/**
 * 将日志输出到文件中
 * @param func  函数名
 * @param file  文件路径
 * @param line  当前行
 * @param type  日志类型
 * @param format 输出格式
 * @param ...
 */

void logC(const char *func, const char *file, const int line,
          const char *type, const char *format, ...)
{
    FILE *file_fp;
    time_t loacl_time;
    char time_str[128];

    // 获取本地时间
    time(&loacl_time);
    strftime(time_str, sizeof(time_str), "[%Y.%m.%d %X]", localtime(&loacl_time));

    // 日志内容格式转换
    va_list ap;
    va_start(ap, format);
    char fmt_str[2048];
    vsnprintf(fmt_str, sizeof(fmt_str), format, ap);
    va_end(ap);

    // 打开日志文件
    const char* logPath =  "/storage/emulated/0/hello.log";
    file_fp = fopen(logPath, "a");

    // 写入到日志文件中
    if (file_fp != NULL)
    {
        fprintf(file_fp, "[%s]%s[%s@%s:%d] %s\n", type, time_str, func,
                file, line, fmt_str);
        fclose(file_fp);
    }
    else
    {
        fprintf(stderr, "[%s]%s[%s@%s:%d] %s\n", type, time_str, func,
                file, line, fmt_str);
    }
    if(file_fp != NULL)
    {
        fclose(file_fp);
    }
}

/**
 *计算原始图片分辨率与目标图片分辨率之间的缩放比率
 * @param srcWidth   原始图片宽度
 * @param srcHeight  原始图片高度
 * @param dstWidth   目标图片宽度
 * @param dstHeight  目标图片高度
 * @return 缩放比率
 */
float getScaleRatio(int srcWidth, int srcHeight, int dstWidth, int dstHeight)
{
    float ratio = 0;
    float wRatio = 0;
    float hRatio = 0;
    wRatio = dstWidth / (float)srcWidth;
    hRatio = dstHeight / (float)srcHeight;
    ratio = (wRatio < hRatio) ? wRatio : hRatio;
    return ratio;
}

/**
 * 函数指针数组,根据rgb类型调用不同的函数,将I420转成指定类型的RGB
 * @return
 */
static int (*i420ToRgbaFunc[])(const uint8_t*, int, const uint8_t*, int, const uint8_t*, int, uint8_t*,
                               int ,int ,int ) = {
        I420ToABGR, I420ToRGBA, I420ToARGB, I420ToBGRA,
        I420ToRGB24, I420ToRGB565
};

/**
 * 函数指针数组,根据rgb类型调用不同的函数,将指定类型的RGB转成I420
 * @return
 */
static int (*rgbaToI420Func[])(const uint8_t*, int, uint8_t*, int, uint8_t*, int , uint8_t*, int, int, int) = {
        ABGRToI420, RGBAToI420, ARGBToI420, BGRAToI420,
        RGB24ToI420, RGB565ToI420
};

/**
 * 写文件,测试专用
 * @param data 要写入文件的字节
 * @param size 字节长度
 * @param path 文件路径
 * @return  0 on success
 */
int writeBytesToFile(uint8_t* data, int size,const char* path)
{
    //const char* path = "/storage/emulated/0/scaleI420.yuv";
    FILE* fp = fopen(path,"wb");
    fwrite(data, size,1, fp);
    fflush(fp);
    fclose(fp);
    return 0;
}

/**
 * 给I420数据图像的左右填充黑边(或其他颜色),使有效像素居中,该方法适用于只需要填充左右,不需要填充上下的图像
 * @param scaleData     原始缩放图像
 * @param scaleWidth    缩放图像宽度
 * @param scaleHeight   缩放图像高度
 * @param dstData       填充后的图像数据
 * @param dstWidth      填充后的图像宽度
 * @param dstHeight     填充后的图像高度
 * @return 0 on success
 */
int I420FillLeftAndRight(uint8_t* scaleData, int scaleWidth, int scaleHeight, uint8_t* dstData, int dstWidth, int dstHeight)
{
    LOGC("DEBUG"," --begin--");
    int offset_w = ((dstWidth - scaleWidth) >> 1);
    int yRows = 0;
    int uRows = 0;
    int vRows = 0;
    uint8_t* uData = dstData + dstWidth * dstHeight;
    uint8_t* vData = uData + dstWidth * dstHeight / 4;
    while(yRows < scaleHeight)
    {
        memcpy(dstData + yRows*dstWidth + offset_w, scaleData, scaleWidth);
        scaleData += scaleWidth;
        yRows++;
    }
    while(uRows < (scaleHeight >> 1))
    {
        memcpy(uData + ((uRows*dstWidth + offset_w) >> 1), scaleData, scaleWidth >> 1);
        scaleData += (scaleWidth >> 1);
        uRows++;
    }
    while(vRows < (scaleHeight >> 1))
    {
        memcpy(vData + ((vRows*dstWidth + offset_w) >> 1), scaleData, scaleWidth >> 1);
        scaleData += (scaleWidth >> 1);
        vRows++;
    }
    LOGC("DEBUG"," --end-- ");
    return 0;
}

/**
 * 给I420数据图像的上下填充黑边(或其他颜色),使有效像素居中,该方法适用于只需要填充上下,不需要填充左右的图像
 * @param scaleData     原始缩放图像
 * @param scaleWidth    缩放图像宽度
 * @param scaleHeight   缩放图像高度
 * @param dstData       填充后的图像数据
 * @param dstWidth      填充后的图像宽度
 * @param dstHeight     填充后的图像高度
 * @return 0 on success
 */
int I420FillUpDown(uint8_t* scaleData, int scaleWidth, int scaleHeight, uint8_t* dstData, int dstWidth, int dstHeight)
{
    LOGC("DEBUG"," --begin--");
    uint8_t* rotateData = malloc(scaleWidth * scaleHeight * 3 /2);
    uint8_t* paddingData = malloc(dstWidth * dstHeight * 3 / 2);
    memset(paddingData, mY,dstWidth * dstHeight ); //填充Y: 目标图片: 1280*720
    memset(paddingData + dstWidth * dstHeight, mU,dstWidth * dstHeight / 4); //填充U
    memset(paddingData + dstWidth * dstHeight*5/4, mV, dstWidth * dstHeight / 4); //填充U
    int rotateWidth = scaleHeight;
    int rotateHeight = scaleWidth;


    int ret = I420Rotate(scaleData, scaleWidth,
               scaleData +  scaleWidth * scaleHeight,scaleWidth >> 1,
               scaleData +  scaleWidth * scaleHeight * 5 / 4, scaleWidth >> 1,
               rotateData, rotateWidth,
               rotateData +  rotateWidth * rotateHeight, rotateWidth >> 1,
               rotateData +  rotateWidth * rotateHeight * 5 / 4, rotateWidth >> 1,
               scaleWidth, scaleHeight, kRotate90);
    scaleData = rotateData;
    scaleWidth = rotateWidth;   //606
    scaleHeight = rotateHeight; //1280

    //2.再填充左右黑边,606*1280 => 720*1280
    int paddingWidth = dstHeight;
    int paddingHeight = dstWidth;
    I420FillLeftAndRight(rotateData, scaleWidth, scaleHeight, paddingData, paddingWidth, paddingHeight);

    //3.最后旋转
    ret = I420Rotate(paddingData, paddingWidth,
            paddingData + paddingWidth * paddingHeight,paddingWidth >> 1,
            paddingData + paddingWidth * paddingHeight * 5/4,paddingWidth >> 1,
             dstData, dstWidth,
            dstData + dstWidth * dstHeight,dstWidth >> 1,
            dstData + dstWidth * dstHeight * 5/4,dstWidth >> 1,
               paddingWidth, paddingHeight, kRotate270);

    if(rotateData != NULL)
    {
        free(rotateData);
        rotateData = NULL;
    }
    if(paddingData != NULL)
    {
        free(paddingData);
        paddingData = NULL;
    }
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

YL_API int convertRGBAToNV21(uint8_t* srcRgba, int srcStride, uint8_t* dstY,
                             int dstStrideY, uint8_t* dstUV, int dstStrideUV, int width, int height)
{
    LOGC("DEBUG"," --begin--");
    uint8_t* dstBgra = malloc(width * height * 4);
    ARGBToABGR(srcRgba, srcStride, dstBgra, srcStride, width, height);

    int ret = ARGBToNV21(dstBgra,
                         srcStride,
                         dstY,
                         width,
                         dstUV,
                         width,
                         width,
                         height);

    if(dstBgra != NULL) //release memory
    {
        free(dstBgra);
        dstBgra = NULL;
    }
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

/**
 * rgba转I420
 * @param srcData      原始rgba字节数据
 * @param srcStride    rgba line_stride
 * @param yuvData      YUV字节数据
 * @param yStride      y stride
 * @param uStride      u stride
 * @param vStride     v stride
 * @param width         图片宽度(有效宽度,不含row padding)
 * @param height        图片高度
 * @param func          函数指针
 * @return 0 on success
 */
int rgbToI420(uint8_t* srcData, int srcStride,
                     uint8_t* yuvData, int yStride, int uStride, int vStride,
                     int width, int height,
                     int (*func)(const uint8_t*, int, uint8_t*, int,uint8_t*, int, uint8_t*, int, int, int)){
    LOGC("DEBUG"," --begin--");
    size_t ySize = (size_t) (yStride * height);
    size_t uSize = (size_t) (uStride * height >> 1);

    int ret = func((const uint8_t*) srcData, srcStride, (uint8_t*) yuvData, yStride,
                   (uint8_t*) (yuvData) + ySize, uStride, (uint8_t*) (yuvData )+ ySize + uSize,
                   vStride, width, height);
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}


/**
 * RGB转I420,支持不同类型的RGB
 * @param type      RGB类型
 * @param srcData   原始rgb数据
 * @param dstData   目标I420数据
 * @param width     图像宽度
 * @param height    图像高度
 * @return
 */
int rgb_type_ToI420(int type, uint8_t* srcData, uint8_t* dstData, int width, int height)
{
    uint8_t cType = (uint8_t) (type & 0x0F); //0x01001040 & 0x0F = 0
    int rgba_stride = ((type & 0xF0) >> 4)*width;
    int y_stride = width;
    int u_stride = width >> 1;
    int v_stride = u_stride;
    return rgbToI420(srcData, rgba_stride, dstData, y_stride, u_stride, v_stride, width, height, rgbaToI420Func[cType]);
}

YL_API int cutI420(uint8_t* srcData, int srcWidth, int srcHeight, uint8_t* dstData, int xStart,
                   int yStart, int xEnd, int yEnd,int srcType)
{
    LOGC("DEBUG"," --begin--");
    int ret = ConvertToI420(
            srcData, srcWidth * srcHeight * 3 / 2,
            dstData, xEnd,
            dstData + xEnd * yEnd, (xEnd + 1) / 2,
            dstData+xEnd * yEnd+((xEnd+1)/2)*((yEnd+1)/2), (xEnd+1)/2,
            xStart, yStart,
            srcWidth, srcHeight,
            xEnd, yEnd,
            kRotate0, srcType);
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

YL_API int rgbaToNV21(uint8_t* rgbaData, int rgbaWidth, int rgbaHeight, uint8_t* srcI420, uint8_t* croppedI420,
                      uint8_t* nv21Data, int nv21Width, int nv21Height)
{
    LOGC("DEBUG"," --begin--");
    //1.RGBA转 I420
    rgb_type_ToI420(0x01001040, rgbaData, srcI420, rgbaWidth, rgbaHeight);
    //2.裁剪I420数据
    cutI420(srcI420, rgbaWidth, rgbaHeight, croppedI420,0,0, nv21Width, nv21Height,YUV_I420);
    //3.I420 转 NV21
    int ret = I420ToNV21(croppedI420, nv21Width,
                         croppedI420 + nv21Width * nv21Height, nv21Width >> 1,
                         croppedI420 + nv21Width * nv21Height * 5 / 4, nv21Width >> 1,
                         nv21Data, nv21Width,
                         nv21Data + nv21Width * nv21Height, nv21Width,
                         nv21Width, nv21Height);
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

YL_API int convertRGBAToI420(uint8_t* rgbaData, int srcStride,
                             uint8_t* dstData, int dstWidth, int dstHeight)
{
    LOGC("DEBUG"," --begin--");
    int dstSize = dstWidth * dstHeight;
    int yStride = dstWidth;
    int uStride = yStride >> 1;
    int vStride = uStride;

    int ret = ABGRToI420(rgbaData, srcStride, dstData, yStride,dstData+dstSize, uStride,
                         dstData + dstSize * 5 / 4, vStride, dstWidth, dstHeight);
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

YL_API int scaleAndFillColor( uint8_t* srcData, int srcWidth, int srcHeight,
                              uint8_t* dstData, int dstWidth, int dstHeight, int rgbColor) {

    LOGC("DEBUG"," --begin--");
    //如果需要放大,则不作任何操作
    int ret = 0;
    if(srcWidth < dstWidth && srcHeight < dstHeight)
    {
        return -1;
    }
    if(srcWidth == dstWidth && srcHeight == dstHeight ) //如果分辨率相同,则直接返回
    {
        memcpy(dstData, srcData,srcWidth*srcHeight * 3 / 2 );
        return ret;
    }
    int needRotation = 0;
    int scaleWidth;
    int scaleHeight;
    int bgWidth = dstWidth;
    int bgHeight = dstHeight;
    float ratio = 1;
    int srcSize = srcWidth * srcHeight;

    uint8_t* rotateData = malloc(srcSize * 3 /2); //memory alloc, remember release!

    int rotateWidth = srcHeight; //720
    int rotateHeight = srcWidth; //1520
    //如果是横屏,先旋转成竖屏 -> 做缩放 -> 填充黑边 ->再次旋转成横屏
    if(srcWidth > srcHeight)
    {
        needRotation = 1;
        bgWidth = dstHeight;
        bgHeight = dstWidth;
        //1.旋转: 720 * 1520
        I420Rotate(srcData, srcWidth,
                   srcData +  srcWidth * srcHeight,srcWidth >> 1,
                   srcData +  srcWidth * srcHeight * 5 / 4, srcWidth >> 1,
                   rotateData, rotateWidth,
                   rotateData +  rotateWidth * rotateHeight, rotateWidth >> 1,
                   rotateData +  rotateWidth * rotateHeight * 5 / 4, rotateWidth >> 1,
                   srcWidth, srcHeight, kRotate90);

        srcWidth = rotateWidth;   //720
        srcHeight = rotateHeight; //1520
        srcData = rotateData;
        ratio = getScaleRatio(srcWidth, srcHeight, bgWidth, bgHeight);   //720,1520,720,1280
    } else{
        ratio = getScaleRatio(srcWidth, srcHeight, dstWidth, dstHeight); //1520,720,1280,720
    }


    scaleWidth = (int)(srcWidth * ratio);   //606
    scaleHeight = (int)(srcHeight * ratio); //1280
    scaleWidth = (scaleWidth % 2 == 0) ? scaleWidth : scaleWidth + 1;
    scaleHeight = (scaleHeight % 2 == 0) ? scaleHeight : scaleHeight + 1;
    int scaledSize = scaleWidth * scaleHeight;
    uint8_t* scaledData = malloc(scaledSize * 3 / 2); //memory alloc, remember release!

    //2.缩放 : 606 * 1280
    ret = I420Scale(srcData, srcWidth,
              srcData + srcSize, srcWidth >> 1,
              srcData + srcSize * 5 / 4, srcWidth >> 1,
              srcWidth, srcHeight,
              scaledData, scaleWidth,
              scaledData + scaledSize, scaleWidth >> 1,
              scaledData + scaledSize * 5 / 4, scaleWidth >> 1,
              scaleWidth, scaleHeight,kFilterBox);


    //填充黑边或其他颜色的边.
    int R = (rgbColor & 0xFF0000) >> 16;
    int G = (rgbColor & 0x00FF00) >> 8;
    int B = (rgbColor & 0x0000FF);

    mY = Y(R, G, B);
    mU = U(R, G, B);
    mV = V(R, G, B);

    //3.填充: 图片左右填充黑边,缩放的图片和目标图片的宽和高必定有一组相等
    uint8_t* paddingData;
    paddingData = malloc(bgWidth * bgHeight * 3 /2);
    memset(paddingData, mY,bgWidth * bgHeight ); //填充Y: 目标图片: 1280*720
    memset(paddingData + bgWidth * bgHeight, mU,bgWidth * bgHeight / 4); //填充U
    memset(paddingData + bgWidth * bgHeight * 5 / 4, mV, bgWidth * bgHeight / 4); //填充U

    if(scaleHeight == bgHeight && scaleWidth <= bgWidth) //宽不够,左右填充
    {
        ret = I420FillLeftAndRight(scaledData, scaleWidth, scaleHeight, paddingData, bgWidth, bgHeight);
        memcpy(dstData, paddingData,bgWidth * bgHeight * 3 / 2);
    }
    else //高不够,上下填充
    {
        I420FillUpDown(scaledData, scaleWidth, scaleHeight, paddingData, bgWidth, bgHeight);
        memcpy(dstData, paddingData,bgWidth * bgHeight * 3 / 2);
    }

    //4.如果前面已经经过横屏到竖屏的处理,则需要再旋转成横屏,并返回横屏的数据
    if(needRotation)
    {
        ret = I420Rotate(paddingData, bgWidth,
                   paddingData + bgWidth * bgHeight, bgWidth >> 1,
                   paddingData + bgWidth * bgHeight * 5 / 4, bgWidth >> 1,
                   dstData, dstWidth,
                   dstData + dstWidth * dstHeight,dstWidth >> 1,
                   dstData + dstWidth * dstHeight * 5 / 4,dstWidth >> 1,
                   bgWidth, bgHeight, kRotate270);

    }

    if(scaledData != NULL)
    {
        free(scaledData);
        scaledData = NULL;
    }
    if(rotateData != NULL)
    {
        free(rotateData);
        rotateData = NULL;
    }
    if(paddingData != NULL)
    {
        free(paddingData);
        paddingData = NULL;
    }
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

YL_API int convertI420ToNV21(uint8_t* i420Data, int width, int height, uint8_t* nv21Data)
{
    LOGC("DEBUG"," --begin--");
    int src_stride_y = width;
    int src_size = width * height;
    int src_stride_u = src_stride_y >> 1;
    int src_stride_v = src_stride_u;

    int dst_stride_y = src_stride_y;
    int dst_stride_vu = dst_stride_y;
    int dst_size = src_size;

    int ret = I420ToNV21(i420Data, src_stride_y,i420Data + src_size, src_stride_u,i420Data+src_size * 5 / 4, src_stride_v,
                         nv21Data, dst_stride_y,nv21Data+dst_size, dst_stride_vu, width, height);
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

YL_API int rotateI420(uint8_t* srcData, int width, int height, uint8_t* dstData, int rotateMode)
{
    LOGC("DEBUG"," --begin--");
    int picSize = width * height;
    int src_stride_y = width;
    int dst_stride_y = src_stride_y;
    int src_stride_u = src_stride_y >> 1;
    int src_stride_v = src_stride_u;

    int dst_stride_u = src_stride_u;
    int dst_stride_v = src_stride_u;
    if(rotateMode == kRotate90 || rotateMode == kRotate270) //旋转90或270度
    {
        dst_stride_y = height;
        dst_stride_u = dst_stride_y >> 1;
        dst_stride_v = dst_stride_u;

    }
    int ret = I420Rotate(srcData, src_stride_y,
                         srcData + picSize, src_stride_u,
                         srcData + picSize * 5 / 4, src_stride_v,
                         dstData, dst_stride_y,
                         dstData + picSize, dst_stride_u,
                         dstData + picSize * 5 / 4, dst_stride_v,
                         width, height, rotateMode);
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

YL_API int mirrorI420(uint8_t* srcData, int width, int height, uint8_t* dstData)
{
    LOGC("DEBUG"," --begin-- ");
    int src_stride_y = width;
    int src_stride_u = src_stride_y >> 1;
    int src_stride_v = src_stride_u;
    int src_size = width * height;

    int ret = I420Mirror(srcData, src_stride_y,
                         srcData + src_size, src_stride_u,
                         srcData + src_size * 5 / 4, src_stride_v,
                         dstData, src_stride_y,
                         dstData+src_size, src_stride_u,
                         dstData+src_size * 5 / 4, src_stride_v,
                         width, height);
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

YL_API int convertNV12ToI420(uint8_t* srcData, int width, int height, uint8_t* dstData)
{
    LOGC("DEBUG"," --begin--");
    int src_stride_y = width;
    int src_stride_u = src_stride_y >> 1;
    int src_stride_v = src_stride_u;
    int src_size = width * height;

    int ret = NV12ToI420(srcData, src_stride_y,
                         srcData + src_size, src_stride_y,
 
                         dstData, src_stride_y,
                         dstData+src_size, src_stride_u,
                         dstData+src_size * 5 / 4, src_stride_v,
                         width, height);
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

YL_API int convertI420ToNV12(uint8_t* srcData, int width, int height, uint8_t* dstData)
{
    LOGC("DEBUG"," --begin--");
    int src_stride_y = width;
    int src_stride_u = src_stride_y >> 1;
    int src_stride_v = src_stride_u;
    int src_size = width * height;

    int ret = I420ToNV12(srcData, src_stride_y,
                         srcData + src_size, src_stride_u,
                         srcData + src_size * 5 / 4, src_stride_v,
                         
                         dstData, src_stride_y,
                         dstData + src_size, src_stride_y,
                         width, height);
    LOGC("DEBUG"," --end ret = %d ",ret);
    return ret;
}

