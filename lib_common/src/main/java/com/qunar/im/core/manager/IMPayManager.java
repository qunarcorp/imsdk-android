package com.qunar.im.core.manager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.jsonbean.AlipayAuthResult;
import com.qunar.im.base.jsonbean.AlipayResult;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.PayApi;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.Constants;
import com.qunar.im.common.R;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.utils.ConnectionUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by froyomu on 2019-08-27
 * <p>
 * Describe:
 */
public class IMPayManager {
    private static IMPayManager instance;

    public static IMPayManager getInstance(){
        synchronized (IMPayManager.class){
            if(instance == null){
                instance = new IMPayManager();
            }
        }
        return instance;
    }

    private void sendAuthFailNotification(){
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_FAIL, Constants.Alipay.AUTH);
    }

    public void getAlipayLoginParams(){
        PayApi.get_alipay_login_params(new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response){
                try{
                    String resultString = Protocol.parseStream(response);
                    JSONObject result = new JSONObject(resultString);
                    if(result != null && result.optInt("ret") == 1){
                        String data = result.optString("data");
                        if(!TextUtils.isEmpty(data)){//唤起支付宝授权登录
                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_AUTH,data);
                        }else {
                            sendAuthFailNotification();
                        }
                    }else {
                        sendAuthFailNotification();
                    }
                }catch (Exception e){
                    sendAuthFailNotification();
                }
            }

            @Override
            public void onFailure(Exception e) {
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_FAIL, Constants.Alipay.AUTH);
            }
        });
    }

    public void checkAlipayAccount(){
        PayApi.get_bind_pay_account(new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                try{
                    String resultString = Protocol.parseStream(response);
                    if(!TextUtils.isEmpty(resultString)){
                        JSONObject result = new JSONObject(resultString);
                        if(result != null && result.optInt("ret") == 1){
                            JSONObject user_info = result.optJSONObject("data").optJSONObject("user_info");
                            if(!TextUtils.isEmpty(user_info.optString("alipay_login_account"))){//绑定了支付宝
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_RED_ENVELOP_CHOICE,Constants.Alipay.RED_ENVELOP_SEND);
                            }else {
                                getAlipayLoginParams();
                            }
                        }else {
                            getAlipayLoginParams();
                        }

                    }
                }catch (Exception e){
                    sendAuthFailNotification();
                }

            }

            @Override
            public void onFailure(Exception e) {
                sendAuthFailNotification();
            }
        });
    }

    public void bindAlipayAccount(String uid,String openId){
        PayApi.bind_alipay_account(uid,openId, new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) throws IOException {
                String resultString = Protocol.parseStream(response);
                Logger.i("bind_alipay_account",resultString);
            }

            @Override
            public void onFailure(Exception e) {
                Logger.e("bind_alipay_account",e.getLocalizedMessage());
            }
        });
    }

    public void payOrder(Activity context, String orderInfo, final ResultStatusCallBack callBack){
        DispatchHelper.Async("PayOrder",true,() -> {
            PayTask alipay = new PayTask(context);
            Map<String, String> result = alipay.payV2(orderInfo, true);
            AlipayResult payResult = new AlipayResult(result);
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
           callBack.result(resultStatus);
        });
    }

    public void payAuth(Activity context, String authInfo, final ResultStatusCallBack callBack){
        DispatchHelper.Async("PayAuth",true,() -> {
            // 构造AuthTask 对象
            AuthTask authTask = new AuthTask(context);
            // 调用授权接口，获取授权结果
            Map<String, String> result = authTask.authV2(authInfo, true);
            AlipayAuthResult alipayAuthResult = new AlipayAuthResult(result, true);
            String resultStatus = alipayAuthResult.getResultStatus();
            Logger.i("payAuth",resultStatus);
            if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(alipayAuthResult.getResultCode(), "200")) {
                // 获取alipay_open_id，调支付时作为参数extern_token 的value
                // 传入，则支付账户为该授权账户
//                payRedEnvelopChioce(Constants.Alipay.RED_ENVELOP_SEND,"");
                Logger.i("payAuth:" + alipayAuthResult.getResult());
                String user_id = Protocol.splitParams(alipayAuthResult.getResult()).get("user_id");
                String open_id = alipayAuthResult.getAlipayOpenId();
                ConnectionUtil.getInstance().bindAlipayAccount(user_id,open_id);
            }
            callBack.result(resultStatus);
        });
    }

    public interface ResultStatusCallBack{
        void result(String resultStatus);
    }

}
