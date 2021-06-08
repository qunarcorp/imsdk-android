package com.qunar.im.base.jsonbean;

/**
 * Created by xingchao.song on 3/15/2016.
 * 从ops获取是否有未读消息
 */
public class OpsUnreadResult {

    /**
     * msg :
     * data : {"hasUnread":true}
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
        /**
         * hasUnread : true
         */

        private boolean hasUnread;

        public boolean isHasUnread() {
            return hasUnread;
        }

        public void setHasUnread(boolean hasUnread) {
            this.hasUnread = hasUnread;
        }
    }
}
