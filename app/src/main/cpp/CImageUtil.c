/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//加入libyuv库需要的头文件

#include "libyuv.h"


#define max(x,y)  (x>y?x:y)
#define min(x,y)  (x<y?x:y)
#define y(r,g,b)  (((66 * r + 129 * g + 25 * b + 128) >> 8) + 16)
#define u(r,g,b)  (((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128)
#define v(r,g,b)  (((112 * r - 94 * g - 18 * b + 128) >> 8) + 128)
#define color(x)  ((unsigned char)((x < 0) ? 0 : ((x > 255) ? 255 : x)))

#define RGBA_YUV420SP   0x00004012
#define BGRA_YUV420SP   0x00004210
#define RGBA_YUV420P    0x00014012
#define BGRA_YUV420P    0x00014210
#define RGB_YUV420SP    0x00003012
#define RGB_YUV420P     0x00013012
#define BGR_YUV420SP    0x00003210
#define BGR_YUV420P     0x00013210

//实现打印

//定义数据类型别名
typedef unsigned int uint32;
typedef int int32;
typedef unsigned short uint16;  // NOLINT
typedef short int16;            // NOLINT
typedef unsigned char uint8;
typedef signed char int8;
/* Header for class com_cmbc_av_jni_ImageFormatUtils */

#ifndef _Included_com_cmbc_av_jni_ImageFormatUtils


#define _Included_com_cmbc_av_jni_ImageFormatUtils
#ifdef __cplusplus
extern "C" {

#endif
static int (*i420ToRgbaFunc[])(const uint8 *,int, const uint8 *,int,const uint8 *,int,uint8 *,
                               int ,int ,int )={
        I420ToABGR, I420ToRGBA, I420ToARGB, I420ToBGRA,
        I420ToRGB24, I420ToRGB565
};

static int (*rgbaToI420Func[])(const uint8 *,int,uint8 *,int,uint8 *,int ,uint8 *,int,int,int)={
        ABGRToI420, RGBAToI420, ARGBToI420, BGRAToI420,
        RGB24ToI420, RGB565ToI420
};

void swap32(uint32_t * pulValue)
{
    if (NULL == pulValue)
    {
        return;
    }

    *pulValue = ((*pulValue <<24) & 0xFF000000) |((*pulValue << 8) & 0x00FF0000) |((*pulValue >> 8) & 0x0000FF00) | ((*pulValue >> 24) & 0x000000FF);
}

int rgbaToI420(JNIEnv * env,jclass clazz,jbyteArray rgba,jint rgba_stride,
               jbyteArray yuv,jint y_stride,jint u_stride,jint v_stride,
               jint width,jint height,
               int (*func)(const uint8 *,int,uint8 *,int,uint8 *,int ,uint8 *,int,int,int)){
    size_t ySize = (size_t) (y_stride * height);
    size_t uSize = (size_t) (u_stride * height >> 1);
    jbyte * rgbaData = (*env)->GetByteArrayElements(env, rgba, JNI_FALSE);
    jbyte * yuvData = (*env)->GetByteArrayElements(env, yuv,JNI_FALSE);

    int ret = func((const uint8 *) rgbaData, rgba_stride, (uint8 *) yuvData, y_stride,
                   (uint8 *) (yuvData) + ySize, u_stride, (uint8 *) (yuvData )+ ySize + uSize,
                   v_stride, width, height);
    (*env)->ReleaseByteArrayElements(env, rgba,rgbaData,JNI_OK);
    (*env)->ReleaseByteArrayElements(env, yuv,yuvData,JNI_OK);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_testAdd
        (JNIEnv *env, jobject obj, jint i1, jint i2)
{
    return i1 + i2;
}

JNIEXPORT jbyteArray JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_getNV21
        (JNIEnv *env, jclass clz, jint jwidth, jint jheight, jintArray j_array)
{
    int i;
    int width = jwidth;
    int height = jheight;
    int size = width * height;

    jint *c_array;
    jint arr_len;
    //1. 获取数组长度
    arr_len = (*env)->GetArrayLength(env,j_array);
    //2. 根据数组长度和数组元素的数据类型申请存放java数组元素的缓冲区
    c_array = (jint*)malloc(sizeof(jint) * arr_len);
    //3. 初始化缓冲区
    memset(c_array,0,sizeof(jint)*arr_len);
    printf("arr_len = %d ", arr_len);
    //4. 拷贝Java数组中的所有元素到缓冲区中
    (*env)->GetIntArrayRegion(env,j_array,0,arr_len,c_array);
    for (i = 0; i < arr_len; i++) {

    }
    free(c_array);  //6. 释放存储数组元素的缓冲区
}

void rgba2yuv(int width,int height,unsigned char * rgb,unsigned char * yuv,int type){
    const int frameSize = width * height;
    const int yuvType=(type&0x10000)>>16;
    const int byteRgba=(type&0x0F000)>>12;
    const int rShift=(type&0x00F00)>>8;
    const int gShift=(type&0x000F0)>>4;
    const int bShift= (type&0x0000F);
    const int uIndex=0;
    const int vIndex=yuvType; //yuvType为1表示YUV420p,为0表示420sp

    int yIndex = 0;
    int uvIndex[2]={frameSize,frameSize+frameSize/4};

    unsigned char R, G, B, Y, U, V;
    unsigned int index = 0;
    for (int j = 0; j < height; j++) {
        for (int i = 0; i < width; i++) {
            index = j * width + i;

            R = rgb[index*byteRgba+rShift]&0xFF;
            G = rgb[index*byteRgba+gShift]&0xFF;
            B = rgb[index*byteRgba+bShift]&0xFF;

            Y = y(R,G,B);
            U = u(R,G,B);
            V = v(R,G,B);

            yuv[yIndex++] = color(Y);
            if (j % 2 == 0 && index % 2 == 0) {
                yuv[uvIndex[uIndex]++] =color(U);
                yuv[uvIndex[vIndex]++] =color(V);
            }
        }
    }
}


JNIEXPORT jint JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_rgbaToYuv
        (JNIEnv *env, jclass clz, jbyteArray rgba,jbyteArray yuv, jint jwidth, jint jheight)
{

    unsigned char* rgbaBuffer = (*env)->GetByteArrayElements(env,rgba,0);

    for(int i = 0; i < 10; i++)
    {
        rgbaBuffer[i] = i;
    }
    unsigned char* yuvBuffer = (*env)->GetByteArrayElements(env,yuv,0);
    unsigned char * cYuv=(unsigned char *)yuvBuffer;
    //rgba2yuv(jwidth,jheight,rgbaBuffer,cYuv,type);
    rgba2yuv(jwidth,jheight,rgbaBuffer,cYuv,RGBA_YUV420SP);
    (*env)->ReleaseByteArrayElements(env,rgba, rgbaBuffer, 0);
    (*env)->ReleaseByteArrayElements(env,yuv, yuvBuffer, 0);
    return 9;
}

JNIEXPORT void JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_ARGBToNV21
        (JNIEnv *env, jclass clz, jbyteArray src_argb, jint src_stride,
                jint width, jint height, jbyteArray ybuffer, jbyteArray uvbuffer)
{
    uint8_t* srcFrame = (uint8_t*) (*env)->GetByteArrayElements(env, src_argb, 0);

    uint8_t* dst_y=(uint8_t*) (*env)->GetByteArrayElements(env, ybuffer, 0);
    uint8_t* dst_uv=(uint8_t*) (*env)->GetByteArrayElements(env, uvbuffer, 0);

/*
 * int ARGBToNV21(const uint8_t* src_argb, //原始ARGB字节流
               int src_stride_argb,		//原始数据: 两行开始像素之间的距离,byte
               uint8_t* dst_y,		//传出参数Y分量的byte数组
               int dst_stride_y,		//目标数据Y分量:两行开始像素之间的距离,byte
               uint8_t* dst_vu,		//传出参数:UV分量的byte数组
               int dst_stride_vu,		//目标数据UV分量:两行开始像素之间的距离,byte
               int width,			//宽度
               int height);		//高度

 */
    //将RGBA 转成 ARGB
    /*
    int ARGBToBGRA(const uint8_t* src_argb,
                   int src_stride_argb,
                   uint8_t* dst_bgra,
                   int dst_stride_bgra,
                   int width,
                   int height);
    */
    uint8_t* dst_bgra = malloc(width*height*4);
    //ARGBToBGRA(srcFrame, src_stride, dst_bgra, src_stride, width, height);
    ARGBToABGR(srcFrame, src_stride, dst_bgra, src_stride, width, height);
    //ARGBToRGBA(srcFrame, src_stride, dst_bgra, src_stride, width, height);
    ARGBToNV21(dst_bgra,
            src_stride,
            dst_y,
            width,
            dst_uv,
            width,
            width,
            height);
    //remember release
    (*env)->ReleaseByteArrayElements(env, src_argb, (jbyte*)srcFrame, 0);
    (*env)->ReleaseByteArrayElements(env, ybuffer, (jbyte*)dst_y, 0);
    (*env)->ReleaseByteArrayElements(env, uvbuffer, (jbyte*)dst_uv, 0);
    if(dst_bgra != NULL)
    {
        free(dst_bgra);
        dst_bgra = NULL;
    }
}

JNIEXPORT void JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_ARGBIntToNV21
        (JNIEnv *env, jclass clz, jintArray rgba, jint src_stride, jint width, jint height, jbyteArray ybuf, jbyteArray uvbuf)
{
    uint32_t* srcFrame = (uint32_t*) (*env)->GetByteArrayElements(env, rgba, 0);

    uint8_t* dst_y=(uint8_t*) (*env)->GetByteArrayElements(env, ybuf, 0);
    uint8_t* dst_uv=(uint8_t*) (*env)->GetByteArrayElements(env, uvbuf, 0);

    //先将srcFrame进行大小端转换
    swap32(srcFrame);
    ARGBToNV21((uint8_t*)srcFrame,
               src_stride,
               dst_y,
               width,
               dst_uv,
               width,
               width,
               height);
    //remember release
    (*env)->ReleaseByteArrayElements(env, rgba, (jbyte*)srcFrame, 0);
    (*env)->ReleaseByteArrayElements(env, ybuf, (jbyte*)dst_y, 0);
    (*env)->ReleaseByteArrayElements(env, uvbuf, (jbyte*)dst_uv, 0);
}

JNIEXPORT void JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_RGBAToARGB
        (JNIEnv *env, jclass clz, jbyteArray src_rgba, jint src_stride,
                jbyteArray dst_argb, jint dst_stride, jint width, jint height)
{
    uint8_t* srcFrame = (uint8_t*) (*env)->GetByteArrayElements(env, src_rgba, 0);
    uint8_t* dstFrame=(uint8_t*) (*env)->GetByteArrayElements(env, dst_argb, 0);
    //调用libyuv将RGBA转成ARGB
    RGBAToARGB(srcFrame,src_stride,dstFrame,dst_stride,width,height);

    (*env)->ReleaseByteArrayElements(env, src_rgba, (jbyte*)srcFrame, 0);
    (*env)->ReleaseByteArrayElements(env, dst_argb, (jbyte*)dstFrame, 0);

}

int i420ToRgba(JNIEnv * env,jclass clazz,jbyteArray yuv,jint y_stride,jint u_stride,jint v_stride,
               jbyteArray rgba,jint rgba_stride,jint width,jint height,
               int (*func)(const uint8 *,int, const uint8 *,int,const uint8 *,int,uint8 *,
                           int ,int ,int )){
    size_t ySize = (size_t) (y_stride * height);
    size_t uSize = (size_t) (u_stride * height >> 1);
    jbyte * rgbaData = (*env)->GetByteArrayElements(env, rgba, JNI_FALSE);
    jbyte * yuvData = (*env)->GetByteArrayElements(env, yuv, JNI_FALSE);
    int ret = func((const uint8 *) yuvData, y_stride, (uint8 *) yuvData + ySize, u_stride,
                 (uint8 *) (yuvData)+ ySize + uSize, v_stride, (uint8 *) (rgbaData),
                 rgba_stride, width, height);
    (*env)->ReleaseByteArrayElements(env, rgba,rgbaData,JNI_OK);
    (*env)->ReleaseByteArrayElements(env, yuv,yuvData,JNI_OK);
    return ret;
}

JNIEXPORT jint JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_I420ToRgba
        (JNIEnv *env, jclass clazz, jint type, jbyteArray yuv, jbyteArray rgba, jint width, jint height)
{
    uint8 cType = (uint8) (type & 0x0F);
    int rgba_stride = ((type & 0xF0) >> 4)*width;
    int y_stride = width;
    int u_stride = width>>1;
    int v_stride = u_stride;
    return i420ToRgba(env,clazz,yuv,y_stride,u_stride,v_stride,rgba,rgba_stride,width,height,i420ToRgbaFunc[cType]);
}

/*
 * RGB to I420 ,this method is migrate from java, performance not good
 */
JNIEXPORT void JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_rgbToYuv
        (JNIEnv *env, jclass clz, jbyteArray rgba, jint width, jint height, jbyteArray yuv)
{
    int frameSize = width * height;

    int yIndex = 0;
    int uIndex = frameSize;
    int vIndex = frameSize + frameSize/4;
    uint8_t* rgbaData = (uint8_t*) (*env)->GetByteArrayElements(env, rgba, 0);
    uint8_t* yuvData = (uint8_t*) (*env)->GetByteArrayElements(env, yuv, 0);

    int R, G, B, Y, U, V;
    int index = 0;
    for (int j = 0; j < height; j++) {
        for (int i = 0; i < width; i++) {
            index = j * width + i;
            if(rgbaData[index*4] > 127 || (rgbaData[index*4] < -128)){
                //LOGD("color","-->" + rgba[index*4]);
            }
            R = rgbaData[index*4] & 0xFF;
            G = rgbaData[index*4+1] & 0xFF;
            B = rgbaData[index*4+2] & 0xFF;

            Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
            U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
            V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

            yuvData[yIndex++] = (uint8_t) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
            if (j % 2 == 0 && index % 2 == 0) {
                yuvData[uIndex++] = (uint8_t) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                yuvData[vIndex++] = (uint8_t) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
            }
        }
    }
    (*env)->ReleaseByteArrayElements(env, rgba,rgbaData,JNI_OK);
    (*env)->ReleaseByteArrayElements(env, yuv,yuvData,JNI_OK);
}

/*
 * convert rgba to I420, because there is no function to convert rgba to NV21 directly.
 * so we have to convert to I420 firstly and then convert I420 to NV21.
 */
JNIEXPORT void JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_rgbaToI420
        (JNIEnv *env, jclass clz, jbyteArray rgba, jint src_stride, jint width, jint height, jint dst_stride,
                jbyteArray ybuf, jbyteArray ubuf, jbyteArray vbuf)
{
    uint8_t* srcFrame = (uint8_t*) (*env)->GetByteArrayElements(env, rgba, 0);

    uint8_t* dstYbuf =(uint8_t*) (*env)->GetByteArrayElements(env, ybuf, 0);
    uint8_t* dstUbuf =(uint8_t*) (*env)->GetByteArrayElements(env, ubuf, 0);
    uint8_t* dstVbuf =(uint8_t*) (*env)->GetByteArrayElements(env, vbuf, 0);

/*
 * int RGBAToI420(const uint8_t* src_rgba,
               int src_stride_rgba,
               uint8_t* dst_y,
               int dst_stride_y,
               uint8_t* dst_u,
               int dst_stride_u,
               uint8_t* dst_v,
               int dst_stride_v,
               int width,
               int height);
 */

    RGBAToI420(srcFrame,
               src_stride,
               dstYbuf,
               src_stride,
               dstUbuf,
               src_stride>>1,
               dstVbuf,
               src_stride>>1,
               width,
               height);
    //remember release
    (*env)->ReleaseByteArrayElements(env, rgba, (jbyte*)srcFrame, 0);
    (*env)->ReleaseByteArrayElements(env, ybuf, (jbyte*)dstYbuf, 0);
    (*env)->ReleaseByteArrayElements(env, ubuf, (jbyte*)dstUbuf, 0);
    (*env)->ReleaseByteArrayElements(env, vbuf, (jbyte*)dstVbuf, 0);
}

JNIEXPORT jint JNICALL Java_com_cmbc_av_jni_ImageFormatUtils_RgbaToI420
        (JNIEnv *env, jclass clz, jint type, jbyteArray rgba, jbyteArray yuv, jint width, jint height)
{
    uint8 cType = (uint8) (type & 0x0F); //0x01001040 & 0x0F = 0
    int rgba_stride = ((type & 0xF0) >> 4)*width;// 4*width
    int y_stride = width;
    int u_stride = width>>1;
    int v_stride = u_stride;
    return rgbaToI420(env,clz,rgba,rgba_stride,yuv,y_stride,u_stride,v_stride,width,height,rgbaToI420Func[cType]);
}

#ifdef __cplusplus
}
#endif
#endif