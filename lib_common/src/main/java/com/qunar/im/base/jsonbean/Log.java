package com.qunar.im.base.jsonbean;

import java.util.List;

public class Log {
    public List<LogInfo> infos;
    public User user;
    public Device device;

    public static class User{
        public String uid;
        public String domain;
        public String nav;
    }

    public static class Device{

        /**
         * os : Android
         * osBrand : BKL HUAWEI
         * osModel : BKL-AL20
         * osVersion : 26
         * versionCode : 218
         * versionName : 3.1.0
         * plat : qtalk
         * ip : 127.0.0.1
         * lat : 39.983605
         * lgt : 116.312536
         * net : WIFI
         */

        public String os;
        public String osBrand;
        public String osModel;
        public int osVersion;
        public String versionCode;
        public String versionName;
        public String plat;
        public String ip;
        public String lat;
        public String lgt;
        public String net;
    }

}
