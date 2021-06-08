package com.qunar.rn_service.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.qunar.im.base.module.SearchKeyData;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.common.CurrentPreference;
import com.qunar.rn_service.rnplugins.QimRNBModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyu.wang on 2016/11/30.
 */

public class QTalkSearchActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {

    private static ReactInstanceManager mReactInstanceManager;

    private ReactRootView mReactRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mReactInstanceManager == null) {
//            if (TextUtils.isEmpty(QtalkNavicationService.getInstance().getNewSerarchUrl())) {
//                mReactInstanceManager = QTalkSearchRNViewOldInstanceManager.getInstanceManager(getApplication());
//            } else {
//            if(QtalkNavicationService.getInstance().getXmppdomain().equals("ejabhost1")){


                if (IMDatabaseManager.getInstance().getFocusSearch()) {
                    mReactInstanceManager = QTalkSearchRNViewOldInstanceManager.getInstanceManager(getApplication());
                } else {
                    mReactInstanceManager = QTalkSearchRNViewInstanceManager.getInstanceManager(getApplication());
                }
//            }else{
//                mReactInstanceManager = QTalkSearchRNViewOldInstanceManager.getInstanceManager(getApplication());
//            }
//            }
        }

        mReactRootView = new ReactRootView(this);
        // render react


//        if (mReactInstanceManager == null) {
//            if (TextUtils.isEmpty(QtalkNavicationService.getInstance().getNewSerarchUrl())) {
////                mReactInstanceManager = QTalkSearchRNViewOldInstanceManager.getInstanceManager(getApplication());
//                mReactRootView.startReactApplication(mReactInstanceManager, QTalkSearchRNViewOldInstanceManager.MODULE, getExtendBundle());

//            } else {
//        if(QtalkNavicationService.getInstance().getXmppdomain().equals("ejabhost1")) {

            if (IMDatabaseManager.getInstance().getFocusSearch()) {
//                    mReactInstanceManager = QTalkSearchRNViewOldInstanceManager.getInstanceManager(getApplication());
                mReactRootView.startReactApplication(mReactInstanceManager, QTalkSearchRNViewOldInstanceManager.MODULE, getExtendBundle());

            } else {
//                    mReactInstanceManager = QTalkSearchRNViewInstanceManager.getInstanceManager(getApplication());
                mReactRootView.startReactApplication(mReactInstanceManager, QTalkSearchRNViewInstanceManager.MODULE, getExtendBundle());

            }
//        }else{
//            mReactRootView.startReactApplication(mReactInstanceManager, QTalkSearchRNViewOldInstanceManager.MODULE, getExtendBundle());
//        }
//            }
//        }

        // set to content
        setContentView(mReactRootView);
    }

    private Bundle getExtendBundle() {
        Bundle bundle = new Bundle();

        bundle.putString("server", QtalkNavicationService.getInstance().getSimpleapiurl());
        bundle.putString("searchUrl", QtalkNavicationService.getInstance().getNewSerarchUrl());
        bundle.putString("singleDefaultPic", QimRNBModule.defaultUserImage);
        bundle.putString("mucDefaultPic", QimRNBModule.defaultMucImage);
        bundle.putString("imageHost", QtalkNavicationService.getInstance().getInnerFiltHttpHost());
        bundle.putString("MyUserId", CurrentPreference.getInstance().getPreferenceUserId());
        List<SearchKeyData> list = IMDatabaseManager.getInstance().getLocalSearchKeyHistory(0, 5);
        List<String> array = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            array.add(list.get(i).getSearchKey());
        }
        bundle.putStringArrayList("searchKeyHistory", (ArrayList<String>) array);

        return bundle;
    }

    public static void clearBridge() {
        //TODO auto reload
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReactRootView != null) mReactRootView.unmountReactApplication();
        if (mReactInstanceManager != null) mReactInstanceManager.onHostDestroy(this);
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
