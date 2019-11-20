package com.qunar.im.base.jsonbean;

/**
 * Created by hubo.hu on 2017/10/31.
 */

public class RbtMsgBackupInfo {
    public int type;
    public Data data;

    public static class Data {
        public String bu = "";
        public String bsid = "";
        public String pid = "";
        public int rbtMsg;
    }
}
