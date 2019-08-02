package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IChatmemberManageView;

/**
 * Created by xinbo.wang on 2015/3/24.
 */
public interface IChatMemberPresenter {
    void kickUser();
    void revokeUser();
    void grantVoice();
    void loadMembers();
    void setChatmemberManageView(IChatmemberManageView view);
}
