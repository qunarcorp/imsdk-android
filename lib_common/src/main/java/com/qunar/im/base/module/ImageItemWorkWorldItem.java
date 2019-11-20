package com.qunar.im.base.module;

import java.io.Serializable;

import static com.qunar.im.base.module.ReleaseCircleType.TYPE_WORK_WORLD_ITEM;

public class ImageItemWorkWorldItem extends ImageItem implements Serializable  {

    @Override
    public int getItemType() {
        return TYPE_WORK_WORLD_ITEM;
    }
}
