package com.qunar.rn_service.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.rn_service.rnmanage.QtalkServiceRNViewInstanceManager;

/**
 * Created by hubin on 2018/4/4.
 */

public class RNFoundFragment extends RNBaseFragment implements IMNotificaitonCenter.NotificationCenterDelegate {

    private static final String MODULE = "FoundPage";
    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;
    private Activity activity;
    private PermissionListener mPermissionListener;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mReactRootView = new ReactRootView(context);
        activity = getActivity();
        mReactInstanceManager = QtalkServiceRNViewInstanceManager.getInstanceManager(activity);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.LOGIN_EVENT);
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        try {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } catch (Exception _ex) {
            // null bridge
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        return mReactRootView;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mReactRootView.startReactApplication(mReactInstanceManager,MODULE,getDefaultBundle());
    }

    public static Bundle getDefaultBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("domain", QtalkNavicationService.getInstance().getXmppdomain());
        //暂时修改
        return bundle;
    }

    public  void clearBridge(){

        if(mReactInstanceManager!=null){
            WritableNativeMap map = new WritableNativeMap();
            sendEvent(mReactInstanceManager.getCurrentReactContext(),"QIM_RN_Will_Show",map);
        }
    }

    @Override
    public void unbundling() {
        super.unbundling();
        mReactRootView.unmountReactApplication();
    }


    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            case QtalkEvent.LOGIN_EVENT:
                if (args[0].equals(LoginStatus.Login)) {
                    clearBridge();
                }
        }
    }







}
