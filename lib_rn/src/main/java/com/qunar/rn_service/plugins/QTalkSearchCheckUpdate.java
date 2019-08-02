package com.qunar.rn_service.plugins;

import android.app.Activity;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.qunar.im.common.CommonConfig;
import com.qunar.rn_service.activity.QTalkSearchActivity;
import com.qunar.rn_service.activity.QTalkSearchRNViewInstanceManager;
import com.qunar.rn_service.util.QTalkPatchDownloadHelper;

/**
 * Created by wangyu.wang on 2016/11/30.
 */

public class QTalkSearchCheckUpdate extends ReactContextBaseJavaModule {


    private static final String FULL_UPDATE = "full";
    private static final String AUTO_UPDATE = "auto";
    private static final String PATCH_UPDATE = "patch";

    public static  final String TAG = "CheckUpdate";

    public QTalkSearchCheckUpdate(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "QTalkSearchCheckUpdate";
    }

    @ReactMethod
    public void update(
            ReadableMap param,
            final Callback callback) {

        WritableNativeMap map = new WritableNativeMap();
        //  @{@"is_ok": @YES, @"errorMsg": @""};
        boolean is_ok = false;

        Activity activity =  getCurrentActivity();
        if(activity != null) {
            // TODO get param
            String fullpackageUrl = param.getString("bundleUrl");
            String fullpackageMd5 = param.getString("zipMd5");
            String patchUrl = param.getString("patchUrl");
            String patchMd5 = param.getString("patchMd5");
            String fullMd5 = param.getString("bundleMd5");
            String bundleName = param.getString("bundleName");

            // TODO switch update type
            if(param.getBoolean("new")){
                String updateType = param.getString("update_type");
                if(updateType.equalsIgnoreCase(FULL_UPDATE)){
                    // todo full update
                    is_ok = QTalkPatchDownloadHelper.downloadFullPackageAndCheck(fullpackageUrl,
                            fullpackageMd5, bundleName, QTalkSearchRNViewInstanceManager.JS_BUNDLE_NAME_ZIP_NAME,
                            QTalkSearchRNViewInstanceManager.getLocalBundlePath(activity),
                            QTalkSearchRNViewInstanceManager.CACHE_BUNDLE_NAME);

                } else if(updateType.equalsIgnoreCase(PATCH_UPDATE)) {
                    // todo patch update
                    is_ok = QTalkPatchDownloadHelper.downloadPatchAndCheck(patchUrl,
                            patchMd5, fullMd5,
                            QTalkSearchRNViewInstanceManager.getLocalBundlePath(activity),
                            QTalkSearchRNViewInstanceManager.CACHE_BUNDLE_NAME,
                            QTalkSearchRNViewInstanceManager.JS_BUNDLE_NAME);
                } else if(updateType.equalsIgnoreCase(AUTO_UPDATE)){
                    // todo auto update
                    // first patch, if patch error full update
                    is_ok = QTalkPatchDownloadHelper.downloadPatchAndCheck(patchUrl,
                            patchMd5, fullMd5,
                            QTalkSearchRNViewInstanceManager.getLocalBundlePath(activity),
                            QTalkSearchRNViewInstanceManager.CACHE_BUNDLE_NAME,
                            QTalkSearchRNViewInstanceManager.JS_BUNDLE_NAME);
                    if(!is_ok){
                        is_ok = QTalkPatchDownloadHelper.downloadFullPackageAndCheck(fullpackageUrl,
                                fullpackageMd5, bundleName,
                                QTalkSearchRNViewInstanceManager.JS_BUNDLE_NAME_ZIP_NAME,
                                QTalkSearchRNViewInstanceManager.getLocalBundlePath(activity),
                                QTalkSearchRNViewInstanceManager.CACHE_BUNDLE_NAME);
                    }

                }
            }
        }

        map.putBoolean("is_ok", is_ok);
        map.putString("errorMsg", "");

        if(is_ok){
            CommonConfig.mainhandler.post(new Runnable() {
                @Override
                public void run() {
                    QTalkSearchActivity.clearBridge();
                }
            });
        }

        callback.invoke(map);
    }

}
