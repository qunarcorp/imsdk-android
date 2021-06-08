package com.qunar.im.base.util;


import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.common.CommonConfig;

import java.io.File;
import java.util.UUID;

/**
 * Created by xinbo.wang on 2015/4/13.
 */
public class PhoneInfoUtils {
    public synchronized static String getUniqueID() {
        File  deviceUnique =new File(CommonConfig.globalContext.getFilesDir(),
                CommonConfig.currentPlat
                        + "_" + IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid)
                        + "_" + IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserdomain)
                        + "_unique");
        Logger.i("push token filename = " + deviceUnique.getName());
       if(!deviceUnique.exists()){
           //生成uuid
           String uuid = BinaryUtil.MD5(UUID.randomUUID().toString());
           FileUtils.writeToFile(uuid, deviceUnique, true);
       }
       String token = FileUtils.readFirstLine(deviceUnique,CommonConfig.globalContext);
       Logger.i("get push token = " + token);
       return token;
    }

//     File  pubkeyFile =new File(CommonConfig.globalContext.getFilesDir(), "_pubkey");

    public synchronized static void delUniqueID() {
        if(!TextUtils.isEmpty(getUniqueID())){
            File  deviceUnique =new File(CommonConfig.globalContext.getFilesDir(),
                    CommonConfig.currentPlat
                            + "_" + IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserid)
                            + "_" + IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastuserdomain)
                            + "_unique");
            Logger.i("del push token filename = " + deviceUnique.getName());
            if(deviceUnique.exists()){
               deviceUnique.delete();
            }
        }
    }


}