package com.qunar.im.other;

/**
 * Created by hubin on 2017/9/26.
 */

public class CacheDataType {
    /**
     * 代表置顶
     */
    public static final int TOP = 1;
    public static final int TOPVER = 101;
    /**
     * 代表提醒
     */
    public static final int REMIND = 2;

    /**
     * 代表qchat 众包 参数配置
     */
    public static final int CONVERSATION_PARAMS = 3;

    /**
     * 收藏表情
     */
    public static final int COLLECTION_EMO = 4;

    public static final int MARKUP_NAMES = 5;

    /**
     * 用户id
     */
    public static final int USER_ID_TYPE = 6;
    public static final String USER_ID = "USER_ID";

    /**
     * 本地群的ReadMark最新时间
     */
    public static final int GROUP_READMARK_TIME = 7;
    public static final String GROUP_READMARK = "GROUP_READMARK";
    /***热线账号列表*/
    public static final String HOTLINE_KEY = "hotline";
    public static final int HOTLINE_TYPE = 8;

    /**
     * 用户加号功能配置
     */
    public static final String userCapabilityValue = "userCapabilityValue";
    public static final int userCapabilityValueType = 9;

    /**
     * 最后一条消息时间
     */
    public static final String lastUpdateTimeValue = "lastUpdateTime";
    public static final int lastUpdateTimeValueType = 10;

    /**
     * 组织架构最新时间戳
     */
    public static final String lastIncrementUserVersion = "lastIncrementUserVersion";
    public static final int lastIncrementUser = 11;

    /**
     * 发现页配置
     */
    public static final int FoundConfigurationType = 12;
    public static final String FoundConfiguration = "FoundConfiguration";

    /**
     * 驼圈开关
     */
    public static final String kCricleCamelNotify = "kCricleCamelNotify";// 驼圈通知展示开关
    public static final int kCricleCamelNotify_Type = 13;


    /**
     * 朋友圈权限功能
     */
    public static final String workWorldPermissionsValue = "workWorldPermissionsValue";
    public static final int workWorldPermissionsType = 14;


    /**
     * 朋友圈提醒功能
     */
    public static final String workWorldRemindValue = "workWorldRemindValue";
    public static final int workWorldRemindType = 15;

    /**
     * 勋章列表版本号
     */
    public static final String medalListVersionValue = "medalListVersionValue";
    public static final int medalListVersionType = 16;

    /**
     * 勋章用户状态列表版本号
     */
    public static final String medalUserStatusValue = "medalUserStatusValue";
    public static final int medalUserStatusType = 17;

    /**
     * 用户id
     */
    public static final int Focus_Search_TYPE = 7;
    public static final String Focus_Search_ID = "Focus_Search_ID";


    /**
     * 代表push开关
     */

    public static final String pushState = "pushState";
    public static final int PushStateType = 500;

    /**
     * 代表新版version版本号
     */
    public static final String userConfigVersion = "userConfigVersion";
    public static final int userConfigVersionType = 99;

    public static final String QUICK_REPLY_VERSION = "quick_reply_version";
    public static final int quick_reply_type = 100;
    /**
     * 代表议程version版本号
     */
    public static final String userTripVersion = "userTripVersion";
    public static final int userTripVersionType = 98;


    public static final String kMarkupNames = "kMarkupNames";    //用户备注（通用）
    public static final String kCollectionCacheKey = "kCollectionCacheKey";//收藏表情（通用）
    public static final String kStickJidDic = "kStickJidDic";//置顶会话（通用）
    public static final String kNotificationSetting = "kNotificationSetting";    //客户端通知中心设置（通用）
    public static final String kNoticeStickJidDic = "kNoticeStickJidDic";    //会话提醒（通用）
    public static final String kConversationParamDic = "kConversationParamDic";    //众包需求（通用）
    public static final String kQuickResponse = "kQuickResponse";    //快捷回复（通用）
    public static final String kStarContact = "kStarContact";//星标联系人
    public static final String kBlackList = "kBlackList";//黑名单
    public static final String kChatColorInfo = "kChatColorInfo";    //消息气泡颜色（IOS)
    public static final String kCurrentFontInfo = "kCurrentFontInfo";    //客户端字体（IOS)
    public static final String kChatMessageFontInfo = "kChatMessageFontInfo";    //客户端字体（Mac）
    public static final String kAdrFontInfo = "kAdrFontInfo";    //客户端字体（Android）
    public static final String kWaterMark = "kWaterMark";//聊天水印背景（Android）


    public static final int set = 1;//操作符 设置
    public static final int cancel = 2;//操作符 取消

    public static final int Y = 1;//为真
    public static final int N = 0;//为取消


}
