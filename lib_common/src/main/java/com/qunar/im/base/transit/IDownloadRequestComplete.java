package com.qunar.im.base.transit;

import com.qunar.im.base.jsonbean.DownloadImageResult;

/**
 * Created by xinbo.wang on 2015/3/13.
 */
public interface IDownloadRequestComplete {
    public void onRequestComplete(DownloadImageResult result);
}