package com.qunar.im.base.jsonbean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hubin on 2017/12/5.
 */


public  class JSONMucHistorys {


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

        private String nick;
        private TimeBean time;
        private double t;
        private MessageBean message;
        private BodyBean body;

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public double getT() {
            return t;
        }

        public void setT(double t) {
            this.t = t;
        }

        public TimeBean getTime() {
            return time;
        }

        public void setTime(TimeBean time) {
            this.time = time;
        }

        public MessageBean getMessage() {
            return message;
        }

        public void setMessage(MessageBean message) {
            this.message = message;
        }

        public BodyBean getBody() {
            return body;
        }

        public void setBody(BodyBean body) {
            this.body = body;
        }

        public static class TimeBean {
            /**
             * stamp : 20171204T12:45:19
             */

            private String stamp;

            public String getStamp() {
                return stamp;
            }

            public void setStamp(String stamp) {
                this.stamp = stamp;
            }
        }

        public static class MessageBean {
            @SerializedName("xml:lang")
            private String _$XmlLang132; // FIXME check this code
            private String client_ver;
            private String msec_times;
            private String from;
            private String to;
            private String realfrom;
            private String client_type;
            private String type;
            private String sendjid;

            public String get_$XmlLang132() {
                return _$XmlLang132;
            }

            public void set_$XmlLang132(String _$XmlLang132) {
                this._$XmlLang132 = _$XmlLang132;
            }

            public String getClient_ver() {
                return client_ver;
            }

            public void setClient_ver(String client_ver) {
                this.client_ver = client_ver;
            }

            public String getMsec_times() {
                return msec_times;
            }

            public void setMsec_times(String msec_times) {
                this.msec_times = msec_times;
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

            public String getRealfrom() {
                return realfrom;
            }

            public void setRealfrom(String realfrom) {
                this.realfrom = realfrom;
            }

            public String getClient_type() {
                return client_type;
            }

            public void setClient_type(String client_type) {
                this.client_type = client_type;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setSendjid(String sendjid) {
                this.sendjid = sendjid;
            }

            public String getSendjid() {
                return sendjid;
            }
        }

        public static class BodyBean {
            /**
             * msgType : 1
             * id : C123E0880EB24C9EB0B7A0CCF7DF94E2
             * content : [obj type="image" value="/file/v2/download/temp/a9b5e6cd8ad68938e9dd341df8345826.jpg?name=a9b5e6cd8ad68938e9dd341df8345826.jpg&file=file/a9b5e6cd8ad68938e9dd341df8345826.jpg&FileName=file/a9b5e6cd8ad68938e9dd341df8345826.jpg" width=200.000000 height=196.000000 ]
             */

            private String msgType;
            private String id;
            private String content;
            private String extendInfo;
            private String maType;
            private String backupinfo;

            public String getBackupinfo() {
                return backupinfo;
            }

            public void setBackupinfo(String backupinfo) {
                this.backupinfo = backupinfo;
            }

            public String getExtendInfo() {
                return extendInfo;
            }

            public void setExtendInfo(String extendInfo) {
                this.extendInfo = extendInfo;
            }

            public String getMaType() {
                return maType;
            }

            public void setMaType(String maType) {
                this.maType = maType;
            }

            public String getMsgType() {
                return msgType;
            }

            public void setMsgType(String msgType) {
                this.msgType = msgType;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}