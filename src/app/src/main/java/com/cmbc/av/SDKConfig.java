package com.cmbc.av;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.cmbc.av.utils.DatetimeUtil;

public class SDKConfig {
    private static Application sApplication;
    public static void init(Application application){
        sApplication = application;
    }
    public static Application getApp(){
        return sApplication;
    }
    /**
     * 获取log日志文件夹
     *
     * @param context
     * @return
     */
    public static String getLogDir(Context context) {
        String logDir;
        final String FILE_SEP = System.getProperty("file.separator");
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.getExternalCacheDir() != null) {
            logDir = context.getExternalCacheDir() + FILE_SEP + "wsc_sdk_log" + FILE_SEP
                    + DatetimeUtil.getCurrentDatetime("yyyy-MM-dd") + FILE_SEP;
        } else {
            logDir = context.getCacheDir() + FILE_SEP + "wsc_sdk_log" + FILE_SEP
                    + DatetimeUtil.getCurrentDatetime("yyyy-MM-dd") + FILE_SEP;
        }
        return logDir;
    }
}
