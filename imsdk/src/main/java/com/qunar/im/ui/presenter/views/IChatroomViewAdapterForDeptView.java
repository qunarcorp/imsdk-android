package com.qunar.im.ui.presenter.views;

import android.util.SparseArray;

import com.qunar.im.base.module.Nick;

import java.util.List;

/**
 * Created by saber on 15-7-2.
 */
public abstract class IChatroomViewAdapterForDeptView implements IChatRoomListView  {
    @Override
    public void setGroupList( SparseArray<List<Nick>> childs) {}

    @Override
    public void resetListView() {}
}
