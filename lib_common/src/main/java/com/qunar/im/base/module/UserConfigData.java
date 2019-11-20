package com.qunar.im.base.module;

import java.util.List;

public class UserConfigData {

    private String username;
    private String host;
    private String key;
    private String subkey;
    private String value;
    private String operate_plat;
    private String resource;
    private int version;
    private int type;//操作用
    private int isdel;
    private TopInfo topInfo;

    public TopInfo getTopInfo() {
        return topInfo;
    }

    public void setTopInfo(TopInfo topInfo) {
        this.topInfo = topInfo;
    }

    private List<Info> batchProcess;

    public List<Info> getBatchProcess() {
        return batchProcess;
    }

    public void setBatchProcess(List<Info> batchProcess) {
        this.batchProcess = batchProcess;
    }

    public String getOperate_plat() {
        return operate_plat;
    }

    public void setOperate_plat(String operate_plat) {
        this.operate_plat = operate_plat;
    }

    public String getSubkey() {
        return subkey;
    }

    public void setSubkey(String subkey) {
        this.subkey = subkey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getIsdel() {
        return isdel;
    }

    public void setIsdel(int isdel) {
        this.isdel = isdel;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public static class Info{
        private String key;
        private String subkey;
        private String value;


        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSubkey() {
            return subkey;
        }

        public void setSubkey(String subkey) {
            this.subkey = subkey;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class TopInfo{
        private String topType;
        private String chatType;

        @Override
        public String toString() {
            return "TopInfo{" +
                    "topType='" + topType + '\'' +
                    ", chatType='" + chatType + '\'' +
                    '}';
        }

        public  String toJson(){
            return "{" +
                    "\"topType\":" + topType +
                    ", \"chatType\":" + chatType +
                    "}";
        }

        public String getTopType() {
            return topType;
        }

        public void setTopType(String topType) {
            this.topType = topType;
        }

        public String getChatType() {
            return chatType;
        }

        public void setChatType(String chatType) {
            this.chatType = chatType;
        }
    }
}
