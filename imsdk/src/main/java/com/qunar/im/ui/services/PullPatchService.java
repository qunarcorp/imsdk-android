package com.qunar.im.ui.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.FixPullRequest;
import com.qunar.im.base.jsonbean.FixPullResult;
import com.qunar.im.base.protocol.HttpContinueDownloadCallback;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.common.CurrentPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 拉取补丁service
 * Created by lihaibin.li on 2017/8/17.
 */

public class PullPatchService extends IntentService {
    private static String TAG = "PullPatchService";
    private static String PATCH_FILE_NAME = "patch.ex";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PullPatchService() {
        super(PullPatchService.class.getSimpleName());
    }

    public static void runPullPatchService(Context context) {
        try {
            Intent intent = new Intent(context, PullPatchService.class);
            context.startService(intent);
        } catch (Throwable throwable) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //暂时屏蔽 因为补丁包不大
//        if (!NetworkUtils.isWifi(this))//wifi情况下才允许热更新
//            return;
//        String url = null;
//        HttpUrlConnectionHandler.executeGet(url, new HttpRequestCallback() {
//            @Override
//            public void onComplete(InputStream response) {
//                try {
//                    String resultString = Protocol.parseStream(response);
//                    NavConfigResult result = JsonUtils.getGson().fromJson(resultString, NavConfigResult.class);
//                    if (result == null || result.baseaddess == null) return;
//                    downLoadPatch(result.baseaddess.hotcheckurl);
//                } catch (Exception e) {
//                    Logger.e(TAG+e.getLocalizedMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                e.printStackTrace();
//            }
//        });

    }

    private void downLoadPatch(String pullPatchUrl) {
        if (!TextUtils.isEmpty(pullPatchUrl)) {
            final FixPullRequest fixPullRequest = new FixPullRequest();
            final String vn = QunarIMApp.getQunarIMApp().getVersionName();
            fixPullRequest.versionName = vn;
            final String currentTimeStamp = DataUtils.getInstance(this).getPreferences(Constants.Preferences.PATCH_TIMESTAMP + "_" + vn, "0");
            fixPullRequest.timeStamp = currentTimeStamp;
            String json = JsonUtils.getGson().toJson(fixPullRequest);
            Logger.d(TAG + json);
            if (pullPatchUrl == null) return;
            HttpUrlConnectionHandler.executePostJson(pullPatchUrl, json, new HttpRequestCallback() {
                @Override
                public void onComplete(InputStream response) {
                    try {
                        String resultString = Protocol.parseStream(response);
                        Logger.d(TAG + resultString);
                        if (resultString == null) return;
                        final FixPullResult fixPullResult = JsonUtils.getGson().fromJson(resultString, FixPullResult.class);
                        if (fixPullResult == null) return;
                        if ("1".equals(fixPullResult.isHasPatch)) {//存在该版本的patch文件
                            String patchUrl = fixPullResult.downloadUrl;
                            if (patchUrl == null) return;
                            HttpUrlConnectionHandler.excuteDownload(patchUrl, null, new HttpContinueDownloadCallback() {
                                @Override
                                public void onComplete(InputStream response, boolean supports) {
                                    if (response != null) {
                                        File patchFile = createFile();
                                        FileOutputStream fileout = null;
                                        try {
                                            deleteFile();
                                            fileout = new FileOutputStream(patchFile);
                                            byte[] buffer = new byte[1024];
                                            int len = 0;
                                            while ((len = response.read(buffer)) != -1) {
                                                fileout.write(buffer, 0, len);
                                            }
                                            fileout.flush();
                                        } catch (Exception e) {
                                            LogUtil.e(TAG, "error", e);
                                        } finally {
                                            if (fileout != null) {
                                                try {
                                                    fileout.close();
                                                } catch (IOException e) {
                                                    LogUtil.e(TAG, "error", e);
                                                }
                                            }
                                            try {
                                                response.close();
                                            } catch (IOException e) {
                                                LogUtil.e(TAG, "error", e);
                                            }

                                        }
                                        DataUtils.getInstance(PullPatchService.this).putPreferences(Constants.Preferences.PATCH_TIMESTAMP + "_" + vn, fixPullResult.timeStamp);
                                        onReceiveUpgradePatch(patchFile.getAbsolutePath());
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.d(TAG, "downLoadPatch:" + e.getLocalizedMessage());
                                }
                            });
                        } else {//不存在新的补丁文件检查当前热修复是否是成功的 不成功则将本地时间戳归0 重新下载补丁
//                            boolean isLoaded = Tinker.with(getApplicationContext()).isTinkerLoaded();
//                            if (!isLoaded) {
//                                Logger.i("还原补丁版本");
//                                DataUtils.getInstance(PullPatchService.this).putPreferences(Constants.Preferences.PATCH_TIMESTAMP + "_" + vn, "0");
//                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "pullPatchInfo:" + e.getLocalizedMessage());
                }
            });
        }
    }

    private File createFile() {
        File file = FileUtils.getExternalFilesDir(this);
        if (!file.exists())
            file.mkdir();

        File finalFile = new File(file, PATCH_FILE_NAME);
        if (!finalFile.exists()) {
            try {
                finalFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return finalFile;

    }

    private void deleteFile() {
        File finalFile = new File(FileUtils.getExternalFilesDir(this), PATCH_FILE_NAME);
        if (finalFile.exists())
            finalFile.delete();
    }

    private void onReceiveUpgradePatch(String patchLocation) {

    }

}
