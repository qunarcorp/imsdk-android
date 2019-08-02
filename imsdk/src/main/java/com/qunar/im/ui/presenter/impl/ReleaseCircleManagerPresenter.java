package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.R;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.CommonUploader;
import com.qunar.im.base.jsonbean.GeneralStringJson;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.module.AnonymousData;
import com.qunar.im.base.module.AtData;
import com.qunar.im.base.module.ImageItem;
import com.qunar.im.base.module.ImageItemWorkWorldItem;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.ReleaseCircleNoChangeItemDate;
import com.qunar.im.base.module.ReleaseContentData;
import com.qunar.im.base.module.ReleaseDataRequest;
import com.qunar.im.base.module.WorkWorldResponse;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.ui.presenter.ReleaseCirclePresenter;
import com.qunar.im.ui.presenter.views.ReleaseCircleView;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.structs.WorkWorldItemState;
import com.qunar.im.base.transit.IUploadRequestComplete;
import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.qunar.im.base.structs.MessageType.link;

public class ReleaseCircleManagerPresenter implements ReleaseCirclePresenter {

    private ReleaseCircleView mView;
    private List uploadList = new ArrayList<>();
    private String uuid;
    private AnonymousData mAnonymousData;
    private ReleaseContentData releaseContentData;

    public static final int REAL_NAME = 0;
    public static final int ANONYMOUS_NAME = 1;

    public ReleaseCircleManagerPresenter() {
        uuid = "0-"+UUID.randomUUID().toString().replace("-","");
    }


    public List deepcopy(List src) throws IOException,
            ClassNotFoundException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteout);
        out.writeObject(src);
        ByteArrayInputStream bytein = new ByteArrayInputStream(byteout
                .toByteArray());
        ObjectInputStream in = new ObjectInputStream(bytein);
        List dest = (List) in.readObject();
        return dest;

    }

    @Override
    public boolean release() {
        List<MultiItemEntity> list=new ArrayList<>();
        try{
            list = deepcopy(mView.getUpdateImageList());
        }catch (Exception e){

        }

        if (list.get(list.size() - 1) instanceof ReleaseCircleNoChangeItemDate) {
            list.remove(list.size() - 1);
        }
        String content = mView.getContent().trim();
        if (list.size() == 0 && TextUtils.isEmpty(content)&&mView.getEntity()==null) {
            mView.showToast("请输入文字或添加图片");
            //这种情况下不能发帖
            return false;
        }
        if(!mView.isCheck()){
            mView.showToast("请输入不超过500个字符的票圈");
            return false;
        }

        releaseContentData = new ReleaseContentData();
        final boolean[] isOk = {true};
        mView.showProgress();
        if (list.size()>0) {
            uploadList = Collections.synchronizedList(new ArrayList<ImageItemWorkWorldItem>());
            for (int i = 0; i < list.size(); i++) {
                uploadList.add(new ImageItemWorkWorldItem());
            }
            //todo 添加图片列表
            final List<MultiItemEntity> finalList = list;
            BackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    final int[] current = {0};

                    for (int i = 0; i < finalList.size(); i++) {
                        final MultiItemEntity item = finalList.get(i);
                        UploadImageRequest request = new UploadImageRequest();
                        request.FileType = UploadImageRequest.IMAGE;
                        request.filePath = ((ImageItem) item).path;
                        final int finalI = i;
                        request.requestComplete = new IUploadRequestComplete() {
                            @Override
                            public void onRequestComplete(String id, final UploadImageResult result) {
                                String url = URLEncoder.encode(QtalkStringUtils.addFilePathDomain(result.httpUrl));
                                String perUrl = Constants.Config.PERSISTENT_IMAGE + "?url=" + url;
                                HttpUrlConnectionHandler.executeGet(perUrl, new HttpRequestCallback() {
                                    @Override
                                    public void onComplete(InputStream response) {
                                        try {

                                            String reJson = Protocol.parseStream(response);
                                            ImageItemWorkWorldItem stringJson =
                                                    JsonUtils.getGson().fromJson(reJson, ImageItemWorkWorldItem.class);
                                            stringJson.local = ((ImageItem) item).path;
                                            com.orhanobut.logger.Logger.i("驼圈图片接口-上传:" + stringJson.data);
                                            if (stringJson.ret) {
                                                uploadList.remove(finalI);
                                                uploadList.add(finalI,stringJson);

                                                synchronized (current) {
                                                    current[0]++;
                                                }
                                                if (current[0] == finalList.size()) {
                                                    checkRelease(isOk[0]);
                                                }

                                            }else{
                                                synchronized (current) {
                                                    current[0]++;
                                                    isOk[0] =false;
                                                }
                                                if (current[0] == finalList.size()) {
                                                    checkRelease(isOk[0]);
                                                }
//                                                checkRelease(false);
                                            }
//

                                        } catch (Exception e) {
//                                        LogUtil.e(TAG, "error", e);
                                            com.orhanobut.logger.Logger.i("驼圈图片接口接口-上传:" + e.getMessage());

                                            Logger.i("发布接口出现问题:" + e.getMessage());
                                        }

                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        com.orhanobut.logger.Logger.i("驼圈图片接口接口-上传:" + e.getMessage());

                                        synchronized (current) {
                                            current[0]++;
                                            isOk[0]= false;
                                        }
                                        if (current[0] == finalList.size()) {
                                            checkRelease(isOk[0]);
//                                        configItem.value = JsonUtils.getGson().toJson(finalRemoteItems);
//                                        HttpUtil.setRemoteConfig(configItems,null);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(String msg) {
                                com.orhanobut.logger.Logger.i("驼圈图片接口接口-上传:" + msg);

                                synchronized (current) {
                                    current[0]++;
                                    isOk[0]=false;
                                }
                                if (current[0] == finalList.size()) {
                                    checkRelease(isOk[0]);
//                                configItem.value = JsonUtils.getGson().toJson(finalRemoteItems);
//                                HttpUtil.setRemoteConfig(configItems,null);
                                }
                            }
                        };
                        CommonUploader.getInstance().setUploadImageRequest(request);
                    }
//                    for (final MultiItemEntity item : finalList) {
//
//                    }
                }
            });
        }else{
            startRelease();
        }
        return true;

    }

    public void checkLink(){
        if(mView.getEntity()==null){
            return;
        }
        releaseContentData.setLinkContent(mView.getEntity());
        releaseContentData.setType(link);
        releaseContentData.setContent("分享了一条链接!");
    }

    public String getAtList(){
       Map<String,String> map =  mView.getAtList();
        List<AtData> dataList = new ArrayList<>();
        AtData ad = new AtData();
        ad.setType(10001);
        List<AtData.DataBean> atList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            AtData.DataBean atdb = new AtData.DataBean();
            atdb.setJid(entry.getKey());
            atdb.setText(entry.getValue().trim());
            atList.add(atdb);
        }
        ad.setData(atList);
        dataList.add(ad);

        String str = "";
        str = JsonUtils.getGson().toJson(dataList);
        return str;
    }

    public void startRelease(){



        String str = mView.getContent();
        String msg = ChatTextHelper.textToHTML(str);



        releaseContentData.setExContent(msg);
        releaseContentData.setContent(str);

        //进行发布前的是否为链接类型检查
        checkLink();


        ReleaseDataRequest releaseDataRequest = new ReleaseDataRequest();


        releaseDataRequest.setUuid(uuid);
        releaseDataRequest.setAtList(getAtList());
        if(mView.getIdentityType()==REAL_NAME){
            releaseDataRequest.setAnonymous(0);
            releaseDataRequest.setAnonymousName("");
            releaseDataRequest.setAnonymousPhoto("");
        }else if(mView.getIdentityType() == ANONYMOUS_NAME){
            mAnonymousData = mView.getAnonymousData();
            if(mAnonymousData==null){
                //未获取到匿名信息状态 禁止发布
                return;
            }
            releaseDataRequest.setAnonymous(1);
            releaseDataRequest.setAnonymousName(mAnonymousData.getData().getAnonymous());
            releaseDataRequest.setAnonymousPhoto(mAnonymousData.getData().getAnonymousPhoto());
        }
        releaseDataRequest.setContent(JsonUtils.getGson().toJson(releaseContentData));
        int  postType =  WorkWorldItemState.hot|WorkWorldItemState.top|WorkWorldItemState.normal;
        releaseDataRequest.setPostType(postType);
        HttpUtil.releaseWorkWorldV2(releaseDataRequest, new ProtocolCallback.UnitCallback<WorkWorldResponse>() {
            @Override
            public void onCompleted(WorkWorldResponse workWorldResponse) {
                mView.dismissProgress();

                if(workWorldResponse==null){
                    mView.showToast("发布失败，请重试！");
                }else {

                    mView.closeActivitvAndResult(workWorldResponse.getData().getNewPost());
                }
            }

            @Override
            public void onFailure(String errMsg) {
                mView.dismissProgress();
                mView.closeActivitvAndResult(null);
//                mView.closeActivitv();
            }
        });
    }

    public void checkRelease(boolean isOk) {

        Logger.i("上传成功:" + isOk + "--:" + JsonUtils.getGson().toJson(uploadList));
        if (isOk) {
            releaseContentData.setImgList(uploadList);
            releaseContentData.setType(MessageType.image);

            startRelease();
        } else {
            mView.dismissProgress();
            mView.showToast("发布失败,请重试");
            //这里直接被拦截 给用户提示上传出现问题
        }
    }

    @Override
    public void setView(ReleaseCircleView view) {
        mView = view;

    }

    @Override
    public void getAnonymous() {
        if (mAnonymousData != null) {
            mView.setAnonymousData(mAnonymousData);
        } else {


            HttpUtil.getAnonymous(uuid, new ProtocolCallback.UnitCallback<AnonymousData>() {
                @Override
                public void onCompleted(AnonymousData anonymousData) {
                    mView.setAnonymousData(anonymousData);
                    mAnonymousData = anonymousData;
                }

                @Override
                public void onFailure(String errMsg) {

                }
            });
        }
    }

    @Override
    public String getUUID() {
        return uuid;
    }
}
