package com.qunar.im.ui.presenter.factory;

import com.qunar.im.ui.presenter.IPersonalInfoPresenter;
import com.qunar.im.ui.presenter.impl.PersonalInfoPresenter;
import com.qunar.im.ui.presenter.impl.QchatPersonalInfoPresenter;
import com.qunar.im.common.CommonConfig;

/**
 * Created by saber on 15-12-11.
 */
public class PersonalInfoFactory {
    public static IPersonalInfoPresenter getPersonalPresenter()
    {
        if(CommonConfig.isQtalk)
        {
            return new PersonalInfoPresenter();
        }
        else {
            return new QchatPersonalInfoPresenter();
        }
    }
}
