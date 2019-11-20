package com.qunar.im.cache;

import android.content.Context;
import android.content.SharedPreferences;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.base.util.IMUserDefaults;
import org.json.JSONObject;

/**
 * Created by may on 2017/6/28.
 */

public class IMUserCacheManager {
    private static volatile IMUserCacheManager defaultInstance;
    private static final String filenameDefault = "default.pref";
    private Context context;
    private SharedPreferences prefs;

    protected IMUserCacheManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(filenameDefault, 0);
    }

    public static IMUserCacheManager getInstance() {
        if (defaultInstance == null) {
            synchronized (IMUserCacheManager.class) {

                if (defaultInstance == null) {
                    defaultInstance = new IMUserCacheManager(GlobalConfigManager.getGlobalContext());
                }
            }
        }
        return defaultInstance;
    }

    public void putConfig(Context context, String key, String value) {
        IMUserDefaults.getStandardUserDefaults().newEditor(context)
                .putObject(key, value)
                .synchronize();
    }

    public void putConfig(Context context, String key, long value) {
        IMUserDefaults.getStandardUserDefaults().newEditor(context)
                .putObject(key, value)
                .synchronize();
    }

    public void putConfig(Context context, String key, int value) {
        IMUserDefaults.getStandardUserDefaults().newEditor(context)
                .putObject(key, value)
                .synchronize();
    }

    public void putConfig(final Context context, final String key, final JSONObject value) {
        IMUserDefaults.getStandardUserDefaults().newEditor(context)
                .putObject(key, value)
                .synchronize();
    }

    public void putConfig(String key, JSONObject jsonObject) {
        putConfig(context, key, jsonObject.toString());
    }

    public void putConfig(String key, String value) {
        putConfig(context, key, value);
    }

    public void putConfig(String key, long value) {
        putConfig(context, key, value);
    }

    public void putConfig(String key, int value) {
        putConfig(context, key, value);
    }

    public String getStringConfig(String key) {
        return IMUserDefaults.getStandardUserDefaults().getStringValue(context, key);
    }

    public int getIntConfig(String key) {
        return IMUserDefaults.getStandardUserDefaults().getIntValue(context, key, 0);
    }


    public JSONObject getJsonConfig(String key) {
        return null;
    }

    public long getLongConfig(String key) {
        return 0;
    }

    public double getDoubleConfig(String key) {
        return 0.0;
    }

    public void removeConfig(String key) {

    }


}
