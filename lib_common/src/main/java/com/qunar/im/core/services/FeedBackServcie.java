package com.qunar.im.core.services;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.CommonUploader;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.BaseJsonResult;
import com.qunar.im.base.jsonbean.SendMailJson;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.ProgressRequestListener;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.transit.IUploadRequestComplete;
import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.utils.DeviceUtil;
import com.qunar.im.utils.UnzipUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FeedBackServcie {
    public static final String IS_NOTIFY = "isNotify";
    public static final String FEEDBACK_CONTENT = "feedBackContent";

    Callback callback;

    File zipFile;
    String content;
    String username;
    String mobile;
    boolean isNotify = true;
    boolean isUploadDb = true;
    String[] voids;

    private String send_mail_url = "";
    private final String folder = MyDiskCache.CACHE_LOG_DIR;//save path
    private String tagetZipName = folder
            + "/log_android_"
            + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date(System.currentTimeMillis()))
            + ".zip";



    public void handleLogs(){
        try {
            if(TextUtils.isEmpty(send_mail_url)){
                return;
            }
            zipFile = new File(tagetZipName);
            content = voids[0];
            if(voids.length >= 3) {
                username = voids[1];
                mobile = voids[2];
            }
            if (!zipFile.exists()) {
                zipFile.delete();
                zipFile.createNewFile();
            }

            String path = CommonConfig.globalContext.getFilesDir().getParentFile().getAbsolutePath() + "/shared_prefs";

            List<File> logFiles = UnzipUtils.listFiles(folder);
            List<File> shareFiles = UnzipUtils.listFiles(path);

            final String userName = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid);
            String dataName = TextUtils.isEmpty(userName) ? "unLogin" : userName;
            List<File> dbAllFiles = UnzipUtils.listFiles(CommonConfig.globalContext.getDatabasePath(dataName).getParent());
            List<File> dbFiles = new ArrayList<>();
            if(isUploadDb){
                for(File file : dbAllFiles){
                    if(file != null && file.getName().startsWith(CurrentPreference.getInstance().getUserid() + "_" + QtalkNavicationService.getInstance().getXmppdomain())){
                        dbFiles.add(file);
                    }
                }
            }
            if (logFiles != null) {
                if (!ListUtil.isEmpty(dbFiles)) {
                    logFiles.addAll(dbFiles);
                }
                if (shareFiles != null) {
                    logFiles.addAll(shareFiles);
                }
                UnzipUtils.zipFiles(logFiles, zipFile, content,(isNotify ? callback : null));
                uploadLogFile(tagetZipName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendNotify(false);
        }
    }

    private void uploadLogFile(final String filepath) {
        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = filepath;
        request.FileType = UploadImageRequest.FILE;
        request.id = UUID.randomUUID().toString();
        request.progressRequestListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                if(isNotify && callback != null){
                    callback.showFeedProgress(bytesWritten,contentLength,FeedType.UPLOAD);
                }
            }
        };
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {

                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    SendMailJson json = new SendMailJson();
                    json.from = "qchat@qunar.com";
                    json.from_name = "QChat Team";
                    List<SendMailJson.ARR> arrs = new ArrayList<>();
                    SendMailJson.ARR arr = new SendMailJson.ARR();
                    arr.to = "hubo.hu@qunar.com";
                    arr.name = "胡泊hu";
                    arrs.add(arr);
                    json.tos = arrs;

                    List<SendMailJson.CC> ccs = new ArrayList<>();
                    SendMailJson.CC cc = new SendMailJson.CC();
                    cc.cc = "hubin.hu@qunar.com";
                    cc.name = "胡滨";
                    ccs.add(cc);
                    cc = new SendMailJson.CC();
                    cc.cc = "lihaibin.li@qunar.com";
                    cc.name = "李海彬";
                    ccs.add(cc);
                    json.ccs = ccs;

                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("\n反馈用户：");
                    stringBuffer.append(username + "\n");
                    stringBuffer.append("\n联系方式：");
                    stringBuffer.append(mobile + "\n");
                    stringBuffer.append("\n反馈内容：");
                    stringBuffer.append(content + "\n");
                    stringBuffer.append("\n平台：");
                    stringBuffer.append(CommonConfig.currentPlat + "\n");
                    stringBuffer.append("\n用户ID：");
                    stringBuffer.append(CurrentPreference.getInstance().getPreferenceUserId() + "\n");
                    stringBuffer.append("\n导航地址：");
                    stringBuffer.append(QtalkNavicationService.getInstance().getCurrentNavUrl() + "\n\n\n");
                    stringBuffer.append("\n日志地址：");
                    stringBuffer.append(QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/" + result.httpUrl + "\n");
                    stringBuffer.append("\n手机信息：");
                    stringBuffer.append(DeviceUtil.getTelephonyManagerInfo());
                    stringBuffer.append("\n当前应用版本号：");
                    stringBuffer.append(QunarIMApp.getQunarIMApp().getVersionName());
                    stringBuffer.append("\n当前热发版本号");
                    stringBuffer.append(DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.PATCH_TIMESTAMP + "_" + QunarIMApp.getQunarIMApp().getVersionName(), "0"));

                    json.body = stringBuffer.toString();
                    json.subject = CurrentPreference.getInstance().getPreferenceUserId();
                    json.alt_body = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/" + result.httpUrl;
                    json.is_html = "1";
                    json.plat = GlobalConfigManager.getAppName().toLowerCase();

                    String url = send_mail_url;//Protocol.makeGetUri(QtalkNavicationService.getInstance().getSimpleapiurl(), QtalkNavicationService.getInstance().getHttpPort(), params.toString(), true);
                    Logger.i("上传日志文件成功  logfile url = " + result.httpUrl + "  \n请求url=" + url + "  \n请求参数=" + JsonUtils.getGson().toJson(json));

                    String q_ckey = Protocol.getCKEY();
                    if (TextUtils.isEmpty(q_ckey)) return;
                    final Map<String, String> cookie = new HashMap<>();
                    cookie.put("Cookie", "q_ckey=" + q_ckey + ";");
                    HttpUrlConnectionHandler.executePostJson(url, cookie, JsonUtils.getGson().toJson(json), new HttpRequestCallback() {
                        @Override
                        public void onComplete(InputStream response) {
                            try {
                                String resutString = Protocol.parseStream(response);
                                if(!TextUtils.isEmpty(resutString)){
                                    BaseJsonResult baseJsonResult = JsonUtils.getGson().fromJson(resutString, BaseJsonResult.class);
                                    if(baseJsonResult != null && baseJsonResult.ret){
                                        sendNotify(true);
                                    }else {
                                        sendNotify(false);
                                    }
                                }else {
                                    sendNotify(false);
                                }
                            } catch (Exception e) {
                                sendNotify(false);
                            }

                        }

                        @Override
                        public void onFailure(Exception e) {
                            sendNotify(false);
                        }
                    });
                } else {
                    sendNotify(false);
                    Logger.i("上传日志文件失败  filepath = " + filepath);
                }
            }

            @Override
            public void onError(String msg) {
                sendNotify(false);
                Logger.i("上传日志文件失败  filepath = " + filepath);
            }

        };

        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    public void setCallBack(Callback callBack){
        this.callback = callBack;
    }


    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public void setVoids(String[] voids) {
        this.voids = voids;
    }

    public void setUploadDb(boolean uploadDb) {
        isUploadDb = uploadDb;
    }

    /**
     * 回调接口
     * @author Ivan Xu
     *
     */
    public interface Callback {
        void showFeedProgress(long current,long total,FeedType feedType);
    }

    public enum FeedType{
        ZIP,
        UPLOAD
    }

    /**
     * 发送通知
     * @param isScuess
     */
    private void sendNotify(boolean isScuess){
        if(isNotify){
            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.FEED_BACK_RESULT, isScuess);
        }
    }
}
