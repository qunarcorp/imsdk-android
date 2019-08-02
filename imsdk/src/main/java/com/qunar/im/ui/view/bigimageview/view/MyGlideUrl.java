package com.qunar.im.ui.view.bigimageview.view;

import com.bumptech.glide.load.model.GlideUrl;

import java.util.HashMap;
import java.util.Map;

import static com.qunar.im.utils.QtalkStringUtils.findRealUrl;

public class MyGlideUrl extends GlideUrl {

    private String mUrl;

    public MyGlideUrl(String url) {
        super(url);
        mUrl = url;
    }

    @Override
    public String getCacheKey() {
        return findRealUrl(mUrl);
    }



}