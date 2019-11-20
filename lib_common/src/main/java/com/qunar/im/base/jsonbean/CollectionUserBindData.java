package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by hubin on 2017/11/30.
 */

public class CollectionUserBindData {


    /**
     * ret : true
     * errcode : 0
     * errmsg : null
     * data : [{"bindname":"lihaibin.li","user_name":"李海彬li","action":1,"bindhost":"qunar.com","url":null},{"bindname":"hubo.hu","user_name":"胡泊hu","action":1,"bindhost":"qunar.com","url":"file/v2/download/avatar/7529949ae407925f7c4123a1b47c7487.jpg?name=7529949ae407925f7c4123a1b47c7487.jpg&file=7529949ae407925f7c4123a1b47c7487.jpg&fileName=7529949ae407925f7c4123a1b47c7487.jpg"},{"bindname":"lffan.liu","user_name":"刘帆lf","action":1,"bindhost":"qunar.com","url":"/file/v2/download/avatar/856ceefaf6e145f3ed9fd3a5634d1dc6.gif?name=856ceefaf6e145f3ed9fd3a5634d1dc6.gif&file=file/856ceefaf6e145f3ed9fd3a5634d1dc6.gif&fileName=file/856ceefaf6e145f3ed9fd3a5634d1dc6.gif"},{"bindname":"hubin.hu","user_name":"胡滨hubin","action":1,"bindhost":"qunar.com","url":null}]
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

        private String bindname;
        private String user_name;
        private int action;
        private String bindhost;
        private String url;

        public String getBindname() {
            return bindname;
        }

        public void setBindname(String bindname) {
            this.bindname = bindname;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        public String getBindhost() {
            return bindhost;
        }

        public void setBindhost(String bindhost) {
            this.bindhost = bindhost;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
