package com.qunar.im.base.jsonbean;

/**
 * Created by hubin on 2018/2/9.
 */

public class PubKeyBean {

    /**
     * ret : true
     * errcode : 0
     * errmsg : null
     * data : {"rsa_pub_key_shortkey":"","rsa_pub_key_fullkey":"","pub_key_fullkey":"-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCy2VXDAlCZlj7gPHvC/vwvbpTN\n/GyW0tmNCqh0UPitdTTGZk3UcLqu9lWMGPViL/5lhboiSogsDxJLHdwo91DDBjTX\n1HbuyuOhvsvayV7Yc8t+ajFW/8RwlvhGSzVplthoU+md9kGeZ8t73VWWZUEB0iyW\nx7Y/RjUwTdnOlNXDzQIDAQAB\n-----END PUBLIC KEY-----","pub_key_shortkey":"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCy2VXDAlCZlj7gPHvC/vwvbpTN/GyW0tmNCqh0UPitdTTGZk3UcLqu9lWMGPViL/5lhboiSogsDxJLHdwo91DDBjTX1HbuyuOhvsvayV7Yc8t+ajFW/8RwlvhGSzVplthoU+md9kGeZ8t73VWWZUEB0iyWx7Y/RjUwTdnOlNXDzQIDAQAB"}
     */

    private boolean ret;
    private int errcode;
    private Object errmsg;
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

    public Object getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(Object errmsg) {
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
         * rsa_pub_key_shortkey :
         * rsa_pub_key_fullkey :
         * pub_key_fullkey : -----BEGIN PUBLIC KEY-----
         MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCy2VXDAlCZlj7gPHvC/vwvbpTN
         /GyW0tmNCqh0UPitdTTGZk3UcLqu9lWMGPViL/5lhboiSogsDxJLHdwo91DDBjTX
         1HbuyuOhvsvayV7Yc8t+ajFW/8RwlvhGSzVplthoU+md9kGeZ8t73VWWZUEB0iyW
         x7Y/RjUwTdnOlNXDzQIDAQAB
         -----END PUBLIC KEY-----
         * pub_key_shortkey : MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCy2VXDAlCZlj7gPHvC/vwvbpTN/GyW0tmNCqh0UPitdTTGZk3UcLqu9lWMGPViL/5lhboiSogsDxJLHdwo91DDBjTX1HbuyuOhvsvayV7Yc8t+ajFW/8RwlvhGSzVplthoU+md9kGeZ8t73VWWZUEB0iyWx7Y/RjUwTdnOlNXDzQIDAQAB
         */

        private String rsa_pub_key_shortkey;
        private String rsa_pub_key_fullkey;
        private String pub_key_fullkey;
        private String pub_key_shortkey;

        public String getRsa_pub_key_shortkey() {
            return rsa_pub_key_shortkey;
        }

        public void setRsa_pub_key_shortkey(String rsa_pub_key_shortkey) {
            this.rsa_pub_key_shortkey = rsa_pub_key_shortkey;
        }

        public String getRsa_pub_key_fullkey() {
            return rsa_pub_key_fullkey;
        }

        public void setRsa_pub_key_fullkey(String rsa_pub_key_fullkey) {
            this.rsa_pub_key_fullkey = rsa_pub_key_fullkey;
        }

        public String getPub_key_fullkey() {
            return pub_key_fullkey;
        }

        public void setPub_key_fullkey(String pub_key_fullkey) {
            this.pub_key_fullkey = pub_key_fullkey;
        }

        public String getPub_key_shortkey() {
            return pub_key_shortkey;
        }

        public void setPub_key_shortkey(String pub_key_shortkey) {
            this.pub_key_shortkey = pub_key_shortkey;
        }
    }
}

