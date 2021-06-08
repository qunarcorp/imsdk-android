package com.qunar.im.base.common;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.QualityInfo;
import com.qunar.im.base.util.graphics.MyDiskCache;

import java.io.File;
import java.lang.ref.WeakReference;


/**
 * Created by xinbo.wang 2015/07/27
 */
public class QunarIMApp {
    public static Handler mainHandler = com.qunar.im.common.CommonConfig.mainhandler;
    private static WeakReference<Context> instance;
    private int version;
    private String versionName;
    private QunarIMApp(){}

    private static QunarIMApp qunarIMApp = new QunarIMApp();

    public  void RegisterContext(final Context context)
    {
        if(instance == null||instance.get()==null)
        {
            instance = new WeakReference<Context>(context);
        }

        ProgressiveJpegConfig config = new ProgressiveJpegConfig() {
            @Override
            public int getNextScanNumberToDecode(int i) {
                return 0;
            }

            @Override
            public QualityInfo getQualityInfo(int i) {
                return null;
            }
        };


        ImagePipelineConfig imagePipelineConfig =  ImagePipelineConfig.newBuilder(context)
                .setProgressiveJpegConfig(config)
                .setDownsampleEnabled(true)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setBitmapMemoryCacheParamsSupplier(new MyBitmapMemoryCacheParamsSupplier((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)))
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(context)
                        .setBaseDirectoryPath(new File(MyDiskCache.getDirectory().getAbsolutePath() + "/files/"))//FileUtils.getExternalFilesDir(context)
                        .setBaseDirectoryName("fresco")//qtalk_cache
                        .setVersion(100)
                        .setMaxCacheSize(10 * 1024 * ByteConstants.MB)
                        .setMaxCacheSizeOnLowDiskSpace(100 * ByteConstants.MB)
                        .setMaxCacheSizeOnVeryLowDiskSpace(20 * ByteConstants.MB)
                        .build())
                .setSmallImageDiskCacheConfig(DiskCacheConfig.newBuilder(context)
                        .setBaseDirectoryPath(new File(MyDiskCache.getDirectory().getAbsolutePath() + "/files/"))//FileUtils.getExternalFilesDir(context)
                        .setBaseDirectoryName("fresco_small_icon")
                        .setVersion(100)
                        .setMaxCacheSize(40 * 1024 * ByteConstants.MB)
                        .setMaxCacheSizeOnLowDiskSpace(100 * ByteConstants.MB)
                        .setMaxCacheSizeOnVeryLowDiskSpace(20 * ByteConstants.MB)
                        .build())
                .build();
        Fresco.initialize(context, imagePipelineConfig);
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public int getVersion(){return this.version;}

    public void setVersionName(String str)
    {
        this.versionName = str;
    }

    public String getVersionName(){return this.versionName;}

    public static Context getContext() {
        return instance.get();
    }

    public static QunarIMApp getQunarIMApp()
    {
        return qunarIMApp;
    }

    public class MyBitmapMemoryCacheParamsSupplier implements Supplier<MemoryCacheParams> {
        private static final int MAX_CACHE_ENTRIES = 56;
        private static final int MAX_CACHE_ASHM_ENTRIES = 128;
        private static final int MAX_CACHE_EVICTION_SIZE = 5;
        private static final int MAX_CACHE_EVICTION_ENTRIES = 5;
        private final ActivityManager mActivityManager;

        public MyBitmapMemoryCacheParamsSupplier(ActivityManager activityManager) {
            mActivityManager = activityManager;
        }

        @Override
        public MemoryCacheParams get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return new MemoryCacheParams(getMaxCacheSize(), MAX_CACHE_ENTRIES, MAX_CACHE_EVICTION_SIZE, MAX_CACHE_EVICTION_ENTRIES, 1);
            } else {
                return new MemoryCacheParams(
                        getMaxCacheSize(),
                        MAX_CACHE_ASHM_ENTRIES,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE);
            }
        }

        private int getMaxCacheSize() {
            final int maxMemory =
                    Math.min(mActivityManager.getMemoryClass() * ByteConstants.MB, Integer.MAX_VALUE);
            if (maxMemory < 32 * ByteConstants.MB) {
                return 4 * ByteConstants.MB;
            } else if (maxMemory < 64 * ByteConstants.MB) {
                return 6 * ByteConstants.MB;
            } else {
                return maxMemory / 5;
            }
        }
    }
}
