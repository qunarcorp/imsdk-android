package com.qunar.im.ui.presenter.impl;

import android.content.Context;
import android.os.Environment;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.ui.presenter.ISystemPresenter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xinbo.wang on 2015/3/5.
 */
//用于加载应用当前的基本信息,保存crash Log等
public class SystemPresenter implements ISystemPresenter {
    private static final java.lang.String TAG = "SystemPresenter";

    protected String PATH;
    protected String FILE_NAME;
    protected String FILE_NAME_SUFFIX;
    protected Timer chkTimer;
    protected TimerTask chkTask;

    public SystemPresenter() {
        PATH = Environment.getExternalStorageDirectory().getPath() + "/qtalk/logcat/";
        FILE_NAME = "logcat_";
        FILE_NAME_SUFFIX = ".txt";
    }

    @Override
    public void loadPreference(Context context, boolean isWrite) {
    }
    @Override
    public void changeProcess2Failed() {
    }

    @Override
    public void checkSendingLine() {
        if (chkTimer != null) chkTimer.cancel();
        if (chkTimer != null) chkTask.cancel();
        chkTask = new TimerTask() {
            @Override
            public void run() {
                if (InternDatas.sendingLine.size() == 0) return;
                for (String key : InternDatas.sendingLine.keySet()) {
                    IMMessage msg = InternDatas.sendingLine.get(key);
                    long duration = System.currentTimeMillis() + CommonConfig.divideTime - msg.getTime().getTime();
                    if (duration > 10000) {
                        msg.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                        InternDatas.sendingLine.remove(key);
                    }
                }
            }
        };
        chkTimer = new Timer();
        chkTimer.schedule(chkTask, 6000, 6000);
    }

    @Override
    public boolean checkUnique() {
        String uuid;
        boolean result = false;
        if (CurrentPreference.getInstance().isTurnOnPsuh()) {
            uuid = PhoneInfoUtils.getUniqueID();
        } else {
            result = true;
            uuid = "";
        }
        return result;
    }

    @Override
    public void getMyCapability() {

    }

    @Override
    public void getMyConfig() {

    }

    @Override
    public void checkTemplate() {
    }

}
