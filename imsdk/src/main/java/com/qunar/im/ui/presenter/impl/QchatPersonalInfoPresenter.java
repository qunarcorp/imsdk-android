package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.facebook.imagepipeline.request.ImageRequest;
import com.qunar.im.base.callbacks.BasicCallback;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.UserVCard;
import com.qunar.im.ui.presenter.ICheckFriendPresenter;
import com.qunar.im.ui.presenter.IPersonalInfoPresenter;
import com.qunar.im.ui.presenter.views.ICheckFriendsView;
import com.qunar.im.base.common.ICommentView;
import com.qunar.im.ui.presenter.views.IGravatarView;
import com.qunar.im.ui.presenter.views.IPersonalInfoView;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import de.greenrobot.event.EventBus;


/**
 * Created by xinbo.wang on 2015/3/24.
 */
public class QchatPersonalInfoPresenter implements IPersonalInfoPresenter,ICheckFriendPresenter {
    IGravatarView gravatarView;
    ICommentView commentView;
    IPersonalInfoView personalInfoView;
    ConnectionUtil connectionUtil;
    public QchatPersonalInfoPresenter() {
    }

    @Override
    public void setGravatarView(IGravatarView view) {
        gravatarView = view;
    }

    @Override
    public void setPersonalInfoView(IPersonalInfoView view) {
        personalInfoView = view;
        connectionUtil = ConnectionUtil.getInstance();
    }

    @Override
    public void setCommentView(ICommentView view) {
        this.commentView = view;
    }

    @Override
    public void loadPersonalInfo() {
        load4mNet();
    }


    @Override
    public void updateMyPersonalInfo() {

    }

    /**
     * 加载头像
     * @param forceUpdate  是否去服务器上更新图片
     */
    @Override
    public void loadGravatar(final boolean forceUpdate) {
        UserVCard vCard = ProfileUtils.getLocalVCard(personalInfoView.getJid());
        if (vCard.gravantarVersion > -1) {
            if (!TextUtils.isEmpty(vCard.gravantarUrl)) {
                FacebookImageUtil.loadWithCache(QtalkStringUtils.getGravatar(vCard.gravantarUrl,true)
                        , personalInfoView.getImagetView(), ImageRequest.CacheChoice.SMALL,
                        CurrentPreference.getInstance().isSupportGifGravantar());
            }
        } else {
            if (forceUpdate)
                load4mNet();
        }
    }

    /**
     * 加载头像大图
     */
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

    /**
     * 显示用户nickname和id
     */
    @Override
    public void showPersonalInfo() {

        connectionUtil.getUserCard(personalInfoView.getJid(), new IMLogicManager.NickCallBack() {
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
        if (vCard.gravantarVersion > -1&&!TextUtils.isEmpty(vCard.gravantarUrl)) {
            if(TextUtils.isEmpty(CurrentPreference.getInstance().getPreferenceUserId())) {
                CurrentPreference.getInstance().setPreferenceUserId(vCard.nickname);
                CurrentPreference.getInstance().setMerchants(vCard.type.equals("merchant"));
            }
            String url = QtalkStringUtils.getGravatar(vCard.gravantarUrl,true);
            FacebookImageUtil.loadWithCache(url,personalInfoView.getImagetView(),
                    ImageRequest.CacheChoice.SMALL,CurrentPreference.getInstance().isSupportGifGravantar());
        }
        if(isForce) {
            ProfileUtils.loadVCard4mNet(personalInfoView.getImagetView(), personalInfoView.getJid(), commentView, false,
                    new BasicCallback<UserVCard>() {
                        @Override
                        public void onSuccess(UserVCard userVCard) {
                            CurrentPreference.getInstance().setPreferenceUserId(userVCard.nickname);
                            CurrentPreference.getInstance().setMerchants(userVCard.type.equals("merchant"));
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    /**
     * 根据id去服务器上获取最新vcard
     */
    private void load4mNet() {
        ProfileUtils.loadVCard4mNet(personalInfoView.getImagetView(), personalInfoView.getJid(), commentView, false,
                new BasicCallback<UserVCard>() {
                    @Override
                    public void onSuccess(UserVCard userVCard) {
                        personalInfoView.setNickName(userVCard.nickname);
                        personalInfoView.setJid(userVCard.id);
                        personalInfoView.setUpdateResult(true);
                    }

                    @Override
                    public void onError() {
                        final UserVCard vCard = ProfileUtils.getLocalVCard(
                                QtalkStringUtils.parseBareJid(personalInfoView.getJid()));
                        personalInfoView.setNickName(vCard.nickname);
                        if (!TextUtils.isEmpty(vCard.gravantarUrl)) {
                            final String imageString = QtalkStringUtils.getGravatar(vCard.gravantarUrl,true);
                            FacebookImageUtil.loadWithCache(imageString,
                                    personalInfoView.getImagetView(), false, ImageRequest.CacheChoice.SMALL, new FacebookImageUtil.ImageLoadCallback() {
                                        @Override
                                        public void onSuccess() {
                                            EventBus.getDefault().post(new EventBusEvent.GravtarGot(imageString, personalInfoView.getJid()));
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                        }
                        personalInfoView.setUpdateResult(false);
                    }
                });
    }

    ICheckFriendsView checkFriendsView = null;
    @Override
    public void setICheckFriendsView(ICheckFriendsView view) {
        checkFriendsView = view;
    }

    @Override
    public void checkFriend() {
        if(checkFriendsView ==null&&personalInfoView!=null)checkFriendsView =  ((ICheckFriendsView)personalInfoView);
        if(checkFriendsView!=null) {
//            String userId = QtalkStringUtils.userId2Jid(checkFriendsView.getCheckedUserId());
//            boolean isExist = buddyDataModel.isExistBuddy(userId);
            String userId = checkFriendsView.getCheckedUserId();
            boolean isExist = ConnectionUtil.getInstance().isMyFriend(userId);
//            if(!isExist)
//            {
//                DepartmentItem item = friendsDataModel.searchFriendByUID(userId);
//                if(!TextUtils.isEmpty(item.userId))isExist = true;
//            }
            checkFriendsView.setCheckResult(isExist);
        }
    }
}
