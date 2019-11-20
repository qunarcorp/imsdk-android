package com.qunar.im.base.module;

import java.util.List;

public class WorkWorldNoticeHistoryResponse {


    /**
     * data : {"msgList":["sit laborum Ut adipisicing"],"total":-2.4242787958718285E7}
     * errcode : -81650.7542372495
     * errmsg : vel
     * ret : false
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
         * msgList : ["sit laborum Ut adipisicing"]
         * total : -2.4242787958718285E7
         */

        private double total;
        private List<WorkWorldNoticeItem> msgList;

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public List<WorkWorldNoticeItem> getMsgList() {
            return msgList;
        }

        public void setMsgList(List<WorkWorldNoticeItem> msgList) {
            this.msgList = msgList;
        }
    }
}
