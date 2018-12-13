package com.qunar.im.ui.entity;

import com.qunar.im.ui.adapter.MyFilesAdapter;
import com.qunar.im.ui.view.recyclerview.entity.AbstractExpandableItem;
import com.qunar.im.ui.view.recyclerview.entity.MultiItemEntity;

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
