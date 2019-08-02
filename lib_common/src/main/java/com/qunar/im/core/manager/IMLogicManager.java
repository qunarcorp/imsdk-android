package com.qunar.im.core.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.LruCache;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.CalendarVersion;
import com.qunar.im.base.jsonbean.NavConfigResult;
import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.jsonbean.OpsUnreadResult;
import com.qunar.im.base.jsonbean.VersionBean;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.IMGroup;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.NavigationNotice;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.RevokeInfo;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.other.QtalkSDK;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.Interfaces.IGroupEventReceivedDelegate;
import com.qunar.im.protobuf.Interfaces.IIMEventReceivedDelegate;
import com.qunar.im.protobuf.Interfaces.IMessageReceivedDelegate;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ParamIsEmptyException;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.protobuf.dispatch.DispatcherQueue;
import com.qunar.im.protobuf.entity.XMPPJID;
import com.qunar.im.protobuf.stream.PbAssemblyUtil;
import com.qunar.im.protobuf.stream.PbParseUtil;
import com.qunar.im.protobuf.stream.ProtobufSocket;
import com.qunar.im.protobuf.utils.StringUtils;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.MD5;
import com.qunar.im.utils.QtalkStringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by may on 2017/6/29.
 */

public class IMLogicManager implements IMessageReceivedDelegate, IGroupEventReceivedDelegate, IIMEventReceivedDelegate {
    private static IMLogicManager instance = new IMLogicManager();
    //    private String domain;
//    private String hostName;
    private String resource;
    //    private int port;
    private String remoteLoginKey;
    private IMProtocol _protocolType;
    private XMPPJID myself;

    private LruCache<String, JSONObject> userCache;

    private LruCache<String, Nick> nickCache;

    private static String defaultMucImage = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/2227ff2e304cb44a1980e9c1a3d78164.png";
    private static String defaultUserImage = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";

//    private List<String>

    private ProtobufSocket _pbSocket;

    private DispatcherQueue _loginComplateQueue;

    public static String getDefaultMucImage() {
        return defaultMucImage;
    }

    public static void setDefaultMucImage(String defaultMucImage) {
        IMLogicManager.defaultMucImage = defaultMucImage;
    }

    public static String getDefaultUserImage() {
        return defaultUserImage;
    }

    public static void setDefaultUserImage(String defaultUserImage) {
        IMLogicManager.defaultUserImage = defaultUserImage;
    }

    protected IMLogicManager() {
        _protocolType = IMProtocol.PROTOCOL_PROTOBUF;
//        remoteLoginKey = null;
        try {
            if (userCache == null) {
                userCache = new LruCache<>(100);
            }
            if (nickCache == null) {
                nickCache = new LruCache<>(500);
            }
//            userCache = new LruCache<>(100);
//            nickCache = new LruCache<>(200);
//            _loginComplateQueue = DispatchHelper.getInstance().takeDispatcher("loginComplateQueue");
        } catch (Exception e) {
        }

        _pbSocket = new ProtobufSocket();
        _pbSocket.addMessageDelegate(this);
        _pbSocket.addGroupEventDelegate(this);
        _pbSocket.addSocketEventDelegate(this);
    }

    public static IMLogicManager getInstance() {
        return instance;
    }

    public void clearCache() {
        if (userCache != null && userCache.size() > 0) {
            userCache.evictAll();
        }
        if (nickCache != null && nickCache.size() > 0) {
            nickCache.evictAll();
        }
    }

    //获取userinfo
    public JSONObject getUserInfoByUserId(XMPPJID myId) {
        if (myId == null) return null;
        String userId = myId.bareJID().fullname();

        JSONObject result = userCache.get(userId);
        if (result == null) {
            result = IMDatabaseManager.getInstance().selectUserByJID(userId);
            if (result != null) {
                userCache.put(userId, result);
            }
        }
        return result;
    }

    public void setNickToCache(Nick nick) {
        nickCache.put(nick.getXmppId(), nick);
    }

    public void getCollectionUserInfoByUserId(XMPPJID targe, final boolean enforce, boolean toDB, final NickCallBack nickCallBack) {
        if (targe == null) return;
        //获取目标id
        final String targeId = targe.fullname();
//        Logger.i("targeId:" + targeId);
        //根据目标id从数据库中获取缓存
        if (TextUtils.isEmpty(targeId)) {
            return;
        }
        Nick targeResult = null;
        if (!enforce) {
            targeResult = nickCache.get(targeId);
            //如果目标id缓存为空
            if (toDB || targeResult == null || TextUtils.isEmpty(targeResult.getXmppId()) || TextUtils.isEmpty(targeResult.getDescInfo()) || TextUtils.isEmpty(targeResult.getName())) {
                //从数据库中获取
                targeResult = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectCollectionUserByJID(targeId).toString(), Nick.class);
                if (targeResult != null && !TextUtils.isEmpty(targeResult.getXmppId())) {
                    nickCache.put(targeId, targeResult);
                    nickCallBack.onNickCallBack(targeResult);
                    return;
                }
                //如果数据库中也为空
                if (targeResult == null || TextUtils.isEmpty(targeResult.getXmppId()) || TextUtils.isEmpty(targeResult.getHeaderSrc())) {
                    //从网络获取
                    try {
                        IMUserCardManager.getInstance().updateCollectionUserCard(targeId, enforce, new IMUserCardManager.InsertDataBaseCallBack() {
                            @Override
                            public void onComplate(String stat) {
                                Nick nick = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectCollectionUserByJID(targeId).toString(), Nick.class);
                                if (stat.equals("success")) {

                                    if (nick != null && !TextUtils.isEmpty(nick.getXmppId())) {
                                        if (TextUtils.isEmpty(nick.getHeaderSrc())) {
                                            nick.setHeaderSrc(defaultUserImage);
                                        }
                                        if (TextUtils.isEmpty(nick.getXmppId())) {
                                            nick.setXmppId(targeId);
                                        }
                                        if (TextUtils.isEmpty(nick.getDescInfo())) {
                                            nick.setDescInfo("无");
                                        }
                                        if (TextUtils.isEmpty(nick.getName())) {
                                            nick.setName(targeId);
                                        }
                                        nickCache.put(targeId, nick);
                                        final Nick finalNick = nick;
                                        QunarIMApp.mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                nickCallBack.onNickCallBack(finalNick);
                                            }
                                        });
                                    }
                                } else {
//                                    Nick n = new Gson().fromJson(IMDatabaseManager.getInstance().selectUserByJID(targeId).toString(), Nick.class);
                                    if (nick == null) {
                                        nick = new Nick();
                                        nick.setXmppId(targeId);
                                        nick.setHeaderSrc(defaultUserImage);
                                        nick.setDescInfo("无");
                                        nick.setName(targeId);
                                    } else {
                                        if (TextUtils.isEmpty(nick.getHeaderSrc())) {
                                            nick.setHeaderSrc(defaultUserImage);
                                        }
                                        if (TextUtils.isEmpty(nick.getXmppId())) {
                                            nick.setXmppId(targeId);
                                        }
                                        if (TextUtils.isEmpty(nick.getDescInfo())) {
                                            nick.setDescInfo("无");
                                        }
                                        if (TextUtils.isEmpty(nick.getName())) {
                                            nick.setName(targeId);
                                        }
                                    }
                                    nickCache.put(targeId, nick);
                                    final Nick finalNick = nick;
                                    QunarIMApp.mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            nickCallBack.onNickCallBack(finalNick);
                                        }
                                    });

                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                nickCallBack.onNickCallBack(targeResult);
            }
        } else {
            try {
                IMUserCardManager.getInstance().updateCollectionUserCard(targeId, enforce, new IMUserCardManager.InsertDataBaseCallBack() {

                    @Override
                    public void onComplate(String stat) {
                        Nick nick = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectCollectionUserByJID(targeId).toString(), Nick.class);
                        if (stat.equals("success")) {

                            if (nick != null && !TextUtils.isEmpty(nick.getXmppId())) {
                                if (TextUtils.isEmpty(nick.getHeaderSrc())) {
                                    nick.setHeaderSrc(defaultUserImage);
                                }
                                if (TextUtils.isEmpty(nick.getXmppId())) {
                                    nick.setXmppId(targeId);
                                }
                                if (TextUtils.isEmpty(nick.getDescInfo())) {
                                    nick.setDescInfo("无");
                                }
                                if (TextUtils.isEmpty(nick.getName())) {
                                    nick.setName(targeId);
                                }
                                nickCache.put(targeId, nick);
                                final Nick finalNick = nick;
                                QunarIMApp.mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        nickCallBack.onNickCallBack(finalNick);
                                    }
                                });
                            }
                        } else {
//                            Nick n = new Gson().fromJson(IMDatabaseManager.getInstance().selectUserByJID(targeId).toString(), Nick.class);
                            if (nick == null) {
                                nick = new Nick();
                                nick.setXmppId(targeId);
                                nick.setHeaderSrc(defaultUserImage);
                                nick.setDescInfo("无");
                                nick.setName(targeId);
                            } else {
                                if (TextUtils.isEmpty(nick.getHeaderSrc())) {
                                    nick.setHeaderSrc(defaultUserImage);
                                }
                                if (TextUtils.isEmpty(nick.getXmppId())) {
                                    nick.setXmppId(targeId);
                                }
                                if (TextUtils.isEmpty(nick.getDescInfo())) {
                                    nick.setDescInfo("无");
                                }
                                if (TextUtils.isEmpty(nick.getName())) {
                                    nick.setName(targeId);
                                }
                            }
                            nickCache.put(targeId, nick);
                            final Nick finalNick = nick;
                            QunarIMApp.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    nickCallBack.onNickCallBack(finalNick);
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //名片接口回调
    public interface NickCallBack {
        void onNickCallBack(Nick nick);
    }

    //只有当 enforce 为false时, toDB才有用作用为数据库根据请求已经更新了,但是缓存中还不是最新的所以从数据库中拿一次
    public void getMucInfoByGroupId(XMPPJID targe, XMPPJID myself, final boolean enforce, boolean toDB, final NickCallBack nickCallBack) {
        if (targe == null) return;

        final String targeId = targe.fullname();
//        Logger.i("targeId:" + targeId);
        //根据目标id从数据库中获取缓存
        if (TextUtils.isEmpty(targeId)) {
            return;
        }

        Nick targeResult;
        if (!enforce) {
            targeResult = nickCache.get(targeId);
            //如果目标id缓存为空
            if (toDB || (targeResult == null || TextUtils.isEmpty(targeResult.getGroupId()) || TextUtils.isEmpty(targeResult.getHeaderSrc()))) {
                //从数据库中获取
                targeResult = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectMucByGroupId(targeId).toString(), Nick.class);
                if (targeResult != null && !TextUtils.isEmpty(targeResult.getGroupId()) && !TextUtils.isEmpty(targeResult.getHeaderSrc())) {
                    nickCache.put(targeId, targeResult);
                    nickCallBack.onNickCallBack(targeResult);
                    return;
                }
                //如果数据库中也为空
                if (targeResult == null || TextUtils.isEmpty(targeResult.getGroupId()) || TextUtils.isEmpty(targeResult.getHeaderSrc())) {
                    //从网络获取
                    updateGroupInfoFromNet(targeId, enforce, nickCallBack);
                }

            } else {
                nickCallBack.onNickCallBack(targeResult);
            }
        } else {
            updateGroupInfoFromNet(targeId, enforce, nickCallBack);
        }

    }

    public void updateGroupInfoFromNet(final String targeId, final boolean enforce, final NickCallBack nickCallBack) {
        DispatchHelper.Async("getGroupInfo", false, new Runnable() {
            @Override
            public void run() {
                try {
                    IMUserCardManager.getInstance().updateMucCardSync(targeId, enforce, new IMUserCardManager.InsertDataBaseCallBack() {
                        @Override
                        public void onComplate(String stat) {
                            Nick n;
                            if (stat.equals("success")) {
                                n = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectMucByGroupId(targeId).toString(), Nick.class);
                                if (n != null && !TextUtils.isEmpty(n.getGroupId())) {
                                    if (TextUtils.isEmpty(n.getHeaderSrc())) {
                                        n.setHeaderSrc(defaultMucImage);
                                    }
                                    if (TextUtils.isEmpty(n.getGroupId())) {
                                        n.setGroupId(targeId);
                                    }
                                    if (TextUtils.isEmpty(n.getDescInfo())) {
                                        n.setDescInfo("无");
                                    }
                                    if (TextUtils.isEmpty(n.getName())) {
                                        n.setName(targeId);
                                    }
                                }
                            } else {
                                n = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectMucByGroupId(targeId).toString(), Nick.class);
                                if (n == null) {
                                    n = new Nick();
                                    n.setGroupId(targeId);
                                    n.setHeaderSrc(defaultMucImage);
                                    n.setDescInfo("无");
                                    n.setName(targeId);
                                } else {
                                    if (TextUtils.isEmpty(n.getHeaderSrc())) {
                                        n.setHeaderSrc(defaultMucImage);
                                    }
                                    if (TextUtils.isEmpty(n.getGroupId())) {
                                        n.setGroupId(targeId);
                                    }
                                    if (TextUtils.isEmpty(n.getDescInfo())) {
                                        n.setDescInfo("无");
                                    }
                                    if (TextUtils.isEmpty(n.getName())) {
                                        n.setName(targeId);
                                    }
                                }

                            }
                            nickCache.put(targeId, n);
                            final Nick finalNick = n;
                            QunarIMApp.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    nickCallBack.onNickCallBack(finalNick);
                                }
                            });
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getCollectionMucInfoByGroupId(XMPPJID targe, boolean enforce, boolean toDB, final NickCallBack nickCallBack) {
        if (targe == null) return;

        final String targeId = targe.fullname();
//        Logger.i("targeId:" + targeId);
        //根据目标id从数据库中获取缓存
        if (TextUtils.isEmpty(targeId)) {
            return;
        }

        Nick targeResult = null;
        if (!enforce) {


            targeResult = nickCache.get(targeId);
            //如果目标id缓存为空
            if (toDB || (targeResult == null || TextUtils.isEmpty(targeResult.getGroupId()) || TextUtils.isEmpty(targeResult.getHeaderSrc()))) {
                //从数据库中获取
                targeResult = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectCollectionMucByGroupId(targeId).toString(), Nick.class);
                if (targeResult != null && !TextUtils.isEmpty(targeResult.getGroupId()) && !TextUtils.isEmpty(targeResult.getHeaderSrc())) {
                    nickCache.put(targeId, targeResult);
                    nickCallBack.onNickCallBack(targeResult);
                    return;
                }
                //如果数据库中也为空
                if (targeResult == null || TextUtils.isEmpty(targeResult.getGroupId()) || TextUtils.isEmpty(targeResult.getHeaderSrc())) {
                    //从网络获取
                    try {

                        IMUserCardManager.getInstance().updateCollectionMucCard(targeId, enforce, new IMUserCardManager.InsertDataBaseCallBack() {
                            @Override
                            public void onComplate(String stat) {
                                Nick n;
                                if (stat.equals("success")) {
                                    n = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectCollectionMucByGroupId(targeId).toString(), Nick.class);
                                    if (n != null && !TextUtils.isEmpty(n.getGroupId())) {
                                        if (TextUtils.isEmpty(n.getHeaderSrc())) {
                                            n.setHeaderSrc(defaultMucImage);
                                        }
                                        if (TextUtils.isEmpty(n.getGroupId())) {
                                            n.setGroupId(targeId);
                                        }
                                        if (TextUtils.isEmpty(n.getDescInfo())) {
                                            n.setDescInfo("无");
                                        }
                                        if (TextUtils.isEmpty(n.getName())) {
                                            n.setName(targeId);
                                        }
                                    }
//                                    targeResult[0] = new Gson().fromJson(IMDatabaseManager.getInstance().selectMucByGroupId(targeId).toString(),Nick.class);
//                                    if (targeResult[0] != null &&  !TextUtils.isEmpty(targeResult[0].getGroupId())) {
//                                        nickCache.put(targeId, targeResult[0]);
//                                        nickCallBack.onNickCallBack(targeResult[0]);
//                                    }
                                } else {
//                                    Nick n = new Nick();
//                                    n.setXmppId(targeId);
//                                    n.setHeaderSrc(defaultMucImage);
//                                    nickCache.put(targeId, n);
//                                    nickCallBack.onNickCallBack(n);


                                    n = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectCollectionMucByGroupId(targeId).toString(), Nick.class);
                                    if (n == null) {
                                        n = new Nick();
                                        n.setGroupId(targeId);
                                        n.setHeaderSrc(defaultMucImage);
                                        n.setDescInfo("无");
                                        n.setName(targeId);
                                    } else {
                                        if (TextUtils.isEmpty(n.getHeaderSrc())) {
                                            n.setHeaderSrc(defaultMucImage);
                                        }
                                        if (TextUtils.isEmpty(n.getGroupId())) {
                                            n.setGroupId(targeId);
                                        }
                                        if (TextUtils.isEmpty(n.getDescInfo())) {
                                            n.setDescInfo("无");
                                        }
                                        if (TextUtils.isEmpty(n.getName())) {
                                            n.setName(targeId);
                                        }
                                    }

                                }
                                nickCache.put(targeId, n);
                                final Nick finalNick = n;
                                QunarIMApp.mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        nickCallBack.onNickCallBack(finalNick);
                                    }
                                });
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                nickCallBack.onNickCallBack(targeResult);
            }
        } else {
            try {
                IMUserCardManager.getInstance().updateCollectionMucCard(targeId, enforce, new IMUserCardManager.InsertDataBaseCallBack() {
                    @Override
                    public void onComplate(String stat) {
                        if (stat.equals("success")) {
                            Nick n = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectCollectionMucByGroupId(targeId).toString(), Nick.class);
                            if (n != null && !TextUtils.isEmpty(n.getGroupId())) {
                                if (TextUtils.isEmpty(n.getHeaderSrc())) {
                                    n.setHeaderSrc(defaultMucImage);
                                }
                                if (TextUtils.isEmpty(n.getGroupId())) {
                                    n.setGroupId(targeId);
                                }
                                if (TextUtils.isEmpty(n.getDescInfo())) {
                                    n.setDescInfo("无");
                                }
                                if (TextUtils.isEmpty(n.getName())) {
                                    n.setName(targeId);
                                }
                                nickCache.put(targeId, n);
                                final Nick finalNick = n;
                                QunarIMApp.mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        nickCallBack.onNickCallBack(finalNick);
                                    }
                                });
                            }
                        } else {
                            Nick n = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectCollectionMucByGroupId(targeId).toString(), Nick.class);
                            if (n == null) {
                                n = new Nick();
                                n.setGroupId(targeId);
                                n.setHeaderSrc(defaultMucImage);
                                n.setDescInfo("无");
                                n.setName(targeId);
                            } else {
                                if (TextUtils.isEmpty(n.getHeaderSrc())) {
                                    n.setHeaderSrc(defaultMucImage);
                                }
                                if (TextUtils.isEmpty(n.getGroupId())) {
                                    n.setGroupId(targeId);
                                }
                                if (TextUtils.isEmpty(n.getDescInfo())) {
                                    n.setDescInfo("无");
                                }
                                if (TextUtils.isEmpty(n.getName())) {
                                    n.setName(targeId);
                                }
                            }

                            final Nick finalNick = n;
                            QunarIMApp.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    nickCallBack.onNickCallBack(finalNick);
                                }
                            });
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    //根据用户名获取UserInfo 目标jid 自身jid 是否强制执行
    public void getUserInfoByUserId(XMPPJID targe, XMPPJID myself, final boolean enforce, boolean toDB, final NickCallBack nickCallBack) {
        if (targe == null) {
            Nick nick = new Nick();
            nick.setXmppId("");
            nick.setHeaderSrc(defaultUserImage);
            nick.setDescInfo("无");
            nick.setName("");
            nick.setMark("");
            nick.setMood("");
            nickCallBack.onNickCallBack(nick);
            return;
        }
        //获取目标id
        String targeId = targe.fullname();
//        Logger.i("targeId:" + targeId);
        //根据目标id从数据库中获取缓存
        if (TextUtils.isEmpty(targeId)) {
            return;
        }
        //获取hotline映射关系.如果没有映射返回本身 add by hubo at 2018/8/29
//        targeId = ConnectionUtil.getInstance().getHotlineJid(targeId);
        if (!enforce) {
            Nick targeResult = nickCache.get(targeId);
            //如果目标id缓存为空
            if (toDB || targeResult == null || TextUtils.isEmpty(targeResult.getXmppId()) || TextUtils.isEmpty(targeResult.getDescInfo()) || TextUtils.isEmpty(targeResult.getName())) {
                targeResult = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectUserByJID(targeId).toString(), Nick.class);

                if (targeResult != null && !TextUtils.isEmpty(targeResult.getXmppId()) && !TextUtils.isEmpty(targeResult.getHeaderSrc())) {
                    targeResult.setMark(ConnectionUtil.getInstance().getMarkupNameById(targeId));
                    nickCache.put(targeId, targeResult);
                    nickCallBack.onNickCallBack(targeResult);
                    return;
                }
                //如果数据库中也为空
                if (targeResult == null || TextUtils.isEmpty(targeResult.getXmppId()) || TextUtils.isEmpty(targeResult.getHeaderSrc())) {
                    //从网络获取
                    updateUserInfoFromNet(targeId, enforce, nickCallBack);
                }

            } else {
                nickCallBack.onNickCallBack(targeResult);
            }
        } else {
            //从网络获取
            updateUserInfoFromNet(targeId, enforce, nickCallBack);
        }
    }

    public void updateUserInfoFromNet(final String targeId, final boolean enforce, final NickCallBack nickCallBack) {
        DispatchHelper.Async("getUserInfo", false, new Runnable() {
            @Override
            public void run() {
                //从数据库中获取
                try {
                    IMUserCardManager.getInstance().updateUserCardSync(targeId, enforce, new IMUserCardManager.InsertDataBaseCallBack() {
                        @Override
                        public void onComplate(String stat) {
                            Nick nick = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectUserByJID(targeId).toString(), Nick.class);
                            if (stat.equals("success")) {

                                if (nick != null && !TextUtils.isEmpty(nick.getXmppId())) {
                                    if (TextUtils.isEmpty(nick.getHeaderSrc())) {
                                        nick.setHeaderSrc(defaultUserImage);
                                    }
                                    if (TextUtils.isEmpty(nick.getXmppId())) {
                                        nick.setXmppId(targeId);
                                    }
                                    if (TextUtils.isEmpty(nick.getDescInfo())) {
                                        nick.setDescInfo("无");
                                    }
                                    if (TextUtils.isEmpty(nick.getName())) {
                                        nick.setName(targeId);
                                    }
                                    nick.setMark(ConnectionUtil.getInstance().getMarkupNameById(targeId));
                                    nickCache.put(targeId, nick);
                                    final Nick finalNick = nick;
                                    QunarIMApp.mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            nickCallBack.onNickCallBack(finalNick);
                                        }
                                    });
                                }
                            } else {
                                if (nick == null) {
                                    nick = new Nick();
                                    nick.setXmppId(targeId);
                                    nick.setHeaderSrc(defaultUserImage);
                                    nick.setDescInfo("无");
                                    nick.setName(targeId);
                                    nick.setMark(ConnectionUtil.getInstance().getMarkupNameById(targeId));
                                } else {
                                    if (TextUtils.isEmpty(nick.getHeaderSrc())) {
                                        nick.setHeaderSrc(defaultUserImage);
                                    }
                                    if (TextUtils.isEmpty(nick.getXmppId())) {
                                        nick.setXmppId(targeId);
                                    }
                                    if (TextUtils.isEmpty(nick.getDescInfo())) {
                                        nick.setDescInfo("无");
                                    }
                                    if (TextUtils.isEmpty(nick.getName())) {
                                        nick.setName(targeId);
                                    }
                                    nick.setMark(ConnectionUtil.getInstance().getMarkupNameById(targeId));
                                }
                                nickCache.put(targeId, nick);
                                final Nick finalNick = nick;
                                QunarIMApp.mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        nickCallBack.onNickCallBack(finalNick);
                                    }
                                });

                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public Nick getNickkk(String jid) {
        if (TextUtils.isEmpty(jid)) {
            return null;
        }
        Nick targeResult = null;
        targeResult = nickCache.get(jid);
        if (targeResult != null) {
            if (TextUtils.isEmpty(targeResult.getHeaderSrc())) {
                targeResult.setHeaderSrc(defaultUserImage);
                nickCache.put(jid, targeResult);
            }
            if (TextUtils.isEmpty(targeResult.getXmppId())) {
                targeResult.setXmppId(jid);
                nickCache.put(jid, targeResult);
            }
            if (TextUtils.isEmpty(targeResult.getDescInfo())) {
                targeResult.setDescInfo("无");
                nickCache.put(jid, targeResult);
            }
            if (TextUtils.isEmpty(targeResult.getName())) {
                targeResult.setName(QtalkStringUtils.parseId(jid));
                nickCache.put(jid, targeResult);
            }
            return targeResult;
        } else {
            return null;
        }
    }

    //根据用户名获取UserInfo 目标jid 自身jid 是否强制执行
    public Nick getNickById(XMPPJID targe) {
        if (targe == null) return new Nick();
        //获取目标id
        final String targeId = targe.fullname();
//        Logger.i("targeId:" + targeId);
        //根据目标id从数据库中获取缓存
        if (TextUtils.isEmpty(targeId)) {
            return new Nick();
        }
        Nick targeResult = null;
        targeResult = nickCache.get(targeId);
        //如果目标id缓存为空
        if (targeResult == null || TextUtils.isEmpty(targeResult.getXmppId()) || TextUtils.isEmpty(targeResult.getName())) {
            //从数据库中获取
            targeResult = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectUserByJID(targeId).toString(), Nick.class);
            if (targeResult != null && !TextUtils.isEmpty(targeResult.getXmppId())) {
//                nickCache.put(targeId, targeResult);
//                return targeResult;
            } else {
                targeResult = new Nick();
                targeResult.setXmppId(targeId);
                targeResult.setHeaderSrc(defaultUserImage);
            }
        }

        if (TextUtils.isEmpty(targeResult.getHeaderSrc())) {
            targeResult.setHeaderSrc(defaultUserImage);
        }
        if (TextUtils.isEmpty(targeResult.getXmppId())) {
            targeResult.setXmppId(targeId);
        }
        if (TextUtils.isEmpty(targeResult.getDescInfo())) {
            targeResult.setDescInfo("无");
        }
        if (TextUtils.isEmpty(targeResult.getName())) {
            targeResult.setName(QtalkStringUtils.parseId(targeId));
        }
        nickCache.put(targeId, targeResult);

        return targeResult;

    }

    public void mandatorySendMessage(ProtoMessageOuterClass.ProtoMessage protoMessage) {
        try {
            _pbSocket.sendProtoMessage(protoMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAuthenticated() {
        return _pbSocket.isAuthenticated();
    }

    public void sendMessage(ProtoMessageOuterClass.ProtoMessage protoMessage) {
        //先进行判断认证
        if (_pbSocket.isAuthenticated()) {
            try {
                //2 6 7分别是心跳 单聊 群聊消息,这三种消息都是又返回值的
                //所以尝试从这三种消息入手,得到得到超时办法

                Logger.i("认证成功,执行发送");
                _pbSocket.sendProtoMessage(protoMessage);
            } catch (Exception e) {
                Logger.i("连接不成功,尝试重试");
                e.printStackTrace();
                //在这里重连可能会出问题,暂时先不进行重连
//                reConnection();
                //更新数据库当前这条消息发送失败的状态
                IMDatabaseManager.getInstance().UpdateChatStateMessage(protoMessage, MessageStatus.LOCAL_STATUS_FAILED);

                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage()).getMessageId());

            }
        } else {
            //没有认证成功
            Logger.i("没有认证成功:" + _pbSocket.isAuthenticated());
            //暂时先不进行重连
            // reConnection();
            //更新数据库当前这条消息发送失败的状态
            IMDatabaseManager.getInstance().UpdateChatStateMessage(protoMessage, MessageStatus.LOCAL_STATUS_FAILED);

            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage()).getMessageId());
        }
    }

    /**
     * 重连
     */
    public void reConnection() {
        if (_pbSocket != null) {
            Logger.i("重连中 " + _pbSocket.isReconnecting() + "是否连接中：" + _pbSocket.isConnecting());
            connectedHandler.sendEmptyMessageDelayed(WHAT, 3 * 1000);
        }
    }

    /**
     * 强制重连
     */
    public synchronized void reConnectionForce() {
        if (_pbSocket != null && _pbSocket.isConnected()) {
            return;
        }
        QtalkSDK.getInstance().login(true);
    }

    public void shutdown() {
        if (_pbSocket != null) {
            Logger.i("TID:" + android.os.Process.myTid() + "单纯暂停");
            _pbSocket.shutdown();
        }

    }

    //获取虚拟用户
    public ProtoMessageOuterClass.IQMessage get_virtual_user_real(String jid) {
        if (_pbSocket.isAuthenticated()) {
            ProtoMessageOuterClass.IQMessage iqMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                    .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyStartSession)
                    .setMessageId(StringUtils.UUIDString())
                    .setValue(jid)
                    .build();
            try {
                ProtoMessageOuterClass.IQMessage result = _pbSocket.syncSendIQMessage(iqMessage, CurrentPreference.getInstance().getPreferenceUserId(), true);
                if (result != null && result.hasDefinedKey() && result.getDefinedKey() == ProtoMessageOuterClass.IQMessageKeyType.IQKeyResult) {
                    Logger.i("获取虚拟用户:" + result.toString());
//                    delegate.onVirtualUserResult(result, "success");
                    return result;
                }
            } catch (Exception e) {
                Logger.e(e, "get_virtual_user_real error");
                Logger.e("错误问题:" + e.toString());
            }
        }

        return null;
    }


    //获取虚拟用户List
    public ProtoMessageOuterClass.IQMessage get_virtual_user_role() {
        //先进行判断是否认证
        if (_pbSocket.isAuthenticated()) {
            //组建iqMessage
            ProtoMessageOuterClass.IQMessage iqMessage = ProtoMessageOuterClass.IQMessage.newBuilder()
                    .setDefinedKey(ProtoMessageOuterClass.IQMessageKeyType.IQKeyGetVUser)
                    .setMessageId(StringUtils.UUIDString())
                    .build();
            Logger.i("虚拟用户1:" + iqMessage);
            Logger.i("发送iq时候的的messageId:" + iqMessage.getMessageId());
            try {
//                delegate.onVirtualUserResult(null,"failed");
                Logger.i("虚拟用户1:" + CurrentPreference.getInstance().getPreferenceUserId());
                ProtoMessageOuterClass.IQMessage result = _pbSocket.syncSendIQMessage(iqMessage, CurrentPreference.getInstance().getPreferenceUserId(), true);

                Logger.i("获取热线返回的数据:" + result);
//                if (result != null) {
//                    Logger.i("result不为空:" + result);
//                    Logger.i("result.hasDefinedKey" + result.hasDefinedKey());
//                    if (result.hasDefinedKey()) {
//                        Logger.i("result.hasDefinedKey" + result.hasDefinedKey());
//                        Logger.i("result.getDefinedKey" + result.getDefinedKey());
//                        if (result.getDefinedKey() == ProtoMessageOuterClass.IQMessageKeyType.IQKeyResult) {
//                            Logger.i("数据验证成功:" + result);
//                            delegate.onVirtualUserResult(result, "success");
//                        }
//                    }
//                }
                if (result != null && result.hasDefinedKey() && result.getDefinedKey() == ProtoMessageOuterClass.IQMessageKeyType.IQKeyResult) {
                    Logger.i("获取虚拟用户:" + result.toString());
//                    delegate.onVirtualUserResult(result, "success");
                    return result;
                }
            } catch (Exception e) {
                Logger.e(e, "get_virtual_user_role_list error");
                Logger.e("错误问题:" + e.toString());
//                delegate.onVirtualUserResult(null, "faild");

            }

        }
        return null;
    }

    public String getRemoteLoginKey() {
//        if (StringUtils.isEmpty(remoteLoginKey)) {
        if (_protocolType == IMProtocol.PROTOCOL_PROTOBUF) {
            return _pbSocket.getRemoteLoginKey();
        } else {
            throw new UnsupportedOperationException("getRemoteLoginKey, XmppStack is Not yet implemented");
        }
//        } else
//            return remoteLoginKey;
    }

    public String getRemoteLoginKey(boolean mandatory) {
//        if (StringUtils.isEmpty(remoteLoginKey)) {
        if (_protocolType == IMProtocol.PROTOCOL_PROTOBUF) {
            return _pbSocket.getRemoteLoginKey(mandatory);
        } else {
            throw new UnsupportedOperationException("getRemoteLoginKey, XmppStack is Not yet implemented");
        }
//        } else
//            return remoteLoginKey;
    }

    /**
     * 清除serverKey
     */
    public String clearAndGetRemoteLoginKey() {
        _pbSocket.clearRemoteLoginKey();
        CurrentPreference.getInstance().setVerifyKey("");
        return getRemoteLoginKey();
    }


    public void login(String username, String password) throws IOException, ParamIsEmptyException {
        if (_protocolType == IMProtocol.PROTOCOL_PROTOBUF) {
            _pbSocket.setHostName(QtalkNavicationService.getInstance().getXmppHost());
            _pbSocket.setDomain(QtalkNavicationService.getInstance().getXmppdomain());
            _pbSocket.setHostPort(QtalkNavicationService.getInstance().getProtobufPort());
            _pbSocket.setUsername(username);
            _pbSocket.setPassword(password);
            _pbSocket.setVersion(GlobalConfigManager.getPBVersion());
            _pbSocket.setPlatForm(GlobalConfigManager.getAppPlatform());
            Logger.i("将_pbSocket基本数据进行初始化");

            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //resource 这个数据因为每次登陆不一样,所以必须放在登陆里面写
//                connectedList = new HashMap<>();
                connectedHandler.removeMessages(WHAT);
                _pbSocket.connect();
            }
        } else {
        }
    }

    /**
     * 出现不可登陆情况时,清除用户的登录信息
     */
    public void clearLastUserInfo() {
        IMUserDefaults.getStandardUserDefaults().newEditor(GlobalConfigManager.getGlobalContext())
                .removeObject(Constants.Preferences.usertoken)
                .synchronize();
        //username放入sp
        IMUserDefaults.getStandardUserDefaults().newEditor(GlobalConfigManager.getGlobalContext())
                .removeObject(Constants.Preferences.lastuserid)
                .synchronize();
    }


    @Override
    //在此处进行插入数据库操作
    public void onChatMessageReceived(ProtoMessageOuterClass.ProtoMessage message) {
        switch (message.getSignalType()) {
//              6 正常收到二人消息:
            case ProtoMessageOuterClass.SignalType.SignalTypeChat_VALUE:
                try {
                    if (message.getFrom().contains("@conference")) {
                        return;
                    }
                    //根据类型插入数据库 Im_message表
                    IMMessage newChatMessage = PbParseUtil.parseReceiveChatMessage(message, MessageStatus.REMOTE_STATUS_CHAT_DELIVERED + "", MessageStatus.LOCAL_STATUS_SUCCESS + "");

                    //抛出到界面上
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Text, newChatMessage);
//                IMDatabaseManager.getInstance().InsertChatMessage(message, "1", "0");
                    IMDatabaseManager.getInstance().InsertChatMessage(newChatMessage, true);
                    //根据类型插入数据库 IM_SessionList表F
                    IMDatabaseManager.getInstance().InsertIMSessionList(newChatMessage, false);
//                IMDatabaseManager.getInstance().InsertSessionList(message);

                    //抛出到界面上
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Text_After_DB, newChatMessage);

//                    if (newChatMessage.getMsgType() == ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE) {
//                        Intent i = new Intent("android.intent.action.VIEW",
//                                Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid="
//                                        + CurrentPreference.getInstance().getPreferenceUserId()
//                                        + "&toid=" + message.getFrom()
//                                        + "&chattype" + ConversitionType.MSG_TYPE_CHAT
//                                        + "&realjid" + message.getFrom()
//                                        + "&isFromChatRoom" + false
//                                        + "&offer=true&video=true&msgid=" + newChatMessage.getId()));
//                        i.putExtra("messge", newChatMessage);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        CommonConfig.globalContext.startActivity(i);
//                    } else if (newChatMessage.getMsgType() == ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Audio_VALUE) {
//                        Intent i = new Intent("android.intent.action.VIEW",
//                                Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid="
//                                        + CurrentPreference.getInstance().getPreferenceUserId()
//                                        + "&toid=" + message.getFrom()
//                                        + "&chattype" + ConversitionType.MSG_TYPE_CHAT
//                                        + "&realjid" + message.getFrom()
//                                        + "&isFromChatRoom" + false
//                                        + "&offer=true&video=false&msgid=" + newChatMessage.getId()));
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        CommonConfig.globalContext.startActivity(i);
//                    }
                    if (newChatMessage.getMsgType() == ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE
                            || newChatMessage.getMsgType() == ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Audio_VALUE) {//音视频通话
                        ConnectionUtil.getInstance().lanuchVideo(newChatMessage.getMsgType() == ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE,
                                message.getFrom(),
                                CurrentPreference.getInstance().getUserid());
                    }

                    if (!newChatMessage.isCarbon()) {
                        JSONObject jb = new JSONObject();
                        jb.put("id", newChatMessage.getMessageId());
                        JSONArray ja = new JSONArray();
                        ja.put(jb);
                        ProtoMessageOuterClass.ProtoMessage receive = PbAssemblyUtil.getBeenNewReadStateMessage(MessageStatus.STATUS_SINGLE_DELIVERED + "", ja, newChatMessage.getFromID(), myself);
                        mandatorySendMessage(receive);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                //todo:根据类型 插入数据库
                break;
            //7 正常收到群组消息
            case ProtoMessageOuterClass.SignalType.SignalTypeGroupChat_VALUE:

                //根据类型插入数据库 Im_message表
                IMMessage newGroupChatMessage = PbParseUtil.parseReceiveGroupChatMessage(message, MessageStatus.REMOTE_STATUS_CHAT_DELIVERED + "", MessageStatus.LOCAL_STATUS_SUCCESS + "");

                //抛出到界面上
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Group_Chat_Message_Text, newGroupChatMessage);

                IMDatabaseManager.getInstance().InsertChatMessage(newGroupChatMessage, true);
//                IMDatabaseManager.getInstance().InsertGroupChatMessage(message, "1", "0");
                //根据类型插入数据库 IM_SessionList表
                IMDatabaseManager.getInstance().InsertIMSessionList(newGroupChatMessage, false);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Group_Chat_Message_Text_After_DB, newGroupChatMessage);
//                IMDatabaseManager.getInstance().InsertSessionList(message);
                break;
            //16  收到消息发送状态
            case ProtoMessageOuterClass.SignalType.SignalTypeMState_VALUE:
//                //todo  暂时注释检查收到新消息未读数不增加1
//                //更新数据库当前这条消息发送的状态
                IMMessage stateMessage = PbParseUtil.parseReceiveChatMessage(message, MessageStatus.REMOTE_STATUS_CHAT_SUCCESS + "", MessageStatus.LOCAL_STATUS_SUCCESS + "");
                IMDatabaseManager.getInstance().UpdateChatStateMessage(stateMessage, false);

                try {
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_State, stateMessage);

                    //                    这种方式抛出消息id
//                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(ProtoMessageOuterClass.XmppMessage.parseFrom(message.getMessage()).getMessageId(), message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // 13 收到消息已读状态
            case ProtoMessageOuterClass.SignalType.SignalTypeReadmark_VALUE:
                // TODO: 2017/9/20 这个方法需要考虑一下是否把pb转成immessage 转换后,先不更改
                try {
                    IMMessage newReadMessage = PbParseUtil.parseReceiveReadMessage(message);

                    //单独处理群的ReadMark时间 用于拉群ReadMark历史使用
                    if (newReadMessage.getCollectionType() == ConversitionType.MSG_TYPE_GROUP) {
                        String time = String.valueOf(newReadMessage.getTime().getTime());
                        IMDatabaseManager.getInstance().updateGroupReadMarkTime(time);
                        Logger.i("SignalTypeReadmark_VALUE:" + time);
                    }
                    //收到消息已读状态,应该更新数据库,并发送通知 群组 单聊共用这一个方法
                    //如果更新的状态比现在库里存的状态小,那么拦截掉
                    IMDatabaseManager.getInstance().UpdateChatReadTypeMessage(newReadMessage);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Read_State, newReadMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Message_Read_Mark, "HaveUpdate");
                break;
            //10 收到正在输入状态
            case ProtoMessageOuterClass.SignalType.SignalTypeTyping_VALUE:
                //这次使用后两个参数其实为无用参数
                IMMessage inputMessage = PbParseUtil.parseReceiveChatMessage(message, "0", "0");
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Input, inputMessage);
                break;
            //14 撤销消息
            case ProtoMessageOuterClass.SignalType.SignalTypeRevoke_VALUE:
                RevokeInfo revokeInfo = PbParseUtil.parseRevokeMessage(message);
                if (revokeInfo.getMessageType().equals("-1")) {
                    IMMessage imMessage = new IMMessage();
                    imMessage.setDirection(IMMessage.DIRECTION_MIDDLE);
                    imMessage.setId(revokeInfo.getMessageId());
                    imMessage.setMessageID(revokeInfo.getMessageId());
                    Nick nick = JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectUserByJID(revokeInfo.getFromId()).toString(), Nick.class);
                    imMessage.setBody(nick.getName() + "撤回了一条消息");
                    IMDatabaseManager.getInstance().UpdateRevokeChatMessage(revokeInfo.getMessageId(), nick.getName() + "撤回了一条消息");
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Revoke, imMessage);
//                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Revoke, revokeInfo);
                }

                break;
            //2 3 iq消息
            case ProtoMessageOuterClass.SignalType.SignalTypeIQ_VALUE:
            case ProtoMessageOuterClass.SignalType.SignalTypeIQResponse_VALUE:
                try {
                    ProtoMessageOuterClass.IQMessage iqMessage = ProtoMessageOuterClass.IQMessage.parseFrom(message.getMessage());
                    if (iqMessage.hasDefinedKey()) {
                        switch (iqMessage.getValue()) {
                            case QtalkEvent.Ping:
//                                String msgId = iqMessage.getMessageId();
//                                connectedList.remove(msgId);
                                connectedHandler.removeMessages(WHAT);
                                break;
                            case QtalkEvent.Muc_Invite_User_V2:
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Muc_Invite_User_V2, message.getFrom());
                                break;
                            case QtalkEvent.IQ_CREATE_MUC:
                                //在这里判断一下返回值
                                String str = iqMessage.getBody().getValue();
                                if (str.equals("success")) {
                                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.IQ_CREATE_MUC, message.getFrom());
                                }
                                break;
                            //获取群成员
                            case QtalkEvent.IQ_GET_MUC_USER:
                                IMDatabaseManager.getInstance().insertUpdateGroupMembers(message);
                                String groupId = message.getFrom();
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Group_Member_Update, groupId);
                                //TODO 暂时注释
//                                List<GroupMember> memberList = IMDatabaseManager.getInstance().SelectGroupMemberByGroupId(groupId);
//                                try {
//                                    IMUserCardManager.getInstance().updateUserCardByMemberList(memberList, false, new IMUserCardManager.InsertDataBaseCallBack() {
//                                        @Override
//                                        public void onComplate(String stat) {
//                                            if ("success".equals(stat)) {
//                                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Group_Member_Update, groupId);
//                                            }
//                                        }
//                                    });
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
                                break;
                            //退群
//                            case QtalkEvent.IQ_LEAVE_GROUP:
//                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.IQ_LEAVE_GROUP, message);
//                                break;
                            case QtalkEvent.USER_GET_FRIEND:
                                List<Nick> fList = PbParseUtil.parseGetFriends(message);

//                                fList = IMDatabaseManager.getInstance().SelectIMUserByFriendList(fList);
//                                if (fList == null || fList.size() <= 0) {
//                                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.USER_GET_FRIEND, "succes");
//                                    return;
//                                }
                                if (fList != null) {
                                    Logger.i("获取到的好友列表:" + JsonUtils.getGson().toJson(fList));
                                }
                                long startUpdateFriend = System.currentTimeMillis();
                                IMDatabaseManager.getInstance().UpdateFriendListByList(fList);
                                Logger.i("更新好友耗时:" + (System.currentTimeMillis() - startUpdateFriend));
//                                IMDatabaseManager.getInstance().UpdateFriendListByList(fList);
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.USER_GET_FRIEND, "succes");
//                                List<Nick> nn = IMDatabaseManager.getInstance().SelectFriendList();
//                                Logger.i(new Gson().toJson(nn) +"数据库中好友");
                                break;
                            case QtalkEvent.Get_Verify_Friend_Mode:
                                Map<String, String> mode = PbParseUtil.parseVerifyFriendMode(message);
                                EventBus.getDefault().post(new EventBusEvent.VerifyFriend(mode));
                                break;
                            case QtalkEvent.User_Del_Friend:
                                EventBus.getDefault().post(new EventBusEvent.FriendsChange(true));
                                break;
                        }
                    }
//                    else {
//                        //证明这是一条心跳消息的返回
//
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            //1 功能类消息
            case ProtoMessageOuterClass.SignalType.SignalTypePresence_VALUE:
                try {
                    ProtoMessageOuterClass.PresenceMessage presenceMessage = ProtoMessageOuterClass.PresenceMessage.parseFrom(message.getMessage());

                    if (presenceMessage.hasCategoryType()) {
                        switch (presenceMessage.getCategoryType()) {
                            case ProtoMessageOuterClass.CategoryType.CategoryOrganizational_VALUE:
                                Logger.i("收到获取组织架构结构图通知");
                                if (CommonConfig.isQtalk) {
                                    LoginComplateManager.processBuddy();
//                                    LoginComplateManager.updateOrganizationsFromUrl();
                                } else {
                                    LoginComplateManager.getQchatDepInfo();
                                }
                                break;
                            case ProtoMessageOuterClass.CategoryType.CategoryTickUser_VALUE:
                                Logger.i("收到踢人通知");
                                clearLastUserInfo();
                                shutdown();
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.LOGIN_FAILED, 0);
                                break;
                            case ProtoMessageOuterClass.CategoryType.CategoryNavigation_VALUE:

                                Logger.i("收到在线更新导航通知");
                                String navname = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_NAME, "");
                                String oldNavString = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(navname, "");
                                NavConfigResult oldNav = JsonUtils.getGson().fromJson(oldNavString, NavConfigResult.class);
                                int oldVersion = !(TextUtils.isEmpty(oldNav.version)) ? Integer.valueOf(oldNav.version) : 0;
                                NavigationNotice navigationNotice = JsonUtils.getGson().fromJson(presenceMessage.getBody().getValue(), NavigationNotice.class);
                                Logger.i("新导航版本:" + navigationNotice.getNavversion() + ",旧导航版本:" + oldVersion);
                                if (navigationNotice != null && !TextUtils.isEmpty(navigationNotice.getNavversion())) {
                                    if (Integer.parseInt(navigationNotice.getNavversion()) > oldVersion) {
                                        ConnectionUtil.getInstance().initNavConfig(true);
                                    }
                                }
                                break;
                            case ProtoMessageOuterClass.CategoryType.CategoryGlobalNotification_VALUE:
                                Logger.i("收到全局通知");
                                NoticeBean globalNotice = JsonUtils.getGson().fromJson(presenceMessage.getBody().getValue(), NoticeBean.class);
                                if (globalNotice == null) {
                                    return;
                                }
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.GLOBALNOTICE, globalNotice);
                                break;
                            case ProtoMessageOuterClass.CategoryType.CategorySpecifyNotification_VALUE:
                                Logger.i("收到指定通知");
                                NoticeBean specifyNotice = JsonUtils.getGson().fromJson(presenceMessage.getBody().getValue(), NoticeBean.class);
                                if (specifyNotice == null) {
                                    return;
                                }
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SPECIFYNOTICE, specifyNotice);
                                break;
                            case ProtoMessageOuterClass.CategoryType.CategoryConfigSync_VALUE:
                                Logger.i("收到个人配置同步通知");
                                VersionBean versionBean = JsonUtils.getGson().fromJson(presenceMessage.getBody().getValue(), VersionBean.class);
                                try {
                                    JSONObject jsonObject = new JSONObject(presenceMessage.getBody().getValue());
                                    if(jsonObject.has("forcequickreply")){
                                        if(jsonObject.getBoolean("forcequickreply")){
                                            LoginComplateManager.updateQuickReply(true);
                                        }
                                    }

                                    if(jsonObject.has("force") && jsonObject.getBoolean("force")){
                                        LoginComplateManager.updateUserServiceConfig(versionBean.isForce());
                                    }else{
                                        boolean isMy = CurrentPreference.getInstance().getResource().equals(versionBean.getResource());
                                        if (isMy) {
                                            return;
                                        }
                                        int newConfigVersion = versionBean.getVersion();
                                        int oldConfigVersion = IMDatabaseManager.getInstance().selectUserConfigVersion();
                                        if (newConfigVersion > oldConfigVersion) {
                                            LoginComplateManager.updateUserServiceConfig(false);
                                        }
                                    }

                                    if(jsonObject.has("forceOldSearch")){
                                        if(jsonObject.getBoolean("forceOldSearch")){
                                            IMDatabaseManager.getInstance().insertFocusSearchCacheData(true+"");
                                        }else{
                                            IMDatabaseManager.getInstance().insertFocusSearchCacheData(false+"");
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case ProtoMessageOuterClass.CategoryType.CategoryOPSNotification_VALUE:
                                Logger.i("收到ops红点通知");
                                boolean isShow = JsonUtils.getGson().fromJson(presenceMessage.getBody().getValue(), OpsUnreadResult.DataBean.class).isHasUnread();
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.refreshOPSUnRead, isShow);
                                break;
                            case ProtoMessageOuterClass.CategoryType.CategoryCalendarSync_VALUE:
                                Logger.i("收到日历同步通知");
                                CalendarVersion calendarVersion = JsonUtils.getGson().fromJson(presenceMessage.getBody().getValue(), CalendarVersion.class);
                                long oldCalendarVersion = IMDatabaseManager.getInstance().selectUserTripVersion();
                                long newCalendarVersion = Long.parseLong(calendarVersion.getUpdateTime());
                                if (newCalendarVersion > oldCalendarVersion) {
                                    LoginComplateManager.updateTripList();
                                } else {
                                    Logger.i("本次未能同步,本地版本:" + oldCalendarVersion + ";服务器版本:" + newCalendarVersion);
                                }


                                break;
                            case ProtoMessageOuterClass.CategoryType.CategoryOnlineClientSync_VALUE:
                                Logger.i("收到在线客户列表");
                                boolean showHead = false;
                                String resource = presenceMessage.getBody().getValue();
                                if (resource != null) {
                                    resource = resource.toLowerCase();
                                    if (resource.contains("pc") || resource.contains("mac") || resource.contains("linux")) {
                                        showHead = true;
                                    } else {
                                        showHead = false;
                                    }
                                }
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.SHOW_HEAD, showHead);
                                break;
                            case ProtoMessageOuterClass.CategoryType.CategoryClientSpecialNotice_VALUE:
                                try {
                                    JSONObject jsonObject = new JSONObject(presenceMessage.getBody().getValue());
                                    if (jsonObject != null) {
                                        boolean checkConfig = jsonObject.optBoolean("checkConfig");
                                        if (checkConfig) {
                                            HttpUtil.getMyCapability(true);
                                        }
                                        boolean uploadLog = jsonObject.optBoolean("uploadLog");
                                        if (uploadLog) {
//                                            FeedBackServcie.runFeedBackServcieService(CommonConfig.globalContext, new String[]{"日志反馈"}, false);
                                            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.FEED_BACK, new String[]{"日志反馈"},false);
                                        }
                                        boolean resetUnreadCount = jsonObject.optBoolean("resetUnreadCount");
                                        if (resetUnreadCount) {
                                            ConnectionUtil.getInstance().resetUnreadCount();
                                        }
                                    }
                                } catch (JSONException e) {

                                }
                                break;

                            case ProtoMessageOuterClass.CategoryType.CategoryWorkWorldNotice_VALUE:
                                try {
                                    //收到朋友圈通知


                                     WorkWorldNoticeItem item = JsonUtils.getGson().fromJson(presenceMessage.getBody().getValue(), WorkWorldNoticeItem.class);
                                    if (item.getEventType().equals(Constants.WorkWorldState.NOTICE)) {
                                        List<WorkWorldNoticeItem> array = new ArrayList<>();
                                        array.add(item);
                                        IMDatabaseManager.getInstance().InsertWorkWorldNoticeByList(array, false);
                                        if (!IMDatabaseManager.getInstance().SelectWorkWorldPremissions()) {
                                            return;
                                        }
                                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE, item);
                                    } else if (item.getEventType().equals(Constants.WorkWorldState.WORKWORLDATMESSAGE)) {
                                        List<WorkWorldNoticeItem> array = new ArrayList<>();
                                        array.add(item);
                                        IMDatabaseManager.getInstance().InsertWorkWorldNoticeByList(array, false);
                                        if (!IMDatabaseManager.getInstance().SelectWorkWorldPremissions()) {
                                            return;
                                        }
                                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE, item);
                                    } else if(item.getEventType().equals(Constants.WorkWorldState.COMMENTATMESSAGE)){
                                        List<WorkWorldNoticeItem> array = new ArrayList<>();
                                        array.add(item);
                                        IMDatabaseManager.getInstance().InsertWorkWorldNoticeByList(array, false);
                                        if (!IMDatabaseManager.getInstance().SelectWorkWorldPremissions()) {
                                            return;
                                        }
                                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_NOTICE, item);
                                    }else if (item.getEventType().equals(Constants.WorkWorldState.WORKWORLD)) {

                                        final String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");
                                       WorkWorldItem workWorldItem = IMDatabaseManager.getInstance().selectWorkWorldItemByUUID(item.getPostUUID());
                                       if(workWorldItem!=null){
                                           return;
                                       }
                                        IMUserDefaults.getStandardUserDefaults().newEditor(CommonConfig.globalContext)
                                                .putObject(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                                                        + QtalkNavicationService.getInstance().getXmppdomain()
                                                        + CommonConfig.isDebug
                                                        + MD5.hex(navurl)
                                                        + "WORKWORLDSHOWUNREAD", true)
                                                .synchronize();
                                        //todo 这里进行入口页  通知
                                        if (!IMDatabaseManager.getInstance().SelectWorkWorldPremissions()) {
                                            return;
                                        }
                                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.WORK_WORLD_FIND_NOTICE, item);


                                    }

                                } catch (Exception e) {
                                    new String();
                                }
                                break;
                        }
                    }
                    if (presenceMessage.hasDefinedKey()) {
                        switch (presenceMessage.getValue()) {
                            //删除好友
                            case QtalkEvent.delete_friend:
                                Nick delete = PbParseUtil.parseRemoveFriend(message);
                                if (delete != null) {
                                    IMDatabaseManager.getInstance().deleteFriend(delete);
                                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.USER_GET_FRIEND);
                                }

                                break;
                            //添加好友
                            case QtalkEvent.Verify_Friend:
                                Nick add = PbParseUtil.parseAddFriend(message);
                                if (add != null) {
                                    IMDatabaseManager.getInstance().addFriend(add);
                                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.USER_GET_FRIEND);
                                }
//                                EventBus.getDefault().post(new EventBusEvent.FriendsChange(true));
                                break;
                            //更新群名片
                            case QtalkEvent.Update_Muc_Vcard:
                                Nick nick = PbParseUtil.parseMucCard(message);
                                List<Nick> list = new ArrayList<>();
                                list.add(nick);
                                IMDatabaseManager.getInstance().updateMucCard(list);
                                nickCache.put(nick.getGroupId(), nick);
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Update_Muc_Vcard, "success");
                                break;
                            //收到移除群组或踢人广播
                            case QtalkEvent.Del_Muc_Register:
                                GroupMember dgm = PbParseUtil.parseDeleteMucMember(message);
                                IMDatabaseManager.getInstance().DeleteGroupMemberByGM(dgm);
                                //删除成员Id与自己相同,证明自己被踢出群了,所以把会话列表也删除,并且把相关群消息也删除
                                if (dgm.getMemberId().equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                                    IMGroup i = new IMGroup();
                                    i.setGroupId(dgm.getGroupId());
                                    IMDatabaseManager.getInstance().DeleteGroupAndSessionListByGM(i);
                                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Remove_Session, dgm.getGroupId());
                                }
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Del_Muc_Register, dgm.getGroupId());
                                break;
                            //收到加人广播
                            case QtalkEvent.Invite_User:
                                GroupMember igm = PbParseUtil.parseInviteMucMember(message);
                                IMDatabaseManager.getInstance().InsertGroupMemberByGM(igm);
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Invite_User, "success");
                                break;
                            //收到群摧毁广播
                            case QtalkEvent.Destory_Muc:
                                IMGroup dimGroup = PbParseUtil.parseDeleteMuc(message);
                                JSONObject mucJson = IMDatabaseManager.getInstance().selectMucByGroupId(dimGroup.getGroupId());
                                String mucName = mucJson.optString("Name");
                                IMDatabaseManager.getInstance().DeleteGroupAndSessionListByGM(dimGroup);
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Remove_Session, dimGroup.getGroupId());
                                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Destory_Muc, dimGroup.getGroupId(), mucName);
                                break;
                            case QtalkEvent.USER_JOIN_MUC:
                                GroupMember member = PbParseUtil.parseGroupAffiliation(message);
                                if(member != null){
                                    IMDatabaseManager.getInstance().InsertGroupMemberByGM(member);
                                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Group_Member_Update, member.getGroupId());
                                }

                                break;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            //136 加密
            case ProtoMessageOuterClass.SignalType.SignalTypeEncryption_VALUE:
                //根据类型插入数据库 Im_message表
                IMMessage encryptMessage = PbParseUtil.parseReceiveChatMessage(message, MessageStatus.REMOTE_STATUS_CHAT_DELIVERED + "", MessageStatus.LOCAL_STATUS_SUCCESS + "");
                //抛出到界面上
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.CHAT_MESSAGE_ENCRYPT, encryptMessage);
//                //根据类型插入数据库 Im_message表
//                IMDatabaseManager.getInstance().InsertChatMessage(message, "1", "0");
//                //根据类型插入数据库 IM_SessionList表
//                IMDatabaseManager.getInstance().InsertSessionList(message);
                break;
            //15
            case ProtoMessageOuterClass.SignalType.SignalTypeSubscription_VALUE:
                //根据类型插入数据库 IM_Public_Number_Message表
                IMMessage newSubscriptionMessage = PbParseUtil.parseReceiveChatMessage(message, MessageStatus.REMOTE_STATUS_CHAT_DELIVERED + "", MessageStatus.LOCAL_STATUS_SUCCESS + "");
                IMDatabaseManager.getInstance().InsertPublicNumberMessage(newSubscriptionMessage);

                //系统消息 公告消息 抢单消息  入库  其他的不入库 暂时处理
                if (newSubscriptionMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeNotice_VALUE || newSubscriptionMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeSystem_VALUE
                        || newSubscriptionMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuVcard_VALUE || newSubscriptionMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuResult_VALUE) {
                    IMDatabaseManager.getInstance().InsertChatMessage(newSubscriptionMessage, true);
                    //根据类型插入数据库 IM_SessionList表
                    IMDatabaseManager.getInstance().InsertIMSessionList(newSubscriptionMessage, false);
                }
                //抛出到界面上
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION, newSubscriptionMessage);
                break;
            // 17 qtalk 系统通知消息
            //17  qtalk 系统通知消息
            case ProtoMessageOuterClass.SignalType.SignalTypeHeadline_VALUE:
                //根据类型插入数据库 Im_message表
                IMMessage headlineMessage = PbParseUtil.parseReceiveChatMessage(message, MessageStatus.REMOTE_STATUS_CHAT_DELIVERED + "", MessageStatus.LOCAL_STATUS_SUCCESS + "");
                headlineMessage.setConversationID(Constants.SYS.SYSTEM_MESSAGE);//防止生成多个会话，写死
                headlineMessage.setFromID(Constants.SYS.SYSTEM_MESSAGE);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Text, headlineMessage);
                IMDatabaseManager.getInstance().InsertChatMessage(headlineMessage, true);
                //根据类型插入数据库 IM_SessionList表
                IMDatabaseManager.getInstance().InsertIMSessionList(headlineMessage, false);
                //抛出到界面上
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Text_After_DB, headlineMessage);
                break;
            //signltype 132 客服类型消息
            case ProtoMessageOuterClass.SignalType.SignalTypeConsult_VALUE:
                try {
                    IMMessage newConsultMessage = PbParseUtil.parseReceiveChatMessage(message, MessageStatus.REMOTE_STATUS_CHAT_DELIVERED + "", MessageStatus.LOCAL_STATUS_SUCCESS + "");
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Text, newConsultMessage);
                    IMDatabaseManager.getInstance().InsertChatMessage(newConsultMessage, true);
                    //根据类型插入数据库 IM_SessionList表
                    IMDatabaseManager.getInstance().InsertIMSessionList(newConsultMessage, true);
                    //抛出到界面上
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Text_After_DB, newConsultMessage);

                    if (!newConsultMessage.isCarbon()) {
                        JSONObject jb = new JSONObject();
                        jb.put("id", newConsultMessage.getMessageId());
                        JSONArray ja = new JSONArray();
                        ja.put(jb);
                        ProtoMessageOuterClass.ProtoMessage receive = PbAssemblyUtil.getBeenNewReadStateMessage(MessageStatus.STATUS_SINGLE_DELIVERED + "", ja, newConsultMessage.getRealfrom(), myself);
                        mandatorySendMessage(receive);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            //140 代收消息逻辑处理
            case ProtoMessageOuterClass.SignalType.SignalTypeCollection_VALUE:
                //生成immessage对象
                IMMessage collectionMessage = PbParseUtil.parseReceiveCollectionMessage(message);
                //插入message表和collectionmessage表
                IMDatabaseManager.getInstance().InsertCollectionMessage(collectionMessage);
                //插入sessionlist表
                IMDatabaseManager.getInstance().InsertIMSessionList(collectionMessage, false);
                //插入collectionuser表
                IMDatabaseManager.getInstance().InsertCollectionUser(collectionMessage);
                //抛出通知
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Collection_Message_Text, collectionMessage);
                break;
            case ProtoMessageOuterClass.SignalType.SignalTypeWebRtc_VALUE:
                IMMessage webrtcMsg = PbParseUtil.parseReceiveChatMessage(message, "0", "0");
//                if (webrtcMsg.getMsgType() == ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Audio_VALUE) {
//                    Intent i = new Intent("android.intent.action.VIEW",
//                            Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid=" +
//                                    QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getPreferenceUserId())
//                                    + "&toid=" + QtalkStringUtils.parseBareJid(webrtcMsg.getFromID())
//                                    + "&offer=true&video=false&msgid=" + webrtcMsg.getId()));
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    CommonConfig.globalContext.startActivity(i);
//                } else if (webrtcMsg.getMsgType() == ProtoMessageOuterClass.MessageType.WebRTC_MsgType_Video_VALUE) {
//                    Intent i = new Intent("android.intent.action.VIEW",
//                            Uri.parse(CommonConfig.schema + "://qcrtc/webrtc?fromid=" +
//                                    QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getPreferenceUserId())
//                                    + "&toid=" + webrtcMsg.getFromID()
//                                    + "&offer=true&video=true&msgid=" + webrtcMsg.getId()));
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    CommonConfig.globalContext.startActivity(i);
//                }
                EventBus.getDefault().post(new EventBusEvent.WebRtcMessage(webrtcMsg));
                break;
            //黑名单
            case ProtoMessageOuterClass.SignalType.SignalTypeError_VALUE:
                IMMessage errorMessage = PbParseUtil.parseErrorMessage(message);
                IMDatabaseManager.getInstance().UpdateChatStateMessage(errorMessage, true);
                if (!TextUtils.isEmpty(errorMessage.getId())) {
                    //todo 暂时只支持普通二人会话，不支持consult消息
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_State, errorMessage);
                    IMMessage newerrormessage = new IMMessage();
                    newerrormessage.setBody("对方已将您的消息屏蔽，您的消息已被拦截。");
                    newerrormessage.setConversationID(errorMessage.getConversationID());
                    newerrormessage.setFromID(errorMessage.getFromID());
                    newerrormessage.setToID(errorMessage.getToID());
                    newerrormessage.setDirection(IMMessage.DIRECTION_MIDDLE);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Text, newerrormessage);
                }
                break;
        }
    }

    @Override
    public void onSocketConnected() {
//        try {
//            IMNotificaitonCenter.getInstance().postNotificationName(QtalkEvent.LOGIN_EVENT, true, "login");
//        } catch (Exception e) {
//            Logger.e(e, "onChatMessageReceived failed {}", "login");
//        }
    }

    @Override
    public void onStreamDidAuthenticate() {
        Logger.i("设置_pbSocket LoginStatus为true");
        _pbSocket.setLoginStatus(true);
        _pbSocket.setConnecting(false);
//        long lastMsgTime = IMDatabaseManager.getInstance().getLastestMessageTime();
//        List tempList = IMDatabaseManager.getInstance().getGroupListMsgMaxTime();
//        UserPresenceManager.getInstance().update();

        //通知界面更新已连接
//        Logger.i("通知界面更新UI:已连接");
//        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.LOGIN_EVENT, LoginStatus.Login);
        //登陆全部完成 开始发送心跳包
        //// TODO: 2017/9/7 开始监测网络发心跳包
        Logger.i("开启网络监听,发送心跳包");
        getCurrentNetDBM(CommonConfig.globalContext, true);
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LoginComplateManager.loginComplate();
            }
        });
    }

    public boolean isLoginStatus() {
        return _pbSocket.isLoginStatus();
    }

    public boolean isConnected() {
        return _pbSocket.isConnected();
    }

    public void setLoginStatus(boolean b) {
        _pbSocket.setLoginStatus(b);
    }

    public XMPPJID getMyself() throws NullPointerException {

        if (_pbSocket.getMyJID() == null) {
            String str = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext, Constants.Preferences.lastMySelf);
            Logger.i("出现myself为null的情况,重新创建,数据源:" + str);
            _pbSocket.setMyJID(JsonUtils.getGson().fromJson(str, XMPPJID.class));

        }
        return _pbSocket.getMyJID();
    }

    public void logout(String userName) {
        Logger.i("登出");
        _pbSocket.shutdown();

    }

    private static Timer timer = new Timer();
    private HeartBeatTimerTask heartBeatTimerTask = null;

    public void startHeartBeat(long period) {
        if (heartBeatTimerTask != null) {
            heartBeatTimerTask.cancel();
        }
        timer.purge();
        heartBeatTimerTask = new HeartBeatTimerTask();
        timer.schedule(heartBeatTimerTask, 3 * 1000, period);

    }

    public class HeartBeatTimerTask extends TimerTask {

        @Override
        public void run() {
            if (_pbSocket == null || !_pbSocket.isLoginStatus()) {
                cancel();
//                timer.purge();
//                pingTimer.cancel();
                return;
            }

            try {
                Logger.i("发送心跳包:");
                sendHeartMessage(PbAssemblyUtil.getHeartBeatMessage());
//                sendMessage(PbAssemblyUtil.getHeartBeatMessage());
            } catch (Exception e) {
                Logger.i("心跳包的异常:" + e);
                cancel();
                timer.purge();
//                pingTimer.cancel();
            }
        }
    }

    private Handler connectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Logger.i("触发了重连");
            if (_pbSocket != null)
                _pbSocket.shutdown(false);
            QtalkSDK.getInstance().login(false);
        }
    };

    private static final int WHAT = 0xff;//handler message标记

    public void wakeup() {
        if (_pbSocket != null) {
            _pbSocket.selectorWakup();
        }
    }

    public void setForceConnect() {
        if (_pbSocket != null) {
            _pbSocket.setForceConnect();
        }
    }

    public boolean isForceConnect() {
        if (_pbSocket != null) {
            return _pbSocket.isForceConnect();
        }
        return false;
    }

    public void sendHeartMessage(final ProtoMessageOuterClass.ProtoMessage protoMessage) {
        DispatchHelper.Async("sendHeart", false, new Runnable() {
            @Override
            public void run() {
                connectedHandler.sendEmptyMessageDelayed(WHAT, 40 * 1000);
                sendMessage(protoMessage);
            }
        });
    }


    static long lastTime = 0;

    /**
     * 得到当前的手机蜂窝网络信号强度
     * 获取LTE网络和3G/2G网络的信号强度的方式有一点不同，
     * LTE网络强度是通过解析字符串获取的，
     * 3G/2G网络信号强度是通过API接口函数完成的。
     * asu 与 dbm 之间的换算关系是 dbm=-113 + 2*asu
     */
    public void getCurrentNetDBM(final Context context, final boolean b) {
        final boolean[] start = {b};
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener mylistener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                try {
                    int lte_rsrp = (Integer) signalStrength.getClass().getMethod("getLteRsrp").invoke(signalStrength);
//                    Logger.i("这个是当前dBm值:" + lte_rsrp);
                    long time = 0;
                    if (0 > lte_rsrp && lte_rsrp >= -50) {
//                        Logger.i("这个是当前dBm值:好的网络" + lte_rsrp);
                        //这个区间可以证明网络超级好
                        time = 60 * 1000;
                    } else if (-50 > lte_rsrp && lte_rsrp >= -90) {
                        //这个区间可以证明网络情况正常
//                        Logger.i("这个是当前dBm值:一般的网络" + lte_rsrp);
                        time = 30 * 1000;
                    } else {
                        //这会手机网络应该不会是满格,不会太好
//                        Logger.i("这个是当前dBm值:较差的网络" + lte_rsrp);
                        time = 15 * 1000;

                    }
                    //如果网络区间有变动,更新发送心跳间隔
                    if (time != lastTime || start[0]) {
                        Logger.i("从新开启了心跳;时间:" + time);
                        //// TODO: 2017/11/21 暂时注释,此处先不发送心跳消息 正式打包时 需要解开
                        startHeartBeat(time);
                        lastTime = time;
                        start[0] = false;
                    }
                } catch (Exception e) {
                    startHeartBeat(15 * 1000);
                    e.printStackTrace();
                    return;
                }

            }
        };
        //开始监听
        tm.listen(mylistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

}