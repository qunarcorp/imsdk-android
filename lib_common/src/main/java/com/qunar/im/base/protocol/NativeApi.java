package com.qunar.im.base.protocol;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import com.qunar.im.base.jsonbean.RNSearchData;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hubin on 2018/4/3.
 */

public class NativeApi {

    public static final String SEARCH_SCOPE = "search_scope";

    public static final String ROBOT_GROUP = "Q03"; // 机器人
    public static final String OUT_GROUP = "Q04"; // 跨域群
    public static final String LOCAL_USER = "Q08"; //本地用户
    public static final String LOCAL_GROUP = "Q09"; //本地群组

    public static final String ROBOT_LABEL = "公众号列表";
    public static final String OUT_GROUP_LABEL = "外域群组";
    public static final String LOCAL_USER_LABEL = "本地用户";
    public static final String LOCAL_GROUP_LABEL = "本地群组";
    public static final String ROBOT_DEFAULT_PORTRAIT = "https://qim.qunar.com/file/v2/download/perm/612752b6f60c3379077f71493d4e02ae.png";
    public static final String OUT_GROUP_DEFAULT_PORTRAIT = "https://qim.qunar.com/file/v2/download/perm/2227ff2e304cb44a1980e9c1a3d78164.png";
    public static final String LOCAL_USER_DEFAULT_PORTRAIT = "https://qim.qunar.com/file/v2/download/perm/3ca05f2d92f6c0034ac9aee14d341fc7.png";
    public static final String LOCAL_GROUP_DEFAULT_PORTRAIT = "https://qim.qunar.com/file/v2/download/perm/2227ff2e304cb44a1980e9c1a3d78164.png";
    public static final int ROBOT_SINGLE_CHAT_EVENT = 8;
    public static final int OUT_GROUP_CHAT_EVENT = 1;
    public static final int LOCAL_GROUP_EVENT = 1;
    public static final int LOCAL_USER_EVENT = 0;

    public static final String KEY_JID = "jid";
    public static final String KEY_IS_CHATROOM = "isFromChatRoom";
    public static final String KEY_REAL_JID = "realJid";
    public static final String KEY_CHAT_TYPE = "chatType";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_ENCRYPT_BODY = "encryptBody";
    public static final String KEY_RIGHTBUTTON = "right_button_type";
    public static final String KEY_INPUTTYPE = "input_type";
    public static final String KEY_AUTO_REPLY = "auto_reply";

    public static final String KEY_ATMSG_INDEX = "atmsg_index";
    public static final String KEY_FILE_NAME = "file_name";
    public static final String KEY_FILE_SIZE = "file_size";
    public static final String KEY_FILE_URL = "file_url";
    public static final String KEY_FILE_MD5 = "file_md5";
    public static final String KEY_FILE_NOMD5 ="file_noMd5";

    public static final String WorkWordJID = "WorkWordJID";

    public static void openUserCardVCByUserId(String userId) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openUserCard?jid=" +
                QtalkStringUtils.userId2Jid(userId)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openUserHongBao() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/hongbao"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openUserHongBaoBalance() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/hongbao_balance"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openAccountInfo() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/account_info"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openSearchActivty() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openSearchActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openDeveloperChat() {

        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/developer_chat"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);

    }

    /**
     * 此处realjid其实无太大意义
     *
     * @param groupId
     * @param RealJid
     */
    public static void openGroupChat(String groupId, String RealJid) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openGroupChat?" +
                KEY_JID + "=" + groupId + "&" + KEY_REAL_JID + "=" + RealJid));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 分享内容到群
     *
     * @param groupId
     * @param RealJid
     */
    public static void openGroupChatForShare(String groupId, String RealJid, String sharMsg) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openGroupChat?" +
                KEY_JID + "=" + groupId + "&" + KEY_REAL_JID + "=" + RealJid + "&shareMsg=" + sharMsg));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openGroupChatInfo(String groupId){
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openGroupChatInfo?groupId=" + groupId));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openMyRnSetting(){
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openMyRnSetting"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开单人会话
     *
     * @param jid
     * @param realJid
     */
    public static void openSingleChat(String jid, String realJid) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openSingleChat?" +
                KEY_JID + "=" + jid + "&" + KEY_REAL_JID + "=" + realJid));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }



    /**
     * 打开驼圈
     *
     * @param jid
     * @param realJid
     */
    public static void openUserWorkWorld(String jid, String realJid) {
        Intent intent;
        if(TextUtils.isEmpty(jid)){
            intent  = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openUserWorkWorld"));
        }else{
            intent  = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openUserWorkWorld?" +
                    WorkWordJID + "=" + jid ));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }


    public static void openSingleChatInfo(String jid,String realJid){
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openSingleChatInfo?userId=" +
                QtalkStringUtils.userId2Jid(jid) + "&realJid=" + QtalkStringUtils.userId2Jid(realJid)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 本地搜索打开会话
     *
     * @param jid
     * @param realJid
     * @param chatType
     */
    public static void openChatForLocalSearch(String jid, String realJid, String chatType, String time) {
        Uri uri = Uri.parse(CommonConfig.schema + "://qunarchat/openChatForSearch?"
                + KEY_JID + "=" + jid
                + "&" + KEY_REAL_JID + "=" + realJid
                + "&" + KEY_CHAT_TYPE + "=" + chatType
                + "&" + KEY_START_TIME + "=" + time);
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 会话内搜索图片
     * @param jid
     * @param realJid
     */
    public static void openLocalSearchImage(String jid, String realJid) {
        Uri uri = Uri.parse(CommonConfig.schema + "://qunarchat/openSearchChatImage?"
                + KEY_JID + "=" + jid
                + "&" + KEY_REAL_JID + "=" + realJid);
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 浏览大图
     *
     * @param url
     */
    public static void openBigImage(String url, String thumbPath) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openBigImage?" +
                Constants.BundleKey.IMAGE_URL + "=" + url + "&" + Constants.BundleKey.IMAGE_ON_LOADING + "=" + thumbPath));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开上传头像图片浏览
     */
    public static void openPictureSelector() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openPictureSelector"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openCamerSelecter() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openCamerSelecter"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开添加好友页面
     *
     * @param jid
     */
    public static void openAddFriend(String jid) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/addFriend?" +
                KEY_JID + "=" + jid));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openDressUpVc() {

        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/dress_up_vc"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openMcConfig() {

        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/mc_config"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openAbout() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/about"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void logout() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/logout"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开手机号界面
     *
     * @param userId
     */
    public static void openPhoneNumber(String userId) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openPhoneNumber?userId=" + userId));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开网页页面,第一个参数为地址
     *
     * @param url
     * @param showNavBar
     */
    public static void openWebPage(String url, boolean showNavBar) {
        Intent intent = new Intent("com.qunar.im.START_BROWSER");
        intent.setClassName(CommonConfig.globalContext, "com.qunar.im.ui.activity.QunarWebActvity");
        intent.setData(Uri.parse(url));
        intent.putExtra("ishidebar", !showNavBar);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开未读消息页面
     */
    public static void openUnReadListActivity() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/unreadList"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);

    }

    /**
     * 打开公众号页面
     */
    public static void openPublicNumber() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/publicNumber"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开组织架构
     */
    public static void openOrganizational() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openOrganizational"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开系统设置
     */
    public static void openSystemSetting() {
        Uri packageURI = Uri.parse("package:" + CommonConfig.globalContext.getApplicationInfo().packageName);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 切换账号
     */
    public static void openAccountSwitch() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/accountSwitch"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开邮件
     *
     * @param userId
     */
    public static void openEmail(String userId) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openEmail?userId=" + userId));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openMyFile() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/myfile"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开创建群组
     */
    public static void openCreateGroup() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://rnservice?module=GroupCard&Screen=GroupMemberAdd"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 分享 打开创建群组
     */
    public static void openCreateGroupForShare(String shareMsg) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://rnservice?module=GroupCard&Screen=GroupMemberAdd"));
        //设置分享数据
        intent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
        intent.putExtra(Constants.BundleKey.SHARE_EXTRA_KEY, shareMsg);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 转发 打开创建群组
     */
    public static void openCreateGroupForTrans(Serializable transMsg) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://rnservice?module=GroupCard&Screen=GroupMemberAdd"));
        //设置转发数据
        intent.putExtra(Constants.BundleKey.IS_TRANS, true);
        intent.putExtra(Constants.BundleKey.TRANS_MSG, JsonUtils.getGson().toJson(transMsg));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开我的群组
     */
    public static void openMyGroups() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://rnservice?module=Contacts&Screen=GroupList"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开我的群组 for share
     *
     * @param shareMsg 分享消息内容
     */
    public static void openMyGroupsForShare(String shareMsg) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://rnservice?module=Contacts&Screen=GroupList"));
        //设置分享数据
        intent.putExtra(Constants.BundleKey.IS_FROM_SHARE, true);
        intent.putExtra(Constants.BundleKey.SHARE_EXTRA_KEY, shareMsg);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开我的群组 for Trans
     *
     * @param transMsg 转发消息内容
     */
    public static void openMyGroupsForTrans(Serializable transMsg) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://rnservice?module=Contacts&Screen=GroupList"));
        //设置转发数据
        intent.putExtra(Constants.BundleKey.IS_TRANS, true);
        intent.putExtra(Constants.BundleKey.TRANS_MSG, JsonUtils.getGson().toJson(transMsg));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openLocalSearch(String xmppid, String realjid, String chatType) {
        Intent intent = new Intent("android.intent.action.VIEW",
                Uri.parse(CommonConfig.schema + "://rnservice?module=Search&Screen=LocalSearch&xmppid=" + xmppid + "&realjid=" + realjid + "&chatType=" + chatType));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开文件下载页面
     */
    public static void openFileDownLoad(TransitFileJSON transitFileJSON) {
        Intent intent = new Intent("android.intent.action.VIEW",
                Uri.parse(CommonConfig.schema + "://qunarchat/openDownLoad?" +
                        KEY_FILE_NAME + "=" + transitFileJSON.FileName + "&"
                        + KEY_FILE_SIZE + "=" + transitFileJSON.FileSize + "&"
                        + KEY_FILE_URL + "=" + transitFileJSON.HttpUrl+ "&"
                        +KEY_FILE_MD5 +"="+ transitFileJSON.FILEMD5+"&"
                        +KEY_FILE_NOMD5+"="+transitFileJSON.noMD5));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);

    }

    public static List localSearch(String key, int start, int len, String groupId) {
        /**
         *  groupId:
         *  Q03 查找公众号
         *  Q04 查找跨域群
         *  Q08 查找本地用户
         *  Q09 查找本地群组
         *  空字符串 查找公众号&跨域群 全查
         */
        List<RNSearchData> result = new ArrayList<>();
        //获取本地用户
        if (groupId.equals("") || groupId.equals(LOCAL_USER)) {
            RNSearchData localUser = getLocalUser(key, start, len);
            if (!localUser.getInfo().isEmpty()) {
                result.add(localUser);
            }
        }
        //获取本地群组
        if (groupId.equals("") || groupId.equals(LOCAL_GROUP)) {
            RNSearchData localGroup = getLocalGroup(key, start, len);
            if (!localGroup.getInfo().isEmpty()) {
                result.add(localGroup);
            }
        }
        //获取外域群组
        if (groupId.equals("") || groupId.equals(OUT_GROUP)) {
            RNSearchData localGroup = getLocalOutGroup(key, start, len);
            if (!localGroup.getInfo().isEmpty()) {
                result.add(localGroup);
            }
        }
        return result;
    }


    /**
     * 获取本地用户
     *
     * @param key   搜索值
     * @param start 从第几个开始
     * @param len   获取几个值
     * @return
     */
    public static RNSearchData getLocalUser(String key, int start, int len) {
        RNSearchData rnSearchData = new RNSearchData();
        List<RNSearchData.InfoBean> infoList = new ArrayList<>();
        if (!TextUtils.isEmpty(key)) {
            infoList = ConnectionUtil.getInstance().getLocalUser(key, start, len + 1);
        }

        rnSearchData.setGroupPriority(0);
        rnSearchData.setTodoType(LOCAL_USER_EVENT);
//        rnSearchData.setInfo(infoList);
        rnSearchData.setHasMore((infoList.size() > len) ? 1 : 0);
        rnSearchData.setGroupLabel(LOCAL_USER_LABEL);
        rnSearchData.setGroupId(LOCAL_USER);
        rnSearchData.setIsLoaclData(1);
        rnSearchData.setDefaultportrait(LOCAL_USER_DEFAULT_PORTRAIT);
//        infoList.remove(len+1);
        if (infoList.size() > len) {
            rnSearchData.setInfo(infoList.subList(0, len));
        } else {
            rnSearchData.setInfo(infoList);
        }

        return rnSearchData;

    }

    /**
     * 获取本地外域群组
     * @param key
     * @param start
     * @param len
     * @return
     */
    public static RNSearchData getLocalOutGroup(String key, int start, int len) {
        RNSearchData rnSearchData = new RNSearchData();
        List<RNSearchData.InfoBean> infoList = new ArrayList<>();
        if (!TextUtils.isEmpty(key)) {
            infoList = ConnectionUtil.getInstance().getOutGroup(key, start, len);
        }

        rnSearchData.setGroupPriority(0);
        rnSearchData.setTodoType(OUT_GROUP_CHAT_EVENT);
        rnSearchData.setHasMore((infoList.size() > len) ? 1 : 0);
        rnSearchData.setGroupLabel(OUT_GROUP_LABEL);
        rnSearchData.setGroupId(OUT_GROUP);
        rnSearchData.setIsLoaclData(1);
        rnSearchData.setDefaultportrait(OUT_GROUP_DEFAULT_PORTRAIT);
        if (infoList.size() > len) {
            rnSearchData.setInfo(infoList.subList(0, len));
        } else {
            rnSearchData.setInfo(infoList);
        }
        return rnSearchData;
    }

    /**
     * 获取本地群组
     *
     * @param key
     * @param start
     * @param len
     * @return
     */
    public static RNSearchData getLocalGroup(String key, int start, int len) {
        RNSearchData rnSearchData = new RNSearchData();
        List<RNSearchData.InfoBean> infoList = new ArrayList<>();
        if (!TextUtils.isEmpty(key)) {
            infoList = ConnectionUtil.getInstance().getLocalGroup(key, start, len);
        }

        rnSearchData.setGroupPriority(0);
        rnSearchData.setTodoType(LOCAL_GROUP_EVENT);
        rnSearchData.setHasMore((infoList.size() > len) ? 1 : 0);
        rnSearchData.setGroupLabel(LOCAL_GROUP_LABEL);
        rnSearchData.setGroupId(LOCAL_GROUP);
        rnSearchData.setIsLoaclData(1);
        rnSearchData.setDefaultportrait(LOCAL_GROUP_DEFAULT_PORTRAIT);
        if (infoList.size() > len) {
            rnSearchData.setInfo(infoList.subList(0, len));
        } else {
            rnSearchData.setInfo(infoList);
        }
        return rnSearchData;

    }


    public static void openUserFriendsVC() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/search?" +
                SEARCH_SCOPE + "=8"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openGroupListVC() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/search?" +
                SEARCH_SCOPE + "=2"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openPublicNumberVC() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/search?" +
                SEARCH_SCOPE + "=4"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openRobotChatByRobotId(String id) {
        // 打开公众号聊天
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/robot?robotId=" +
                QtalkStringUtils.userId2Jid(id)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }



    public static void openSearchDetailActivity(String jid, String t, String id,String action) {
        // 打开聊天记录上下文

        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openWebView?"
                + "action=" + action
                + "&time=" + t
                + "&jid=" + jid));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openQtalkWebViewForUrl(String url, Boolean showNavBar) {
        Intent intent = new Intent("com.qunar.im.START_BROWSER");
        intent.setClassName(CommonConfig.globalContext, "com.qunar.im.ui.activity.QunarWebActvity");
        intent.setData(Uri.parse(url));
        intent.putExtra( "ishidebar", !showNavBar);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开二维码
     */

    public static void openScan() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openScan"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开记事本
     */
    public static void openNoteBook() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openNoteBook"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开行程
     */

    public static void openTravelCalendar() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openTravelCalendar"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开跨域搜索页面
     */
    public static void openDomainSearch(){
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openDomainSearch"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开导航配置
     */
    public static void openNavConfig() {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openNavConfig"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 打开外部RN应用
     *
     */
    public static void openExternalRN(Map<String,Object> map) {
        String str = CommonConfig.schema + "://qunarchat/openExternalRN?";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
//                        intent.putExtra(entry.getKey(), entry.getValue() + "");
//            map.put(entry.getKey(),entry.getValue());
            str+=entry.getKey()+"="+entry.getValue()+"&";
        }
        if(str.endsWith("&")){
            str= str.substring(0,str.length()-1);
        }




        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    /**
     * 显示搜索历史目标位置结果
     * @param jid
     * @param realJid
     * @param chatType
     * @param time
     */
    public static void showSearchHistoryResult(String jid,String realJid,String chatType,String time){
        Uri uri = Uri.parse(CommonConfig.schema + "://qunarchat/openChatForNetSearch?"
                + KEY_JID + "=" + jid
                + "&" + KEY_REAL_JID + "=" + realJid
                + "&" + KEY_CHAT_TYPE + "=" + chatType
                + "&" + KEY_START_TIME + "=" + time);
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }

    public static void openUserMedal(String userId) {
        // 打开聊天记录上下文

        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(CommonConfig.schema + "://qunarchat/openMedalPage?"
                + "&jid=" + userId));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommonConfig.globalContext.startActivity(intent);
    }
}
