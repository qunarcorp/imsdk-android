package com.qunar.rn_service.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;

public class RNWorkWorldFragment extends RNBaseFragment implements IMNotificaitonCenter.NotificationCenterDelegate,PermissionCallback {

    private static final String MODULE = "WorkWorld";
    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;
    private Activity activity;
    private static final int PERMISSION_REQUIRE = PermissionDispatcher.getRequestCode();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {


    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {

    }
}
