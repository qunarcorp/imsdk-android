package com.qunar.im.ui.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;

/**
 * Created by xinbo.wang on 2015/4/3.
 */
public class ConnectionStateReceiver extends BroadcastReceiver {

    private String lastType = "";
    private long time;

    @Override
    public void onReceive(final Context context, Intent intent) {
        synchronized (ConnectionStateReceiver.class){
            Logger.i("收到了广播");
            //在最上面获取连接的信息
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connManager.getActiveNetworkInfo();
            //如果info有信息
            if(info!=null){
                Logger.i("info信息:" + info.toString());
                if(System.currentTimeMillis() - time < 500){
                    return;
                }else {
                    time = System.currentTimeMillis();
                }

            }else{
                Logger.i("没有相关info信息:");
                ConnectionUtil.getInstance().shutdown();
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.No_NetWork, "");
                return;
            }
            boolean isConnected = ConnectionUtil.getInstance().isConnected();

            Logger.i("上一次的类型:"+lastType+" 当前连接状态:"+isConnected);
            if ((!TextUtils.isEmpty(lastType) && !info.getTypeName().equals(lastType)) || !isConnected) {
                Logger.i("进入判断开始连接:" + info.getTypeName() + ";上一次类型:" + lastType + "  " + intent.getAction());
                lastType = info.getTypeName();
                ConnectionUtil.getInstance().reConnectionForce();
            }
        }
    }
}
