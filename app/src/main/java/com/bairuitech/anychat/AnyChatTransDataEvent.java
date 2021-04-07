package com.bairuitech.anychat;

public interface AnyChatTransDataEvent {
    void OnAnyChatSDKFilterData(byte[] bArr, int i);

    void OnAnyChatTransBuffer(int i, byte[] bArr, int i2);

    void OnAnyChatTransBufferEx(int i, byte[] bArr, int i2, int i3, int i4, int i5);

    void OnAnyChatTransFile(int i, String str, String str2, int i2, int i3, int i4, int i5);
}
