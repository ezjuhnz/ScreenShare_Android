package com.cmbc.av.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ViewUtils {
    public static float getPhoneWidth(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static float getPhoneHeight(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    public static int getDensityDpi(Activity context) {
        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        return densityDpi;
    }

}
