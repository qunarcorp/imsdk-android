package com.qunar.im.base.protocol;

/**
 * Created by saber on 15-12-24.
 */
public interface ProgressRequestListener {
    void onRequestProgress(long bytesWritten, long contentLength, boolean done);
}
