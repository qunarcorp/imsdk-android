package com.qunar.rn_service.activity;

import android.app.Activity;
import android.app.Application;

import com.AlexanderZaytsev.RNI18n.RNI18nPackage;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;
import com.horcrux.svg.SvgPackage;
import com.qunar.im.common.CommonConfig;
import com.qunar.rn_service.rnpackage.SearchReactPackage;

import java.io.File;

public class QTalkSearchRNViewOldInstanceManager {

    public static final String MODULE = "qtalkSearch";
    public static final String JS_BUNDLE_NAME = "rn-qtalk-search.android.jsbundle";
    public static final String JS_BUNDLE_NAME_ZIP_NAME = JS_BUNDLE_NAME + ".zip";
    public static final String CACHE_BUNDLE_NAME = "rn-qtalk-search.ios.jsbundle_v3";
    public static String JS_BUNDLE_LOCAL_BASE_PATH = "";

    public static ReactInstanceManager mReactInstanceManager;

    public static ReactInstanceManager getInstanceManager(Application application) {

        if (mReactInstanceManager == null && !buildBundle(application)) {
            // build error
        }
        ;

        return mReactInstanceManager;
    }

    ;

    public static String getLocalBundleFilePath(Application application) {
        QTalkSearchRNViewInstanceManager.JS_BUNDLE_LOCAL_BASE_PATH =
                application.getApplicationContext().getFilesDir().getPath() +
                        File.separator + "rnRes" + File.separator + "qtalk_service" + File.separator;

        return JS_BUNDLE_LOCAL_BASE_PATH + CACHE_BUNDLE_NAME;
    }

    public static String getLocalBundlePath(Activity activity) {
        QTalkSearchRNViewInstanceManager.JS_BUNDLE_LOCAL_BASE_PATH =
                activity.getApplicationContext().getFilesDir().getPath() +
                        File.separator + "rnRes" + File.separator + "qtalk_service" + File.separator;

        return JS_BUNDLE_LOCAL_BASE_PATH;
    }

    public static boolean buildBundle(Application application) {
        boolean is_ok = false;

        try {
            ReactInstanceManagerBuilder builder = ReactInstanceManager.builder()
                    .setApplication(application)
                    .setJSMainModulePath("index.android")
                    .addPackage(new MainReactPackage())
                    .addPackage(new SearchReactPackage())
                    .addPackage(new SvgPackage())
                    .addPackage(new RNI18nPackage())
                    .setUseDeveloperSupport(CommonConfig.isDebug)
                    .setInitialLifecycleState(LifecycleState.RESUMED);

            String localBundleFile = getLocalBundleFilePath(application);

            File file = new File(localBundleFile);
            if (file.exists()) {
                // load from cache
                builder.setJSBundleFile(localBundleFile);
            } else {
                // load from asset
                builder.setBundleAssetName(JS_BUNDLE_NAME);
            }

            mReactInstanceManager = builder.build();

            is_ok = true;
        } catch (Exception e) {

        }

        return is_ok;
    }

    ;
}
