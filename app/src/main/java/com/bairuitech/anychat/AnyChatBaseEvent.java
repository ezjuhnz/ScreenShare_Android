package com.bairuitech.anychat;

public interface AnyChatBaseEvent {
    void OnAnyChatConnectMessage(boolean z);

    void OnAnyChatEnterRoomMessage(int i, int i2);

    void OnAnyChatLinkCloseMessage(int i);

    void OnAnyChatLoginMessage(int i, int i2);

    void OnAnyChatOnlineUserMessage(int i, int i2);

    void OnAnyChatUserAtRoomMessage(int i, boolean z);
}
