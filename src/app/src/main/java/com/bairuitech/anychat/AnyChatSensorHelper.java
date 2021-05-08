package com.bairuitech.anychat;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;
import java.util.Date;

public class AnyChatSensorHelper implements SensorEventListener {
    private Date LastSportTime = new Date();
    private float LastXSpead = 0.0f;
    private float LastYSpead = 0.0f;
    private float LastZSpead = 0.0f;
    private boolean bCameraNeedFocus = false;
    private AnyChatOrientationEventListener orientationListener = null;
    private SensorManager sm;

    public void InitSensor(Context context) {
        int iDeviceType;
        if ((context.getResources().getConfiguration().screenLayout & 15) >= 3) {
            iDeviceType = 2;
        } else {
            iDeviceType = 1;
        }
        AnyChatCoreSDK.SetSDKOptionInt(103, iDeviceType);
        int degrees = 0;
        switch (((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case 0:
                degrees = 0;
                break;
            case 1:
                degrees = 90;
                break;
            case 2:
                degrees = 180;
                break;
            case 3:
                degrees = 270;
                break;
        }
        AnyChatCoreSDK.SetSDKOptionInt(99, degrees);
        AnyChatCoreSDK.mCameraHelper.SetContext(context);
        AnyChatCoreSDK.mAudioHelper.SetContext(context);
        if (this.orientationListener == null) {
            this.orientationListener = new AnyChatOrientationEventListener(context, 3);
        }
        this.orientationListener.enable();
        sm= (SensorManager) context.getSystemService("sensor");
        sm.registerListener(this, sm.getDefaultSensor(1), 3);
    }

    public void DestroySensor() {
        this.orientationListener.disable();
        sm.unregisterListener(this, sm.getDefaultSensor(1));
        sm = null;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (1 == event.sensor.getType()) {
            float X = event.values[0];
            float Y = event.values[1];
            float Z = event.values[2];
            if (((double) Math.abs(X - this.LastXSpead)) > 0.5d || ((double) Math.abs(Y - this.LastYSpead)) > 0.5d || ((double) Math.abs(Z - this.LastZSpead)) > 0.5d) {
                this.bCameraNeedFocus = true;
                this.LastSportTime.setTime(System.currentTimeMillis());
            } else {
                long interval = new Date().getTime() - this.LastSportTime.getTime();
                if (this.bCameraNeedFocus && interval > 1000) {
                    this.bCameraNeedFocus = false;
                    if (AnyChatCoreSDK.GetSDKOptionInt(95) == 3) {
                        AnyChatCoreSDK.mCameraHelper.CameraAutoFocus();
                    } else {
                        AnyChatCoreSDK.SetSDKOptionInt(90, 1);
                    }
                }
            }
            this.LastXSpead = X;
            this.LastYSpead = Y;
            this.LastZSpead = Z;
        }
    }
}
