package com.qunar.im.base.module;

public class WorkWorldDetailsCommentHotData implements MultiItemEntity{

    private WorkWorldDetailsCommenData.DataBean data;
    private double errcode;
    private String errmsg;
    private boolean ret;


    public WorkWorldDetailsCommenData.DataBean getData() {
        return data;
    }

    public void setData(WorkWorldDetailsCommenData.DataBean data) {
        this.data = data;
    }

    public double getErrcode() {
        return errcode;
    }

    public void setErrcode(double errcode) {
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

    @Override
    public int getItemType() {
        return 0;
    }
}
