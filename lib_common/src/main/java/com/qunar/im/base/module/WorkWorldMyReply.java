package com.qunar.im.base.module;

import java.util.List;

public class WorkWorldMyReply {


    private DataBean data;
    private int errcode;
    private String errmsg;
    private boolean ret;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public static class DataBean {
        private List<WorkWorldDetailsCommenData.DataBean.DeleteCommentsBean> deleteComments;
        private List<WorkWorldNoticeItem> newComment;

        public List<WorkWorldDetailsCommenData.DataBean.DeleteCommentsBean> getDeleteComments() {
            return deleteComments;
        }

        public void setDeleteComments(List<WorkWorldDetailsCommenData.DataBean.DeleteCommentsBean> deleteComments) {
            this.deleteComments = deleteComments;
        }

        public List<WorkWorldNoticeItem> getNewComment() {
            return newComment;
        }

        public void setNewComment(List<WorkWorldNoticeItem> newComment) {
            this.newComment = newComment;
        }


    }
}
