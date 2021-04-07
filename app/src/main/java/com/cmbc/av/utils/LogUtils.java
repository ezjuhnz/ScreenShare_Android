package com.cmbc.av.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;

import com.cmbc.av.SDKConfig;
import com.cmbc.av.common.MyThrowableCommon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//import android.support.annotation.IntDef;
//import android.support.annotation.IntRange;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/21
 *     desc  : utils about log
 * </pre>
 */
public final class LogUtils {
    public static final int V = Log.VERBOSE;
    public static final int D = Log.DEBUG;
    public static final int I = Log.INFO;
    public static final int W = Log.WARN;
    public static final int E = Log.ERROR;
    public static final int A = Log.ASSERT;
    private static final long logFileSize = 10 * 1024 * 1024;
    private static final char[] T = new char[]{'V', 'D', 'I', 'W', 'E', 'A'};
    private static final int FILE = 0x10;
    private static final int JSON = 0x20;
    private static final int XML = 0x30;
    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String TOP_CORNER = "┌";
    private static final String MIDDLE_CORNER = "├";
    private static final String LEFT_BORDER = "│ ";
    private static final String BOTTOM_CORNER = "└";
    private static final String SIDE_DIVIDER =
            "────────────────────────────────────────────────────────";
    private static final String MIDDLE_DIVIDER =
            "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER;
    private static final int MAX_LEN = 3000;
    private static final Format FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");
    private static final String NOTHING = "log nothing";
    private static final String NULL = "null";
    private static final String ARGS = "args";
    private static final String PLACEHOLDER = " ";
    private static final Config CONFIG = new Config();
    private static ExecutorService sExecutor;
    private static ScheduledExecutorService executorService;

    private LogUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Config getConfig() {
        return CONFIG;
    }

    private static void v(final Object... contents) {
        log(V, CONFIG.mTagStart + CONFIG.mGlobalTag, contents);
    }

    public static void vTag(final String tag, final Object... contents) {
        log(V, CONFIG.mTagStart + tag, contents);
    }

    private static void d(final Object... contents) {
        log(D, CONFIG.mTagStart + CONFIG.mGlobalTag, contents);
    }

    public static void dTag(final String tag, final Object... contents) {
        log(D, CONFIG.mTagStart + tag,  contents);
    }

    private static void i(final Object... contents) {
        log(I, CONFIG.mTagStart + CONFIG.mGlobalTag, contents);
    }

    public static void iTag(final String tag, final Object... contents) {
        log(I, CONFIG.mTagStart + tag, contents);
    }

    private static void w(final Object... contents) {
        log(W, CONFIG.mTagStart + CONFIG.mGlobalTag, contents);
    }

    public static void wTag(final String tag, final Object... contents) {
        log(W, CONFIG.mTagStart + tag, contents);
    }

    private static void e(final Object... contents) {
        log(E, CONFIG.mTagStart + CONFIG.mGlobalTag, contents);
    }

    public static void eTag(final String tag, final Object... contents) {
        log(E, CONFIG.mTagStart + tag, contents);
    }

    public static void a(final Object... contents) {
        log(A, CONFIG.mTagStart + CONFIG.mGlobalTag, contents);
    }

    public static void aTag(final String tag, final Object... contents) {
        log(A, CONFIG.mTagStart + tag, contents);
    }

    public static void file(final Object content) {
        log(FILE | D, CONFIG.mTagStart + CONFIG.mGlobalTag, content);
    }

    public static void file(@TYPE final int type, final Object content) {
        log(FILE | type, CONFIG.mTagStart + CONFIG.mGlobalTag, content);
    }

    public static void file(final String tag, final Object content) {
        log(FILE | D, CONFIG.mTagStart + tag, content);
    }

    public static void file(@TYPE final int type, final String tag, final Object content) {
        log(FILE | type, CONFIG.mTagStart + tag, content);
    }

    public static void json(final String content) {
        log(JSON | D, CONFIG.mTagStart + CONFIG.mGlobalTag, content);
    }

    public static void json(@TYPE final int type, final String content) {
        log(JSON | type, CONFIG.mTagStart + CONFIG.mGlobalTag, content);
    }

    public static void json(final String tag, final String content) {
        log(JSON | D, CONFIG.mTagStart + tag, content);
    }

    public static void json(@TYPE final int type, final String tag, final String content) {
        log(JSON | type, CONFIG.mTagStart + tag, content);
    }

    public static void xml(final String content) {
        log(XML | D, CONFIG.mTagStart + CONFIG.mGlobalTag, content);
    }

    public static void xml(@TYPE final int type, final String content) {
        log(XML | type, CONFIG.mTagStart + CONFIG.mGlobalTag, content);
    }

    public static void xml(final String tag, final String content) {
        log(XML | D, CONFIG.mTagStart + tag, content);
    }

    public static void xml(@TYPE final int type, final String tag, final String content) {
        log(XML | type, CONFIG.mTagStart + tag, content);
    }

    public static void log(final int type, final String tag, final Object... contents) {
        if (!CONFIG.mLogSwitch || (!CONFIG.mLog2ConsoleSwitch && !CONFIG.mLog2FileSwitch)) {
            return;
        }
        int type_low = type & 0x0f, type_high = type & 0xf0;
        if (type_low < CONFIG.mConsoleFilter && type_low < CONFIG.mFileFilter) {
            return;
        }
        final TagHead tagHead = processTagAndHead(tag);
        String body = processBody(type_high, contents);
        if (CONFIG.mLog2ConsoleSwitch && type_low >= CONFIG.mConsoleFilter && type_high != FILE) {
            print2Console(type_low, tagHead.tag, tagHead.consoleHead, body);
        }
        if ((CONFIG.mLog2FileSwitch || type_high == FILE) && type_low >= CONFIG.mFileFilter) {
            print2File(type_low, tagHead.tag, tagHead.fileHead + body);
        }
    }

    private static TagHead processTagAndHead(String tag) {
        if (!CONFIG.mTagIsSpace && !CONFIG.mLogHeadSwitch) {
            tag = CONFIG.mGlobalTag;
        } else {
            final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            final int stackIndex = 3 + CONFIG.mStackOffset;
            if (stackIndex >= stackTrace.length) {
                StackTraceElement targetElement = stackTrace[3];
                final String fileName = getFileName(targetElement);
                if (CONFIG.mTagIsSpace && isSpace(tag)) {
                    int index = fileName.indexOf('.');// Use proguard may not find '.'.
                    tag = index == -1 ? fileName : fileName.substring(0, index);
                }
                return new TagHead(tag, null, ": ");
            }
            StackTraceElement targetElement = stackTrace[stackIndex];
            final String fileName = getFileName(targetElement);
            if (CONFIG.mTagIsSpace && isSpace(tag)) {
                int index = fileName.indexOf('.');// Use proguard may not find '.'.
                tag = index == -1 ? fileName : fileName.substring(0, index);
            }
            if (CONFIG.mLogHeadSwitch) {
                String tName = Thread.currentThread().getName();
                final String head =
                        new Formatter()
                                .format("%s, %s.%s(%s:%d)", tName, targetElement.getClassName(),
                                        targetElement.getMethodName(), fileName,
                                        targetElement.getLineNumber())
                                .toString();
                final String fileHead = " [" + head + "]: ";
                if (CONFIG.mStackDeep <= 1) {
                    return new TagHead(tag, new String[]{head}, fileHead);
                } else {
                    final String[] consoleHead =
                            new String[Math.min(CONFIG.mStackDeep, stackTrace.length - stackIndex)];
                    consoleHead[0] = head;
                    int spaceLen = tName.length() + 2;
                    String space = new Formatter().format("%" + spaceLen + "s", "").toString();
                    for (int i = 1, len = consoleHead.length; i < len; ++i) {
                        targetElement = stackTrace[i + stackIndex];
                        consoleHead[i] = new Formatter().format("%s%s.%s(%s:%d)", space,
                                targetElement.getClassName(),
                                targetElement.getMethodName(), getFileName(targetElement),
                                targetElement.getLineNumber()).toString();
                    }
                    return new TagHead(tag, consoleHead, fileHead);
                }
            }
        }
        return new TagHead(tag, null, ": ");
    }

    private static String getFileName(final StackTraceElement targetElement) {
        String fileName = targetElement.getFileName();
        if (fileName != null) {
            return fileName;
        }
        // If name of file is null, should add
        // "-keepattributes SourceFile,LineNumberTable" in proguard file.
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1];
        }
        int index = className.indexOf('$');
        if (index != -1) {
            className = className.substring(0, index);
        }
        return className + ".java";
    }

    private static String processBody(final int type, final Object... contents) {
        String body = NULL;
        if (contents != null) {
            if (contents.length == 1) {
                Object object = contents[0];
                if (object != null) {
                    body = (object instanceof Throwable ?
                            (MyThrowableCommon.throwables2String((Throwable) object) + "")
                            : object.toString());
                }
                if (type == JSON) {
                    body = formatJson(body);
                } else if (type == XML) {
                    body = formatXml(body);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, len = contents.length; i < len; ++i) {
                    Object content = contents[i];
                    sb.append(ARGS).append("[").append(i).append("]").append(" = ")
                            .append(content == null ? NULL
                                    : (content instanceof Throwable
                                    ?
                                    String.valueOf(MyThrowableCommon.throwables2String((Throwable) content))
                                    : content.toString()))
                            .append(LINE_SEP);
                }
                body = sb.toString();
            }
        }
        return body.length() == 0 ? NOTHING : body;
    }

    private static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (Exception e) {
        }
        return json;
    }

    private static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + LINE_SEP);
        } catch (Exception e) {
        }
        return xml;
    }

    private static void print2Console(final int type, final String tag, final String[] head,
                                      final String msg) {
        if (CONFIG.mSingleTagSwitch) {
            StringBuilder sb = new StringBuilder();
            sb.append(PLACEHOLDER).append(LINE_SEP);
            if (CONFIG.mLogBorderSwitch) {
                sb.append(TOP_BORDER).append(LINE_SEP);
                if (head != null) {
                    for (String aHead : head) {
                        sb.append(LEFT_BORDER).append(aHead).append(LINE_SEP);
                    }
                    sb.append(MIDDLE_BORDER).append(LINE_SEP);
                }
                for (String line : msg.split(LINE_SEP)) {
                    sb.append(LEFT_BORDER).append(line).append(LINE_SEP);
                }
                sb.append(BOTTOM_BORDER);
            } else {
                if (head != null) {
                    for (String aHead : head) {
                        sb.append(aHead).append(LINE_SEP);
                    }
                }
                sb.append(msg);
            }
            printMsgSingleTag(type, tag, sb.toString());
        } else {
            printBorder(type, tag, true);
            printHead(type, tag, head);
            printMsg(type, tag, msg);
            printBorder(type, tag, false);
        }
    }

    private static void printBorder(final int type, final String tag, boolean isTop) {
        if (CONFIG.mLogBorderSwitch) {
            Log.println(type, tag, isTop ? TOP_BORDER : BOTTOM_BORDER);
        }
    }

    private static void printHead(final int type, final String tag, final String[] head) {
        if (head != null) {
            for (String aHead : head) {
                Log.println(type, tag, CONFIG.mLogBorderSwitch ? LEFT_BORDER + aHead : aHead);
            }
            if (CONFIG.mLogBorderSwitch) {
                Log.println(type, tag, MIDDLE_BORDER);
            }
        }
    }

    private static void printMsg(final int type, final String tag, final String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            int index = 0;
            for (int i = 0; i < countOfSub; i++) {
                printSubMsg(type, tag, msg.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }
            if (index != len) {
                printSubMsg(type, tag, msg.substring(index, len));
            }
        } else {
            printSubMsg(type, tag, msg);
        }
    }

    private static void printMsgSingleTag(final int type, final String tag, final String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            if (CONFIG.mLogBorderSwitch) {
                Log.println(type, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER);
                int index = MAX_LEN;
                for (int i = 1; i < countOfSub; i++) {
                    Log.println(type, tag,
                            PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP + LEFT_BORDER
                            + msg.substring(index, index + MAX_LEN) + LINE_SEP + BOTTOM_BORDER);
                    index += MAX_LEN;
                }
                if (index != len) {
                    Log.println(type, tag,
                            PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP + LEFT_BORDER + msg.substring(index, len));
                }
            } else {
                int index = 0;
                for (int i = 0; i < countOfSub; i++) {
                    Log.println(type, tag, msg.substring(index, index + MAX_LEN));
                    index += MAX_LEN;
                }
                if (index != len) {
                    Log.println(type, tag, msg.substring(index, len));
                }
            }
        } else {
            Log.println(type, tag, msg);
        }
    }

    private static void printSubMsg(final int type, final String tag, final String msg) {
        if (!CONFIG.mLogBorderSwitch) {
            Log.println(type, tag, msg);
            return;
        }
        StringBuilder sb = new StringBuilder();
        String[] lines = msg.split(LINE_SEP);
        for (String line : lines) {
            Log.println(type, tag, LEFT_BORDER + line);
        }
    }

    private static void printSubMsg1(final int type, final String tag, final String msg) {
        if (!CONFIG.mLogBorderSwitch) {

            return;
        }
        StringBuilder sb = new StringBuilder();
        String[] lines = msg.split(LINE_SEP);
        for (String line : lines) {
            Log.println(type, tag, LEFT_BORDER + line);
        }
    }

    private static void print2File(final int type, final String tag, final String msg) {
        Date now = new Date(System.currentTimeMillis());
        String format = FORMAT.format(now);
        String date = format.substring(0, 10);
        String time = format.substring(11);
        final String fullPath = getLogFilePath(CONFIG.mDir == null ? CONFIG.mDefaultDir :
                CONFIG.mDir, date);
        if (!createOrExistsFile(fullPath)) {
            Log.e("LogUtils", "create " + fullPath + " failed!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(time).append(T[type - V]).append("/").append(tag).append(msg).append(LINE_SEP);
        String content = "";
        // if (WSCSDKStatic.logfile_encode_switch) {
        // try {
        // content = Log2FileSafeUtils.encrypt(sb.toString());
        // } catch (Exception e) {
        // content = sb.toString();
        // }
        // } else {
        // content = sb.toString();
        // }
        content = sb.toString();
        input2File(content, fullPath);
    }

    private static String getLogFilePath(String parentPath, String date) {
        // parentPath + CONFIG.mFilePrefix + "-" + date + ".txt";
        if (createFolder(parentPath)) {
            File folder = new File(parentPath);
            if (folder.list() == null || folder.list().length <= 0) {
                return parentPath + CONFIG.mFilePrefix + "_0" + ".txt";
            } else {
                String filePath =
                        parentPath + CONFIG.mFilePrefix + "_" + (folder.list().length - 1) + ".txt";
                File file = new File(filePath);
                if (file.length() < logFileSize) {
                    return file.getAbsolutePath();
                } else {
                    return parentPath + CONFIG.mFilePrefix + "_" + folder.list().length + ".txt";
                }
            }
        }
        return "";
    }

    private static boolean createFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.getParentFile().exists()) {
            createFolder(file.getParent());
        }
        if (file.exists() && file.isDirectory()) {
            return true;
        } else {
            return file.mkdir();
        }
    }

    private static boolean createOrExistsFile(final String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            boolean isCreate = file.createNewFile();
            if (isCreate) {
                printDeviceInfo(filePath);
            }
            return isCreate;
        } catch (IOException e) {
            if (getConfig().mLogSwitch) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private static void printDeviceInfo(final String filePath) {
        String versionName = "";
        int versionCode = 0;
        try {
            PackageInfo pi =
                    SDKConfig.getApp().getPackageManager().getPackageInfo(SDKConfig.getApp().getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        String time = DatetimeUtil.getCurrentDatetime(DatetimeUtil.DEFAULT_FORMAT2);
        final String head =
                "************* Log Head ****************" + "\nDate of Log        : " + time
                + "\nDevice Manufacturer: " + Build.MANUFACTURER + "\nDevice Model       : " + Build.MODEL
                + "\nAndroid Version    : " + Build.VERSION.RELEASE + "\nAndroid SDK        : " + Build.VERSION.SDK_INT
                + "\nApp VersionName    : " + versionName + "\nApp VersionCode    : " + versionCode
                + "\n************* Log Head ****************\n\n";
        String content = "";
        // if (WSCSDKStatic.logfile_encode_switch){
        // try {
        // content = SafeUtils.encrypt(head);
        // } catch (Exception e) {
        // content = head;
        // }
        // }else {
        // content = head;
        // }
        content = head;
        input2File(content, filePath);
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static void newInput2File(final String input, final String filePath) {
        if (executorService == null) {

        }
    }


    private static void input2File(final String input, final String filePath) {
        if (sExecutor == null) {
            sExecutor = Executors.newSingleThreadExecutor();
        }
        Future<Boolean> submit = sExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(filePath, true));
                    bw.write(input);
                    return true;
                } catch (IOException e) {
                    return false;
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
        try {
            if (submit.get()) {
                return;
            }
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        Log.e("LogUtils", "log to " + filePath + " failed!");
    }

    @IntDef({V, D, I, W, E, A})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {
    }

    public static class Config {
        private String mDefaultDir;// The default storage directory of log.
        private String mDir; // The storage directory of log.
        private String mFilePrefix = "util";// The file prefix of log.
        private boolean mLogSwitch = true; // The switch of log.
        private boolean mLog2ConsoleSwitch = true; // The logcat's switch of log.
        private String mGlobalTag = null; // The global tag of log.
        private String mTagStart = null; // tag start
        private boolean mTagIsSpace = true; // The global tag is space.
        private boolean mLogHeadSwitch = true; // The head's switch of log.
        private boolean mLog2FileSwitch = false; // The file's switch of log.
        private boolean mLogBorderSwitch = true; // The border's switch of log.
        private boolean mSingleTagSwitch = true; // The single tag of log.
        private int mConsoleFilter = V; // The console's filter of log.
        private int mFileFilter = V; // The file's filter of log.
        private int mStackDeep = 1; // The stack's deep of log.
        private int mStackOffset = 0; // The stack's offset of log.

        private Config() {
            if (mDefaultDir != null) {
                return;
            }
            mDefaultDir = SDKConfig.getLogDir(SDKConfig.getApp());
        }

        public Config setLogSwitch(final boolean logSwitch) {
            mLogSwitch = logSwitch;
            return this;
        }

        public Config setConsoleSwitch(final boolean consoleSwitch) {
            mLog2ConsoleSwitch = consoleSwitch;
            return this;
        }

        public Config setTagStart(String tag) {
            if (isSpace(tag)) {
                mTagStart = "";
            } else {
                mTagStart = tag;
            }
            return this;
        }

        public Config setGlobalTag(final String tag) {
            if (isSpace(tag)) {
                mGlobalTag = "";
                mTagIsSpace = true;
            } else {
                mGlobalTag = tag;
                mTagIsSpace = false;
            }
            return this;
        }

        public Config setLogHeadSwitch(final boolean logHeadSwitch) {
            mLogHeadSwitch = logHeadSwitch;
            return this;
        }

        public Config setLog2FileSwitch(final boolean log2FileSwitch) {
            mLog2FileSwitch = log2FileSwitch;
            return this;
        }

        public Config setDir(final String dir) {
            if (isSpace(dir)) {
                mDir = null;
            } else {
                mDir = dir.endsWith(FILE_SEP) ? dir : dir + FILE_SEP;
            }
            return this;
        }

        public Config setDir(final File dir) {
            mDir = dir == null ? null : dir.getAbsolutePath() + FILE_SEP;
            return this;
        }

        public Config setFilePrefix(final String filePrefix) {
            if (isSpace(filePrefix)) {
                mFilePrefix = "util";
            } else {
                mFilePrefix = filePrefix;
            }
            return this;
        }

        public Config setBorderSwitch(final boolean borderSwitch) {
            mLogBorderSwitch = borderSwitch;
            return this;
        }

        public Config setSingleTagSwitch(final boolean singleTagSwitch) {
            mSingleTagSwitch = singleTagSwitch;
            return this;
        }

        public Config setConsoleFilter(@TYPE final int consoleFilter) {
            mConsoleFilter = consoleFilter;
            return this;
        }

        public Config setFileFilter(@TYPE final int fileFilter) {
            mFileFilter = fileFilter;
            return this;
        }

        public Config setStackDeep(@IntRange(from = 1) final int stackDeep) {
            mStackDeep = stackDeep;
            return this;
        }

        public Config setStackOffset(@IntRange(from = 0) final int stackOffset) {
            mStackOffset = stackOffset;
            return this;
        }

        @Override
        public String toString() {
            return "switch: " + mLogSwitch + LINE_SEP + "console: " + mLog2ConsoleSwitch + LINE_SEP + "tag: "
                    + (mTagIsSpace ? "null" : mGlobalTag) + LINE_SEP + "head: " + mLogHeadSwitch + LINE_SEP + "file: "
                    + mLog2FileSwitch + LINE_SEP + "dir: " + (mDir == null ? mDefaultDir : mDir) + LINE_SEP
                    + "filePrefix: " + mFilePrefix + LINE_SEP + "border: " + mLogBorderSwitch + LINE_SEP + "singleTag: "
                    + mSingleTagSwitch + LINE_SEP + "consoleFilter: " + T[mConsoleFilter - V] + LINE_SEP
                    + "fileFilter: " + T[mFileFilter - V] + LINE_SEP + "stackDeep: " + mStackDeep + LINE_SEP
                    + "mStackOffset: " + mStackOffset;
        }
    }

    private static class TagHead {
        String tag;
        String[] consoleHead;
        String fileHead;

        TagHead(String tag, String[] consoleHead, String fileHead) {
            this.tag = tag;
            this.consoleHead = consoleHead;
            this.fileHead = fileHead;
        }
    }
}
