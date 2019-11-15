package com.qunar.im.ui.util;

import android.content.Context;
import android.net.Uri;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;

import static com.qunar.im.utils.QtalkStringUtils.findRealUrl;

public class HttpProxyUtil {
    private static HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        return proxy == null ? (proxy = newProxy(context)) : proxy;
    }



    private static HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .fileNameGenerator(new FileNameGenerator() {
                    @Override
                    public String generate(String url) {
                        return  findRealUrl(url);
                    }
                })
                .build();
    }


//    public static class MyFileNameGenerator implements FileNameGenerator {
//
//        // Urls contain mutable parts (parameter 'sessionToken') and stable video's id (parameter 'videoId').
//        // e. g. http://example.com?videoId=abcqaz&sessionToken=xyz987
//        public String generate(String url) {
//            String name = findRealUrl(url);
//
//            return name;
//        }
//    }
}
