package com.qunar.im.ui.presenter.factory;

import com.qunar.im.ui.presenter.IFriendsManagePresenter;
import com.qunar.im.ui.presenter.impl.FriendsManagePresenter;
import com.qunar.im.ui.presenter.impl.QChatGetOrganizationPresenter;
import com.qunar.im.common.CommonConfig;

/**
 * Created by saber on 15-11-30.
 */
public class FriendsManagerFactory {
    public static IFriendsManagePresenter getFriendManagerPresenter()
    {
        if(CommonConfig.isQtalk){

            return new FriendsManagePresenter();
        }else{
            return new QChatGetOrganizationPresenter();
        }
    }
}
