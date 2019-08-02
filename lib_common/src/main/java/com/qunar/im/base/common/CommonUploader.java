package com.qunar.im.base.common;

import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.transit.UploadLine;
import com.qunar.im.base.transit.UploadManager;

import java.util.concurrent.ExecutorService;

/**
 * Created by xinbo.wang on 2015/5/26.
 */
public class CommonUploader {
    private static final int UPLOAD_LINE_MAX = 10;
    ExecutorService exec;
    UploadLine uploadImageRequests;

    int adjustmentPeriod = 500;

    private CommonUploader()
    {
        int maxWorkCount =  Runtime.getRuntime().availableProcessors();
        exec = (ExecutorService) BackgroundExecutor.DEFAULT_EXECUTOR;
        uploadImageRequests = new UploadLine(UPLOAD_LINE_MAX);
        exec.execute(new UploadManager(exec,uploadImageRequests,adjustmentPeriod,maxWorkCount-1));
    }

    private final static CommonUploader instance = new CommonUploader();

    public static CommonUploader getInstance()
    {
        return instance;
    }

    public void setUploadImageRequest(UploadImageRequest imageRequest)
    {
        if(uploadImageRequests.contains(imageRequest)){
            return;
        }
        if(!uploadImageRequests.offer(imageRequest)){
            imageRequest.requestComplete.onRequestComplete("id",null);
        }
    }
}
