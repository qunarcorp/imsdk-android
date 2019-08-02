package com.qunar.im.base.transit;

import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.HttpUtils;
import com.qunar.im.base.util.LogUtil;

/**
 * Created by xinbo.wang on 2015/3/10.
 */
public class Downloader  implements Runnable,Comparable<Downloader> {
    private static final String TAG = Downloader.class.getSimpleName();
    private static int counter = 0;
    private final int id = counter++;

    private int downloadRequestServed = 0;
    private DownloadLine downloadRequests;
    private boolean servingRequestLine = true;

    public Downloader(DownloadLine ul)
    {
        this.downloadRequests = ul;
    }

    @Override
    public void run() {
        while (!Thread.interrupted())
        {
            DownloadRequest request = null;
            try {
                request = downloadRequests.take();
                //构建URL
                StringBuilder url =new StringBuilder(request.url);
                if(request.source) {
                    Protocol.addBasicParamsOnHead(url);
                }
                HttpUtils.getDownloadDrable(url.toString(),request.savePath,request);
                synchronized (this)
                {
                    downloadRequestServed ++;
                    while (!servingRequestLine)
                    {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                LogUtil.e(TAG,"error",e);
            }
            catch (Exception ex)
            {
                LogUtil.e(TAG,"error",ex);
                if(request!=null)
                {
                    request.requestComplete.onRequestComplete(null);
                }
            }
        }
    }

    public synchronized void doSomethingElse()
    {
        downloadRequestServed = 0;
        servingRequestLine = false;
    }

    public synchronized void serveRequestLine()
    {
        servingRequestLine = true;
        notifyAll();
    }

    @Override
    public synchronized int compareTo(Downloader downloader)
    {
        return downloadRequestServed < downloader.downloadRequestServed ? -1:
                (downloadRequestServed == downloader.downloadRequestServed ?0:1);
    }
}
