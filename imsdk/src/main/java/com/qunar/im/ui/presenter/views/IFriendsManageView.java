package com.qunar.im.ui.presenter.views;


import android.content.Context;

import com.qunar.im.base.module.Nick;
import com.qunar.im.base.view.multilLevelTreeView.Node;

import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/3/17.
 */
public interface IFriendsManageView {
    void setFrineds(Map<Integer, List<Node>> contacts);  //重新刷新列表
    void setBuddyFrineds(Map<Integer, List<Nick>> contacts);  //重新刷新列表

    boolean isTransfer();//是否是会话转移

    void resetListView();
    /**
     * 返回根节点名称
     * @return
     */
    String getRootName();

    Context getContext();
}
