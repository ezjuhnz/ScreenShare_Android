package com.cmbc.av.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.cmbc.av.utils.FloatTool;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import xyz.djytest.screenshared.R;

//import android.support.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import android.support.annotation.RequiresApi;
//import android.support.v7.app.AppCompatActivity;

public class PermissionActivity extends AppCompatActivity {
    final String TAG = "PermissionActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wbank_activity_permission);
        FloatTool.RequestOverlayPermission(PermissionActivity.this);

        requestPermission();
    }

    private void requestPermission() {
        //FragmentActivity vs AppCompatActivity
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.SYSTEM_ALERT_WINDOW/*,
                "android.permission.CAPTURE_VIDEO_OUTPUT"*/).subscribe(new Observer<Permission>() {
            boolean flag = true;

            @Override
            public void onSubscribe(Disposable disposable) {
//                Log.d(TAG, "disposable.isDisposed = " + disposable.isDisposed());
                if (disposable.isDisposed()) {
                    disposable.dispose();
                }
            }

            @Override
            public void onNext(Permission permission) {
                if (permission.name.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
//                    Log.d(TAG,"window psermission = " + permission.granted + ", "+permission.shouldShowRequestPermissionRationale);
                } else if (!permission.granted) {
                    flag = false;
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                if (flag) {
                    Intent intent = new Intent(PermissionActivity.this, ScreenShareActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PermissionActivity.this)
                                    .setTitle("警告")
                                    .setMessage("必须开启音视频权限")
                                    .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        FloatTool.onActivityResult(requestCode, resultCode, data, this);
        requestPermission();
    }
}
