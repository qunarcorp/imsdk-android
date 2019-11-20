package com.qunar.im.base.module;

import java.io.Serializable;

import static com.qunar.im.base.module.ReleaseCircleType.TYPE_CLICKABLE;

public class ReleaseCircleImageItemDate implements MultiItemEntity,Serializable {


    @Override
    public int getItemType() {
        return TYPE_CLICKABLE;
    }
}
