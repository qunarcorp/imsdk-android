package com.qunar.im.ui.presenter.impl;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.SetMucVCardResult;
import com.qunar.im.ui.presenter.IChatroomCreatedPresenter;
import com.qunar.im.ui.presenter.views.IChatroomCreatedView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.SetMucVCardData;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by xinbo.wang on 2015/4/10.
 * 群成员创建逻辑
 */
public class ChatroomCreatedPresenter implements IChatroomCreatedPresenter, IMNotificaitonCenter.NotificationCenterDelegate {
    IChatroomCreatedView chatroomCreatedView;

    public ChatroomCreatedPresenter() {
        ConnectionUtil.getInstance().addEvent(this, QtalkEvent.IQ_CREATE_MUC);

    }

    @Override
    public void setView(IChatroomCreatedView view) {
        chatroomCreatedView = view;
    }

    @Override
    public void createChatroom() {
        String id = UUID.randomUUID().toString().replace("-", "");
        final String roomId = QtalkStringUtils.roomId2Jid(id);
        ConnectionUtil.getInstance().createGroup(roomId);

    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        Logger.i("收到创建群返回:" + key);
        switch (key) {
            case QtalkEvent.IQ_CREATE_MUC:
                //收到创建群的返回,在sdk中判断了是否成功
                //成功才返回,这里直接返回创建群的id在根据id去设
                // 置一次群名片
                final String groupId = (String) args[0];
                SetMucVCardData setMucVCardData = new SetMucVCardData();
                setMucVCardData.muc_name = groupId;
                setMucVCardData.desc = "没有公告";
                setMucVCardData.nick = "群组";
                setMucVCardData.title = "欢迎加入";
//                //这张图片是各大群都默认的图,我也用!
                setMucVCardData.pic = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/2227ff2e304cb44a1980e9c1a3d78164.png";
                List<SetMucVCardData> list = new ArrayList<>();
                list.add(setMucVCardData);

                HttpUtil.setMucVCard(list, new ProtocolCallback.UnitCallback<SetMucVCardResult>() {
                    @Override
                    public void onCompleted(SetMucVCardResult setMucVCardResult) {
                        if (setMucVCardResult != null && setMucVCardResult.data != null && setMucVCardResult.data.size() > 0) {
                            chatroomCreatedView.setResult(true, groupId);
                        }
                    }

                    @Override
                    public void onFailure(String errMsg) {
//                        chatroomCreatedView.setResult(true, groupId);
                    }
                });
                break;
//            case QtalkEvent.Muc_Invite_User_V2:
//                chatroomCreatedView
//                break;
        }
//        final ProtoMessageOuterClass.ProtoMessage message = (ProtoMessageOuterClass.ProtoMessage) args[0];
//        ProtoMessageOuterClass.IQMessage iq = null;
//        try {
//            iq = ProtoMessageOuterClass.IQMessage.parseFrom(message.getMessage());
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//        ConnectionUtil.getInstance(QunarIMApp.getContext()).workworldremoveEvent(this, QtalkEvent.IQ_CREATE_MUC);
//
//        if(iq != null && iq.getBody().getValue().equals("success")){
//            String name = chatroomCreatedView.getChatrooName();
//            String subject = chatroomCreatedView.getSubject();
//            boolean isPersist = chatroomCreatedView.isPersist();
//            if(TextUtils.isEmpty(name))
//            {
//                name = "讨论组";
//            }
//            if(subject == null)
//            {
//                subject = "";
//            }
//            // TODO: 2017/8/24 新版数据库有改变
////            final ChatRoom cr = new ChatRoom();
////            cr.setJid(message.getFrom());
////            cr.setName(name);
////            cr.setSubject(subject);
////            cr.setIsPersistent(isPersist ? ChatRoom.YES : ChatRoom.NO);
////            cr.setIsSubjectModifiable(ChatRoom.YES);
////            cr.setIsJoined(ChatRoom.JOINED);
////            dataModel.updateChatRoom(cr);
//
////            RecentConversation rc = new RecentConversation();
////            rc.setId(message.getFrom());
////            rc.setConversationType(ConversitionType.MSG_TYPE_GROUP);
////            recentConvDataModel.insertRecentConvToLocal(rc);
//            IMSessionList session = new IMSessionList();
//            session.setXmppId(message.getFrom());
//            session.setRealJid(message.getFrom());
//            session.setUserId(message.getFrom());
//            session.setLastMessageId("");
//            session.setExtendedFlag("");
//            session.setChatType(String.valueOf(ConversitionType.MSG_TYPE_GROUP));
//            session.setLastUpdateTime(String.valueOf(iq.getReceivedTime()));
//            IMDatabaseManager.getInstance().InsertSessionList(session);
//            SetMucVCardData data = new SetMucVCardData();
//            data.muc_name = message.getFrom();
//            data.nick = name;
//            data.desc = "";
//            data.title = subject;
//            data.pic = "";
//            List<SetMucVCardData> datas = new ArrayList<SetMucVCardData>();
//            datas.add(data);
////            Nick data = new Nick();
////            data.setName(name);
////            data.setTopic(subject);
////            data.setGroupId(message.getFrom());
////            data.setIntroduce("");
////            List<Nick> datas = new ArrayList<Nick>();
////            datas.add(data);
////            IMDatabaseManager.getInstance().updateMucCard(datas);
//            VCardAPI.setMucVCard(datas, new ProtocolCallback.UnitCallback<SetMucVCardResult>() {
//                @Override
//                public void onCompleted(SetMucVCardResult s) {
////                    if (s != null && s.data != null && s.data.size() > 0) {
////                        cr.setVersion(Integer.valueOf(s.data.get(0).get("version")));
////                        dataModel.updateChatRoom(cr);
////                    }
//                    chatroomCreatedView.setResult(true, message.getFrom());
//                }
//
//                @Override
//                public void onFailure() {
//                    chatroomCreatedView.setResult(true, message.getFrom());
//                }
//            });
//        }
//        else {
//            chatroomCreatedView.setResult(false,message.getFrom());
//        }
    }
}
