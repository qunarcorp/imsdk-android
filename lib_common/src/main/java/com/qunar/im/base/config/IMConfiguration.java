package com.qunar.im.base.config;

/**
 * Created by saber on 15-7-27.
 */
public interface IMConfiguration {
    boolean isDebug();
    String getHost();
    String getDomain();
    String getResource();
    int getPort();
    int TIMEOUT();
    void setHttpsKey(String key);
    String getHttpUrl();
    int getHttpPort();
    String getSmsSendUrl();
    String getSmsVerifyUrl();
    String getOpsUrl();
}
