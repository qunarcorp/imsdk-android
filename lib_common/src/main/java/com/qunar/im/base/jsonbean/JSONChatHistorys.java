package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by hubin on 2017/12/5.
 */

public class JSONChatHistorys {

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

        private String from_host;
        private String to_host;
        private String from;
        private TimeBean time;
        private double t;
        private String to;
        private MessageBean message;
        private BodyBean body;
        private int read_flag;


        public double getT() {
            return t;
        }

        public void setT(double t) {
            this.t = t;
        }

        public String getFrom_host() {
            return from_host;
        }

        public void setFrom_host(String from_host) {
            this.from_host = from_host;
        }

        public String getTo_host() {
            return to_host;
        }

        public void setTo_host(String to_host) {
            this.to_host = to_host;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public TimeBean getTime() {
            return time;
        }

        public void setTime(TimeBean time) {
            this.time = time;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
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

        public int getRead_flag() {
            return read_flag;
        }

        public void setRead_flag(int read_flag) {
            this.read_flag = read_flag;
        }

        public static class TimeBean {
            /**
             * stamp : 20171204T03:20:09
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

            private String origintype;
            private String originto;
            private String msec_times;
            private String originfrom;
            private String from;
            private String to;
            private String realfrom;
            private String realto;
            private String type;
            private String realjid;
            private String client_type;
            private String qchatid;

            public String getQchatid() {
                return qchatid;
            }

            public void setQchatid(String qchatid) {
                this.qchatid = qchatid;
            }

            public String getRealto() {
                return realto;
            }

            public void setRealto(String realto) {
                this.realto = realto;
            }

            public String getClient_type() {
                return client_type;
            }

            public void setClient_type(String client_type) {
                this.client_type = client_type;
            }

            public String getOrigintype() {
                return origintype;
            }

            public void setOrigintype(String origintype) {
                this.origintype = origintype;
            }

            public String getOriginto() {
                return originto;
            }

            public void setOriginto(String originto) {
                this.originto = originto;
            }

            public String getMsec_times() {
                return msec_times;
            }

            public void setMsec_times(String msec_times) {
                this.msec_times = msec_times;
            }

            public String getOriginfrom() {
                return originfrom;
            }

            public void setOriginfrom(String originfrom) {
                this.originfrom = originfrom;
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

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getRealjid() {
                return realjid;
            }

            public void setRealjid(String realjid) {
                this.realjid = realjid;
            }
        }

        public static class BodyBean {
            /**
             * maType : 1
             * msgType : 1
             * id : e1fb97e4dbc84799900a244cd921bd58_9EA7304C62E14F7183833468D41A6D0F
             * content : 1
             */

            private String maType;
            private String msgType;
            private String id;
            private String content;
            private String extendInfo;

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
