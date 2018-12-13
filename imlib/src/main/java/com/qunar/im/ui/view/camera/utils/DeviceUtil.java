package com.qunar.im.ui.view.camera.utils;

import android.os.Build;


public class DeviceUtil {

    private static String[] huaweiRongyao = {
            "hwH60",    //荣耀6
            "hwPE",     //荣耀6 plus
            "hwH30",    //3c
            "hwHol",    //3c畅玩版
            "hwG750",   //3x
            "hw7D",      //x1
            "hwChe2",      //x1
    };

    public static String getDeviceInfo() {
        String handSetInfo =
                "手机型号：" + Build.DEVICE +
                        "\n系统版本：" + Build.VERSION.RELEASE +
                        "\nSDK版本：" + Build.VERSION.SDK_INT;
        return handSetInfo;
    }

    public static String getDeviceModel() {
        return Build.DEVICE;
    }

    public static boolean isHuaWeiRongyao() {
        int length = huaweiRongyao.length;
        for (int i = 0; i < length; i++) {
            if (huaweiRongyao[i].equals(getDeviceModel())) {
                return true;
            }
        }
        return false;
    }

    public static String getTelephonyManagerInfo() {
//        TelephonyManager tm = (TelephonyManager) CommonConfig.globalContext.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuffer sb = new StringBuffer();
//        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
//        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
//        sb.append("\nLine1Number = " + tm.getLine1Number());
//        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
//        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
//        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
//        sb.append("\nNetworkType = " + tm.getNetworkType());
//        sb.append("\nPhoneType = " + tm.getPhoneType());
//        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
//        sb.append("\nSimOperator = " + tm.getSimOperator());
//        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
//        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
//        sb.append("\nSimState = " + tm.getSimState());
//        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
//        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        sb.append("\nBrand = " + getPhoneBrand());
        sb.append("\nModel = " + getPhoneModel());
        sb.append("\nVersion = " + getPhoneAndroidVersion());
        return sb.toString();
    }

    /**
     * 设备厂商
     *
     * @return
     */
    public static String getPhoneBrand() {
        return Build.BOARD + "  " + Build.MANUFACTURER;
    }

    /**
     * 设备名称
     *
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    } /**
     * 系统版本号
     *
     * @return
     */
    public static int getPhoneAndroidVersion() {
        return Build.VERSION.SDK_INT;
    }
}
