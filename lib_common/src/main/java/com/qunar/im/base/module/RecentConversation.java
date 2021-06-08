package com.qunar.im.base.module;

import android.text.TextUtils;

import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.Serializable;

/**
 * Created by xinbo.wang on 2015/2/9.
 */
public class RecentConversation  extends BaseModel implements Serializable,Comparable<RecentConversation> {

    private static final long serialVersionUID = 1001L;

    public static final String FRIENDS_REQUEST_ID= "request_friends";
    public static final String ID_DEFAULT_PLATFORM = "platform"; //会话列表中用于存储普通的公众号消息默认Id
    //名片类
    private Nick nick;
    private String id; //会话列表id
    private String fullname;
    private String lastMsg;//最后一条消息文本
    private String lastFrom;//最后一个说话人id
    private String lastState;//最后一条消息状态
    private long lastMsgTime;//会话列表最后一条消息时间
    //热线的真实用户
    private String realUser;//真实id
    //判断为接受或者发送方
    private String isChan = "send";
    private int unread_msg_cont; //当前会话未读消息
    private int conversationType; //会话列表类型 0是单人,1是多人
    private String hasAtMsg;
    private int atMsgIndex;
    private String msgType;
    private int top; //置顶会话
    private int remind;//提醒会话

    private boolean isOnlyText= false;

    private boolean toDB;
    private boolean toNetWork;

    private String nickName;
    private String headerSrc;


    public boolean isOnlyText() {
        return isOnlyText;
    }

    public void setOnlyText(boolean onlyText) {
        isOnlyText = onlyText;
    }

    public boolean isToDB() {
        return toDB;
    }

    public void setToDB(boolean toDB) {
        this.toDB = toDB;
    }

    public boolean isToNetWork() {
        return toNetWork;
    }

    public void setToNetWork(boolean toNetWork) {
        this.toNetWork = toNetWork;
    }

    public String getLastState() {
        return lastState;
    }

    public void setLastState(String lastState) {
        this.lastState = lastState;
    }

    public String getLastFrom() {
        return lastFrom;
    }

    public void setLastFrom(String lastFrom) {
        this.lastFrom = lastFrom;
    }

    public int getRemind() {
        return remind;
    }

    public void setRemind(int remind) {
        this.remind = remind;
    }

    public Nick getNick() {
        return nick;
    }

    public void setNick(Nick nick) {
        this.nick = nick;
    }

    public String isChan() {
        return isChan;
    }

    public void setIsChan(String isChan) {
        this.isChan = isChan;
    }

    public String getRealUser() {
        return realUser;
    }

    public void setRealUser(String realUser) {
        this.realUser = realUser;
    }

    public int getMsgType() {
        if(TextUtils.isEmpty(msgType)) return ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE;
        return Integer.parseInt(msgType);
    }

    public void setMsgType(int msgType) {
        this.msgType = String.valueOf(msgType);
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    /**
     * 置顶消息
     * @return
     */
    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    /**
     *  Id equivalent JID
     * @return
     */
    public String getId() {
        return QtalkStringUtils.parseBareJid(id);
    }

    public void setId(String id) {
        this.id = QtalkStringUtils.parseBareJid(id);
    }

    public String getFullname() {
            return fullname;
    }


    public String getLastMsg() {
        return lastMsg;
    }

    public int getUnread_msg_cont() {
        return unread_msg_cont;
    }

    public void setUnread_msg_cont(int unread_msg_cont) {
        if(unread_msg_cont<0) unread_msg_cont = 0;
        this.unread_msg_cont = unread_msg_cont;
    }

    public String getHasAtMsg() {
        return hasAtMsg;
    }

    public void setHasAtMsg(String hasAtMsg) {
        this.hasAtMsg = hasAtMsg;
    }

    public int getAtMsgIndex() {
        return atMsgIndex;
    }

    public void setAtMsgIndex(int atMsgIndex) {
        this.atMsgIndex = atMsgIndex;
    }

    public int getConversationType() {
        return conversationType;
    }

    public void setConversationType(int conversationType) {
        this.conversationType = conversationType;
    }

    @Override
    public int compareTo(RecentConversation another) {
        if(this.id.equals(another.getId())){
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object rc)
    {
        return this.id.equals(((RecentConversation)rc).getId());
    }
}
