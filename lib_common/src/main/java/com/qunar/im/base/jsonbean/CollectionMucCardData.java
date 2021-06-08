package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by hubin on 2017/11/28.
 */

public class CollectionMucCardData {



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
        /**
         * muc_name : 1860101e80444e8082022fbc71ff58dd@conference.ejabhost1
         * show_name : 111111111222111
         * muc_desc :
         * muc_title : 222222222222222222222
         * muc_pic : file/v2/download/perm/bc0fca9b398a0e4a1f981a21e7425c7a.png
         * version : 2
         */

        private String muc_name;
        private String show_name;
        private String muc_desc;
        private String muc_title;
        private String muc_pic;
        private int version;

        public String getMuc_name() {
            return muc_name;
        }

        public void setMuc_name(String muc_name) {
            this.muc_name = muc_name;
        }

        public String getShow_name() {
            return show_name;
        }

        public void setShow_name(String show_name) {
            this.show_name = show_name;
        }

        public String getMuc_desc() {
            return muc_desc;
        }

        public void setMuc_desc(String muc_desc) {
            this.muc_desc = muc_desc;
        }

        public String getMuc_title() {
            return muc_title;
        }

        public void setMuc_title(String muc_title) {
            this.muc_title = muc_title;
        }

        public String getMuc_pic() {
            return muc_pic;
        }

        public void setMuc_pic(String muc_pic) {
            this.muc_pic = muc_pic;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }
}
