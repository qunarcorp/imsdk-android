package com.qunar.im.ui.presenter.impl;

import com.qunar.im.utils.HttpUtil;
import com.qunar.im.ui.presenter.IProfilePresenter;
import com.qunar.im.ui.presenter.views.IChangeFontSizeView;
import com.qunar.im.ui.presenter.views.IProfileView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.PushSettinsStatus;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.common.CurrentPreference;

/**
 * Created by xinbo.wang on 2015/4/2.
 * updated by huayu.chen on 16/5/20.
 */
//个人设置实现
public class ProfilePresenter implements IProfilePresenter {

    IProfileView profileView;

    @Override
    public void setProfileView(IProfileView view) {
        profileView = view;
    }

    @Override
    public void changeMsgSoundState() {
        boolean state = profileView.getMsgSoundState();
        CurrentPreference.getInstance().setTurnOnMsgSound(state);
        IMDatabaseManager.getInstance().updateConfig();
    }

    @Override
    public void changeMsgShockState() {
        boolean state = profileView.getMsgShockState();
        CurrentPreference.getInstance().setTurnOnMsgShock(state);
        IMDatabaseManager.getInstance().updateConfig();
    }

    @Override
    public void changePushState() {
        boolean state = profileView.getPushMsgState();
        CurrentPreference.getInstance().setTurnOnPsuh(state);
        IMDatabaseManager.getInstance().updateConfig();
    }

    @Override
    public void changeShakeEvent() {
//        boolean state = profileView.getShakeEvent();
//        CurrentPreference.getInstance().setShakeEvent(state);
//        CurrentPreference.getInstance().saveProfile();
    }

    @Override
    public void changeLandscapeState() {
//        boolean state = profileView.getLandscape();
//        CurrentPreference.getInstance().setLandscape(state);
//        CurrentPreference.getInstance().saveProfile();
    }

    @Override
    public void changeFontSize() {
        if(profileView instanceof IChangeFontSizeView) {
            int state = ((IChangeFontSizeView)profileView).getFontSizeMode();
            CurrentPreference.getInstance().setFontSizeMode(state);
            IMDatabaseManager.getInstance().updateConfig();
        }
    }

    @Override
    public void changePushShowContent() {
        final boolean state = profileView.getPushShowContent();

        HttpUtil.setPushMsgSettings(PushSettinsStatus.SHOW_CONTENT, state ? 1 : 0, new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                CurrentPreference.getInstance().setShowContent(state);
                IMDatabaseManager.getInstance().updateConfig();
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });

    }

    @Override
    public void changeOfflinePush() {
        final boolean state = profileView.getOfflinePush();
        HttpUtil.setPushMsgSettings(PushSettinsStatus.PUSH_ONLINE, state ? 1 : 0, new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                CurrentPreference.getInstance().setOfflinePush(state);
                IMDatabaseManager.getInstance().updateConfig();
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
//        Protocol.setPushState(state, new ProtocolCallback.UnitCallback<String>() {
//            @Override
//            public void onCompleted(String s) {
//                Logger.i("changeOfflinePush  onCompleted  " + s);
//            }
//
//            @Override
//            public void onFailure() {
//                Logger.i("changeOfflinePush  onFailure  ");
//            }
//        });

    }


}
