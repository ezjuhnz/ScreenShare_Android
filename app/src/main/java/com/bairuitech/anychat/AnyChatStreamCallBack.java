package com.bairuitech.anychat;

public interface AnyChatStreamCallBack {
    void OnAnyChatAudioDataCallBack(int i, int i2, byte[] bArr, int i3, int i4, int i5, int i6,
                                    int i7);

    void OnAnyChatVideoDataCallBack(int i, int i2, byte[] bArr, int i3, int i4, int i5);
}
