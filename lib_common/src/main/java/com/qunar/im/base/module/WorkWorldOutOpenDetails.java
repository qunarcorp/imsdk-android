package com.qunar.im.base.module;

import java.io.Serializable;

public class WorkWorldOutOpenDetails implements MultiItemEntity,Serializable {

    private int count;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int getItemType() {
        return 6;
    }
}
