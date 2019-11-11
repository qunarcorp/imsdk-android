package com.qunar.rn_service.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.qunar.rn_service.rnmanage.QtalkServiceRNViewInstanceManager;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by hubin on 2018/1/18.
 */

public class QtalkServiceRNActivity extends Activity implements DefaultHardwareBackBtnHandler, PermissionAwareActivity, IMNotificaitonCenter.NotificationCenterDelegate {

    public  ReactInstanceManager mReactInstanceManager;
    public  ReactInstanceManagerBuilder builder;


    public  ReactRootView mReactRootView;
    private PermissionListener mPermissionListener;

    private  String module;
    private  Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReactRootView = new ReactRootView(this);
        addEvent();
        startRNApplicationWithBundle(getExtendBundle());
        setContentView(mReactRootView);

    }

    private void startRNApplicationWithBundle(Bundle extendBundle) {
        if (mReactInstanceManager == null) {
            mReactInstanceManager = QtalkServiceRNViewInstanceManager.getInstanceManager(this);
        }
        String module = extendBundle.getString("name");
        // render react
        mReactRootView.startReactApplication(mReactInstanceManager, module, extendBundle);
    }

    private Bundle getExtendBundle() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        Bundle bundle = intent.getExtras();
        if(bundle == null){
            bundle = new Bundle();
        }
        if(data != null){//schema 跳转
            HashMap<String, String> map = Protocol.splitParams(data);
            for (Map.Entry<String,String> entry : map.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }
            bundle.putString("name", map.get("module"));
        }else { //intent 跳转
            bundle.putString("name", intent.getStringExtra("module"));
        }
        return bundle;

    }

    @Override
    public void invokeDefaultOnBackPressed() {
        onBackPressed();
    }

    @Override
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        mPermissionListener = listener;
        ActivityCompat.requestPermissions(this, permissions, requestCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }

        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
            mReactRootView = null;
        }
        removeEvent();

    }

    public void updatePage() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
            mReactInstanceManager = null;
            QtalkServiceRNViewInstanceManager.mReactInstanceManager=null;
        }
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
            mReactRootView = null;
        }
        mReactRootView = new ReactRootView(this);
        startRNApplicationWithBundle(getExtendBundle());
        setContentView(mReactRootView);
    }

    @Override
    public void onBackPressed() {

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, new DefaultHardwareBackBtnHandler() {
                @Override
                public void invokeDefaultOnBackPressed() {
                    finish();
                }
            });
        }
    }


    private void addEvent() {
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.RN_UPDATE);
    }

    private void removeEvent() {
        ConnectionUtil.getInstance().removeEvent(this, QtalkEvent.RN_UPDATE);
    }


    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            case QtalkEvent.RN_UPDATE:
                updatePage();
                break;
        }
    }
}
