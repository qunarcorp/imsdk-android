package com.qunar.im.base.jsonbean;

/**
 * Created by xinbo.wang on 2015/3/13.
 */
public class DownloadImageResult extends BaseResult {
    private boolean downloadComplete;

    public boolean isDownloadComplete() {
        return downloadComplete;
    }

    public void setDownloadComplete(boolean downloadComplete) {
        this.downloadComplete = downloadComplete;
    }
}
