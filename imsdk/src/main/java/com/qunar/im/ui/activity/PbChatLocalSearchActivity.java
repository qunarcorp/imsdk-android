package com.qunar.im.ui.activity;

import android.content.Intent;

import com.qunar.im.base.module.IMMessage;

import java.util.List;

/**
 * 本地搜索 跳转的会话页
 */
public class PbChatLocalSearchActivity extends PbChatActivity {

    public static final String KEY_START_TIME = "start_time";

    private long startTime;

    @Override
    protected void initHistoryMsg() {
        chatingPresenter.reloadMessagesFromTime(startTime);
    }

    @Override
    protected void injectExtras(Intent intent) {
        super.injectExtras(intent);
        startTime = intent.getLongExtra(KEY_START_TIME,0);
    }

    @Override
    public void setHistoryMessage(List<IMMessage> historyMessage, int unread) {
        super.setHistoryMessage(historyMessage, unread);
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chat_region.getRefreshableView().setSelection(0);
            }
        },500);
    }
}
