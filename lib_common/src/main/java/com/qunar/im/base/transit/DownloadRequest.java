package com.qunar.im.base.transit;


import com.qunar.im.base.protocol.ProgressResponseListener;

/**
 * Created by xinbo.wang on 2015/3/9.
 */
public class DownloadRequest implements Comparable<DownloadRequest> {
    public String url;
    public String savePath;
    public IDownloadRequestComplete requestComplete;
    public long level;
    public boolean source=true;
    public ProgressResponseListener progressListener;

    @Override
    public int compareTo(DownloadRequest another) {
        return this.level < another.level ? -1:
                (this.level == another.level ?0:1);
    }
}
