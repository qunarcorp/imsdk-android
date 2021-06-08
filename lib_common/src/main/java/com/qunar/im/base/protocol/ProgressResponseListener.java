package com.qunar.im.base.protocol;

/**
 * Created by saber on 15-12-24.
 */
public interface ProgressResponseListener {
    void onResponseProgress(long bytesRead, long contentLength, boolean done);
}
