package com.qunar.im.other;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试账号类
 */
public class TestAccount {

    private static Map<String,String> testAccountMap = new HashMap<>();

    static {

    }

    public static boolean isTestAccount(String account){
        return testAccountMap.containsKey(account);
    }

    public static String getTestAccountToken(String account){
        return testAccountMap.get(account);
    }
}
