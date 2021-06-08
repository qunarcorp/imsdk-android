package com.qunar.im.base.module;

public class WorkWorldAtShowItem extends WorkWorldNoticeItem{

    @Override
    public int getItemType() {
        return Integer.parseInt(getEventType());
    }
}
