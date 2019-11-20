package com.qunar.im.core.manager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * Created by may on 2017/6/28.
 */

class IMNetworkObserver extends Service {
    private static IMNetworkObserver instance;

    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    public static final String ACTION_NO_CONNECTION = "ACTION_NO_CONNECTION"; //网络没有连接
    public static final String ACTION_CONNECTIONED = "ACTION_CONNECTIONED";   //网络连接


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //当网络发生变化时
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    String name = info.getTypeName();

                    if (name.equals("WIFI")) {
                        // wifi 怎么着了应该是
                    } else {
                        // 否则应该是4G神马的
                    }
                    sendNetworkStateBroadCast(ACTION_CONNECTIONED);
                } else {
                    sendNetworkStateBroadCast(ACTION_NO_CONNECTION);
                }
            }
        }
    };


    /**
     * 发送本地广播通知网络状态
     *
     * @param action
     */
    private void sendNetworkStateBroadCast(String action) {
//        if (TextUtils.equals(ACTION_CONNECTIONED, action) ||
//                TextUtils.equals(ACTION_NO_CONNECTION, action)) {
//            Intent intent = new Intent(action);
//            LocalBroadcastUtils.send(getApplication(), intent);
//        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


    public static IMNetworkObserver getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
