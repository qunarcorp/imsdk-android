package com.qunar.im.base.common;

import com.qunar.im.base.transit.DownloadLine;
import com.qunar.im.base.transit.DownloadManager;
import com.qunar.im.base.transit.DownloadRequest;

import java.util.concurrent.ExecutorService;

/**
 * Created by xinbo.wang on 2015/5/26.
 */
public class CommonDownloader {
    private static final int DOWNLOAD_LINE_MAX = 100;
    DownloadLine downloadLine;
    ExecutorService downExec;
    int adjustmentPeriod = 100;

    private CommonDownloader()
    {
        int maxWorkCount =  Runtime.getRuntime().availableProcessors();
        downExec  = (ExecutorService) BackgroundExecutor.DEFAULT_EXECUTOR;
        downloadLine = new DownloadLine(DOWNLOAD_LINE_MAX);
        downExec.execute(new DownloadManager(downExec,downloadLine,adjustmentPeriod,maxWorkCount-1));
    }

    private final static CommonDownloader insatnce = new CommonDownloader();
    public static CommonDownloader getInsatnce()
    {
        return insatnce;
    }

    public void setDownloadRequest(DownloadRequest request)
    {
        if(downloadLine.contains(request))
        {
            return;
        }
        if(downloadLine.size() == DOWNLOAD_LINE_MAX)
        {
            DownloadRequest preRequest = downloadLine.poll();
            preRequest.requestComplete.onRequestComplete(null);
        }
        if(!downloadLine.offer(request))
        {
            request.requestComplete.onRequestComplete(null);
        }
    }
}
