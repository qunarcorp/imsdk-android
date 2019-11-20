package com.qunar.im.ui.presenter.impl;

import android.text.TextUtils;

import com.qunar.im.base.common.CommonDownloader;
import com.qunar.im.base.jsonbean.AdvertiserBean;
import com.qunar.im.base.jsonbean.DownloadImageResult;
import com.qunar.im.base.protocol.AdCallback;
import com.qunar.im.base.protocol.AdvertiserApi;
import com.qunar.im.base.protocol.ProgressResponseListener;
import com.qunar.im.base.transit.DownloadRequest;
import com.qunar.im.base.transit.IDownloadRequestComplete;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.common.CurrentPreference;

import java.io.File;

/**
 * Created by xinbo.wang on 2016-09-23.
 */
public class AdPresenter {
    public static final String AD_DIR_NAME = "Advertise";

    public String showAd()
    {
        String json = AdvertiserApi.loadNavConfig(CommonConfig.globalContext);
        String version="0";
        if(!TextUtils.isEmpty(json))
        {
            AdvertiserBean bean = JsonUtils.getGson().fromJson(json,AdvertiserBean.class);
            version = String.valueOf(bean.version);
            long lastShowadTime = DataUtils.getInstance(CommonConfig.globalContext).getPreferences("lastShowadTime", 0L);
            if(System.currentTimeMillis() - lastShowadTime < bean.interval * 1000){//广告展示间隔
                return "";
            }
        }
        updateAd(version);
        return json;
    }

    public void updateAd(String version)
    {
        AdvertiserApi.getAdvertiser(version, CurrentPreference.getInstance().getUserid(),new AdCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onCompleted(AdvertiserBean advertiserBean) {
                if(advertiserBean==null|| ListUtil.isEmpty(advertiserBean.adlist)) return;
                File adDir = new File(CommonConfig.globalContext.getFilesDir(),AD_DIR_NAME);
                if(adDir.exists()){
//                    adDir.delete();
                    FileUtils.removeDir(adDir);
                }
                for(AdvertiserBean.AdContent content:advertiserBean.adlist)
                {
                    File file;
                    if(content.adtype==2)
                    {
                        file = MyDiskCache.getFile(content.url);
                    }
                    else if(content.adtype == 3) {
                        String name = BinaryUtil.MD5(content.url) + ".cnt";
                        file = new File(CommonConfig.globalContext.getFilesDir(), name);
                        downloadVideo(file.getPath(),content.url);
                    }
                    else {
                        continue;
                    }
//                    HttpUtils.getDownloadDrable(content.url,file.getPath(),null);
                }
            }
        });
    }



    private void downloadVideo(final String savePath,String url)
    {
        final DownloadRequest request = new DownloadRequest();
        request.savePath = savePath;
        request.url = url;
        request.requestComplete = new IDownloadRequestComplete() {
            @Override
            public void onRequestComplete(DownloadImageResult result) {
//                Message msg = mHandler.obtainMessage();
//                msg.what = DOWNLOAD_FINISH;
//                Bundle b = new Bundle();
//                b.putString(URL_KEY,savePath);
//                msg.setData(b);
//                mHandler.sendMessage(msg);
            }
        };
        request.progressListener = new ProgressResponseListener() {
            @Override
            public void onResponseProgress(long bytewriten, long length, boolean complete) {
//                int current = (int) (bytewriten*100/length);
//                Message msg = mHandler.obtainMessage();
//                msg.what = UPDATE_PROGRESS;
//                Bundle b = new Bundle();
//                b.putInt(PROGRESS,current);
//                msg.setData(b);
//                mHandler.sendMessage(msg);
            }
        };
        CommonDownloader.getInsatnce().setDownloadRequest(request);
    }
}
