package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.jsonbean.DailyMindMain;

/**
 * Created by lihaibin.li on 2017/8/22.
 */

public interface IDailyMindMainView {
    void setCloudMain();
    void addDailyMain(DailyMindMain dailyMindMain);
    void showErrMsg(String error);
}
