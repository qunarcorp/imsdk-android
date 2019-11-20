package com.qunar.im.log;

/**
 * Created by froyomu on 2019/1/25
 * <p>
 * Describe: 日志相关
 */
public class LogConstans {
    public static final class LogType{
        public static final String CAT = "CAT";//端到端日志
        public static final String COD = "COD";//代码级日志
        public static final String FIL = "FIL";//文件日志
        public static final String CRA = "CRA";//崩溃异常日志
        public static final String ACT = "ACT";//行为日志
    }

    public static final class LogSubType{
        /**
         * COD SubType
         */
        public static final String SQL = "sql";
        public static final String NATIVE = "native";

        /**
         * CAT SubType
         */
        public static final String TCP = "tcp";
        public static final String HTTP = "http";

        /**
         * ACT SubType
         */
        public static final String CLICK = "click";
        public static final String SHOW = "show";

        /**
         * CRA SubType
         */
        public static final String CRASH = "crash";
        public static final String ANR = "anr";
    }
}
