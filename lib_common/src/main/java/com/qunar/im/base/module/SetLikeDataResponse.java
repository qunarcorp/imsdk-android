package com.qunar.im.base.module;

public class SetLikeDataResponse {

    private boolean ret;
    private String errmsg;
    private int errcode;
    private SetLikeData data;

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public SetLikeData getData() {
        return data;
    }

    public void setData(SetLikeData data) {
        this.data = data;
    }
}
