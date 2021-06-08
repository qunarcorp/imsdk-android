package com.qunar.im.base.protocol;

import java.io.InputStream;

/**
 * Created by xinbo.wang on 2016/7/13.
 */
public interface HttpContinueDownloadCallback {
    void onComplete(InputStream response,boolean supports);
    void onFailure(Exception e);
}
