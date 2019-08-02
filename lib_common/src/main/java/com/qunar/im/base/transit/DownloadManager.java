package com.qunar.im.base.transit;

import com.qunar.im.base.util.LogUtil;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by xinbo.wang on 2015/3/13.
 */
public class DownloadManager implements Runnable {
    private ExecutorService exec;
    private DownloadLine dowanImageRequests;
    private PriorityQueue<Downloader> workingDownloader = new PriorityQueue<Downloader>();
    private Queue<Downloader> downloaderDoingOtherThings = new LinkedList<Downloader>();
    private int ajustmentPeriod;
    private int worker_max;

    public DownloadManager(ExecutorService e,DownloadLine dl,int ajustmentPeriod,int max)
    {
        this.exec = e;
        this.dowanImageRequests = dl;
        this.ajustmentPeriod = ajustmentPeriod;
        this.worker_max = max;
        Downloader downloader = new Downloader(this.dowanImageRequests);
        exec.execute(downloader);
        workingDownloader.add(downloader);
    }

    public void adjuestUploaderNumber()
    {
        if(dowanImageRequests.size()/workingDownloader.size() > 2)
        {
            if(downloaderDoingOtherThings.size() > 0)
            {
                Downloader downloader = downloaderDoingOtherThings.remove();
                downloader.serveRequestLine();
                workingDownloader.offer(downloader);
                return;
            }
            if(workingDownloader.size() >= this.worker_max)
            {
                return;
            }
            if(!exec.isShutdown()) {
                Downloader downloader = new Downloader(dowanImageRequests);
                exec.execute(downloader);
                workingDownloader.add(downloader);
            }
            return;
        }
        if(workingDownloader.size() > 1 && dowanImageRequests.size()/workingDownloader.size() < 2)
            reassignOneUploader();
        if(dowanImageRequests.size() == 0)
        {
            while (workingDownloader.size() > 1)
                reassignOneUploader();
        }
    }

    private void reassignOneUploader(){
        Downloader d = workingDownloader.poll();
        d.doSomethingElse();
        downloaderDoingOtherThings.offer(d);
    }

    @Override
    public void run() {

        while (!Thread.interrupted()) {
            try {
                TimeUnit.MILLISECONDS.sleep(ajustmentPeriod);
                adjuestUploaderNumber();
            } catch (InterruptedException ex) {
                LogUtil.e("DownloadManager","error",ex);
            }
        }

    }
}
