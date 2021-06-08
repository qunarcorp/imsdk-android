package com.qunar.im.base.jsonbean;

public class SetWorkWorldRemindResponse {


    /**
     * data : {"flag":-2551346.174412787,"host":"incididunt anim qui tempor","id":-2.5693732546345234E7,"notifyKey":"eiusmod","notifyUser":"anim laborum","updateTime":-5.989472605901836E7,"version":8.756102668064383E7}
     * errcode : -1.836574071307005E7
     * errmsg : ut quis in
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
         * flag : -2551346.174412787
         * host : incididunt anim qui tempor
         * id : -2.5693732546345234E7
         * notifyKey : eiusmod
         * notifyUser : anim laborum
         * updateTime : -5.989472605901836E7
         * version : 8.756102668064383E7
         */

        private int flag;
        private String host;
        private double id;
        private String notifyKey;
        private String notifyUser;
        private double updateTime;
        private double version;

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public double getId() {
            return id;
        }

        public void setId(double id) {
            this.id = id;
        }

        public String getNotifyKey() {
            return notifyKey;
        }

        public void setNotifyKey(String notifyKey) {
            this.notifyKey = notifyKey;
        }

        public String getNotifyUser() {
            return notifyUser;
        }

        public void setNotifyUser(String notifyUser) {
            this.notifyUser = notifyUser;
        }

        public double getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(double updateTime) {
            this.updateTime = updateTime;
        }

        public double getVersion() {
            return version;
        }

        public void setVersion(double version) {
            this.version = version;
        }
    }
}
