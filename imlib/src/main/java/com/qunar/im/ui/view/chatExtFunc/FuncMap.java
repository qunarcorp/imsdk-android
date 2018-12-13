package com.qunar.im.ui.view.chatExtFunc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xinbo.wang on 2016/5/18.
 */
public class FuncMap {
    public static final int PHOTO = 1;
    public static final int FILE =2;
    public static final int LOCATION = 3;
    public static final int VIDEO=4;
    public static final int FIREAFTERREAD=5;
    public static final int HONGBAO=6;
    public static final int AA=7;
//    public static final int TRANSFER=8;
    public static final int CAMERA=8;
    public static final int QUICKREPLY=12;
    //投票22 任务系统15 发送产品14 帮代购16  优惠券18


    private volatile int LATESTID=9;

    private Map<Integer,FuncItem> funcItemMap = new LinkedHashMap<>();

    public int genNewId()
    {
        return LATESTID++;
    }

    public void regisger(FuncItem item)
    {
        funcItemMap.put(item.id,item);
    }

    public void unregisger(int key)
    {
        if(funcItemMap.containsKey(key))
            funcItemMap.remove(key);
    }

    public int getCount()
    {
        return funcItemMap.size();
    }

    public FuncItem getItem(int id)
    {
        if(funcItemMap.containsKey(id))
        {
            return funcItemMap.get(id);
        }
        return null;
    }

    public Set<Integer> getKeys()
    {
        return funcItemMap.keySet();
    }
}
