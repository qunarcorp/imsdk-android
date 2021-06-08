package com.qunar.im.base.module;

import java.io.Serializable;
import java.util.List;

public class WorkWorldNewCommentBean implements MultiItemEntity,Serializable {
    /**
     * anonymousName : Excepteur qui deserunt
     * anonymousPhoto : adipisicing
     * commentUUID : do in irure eiusmod
     * content : sed sunt fugiat
     * createTime : 8.269868233724448E7
     * fromHost : laboris amet
     * fromUser : sunt
     * id : 5.5106199377797574E7
     * isAnonymous : -6.002114353700061E7
     * isDelete : 2.0608622232486784E7
     * isLike : 9.480324758136329E7
     * likeNum : -9.569283989306125E7
     * parentCommentUUID : id dolore culpa ea
     * postUUID : do adipisicing labore voluptate
     * reviewStatus : -9.108365232187854E7
     * toHost : nostrud Duis eu consequat elit
     * toUser : ex exercitation
     * updateTime : -3.375192692442859E7
     */

    private int type = 1;//COMMENT

    private String anonymousName;
    private String anonymousPhoto;
    private String commentUUID;
    private String content;
    private String createTime;
    private String fromHost;
    private String fromUser;
    private String id;
    private String isAnonymous;
    private String isDelete;
    private String isLike;
    private String likeNum;
    private String parentCommentUUID;
    private String postUUID;
    private String reviewStatus;
    private String toHost;
    private String toUser;
    private String updateTime;
    private String toisAnonymous;
    private String toAnonymousName;
    private String toAnonymousPhoto;
    private String postOwner;
    private String postOwnerHost;
    private String superParentUUID;
    private String commentStatus;
    private List<String> hotCommentUUID;
    private String atList;

    public String getAtList() {
        return atList;
    }

    public void setAtList(String atList) {
        this.atList = atList;
    }

    public List<String> getHotCommentUUID() {
        return hotCommentUUID;
    }

    public void setHotCommentUUID(List<String> hotCommentUUID) {
        this.hotCommentUUID = hotCommentUUID;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    private List<WorkWorldChildCommentBean> newChild;
    private String newChildString;

    private List<WorkWorldDetailsCommenData.DataBean.DeleteCommentsBean> deleteChild;


    public List<WorkWorldDetailsCommenData.DataBean.DeleteCommentsBean> getDeleteChild() {
        return deleteChild;
    }

    public void setDeleteChild(List<WorkWorldDetailsCommenData.DataBean.DeleteCommentsBean> deleteChild) {
        this.deleteChild = deleteChild;
    }

    public List<WorkWorldChildCommentBean> getNewChild() {
        return newChild;
    }

    public void setNewChild(List<WorkWorldChildCommentBean> newChild) {
        this.newChild = newChild;
    }

    public String getNewChildString() {
        return newChildString;
    }

    public void setNewChildString(String newChildString) {
        this.newChildString = newChildString;
    }

    public String getSuperParentUUID() {
        return superParentUUID;
    }

    public void setSuperParentUUID(String superParentUUID) {
        this.superParentUUID = superParentUUID;
    }

    public String getPostOwner() {
        return postOwner;
    }

    public void setPostOwner(String postOwner) {
        this.postOwner = postOwner;
    }

    public String getPostOwnerHost() {
        return postOwnerHost;
    }

    public void setPostOwnerHost(String postOwnerHost) {
        this.postOwnerHost = postOwnerHost;
    }

    public String getToisAnonymous() {
        return toisAnonymous;
    }

    public void setToisAnonymous(String toisAnonymous) {
        this.toisAnonymous = toisAnonymous;
    }

    public String getToAnonymousName() {
        return toAnonymousName;
    }

    public void setToAnonymousName(String toAnonymousName) {
        this.toAnonymousName = toAnonymousName;
    }

    public String getToAnonymousPhoto() {
        return toAnonymousPhoto;
    }

    public void setToAnonymousPhoto(String toAnonymousPhoto) {
        this.toAnonymousPhoto = toAnonymousPhoto;
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

    public String getCommentUUID() {
        return commentUUID;
    }

    public void setCommentUUID(String commentUUID) {
        this.commentUUID = commentUUID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFromHost() {
        return fromHost;
    }

    public void setFromHost(String fromHost) {
        this.fromHost = fromHost;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(String isAnonymous) {
        this.isAnonymous = isAnonymous;
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

    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    public String getParentCommentUUID() {
        return parentCommentUUID;
    }

    public void setParentCommentUUID(String parentCommentUUID) {
        this.parentCommentUUID = parentCommentUUID;
    }

    public String getPostUUID() {
        return postUUID;
    }

    public void setPostUUID(String postUUID) {
        this.postUUID = postUUID;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getToHost() {
        return toHost;
    }

    public void setToHost(String toHost) {
        this.toHost = toHost;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public int getItemType() {
        return type;
    }



}
