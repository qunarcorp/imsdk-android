package com.qunar.im.ui.presenter;

import com.qunar.im.ui.presenter.views.IOrganizationView;

/**
 * Created by lihaibin.li on 2018/1/4.
 */

public interface IOrganizationPresenter {
    void getOrganizations();
    void setView(IOrganizationView view);
}
