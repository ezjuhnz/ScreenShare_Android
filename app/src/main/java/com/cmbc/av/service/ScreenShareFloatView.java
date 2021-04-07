package com.cmbc.av.service;

import android.app.AppOpsManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cmbc.av.activity.ScreenShareActivity;
import com.cmbc.av.utils.LogUtils;

import java.lang.reflect.Method;

import xyz.djytest.screenshared.R;

public class ScreenShareFloatView extends Service {
    final String TAG = "ScreenShareFloatView";
    ScreenShareActivity mScreenShareActivity;
    private WindowManager.LayoutParams params;
    private TextView                   tv_text;
    private ImageView                  screenImg;
    private View                       floatview;
    private WindowManager              wm;


    MediaProjection mediaProjection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new FloatViewServiceBinder();
    }
    boolean permissionState = false;
    /**
     * 创建悬浮窗
     */
    private void createFloatView() {
        permissionState = getPermission();
        // if (!getPermission()) {
        // LogUtils.dTag(TAG, "没有悬浮窗权限");
        // return;
        // }
        LogUtils.dTag(TAG, "createFloatView");
        wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        floatview = View.inflate(this, R.layout.wbank_service_floatview, null); //yellow bg
        floatview.setClickable(true);
        screenImg = floatview.findViewById(R.id.screenImg);
        tv_text = floatview.findViewById(R.id.tv_text);
//        tv_text.setVisibility(View.GONE);
        floatview.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                LogUtils.dTag(TAG, "setOnClickListener");
                mScreenShareActivity.moveTaskToBack(false);
            }
        });
        initFloatViewParams();
    }

    // TODO: 2020/1/15 0015 设置的参数目前没办法显示出来，type不对
    private void initFloatViewParams() {
        wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                600,
                650,
//                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.y = 600;
        params.x = 0;
        params.width = 300;
        params.height = 300;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (addstate&&wm!=null){
            wm.removeView(floatview);
        }
        stopSelf();
    }

    /**
     * 判断 悬浮窗口权限是否打开
     *
     * @param context
     * @return true 允许 false禁止
     */
    private boolean getAppOps(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }

    private boolean getPermission() {
        LogUtils.dTag(TAG, "Build.VERSION.SDK_INT----> " + Build.VERSION.SDK_INT);
        boolean flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  6.0 有对应的权限使用方法
            flag = Settings.canDrawOverlays(this);
            LogUtils.dTag(TAG, "Settings.canDrawOverlays()---->权限为 " + flag);
            return flag;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //自己写就是24 为什么是24?看 AppOpsManager
            try {
                flag = getAppOps(this);
                LogUtils.dTag(TAG, "getAppOps()---->权限为" + flag);
                return flag;
            } catch (Exception e) {
                return false;
            }

        }
        // 4.4以下不做判断
        return true;
    }

    boolean addstate = false;

    private void setBindService(ScreenShareActivity activity) {
        mScreenShareActivity = activity;
    }

    public class FloatViewServiceBinder extends Binder {

        public void createFloatView() {
            ScreenShareFloatView.this.createFloatView();
        }

        public void setVideoSpeakActivity(ScreenShareActivity activity) {
            ScreenShareFloatView.this.setBindService(activity);
        }

        public void addView() {
            if (!addstate && permissionState) {
                wm.addView(floatview, params);
                addstate = true;
            }
        }

        public void removeView() {
            if (addstate && permissionState) {
                addstate = false;
                wm.removeView(floatview);
            }
        }

        public void updateView(Bitmap bitmap){
            if (addstate&&bitmap!=null){
                screenImg.setImageBitmap(bitmap);
            }
        }
    }
}
