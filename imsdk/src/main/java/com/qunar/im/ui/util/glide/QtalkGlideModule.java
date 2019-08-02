package com.qunar.im.ui.util.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.qunar.im.base.util.graphics.MyDiskCache;

import java.io.File;

/**
 * Created by hubo.hu on 2017/10/9.
 * glide 自定义磁盘缓存
 */

public class QtalkGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        final String dir = "/files/glide";
        int diskCacheSize = 1024 * 1024 * 1024;//最多可以缓存多少字节的数据
//        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, dir, diskCacheSize));
        builder.setDiskCache(new DiskLruCacheFactory(new DiskLruCacheFactory.CacheDirectoryGetter() {
            @Override
            public File getCacheDirectory() {
                return new File(MyDiskCache.getDirectory().getAbsolutePath() + "/files/glide");
            }
        }, diskCacheSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }

//    @Override
//    public void registerComponents(Context context, Glide glide, Registry registry) {
//
//    }
}
