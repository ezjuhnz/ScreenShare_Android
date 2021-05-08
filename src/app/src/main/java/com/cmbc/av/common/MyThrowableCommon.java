package com.cmbc.av.common;

import com.cmbc.av.utils.DatetimeUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class MyThrowableCommon {
    public static final String AV_SDK_CATCH_FLAG = "cmbcav_catch_exception_";

    /**
     * 网络回调异常  包含  数据处理/成功/失败/
     */
    public static class NetWorkCallbackException extends Exception {
        public NetWorkCallbackException(String... strings) {
            super(AV_SDK_CATCH_FLAG + "NetWorkCallback catch Exception \n"
                    + (strings == null ? "" : Arrays.toString(strings)));
        }

        public NetWorkCallbackException setException(Throwable throwable) {
            initCause(throwable);
            return this;
        }
    }

    /**
     * 获取设备信息异常
     */
    public static class CollectDeviceInfoException extends Exception {
        public CollectDeviceInfoException(String... strings) {
            super(AV_SDK_CATCH_FLAG + "CollectDeviceInfo catch Exception :Time = " + DatetimeUtil.getCurrentDatetime()
                    + "\n" + (strings == null ? "" : Arrays.toString(strings)));
        }

        public CollectDeviceInfoException setException(Throwable throwable) {
            initCause(throwable);
            return this;
        }
    }

    /**
     * activity生命周期异常  包含restart/start/pause/resume/stop/desdroy
     */
    public static class ActivityRunTimeException extends Exception {
        public ActivityRunTimeException(String... strings) {
            super(AV_SDK_CATCH_FLAG + "ActivityRunTime catch Exception \n"
                    + (strings == null ? "" : Arrays.toString(strings)));
        }

        public ActivityRunTimeException setException(Throwable throwable) {
            initCause(throwable);
            return this;
        }
    }

    /**
     * 网络获取cookie异常
     */
    public static class NetWorkGetCookiesException extends Exception {
        public NetWorkGetCookiesException(String... strings) {
            super(AV_SDK_CATCH_FLAG + "NetWorkGetCookies catch Exception \n"
                    + (strings == null ? "" : Arrays.toString(strings)));
        }

        public NetWorkGetCookiesException setException(Throwable throwable) {
            initCause(throwable);
            return this;
        }
    }

    /**
     * 时间转换异常
     */
    public static class DateFormatException extends Exception {
        public DateFormatException(String... strings) {
            super(AV_SDK_CATCH_FLAG + "DateFormat catch Exception \n"
                    + (strings == null ? "" : Arrays.toString(strings)));
        }

        public DateFormatException setException(Throwable throwable) {
            initCause(throwable);
            return this;
        }
    }

    /**
     * 调用方实现的sdk接口，sdk调用时捕获到异常
     */
    public static class ExceptionFromUserImplSDKMethod extends Exception {
        public ExceptionFromUserImplSDKMethod(String... strings) {
            super(AV_SDK_CATCH_FLAG + "SDKCallBackInner method catch Exception \n"
                    + (strings == null ? "" : Arrays.toString(strings)));
        }

        public ExceptionFromUserImplSDKMethod setException(Throwable throwable) {
            initCause(throwable);
            return this;
        }
    }

    /**
     * 摄像头异常
     */
    public static class GetCameraException extends Exception {
        public GetCameraException(String... strings) {
            super(AV_SDK_CATCH_FLAG + "当前还未获取摄像头 \n" + (strings == null ? "" : Arrays.toString(strings)));
        }

        public GetCameraException setException(Throwable throwable) {
            initCause(throwable);
            return this;
        }
    }

    public static StringBuffer throwables2String(Throwable... throwable) {
        if (throwable == null) {
            return new StringBuffer("  null");
        }
        StringWriter stringWriter = new StringWriter();
        StringBuffer stringBuffer = new StringBuffer();
        for (Throwable t : throwable) {
            if (t == null) {
                stringWriter.append("  null");
            } else {
                t.printStackTrace(new PrintWriter(stringWriter));
                stringWriter.flush();
                stringBuffer.append(stringWriter.toString());
            }
        }
        return stringBuffer;
    }

    /**
     * sdk捕获到的异常
     */
    public static class SDKCatchException extends Exception {
        public SDKCatchException(String... strings) {
            super(AV_SDK_CATCH_FLAG + "sdk手动catch到异常 \n" + (strings == null ? "" : Arrays.toString(strings)));
        }

        public SDKCatchException(Exception e, String... strings) {
            super(AV_SDK_CATCH_FLAG + "sdk手动catch到异常 \n" + (strings == null ? "" : Arrays.toString(strings)), e);
        }

        public SDKCatchException(String strings, Exception e) {
            super(AV_SDK_CATCH_FLAG + "sdk手动catch到异常 \n" + (strings == null ? "" : strings), e);
        }

        public SDKCatchException setException(Throwable throwable) {
            initCause(throwable);
            return this;
        }
    }
    /**
     * sdk捕获到的异常
     */
    public static class SDKInitException extends Exception {
        public SDKInitException(String... strings) {
            super(AV_SDK_CATCH_FLAG + "初始化sdk时catch到异常 \n" + (strings == null ? "" : Arrays.toString(strings)));
        }

        public SDKInitException(Exception e, String... strings) {
            super(AV_SDK_CATCH_FLAG + "初始化sdk时catch到异常 \n" + (strings == null ? "" : Arrays.toString(strings)), e);
        }

        public SDKInitException(String strings, Exception e) {
            super(AV_SDK_CATCH_FLAG + "初始化sdk时catch到异常 \n" + (strings == null ? "" : strings), e);
        }

        public SDKInitException setException(Throwable throwable) {
            initCause(throwable);
            return this;
        }
    }


}
