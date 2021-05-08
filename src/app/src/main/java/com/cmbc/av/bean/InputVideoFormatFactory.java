package com.cmbc.av.bean;

public class InputVideoFormatFactory {
    public static String SOURCE_FORMAT_IMG_NV21   = "nv21";
    public static String SOURCE_FORMAT_VIDEO_H264 = "h264";

    public static InputVideoFormat create(String format) {
        if (SOURCE_FORMAT_VIDEO_H264.equals(format)) {
            return new InputVideoFormat(-1, 201);
        } else if (SOURCE_FORMAT_IMG_NV21.equals(format)) {
            return new InputVideoFormat(17, 8);
        }
        return null;
    }

    public static class InputVideoFormat {
        public int sourceFormat;
        public int inputVideoFormat;

        public InputVideoFormat(int sourceFormat, int inputVideoFormat) {
            this.sourceFormat = sourceFormat;
            this.inputVideoFormat = inputVideoFormat;
        }

    }
}

