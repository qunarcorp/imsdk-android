package com.qunar.im.ui.presenter.views;

import android.content.Context;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.RecentConversation;

import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public interface IConversationListView {
    void setRecentConvList(List<RecentConversation> convers);
    void setRecentConvListCache(List<RecentConversation> convers);
    String getXmppId();
    String getRealUserId();
    Context getContext();
    boolean isOnlyUnRead();
    void refresh();
    boolean readAllConversations();
    void parseEncryptMessage(IMMessage message);
    void showFileSharing();
    void hidenFileSharing();
    void loginState(boolean isLogin);

    RecentConversation getCurrentConv();

    void CreadSession(Map<String, RecentConversation> sessionMap, List<RecentConversation> list, IMMessage message);
    void showDialog(String msg);
}
