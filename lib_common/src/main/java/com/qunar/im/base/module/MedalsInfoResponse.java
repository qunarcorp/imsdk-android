package com.qunar.im.base.module;

import java.util.List;

public class MedalsInfoResponse {

    private boolean ret;
    private int errcode;
    private String errmsg;
    private List<MedalsInfo> data;
    public void setRet(boolean ret) {
        this.ret = ret;
    }
    public boolean getRet() {
        return ret;
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

    public void setData(List<MedalsInfo> data) {
        this.data = data;
    }
    public List<MedalsInfo> getData() {
        return data;
    }

}
