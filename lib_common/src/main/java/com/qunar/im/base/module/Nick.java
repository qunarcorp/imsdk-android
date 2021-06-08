package com.qunar.im.base.module;

import android.text.TextUtils;

import com.qunar.im.utils.QtalkStringUtils;

import java.io.Serializable;

/**
 * Created by hubin on 2017/8/23.
 */

public class Nick implements Serializable {

    private static final long serialVersionUID = 1002L;
    //名片编号,方便获取不属于数据库数据
    private int id;
    //好友列表展示的时候用了 不知道干啥的
    private boolean root;

    //单人名片
    private String UserId="";
    private String XmppId="";
    private String DescInfo="";
    private String UserInfo="";
    private String SearchIndex="";
    private String IncrementVersion="";
    private String mark="";
    private String mood = "";

    //群名片
    private String GroupId="";
    private String Introduce="";
    private String Topic="";
    private String ExtendedFlag="";
    //共用
    private String Name="";
    private String LastUpdateTime="";
    private String HeaderSrc="";
    //其他字段
    private int collectionBind;//绑定用户是否还在bind中
    private int collectionUnReadCount;//代收绑定用户未读消息数,这里只做存储,方便数据传递,没有其他实质作用

    private boolean isInGroup;//是否在指定的群里


    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getCollectionUnReadCount() {
        return collectionUnReadCount;
    }

    public void setCollectionUnReadCount(int collectionUnReadCount) {
        this.collectionUnReadCount = collectionUnReadCount;
    }

    public int getCollectionBind() {
        return collectionBind;
    }

    public void setCollectionBind(int collectionBind) {
        this.collectionBind = collectionBind;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIncrementVersion() {
        return IncrementVersion;
    }

    public void setIncrementVersion(String incrementVersion) {
        IncrementVersion = incrementVersion;
    }

    public String getSearchIndex() {
        return SearchIndex;
    }

    public void setSearchIndex(String searchIndex) {
        SearchIndex = searchIndex;
    }

    public String getDescInfo() {
        return DescInfo;
    }

    public void setDescInfo(String descInfo) {
        DescInfo = descInfo;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getXmppId() {
        return XmppId;
    }

    public void setXmppId(String xmppId) {
        XmppId = xmppId;
    }

    public String getName() {
        return Name;
    }

    public String getShowName(){
        if(!TextUtils.isEmpty(mark)){
            return mark;
        }
        if(!TextUtils.isEmpty(Name)){
            return Name;
        }

        return QtalkStringUtils.parseBareJid(XmppId);
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserInfo() {
        return UserInfo;
    }

    public void setUserInfo(String userInfo) {
        UserInfo = userInfo;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getIntroduce() {
        return Introduce;
    }

    public void setIntroduce(String introduce) {
        Introduce = introduce;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

    public String getExtendedFlag() {
        return ExtendedFlag;
    }

    public void setExtendedFlag(String extendedFlag) {
        ExtendedFlag = extendedFlag;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    public String getHeaderSrc() {
        return HeaderSrc;
    }

    public void setHeaderSrc(String headerSrc) {
        HeaderSrc = headerSrc;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getMood() {
        return mood;
    }

    public boolean isInGroup() {
        return isInGroup;
    }

    public void setInGroup(boolean inGroup) {
        isInGroup = inGroup;
    }
}
