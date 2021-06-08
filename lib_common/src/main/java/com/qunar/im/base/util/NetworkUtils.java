package com.qunar.im.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by xinbo.wang on 2015/3/29.
 */
public class NetworkUtils {
    private static ConnectivityManager connectivityManager;

    public static ConnectStatus isConnection(Context context) {
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null)
                return ConnectStatus.unkown;
        }
        boolean isConnected = false;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            isConnected = networkInfo.isConnected();
        }
        return isConnected ? ConnectStatus.connected : ConnectStatus.disconnected;
    }

    public static int getNetworkType() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null)
            return -100;
        return networkInfo.getType();
    }

    public static boolean isWifi(Context context) {
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null)
                return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) return false;
        return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public enum ConnectStatus {
        connected, disconnected, unkown
    }
}
