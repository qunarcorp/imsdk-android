package com.qunar.im.base.util;

import android.text.TextUtils;
import android.util.LruCache;

import com.qunar.im.base.module.IMMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by saber on 15-8-18.
 */
public class InternDatas {
    //头像地址缓存
    public static LruCache<String, String> JidToUrl = new LruCache<String, String>(144);
    public static LruCache<String,Object> cache = new LruCache<String,Object>(144);
    //qtalk时，缓存非组织架构
    private static LruCache<String, String> notInOrgnazition = new LruCache<String, String>(144);
    //缓存nick -> jid
    private static LruCache<String, String> nick2Jid = new LruCache<String, String>(144);
    //草稿
    private static Map<String,String> draftData = new LinkedHashMap<String,String>();
    //缓存组织架构
    private static Map<String, Object> internMap = new LinkedHashMap<String, Object>();
    //缓存ChatId
    public static LruCache<String,String> chatidCache = new LruCache<String,String>(144);

    //我发送的消息,收到应答移除
    public static List<String> sentMsgIdByMe = new ArrayList<String>();
    //缓存发送中队列，发送成功或者重试两次移除
    public static Map<String,IMMessage> sendingLine = new ConcurrentHashMap<>();

    public static String getJid(String nick)
    {
        return nick2Jid.get(nick);
    }

    public static void  saveJid(String nick,String jid)
    {
        if (nick == null) return;
        if (jid == null) jid = "";
        nick2Jid.put(nick,jid);
    }

    public static String getName(String uid) {
        return notInOrgnazition.get(uid);
    }

    public static void saveName(String uid, String nickName) {
        if (uid == null) return;
        if (nickName == null) nickName = "";
        notInOrgnazition.put(uid, nickName);
    }

    public static String getDraft(String id) {
        if (draftData.containsKey(id)) {
            return draftData.get(id);
        }
        return "";
    }

    public static void putDraft(String id, String content) {
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(content)) {
            draftData.put(id, content);
        }
    }

    public static void removeDraft(String id){
        if(id != null){
            draftData.remove(id);
        }
    }

    public static void addData(String key, Object obj) {
        internMap.put(key, obj);
    }

    public static Object getData(String key) {
        return internMap.get(key);
    }

    public static boolean existKey(String key)
    {
        return internMap.containsKey(key);
    }
}
