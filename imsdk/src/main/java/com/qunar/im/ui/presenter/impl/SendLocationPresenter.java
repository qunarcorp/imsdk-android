package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.jsonbean.ShareLocationExtendInfo;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.ISendLocationPresenter;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by zhaokai on 16-2-22.
 */
public class SendLocationPresenter implements ISendLocationPresenter {

    IChatView chatView;

    @Override
    public void sendShareLocationMessage(String shareId) {
        IMMessage message = generateIMMessage();
        ShareLocationExtendInfo info = new ShareLocationExtendInfo();
        info.fromId = chatView.getFromId();
        info.shareId = shareId;
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeShareLocation_VALUE);
        String ext = JsonUtils.getGson().toJson(info, ShareLocationExtendInfo.class);
        message.setExt(ext);
        message.setBody("发起了位置共享,请升级最新App客户端查看");
        chatView.setNewMsg2DialogueRegion(message);
    }

    @Override
    public void setView(IChatView view) {
        chatView = view;
    }

    private IMMessage generateIMMessage() {
        IMMessage message = new IMMessage();
        Date time = Calendar.getInstance().getTime();
        time.setTime(time.getTime()+ CommonConfig.divideTime);
        String id = UUID.randomUUID().toString();
        message.setId(id);
        message.setMessageID(id);
        message.setType(ConversitionType.MSG_TYPE_CHAT);
        message.setFromID(chatView.getFromId());
        message.setToID(chatView.getToId());
        message.setMessageID(id);
        message.setTime(time);
        message.setDirection(IMMessage.DIRECTION_SEND);
        message.setIsRead(1);
        message.setIsRead(IMMessage.MSG_READ);
        message.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
        message.setConversationID(chatView.getToId());
        return message;
    }
}