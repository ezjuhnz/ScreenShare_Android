package com.cmbc.av.base;

public class MVCFactioy {
    public static <MODEL extends IModel,CONTROL extends IControl> MODEL createModel(CONTROL control){
//        if (control instanceof ScreenShareControl){
//            return (MODEL) new ScreenShareModel((ScreenShareControl)control);
//        }

        return null;
    }
}
