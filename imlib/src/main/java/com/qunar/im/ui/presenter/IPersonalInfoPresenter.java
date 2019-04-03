package com.qunar.im.ui.presenter;

import com.qunar.im.base.common.ICommentView;
import com.qunar.im.ui.presenter.views.IGravatarView;
import com.qunar.im.ui.presenter.views.IPersonalInfoView;

/**
 * Created by xinbo.wang on 2015/3/24.
 */
public interface IPersonalInfoPresenter {
    void setGravatarView(IGravatarView view);
    void setPersonalInfoView(IPersonalInfoView view);
    void setCommentView(ICommentView view);
    void loadPersonalInfo();
    void updateMyPersonalInfo();
    void loadGravatar(boolean forceUpdate);
    void showLargeGravatar();
    void showPersonalInfo();
    //获取自己的VCard
    void getVCard(boolean isForce);
}
