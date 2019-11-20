package com.qunar.im.base.module;

public class ReleaseDataRequest {

    private String uuid ;//帖子id
    private String content;//数据内容
    private int isAnonymous;//是否匿名
    private String AnonymousName;//匿名名称
    private String AnonymousPhoto;//匿名头像
    private String owner;//发布人id
    private String owner_host;//发布人域名
    private String atList;//atlist
    private int postType;

    public String getAtList() {
        return atList;
    }

    public void setAtList(String atList) {
        this.atList = atList;
    }

    public int getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(int isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(int anonymous) {
        isAnonymous = anonymous;
    }

    public String getAnonymousName() {
        return AnonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        AnonymousName = anonymousName;
    }

    public String getAnonymousPhoto() {
        return AnonymousPhoto;
    }

    public void setAnonymousPhoto(String anonymousPhoto) {
        AnonymousPhoto = anonymousPhoto;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner_host() {
        return owner_host;
    }

    public void setOwner_host(String owner_host) {
        this.owner_host = owner_host;
    }
}
