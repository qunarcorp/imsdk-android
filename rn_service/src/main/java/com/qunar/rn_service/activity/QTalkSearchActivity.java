package com.qunar.rn_service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.qunar.im.core.services.QtalkNavicationService;

/**
 * Created by wangyu.wang on 2016/11/30.
 */

public class QTalkSearchActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {

    private static ReactInstanceManager mReactInstanceManager;

    private ReactRootView mReactRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(mReactInstanceManager == null){
            mReactInstanceManager = QTalkSearchRNViewInstanceManager.getInstanceManager(getApplication());
        }

        mReactRootView = new ReactRootView(this);
        // render react
        mReactRootView.startReactApplication(mReactInstanceManager, QTalkSearchRNViewInstanceManager.MODULE, getExtendBundle());
        // set to content
        setContentView(mReactRootView);
    }

    private Bundle getExtendBundle() {
        Bundle bundle = new Bundle();

        bundle.putString("server", QtalkNavicationService.getInstance().getSearchurl());

        return bundle;
    }

    public static void clearBridge(){
        //TODO auto reload
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReactRootView.unmountReactApplication();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
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
    public void invokeDefaultOnBackPressed() {
        onBackPressed();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onActivityResult(mReactInstanceManager.getCurrentReactContext().getCurrentActivity(), requestCode, resultCode, data);
        }
    }
}
