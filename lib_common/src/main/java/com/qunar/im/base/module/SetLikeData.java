package com.qunar.im.base.module;

import java.util.List;

public class SetLikeData {
    private String commentId;
    private int likeType;
    private int opType;
    private int isLike;
    private String postId;
    private String userId;
    private String userHost;
    private String likeId;
    private int likeNum;
    private String postOwnerHost;
    private String postOwner;
    private String superParentUUID;
    private List<WorkWorldOutCommentBean> attachCommentList;


    public List<WorkWorldOutCommentBean> getAttachCommentList() {
        return attachCommentList;
    }

    public void setAttachCommentList(List<WorkWorldOutCommentBean> attachCommentList) {
        this.attachCommentList = attachCommentList;
    }

    public String getSuperParentUUID() {
        return superParentUUID;
    }

    public void setSuperParentUUID(String superParentUUID) {
        this.superParentUUID = superParentUUID;
    }

    public String getPostOwnerHost() {
        return postOwnerHost;
    }

    public void setPostOwnerHost(String postOwnerHost) {
        this.postOwnerHost = postOwnerHost;
    }

    public String getPostOwner() {
        return postOwner;
    }

    public void setPostOwner(String postOwner) {
        this.postOwner = postOwner;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getUserHost() {
        return userHost;
    }

    public void setUserHost(String userHost) {
        this.userHost = userHost;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public int getLikeType() {
        return likeType;
    }

    public void setLikeType(int likeType) {
        this.likeType = likeType;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }
}
