package com.qunar.im.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.IMGroup;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.RevokeInfo;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hubin on 2017/8/25.
 */

public class PbParseUtil {

    private static final String CHAT = "chat";
    private static final String GROUP = "groupchat";

    //解析撤回消息pb
    public static RevokeInfo parseRevokeMessage(ProtoMessageOuterClass.ProtoMessage message) {
        try {
            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(message.getMessage());
            RevokeInfo revokeInfo = new Gson().fromJson(xmppMessage.getBody().getValue(), RevokeInfo.class);
            revokeInfo.setMessageType(xmppMessage.getMessageType() + "");
            revokeInfo.setFrom(QtalkStringUtils.parseIdAndDomain(message.getFrom()));
            revokeInfo.setTo(message.getTo());
            return revokeInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //解析添加好友通知
    public static Nick parseAddFriend(ProtoMessageOuterClass.ProtoMessage message) {
        try {
            ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.parseFrom(message.getMessage());
            ProtoMessageOuterClass.MessageBody messageBody = presenceMessage.getBody();
            List<ProtoMessageOuterClass.StringHeader> list = messageBody.getHeadersList();
            Nick nick = new Nick();
            boolean isSuccess = false;

            for (int i = 0; i < list.size(); i++) {
                ProtoMessageOuterClass.StringHeader stringHeader = list.get(i);
                if (stringHeader.hasDefinedKey()) {
                    if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeResult)) {
                        if ("success".equals(stringHeader.getValue())) {
                            isSuccess = true;
                        } else {
                            isSuccess =false;
                        }
                    }
                }

            }
            if(isSuccess){
                nick.setXmppId(message.getFrom());
                return nick;
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //解析删除好友通知
    public static Nick parseRemoveFriend(ProtoMessageOuterClass.ProtoMessage message) {
        try {
            ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.parseFrom(message.getMessage());
            ProtoMessageOuterClass.MessageBody messageBody = presenceMessage.getBody();
            List<ProtoMessageOuterClass.StringHeader> list = messageBody.getHeadersList();
            Nick nick = new Nick();
            boolean isSuccess = false;
            String jid="";
            String domain="";
            for (int i = 0; i < list.size(); i++) {
                ProtoMessageOuterClass.StringHeader stringHeader = list.get(i);
                if (stringHeader.hasDefinedKey()) {
                    if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeResult)) {
                        if ("success".equals(stringHeader.getValue())) {
                            isSuccess = true;
                        } else {
                            isSuccess =false;
                        }
                    } else if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeJid)) {
                        jid = stringHeader.getValue();
                    } else if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeDomain)) {
                        domain = stringHeader.getValue();
                    }
                }

            }
            if(isSuccess){
                nick.setXmppId(jid+"@"+domain);
                return nick;
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 解析群成员
     *
     * @param message
     * @return
     */
    public static Nick parseMucCard(ProtoMessageOuterClass.ProtoMessage message) {

        try {
            Nick nick = new Nick();
            ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.parseFrom(message.getMessage());
            ProtoMessageOuterClass.MessageBody messageBody = presenceMessage.getBody();
            nick.setGroupId(message.getFrom());
            List<ProtoMessageOuterClass.StringHeader> list = messageBody.getHeadersList();
            for (int i = 0; i < list.size(); i++) {
                ProtoMessageOuterClass.StringHeader stringHeader = list.get(i);
                if (stringHeader.hasDefinedKey()) {
                    if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeNick)) {
                        nick.setName(stringHeader.getValue());
                    } else if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeTitle)) {
                        nick.setTopic(stringHeader.getValue());
                    } else if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypePic)) {
                        nick.setHeaderSrc(UrlCheckUtil.checkUrlForHttp(QtalkNavicationService.getInstance().getInnerFiltHttpHost(), stringHeader.getValue()));
                    } else if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeVersion)) {
                        nick.setLastUpdateTime(stringHeader.getValue());
                    }
                }
                if (stringHeader.hasKey()) {
                    if (stringHeader.getKey().equals("desc")) {
                        nick.setIntroduce(stringHeader.getValue());
                    }
                }
            }
            nick.setExtendedFlag("");
            return nick;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解析删除成员消息
     *
     * @param message
     * @return
     */
    public static GroupMember parseDeleteMucMember(ProtoMessageOuterClass.ProtoMessage message) {
        try {
            GroupMember gm = new GroupMember();
            ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.parseFrom(message.getMessage());
            ProtoMessageOuterClass.MessageBody messageBody = presenceMessage.getBody();
            gm.setGroupId(message.getFrom());
            List<ProtoMessageOuterClass.StringHeader> list = messageBody.getHeadersList();

            for (int i = 0; i < list.size(); i++) {
                ProtoMessageOuterClass.StringHeader stringHeader = list.get(i);
                if (stringHeader.hasDefinedKey()) {
                    if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeDeleleJid)) {
                        gm.setMemberId(stringHeader.getValue());
                    }
                }
            }

            return gm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析销毁群组消息
     *
     * @param message
     * @return
     */
    public static IMGroup parseDeleteMuc(ProtoMessageOuterClass.ProtoMessage message) {
        try {
            IMGroup img = new IMGroup();
            ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.parseFrom(message.getMessage());
            ProtoMessageOuterClass.MessageBody messageBody = presenceMessage.getBody();
            img.setGroupId(QtalkStringUtils.parseIdAndDomain(message.getFrom()));
            List<ProtoMessageOuterClass.StringHeader> list = messageBody.getHeadersList();

            for (int i = 0; i < list.size(); i++) {
                ProtoMessageOuterClass.StringHeader stringHeader = list.get(i);
                if(stringHeader.hasKey()){
                    if("mucname".equals(stringHeader.getKey())){
                        String value = stringHeader.getValue();
                        if(!TextUtils.isEmpty(value)){
                            img.setName(value);
                        }
                    }
                }
//                if (stringHeader.hasDefinedKey()) {
//                    if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeDeleleJid)) {
//                        img.setMemberId(stringHeader.getValue());
//                    }
//                }
            }

            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解析成员加入广播
     *
     * @param message
     * @return
     */
    public static GroupMember parseInviteMucMember(ProtoMessageOuterClass.ProtoMessage message) {
        try {
            GroupMember gm = new GroupMember();
            ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.parseFrom(message.getMessage());
            ProtoMessageOuterClass.MessageBody messageBody = presenceMessage.getBody();
            gm.setGroupId(message.getFrom());
            List<ProtoMessageOuterClass.StringHeader> list = messageBody.getHeadersList();
            for (int i = 0; i < list.size(); i++) {
                ProtoMessageOuterClass.StringHeader stringHeader = list.get(i);
                if (stringHeader.hasDefinedKey()) {
                    if (stringHeader.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeInviteJid)) {
                        gm.setMemberId(stringHeader.getValue());
                    }
                }
            }
            gm.setAffiliation(2 + "");
            gm.setLastUpdateTime(String.valueOf(presenceMessage.getReceivedTime()));
            return gm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析收到的群组消息
     *
     * @param protoMessage
     * @param readState
     * @param messageState
     * @return
     */
    public static IMMessage parseReceiveGroupChatMessage(ProtoMessageOuterClass.ProtoMessage protoMessage, String readState, String messageState) {
        try {


            IMMessage imMessage = new IMMessage();

            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());
//            String myNickName = CurrentPreference.getInstance().getUserName();

            //获取xmppid
            String xmppid = QtalkStringUtils.parseIdAndDomain(protoMessage.getFrom());
            //获取发送消息人名
            String nickName = QtalkStringUtils.parseNickName(protoMessage.getFrom());
//            Nick nick = IMDatabaseManager.getInstance().selectUserByName(nickName);
            String realFrom = protoMessage.getRealfrom();
            imMessage.setUserId(realFrom);
//            imMessage.setRealfrom(realFrom);
//            if (nick != null) {
//                imMessage.setUserId(nick.getXmppId());
//            } else {
//                imMessage.setUserId(nickName);
//            }
            List<ProtoMessageOuterClass.StringHeader> stringHeaders = xmppMessage.getBody().getHeadersList();
            //  imMessage.setExt("");

            for (int i = 0; i < stringHeaders.size(); i++) {
                ProtoMessageOuterClass.StringHeader sh = stringHeaders.get(i);

                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)) {
                    imMessage.setExt(sh.getValue());
//                     = sh.getValue();
                } else if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeBackupInfo)) {
                    imMessage.setExt(sh.getValue());
                }

            }
            //设置中文名
            imMessage.setNickName(nickName);
            //// TODO: 2017/8/21 后面应该有判断是左边 还是右边的方法
            //设置消息id
            imMessage.setMessageID(xmppMessage.getMessageId());
            imMessage.setId(xmppMessage.getMessageId());
            //设置xmppid
            imMessage.setConversationID(xmppid);
            imMessage.setRealfrom(TextUtils.isEmpty(realFrom) ? nickName : realFrom);
            //设置from 群组消息的from都是名字
            imMessage.setFromID(TextUtils.isEmpty(realFrom) ? nickName : realFrom);
            imMessage.setToID("");
            //设置文本消息
            imMessage.setBody(xmppMessage.getBody().getValue());
            //platfrom
            imMessage.setMaType(String.valueOf(xmppMessage.getClientType()));
            //设置消息类型
            int type = xmppMessage.getMessageType();
            imMessage.setMsgType(type);
            //设置消息状态
            imMessage.setReadState(Integer.parseInt(readState));
            //设置方向
            if (realFrom.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                imMessage.setDirection(1);
                imMessage.setIsRead(1);
            } else {
                imMessage.setDirection(0);
                imMessage.setIsRead(Integer.parseInt(messageState));

            }
            imMessage.setMessageState(Integer.parseInt(messageState));
            if (type == -1 || type == 15) {
                imMessage.setDirection(2);
            }

            if (protoMessage.getSignalType() == ProtoMessageOuterClass.SignalType.SignalTypeChat_VALUE) {
//                imMessage.setSignalType(ConversitionType.MSG_TYPE_CHAT);
                imMessage.setType(ConversitionType.MSG_TYPE_CHAT);
            } else if (protoMessage.getSignalType() == ProtoMessageOuterClass.SignalType.SignalTypeGroupChat_VALUE) {
//                imMessage.setSignalType(ConversitionType.MSG_TYPE_GROUP);
                imMessage.setType(ConversitionType.MSG_TYPE_GROUP);
            } else {
//                imMessage.setSignalType(ConversitionType.MSG_TYPE_CHAT);
                imMessage.setType(ConversitionType.MSG_TYPE_CHAT);
            }

            //设置消息时间
            imMessage.setTime(new Date(xmppMessage.getReceivedTime()));
            //设置消息字符串
            imMessage.setMessageRaw(String.valueOf(xmppMessage.toByteString()));


            return imMessage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 收到pb消息 阅读状态解析
     *
     * @param protoMessage
     * @return
     */
    public static IMMessage parseReceiveReadMessage(ProtoMessageOuterClass.ProtoMessage protoMessage) {
        try {
            IMMessage imMessage = new IMMessage();

            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());
            ProtoMessageOuterClass.MessageBody messageBody = xmppMessage.getBody();
            List<ProtoMessageOuterClass.StringHeader> stringHeaders = xmppMessage.getBody().getHeadersList();
            //  imMessage.setExt("");

            imMessage.setSignalType(protoMessage.getSignalType());
            for (int i = 0; i < stringHeaders.size(); i++) {
                ProtoMessageOuterClass.StringHeader sh = stringHeaders.get(i);
                //如果为真,证明为抄送类型
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeReadType)) {
                    if ((MessageStatus.STATUS_SINGLE_DELIVERED + "").equals(sh.getValue())) {
                        imMessage.setReadState(MessageStatus.REMOTE_STATUS_CHAT_DELIVERED);
                        imMessage.setCollectionType(ConversitionType.MSG_TYPE_CHAT);
                    } else if ((MessageStatus.STATUS_SINGLE_READED + "").equals(sh.getValue())) {
                        imMessage.setReadState(MessageStatus.REMOTE_STATUS_CHAT_READED);
                        imMessage.setCollectionType(ConversitionType.MSG_TYPE_CHAT);
                    }else if((MessageStatus.STATUS_SINGLE_OPERATION+"").equals(sh.getValue())){
                        imMessage.setReadState((MessageStatus.REMOTE_STATUS_CHAT_OPERATION|MessageStatus.REMOTE_STATUS_CHAT_READED));
                        imMessage.setCollectionType(ConversitionType.MSG_TYPE_CHAT);
                    } else if ((MessageStatus.STATUS_GROUP_READED + "").equals(sh.getValue())) {
                        imMessage.setReadState(MessageStatus.REMOTE_STATUS_GROUP_READED);
                        imMessage.setCollectionType(ConversitionType.MSG_TYPE_GROUP);
                    }
                }
                //强化消息字段储存
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)) {
                    imMessage.setExt(sh.getValue());
                }
            }
            imMessage.setNewReadList(new JSONArray(messageBody.getValue()));
            //得到from
            String to = QtalkStringUtils.parseIdAndDomain(protoMessage.getTo());
            //得到to
            String from = QtalkStringUtils.parseIdAndDomain(protoMessage.getFrom());
            //设置消息id
            imMessage.setId(xmppMessage.getMessageId());
            imMessage.setMessageID(xmppMessage.getMessageId());
            imMessage.setTime(new Date(xmppMessage.getReceivedTime()));
            if (from.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                if(imMessage.getCollectionType()==ConversitionType.MSG_TYPE_GROUP){
                    imMessage.setConversationID(imMessage.getExt());
                    imMessage.setUserId(to);
                }else{
                    imMessage.setConversationID(to);
                    imMessage.setUserId(to);
                }
            } else {
                imMessage.setConversationID(from);
                imMessage.setUserId(from);
            }
            return imMessage;


        } catch (Exception e) {
            Logger.i(e + "");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 收到pb消息解析成IMMessage消息
     *
     * @param protoMessage
     * @param readState
     * @param messageState
     * @return
     */
    public static IMMessage parseReceiveChatMessage(ProtoMessageOuterClass.ProtoMessage protoMessage, String readState, String messageState) {
        try {
            IMMessage imMessage = new IMMessage();

            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());

            //获得当前消息载体是否为抄送类型
            //先获取头部信息列表
            boolean carbon = false;
            List<ProtoMessageOuterClass.StringHeader> stringHeaders = xmppMessage.getBody().getHeadersList();
            //  imMessage.setExt("");

            imMessage.setSignalType(protoMessage.getSignalType());
            for (int i = 0; i < stringHeaders.size(); i++) {
                ProtoMessageOuterClass.StringHeader sh = stringHeaders.get(i);
                //如果为真,证明为抄送类型
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeCarbon)) {
                    carbon = true;
                    imMessage.setCarbon(true);
                }
                //强化消息字段储存
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)) {
                    imMessage.setExt(sh.getValue());
//                     = sh.getValue();
                }
                //自动回复消息
                if (sh.getKey().equals("auto_reply")) {
                    imMessage.setAuto_reply(Boolean.parseBoolean(sh.getValue()));
                }
                //chatid 已经废弃了.  现在改为使用QchatId
                //cousult消息时 获取qchatid
//                if(sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeChatId)){
//                    imMessage.setQchatid(sh.getValue());
//                }
                if (sh.getKey().equals("qchatid")) {
                    imMessage.setQchatid(sh.getValue());
                }

            }
            //得到from
            String to = QtalkStringUtils.parseIdAndDomain(protoMessage.getTo());
            //得到to
            String from = QtalkStringUtils.parseIdAndDomain(protoMessage.getFrom());
            //设置消息id
            imMessage.setId(xmppMessage.getMessageId());
            imMessage.setMessageID(xmppMessage.getMessageId());
            //设置消息列表会话id
            if (!carbon) {
                if (from.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    imMessage.setConversationID(to);
                    imMessage.setUserId(to);
//                    stat.bindString(2, to);
                } else {
                    imMessage.setConversationID(from);
                    imMessage.setUserId(from);
//                    stat.bindString(2, from);
                }
            } else {
                imMessage.setConversationID(from);
                imMessage.setUserId(from);
//                stat.bindString(2, from);
            }
            int type = ConversitionType.getConversitionType(protoMessage.getSignalType(), imMessage.getQchatid());
            String realfrom = QtalkStringUtils.parseIdAndDomain(protoMessage.getRealfrom());
            if(type == ConversitionType.MSG_TYPE_GROUP){//群 的realfrom 取sendjid
                realfrom = QtalkStringUtils.parseIdAndDomain(protoMessage.getSendjid());
            }
            String realto = QtalkStringUtils.parseIdAndDomain(protoMessage.getRealto());
            //判断真实id是否存在
            if (protoMessage.getSignalType() != ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE) {
                if (!carbon) {
                    if (from.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                        imMessage.setRealfrom(to);
                        imMessage.setRealto(from);
                    } else {
                        imMessage.setRealfrom(from);
                        imMessage.setRealto(to);
                    }
                } else {
                    imMessage.setRealfrom(from);
                    imMessage.setRealto(to);
                }
            } else {
                if (!carbon) {
                    if (realfrom.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                        imMessage.setRealfrom(realto);
                        imMessage.setRealto(realfrom);
                    } else {
                        if(imMessage.getQchatid().equalsIgnoreCase(ConversitionType.MSG_TYPE_CONSULT_SERVER + "")) {
                            imMessage.setRealfrom(from);
                            imMessage.setRealto(realto);
                        }else {
                            imMessage.setRealfrom(realfrom);
                            imMessage.setRealto(realto);
                        }
                    }
                } else {
                    imMessage.setRealfrom(realfrom);
                    imMessage.setRealto(realto);
                }
//                imMessage.setRealfrom(realfrom);
            }

            imMessage.setType(type);


            Logger.i("截取后的字段:" + from);
            if (!carbon) {
                //设置from
                imMessage.setFromID(from);
                //设置to
                imMessage.setToID(to);
            } else {
                //设置from 因为是抄送,所以应该从自己发出
                imMessage.setFromID(to);
                //设置to 因为是抄送,所以应该是给对方
                imMessage.setToID(from);
            }

            //设置消息文本
            imMessage.setBody(xmppMessage.getBody().getValue());
            //platFrom 平台 android 4
            imMessage.setMaType(String.valueOf(xmppMessage.getClientType()));
            //设置消息类型
            imMessage.setMsgType(xmppMessage.getMessageType());
            //设置消息状态
            imMessage.setReadState(Integer.parseInt(readState));
            //根据发送对象判断显示方向
            //如果不是抄送
            if (!carbon) {
                if (from.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    imMessage.setDirection(1);
                } else {
                    imMessage.setDirection(0);
                }
            } else {
                imMessage.setDirection(1);
            }

            //设置时间
            imMessage.setTime(new Date(xmppMessage.getReceivedTime()));
            //设置已读未读状态
            imMessage.setMessageRaw(String.valueOf(xmppMessage.toByteString()));
            imMessage.setMessageState(Integer.parseInt(messageState));
            if (!carbon) {
                imMessage.setIsRead(Integer.parseInt(messageState));
            } else {
                imMessage.setIsRead(1);
            }
            return imMessage;
            //把message对象存入
//

//            imMessage.getSignalType()


        } catch (Exception e) {
            Logger.i(e + "");
            e.printStackTrace();
        }

        return null;
    }

    public static IMMessage parseReceiveCollectionMessage(ProtoMessageOuterClass.ProtoMessage protoMessage) {
        IMMessage imMessage = new IMMessage();
        try {
            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());
            //获得当前消息载体是否为抄送类型
            //先获取头部信息列表
//            boolean carbon = false;
            List<ProtoMessageOuterClass.StringHeader> stringHeaders = xmppMessage.getBody().getHeadersList();
            //  imMessage.setExt("");
            String type = protoMessage.getOrigintype();
            imMessage.setSignalType(protoMessage.getSignalType());
            for (int i = 0; i < stringHeaders.size(); i++) {
                ProtoMessageOuterClass.StringHeader sh = stringHeaders.get(i);
                //如果为真,证明为抄送类型
//                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeCarbon)) {
//                    carbon = true;
//                    imMessage.setCarbon(true);
//                }
                //强化消息字段储存
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)) {
                    imMessage.setExt(sh.getValue());
//                     = sh.getValue();
                }
                if (sh.getKey().equals("qchatid")) {
                    imMessage.setQchatid(sh.getValue());
                }

            }
            //得到from
            String to = QtalkStringUtils.parseIdAndDomain(protoMessage.getTo());
            //得到to
            String from = QtalkStringUtils.parseIdAndDomain(protoMessage.getFrom());
            //得到oFrom
            String oFrom = QtalkStringUtils.parseIdAndDomain(protoMessage.getOriginfrom());
            //得到oTo
            String oTo = QtalkStringUtils.parseIdAndDomain(protoMessage.getOriginto());
            //群消息真是发送人
            String realfrom = QtalkStringUtils.parseIdAndDomain(protoMessage.getRealfrom());
            //暂时显示名字
//            String nickName = QtalkStringUtils.parseNickName(protoMessage.getOriginfrom());

            imMessage.setNickName(realfrom);
            //设置消息id
            imMessage.setId(xmppMessage.getMessageId());
            imMessage.setMessageID(xmppMessage.getMessageId());
            //设置消息列表会话id
            imMessage.setFromID(from);
            imMessage.setToID(to);
            imMessage.setoFromId(oFrom);
            imMessage.setoToId(oTo);
            imMessage.setConversationID(from);
            imMessage.setUserId(from);
            imMessage.setRealfrom(realfrom);

            String realto = QtalkStringUtils.parseIdAndDomain(protoMessage.getRealto());
            //判断真实id是否存在
//            if (protoMessage.getSignalType() != ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE) {
//                if (!carbon) {
//            if (from.equals(IMLogicManager.getInstance().getMyself().bareJID().fullname())) {
//                imMessage.setRealfrom(to);
//                imMessage.setRealto(from);
//            } else {
//                imMessage.setRealfrom(from);
//                imMessage.setRealto(to);
//            }
//                } else {
//                    imMessage.setRealfrom(from);
//                    imMessage.setRealto(to);
//                }
//            } else {
//                if (!carbon) {
//                    if (realfrom.equals(IMLogicManager.getInstance().getMyself().bareJID().fullname())) {
//                        imMessage.setRealfrom(realto);
//                        imMessage.setRealto(realfrom);
//                    } else {
//                        imMessage.setRealfrom(realfrom);
//                        imMessage.setRealto(realto);
//                    }
//                } else {
//                    imMessage.setRealfrom(realfrom);
//                    imMessage.setRealto(realto);
//                }
////                imMessage.setRealfrom(realfrom);
//            }

//
            imMessage.setType(ConversitionType.MSG_TYPE_COLLECTION);
            if (CHAT.equals(type)) {
                imMessage.setCollectionType(ConversitionType.MSG_TYPE_CHAT);
            } else if (GROUP.equals(type)) {
                imMessage.setCollectionType(ConversitionType.MSG_TYPE_GROUP);
            }

            //设置消息文本
            imMessage.setBody(xmppMessage.getBody().getValue());
            //platFrom 平台 android 4
            imMessage.setMaType(String.valueOf(xmppMessage.getClientType()));
            //设置消息类型
            imMessage.setMsgType(xmppMessage.getMessageType());
            //设置消息状态
            imMessage.setReadState(1);
            imMessage.setMessageState(MessageStatus.LOCAL_STATUS_SUCCESS_PROCESSION);
            imMessage.setReadState(MessageStatus.REMOTE_STATUS_CHAT_DELIVERED);
            //设置方向
            imMessage.setDirection(0);
            //设置时间
            imMessage.setTime(new Date(xmppMessage.getReceivedTime()));
            //设置已读未读状态
            imMessage.setMessageRaw(String.valueOf(xmppMessage.toByteString()));
            imMessage.setIsRead(0);
            return imMessage;

        } catch (Exception e) {
            Logger.i(e + "");
        }
        return null;
    }


    public static List<Nick> parseGetFriends(ProtoMessageOuterClass.ProtoMessage message) {
        try {
            ProtoMessageOuterClass.IQMessage iqMessage = ProtoMessageOuterClass.IQMessage.parseFrom(message.getMessage());
            ProtoMessageOuterClass.MessageBody body = iqMessage.getBody();
            List<ProtoMessageOuterClass.StringHeader> headerList = body.getHeadersList();
            List<Nick> nicks = new ArrayList<>();
            JSONArray jsonArray = null;
            for (int i = 0; i < headerList.size(); i++) {
                ProtoMessageOuterClass.StringHeader sh = headerList.get(i);
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeFriends)) {
                    jsonArray = new JSONArray(sh.getValue());
                }
            }
            if (jsonArray == null || jsonArray.length() <= 0) {
                return null;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jb = jsonArray.getJSONObject(i);
                Nick n = new Nick();
                n.setXmppId(jb.getString("F") + "@" + jb.getString("H"));
                n.setUserId(jb.getString("F"));
                nicks.add(n);
            }
            return nicks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> parseVerifyFriendMode(ProtoMessageOuterClass.ProtoMessage message) {
        Map<String, String> mode = new HashMap<>();
        try {
            ProtoMessageOuterClass.IQMessage iqMessage = ProtoMessageOuterClass.IQMessage.parseFrom(message.getMessage());
            ProtoMessageOuterClass.MessageBody body = iqMessage.getBody();
            List<ProtoMessageOuterClass.StringHeader> headerList = body.getHeadersList();
            String jid = "";
            String value = "";
            String Question="";
            for (int i = 0; i < headerList.size(); i++) {
                ProtoMessageOuterClass.StringHeader sh = headerList.get(i);
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeJid)) {
                    jid = sh.getValue();
                } else if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeMode)) {
                    value = sh.getValue();
                }else if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeQuestion)){
                    Question = sh.getValue();
                }
            }
            mode.put(jid, value);
            if(!TextUtils.isEmpty(Question)){
                mode.put("Question",Question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mode;
    }

    public static IMMessage parseErrorMessage(ProtoMessageOuterClass.ProtoMessage protoMessage){
        IMMessage imMessage = new IMMessage();
        try {
            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());
            List<ProtoMessageOuterClass.StringHeader> stringHeaders = xmppMessage.getBody().getHeadersList();
            imMessage.setSignalType(protoMessage.getSignalType());
            for (int i = 0; i < stringHeaders.size(); i++) {
                ProtoMessageOuterClass.StringHeader sh = stringHeaders.get(i);
                //强化消息字段储存
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)) {
                    imMessage.setExt(sh.getValue());
                }
                if ("errcode".equals(sh.getKey()) && "406".equals(sh.getValue())) {
                    //设置消息状态
                    imMessage.setIsRead(1);
                    imMessage.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    imMessage.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
                }

            }
            //得到from
            String to = QtalkStringUtils.parseIdAndDomain(protoMessage.getTo());
            //得到to
            String from = QtalkStringUtils.parseIdAndDomain(protoMessage.getFrom());
            //得到oFrom
            String oFrom = QtalkStringUtils.parseIdAndDomain(protoMessage.getOriginfrom());
            //得到oTo
            String oTo = QtalkStringUtils.parseIdAndDomain(protoMessage.getOriginto());
            //群消息真是发送人
            String realfrom = QtalkStringUtils.parseIdAndDomain(protoMessage.getRealfrom());
            //暂时显示名字
//            String nickName = QtalkStringUtils.parseNickName(protoMessage.getOriginfrom());

            imMessage.setNickName(realfrom);
            //设置消息id
            imMessage.setId(xmppMessage.getMessageId());
            imMessage.setMessageID(xmppMessage.getMessageId());
            //设置消息列表会话id
            imMessage.setFromID(from);
            imMessage.setToID(to);
            imMessage.setoFromId(oFrom);
            imMessage.setoToId(oTo);
            imMessage.setConversationID(from);
            imMessage.setUserId(from);
            imMessage.setRealfrom(realfrom);

            //设置消息文本
            imMessage.setBody(xmppMessage.getBody().getValue());
            //platFrom 平台 android 4
            imMessage.setMaType(String.valueOf(xmppMessage.getClientType()));
            //设置消息类型
            imMessage.setMsgType(xmppMessage.getMessageType());

            imMessage.setType(ConversitionType.MSG_TYPE_CHAT);
            //设置方向
            imMessage.setDirection(1);
            //设置时间
            imMessage.setTime(new Date(xmppMessage.getReceivedTime()));
            //设置已读未读状态
            imMessage.setMessageRaw(String.valueOf(xmppMessage.toByteString()));
            return imMessage;

        } catch (Exception e) {
            Logger.i(e + "");
        }
        return null;
    }

    public static GroupMember parseGroupAffiliation(ProtoMessageOuterClass.ProtoMessage message){
        try{
            ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.parseFrom(message.getMessage());
            GroupMember member = new GroupMember();
            List<ProtoMessageOuterClass.StringHeader> headerList = presenceMessage.getBody().getHeadersList();
            String userId = "";
            String domain = "";
            String affiliation = "";
            String groupId = QtalkStringUtils.parseIdAndDomain(message.getFrom());
            long lastUodateTime = presenceMessage.getReceivedTime();
            if(headerList != null){
                for(ProtoMessageOuterClass.StringHeader header : headerList){
                    if(header.hasDefinedKey()){
                        if(header.getDefinedKey() == ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeJid){
                            userId = header.getValue();
                        }else if(header.getDefinedKey() == ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeAffiliation){
                            affiliation = header.getValue();
                        }else if(header.getDefinedKey() == ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeDomain){
                            domain = header.getValue();
                        }
                    }
                }
                if(!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(domain) && !TextUtils.isEmpty(affiliation)){
                    String xmppid = userId + "@" + domain;
                    int per = "admin".equals(affiliation) ? 1 : 2;
                    member.setAffiliation(String.valueOf(per));
                    member.setGroupId(groupId);
                    member.setMemberId(xmppid);
                    member.setLastUpdateTime(String.valueOf(lastUodateTime));
                }

            }
            return member;
        }catch (Exception e){

        }
        return null;

    }


}
