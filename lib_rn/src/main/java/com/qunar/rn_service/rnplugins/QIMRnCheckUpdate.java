package com.qunar.rn_service.rnplugins;

import android.text.TextUtils;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.orhanobut.logger.Logger;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.rn_service.activity.QTalkSearchRNViewInstanceManager;
import com.qunar.rn_service.activity.QTalkSearchRNViewOldInstanceManager;
import com.qunar.rn_service.util.QTalkServicePatchDownloadHelper;

import java.io.File;

/**
 * Created by hubin on 2018/4/2.
 */

public class QIMRnCheckUpdate extends ReactContextBaseJavaModule {


    public static String JS_BUNDLE_LOCAL_BASE_PATH = "";
    private static final String BUNDLE_NAME = "index.android.jsbundle";
    private static final String BUNDLE_ZIP_NAME = BUNDLE_NAME + ".zip";

    public static final String JS_BUNDLE_NAME = "index.android_search.bundle_v2";
    public static final String JS_BUNDLE_NAME_ZIP_NAME = JS_BUNDLE_NAME + ".zip";
    private static final String FULL_UPDATE = "full";
    private static final String AUTO_UPDATE = "auto";
    private static final String PATCH_UPDATE = "patch";

    public static final String TAG = "CheckUpdate";


    public QIMRnCheckUpdate(ReactApplicationContext reactContext) {
        super(reactContext);
        JS_BUNDLE_LOCAL_BASE_PATH = reactContext.getFilesDir().getPath() + File.separator + "rnRes" + File.separator + "qtalk_service" + File.separator;
    }

    @Override
    public String getName() {
        return "QIMRnCheckUpdate";
    }


    @ReactMethod
    public void update(
            ReadableMap param,
            final Callback success) {
        Logger.i("开始尝试更新Rn");
        boolean is_ok = false;
        // TODO get param
        String fullpackageUrl = param.getString("bundleUrl");
        String fullpackageMd5 = param.getString("zipMd5");
        String patchUrl = TextUtils.isEmpty(param.getString("patchUrl")) ? param.getString("bundleUrl") : param.getString("patchUrl");
        String patchMd5 = param.getString("patchMd5");
        String fullMd5 = param.getString("bundleMd5");
        String bundleName = param.getString("bundleName");

        // TODO switch update type
        if (!TextUtils.isEmpty(fullpackageUrl)) {


            if (param.getBoolean("new")) {
//            String updateType = param.getString("update_type");
//            if (updateType.equalsIgnoreCase(FULL_UPDATE)) {
                // todo full update
//                if (TextUtils.isEmpty(QtalkNavicationService.getInstance().getNewSerarchUrl())) {
//                    is_ok = QTalkServicePatchDownloadHelper.downloadFullPackageAndCheck(fullpackageUrl,
//                            fullpackageMd5, bundleName, BUNDLE_ZIP_NAME, JS_BUNDLE_LOCAL_BASE_PATH, QTalkSearchRNViewOldInstanceManager.CACHE_BUNDLE_NAME);
////                    mReactInstanceManager = QTalkSearchRNViewOldInstanceManager.getInstanceManager(getApplication());
//                } else {
                    if (IMDatabaseManager.getInstance().getFocusSearch()) {
                        is_ok = QTalkServicePatchDownloadHelper.downloadFullPackageAndCheck(fullpackageUrl,
                                fullpackageMd5, bundleName, BUNDLE_ZIP_NAME, JS_BUNDLE_LOCAL_BASE_PATH, QTalkSearchRNViewOldInstanceManager.CACHE_BUNDLE_NAME);
                    } else {
                        is_ok = QTalkServicePatchDownloadHelper.downloadFullPackageAndCheck(fullpackageUrl,
                                fullpackageMd5, bundleName, JS_BUNDLE_NAME_ZIP_NAME, JS_BUNDLE_LOCAL_BASE_PATH, QTalkSearchRNViewInstanceManager.CACHE_BUNDLE_NAME);
                    }
//                }


//            } else if (updateType.equalsIgnoreCase(PATCH_UPDATE)) {
//                // todo patch update
//                is_ok = QTalkServicePatchDownloadHelper.downloadPatchAndCheck(patchUrl,
//                        patchMd5, fullMd5, JS_BUNDLE_LOCAL_BASE_PATH, QtalkServiceRNViewInstanceManager.CACHE_BUNDLE_NAME, BUNDLE_NAME);
//            } else if (updateType.equalsIgnoreCase(AUTO_UPDATE)) {
//                // todo auto update
//                // first patch, if patch error full update
//                is_ok = QTalkServicePatchDownloadHelper.downloadPatchAndCheck(patchUrl,
//                        patchMd5, fullMd5, JS_BUNDLE_LOCAL_BASE_PATH, QtalkServiceRNViewInstanceManager.CACHE_BUNDLE_NAME, BUNDLE_NAME);
//                if (!is_ok) {
//                    is_ok = QTalkServicePatchDownloadHelper.downloadFullPackageAndCheck(fullpackageUrl,
//                            fullpackageMd5, bundleName, BUNDLE_ZIP_NAME, JS_BUNDLE_LOCAL_BASE_PATH, QtalkServiceRNViewInstanceManager.CACHE_BUNDLE_NAME);
//                }
//
//            }
            }
        }

        WritableNativeMap map = new WritableNativeMap();
        //  @{@"is_ok": @YES, @"errorMsg": @""};
        map.putBoolean("is_ok", is_ok);
        map.putString("errorMsg", "");

        if (is_ok) {
//            CommonConfig.mainhandler.post(new Runnable() {
//                @Override
//                public void run() {
////                    DiscoverFragment.clearBridge();
////                    HyMainActivity.clearBridge();
//                }
//            });
            Logger.i("rn更新成功");
            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.RN_UPDATE);

            success.invoke(map);
        } else {
//            error.invoke(map);
        }

    }
}
