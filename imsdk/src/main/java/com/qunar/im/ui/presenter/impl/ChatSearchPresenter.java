package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.views.IChatSearchView;
import com.qunar.im.utils.ConnectionUtil;

import java.util.List;

/**
 * 会话内搜索
 */
public class ChatSearchPresenter {
    IChatSearchView iChatSearchView;

    public void setView(IChatSearchView view){
        this.iChatSearchView = view;
    }

    public void searchImgAndVideo(String xmppId, String realJid, int startOff, int end){
        List<IMMessage> result =  ConnectionUtil.getInstance().searchImageVideoMsg(xmppId,realJid,startOff,end);
        if(iChatSearchView != null){
            iChatSearchView.setSearchResult(result);
        }
    }
}
