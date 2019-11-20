package com.qunar.im.base.module;

import android.text.TextUtils;

public class  WorkWorldNoticeItem implements MultiItemEntity{
    private String eventType="";
    private String userFrom="";
    private String userFromHost="";
    private String userTo="";
    private String userToHost="";
    private String fromIsAnonymous="";
    private String fromAnonymousName="";
    private String fromAnonymousPhoto="";
    private String toIsAnonymous="";
    private String toAnonymousName="";
    private String toAnonymousPhoto="";
    private String content="";
    private String postUUID="";
    private String uuid="";
    private String createTime="";
    private String readState="";

    private String owner="";
    private String anyonousName="";
    private String anyonousPhoto="";
    private String ownerHost="";
    private String isAnyonous="";


    private String fromHost="";
    private String fromUser="";
    private String toHost="";
    private String toUser="";


    private String commentUUID;



    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAnyonousName() {
        return anyonousName;
    }

    public void setAnyonousName(String anyonousName) {
        this.anyonousName = anyonousName;
    }

    public String getAnyonousPhoto() {
        return anyonousPhoto;
    }

    public void setAnyonousPhoto(String anyonousPhoto) {
        this.anyonousPhoto = anyonousPhoto;
    }

    public String getOwnerHost() {
        return ownerHost;
    }

    public void setOwnerHost(String ownerHost) {
        this.ownerHost = ownerHost;
    }

    public String getIsAnyonous() {
        return isAnyonous;
    }

    public void setIsAnyonous(String isAnyonous) {
        this.isAnyonous = isAnyonous;
    }

    public String  getReadState() {
        return readState;
    }

    public void setReadState(String readState) {
        this.readState = readState;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUserFrom() {
        if(TextUtils.isEmpty(userFrom)){
            return fromUser;
        }else {
            return userFrom;
        }
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserFromHost() {
        if(TextUtils.isEmpty(userFromHost)){
            return fromHost;
        }else {
            return userFromHost;
        }
    }

    public void setUserFromHost(String userFromHost) {
        this.userFromHost = userFromHost;
    }

    public String getUserTo() {
        if(TextUtils.isEmpty(userTo)){
            return toUser;
        }else {
            return userTo;
        }
    }

    public void setUserTo(String userTo) {

        this.userTo = userTo;
    }

    public String getUserToHost() {
        if(TextUtils.isEmpty(userToHost)){
            return toHost;
        }else {
            return userToHost;
        }
    }

    public void setUserToHost(String userToHost) {
        this.userToHost = userToHost;
    }

    public String getFromIsAnonymous() {
        return fromIsAnonymous;
    }

    public void setFromIsAnonymous(String fromIsAnyonous) {
        this.fromIsAnonymous = fromIsAnyonous;
    }

    public String getFromAnonymousName() {
        return fromAnonymousName;
    }

    public void setFromAnonymousName(String fromAnyonousName) {
        this.fromAnonymousName = fromAnyonousName;
    }

    public String getFromAnonymousPhoto() {
        return fromAnonymousPhoto;
    }

    public void setFromAnonymousPhoto(String fromAnyonousPhoto) {
        this.fromAnonymousPhoto = fromAnyonousPhoto;
    }

    public String getToIsAnonymous() {
        return toIsAnonymous;
    }

    public void setToIsAnonymous(String toIsAnyonous) {
        this.toIsAnonymous = toIsAnyonous;
    }

    public String getToAnonymousName() {
        return toAnonymousName;
    }

    public void setToAnonymousName(String toAnyonousName) {
        this.toAnonymousName = toAnyonousName;
    }

    public String getToAnonymousPhoto() {
        return toAnonymousPhoto;
    }

    public void setToAnonymousPhoto(String toAnyonousPhoto) {
        this.toAnonymousPhoto = toAnyonousPhoto;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostUUID() {
        return postUUID;
    }

    public void setPostUUID(String postUUID) {
        this.postUUID = postUUID;
    }

    @Override
    public int getItemType() {
        return 2;
    }
}
