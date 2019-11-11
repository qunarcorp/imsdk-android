package com.qunar.im.thirdpush.client.huawei;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.PushException;
import com.huawei.hms.support.api.push.TokenResult;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.PhoneInfoUtils;
import com.qunar.im.thirdpush.Constants;
import com.qunar.im.thirdpush.QTPushConfiguration;
import com.qunar.im.thirdpush.core.QMessageProvider;
import com.qunar.im.thirdpush.core.QPushManager;
import com.qunar.im.utils.HttpUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class HwPushManager implements QPushManager {
    public static final String TAG = "HwPushManager";
    public static final String NAME = Constants.NAME_HUAWEI;
    public static QMessageProvider sQMessageProvider;
    private String appId;
    private String appKey;
    private HuaweiApiClient client;

    public HwPushManager(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }

    @Override
    public void registerPush(Context context) {
        connectHuaweiClient(context);
    }

    private void connectHuaweiClient(final Context context){
        if(client != null && client.isConnected()) {
            return;
        }
        //创建华为移动服务client实例用以使用华为push服务
        //需要指定api为HuaweiPush.PUSH_API
        //连接回调以及连接失败监听
        client = new HuaweiApiClient.Builder(context)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(new HuaweiApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected() {
                        Logger.i(TAG + "华为push连接成功");
                        getTokenAsyn();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Logger.i(TAG + "华为push连接 onConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new HuaweiApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(final ConnectionResult connectionResult) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connectHuaweiClient(context);
                                Logger.i(TAG + "华为push连接失败: " + (connectionResult != null ? connectionResult.getErrorCode() : "connectionResult为空"));
                            }
                        }, 30 * 1000);
                    }
                })
                .build();
        //建议在oncreate的时候连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        Logger.i(TAG + "华为建立连接" + "getAppID ：" + client.getAppID() + " : " + client.getCpID() + " ：" + getSingInfo(context, context.getPackageName().toString(), SHA256));
        client.connect((Activity) context);
    }

    @Override
    public void unRegisterPush(Context context) {
        Logger.i("注销华为推送 unRegisterPush" );
        unsetAlias(context, null);
        HttpUtil.unregistPushinfo(PhoneInfoUtils.getUniqueID(), QTPushConfiguration.getPlatName(), true);
        if (client != null) {
            //建议在oncreate的时候连接华为移动服务
            //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
            client.disconnect();
        }
    }

    private void getTokenAsyn() {
        //得到的token需要注册到服务器，token再HwPushMessageReceiver回调得到
        if(!client.isConnected()) {
            Logger.i(TAG + "获取TOKEN失败，原因：HuaweiApiClient未连接");
            return;
        }

        Logger.i(TAG + "异步接口获取PUSH TOKEN");
        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {

            @Override
            public void onResult(TokenResult result) {
                if(result != null && result.getTokenRes() !=null){
                    Logger.i(TAG + "华为push  getTokenAsyn ： " + result.getTokenRes().getToken());
                }
            }
        });
    }

    @Override
    public void setAlias(Context context, String alias) {
//        getTokenAsyn();
    }

    private void deleteToken(final String token) {

        if(client != null && !client.isConnected()) {
            Log.i(TAG, "注销TOKEN失败，原因：HuaweiApiClient未连接");
            return;
        }

        //需要在子线程中执行删除TOKEN操作
        new Thread() {
            @Override
            public void run() {
                //调用删除TOKEN需要传入通过getToken接口获取到TOKEN，并且需要对TOKEN进行非空判断
                Logger.i(TAG + "删除TOKEN：" + token);
                if (!TextUtils.isEmpty(token)){
                    try {
                        HuaweiPush.HuaweiPushApi.deleteToken(client, token);
                    } catch (PushException e) {
                        Logger.i(TAG + "删除TOKEN失败:" + e.getMessage());
                    }
                }

            }
        }.start();
    }

    private void getPushStatus() {
        if(client != null && !client.isConnected()) {
            Logger.i(TAG + "获取PUSH连接状态失败，原因：HuaweiApiClient未连接");
            return;
        }

        //需要在子线程中调用函数
        new Thread() {
            public void run() {
                Logger.i(TAG + "开始获取PUSH连接状态");
                HuaweiPush.HuaweiPushApi.getPushState(client);
                // 状态结果通过广播返回
            }
        }.start();
    }

    private void setReceiveNormalMsg(boolean flag) {
        if(client != null && !client.isConnected()) {
            Logger.i(TAG + "设置是否接收PUSH消息失败，原因：HuaweiApiClient未连接");
            return;
        }
        if(flag == true) {
            Logger.i(TAG + "允许应用接收PUSH消息");
        } else {
            Logger.i(TAG + "禁止应用接收PUSH消息");
        }
        HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(client, flag);
    }

    private void setReceiveNotifyMsg(boolean flag) {
        if(client != null && !client.isConnected()) {
            Logger.i(TAG + "设置是否接收PUSH通知消息失败，原因：HuaweiApiClient未连接");
            return;
        }
        if(flag == true) {
            Logger.i(TAG + "允许应用接收PUSH消息");
        } else {
            Logger.i(TAG + "禁止应用接收PUSH消息");
        }
        HuaweiPush.HuaweiPushApi.enableReceiveNotifyMsg(client, flag);
    }

    @Override
    public void unsetAlias(Context context, String alias) {
        deleteToken(alias);
    }

    @Override
    public void setTags(Context context, String... tags) {


    }

    @Override
    public void unsetTags(Context context, String... tags) {

    }

    @Override
    public void clearNotification(Context context){
        NotificationManager notificationManager =
                (NotificationManager) context.getApplicationContext().
                        getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setMessageProvider(QMessageProvider provider) {
        sQMessageProvider = provider;
    }

    @Override
    public void disable(Context context) {
        unRegisterPush(context);
    }

    public final static String MD5    = "MD5";
    public final static String SHA1   = "SHA1";
    public final static String SHA256 = "SHA256";

    /**
     * 返回一个签名的对应类型的字符串
     *
     * @param context
     * @param packageName
     * @param type
     *
     * @return
     */
    public static ArrayList<String> getSingInfo(Context context, String packageName, String type)
    {
        ArrayList<String> result = new ArrayList<String>();
        try
        {
            Signature[] signs = getSignatures(context, packageName);
            for (Signature sig : signs)
            {
                String tmp = "error!";
                if (MD5.equals(type))
                {
                    tmp = getSignatureString(sig, MD5);
                }
                else if (SHA1.equals(type))
                {
                    tmp = getSignatureString(sig, SHA1);
                }
                else if (SHA256.equals(type))
                {
                    tmp = getSignatureString(sig, SHA256);
                }
                result.add(tmp);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 返回对应包的签名信息
     *
     * @param context
     * @param packageName
     *
     * @return
     */
    public static Signature[] getSignatures(Context context, String packageName)
    {
        PackageInfo packageInfo = null;
        try
        {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo.signatures;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取相应的类型的字符串（把签名的byte[]信息转换成16进制）
     *
     * @param sig
     * @param type
     *
     * @return
     */
    public static String getSignatureString(Signature sig, String type)
    {
        byte[] hexBytes = sig.toByteArray();
        String fingerprint = "error!";
        try
        {
            MessageDigest digest = MessageDigest.getInstance(type);
            if (digest != null)
            {
                byte[] digestBytes = digest.digest(hexBytes);
                StringBuilder sb = new StringBuilder();
                for (byte digestByte : digestBytes)
                {
                    sb.append((Integer.toHexString((digestByte & 0xFF) | 0x100)).substring(1, 3));
                }
                fingerprint = sb.toString();
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return fingerprint;
    }
}
