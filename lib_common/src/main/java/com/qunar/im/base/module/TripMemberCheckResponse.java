package com.qunar.im.base.module;

public class TripMemberCheckResponse {


    /**
     * ret : true
     * errcode : 0
     * errmsg :
     * data : {"isConform":true}
     */

    private boolean ret;
    private int errcode;
    private String errmsg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * isConform : true
         */

        private boolean isConform;

        public boolean isIsConform() {
            return isConform;
        }

        public void setIsConform(boolean isConform) {
            this.isConform = isConform;
        }
    }
}
