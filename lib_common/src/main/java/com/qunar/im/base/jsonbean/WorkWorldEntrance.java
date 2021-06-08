package com.qunar.im.base.jsonbean;

public class WorkWorldEntrance {
    /**
     * data : {"authSign":false}
     * errcode : 6.75510084911507E7
     * errmsg : magna veniam culpa nisi Duis
     * ret : true
     */

    private DataBean data;
    private double errcode;
    private String errmsg;
    private boolean ret;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
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

    public static class DataBean {
        /**
         * authSign : false
         */

        private boolean authSign;

        public boolean isAuthSign() {
            return authSign;
        }

        public void setAuthSign(boolean authSign) {
            this.authSign = authSign;
        }
    }
//    public Object errcode;
//    public String errmsg;
//    public boolean ret;
//    public int ver;


}
