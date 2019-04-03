package com.qunar.im.ui.presenter.factory;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;

import java.util.List;
import java.util.Map;

public class SessionFactory {

    public static void createSession(Map<String, RecentConversation> sessionMap, List<RecentConversation> list, IMMessage message) {
        String conversationId = message.getConversationID();
        RecentConversation recentConversation = sessionMap.get(conversationId);
        int signaleType = message.getSignalType();
        if (signaleType == ProtoMessageOuterClass.SignalType.SignalTypeReadmark_VALUE) {//消息已读状态
            boolean isRead = MessageStatus.isExistStatus(message.getReadState(),MessageStatus.REMOTE_STATUS_CHAT_READED);
            if(recentConversation != null && isRead)
                recentConversation.setUnread_msg_cont(0);
        } else {
            int topCounts = ConnectionUtil.getInstance().querryConversationTopCount();
            if (recentConversation == null) {
                recentConversation = new RecentConversation();
                recentConversation.setId(conversationId);
                sessionMap.put(conversationId, recentConversation);
            }
            if (message.getDirection() == IMMessage.DIRECTION_RECV) {
                int unReadCount = recentConversation.getUnread_msg_cont();
                recentConversation.setUnread_msg_cont(++unReadCount);
            }
            recentConversation.setLastFrom(message.getRealfrom());
            recentConversation.setLastMsgTime(message.getTime().getTime());
            recentConversation.setLastMsg(ConnectionUtil.getInstance().getLastMsg(message.getMsgType(), message.getBody()));

            list.remove(recentConversation);
            if (recentConversation.getTop() == 1) {//置顶排在第一个
                list.add(0, recentConversation);
            } else {
                list.add(topCounts, recentConversation);
            }
        }

    }
}
