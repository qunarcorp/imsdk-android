package com.qunar.im.ui.view.chatExtFunc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xinbo.wang on 2016/5/18.
 */
public class FuncMap {
    public static final String PHOTO = "Album";
    public static final String FILE ="MyFile";
    public static final String LOCATION = "Location";
    public static final String VIDEO="Video";
    public static final String VIDEO_CALL="VideoCall";
//    public static final String FIREAFTERREAD=5;
    public static final String HONGBAO="RedPack";
    public static final String AA="AACollection";
    public static final String CAMERA="Camera";
    public static final String QUICKREPLY="QuickReply";
    public static final String TRANSFER = "ChatTransfer";
    public static final String Shock="Shock";
    public static final String ENCRYPT="Encrypt";
    public static final String ACTIVITY ="SendActivity";
    public static final String TASK="Task_list";
    public static final String BALLSOT="toupiao";
    public static final String SHARE_CARD="ShareCard";
    public static final String SEND_PRODUCT = "SendProduct";
    public static final String WECHAT_PUSH = "WeChatPush";
    public static final String HELPSHOP = "bangdaigou";
    public static final String COUPON = "youhuiquan";
    //投票22 任务系统15 发送产品14 帮代购16  优惠券18


    private volatile int LATESTID=9;

    private Map<String,FuncItem> funcItemMap = new LinkedHashMap<>();

    public int genNewId()
    {
        return LATESTID++;
    }

    public void regisger(FuncItem item)
    {
        funcItemMap.put(item.id,item);
    }

    public void unregisger(String key)
    {
        if(funcItemMap.containsKey(key))
            funcItemMap.remove(key);
    }

    public int getCount()
    {
        return funcItemMap.size();
    }

    public FuncItem getItem(String id)
    {
        if(funcItemMap.containsKey(id))
        {
            return funcItemMap.get(id);
        }
        return null;
    }

    public void clear(){
        if(funcItemMap != null){
            funcItemMap.clear();
        }
    }

    public Set<String> getKeys()
    {
        return funcItemMap.keySet();
    }
}
