package com.qunar.im.base.jsonbean;

/**
 * Created by hubin on 2018/3/30.
 */

public class LeadInfo {


    /**
     * msg : 查询成功
     * data :
     * errcode : 0
     */

    private String msg;
    private DataBean data;
    private int errcode;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public static class DataBean {

        private String leader;
        private String email;
        private String qtalk_id;
        private String sn;

        public String getLeader() {
            return leader;
        }

        public void setLeader(String leader) {
            this.leader = leader;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getQtalk_id() {
            return qtalk_id;
        }

        public void setQtalk_id(String qtalk_id) {
            this.qtalk_id = qtalk_id;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }
    }
}
