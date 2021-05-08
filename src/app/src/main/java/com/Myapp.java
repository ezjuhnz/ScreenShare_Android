package com;

import android.app.Application;

import com.cmbc.av.SDKConfig;
import com.cmbc.av.utils.LogUtils;

/**
 * Time: 2021/3/24
 * Author: zhongjunhong
 * LastEdit: 2021/3/24
 * Description:
 */
public class Myapp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKConfig.init(this);
        LogUtils.getConfig().setLogSwitch(true);
    }
}
