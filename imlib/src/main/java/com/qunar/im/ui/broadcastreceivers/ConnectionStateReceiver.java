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

    private static String lastType = "";
    private long time;

    @Override
    public void onReceive(final Context context, Intent intent) {

        Logger.i("收到了广播");
        //在最上面获取连接的信息
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo gprs = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
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


//
//        if (NetworkUtils.isWifi(context)) {//切换到wifi 检测热更新
//            PullPatchService.runPullPatchService(context);
//        }


//        if (NetworkUtils.isConnection(context) == NetworkUtils.ConnectStatus.disconnected) {
//            BackgroundExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    //断网情况下,登陆状态为fasle
//                    ConnectionUtil.getInstance(context).shutdown();
//
////                    IMLogic.disconnetOnPause(true);
////                    EventBus.getDefault().post(new EventBusEvent.LoginComplete(false));
//                }
//            });
//            return;
//        }

        boolean isConnected = ConnectionUtil.getInstance().isConnected();

        Logger.i("上一次的类型:"+lastType+" 当前连接状态:"+isConnected);
        if ((!TextUtils.isEmpty(lastType) && !info.getTypeName().equals(lastType)) || !isConnected) {
//            Logger.i("准备登陆 检测到了切换网络,网络类型:" + info.getTypeName());
//            Logger.i("准备登陆 ");
            Logger.i("进入判断开始连接:" + info.getTypeName() + ";上一次类型:" + lastType + "  " + intent.getAction());
//            boolean loginStatus = ConnectionUtil.getInstance(context).isLoginStatus();
//            if (!loginStatus) {
//            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Connect_Interrupt, "");
            lastType = info.getTypeName();
            ConnectionUtil.getInstance().reConnectionForce();
//            }
        }


//        if (!update && (wifi.isConnected() || gprs.isConnected())) {
//            //有网但是还没刷新
//            update = true;
//
//            Logger.i("现在切换可用网络:wifi:" + wifi.isConnected() + ",,gprs:" + gprs.isConnected());
//        } else if (!wifi.isConnected() && !gprs.isConnected()) {
//            //没网
//            Logger.i("现在没有网络:");
//            ConnectionUtil.getInstance(context).setLoginStatus(false);
//            update = false;
//        }


//        if (!CurrentPreference.getInstance().isLogin()) {
//            if (CurrentPreference.getInstance().isLogin()) {
//                return;
//            }
////            EventBus.getDefault().post(new EventBusEvent.ReloginEvent(false));
//        }
    }
}
