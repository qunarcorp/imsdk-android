package com.qunar.im.ui.entity;

import com.qunar.im.base.module.AbstractExpandableItem;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.ui.adapter.MyFilesAdapter;

/**
 * Created by Lex lex on 2018/5/30.
 */

public class MyFilesTitle extends AbstractExpandableItem<MyFilesItem> implements MultiItemEntity {

    public String title;

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public int getLevel() {
        return MyFilesAdapter.TYPE_LEVEL_0;
    }
}
