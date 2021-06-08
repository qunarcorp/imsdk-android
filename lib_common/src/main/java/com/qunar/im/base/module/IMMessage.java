package com.qunar.im.base.module;

import android.text.TextUtils;

import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.utils.QtalkStringUtils;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Date;

public class IMMessage extends BaseIMMessage implements Serializable,Comparable<IMMessage> {
    public final static int DIRECTION_RECV = 0;
    public final static int DIRECTION_SEND = 1;
    public final static int DIRECTION_MIDDLE = 2; //可点右上角信息.....的说明类型消息

    public final static int MSG_UNREAD = 0;
    public final static int MSG_READ = 1;

    //名片
    private Nick nick;
    private boolean carbon;//是否抄送
    private boolean auto_reply;//是否自动回复消息
    private String id;//消息id
    private int type;//代表群或者单聊或者cus消息或者代收消息
    private int collectionType; //代表代收消息时,消息所属类型 单聊, 群, 或者cus等
    private String fromID;//来自于谁
    private String toID;//发送给谁
    private String oFromId;//代收发送人
    private String oToId;//代收接收人
    private String conversationID;//列表id
    private String body;//消息正文
    private long time;//时间
    private int isRead;//是否已读
    private int readState;//发送状态 已读 未读
    private int messageState;//消息状态 发送成功 发送失败 发送中
    private int direction;//方向
    private String messageId;//消息Id
    private String msgType;//消息类型
    private String maType;//客户端类型
    private String ext;//强化消息字段
    private String backUp;//目前只有@消息用
    private String messageRaw;//消息本体
    private String userId;//基本不会用到的字段,主要用于插入sessionList使用
    private int signalType; //pb消息外层判断字段
    private boolean isCollection; //是否是代收类型消息
    private JSONArray newReadList; //新版阅读指针数据


    //上传图片或视频是否 完成
//    private boolean isDone = true;
    //上传进度
    private int progress;
    //用于发送consult消息时记录一些信息
    private ConsultInfo consultInfo;



    public JSONArray getNewReadList() {
        return newReadList;
    }

    public void setNewReadList(JSONArray newReadList) {
        this.newReadList = newReadList;
    }

    public String getBackUp() {
        return backUp;
    }

    public void setBackUp(String backUp) {
        this.backUp = backUp;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public int getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(int collectionType) {
        this.collectionType = collectionType;
    }

    public String getoFromId() {
        return oFromId;
    }

    public void setoFromId(String oFromId) {
        this.oFromId = oFromId;
    }

    public String getoToId() {
        return oToId;
    }

    public void setoToId(String oToId) {
        this.oToId = oToId;
    }

    public ConsultInfo getConsultInfo() {
        return consultInfo;
    }

    public void setConsultInfo(ConsultInfo consultInfo) {
        this.consultInfo = consultInfo;
    }

    public int getSignalType() {
        return signalType;
    }

    public void setSignalType(int signalType) {
        this.signalType = signalType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageRaw() {
        return messageRaw;
    }

    public void setMessageRaw(String messageRaw) {
        this.messageRaw = messageRaw;
    }

    public Nick getNick() {
        return nick;
    }

    public void setNick(Nick nick) {
        this.nick = nick;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    //群聊时,显示名字
    private String nickName;

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public String getQchatid() {
        return qchatid;
    }

    public void setQchatid(String qchatid) {
        this.qchatid = qchatid;
    }

    public String getRealto() {
        return realto;
    }

    public void setRealto(String realto) {
        this.realto = realto;
    }

    public String getRealfrom() {
        return realfrom;
    }

    public void setRealfrom(String realfrom) {
        this.realfrom = realfrom;
    }

    public int getMsgType() {
        if(TextUtils.isEmpty(msgType)) return ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE;
        return Integer.parseInt(msgType);
    }

    public void setMsgType(int msgType) {
        this.msgType = String.valueOf(msgType);
    }

    public String getMaType() {
        return maType;
    }

    public void setMaType(String maType) {
        this.maType = maType;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromID() {
        return this.fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return this.toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getTime() {
        return new Date(time);
    }

    public void setTime(Date time) {
        this.time = time.getTime();
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getReadState() {
        return readState;
    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    public void setReadState(int readState) {
        this.readState = readState;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }


    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = QtalkStringUtils.parseBareJid(conversationID);
    }

    public boolean isCarbon() {
        return carbon;
    }

    public void setCarbon(boolean carbon) {
        this.carbon = carbon;
    }

    public boolean isAuto_reply() {
        return auto_reply;
    }

    public void setAuto_reply(boolean auto_reply) {
        this.auto_reply = auto_reply;
    }
//    public boolean isDone() {
//        return isDone;
//    }
//
//    public void setDone(boolean done) {
//        isDone = done;
//    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    /**
     *  id for messageId
     * @return
     */
    public void setMessageID(String messageID) {
        this.messageId = messageID;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public int compareTo(IMMessage another) {
        return this.getId().compareTo(another.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(this.getId() == null) return false;
        if(this.getMessageId() == null) return false;
        String msgId = ((IMMessage)o).getMessageId();
        if(TextUtils.isEmpty(msgId)) return false;
        if(TextUtils.isEmpty(messageId)) return false;
        //(((IMMessage)o).getSignalType() == ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE || ((IMMessage)o).getSignalType() == ProtoMessageOuterClass.SignalType.SignalTypeRevoke_VALUE) &&
        if(msgId.contains("consult-")) {
            msgId = msgId.replace("consult-", "");
        }
        //(signalType == ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE || signalType == ProtoMessageOuterClass.SignalType.SignalTypeRevoke_VALUE) &&
        if(messageId.contains("consult-")) {
            messageId = messageId.replace("consult-", "");
        }
        return this.getMessageId().equals(msgId);
    }



    public static class ConsultInfo implements Serializable {

        /**
         * cn : consult
         * d : send
         * userType : usr
         */

        private String cn;
        private String d;
        private String userType;

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }
    }
}