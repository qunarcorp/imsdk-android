package com.qunar.im.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.rn_service.rnmanage.QtalkServiceRNViewInstanceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QtalkOpenTravelCalendar  extends SwipeBackActivity  implements DefaultHardwareBackBtnHandler, PermissionAwareActivity, IMNotificaitonCenter.NotificationCenterDelegate, PermissionCallback {

    public ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;
    protected QtNewActionBar qtNewActionBar;//头部导航
    private PermissionListener mPermissionListener;

    private static final int PERMISSION_REQUIRE = PermissionDispatcher.getRequestCode();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_travel_calendar);
        bindView();
        addEvent();
        startRNApplicationWithBundle(getExtendBundle());
        bindData();
    }

    private void bindData() {
        setActionBarRightSpecial(0);
//        setActionBarSingleTitle(mTitles[mViewPager.getCurrentItem()]);
        setActionBarRightIcon(R.string.atom_ui_new_select_calendar);
        setActionBarRightIconClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView tpv = new TimePickerBuilder(QtalkOpenTravelCalendar.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        String time = new SimpleDateFormat("yyyy-MM-dd").format(date);
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SELECT_DATE,time);
                    }
                }).build();

                tpv.show();
            }
        });
        checkPermission();
    }

    /**
     * 检查权限
     */
    private void checkPermission(){
        PermissionDispatcher.requestPermissionWithCheck(this,
                new int[]{PermissionDispatcher.REQUEST_READ_CALENDAR,PermissionDispatcher.REQUEST_WRITE_CALENDAR
                        /*, PermissionDispatcher.REQUEST_READ_PHONE_STATE*/}, this,
                PERMISSION_REQUIRE);
    }

    private void bindView() {
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        setActionBarTitle("行程");
        mReactRootView = (ReactRootView) findViewById(R.id.mReactRootView);
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
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        mPermissionListener = listener;
        ActivityCompat.requestPermissions(this, permissions, requestCode);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionListener!=null){
            mPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }

//        if (mReactRootView != null) {
//            mReactRootView.unmountReactApplication();
////            mReactRootView = null;
//        }
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
        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.UPDATE_TRIP);
    }

    private void removeEvent() {
        ConnectionUtil.getInstance().removeEvent(this, QtalkEvent.RN_UPDATE);
        ConnectionUtil.getInstance().removeEvent(this,QtalkEvent.UPDATE_TRIP);
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        onBackPressed();
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

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            case QtalkEvent.RN_UPDATE:
                updatePage();
                break;
            case QtalkEvent.UPDATE_TRIP:
                //请求最新的好友
                WritableNativeMap map = new WritableNativeMap();
                sendEvent(mReactInstanceManager.getCurrentReactContext(),"QIM_RN_Will_Show",map);
//                clearBridge();
                break;
        }
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (requestCode == PERMISSION_REQUIRE) {
            if (!granted) {
                Toast.makeText(this,"没有日历权限,Qtalk无法同步日程到系统日历", Toast.LENGTH_LONG).show();
//                finish();
                return;
            }


        }
    }
}
