package com.qunar.im.common;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;

/**
 * Created by huayu.chen on 2016/6/16.
 */
public class NoiseSuppressorHelper {
    //1）判断当前机型是否支持NS，如果支持设置NS(噪声抑制器)
    public static void setNoiseSuppressor(int audioSession) {
        NoiseSuppressor noiseSuppressor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                NoiseSuppressor.isAvailable()) {
            noiseSuppressor = NoiseSuppressor.create(audioSession);
            if(noiseSuppressor!=null)noiseSuppressor.setEnabled(true);
        }
    }

    //2）判断当前机型是否支持AES，如果支持设置AES(回声消除器)
    public static void setAcousticEchoCanceler(int audioSession) {
        AcousticEchoCanceler acousticEchoCanceler;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN&&
                AcousticEchoCanceler.isAvailable()) {
            acousticEchoCanceler = AcousticEchoCanceler.create(audioSession);
            if(acousticEchoCanceler!=null)acousticEchoCanceler.setEnabled(true);
        }
    }
    //3）判断当前机型是否支持AGC，如果支持设置AGC(自动增益控制)
    public static void setAutomaticGainControl(int audioSession) {
        AutomaticGainControl automaticGainControl;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN&&
                AutomaticGainControl.isAvailable()) {
            automaticGainControl = AutomaticGainControl.create(audioSession);
            if(automaticGainControl!=null)automaticGainControl.setEnabled(true);
        }
    }
}
