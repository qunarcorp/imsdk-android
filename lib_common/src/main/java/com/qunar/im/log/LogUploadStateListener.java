package com.qunar.im.log;

import com.qunar.im.base.jsonbean.LogInfo;

import java.util.List;

/**
 * Created by froyomu on 2019/1/17
 * <p>
 * Describe:
 */
public interface LogUploadStateListener {
    void onSuccess(List<LogInfo> infos);
    void onFail(String msg);
}
