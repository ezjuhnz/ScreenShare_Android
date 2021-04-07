package com.cmbc.av.base;

public interface IView<CONTROL extends IControl, VIEW extends IView> {
    <VIEW extends android.view.View> VIEW findView(int viewId);

    CONTROL getControl();

    VIEW getActivity();
}
