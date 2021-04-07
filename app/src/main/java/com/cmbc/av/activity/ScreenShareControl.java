package com.cmbc.av.activity;

import com.cmbc.av.base.AbsControl;

public class ScreenShareControl extends AbsControl<ScreenShareActivity,ScreenShareModel,ScreenShareAnychat,ScreenShareControl> {


    public ScreenShareControl(ScreenShareActivity screenShareActivity) {
        super(screenShareActivity);
    }

    @Override
    public ScreenShareControl getControl() {
        return this;
    }
}
