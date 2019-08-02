package com.qunar.im.base.transit;

import com.qunar.im.base.jsonbean.UploadImageResult;

/**
 * Created by xinbo.wang on 2015/3/10.
 */
public interface IUploadRequestComplete {
     void onRequestComplete(String id, UploadImageResult result);
     void onError(String msg);
}
