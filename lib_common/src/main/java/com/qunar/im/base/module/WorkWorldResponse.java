package com.qunar.im.base.module;

import java.util.ArrayList;

public class WorkWorldResponse {
    private boolean ret;
    private int errcode;
    private String errmsg;
    private Data data;

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private ArrayList<WorkWorldDeleteResponse.Data> deletePost = new ArrayList<>();
        private ArrayList<WorkWorldItem> newPost = new ArrayList<>();

        public ArrayList<WorkWorldDeleteResponse.Data> getDeletePost() {
            return deletePost;
        }

        public void setDeletePost(ArrayList<WorkWorldDeleteResponse.Data> deletePost) {
            this.deletePost = deletePost;
        }

        public ArrayList<WorkWorldItem> getNewPost() {
            return newPost;
        }

        public void setNewPost(ArrayList<WorkWorldItem> newPost) {
            this.newPost = newPost;
        }
    }
}
