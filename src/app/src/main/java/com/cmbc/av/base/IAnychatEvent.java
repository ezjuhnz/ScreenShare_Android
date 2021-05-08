package com.cmbc.av.base;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDKEvent;
import com.bairuitech.anychat.AnyChatDataEncDecEvent;
import com.bairuitech.anychat.AnyChatObjectEvent;
import com.bairuitech.anychat.AnyChatPrivateChatEvent;
import com.bairuitech.anychat.AnyChatRecordEvent;
import com.bairuitech.anychat.AnyChatStateChgEvent;
import com.bairuitech.anychat.AnyChatStreamCallBack;
import com.bairuitech.anychat.AnyChatTextMsgEvent;
import com.bairuitech.anychat.AnyChatTransDataEvent;
import com.bairuitech.anychat.AnyChatUserInfoEvent;
import com.bairuitech.anychat.AnyChatVideoCallEvent;

public interface IAnychatEvent<CONTROL extends IControl,ANYCHAT extends  IAnychatEvent> extends AnyChatRecordEvent, AnyChatBaseEvent, AnyChatObjectEvent
        , AnyChatTextMsgEvent, AnyChatCoreSDKEvent, AnyChatPrivateChatEvent, AnyChatStateChgEvent,
        AnyChatTransDataEvent, AnyChatUserInfoEvent, AnyChatVideoCallEvent, AnyChatDataEncDecEvent, AnyChatStreamCallBack {
    CONTROL getControl();
    ANYCHAT getAnychat();
    void onStop();
    void onResume();
}
