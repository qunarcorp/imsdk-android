package com.qunar.im.base.transit;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by xinbo.wang on 2015/3/10.
 */
public class DownloadLine extends PriorityBlockingQueue<DownloadRequest> {
    public DownloadLine(int capacity) {
        super(capacity);
    }

}
