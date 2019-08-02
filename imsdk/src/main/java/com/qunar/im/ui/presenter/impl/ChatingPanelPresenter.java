package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.module.ChatingExtention;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.module.UserConfigData;
import com.qunar.im.ui.presenter.IChatingPanelPresenter;
import com.qunar.im.ui.presenter.views.IChatingPanelView;
import com.qunar.im.ui.presenter.views.IShowNickView;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.other.CacheDataType;
import com.qunar.im.utils.ConnectionUtil;

public class ChatingPanelPresenter implements IChatingPanelPresenter {
    private static final String TAG ="ChatingPanelPresenter";

    IChatingPanelView panelView;
    IShowNickView showNickView;
    //核心连接管理类
    private ConnectionUtil connectionUtil;
    private RecentConversation rc;

    @Override
    public void setPanelView(IChatingPanelView view) {
        panelView = view;
        connectionUtil = ConnectionUtil.getInstance();
        //注意 这个rc对象查出来的应该是不准的
        rc = connectionUtil.selectRecentConversationByXmppId(view.getJid());
//        view.setRecentConversation(connectionUtil.);

    }

    @Override
    public void setShowNickView(IShowNickView view) {
        showNickView = view;
    }


    @Override
    public void topMessage() {

    }

    /**
     * 设置置顶或者取消置顶
     */
    @Override
    public void setConversationTopOrCancel() {
        UserConfigData userConfigData = new UserConfigData();
        userConfigData.setKey(CacheDataType.kStickJidDic);
        userConfigData.setSubkey(panelView.getJid()+"<>"+panelView.getRJid());
        UserConfigData.TopInfo topInfo = IMDatabaseManager.getInstance().selectSessionChatType(userConfigData);
        userConfigData.setTopInfo(topInfo);
        connectionUtil.setConversationTopOrCancel(userConfigData, new ConnectionUtil.CallBackByUserConfig() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    /**
     * 设置提醒或取消提醒
     */
    @Override
    public void setConversationReMindOrCancel() {
        UserConfigData userConfigData = new UserConfigData();
        userConfigData.setKey(CacheDataType.kNoticeStickJidDic);
        userConfigData.setSubkey(panelView.getJid());
        connectionUtil.setConversationReMindOrCancel(userConfigData, new ConnectionUtil.CallBackByUserConfig() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    /**
     * 显示现在开关是否置顶
     */
    @Override
    public void showIsTop() {
        RecentConversation rc = new RecentConversation();
        rc.setId(panelView.getJid());
        rc.setRealUser(panelView.getRJid());
        rc = connectionUtil.SelectConversationByRC(rc);
//        recentConvDataModel.selectRecentConvById(rc);
        panelView.setTop(rc.getTop()>0);
    }

    @Override
    public void showIsDnd() {
        RecentConversation rc = new RecentConversation();
        rc.setId(panelView.getJid());
        rc.setRealUser(panelView.getRJid());
        rc = connectionUtil.SelectConversationByRC(rc);
        panelView.setReMind(rc.getRemind()>0);
    }

    @Override
    public void showIsNick() {
        ChatingExtention extention = new ChatingExtention();
        extention.setId(panelView.getJid());
        showNickView.setShowNick(extention.getShowNick() == ChatingExtention.SHOW_NICK);
    }

    @Override
    public void dnd() {

    }

    @Override
    public void nick(boolean param) {
        ChatingExtention extention = new ChatingExtention();
        extention.setId(panelView.getJid());
        showNickView.setShowNick(param);
    }
}
