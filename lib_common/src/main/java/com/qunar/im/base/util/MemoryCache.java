package com.qunar.im.base.util;

import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.rastermill.Cacheable;
import android.util.LruCache;

import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.util.graphics.BitmapHelper;

import java.lang.ref.WeakReference;

/**
 * Created by xinbo.wang on 2015/3/24.
 */
public  class MemoryCache {
    public static void emptyCache()
    {
        mMemoryCache.evictAll();
    }
    private static int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private static int mCacheSize = maxMemory / 8;

    public static int getCurrentSize()
    {
        return mMemoryCache.size();
    }

    public static int getMaxMemory()
    {
        return mMemoryCache.size();
    }

    private static LruCache<String, Parcelable> mMemoryCache = new LruCache<String, Parcelable>(mCacheSize) {
        @Override
        protected int sizeOf(String key, Parcelable value) {
            if(value instanceof Bitmap) {
                return ((Bitmap)value).getByteCount();
            }
            else if(value instanceof Cacheable)
            {
                return ((Cacheable) value).getByteCount();
            }
            else {
                return 128;
            }
        }
    };

    public static void addObjToMemoryCache(String key, Parcelable obj) {
        if (obj != null) {
            mMemoryCache.put(key, obj);
        }
    }

    /**
     * 从内存缓存中获取一个Object
     * @param key
     * @return
     */
    public static Parcelable getMemoryCache(String key) {
        if(key == null)
            return null;
        return mMemoryCache.get(key);
    }
    public static void removeFromMemCache(String key){
        mMemoryCache.remove(key);
    }


    static WeakReference<Bitmap> defaultGravatar = new WeakReference<Bitmap>(null);
    public static Bitmap getDefaultGravatar(int rid)
    {
        if(defaultGravatar.get() == null)
        {
            Bitmap bitmap = BitmapHelper.decodeResource(QunarIMApp.getContext().getResources(), rid);
            defaultGravatar = new WeakReference<Bitmap>(bitmap);
        }
        return defaultGravatar.get();
    }
}
