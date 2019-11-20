package com.qunar.im.ui.util;


import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.entity.NavConfigInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihaibin.li on 2017/8/31.
 */

public class NavConfigUtils {

    public static void saveNavInfo(String name,String url){
        NavConfigInfo navConfigInfo = new NavConfigInfo();
        navConfigInfo.setName(name);
        navConfigInfo.setUrl(url);

        String configs = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_JSON, "");
        Logger.i("当前配置的所有导航:" + configs);

        List<NavConfigInfo> mConfigInfoList = JsonUtils.getGson().fromJson(configs, new TypeToken<List<NavConfigInfo>>() {}.getType());

        if(mConfigInfoList == null){
            mConfigInfoList = new ArrayList<>();
            mConfigInfoList.add(navConfigInfo);
        }else {
            if(!mConfigInfoList.contains(navConfigInfo)){
                mConfigInfoList.add(navConfigInfo);
            }
        }

        String configjson = JsonUtils.getGson().toJson(mConfigInfoList);
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(QtalkNavicationService.NAV_CONFIG_JSON, configjson);
        //保存当前使用的配置名
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, navConfigInfo.getName());
        //保存当前使用的URL
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, navConfigInfo.getUrl());
    }

    public static void saveNavInfo(NavConfigInfo navConfigInfo){
        if(navConfigInfo == null){
            return;
        }
        saveNavInfo(navConfigInfo.getName(),navConfigInfo.getUrl());
    }

    public static void saveAllNavJSONInfo(String configjson){
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(QtalkNavicationService.NAV_CONFIG_JSON, configjson);
    }

    /**
     * 保存当前对应导航名字的json
     * @param name
     * @param json
     */
    public static void saveCurrentNavJSONInfo(String name,String json){
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(name, json);
    }

    public static void removeNavJSONInfoByName(String name){
        DataUtils.getInstance(CommonConfig.globalContext).removePreferences(name);
    }

    public static void saveCurrentNavDomain(String navName){
        DataUtils.getInstance(CommonConfig.globalContext).putPreferences(Constants.Preferences.COMPANY,navName);
    }

    public static String getCurrentNavDomain(){
        return DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.COMPANY,"");
    }

    /**
     * 获取当前导航地址
     * @return
     */
    public static String getCurrentNavUrl(){
        return DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL,"");
    }

}
