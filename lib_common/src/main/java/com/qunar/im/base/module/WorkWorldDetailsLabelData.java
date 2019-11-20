package com.qunar.im.base.module;

import java.io.Serializable;

public class WorkWorldDetailsLabelData implements MultiItemEntity,Serializable {

    public static int all = 1;
    public static int hot = 0;

    private String name;
    private int count;
    private int type;

    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int getItemType() {
        return 3;
    }
}
