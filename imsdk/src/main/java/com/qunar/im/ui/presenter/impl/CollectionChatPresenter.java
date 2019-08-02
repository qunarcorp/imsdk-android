package com.qunar.im.ui.presenter.impl;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.utils.ConnectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by hubin on 2017/11/27.
 */

public class CollectionChatPresenter extends SingleSessionPresenter {


    @Override
    public void addEvent() {
        super.addEvent();
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Collection_Message_Text);
    }

    @Override
    public void removeEvent() {
        super.removeEvent();
        ConnectionUtil.getInstance().removeEvent(this, QtalkEvent.Collection_Message_Text);
    }


    @Override
    public void showMoreOldMsg(boolean isFromGroup) {
//        super.showMoreOldMsg(isFromGroup);
        List<IMMessage> messageList = connectionUtil.SelectHistoryCollectionChatMessage(chatView.getOf(), chatView.getOt(), chatView.getChatType(), chatView.getListSize(), numPerPage);

        if (messageList.size() > 0) {
            curMsgNum += messageList.size();
            historyTime = messageList.get(0).getTime().getTime() - 1;
            Collections.reverse(messageList);
        } else {
            Logger.i("没有数据了:");
            //没有数据
        }
        chatView.addHistoryMessage(messageList);


    }

    @Override
    public void reloadMessages() {
        int start = 0;
        int firstLoadCount = curMsgNum > 0 ? curMsgNum : numPerPage;
        int unreadCount = connectionUtil.SelecCollectiontUnReadCountByConvid(chatView.getOf(), chatView.getOt());
//        int unreadCount = 0;
        if (unreadCount > firstLoadCount) {
            firstLoadCount = unreadCount;
        }
        List<IMMessage> historyMessage = new ArrayList<>();
//        if(chatView.isFromChatRoom()){
//             historyMessage = connectionUtil.SelectInitReloadCollectionGroupChatMessage(chatView.getOf(),chatView.getOt(), start, firstLoadCount);
//        }else{

        historyMessage = connectionUtil.SelectInitReloadCollectionChatMessage(chatView.getOf(), chatView.getOt(), chatView.getChatType(), start, firstLoadCount);
//        }

        curMsgNum = historyMessage.size();
        if (curMsgNum > 0) {
            Collections.reverse(historyMessage);
            if (unreadCount > 0) {
                //该条显示在未读消息上面
                IMMessage hisDivide = new IMMessage();
                String uid = UUID.randomUUID().toString();
                hisDivide.setId(uid);
                hisDivide.setMessageID(uid);
                hisDivide.setType(ConversitionType.MSG_TYPE_CHAT);
                hisDivide.setMsgType(MessageType.MSG_HISTORY_SPLITER);
                hisDivide.setTime(historyMessage.get(0).getTime());
                historyMessage.add(curMsgNum - unreadCount, hisDivide);
            }

            chatView.setHistoryMessage(historyMessage, unreadCount);
            if (unreadCount > 0) {
                connectionUtil.sendCollectionAllRead(chatView.getOf(), chatView.getOt());
            }
        } else {
            chatView.setHistoryMessage(historyMessage, 0);
        }
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        super.didReceivedNotification(key, args);
        switch (key) {
            case QtalkEvent.Collection_Message_Text:
                IMMessage imMessage = (IMMessage) args[0];
                if (imMessage.getoFromId().equals(chatView.getOf()) && imMessage.getoToId().equals(chatView.getOt())) {
                    chatView.setNewMsg2DialogueRegion(imMessage);
                    connectionUtil.sendCollectionAllRead(chatView.getOf(), chatView.getOt());
                }
                break;
        }
    }
}
