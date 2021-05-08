package com.cmbc.av.base;

public abstract class AbsModel<CONTROL extends AbsControl,MODEL extends AbsModel> implements IModel<CONTROL,MODEL> {
    public CONTROL mCONTROL;

    public AbsModel(CONTROL CONTROL) {
        mCONTROL = CONTROL;
    }

    public CONTROL getControl() {
        return mCONTROL;
    }

}
