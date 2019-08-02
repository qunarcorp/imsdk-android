package com.qunar.im.base.view.faceGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/2/5.
 */
public class EmoticionMap {
    public Map<String,EmoticonEntity> emoticonEntityMap;

    private List<String> indexes;

    public int count;
    public String version;
    public int showAll;
    public int line;
    public String packgeId;

    public EmoticionMap(String v,int c,int s,int l,String p)
    {
        this.count = c;
        this.version = v;
        this.showAll = s;
        this.line = l;
        this.packgeId = p;
        indexes = new ArrayList<String>(count);
        emoticonEntityMap = new HashMap<String,EmoticonEntity>(count);
    }

    public void pusEntity(String key,EmoticonEntity entity)
    {
        indexes.add(key);
        emoticonEntityMap.put(key,entity);
    }

    public EmoticonEntity getEntity(String key)
    {
        return emoticonEntityMap.get(key);
    }

    public EmoticonEntity getEntity(int index)
    {
        String key = indexes.get(index);
        return getEntity(key);
    }

    public boolean containKey(String key)
    {
        return emoticonEntityMap.containsKey(key);
    }
}
