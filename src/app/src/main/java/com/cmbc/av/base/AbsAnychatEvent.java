package com.cmbc.av.base;

import android.os.Build;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.cmbc.av.bean.AnychatVideoParams;
import com.cmbc.av.bean.InputVideoFormatFactory;
import com.cmbc.av.utils.LogUtils;
import com.cmbc.av.utils.ViewUtils;

public abstract class AbsAnychatEvent<CONTROL extends AbsControl, ANYCHAT extends AbsAnychatEvent> implements IAnychatEvent<CONTROL, ANYCHAT> {
    public String         TAG = getAnychat().getClass().getSimpleName();
    public CONTROL        mCONTROL;
    public AnyChatCoreSDK mAnyChatCoreSDK;
    //    public int            videoWidth = 480, videoHeight = 640;
//        public int            videoWidth = 1080, videoHeight = 1920;
//        public int            videoWidth = 1080, videoHeight = 2280;
    public int            videoWidth, videoHeight;
    public int phoneHeight, phoneWidth, phoneDensityDpi;
    public  AnychatVideoParams                       mAnychatVideoParams;
    public  boolean                                  atRoomState = false;
    private InputVideoFormatFactory.InputVideoFormat mInputVideoFormat;


    public AbsAnychatEvent(CONTROL CONTROL) {
        mCONTROL = CONTROL;
        videoHeight = phoneHeight = (int) ViewUtils.getPhoneHeight(CONTROL.mVIEW);
        videoWidth = phoneWidth = (int) ViewUtils.getPhoneWidth(CONTROL.mVIEW);
        phoneDensityDpi = ViewUtils.getDensityDpi(CONTROL.getView());
        mAnychatVideoParams = new AnychatVideoParams("默认", videoWidth, videoHeight);
//        mAnychatVideoParams = new AnychatVideoParams("默认", 640, 480);
        init();
    }

    public void setInputVideoFormat(String inputVideoSourceFormat) {
        this.mInputVideoFormat = InputVideoFormatFactory.create(inputVideoSourceFormat);
    }


    @Override
    public CONTROL getControl() {
        return mCONTROL;
    }

    public AnyChatCoreSDK getAnyChatCoreSDK() {
        return mAnyChatCoreSDK;
    }

    public int init() {
        mAnyChatCoreSDK = AnyChatCoreSDK.getInstance(mCONTROL.getView().getApplication());
        int code = 0;
        code = mAnyChatCoreSDK.InitSDK(Build.VERSION.SDK_INT, 0);
        if (code != 0) {
            return code;
        }
        initConfig();
        return code;
    }

    public void initConfig() {
        mAnyChatCoreSDK.SetBaseEvent(this);
        // 获取sdkbuild时间
        String sdkBuildTime = mAnyChatCoreSDK.GetSDKBuildTime();
        int sdkMainVersion = mAnyChatCoreSDK.GetSDKMainVersion();
        int sdkSubVersion = mAnyChatCoreSDK.GetSDKSubVersion();
        int param1 = mAnyChatCoreSDK.GetSDKOptionInt(1);
        String version = sdkMainVersion + "." + sdkSubVersion;
        LogUtils.dTag(TAG, "version = " + version);
        LogUtils.dTag(TAG,
                "AVCoreBuildTime = " + sdkBuildTime + ",AVCoreMainVersion = " + sdkMainVersion
                        + ",AVCoreSubVersion = " + sdkSubVersion + ",param1 = " + param1);
    }

    public void setOrientationSensor() {
        mAnyChatCoreSDK.mSensorHelper.InitSensor(getControl().getView());
    }

    public int setAudioConfig() {
        // 下面四条属性 服务端跟客户端统一的
        // 音频噪音抑制控制（参数为：int型：1打开，0关闭）
        int code = 0;
        code = AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_NSCTRL, 1);
        if (code != 0) {
            return code;
        }
        // 音频回音消除控制（参数为：int型：1打开，0关闭）
        code = AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL, 1);
        if (code != 0) {
            return code;
        }
        // 音频自动增益控制（参数为：int型：1打开，0关闭）
        code = AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_AGCCTRL, 1);
        if (code != 0) {
            return code;
        }
        // 音频静音检测控制（参数为：int型：1打开，0关闭）
        code = AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_VADCTRL, 0);
        if (code != 0) {
            return code;
        }
        // 音频回声消除水平参数设置（参数为int型，0 - 4，默认为4，值越大回声消除能力越强）
        code = AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOLEVEL, 4);
        if (code != 0) {
            return code;
        }
        // 设置关闭anychat自动重连
        code = AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_NETWORK_AUTORECONNECT, 0);
        if (code != 0) {
            return code;
        }
        return code;
    }

    public void setVideoConfig() {
        // 设置本地视频采集分辨率
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL, mAnychatVideoParams.getWidth());
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL, mAnychatVideoParams.getHeight());
        // 设置本地视频编码的码率
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, mAnychatVideoParams.getKbs());
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, 400 * 1000);
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, 4000 * 1000);
//        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL, 100*1000);
        // 设置本地视频编码的帧率
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL, mAnychatVideoParams.getFps());
        // 设置本地视频关键帧
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL, mAnychatVideoParams.getGop());
        // 预设参数
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL, mAnychatVideoParams.getSpyscs());
        // 设置本地视频编码的质量
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL, mAnychatVideoParams.getSpzl());

        // 让视频参数生效
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM, 1);

    }

    public int writeAVLog(String msg) {
        return /*AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, "AVP_LOG --> " + msg)*/0;
    }

    public int setInputDataFormat() {

        setVideoConfig();
        int code = 0;
        //开启外部视频数据输入
        writeAVLog(" BRAC_SO_CORESDK_EXTVIDEOINPUT 开启， code = " + AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_CORESDK_EXTVIDEOINPUT, 1));
        //设置外部视频数据格式
        code = AnyChatCoreSDK.SetInputVideoFormat(mInputVideoFormat.inputVideoFormat, mAnychatVideoParams.getWidth(),
                mAnychatVideoParams.getHeight(), mAnychatVideoParams.getFps(), 0);
        writeAVLog("setformatcode = " + code);
        if (code != 0) {
            return code;
        }
        return 0;
    }

    public int inputVideoData(byte[] data) {
        int code = AnyChatCoreSDK.InputVideoData(data, data.length, 0);
        LogUtils.dTag(TAG, "InputVideoData = " + code + ",dataLength = " + data.length);
        writeAVLog("InputVideoData = " + code + ",dataLength = " + data.length);
        if (code != 0) {
            setInputDataFormat();
        }
        return code;
    }

    public void release() {
        mAnyChatCoreSDK.LeaveRoom(-1);
        mAnyChatCoreSDK.Logout();
        mAnyChatCoreSDK.removeEvent(this);
        mAnyChatCoreSDK.Release();
    }

    public int getSourceFormat() {
        return mInputVideoFormat.sourceFormat;
    }
}
