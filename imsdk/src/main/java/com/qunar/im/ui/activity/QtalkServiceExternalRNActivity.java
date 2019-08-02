package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.orhanobut.logger.Logger;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.rn_service.rnmanage.QtalkServiceExternalRNViewInstanceManager;
import com.qunar.rn_service.rnmanage.QtalkServiceRNViewInstanceManager;

public class QtalkServiceExternalRNActivity extends SwipeBackActivity implements DefaultHardwareBackBtnHandler, PermissionAwareActivity{

    public static ReactInstanceManager mReactInstanceManager;
    public static ReactInstanceManagerBuilder builder;
    protected QtNewActionBar qtNewActionBar;//头部导航



    public static ReactRootView mReactRootView;
    private PermissionListener mPermissionListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_rn_external);
        bindView();


    }

    private void bindView() {
        try {
            qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
            setNewActionBar(qtNewActionBar);
            boolean isShow = Boolean.parseBoolean(getIntent().getStringExtra("showNativeNav"));
            if (isShow) {
                String title = getIntent().getStringExtra("navTitle");
                setActionBarTitle(title);
                setActionBarVisibility(true);
            } else {
                setActionBarVisibility(false);
            }
//        mReactRootView = new ReactRootView(this);
            mReactRootView = (ReactRootView) findViewById(R.id.mReactRootView);
            startRNApplicationWithBundle(getExtendBundle());
//        setContentView(mReactRootView);
        }catch (Exception e){
            Logger.i("打开外部RN应用出现不可预知错误:"+e.getMessage());
        }
    }

    private void startRNApplicationWithBundle(Bundle extendBundle) {
        String bundleName = extendBundle.getString("Bundle");
        String Entrance = extendBundle.getString("Entrance");
        if(TextUtils.isEmpty(bundleName)){
            finish();
            return;
        }
//        if (mReactInstanceManager == null) {
            mReactInstanceManager = QtalkServiceExternalRNViewInstanceManager.getInstanceManager(this,bundleName,Entrance);
//        }
        String module = extendBundle.getString("name");
        // render react
        if(mReactInstanceManager ==null){
            return;
        }
        mReactRootView.startReactApplication(mReactInstanceManager, module, extendBundle);
    }

    private Bundle getExtendBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        bundle.putString("name", intent.getStringExtra("module"));
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

//        if (mReactRootView != null) {
//            mReactRootView.unmountReactApplication();
////            mReactRootView = null;
//        }

    }

    public void updatePage() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
            mReactInstanceManager = null;
            QtalkServiceRNViewInstanceManager.mReactInstanceManager = null;
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




}