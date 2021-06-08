package com.qunar.im.base.module;

import java.io.Serializable;

/**
 * Created by zhao.liu on 2014/11/26.
 */

public class ChatRoom extends BaseModel implements Serializable,Comparable<ChatRoom> {
    public final static int JOINED = 1;
    public final static int UNJOINED = 0;

    public final static int YES = 1;
    public final static int NO = 0;

    public ChatRoom(){}
    private String name;
    private String id;
    private String subject;
    private int isJoined = JOINED;
    private String password;
    private String description;
    private String picUrl;

    private int version;
    private int isSubjectModifiable;
    private int isMembersOnly;
    private int isModerated;
    private int isNonanonymous;
    private int isPasswordProtected;
    private int isPersistent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJid() {
        return id;
    }

    public void setJid(String jid) {
        this.id = jid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getIsJoined() {
        return isJoined;
    }

    public void setIsJoined(int isJoined) {
        this.isJoined = isJoined;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsSubjectModifiable() {
        return isSubjectModifiable;
    }

    public void setIsSubjectModifiable(int isSubjectModifiable) {
        this.isSubjectModifiable = isSubjectModifiable;
    }

    public int getIsMembersOnly() {
        return isMembersOnly;
    }

    public void setIsMembersOnly(int isMembersOnly) {
        this.isMembersOnly = isMembersOnly;
    }

    public int getIsModerated() {
        return isModerated;
    }

    public void setIsModerated(int isModerated) {
        this.isModerated = isModerated;
    }

    public int getIsNonanonymous() {
        return isNonanonymous;
    }

    public void setIsNonanonymous(int isNonanonymous) {
        this.isNonanonymous = isNonanonymous;
    }

    public int getIsPasswordProtected() {
        return isPasswordProtected;
    }

    public void setIsPasswordProtected(int isPasswordProtected) {
        this.isPasswordProtected = isPasswordProtected;
    }

    public int getIsPersistent() {
        return isPersistent;
    }

    public void setIsPersistent(int isPersistent) {
        this.isPersistent = isPersistent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int compareTo(ChatRoom another) {
        return this.getJid().compareTo(another.getJid());
    }

    @Override
    public boolean equals(Object object)
    {
        return object!=null&&getJid()!=null&&
                object instanceof ChatRoom&&getJid().equals(((ChatRoom)object).getJid());
    }
}