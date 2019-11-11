package com.qunar.im.ui.presenter.impl;

import com.qunar.im.other.CacheDataType;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.CommonUploader;
import com.qunar.im.base.jsonbean.GeneralStringJson;
import com.qunar.im.base.jsonbean.NewRemoteConfig;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.module.UserConfigData;
import com.qunar.im.ui.presenter.IManageEmotionPresenter;
import com.qunar.im.ui.presenter.views.IManageEmotionView;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.transit.IUploadRequestComplete;
import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saber on 16-4-20.
 */
public class ManageEmotionPresenter implements IManageEmotionPresenter {
    private static final String TAG = ManageEmotionPresenter.class.getSimpleName();

    IManageEmotionView manageEmotionView;
//    IDictionaryDataModel dictionaryDataModel = new DictionaryDataModel();

    @Override
    public boolean deleteEmotions() {
        final List<UserConfigData> list = manageEmotionView.getDeletedEmotions();
        final List<String> names = new ArrayList<>(list.size());
        final UserConfigData ucd = new UserConfigData();
        ucd.setType(CacheDataType.cancel);
        final List<UserConfigData.Info> rl = new ArrayList<>();
        for (UserConfigData file : list) {
            UserConfigData.Info info = new UserConfigData.Info();
//            names.add(file.getName());
            info.setKey(CacheDataType.kCollectionCacheKey);
            info.setSubkey(file.getSubkey());
            rl.add(info);

//            file.delete();
        }
        ucd.setBatchProcess(rl);
        //todo 在这里进行表情的删除操作
//        BackgroundExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
        HttpUtil.setUserConfig(ucd, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
            @Override
            public void onCompleted(NewRemoteConfig newRemoteConfig) {
                if (newRemoteConfig.getData().getClientConfigInfos().size() > 0) {
                    ConnectionUtil.getInstance().refreshTheConfig(newRemoteConfig);
//                    for (File file : list) {
//                        file.delete();
//                    }
//                    ConnectionUtil.getInstance().insertUserConfigVersion(newRemoteConfig.getData().getVersion());
//                    ConnectionUtil.getInstance().updateUserConfigBatch(ucd);
                    manageEmotionView.updateSuccessful();
                    UserConfigData userConfigData = new UserConfigData();
                    userConfigData.setKey(CacheDataType.kCollectionCacheKey);
                    List<UserConfigData> list = ConnectionUtil.getInstance().selectUserConfigValueInString(userConfigData);
                    manageEmotionView.setEmotionNewList(list);
                } else {
//                    for (File file : list) {
//                        file.delete();
//                    }
////                    ConnectionUtil.getInstance().insertUserConfigVersion(newRemoteConfig.getData().getVersion());
////                    ConnectionUtil.getInstance().updateUserConfigBatch(ucd);
//                    manageEmotionView.updateSuccessful();
//                    manageEmotionView.setEmotionList(Arrays.asList(EmotionUtils.getFavorEmoticonFileDir().list()));
//
                }
            }

            @Override
            public void onFailure(String errMsg) {
                UserConfigData userConfigData = new UserConfigData();
                userConfigData.setKey(CacheDataType.kCollectionCacheKey);
                List<UserConfigData> list = ConnectionUtil.getInstance().selectUserConfigValueInString(userConfigData);
                manageEmotionView.setEmotionNewList(list);
            }
        });

        return true;
    }

    private List<GeneralStringJson> uploadList = new ArrayList<>();

    @Override
    public boolean addEmotions() {
        //todo 添加图片列表
        uploadList.clear();
        final List<String> addEmoj = manageEmotionView.getAddedEmotions();
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final int[] current = {0};
                for (final String path : addEmoj) {
                    UploadImageRequest request = new UploadImageRequest();
                    request.FileType = UploadImageRequest.IMAGE;
                    request.filePath = path;
                    request.requestComplete = new IUploadRequestComplete() {
                        @Override
                        public void onRequestComplete(String id, final UploadImageResult result) {
                            String url = URLEncoder.encode(QtalkStringUtils.addFilePathDomain(result.httpUrl, true));
                            String perUrl = Constants.Config.PERSISTENT_IMAGE + "?url=" + url;
                            HttpUrlConnectionHandler.executeGet(perUrl, new HttpRequestCallback() {
                                @Override
                                public void onComplete(InputStream response) {
                                    try {
                                        String reJson = Protocol.parseStream(response);
                                        GeneralStringJson stringJson =
                                                JsonUtils.getGson().fromJson(reJson, GeneralStringJson.class);
                                        stringJson.local = path;
                                        if (stringJson.ret) {
                                            uploadList.add(stringJson);
                                            com.orhanobut.logger.Logger.i("新版个人配置接口-上传:" + stringJson.data);

                                        }

                                    } catch (Exception e) {
                                        LogUtil.e(TAG, "error", e);
                                    }
                                    synchronized (current) {
                                        current[0]++;
                                    }
                                    if (current[0] == addEmoj.size()) {
                                        bulkUpLoad();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    synchronized (current) {
                                        current[0]++;
                                    }
                                    if (current[0] == addEmoj.size()) {
                                        bulkUpLoad();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(String msg) {
                            synchronized (current) {
                                current[0]++;
                            }
                            if (current[0] == addEmoj.size()) {
                                bulkUpLoad();
                            }
                        }
                    };
                    CommonUploader.getInstance().setUploadImageRequest(request);
                }
            }
        });
        return true;
    }

    public void bulkUpLoad() {
        com.orhanobut.logger.Logger.i("新版个人配置接口-结果:" + JsonUtils.getGson().toJson(uploadList));
        final UserConfigData data = new UserConfigData();
        data.setType(CacheDataType.set);
        final List<UserConfigData.Info> rl = new ArrayList<>();
        final List<String> strs = new ArrayList<>();
        for (int i = 0; i < uploadList.size(); i++) {
            GeneralStringJson gs = uploadList.get(i);
            UserConfigData.Info info = new UserConfigData.Info();
            try {
                strs.add(gs.local);
                URL u = new URL(gs.data);
                String path = u.getPath();
                String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
                fileName= fileName.substring(0,fileName.lastIndexOf("."));
                info.setKey(CacheDataType.kCollectionCacheKey);
                info.setSubkey(fileName);
                info.setValue(gs.data);
                rl.add(info);
            } catch (Exception e) {

            }
        }
        data.setBatchProcess(rl);
        HttpUtil.setUserConfig(data, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
            @Override
            public void onCompleted(NewRemoteConfig newRemoteConfig) {
                if (newRemoteConfig.getData().getClientConfigInfos().size() > 0) {
                    ConnectionUtil.getInstance().refreshTheConfig(newRemoteConfig);
                    UserConfigData userConfigData = new UserConfigData();
                    userConfigData.setKey(CacheDataType.kCollectionCacheKey);
                    List<UserConfigData> list = ConnectionUtil.getInstance().selectUserConfigValueInString(userConfigData);
                    manageEmotionView.setEmotionNewList(list);
                } else {
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }

    @Override
    public void setView(IManageEmotionView view) {
        manageEmotionView = view;
    }

    @Override
    public void loadLocalEmotions() {
        UserConfigData userConfigData = new UserConfigData();
        userConfigData.setKey(CacheDataType.kCollectionCacheKey);
       List<UserConfigData> list = ConnectionUtil.getInstance().selectUserConfigValueInString(userConfigData);
        manageEmotionView.setEmotionNewList(list);
    }
}
