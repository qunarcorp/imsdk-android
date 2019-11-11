package com.qunar.im.base.protocol;

import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by froyomu on 2019-08-22
 * <p>
 * Describe:
 */
public class PayApi {

    public static final String ALIPAY_PAY_CHANNEL = "alipay";

    /**
     * 获取绑定关系
     * @param callback
     */
    public static void get_bind_pay_account(final HttpRequestCallback callback){
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getPayurl() + "/red_envelope/get_bind_pay_account?user_id=" + CurrentPreference.getInstance().getUserid());
        Protocol.addBasicParamsOnHead(url);
        HttpUrlConnectionHandler.executeGet(url.toString(),callback);
    }

    /**
     * 支付宝绑定
     * @param uid 支付宝的uid
     * @param openId
     * @param callback
     */
    public static void bind_alipay_account(String uid,String openId,HttpRequestCallback callback){
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getPayurl() + "/red_envelope/bind_alipay_account?user_id=" + CurrentPreference.getInstance().getUserid());
        Protocol.addBasicParamsOnHead(url);
        Map<String,String> params = new HashMap<>();
        params.put("account",uid);
        params.put("openId",openId);
        HttpUrlConnectionHandler.executePostJson(url.toString(), JsonUtils.getGson().toJson(params),callback);
    }

    /**
     * 获取支付宝授权登录参数
     * @param callback
     */
    public static void get_alipay_login_params(final HttpRequestCallback callback){
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getPayurl() + "/red_envelope/alipay_app_login");
        Protocol.addBasicParamsOnHead(url);
        HttpUrlConnectionHandler.executeGet(url.toString(),callback);
    }

    /**
     * 发红包
     * @param params
     * @param callback
     */
    public static void send_red_envelope(Map<String,Object> params,final HttpRequestCallback callback){
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getPayurl() + "/red_envelope/create");
        Protocol.addBasicParamsOnHead(url);
        HttpUrlConnectionHandler.executePostJson(url.toString(),JsonUtils.getGson().toJson(params),callback);
    }

    /**
     * 红包详细
     * @param xmppid
     * @param rid
     * @param isRoom
     * @param callback
     */
    public static void red_envelope_get(String xmppid,String rid,boolean isRoom,final HttpRequestCallback callback){
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getPayurl() + "/red_envelope/get");
        if(isRoom)
            url.append("?group_id=");
        else
            url.append("?user_id=");
        url.append(xmppid);
        url.append("&rid=").append(rid);
        Protocol.addBasicParamsOnHead(url);
        HttpUrlConnectionHandler.executeGet(url.toString(),callback);

    }

    /**
     * 抢红包第一步--获取抢红包状态
     * @param xmppid
     * @param rid
     * @param isRoom
     * @param callback
     */
    public static void red_envelope_open(String xmppid,String rid,boolean isRoom,final HttpRequestCallback callback){
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getPayurl() + "/red_envelope/open");
        if(isRoom)
            url.append("?group_id=");
        else
            url.append("?user_id=");
        url.append(xmppid);
        url.append("&rid=").append(rid);
        url.append("&action=open_red_envelope");
        Protocol.addBasicParamsOnHead(url);
        HttpUrlConnectionHandler.executeGet(url.toString(),callback);
    }

    /**
     * 抢红包第二步--拆红包
     * @param xmppid
     * @param rid
     * @param isRoom
     * @param callback
     */
    public static void red_envelope_grap(String xmppid,String rid,boolean isRoom,final HttpRequestCallback callback){
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getPayurl() + "/red_envelope/grab");
        if(isRoom)
            url.append("?group_id=");
        else
            url.append("?user_id=");
        url.append(xmppid);
        url.append("&rid=").append(rid);
        url.append("&action=grab_red_envelope");
        Protocol.addBasicParamsOnHead(url);
        HttpUrlConnectionHandler.executeGet(url.toString(),callback);
    }

    /**
     * 我收到的红包
     * @param page
     * @param pagesize
     * @param year
     * @param callback
     */
    public static void red_envelope_receive(int page,int pagesize,int year,final HttpRequestCallback callback){
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getPayurl() + "/red_envelope/my_receive?");
        url.append("page=").append(page);
        url.append("&get_count=1");
        url.append("&pagesize=").append(pagesize);
        url.append("&year=").append(year);
        Protocol.addBasicParamsOnHead(url);
        HttpUrlConnectionHandler.executeGet(url.toString(),callback);
    }

    /**
     * 我发出去的红包
     * @param page
     * @param pagesize
     * @param year
     * @param callback
     */
    public static void red_envelope_send(int page,int pagesize,int year,final HttpRequestCallback callback){
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getPayurl() + "/red_envelope/my_send?");
        url.append("page=").append(page);
        url.append("&get_count=1");
        url.append("&pagesize=").append(pagesize);
        url.append("&year=").append(year);
        Protocol.addBasicParamsOnHead(url);
        HttpUrlConnectionHandler.executeGet(url.toString(),callback);
    }


}
