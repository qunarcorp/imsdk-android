package com.qunar.im.base.module;

import java.util.List;

public class WorkWorldDeleteResponse {
    /**
     * Copyright 2019 bejson.com
     */


    /**
     * Auto-generated: 2019-01-09 11:16:54
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */


    private Data data;
    private int errcode;
    private String errmsg;
    private boolean ret;

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public boolean getRet() {
        return ret;
    }


    public class Data {

        private int isDelete;
        private int commentStatus;
        private List<WorkWorldOutCommentBean> attachCommentList;

        public List<WorkWorldOutCommentBean> getAttachCommentList() {
            return attachCommentList;
        }

        public void setAttachCommentList(List<WorkWorldOutCommentBean> attachCommentList) {
            this.attachCommentList = attachCommentList;
        }

        public int getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(int isDelete) {
            this.isDelete = isDelete;
        }

        public int getCommentStatus() {
            return commentStatus;
        }

        public void setCommentStatus(int commentStatus) {
            this.commentStatus = commentStatus;
        }

                private int id;

        private CommentDeleteInfo deleteCommentData;
//        private CommentDeleteInfo childComment;

        private int postCommentNum;
        private int postLikeNum;

        public CommentDeleteInfo getDeleteCommentData() {
            return deleteCommentData;
        }

        public void setDeleteCommentData(CommentDeleteInfo deleteCommentData) {
            this.deleteCommentData = deleteCommentData;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        public int getPostCommentNum() {
            return postCommentNum;
        }

        public void setPostCommentNum(int postCommentNum) {
            this.postCommentNum = postCommentNum;
        }

        public int getPostLikeNum() {
            return postLikeNum;
        }

        public void setPostLikeNum(int postLikeNum) {
            this.postLikeNum = postLikeNum;
        }

//        public String getCommentUUID() {
//            return commentUUID;
//        }
//
//        public void setCommentUUID(String commentUUID) {
//            this.commentUUID = commentUUID;
//        }
//
//        public void setIsDelete(int isDelete) {
//            this.isDelete = isDelete;
//        }
//
//        public int getIsDelete() {
//            return isDelete;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public int getId() {
//            return id;
//        }


    }


    public class CommentDeleteInfo{
        private String commentUUID;
        private int deleteType;
        private String postUUID;
        private  String superParentCommentUUID;
        private int superParentStatus;
        private int isDelete;
//        private int commentStatus;


        public int getDeleteType() {
            return deleteType;
        }

        public void setDeleteType(int deleteType) {
            this.deleteType = deleteType;
        }

        public String getPostUUID() {
            return postUUID;
        }

        public void setPostUUID(String postUUID) {
            this.postUUID = postUUID;
        }

        public String getSuperParentCommentUUID() {
            return superParentCommentUUID;
        }

        public void setSuperParentCommentUUID(String superParentCommentUUID) {
            this.superParentCommentUUID = superParentCommentUUID;
        }

        public int getSuperParentStatus() {
            return superParentStatus;
        }

        public void setSuperParentStatus(int superParentStatus) {
            this.superParentStatus = superParentStatus;
        }

        public String getCommentUUID() {
            return commentUUID;
        }

        public void setCommentUUID(String commentUUID) {
            this.commentUUID = commentUUID;
        }

        public int getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(int isDelete) {
            this.isDelete = isDelete;
        }

//        public int getCommentStatus() {
//            return commentStatus;
//        }
//
//        public void setCommentStatus(int commentStatus) {
//            this.commentStatus = commentStatus;
//        }
    }
}
