package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by hubin on 2018/3/8.
 */

public class NewReadStateByJson {


    /**
     * ret : true
     * errcode : 0
     * errmsg : null
     * data : [{"readflag":3,"msgid":"0d13f27f-0588-4147-adcd-283a170b6414","id":2434163,"updatetime":1.520488506298526E9},{"readflag":3,"msgid":"b89ce99b-66eb-4e83-ab38-9be4d04c8236","id":2434164,"updatetime":1.520488506298207E9},{"readflag":3,"msgid":"8ca17243-c32e-4e70-b4c6-99c3fa5207d1","id":2434165,"updatetime":1.52048850629804E9},{"readflag":3,"msgid":"8c2d7bba-a383-4e69-8133-1024a80b599b","id":2434166,"updatetime":1.520488506298526E9},{"readflag":3,"msgid":"02920f40-c756-4a87-b5af-5dd6c82bdf5d","id":2434167,"updatetime":1.520488506298417E9},{"readflag":3,"msgid":"32aac9be-fca3-4886-a0e3-46bba274b2fa","id":2434168,"updatetime":1.520488506298417E9},{"readflag":3,"msgid":"c190a46c-e760-483a-9e98-dd5f4027020b","id":2434169,"updatetime":1.520488506298674E9}]
     */

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
         * readflag : 3
         * msgid : 0d13f27f-0588-4147-adcd-283a170b6414
         * id : 2434163
         * updatetime : 1.520488506298526E9
         */

        private int readflag;
        private String msgid;
        private int id;
        private double updatetime;

        public int getReadflag() {
            return readflag;
        }

        public void setReadflag(int readflag) {
            this.readflag = readflag;
        }

        public String getMsgid() {
            return msgid;
        }

        public void setMsgid(String msgid) {
            this.msgid = msgid;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(double updatetime) {
            this.updatetime = updatetime;
        }
    }
}
