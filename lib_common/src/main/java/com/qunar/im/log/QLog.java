package com.qunar.im.log;

import com.qunar.im.base.jsonbean.LogInfo;

/**
 * Created by froyomu on 2019/1/17
 * <p>
 * Describe:
 */
public class QLog {

    public static LogInfo build(){
        LogInfo logInfo = new LogInfo();
        return logInfo;
    }

    public static LogInfo build(String type){
        LogInfo logInfo = new LogInfo(type);
        return logInfo;
    }

    public static LogInfo build(String type,String subType){
        LogInfo logInfo = new LogInfo(type,subType);
        return logInfo;
    }
}
