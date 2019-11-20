package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.BuddyRequest;
import com.qunar.im.base.module.Nick;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.presenter.IAnswerBuddyPresenter;
import com.qunar.im.ui.presenter.IBuddyPresenter;
import com.qunar.im.ui.presenter.IFriendsManagePresenter;
import com.qunar.im.ui.presenter.views.IAnswerBuddyRequestView;
import com.qunar.im.ui.presenter.views.IAnswerForResultView;
import com.qunar.im.ui.presenter.views.IBuddyView;
import com.qunar.im.ui.presenter.views.IFriendsManageView;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.HanziToPinyin;
import com.qunar.im.base.util.NetworkUtils;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 好友列表的逻辑实现
 */

public class BuddyPresenter implements IFriendsManagePresenter, IBuddyPresenter, IAnswerBuddyPresenter,IMNotificaitonCenter.NotificationCenterDelegate {
    public static final String TAG = "BuddyPresenter";
    IFriendsManageView mFriendsView;
    IBuddyView mBuddyView;
    IAnswerBuddyRequestView mAnswerView;

    public BuddyPresenter() {

        addEvent();
    }

    @Override
    public void setAnswerView(IAnswerBuddyRequestView view) {
        mAnswerView = view;
    }

    @Override
    public void deleteBuddy() {
        ConnectionUtil.getInstance().deleteFriend(QtalkStringUtils.parseId(mBuddyView.getTargetId()), QtalkNavicationService.getInstance().getXmppdomain());

    }

    @Override
    public void answerForRequest() {
        if (NetworkUtils.isConnection(QunarIMApp.getContext()) != NetworkUtils.ConnectStatus.connected) {
            mAnswerView.setStatus(false);
            return;
        }
        boolean status = false;
        if (status) {
            BuddyRequest request = ((IAnswerForResultView) mAnswerView).getBuddyRequest();
            request.setStatus(mAnswerView.getFriendRequstResult() ? BuddyRequest.Status.ACCEPT : BuddyRequest.Status.DENY);
            EventBus.getDefault().post(new EventBusEvent.FriendsChange(true));
        }
        mAnswerView.setStatus(status);
    }

    @Override
    public void registerRoster() {

    }


    @Override
    public void setBuddyView(IBuddyView view) {
        mBuddyView = view;
    }

    @Override
    public void addFriend() {
        final String targetId = mBuddyView.getTargetId();
        ConnectionUtil.getInstance().sendVerifyFriend(targetId);
    }


    @Override
    public void setFriendsView(IFriendsManageView view) {
        mFriendsView = view;
    }

    public void addEvent(){
        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.Update_Buddy);
        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.USER_GET_FRIEND);
    }
    public void removeEvent(){
        ConnectionUtil.getInstance().removeEvent(this,QtalkEvent.USER_GET_FRIEND);
        ConnectionUtil.getInstance().removeEvent(this,QtalkEvent.Update_Buddy);
    }


    /**
     * 从数据库加载好友数据
     */
    @Override
    public void updateContacts() {
        List<Nick> fList = ConnectionUtil.getInstance().SelectFriendList();
        showFriendsList(fList);
        //从数据库加载好友列表
//        List<UserVCard> buddies = buddyDataModel.selectMyFriendsVCard();
//        showFriendsList(buddies);
    }

    private void showFriendsList(List<Nick> buddies) {
        if (mFriendsView != null) {
            Map<Integer, List<Nick>> map = new HashMap<Integer, List<Nick>>();
            String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            for (Nick buddy : buddies) {
//                Nick node = new Nick();
                String nick = TextUtils.isEmpty(buddy.getName()) ? buddy.getXmppId() : buddy.getName();
//                node.setKey(buddy.getXmppId());
//                node.setName(buddy.getName());
                char ch = HanziToPinyin.getFirstLetter(nick.charAt(0));

                int ids = mSections.indexOf(ch) + 1;
                if (ids <= 0) {
                    List<Nick> nodes = map.get(27);
                    if (nodes == null){
                        nodes = new ArrayList<Nick>();
                        map.put(27, nodes);
                    }
                    nodes.add(buddy);

                } else {
                    List<Nick> nodes = map.get(ids);
                    if (nodes == null) {
                        nodes = new ArrayList<Nick>();
                        map.put(ids, nodes);
                    }
                    nodes.add(buddy);
                }
            }
            mFriendsView.setBuddyFrineds(map);
        }
    }


    @Override
    public void forceUpdateContacts() {
        updateFriends();
    }

    @Override
    public void sendAddBuddyRequest() {
        int type = mBuddyView.getAuthType();
        if (type == 0) return;
//        IMLogic.instance().requestFriends(QtalkStringUtils.userId2Jid(mBuddyView.getTargetId())
//                , type, type == 1 ? mBuddyView.getRequestReason() : type == 2 ? mBuddyView.getAnswerForQuestion() : "");
        if(type!=2){
            ConnectionUtil.getInstance().verifyFriend(mBuddyView.getTargetId(), CurrentPreference.getInstance().getPreferenceUserId());
        }else{
            ConnectionUtil.getInstance().verifyFriend(mBuddyView.getTargetId(), CurrentPreference.getInstance().getPreferenceUserId(),mBuddyView.getAnswerForQuestion());
        }

        if (type == 1) {
            if (mBuddyView != null) mBuddyView.setNofity(true, "好友请求已发送");
        } else if (type == 3) {
            if (mBuddyView != null) mBuddyView.setNofity(true, "好友请求已发送");
        }else if(type==2){
            if (mBuddyView != null) mBuddyView.setNofity(true, "问题答案已发送");
        }
    }

    private void updateFriends() {
        Logger.i("开始获取好友列表");
        ConnectionUtil.getInstance().getFriends("");
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key){
            case QtalkEvent.Update_Buddy:
                //请求最新的好友
                updateFriends();
                break;
            case QtalkEvent.USER_GET_FRIEND:
                //从数据库拿好友列表
                updateContacts();
                break;

        }
    }

}
