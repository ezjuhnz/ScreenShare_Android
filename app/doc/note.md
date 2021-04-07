该文档记录截屏后将数据转成YUV420原始数据
截屏API使用MediaProjection, 通过ImageReader获取图片数据(注意:ImageReader生成的图片格式不是ARGB,虽然在ImageReader.NewInstance()中
传递了ARGB格式参数,但是通过截屏得到的数据本身就不是原始数据流,如YUV420);套用网上的原话
Generally speaking, the point of ImageReader is to give you raw access to the pixels sent to the Surface with minimal overhead, 
so attempting to have it perform color conversions doesn't make sense.

For the Camera you get to pick one of two output formats (NV21 or YV12), so pick YV12. 
That's your raw YUV data. For screen capture the output will always be RGB, 
so you need to pick RGBA_8888 (format 0x1) for your ImageReader, rather than YUV_420_888 (format 0x23).
If you need YUV for that, you will have to do the conversion yourself. 
The ImageReader gives you a series of Plane objects, not a byte[], so you will need to adapt to that.
意思就是: ImageReader是用来给你访问Surface中的像素的,而不是用来

//1. RGBAToARGB
int RGBAToARGB(const uint8_t* src_rgba, //原始RGBA bytes
   int src_stride_rgba,                 //原始RGBA数据的两行开始像素之间的距离,byte
   uint8_t* dst_argb,                   //传出参数: 目标ARGB数组
   int dst_stride_argb,                 //目标ARGB数据的两行开始像素之间的距离,byte
   int width,                           //宽
   int height);                         //高
 
                     
// 2. Convert ARGB To NV21.
LIBYUV_API
int ARGBToNV21(const uint8_t* src_argb, //原始ARGB字节流
               int src_stride_argb,		//原始数据: 两行开始像素之间的距离,byte
               uint8_t* dst_y,		//?
               int dst_stride_y,		//目标数据:两行开始像素之间的距离,byte
               uint8_t* dst_vu,		//?
               int dst_stride_vu,		//?
               int width,			//宽度
               int height);		//高度

===================================================
4,669,440 (byte)
720*1520 = 1,094,400 (pixel)
1,094,400 * 4 = 4,377,600 (byte)

4,669,440 - 4,377,600 = 291,840 (byte)
291,840 /4 = 72,960 (pixel)
72,960 / 1520 = 48 (pixel)


问题记录:
1.处理ImageReader获取的图片数据(RGBA格式),保存成bitmap(ARGB格式)后,再将bitmap写到文件中,文件中部分颜色异常
app中的颜色正常,在View中显示的颜色不正常,说明bitmap是没错的,只是在view中显示时出错了.

2.处理ImageReader获取的图片数据(RGBA格式),使用libyuv转成ARGB后再生成bitmap(ARGB),最后写到文件中,文件中颜色全部异常
说明图片格式转换出错导致颜色异常

RGBA      ARGB
F00?(红) ->F00?(蓝)
0F0?(绿) ->0F0?()

