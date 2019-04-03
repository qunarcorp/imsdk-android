package com.qunar.im.ui.presenter.views;

import android.content.Context;
import android.util.SparseArray;

import com.qunar.im.base.module.Nick;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public interface IChatRoomListView {
    void setGroupList(SparseArray<List<Nick>> childs);
    void resetListView();
    Context getContext();
}
