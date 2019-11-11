package com.qunar.rn_service.rnmanage;

import android.app.Activity;

import com.AlexanderZaytsev.RNI18n.RNI18nPackage;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.modules.i18nmanager.I18nManagerModule;
import com.horcrux.svg.SvgPackage;
import com.psykar.cookiemanager.CookieManagerPackage;
//import com.qunar.rn_service.rnpackage.CookieManagerPackage;
import com.qunar.rn_service.rnpackage.QtalkServiceReactPackage;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;
import com.qunar.im.common.CommonConfig;

import java.io.File;

/**
 * Created by hubin on 2018/1/18.
 */

public class QtalkServiceRNViewInstanceManager {
    public static final String JS_BUNDLE_NAME = "index.androidserver.bundle";
    public static final String JS_BUNDLE_NAME_ZIP_NAME = JS_BUNDLE_NAME + ".zip";
    public static final String CACHE_BUNDLE_NAME = "index.androidserver.bundle_v1";
    public static String JS_BUNDLE_LOCAL_BASE_PATH = "";
    public static ReactInstanceManager mReactInstanceManager;

    public synchronized static ReactInstanceManager getInstanceManager(Activity mActivity){

        if(mReactInstanceManager == null) {
            buildBundle(mActivity);
        }
//        if(mReactInstanceManager == null && !buildBundle(mActivity)){
//            // build error
//        };

        return mReactInstanceManager;
    }


    public static String getLocalBundleFilePath(Activity mActivity) {
        QtalkServiceRNViewInstanceManager.JS_BUNDLE_LOCAL_BASE_PATH =
                mActivity.getApplicationContext().getFilesDir().getPath() +
                        File.separator + "rnRes" + File.separator + "qtalk_rn_service" + File.separator;

        return JS_BUNDLE_LOCAL_BASE_PATH + CACHE_BUNDLE_NAME;
    }

    public static String getLocalBundlePath(Activity mActivity) {
        QtalkServiceRNViewInstanceManager.JS_BUNDLE_LOCAL_BASE_PATH =
                mActivity.getApplicationContext().getFilesDir().getPath() +
                        File.separator + "rnRes" + File.separator + "qtalk_rn_service" + File.separator;

        return JS_BUNDLE_LOCAL_BASE_PATH ;
    }

    public static boolean buildBundle(Activity activity){
        boolean is_ok = false;

        try {
            ReactInstanceManagerBuilder builder = ReactInstanceManager.builder()
                    .setApplication(activity.getApplication())
                    .setJSMainModulePath("index.android")
                    .addPackage(new MainReactPackage())
                    .addPackage(new QtalkServiceReactPackage(activity))
                    .addPackage(new CookieManagerPackage())
                    .addPackage( new SvgPackage())
                    .addPackage(new RNI18nPackage())
                    .setUseDeveloperSupport(CommonConfig.isDebug)
                    .setInitialLifecycleState(LifecycleState.RESUMED);


            String localBundleFile = getLocalBundleFilePath(activity);

            File file = new File(localBundleFile);
            if (file.exists()) {
                // load from cache
                builder.setJSBundleFile(localBundleFile);
            } else {
//                 load from asset
                builder.setBundleAssetName(JS_BUNDLE_NAME);
            }

            mReactInstanceManager = builder.build();

            is_ok = true;
        }catch (Exception e){

        }

        return is_ok;
    }
}
