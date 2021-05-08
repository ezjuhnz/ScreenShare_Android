package com.bairuitech.anychat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import com.cmbc.av.utils.LogUtils;

import java.lang.ref.WeakReference;


public class AnyChatCoreSDK {
    private static int HANDLE_TYPE_CORESDKEVENT = 10;
    private static int HANDLE_TYPE_NOTIFYMSG = 1;
    private static int HANDLE_TYPE_OBJECTEVENT = 9;
    private static int HANDLE_TYPE_RECORD = 8;
    private static int HANDLE_TYPE_SDKFILTER = 6;
    private static int HANDLE_TYPE_TEXTMSG = 2;
    private static int HANDLE_TYPE_TRANSBUF = 4;
    private static int HANDLE_TYPE_TRANSBUFEX = 5;
    private static int HANDLE_TYPE_TRANSFILE = 3;
    private static int HANDLE_TYPE_VIDEOCALL = 7;
    private static AnyChatCoreSDK mAnyChat = null;
    public static AnyChatAudioHelper mAudioHelper = new AnyChatAudioHelper();
    public static AnyChatCameraHelper mCameraHelper = new AnyChatCameraHelper();
    static MainHandler mHandler = null;
    AnyChatBaseEvent baseEvent;
    AnyChatCoreSDKEvent coresdkEvent;
    AnyChatDataEncDecEvent encdecEvent;
    public AnyChatSensorHelper mSensorHelper = new AnyChatSensorHelper();
    public AnyChatVideoHelper mVideoHelper = new AnyChatVideoHelper();
    AnyChatObjectEvent objectEvent;
    AnyChatPrivateChatEvent privateChatEvent;
    AnyChatRecordEvent recordEvent;
    AnyChatStateChgEvent stateChgEvent;
    AnyChatStreamCallBack streamcbEvent;
    AnyChatTextMsgEvent textMsgEvent;
    AnyChatTransDataEvent transDataEvent;
    AnyChatUserInfoEvent userInfoEvent;
    AnyChatVideoCallEvent videoCallEvent;

    static class MainHandler extends Handler {
        WeakReference<AnyChatCoreSDK> mAnyChat;

        public MainHandler(AnyChatCoreSDK anychat) {
            this.mAnyChat = new WeakReference(anychat);
        }

        public MainHandler(Looper L) {
            super(L);
        }

        public void handleMessage(Message nMsg) {
            AnyChatCoreSDK anychat = (AnyChatCoreSDK) this.mAnyChat.get();
            if (anychat != null) {
                super.handleMessage(nMsg);
                Bundle tBundle = nMsg.getData();
                int type = tBundle.getInt("HANDLETYPE");
                if (type == AnyChatCoreSDK.HANDLE_TYPE_NOTIFYMSG) {
                    anychat.OnNotifyMsg(tBundle.getInt("MSG"), tBundle.getInt("WPARAM"), tBundle.getInt("LPARAM"));
                } else if (type == AnyChatCoreSDK.HANDLE_TYPE_TEXTMSG) {
                    int fromid = tBundle.getInt("FROMUSERID");
                    int toid = tBundle.getInt("TOUSERID");
                    int secret = tBundle.getInt("SECRET");
                    String message = tBundle.getString("MESSAGE");
                    if (anychat.textMsgEvent != null) {
                        anychat.textMsgEvent.OnAnyChatTextMessage(fromid, toid, secret != 0, message);
                    }
                } else if (type == AnyChatCoreSDK.HANDLE_TYPE_TRANSFILE) {
                    int  userid = tBundle.getInt("USERID");
                    String filename = tBundle.getString("FILENAME");
                    String tempfile = tBundle.getString("TEMPFILE");
                    int length = tBundle.getInt("LENGTH");
                    int wparam = tBundle.getInt("WPARAM");
                    int lparam = tBundle.getInt("LPARAM");
                    int taskid = tBundle.getInt("TASKID");
                    if (anychat.transDataEvent != null) {
                        anychat.transDataEvent.OnAnyChatTransFile(userid, filename, tempfile, length, wparam, lparam, taskid);
                    }
                } else if (type == AnyChatCoreSDK.HANDLE_TYPE_TRANSBUF) {
                    int userid = tBundle.getInt("USERID");
                    int length = tBundle.getInt("LENGTH");
                    byte[]buf = tBundle.getByteArray("BUF");
                    if (anychat.transDataEvent != null) {
                        anychat.transDataEvent.OnAnyChatTransBuffer(userid, buf, length);
                    }
                } else if (type == AnyChatCoreSDK.HANDLE_TYPE_TRANSBUFEX) {
                   int  userid = tBundle.getInt("USERID");
                   int  length = tBundle.getInt("LENGTH");
                   byte[] buf = tBundle.getByteArray("BUF");
                   int  wparam = tBundle.getInt("WPARAM");
                   int  lparam = tBundle.getInt("LPARAM");
                   int  taskid = tBundle.getInt("TASKID");
                    if (anychat.transDataEvent != null) {
                        anychat.transDataEvent.OnAnyChatTransBufferEx(userid, buf, length, wparam, lparam, taskid);
                    }
                } else if (type == AnyChatCoreSDK.HANDLE_TYPE_SDKFILTER) {
                    int  length = tBundle.getInt("LENGTH");
                   byte[] buf = tBundle.getByteArray("BUF");
                    if (anychat.transDataEvent != null) {
                        anychat.transDataEvent.OnAnyChatSDKFilterData(buf, length);
                    }
                } else if (type == AnyChatCoreSDK.HANDLE_TYPE_VIDEOCALL) {
                   int  dwEventType = tBundle.getInt("EVENTTYPE");
                   int  dwUserId = tBundle.getInt("USERID");
                   int  dwErrorCode = tBundle.getInt("ERRORCODE");
                   int  dwFlags = tBundle.getInt("FLAGS");
                   int  dwParam = tBundle.getInt("PARAM");
                    String userStr = tBundle.getString("USERSTR");
                    if (anychat.videoCallEvent != null) {
                        anychat.videoCallEvent.OnAnyChatVideoCallEvent(dwEventType, dwUserId, dwErrorCode, dwFlags, dwParam, userStr);
                    }
                } else if (type == AnyChatCoreSDK.HANDLE_TYPE_RECORD) {
                  int   dwUserId = tBundle.getInt("USERID");
                  int   dwErrorCode = tBundle.getInt("ERRORCODE");
                  String  filename = tBundle.getString("FILENAME");
                    int dwElapse = tBundle.getInt("ELAPSE");
                  int   dwFlags = tBundle.getInt("FLAGS");
                  int   dwParam = tBundle.getInt("PARAM");
                    String userstr = tBundle.getString("USERSTR");
                    if (anychat.recordEvent == null) {
                        return;
                    }
                    if ((dwFlags & 1024) == 0) {
                        anychat.recordEvent.OnAnyChatRecordEvent(dwUserId, dwErrorCode, filename, dwElapse, dwFlags, dwParam, userstr);
                    } else {
                        anychat.recordEvent.OnAnyChatSnapShotEvent(dwUserId, dwErrorCode, filename, dwFlags, dwParam, userstr);
                    }
                } else if (type == AnyChatCoreSDK.HANDLE_TYPE_OBJECTEVENT) {
                    int dwObjectType = tBundle.getInt("OBJECTTYPE");
                    int dwObjectId = tBundle.getInt("OBJECTID");
                    int dwEventType = tBundle.getInt("EVENTTYPE");
                    int dwParam1 = tBundle.getInt("PARAM1");
                    int dwParam2 = tBundle.getInt("PARAM2");
                    int dwParam3 = tBundle.getInt("PARAM3");
                    int dwParam4 = tBundle.getInt("PARAM4");
                    String strParam = tBundle.getString("STRPARAM");
                    if (anychat.objectEvent != null) {
                        anychat.objectEvent.OnAnyChatObjectEvent(dwObjectType, dwObjectId, dwEventType, dwParam1, dwParam2, dwParam3, dwParam4, strParam);
                    }
                } else if (type == AnyChatCoreSDK.HANDLE_TYPE_CORESDKEVENT && anychat.coresdkEvent != null) {
                    anychat.coresdkEvent.OnAnyChatCoreSDKEvent(tBundle.getInt("EVENTTYPE"), tBundle.getString("JSONSTR"));
                }
            }
        }
    }

    public static native byte[] FetchAudioPlayBuffer(int i);

    public static native int GetSDKOptionInt(int i);

    public static native String GetSDKOptionString(int i);

    public static native int InputAudioData(byte[] bArr, int i, int i2);

    public static native int InputAudioDataEx(int i, byte[] bArr, int i2, int i3, int i4);

    public static native int InputVideoData(byte[] bArr, int i, int i2);

    public static native int InputVideoDataEx(int i, byte[] bArr, int i2, int i3, int i4);

    public static native int ObjectControl(int i, int i2, int i3, int i4, int i5, int i6, int i7, String str);

    public static native int[] ObjectGetIdList(int i);

    public static native int ObjectGetIntValue(int i, int i2, int i3);

    public static native String ObjectGetStringValue(int i, int i2, int i3);

    public static native int ObjectSetIntValue(int i, int i2, int i3, int i4);

    public static native int ObjectSetStringValue(int i, int i2, int i3, String str);

    public static native int SetInputAudioFormat(int i, int i2, int i3, int i4);

    public static native int SetInputAudioFormatEx(int i, int i2, int i3, int i4, int i5, int i6);

    public static native int SetInputVideoFormat(int i, int i2, int i3, int i4, int i5);

    public static native int SetInputVideoFormatEx(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    public static native int SetSDKOptionInt(int i, int i2);

    public static native int SetSDKOptionString(int i, String str);

    public native int AudioGetVolume(int i);

    public native int AudioSetVolume(int i, int i2);

    public native int CancelTransTask(int i, int i2);

    public native int CancelTransTaskEx(String str, int i, int i2);

    public native int ChangeChatMode(int i);

    public native int Connect(String str, int i);

    public native int EnterRoom(int i, String str);

    public native int EnterRoomEx(String str, String str2);

    public native String[] EnumAudioCapture();

    public native String[] EnumAudioPlayback();

    public native String[] EnumVideoCapture();

    public native int GetCameraState(int i);

    public native String GetCurAudioCapture();

    public native String GetCurAudioPlayback();

    public native String GetCurVideoCapture();

    public native int GetFriendStatus(int i);

    public native int[] GetGroupFriends(int i);

    public native String GetGroupName(int i);

    public native int[] GetOnlineUser();

    public native int[] GetRoomOnlineUsers(int i);

    public native int GetSpeakState(int i);

    public native int GetUserChatMode(int i);

    public native int[] GetUserFriends();

    public native int[] GetUserGroups();

    public native String GetUserInfo(int i, int i2);

    public native int GetUserSpeakVolume(int i);

    public native int GetUserStreamInfoInt(int i, int i2, int i3);

    public native String GetUserStreamInfoString(int i, int i2, int i3);

    public native int GetUserVideoHeight(int i);

    public native int GetUserVideoWidth(int i);

    public native int InitSDK(int i, int i2);

    public native int LeaveRoom(int i);

    public native int Login(String str, String str2);

    public native int LoginEx(String str, int i, String str2, String str3, int i2, String str4, String str5);

    public native int Logout();

    public native int MultiCastControl(String str, int i, String str2, int i2, int i3);

    public native int PrivateChatEcho(int i, int i2, int i3);

    public native int PrivateChatEchoEx(int i, int i2, int i3);

    public native int PrivateChatExit(int i);

    public native int PrivateChatRequest(int i);

    public native String QueryInfoFromServer(int i, String str, int i2);

    public native int QueryRoomStateInt(int i, int i2);

    public native String QueryRoomStateString(int i, int i2);

    public native int QueryTransTaskInfo(int i, int i2, int i3, AnyChatOutParam anyChatOutParam);

    public native int QueryTransTaskInfoEx(String str, int i, AnyChatOutParam anyChatOutParam);

    public native int QueryUserStateInt(int i, int i2);

    public native String QueryUserStateString(int i, int i2);

    public native int RegisterNotify();

    public native int Release();

    public native String SDKControl(int i, String str);

    public native int SelectAudioCapture(String str);

    public native int SelectAudioPlayback(String str);

    public native int SelectVideoCapture(String str);

    public native int SendSDKFilterData(byte[] bArr, int i);

    public native int SendTextMessage(int i, int i2, String str);

    public native int SetServerAuthPass(String str);

    public native int SetUserStreamInfoInt(int i, int i2, int i3, int i4);

    public native int SetUserStreamInfoString(int i, int i2, int i3, String str);

    public native int SetVideoPos(int i, Surface surface, int i2, int i3, int i4, int i5);

    public native int SetVideoPosEx(int i, Surface surface, int i2, int i3, int i4, int i5, int i6, int i7);

    public native int SnapShot(int i, int i2, int i3);

    public native int StreamPlayControl(String str, int i, int i2, int i3, String str2);

    public native int StreamPlayDestroy(String str, int i);

    public native String StreamPlayGetInfo(String str, int i);

    public native int StreamPlayInit(String str, String str2, int i, String str3);

    public native int StreamPlaySetVideoPos(String str, Surface surface, int i, int i2, int i3, int i4);

    public native int StreamRecordCtrl(int i, int i2, int i3, int i4);

    public native int StreamRecordCtrlEx(int i, int i2, int i3, int i4, String str);

    public native int TransBuffer(int i, byte[] bArr, int i2);

    public native int TransBufferEx(int i, byte[] bArr, int i2, int i3, int i4, int i5, AnyChatOutParam anyChatOutParam);

    public native int TransFile(int i, String str, int i2, int i3, int i4, AnyChatOutParam anyChatOutParam);

    public native int TransFileEx(String str, int i, String str2, int i2, String str3);

    public native int UserCameraControl(int i, int i2);

    public native int UserCameraControlEx(int i, int i2, int i3, int i4, String str);

    public native int UserInfoControl(int i, int i2, int i3, int i4, String str);

    public native int UserSpeakControl(int i, int i2);

    public native int UserSpeakControlEx(int i, int i2, int i3, int i4, String str);

    public native int VideoCallControl(int i, int i2, int i3, int i4, int i5, String str);

    static {
        System.loadLibrary("audio_preprocessing");
        System.loadLibrary("mediacore");
        System.loadLibrary("anychatcore");
    }

    public static synchronized AnyChatCoreSDK getInstance(Context context) {
        AnyChatCoreSDK anyChatCoreSDK;
        synchronized (AnyChatCoreSDK.class) {
            if (mAnyChat == null) {
                mAnyChat = new AnyChatCoreSDK();
            }
            anyChatCoreSDK = mAnyChat;
        }
        return anyChatCoreSDK;
    }

    private AnyChatCoreSDK() {
    }

    public void SetBaseEvent(AnyChatBaseEvent e) {
        if (mHandler == null) {
            mHandler = new MainHandler(this);
        }
        RegisterNotify();
        this.baseEvent = e;
    }

    public void SetStateChgEvent(AnyChatStateChgEvent e) {
        RegisterNotify();
        this.stateChgEvent = e;
    }

    public void SetPrivateChatEvent(AnyChatPrivateChatEvent e) {
        RegisterNotify();
        this.privateChatEvent = e;
    }

    public void SetTextMessageEvent(AnyChatTextMsgEvent e) {
        RegisterNotify();
        this.textMsgEvent = e;
    }

    public void SetTransDataEvent(AnyChatTransDataEvent e) {
        RegisterNotify();
        this.transDataEvent = e;
    }

    public void SetVideoCallEvent(AnyChatVideoCallEvent e) {
        RegisterNotify();
        this.videoCallEvent = e;
    }

    public void SetUserInfoEvent(AnyChatUserInfoEvent e) {
        RegisterNotify();
        this.userInfoEvent = e;
    }

    public void SetDataEncDecEvent(AnyChatDataEncDecEvent e) {
        RegisterNotify();
        this.encdecEvent = e;
    }

    public void SetRecordSnapShotEvent(AnyChatRecordEvent e) {
        RegisterNotify();
        this.recordEvent = e;
    }

    public void SetObjectEvent(AnyChatObjectEvent e) {
        RegisterNotify();
        this.objectEvent = e;
    }

    public void SetCoreSDKEvent(AnyChatCoreSDKEvent e) {
        RegisterNotify();
        this.coresdkEvent = e;
    }

    public void SetMediaCallBackEvent(AnyChatStreamCallBack e) {
        RegisterNotify();
        this.streamcbEvent = e;
    }

    public void removeEvent(Object e) {
        if (this.baseEvent == e) {
            this.baseEvent = null;
        }
        if (this.stateChgEvent == e) {
            this.stateChgEvent = null;
        }
        if (this.privateChatEvent == e) {
            this.privateChatEvent = null;
        }
        if (this.textMsgEvent == e) {
            this.textMsgEvent = null;
        }
        if (this.transDataEvent == e) {
            this.transDataEvent = null;
        }
        if (this.videoCallEvent == e) {
            this.videoCallEvent = null;
        }
        if (this.userInfoEvent == e) {
            this.userInfoEvent = null;
        }
        if (this.encdecEvent == e) {
            this.encdecEvent = null;
        }
        if (this.recordEvent == e) {
            this.recordEvent = null;
        }
        if (this.objectEvent == e) {
            this.objectEvent = null;
        }
        if (this.coresdkEvent == e) {
            this.coresdkEvent = null;
        }
        if (this.streamcbEvent == e) {
            this.streamcbEvent = null;
        }
    }

    public int GetSDKMainVersion() {
        return GetSDKOptionInt(22);
    }

    public int GetSDKSubVersion() {
        return GetSDKOptionInt(23);
    }

    public String GetSDKBuildTime() {
        return GetSDKOptionString(24);
    }

    public void CameraAutoFocus() {
        SetSDKOptionInt(90, 1);
    }

    public String GetUserName(int userid) {
        return QueryUserStateString(userid, 6);
    }

    public String GetUserIPAddr(int userid) {
        return QueryUserStateString(userid, 8);
    }

    public void OnNotifyMsg(int dwNotifyMsg, int wParam, int lParam) {
        boolean z = false;
        boolean z2 = true;
        AnyChatBaseEvent anyChatBaseEvent;
        AnyChatStateChgEvent anyChatStateChgEvent;
        LogUtils.eTag("ANYCHAT", "dwNotifyMsg = " + dwNotifyMsg);
        switch (dwNotifyMsg) {
            case 1225:
                if (this.baseEvent != null) {
                    LogUtils.eTag("ANYCHAT", "OnNotifyMsg,wParam::" + wParam);
                }
                anyChatBaseEvent = this.baseEvent;
                if (wParam < 1) {
                    z2 = false;
                }
                anyChatBaseEvent.OnAnyChatConnectMessage(z2);
                return;
            case 1226:
                if (this.baseEvent != null) {
                    this.baseEvent.OnAnyChatLoginMessage(wParam, lParam);
                    return;
                }
                return;
            case 1227:
                if (this.baseEvent != null) {
                    this.baseEvent.OnAnyChatEnterRoomMessage(wParam, lParam);
                    return;
                }
                return;
            case 1228:
                if (this.stateChgEvent != null) {
                    anyChatStateChgEvent = this.stateChgEvent;
                    if (lParam != 0) {
                        z = true;
                    }
                    anyChatStateChgEvent.OnAnyChatMicStateChgMessage(wParam, z);
                    return;
                }
                return;
            case 1229:
                if (this.baseEvent != null) {
                    anyChatBaseEvent = this.baseEvent;
                    if (lParam < 1) {
                        z2 = false;
                    }
                    anyChatBaseEvent.OnAnyChatUserAtRoomMessage(wParam, z2);
                    return;
                }
                return;
            case 1230:
                if (this.baseEvent != null) {
                    this.baseEvent.OnAnyChatLinkCloseMessage(lParam);
                    return;
                }
                return;
            case 1231:
                if (this.baseEvent != null) {
                    this.baseEvent.OnAnyChatOnlineUserMessage(wParam, lParam);
                    return;
                }
                return;
            case 1235:
                if (this.stateChgEvent != null) {
                    this.stateChgEvent.OnAnyChatCameraStateChgMessage(wParam, lParam);
                    return;
                }
                return;
            case 1236:
                if (this.stateChgEvent != null) {
                    anyChatStateChgEvent = this.stateChgEvent;
                    if (lParam != 0) {
                        z2 = false;
                    }
                    anyChatStateChgEvent.OnAnyChatChatModeChgMessage(wParam, z2);
                    return;
                }
                return;
            case 1237:
                if (this.stateChgEvent != null) {
                    this.stateChgEvent.OnAnyChatActiveStateChgMessage(wParam, lParam);
                    return;
                }
                return;
            case 1238:
                if (this.stateChgEvent != null) {
                    this.stateChgEvent.OnAnyChatP2PConnectStateMessage(wParam, lParam);
                    return;
                }
                return;
            case 1240:
                if (this.userInfoEvent != null) {
                    this.userInfoEvent.OnAnyChatUserInfoUpdate(wParam, lParam);
                    return;
                }
                return;
            case 1241:
                if (this.userInfoEvent != null) {
                    this.userInfoEvent.OnAnyChatFriendStatus(wParam, lParam);
                    return;
                }
                return;
            case 1245:
                if (this.privateChatEvent != null) {
                    this.privateChatEvent.OnAnyChatPrivateRequestMessage(wParam, lParam);
                    return;
                }
                return;
            case 1246:
                if (this.privateChatEvent != null) {
                    this.privateChatEvent.OnAnyChatPrivateEchoMessage(wParam, lParam);
                    return;
                }
                return;
            case 1247:
                if (this.privateChatEvent != null) {
                    this.privateChatEvent.OnAnyChatPrivateExitMessage(wParam, lParam);
                    return;
                }
                return;
            case 1324:
                if (mAudioHelper == null) {
                    return;
                }
                if (wParam == 1) {
                    mAudioHelper.InitAudioPlayer(lParam);
                    return;
                } else {
                    mAudioHelper.ReleaseAudioPlayer();
                    return;
                }
            case 1325:
                if (mAudioHelper == null) {
                    return;
                }
                if (wParam == 1) {
                    mAudioHelper.InitAudioRecorder(lParam);
                    return;
                } else {
                    mAudioHelper.ReleaseAudioRecorder();
                    return;
                }
            case 1326:
                AnyChatCameraHelper anyChatCameraHelper = mCameraHelper;
                if (wParam != 0) {
                    z = true;
                }
                anyChatCameraHelper.CaptureControl(z);
                return;
            default:
                return;
        }
    }

    private void OnAnyChatNotifyMsg(int dwNotifyMsg, int wParam, int lParam) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_NOTIFYMSG);
            tBundle.putInt("MSG", dwNotifyMsg);
            tBundle.putInt("WPARAM", wParam);
            tBundle.putInt("LPARAM", lParam);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }

    private void OnTextMessageCallBack(int dwFromUserid, int dwToUserid, int bSecret, String message) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_TEXTMSG);
            tBundle.putInt("FROMUSERID", dwFromUserid);
            tBundle.putInt("TOUSERID", dwToUserid);
            tBundle.putInt("SECRET", bSecret);
            tBundle.putString("MESSAGE", message);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }

    private void OnTransFileCallBack(int userid, String filename, String tempfilepath, int filelength, int wparam, int lparam, int taskid) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_TRANSFILE);
            tBundle.putInt("USERID", userid);
            tBundle.putString("FILENAME", filename);
            tBundle.putString("TEMPFILE", tempfilepath);
            tBundle.putInt("LENGTH", filelength);
            tBundle.putInt("WPARAM", wparam);
            tBundle.putInt("LPARAM", lparam);
            tBundle.putInt("TASKID", taskid);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }

    private void OnTransBufferCallBack(int userid, byte[] buf, int len) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_TRANSBUF);
            tBundle.putInt("USERID", userid);
            tBundle.putByteArray("BUF", buf);
            tBundle.putInt("LENGTH", len);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }

    private void OnTransBufferExCallBack(int userid, byte[] buf, int len, int wparam, int lparam, int taskid) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_TRANSBUFEX);
            tBundle.putInt("USERID", userid);
            tBundle.putByteArray("BUF", buf);
            tBundle.putInt("LENGTH", len);
            tBundle.putInt("WPARAM", wparam);
            tBundle.putInt("LPARAM", lparam);
            tBundle.putInt("TASKID", taskid);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }

    private void OnSDKFilterDataCallBack(byte[] buf, int len) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_SDKFILTER);
            tBundle.putByteArray("BUF", buf);
            tBundle.putInt("LENGTH", len);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }

    private void OnVideoDataCallBack(int userid, byte[] buf, int len, int width, int height) {
        this.mVideoHelper.SetVideoFmt(userid, 0, width, height);
        this.mVideoHelper.ShowVideo(userid, 0, buf, QueryUserStateInt(userid, 18), QueryUserStateInt(userid, 19));
        if (this.streamcbEvent != null) {
            this.streamcbEvent.OnAnyChatVideoDataCallBack(userid, 0, buf, len, width, height);
        }
    }

    private void OnVideoDataCallBackEx(int userid, int streamindex, byte[] buf, int len, int width, int height) {
        this.mVideoHelper.SetVideoFmt(userid, streamindex, width, height);
        this.mVideoHelper.ShowVideo(userid, streamindex, buf, QueryUserStateInt(userid, 18), QueryUserStateInt(userid, 19));
        if (this.streamcbEvent != null) {
            this.streamcbEvent.OnAnyChatVideoDataCallBack(userid, streamindex, buf, len, width, height);
        }
    }

    private void OnAudioDataCallBack(int userid, int streamindex, byte[] buf, int len, int timestamp, int channels, int samplespersecond, int bitspersample) {
        if (this.streamcbEvent != null) {
            this.streamcbEvent.OnAnyChatAudioDataCallBack(userid, streamindex, buf, len, timestamp, channels, samplespersecond, bitspersample);
        }
    }

    private void OnVideoCallEventCallBack(int eventtype, int userid, int errorcode, int flags, int param, String userStr) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_VIDEOCALL);
            tBundle.putInt("EVENTTYPE", eventtype);
            tBundle.putInt("USERID", userid);
            tBundle.putInt("ERRORCODE", errorcode);
            tBundle.putInt("FLAGS", flags);
            tBundle.putInt("PARAM", param);
            tBundle.putString("USERSTR", userStr);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }

    private void OnRecordSnapShotExCallBack(int dwUserId, int dwErrorCode, String lpFileName, int dwElapse, int dwFlags, int dwParam, String lpUserStr) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_RECORD);
            tBundle.putInt("USERID", dwUserId);
            tBundle.putInt("ERRORCODE", dwErrorCode);
            tBundle.putString("FILENAME", lpFileName);
            tBundle.putInt("ELAPSE", dwElapse);
            tBundle.putInt("FLAGS", dwFlags);
            tBundle.putInt("PARAM", dwParam);
            tBundle.putString("USERSTR", lpUserStr);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }

    private int OnDataEncDecCallBack(int userid, int flags, byte[] buf, int len, AnyChatOutParam outParam) {
        if (this.encdecEvent != null) {
            return this.encdecEvent.OnAnyChatDataEncDec(userid, flags, buf, len, outParam);
        }
        return -1;
    }

    private void OnObjectEventNotifyCallBack(int dwObjectType, int dwObjectId, int dwEventType, int dwParam1, int dwParam2, int dwParam3, int dwParam4, String lpStrParam) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_OBJECTEVENT);
            tBundle.putInt("OBJECTTYPE", dwObjectType);
            tBundle.putInt("OBJECTID", dwObjectId);
            tBundle.putInt("EVENTTYPE", dwEventType);
            tBundle.putInt("PARAM1", dwParam1);
            tBundle.putInt("PARAM2", dwParam2);
            tBundle.putInt("PARAM3", dwParam3);
            tBundle.putInt("PARAM4", dwParam4);
            tBundle.putString("STRPARAM", lpStrParam);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }

    private void OnAnyChatCoreSDKEventCallBack(int dwEventType, String lpJsonStr) {
        if (mHandler != null) {
            Message tMsg = new Message();
            Bundle tBundle = new Bundle();
            tBundle.putInt("HANDLETYPE", HANDLE_TYPE_CORESDKEVENT);
            tBundle.putInt("EVENTTYPE", dwEventType);
            tBundle.putString("JSONSTR", lpJsonStr);
            tMsg.setData(tBundle);
            mHandler.sendMessage(tMsg);
        }
    }
}
