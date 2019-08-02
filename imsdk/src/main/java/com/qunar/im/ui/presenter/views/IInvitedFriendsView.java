package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.module.Nick;
import com.qunar.im.base.view.multilLevelTreeView.Node;

import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/3/4.
 */
public interface IInvitedFriendsView {
     List<Node> getSelectedFriends();
     void setAllContacts(List<Nick> contacts);
     String getRoomId();
     String getFullName();

     /**
      * 初始化树形界面
      */
     void initTreeView(Map<Integer, List<Node>> mAllNodes);

     void setResult(boolean result);
}
