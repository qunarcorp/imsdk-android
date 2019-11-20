package com.qunar.im.base.util;


import java.util.HashMap;
import java.util.Map;

/**
 * 数据中心 运行时数据
 * Created by lihaibin.li on 2017/8/29.
 */

public class DataCenter {

    //当前加密会话用户
    public static Map<String,String> encryptUsers = new HashMap<>();

    //当前解密会话用户
    public static Map<String,String> decryptUsers = new HashMap<>();

    //当前发送的本地图片对 messageid-path
    public static Map<String,String> localImageMessagePath = new HashMap<>();

    public static void clear(){
        encryptUsers.clear();
        decryptUsers.clear();
    }
}
