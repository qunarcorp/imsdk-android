package com.qunar.im.ui.presenter.impl;

import android.app.Activity;
import android.text.TextUtils;

import com.facebook.imagepipeline.request.ImageRequest;
import com.qunar.im.base.callbacks.BasicCallback;
import com.qunar.im.base.common.CommonUploader;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.jsonbean.SetVCardResult;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.UserVCard;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.presenter.ICheckFriendPresenter;
import com.qunar.im.ui.presenter.IPersonalInfoPresenter;
import com.qunar.im.ui.presenter.views.ICheckFriendsView;
import com.qunar.im.base.common.ICommentView;
import com.qunar.im.ui.presenter.views.IGravatarView;
import com.qunar.im.ui.presenter.views.IPersonalInfoView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.VCardAPI;
import com.qunar.im.base.structs.SetVCardData;
import com.qunar.im.base.transit.IUploadRequestComplete;
import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by xinbo.wang on 2015/3/24.
 */
public class PersonalInfoPresenter implements IPersonalInfoPresenter, ICheckFriendPresenter {
    private static final String TAG = PersonalInfoPresenter.class.getSimpleName();
    IPersonalInfoView personalInfoView;
    IGravatarView gravatarView;
    ICommentView commentView;
    ConnectionUtil connectionUtil;
    Nick item = null;

    public PersonalInfoPresenter() {
    }

    @Override
    public void setGravatarView(IGravatarView view) {
        gravatarView = view;
    }

    @Override
    public void setPersonalInfoView(IPersonalInfoView view) {
        connectionUtil = ConnectionUtil.getInstance();
        personalInfoView = view;
    }

    @Override
    public void setCommentView(ICommentView view) {
        this.commentView = view;
    }

    @Override
    public void loadPersonalInfo() {
            String jid = personalInfoView.getJid();
            if (!TextUtils.isEmpty(jid)) {
                connectionUtil.getUserCard(jid, new IMLogicManager.NickCallBack() {
                    @Override
                    public void onNickCallBack(Nick nick) {
                        setPersonalInfo(nick);
                        if (personalInfoView != null) {
                            if(nick!=null&&!TextUtils.isEmpty(nick.getXmppId())){
                                //old
//                                ProfileUtils.displayGravatarByImageSrc(nick.getXmppId(),nick.getHeaderSrc(),personalInfoView.getImagetView());
                                //new
                                ProfileUtils.displayGravatarByImageSrc((Activity) personalInfoView.getContext(), nick.getHeaderSrc(), personalInfoView.getImagetView(), 0, 0);
                                EventBus.getDefault().post(
                                        new EventBusEvent.GravtarGot(nick.getHeaderSrc(), personalInfoView.getJid()));
                            }

//                            ProfileUtils.loadVCard4mNet(personalInfoView.getImagetView(), personalInfoView.getJid(), commentView, false);
                        }
                    }
                }, true, false);
            }
    }

    private Nick getDepartmentItem() {
        Nick item = null;

        try {
            if (this.item == null) {

            } else {
                item = this.item;
            }
        } catch (Exception ex) {
            LogUtil.e(TAG, "error", ex);
        }

        return item;
    }

    private void setPersonalInfo(Nick item) {

        if (item != null) {
            personalInfoView.setNickName(item.getName());
            personalInfoView.setDeptName(item.getDescInfo());
            personalInfoView.setJid(item.getXmppId());
        }
//        else {
//            UserVCard vCard = ProfileUtils.getLocalVCard(personalInfoView.getJid());
//            if (vCard.gravantarVersion > -1) {
//                personalInfoView.setNickName(vCard.nickname);
//                personalInfoView.setJid(vCard.id);
//
//                personalInfoView.setDeptName("非去哪儿员工");
//            }
//        }
    }


    @Override
    public void updateMyPersonalInfo() {
        if (gravatarView == null || TextUtils.isEmpty(gravatarView.getGravatarPath())) {
            personalInfoView.setUpdateResult(false);
        }
        final UploadImageRequest request = new UploadImageRequest();
        request.FileType = UploadImageRequest.LOGO;
        request.id = QtalkStringUtils.parseLocalpart(personalInfoView.getJid()) + ".gravatar";
        request.filePath = gravatarView.getGravatarPath();
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, final UploadImageResult result) {

                if (result != null && !TextUtils.isEmpty(result.httpUrl) && !result.httpUrl.contains("error")) {
                    final List<SetVCardData> datas = generateSetData(result.httpUrl, personalInfoView.getJid());
                    VCardAPI.setVCardInfo(datas, new ProtocolCallback.UnitCallback<SetVCardResult>() {
                        @Override
                        public void onCompleted(SetVCardResult setVCardResult) {
                            if (setVCardResult != null && !ListUtil.isEmpty(setVCardResult.data)) {
                                SetVCardResult.SetVCardItem data = setVCardResult.data.get(0);
                                if (data == null || TextUtils.isEmpty(data.version) ||
                                        data.version.equals("-1")) {
                                    personalInfoView.setUpdateResult(false);
                                } else {
                                    ProfileUtils.updateGVer(result.httpUrl,
                                            data.version, null, personalInfoView.getJid());
                                    String cacheUrl = QtalkStringUtils.
                                            getGravatar(result.httpUrl, true);
                                    File targetFile = MyDiskCache.getSmallFile(cacheUrl);
                                    File file = new File(gravatarView.getGravatarPath());
                                    file.renameTo(targetFile);
                                    InternDatas.JidToUrl.put(personalInfoView.getJid(), cacheUrl);
                                    personalInfoView.setUpdateResult(true);
                                }
                            }
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            personalInfoView.setUpdateResult(false);
                        }
                    });
                } else {
                    personalInfoView.setUpdateResult(false);
                }
            }

            @Override
            public void onError(String msg) {

            }
        };

        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    private static List<SetVCardData> generateSetData(String url, String userId) {
        SetVCardData vCardData = new SetVCardData();
        vCardData.url = url;
        vCardData.user = QtalkStringUtils.parseLocalpart(userId);
        vCardData.domain = QtalkNavicationService.getInstance().getXmppdomain();
        List<SetVCardData> list = new ArrayList<SetVCardData>(1);
        list.add(vCardData);
        return list;
    }

    @Override
    public void loadGravatar(final boolean forceUpdate) {
        if (forceUpdate) {
            loadPersonalInfo();
        }
    }

    @Override
    public void showLargeGravatar() {
        connectionUtil.getUserCard(personalInfoView.getJid(), new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                if (nick != null) {
                    String url = nick.getHeaderSrc();
                    personalInfoView.setLargeGravatarInfo(url,
                            MyDiskCache.getSmallFile(url + "&w=96&h=96").getAbsolutePath());
                }

            }
        }, false, false);
    }

    @Override
    public void showPersonalInfo() {
        ConnectionUtil.getInstance().getUserCard(personalInfoView.getJid(), new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                personalInfoView.setNickName(nick.getName());
                personalInfoView.setDeptName(nick.getDescInfo());
                personalInfoView.setLargeGravatarInfo(nick.getHeaderSrc(), "");
                personalInfoView.setJid(nick.getUserId());
            }
        }, false, false);
    }

    @Override
    public void getVCard(boolean isForce) {
        UserVCard vCard = ProfileUtils.getLocalVCard(personalInfoView.getJid());
        if (!TextUtils.isEmpty(vCard.gravantarUrl)) {
            String url = QtalkStringUtils.getGravatar(vCard.gravantarUrl, true);
            FacebookImageUtil.loadWithCache(url, personalInfoView.getImagetView(),
                    ImageRequest.CacheChoice.SMALL, CurrentPreference.getInstance().isSupportGifGravantar());
        }
        if (isForce) {
            ProfileUtils.loadVCard4mNet(personalInfoView.getImagetView(), personalInfoView.getJid(), commentView, false,
                    new BasicCallback<UserVCard>() {
                        @Override
                        public void onSuccess(UserVCard userVCard) {
                            CurrentPreference.getInstance().setPreferenceUserId(userVCard.nickname);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    ICheckFriendsView checkFriendsView = null;

    @Override
    public void setICheckFriendsView(ICheckFriendsView view) {
        checkFriendsView = view;
    }

    @Override
    public void checkFriend() {
        if (checkFriendsView == null && personalInfoView != null)
            checkFriendsView = ((ICheckFriendsView) personalInfoView);
        if (checkFriendsView != null) {
            String userId = checkFriendsView.getCheckedUserId();
            boolean isExist = ConnectionUtil.getInstance().isMyFriend(userId);
            checkFriendsView.setCheckResult(isExist);
        }
    }
}
