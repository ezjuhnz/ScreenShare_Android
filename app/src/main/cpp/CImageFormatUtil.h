//
// Created by acer on 2021/4/7.
//

#ifndef SCREENSHARE_ANDROID_CIMAGEFORMATUTIL_H
#define SCREENSHARE_ANDROID_CIMAGEFORMATUTIL_H
void ConvertARGBToNV21(unsigned char *src_argb, int src_stride_argb, unsigned char  *dst_y,
                       int dst_stride_y, unsigned char  *dst_vu, int dst_stride_vu, int width, int height);
#endif //SCREENSHARE_ANDROID_CIMAGEFORMATUTIL_H
