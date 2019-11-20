package com.qunar.im.base.module;

/**
 * Created by saber on 16-4-19.
 */
public class Dictionary extends BaseModel implements Comparable<Dictionary> {
    public static final int CATEGORY_CONFIG = 1;
    public static final int CATEGORY_CONV = 2;
    public static final int CATEGORY_CUSTOM = 3;
    public static final int CATEGORY_BACKUP=4;
    public static final int CATEGORY_CONVERSATION_PARAM = 5;
    public static final int CATEGORY_COLLECT_EMO = 6;

    public int category;
    public String key;
    public String value;
    public int version;

    @Override
    public int compareTo(Dictionary another) {
        if(this.key==null)return 0;
        return this.key.compareTo(another.key);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!Dictionary.class.isInstance(obj))return false;
        Dictionary another = (Dictionary) obj;
        return this.key!=null&&this.category==another.category&&this.key.equals(another.key);
    }
}
