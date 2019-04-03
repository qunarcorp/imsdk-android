package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IChatRoomListView;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public interface IChatRoomManagePresenter {
    void listGroups();
    void forceReloadChatRooms();
    void loadCache();
    void onClientConnected();
    void clearTemporaryRoom();
    void setChatRoomManagerView(IChatRoomListView view);
}
