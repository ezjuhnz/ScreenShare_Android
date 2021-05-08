package com.cmbc.av.base;

public interface IControl<VIEW extends IView,MODEL extends IModel,ANYCHAT extends IAnychatEvent,CONTROL extends IControl> {
    ANYCHAT getAnychat();
    VIEW getView();
    MODEL getModel();
    CONTROL getControl();
}
