package com.cmbc.av.base;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cmbc.av.common.CrashExceptionHandler;
import com.cmbc.av.utils.LogUtils;

import java.util.UUID;


public abstract class AbsActivity<CONTROL extends AbsControl, ACTIVITY extends AbsActivity,MODEL extends AbsModel,ANYCHAT extends AbsAnychatEvent> extends AppCompatActivity implements IView<CONTROL, ACTIVITY> {
    public final  String TAG           = getActivity().getClass().getSimpleName();
    private final String ACTIVITY_LIFE = "ACTIVITY_LIFE";
    private final String Activity_UUID = UUID.randomUUID().toString();
    public CONTROL mCONTROL;

    public CONTROL getControl() {
        return mCONTROL;
    }

    @Override
    public <VIEW extends View> VIEW findView(int viewId) {
        return findViewById(viewId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.dTag(ACTIVITY_LIFE, "onCreate");
        CrashExceptionHandler crashExceptionHandler = new CrashExceptionHandler();
        crashExceptionHandler.init(this);
        setContentView(getLayoutId());
        initMVC();
        initView();
        initData();
        initListener();
    }

    protected abstract void initData();

    protected abstract void initListener();

    protected abstract void initView();

    private void initMVC() {
        mCONTROL = createControl();
        mCONTROL.setMODEL(createModel(mCONTROL));
        mCONTROL.setANYCHAT(createAnychat(mCONTROL));
    }

    protected abstract ANYCHAT createAnychat(CONTROL control);

    protected abstract MODEL createModel(CONTROL control);


    protected abstract CONTROL createControl();

    protected abstract int getLayoutId();

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.dTag(ACTIVITY_LIFE, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.dTag(ACTIVITY_LIFE, "onRestart");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.dTag(ACTIVITY_LIFE, "onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.dTag(ACTIVITY_LIFE, "onResume");
        getControl().getAnychat().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.dTag(ACTIVITY_LIFE, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.dTag(ACTIVITY_LIFE, "onStop");
        getControl().getAnychat().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.dTag(ACTIVITY_LIFE, "onDestroy");
        getControl().getAnychat().release();
    }
}
