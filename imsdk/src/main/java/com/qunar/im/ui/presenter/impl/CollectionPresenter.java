package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.module.CollectionConvItemDate;
import com.qunar.im.base.module.CollectionConversation;
import com.qunar.im.base.module.CollectionUserDate;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.views.ICollectionPresenter;
import com.qunar.im.ui.presenter.views.ICollectionView;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.utils.ConnectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hubin on 2017/11/21.
 */

public class CollectionPresenter implements ICollectionPresenter, IMNotificaitonCenter.NotificationCenterDelegate {
    protected ICollectionView collectionView;
    private ConnectionUtil connectionUtil;

    @Override
    public void setView(ICollectionView view) {
        this.collectionView = view;
        connectionUtil = ConnectionUtil.getInstance();
        addEvent();
    }

    @Override
    public void removeEvent() {
        connectionUtil.removeEvent(this, QtalkEvent.COLLECTION_CHANGE);
        connectionUtil.removeEvent(this, QtalkEvent.Collection_Message_Text);
        connectionUtil.removeEvent(this, QtalkEvent.Collection_Bind_User_Update);
    }

    @Override
    public void addEvent() {
        connectionUtil.addEvent(this, QtalkEvent.COLLECTION_CHANGE);
        connectionUtil.addEvent(this, QtalkEvent.Collection_Message_Text);
        connectionUtil.addEvent(this, QtalkEvent.Collection_Bind_User_Update);

    }

    @Override
    public void propose() {

    }

    @Override
    public void reloadMessages() {
        List<Nick> nickList = connectionUtil.selectCollectionUser();

        ArrayList<MultiItemEntity> res = new ArrayList<>();
        for (int i = 0; i < nickList.size(); i++) {
            CollectionUserDate userDate = new CollectionUserDate();
            Nick nick = nickList.get(i);
            userDate.setUserId(nick.getXmppId());
            userDate.setBind(nick.getCollectionBind());
            userDate.setUnRead(nick.getCollectionUnReadCount());
            userDate.setPosition(i);
            List<CollectionConversation> recentConversationList = connectionUtil.SelectCollectionConversationList(userDate.getUserId());
            if(recentConversationList!=null && !recentConversationList.isEmpty()){
                for (int j = 0; j < recentConversationList.size(); j++) {
                    CollectionConversation data = recentConversationList.get(j);
                    CollectionConvItemDate collectionConvItemDate = new CollectionConvItemDate();
                    collectionConvItemDate.setRealJid(data.getRealJid());
                    collectionConvItemDate.setContent(data.getContent());
                    collectionConvItemDate.setDirection(data.getDirection());
                    collectionConvItemDate.setFrom(data.getFrom());
                    collectionConvItemDate.setLastUpdateTime(data.getLastUpdateTime());
                    collectionConvItemDate.setMsgId(data.getMsgId());
                    collectionConvItemDate.setOriginFrom(data.getOriginFrom());
                    collectionConvItemDate.setOriginTo(data.getOriginTo());
                    if(TextUtils.isEmpty(data.getOriginType())){
                     continue;
                    }
                    collectionConvItemDate.setOriginType(data.getOriginType());
                    collectionConvItemDate.setReadedTag(data.getReadedTag());
                    collectionConvItemDate.setState(data.getState());
                    collectionConvItemDate.setTo(data.getTo());
                    collectionConvItemDate.setType(data.getType());
                    collectionConvItemDate.setXmppId(data.getXmppId());
                    collectionConvItemDate.setUnCount(data.getUnCount());
                    userDate.addSubItem(collectionConvItemDate);
                }
            }
            res.add(userDate);
        }
        collectionView.setList(res);
    }

    @Override
    public void getBindUser() {
        ConnectionUtil.getInstance().getBindUser();
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            case QtalkEvent.COLLECTION_CHANGE:
            case QtalkEvent.Collection_Message_Text:
            case QtalkEvent.Collection_Bind_User_Update:
                reloadMessages();
                break;
        }
    }
}
