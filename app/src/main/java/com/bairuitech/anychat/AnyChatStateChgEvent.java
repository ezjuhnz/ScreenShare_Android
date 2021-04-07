package com.bairuitech.anychat;

public interface AnyChatStateChgEvent {
    void OnAnyChatActiveStateChgMessage(int i, int i2);

    void OnAnyChatCameraStateChgMessage(int i, int i2);

    void OnAnyChatChatModeChgMessage(int i, boolean z);

    void OnAnyChatMicStateChgMessage(int i, boolean z);

    void OnAnyChatP2PConnectStateMessage(int i, int i2);
}
