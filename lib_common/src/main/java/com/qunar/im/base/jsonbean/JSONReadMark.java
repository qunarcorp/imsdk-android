package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by hubin on 2017/12/6.
 */

public class JSONReadMark {


    private boolean ret;
    private int errcode;
    private Object errmsg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private String muc_name;
        private String domain;
        private String date;

        public String getMuc_name() {
            return muc_name;
        }

        public void setMuc_name(String muc_name) {
            this.muc_name = muc_name;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
