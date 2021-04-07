# ScreenShare_Android 
keyword: ScreenCapture, yuv, android, libyuv, color convert.
this project can capture image from screen and get yuv(NV21) data by libyuv third party library, fix color incorrect bug, such as the red transform to blue.
because the order of ARGB is constrast to the order of ARGB of libyuv definition. so another convert is necessary.
