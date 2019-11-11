package com.qunar.rn_service.rnmanage;

import android.app.Activity;
import android.content.Context;

import com.AlexanderZaytsev.RNI18n.RNI18nPackage;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;
import com.horcrux.svg.SvgPackage;
import com.psykar.cookiemanager.CookieManagerPackage;
import com.qunar.im.common.CommonConfig;
import com.qunar.rn_service.rnpackage.QtalkServiceReactPackage;

import java.io.File;

public class QtalkServiceExternalRNViewInstanceManager {

    public static final String JS_BUNDLE_NAME = "index.androidserver.bundle";
    public static final String JS_BUNDLE_NAME_ZIP_NAME = JS_BUNDLE_NAME + ".zip";
    public static final String CACHE_BUNDLE_NAME = "index.androidserver.bundle";
    public static String JS_BUNDLE_LOCAL_BASE_PATH = "";
    public static ReactInstanceManager mReactInstanceManager;
    public static String lastBundleName="";

    public static ReactInstanceManager getInstanceManager(Activity mActivity, String bundleName, String Entrance) {

        if(lastBundleName.equals(bundleName)){
            if (mReactInstanceManager == null && !buildBundle(mActivity, bundleName, Entrance)) {
                // build error
                mReactInstanceManager = null;
            }
        }else{
            buildBundle(mActivity, bundleName, Entrance);
            lastBundleName = bundleName;
        }


//        if (mReactInstanceManager == null && !buildBundle(mActivity, bundleName, Entrance)) {
//            // build error
//            mReactInstanceManager = null;
//        }

        return mReactInstanceManager;
    }


    public static String getLocalBundleFilePath(Context application, String bundleName) {
        QtalkServiceExternalRNViewInstanceManager.JS_BUNDLE_LOCAL_BASE_PATH =
                application.getFilesDir().getPath() +
                        File.separator + "External" + File.separator + bundleName;

        return JS_BUNDLE_LOCAL_BASE_PATH;
    }

    public static String getLocalBundlePath(Context activity) {
        QtalkServiceExternalRNViewInstanceManager.JS_BUNDLE_LOCAL_BASE_PATH =
                activity.getFilesDir().getPath() +
                        File.separator + "External" + File.separator;

        return JS_BUNDLE_LOCAL_BASE_PATH;
    }

    public static boolean buildBundle(Activity mActivity, String bundleName, String Entrance) {
        boolean is_ok = false;

        try {
            ReactInstanceManagerBuilder builder = ReactInstanceManager.builder()
                    .setApplication(mActivity.getApplication())
                    .setJSMainModulePath(Entrance)
                    .addPackage(new MainReactPackage())
                    .addPackage(new QtalkServiceReactPackage(mActivity))
                    .addPackage(new CookieManagerPackage())
                    .addPackage(new SvgPackage())
                    .addPackage(new RNI18nPackage())
                    .setUseDeveloperSupport(CommonConfig.isDebug)
                    .setInitialLifecycleState(LifecycleState.RESUMED);

            String localBundleFile = getLocalBundleFilePath(mActivity, bundleName);

            File file = new File(localBundleFile);
            if (file.exists()) {
                // load from cache
                is_ok = true;
                builder.setJSBundleFile(localBundleFile);
            } else {
//                 load from asset
                is_ok = false;
            }

            mReactInstanceManager = builder.build();


        } catch (Exception e) {
            is_ok = false;
        }

        return is_ok;
    }

}
