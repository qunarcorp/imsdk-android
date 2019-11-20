package com.qunar.im.ui.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.qunar.im.base.common.DailyMindConstants;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.ui.presenter.IDailyMindPresenter;
import com.qunar.im.ui.presenter.impl.DailyMindPresenter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 拉取密码箱 和 会话密码 service
 * Created by lihaibin.li on 2017/10/12.
 */

public class PullPasswordBoxService extends IntentService {
    public static void runPullPasswordBoxService(Context context) {
        Intent intent = new Intent(context, PullPasswordBoxService.class);
        context.startService(intent);
    }

    private IDailyMindPresenter passwordBoxPresenter;

    public PullPasswordBoxService() {
        super(PullPasswordBoxService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        passwordBoxPresenter = new DailyMindPresenter();
        DailyMindMain pbm = passwordBoxPresenter.getDailyMainByTitleFromDB();
        if (pbm == null) {
            getCloudPasswordBoxMain();
        } else getCloudPasswordBoxSub(String.valueOf(pbm.qid));
    }

    private void getCloudPasswordBoxMain() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("version", "0");
        params.put("type", String.valueOf(DailyMindConstants.CHATPASSWORD));
        passwordBoxPresenter.operateDailyMindFromHttp(true,DailyMindConstants.GET_CLOUD_MAIN, params);
    }

    private void getCloudPasswordBoxSub(String qid) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("qid", qid);
        params.put("version", "0");
        params.put("type", String.valueOf(DailyMindConstants.PASSOWRD));
        passwordBoxPresenter.operateDailyMindFromHttp(true,DailyMindConstants.GET_CLOUD_SUB, params);
    }
}
