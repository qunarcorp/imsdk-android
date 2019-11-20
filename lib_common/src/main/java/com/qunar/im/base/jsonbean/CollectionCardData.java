package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by hubin on 2017/11/28.
 */

public class CollectionCardData  {

    /**
     * ret : true
     * errcode : 0
     * errmsg : null
     * data : [{"username":"hubo.hu","domain":"qunar.com","usernick":"胡泊hu","url":"file/v2/download/avatar/c3232e4f8b18dcc109f6a8df2c4198cf.jpg?name=c3232e4f8b18dcc109f6a8df2c4198cf.jpg&file=c3232e4f8b18dcc109f6a8df2c4198cf.jpg&fileName=c3232e4f8b18dcc109f6a8df2c4198cf.jpg","version":2}]
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

        private String username;
        private String domain;
        private String usernick;
        private String url;
        private int version;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getUsernick() {
            return usernick;
        }

        public void setUsernick(String usernick) {
            this.usernick = usernick;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }
}
