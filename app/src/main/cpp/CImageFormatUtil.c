//
// Created by acer on 2021/4/7.
//
#include <stdlib.h>
#include "libyuv.h"
void ConvertARGBToNV21(unsigned char *src_argb, int src_stride_argb, unsigned char  *dst_y,
        int dst_stride_y, unsigned char  *dst_vu, int dst_stride_vu, int width, int height)
{
    unsigned char* dst_bgra = malloc(width*height*4);
    //ARGBToBGRA(srcFrame, src_stride, dst_bgra, src_stride, width, height);
    ARGBToABGR(src_argb, src_stride_argb, dst_bgra, src_stride_argb, width, height);
    //ARGBToRGBA(srcFrame, src_stride, dst_bgra, src_stride, width, height);
    ARGBToNV21(dst_bgra,
               src_stride_argb,
               dst_y,
               width,
               dst_vu,
               width,
               width,
               height);
    //remember release

    if(dst_bgra != NULL)
    {
        free(dst_bgra);
        dst_bgra = NULL;
    }
}

