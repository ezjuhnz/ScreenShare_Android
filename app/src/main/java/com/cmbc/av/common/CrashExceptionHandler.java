package com.cmbc.av.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.cmbc.av.SDKConfig;
import com.cmbc.av.utils.DatetimeUtil;
import com.cmbc.av.utils.LogUtils;
import com.cmbc.av.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author Jycainiao
 */
public class CrashExceptionHandler implements UncaughtExceptionHandler {
    /**
     * 捕捉到的错误写入文件内容是否加密
     */
    private static final boolean ENCRYPT_STATE = false;

    private static final String TAG = "CrashHandler";

    /**
     * 使用次数，如果使用次数为0，置空对象
     */
    public static int used_count = 0;
    // 用来存储设备信息和异常信息
    private static                Map<String, String>      infos           = new HashMap<String, String>();
    // 系统默认的UncaughtException处理类
    private                       UncaughtExceptionHandler mDefaultHandler = null;
    // CrashHandler实例
    private /* static volatile */ CrashExceptionHandler    instance        = null;
    // 程序的Context对象
    private                       Context                  mContext;

    /**
     * 保证只有一个CrashHandler实例
     */
    public CrashExceptionHandler() {
        instance = this;
        used_count++;
        LogUtils.dTag(TAG, "异常捕获初始化次数:" + used_count);
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public static Map<String, String> collectDeviceInfo(Context ctx)
            throws MyThrowableCommon.CollectDeviceInfoException {
        try {
            LogUtils.dTag(TAG, "收集设备信息");
            String currentTime = DatetimeUtil.getCurrentDatetime();
            infos.put("currentTime", currentTime);
            try {
                PackageManager pm = ctx.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
                if (pi != null) {
                    String versionName = pi.versionName == null ? "null" : pi.versionName;
                    String versionCode = pi.versionCode + "";
                    infos.put("versionName", versionName);
                    infos.put("versionCode", versionCode);
                }
            } catch (NameNotFoundException e) {
                LogUtils.dTag(TAG, "an error occured when collect package info", e);
            }
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    if (!TextUtils.isEmpty(field.getName()) && field.getName().contains("ABI")) {
                        try {
                            infos.put(field.getName(), Arrays.toString((String[]) field.get(null)));
                        } catch (Exception e) {
                            infos.put(field.getName(), field.get(null).toString());
                        }
                    } else {
                        infos.put(field.getName(), field.get(null).toString());
                    }
                } catch (Exception e) {
                    LogUtils.dTag(TAG, "an error occured when collect crash info", e);
                }
            }
            LogUtils.dTag(TAG, "收集设备信息完成");
            return infos;
        } catch (Exception e) {
            throw new MyThrowableCommon.CollectDeviceInfoException("手机设备信息捕获异常").setException(e);
        }
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        LogUtils.dTag(TAG, "记录app的异常捕获");
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        LogUtils.dTag(TAG, "设置自定义异常捕获");
    }

    public void release() {
        used_count--;
        if (mDefaultHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
            LogUtils.dTag(TAG, "设置异常捕获为app的异常捕获");
            instance = null;
        }
        if (mContext != null) {
            mContext = null;
        }
        if (used_count == 0) {
            LogUtils.dTag(TAG, "异常捕获对象全部释放成功");
        } else {
            LogUtils.dTag(TAG, "异常捕获对象正在使用中,占用资源：" + used_count);
        }
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 打印日志
        LogUtils.eTag(TAG, ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            LogUtils.dTag(TAG, "系统处理异常");
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            LogUtils.dTag(TAG, "自定义处理异常");
            restart();
        }
    }

    private void restart() {
        LogUtils.dTag(TAG, "重启app");
//        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent);
//        System.exit(1);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        LogUtils.dTag(TAG, "错误捕获到");
        LogUtils.eTag(TAG, "自定义异常捕获对象捕获到的异常", ex);
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                ToastUtils.showLong("很抱歉,程序出现异常,即将退出.");
                Looper.loop();
            }
        }.start();
        // 收集设备参数信息
        try {
            collectDeviceInfo(mContext);
        } catch (MyThrowableCommon.CollectDeviceInfoException e) {
            LogUtils.eTag(TAG, e);
        }
        // 保存日志文件
        String filePath = saveCrashInfo2File(ex);
        LogUtils.eTag(TAG, "异常文件保存位置：" + filePath);
        // 上传日志文件
        uploadCrashInfoLog(filePath);
        return true;
    }

    private void showDialog(final Context context) {
        final Dialog dialog;
        AlertDialog.Builder buder = new AlertDialog.Builder(context);
        buder.setTitle("温馨提示");
        buder.setMessage("由于发生了一个未知错误，应用已关闭，我们对此引起的不便表示抱歉！" + "您可以将错误信息上传到我们的服务器，帮助我们尽快解决该问题，谢谢！");
        buder.setPositiveButton("上传", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 上传处理---我这里没写，大家根据实际情况自己补上，我这里是一个Toast提示，提示内容就是我们要上传的信息
                ToastUtils.showLong("捕获到异常");
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            // mDefaultHandler.uncaughtException(thread, ex);
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((Activity) mContext).moveTaskToBack(true);
                                }
                            });
                        } catch (InterruptedException e) {
                        }
                        // 退出程序
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }).start();

            }
        });
        buder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });

        dialog = buder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕其他地方，dialog不消失
        dialog.setCancelable(false);// 设置点击返回键和HOme键，dialog不消失
        dialog.show();

    }

    /**
     * 上传文件到服务器
     * 待实现
     *
     * @param filePath
     */
    private void uploadCrashInfoLog(String filePath) {
        File file = new File(filePath);

    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件路径, 便于将文件传送到服务器
     */
    @SuppressWarnings("unused")
    private String saveCrashInfo2File(Throwable ex) {
        LogUtils.dTag(TAG, "保存日志");
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append("****************************************************\n");
        String currentTime = DatetimeUtil.getCurrentDate("yyyy/MM/dd HH:mm:ss");
        sb.append("出错时间（本地）：" + currentTime + "\n");
        sb.append("\n");
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        sb.append("\n");
        sb.append("****************************************************\n");
        sb.append("\n");
        FileOutputStream fos = null;
        try {
            String fileName =
                    "crashException_" + DatetimeUtil.getCurrentDatetime(DatetimeUtil.DEFAULT_FORMAT2) + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String filePath = SDKConfig.getLogDir(mContext) + "crash/";
                File dir = new File(filePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileAbsPath = filePath + fileName;
                fos = new FileOutputStream(fileAbsPath, true);
                String sbEncrypt = sb.toString();
                byte[] bytes = null;
                if (ENCRYPT_STATE == false) {// 不加密
                    bytes = sbEncrypt.getBytes();
                } else {// 加密
//                    bytes = SafeInformation.encryptMsgWithCMBC(sbEncrypt).getBytes();
                }
                fos.write(bytes);// 加密后写入文件
                LogUtils.dTag(TAG, "保存日志成功");
                return fileAbsPath;
            }
        } catch (Exception e) {
            LogUtils.dTag(TAG, "an error occured while writing file...", e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                // throw new IllegalArgumentException("流关闭失败");
                LogUtils.dTag(TAG, "流关闭失败");
            }
        }
        LogUtils.dTag(TAG, "保存日志失败");
        return null;
    }

}
