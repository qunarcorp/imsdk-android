package com.qunar.im.ui.presenter.impl;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.IHandleVoiceMsgPresenter;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.structs.TransitSoundJSON;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by huayu.chen on 2016/6/23.
 */
public class HandleVoiceMsgPresenter implements IHandleVoiceMsgPresenter, IMNotificaitonCenter.NotificationCenterDelegate{
    private int pointer = 0;
    private List<IMMessage> messageList= new ArrayList<>();

    public HandleVoiceMsgPresenter(){
        addEvent();
    }

    @Override
    public void start(long time,String convId) {
        pointer = 0;
        List<IMMessage> msgList = ConnectionUtil.getInstance().searchVoiceMsg(convId, time, ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE);
        for(IMMessage msg:msgList)
        {
            TransitSoundJSON json = JsonUtils.getGson().fromJson(msg.getBody(),TransitSoundJSON.class);
            if(TransitSoundJSON.PLAYED!=json.s)
            {
                messageList.add(msg);
            }
        }
    }
    public void addEvent(){
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Chat_Message_Text);
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.Group_Chat_Message_Text);
    }
    @Override
    public void shutdown() {
        messageList.clear();

        ConnectionUtil.getInstance().removeEvent(this, QtalkEvent.Chat_Message_Text);
        ConnectionUtil.getInstance().removeEvent(this, QtalkEvent.Group_Chat_Message_Text);
    }

    @Override
    public IMMessage next() {
        if(pointer<messageList.size())
        {
            return messageList.get(pointer++);
        }
        return null;
    }

    public void onEvent(final EventBusEvent.HasNewMessageEvent event) {

    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key) {
            //收到新消息
            case QtalkEvent.Chat_Message_Text:
                IMMessage imMessageChat = (IMMessage) args[0];
                if(imMessageChat!=null&&imMessageChat.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE){
                    messageList.add(imMessageChat);
                }

                break;
            //收到群组新消息
            case QtalkEvent.Group_Chat_Message_Text:
                IMMessage imMessageGroup = (IMMessage) args[0];
                imMessageGroup.setType(ConversitionType.MSG_TYPE_GROUP);
                if(imMessageGroup!=null&&imMessageGroup.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE){
                    messageList.add(imMessageGroup);
                }
                break;
        }
    }
}
