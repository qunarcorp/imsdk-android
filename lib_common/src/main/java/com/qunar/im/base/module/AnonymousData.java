package com.qunar.im.base.module;

import java.io.Serializable;

public class AnonymousData implements Serializable {


    private boolean ret;
    private int errcode;
    private String errmsg;
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable {

        private int id;
        private String anonymous;
        private String anonymousPhoto;
        private int replaceable;//0不可更换 1 可更换

        public int getReplaceable() {
            return replaceable;
        }

        public void setReplaceable(int replaceable) {
            this.replaceable = replaceable;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setAnonymous(String anonymous) {
            this.anonymous = anonymous;
        }
        public String getAnonymous() {
            return anonymous;
        }

        public void setAnonymousPhoto(String anonymousPhoto) {
            this.anonymousPhoto = anonymousPhoto;
        }
        public String getAnonymousPhoto() {
            return anonymousPhoto;
        }

    }


}
