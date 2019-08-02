package com.qunar.rn_service.rnpackage;

import android.app.Activity;

import com.qunar.rn_service.rnplugins.QIMRnCheckUpdate;
import com.qunar.rn_service.rnplugins.QimRNBModule;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.qunar.rn_service.rnplugins.QtalkPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hubin on 2018/1/18.
 */

public class QtalkServiceReactPackage implements ReactPackage {

    private Activity mActivity;//华为push要用

    public QtalkServiceReactPackage(Activity mActivity){
        this.mActivity = mActivity;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new QimRNBModule(reactContext,mActivity));
        modules.add(new QtalkPlugin(reactContext));
        modules.add(new QIMRnCheckUpdate(reactContext));
        return modules;
    }



//    @Override
//    public List<Class<? extends JavaScriptModule>> createJSModules() {
//        return Collections.emptyList();
//    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
