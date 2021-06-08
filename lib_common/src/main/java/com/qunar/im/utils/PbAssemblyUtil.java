package com.qunar.im.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.entity.XMPPJID;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by hubin on 2017/8/15.
 */

//拼接pb消息工具类
public class PbAssemblyUtil {

    //根据immessage消息转换成发送正在输入状态消息
    public static ProtoMessageOuterClass.ProtoMessage getTypingStatusMessage(IMMessage imMessage) {
        ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder()
                //设置消息正文 就是空的
                .setValue("")
                .build();
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置消息类型
                .setMessageType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE)
                //设置客户端类型
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本
                .setClientVersion(0)
                //设置messageid
                .setMessageId("")
                //设置消息时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                //设置body
                .setBody(messageBody)
                .build();
        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置fromId
                .setFrom(imMessage.getFromID())
                //设置toId
                .setTo(imMessage.getToID())
                //这个数据意图不明显,接收到的消息都有
                .setOptions(0)
                //设置消息类型 正在输入中
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeTyping_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
                //编译对象
                .build();
        return protoMessage;
    }


    public static ProtoMessageOuterClass.ProtoMessage getRevokeMessage(IMMessage imMessage) {
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        ProtoMessageOuterClass.MessageBody.Builder builder = ProtoMessageOuterClass.MessageBody.newBuilder();

        ProtoMessageOuterClass.StringHeader qchatId;
        ProtoMessageOuterClass.StringHeader keyQchatId;
        if (!TextUtils.isEmpty(imMessage.getQchatid())) {
            qchatId = ProtoMessageOuterClass.StringHeader.newBuilder()
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeChatId)
                    .setValue(imMessage.getQchatid())
                    .build();
            keyQchatId = ProtoMessageOuterClass.StringHeader.newBuilder()
                    .setKey("qchatid")
                    .setValue(imMessage.getQchatid())
                    .build();
            builder.addHeaders(qchatId);
            builder.addHeaders(keyQchatId);
        }
        ProtoMessageOuterClass.StringHeader consultInfo;
        if (imMessage.getConsultInfo() != null) {
            consultInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeChannelId)
                    .setValue(new Gson().toJson(imMessage.getConsultInfo()))
                    .build();
            builder.addHeaders(consultInfo);
        }

        //设置消息正文
        ProtoMessageOuterClass.MessageBody messageBody = builder.setValue(imMessage.getBody())
                .build();

        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置消息id 这里设置要撤销的那条消息的id
                .setMessageId(imMessage.getId())
                //设置消息类型
                .setMessageType(imMessage.getMsgType())
                //设置客户端类型
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置版本
                .setClientVersion(0)
                //设置消息时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                //设置消息体
                .setBody(messageBody)
                .build();
        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //?
                .setOptions(0)
                //设置撤回消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeRevoke_VALUE)
                //设置发送人
                .setFrom(imMessage.getFromID())
                //设置接收人
                .setTo(imMessage.getToID())
                //设置消息
                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
                .build();

        if (!TextUtils.isEmpty(imMessage.getRealfrom())
                && !TextUtils.isEmpty(imMessage.getRealto())) {

            protoMessage = protoMessage.toBuilder()
                    .setRealfrom(imMessage.getRealfrom())
                    .setRealto(imMessage.getRealto())
                    .build();
        }

        return protoMessage;
    }


    //根据IMMessage类型转换成ProtoMessage 纯文本消息
    public static ProtoMessageOuterClass.ProtoMessage getGroupTextOrEmojiMessage(IMMessage imMessage) {
        ProtoMessageOuterClass.MessageBody.Builder builder = ProtoMessageOuterClass.MessageBody.newBuilder();
        ProtoMessageOuterClass.StringHeader extendInfo;
        ProtoMessageOuterClass.StringHeader backUpInfo;
        if (!TextUtils.isEmpty(imMessage.getExt())) {//添加extendinfo
            extendInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    //设置消息头部key 更新已读状态头部key为extendInfo类型
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                    //设置相对应value 更新已读状态头部value为消息是谁发来的
                    .setValue(imMessage.getExt())
                    .build();
            builder.addHeaders(extendInfo);
        }
        if(!TextUtils.isEmpty(imMessage.getBackUp())){
            backUpInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    //设置消息头部key 更新已读状态头部key为extendInfo类型
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeBackupInfo)
//                .setKey("extendInfo")
                    //设置相对应value 更新已读状态头部value为消息是谁发来的
                    .setValue(imMessage.getBackUp())
                    .build();
            builder.addHeaders(backUpInfo);
        }

        builder.setValue(imMessage.getBody());
        ProtoMessageOuterClass.MessageBody messageBody = builder.build();
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        //获得随机messageId
        String id = imMessage.getId();
        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置消息类型!
                .setMessageType(imMessage.getMsgType())
                //设置客户端类型 androidsdk 这个是1
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本? 暂时先写个0
                .setClientVersion(0)
                //设置消息发送时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                //设置随机Id
                .setMessageId(id)
                //设置消息体
                .setBody(messageBody)
                //编译
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置fromId
                .setFrom(imMessage.getFromID())
                //设置toId
                .setTo(imMessage.getToID())
                //这个数据意图不明显,接收到的消息都有
                .setOptions(0)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeGroupChat_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
                //编译对象
                .build();
//
//
//
////
        return protoMessage;
//        return null;
    }

    /**
     * 发送错误方法单独使用
     * @param userid
     * @param str
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage getTextErrorMessage(String userid,String str) {
        ProtoMessageOuterClass.MessageBody.Builder builder = ProtoMessageOuterClass.MessageBody.newBuilder();




        builder.setValue(str+"|sendId:"+userid);
        ProtoMessageOuterClass.MessageBody messageBody = builder.build();
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        //获得随机messageId
        String id = UUID.randomUUID()+"";
        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置消息类型!
                .setMessageType(1)
                //设置客户端类型 androidsdk 这个是1
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本? 暂时先写个0
                .setClientVersion(0)
                //设置消息发送时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                //设置随机Id
                .setMessageId(id)
                //设置消息体
                .setBody(messageBody)
                //编译
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置fromId
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                //设置toId
                .setTo(userid+"@"+ QtalkNavicationService.getInstance().getXmppdomain())
                //这个数据意图不明显,接收到的消息都有
                .setOptions(0)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeChat_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
                //编译对象
                .build();


//
//
//
////
        return protoMessage;
//        return null;
    }

    //根据IMMessage类型转换成ProtoMessage 纯文本消息
    public static ProtoMessageOuterClass.ProtoMessage getTextOrEmojiMessage(IMMessage imMessage) {
        ProtoMessageOuterClass.MessageBody.Builder builder = ProtoMessageOuterClass.MessageBody.newBuilder();


        ProtoMessageOuterClass.StringHeader extendInfo;
        ProtoMessageOuterClass.StringHeader backUpInfo;
        if (!TextUtils.isEmpty(imMessage.getExt())) {//添加extendinfo
            extendInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    //设置消息头部key 更新已读状态头部key为extendInfo类型
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                    //设置相对应value 更新已读状态头部value为消息是谁发来的
                    .setValue(imMessage.getExt())
                    .build();
            builder.addHeaders(extendInfo);
        }
        if(!TextUtils.isEmpty(imMessage.getBackUp())){
            backUpInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    //设置消息头部key 更新已读状态头部key为extendInfo类型
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeBackupInfo)
//                .setKey("extendInfo")
                    //设置相对应value 更新已读状态头部value为消息是谁发来的
                    .setValue(imMessage.getBackUp())
                    .build();
            builder.addHeaders(backUpInfo);
        }
        ProtoMessageOuterClass.StringHeader qchatId;
        ProtoMessageOuterClass.StringHeader keyQchatId;
        if (!TextUtils.isEmpty(imMessage.getQchatid())) {
            qchatId = ProtoMessageOuterClass.StringHeader.newBuilder()
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeChatId)
                    .setValue(imMessage.getQchatid())
                    .build();
            keyQchatId = ProtoMessageOuterClass.StringHeader.newBuilder()
                    .setKey("qchatid")
                    .setValue(imMessage.getQchatid())
                    .build();
            builder.addHeaders(qchatId);
            builder.addHeaders(keyQchatId);
        }
        ProtoMessageOuterClass.StringHeader consultInfo;
        if (imMessage.getConsultInfo() != null) {
            consultInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeChannelId)
                    .setValue(new Gson().toJson(imMessage.getConsultInfo()))
                    .build();
            builder.addHeaders(consultInfo);
        }


        builder.setValue(imMessage.getBody());
        ProtoMessageOuterClass.MessageBody messageBody = builder.build();
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        //获得随机messageId
        String id = imMessage.getId();
        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置消息类型!
                .setMessageType(imMessage.getMsgType())
                //设置客户端类型 androidsdk 这个是1
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本? 暂时先写个0
                .setClientVersion(0)
                //设置消息发送时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                //设置随机Id
                .setMessageId(id)
                //设置消息体
                .setBody(messageBody)
                //编译
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置fromId
                .setFrom(imMessage.getFromID())
                //设置toId
                .setTo(imMessage.getToID())
                //这个数据意图不明显,接收到的消息都有
                .setOptions(0)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeChat_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
                //编译对象
                .build();
        if ((imMessage.getType() == ConversitionType.MSG_TYPE_CONSULT_SERVER
                || imMessage.getType() == ConversitionType.MSG_TYPE_CONSULT) &&
                !TextUtils.isEmpty(imMessage.getRealfrom())
                && !TextUtils.isEmpty(imMessage.getRealto())) {

            imMessage.setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE);
            protoMessage = protoMessage.toBuilder()
                    .setRealfrom(imMessage.getRealfrom())
                    .setRealto(imMessage.getRealto())
                    .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE)
                    .build();
        }
//
//
//
////
        return protoMessage;
//        return null;
    }

    //根据IMMessage类型转换成ProtoMessage 机器人消息
    public static ProtoMessageOuterClass.ProtoMessage getSubscriptionMessage(IMMessage imMessage) {
        ProtoMessageOuterClass.MessageBody.Builder builder = ProtoMessageOuterClass.MessageBody.newBuilder();
        ProtoMessageOuterClass.StringHeader extendInfo;
        if (!TextUtils.isEmpty(imMessage.getExt())) {//添加extendinfo
            extendInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    //设置消息头部key 更新已读状态头部key为extendInfo类型
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                    //设置相对应value 更新已读状态头部value为消息是谁发来的
                    .setValue(imMessage.getExt())
                    .build();
            builder.addHeaders(extendInfo);
        }
        builder.setValue(imMessage.getBody());
        ProtoMessageOuterClass.MessageBody messageBody = builder.build();
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        //获得随机messageId
        String id = imMessage.getId();
        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置消息类型!
                .setMessageType(imMessage.getMsgType())
                //设置客户端类型 androidsdk 这个是1
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本? 暂时先写个0
                .setClientVersion(0)
                //设置消息发送时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                //设置随机Id
                .setMessageId(id)
                //设置消息体
                .setBody(messageBody)
                //编译
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置fromId
                .setFrom(imMessage.getFromID())
                //设置toId
                .setTo(imMessage.getToID())
                //这个数据意图不明显,接收到的消息都有
                .setOptions(0)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeSubscription_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
                //编译对象
                .build();
//
//
//
////
        return protoMessage;
//        return null;
    }

    //根据IMMessage类型转换成ProtoMessage 加密信令消息
    public static ProtoMessageOuterClass.ProtoMessage getEncryptSignalMessage(IMMessage imMessage) {
        ProtoMessageOuterClass.MessageBody.Builder builder = ProtoMessageOuterClass.MessageBody.newBuilder();
        ProtoMessageOuterClass.StringHeader extendInfo;
        if (!TextUtils.isEmpty(imMessage.getExt())) {//添加extendinfo
            extendInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    //设置消息头部key 更新已读状态头部key为extendInfo类型
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                    //设置相对应value 更新已读状态头部value为消息是谁发来的
                    .setValue(imMessage.getExt())
                    .build();
            builder.addHeaders(extendInfo);
        }
        builder.setValue(imMessage.getBody());
        ProtoMessageOuterClass.MessageBody messageBody = builder.build();
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        //获得随机messageId
        String id = imMessage.getId();
        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置消息类型!
                .setMessageType(imMessage.getMsgType())
                //设置客户端类型 androidsdk 这个是1
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本? 暂时先写个0
                .setClientVersion(0)
                //设置消息发送时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                //设置随机Id
                .setMessageId(id)
                //设置消息体
                .setBody(messageBody)
                //编译
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置fromId
                .setFrom(imMessage.getFromID())
                //设置toId
                .setTo(imMessage.getToID())
                //这个数据意图不明显,接收到的消息都有
                .setOptions(0)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeEncryption_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
                //编译对象
                .build();
//
//
//
////
        return protoMessage;
//        return null;
    }

    //根据IMMessage类型转换成ProtoMessage 机器人消息
    public static ProtoMessageOuterClass.ProtoMessage getWebrtcMessage(IMMessage imMessage) {
        ProtoMessageOuterClass.MessageBody.Builder builder = ProtoMessageOuterClass.MessageBody.newBuilder();
        ProtoMessageOuterClass.StringHeader extendInfo;
        if (!TextUtils.isEmpty(imMessage.getExt())) {//添加extendinfo
            extendInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    //设置消息头部key 更新已读状态头部key为extendInfo类型
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                    //设置相对应value 更新已读状态头部value为消息是谁发来的
                    .setValue(imMessage.getExt())
                    .build();
            builder.addHeaders(extendInfo);
        }
        builder.setValue(imMessage.getBody());
        ProtoMessageOuterClass.MessageBody messageBody = builder.build();
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        //获得随机messageId
        String id = imMessage.getId();
        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置消息类型!
                .setMessageType(imMessage.getMsgType())
                //设置客户端类型 androidsdk 这个是1
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本? 暂时先写个0
                .setClientVersion(0)
                //设置消息发送时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                //设置随机Id
                .setMessageId(id)
                //设置消息体
                .setBody(messageBody)
                //编译
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置fromId
                .setFrom(imMessage.getFromID())
                //设置toId
                .setTo(imMessage.getToID())
                //这个数据意图不明显,接收到的消息都有
                .setOptions(0)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeWebRtc_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
                //编译对象
                .build();
        return protoMessage;
    }

    //获取群组消息已读拼接对象
    public static ProtoMessageOuterClass.ProtoMessage getGroupBeenReadMessage(JSONArray jsonArray, String tager, XMPPJID self) {
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        ProtoMessageOuterClass.StringHeader stringHeader1 = ProtoMessageOuterClass.StringHeader.newBuilder()
                //设置消息头部key 更新已读状态头部key为extendInfo类型
                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                //设置相对应value 更新已读状态头部value为消息是谁发来的
                .setValue(tager)
                .build();
        ProtoMessageOuterClass.StringHeader stringHeader2 = ProtoMessageOuterClass.StringHeader.newBuilder()
                //设置消息头部 还需要设置read type
                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeReadType)
//                .setKey("read_type")
                //设置value 因为是群组消息 所以设置为2
                .setValue("2")
                .build();

        ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder()
                //设置消息正文
                .setValue(jsonArray.toString())
                //设置头部
                .addHeaders(stringHeader1)
                .addHeaders(stringHeader2)
                //编译
                .build();

        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置主体
                .setBody(messageBody)
                //设置消息id
                .setMessageId(UUID.randomUUID().toString())
                //设置消息类型
                .setMessageType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE)
                //设置客户端类型
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本
                .setClientVersion(0)
                //设置时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeReadmark_VALUE)
                //设置发送方 此处应为自己
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                //设置接收方 此处应为自己
                .setTo(CurrentPreference.getInstance().getPreferenceUserId())
                //设置Message
                .setMessage(xmppMessage.toByteString())
                .build();
        return protoMessage;
    }

    //获取要发送的已读消息对象
    public static ProtoMessageOuterClass.ProtoMessage getBeenReadMessage(JSONArray jsonArray, String target, XMPPJID self) {
        Logger.i("发送已读状态:" + jsonArray.toString());

        //获取当前时间
        Date time = Calendar.getInstance().getTime();

        ProtoMessageOuterClass.StringHeader stringHeader1 = ProtoMessageOuterClass.StringHeader.newBuilder()
                //设置消息头部key 更新已读状态头部key为extendInfo类型
                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                //设置相对应value 更新已读状态头部value为消息是谁发来的
                .setValue(target)
                .build();
        ProtoMessageOuterClass.StringHeader stringHeader2 = ProtoMessageOuterClass.StringHeader.newBuilder()
                //设置消息头部 还需要设置read type
                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeReadType)
//                .setKey("read_type")
                //设置value
                .setValue("1")
                .build();
        ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder()
                //设置消息正文
                .setValue(jsonArray.toString())
                //设置头部
                .addHeaders(stringHeader1)
                .addHeaders(stringHeader2)
                //编译
                .build();

        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置主体
                .setBody(messageBody)
                //设置消息id
                .setMessageId(UUID.randomUUID().toString())
                //设置消息类型
                .setMessageType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE)
                //设置客户端类型
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本
                .setClientVersion(0)
                //设置时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeReadmark_VALUE)
                //设置发送方 此处应为自己
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                //设置接收方 此处应为自己
                .setTo(CurrentPreference.getInstance().getPreferenceUserId())
                //设置Message
                .setMessage(xmppMessage.toByteString())
                .build();
        return protoMessage;

    }



    //获取要发送的已读消息对象
    public static ProtoMessageOuterClass.ProtoMessage getBeenNewReadStateForTimeMessage(String state,JSONObject messageTime, String target, XMPPJID self) {
//        Logger.i("发送已读状态:" + jsonArray.toString());

        //获取当前时间
        Date time = Calendar.getInstance().getTime();

        ProtoMessageOuterClass.StringHeader stringHeader1 = ProtoMessageOuterClass.StringHeader.newBuilder()
                //设置消息头部key 更新已读状态头部key为extendInfo类型
                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                //设置相对应value 更新已读状态头部value为消息是谁发来的
                .setValue(target)
                .build();
        ProtoMessageOuterClass.StringHeader stringHeader2 = ProtoMessageOuterClass.StringHeader.newBuilder()
                //设置消息头部 还需要设置read type
                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeReadType)
//                .setKey("read_type")
                //设置value
                .setValue(state)
                .build();
        ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder()
                //设置消息正文
                .setValue(messageTime.toString())
                //设置头部
                .addHeaders(stringHeader1)
                .addHeaders(stringHeader2)
                //编译
                .build();

        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置主体
                .setBody(messageBody)
                //设置消息id
                .setMessageId(UUID.randomUUID().toString())
                //设置消息类型
                .setMessageType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE)
                //设置客户端类型
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本
                .setClientVersion(0)
                //设置时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeReadmark_VALUE)
                //设置发送方 此处应为自己
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                //设置接收方 此处应为自己
                .setTo(CurrentPreference.getInstance().getPreferenceUserId())
                //设置Message
                .setMessage(xmppMessage.toByteString())
                .build();
        return protoMessage;

    }


    //获取要发送的已读消息对象
    public static ProtoMessageOuterClass.ProtoMessage getBeenNewReadStateMessage(String state,JSONArray messageiD, String target, XMPPJID self) {
//        Logger.i("发送已读状态:" + jsonArray.toString());

        //获取当前时间
        Date time = Calendar.getInstance().getTime();

        ProtoMessageOuterClass.StringHeader stringHeader1 = ProtoMessageOuterClass.StringHeader.newBuilder()
                //设置消息头部key 更新已读状态头部key为extendInfo类型
                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                //设置相对应value 更新已读状态头部value为消息是谁发来的
                .setValue(target)
                .build();
        ProtoMessageOuterClass.StringHeader stringHeader2 = ProtoMessageOuterClass.StringHeader.newBuilder()
                //设置消息头部 还需要设置read type
                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeReadType)
//                .setKey("read_type")
                //设置value
                .setValue(state)
                .build();
        ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder()
                //设置消息正文
                .setValue(messageiD.toString())
                //设置头部
                .addHeaders(stringHeader1)
                .addHeaders(stringHeader2)
                //编译
                .build();

        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置主体
                .setBody(messageBody)
                //设置消息id
                .setMessageId(UUID.randomUUID().toString())
                //设置消息类型
                .setMessageType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE)
                //设置客户端类型
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本
                .setClientVersion(0)
                //设置时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeReadmark_VALUE)
                //设置发送方 此处应为自己
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                //设置接收方 此处应为自己
                .setTo(target)
                //设置Message
                .setMessage(xmppMessage.toByteString())
                .build();
        return protoMessage;

    }

    //根据IMMessage类型转换成ProtoMessage 机器人消息
    public static ProtoMessageOuterClass.ProtoMessage getTransMessage(IMMessage imMessage) {
        ProtoMessageOuterClass.MessageBody.Builder builder = ProtoMessageOuterClass.MessageBody.newBuilder();
        ProtoMessageOuterClass.StringHeader extendInfo;
        if (!TextUtils.isEmpty(imMessage.getExt())) {//添加extendinfo
            extendInfo = ProtoMessageOuterClass.StringHeader.newBuilder()
                    //设置消息头部key 更新已读状态头部key为extendInfo类型
                    .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)
//                .setKey("extendInfo")
                    //设置相对应value 更新已读状态头部value为消息是谁发来的
                    .setValue(imMessage.getExt())
                    .build();
            builder.addHeaders(extendInfo);
        }
        builder.setValue(imMessage.getBody());
        ProtoMessageOuterClass.MessageBody messageBody = builder.build();
        //获取当前时间
        Date time = Calendar.getInstance().getTime();
        //获得随机messageId
        String id = imMessage.getId();
        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
                //设置消息类型!
                .setMessageType(imMessage.getMsgType())
                //设置客户端类型 androidsdk 这个是1
                .setClientType(ProtoMessageOuterClass.ClientType.ClientTypeAndroid_VALUE)
                //设置客户端版本? 暂时先写个0
                .setClientVersion(0)
                //设置消息发送时间
                .setReceivedTime(time.getTime()- CurrentPreference.getInstance().getServerTimeDiff())
                //设置随机Id
                .setMessageId(id)
                //设置消息体
                .setBody(messageBody)
                //编译
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置fromId
                .setFrom(imMessage.getFromID())
                //设置toId
                .setTo(imMessage.getToID())
                //这个数据意图不明显,接收到的消息都有
                .setOptions(0)
                //设置消息类型
                .setSignalType(141)
                //设置Message
                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
                //编译对象
                .build();
        return protoMessage;
    }



    /**
     * 心跳消息
     *
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage getHeartBeatMessage() {

        ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder()
                //设置消息正文
                .setValue(" ")
                //编译
                .build();

        ProtoMessageOuterClass.IQMessage xmppMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                //设置主体
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyPing)
                .setMessageId(UUID.randomUUID().toString())
                .setBody(messageBody)//设置客户端类型
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置Message
                .setMessage(xmppMessage.toByteString())
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                .build();
        return protoMessage;

    }

    /**
     * 好友验证消息
     *
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage getVerifyFriendModeMessage(String target) {
        ProtoMessageOuterClass.IQMessage protoIQMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyGetVerifyFriendOpt)
                .setMessageId(UUID.randomUUID().toString())
                .setValue(target)
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置
                .setTo(target)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(protoIQMessage.toByteArray()))
                //编译对象
                .build();

        return protoMessage;

    }

    public static ProtoMessageOuterClass.ProtoMessage getVerifyFriendMessage(String target,String from) {
        ProtoMessageOuterClass.MessageBody body = ProtoMessageOuterClass.MessageBody.newBuilder()
                .setValue(QtalkEvent.Verify_Friend)
                .build();

        ProtoMessageOuterClass.PresenceMessage protoPresenceMessage = ProtoMessageOuterClass.PresenceMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.PresenceKeyType.PresenceKeyVerifyFriend)
                .setMessageId(UUID.randomUUID().toString())
                .setBody(body)
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置
                .setTo(target)
                .setFrom(from)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypePresence_VALUE)
                //设置Message
                .setMessage(protoPresenceMessage.toByteString())
                //编译对象
                .build();

        return protoMessage;

    }




//    public static ProtoMessageOuterClass.ProtoMessage getSpecialNoticeMessage(String target,String from,String key,String value){
//
//
//
//        ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder()
//                //设置头部
////                .addHeaders(type)
//                .setValue(key+value)
//                //编译
//                .build();
//        ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.newBuilder()
//                //设置主体
//                .setBody(messageBody)
//                .setCategoryType(ProtoMessageOuterClass.CategoryType.CategorySpecialNotice_VALUE)
//                //设置消息id
//                .setMessageId(UUID.randomUUID().toString())
//                .build();
//
//        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
//                //设置
//                .setTo(target)
//                .setFrom(from)
//                //设置消息类型
//                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypePresence_VALUE)
//                //设置Message
//                .setMessage(presenceMessage.toByteString())
//                //设置Message
//                //编译对象
//                .build();
//
//        return protoMessage;
//    }

    public static ProtoMessageOuterClass.ProtoMessage getVerifyFriendMessage(String target,String from,String answer) {
        ProtoMessageOuterClass.StringHeader header = ProtoMessageOuterClass.StringHeader.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeAnswer)
                .setValue(answer)
                .build();

        ProtoMessageOuterClass.MessageBody body = ProtoMessageOuterClass.MessageBody.newBuilder()
                .setValue(QtalkEvent.Verify_Friend)
                .addHeaders(header)
                .build();

        ProtoMessageOuterClass.IQMessage protoPresenceMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyMucCreate)
                .setMessageId(UUID.randomUUID().toString())
                .setBody(body)
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置
                .setTo(target)
                .setFrom(from)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypePresence_VALUE)
                //设置Message
                .setMessage(protoPresenceMessage.toByteString())
                //编译对象
                .build();

        return protoMessage;

    }


//    public static ProtoMessageOuterClass.ProtoMessage getTextOrEmojiMessage(String fromId, String toId,String str) {
//
//        ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder()
//                //设置消息正文
//                .setValue(str)
//                //编译
//                .build();
//        //获取当前时间
//        Date time = Calendar.getInstance().getTime();
//        //获得随机messageId
//        String id = UUID.randomUUID().toString();
//        ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.newBuilder()
//                //设置消息类型!
//                .setMessageType(MessageType.TEXT_MESSAGE)
//                //设置客户端类型 androidsdk 这个是1
//                .setClientType(1)
//                //设置客户端版本? 暂时先写个0
//                .setClientVersion(0)
//                //设置消息发送时间
//                .setReceivedTime(time.getTime())
//                //设置随机Id
//                .setMessageId(id)
//                //设置消息体
//                .setBody(messageBody)
//                //编译
//                .build();
//
//        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
//                //设置fromId
//                .setFrom(fromId)
//                //设置toId
//                .setTo(toId)
//                //这个数据意图不明显,接收到的消息都有
//                .setOptions(0)
//                //设置消息类型
//                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeChat_VALUE)
//                //设置Message
//                .setMessage(ByteString.copyFrom(xmppMessage.toByteArray()))
//                //编译对象
//                .build();
//
//
//

//        return protoMessage;
//    }

    /**
     * 获取群成员列表
     *
     * @param key 群ID
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage getMembersAfterJoin(String key) {
        ProtoMessageOuterClass.IQMessage protoIQMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyGetMucUser)
                .setMessageId(UUID.randomUUID().toString())
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置toId
                .setTo(key)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(protoIQMessage.toByteArray()))
                //编译对象
                .build();

        return protoMessage;
    }

    /**
     * 创建群
     *
     * @param key
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage createGroup(String key) {
        Logger.i("创建的群的id:" + key);
        ProtoMessageOuterClass.IQMessage protoIQMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyMucCreate)
                .setMessageId(UUID.randomUUID().toString())
                //群id
                .setValue(key)
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置toId
                .setTo(key)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(protoIQMessage.toByteArray()))
                //编译对象
                .build();

        return protoMessage;

    }

    /**
     * 邀请人消息(v2)：
     *
     * @param groupId
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage inviteMessageV2(XMPPJID self, String groupId, List<String> invitedList) {
//   ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder().build();
//        messageBody.toBuilder()
        List<ProtoMessageOuterClass.MessageBody> bodyList = new ArrayList<>();
        for (int i = 0; i < invitedList.size(); i++) {
            ProtoMessageOuterClass.MessageBody mb = ProtoMessageOuterClass.MessageBody.newBuilder()
                    //固定写法 加入群组value为invite
                    .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setKey("jid").setValue(invitedList.get(i)).build())
                    .setValue("invite")
                    .build();
            bodyList.add(mb);
        }

        ProtoMessageOuterClass.IQMessage protoIQMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyMucInviteV2)
                .setMessageId(UUID.randomUUID().toString())
                .build();
        for (int i = 0; i < bodyList.size(); i++) {
            protoIQMessage = protoIQMessage.toBuilder().addBodys(bodyList.get(i)).build();
        }
//        protoIQMessage.getBodysList()

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                //设置toId
                .setTo(groupId)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(protoIQMessage.toByteArray()))
                //编译对象
                .setOptions(1)
                .build();

        return protoMessage;
    }

    /**
     * 入群注册
     *
     * @param key
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage regitstInGroup(String key) {
        ProtoMessageOuterClass.IQMessage protoIQMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setKey("MUC_INVITE_V2")
                .setMessageId(UUID.randomUUID().toString())
                //群id
                .setValue(key)
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置toId
                .setTo(key)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(protoIQMessage.toByteArray()))
                //编译对象
                .build();

        return protoMessage;
    }

    /**
     * 退出群
     *
     * @param key
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage leaveGroup(String key) {
        ProtoMessageOuterClass.IQMessage protoIQMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyDelMucUser)

                .setMessageId(UUID.randomUUID().toString())
                //群id
                .setValue(key)
                .build();
        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置toId
                .setTo(key)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(protoIQMessage.toByteArray()))
                //编译对象
                .build();

        return protoMessage;
    }


    /**
     * 销毁群组
     *
     * @param key
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage destroyGroup(String key) {
        ProtoMessageOuterClass.IQMessage protoIQMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyDestroyMuc)
                .setMessageId(UUID.randomUUID().toString())
                //群id
                .setValue(key)
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置toId
                .setTo(key)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(protoIQMessage.toByteArray()))
                //编译对象
                .build();

        return protoMessage;
    }


    //presence消息

    /**
     * 删除群成员
     *
     * @param
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage delGroupMember(XMPPJID self, String groupId, String name, String jid) {
////        ProtoMessageOuterClass.StringHeader header = ProtoMessageOuterClass.StringHeader.newBuilder()
////                .setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeAffiliation)
////                .build();
//        List<ProtoMessageOuterClass.MessageBody> bodyList = new ArrayList<>();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
        ProtoMessageOuterClass.MessageBody mb = ProtoMessageOuterClass.MessageBody.newBuilder()
                //固定写法 加入群组value为invite
                .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setKey("read_jid").setValue(jid).build())
                .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeNick).setValue(name).build())
                .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeRole).setValue("none"))
                .setValue("item")
                .build();


        ProtoMessageOuterClass.IQMessage iqMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyCancelMember)
                .setMessageId(UUID.randomUUID().toString())
                .setBody(mb)
                .build();
//        for (int i = 0; i < bodyList.size(); i++) {
//            iqMessage = iqMessage.toBuilder().addBodys(bodyList.get(i)).build();
//        }


        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                //设置toId
                .setTo(groupId)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(iqMessage.toByteArray()))
                //编译对象
                .setOptions(1)
                .build();
        return protoMessage;
    }

    public static ProtoMessageOuterClass.ProtoMessage setGroupAdmin(String groupId,String xmppid,String nickName,boolean isAdmin){
        ProtoMessageOuterClass.MessageBody mb = ProtoMessageOuterClass.MessageBody.newBuilder()
                //固定写法 加入群组value为invite
                .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setKey("read_jid").setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeRealJid).setValue(xmppid).build())
                .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setKey("nick").setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeNick).setValue(nickName).build())
                .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setKey("affiliation").setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeAffiliation).setValue(isAdmin ? "admin" : "member"))
                .setValue("item")
                .build();

        ProtoMessageOuterClass.IQMessage iqMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(isAdmin ? ProtoMessageOuterClass.IQMessageKeyType.IQKeySetAdmin : ProtoMessageOuterClass.IQMessageKeyType.IQKeySetMember)
                .setMessageId(UUID.randomUUID().toString())
                .setBody(mb)
                .build();


        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                .setTo(groupId)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(iqMessage.toByteArray()))
                .build();
        return protoMessage;
    }

    public static ProtoMessageOuterClass.ProtoMessage deleteFriend(XMPPJID self,String jid,String domain){
        ProtoMessageOuterClass.MessageBody mb = ProtoMessageOuterClass.MessageBody.newBuilder()
                //固定写法 加入群组value为invite
                .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeJid).setValue(jid).build())
                .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeDomain).setValue(domain).build())
                .addHeaders(ProtoMessageOuterClass.StringHeader.newBuilder().setKey("mode").setDefinedKey(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeMode).setValue("2").build())
                .setValue("delete_friend")
                .build();


        ProtoMessageOuterClass.IQMessage iqMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyDelUserFriend)
                .setMessageId(UUID.randomUUID().toString())
                .setBody(mb)
                .build();


        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(iqMessage.toByteArray()))
                .build();
        return protoMessage;
    }

    /**
     * 获取好友列表
     *
     * @param key
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage getFriends(String key) {
        ProtoMessageOuterClass.IQMessage protoIQMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyGetUserFriend)
                .setMessageId(UUID.randomUUID().toString())
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置
                .setFrom(key)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(protoIQMessage.toByteArray()))
                //编译对象
                .build();

        return protoMessage;
    }

    /**
     * 获取用户注册的群列表
     *
     * @param fullname
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage getUserMucs(String fullname) {
        ProtoMessageOuterClass.IQMessage protoIQMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyGetUserMucs)
                .setMessageId(UUID.randomUUID().toString())
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置
                .setTo(fullname)
                .setFrom(fullname)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE)
                //设置Message
                .setMessage(ByteString.copyFrom(protoIQMessage.toByteArray()))
                //编译对象
                .build();

        return protoMessage;
    }

    /**
     * 设置用户状态
     * Key: "status", Value:"user_update_status", Headers:["show":"away", "priority":"5"]
     * @param state
     * @return
     */
    public static ProtoMessageOuterClass.ProtoMessage setUserState(String state) {
        ProtoMessageOuterClass.StringHeader stringHeader1 = ProtoMessageOuterClass.StringHeader.newBuilder()
                .setKey("show")
                .setValue(state)
                .build();
        ProtoMessageOuterClass.StringHeader stringHeader2 = ProtoMessageOuterClass.StringHeader.newBuilder()
                .setKey("priority")
                .setValue("5")
                .build();

        ProtoMessageOuterClass.MessageBody messageBody = ProtoMessageOuterClass.MessageBody.newBuilder()
                .setValue("user_update_status")
                //设置头部
                .addHeaders(stringHeader1)
                .addHeaders(stringHeader2)
                //编译
                .build();

        ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.newBuilder()
                //设置主体
                .setBody(messageBody)
                //设置消息id
                .setMessageId(UUID.randomUUID().toString())
                .setKey("status")
                .setValue("user_update_status")
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypePresence_VALUE)
                //设置发送方 此处应为自己
                .setFrom(CurrentPreference.getInstance().getPreferenceUserId())
                //设置接收方 此处应为自己
                .setTo(CurrentPreference.getInstance().getPreferenceUserId())
                //设置Message
                .setMessage(presenceMessage.toByteString())
                .build();
        return protoMessage;
    }

    public static ProtoMessageOuterClass.ProtoMessage conversationSynchronizationMessage(String json, String from, String target) {
        ProtoMessageOuterClass.MessageBody body = ProtoMessageOuterClass.MessageBody.newBuilder()
                .setValue(json)
                .build();

        ProtoMessageOuterClass.PresenceMessage protoPresenceMessage = ProtoMessageOuterClass.PresenceMessage.newBuilder()
                .setDefinedKey(ProtoMessageOuterClass.PresenceKeyType.PresenceKeyNotify)
                .setMessageId(UUID.randomUUID().toString())
                .setCategoryType(ProtoMessageOuterClass.CategoryType.CategorySessionList_VALUE)
                .setBody(body)
                .build();

        ProtoMessageOuterClass.ProtoMessage protoMessage = ProtoMessageOuterClass.ProtoMessage.newBuilder()
                //设置
                .setTo(target)
                .setFrom(from)
                //设置消息类型
                .setSignalType(ProtoMessageOuterClass.SignalType.SignalTypePresence_VALUE)
                //设置Message
                .setMessage(protoPresenceMessage.toByteString())
                //编译对象
                .build();

        return protoMessage;

    }

}
