package com.bairuitech.anychat;

import android.content.Context;
import android.view.OrientationEventListener;

/* compiled from: AnyChatSensorHelper */
class AnyChatOrientationEventListener extends OrientationEventListener {
    public AnyChatOrientationEventListener(Context context, int rate) {
        super(context, rate);
    }

    public void onOrientationChanged(int degree) {
        int orientation = 0;
        if (degree == -1) {
            orientation = 1;
        } else if (degree > 325 || degree <= 45) {
            orientation = 5;
        } else if (degree > 45 && degree <= AnyChatDefine.BRAC_SO_CORESDK_WRITELOG) {
            orientation = 4;
        } else if (degree <= AnyChatDefine.BRAC_SO_CORESDK_WRITELOG || degree >= AnyChatDefine.BRAC_SO_CORESDK_CBMEDIASTREAM) {
            orientation = 3;
        } else {
            orientation = 6;
        }
        AnyChatCoreSDK.SetSDKOptionInt(97, orientation);
    }
}
