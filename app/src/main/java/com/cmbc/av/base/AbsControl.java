package com.cmbc.av.base;

public abstract class AbsControl<VIEW extends AbsActivity,MODEL extends AbsModel,ANYCHAT extends AbsAnychatEvent,CONTROL extends AbsControl> implements IControl<VIEW,MODEL,ANYCHAT,CONTROL> {
    public VIEW mVIEW;
    public MODEL mMODEL;
    public ANYCHAT mANYCHAT;

    public AbsControl(VIEW view) {
        this.mVIEW = view;
    }

    public ANYCHAT getAnychat() {
        return mANYCHAT;
    }

    public void setANYCHAT(ANYCHAT ANYCHAT) {
        mANYCHAT = ANYCHAT;
    }

    public VIEW getView() {
        return mVIEW;
    }

    public MODEL getModel() {
        return mMODEL;
    }

    public void setMODEL(MODEL MODEL) {
        mMODEL = MODEL;
    }
}
