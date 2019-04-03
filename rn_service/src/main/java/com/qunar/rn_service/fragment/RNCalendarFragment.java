package com.qunar.rn_service.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.rn_service.rnmanage.QtalkServiceRNViewInstanceManager;

public class RNCalendarFragment extends RNBaseFragment implements IMNotificaitonCenter.NotificationCenterDelegate,PermissionCallback {

    private static final String MODULE = "TravelCalendar";
    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;
    private Activity activity;
    private static final int PERMISSION_REQUIRE = PermissionDispatcher.getRequestCode();


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
        addEvent();
    }

    public void addEvent(){
        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.UPDATE_TRIP);
//        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Update_Buddy);
//        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.USER_GET_FRIEND);
//        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.LOGIN_EVENT);
    }

    public void removeEvent(){
        ConnectionUtil.getInstance().removeEvent(this,QtalkEvent.UPDATE_TRIP);
//        ConnectionUtil.getInstance().removeEvent(this,QtalkEvent.USER_GET_FRIEND);
//        ConnectionUtil.getInstance().removeEvent(this,QtalkEvent.Update_Buddy);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mReactRootView;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mReactRootView.startReactApplication(mReactInstanceManager,MODULE,getDefaultBundle());
    }


    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        checkPermission();


    }

    /**
     * 检查权限
     */
    private void checkPermission(){
        PermissionDispatcher.requestPermissionWithCheck(getActivity(),
                new int[]{PermissionDispatcher.REQUEST_READ_CALENDAR,PermissionDispatcher.REQUEST_WRITE_CALENDAR
                        /*, PermissionDispatcher.REQUEST_READ_PHONE_STATE*/}, this,
                PERMISSION_REQUIRE);
    }

    public static Bundle getDefaultBundle() {
        Bundle bundle = new Bundle();


        return bundle;
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

    public  void clearBridge(){


        WritableNativeMap map = new WritableNativeMap();
        sendEvent(mReactInstanceManager.getCurrentReactContext(),"QIM_RN_Will_Show",map);
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key){
            case QtalkEvent.UPDATE_TRIP:
                //请求最新的好友
                clearBridge();
                break;
//            case QtalkEvent.USER_GET_FRIEND:
//                //从数据库拿好友列表
//                clearBridge();
//                break;
//            case QtalkEvent.LOGIN_EVENT:
//                if(args[0].equals(LoginStatus.Login)){
//                    Logger.i("开始获取好友列表");
//                    ConnectionUtil.getInstance().getFriends("");
//                }


        }
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (requestCode == PERMISSION_REQUIRE) {
            if (!granted) {
                Toast.makeText(getActivity(),"没有日历权限,Qtalk无法同步日程到系统日历", Toast.LENGTH_LONG).show();
//                finish();
                return;
            }


        }
    }
}