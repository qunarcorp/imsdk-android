package com.qunar.im.utils;

import android.content.Context;
import android.provider.Settings;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.protobuf.utils.StringUtils;

import java.util.UUID;

/**
 * Created by may on 2017/6/29.
 */

public class DeviceInfoManager {

    private String androidId;
    private static DeviceInfoManager INSTANCE = new DeviceInfoManager();
    private String userName;
    private String deviceId;

    public static DeviceInfoManager getInstance() {
        return INSTANCE;
    }

    protected DeviceInfoManager() {
    }

    public String getId(Context context) {
        androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeviceId(Context context) {
        String id;
        if (StringUtils.isNotEmpty(getId(context)))
            id = androidId;
        else if (StringUtils.isNotEmpty(deviceId))
            id = deviceId;
        else {
            id = IMUserDefaults.getStandardUserDefaults().getStringValue(GlobalConfigManager.getGlobalContext(), "AndroidDeviceId");
        }

        if (StringUtils.isEmpty(id)) {
            id = UUID.randomUUID().toString().replace("-", "");
            IMUserDefaults.getStandardUserDefaults().newEditor(GlobalConfigManager.getGlobalContext())
                    .putObject("AndroidDeviceId", id)
                    .synchronize();
        }
        return id;
    }
}
