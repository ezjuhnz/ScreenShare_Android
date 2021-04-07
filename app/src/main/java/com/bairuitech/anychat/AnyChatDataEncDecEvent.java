package com.bairuitech.anychat;

public interface AnyChatDataEncDecEvent {
    public static final int BRAC_DATAENCDEC_FLAGS_AUDIO = 16;
    public static final int BRAC_DATAENCDEC_FLAGS_BUFFER = 64;
    public static final int BRAC_DATAENCDEC_FLAGS_DECMODE = 2;
    public static final int BRAC_DATAENCDEC_FLAGS_ENCMODE = 1;
    public static final int BRAC_DATAENCDEC_FLAGS_TXTMSG = 128;
    public static final int BRAC_DATAENCDEC_FLAGS_VIDEO = 32;

    int OnAnyChatDataEncDec(int i, int i2, byte[] bArr, int i3, AnyChatOutParam anyChatOutParam);
}
