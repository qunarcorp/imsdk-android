package com.qunar.im.ui.presenter;

import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.ui.presenter.views.IDailyMindMainView;
import com.qunar.im.ui.presenter.views.IDailyMindSubEditView;
import com.qunar.im.ui.presenter.views.IDailyMindSubView;

import java.util.List;
import java.util.Map;

/**
 * Created by lihaibin.li on 2017/8/22.
 */

public interface IDailyMindPresenter {
    void setView(IDailyMindMainView iDailyMindMainView);
    void setView(IDailyMindSubView iDailyMindSubView);
    void setView(IDailyMindSubEditView iDailyMindSubEditView);
    void dropBoxMainTable();
    void operateDailyMindFromHttp(String method, Map<String, String> requestParams);
    void operateDailyMindFromHttp(boolean isFromLogin, String method, Map<String, String> requestParams);
    void operateDailyMindFromHttp(String method, DailyMindSub dailyMindSub);
    List<DailyMindMain> getDailyMainFromDB(int type, int offset, int number);
    List<DailyMindSub> getDailySubFromDB(int offset, int number, int qid);
    DailyMindSub getDailySubByTitleFromDB(String title, String qid);
    DailyMindMain getDailyMainByTitleFromDB();
}
