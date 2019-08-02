package com.qunar.im.base.protocol;

import com.qunar.im.base.jsonbean.AdvertiserBean;

/**
 * Created by xinbo.wang on 2016/6/6.
 */
public interface AdCallback {
    void onFailure();
    void onCompleted(AdvertiserBean advertiserBean);
}
