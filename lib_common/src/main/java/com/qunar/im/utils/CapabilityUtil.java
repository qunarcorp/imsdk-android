package com.qunar.im.utils;

import com.qunar.im.base.jsonbean.CapabilityResult;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.services.QtalkNavicationService;

import de.greenrobot.event.EventBus;

public class CapabilityUtil {
    private static CapabilityUtil instance;

    public static CapabilityUtil getInstance(){
        if(instance == null){
            instance = new CapabilityUtil();
        }
        return instance;
    }


    public CapabilityResult getCurrentCapabilityData(){
        return IMDatabaseManager.getInstance().getCapability();
    }

    /**
     * 设置配置信息
     *
     * @param capability
     */
    public void saveExtConfig(CapabilityResult capability) {
        if (capability != null && capability.otherconfig != null && capability.ability != null) {
            QtalkNavicationService.HONGBAO_URL = (String) capability.otherconfig.get("redpackageurl");
            QtalkNavicationService.MY_HONGBAO = (String) capability.otherconfig.get("myredpackageurl");
            QtalkNavicationService.HONGBAO_BALANCE = (String) capability.otherconfig.get("balanceurl");
            QtalkNavicationService.AA_PAY_URL = (String) capability.
                    otherconfig.get("aacollectionurl");
            QtalkNavicationService.THANKS_URL = (String) capability.
                    otherconfig.get("thanksurl");
            QtalkNavicationService.COMPANY = capability.company;

            if (capability.ability != null) {
                if (capability.ability.base != null) {
                    for (String str : capability.ability.base) {
                        if (str.equals("group")) {
                            CommonConfig.showQchatGroup = true;
                        }
                    }
                }
                CommonConfig.showHongbao = false;
                if (capability.ability.group != null) {
                    for (String str : capability.ability.group) {
                        if (str.equals("redpackage")) {
                            CommonConfig.showHongbao = true;
                        }
                    }
                }
            }
            EventBus.getDefault().post(new EventBusEvent.ShowGroupEvent(CommonConfig.showQchatGroup));
        }
    }
}
