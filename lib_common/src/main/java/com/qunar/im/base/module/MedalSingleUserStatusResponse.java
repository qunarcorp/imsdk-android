package com.qunar.im.base.module;

public class MedalSingleUserStatusResponse {

    /**
     * ret : true
     * errcode : 0
     * errmsg :
     * data : {"userId":"hubin.hu","medalId":42,"medalStatus":1,"mappingVersion":189,"host":"ejabhost1","createTime":1568104278640,"updateTime":1570783560095}
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
         * userId : hubin.hu
         * medalId : 42
         * medalStatus : 1
         * mappingVersion : 189
         * host : ejabhost1
         * createTime : 1568104278640
         * updateTime : 1570783560095
         */

        private String userId;
        private int medalId;
        private int medalStatus;
        private int mappingVersion;
        private String host;
        private long createTime;
        private long updateTime;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getMedalId() {
            return medalId;
        }

        public void setMedalId(int medalId) {
            this.medalId = medalId;
        }

        public int getMedalStatus() {
            return medalStatus;
        }

        public void setMedalStatus(int medalStatus) {
            this.medalStatus = medalStatus;
        }

        public int getMappingVersion() {
            return mappingVersion;
        }

        public void setMappingVersion(int mappingVersion) {
            this.mappingVersion = mappingVersion;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
    }
}
