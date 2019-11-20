package com.qunar.im.base.module;

import java.io.Serializable;
import java.util.List;

public class WorkWorldItem implements Serializable {

    public String id = "";
   public String uuid = "";
   public String owner = "";
   public String ownerHost = "";
   public String isAnonymous = "";
   public String anonymousName = "";
   public String anonymousPhoto = "";
   public String createTime = "";
   public String updateTime = "";
   public String content = "{}";
   public String likeNum = "";
   public String commentsNum = "";
   public String reviewStatus = "";
   public String atList = "";
   public String isDelete ="";
   public String isLike="";
   public String postType="";
   public List< WorkWorldOutCommentBean> attachCommentList =null;
   public String attachCommentListString="";
//   public String readState="";


    public String getAttachCommentListString() {
        return attachCommentListString;
    }

    public void setAttachCommentListString(String attachCommentListString) {
        this.attachCommentListString = attachCommentListString;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public List< WorkWorldOutCommentBean> getAttachCommentList() {
        return attachCommentList;
    }

    public void setAttachCommentList(List< WorkWorldOutCommentBean> attachCommentList) {
        this.attachCommentList = attachCommentList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerHost() {
        return ownerHost;
    }

    public void setOwnerHost(String ownerHost) {
        this.ownerHost = ownerHost;
    }

    public String getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(String isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public String getAnonymousName() {
        return anonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
    }

    public String getAnonymousPhoto() {
        return anonymousPhoto;
    }

    public void setAnonymousPhoto(String anonymousPhoto) {
        this.anonymousPhoto = anonymousPhoto;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    public String getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(String commentsNum) {
        this.commentsNum = commentsNum;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getAtList() {
        return atList;
    }

    public void setAtList(String atList) {
        this.atList = atList;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

//    public String getReadState() {
//        return readState;
//    }
//
//    public void setReadState(String readState) {
//        this.readState = readState;
//    }
}
