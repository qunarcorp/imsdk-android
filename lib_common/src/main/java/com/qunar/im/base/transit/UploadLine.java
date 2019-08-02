package com.qunar.im.base.transit;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by xinbo.wang on 2015/3/10.
 */
public class UploadLine extends PriorityBlockingQueue<UploadImageRequest> {
    public UploadLine(int capacity) {
        super(capacity);
    }
}
