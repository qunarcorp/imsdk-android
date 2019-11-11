package com.qunar.im.utils;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.LruCache;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.common.QChatRSA;
import com.qunar.im.base.jsonbean.AtInfo;
import com.qunar.im.base.jsonbean.GetDepartmentResult;
import com.qunar.im.base.jsonbean.JSONChatHistorys;
import com.qunar.im.base.jsonbean.JSONMucHistorys;
import com.qunar.im.base.jsonbean.NewRemoteConfig;
import com.qunar.im.base.jsonbean.NotificationConfig;
import com.qunar.im.base.jsonbean.QuickReplyResult;
import com.qunar.im.base.jsonbean.RNSearchData;
import com.qunar.im.base.jsonbean.RemoteConfig;
import com.qunar.im.base.module.CollectionConversation;
import com.qunar.im.base.module.DepartmentItem;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.IMGroup;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.MedalListResponse;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.QuickReplyData;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.module.UserConfigData;
import com.qunar.im.base.module.UserHaveMedalStatus;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.module.WorkWorldSingleResponse;
import com.qunar.im.base.protocol.LoginAPI;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.shortutbadger.ShortcutBadger;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.manager.IMPayManager;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.other.CacheDataType;
import com.qunar.im.other.IQTalkLoginDelegate;
import com.qunar.im.other.QtalkSDK;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.LoginType;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.protobuf.entity.XMPPJID;
import com.qunar.im.protobuf.stream.PbAssemblyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by hubin on 2017/8/8.
 * pb连接管理类
 */


public class ConnectionUtil {
    public static ConnectionUtil instance = null;
    public QtalkSDK qtalkSDK;


    public static String defaultUserImage = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";


    private ConnectionUtil() {
        qtalkSDK = QtalkSDK.getInstance();
    }

    /**
     * 获取实例,在此处应该传入Applactioncontext 防止内存泄漏
     *
     * @return
     */

    public static ConnectionUtil getInstance() {
        if (instance == null) {
            synchronized (ConnectionUtil.class) {
                if (instance == null) {
                    instance = new ConnectionUtil();
                }
            }
        }
        return instance;
    }

    public static void setInitialized(boolean initialized) {
        IMDatabaseManager.getInstance().setInitialized(initialized);
    }

    //获取短信验证码接口
    public void takeSmsCode(final String userName, final IQTalkLoginDelegate delegate) {
        qtalkSDK.takeSmsCode(userName, delegate);
    }

    //清空短信登录信息
    public void clearSmsCode() {
        qtalkSDK.clearSmscode();
    }

    public String getMarkupNameById(String xmppid){
        String name =  CurrentPreference.getInstance().getMarkupNames().get(xmppid);
        if(TextUtils.isEmpty(name)){
            name = selectMarkupNameById(xmppid);
        }
        return name;
    }
    /**
     * 初始化导航
     *
     * @return
     * @param isForce
     */
    public void initNavConfig(boolean isForce) {
        qtalkSDK.initNavConfig(isForce);
    }

    //根据导航地址配置获取登陆类型
    public LoginType getLoginType() {
        return QtalkNavicationService.getInstance().getLoginType();
    }

    //添加到event
    public void addEvent(IMNotificaitonCenter.NotificationCenterDelegate delegate, String key) {
        qtalkSDK.addEvent(delegate, key);
    }

    //删除event
    public void removeEvent(IMNotificaitonCenter.NotificationCenterDelegate delegate, String key) {
        qtalkSDK.removeEvent(delegate, key);
    }

    //发送event
    public void sendEvent(String key,Object... objects){
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.PAY_SUCCESS, objects);
    }

    /**
     * 是否是热线账号
     * @param jid
     * @return
     */
    public boolean isHotline(String jid) {
        List<String> hotlines = CurrentPreference.getInstance().getHotLineList();
        if(hotlines == null){
            hotlines = IMDatabaseManager.getInstance().searchHotlines();
            if(hotlines == null) {
                return false;
            }else {
                CurrentPreference.getInstance().setHotLineList(hotlines);
            }
        }

        return hotlines.contains(jid);
    }

    /**
     * 我是否是热线客服
     * @return
     */
    public boolean isHotlineMerchant(String hotlinejid) {
        List<String> myholines = CurrentPreference.getInstance().getMyHotlines();
        if(myholines != null && myholines.contains(hotlinejid)) {
            return true;
        }
        return false;
    }

    public void reConnection() {
        IMLogicManager.getInstance().reConnection();
    }

    public void reConnectionForce(){
        IMLogicManager.getInstance().reConnectionForce();
    }

    public void shutdown() {
        IMLogicManager.getInstance().shutdown();
    }

//    //设置登陆状态
//    public void setLoginStatus(boolean b) {
//        QtalkSDK.getInstance().setLoginStatus(b);
//    }

    //pb协议登陆接口
    public void pbLogin(String username, String password, boolean isPublic) {
        //登录之前先登出
        pbLogout();
        CurrentPreference.getInstance().setUserid(username);
        initNavConfig(true);
        setInitialized(false);
        if (isPublic) {
            qtalkSDK.publicLogin(username, password);
        } else {
            qtalkSDK.login(username, password);
        }
    }

    /**
     * 新登录 通过接口获取token
     * @param username
     * @param password
     */
    public void pbLoginNew(final String username, String password){
        //登录之前先登出
        pbLogout();
        CurrentPreference.getInstance().setUserid(username);
        initNavConfig(true);
        setInitialized(false);
        try {
            password = QChatRSA.QTalkEncodePassword(password);
        } catch (Exception e) {
            Logger.e("QChatRSAError:" + e.getLocalizedMessage());
        }
        LoginAPI.getNewLoginToken(username, password, new ProtocolCallback.UnitCallback<String[]>() {
            @Override
            public void onCompleted(String[] ss) {
                if(ss != null && ss.length > 1){
                    qtalkSDK.newLogin(ss[0],ss[1]);
                }else {
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.LOGIN_FAILED, -99);
                }
            }

            @Override
            public void onFailure(String errMsg) {
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.LOGIN_FAILED, -99);
            }
        });
    }

    /**
     * 删除会话内消息
     * @param xmppId
     */
    public void deleteIMmessageByXmppId(String xmppId){
        IMDatabaseManager.getInstance().deleteIMmessageByXmppId(xmppId);
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.CLEAR_MESSAGE,xmppId);
    }

    /**
     * pb 退出登录
     */
    public void pbLogout() {
        qtalkSDK.logout("");
    }

    public void pbAutoLogin() {
        qtalkSDK.login(false);
    }

    //获取当前连接状态
    public boolean isConnected() {
        return qtalkSDK.isConnected();
    }

    //获取当前登陆状态
    public boolean isLoginStatus() {
        return qtalkSDK.isLoginStatus();
    }

    //得到当前是否可以自动登录返回
    public boolean isCanAutoLogin() {
        return qtalkSDK.needTalkSmscode();
    }


    //--------------------------------------------xmpp消息-------------------------------------------

    /**
     * 暂时注释掉 这个是发送特殊通知 被弃用
     * @param imMessage
     */
//    public void sendSpecialNoticeMessage(String target,String key,String value){
//        String from = CurrentPreference.getInstance().getPreferenceUserId();
//        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getSpecialNoticeMessage(from,from,key,value);
//        qtalkSDK.sendMessage(protoMessage);
//    }

    //发送正在发送状态消息
    public void sendTypingStatus(IMMessage imMessage) {
        //生成对应的protoMessage
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getTypingStatusMessage(imMessage);
        //发送正在输入状态消息
        qtalkSDK.sendMessage(protoMessage);

    }

    //发送普通消息
    public void sendTextOrEmojiMessage(final IMMessage imMessage) {
        DispatchHelper.Async("sendMessage",false, new Runnable() {
            @Override
            public void run() {
                //生成相对应的protoMessage
                ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getTextOrEmojiMessage(imMessage);
                //把发送的信息放到数据库 im_message表中
                IMDatabaseManager.getInstance().InsertChatMessage(imMessage, false);
                //IMDatabaseManager.getInstance().InsertChatMessage(protoMessage, "2", "1");
                //把发送的数据,放到im_sessionList表中
                IMDatabaseManager.getInstance().InsertIMSessionList(imMessage, false);
                //IMDatabaseManager.getInstance().InsertSessionList(protoMessage);
                qtalkSDK.sendMessageSync(protoMessage);
            }
        });
    }

    public void sendTransMessage(IMMessage imMessage) {
        //生成相对应的protoMessage
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getTransMessage(imMessage);
        qtalkSDK.sendMessage(protoMessage);
    }

    public void sendErrorMessage(String userid,String str){
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getTextErrorMessage(userid,str);

        qtalkSDK.sendMessage(protoMessage);
    }


    //发送加密信令消息
    public void sendEncryptSignalMessage(IMMessage imMessage) {
        //生成相对应的protoMessage
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getEncryptSignalMessage(imMessage);
//        //把发送的信息放到数据库 im_message表中
//        IMDatabaseManager.getInstance().InsertChatMessage(protoMessage, "2", "1");
//        //把发送的数据,放到im_sessionList表中
//        IMDatabaseManager.getInstance().InsertSessionList(protoMessage);
        qtalkSDK.sendMessage(protoMessage);
    }

    //公众号消息
    public void sendSubscriptionMessage(IMMessage imMessage) {
        //生成相对应的protoMessage
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getSubscriptionMessage(imMessage);
        //把发送的信息放到数据库 IM_Public_Number_Message表中
        IMDatabaseManager.getInstance().InsertPublicNumberMessage(imMessage);
        //把发送的数据,放到im_sessionList表中
//        IMDatabaseManager.getInstance().InsertIMSessionList(imMessage);
        qtalkSDK.sendMessage(protoMessage);
    }
    //webrtc
    public void sendWebrtcMessage(IMMessage imMessage){
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getWebrtcMessage(imMessage);
        qtalkSDK.sendMessage(protoMessage);
    }

    //发送撤销消息
    public void sendRevokeMessage(IMMessage imMessage) {
        //拼接pb
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getRevokeMessage(imMessage);
        //更新数据库消息
        IMDatabaseManager.getInstance().UpdateRevokeChatMessage(imMessage.getId(), imMessage.getNick().getName() + "撤回了一条消息", imMessage.getMsgType());
        //发送消息
        qtalkSDK.sendMessage(protoMessage);
    }


    //发送群组消息
    public void sendGroupTextOrEmojiMessage(IMMessage imMessage) {
        if (TextUtils.isEmpty(imMessage.getFromID())) {
            imMessage.setFromID(CurrentPreference.getInstance().getPreferenceUserId());
        }
        //生成相对应的protoMessage
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getGroupTextOrEmojiMessage(imMessage);
        //把发送的信息放到数据库 im_message表中
//        IMDatabaseManager.getInstance().InsertSendGroupChatMessage(protoMessage, "2", "1");
        IMDatabaseManager.getInstance().InsertChatMessage(imMessage, false);
        //把发送的数据,放到im_sessionList表中
//        IMDatabaseManager.getInstance().InsertIMSessionList(imMessage);
        IMDatabaseManager.getInstance().InsertSessionList(protoMessage);
        qtalkSDK.sendMessage(protoMessage);
        //TODO 没明白自己发的消息还要告诉服务器设置已读？？？ 暂时注释
//        sendGroupAllRead(imMessage.getToID());
    }

    public Nick getMyselfCard(String jid) {
        XMPPJID target = XMPPJID.parseJID(jid);
        return JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectUserByJID(target.fullname()).toString(), Nick.class);
    }

    //查询名片系统
    public void getUserCard(String jid, IMLogicManager.NickCallBack nickCallBack, boolean enforce, boolean toDB) {
        XMPPJID target = XMPPJID.parseJID(jid);
        IMLogicManager.getInstance().getUserInfoByUserId(target, IMLogicManager.getInstance().getMyself(), enforce, toDB, nickCallBack);
    }

    public List<UserHaveMedalStatus>  getUserMedalList(String xmppId){
        return IMLogicManager.getInstance().getUserMedalList(xmppId);
    }

    //查询名片系统
    public void getUserCard(String jid, IMLogicManager.NickCallBack nickCallBack) {
        XMPPJID target = XMPPJID.parseJID(jid);
        IMLogicManager.getInstance().getUserInfoByUserId(target, nickCallBack);
    }

    public Nick testgetnick(String jid){
        return IMLogicManager.getInstance().getNickkk(jid);
    }

    public LruCache<String,String> selectMarkupNames(){
           return    IMDatabaseManager.getInstance().selectMarkupNames();
    }

    public String selectMarkupNameById(String xmppid){
        return  IMDatabaseManager.getInstance().selectMarkupNameById(xmppid);
    }

    public void getCollectionUserCard(final String jid, final IMLogicManager.NickCallBack nickCallBack, final boolean enforce, final boolean toDB) {
//        DispatchHelper.Async("getCollectionuserAsync", true, new Runnable() {
//            @Override
//            public void run() {
        XMPPJID target = XMPPJID.parseJID(jid);
        IMLogicManager.getInstance().getCollectionUserInfoByUserId(target, enforce, toDB, nickCallBack);
//            }
//        });

    }

    public void getCollectionMucCard(final String jid, final IMLogicManager.NickCallBack nickCallBack, final boolean enforce, final boolean toDB) {
//        DispatchHelper.Async("getCollectionmucAsync", true, new Runnable() {
//            @Override
//            public void run() {
        XMPPJID target = XMPPJID.parseJID(jid);
        IMLogicManager.getInstance().getCollectionMucInfoByGroupId(target, enforce, toDB, nickCallBack);
//            }
//        });
    }


    //查询名片系统(根据id取单人 本地)
    public Nick getNickById(String jid) {
        XMPPJID target = XMPPJID.parseJID(jid);
        return IMLogicManager.getInstance().getNickById(target);
    }

    public void updateUserImage(String jid,String url){
        IMDatabaseManager.getInstance().updateUserImage(jid,url);
    }


    //查询名片系统(根据id取群 本地)
    public Nick getMucNickById(String id) {
        return JsonUtils.getGson().fromJson(IMDatabaseManager.getInstance().selectMucByGroupId(id).toString(), Nick.class);
    }

    public boolean checkGroupByJid(String jid){
        return IMDatabaseManager.getInstance().checkGroupByJid(jid);
    }


    //查询群成员根据GroupId
    public List<GroupMember> SelectGroupMemberByGroupId(String groupId) {
        return IMDatabaseManager.getInstance().SelectGroupMemberByGroupId(groupId);
    }

    //查询指定人员在指定群的管理权限
    public int selectGroupMemberPermissionsByGroupIdAndMemberId(String groupId,String memberId){
        return IMDatabaseManager.getInstance().selectGroupMemberPermissionsByGroupIdAndMemberId(groupId,memberId);
    }

    /**
     * 清空所有的会话
     */
    public void DeleteSessionList(){
        IMDatabaseManager.getInstance().DeleteSessionList();
    }

    /**
     * 获取所有组织架构人员
     * @return
     */
    public List<GetDepartmentResult.UserItem> getAllOrgaUsers(){
        return IMDatabaseManager.getInstance().getAllOrgaUsers();
    }


    //查询群名片
    public void getMucCard(String jid, IMLogicManager.NickCallBack nickCallBack, boolean enforce, boolean toDB) {
        XMPPJID target = XMPPJID.parseJID(jid);
        IMLogicManager.getInstance().getMucInfoByGroupId(target, IMLogicManager.getInstance().getMyself(), enforce, toDB, nickCallBack);
    }

    public List<Nick> SelectFriendList() {
        return IMDatabaseManager.getInstance().SelectFriendList();
    }

    public List<Nick> SelectFriendListForRN(){
        return IMDatabaseManager.getInstance().SelectFriendListForRN();
    }

    public boolean isMyFriend(String XmppId){
        return IMDatabaseManager.getInstance().isFriend(XmppId);
    }

    public List<IMMessage> searchVoiceMsg(String convid, long t, int msgType) {
        return IMDatabaseManager.getInstance().searchVoiceMsg(convid, t, msgType);
    }

    public List<IMMessage> searchEncryptMsg(String convid, long t) {
        return IMDatabaseManager.getInstance().searchMessageByMsgType(convid, t, ProtoMessageOuterClass.MessageType.MessageTypeEncrypt_VALUE);
    }

    public Nick getUserCardByName(String name) {
        return IMDatabaseManager.getInstance().selectUserByName(name);
    }


    public List<IMMessage> SelectHistoryCollectionChatMessage(String of, String ot, String chatType, int count, int size) {
        List<IMMessage> list = SelectInitReloadCollectionChatMessage(of, ot, chatType, count, size);
        if (list != null && list.size() > 0) {
            return list;
        } else {
            List<IMMessage> noMessage = createNoMessage();
            return noMessage;
        }

    }

    /**
     * 查询单聊历史消息 如果数据库有 从数据库查,如果数据库没有  从网络查
     *
     * @param xmppid
     * @param count
     * @param size
     * @param historyMessage
     */
    public void SelectHistoryChatMessage(String chattype, String xmppid, String realJid, int count, int size, final HistoryMessage historyMessage) {
        List<IMMessage> messageList;
        long time;
        if ((ConversitionType.MSG_TYPE_CONSULT_SERVER + "").equals(chattype)) {
            messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, realJid, count, size);
            time = IMDatabaseManager.getInstance().getFirstMessageTimeByXmppIdAndRealJid(xmppid, realJid);
        } else if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chattype)) {
            messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, xmppid, count, size);
            time = IMDatabaseManager.getInstance().getFirstMessageTimeByXmppIdAndRealJid(xmppid, xmppid);
        } else {
            messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, realJid, count, size);
            time = IMDatabaseManager.getInstance().getFirstMessageTimeByXmppIdAndRealJid(xmppid, realJid);
        }


//        List<IMMessage> messageList = IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid,realJid, count, size);
        if (messageList.size() > 0) {
            historyMessage.onMessageResult(messageList);
        } else {
            if ((ConversitionType.MSG_TYPE_CONSULT_SERVER + "").equals(chattype)) {
                HttpUtil.getJsonConsultChatOfflineMsg(chattype, xmppid, realJid, time, count, size, 0, true, false, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
                    @Override
                    public void onCompleted(List<IMMessage> messageList) {
                        if (messageList != null) {
                            historyMessage.onMessageResult(messageList);
                        } else {
                            List<IMMessage> noMessage = createNoMessage();
                            historyMessage.onMessageResult(noMessage);
                        }
                    }

                    @Override
                    public void onFailure(String errMsg) {

                    }
                });
            } else if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chattype)) {
                HttpUtil.getJsonSingleChatOfflineMsg(chattype, xmppid, realJid, time, count, size, true, 0, false, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
                    @Override
                    public void onCompleted(List<IMMessage> messageList) {
                        if (messageList != null) {
                            historyMessage.onMessageResult(messageList);
                        } else {
                            List<IMMessage> noMessage = createNoMessage();
                            historyMessage.onMessageResult(noMessage);
                        }

                    }

                    @Override
                    public void onFailure(String errMsg) {

                    }
                });
            } else {
                HttpUtil.getJsonSingleChatOfflineMsg(chattype, xmppid, realJid, time, count, size, true, 0, false, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
                    @Override
                    public void onCompleted(List<IMMessage> messageList) {
                        if (messageList != null) {
                            historyMessage.onMessageResult(messageList);
                        } else {
                            List<IMMessage> noMessage = createNoMessage();
                            historyMessage.onMessageResult(noMessage);
                        }

                    }

                    @Override
                    public void onFailure(String errMsg) {

                    }
                });
//                HttpUtil.getSingleChatOfflineMsg(chattype, xmppid, realJid, time, count, size, 0, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
//                    @Override
//                    public void onCompleted(List<IMMessage> messageList) {
//                        if (messageList != null) {
//                            historyMessage.onMessageResult(messageList);
//                        } else {
//                            List<IMMessage> noMessage = new ArrayList<IMMessage>();
//                            IMMessage imMessage = new IMMessage();
//                            String uid = UUID.randomUUID().toString();
//                            imMessage.setId(uid);
//                            imMessage.setMessageID(uid);
//                            imMessage.setDirection(2);
//                            imMessage.setBody("没有更多消息了");
//                            noMessage.add(imMessage);
//                            historyMessage.onMessageResult(noMessage);
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure() {
//
//                    }
//                });
            }

        }
//        return  messageList;
    }

    /**
     * 查询群聊历史数据,先从数据库查,如果数据库没有,从网络查
     *
     * @param xmppid
     * @param count
     * @param size
     * @param historyMessage
     * @return
     */
    public void SelectHistoryGroupChatMessage(String xmppid, String realJid, int count, int size, final HistoryMessage historyMessage) {
        List<IMMessage> list = IMDatabaseManager.getInstance().SelectHistoryGroupChatMessage(xmppid, realJid, count, size);
        if (list.size() > 0) {
            historyMessage.onMessageResult(list);
        } else {
            long time = IMDatabaseManager.getInstance().getFirstMessageTimeByXmppIdAndRealJid(xmppid, realJid);

            HttpUtil.getJsonMultiChatOffLineMsg(xmppid, realJid, time, count, size, 0, true, false, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
                @Override
                public void onCompleted(List<IMMessage> messageList) {
                    if (messageList != null) {
                        historyMessage.onMessageResult(messageList);
                    } else {
                        List<IMMessage> noMessage = createNoMessage();
                        historyMessage.onMessageResult(noMessage);
                    }
                }

                @Override
                public void onFailure(String errMsg) {

                }
            });
        }

    }

    /**
     * 单人不保存本地数据库拉取数据
     * @param xmppid
     * @param realJid
     * @param count
     * @param size
     * @param time
     * @param direction
     * @param isInclude
     * @param historyMessage
     */
    public void SelectHistoryChatMessageForNet(String xmppid, String realJid, int count, int size, long time,int direction,boolean isInclude,String chattype,final HistoryMessage historyMessage){
        if ((ConversitionType.MSG_TYPE_CONSULT_SERVER + "").equals(chattype)) {
            HttpUtil.getJsonConsultChatOfflineMsg(chattype, xmppid, realJid, time, count, size, direction, false, isInclude, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
                @Override
                public void onCompleted(List<IMMessage> messageList) {
                    if (messageList != null) {
                        historyMessage.onMessageResult(messageList);
                    } else {
                        List<IMMessage> noMessage = createNoMessage();
                        historyMessage.onMessageResult(noMessage);
                    }
                }

                @Override
                public void onFailure(String errMsg) {

                }
            });
        } else if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chattype)) {
            HttpUtil.getJsonSingleChatOfflineMsg(chattype, xmppid, realJid, time, count, size, false, direction, isInclude, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
                @Override
                public void onCompleted(List<IMMessage> messageList) {
                    if (messageList != null) {
                        historyMessage.onMessageResult(messageList);
                    } else {
                        List<IMMessage> noMessage = createNoMessage();
                        historyMessage.onMessageResult(noMessage);
                    }

                }

                @Override
                public void onFailure(String errMsg) {

                }
            });
        } else {
            HttpUtil.getJsonSingleChatOfflineMsg(chattype, xmppid, realJid, time, count, size, false, direction, isInclude, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
                @Override
                public void onCompleted(List<IMMessage> messageList) {
                    if (messageList != null) {
                        historyMessage.onMessageResult(messageList);
                    } else {
                        List<IMMessage> noMessage = createNoMessage();
                        historyMessage.onMessageResult(noMessage);
                    }

                }

                @Override
                public void onFailure(String errMsg) {

                }
            });
//                HttpUtil.getSingleChatOfflineMsg(chattype, xmppid, realJid, time, count, size, 0, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
//                    @Override
//                    public void onCompleted(List<IMMessage> messageList) {
//                        if (messageList != null) {
//                            historyMessage.onMessageResult(messageList);
//                        } else {
//                            List<IMMessage> noMessage = new ArrayList<IMMessage>();
//                            IMMessage imMessage = new IMMessage();
//                            String uid = UUID.randomUUID().toString();
//                            imMessage.setId(uid);
//                            imMessage.setMessageID(uid);
//                            imMessage.setDirection(2);
//                            imMessage.setBody("没有更多消息了");
//                            noMessage.add(imMessage);
//                            historyMessage.onMessageResult(noMessage);
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure() {
//
//                    }
//                });
        }
    }

    /**
     * 群不加载网络数据拉取历史记录
     * @param xmppid
     * @param realJid
     * @param count
     * @param size
     * @param time
     * @param direction
     * @param isInclude
     * @param historyMessage
     */
    public void SelectHistoryGroupChatMessageForNet(String xmppid, String realJid, int count, int size, long time,int direction,boolean isInclude,final HistoryMessage historyMessage){
        HttpUtil.getJsonMultiChatOffLineMsg(xmppid, realJid, time, count, size, direction, false, isInclude, new ProtocolCallback.UnitCallback<List<IMMessage>>() {
            @Override
            public void onCompleted(List<IMMessage> messageList) {
                if (messageList != null) {
                    historyMessage.onMessageResult(messageList);
                } else {
                    List<IMMessage> noMessage = createNoMessage();
                    historyMessage.onMessageResult(noMessage);
                }
            }

            @Override
            public void onFailure(String errMsg) {

            }
        });
    }


    /**
     * 插入单聊历史数据,根据json
     *
     * @param list
     * @param selfId
     */
    public   List<IMMessage> ParseHistoryChatData(List<JSONChatHistorys.DataBean> list, String selfId) {
          List<IMMessage> messageList = new ArrayList<>();
        try {

            int size = list.size();
            for (int i = 0; i < size; ++i) {
                IMMessage imMessage = new IMMessage();
                try {

                    JSONChatHistorys.DataBean data = list.get(i);
                    JSONChatHistorys.DataBean.BodyBean body = data.getBody();
                    JSONChatHistorys.DataBean.MessageBean message = data.getMessage();
                    JSONChatHistorys.DataBean.TimeBean time = data.getTime();
                    if (data == null || body == null || message == null || time == null) {
                        continue;
                    }
                    String from = TextUtils.isEmpty(QtalkStringUtils.parseIdAndDomain(message.getFrom())) ? data.getFrom() + "@" + data.getFrom_host() : QtalkStringUtils.parseIdAndDomain(message.getFrom());
                    String to = TextUtils.isEmpty(QtalkStringUtils.parseIdAndDomain(message.getTo())) ? data.getTo() + "@" + data.getTo_host() : QtalkStringUtils.parseIdAndDomain(message.getTo());
                    String ofrom = QtalkStringUtils.parseIdAndDomain(message.getOriginfrom());
                    String oto = QtalkStringUtils.parseIdAndDomain(message.getOriginto());
                    String realFrom = TextUtils.isEmpty(QtalkStringUtils.parseIdAndDomain(message.getRealjid())) ? QtalkStringUtils.parseIdAndDomain(message.getRealfrom()) : QtalkStringUtils.parseIdAndDomain(message.getRealjid());
                    String realTo = QtalkStringUtils.parseIdAndDomain(message.getRealto());
                    //消息表数据绑定

                    String msgId = body.getId();

//                    isstat.bindString(3, from.equals(selfId) ? to : from);
                    imMessage.setFromID(from.equals(selfId) ? to : from);

//                    imstat.bindString(1, body.getId());
                    imMessage.setId(body.getId());
                    imMessage.setMessageID(body.getId());
//                    imstat.bindString(2, from.equals(selfId) ? to : from);//普通情况me的xmppid
                    imMessage.setConversationID(from.equals(selfId) ? to : from);
//                    imstat.bindString(3, from);
                    imMessage.setFromID(from);
//                    imstat.bindString(4, to);
                    imMessage.setToID(to);
//                    imstat.bindString(5, body.getContent());
                    imMessage.setBody(body.getContent());
                    if (!TextUtils.isEmpty(message.getClient_type())) {
                        switch (message.getClient_type()) {
                            case "ClientTypeMac":
//                                imstat.bindString(6, 1 + "");
                                imMessage.setMaType(1+"");
                                break;
                            case "ClientTypeiOS":
//                                imstat.bindString(6, 2 + "");
                                imMessage.setMaType(2+"");
                                break;
                            case "ClientTypePC":
//                                imstat.bindString(6, 3 + "");
                                imMessage.setMaType(3+"");
                                break;
                            case "ClientTypeAndroid":
//                                imstat.bindString(6, 4 + "");
                                imMessage.setMaType(4+"");
                                break;
                            default:
//                                imstat.bindString(6, 0 + "");
                                imMessage.setMaType(0+"");
                                break;
                        }
                    } else if (!TextUtils.isEmpty(body.getMaType())) {
//                        imstat.bindString(6, body.getMaType());
                        imMessage.setMaType(body.getMaType());
                    } else {
//                        imstat.bindString(6, 0 + "");
                        imMessage.setMaType(0+"");
                    }
                    String msgType = body.getMsgType();
//                    imstat.bindString(7, msgType);
                    imMessage.setMsgType(Integer.parseInt(msgType));

//                    imstat.bindString(8, String.valueOf(MessageStatus.LOCAL_STATUS_SUCCESS));
                    imMessage.setMessageState(MessageStatus.LOCAL_STATUS_SUCCESS);

                    if ("-1".equals(body.getMsgType())
                            || "15".equals(body.getMsgType())
                            || (ProtoMessageOuterClass.MessageType.MessageTypeRobotTurnToUser_VALUE + "").equals(body.getMsgType())) {
//                        imstat.bindString(9, "2");
                        imMessage.setDirection(2);
                    } else {
//                        imstat.bindString(9, from.equals(selfId) ? "1" : "0");
                        imMessage.setDirection(from.equals(selfId) ? 1 : 0);
                    }
                    String t = "";
                    if (TextUtils.isEmpty(message.getMsec_times())) {
                        String d = time.getStamp();
                        String str = "yyyyMMdd'T'HH:mm:ss";
                        SimpleDateFormat sdf = new SimpleDateFormat(str);
                        TimeZone timeZone = TimeZone.getTimeZone("GMT");
                        sdf.setTimeZone(timeZone);
                        Date date = null;
                        try {
                            if (TextUtils.isEmpty(d)) {
                                date = new Date();
                            } else {
                                date = sdf.parse(d, new ParsePosition(0));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        t = date.getTime() + "";
//                        stat.bindString(10, date.getTime() + "");
                    } else {
                        t = message.getMsec_times();
//                        stat.bindString(10, message.getMsec_times());
                    }
//                    imstat.bindString(10, t);
                    imMessage.setTime(new Date(Long.parseLong(t)));

//                    imstat.bindString(11, String.valueOf(data.getRead_flag()));
                    imMessage.setReadState(MessageStatus.REMOTE_STATUS_CHAT_READED);
//                    imstat.bindString(12, JsonUtils.getGson().toJson(data));
//                    imstat.bindString(13, from.equals(selfId) ? to : from);
                    imMessage.setRealfrom( from.equals(selfId) ? to : from);
                    //// TODO: 2017/12/6 少一个强化消息字段
//                    imstat.bindString(14, TextUtils.isEmpty(body.getExtendInfo()) ? "" : body.getExtendInfo());
                    imMessage.setExt( TextUtils.isEmpty(body.getExtendInfo()) ? "" : body.getExtendInfo());

                    //代收表数据绑定
                    if ("collection".equals(message.getType())) {
                        //代收数据方向肯定在左边故写死
//                        isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_COLLECTION));
//                        imstat.bindString(9, "0");
                        imMessage.setDirection(0);
//                        imcstat.bindString(1, body.getId());
//                        imcstat.bindString(2, ofrom);
//                        imcstat.bindString(3, oto);
                        if ("chat".equals(message.getOrigintype())) {
//                            imcstat.bindString(4, 0 + "");
                        } else if ("groupchat".equals(message.getOrigintype())) {
                            //代收群组类型消息 message表from存realJid
//                            imstat.bindString(3, realFrom);
                            imMessage.setFromID(realFrom);
//                            imcstat.bindString(4, 1 + "");
                        }
//                        imcstat.executeInsert();
//                        icustat.bindString(1, oto);
//                        icustat.bindString(2, "1");
//                        icustat.executeInsert();
                    }
                    if ("consult".equals(message.getType())) {

                        if (from.equals(selfId)) {//我发送的的情况
                            if ("5".equals(message.getQchatid())) {//证明我是客服 在进行回复
//                                imstat.bindString(13, realTo);
                                imMessage.setRealfrom(realTo);
//                                isstat.bindString(2, realTo);//consult消息情况下的 sessionlist表的realJid
//                                updatestat.bindString(4, realTo);
//                                isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER));//consult消息情况下 窗口类型
                            } else {//证明我是咨询者,默认咨询者,因为这类用户多
//                                isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_CONSULT));
                            }
                        } else {//我接收的情况
                            if ("5".equals(message.getQchatid())) {//证明我是咨询者
//                                isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_CONSULT));//consult消息情况下 窗口类型
                            } else {//其他情况,证明我是客服,
//                                imstat.bindString(13, realFrom);
                                imMessage.setRealfrom(realFrom);
//                                updatestat.bindString(4, realFrom);
//                                isstat.bindString(2, realFrom);//consult消息情况下的 sessionlist表的realJid
//                                isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER));
                            }
                        }
                    }
                    if ("headline".equals(message.getType())) {
//                        imstat.bindString(2, Constants.SYS.SYSTEM_MESSAGE);
                        imMessage.setConversationID(Constants.SYS.SYSTEM_MESSAGE);
//                        imstat.bindString(3, "history");
                        imMessage.setFromID("history");
//                        imstat.bindString(4, selfId);
                        imMessage.setToID(selfId);
//                        imstat.bindString(9, "0");
                        imMessage.setDirection(0);
//                        imstat.bindString(13, Constants.SYS.SYSTEM_MESSAGE);
                        imMessage.setRealfrom(Constants.SYS.SYSTEM_MESSAGE);
                    }
                    if ("subscription".equals(message.getType())) {
//                        isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_SUBSCRIPT));
                    }

//                    if (!isUpdown) {//不是上翻历史(因为上翻历史更新session的话 会导致最后一条消息&时间展示不对)
//                        int count = updatestat.executeUpdateDelete();
//                        if (count <= 0) {//不存在再插入
//                            isstat.executeInsert();
//                        }
//                    } else {//暂时未处理 可能导致 不在会话列表的会话上翻历史无法在会话列表展示
//
//                    }
//                    long count = imstat.executeInsert();
//                    if (count <= 0 && msgType.equals(String.valueOf(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE))) {//撤销消息 update body
//                        revokeStat.bindString(1, body.getContent());
//                        revokeStat.bindString(2, String.valueOf(IMMessage.DIRECTION_MIDDLE));
//                        revokeStat.bindString(3, String.valueOf(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE));
//                        revokeStat.bindString(4, msgId);
//                        revokeStat.executeUpdateDelete();
//                    }
                } catch (Exception e) {
//                    success = false;
                    continue;
                }

                messageList.add(imMessage);
            }
//            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "bulkInsertMessage crashed.");
            throw e;
        } finally {
//            db.endTransaction();
        }
        return messageList;
    }

    public List<MedalListResponse.DataBean.MedalListBean> selectMedalList(){
        return IMDatabaseManager.getInstance().selectMedalList();
    }

    public List<UserHaveMedalStatus> selectUserHaveMedalStatus(String userid,String host){
        return IMDatabaseManager.getInstance().selectUserHaveMedalStatus(userid,host);
    }

    public List<IMMessage> ParseHistoryGroupChatData(List<JSONMucHistorys.DataBean> list, String selfUser){
        String sql = "insert or ignore into IM_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                "Platform, Type, State, Direction,LastUpdateTime,ReadedTag,MessageRaw,RealJid,ExtendedInfo) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?);";
        List<IMMessage> messageList = new ArrayList<>();
            //遍历最外层
            int size = list.size();
            for (int i = 0; i < size; i++) {
                IMMessage imMessage = new IMMessage();
                Cursor cursor = null;
                try {
                    JSONMucHistorys.DataBean msg = list.get(i);
                    JSONMucHistorys.DataBean.BodyBean body = msg.getBody();
                    JSONMucHistorys.DataBean.MessageBean message = msg.getMessage();
                    JSONMucHistorys.DataBean.TimeBean time = msg.getTime();
                    if (msg == null || body == null || message == null || time == null) {
                        continue;
                    }
                    String msgId = body.getId();

//                    imstat.bindString(1, msgId);
                    imMessage.setId(msgId);
                    imMessage.setMessageID(msgId);
                    imMessage.setType(ConversitionType.MSG_TYPE_GROUP);
//                    imstat.bindString(2, message.getTo());
                    imMessage.setConversationID(message.getTo());

//                    imstat.bindString(3, message.getSendjid());
                    imMessage.setFromID(message.getSendjid());

//                    imstat.bindString(4, "");
                    imMessage.setToID(message.getTo());

//                    imstat.bindString(5, body.getContent());
                    imMessage.setBody(body.getContent());
                    if (!TextUtils.isEmpty(message.getClient_type())) {
                        switch (message.getClient_type()) {
                            case "ClientTypeMac":
//                                imstat.bindString(6, 1 + "");
                                imMessage.setMaType(1+"");
                                break;
                            case "ClientTypeiOS":
//                                imstat.bindString(6, 2 + "");
                                imMessage.setMaType(2+"");
                                break;
                            case "ClientTypePC":
//                                imstat.bindString(6, 3 + "");
                                imMessage.setMaType(3+"");
                                break;
                            case "ClientTypeAndroid":
//                                imstat.bindString(6, 4 + "");
                                imMessage.setMaType(4+"");
                                break;
                            default:
//                                imstat.bindString(6, 0 + "");
                                imMessage.setMaType(0+"");
                                break;
                        }
                    } else if (!TextUtils.isEmpty(body.getMaType())) {
//                        imstat.bindString(6, body.getMaType());
                        imMessage.setMaType(body.getMaType());
                    } else {
//                        imstat.bindString(6, 0 + "");
                        imMessage.setMaType(0+"");
                    }
                    String msgType = body.getMsgType();
//                    imstat.bindString(7, msgType);
                    imMessage.setMsgType(Integer.parseInt(msgType));
                    //批量获取的历史记录,消息状态应该都是正常的
//                    imstat.bindString(8, String.valueOf(MessageStatus.LOCAL_STATUS_SUCCESS_PROCESSION));
                    imMessage.setMessageState(MessageStatus.LOCAL_STATUS_SUCCESS_PROCESSION);
                    //如果真实发送人等于自己,证明是自己发出的
                    //否则是其他人发出,根据这个条件判断方向
                    if (selfUser.equals(message.getRealfrom())) {
//                        imstat.bindString(9, "1");
                        imMessage.setDirection(1);
                    } else {
//                        imstat.bindString(9, "0");
                        imMessage.setDirection(0);
                    }
                    if ("-1".equals(body.getMsgType())) {
//                        imstat.bindString(9, "2");
                        imMessage.setDirection(2);
//                        imstat.bindString(3, message.getFrom());
                        imMessage.setFromID(message.getFrom());
//                        imstat.bindString(5, msg.getNick() + body.getContent());
                        imMessage.setBody(msg.getNick()+body.getContent());
                    }
                    if ("15".equals(body.getMsgType())) {
//                        imstat.bindString(9, "2");
                        imMessage.setDirection(2);
//                        imstat.bindString(3, message.getFrom());
                    }
                    String t = "";
                    if (TextUtils.isEmpty(message.getMsec_times())) {
                        if (time == null || TextUtils.isEmpty(time.getStamp())) {
                            new String();
                        }
                        String d = time.getStamp();
                        String str = "yyyyMMdd'T'HH:mm:ss";
                        SimpleDateFormat sdf = new SimpleDateFormat(str);
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                        Date date = null;
                        try {
                            if (TextUtils.isEmpty(d)) {
                                date = new Date();
                            } else {
                                date = sdf.parse(d);


                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        t = date.getTime() + "";
                    } else {
                        t = message.getMsec_times();
                    }
//                    imstat.bindString(10, t);
                    imMessage.setTime(new Date(Long.parseLong(t)));
                    //登录拉历史插成未读 会话上翻拉历史插已读
//                    imstat.bindString(11, String.valueOf(isUpturn ? MessageStatus.REMOTE_STATUS_CHAT_READED : MessageStatus.REMOTE_STATUS_CHAT_DELIVERED));
                    imMessage.setReadState(MessageStatus.REMOTE_STATUS_CHAT_READED);

//                    imstat.bindString(12, JsonUtils.getGson().toJson(msg));

//                    imstat.bindString(13, message.getTo());
                    imMessage.setRealfrom(message.getSendjid());
                    if (!TextUtils.isEmpty(body.getExtendInfo())) {
//                        imstat.bindString(14, body.getExtendInfo());
                        imMessage.setExt(body.getExtendInfo());
                    } else if (!TextUtils.isEmpty(body.getBackupinfo())) {
//                        imstat.bindString(14, body.getBackupinfo());
                        imMessage.setExt(body.getBackupinfo());
                    } else {
//                        imstat.bindString(14, "");
                        imMessage.setExt("");
                    }




                } catch (Exception e) {
                    continue;

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                messageList.add(imMessage);
            }

            return messageList;
        }


    /**
     * 本地会话搜索 跳转会话 消息展示(chat)
     * @param xmppid
     * @param realJid
     * @param t
     */
    public List<IMMessage> selectChatMessageAfterSearch(String xmppid, String realJid, long t){
        List<IMMessage> list = IMDatabaseManager.getInstance().selectChatMessageAfterSearch(xmppid,realJid,t);
        if(list!=null && !list.isEmpty()){
            return list;
        }else {
            List<IMMessage> noMessage = createNoMessage();
            return noMessage;
        }
    }

    /**
     * 本地会话搜索 跳转会话 消息展示(Group)
     * @param xmppid
     * @param realJid
     * @param t
     */
    public List<IMMessage> selectGroupMessageAfterSearch(String xmppid, String realJid, long t){
        List<IMMessage> list = IMDatabaseManager.getInstance().selectGroupMessageAfterSearch(xmppid,realJid,t);
        if(list!=null && !list.isEmpty()){
            return list;
        }else {
            List<IMMessage> noMessage = createNoMessage();
            return noMessage;
        }
    }

    /**
     * 根据搜索文本查找联系人
     * @param searchText
     * @return
     */
    public List<Nick> SelectUserListBySearchText(String groupId,String searchText){
        return IMDatabaseManager.getInstance().SelectUserListBySearchText(groupId,searchText);
    }

    public List<String> SelectAllUserListBySearchText(String searchText){
        return IMDatabaseManager.getInstance().SelectAllUserXmppIdListBySearchText(searchText);
    }

    /**
     * 群加人 好友点选
     * @param groupId
     * @return
     */
    public List<Nick> selectFriendsForGroupAdd(String groupId){
        return IMDatabaseManager.getInstance().selectFriendListForGroupAdd(groupId);
    }

    /**
     * 群踢人
     * @param groupId
     * @return
     */
    public List<Nick> selectGroupMemberForKick(String groupId){
        return IMDatabaseManager.getInstance().selectGroupMemberForKick(groupId);
    }

    /**
     * 根据groupid&searchindex搜索群成员
     * @param groupId
     * @param searchIndex
     * @return
     */
    public List<Nick> selectMemberFromGroup(String groupId,String searchIndex){
        return IMDatabaseManager.getInstance().selectMemberFromGroup(groupId,searchIndex);
    }


    /**
     * 获取公司组织架构所有联系人
     *
     * @return
     */
    public List<Nick> SelectAllContacts() {

        return IMDatabaseManager.getInstance().SelectAllUserCard();
    }

    /**
     * 获取用户所有群组
     *
     * @return
     */
    public List<Nick> SelectAllGroup() {
        return IMDatabaseManager.getInstance().SelectAllGroupCard();
    }

    /**
     * 获取用户群组根据搜索文字
     * @param searchText
     * @return
     */
    public List<Nick> SelectGroupListBySearchText(String searchText,int limit){
        return IMDatabaseManager.getInstance().SelectGroupListBySearchText(searchText,limit);
    }

    /**
     * 查询组织架构联系人
     *
     * @param ser   中文名或缩写
     * @param limit 查找行数
     * @return
     */
    public List<Nick> SelectContactsByLike(String ser, int limit) {
        return IMDatabaseManager.getInstance().SelectContactsByLike(ser, limit);
    }

    /**
     * 插入qchat组织架构
     *
     * @param ditems
     * @param isReplase
     */
    public void insertQchatOrgDatas(List<DepartmentItem> ditems, boolean isReplase) {
        IMDatabaseManager.getInstance().insertQchatOrgDatas(ditems, isReplase);
    }

    public List<IMGroup> SelectIMGroupByLike(String ser, int limit) {
        return IMDatabaseManager.getInstance().SelectIMGroupByLike(ser, limit);
    }

    /**
     * 更新语音消息已读状态
     *
     * @param message
     */
    public void updateVoiceMessage(IMMessage message) {
        IMDatabaseManager.getInstance().updateVoiceMessage(message);
    }

    /**
     * 删除消息
     *
     * @param message
     */
    public void deleteMessage(IMMessage message) {
        IMDatabaseManager.getInstance().DeleteMessageByMessage(message);
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Delete_Message);
    }

    /**
     * 删除会话
     *
     * @param xmppId
     * @param realUserId
     */
    public void deleteCoversationAndMessage(String xmppId, String realUserId) {
        IMDatabaseManager.getInstance().DeleteSessionAndMessageByXmppId(xmppId, realUserId);
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Remove_Session, xmppId);
    }

    /**
     * RN本地搜索所需要数据
     *
     * @param key
     * @param start
     * @param len
     */
    public List<RNSearchData> getLocalSearch(String key, int start, int len) {

        return IMDatabaseManager.getInstance().getLocalSearch(key, start, len);
    }

    /**
     * 获取本地用户RN用
     *
     * @param key
     * @param start
     * @param len
     * @return
     */
    public List<RNSearchData.InfoBean> getLocalUser(String key, int start, int len) {
        return IMDatabaseManager.getInstance().getLocalUser(key, start, len);
    }

    /**
     * 获取本地群组RN用
     *
     * @param key
     * @param start
     * @param len
     * @return
     */
    public List<RNSearchData.InfoBean> getLocalGroup(String key, int start, int len) {
        return IMDatabaseManager.getInstance().getLocalGroup(key, start, len);
    }

    /**
     * 获取外域群组RN用
     *
     * @param key
     * @param start
     * @param len
     * @return
     */
    public List<RNSearchData.InfoBean> getOutGroup(String key, int start, int len) {
        return IMDatabaseManager.getInstance().getOutGroup(key, start, len);
    }

    public List<CollectionConversation> SelectCollectionConversationList(String xmppid) {
        return IMDatabaseManager.getInstance().SelectCollectionConversationList(xmppid);
    }

    public void sendCollectionAllRead(String of, String ot) {
        IMDatabaseManager.getInstance().updateCollectionRead(of, ot);
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.COLLECTION_CHANGE, "succes");
    }

    /**
     * 获取绑定用户
     */
    public void getBindUser() {
        HttpUtil.getBindUser();
    }

    /**
     * 不可登录时,清除上一次登陆信息
     */
    public static void clearLastUserInfo() {
        Logger.i("清理上次登录信息");
        IMLogicManager.getInstance().clearLastUserInfo();
    }

    /**
     * 根据参数获取状态
     * @param pushName
     */
    public boolean getPushStateBy(int pushName) {
       return IMDatabaseManager.getInstance().getPushStateBy(pushName);
    }

    /**
     * 设置push开关操作
     * @param pushIndex 设置 第几位开关
     * @param state 设置开关状态
     */
    public void setPushState(int pushIndex, int state) {
        IMDatabaseManager.getInstance().setPushState(pushIndex,state);
    }

    /**
     * 将更改过数据的nick放入缓存
     * @param nick
     */
    public void setNickToCache(Nick nick) {
        IMLogicManager.getInstance().setNickToCache(nick);
    }


    /**
     * 查询value 根据 key
     * @param userConfigData
     * @return
     */
    public List<UserConfigData> selectUserConfigValueInString(UserConfigData userConfigData) {
        return IMDatabaseManager.getInstance().selectUserConfigValueInString(userConfigData);
    }

    public List<QuickReplyData> selectQuickReplies() {
        return IMDatabaseManager.getInstance().selectQuickReplies();
    }

    public int selectWorkWorldNotice() {
        return IMDatabaseManager.getInstance().selectWorkWorldNotice();
    }


    public interface HistoryMessage {
        void onMessageResult(List<IMMessage> messageList);
    }


    //查询会话列表
    public List<RecentConversation> SelectConversationList(boolean isOnlyUnRead) {

        return IMDatabaseManager.getInstance().SelectConversationList(isOnlyUnRead);
    }

    //获取sessionmap
    public Map<String,List<AtInfo>> getAtMessageMap(){
        return IMDatabaseManager.getInstance().getAtMessageMap();
    }

    public void initAtMessage(){
        IMDatabaseManager.getInstance().selectAtOwnMessage();
    }
    public String getLastMsg(int type,String msg){
        return IMDatabaseManager.getInstance().getLastMessageText(type,msg);
    }

    public int querryConversationTopCount(){
        return IMDatabaseManager.getInstance().querryConversationTopCount();
    }
    //查询会话缓存表
//    public List<RecentConversation> SelectConversationListCache() {
//
//        return IMDatabaseManager.getInstance().SelectConversationListCache();
//    }



    //查询未读消息条目数
    public int SelectUnReadCount() {
        return IMDatabaseManager.getInstance().SelectUnReadCount();
    }


//    public void setConversationTopSession(RecentConversation rc){
//        int version = IMDatabaseManager.getInstance().selectUserConfigVersion();
//        Logger.i("userconfig-查询版本号:"+version);
//    }


    /**
     * 更新用户配置批量,本方法只更新 isdel 不会更新value数据
     *
     * @param userConfigData
     */
    public void updateUserConfigBatch(UserConfigData userConfigData) {
        IMDatabaseManager.getInstance().updateUserConfigBatch(userConfigData);
    }

    /**
     * 查询用户配置版本
     *
     * @return
     */
    public int selectUserConfigVersion() {
        return IMDatabaseManager.getInstance().selectUserConfigVersion();
    }

    /**
     * 获取最新用户配置后更新本地数据
     *
     * @param newRemoteConfig
     */
    public static void refreshTheConfig(NewRemoteConfig newRemoteConfig) {
        IMDatabaseManager.getInstance().insertUserConfigVersion(newRemoteConfig.getData().getVersion());
        IMDatabaseManager.getInstance().bulkUserConfig(newRemoteConfig);


        for (int i = 0; i < newRemoteConfig.getData().getClientConfigInfos().size(); i++) {
            if (newRemoteConfig.getData().getClientConfigInfos().get(i).getKey().equals(CacheDataType.kCollectionCacheKey)) {
//                ConnectionUtil.getInstance().handleMyEmotion(newRemoteConfig.getData().getClientConfigInfos().get(i));
            } else if(newRemoteConfig.getData().getClientConfigInfos().get(i).getKey().equals(CacheDataType.kMarkupNames)){
                LruCache<String,String> markups = ConnectionUtil.getInstance().selectMarkupNames();
//                Logger.i("initreload map:" + JsonUtils.getGson().toJson(markups));
                CurrentPreference.getInstance().setMarkupNames(markups);
//                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_List);
            } else if(newRemoteConfig.getData().getClientConfigInfos().get(i).getKey().equals(CacheDataType.kStickJidDic)){
                IMDatabaseManager.getInstance().setConversationTopSession(newRemoteConfig.getData().getClientConfigInfos().get(i));

            } else if(newRemoteConfig.getData().getClientConfigInfos().get(i).getKey().equals(CacheDataType.kNoticeStickJidDic)){
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Update_ReMind);
            } else if(newRemoteConfig.getData().getClientConfigInfos().get(i).getKey().equals(CacheDataType.kQuickResponse)) {
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.UPDATE_QUICK_REPLY);
            }
        }
        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_List);
    }
    /**
     * 获取最新快捷回复后更新本地数据
     * @param dataBean
     */
    public static void refreshTheQuickReply(QuickReplyResult.DataBean dataBean) {
//        IMDatabaseManager.getInstance().insertUserConfigVersion(newRemoteConfig.getData().getVersion());
        IMDatabaseManager.getInstance().batchInsertQuickReply(dataBean);

        QuickReplyUtils.getQuickReplies();

        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.UPDATE_QUICK_REPLY);
    }

    /**
     * 缓存热线列表
     * @param hotlines
     */
    public static void cacheHotlines(List<String> hotlines) {
        IMDatabaseManager.getInstance().InsertHotlines(JsonUtils.getGson().toJson(hotlines));
    }


    /**
     * 设置个人配置版本号
     *
     * @param version
     */
    public void insertUserConfigVersion(int version) {
        IMDatabaseManager.getInstance().insertUserConfigVersion(version);
    }
    public UserConfigData selectUserConfigValueForKey(UserConfigData userConfigData){
        return IMDatabaseManager.getInstance().selectUserConfigValueForKey(userConfigData);
    }


    public interface CallBackByUserConfig{
        public void onCompleted();
        public void onFailure();
    }

    public void setConversationReMindOrCancel(final UserConfigData userConfigData, final CallBackByUserConfig callBackByUserConfig){
        final UserConfigData ucd = IMDatabaseManager.getInstance().selectUserConfigValueForKey(userConfigData);
        if(ucd == null){
            userConfigData.setValue(CacheDataType.Y+"");
            userConfigData.setType(CacheDataType.set);
            userConfigData.setIsdel(CacheDataType.N);

        }else{
            userConfigData.setValue(CacheDataType.N+"");
            userConfigData.setType(CacheDataType.cancel);
            userConfigData.setIsdel(CacheDataType.Y);

        }
        HttpUtil.setUserConfig(userConfigData, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
            @Override
            public void onCompleted(NewRemoteConfig newRemoteConfig) {
                if (newRemoteConfig.getData().getClientConfigInfos().size() > 0) {
                    ConnectionUtil.getInstance().refreshTheConfig(newRemoteConfig);
                    callBackByUserConfig.onCompleted();
                }else{
                    callBackByUserConfig.onFailure();
                }
            }

            @Override
            public void onFailure(String errMsg) {
                callBackByUserConfig.onFailure();

            }
        });
    }

    public void setConversationTopOrCancel(final UserConfigData userConfigData, final CallBackByUserConfig callBackByUserConfig) {
        final UserConfigData ucd = IMDatabaseManager.getInstance().selectUserConfigValueForKey(userConfigData);
        //todo 根据判断确认本地是否存在 决定如何操作
        if (ucd == null) {
            UserConfigData.TopInfo topInfo = userConfigData.getTopInfo();
            topInfo.setTopType(CacheDataType.Y+"");
            userConfigData.setValue(topInfo.toJson());
            userConfigData.setType(CacheDataType.set);
            userConfigData.setIsdel(CacheDataType.N);

        } else {

            UserConfigData.TopInfo topInfo = JsonUtils.getGson().fromJson(ucd.getValue(),UserConfigData.TopInfo.class);
            if ((CacheDataType.Y + "").equals(topInfo.getTopType())) {
                topInfo.setTopType(CacheDataType.N + "");
                userConfigData.setType(CacheDataType.cancel);
//                ucd.setValue();
            } else {
                topInfo.setTopType(CacheDataType.Y + "");
                userConfigData.setType(CacheDataType.set);
//                ucd.setValue(CacheDataType.Y + "");
            }
            userConfigData.setValue(topInfo.toJson());

        }

        HttpUtil.setUserConfig(userConfigData, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
            @Override
            public void onCompleted(NewRemoteConfig newRemoteConfigs) {
//                            Logger.i("新版个人配置接口 set");
                if (newRemoteConfigs.getData().getClientConfigInfos().size() > 0) {
//                    IMDatabaseManager.getInstance().insertUserConfigVersion(newRemoteConfigs.getData().getVersion());
//                    IMDatabaseManager.getInstance().bulkUserConfig(newRemoteConfigs);
                    ConnectionUtil.getInstance().refreshTheConfig(newRemoteConfigs);
                    callBackByUserConfig.onCompleted();
                } else {
//                        IMDatabaseManager.getInstance().insertUserConfigVersion(newRemoteConfigs.getData().getVersion());
//                                if(ucd.getType()==CacheDataType.set){
//                                    userConfigData.setIsdel(CacheDataType.Y);
//                                }else{
//                                    userConfigData.setIsdel(CacheDataType.N);
//                                }

//                                userConfigData.setVersion(newRemoteConfigs.getData().getVersion());
//                        ucd.setIsdel(CacheDataType.Y);
//                        IMDatabaseManager.getInstance().insertUserConfigVersion(ucd);
                    callBackByUserConfig.onFailure();
                    //todo 这里应该做出一些什么通知
                }
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Show_List);
            }

            @Override
            public void onFailure(String errMsg) {
            callBackByUserConfig.onFailure();
            }
        });

    }

    public void setConversationParams(String key, String value) {
        //更新本地库文件
        Map<String, Object> param = new HashMap<>();
        param.put(key, value);
        IMDatabaseManager.getInstance().updateConversationParams(param);

        String params = IMDatabaseManager.getInstance().selectAllConversationParams().toString();
        if (TextUtils.isEmpty(params)) {
            return;
        }
        //拼出json对象
        final List<RemoteConfig.ConfigItem> configItems = new ArrayList<>();
        //拼单个Item数据,针对置顶,
        final RemoteConfig.ConfigItem configItem = new RemoteConfig.ConfigItem();
        configItem.key = Constants.SYS.CONVERSATION_PARAMS;
        configItem.value = params;
        String version = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(Constants.Preferences.qchat_conversation_params_version, "0");
        configItem.version = version;
        configItems.add(configItem);

        //发起请求
        HttpUtil.setRemoteConfig(configItems, null);
    }

    /**
     * 上传应用通知设置选项
     */
    public void setNotificationConfig() {
        NotificationConfig notificationConfig = new NotificationConfig();
        notificationConfig.BadgeSetting = ShortcutBadger.isBadgeCounterSupported(CommonConfig.globalContext);
        notificationConfig.NotificationCenterSetting = SystemUtil.isNotificationEnabled(CommonConfig.globalContext);
        notificationConfig.SoundSetting = SystemUtil.isMusicEnable(CommonConfig.globalContext);
        String params = JsonUtils.getGson().toJson(notificationConfig);
        if (TextUtils.isEmpty(params)) {
            return;
        }
        //拼出json对象
        final List<RemoteConfig.ConfigItem> configItems = new ArrayList<>();
        //拼单个Item数据,针对置顶,
        final RemoteConfig.ConfigItem configItem = new RemoteConfig.ConfigItem();
        configItem.key = Constants.SYS.NOTIFICATION_CONFIG;
        configItem.value = params;
        configItems.add(configItem);
        //发起请求
        HttpUtil.setRemoteConfig(configItems, null);
    }

    /**
     * 查询一条sessionList根据xmppid 额realId
     */

    public RecentConversation SelectConversationByRC(RecentConversation rc) {
        return IMDatabaseManager.getInstance().SelectConversationByRC(rc);
    }

    /**
     * 根据会话id查询未读消息
     *
     * @param xmppid
     * @param realJid
     * @return
     */
    public int SelectUnReadCountByConvid(String xmppid, String realJid, String chatType) {
        if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chatType)) {
            return IMDatabaseManager.getInstance().SelectUnReadCountByConvid(xmppid, xmppid);
        } else if ((ConversitionType.MSG_TYPE_CONSULT_SERVER + "").equals(chatType)) {
            return IMDatabaseManager.getInstance().SelectUnReadCountByConvid(xmppid, realJid);
        } else {
            return IMDatabaseManager.getInstance().SelectUnReadCountByConvid(xmppid, realJid);
        }

    }

    public void ALLMessageRead(){
        IMDatabaseManager.getInstance().ALLMessageRead();
        ProtoMessageOuterClass.ProtoMessage receive = PbAssemblyUtil.getBeenNewReadStateMessage(MessageStatus.STATUS_ALL_READED + "", new JSONArray(), CurrentPreference.getInstance().getPreferenceUserId(), null);
        qtalkSDK.sendMessage(receive);
    }


    /**
     * 查询代收单条未读
     *
     * @param of
     * @param ot
     * @return
     */
    public int SelecCollectiontUnReadCountByConvid(String of, String ot) {

        return IMDatabaseManager.getInstance().SelectCollectionUnReadCountByConvid(of, ot);


    }

    /**
     * 搜索本地图片和video
     * @param xmppId
     * @param realJid
     * @param start
     * @param end
     * @return
     */
    public List<IMMessage> searchImageVideoMsg(String xmppId,String realJid,int start,int end){
        return IMDatabaseManager.getInstance().searchImageVideoMsg(xmppId,realJid,start,end);
    }

    /**
     * 查找图片
     * @param convId
     * @param limit
     * @return
     */
    public List<IMMessage> searchImageMsg(String convId, int limit) {
        return IMDatabaseManager.getInstance().searchImageMsg(convId, limit);
    }

    /**
     * 查找代收图片
     * @param of
     * @param ot
     * @param limit
     * @return
     */
    public List<IMMessage> searchImageMsg(String of,String ot, int limit) {
        return IMDatabaseManager.getInstance().searchImageMsg(of,ot, limit);
    }

    public List<IMMessage> searchFilesMsg(){
        return IMDatabaseManager.getInstance().searchFilesMsg();
    }

    public JSONArray searchFilesMsgByXmppid(String xmppid){
        return IMDatabaseManager.getInstance().searchFilesMsgByXmppId(xmppid);
    }


    public List<IMMessage> searchMsg(String xmppId, String term, int limit){
        return IMDatabaseManager.getInstance().searchMsg(xmppId, term, limit);
    }

    public List<IMMessage> SelectInitReloadCollectionChatMessage(String of, String ot, String chatType, int count, int size) {
        if (chatType.equals("1")) {
            return IMDatabaseManager.getInstance().SelectHistoryCollectionGroupChatMessage(of, ot, count, size);
        } else {
            return IMDatabaseManager.getInstance().SelectHistoryCollectionChatMessage(of, ot, count, size);
        }

    }

    //第一次加载历史消息 需要把之前是未读状态的数据,更改为已读
    public List<IMMessage> SelectInitReloadChatMessage(String xmppid, String realjid, String chatType, int count, int size) {
        if ((ConversitionType.MSG_TYPE_CONSULT_SERVER + "").equals(chatType)) {
//            sendSingleAllRead(realjid, MessageStatus.STATUS_SINGLE_READED+"");
            return IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, realjid, count, size);
        } else if ((ConversitionType.MSG_TYPE_CONSULT + "").equals(chatType)) {
//            sendSingleAllRead(xmppid,MessageStatus.STATUS_SINGLE_READED+"");
            return IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, xmppid, count, size);
        } else {
//            sendSingleAllRead(realjid,MessageStatus.STATUS_SINGLE_READED+"");
            return IMDatabaseManager.getInstance().SelectHistoryChatMessage(xmppid, realjid, count, size);
        }
//        if(CommonConfig.isQtalk&&(chatType.equals(ConversitionType.MSG_TYPE_CONSULT_SERVER)||chatType.equals(ConversitionType.MSG_TYPE_CONSULT))){
//
//            //返回查询的历史记录
//
//        }else {
//
//            //返回查询的历史记录
//
//        }

    }

    //第一次加载群组历史消息 需要把之前是未读状态的数据,更改为已读
    public List<IMMessage> SelectInitReloadGroupChatMessage(String xmppid, String realJid, int count, int size) {
//        sendGroupAllRead(xmppid);
        //返回查询的历史记录
        return IMDatabaseManager.getInstance().SelectHistoryGroupChatMessage(xmppid, realJid, count, size);
    }

    public List<IMMessage> SelectInitReloadCollectionGroupChatMessage(String of, String ot, int start, int firstLoadCount) {
        return IMDatabaseManager.getInstance().SelectHistoryCollectionGroupChatMessage(of, ot, start, firstLoadCount);
    }


    /**
     * 发送单人会话消息已操作状态
     * @param realJid
     * @param state
     * @param msgId
     */
    public void sendMessageOperation(String realJid,String state,String msgId){
        try {
            JSONArray array = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", msgId);
            array.put(jsonObject);
            IMDatabaseManager.getInstance().UpdateReadState(array, MessageStatus.REMOTE_STATUS_CHAT_OPERATION);
            ProtoMessageOuterClass.ProtoMessage sendMessage = PbAssemblyUtil.getBeenNewReadStateMessage(state,array, realJid, IMLogicManager.getInstance().getMyself());
            //TODO 不知道为什么要发一次通知
//            IMMessage newReadMessage = PbParseUtil.parseReceiveReadMessage(sendMessage);
//            IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Read_State, newReadMessage);
            //发送消息
            qtalkSDK.sendMessage(sendMessage);
        }catch (Exception e){
            Logger.i(e.getMessage());
        }
    }

    /**
     * 发送设置单人会话消息已读
     *
     * @param realJid
     */
    public void sendSingleAllRead(String xmppid,String realJid,String state) {
        if(!IMLogicManager.getInstance().isAuthenticated()){
            return;
        }
        boolean isHasMore = true;
        while (isHasMore){//防止一次 单人未读消息过多导致卡死 每次limit200
            //返回查询到的未读消息
            JSONArray jsonArray = IMDatabaseManager.getInstance().SelectUnReadByXmppid(xmppid,realJid,200);
            if (jsonArray != null && jsonArray.length() > 0) {
                if(jsonArray.length() < 200){
                    isHasMore = false;
                }
                IMDatabaseManager.getInstance().UpdateReadState(jsonArray, MessageStatus.REMOTE_STATUS_CHAT_READED);
                //拼接pb消息
                ProtoMessageOuterClass.ProtoMessage sendMessage = PbAssemblyUtil.getBeenNewReadStateMessage(state,jsonArray, realJid, IMLogicManager.getInstance().getMyself());
                //发送消息
                qtalkSDK.sendMessage(sendMessage);
            } else {
                isHasMore = false;
            }
        }
    }

    /**
     * 发送设置群消息已读
     *
     * @param xmppid
     */
    public void sendGroupAllRead(String xmppid) {

        if(!IMLogicManager.getInstance().isAuthenticated()){
            return;
        }
        IMMessage imMessage = new IMMessage();
        imMessage.setConversationID(xmppid);
        imMessage.setTime(new Date());

        //发送已读状态
        setGroupRead(imMessage);
    }


    /**
     * 设置群组已读
     *
     * @param imMessage
     */
    public void setGroupRead(IMMessage imMessage) {
        //home出去，不设置已读
        if (CommonConfig.leave) {
            return;
        }
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        //获取要更新的消息id

        String id = imMessage.getConversationID();
        //获取群id
        String groupId = QtalkStringUtils.parseId(id);
        //获取群 域名
        String domain = QtalkStringUtils.parseGroupDomain(id);
        //获取消息时间
        long time = imMessage.getTime().getTime();
        String target = QtalkStringUtils.parseBareJid(id);
        try {
            message.put("id", groupId);
            message.put("domain", domain);
            message.put("t", time);
            messages.put(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //对数据库中的未读消息进行更新（防止收不到readmark response导致未读不消失）
       IMDatabaseManager.getInstance().updateGroupMessageReadedTag(id,MessageStatus.REMOTE_STATUS_CHAT_READED,time);
        ProtoMessageOuterClass.ProtoMessage sendMessage = PbAssemblyUtil.getGroupBeenReadMessage(messages, target, IMLogicManager.getInstance().getMyself());
        qtalkSDK.sendMessage(sendMessage);
    }

    /**
     * 单条接收消息设置为已读
     *
     * @param imMessage
     */
    //单条接收消息设置为已读
    public void setSingleRead(IMMessage imMessage,String state) {

        if(!IMLogicManager.getInstance().isAuthenticated()){
            return;
        }

        //home出去，不设置已读
        if (CommonConfig.leave) {
            return;
        }
        //生成JSONArray 对象来存储要更新的id
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        String messageId = imMessage.getMessageId();

        String target = "";

        if("5".equals(imMessage.getQchatid())||"4".equals(imMessage.getQchatid())){
            target = imMessage.getRealfrom();
        }else{
            target = imMessage.getFromID();
        }
//        String from = ProtoMessageParseUtil.getSingleMessageFrom(protoMessage);
        try {
            message.put("id", messageId);
            messages.put(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IMDatabaseManager.getInstance().UpdateReadState(messages, MessageStatus.REMOTE_STATUS_CHAT_READED);
//        ProtoMessageOuterClass.ProtoMessage sendMessage = PbAssemblyUtil.getBeenReadMessage(messages, target, IMLogicManager.getInstance().getMyself());
        ProtoMessageOuterClass.ProtoMessage sendMessage = PbAssemblyUtil.getBeenNewReadStateMessage(state,messages, target, IMLogicManager.getInstance().getMyself());

        qtalkSDK.sendMessage(sendMessage);

    }

    public RecentConversation selectRecentConversationByXmppId(String xmppid) {
        return IMDatabaseManager.getInstance().selectRecentConversationByXmppId(xmppid);
    }

    /**
     * 发送心跳消息
     */
    public void sendHeartBeat() {

//        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getHeartBeatMessage();
        qtalkSDK.sendHeartMessage();
    }

    /**
     * 好友验证类型消息
     */
    public void sendVerifyFriend(String target){
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getVerifyFriendModeMessage(target);
        qtalkSDK.sendMessage(protoMessage);
    }

    public void verifyFriend(String target,String from){
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getVerifyFriendMessage(target,from);
        qtalkSDK.sendMessage(protoMessage);
    }

    public void verifyFriend(String target,String from,String answer){

        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getVerifyFriendMessage(target,from,answer);
        qtalkSDK.sendMessage(protoMessage);
    }


    //发送文本和emj消息方法,暂时只实现文本
//    public void sendTextOrEmojiMessage(String str,String toId,String fromId)  {
//        //根据发送内容 获得要发送的数据的message对象
//        String msg = PbChatTextHelper.textToHTML(str);
//        //拼接处要发送的protoMessage
//       ProtoMessageOuterClass.ProtoMessage protoMessage =  PbAssemblyUtil.getTextOrEmojiMessage(fromId,toId,msg);
////        Logger.i("单聊发送的消息:"+protoMessage);
////        try {
////            Logger.i("单聊发送的消息Message:"+ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage()));
////        } catch (InvalidProtocolBufferException e) {
////            e.printStackTrace();
////        }
//        //把ProtoMessage转换成PBIMMessage
//        PBIMMessage pbimMessage = null;
//        try {
//            pbimMessage = ProtoMessageParseUtil.sendPbMessage2IMMessage(protoMessage);
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//        //调用sdk方法发送消息
//        //存入数据库, 当前数据处于发送中 发送中为2
//
//        //发送消息
//        qtalkSDK.sendMessage(protoMessage);
//        //返回PBIMMessage对象用于在界面中显示!
//
//    }
//------------------------------------------------IQ消息---------------------------------------------

    /**
     * 获取群成员IQ消息请求
     *
     * @param key
     */
    public void getMembersAfterJoin(String key) {
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getMembersAfterJoin(key);
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 创建群IQ消息请求
     *
     * @param key
     */
    public void createGroup(String key) {
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.createGroup(key);
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 设置群管理员权限
     * @param groupId
     * @param xmppid
     * @param nickName
     * @param isAdmin
     */
    public void setGroupAdmin(String groupId,String xmppid,String nickName,boolean isAdmin){
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.setGroupAdmin(groupId,xmppid,(nickName == null ? "" :nickName),isAdmin);
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 邀请人消息(v2)IQ消息请求
     *
     * @param groupId
     * @param invitedList
     */
    public void inviteMessageV2(String groupId, List<String> invitedList) {
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.inviteMessageV2(IMLogicManager.getInstance().getMyself(), groupId, invitedList);
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 入群注册IQ消息请求
     *
     * @param key
     */
    public void regitstInGroup(String key) {
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.regitstInGroup(key);
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 退出群IQ消息请求
     *
     * @param key
     */
    public void leaveGroup(String key) {
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.leaveGroup(key);
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 销毁群组
     *
     * @param key
     */
    public void destroyGroup(String key) {
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.destroyGroup(key);
        qtalkSDK.sendMessage(protoMessage);
    }


    /**
     * 获取好友IQ消息请求
     *
     * @param key
     */
    public void getFriends(String key) {
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getFriends(CurrentPreference.getInstance().getPreferenceUserId());
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 删除好友
     * @param jid
     * @param domain
     */
    public void deleteFriend(String jid,String domain){
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.deleteFriend(IMLogicManager.getInstance().getMyself(),jid,domain);
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 获取用户注册群列表IQ消息请求
     *
     * @param key
     */
    public void getUserMucs(String key) {
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.getUserMucs(CurrentPreference.getInstance().getPreferenceUserId());
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 删除群成员
     *
     * @param groupId
     * @param map
     */
    public void delGroupMember(String groupId, Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.delGroupMember(IMLogicManager.getInstance().getMyself(), groupId, entry.getKey(), entry.getValue());
            qtalkSDK.sendMessage(protoMessage);
        }
    }

    public  List<WorkWorldItem> selectHistoryWorkWorldItem(int size,int limit){
        return IMDatabaseManager.getInstance().selectHistoryWorkWorldItem(size, limit,"");
    }

    public List<WorkWorldItem> selectHistoryWorkWorldItem(int size, int limit, String searchId) {
        return IMDatabaseManager.getInstance().selectHistoryWorkWorldItem(size, limit,searchId);
    }


    public List<WorkWorldNewCommentBean> selectHistoryWorkWorldCommentItem(int size,int limit){
        return IMDatabaseManager.getInstance().selectHistoryWorkWorldCommentItem(size, limit);
    }

    public List<WorkWorldNewCommentBean> selectHistoryWorkWorldNewCommentBean(int size,int limit,String uuid){
        return IMDatabaseManager.getInstance().selectHistoryWorkWorldNewCommentBean(size, limit,uuid);
    }

    public List<WorkWorldNoticeItem> selectHistoryWorkWorldNotice(int size,int limit){
        return IMDatabaseManager.getInstance().selectHistoryWorkWorldNotice(size, limit);
    }

    public List<? extends WorkWorldNoticeItem> selectHistoryWorkWorldNoticeByEventType(int size, int limit, List<String> eventType, boolean isAtShow){
        return IMDatabaseManager.getInstance().selectHistoryWorkWorldNoticeByEventType(size, limit,eventType, isAtShow);
    }

    public interface WorkWorldCallBack{
        void callBack(WorkWorldItem item);
        void goToNetWork();
    }


    public boolean SelectWorkWorldPremissions(){
        return IMDatabaseManager.getInstance().SelectWorkWorldPremissions();
    }

    public boolean SelectWorkWorldRemind(){
        return IMDatabaseManager.getInstance().SelectWorkWorldRemind();
    }

    public void getWorkWorldByUUID(String uuid, final WorkWorldCallBack workWorldCallBack){
        WorkWorldItem item = IMDatabaseManager.getInstance().selectWorkWorldItemByUUID(uuid);
        if(item!=null){
            workWorldCallBack.callBack(item);
        }else{
            workWorldCallBack.goToNetWork();
            HttpUtil.getWorkWorldItemByUUID(uuid, new ProtocolCallback.UnitCallback<WorkWorldSingleResponse>() {
                @Override
                public void onCompleted(WorkWorldSingleResponse workWorldSingleResponse) {
                    //在这里强制把帖子获取为普通帖
                    workWorldSingleResponse.getData().setPostType("1");
                    workWorldCallBack.callBack(workWorldSingleResponse.getData());
                }

                @Override
                public void onFailure(String errMsg) {
                    workWorldCallBack.callBack(null);
                }
            });
        }
    }



    //--------------------------------------------presence消息-------------------------------------------
    public void setUserState(String state){
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.setUserState(state);
        qtalkSDK.sendMessage(protoMessage);
    }

    /**
     * 会话同步
     * @param json
     * @param from
     * @param target
     */
    public void conversationSynchronizationMessage(String json, String from, String target){
        ProtoMessageOuterClass.ProtoMessage protoMessage = PbAssemblyUtil.conversationSynchronizationMessage(json, from, target);
        qtalkSDK.sendMessage(protoMessage);
    }

    public void lanuchChatVideo(boolean isVideo, String caller, String callee){
        Intent intent = new Intent("com.qunar.im.START_BROWSER");
        intent.setClassName(CommonConfig.globalContext, "com.qunar.im.ui.activity.QunarWebActvity");
        StringBuilder url = new StringBuilder(QtalkNavicationService.getInstance().getVideoHost());
        Map<String,String> params = new HashMap<>();
        params.put("video",String.valueOf(isVideo));
        Protocol.spiltJointUrl(url,params);
        intent.setData(Uri.parse(url.toString()));
        intent.putExtra(Constants.BundleKey.IS_HIDE_BAR, true);
        intent.putExtra(Constants.BundleKey.IS_VIDEO_AUDIO_CALL,true);
        intent.putExtra(Constants.BundleKey.VIDEO_AUODIO_CALLER,caller);
        intent.putExtra(Constants.BundleKey.VIDEO_AUODIO_CALLEE,callee);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public void lanuchGroupVideo(String roomId,String groupName){
        Intent intent = new Intent("com.qunar.im.START_BROWSER");
        intent.setClassName(CommonConfig.globalContext, "com.qunar.im.ui.activity.QunarWebActvity");
        //群视频使用webview加载，必须是https的，所以兼容导航是http的情况，强制转https
        String videoUrl = QtalkNavicationService.getInstance().getVideoHost();
        if(!videoUrl.startsWith("https://")) {
            videoUrl.replace("http", "https");
        }
        StringBuilder url = new StringBuilder(videoUrl + "conference#/login");
        Map<String,String> params = new HashMap<>();
        params.put("userId",CurrentPreference.getInstance().getPreferenceUserId());
        params.put("roomId",roomId);
        params.put("topic", groupName);
        params.put("plat", "2");
        Protocol.spiltJointUrl(url,params);
        intent.setData(Uri.parse(url.toString()));
        intent.putExtra(Constants.BundleKey.IS_HIDE_BAR, true);
        intent.putExtra(Constants.BundleKey.IS_VIDEO_AUDIO_CALL,true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 检查是否绑定了支付宝账户
     */
    public void checkAlipayAccount(){
        IMPayManager.getInstance().checkAlipayAccount();
    }

    /**
     * 绑定支付宝账户
     * @param uid
     */
    public void bindAlipayAccount(String uid,String openId){
        IMPayManager.getInstance().bindAlipayAccount(uid,openId);
    }

    /**
     * 切换新老版本搜索
     * @param isOld
     */
    public void switchSearchVersion(boolean isOld){
        IMDatabaseManager.getInstance().insertFocusSearchCacheData(isOld+"");
    }
    /**
     * 查询绑定用户信息
     *
     * @return
     */
    public List<Nick> selectCollectionUser() {
        List<Nick> list = IMDatabaseManager.getInstance().selectCollectionUser();
        return list;
    }

    public void setAllMsgRead(){
        IMDatabaseManager.getInstance().updateAllRead();
    }

    private List<IMMessage> createNoMessage(){
        List<IMMessage> noMessage = new ArrayList<IMMessage>();
        IMMessage imMessage = new IMMessage();
        String uid = UUID.randomUUID().toString();
        imMessage.setId(uid);
        imMessage.setMessageID(uid);
        imMessage.setDirection(2);
        imMessage.setMsgType(MessageType.MSG_TYPE_NO_MORE_MESSAGE);
        imMessage.setBody("没有更多消息了");
        noMessage.add(imMessage);
        return noMessage;
    }

    /****************************** TEST ***********************/
    public String queryMessageContent(String msgid){
        return IMDatabaseManager.getInstance().queryMessageContent(msgid);
    }

    public void resetUnreadCount(){
        IMDatabaseManager.getInstance().resetUnreadCount();
    }

    /*************************** For Update ************************/
    public boolean isTableExit(String tableName){
        return IMDatabaseManager.getInstance().isTableExit(tableName);
    }
    public boolean isTriggerExit(String triggerName){
        return IMDatabaseManager.getInstance().isTriggerExit(triggerName);
    }

    public void update_DB_version_20(){
        IMDatabaseManager.getInstance().update_DB_version_20();
    }

    public void update_DB_reduction(){
        IMDatabaseManager.getInstance().update_DB_reduction();
    }
}
