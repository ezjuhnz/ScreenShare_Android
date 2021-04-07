package com.cmbc.av.activity;

import com.bairuitech.anychat.AnyChatOutParam;
import com.cmbc.av.base.AbsAnychatEvent;
import com.cmbc.av.utils.LogUtils;

public class ScreenShareAnychat extends AbsAnychatEvent<ScreenShareControl, ScreenShareAnychat> {
    public ScreenShareAnychat(ScreenShareControl CONTROL) {
        super(CONTROL);
    }

    @Override
    public void OnAnyChatConnectMessage(boolean z) {
        LogUtils.dTag(TAG, "OnAnyChatConnectMessage > z = " + z);
    }

    @Override
    public void OnAnyChatEnterRoomMessage(int i, int i2) {
        if (i2 == 0){
            atRoomState = true;
        }
        LogUtils.dTag(TAG, "OnAnyChatEnterRoomMessage > i = " + i + ",i2 = " + i2);
        getAnyChatCoreSDK().UserSpeakControl(-1, 1);
    }

    @Override
    public void OnAnyChatLinkCloseMessage(int i) {
        atRoomState = false;
        LogUtils.dTag(TAG, "OnAnyChatLinkCloseMessage > i = " + i);
        getControl().getView().finish();

    }

    @Override
    public void OnAnyChatLoginMessage(int i, int i2) {
        LogUtils.dTag(TAG, "OnAnyChatLoginMessage > i = " + i + ",i2 = " + i2);
        if (i2==0){
            int code = getControl().getAnychat().getAnyChatCoreSDK().EnterRoom(1, "");
            LogUtils.dTag(TAG, "enterRoom = " + code);
            getControl().getView().localUserId = i;
            getControl().getView().localUser.setText(i + "");
        }

    }

    @Override
    public void OnAnyChatOnlineUserMessage(int i, int i2) {
        int[] ints = mAnyChatCoreSDK.GetOnlineUser();
        for (int user : ints) {
            if (user != getControl().getView().localUserId) {
                mAnyChatCoreSDK.UserSpeakControl(user, 1);
            }
        }
    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int i, boolean z) {
        if (z) {
            if (i != getControl().getView().localUserId) {
                mAnyChatCoreSDK.UserSpeakControl(i, 1);
            }
        }
    }

    @Override
    public void OnAnyChatCoreSDKEvent(int i, String str) {

    }

    @Override
    public int OnAnyChatDataEncDec(int i, int i2, byte[] bArr, int i3, AnyChatOutParam anyChatOutParam) {
        return 0;
    }

    @Override
    public void OnAnyChatObjectEvent(int i, int i2, int i3, int i4, int i5, int i6, int i7, String str) {

    }

    @Override
    public void OnAnyChatPrivateEchoMessage(int i, int i2) {

    }

    @Override
    public void OnAnyChatPrivateExitMessage(int i, int i2) {

    }

    @Override
    public void OnAnyChatPrivateRequestMessage(int i, int i2) {

    }

    @Override
    public void OnAnyChatRecordEvent(int i, int i2, String str, int i3, int i4, int i5, String str2) {

    }

    @Override
    public void OnAnyChatSnapShotEvent(int i, int i2, String str, int i3, int i4, String str2) {

    }

    @Override
    public void OnAnyChatActiveStateChgMessage(int i, int i2) {

    }

    @Override
    public void OnAnyChatCameraStateChgMessage(int i, int i2) {

    }

    @Override
    public void OnAnyChatChatModeChgMessage(int i, boolean z) {

    }

    @Override
    public void OnAnyChatMicStateChgMessage(int i, boolean z) {

    }

    @Override
    public void OnAnyChatP2PConnectStateMessage(int i, int i2) {

    }

    @Override
    public void OnAnyChatTextMessage(int i, int i2, boolean z, String str) {

    }

    @Override
    public void OnAnyChatSDKFilterData(byte[] bArr, int i) {

    }

    @Override
    public void OnAnyChatTransBuffer(int i, byte[] bArr, int i2) {

    }

    @Override
    public void OnAnyChatTransBufferEx(int i, byte[] bArr, int i2, int i3, int i4, int i5) {

    }

    @Override
    public void OnAnyChatTransFile(int i, String str, String str2, int i2, int i3, int i4, int i5) {

    }

    @Override
    public void OnAnyChatFriendStatus(int i, int i2) {

    }

    @Override
    public void OnAnyChatUserInfoUpdate(int i, int i2) {

    }

    @Override
    public void OnAnyChatVideoCallEvent(int i, int i2, int i3, int i4, int i5, String str) {

    }

    @Override
    public ScreenShareAnychat getAnychat() {
        return this;
    }

    @Override
    public void onStop() {
        if (getControl().getView().remoteUserId != 0) {
//            setInputDataFormat();
//            mAnyChatCoreSDK.UserCameraControl(getControl().getView().remoteUserId, 1);
            mAnyChatCoreSDK.UserCameraControl(-1, 1);
        }
    }

    @Override
    public void onResume() {
        if (getControl().getView().remoteUserId != 0) {
//            setInputDataFormat();
//            mAnyChatCoreSDK.UserCameraControl(getControl().getView().remoteUserId, 1);
            mAnyChatCoreSDK.UserCameraControl(-1, 1);
        }
    }

    @Override
    public void OnAnyChatAudioDataCallBack(int i, int i2, byte[] bArr, int i3, int i4, int i5, int i6, int i7) {
        LogUtils.dTag(TAG, "OnAnyChatAudioDataCallBack --> i = " + i + ",i2 = " + i2 + ",arr.length = " + bArr.length +
                ",i3 = " + i3 + ",i4 = " + i4 + ",i5 = " + i5 + ",i6 = " + i6 + ",i7 = " + i7);
    }

    @Override
    public void OnAnyChatVideoDataCallBack(int i, int i2, byte[] bArr, int i3, int i4, int i5) {
        LogUtils.dTag(TAG, "OnAnyChatVideoDataCallBack --> i = " + i + ",i2 = " + i2 + ",arr.length = " + bArr.length +
                ",i3 = " + i3 + ",i4 = " + i4 + ",i5 = " + i5);
    }
}
