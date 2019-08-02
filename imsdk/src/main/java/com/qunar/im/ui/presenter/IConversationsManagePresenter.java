package com.qunar.im.ui.presenter;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.views.IConversationListView;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public interface   IConversationsManagePresenter {
    void setCoversationListView(IConversationListView view);
    void deleteCoversation();
    void deleteChatRecord();
    void showRecentConvs();
    void initReload(boolean toDB);
    void handleMessage(IMMessage message);
    void allRead();
    void markReadById();
    void removeEvent();


}
