package com.qunar.im.base.module;

public class WorkWorldSingleResponse {
    private boolean ret;
    private int errcode;
    private String errmsg;
    private WorkWorldItem data;

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

    public WorkWorldItem getData() {
        return data;
    }

    public void setData(WorkWorldItem data) {
        this.data = data;
    }
}
