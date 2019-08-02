package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.jsonbean.DailyMindSub;

/**
 * Created by lihaibin.li on 2017/8/23.
 */

public interface IDailyMindSubView {
    void showErrMsg(String error);

    void setCloudSub();
    void addDailySub(DailyMindSub dailyMindSub);
}
