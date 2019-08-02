package com.qunar.im.base.transit;

import com.qunar.im.base.util.LogUtil;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by xinbo.wang on 2015/3/10.
 */
public class UploadManager implements Runnable {
    private ExecutorService exec;
    private UploadLine uploadImageRequests;
    private PriorityQueue<Uploader> workingUploader = new PriorityQueue<Uploader>();
    private Queue<Uploader> uploadersDoingOtherThings = new LinkedList<Uploader>();
    private int ajustmentPeriod;
    private int worker_max;

    public UploadManager(ExecutorService e,UploadLine ul,int ajustmentPeriod,int max)
    {
        this.exec = e;
        this.uploadImageRequests = ul;
        this.ajustmentPeriod = ajustmentPeriod;
        this.worker_max = max;
        Uploader uploader = new Uploader(this.uploadImageRequests);
        exec.execute(uploader);
        workingUploader.add(uploader);
    }

    public void adjuestUploaderNumber()
    {
        if(uploadImageRequests.size()/workingUploader.size() > 2)
        {
            if(uploadersDoingOtherThings.size() > 0)
            {
                Uploader uploader = uploadersDoingOtherThings.remove();
                uploader.serveRequestLine();
                workingUploader.offer(uploader);
                return;
            }
            if(workingUploader.size() >= this.worker_max)
            {
                return;
            }
            Uploader uploader = new Uploader(uploadImageRequests);
            exec.execute(uploader);
            workingUploader.add(uploader);
            return;
        }
        if(workingUploader.size() > 1 && uploadImageRequests.size()/workingUploader.size() < 2)
            reassignOneUploader();
        if(uploadImageRequests.size() == 0)
        {
            while (workingUploader.size() > 1)
                reassignOneUploader();
        }
    }

    private void reassignOneUploader(){
        Uploader uploader = workingUploader.poll();
        uploader.doSomethingElse();
        uploadersDoingOtherThings.offer(uploader);
    }

    @Override
    public void run() {

        while (!Thread.interrupted()) {
            try {
                TimeUnit.MILLISECONDS.sleep(ajustmentPeriod);
                adjuestUploaderNumber();
            } catch (InterruptedException e) {
                LogUtil.e("UploadManager","error",e);
            }
        }

    }
}
