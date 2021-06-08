package com.qunar.im.base.jsonbean;

public class PushSettingResponseBean {


    /**
     * ret : true
     * errcode : 0
     * errmsg : null
     * data : {"push_flag":30}
     */

    private boolean ret;
    private int errcode;
    private Object errmsg;
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

    public Object getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(Object errmsg) {
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
         * push_flag : 30
         */

        private int push_flag;

        public int getPush_flag() {
            return push_flag;
        }

        public void setPush_flag(int push_flag) {
            this.push_flag = push_flag;
        }
    }
}
