package com.bairuitech.anychat;

public interface AnyChatPrivateChatEvent {
    void OnAnyChatPrivateEchoMessage(int i, int i2);

    void OnAnyChatPrivateExitMessage(int i, int i2);

    void OnAnyChatPrivateRequestMessage(int i, int i2);
}
