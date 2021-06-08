package com.qunar.im.base.jsonbean;

/**
 * Created by hubin on 2018/3/1.
 */

public class NoticeRequestBean {


    /**
     * ret : true
     * data : {"url":"www.baidu.com","desc":"这一段是跳转","type":"newChat","from":"shop323","to":"hubin.hu","realFrom":"wz.wang","realTo":"hubin.hu","isCouslt":true}
     */

    private boolean ret;
    private DataBean data;

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * url : www.baidu.com
         * desc : 这一段是跳转
         * type : newChat
         * from : shop323
         * to : hubin.hu
         * realFrom : wz.wang
         * realTo : hubin.hu
         * isCouslt : true
         */

        private String url;
        private String desc;
        private String type;
        private String from;
        private String to;
        private String realFrom;
        private String realTo;
        private boolean isCouslt;
        private String couslt;

        public String getCouslt() {
            return couslt;
        }

        public void setCouslt(String couslt) {
            this.couslt = couslt;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getRealFrom() {
            return realFrom;
        }

        public void setRealFrom(String realFrom) {
            this.realFrom = realFrom;
        }

        public String getRealTo() {
            return realTo;
        }

        public void setRealTo(String realTo) {
            this.realTo = realTo;
        }

        public boolean isIsCouslt() {
            return isCouslt;
        }

        public void setIsCouslt(boolean isCouslt) {
            this.isCouslt = isCouslt;
        }
    }
}
