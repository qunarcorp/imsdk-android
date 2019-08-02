package com.qunar.im.utils;

import com.qunar.im.base.module.QuickReplyData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lex lex on 2018/7/30.
 */
public class QuickReplyUtils {

    public static Map<String, List<String>> quickRepliesMerchant = new LinkedHashMap<>();

    public static void getQuickReplies() {

        List<QuickReplyData> list = ConnectionUtil.getInstance().selectQuickReplies();

        for(int i = 0; i < list.size(); i++) {
            QuickReplyData quickReplyData = list.get(i);
            quickRepliesMerchant.put(quickReplyData.groupname, quickReplyData.contents);
        }
    }
}
