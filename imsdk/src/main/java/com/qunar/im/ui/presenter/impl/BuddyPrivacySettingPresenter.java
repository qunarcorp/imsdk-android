package com.qunar.im.ui.presenter.impl;

import com.qunar.im.ui.presenter.IChangeBuddyPrivacySetting;

/**
 * 好友列表的逻辑实现
 */

public class BuddyPrivacySettingPresenter {
    private static final String TAG = BuddyPrivacySettingPresenter.class.getSimpleName();

    IChangeBuddyPrivacySetting mView;
    public BuddyPrivacySettingPresenter(IChangeBuddyPrivacySetting view){
        mView = view;
    }
    public void getMode(String targetId) {
        //get mode from database first
        // TODO: 2019/1/17 功能缺失，暂未实现
        VerifyQuestion verfiy = new VerifyQuestion();
//        verfiy.question = CurrentPreference.getInstance().extConfig.buddyPrivacySettingQuestion;
//        verfiy.answer = CurrentPreference.getInstance().extConfig.buddyPrivacySettingAnswer;
//        updateView(CurrentPreference.getInstance().extConfig.buddyPrivacySettingType,verfiy);


    }

    private void updateView(String mode ,VerifyQuestion question){
        if("0".equals(mode)){
            mView.setMode(0,null);
        }else if("1".equals(mode)){
            mView.setMode(1,null);
        }else if("2".equals(mode)){
            mView.setMode(2,question);
        }else if("3".equals(mode)){
            mView.setMode(3,null);
        }
    }

    public void updateServer(final int mode, final BuddyPrivacySettingPresenter.VerifyQuestion question){
    }
    public static class VerifyQuestion{
       public  String question;
       public  String answer;
    }

}
