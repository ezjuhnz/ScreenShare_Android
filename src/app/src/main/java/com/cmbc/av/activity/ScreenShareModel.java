package com.cmbc.av.activity;

import com.cmbc.av.base.AbsModel;

public class ScreenShareModel extends AbsModel<ScreenShareControl,ScreenShareModel> {
    public ScreenShareModel(ScreenShareControl CONTROL) {
        super(CONTROL);
    }

    @Override
    public ScreenShareModel getModel() {
        return this;
    }
}
