package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by hubin on 2018/2/8.
 */

public class NoticeBean {


    /**
     * from : shop323
     * to : hubin.hu
     * isCouslt : true
     * realFrom : xxxx
     * realTo : xxxxx
     * couslt : 4
     * noticeStr : [{"str":"你好","type":"link","url":"www.baidu.com","strColor":"#ffffff"},{"str":",本段是文本","type":"text","strColor":"#ffffff"},{"str":"这一段是跳转","type":"newChat","from":"shop323","to":"hubin.hu","realFrom":"wz.wang","realTo":"hubin.hu","couslt":"4","strColor":"#ffffff","isCouslt":true}]
     */

    private String from;
    private String to;
    private boolean isCouslt;
    private String realFrom;
    private String realTo;
    private String couslt;
    private List<NoticeStrBean> noticeStr;


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

    public boolean isIsCouslt() {
        return isCouslt;
    }

    public void setIsCouslt(boolean isCouslt) {
        this.isCouslt = isCouslt;
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

    public String getCouslt() {
        return couslt;
    }

    public void setCouslt(String couslt) {
        this.couslt = couslt;
    }

    public List<NoticeStrBean> getNoticeStr() {
        return noticeStr;
    }

    public void setNoticeStr(List<NoticeStrBean> noticeStr) {
        this.noticeStr = noticeStr;
    }

    public static class NoticeStrBean {
        /**
         * str : 你好
         * type : link
         * url : www.baidu.com
         * strColor : #ffffff
         * from : shop323
         * to : hubin.hu
         * realFrom : wz.wang
         * realTo : hubin.hu
         * couslt : 4
         * isCouslt : true
         */

        private String str;
        private String type;
        private String url;
        private String strColor;
        private String from;
        private String to;
        private String realFrom;
        private String realTo;
        private String couslt;
        private boolean isCouslt;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStrColor() {
            return strColor;
        }

        public void setStrColor(String strColor) {
            this.strColor = strColor;
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

        public String getCouslt() {
            return couslt;
        }

        public void setCouslt(String couslt) {
            this.couslt = couslt;
        }

        public boolean isIsCouslt() {
            return isCouslt;
        }

        public void setIsCouslt(boolean isCouslt) {
            this.isCouslt = isCouslt;
        }
    }
}
