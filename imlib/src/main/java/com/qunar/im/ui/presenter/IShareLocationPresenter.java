package com.qunar.im.ui.presenter;

import com.qunar.im.base.jsonbean.ShareLocationData;
import com.qunar.im.ui.presenter.views.IShareLocationView;

/**
 * Created by zhaokai on 16-2-19.
 */
public interface IShareLocationPresenter {
    void joinShareLocation();
    void quitShareLocation();
    void sendLocationData(ShareLocationData data);
    void setShareLocationView(IShareLocationView view);
}