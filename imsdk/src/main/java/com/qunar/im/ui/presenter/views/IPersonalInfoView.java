package com.qunar.im.ui.presenter.views;

import android.content.Context;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by xinbo.wang on 2015/3/24.
 */
public interface IPersonalInfoView {
    void setNickName(String nick);
    void setDeptName(String deptName);
    void setJid(String jid);
    SimpleDraweeView getImagetView();
    String getJid();
    void setUpdateResult(boolean result);
    void setLargeGravatarInfo(String url, String thumbPath);
    Context getContext();
}
