package com.qunar.im.ui.presenter;

import com.qunar.im.base.module.IMMessage;

/**
 * Created by huayu.chen on 2016/6/23.
 */
public interface IHandleVoiceMsgPresenter {
    void start(long time,String convId);
    void shutdown();
    IMMessage next();
}
