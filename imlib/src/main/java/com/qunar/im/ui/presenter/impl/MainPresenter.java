package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.ui.presenter.IMainPresenter;
import com.qunar.im.ui.presenter.views.IMainView;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.enums.LoginStatus;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.utils.MD5;

/**
 * Created by xingchao.song on 3/15/2016.
 */
public class MainPresenter implements IMainPresenter, IMNotificaitonCenter.NotificationCenterDelegate {
    IMainView mMainView;
    //核心连接管理类
    private ConnectionUtil connectionUtil;


    public MainPresenter(IMainView mainView) {
        mMainView = mainView;
        connectionUtil = ConnectionUtil.getInstance();
        addEvent();

    }

    //注册通知
    public void addEvent() {
        //二人消息通知
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Text);
        //群组消息通知
        connectionUtil.addEvent(this, QtalkEvent.Group_Chat_Message_Text);
        //已读通知
        connectionUtil.addEvent(this, QtalkEvent.Message_Read_Mark);
        //登陆状态通知
        connectionUtil.addEvent(this, QtalkEvent.LOGIN_EVENT);
        //收到一个删除list的通知(被踢出群)
        connectionUtil.addEvent(this, QtalkEvent.Remove_Session);
        connectionUtil.addEvent(this, QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION);
        connectionUtil.addEvent(this, QtalkEvent.Update_ReMind);
        //修改消息状态
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Read_State);

        connectionUtil.addEvent(this, QtalkEvent.refreshOPSUnRead);

        connectionUtil.addEvent(this, QtalkEvent.CLEAR_MESSAGE);

        connectionUtil.addEvent(this, QtalkEvent.CLEAR_BRIDGE_OPS);
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Text_After_DB);
        connectionUtil.addEvent(this, QtalkEvent.Group_Chat_Message_Text_After_DB);
        connectionUtil.addEvent(this, QtalkEvent.Show_Home_Unread_Count);
        connectionUtil.addEvent(this, QtalkEvent.WORK_WORLD_NOTICE);
        connectionUtil.addEvent(this,QtalkEvent.WORK_WORLD_FIND_NOTICE);
    }

    @Override
    public void getUnreadConversationMessage() {
        int total = connectionUtil.SelectUnReadCount();
        mMainView.refreshShortcutBadger(total);

        mMainView.setUnreadConversationMessage(total);

    }


    @Override
    public void showErrorMessage(String str) {
        mMainView.showDialog(str);
    }

    @Override
    public void refreshOPSUnRead(final boolean isShow) {
        DispatchHelper.Async("refreshOPSUnRead", new Runnable() {
            @Override
            public void run() {
                mMainView.refreshOPSUnRead(isShow);
            }
        });
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        final String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");

        switch (key) {
            case QtalkEvent.Show_Home_Unread_Count:
            case QtalkEvent.Message_Read_Mark:
            case QtalkEvent.Chat_Message_Read_State:
            case QtalkEvent.Chat_Message_Text_After_DB:
            case QtalkEvent.Group_Chat_Message_Text_After_DB:
            case QtalkEvent.Remove_Session:
            case QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION:
            case QtalkEvent.Update_ReMind:
            case QtalkEvent.CLEAR_MESSAGE:
                getUnreadConversationMessage();
                break;

            case QtalkEvent.CLEAR_BRIDGE_OPS:
                CurrentPreference.getInstance().setSwitchAccount(true);
                mMainView.refresh();
                break;

            case QtalkEvent.LOGIN_EVENT:

                //当登陆成功后
                if (args[0].equals(LoginStatus.Login)) {
                    //去获取所有未读消息条目数
                    getUnreadConversationMessage();
                    mMainView.loginSuccess();
//                    mMainView.startOPS();
                } else if (args[0].equals(LoginStatus.Updating)) {
                    mMainView.refresh();
                    mMainView.synchronousing();
                }
                break;
            case QtalkEvent.refreshOPSUnRead:
                refreshOPSUnRead((Boolean) args[0]);
                break;

            case QtalkEvent.WORK_WORLD_FIND_NOTICE:
            case QtalkEvent.WORK_WORLD_NOTICE:
                int count = ConnectionUtil.getInstance().selectWorkWorldNotice();
                boolean workWorldUnReadState  = IMUserDefaults.getStandardUserDefaults().getBooleanValue(CommonConfig.globalContext,
                        com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                + QtalkNavicationService.getInstance().getXmppdomain()
                                + CommonConfig.isDebug
                                + MD5.hex(navurl)
                                + "WORKWORLDSHOWUNREAD", false);;
                if ((count > 0)||workWorldUnReadState) {

                    mMainView.refreshNoticeRed(true);
                } else {
//                    mMainView.hiddenHeadView();
                    mMainView.refreshNoticeRed(false);
                }
                break;
        }
    }
}
