package com.cmbc.av.base;

public interface IModel<CONTROL extends IControl,MODEL extends IModel> {
    CONTROL getControl();
    MODEL getModel();
}
