package com.qunar.im.log;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.core.services.QtalkNavicationService;

import java.util.List;

/**
 * Created by froyomu on 2019/1/17
 * <p>
 * Describe:日志上报thread
 */

public class LogService implements LogUploadStateListener{
    private static volatile LogService instance;

    private static final int UPLOAD = 0x01;
    private static final int SAVE = 0x02;

    private static final int MAX_FAIL_COUNTS = 5;//最多失败次数

    private static final int UNIT_MAX_UPLOAD_LOG_COUNT = 20;//每次上报最大日志条数
    private static final int DELAY_TIME = 10*1000;

    private int failCount;

    private HandlerThread handlerThread = new HandlerThread("log_upload");
    private Handler handler;

    private List<LogInfo> allLogs;

    @Override
    public void onSuccess(List<LogInfo> infos) {
        if(!ListUtil.isEmpty(infos)){
            long start = infos.get(0).getReportTime();
            long end = infos.get(infos.size() - 1).getReportTime();
            boolean result = LogDatabaseManager.getInstance().clearLogs(start,end);
            if(result){
                infos.clear();
            }
            if(!ListUtil.isEmpty(allLogs)){
                upload(0);
            }
        }
    }

    @Override
    public void onFail(String msg) {
        if(failCount > MAX_FAIL_COUNTS){
            return;
        }
        failCount++;
        upload(failCount * DELAY_TIME);
    }

    public static LogService getInstance(){
        synchronized (LogService.class){
            if(instance == null){
                instance = new LogService();
            }
        }
        return instance;
    }

    public LogService(){
        start();
    }

    private void start(){
        handlerThread.start();
        if(handler == null){
            handler = new Handler(handlerThread.getLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(!TextUtils.isEmpty(QtalkNavicationService.getInstance().getUploadLog())){
                        deawithlLog(msg);
                    }
                }
            };
        }
        queryLogsToUpload();
    }

    private void deawithlLog(Message msg){
        switch (msg.what){
            case UPLOAD:
                LogUploadApi.upload(spiltLogs(),LogService.this);
                break;
            case SAVE:
                LogInfo log = (LogInfo) msg.obj;
                LogDatabaseManager.getInstance().insertLog(log);
                break;
        }
    }

    private List<LogInfo> spiltLogs(){
        List<LogInfo> logs = null;
        if(!ListUtil.isEmpty(allLogs)){
            int size = allLogs.size();
            if(size > UNIT_MAX_UPLOAD_LOG_COUNT){
                logs = allLogs.subList(0,UNIT_MAX_UPLOAD_LOG_COUNT);
            }else {
                logs = allLogs;
            }
        }
        return logs;
    }

    private void queryLogsToUpload(){
        long currentTime = System.currentTimeMillis();
        allLogs = LogDatabaseManager.getInstance().queryAllLogs(currentTime);
        upload(DELAY_TIME);
    }

    private void upload(long time){
        handler.sendEmptyMessageDelayed(UPLOAD,time);
    }

    public void saveLog(Object log){
        Message message = Message.obtain();
        message.what = SAVE;
        message.obj = log;
        handler.sendMessage(message);
    }

    public void quit(){
        if(handlerThread != null){
            handlerThread.quit();
        }
    }

}
