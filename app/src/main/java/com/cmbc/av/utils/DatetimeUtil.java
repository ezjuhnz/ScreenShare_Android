package com.cmbc.av.utils;

import com.cmbc.av.common.MyThrowableCommon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author JYcainiao
 */
public class DatetimeUtil {
    private static final String DEFAULT_FORMAT1 = "yyyy-MM-dd HH:mm:ss";
    // 默认时间格式1 年-月-日 时:分:秒
    public static final String DEFAULT_FORMAT2 = "yyyy-MM-dd";
    // 默认时间格式2 年-月-日
    public static final String DEFAULT_FORMAT3 = "HH:mm:ss";
    // 默认时间格式3 时:分:秒
    public static final long DAY_TIME_MILLIS = 24 * 60 * 60 * 1000;

    // 默认时间格式3 时:分:秒.毫秒
    private static final String DEFAULT_FORMAT4 = "HH:mm:ss.SSS";
    private static final String TAG = DatetimeUtil.class.getName();
    // 一天的毫秒数

    /**
     * 获得当前时间的字符串
     *
     * @param format 格式化字符串，如"yyyy-MM-dd HH:mm:ss"
     * @return String类型的当前日期时间
     */
    public static String getCurrentDatetime(String format) {
        String currentDateTime = "";
        if (null == format || "".equals(format)) {
            return null;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Calendar calendar = new GregorianCalendar();
            currentDateTime = simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            LogUtils.dTag(TAG, "时间格式化失败");
            Exception exception = new MyThrowableCommon.DateFormatException().setException(e);
            LogUtils.eTag(TAG, exception);
        }
        return currentDateTime;
    }

    /**
     * 得到当前时间的格式化时间
     *
     * @return 年-月-日 时:分:秒 格式的当前时间
     */
    public static String getCurrentDatetime() {
        return getCurrentDatetime(DEFAULT_FORMAT1);
    }

    public static String getCurrentTimeSSS() {
        return getCurrentDatetime(DEFAULT_FORMAT4);
    }

    /**
     * 获得指定时间的date格式
     *
     * @param date   指定时间Date类型
     * @param format 格式化字符串
     * @return 格式化完后的时间
     */
    public static String getDate(Date date, String format) {
        String currentDate = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            currentDate = formatter.format(date);
        } catch (Exception e) {
            // throw new IllegalArgumentException("格式化时间出错");
            LogUtils.dTag(TAG, "格式化时间出错");
            Exception exception = new MyThrowableCommon.DateFormatException().setException(e);
            LogUtils.eTag(TAG, exception);
        }
        return currentDate;

    }

    /**
     * 获得指定毫秒数，指定格式的格式化时间
     *
     * @param time   毫秒数
     * @param format 格式化字符串
     * @return 格式化后的时间
     */
    public static String getDate(long time, String format) {
        Date date = new Date(time);
        return getDate(date, format);
    }

    /**
     * 获得当前时间指定格式化字符串的格式化时间
     *
     * @param format 格式化字符串
     * @return 格式化后的时间
     */
    public static String getCurrentDate(String format) {
        return getDate(System.currentTimeMillis(), format);
    }

    /**
     * 获取指定毫秒值的时分秒字符串
     *
     * @param time
     * @return
     */
    public static String getTimeString(long time) {
        int hour = (int) ((time / 1000) / 60 / 60);
        int secondnd = (int) ((time / 1000 - hour * 60 * 60) / 60);
        int million = (int) ((time / 1000) % 60);
        String hourS = "";
        String secondndS = "";
        String millionS = "";
        if (hour < 10) {
            hourS = "0" + hour;
        } else {
            hourS = "" + hour;
        }
        if (secondnd < 10) {
            secondndS = "0" + secondnd;
        } else {
            secondndS = "" + secondnd;
        }
        if (million < 10) {
            millionS = "0" + million;
        } else {
            millionS = "" + million;
        }
        return hourS + ":" + secondndS + ":" + millionS;
    }

    /**
     * 时分秒字符串转换为毫秒数
     *
     * @param timeString
     * @return 毫秒数
     */
    public static long timeString2Long(String timeString) {
        String[] split = timeString.split(":");
        int hour = Integer.parseInt(split[0]);
        int secondnd = Integer.parseInt(split[1]);
        int million = Integer.parseInt(split[2]);
        return hour * 60 * 60 + secondnd * 60 + million;
    }
}
