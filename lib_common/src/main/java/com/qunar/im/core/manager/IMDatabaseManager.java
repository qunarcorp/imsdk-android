package com.qunar.im.core.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.LruCache;

import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.jsonbean.AtInfo;
import com.qunar.im.base.jsonbean.CapabilityResult;
import com.qunar.im.base.jsonbean.CollectionCardData;
import com.qunar.im.base.jsonbean.CollectionMucCardData;
import com.qunar.im.base.jsonbean.CollectionUserBindData;
import com.qunar.im.base.jsonbean.DailyMindMain;
import com.qunar.im.base.jsonbean.DailyMindSub;
import com.qunar.im.base.jsonbean.GetDepartmentResult;
import com.qunar.im.base.jsonbean.IncrementUsersResult;
import com.qunar.im.base.jsonbean.JSONChatHistorys;
import com.qunar.im.base.jsonbean.JSONMucHistorys;
import com.qunar.im.base.jsonbean.JSONReadMark;
import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.base.jsonbean.MessageStateSendJsonBean;
import com.qunar.im.base.jsonbean.NewReadStateByJson;
import com.qunar.im.base.jsonbean.NewRemoteConfig;
import com.qunar.im.base.jsonbean.QuickReplyResult;
import com.qunar.im.base.jsonbean.RNSearchData;
import com.qunar.im.base.module.CityLocal;
import com.qunar.im.base.module.FoundConfiguration;
import com.qunar.im.base.module.SearchKeyData;
import com.qunar.im.base.module.WorkWorldAtShowItem;
import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.AreaLocal;
import com.qunar.im.base.module.AtData;
import com.qunar.im.base.module.CalendarTrip;
import com.qunar.im.base.module.CollectionConversation;
import com.qunar.im.base.module.DepartmentItem;
import com.qunar.im.base.module.GroupMember;
import com.qunar.im.base.module.IMGroup;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.IMSessionList;
import com.qunar.im.base.module.MedalsInfo;
import com.qunar.im.base.module.MucListResponse;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.base.module.PublishPlatformNews;
import com.qunar.im.base.module.QuickReplyData;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.module.SetLikeDataResponse;
import com.qunar.im.base.module.UserConfigData;
import com.qunar.im.base.module.WorkWorldDetailsCommenData;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNewCommentBean;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.base.module.WorkWorldNoticeTimeData;
import com.qunar.im.base.module.WorkWorldOutCommentBean;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataCenter;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.enums.MessageState;
import com.qunar.im.core.helper.DatabaseHelper;
import com.qunar.im.core.intf.IQuery;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.data.cache.IMUserCacheManager;
import com.qunar.im.log.LogConstans;
import com.qunar.im.log.LogService;
import com.qunar.im.log.QLog;
import com.qunar.im.other.CacheDataType;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.entity.XMPPJID;
import com.qunar.im.protobuf.utils.JSONUtils;
import com.qunar.im.protobuf.utils.StringUtils;
import com.qunar.im.protobuf.utils.XmlUtils;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.MD5;
import com.qunar.im.utils.QtalkStringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Created by may on 2017/7/5.
 */

public class IMDatabaseManager {
    private static final long DBDefalultTimeThreshold = 200;//sql耗时阀值 单位毫秒
    private static IMDatabaseManager instance = new IMDatabaseManager();
    private String dataCachePath;
    private String username;
    private volatile boolean initialized = false;
    private Context context;
    private String filename;
    private DatabaseHelper helper;

    private Map<String, List<AtInfo>> AtMessageMap = Collections.synchronizedMap(new HashMap<String, List<AtInfo>>());

    protected IMDatabaseManager() {

    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void initialize(String username, Context context) {
        this.username = username;
        this.context = context;
        String navurl = DataUtils.getInstance(CommonConfig.globalContext).getPreferences(QtalkNavicationService.NAV_CONFIG_CURRENT_URL, "");
        dataCachePath = context.getDatabasePath(username).getAbsolutePath() + "_" + QtalkNavicationService.getInstance().getXmppdomain() + "_" + CommonConfig.isDebug + "_" + MD5.hex(navurl) + ".dat";
        Logger.i("数据库路径:" + dataCachePath + "  导航地址：" + navurl);
        initDB();
    }

    public static IMDatabaseManager getInstance() {

        return instance;
    }

    public void deleteJournal() {
        String dat_journal = dataCachePath + "-journal";
        File file_journal = new File(dat_journal);
        if (file_journal.exists()) {
            Logger.i("数据库有临时文件:进行了删除");
            file_journal.delete();
        }
    }

    public void initDB() {

        synchronized (this) {
            if (!initialized) {
                helper = new DatabaseHelper(context, dataCachePath);
                this.checkMessageState(3600 * 24);
                initialized = true;
            }
        }
    }


    private void checkMessageState(double timeRange) {
        long now = System.currentTimeMillis();
        long lastLoginTime = IMUserCacheManager.getInstance().getLongConfig("IM_LastLoginTime");

        if (lastLoginTime <= 0) {
            lastLoginTime = now;
        }
        double distance = now - lastLoginTime;
        double errorTime = IMUserCacheManager.getInstance().getDoubleConfig("kGetSingleHistoryMsgError");

        if (distance > timeRange || (now - errorTime > timeRange)) {
            IMUserCacheManager.getInstance().removeConfig("kGetSingleHistoryMsgError");
            this.removeAllMessages();
        }
    }

    private void removeAllMessages() {

    }

    /**
     * 手动checkpoint
     */
    public void manualCheckPoint() {
        String sql = "PRAGMA wal_checkpoint;";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询群组版本号
     *
     * @return
     */
    public long getGroupLastUpdateTime() {
        deleteJournal();
        String sql = "select Max(LastUpdateTime) from IM_Group;";
        Object result = query(sql, null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                long time = 0;
                try {
                    while (cursor.moveToNext()) {
                        time = cursor.getLong(0);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return time;
            }
        });
        if (result == null) {
            return 0;
        } else {
            return (long) result;
        }
    }


    /**
     * 获取最后一条消息时间,
     * 这条消息一定是正常状态,即为 1
     * 防止出现时间戳时间错乱,历史消息拿的有问题
     *
     * @return
     */
    public long getLastestMessageTime() {
        deleteJournal();
        String sql = "select valueInt from IM_Cache_Data where key=? and type=?";
        Object result = query(sql, new String[]{CacheDataType.lastUpdateTimeValue, String.valueOf(CacheDataType.lastUpdateTimeValueType)}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                long start = System.currentTimeMillis();
                long value = 0;
                try {
                    while (cursor.moveToNext()) {
                        value = cursor.getLong(0);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                Logger.i("getLastestMessageTime" + (System.currentTimeMillis() - start));
                return value;
            }
        });
        if (result == null) {
            return 0;
        } else {
            return (long) result;
        }

    }

    /**
     * 获取最后一条消息时间,
     * 这条消息一定是正常状态,即为 1
     * 防止出现时间戳时间错乱,历史消息拿的有问题
     *
     * @return
     */
    public String getLastestMessageId() {
        deleteJournal();
        String result = "";
        String sql = "select MsgId from IM_Message where (state&" + MessageStatus.LOCAL_STATUS_SUCCESS + ")=" + MessageStatus.LOCAL_STATUS_SUCCESS + " order by lastupdatetime desc limit 1";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, null);
        try {


            while (cursor.moveToNext()) {
                result = cursor.getString(0);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;

    }


    /**
     * 根据xmppid查出本会话的最早的一条消息
     *
     * @param xmppid
     * @return
     */
    public long getFirstMessageTimeByXmppId(String xmppid) {
        deleteJournal();
        String sql = "select LastUpdateTime from IM_Message where xmppid = ? and RealJid = ? order by LastUpdateTime  limit 1";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{xmppid});
        long result = 0;
        try {
            while (cursor.moveToNext()) {
                result = cursor.getLong(0);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 根据xmppid查出本会话的最早的一条消息
     *
     * @param xmppid
     * @return
     */
    public long getFirstMessageTimeByXmppIdAndRealJid(String xmppid, String realJid) {
        deleteJournal();
        String sql = "select LastUpdateTime from IM_Message where xmppid = ? and RealJid = ? order by LastUpdateTime  limit 1";
        Object result = query(sql, new String[]{xmppid, realJid}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                long time = 0;
                try {
                    while (cursor.moveToNext()) {
                        time = cursor.getLong(0);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return time;
            }
        });
        if (result == null) {
            return 0;
        } else {
            return (long) result;
        }
    }

    /**
     * 获取当前数据库所有人员数量
     *
     * @return
     */
    public int getLastIncrementUsersVersion() {
        deleteJournal();
        String sql = "select valueInt from IM_Cache_Data where key=? and type=?";
        Object result = query(sql, new String[]{CacheDataType.lastIncrementUserVersion, String.valueOf(CacheDataType.lastIncrementUser)}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                int count = 0;
                try {
                    while (cursor.moveToNext()) {
                        count = cursor.getInt(0);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return count;
            }
        });
        if (result == null) {
            return 0;
        } else {
            return (int) result;
        }
    }

    public List getGroupListMsgMaxTime() {
        deleteJournal();
        Vector<HashMap> groupList = new Vector<>();

        String sql = "Select a.GroupId,max(b.LastUpdateTime) FROM IM_Group as a Left join IM_Message as b on a.GroupId = b.XmppId Group by a.GroupId;";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, null);
        try {


            while (cursor.moveToNext()) {
                String groupId = cursor.getString(0);
                long lastUpdateTime = cursor.getLong(1);
                HashMap<String, Long> item = new HashMap();
                item.put(groupId, lastUpdateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 更新所有的发送中状态为失败
     */
    public void updateMessageStateFailed() {
        String sql = "update IM_Message  set State = 0 where (State&" + MessageStatus.LOCAL_STATUS_PROCESSION + ")" +
                "==" + MessageStatus.LOCAL_STATUS_PROCESSION + " and (State&" + MessageStatus.LOCAL_STATUS_SUCCESS + ")<>" + MessageStatus.LOCAL_STATUS_SUCCESS + "";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransactionNonExclusive();
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }


    }


    public boolean checkGroupByJid(String jid) {
        deleteJournal();
        if (!TextUtils.isEmpty(jid)) {
            String sql = "Select * from IM_Group_Member Where GroupId  = ? and MemberId = ?";
            Object result = query(sql, new String[]{jid, CurrentPreference.getInstance().getPreferenceUserId()}, new IQuery() {
                @Override
                public Object onQuery(Cursor cursor) {
                    boolean check = false;
                    try {
                        if (cursor.moveToNext()) {
                            check = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return check;
                }
            });
            if (result == null) {
                return false;
            }
            return (boolean) result;
        }

        return false;
    }

    //获取群信息
    public JSONObject selectMucByGroupId(final String groupId) {
        deleteJournal();
        if (StringUtils.isNotEmpty(groupId)) {
            String sql = "Select GroupId,Name,Introduce,HeaderSrc,Topic,LastUpdateTime,ExtendedFlag from IM_Group Where GroupId  = ?";
            Object result = query(sql, new String[]{groupId}, new IQuery() {
                @Override
                public Object onQuery(Cursor cursor) {
                    JSONObject mucObject = new JSONObject();
                    try {
                        if (cursor.moveToNext()) {
                            try {
                                JSONUtils.putStringValue(mucObject, "GroupId", cursor.getString(0));
                                String str = cursor.getString(1);
                                JSONUtils.putStringValue(mucObject, "Name", StringUtils.isNotEmpty(str) ? str : groupId);
                                JSONUtils.putStringValue(mucObject, "Introduce", cursor.getString(2));
                                JSONUtils.putStringValue(mucObject, "HeaderSrc", cursor.getString(3));
                                JSONUtils.putStringValue(mucObject, "Topic", cursor.getString(4));
                                mucObject.put("LastUpdateTime", cursor.getInt(5));
                                JSONUtils.putStringValue(mucObject, "ExtendedFlag", cursor.getString(6));

                            } catch (JSONException e) {
                                Logger.e(e, "selectMucByGroupId Json Parse failed.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return mucObject;
                }
            });
            if (result == null) {
                return new JSONObject();
            } else {
                return (JSONObject) result;
            }
        }
        return null;
    }

    public JSONObject selectCollectionMucByGroupId(final String groupId) {
        deleteJournal();
        if (StringUtils.isNotEmpty(groupId)) {
            String sql = "Select GroupId,Name,Introduce,HeaderSrc,Topic,LastUpdateTime,ExtendedFlag from IM_Collection_Group_Card Where GroupId  = ?";
            Object result = query(sql, new String[]{groupId}, new IQuery() {
                @Override
                public Object onQuery(Cursor cursor) {
                    JSONObject mucObject = new JSONObject();
                    try {
                        if (cursor.moveToNext()) {
                            try {
                                JSONUtils.putStringValue(mucObject, "GroupId", cursor.getString(0));
                                String str = cursor.getString(1);
                                JSONUtils.putStringValue(mucObject, "Name", StringUtils.isNotEmpty(str) ? str : groupId);
                                JSONUtils.putStringValue(mucObject, "Introduce", cursor.getString(2));
                                JSONUtils.putStringValue(mucObject, "HeaderSrc", cursor.getString(3));
                                JSONUtils.putStringValue(mucObject, "Topic", cursor.getString(4));
                                mucObject.put("LastUpdateTime", cursor.getInt(5));
                                JSONUtils.putStringValue(mucObject, "ExtendedFlag", cursor.getString(6));

                            } catch (JSONException e) {
                                Logger.e(e, "selectMucByGroupId Json Parse failed.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return mucObject;
                }
            });
            if (result == null) {
                return new JSONObject();
            } else {
                return (JSONObject) result;
            }
        }
        return null;
    }

    public int selectGroupMemberPermissionsByGroupIdAndMemberId(String groupId, String memberId) {
        deleteJournal();
        String sql = "Select Affiliation from IM_Group_Member where GroupId = ? and MemberId = ?";
        Object result = query(sql, new String[]{groupId, memberId}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                int per = -1;
                try {
                    if (cursor.moveToNext()) {
                        per = cursor.getInt(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    return per;
                }
            }
        });
        if (result == null) {
            return -1;
        } else {
            return (int) result;
        }
    }


    //根据群Id获取群成员列表
    public List<GroupMember> SelectGroupMemberByGroupId(String groupId) {
        deleteJournal();
        String sql = "Select a.*,b.Searchindex,b.Name,b.HeaderSrc from IM_Group_Member as a left join IM_User as b on a.MemberId = b.XmppId  WHERE GroupId = ? order by Affiliation";
        Object result = query(sql, new String[]{groupId}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<GroupMember> groupMemberList = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        String fuzzy = cursor.getString(7);
                        String name = cursor.getString(8);
                        String headerSrc = cursor.getString(9);

                        GroupMember gm = new GroupMember();
                        gm.setMemberId(cursor.getString(0));
                        gm.setGroupId(cursor.getString(1));
                        gm.setMemberJid(cursor.getString(2));
                        gm.setName(name);
                        gm.setAffiliation(cursor.getString(4));
                        gm.setLastUpdateTime(cursor.getString(5));
                        gm.setExtendedFlag(cursor.getString(6));
                        gm.setFuzzy(fuzzy);
                        gm.setHeaderSrc(headerSrc);
                        groupMemberList.add(gm);
                    }
                } catch (Exception e) {
                    Logger.i("SelectGroupMemberByGroupId crash " + e.getLocalizedMessage());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return groupMemberList;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        } else {
            return (List<GroupMember>) result;
        }
    }


    //获取用户信息
    public Nick selectUserByName(String name) {
        deleteJournal();
        if (StringUtils.isNotEmpty(name)) {
            String sql = "Select UserId, XmppId, Name, DescInfo, HeaderSrc, UserInfo,LastUpdateTime,SearchIndex from IM_User Where Name = ?;";
            Object result = query(sql, new String[]{name}, new IQuery() {
                @Override
                public Object onQuery(Cursor cursor) {
                    Nick nick = new Nick();
                    try {
                        if (cursor.moveToNext()) {
                            nick.setUserId(cursor.getString(0));
                            nick.setXmppId(cursor.getString(1));
                            nick.setName(cursor.getString(2));
                            nick.setDescInfo(cursor.getString(3));
                            nick.setHeaderSrc(cursor.getString(4));
                            nick.setUserInfo(cursor.getString(5));
                            nick.setLastUpdateTime(cursor.getString(6));
                            nick.setSearchIndex(cursor.getString(7));
                        }
                    } catch (Exception e) {

                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return nick;
                }
            });
            if (result == null) {
                return new Nick();
            }
            return (Nick) result;
        }
        return null;
    }

    //获取用户信息
    public JSONObject selectUserByJID(final String userId) {
        deleteJournal();
        if (StringUtils.isNotEmpty(userId)) {
            String sql = "Select UserId, XmppId, Name, DescInfo, HeaderSrc, UserInfo,LastUpdateTime,SearchIndex,mood from IM_User Where XmppId = ?;";
            Object result = query(sql, new String[]{userId}, new IQuery() {
                @Override
                public Object onQuery(Cursor cursor) {
                    JSONObject userObject = new JSONObject();
                    try {
                        if (cursor.moveToNext()) {
                            try {
                                JSONUtils.putStringValue(userObject, "UserId", cursor.getString(0));
                                JSONUtils.putStringValue(userObject, "XmppId", cursor.getString(1));
                                String str = cursor.getString(2);
                                JSONUtils.putStringValue(userObject, "Name", StringUtils.isNotEmpty(str) ? str : userId);
                                JSONUtils.putStringValue(userObject, "DescInfo", cursor.getString(3));
                                JSONUtils.putStringValue(userObject, "HeaderSrc", cursor.getString(4));
                                userObject.put("UserInfo", cursor.getBlob(5));
                                userObject.put("LastUpdateTime", cursor.getInt(6));
                                JSONUtils.putStringValue(userObject, "SearchIndex", cursor.getString(7));
                                JSONUtils.putStringValue(userObject, "mood", cursor.getString(8));
                            } catch (JSONException e) {
                                Logger.e(e, "selectUserByJID Json Parse failed.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return userObject;
                }
            });
            if (result == null) {
                return new JSONObject();
            }
            return (JSONObject) result;
        }
        return null;
    }

    /**
     * 获取所有组织架构人员
     *
     * @return
     */
    public List<GetDepartmentResult.UserItem> getAllOrgaUsers() {
        String sql = "Select UserId,Name, DescInfo,XmppId from IM_User;";
        Object result = query(sql, null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<GetDepartmentResult.UserItem> list = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        String u = cursor.getString(0);
                        String n = cursor.getString(1);
                        String d = cursor.getString(2);
                        String xmppid = cursor.getString(3);
                        if (!TextUtils.isEmpty(xmppid) && xmppid.endsWith(QtalkNavicationService.getInstance().getXmppdomain())) {
                            GetDepartmentResult.UserItem userItem = new GetDepartmentResult.UserItem();
                            userItem.U = u;
                            userItem.N = n;
                            userItem.D = d == null ? "/Staff" : ("/Staff" + d);
                            list.add(userItem);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        }
        return (List<GetDepartmentResult.UserItem>) result;
    }

    /**
     * 代收用户名片
     *
     * @param userId
     * @return
     */
    public JSONObject selectCollectionUserByJID(final String userId) {
        deleteJournal();
        if (StringUtils.isNotEmpty(userId)) {
            String sql = "Select UserId, XmppId, Name, DescInfo, HeaderSrc, UserInfo,LastUpdateTime,SearchIndex from IM_Collection_User_Card Where XmppId = ?;";
            Object result = query(sql, new String[]{userId}, new IQuery() {
                @Override
                public Object onQuery(Cursor cursor) {
                    JSONObject userObject = new JSONObject();
                    try {
                        if (cursor.moveToNext()) {
                            try {
                                JSONUtils.putStringValue(userObject, "UserId", cursor.getString(0));
                                JSONUtils.putStringValue(userObject, "XmppId", cursor.getString(1));
                                String str = cursor.getString(2);
                                JSONUtils.putStringValue(userObject, "Name", StringUtils.isNotEmpty(str) ? str : userId);
                                JSONUtils.putStringValue(userObject, "DescInfo", cursor.getString(3));
                                JSONUtils.putStringValue(userObject, "HeaderSrc", cursor.getString(4));
                                userObject.put("UserInfo", cursor.getBlob(5));
                                userObject.put("LastUpdateTime", cursor.getInt(6));
                                JSONUtils.putStringValue(userObject, "SearchIndex", cursor.getString(7));

                            } catch (JSONException e) {
                                Logger.e(e, "selectUserByJID Json Parse failed.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return userObject;
                }
            });
            if (result == null) {
                return new JSONObject();
            } else {
                return (JSONObject) result;
            }
        }
        return null;
    }

    //获取群名片
    public JSONObject getMucInfos(List<String> mucIds) throws JSONException {
        deleteJournal();
        String sql = "Select GroupId,Name,Introduce,HeaderSrc,Topic,LastUpdateTime,ExtendedFlag from IM_Group Where GroupId in(";
        StringBuilder sb = new StringBuilder(sql);
        for (int i = 0; i < mucIds.size(); ++i) {
            if (i >= mucIds.size() - 1) {
                sb.append(String.format("'%s');", mucIds.get(i)));
            } else {
                sb.append(String.format("'%s', ", mucIds.get(i)));
            }
        }
        Object result = query(sb.toString(), null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                JSONObject jsonObject = new JSONObject();
                try {
                    while (cursor.moveToNext()) {
                        String GroupId = cursor.getString(0);
                        if (GroupId == null)
                            continue;
                        String Name = cursor.getString(1);
                        String Introduce = cursor.getString(2);
                        String HeaderSrc = cursor.getString(3);
                        String Topic = cursor.getString(4);

                        long LastUpdateTime = cursor.getLong(5);
                        String ExtendedFlag = cursor.getString(6);

                        JSONObject itemObject = new JSONObject();
                        JSONUtils.putStringValue(itemObject, "GroupId", GroupId);
                        JSONUtils.putStringValue(itemObject, "Name", Name);
                        JSONUtils.putStringValue(itemObject, "Introduce", Introduce);
                        JSONUtils.putStringValue(itemObject, "HeaderSrc", HeaderSrc);
                        JSONUtils.putStringValue(itemObject, "Topic", Topic);
                        itemObject.put("LastUpdateTime", LastUpdateTime);
                        JSONUtils.putStringValue(itemObject, "ExtendedFlag", ExtendedFlag);

                        jsonObject.put(GroupId, itemObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return jsonObject;
            }
        });
        if (result == null) {
            return new JSONObject();
        } else {
            return (JSONObject) result;
        }
    }

    //获取单人名片
    public JSONObject getUserInfos(List<String> userIDs) throws JSONException {
        deleteJournal();
        String sql = "Select UserId, XmppId, Name, DescInfo, HeaderSrc, UserInfo,LastUpdateTime,SearchIndex from IM_User Where XmppId in (";
        StringBuilder sb = new StringBuilder(sql);

        for (int i = 0; i < userIDs.size(); ++i) {
            if (i >= userIDs.size() - 1) {
                sb.append(String.format("'%s');", userIDs.get(i)));
            } else {
                sb.append(String.format("'%s', ", userIDs.get(i)));
            }
        }
        Object result = query(sb.toString(), null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                JSONObject jsonObject = new JSONObject();
                try {
                    while (cursor.moveToNext()) {
                        String userId = cursor.getString(0);
                        if (userId == null)
                            continue;
                        String xmppId = cursor.getString(1);
                        String name = cursor.getString(2);
                        String descInfo = cursor.getString(3);
                        String headerSrc = cursor.getString(4);
                        String userInfo = cursor.getString(5);
                        long lastUpdateTime = cursor.getLong(6);
                        String searchIndex = cursor.getString(7);

                        JSONObject itemObject = new JSONObject();
                        JSONUtils.putStringValue(itemObject, "UserId", userId);
                        JSONUtils.putStringValue(itemObject, "XmppId", xmppId);
                        JSONUtils.putStringValue(itemObject, "Name", name);
                        JSONUtils.putStringValue(itemObject, "DescInfo", descInfo);
                        JSONUtils.putStringValue(itemObject, "HeaderSrc", headerSrc);
                        JSONUtils.putStringValue(itemObject, "UserInfo", userInfo);
                        itemObject.put("LastUpdateTime", lastUpdateTime);
                        JSONUtils.putStringValue(itemObject, "SearchIndex", searchIndex);

                        jsonObject.put(xmppId, itemObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return jsonObject;
            }
        });
        if (result == null) {
            return new JSONObject();
        } else {
            return (JSONObject) result;
        }
    }

    /**
     * 根据所在群组和以离开群组对数据库中的本地群组进行更改
     *
     * @param okList
     * @param noList
     */
    public void updateMucList(List<MucListResponse.Data> okList, List<MucListResponse.Data> noList) {
        String insertSql = "insert or ignore into IM_Group (GroupId,LastUpdateTime) values(?,?);";
        String updateSql = "update IM_Group set LastUpdateTime = ?  where GroupId = ?";
        String deleteGroup = "Delete from IM_Group Where  GroupId = ? ";
        String deleteSession = "Delete from IM_SessionList where XmppId = ?";
        String deleteMessage = "Delete from IM_Message Where XmppId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            SQLiteStatement istat = db.compileStatement(insertSql);
            SQLiteStatement ustat = db.compileStatement(updateSql);
            SQLiteStatement dgstat = db.compileStatement(deleteGroup);
            SQLiteStatement dsstat = db.compileStatement(deleteSession);
            SQLiteStatement dmstat = db.compileStatement(deleteMessage);
            for (int i = 0; i < okList.size(); i++) {
                MucListResponse.Data od = okList.get(i);
                istat.bindString(1, od.getM() + "@" + od.getD());
                ustat.bindString(1, od.getT());
                istat.bindString(2, od.getT());
                ustat.bindString(2, od.getM() + "@" + od.getD());
                istat.executeInsert();
                ustat.executeUpdateDelete();
            }
            for (int i = 0; i < noList.size(); i++) {
                MucListResponse.Data nd = noList.get(i);
                dgstat.bindString(1, nd.getM() + "@" + nd.getD());
                dsstat.bindString(1, nd.getM() + "@" + nd.getD());
                dmstat.bindString(1, nd.getM() + "@" + nd.getD());
                dgstat.executeUpdateDelete();
                dsstat.executeUpdateDelete();
                dmstat.executeUpdateDelete();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void updateMucCard(JSONArray dataList) {
        String insertReplace = "insert or ignore into IM_Group (GroupId,Name,Introduce,HeaderSrc,Topic,ExtendedFlag,LastUpdateTime) values(?,?,?,?,?,?,?);";
        String updateSql = "update IM_Group set Name =?,Introduce=?,HeaderSrc = ?,Topic = ? ,LastUpdateTime = ? where GroupId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();

        try {
            SQLiteStatement stat = db.compileStatement(insertReplace);
            SQLiteStatement ustat = db.compileStatement(updateSql);
            for (int i = 0; i < dataList.length(); ++i) {
//
                JSONObject muc = dataList.getJSONObject(i);
                String GroupId = JSONUtils.getStringValue(muc, "MN");
                String Name = JSONUtils.getStringValue(muc, "SN");
                String Introduce = JSONUtils.getStringValue(muc, "MD");
                String HeaderSrc = JSONUtils.getStringValue(muc, "MP");
                if (TextUtils.isEmpty(HeaderSrc)) {//没有的话 插入默认头像
                    HeaderSrc = QtalkNavicationService.getInstance().getInnerFiltHttpHost() + "/file/v2/download/perm/2227ff2e304cb44a1980e9c1a3d78164.png";
                }
                String Topic = JSONUtils.getStringValue(muc, "MT");
                String LastUpdateTime = JSONUtils.getStringValue(muc, "VS");

                ustat.bindString(1, Name);
                ustat.bindString(2, Introduce);
                ustat.bindString(3, HeaderSrc);
                ustat.bindString(4, Topic);
                ustat.bindString(5, LastUpdateTime);
                ustat.bindString(6, GroupId);
                int count = ustat.executeUpdateDelete();
                if(count <= 0){
                    stat.bindString(1, GroupId);
                    stat.bindString(2, Name);
                    stat.bindString(3, Introduce);
                    stat.bindString(4, HeaderSrc);
                    stat.bindString(5, Topic);
                    stat.bindString(6, "");
                    stat.bindString(7,LastUpdateTime);
                    stat.executeInsert();
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "updateMucCard crashed.");

        } finally {
            db.endTransaction();
        }
    }

    /**
     * 更新群信息
     *
     * @param dataList
     */
    public void updateMucCard(List<Nick> dataList) {
        String insertReplace = "insert or ignore into IM_Group (GroupId,Name,Introduce,HeaderSrc,Topic,ExtendedFlag) values(?,?,?,?,?,?);";
        String updateSql = "update IM_Group set Name =?,Introduce=?,HeaderSrc = ?,Topic = ? where GroupId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();

        HashSet<String> existsMuc = new HashSet<>();

        db.beginTransactionNonExclusive();

        try {
            SQLiteStatement stat = db.compileStatement(insertReplace);
            SQLiteStatement ustat = db.compileStatement(updateSql);
            for (int i = 0; i < dataList.size(); ++i) {
                Nick muc = dataList.get(i);
                stat.bindString(1, muc.getGroupId());
                stat.bindString(2, muc.getName());
                stat.bindString(3, muc.getIntroduce());
                stat.bindString(4, muc.getHeaderSrc());
                stat.bindString(5, muc.getTopic());
//                stat.bindString(6, muc.getLastUpdateTime());
                stat.bindString(6, muc.getExtendedFlag());
                ustat.bindString(1, muc.getName());
                ustat.bindString(2, muc.getIntroduce());
                ustat.bindString(3, muc.getHeaderSrc());
                ustat.bindString(4, muc.getTopic());
                ustat.bindString(5, muc.getGroupId());
                ustat.executeUpdateDelete();

                long result = stat.executeInsert();

                if (result < 0) {
                    existsMuc.add(muc.getGroupId());
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "updateMucCard crashed.");

        } finally {
            db.endTransaction();
        }
    }

    /**
     * 根据群名模糊查找群
     *
     * @param ser
     * @param limit
     * @return
     */
    public List<IMGroup> SelectIMGroupByLike(String ser, int limit) {
        deleteJournal();
        String sql = "select * from IM_Group where Name like ? order by Name desc limit ?";
        Object result = query(sql, new String[]{"%" + ser + "%", limit + ""}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<IMGroup> list = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        IMGroup imGroup = new IMGroup();
                        imGroup.setGroupId(cursor.getString(0));
                        imGroup.setName(cursor.getString(1));
                        imGroup.setIntroduce(cursor.getString(2));
                        imGroup.setHeaderSrc(cursor.getString(3));
                        imGroup.setTopic(cursor.getString(4));
                        imGroup.setLastUpdateTime(cursor.getString(5));
                        imGroup.setExtendedFlag(cursor.getString(6));
                        list.add(imGroup);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        } else {
            return (List<IMGroup>) result;
        }
    }

    /**
     * 查询所有群
     *
     * @return
     */
    public List<String> SelectIMGroupId() {
        deleteJournal();
        String sql = "select GroupId from IM_Group ";
        Object result = query(sql, null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<String> list = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {

                        list.add(cursor.getString(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        } else {
            return (List<String>) result;
        }
    }

    /**
     * 返回根据模糊查询的联系人列表
     *
     * @param ser
     * @param limit
     * @return
     */
    public List<Nick> SelectContactsByLike(String ser, int limit) {
        deleteJournal();
        String sql = "select * from IM_User where SearchIndex like  ?  or Name like  ?  or UserId like ? order by Name desc limit ?";
        Object result = query(sql, new String[]{"%" + ser + "%", "%" + ser + "%", "%" + ser + "%", limit + ""}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<Nick> list = new ArrayList<>();
                int id = 0;
                try {
                    while (cursor.moveToNext()) {
                        Nick nick = new Nick();
                        id++;
                        nick.setId(id);
                        nick.setUserId(cursor.getString(0));
                        nick.setXmppId(cursor.getString(1));
                        nick.setName(cursor.getString(2));
                        nick.setDescInfo(cursor.getString(3));
                        nick.setHeaderSrc(cursor.getString(4));
                        nick.setSearchIndex(cursor.getString(5));
                        nick.setUserInfo(cursor.getString(6));
                        nick.setLastUpdateTime(cursor.getString(7));
                        nick.setIncrementVersion(cursor.getColumnName(8));
                        list.add(nick);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        } else {
            return (List<Nick>) result;
        }
    }

    /**
     * 插入qchat组织架构
     *
     * @param ditems
     * @param isReplase
     */
    public void insertQchatOrgDatas(List<DepartmentItem> ditems, boolean isReplase) {
        String insertSql = "insert or IGNORE into IM_User (UserId,XmppId,Name,DescInfo,SearchIndex,LastUpdateTime) values(?,?,?,?,?,0);";

//        SQLiteDatabase db = helper.getWritableDatabase();
//
//        db.beginTransactionNonExclusive();
//        SQLiteStatement insertStat = db.compileStatement(insertSql);
//        try {
//            int count = ditems == null ? 0 : ditems.size();
//            for (int i = 0; i < count; i++) {
//                DepartmentItem item = ditems.get(i);
//                insertStat.bindString(1, item.qsid + "");
//                insertStat.bindString(2, item.qid + "");
//                insertStat.bindString(3, item.version);
//                insertStat.bindString(4, item.type + "");
//                insertStat.bindString(5, item.title);
//                insertStat.bindString(6, TextUtils.isEmpty(item.desc) ? "" : item.desc);on
//                insertStat.executeInsert();
//            }
//
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            Logger.e(e, "insertMultiPasswordBoxSub crashed.");
//        } finally {
//            db.endTransaction();
//        }
    }

    /**
     * 获取用户所有群组
     *
     * @return
     */
    public List<Nick> SelectAllGroupCard() {
        deleteJournal();
        String sql = "select * from IM_Group";
        Object result = query(sql, null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<Nick> list = new ArrayList<>();
                int id = 0;
                try {


                    while (cursor.moveToNext()) {
                        Nick nick = new Nick();
                        id++;
                        nick.setId(id);
                        nick.setGroupId(cursor.getString(0));
                        nick.setName(cursor.getString(1));
                        nick.setIntroduce(cursor.getString(2));
                        nick.setHeaderSrc(cursor.getString(3));
                        nick.setTopic(cursor.getString(4));
                        list.add(nick);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        } else {
            return (List<Nick>) result;
        }
    }


    /**
     * 搜索全部数据
     *
     * @param text
     * @return
     */
    public List<String> SelectAllUserXmppIdListBySearchText(String text) {
        deleteJournal();
        String sql = "select * from IM_User Where (UserId like ? or Name like  ? or SearchIndex like ?)  order by UserId limit 100  ";
        Object result = query(sql, new String[]{"%" + text + "%", "%" + text + "%", "%" + text + "%"}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<String> list = new ArrayList<>();
                int id = 0;
                try {
                    while (cursor.moveToNext()) {
//                        Nick nick = new Nick();
                        id++;
//                        nick.setId(id);
//                        nick.setUserId(cursor.getString(0));
//                        nick.setXmppId(cursor.getString(1));
//                        nick.setName(cursor.getString(2));
//                        nick.setDescInfo(cursor.getString(3));
//                        nick.setHeaderSrc(cursor.getString(4));
//                        nick.setSearchIndex(cursor.getString(5) + "|" + cursor.getString(0) + "|" + cursor.getString(1) + "|" + cursor.getString(2));
//                        nick.setUserInfo(cursor.getString(6));
//                        nick.setLastUpdateTime(cursor.getString(7));
//                        nick.setIncrementVersion(cursor.getColumnName(8));
                        list.add(cursor.getString(1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        }
        return (List<String>) result;
    }


    /**
     * 在指定群中搜索用户
     *
     * @param groupId
     * @param text
     * @return
     */
    public List<Nick> SelectUserListBySearchText(String groupId, String text) {
        deleteJournal();
        String sql = "select * ,(case when xmppid in (select MemberId from IM_Group_Member where GroupId = ?) then 1 else 0 END) as isInGroup from IM_User as a Where (UserId like ? or Name like  ? or SearchIndex like ?) order by UserId limit 100 ";
        Object result = query(sql, new String[]{groupId, "%" + text + "%", "%" + text + "%", "%" + text + "%"}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<Nick> list = new ArrayList<>();
                int id = 0;
                try {
                    while (cursor.moveToNext()) {
                        Nick nick = new Nick();
                        id++;
                        nick.setId(id);
                        nick.setUserId(cursor.getString(0));
                        nick.setXmppId(cursor.getString(1));
                        nick.setName(cursor.getString(2));
                        nick.setDescInfo(cursor.getString(3));
                        nick.setHeaderSrc(cursor.getString(4));
                        nick.setSearchIndex(cursor.getString(5) + "|" + cursor.getString(0) + "|" + cursor.getString(1) + "|" + cursor.getString(2));
                        nick.setUserInfo(cursor.getString(6));
                        nick.setLastUpdateTime(cursor.getString(7));
                        nick.setIncrementVersion(cursor.getColumnName(8));
                        nick.setInGroup(cursor.getInt(11) == 1);
                        list.add(nick);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        }
        return (List<Nick>) result;
    }

    /**
     * 查询好友 群加人点选
     *
     * @param groupId
     * @return
     */
    public List<Nick> selectFriendListForGroupAdd(String groupId) {
        String sql = "select a.UserId,a.XmppId,b.Name,b.HeaderSrc,b.SearchIndex from IM_Friend_List as a join IM_User as b where a.UserId = b.UserId " +
                "and a.XmppId NOT IN(select MemberId from IM_Group_Member where GroupId = ?)";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{groupId});
        List<Nick> list = new ArrayList<>();
        int id = 0;
        try {
            while (cursor.moveToNext()) {
                Nick nick = new Nick();
                id++;
                nick.setId(id);
                nick.setUserId(cursor.getString(0));
                nick.setXmppId(cursor.getString(1));
                nick.setName(cursor.getString(2));
                nick.setHeaderSrc(cursor.getString(3));
                nick.setSearchIndex(cursor.getString(4));
                list.add(nick);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 群踢人
     *
     * @param groupId
     * @return
     */
    public List<Nick> selectGroupMemberForKick(String groupId) {
        String sql = "select a.MemberId,a.Affiliation,b.UserId,b.Name,b.HeaderSrc,b.SearchIndex from IM_Group_Member as a left join IM_User as b  where a.MemberId = b.XmppId and GroupId = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{groupId});
        List<Nick> list = new ArrayList<>();
        List<Integer> affiliations = new ArrayList<>();
        int id = 0;
        int selfAffiliation = -1;
        try {
            while (cursor.moveToNext()) {
                Nick nick = new Nick();
                id++;
                nick.setId(id);
                String xmppId = cursor.getString(0);
                int per = cursor.getInt(1);
                if (CurrentPreference.getInstance().getPreferenceUserId().equals(xmppId)) {
                    selfAffiliation = per;
                }
                nick.setXmppId(xmppId);
                nick.setUserId(cursor.getString(2));
                nick.setName(cursor.getString(3));
                nick.setHeaderSrc(cursor.getString(4));
                nick.setSearchIndex(cursor.getString(5));
                affiliations.add(per);
                list.add(nick);
            }
            for (int i = 0; i < affiliations.size(); i++) {
                if (selfAffiliation >= affiliations.get(i)) {
                    list.remove(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 根据groupid&searchindex搜索群成员
     *
     * @param groupId
     * @param searchIndex
     * @return
     */
    public List<Nick> selectMemberFromGroup(String groupId, String searchIndex) {
        String sql = "select a.MemberId,b.UserId,b.Name,b.HeaderSrc from IM_Group_Member as a left join IM_User as b  where a.MemberId = b.XmppId and b.SearchIndex like ? and GroupId = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{"%" + searchIndex + "%", groupId});
        List<Nick> list = new ArrayList<>();
        int id = 0;
        try {
            while (cursor.moveToNext()) {
                Nick nick = new Nick();
                id++;
                nick.setId(id);
                nick.setXmppId(cursor.getString(0));
                nick.setUserId(cursor.getString(1));
                nick.setName(cursor.getString(2));
                nick.setHeaderSrc(cursor.getString(3));
                list.add(nick);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 不在星标里面的好友
     *
     * @return
     */
    public List<Nick> selectFriendsNotInStarContacts() {
        String sql = "select a.UserId,a.XmppId,b.Name,b.HeaderSrc from IM_Friend_List as a left join IM_User as b on a.XmppId = b.XmppId where a.XmppId not in (select subkey from IM_USER_CONFIG where pkey=? and isDel = ?);";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{CacheDataType.kStarContact, String.valueOf(CacheDataType.N)});
        List<Nick> list = new ArrayList<>();
        int id = 0;
        try {
            while (cursor.moveToNext()) {
                Nick nick = new Nick();
                id++;
                nick.setId(id);
                nick.setUserId(cursor.getString(0));
                nick.setXmppId(cursor.getString(1));
                nick.setName(cursor.getString(2));
                nick.setHeaderSrc(cursor.getString(3));
                list.add(nick);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 返回不在星标用户的user
     *
     * @param key 关键字
     * @return
     */
    public List<Nick> selectUserNotInStartContacts(String key) {
        String sql = "select UserId,XmppId,Name,HeaderSrc from IM_User Where (UserId like ? or Name like  ? or SearchIndex like ?) and XmppId NOT IN(select subkey from IM_USER_CONFIG where pkey = ? and isDel = ?) order by UserId limit 100; ";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{"%" + key + "%", "%" + key + "%", "%" + key + "%", CacheDataType.kStarContact, String.valueOf(CacheDataType.N)});
        List<Nick> list = new ArrayList<>();
        int id = 0;
        try {
            while (cursor.moveToNext()) {
                Nick nick = new Nick();
                id++;
                nick.setId(id);
                nick.setUserId(cursor.getString(0));
                nick.setXmppId(cursor.getString(1));
                nick.setName(cursor.getString(2));
                nick.setHeaderSrc(cursor.getString(3));
                list.add(nick);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 查询星标用户 nick
     *
     * @param pkey
     * @return
     */
    public List<Nick> selectStarOrBlackContactsAsNick(String pkey) {
        String sql = "select b.UserId,a.subkey,b.Name,b.HeaderSrc from IM_USER_CONFIG as a left JOIN IM_User as b on a.subkey = b.XmppId where a.isDel = ? and pkey= ?;";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(CacheDataType.N), pkey});
        List<Nick> list = new ArrayList<>();
        int id = 0;
        try {
            while (cursor.moveToNext()) {
                Nick nick = new Nick();
                id++;
                nick.setId(id);
                nick.setUserId(cursor.getString(0));
                nick.setXmppId(cursor.getString(1));
                nick.setName(cursor.getString(2));
                nick.setHeaderSrc(cursor.getString(3));
                list.add(nick);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 是否是星标联系人 黑名单
     *
     * @param xmppid
     * @param pkey
     * @return
     */
    public boolean isStarContact(String xmppid, String pkey) {
        String sql = "select count(*) from IM_USER_CONFIG where pkey= ? and subkey = ? and isDel = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{pkey, xmppid, String.valueOf(CacheDataType.N)});
        int count = 0;
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }

    /**
     * 查询所有的联系人
     *
     * @return
     */
    public List<Nick> SelectAllUserCard() {
        deleteJournal();
        String sql = "select * from IM_User";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, null);
        List<Nick> list = new ArrayList<>();
        int id = 0;
        try {


            while (cursor.moveToNext()) {
                Nick nick = new Nick();
                id++;
                nick.setId(id);
                nick.setUserId(cursor.getString(0));
                nick.setXmppId(cursor.getString(1));
                nick.setName(cursor.getString(2));
                nick.setDescInfo(cursor.getString(3));
                nick.setHeaderSrc(cursor.getString(4));
                nick.setSearchIndex(cursor.getString(5) + "|" + cursor.getString(0) + "|" + cursor.getString(1) + "|" + cursor.getString(2));
                nick.setUserInfo(cursor.getString(6));
                nick.setLastUpdateTime(cursor.getString(7));
                nick.setIncrementVersion(cursor.getColumnName(8));
                list.add(nick);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public void InsertGroupMemberByGM(GroupMember groupMember) {
        String sql = "INSERT or REPLACE INTO IM_Group_Member (MemberId,GroupId,Affiliation,LastUpdateTime) VALUES(?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            stat.bindString(1, groupMember.getMemberId());
            stat.bindString(2, groupMember.getGroupId());
            stat.bindString(3, groupMember.getAffiliation());
            stat.bindString(4, groupMember.getLastUpdateTime());
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    /**
     * 从数据库中删除群成员
     *
     * @param gm
     */
    public void DeleteGroupMemberByGM(GroupMember gm) {
        String sql = "Delete from IM_Group_Member Where MemberId = ? and GroupId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            stat.bindString(1, gm.getMemberId());
            stat.bindString(2, gm.getGroupId());
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    /**
     * 删除群和会话列表根绝xmppid 并且把该会话相关消息也进行删除
     *
     * @param gm
     */
    public void DeleteGroupAndSessionListByGM(IMGroup gm) {
        String deleteGroup = "Delete from IM_Group Where GroupId = ?";
        String deleteSession = "Delete from IM_SessionList Where XmppId = ?";
        String deleteMessage = "Delete from IM_Message Where Xmppid = ?";
        SQLiteDatabase db = helper.getReadableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement d1 = db.compileStatement(deleteGroup);
            SQLiteStatement d2 = db.compileStatement(deleteSession);
            SQLiteStatement d3 = db.compileStatement(deleteMessage);
            d1.bindString(1, gm.getGroupId());
            d2.bindString(1, gm.getGroupId());
            d3.bindString(1, gm.getGroupId());
            d1.executeUpdateDelete();
            d2.executeUpdateDelete();
            d3.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


    /**
     * 插入所有的联系人
     *
     * @param result
     */
    public void InsertUserCardInIncrementUser(IncrementUsersResult result) {
        if (result == null || result.data == null) return;
        long start = System.currentTimeMillis();
        int a = 0;
        int b = 0;
        int c = 0;
        int version = result.data.version;
        List<IncrementUsersResult.IncrementUser> updates = result.data.update;
        List<IncrementUsersResult.IncrementUser> deletes = result.data.delete;
        if (updates != null) {
            Logger.i("更新数据量:" + updates.size());
        }
        if (deletes != null) {
            Logger.i("删除数据量:" + deletes.size());
        }

        if (ListUtil.isEmpty(updates) && ListUtil.isEmpty(deletes)) {
            return;
        }

        String domain = QtalkNavicationService.getInstance().getXmppdomain();

        String insertSql = "insert or IGNORE into IM_User (UserId,XmppId,Name,DescInfo,SearchIndex,LastUpdateTime) values(?,?,?,?,?,?);";
        String updateSql = "update IM_User set DescInfo = ? , SearchIndex =? ,LastUpdateTime =? where XmppId = ?";
        String deleteSql = "delete from IM_User where XmppId = ?";
        String changeVersionSql = "insert or replace into IM_Cache_Data (key,type,valueInt) values (?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement insertStat = db.compileStatement(insertSql);
        SQLiteStatement updateStat = db.compileStatement(updateSql);
        SQLiteStatement deleteStat = db.compileStatement(deleteSql);
        SQLiteStatement versionStat = db.compileStatement(changeVersionSql);
        for (IncrementUsersResult.IncrementUser incrementUser : updates) {
            a++;
            insertStat.bindString(1, incrementUser.U);
            insertStat.bindString(2, incrementUser.U + "@" + domain);
            insertStat.bindString(3, incrementUser.N);
            insertStat.bindString(4, incrementUser.D);
            insertStat.bindString(5, incrementUser.pinyin);
            insertStat.bindString(6, version + "");
            insertStat.executeInsert();

            updateStat.bindString(1, incrementUser.D);
            updateStat.bindString(2, incrementUser.pinyin);
            updateStat.bindString(3, version + "");
            updateStat.bindString(4, incrementUser.U + "@" + domain);
            updateStat.executeUpdateDelete();
        }
        for (IncrementUsersResult.IncrementUser incrementUser : deletes) {
            c++;
            deleteStat.bindString(1, incrementUser.U + "@" + domain);
            deleteStat.executeUpdateDelete();
        }

        versionStat.bindString(1, CacheDataType.lastIncrementUserVersion);
        versionStat.bindString(2, String.valueOf(CacheDataType.lastIncrementUser));
        versionStat.bindString(3, String.valueOf(version));
        versionStat.executeInsert();

        db.setTransactionSuccessful();
        db.endTransaction();

        long end = System.currentTimeMillis();
        Logger.i("时间:" + (end - start));
        Logger.i("正常插入:" + a + ";更新数据:" + b + "删除数据:" + c);

    }

    /**
     * 更新语音消息已读
     *
     * @param message
     */
    public void updateVoiceMessage(IMMessage message) {
        String sql = "update im_message set content = ? where msgid =?";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            stat.bindString(1, message.getBody());
            stat.bindString(2, message.getId());
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


    public void updateUserCard(JSONArray dataList) {
        Logger.i("更新用户名片时的数据:" + dataList);
        String insertSql = "insert or ignore into IM_User (UserId, XmppId, Name,  HeaderSrc,mood) values(?, ?, ?, ?,?);";
        String updateSql = "update IM_User Set Name = ?,  HeaderSrc = ? ,mood = ? Where XmppId = ?;";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();

        try {
            SQLiteStatement insertStat = db.compileStatement(insertSql);
            SQLiteStatement updateStat = db.compileStatement(updateSql);
            for (int i = 0; i < dataList.length(); ++i) {
                JSONObject user = dataList.getJSONObject(i);
                String userId = JSONUtils.getStringValue(user, "U");
                String xmppId = JSONUtils.getStringValue(user, "X");
                String name = JSONUtils.getStringValue(user, "N");
                String headerSrc = JSONUtils.getStringValue(user, "H");
                String mood = JSONUtils.getStringValue(user, "M");
//                String lastUpdateTime = JSONUtils.getStringValue(user, "V");
                updateStat.bindString(1, name);
                updateStat.bindString(2, headerSrc);
                updateStat.bindString(3, mood);
                updateStat.bindString(4, xmppId);
                int count = updateStat.executeUpdateDelete();
                if (count <= 0) {
                    insertStat.bindString(1, userId);
                    insertStat.bindString(2, xmppId);
                    insertStat.bindString(3, name);
                    insertStat.bindString(4, headerSrc);
                    insertStat.bindString(5, mood);
                    insertStat.executeInsert();
                }

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "updateUserCard crashed.");

        } finally {
            db.endTransaction();
        }
    }

    public List bulkInsertGroupHistory(JSONArray resultList, String groupId, String myNickName, long readMarkT, MessageState didRead, String rtxId) throws Exception {

        if (resultList == null)
            return null;

        long lastDate = 0;//[self getMaxMsgTimeStampByXmppId:xmppId]/1000;
        Logger.i("jsonResult" + resultList);

        JSONArray msgLsit = new JSONArray();
        Map<String, JSONObject> sessionList = new HashMap<>();

        JSONObject messageItem;
        String xml;

        for (int i = 0; i < resultList.length(); ++i) {
            try {
                JSONObject dic = resultList.getJSONObject(i);
//                Logger.i("数据库信息:"+dic);
                String nickName = dic.getString("N");
                xml = dic.getString("B");
                messageItem = XmlUtils.parseXmlMessage(xml);
//                JSONObject messageItem1 = XmlUtils.parseXmlMessage("<message from='dba632082f6b4c7f89159c47537df561@conference.ejabhost1/胡滨hubin' to='dba632082f6b4c7f89159c47537df561@conference.ejabhost1' msec_times='1503370851085' realfrom='hubin.hu@ejabhost1' xml:lang='en' type='groupchat' client_type='ClientTypeAndroid' client_ver='0'><body id='244fcfd5-b311-4b25-a6e1-9de149055839' msgType='1'>qun shuo hua </body><stime xmlns='jabber:stime:delay' stamp='20170822T03:00:51'/></message>");
//                JSONObject messageItem2 = XmlUtils.parseXmlMessage("<message from='dba632082f6b4c7f89159c47537df561@conference.ejabhost1/胡滨hubin' to='dba632082f6b4c7f89159c47537df561@conference.ejabhost1' msec_times='1503374398785' realfrom='hubin.hu@ejabhost1' xml:lang='en' type='groupchat'>\\n <body msgType='1' maType='3' id='0736fed69e524d338d51c26324fa8c3b'>  1</body>\\n<stime xmlns='jabber:stime:delay' stamp='20170822T03:59:58'/></message>");
//                Logger.i("第一个消息:"+messageItem);
//                Logger.i("第一个消息:"+messageItem2);

                JSONObject perMessage = new JSONObject();
                JSONObject body = messageItem.getJSONObject("body");
                JSONObject message = messageItem.getJSONObject("message");
                JSONObject stime = messageItem.getJSONObject("stime");
                if (!body.has("_text")) {
                    continue;
                }
//                if (message.getString("to").contains("be098791380942c0b42efe8b360f23eb")) {
//                    new String();
//                    if (!body.has("_text")) {
//                        new String();
//                    }
//                }
                String tarId = QtalkStringUtils.parseIdAndDomain(message.optString("realfrom"));
                perMessage.put("MsgId", body.getString("id"));
                perMessage.put("XmppId", message.getString("to"));
                perMessage.put("RealJid", message.getString("to"));
                perMessage.put("from", TextUtils.isEmpty(tarId) ? nickName : tarId);
                perMessage.put("to", null);
                perMessage.put("From", TextUtils.isEmpty(tarId) ? nickName : tarId);
                perMessage.put("To", null);
                perMessage.put("Content", body.getString("_text"));//这行代码有错误
                if (body.has("maType")) {
                    perMessage.put("Platform", body.getString("maType"));
                } else {
                    perMessage.put("Platform", "0");
                }

                perMessage.put("MsgType", body.getInt("msgType"));
                perMessage.put("MsgRaw", xml);

                perMessage.put("MsgState", 1);
                if (body.has("extendInfo")) {
                    perMessage.put("extendInfo", body.getString("extendInfo"));
                } else {
                    perMessage.put("extendInfo", "");
                }

                long t;
                if (message.has("msec_times")) {
                    t = message.getLong("msec_times");
//                    perMessage.put("MsgDateTime", );
                } else {
                    String str = "yyyyMMdd'T'HH:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(str);
                    Date date = null;
                    try {
                        if (TextUtils.isEmpty(stime.getString("stamp"))) {
                            date = new Date();
                        } else {
                            date = sdf.parse(stime.getString("stamp"));
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    t = date.getTime();
//                    perMessage.put("MsgDateTime", date.getTime());
                }
                perMessage.put("MsgDateTime", t);
                if (t <= readMarkT) {
                    perMessage.put("ReadedTag", 1);
                } else {
                    perMessage.put("ReadedTag", 0);
                }


                String myUserId = IMLogicManager.getInstance().getMyself().getUser();
                //不相等应该是0 是收消息在左边
                String mt = body.getString("msgType");
                if (mt.equals("-1") || mt.equals("15")) {
                    perMessage.put("MsgDirection", 2);
                } else {
//                    int direction = myNickName.equalsIgnoreCase(nickName) || myNickName.equalsIgnoreCase(myUserId) ? 1 : 0;
                    int direction = tarId.equals(CurrentPreference.getInstance().getPreferenceUserId()) ? 1 : 0;
                    perMessage.put("MsgDirection", direction);

                }

                sessionList.put(message.getString("to"), perMessage);
                msgLsit.put(perMessage);
            } catch (JSONException e) {

                Logger.e(e, "bulkInsertGroupHistory fail!{}", groupId);
                throw e;
            }
        }

        bulkInsertMessage(msgLsit);
        bulkInsertChatSession(sessionList, "1", "7");

        return null;

    }

    public void bulkInsertChatHistory(JSONArray chatlog, String toUser, MessageState msgState) throws Exception {

        Logger.i("单人聊天历史数据:" + chatlog);
        JSONArray messages = new JSONArray();
        JSONArray collectionMessages = new JSONArray();
        Map<String, JSONObject> sessionList = new HashMap<>();
        for (int i = 0; i < chatlog.length(); ++i) {
            try {
                JSONObject object = chatlog.getJSONObject(i);
                String user = object.getString("F");
                String oUser = object.getString("F");

                //解析domain qtalk的headline消息没有FH 只有D
                String domain = TextUtils.isEmpty(object.optString("FH")) ? object.optString("D") : object.optString("FH");
                XMPPJID fromJid = XMPPJID.jidWithString(user, domain, null);

                user = object.getString("T");
                //解析domain qtalk的headline消息没有FH 只有D
                domain = TextUtils.isEmpty(object.optString("TH")) ? object.optString("D") : object.optString("TH");
                XMPPJID toJid = XMPPJID.jidWithString(user, domain, null);


//                resultObj.put("From", fromJid.fullname());
//                resultObj.put("To", toJid.fullname());


                String xml = object.optString("B");
                JSONObject resultObj = XmlUtils.parseMessageObject(xml, null, null, null, null);
                String fid = QtalkStringUtils.parseIdAndDomain(resultObj.optString("from"));
                String tid = QtalkStringUtils.parseIdAndDomain(resultObj.optString("to"));
                String f = TextUtils.isEmpty(fid) ? fromJid.fullname() : fid;
                String t = TextUtils.isEmpty(tid) ? toJid.fullname() : tid;
                resultObj.put("from", f);
                resultObj.put("to", t);


                if (resultObj.optString("from").equals(IMLogicManager.getInstance().getMyself().bareJID().fullname())) {
                    resultObj.put("XmppId", resultObj.optString("to"));
                    if (resultObj.optString("messageType").equals("consult")) {
                        //为了键入老客户端,有没有qchatid的情况, 默认qchatId为4,视为主动咨询消息
                        if (resultObj.optString("qchatid").equals("5")) {
                            resultObj.put("chatType", "5");
                            resultObj.put("RealJid", QtalkStringUtils.parseIdAndDomain(resultObj.optString("realto")));
                        } else if (resultObj.optString("qchatid").equals("4")) {
                            resultObj.put("chatType", "4");
//                        resultObj.put("RealJid", QtalkStringUtils.parseIdAndDomain(resultObj.optString("realto")));
                        } else {
                            resultObj.put("chatType", "4");
                        }
//                        resultObj.put("chatType","5");
//                        resultObj.put("RealJid", QtalkStringUtils.parseIdAndDomain(resultObj.optString("realto")));
                    } else if (resultObj.optString("messageType").equals("headline")) {
                        resultObj.put("XmppId", Constants.SYS.SYSTEM_MESSAGE);
                    }
//                    else{
////                        resultObj.put("RealJid", QtalkStringUtils.parseIdAndDomain(resultObj.optString("realto")));
//                    }

                    resultObj.put("MsgDirection", 1);
                } else {
                    //收到的consult消息,与发出逻辑相反
                    resultObj.put("XmppId", resultObj.optString("from"));
                    if (resultObj.optString("messageType").equals("consult")) {
                        if (resultObj.optString("qchatid").equals("5")) {
                            resultObj.put("chatType", "4");
//                            resultObj.put("RealJid", QtalkStringUtils.parseIdAndDomain(resultObj.optString("realfrom")));
                        } else if (resultObj.optString("qchatid").equals("4")) {
                            resultObj.put("chatType", "5");
                            resultObj.put("RealJid", QtalkStringUtils.parseIdAndDomain(resultObj.optString("realfrom")));
                        } else {
                            resultObj.put("chatType", "5");
                            resultObj.put("RealJid", QtalkStringUtils.parseIdAndDomain(resultObj.optString("realfrom")));
                        }

                    } else if (resultObj.optString("messageType").equals("headline")) {
                        resultObj.put("XmppId", Constants.SYS.SYSTEM_MESSAGE);
                    }
//                    else{
//                        resultObj.put("RealJid", QtalkStringUtils.parseIdAndDomain(resultObj.optString("realfrom")));
//                    }

                    resultObj.put("MsgDirection", 0);
                }
                //判断消息类型 如果不是正常消息 显示为2 在屏幕中间显示
                if (resultObj.getString("MsgType").equals("-1")) {
                    resultObj.put("MsgDirection", 2);
                }

                // 判断消息状态
                boolean hasMessageState = object.has("R");
                if (hasMessageState) {
                    int messageState = object.getInt("R");
                    if (resultObj.optString("from").equals(IMLogicManager.getInstance().getMyself().bareJID().fullname())) {
                        resultObj.put("ReadedTag", 1);
                    } else {
                        resultObj.put("ReadedTag", messageState == 1 ? 1 : 0);
                    }

                } else {
                    if (resultObj.optString("from").equals(IMLogicManager.getInstance().getMyself().bareJID().fullname())) {
                        resultObj.put("ReadedTag", 1);
                    } else {
                        resultObj.put("ReadedTag", 0);
                    }

                }

                if (oUser.equals("collection_rbt")) {
                    resultObj.put("MsgDirection", 0);
                    collectionMessages.put(resultObj);
                    if ("groupchat".equals(resultObj.optString("origintype"))) {
                        resultObj.put("from", resultObj.opt("realfrom"));
                    }
                }
                sessionList.put(resultObj.optString("XmppId") + "-" + resultObj.optString("RealJid"), resultObj);
                messages.put(resultObj);
            } catch (JSONException e) {
                Logger.e(e, "bulkInsertChathistory crashed");
            }
        }

        long beginTime = System.currentTimeMillis();

        bulkInsertMessage(messages);
        bulkInsertChatSession(sessionList, "0", "6");
        bulkInsertCollectionChat(collectionMessages);

        long endTime = System.currentTimeMillis();

        long cost = endTime - beginTime;


    }

    /**
     * 插入代收消息强化表表
     *
     * @param collectionMessages
     */
    private void bulkInsertCollectionChat(JSONArray collectionMessages) {
        String insertSql = "insert or ignore into IM_Message_Collection(MsgId,Originfrom,Originto,Origintype) values" +
                "(?,?,?,?)";
        String userSql = "insert or ignore into IM_Collection_User (XmppId,BIND) values (?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(insertSql);
            SQLiteStatement ustat = db.compileStatement(userSql);
            for (int i = 0; i < collectionMessages.length(); i++) {
                stat.bindString(1, collectionMessages.getJSONObject(i).optString("MsgId"));
                stat.bindString(2, QtalkStringUtils.parseIdAndDomain(collectionMessages.getJSONObject(i).optString("originfrom")));
                stat.bindString(3, collectionMessages.getJSONObject(i).optString("originto"));
                if ("chat".equals(collectionMessages.getJSONObject(i).optString("origintype"))) {
                    stat.bindString(4, 0 + "");
                } else if ("groupchat".equals(collectionMessages.getJSONObject(i).optString("origintype"))) {
                    stat.bindString(4, 1 + "");
                }
                ustat.bindString(1, collectionMessages.getJSONObject(i).optString("originto"));
                ustat.bindString(2, 1 + "");
                stat.executeInsert();
                ustat.executeInsert();
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param map
     * @param chatType 消息类型  单聊、群聊、consult
     * @param ef
     * @throws Exception
     */
    public void bulkInsertChatSession(Map<String, JSONObject> map, String chatType, String ef) throws Exception {
        String sql = "insert or replace into IM_SessionList (XmppId,RealJid,UserId,LastMessageId," +
                "LastUpdateTime,ChatType,ExtendedFlag) values(?,?,?,?,?,?,?);";
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();

        try {
            SQLiteStatement stat = db.compileStatement(sql);

            for (Map.Entry<String, JSONObject> entry : map.entrySet()) {
                JSONObject jsonObject = entry.getValue();
                stat.bindString(1, jsonObject.optString("XmppId"));
                stat.bindString(2, TextUtils.isEmpty(jsonObject.optString("RealJid")) ? jsonObject.optString("XmppId") : jsonObject.optString("RealJid"));
                stat.bindString(3, jsonObject.optString("XmppId"));
                stat.bindString(4, jsonObject.optString("MsgId"));
                stat.bindString(5, jsonObject.optString("MsgDateTime"));
                String ct = chatType;
                if ("consult".equals(jsonObject.optString("messageType"))) {
//                    if(){
//
//                    }
//                    ct = jsonObject.optString("qchatid");
                    ct = jsonObject.optString("chatType");
                } else if ("subscription".equals(jsonObject.optString("messageType"))) {
                    ct = String.valueOf(ConversitionType.MSG_TYPE_SUBSCRIPT);
                } else if ("headline".equals(jsonObject.optString("messageType"))) {
                    ct = String.valueOf(ConversitionType.MSG_TYPE_HEADLINE);
                } else if ("collection".equals(jsonObject.optString("messageType"))) {
                    ct = String.valueOf(ConversitionType.MSG_TYPE_COLLECTION);
                }
                stat.bindString(6, ct);
                stat.bindString(7, ef);
                stat.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "bulkInsertMessage crashed.");
            throw e;
        } finally {
            db.endTransaction();
        }


    }

    //查找未读消息根据xmppid
    public JSONArray SelectUnReadByXmppid(String xmppid, String realjid, int limit) {
        if (TextUtils.isEmpty(realjid)) {
            return null;
        }
        deleteJournal();
        String sql = "SELECT MsgId FROM IM_MESSAGE  where xmppid=? and RealJid = ? and IM_MESSAGE.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + ") and (Readedtag&" + MessageStatus.REMOTE_STATUS_CHAT_READED + ")<>" + MessageStatus.REMOTE_STATUS_CHAT_READED + " limit ? ";
        Object result = query(sql, new String[]{xmppid, realjid, String.valueOf(limit)}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                JSONArray array = new JSONArray();
                try {
                    while (cursor.moveToNext()) {

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", cursor.getString(0));
                            array.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return array;
            }
        });
        if (result == null) {
            return new JSONArray();
        } else {
            return (JSONArray) result;
        }
    }

    //查询所有未读消息条目数
    public int SelectUnReadCount() {
        deleteJournal();
        String sql = "select sum(UnreadCount) from IM_SessionList where xmppid  not in (SELECT subkey  FROM IM_USER_CONFIG where pkey = ? and isdel = ?);";
        Object result = query(sql, new String[]{CacheDataType.kNoticeStickJidDic, CacheDataType.N + ""}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                int count = 0;
                try {
                    while (cursor.moveToNext()) {

                        count = cursor.getInt(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return count;
            }
        });
        if (result == null) {
            return 0;
        }
        return (int) result;
    }

    //根据会话id查询所有未读消息条目数
    public int SelectUnReadCountByConvid(String xmppid, String realJid) {
        deleteJournal();
        if (TextUtils.isEmpty(xmppid)) {
            return 0;
        }
        String sql = "select UnreadCount from IM_SessionList where XmppId = ? and RealJid = ? ;";
        Object result = query(sql, new String[]{xmppid, TextUtils.isEmpty(realJid) ? xmppid : realJid}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                int count = 0;
                try {


                    while (cursor.moveToNext()) {

                        count = cursor.getInt(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return count;
            }
        });
        if (result == null) {
            return 0;
        }
        return (int) result;
    }

    //查询群成员根据GroupId
    public List<GroupMember> SelectGroupMembersByGroupId(String groupId) {
        deleteJournal();
        String sql = "Select * from IM_Group_Member Where GroupId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{groupId});
        List<GroupMember> memberList = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                GroupMember gm = new GroupMember();
                gm.setMemberId(cursor.getString(0));
                gm.setGroupId(cursor.getString(1));
                gm.setMemberJid(cursor.getString(2));
                gm.setName(cursor.getString(3));
                gm.setAffiliation(cursor.getString(4));
                gm.setLastUpdateTime(cursor.getString(5));
                gm.setExtendedFlag(cursor.getString(6));
                memberList.add(gm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return memberList;
    }

    //分页查询群组历史记录
    public List<IMMessage> SelectHistoryGroupChatMessage(final String xmppid, final String realJid, int count, int size) {
        deleteJournal();
        long start = System.currentTimeMillis();
        String sql = "SELECT MsgId,XmppId,Platform,a.'From',a.'To'," +
                " (case when a.type = '" + ProtoMessageOuterClass.MessageType.MessageTypeSourceCode_VALUE + "' then " + "'[代码段]'" + " else a.Content end)," +
                "Type,State,Direction,ReadedTag,LastUpdateTime," +
                " (case when a.type = '" + ProtoMessageOuterClass.MessageType.MessageTypeSourceCode_VALUE + "' then " + "'[代码段]'" + " else a.ExtendedInfo end)" +
                "FROM IM_MESSAGE as a where XmppId = ? and RealJid = ? ORDER BY LastUpdateTime DESC Limit ?,?\n";
        Object result = query(sql, new String[]{xmppid, realJid, count + "", size + ""}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<IMMessage> imMessageList = new ArrayList<>();
                if (xmppid == null || realJid == null) {//防止绑定crash
                    return imMessageList;
                }
                try {
                    while (cursor.moveToNext()) {
                        IMMessage imMessage = new IMMessage();
                        //设置消息id
                        imMessage.setId(cursor.getString(0));
                        imMessage.setMessageID(cursor.getString(0));
                        //设置会话列表id
                        imMessage.setConversationID(cursor.getString(1));
                        imMessage.setToID(cursor.getString(1));
                        imMessage.setMaType(cursor.getString(2));
                        String nickName = cursor.getString(3);
//                imMessage.setNickName(nickName);
                        imMessage.setRealfrom(nickName);
                        imMessage.setFromID(nickName);
                        //因为取得是历史群消息,有部分功能需要知道msg的消息类型
                        imMessage.setType(ConversitionType.MSG_TYPE_GROUP);
                        //消息正文
                        imMessage.setBody(cursor.getString(5));
                        imMessage.setMsgType(cursor.getInt(6));
                        //设置发送状态
                        imMessage.setReadState(cursor.getInt(9));
                        //设置方向
                        imMessage.setDirection(cursor.getInt(8));
                        //设置时间
                        long time = cursor.getLong(10);
                        imMessage.setExt(cursor.getString(11));
                        imMessage.setTime(new Date(time));
                        imMessage.setMessageState(cursor.getInt(7));
                        imMessageList.add(imMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return imMessageList;
            }
        }, DBDefalultTimeThreshold);

        long end = System.currentTimeMillis();
        Logger.i("SelectHistoryGroupChatMessage" + (end - start));
        if (result == null) {
            return new ArrayList<>();
        }
        return (List<IMMessage>) result;
    }

    //分页查询历史记录
    public List<IMMessage> SelectHistoryChatMessage(String xmppid, String realJid, int count, int size) {
        deleteJournal();
        if (xmppid == null || realJid == null) {//防止绑定crash
            return new ArrayList<>();
        }
        long start = System.currentTimeMillis();
        String sql = "SELECT MsgId,XmppId,Platform,a.'From',a.'To'," +
                " (case when a.type = '" + ProtoMessageOuterClass.MessageType.MessageTypeSourceCode_VALUE + "' then " + "'[代码段]'" + " else a.Content end)," +
                "Type,State,Direction,ReadedTag,LastUpdateTime,RealJid," +
                " (case when a.type = '" + ProtoMessageOuterClass.MessageType.MessageTypeSourceCode_VALUE + "' then " + "'[代码段]'" + " else a.ExtendedInfo end)" +
                "FROM IM_MESSAGE as a where realJid = ? and XmppId = ? ORDER BY LastUpdateTime DESC Limit ?,?";
        Object result = query(sql, new String[]{realJid, xmppid, count + "", size + ""}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<IMMessage> imMessageList = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        IMMessage imMessage = new IMMessage();

                        //设置消息id
                        imMessage.setId(cursor.getString(0));
                        imMessage.setMessageID(cursor.getString(0));
                        //设置会话列表id
                        imMessage.setConversationID(cursor.getString(1));
                        imMessage.setMaType(cursor.getString(2));
                        String from = cursor.getString(3);
                        String to = cursor.getString(4);
                        //to
                        imMessage.setToID(to);
                        //消息正文
                        imMessage.setBody(cursor.getString(5));
                        //消息类型2
                        imMessage.setMsgType(cursor.getInt(6));
                        int direction = cursor.getInt(8);
                        //from
                        imMessage.setFromID(from);
                        imMessage.setRealfrom(cursor.getString(11));
//                imMessage.setRealfrom(from);
                        //设置方向
                        imMessage.setDirection(direction);
                        //设置时间
                        long time = cursor.getLong(10);
                        imMessage.setTime(new Date(time));
                        imMessage.setExt(cursor.getString(12));
//
                        imMessage.setReadState(cursor.getInt(9));
                        imMessage.setMessageState(cursor.getInt(7));
                        imMessageList.add(imMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return imMessageList;
            }
        });
        long end = System.currentTimeMillis();
        Logger.i("SelectHistoryChatMessage" + (end - start));
        if (result == null) {
            return new ArrayList<>();
        } else {
            return (List<IMMessage>) result;
        }
    }


    /**
     * 置顶 重新插入,并根据当前字段决定是0 或1
     * 同时创建会话
     */
    public void setConversationTopOrCancel(RecentConversation rc) {
        // TODO: 2017/9/25 继续消息zhiding !!!!!!!!
        String insertSql = "INSERT or REPLACE into IM_Cache_Data (key,type,value) values " +
                "(?," + CacheDataType.TOP + ",(CASE WHEN (select value from " +
                "IM_Cache_Data where key =? and type = " + CacheDataType.TOP + ")" +
                " = 1 THEN 0 ELSE 1 END))";
        String insertTopConv = "insert or IGNORE into IM_SessionList (XmppId,RealJid,UserId,ChatType) values(?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(insertSql);
        SQLiteStatement stattc = db.compileStatement(insertTopConv);
        try {
            db.beginTransactionNonExclusive();
            stat.bindString(1, rc.getId() + "-" + rc.getRealUser());
            stat.bindString(2, rc.getId() + "-" + rc.getRealUser());
            stat.executeInsert();
            stattc.bindString(1, rc.getId());
            stattc.bindString(2, rc.getId());
            stattc.bindString(3, rc.getId());
            String type = "";
//            if (rc.getId().startsWith("collection_rbt")) {
//                type = "8";
//            } else {
            type = rc.getId().contains("conference") ? "1" : "0";
//            }
            stattc.bindString(4, type);
            stattc.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 清空sessionlist
     */
    public void DeleteSessionList() {
        String sql = "delete from im_sessionlist";
        String deleteMessage = "Delete from IM_Message";

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement deleteStat = db.compileStatement(sql);
            SQLiteStatement dmStat = db.compileStatement(deleteMessage);
            deleteStat.executeUpdateDelete();
            dmStat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


    //查询@自己的消息展示在sessionlist 目前为了兼容老客户端 比较的content没有用单独的msgtype 太low逼
    public void selectAtOwnMessage() {
        if (AtMessageMap == null) {
            return;
        }
        String sql = "select a.Content,a.'from',a.xmppid,a.msgid,a.type,a.ExtendedInfo from IM_Message as a where (type = " + ProtoMessageOuterClass.MessageType.MessageTypeGroupAt_VALUE + " or type = " + ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE + ") " +
                " and a.xmppid not in (select subkey  from IM_USER_CONFIG where pkey=? and isdel=?) " +
                " and a.direction=0" +
                " and " + MessageStatus.REMOTE_STATUS_CHAT_READED + "<>(ReadedTag&" + MessageStatus.REMOTE_STATUS_CHAT_READED + ") order by LastUpdateTime desc;";
        query(sql, new String[]{CacheDataType.kNoticeStickJidDic, CacheDataType.N + ""}, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                String userId = CurrentPreference.getInstance().getPreferenceUserId();
                try {
                    while (cursor.moveToNext()) {
                        String content = cursor.getString(0);
                        String from = cursor.getString(1);
                        String xmppid = cursor.getString(2);
                        String msgid = cursor.getString(3);
                        int type = cursor.getInt(4);
                        String ext = cursor.getString(5);
                        if (type == ProtoMessageOuterClass.MessageType.MessageTypeGroupAt_VALUE) {
                            List<AtData.DataBean> list = null;
                            if (!TextUtils.isEmpty(ext)) {
                                List<AtData> personList = JsonUtils.getGson().fromJson(ext, new TypeToken<List<AtData>>() {
                                }.getType());
                                for (AtData atPerson : personList) {
                                    if (atPerson.getType() == 10001) {
                                        list = atPerson.getData();
                                        break;
                                    }
                                }
                            }
                            if (!ListUtil.isEmpty(list)) {
                                for (AtData.DataBean person : list) {
                                    if (person != null && userId.equals(person.getJid())) {
                                        AtInfo atInfo = new AtInfo();
//                                        atInfo.atcontent = content;
                                        atInfo.msgId = msgid;
                                        atInfo.from = from;
                                        atInfo.xmppid = xmppid;
                                        atInfo.isAtAll = false;
                                        setAtMessageValue(atInfo);
                                    }
                                }
                            }
                        } else if (type == ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE) {
                            if (content.contains("@全体成员") ||
                                    content.contains("@所有人") ||
                                    content.contains("@all") ||
                                    content.contains("@ALL") ||
                                    content.contains("@All")) {
                                AtInfo atInfo = new AtInfo();
//                                atInfo.atcontent = content;
                                atInfo.msgId = msgid;
                                atInfo.from = from;
                                atInfo.xmppid = xmppid;
                                atInfo.isAtAll = true;
                                setAtMessageValue(atInfo);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return null;
            }
        });
    }

    private void setAtMessageValue(AtInfo atInfo) {
        List<AtInfo> atList = AtMessageMap.get(atInfo.xmppid);
        if(atList == null){
            atList = new ArrayList<>();
        }
        if(!atList.contains(atInfo))
            atList.add(0,atInfo);
        AtMessageMap.put(atInfo.xmppid,atList);
    }

    //获取会话列表数据
    public List<RecentConversation> SelectConversationList(boolean isOnlyUnRead) {
        long start = System.currentTimeMillis();
        deleteJournal();
        String sql;
        if (isOnlyUnRead) {
            sql = "Select a.XmppId,a.RealJid,a.UserId,a.chatType," +
                    " (case when b.type = '" + ProtoMessageOuterClass.MessageType.MessageTypeSourceCode_VALUE + "' then " + "'[代码段]'" + " else b.Content end)," +
                    "b.'From',b.Type,b.State," +
                    "a.UnreadCount as count," +
                    "CASE b.LastUpdateTime  When NULL THEN a.LastUpdateTime ELSE b.LastUpdateTime END as orderTime," +
                    "(case when (select count(*) from IM_USER_CONFIG where subkey =(a.XmppId ||'<>'||a.RealJid) and isdel ='" + CacheDataType.N + "' and pkey = '" + CacheDataType.kStickJidDic + "')=1 THEN 1 ELSE 0 END )as top," +
                    "(CASE WHEN (select COUNT(*) from IM_USER_CONFIG where subkey =a.XmppId  and isdel = '" + CacheDataType.N + "' and pkey ='" + CacheDataType.kNoticeStickJidDic + "') = 1 THEN 1 ELSE 0 END) as remind  " +
                    "  From IM_SessionList as a Left join IM_Message as b ON a.LastMessageId = b.MsgId where count>0 and remind = 0 Order By top DESC, orderTime DESC;";

        } else {

            sql = "select a.XmppId,a.realjid,a.userid,a.chattype," +
                    " (case when b.type = '" + ProtoMessageOuterClass.MessageType.MessageTypeSourceCode_VALUE + "' then " + "'[代码段]'" + " else b.Content end)," +
                    " b.'from',b.type,b.State," +
                    "a.UnreadCount," +
                    "a.LastUpdateTime," +
                    "(case when (select count(*) from IM_USER_CONFIG where subkey =(a.XmppId ||'<>'||a.RealJid) and isdel ='" + CacheDataType.N + "' and pkey = '" + CacheDataType.kStickJidDic + "')=1 THEN 1 ELSE 0 END )as top," +
                    "(CASE WHEN (select COUNT(*) from IM_USER_CONFIG where subkey =a.XmppId  and isdel = '" + CacheDataType.N + "' and pkey ='" + CacheDataType.kNoticeStickJidDic + "') = 1 THEN 1 ELSE 0 END) as remind  " +
                    " from IM_SessionList as a left join IM_Message as b on a.LastMessageId = b.MsgId order by top desc,a.LastUpdateTime desc;";

        }
        SQLiteDatabase db = helper.getWritableDatabase();
        if (!db.isOpen()) {
            return new ArrayList<>();
        }
        Object result = query(sql, null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<RecentConversation> list = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        RecentConversation rc = new RecentConversation();
                        //获取会话列表id
                        rc.setId(cursor.getString(0));
                        //设置置顶字段
                        rc.setTop(cursor.getInt(10));
                        //设置提醒字段
                        rc.setRemind(cursor.getInt(11));
                        XMPPJID target = XMPPJID.parseJID(rc.getId());
                        int chatType = cursor.getInt(3);
                        JSONObject jsonObject = null;
//
                        //获取真实id
                        rc.setRealUser(cursor.getString(1));
                        //获取列表类型0是单人1是多人4 5都是客服
                        rc.setConversationType(chatType);
                        //获取会话列表最后一条消息
                        int type = cursor.getInt(6);
                        String from = cursor.getString(5);
                        rc.setLastFrom(from);
                        String msg = cursor.getString(4);

                        rc.setLastMsg(getLastMessageText(type, msg));

                        //设置最后一条消息状态
                        rc.setLastState(cursor.getString(7));
                        rc.setMsgType(type);
                        if (rc.getMsgType() == 0) {
                            rc.setLastMsg("");
                            rc.setMsgType(1);
                            rc.setLastState(String.valueOf(MessageStatus.LOCAL_STATUS_SUCCESS));
                        }
                        //获取当前会话未读消息
                        rc.setUnread_msg_cont(cursor.getInt(8));
                        //获取会话列表最后一条消息时间
                        rc.setLastMsgTime(cursor.getLong(9));
                        list.add(rc);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });

        long end = System.currentTimeMillis();
        Logger.i("SelectConversationList:" + (end - start));
        if (result == null) {
            return new ArrayList<>();
        } else {
            return (List<RecentConversation>) result;
        }
    }

    public String getLastMessageText(int type, String msg) {
        return ChatTextHelper.showContentType(msg, type);
    }

    public Map<String, List<AtInfo>> getAtMessageMap() {
        return AtMessageMap;
    }

    public boolean UpdateChatReadTypeMessage(IMMessage imMessage) {
        String xmppid = "";
        boolean success = true;
        boolean isGroup = false;
        if (imMessage.getCollectionType() == ConversitionType.MSG_TYPE_GROUP) {
            isGroup = true;
            xmppid = imMessage.getExt();
        }
        if (isGroup) {
            updateGroupMessageReadedTag(xmppid, imMessage.getReadState(), imMessage.getTime().getTime());
        } else {
            long start = System.currentTimeMillis();
            SQLiteDatabase db = helper.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                String chatSql = "update IM_Message set ReadedTag = (ReadedTag | ?) where MsgId = ? or MsgId = ?";
                SQLiteStatement chatStat = db.compileStatement(chatSql);

                for (int i = 0; i < imMessage.getNewReadList().length(); i++) {
//                    //单人消息更新方式
                    int state = imMessage.getReadState();

                    String id = JSONUtils.getStringValue(imMessage.getNewReadList().getJSONObject(i), "id");
                    String tempId = id.replace("consult-", "");
                    chatStat.bindString(1, state + "");
                    chatStat.bindString(2, id);
                    chatStat.bindString(3, tempId);
                    int updateState = chatStat.executeUpdateDelete();
                    if (updateState == 0) {
                        success = false;
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {

            } finally {
                db.endTransaction();
            }
            Logger.i("更新当人状态时间:" + (System.currentTimeMillis() - start));
        }

        return success;

    }

    public void updateGroupMessageReadedTag(String xmppid, int readedTag, long time) {
        long start = System.currentTimeMillis();
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            String groupSql = "update IM_Message set ReadedTag = (ReadedTag | ?) where XmppId = ? and LastUpdateTime <= ? " +
                    "and ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + "<>" + MessageStatus.REMOTE_STATUS_CHAT_READED;
            SQLiteStatement groupStat = db.compileStatement(groupSql);
            groupStat.bindString(1, String.valueOf(readedTag));
            groupStat.bindString(2, xmppid);
            groupStat.bindLong(3, time);
            groupStat.executeUpdateDelete();
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        Logger.i("更新群阅读状态时间:" + (System.currentTimeMillis() - start));
    }

    /**
     * 更新消息发送状态在发送的时候出错,因为那会直接就是protoMessage 所以放进来比较方便
     *
     * @param protoMessage
     * @param state
     */
    //更新消息的发送状态
    public void UpdateChatStateMessage(ProtoMessageOuterClass.ProtoMessage protoMessage, int state) {
        String sql = "update IM_Message set State = ? where MsgId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            //获取message中对象
            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());
            stat.bindString(1, String.valueOf(state));
            stat.bindString(2, xmppMessage.getMessageId());
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();

        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
//        db.execSQL();
    }


    /**
     * 收到消息的正常返回的时候更新消息发送状态使用IMMessage
     *
     * @param imMessage
     * @param isForce
     */
    //更新消息的发送状态
    public void UpdateChatStateMessage(IMMessage imMessage, boolean isForce) {
        String sql = "update IM_Message set ReadedTag = (ReadedTag| ? ),State = (State|?),lastUpdateTime=? where MsgId = ?";
        if (isForce) {
            sql = "update IM_Message set ReadedTag = ? , State = ? ,lastUpdateTime=? where MsgId = ?";
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            stat.bindString(1, String.valueOf(imMessage.getReadState()));
            stat.bindString(2, String.valueOf(imMessage.getMessageState()));
            stat.bindString(3, String.valueOf(imMessage.getTime().getTime()));
            stat.bindString(4, imMessage.getMessageId());
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param imMessage
     * @param noDirection 用来判断是不是consult true为consult
     */
    public void InsertIMSessionList(IMMessage imMessage, boolean noDirection) {

        //当自动恢复消息类型时，消息不插入im-message表，同时也不更新sessionlist
        if (imMessage.isAuto_reply()) {
            return;
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        String conversationID = imMessage.getConversationID();
        String realJid = conversationID;
        String chatType = String.valueOf(imMessage.getType());

        if (noDirection) {
            if (!imMessage.isCarbon()) {
                if (imMessage.getFromID().equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    if (imMessage.getType() == 4) {
                        chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT);
                    } else if (imMessage.getType() == 5) {
                        realJid = imMessage.getRealto();
                        chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER);
                    }
                } else {
                    if (imMessage.getType() == 4) {
                        realJid = imMessage.getRealfrom();
                        chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER);
                    } else if (imMessage.getType() == 5) {
                        chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT);
                    }
                }
            } else {
                if (imMessage.getType() == 4) {
                    chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT);
                } else if (imMessage.getType() == 5) {
                    realJid = imMessage.getRealto();
                    chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER);
                }
            }
        } else {
            if (imMessage.getDirection() == 0) {
                if (imMessage.getType() == 4) {
                    realJid = imMessage.getRealfrom();
                    chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER);
                } else if (imMessage.getType() == 5) {
                    chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT);
                }
            } else {
                if (imMessage.getType() == 4) {
                    chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT);
                } else if (imMessage.getType() == 5) {
                    realJid = imMessage.getRealto();
                    chatType = String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER);
                }
            }
        }


//

        String sql = "insert or replace into IM_SessionList (XmppId,RealJid,UserId,LastMessageId," +
                "LastUpdateTime,ChatType,ExtendedFlag,UnReadCount) values(?,?,?,?,?,?,?,?);";

        String updateSql = "update IM_SessionList set UserId = ?,LastMessageId = ?," +
                "LastUpdateTime = ?,ChatType = ? where XmppId = ? and RealJid = ?;";

        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement statUpdate = db.compileStatement(updateSql);
            statUpdate.bindString(1, imMessage.getUserId());
            statUpdate.bindString(2, imMessage.getMessageId());
            statUpdate.bindString(3, String.valueOf(imMessage.getTime().getTime()));
            statUpdate.bindString(4, chatType);
            statUpdate.bindString(5, conversationID);
            statUpdate.bindString(6, realJid);
            int count = statUpdate.executeUpdateDelete();
            if (count <= 0) {//不存在 插入
                SQLiteStatement statInsert = db.compileStatement(sql);
                statInsert.bindString(1, conversationID);
                statInsert.bindString(2, realJid);
                statInsert.bindString(6, chatType);
                statInsert.bindString(3, imMessage.getUserId());
                statInsert.bindString(4, imMessage.getMessageId());
                statInsert.bindString(5, String.valueOf(imMessage.getTime().getTime()));
                statInsert.bindString(7, "");
                if (!imMessage.getFromID().equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    statInsert.bindString(8, "1");
                } else {//自己发的消息默认插入0 否则会变成NULL 导致触发器失效
                    statInsert.bindString(8, "0");
                }
                statInsert.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    //接到单条消息后,应该进行插入InsertSession表的操作,用以显示会话列表
    public void InsertSessionList(ProtoMessageOuterClass.ProtoMessage protoMessage) {
        String sql = "insert or replace into IM_SessionList (XmppId,RealJid,UserId,LastMessageId," +
                "LastUpdateTime,ChatType,ExtendedFlag) values(?,?,?,?,?,?,?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());

            //获得当前消息载体是否为抄送类型
            //先获取头部信息列表
            boolean carbon = false;
            List<ProtoMessageOuterClass.StringHeader> stringHeaders = xmppMessage.getBody().getHeadersList();
            for (int i = 0; i < stringHeaders.size(); i++) {
                ProtoMessageOuterClass.StringHeader sh = stringHeaders.get(i);
                //如果为真,证明为抄送类型
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeCarbon)) {
                    carbon = true;
                }
            }
            //得到from
            String to = QtalkStringUtils.parseIdAndDomain(protoMessage.getTo());

            //得到to
            String from = QtalkStringUtils.parseIdAndDomain(protoMessage.getFrom());

            //设置消息id
            //不是抄送的情况下
            if (!carbon) {
                //如果是我自己发出去的
                if (from.equals(IMLogicManager.getInstance().getMyself().bareJID().fullname())) {
                    //xmppid应该设置成收消息的人就是to
                    stat.bindString(1, to);
                    //如果收到消息的情况
                } else {
                    //xmppid应该设置成发送的人,就是from
                    stat.bindString(1, from);
                }
                //抄送情况下
            } else {
                //xmppid应该设置发来消息的人 也就是对方
                stat.bindString(1, from);
            }
            //设置真实id
            //获得真实id
            String realId = protoMessage.getRealfrom();
            //判断真实id是否存在
            if (TextUtils.isEmpty(realId)) {
                if (!carbon) {
                    if (from.equals(IMLogicManager.getInstance().getMyself().bareJID().fullname())) {
                        stat.bindString(2, to);
                    } else {
                        stat.bindString(2, from);
                    }
                } else {
                    stat.bindString(2, from);
                }
            } else {
                stat.bindString(2, realId);
            }

            //设置userId
            if (!carbon) {
                if (from.equals(IMLogicManager.getInstance().getMyself().bareJID().fullname())) {
                    stat.bindString(3, to);
                } else {
                    stat.bindString(3, from);
                }
            } else {
                stat.bindString(3, from);
            }
            //设置 messageId
            stat.bindString(4, xmppMessage.getMessageId());
            //设置 最后消息时间
            stat.bindString(5, String.valueOf(xmppMessage.getReceivedTime()));
            //设置消息类型
            if (protoMessage.getSignalType() == ProtoMessageOuterClass.SignalType.SignalTypeChat_VALUE) {
                //如果是两人会话消息消息 先设置为0
                stat.bindString(6, 0 + "");
            } else if (protoMessage.getSignalType() == ProtoMessageOuterClass.SignalType.SignalTypeGroupChat_VALUE) {
                //如果是群组消息,先设置为1
                stat.bindString(6, 1 + "");
            } else {
                //其他情况 暂时设置为1
                stat.bindString(6, 0 + "");
            }
            //设置扩展消息类型,目前好像没用,直接设置成pb的消息类型
            stat.bindString(7, String.valueOf(protoMessage.getSignalType()));


            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    /**
     * 插入conversition
     *
     * @param session
     */
    public void InsertSessionList(IMSessionList session) {
        String sql = "insert or replace into IM_SessionList (XmppId,RealJid,UserId,LastMessageId," +
                "LastUpdateTime,ChatType,ExtendedFlag) values(?,?,?,?,?,?,?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            stat.bindString(1, session.getXmppId());
            stat.bindString(2, session.getRealJid());
            stat.bindString(3, session.getUserId());
            stat.bindString(4, session.getLastMessageId());
            stat.bindString(5, session.getLastUpdateTime());
            stat.bindString(6, session.getChatType());
            stat.bindString(7, session.getExtendedFlag());

            stat.executeInsert();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    //发送插入群组消息
    public void InsertSendGroupChatMessage(ProtoMessageOuterClass.ProtoMessage protoMessage, String state, String readedTag) {
        String sql = "insert or IGNORE into IM_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                "Platform, Type, State, Direction,LastUpdateTime,ReadedTag,MessageRaw,RealJid) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());
            //获取名字
            String myNickName = IMMessageManager.getInstance().getGroupNickNameByGroupId("");
            //xmppid
            String xmppid = QtalkStringUtils.parseIdAndDomain(protoMessage.getTo());
            //设置消息id
            stat.bindString(1, xmppMessage.getMessageId());
            //设置xmppid
            stat.bindString(2, xmppid);
            //设置from 群组消息的from都是名字
            stat.bindString(3, myNickName);
            stat.bindString(4, "");
            //设置文本消息
            stat.bindString(5, xmppMessage.getBody().getValue());
            //platform
            stat.bindString(6, "0");
            //设置消息类型
            stat.bindString(7, String.valueOf(xmppMessage.getMessageType()));
            //设置消息状态
            stat.bindString(8, state);
            //设置方向
            stat.bindString(9, "1");
            //设置时间
            stat.bindString(10, String.valueOf(xmppMessage.getReceivedTime()));
            //设置已读
            stat.bindString(11, readedTag);
            stat.bindString(12, String.valueOf(xmppMessage.toByteString()));
            stat.bindString(13, "");
            stat.executeInsert();

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    //接收插入群组消息
    public void InsertGroupChatMessage(ProtoMessageOuterClass.ProtoMessage protoMessage, String state, String readedTag) {
        String sql = "insert or IGNORE into IM_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                "Platform, Type, State, Direction,LastUpdateTime,ReadedTag,MessageRaw,RealJid) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());
            String myNickName = IMMessageManager.getInstance().getGroupNickNameByGroupId("");
            //获取xmppid
            String xmppid = QtalkStringUtils.parseIdAndDomain(protoMessage.getFrom());
            //获取发送消息人名
            String nickName = QtalkStringUtils.parseNickName(protoMessage.getFrom());
            //// TODO: 2017/8/21 后面应该有判断是左边 还是右边的方法
            //设置消息id
            stat.bindString(1, xmppMessage.getMessageId());
            //设置xmppid
            stat.bindString(2, xmppid);
            //设置from 群组消息的from都是名字
            stat.bindString(3, nickName);
            stat.bindString(4, "");
            //设置文本消息
            stat.bindString(5, xmppMessage.getBody().getValue());
            //platfrom
            stat.bindString(6, "0");
            //设置消息类型
            int type = xmppMessage.getMessageType();
            stat.bindString(7, type + "");
            //设置消息状态
            stat.bindString(8, state);
            //设置方向
            if (myNickName.equals(nickName)) {
                stat.bindString(9, "1");
                stat.bindString(11, "1");
            } else {
                stat.bindString(9, "0");
                stat.bindString(11, readedTag);

            }
            if (type == -1 || type == 15) {
                stat.bindString(9, "2");
            }
            stat.bindString(10, String.valueOf(xmppMessage.getReceivedTime()));


            stat.bindString(12, String.valueOf(xmppMessage.toByteString()));
            stat.bindString(13, "");
            stat.executeInsert();

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    //有撤销消息更新数据
    public void UpdateRevokeChatMessage(String MsgId, String str) {
        String sql = "update IM_Message set Content = ?, Direction =2,Type = -1 where MsgId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);

        stat.bindString(1, str);
        stat.bindString(2, MsgId);
        stat.executeUpdateDelete();
        db.setTransactionSuccessful();

        db.endTransaction();


    }

    public void UpdateMucVcard(String MsgId, String str) {
        String sql = "update IM_Message set Content = ?, Direction =2 where MsgId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);

        stat.bindString(1, str);
        stat.bindString(2, MsgId);
        stat.executeUpdateDelete();
        db.setTransactionSuccessful();

        db.endTransaction();


    }

    /**
     * 插入代收消息
     *
     * @param imMessage
     */
    public void InsertCollectionMessage(IMMessage imMessage) {
        InsertChatMessage(imMessage, true);
        String insertSql = "insert or ignore into IM_Message_Collection(MsgId,Originfrom,Originto,Origintype) values" +
                "(?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(insertSql);
            stat.bindString(1, imMessage.getMessageId());
            stat.bindString(2, imMessage.getoFromId());
            stat.bindString(3, imMessage.getoToId());
            stat.bindString(4, String.valueOf(imMessage.getCollectionType()));
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void InsertCollectionUser(IMMessage imMessage) {
        String insertSql = "insert or replace into IM_Collection_User(XmppId,BIND) values (?,1)";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(insertSql);
            stat.bindString(1, imMessage.getoToId());

            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 插入消息发送状态和消息已读状态
     *
     * @param imMessage
     */
    public void InsertChatMessageReadAndState(IMMessage imMessage) {
        String readsql = "insert or replace into IM_Message(MsgId,ReadedTag,State) values (?,?,?)";
        String updateReadSql = "update IM_Message set ReadedTag=?,State=? where MsgId=?;";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement readstat = db.compileStatement(readsql);
        SQLiteStatement readUpdateStat = db.compileStatement(updateReadSql);

        try {
            readUpdateStat.bindString(1, imMessage.getReadState() + "");
            readUpdateStat.bindString(2, imMessage.getMessageState() + "");
            readUpdateStat.bindString(3, imMessage.getMessageId());

            int count = readUpdateStat.executeUpdateDelete();
            if (count <= 0) {
                readstat.bindString(1, imMessage.getMessageId());
                readstat.bindString(2, imMessage.getReadState() + "");
                readstat.bindString(3, imMessage.getMessageState() + "");
                readstat.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i("" + e.getMessage());
        } finally {
            db.endTransaction();

        }
    }

    /**
     * 插入消息
     *
     * @param imMessage
     * @param isIgnore
     */
    public void InsertChatMessage(IMMessage imMessage, boolean isIgnore) {
        //自动回复消息不入库
        if (imMessage.isAuto_reply()) {
            return;
        }
        String sql;
        if (isIgnore) {
            sql = "insert or IGNORE into IM_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                    "Platform, Type, State, Direction,LastUpdateTime,ReadedTag,MessageRaw,RealJid, ContentResolve,ExtendedInfo) values" +
                    "(? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? );";
        } else {
            sql = "insert or REPLACE into IM_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                    "Platform, Type, State, Direction,LastUpdateTime,ReadedTag,MessageRaw,RealJid, ContentResolve,ExtendedInfo) values" +
                    "(? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? );";
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);

//            SQLiteStatement ustat = db.compileStatement(usql);
            stat.bindString(1, imMessage.getMessageId());


            stat.bindString(2, imMessage.getConversationID());
            if (imMessage.getType() == ConversitionType.MSG_TYPE_GROUP
                    || imMessage.getCollectionType() == ConversitionType.MSG_TYPE_GROUP) {
                stat.bindString(3, TextUtils.isEmpty(imMessage.getRealfrom()) ? imMessage.getFromID() : imMessage.getRealfrom());
            } else {
                stat.bindString(3, imMessage.getFromID());
            }

            stat.bindString(4, imMessage.getToID());
            stat.bindString(5, imMessage.getBody());
            stat.bindString(6, imMessage.getMaType());
            stat.bindString(7, String.valueOf(imMessage.getMsgType()));
            stat.bindString(8, String.valueOf(imMessage.getMessageState()));
            //机器人转人工消息显示中间
            int dir = imMessage.getDirection();
            if (imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeRobotTurnToUser_VALUE |
                    imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypeGroupNotify_VALUE) {
                dir = IMMessage.DIRECTION_MIDDLE;
            }
            stat.bindString(9, String.valueOf(dir));
            stat.bindString(10, String.valueOf(imMessage.getTime().getTime()));
            stat.bindString(11, String.valueOf(imMessage.getReadState()));
            stat.bindString(12, imMessage.getMessageRaw() != null ? imMessage.getMessageRaw() : "");
//
            if (imMessage.getDirection() == IMMessage.DIRECTION_RECV) {
                if (imMessage.getType() == 4) {
                    stat.bindString(13, imMessage.getRealfrom());
                } else if (imMessage.getType() == 5) {
                    stat.bindString(13, imMessage.getConversationID());
                } else {
                    stat.bindString(13, imMessage.getConversationID());
                }
            } else {
                if (imMessage.getType() == 4) {
                    stat.bindString(13, imMessage.getConversationID());
                } else if (imMessage.getType() == 5) {
                    stat.bindString(13, TextUtils.isEmpty(imMessage.getRealto()) ? imMessage.getConversationID() : imMessage.getRealto());
                } else {
                    stat.bindString(13, imMessage.getConversationID());
                }
            }
//
            stat.bindString(14, "");

            if (imMessage.getMsgType() == ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE
                    && DataCenter.localImageMessagePath.containsKey(imMessage.getMessageId())) {
                stat.bindString(15, DataCenter.localImageMessagePath.get(imMessage.getMessageId()));
            } else {
                stat.bindString(15, imMessage.getExt() != null ? imMessage.getExt() : "");
            }
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /*********************************公众号相关 start****************************************************/
    //插入公众号
    public boolean InsertPublicNumber(List<PublishPlatform> publishPlatforms) {
        String sql = "insert or replace into IM_Public_Number(XmppId, PublicNumberId, PublicNumberType, Name, " +
                "DescInfo, HeaderSrc, SearchIndex,LastUpdateTime,PublicNumberInfo) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            for (PublishPlatform publishPlatform : publishPlatforms) {
                stat.bindString(1, publishPlatform.getId());
                stat.bindString(2, QtalkStringUtils.parseId(publishPlatform.getId()));
                stat.bindString(3, String.valueOf(publishPlatform.getPublishPlatformType()));
                stat.bindString(4, publishPlatform.getName());
                stat.bindString(5, publishPlatform.getDescription());
                stat.bindString(6, publishPlatform.getGravatarUrl());
//            stat.bindString(7, publicNumber.getSearchIndex());
                stat.bindString(8, String.valueOf(publishPlatform.getVersion()));
                stat.bindString(9, publishPlatform.getPublishPlatformInfo());
                stat.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }

    public List<PublishPlatform> selectPublishPlatfroms(int limit) {
        List<PublishPlatform> publishPlatforms = new ArrayList<>();
        String sql = "select * from IM_Public_Number where PublicNumberType <> 4 limit ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(limit)});
        try {
            while (cursor.moveToNext()) {
                PublishPlatform publishPlatform = new PublishPlatform();
                publishPlatform.setId(cursor.getString(0));
                publishPlatform.setPublishPlatformType(cursor.getInt(2));
                publishPlatform.setName(cursor.getString(3));
                publishPlatform.setDescription(cursor.getString(4));
                publishPlatform.setGravatarUrl(cursor.getString(5));
                publishPlatform.setPublishPlatformInfo(cursor.getString(7));
                publishPlatform.setVersion(cursor.getInt(8));
                publishPlatform.setExtentionFlag(cursor.getInt(9));
                publishPlatforms.add(publishPlatform);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return publishPlatforms;
    }

    public PublishPlatform selectPublishPlatformById(String id) {
        String sql = "select * from IM_Public_Number where XmppId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{id});

        PublishPlatform publishPlatform = null;
        try {
            while (cursor.moveToNext()) {
                publishPlatform = new PublishPlatform();
                publishPlatform.setId(cursor.getString(0));
                publishPlatform.setPublishPlatformType(cursor.getInt(2));
                publishPlatform.setName(cursor.getString(3));
                publishPlatform.setDescription(cursor.getString(4));
                publishPlatform.setGravatarUrl(cursor.getString(5));
                publishPlatform.setPublishPlatformInfo(cursor.getString(7));
                publishPlatform.setVersion(cursor.getInt(8));
                publishPlatform.setExtentionFlag(cursor.getInt(9));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return publishPlatform;
    }

    public boolean deletePublishPlatformById(String id) {
        String sql = "delete from IM_Public_Number where XmppId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement deleteStat = db.compileStatement(sql);
            deleteStat.bindString(1, id);
            deleteStat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }

    public List<PublishPlatform> searchPublishPlatform(String term, int limit, int offset) {
        if (term == null) {
            return null;
        }
        if (term.isEmpty() || term.equals("")) {
            return selectPublishPlatfroms(10);
        }
        String sql = "SELECT XmppId,Name,DescInfo,HeaderSrc FROM IM_Public_Number WHERE PublicNumberType <> 4 AND " +
                "XmppId LIKE \'%" + term + "%\' " +
                "OR Name LIKE \'%" + term + "%\' " +
                "OR DescInfo LIKE \'%" + term + "%\'" +
                " limit " + limit + " offset " + offset +
                ";";
        List<PublishPlatform> list = new ArrayList<PublishPlatform>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                PublishPlatform platform = new PublishPlatform();
                platform.setId(cursor.getString(0));
                platform.setName(cursor.getString(1));
                platform.setGravatarUrl(cursor.getString(3));
                platform.setDescription(cursor.getString(2));
                list.add(platform);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<PublishPlatform> searchPublishPlatform(String term, int limit) {
        if (term == null) {
            return null;
        }
        if (term.isEmpty() || term.equals("")) {
            return selectPublishPlatfroms(10);
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT XmppId,Name,DescInfo,HeaderSrc FROM IM_Public_Number WHERE PublicNumberType <> 4 AND ")
                .append("XmppId LIKE \'%").append(term).append("%\' ")
                .append("OR Name LIKE \'%").append(term).append("%\' ")
                .append("OR DescInfo LIKE \'%").append(term).append("%\'");

        if (limit > 0) {
            sql.append(" limit ").append(limit);
        }
        sql.append(";");
        List<PublishPlatform> list = new ArrayList<PublishPlatform>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql.toString(), null);
        try {
            while (cursor.moveToNext()) {
                PublishPlatform platform = new PublishPlatform();
                platform.setId(cursor.getString(0));
                platform.setName(cursor.getString(1));
                platform.setGravatarUrl(cursor.getString(3));
                platform.setDescription(cursor.getString(2));
                list.add(platform);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    //插入公众号消息

    public void InsertPublicNumberMessage(IMMessage imMessage) {
        String sql = "insert or IGNORE into IM_Public_Number_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                "Type, State, Direction,LastUpdateTime,ReadedTag,ExtendedFlag) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";
        String usql = "update IM_Public_Number_Message set State =? ,LastUpdateTime = ? where MsgId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            SQLiteStatement ustat = db.compileStatement(usql);
            stat.bindString(1, imMessage.getMessageId());
            stat.bindString(2, imMessage.getConversationID());
            stat.bindString(3, imMessage.getFromID());
            stat.bindString(4, imMessage.getToID());
            stat.bindString(5, imMessage.getBody());
            stat.bindString(6, String.valueOf(imMessage.getMsgType()));
            stat.bindString(7, String.valueOf(imMessage.getReadState()));
            stat.bindString(8, String.valueOf(imMessage.getDirection()));
            stat.bindString(9, String.valueOf(imMessage.getTime().getTime()));
            stat.bindString(10, String.valueOf(imMessage.getIsRead()));
            stat.bindString(11, TextUtils.isEmpty(imMessage.getExt()) ? "" : imMessage.getExt());

            ustat.bindString(1, String.valueOf(imMessage.getReadState()));
            ustat.bindString(2, String.valueOf(imMessage.getTime().getTime()));
            ustat.bindString(3, imMessage.getMessageId());
            stat.executeInsert();
            ustat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public boolean insertPublishPlatformNews(List<PublishPlatformNews> platformNewsList) {
        String sql = "insert or replace into IM_Public_Number_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                "Type, State, Direction,LastUpdateTime,ReadedTag,ExtendedFlag) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";


        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            for (PublishPlatformNews platformNews : platformNewsList) {
                stat.bindString(1, platformNews.id);
                stat.bindString(2, platformNews.platformXmppId);
//                stat.bindString(3, platformNews.);
//                stat.bindString(4, imMessage.getToID());
                stat.bindString(5, platformNews.content);
                stat.bindString(6, String.valueOf(platformNews.msgType));
                stat.bindString(7, String.valueOf(platformNews.state));
                stat.bindString(8, String.valueOf(platformNews.direction));
                stat.bindString(9, String.valueOf(platformNews.latestUpdateTime));
                stat.bindString(10, String.valueOf(platformNews.readTag));
                stat.bindString(11, TextUtils.isEmpty(platformNews.extentionFlag) ? "" : platformNews.extentionFlag);
                stat.executeInsert();

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }


    public List<PublishPlatformNews> selectPublishPlatformNews(String id, int count, int offset) {
        List<PublishPlatformNews> publishPlatformNewses = new ArrayList<>();
        String sql = "SELECT * FROM IM_Public_Number_Message where XmppId = ? ORDER BY LastUpdateTime DESC Limit ?,?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{id, offset + "", count + ""});
        try {
            while (cursor.moveToNext()) {
                PublishPlatformNews publishPlatformNews = new PublishPlatformNews();
                publishPlatformNews.id = cursor.getString(0);
                publishPlatformNews.platformXmppId = cursor.getString(1);
                publishPlatformNews.content = cursor.getString(4);
                publishPlatformNews.msgType = Integer.parseInt(cursor.getString(5));
//                publishPlatformNews.state = Integer.parseInt(cursor.getString(6));
                publishPlatformNews.direction = Integer.parseInt(cursor.getString(7));
                if (!TextUtils.isEmpty(cursor.getString(9))) {
                    publishPlatformNews.latestUpdateTime = Long.parseLong(cursor.getString(9));
                }
//                publishPlatformNews.readTag = Integer.parseInt(cursor.getString(9));
                publishPlatformNews.extentionFlag = cursor.getString(10);
                publishPlatformNews.readTag = Integer.parseInt(cursor.getString(8));
                publishPlatformNews.state = Integer.parseInt(cursor.getString(6));
                publishPlatformNewses.add(publishPlatformNews);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return publishPlatformNewses;
    }

    public PublishPlatformNews selectLastPublishPlatformNewsById(String id) {
        PublishPlatformNews publishPlatformNews = null;
        String sql = "SELECT MsgId, XmppId, \"From\", \"To\", Content, " +
                "Type, State, Direction,LastUpdateTime,ReadedTag,ExtendedFlag FROM IM_Public_Number_Message WHERE XmppId='" + id + "' ORDER BY LastUpdateTime DESC Limit 0,1";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                publishPlatformNews = new PublishPlatformNews();
                publishPlatformNews.id = cursor.getString(0);
                publishPlatformNews.platformXmppId = cursor.getString(1);
                publishPlatformNews.content = cursor.getString(4);
                publishPlatformNews.direction = Integer.parseInt(cursor.getString(7));
                publishPlatformNews.extentionFlag = cursor.getString(10);
                if (!TextUtils.isEmpty(cursor.getString(8)))
                    publishPlatformNews.latestUpdateTime = Long.parseLong(cursor.getString(8));
                publishPlatformNews.msgType = Integer.parseInt(cursor.getString(5));
                publishPlatformNews.readTag = Integer.parseInt(cursor.getString(9));
                publishPlatformNews.state = Integer.parseInt(cursor.getString(6));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return publishPlatformNews;
    }

    public PublishPlatformNews selectLastPublishPlatformNews() {
        PublishPlatformNews publishPlatformNews = null;
        String sql = "SELECT MsgId, XmppId, \"From\", \"To\", Content, " +
                "Type, State, Direction,LastUpdateTime,ReadedTag,ExtendedFlag FROM IM_Public_Number_Message ORDER BY LastUpdateTime DESC Limit 0,1";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                publishPlatformNews = new PublishPlatformNews();
                publishPlatformNews.id = cursor.getString(0);
                publishPlatformNews.platformXmppId = cursor.getString(1);
                publishPlatformNews.content = cursor.getString(4);
                publishPlatformNews.direction = Integer.parseInt(cursor.getString(7));
                publishPlatformNews.extentionFlag = cursor.getString(10);
                if (!TextUtils.isEmpty(cursor.getString(8)))
                    publishPlatformNews.latestUpdateTime = Long.parseLong(cursor.getString(8));
                publishPlatformNews.msgType = Integer.parseInt(cursor.getString(5));
                publishPlatformNews.readTag = Integer.parseInt(cursor.getString(9));
                publishPlatformNews.state = Integer.parseInt(cursor.getString(6));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return publishPlatformNews;
    }

    public boolean deletPlatformNewsById(String id) {
        String sql = "delete from IM_Public_Number_Message where XmppId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement deleteStat = db.compileStatement(sql);
            deleteStat.bindString(1, id);
            deleteStat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }


    /*********************************公众号相关 end****************************************************/


    //插入单条发送或接收数据
    public void InsertChatMessage(ProtoMessageOuterClass.ProtoMessage protoMessage, String state, String readedTag) {
        String sql = "insert or IGNORE into IM_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                "Platform, Type, State, Direction,LastUpdateTime,ReadedTag,MessageRaw,RealJid, ContentResolve) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?);";
        String usql = "update IM_Message set State =? ,LastUpdateTime = ? where MsgId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        SQLiteStatement ustat = db.compileStatement(usql);
        try {
            ProtoMessageOuterClass.XmppMessage xmppMessage = ProtoMessageOuterClass.XmppMessage.parseFrom(protoMessage.getMessage());
            //获得当前消息载体是否为抄送类型
            //先获取头部信息列表
            boolean carbon = false;
            String extendInfo = "";
            List<ProtoMessageOuterClass.StringHeader> stringHeaders = xmppMessage.getBody().getHeadersList();
            for (int i = 0; i < stringHeaders.size(); i++) {
                ProtoMessageOuterClass.StringHeader sh = stringHeaders.get(i);
                //如果为真,证明为抄送类型
                if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeCarbon)) {
                    carbon = true;
                } else if (sh.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeExtendInfo)) {
                    extendInfo = sh.getValue();
                }

            }
            //得到from
            String to = QtalkStringUtils.parseIdAndDomain(protoMessage.getTo());
            //得到to
            String from = QtalkStringUtils.parseIdAndDomain(protoMessage.getFrom());
            //设置消息id
            stat.bindString(1, xmppMessage.getMessageId());
            //设置消息列表会话id
            if (!carbon) {
                if (from.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    stat.bindString(2, to);
                } else {
                    stat.bindString(2, from);
                }
            } else {
                stat.bindString(2, from);
            }
            ustat.bindString(3, xmppMessage.getMessageId());
            Logger.i("截取后的字段:" + from);
            if (!carbon) {
                //设置from
                stat.bindString(3, from);
                //设置to
                stat.bindString(4, to);
            } else {
                //设置from 因为是抄送,所以应该从自己发出
                stat.bindString(3, to);
                //设置to 因为是抄送,所以应该是给对方
                stat.bindString(4, from);
            }
            //设置消息文本
            stat.bindString(5, xmppMessage.getBody().getValue());
            //platFrom 平台 android 0
            stat.bindString(6, "0");
            //设置消息类型
            stat.bindString(7, String.valueOf(xmppMessage.getMessageType()));
            //设置消息状态
            stat.bindString(8, state);//0发送失败 1发送成功 2发送中 3收到
            ustat.bindString(1, state);
            //根据发送对象判断显示方向
            //如果不是抄送
            if (!carbon) {
                if (from.equals(CurrentPreference.getInstance().getPreferenceUserId())) {
                    stat.bindString(9, "1");//0左边1右边
                } else {
                    stat.bindString(9, "0");
                }
            } else {
                stat.bindString(9, "1");
            }
            //设置时间
            stat.bindString(10, String.valueOf(xmppMessage.getReceivedTime()));
            ustat.bindString(2, String.valueOf(xmppMessage.getReceivedTime()));
            //设置已读未读状态
            if (!carbon) {
                stat.bindString(11, readedTag);//0未读 1已读
            } else {
                stat.bindString(11, "1");
            }
            //把message对象存入
            stat.bindString(12, String.valueOf(xmppMessage.toByteString()));
            stat.bindString(13, "");

            stat.executeInsert();
            ustat.executeUpdateDelete();

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    //更新群组消息已读 根据xmppid
    public void UpdateGroupRead(String xmppid) {
        String sql = "update IM_MESSAGE set ReadedTag = 1 where XmppId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            SQLiteStatement stat = db.compileStatement(sql);

            stat.bindString(1, xmppid);
            stat.executeUpdateDelete();

//
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    //更新单条消息已读 根据MessageId 设置单条消息已读
    public void UpdateReadState(JSONArray jsonArray, int state) {
        String sql = "update IM_Message set ReadedTag = (ReadedTag|" + state + ") where MsgId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            SQLiteStatement stat = db.compileStatement(sql);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                stat.bindString(1, JSONUtils.getStringValue(jsonObject, "id"));
                stat.executeUpdateDelete();
            }
//
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    //批量插入消息
    private void bulkInsertMessage(JSONArray messages) {

//        Logger.i("整理后的json数据:" + messages);
        String sql = "insert or replace into IM_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                "Platform, Type, State, Direction,LastUpdateTime,ReadedTag,MessageRaw,RealJid,ExtendedInfo) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?);";

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();

        try {
            SQLiteStatement stat = db.compileStatement(sql);
            for (int i = 0; i < messages.length(); ++i) {
                JSONObject message = messages.getJSONObject(i);

                stat.bindString(1, message.optString("MsgId"));
                stat.bindString(2, message.optString("XmppId"));
                stat.bindString(3, message.optString("from"));
                stat.bindString(4, message.optString("to"));
                stat.bindString(5, message.optString("Content"));
                stat.bindString(6, message.optString("Platform"));
                stat.bindString(7, message.optString("MsgType"));
                //由于批量插入的群组消息没有MsgType所以在这里进行下判断

                stat.bindString(8, message.optString("MsgState"));

                stat.bindString(9, message.optString("MsgDirection"));

                if (message.optString("MsgDateTime").equals("-1")) {
                    String d = message.optString("MsgStime");
                    String str = "yyyyMMdd'T'HH:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(str);
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
                    stat.bindString(10, date.getTime() + "");
                } else {
                    stat.bindString(10, message.optString("MsgDateTime"));
                }

                stat.bindString(11, message.optString("ReadedTag"));
                stat.bindString(12, message.optString("MsgRaw"));
                stat.bindString(13, TextUtils.isEmpty(message.optString("RealJid")) ? message.optString("XmppId") : message.optString("RealJid"));
                stat.bindString(14, message.optString("extendInfo"));


                stat.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "bulkInsertMessage crashed.");

        } finally {
            db.endTransaction();
        }
    }


    /**
     * 插入更新删除群组成员列表
     *
     * @param protoMessage
     */
    public void insertUpdateGroupMembers(ProtoMessageOuterClass.ProtoMessage protoMessage) {
        String insertReplace = "insert or replace into IM_Group_Member (GroupId,MemberId , Affiliation, LastUpdateTime) values(?, ?, ?, ?);";
//        String updateSql = "update IM_Group_Member set Affiliation = ? , LastUpdateTime = ? where MemberId =?";
        List<String> members = new ArrayList<>();
        String groupId = "";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(insertReplace);
//        SQLiteStatement updateStat = db.compileStatement(updateSql);
        try {
            ProtoMessageOuterClass.IQMessage iqMessage = ProtoMessageOuterClass.IQMessage.parseFrom(protoMessage.getMessage());
            List<ProtoMessageOuterClass.MessageBody> bodies = iqMessage.getBodysList();
            //群ID
            stat.bindString(1, protoMessage.getFrom());
            groupId = protoMessage.getFrom();
            //时间
            stat.bindString(4, String.valueOf(iqMessage.getReceivedTime()));
//            updateStat.bindString(2, String.valueOf(iqMessage.getReceivedTime()));
            for (int i = 0; i < bodies.size(); i++) {
                ProtoMessageOuterClass.MessageBody body = bodies.get(i);
                List<ProtoMessageOuterClass.StringHeader> headers = body.getHeadersList();

                //默认普通成员
                stat.bindString(3, 2 + "");

                for (int j = 0; j < headers.size(); j++) {

                    ProtoMessageOuterClass.StringHeader header = headers.get(j);
                    if (header.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeJid)) {
                        //memberid
                        stat.bindString(2, header.getValue());
//                        updateStat.bindString(3, header.getValue());
                        members.add(header.getValue());
                    }else if (header.getDefinedKey().equals(ProtoMessageOuterClass.StringHeaderType.StringHeaderTypeAffiliation)) {
                        String mPower = header.getValue();
                        if (mPower.equals("m_user")) {
                            //普通成员
                            stat.bindString(3, 2 + "");
                        } else if (mPower.equals("admin")) {
                            //管理员
                            stat.bindString(3, 1 + "");
                        } else if (mPower.equals("owner")) {
                            //群主
                            stat.bindString(3, 0 + "");
                        } else {
                            stat.bindString(3, 4 + "");
                        }
                    }
                }
                stat.executeInsert();
//                updateStat.executeUpdateDelete();
            }

            if (members.size() > 0) {
                StringBuffer sql = new StringBuffer();
                sql.append("select MemberId from IM_Group_Member WHERE MemberId NOT in (");
                for (int i = 0; i < members.size(); i++) {
                    String str = "'" + members.get(i) + "',";
                    sql.append(str);
                }
                String deleteSql = "delete from IM_Group_Member  where memberId = ? and GroupId = ?";
                SQLiteStatement deleteStat = db.compileStatement(deleteSql);
                sql.deleteCharAt(sql.length() - 1).append(") and GroupId = '" + groupId + "';");
                Cursor deleteCursor = db.rawQuery(sql.toString(), null);
                try {
                    while (deleteCursor.moveToNext()) {
                        deleteStat.bindString(1, deleteCursor.getString(0));
                        deleteStat.bindString(2, groupId);
                        deleteStat.executeUpdateDelete();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    deleteCursor.close();
                }


            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    public List<IMMessage> searchMsg(String xmppId, String term, int limit) {
        deleteJournal();
        List<IMMessage> imMessageList = new ArrayList<>();
        String searchSql = "select * from IM_Message where XmppId = ? and LastUpdateTime < ? order by LastUpdateTime limit ?";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(searchSql, new String[]{xmppId, term, limit + ""});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                //设置消息id
                imMessage.setId(cursor.getString(0));
                imMessage.setMessageID(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                String from = cursor.getString(3);
                XMPPJID target = XMPPJID.parseJID(from);
                String to = cursor.getString(4);
                int direction = cursor.getInt(8);
                //from
                imMessage.setFromID(from);
                //to
                imMessage.setToID(to);
                //设置姓名
                imMessage.setNickName(from);
                //消息正文
                imMessage.setBody(cursor.getString(5));
                //消息类型2
                imMessage.setMsgType(cursor.getInt(6));
                //设置发送状态
                imMessage.setReadState(cursor.getInt(10));
                //设置方向
                imMessage.setDirection(direction);
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setTime(new Date(time));
                imMessageList.add(imMessage);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imMessageList;
    }

    public List<IMMessage> searchVoiceMsg(String convid, long t, int msgType) {
        List<IMMessage> imMessageList = new ArrayList<>();
        String sql = "SELECT * FROM IM_MESSAGE where XmppId = ? AND LastUpdateTime > ? AND Type = ? AND Direction = 0 AND Content is not null";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{convid, t + "", msgType + ""});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                //设置消息id
                imMessage.setId(cursor.getString(0));
                imMessage.setMessageID(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                String from = cursor.getString(3);
                XMPPJID target = XMPPJID.parseJID(from);
                String to = cursor.getString(4);
                int direction = cursor.getInt(8);
                //from
                imMessage.setFromID(from);
                imMessage.setRealfrom(from);
                //to
                imMessage.setToID(to);
                //设置姓名
                imMessage.setNickName(from);
                //消息正文
                imMessage.setBody(cursor.getString(5));
                //消息类型2
                imMessage.setMsgType(cursor.getInt(6));
                //设置发送状态
                imMessage.setReadState(cursor.getInt(10));
                //设置方向
                imMessage.setDirection(direction);
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setTime(new Date(time));
                imMessageList.add(imMessage);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imMessageList;
    }

    /**
     * 查询对应msgtype的消息
     *
     * @param convid
     * @param t
     * @param msgType
     * @return
     */
    public List<IMMessage> searchMessageByMsgType(String convid, long t, int msgType) {
        List<IMMessage> imMessageList = new ArrayList<>();
        String sql = "SELECT * FROM IM_MESSAGE where XmppId = ? AND LastUpdateTime > ? AND Type = ? AND Direction = 0 AND Content is not null";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{convid, t + "", msgType + ""});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                //设置消息id
                imMessage.setId(cursor.getString(0));
                imMessage.setMessageID(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                String from = cursor.getString(3);
                XMPPJID target = XMPPJID.parseJID(from);
                String to = cursor.getString(4);
                int direction = cursor.getInt(8);
                //from
                imMessage.setFromID(from);
                //to
                imMessage.setToID(to);
                //设置姓名
                imMessage.setNickName(from);
                //消息正文
                imMessage.setBody(cursor.getString(5));
                //消息类型2
                imMessage.setMsgType(cursor.getInt(6));
                //设置发送状态
                imMessage.setReadState(cursor.getInt(10));
                //设置方向
                imMessage.setDirection(direction);
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setTime(new Date(time));
                imMessageList.add(imMessage);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imMessageList;
    }

    /**
     * 更新好友列表根据List
     * 先插入所有数据,在更新所有数据,最后在not in 不在列表里的删除
     *
     * @param fList
     */
    public void UpdateFriendListByList(List<Nick> fList) {
        String insertSql = "insert or Ignore into IM_Friend_List(UserId,XmppId,Name,DescInfo,HeaderSrc,SearchIndex,UserInfo,LastUpdateTime,IncrementVersion,ExtendedFlag) values(?,?,?,?,?,?,?,?,?,?)";
        String updateSql = "update IM_Friend_List set UserId =?,Name=?,DescInfo=?,HeaderSrc=?,SearchIndex=?,UserInfo=?,LastUpdateTime=?,IncrementVersion=?,ExtendedFlag=? where XmppId =?";
        StringBuffer deleteSql = new StringBuffer("delete from IM_Friend_List Where XmppId in (select Xmppid from IM_Friend_List where XmppId not in (");
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransactionNonExclusive();
            if (fList == null || fList.size() == 0) {
                db.execSQL("delete from IM_Friend_List");
                db.setTransactionSuccessful();
                return;
            }
            SQLiteStatement istat = db.compileStatement(insertSql);
            SQLiteStatement ustat = db.compileStatement(updateSql);
            for (int i = 0; i < fList.size(); i++) {
                Nick n = fList.get(i);
                deleteSql.append("'" + n.getXmppId() + "',");
                istat.bindString(1, !TextUtils.isEmpty(n.getUserId()) ? n.getUserId() : "");
                istat.bindString(2, !TextUtils.isEmpty(n.getXmppId()) ? n.getXmppId() : "");
                istat.bindString(3, !TextUtils.isEmpty(n.getName()) ? n.getName() : "");
                istat.bindString(4, !TextUtils.isEmpty(n.getDescInfo()) ? n.getDescInfo() : "");
                istat.bindString(5, !TextUtils.isEmpty(n.getHeaderSrc()) ? n.getHeaderSrc() : "");
                istat.bindString(6, !TextUtils.isEmpty(n.getSearchIndex()) ? n.getSearchIndex() : "");
                istat.bindString(7, !TextUtils.isEmpty(n.getUserInfo()) ? n.getUserInfo() : "");
                istat.bindString(8, !TextUtils.isEmpty(n.getLastUpdateTime()) ? n.getLastUpdateTime() : "");
                istat.bindString(9, !TextUtils.isEmpty(n.getIncrementVersion()) ? n.getIncrementVersion() : "");
                istat.bindString(10, !TextUtils.isEmpty(n.getExtendedFlag()) ? n.getExtendedFlag() : "");
                ustat.bindString(1, !TextUtils.isEmpty(n.getUserId()) ? n.getUserId() : "");
                ustat.bindString(2, !TextUtils.isEmpty(n.getName()) ? n.getName() : "");
                ustat.bindString(3, !TextUtils.isEmpty(n.getDescInfo()) ? n.getDescInfo() : "");
                ustat.bindString(4, !TextUtils.isEmpty(n.getHeaderSrc()) ? n.getHeaderSrc() : "");
                ustat.bindString(5, !TextUtils.isEmpty(n.getSearchIndex()) ? n.getSearchIndex() : "");
                ustat.bindString(6, !TextUtils.isEmpty(n.getUserInfo()) ? n.getUserInfo() : "");
                ustat.bindString(7, !TextUtils.isEmpty(n.getLastUpdateTime()) ? n.getLastUpdateTime() : "");
                ustat.bindString(8, !TextUtils.isEmpty(n.getIncrementVersion()) ? n.getIncrementVersion() : "");
                ustat.bindString(9, !TextUtils.isEmpty(n.getExtendedFlag()) ? n.getExtendedFlag() : "");
                ustat.bindString(10, !TextUtils.isEmpty(n.getXmppId()) ? n.getXmppId() : "");
                istat.executeInsert();
                ustat.executeUpdateDelete();
            }
            deleteSql.deleteCharAt(deleteSql.length() - 1);
            deleteSql.append("))");
            db.execSQL(deleteSql.toString());

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 查询所有的好友Rn用数据
     *
     * @return
     */
    public List<Nick> SelectFriendListForRN() {
        String sql = "SELECT A.UserId,A.XmppId,B.Name,B.Descinfo,B.HeaderSrc,B.SearchIndex,B.UserInfo,B.LastUpdateTime,B.IncrementVersion,B.ExtendedFlag,B.mood " +
                " from IM_Friend_List AS A left JOIN IM_User AS B ON A.XmppId = B.XmppId";
        Object result = query(sql, null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<Nick> list = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        Nick n = new Nick();
                        String xmppid = cursor.getString(1);
                        n.setUserId(cursor.getString(0));
                        n.setXmppId(xmppid);
                        n.setName(cursor.getString(2));
                        n.setDescInfo(cursor.getString(3));
                        n.setHeaderSrc(cursor.getString(4));
                        n.setSearchIndex(cursor.getString(5));
                        n.setUserInfo(cursor.getString(6));
                        n.setLastUpdateTime(cursor.getString(7));
                        n.setIncrementVersion(cursor.getString(8));
                        n.setExtendedFlag(cursor.getString(9));
                        n.setMood(cursor.getString(10));
                        //设置备注
                        n.setMark(ConnectionUtil.getInstance().getMarkupNameById(xmppid));
                        list.add(n);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        } else {
            return (List<Nick>) result;
        }
    }

    /**
     * 查询所有的好友
     *
     * @return
     */
    public List<Nick> SelectFriendList() {
        String sql = "select * from IM_Friend_List";
        Object result = query(sql, null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                List<Nick> list = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        Nick n = new Nick();
                        n.setUserId(cursor.getString(0));
                        n.setXmppId(cursor.getString(1));
                        n.setName(cursor.getString(2));
                        n.setDescInfo(cursor.getString(3));
                        n.setHeaderSrc(cursor.getString(4));
                        n.setSearchIndex(cursor.getString(5));
                        n.setUserInfo(cursor.getString(6));
                        n.setLastUpdateTime(cursor.getString(7));
                        n.setIncrementVersion(cursor.getString(8));
                        n.setExtendedFlag(cursor.getString(9));
                        list.add(n);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return list;
            }
        });
        if (result == null) {
            return new ArrayList<>();
        } else {
            return (List<Nick>) result;
        }
    }

    /**
     * 是不是好友
     *
     * @param xmppid
     * @return
     */
    public boolean isFriend(String xmppid) {
        String sql = "select * from IM_Friend_List where XmppId='" + xmppid + "'";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            return cursor.moveToNext();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }


    /**
     * 查询session根据xmppid
     * 理论上这个方法查出来的是不准确的 应该不要用
     *
     * @param xmppid
     * @return
     */
    public RecentConversation selectRecentConversationByXmppId(String xmppid) {
        String sql = "select * from IM_SessionList where XmppId = ?";
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, new String[]{xmppid});
        try {
            RecentConversation rc = new RecentConversation();
            while (cursor.moveToNext()) {
                rc.setId(cursor.getString(0));
                rc.setRealUser(cursor.getString(1));
                rc.setLastMsgTime(cursor.getLong(4));
//                rc.setla
            }
            return rc;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查询置顶,提醒
     *
     * @param rc
     * @return
     */
    public RecentConversation SelectConversationByRC(RecentConversation rc) {
        String sql = "select *,(CASE WHEN (select value from IM_Cache_Data where key = ?" +
                " and type = 1) = 1 THEN 1 ELSE 0 END) as top ,(CASE WHEN (select value from " +
                "IM_Cache_Data where key = ? and type = 2)  = 1 THEN 1 ELSE 0 END) as " +
                "remind from IM_SessionList where Xmppid =? and RealJid = ?";
        SQLiteDatabase db = helper.getReadableDatabase();
        String key = rc.getId() + "-" + rc.getRealUser();
        Cursor cursor = db.rawQuery(sql, new String[]{key, key, rc.getId(), rc.getRealUser()});
        try {
            RecentConversation conversation = new RecentConversation();
            while (cursor.moveToNext()) {
                conversation.setId(cursor.getString(0));
                conversation.setRealUser(cursor.getString(1));
                conversation.setLastMsgTime(cursor.getLong(4));
                conversation.setTop(cursor.getInt(7));
                conversation.setRemind(cursor.getInt(8));
//                rc.setla
            }
            return conversation;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查找代收图片
     *
     * @param of
     * @param ot
     * @param limit
     * @return
     */
    public List<IMMessage> searchImageMsg(String of, String ot, int limit) {
        List<IMMessage> list = new ArrayList<>();
        String sql = "select b.* from IM_Message_Collection AS a left join IM_Message AS b on a.msgId = b.msgId  where Originfrom = ? and Originto = ?  order by LastUpdateTime desc LIMIT ?";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{of, ot, limit + ""});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                //设置消息id
                imMessage.setId(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                String nickName = cursor.getString(3);
                imMessage.setNickName(nickName);
                imMessage.setMessageID(cursor.getString(0));
                imMessage.setToID(cursor.getString(1));
                imMessage.setMsgType(cursor.getInt(6));
//                //from
//                imMessage.setFromID(cursor.getString(3));
//                //to
//                imMessage.setToID(cursor.getString(4));
                //消息正文
                imMessage.setBody(cursor.getString(5));
                //设置发送状态
                imMessage.setReadState(cursor.getInt(10));
                //设置方向
                imMessage.setDirection(cursor.getInt(8));
                imMessage.setExt(cursor.getString(14));
                imMessage.setIsRead(cursor.getInt(10));
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setTime(new Date(time));
                list.add(imMessage);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public List<IMMessage> searchImageVideoMsg(String xmppId, String realJid, int start, int end) {
        List<IMMessage> list = new ArrayList<>();
        String sql = "select * from IM_Message where XmppId = ? and realJid = ? AND (content like '%[obj type=\"image\"%' or type = ?) order by LastUpdateTime desc LIMIT ?,? ";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{xmppId, realJid, String.valueOf(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE), String.valueOf(start), String.valueOf(end)});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                //设置消息id
                imMessage.setId(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                String nickName = cursor.getString(3);
                imMessage.setNickName(nickName);
                imMessage.setMessageID(cursor.getString(0));
                imMessage.setToID(cursor.getString(1));
                imMessage.setMsgType(cursor.getInt(6));
                //消息正文
                imMessage.setBody(cursor.getString(5));
                //设置发送状态
                imMessage.setReadState(cursor.getInt(7));
                //设置方向
                imMessage.setDirection(cursor.getInt(8));
                imMessage.setExt(cursor.getString(14));
                imMessage.setIsRead(cursor.getInt(10));
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setTime(new Date(time));
                list.add(imMessage);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    public List<IMMessage> searchImageMsg(String convId, int limit) {
        List<IMMessage> list = new ArrayList<>();
        String sql = "select * from IM_Message where XmppId = ? AND content like '%[obj type=\"image\"%' order by LastUpdateTime desc LIMIT ? ";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{convId, limit + ""});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                //设置消息id
                imMessage.setId(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                String nickName = cursor.getString(3);
                imMessage.setNickName(nickName);
                imMessage.setMessageID(cursor.getString(0));
                imMessage.setToID(cursor.getString(1));
                imMessage.setMsgType(cursor.getInt(6));
//                //from
//                imMessage.setFromID(cursor.getString(3));
//                //to
//                imMessage.setToID(cursor.getString(4));
                //消息正文
                imMessage.setBody(cursor.getString(5));
                //设置发送状态
                imMessage.setReadState(cursor.getInt(10));
                //设置方向
                imMessage.setDirection(cursor.getInt(8));
                imMessage.setExt(cursor.getString(14));
                imMessage.setIsRead(cursor.getInt(10));
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setTime(new Date(time));
                list.add(imMessage);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public List<IMMessage> searchFilesMsg() {
        String sql = "Select * From IM_Message where Type=5 Order By LastUpdateTime DESC;";
        List<IMMessage> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                //设置消息id
                imMessage.setId(cursor.getString(0));
                imMessage.setMessageID(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                imMessage.setToID(cursor.getString(1));
                imMessage.setMaType(cursor.getString(2));
                String nickName = cursor.getString(3);
//                imMessage.setNickName(nickName);
                imMessage.setRealfrom(nickName);
                imMessage.setFromID(nickName);
                //消息正文
                imMessage.setBody(cursor.getString(5));
                imMessage.setMsgType(cursor.getInt(6));
                //设置发送状态
                imMessage.setReadState(cursor.getInt(10));
                //设置方向
                imMessage.setDirection(cursor.getInt(8));
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setExt(cursor.getString(14));
                imMessage.setMessageState(cursor.getInt(7));
                imMessage.setTime(new Date(time));
                list.add(imMessage);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public JSONArray searchFilesMsgByXmppId(String xmppid) {
        JSONArray jsonArray = new JSONArray();
        String sql = "Select a.'from',a.content, a.LastUpdateTime,b.name,b.HeaderSrc From IM_Message as a left join IM_User as b on a.'from' = b.xmppid Where Type=5 and a.xmppid = ? Order By a.LastUpdateTime DESC;";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{xmppid});
        try {
            while (cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("from", cursor.getString(0));
                jsonObject.put("content", cursor.getString(1));
                jsonObject.put("time", DateTimeUtils.getTime(cursor.getLong(2), false));
                jsonObject.put("name", cursor.getString(3));
                jsonObject.put("headerSrc", cursor.getString(4));
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return jsonArray;
    }

    /**
     * 更新收藏表情配置
     *
     * @param value
     */
    public void updateCollectEmoConfig(String value) {
        String sql = "insert or replace into IM_Cache_Data(key, type, value" + ") values" +
                "(?, ?, ?);";

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            stat.bindString(1, Constants.SYS.MY_EMOTION_KEY);
            stat.bindString(2, String.valueOf(CacheDataType.COLLECTION_EMO));
            stat.bindString(3, value);
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "updateCollectEmojConfig crashed.");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 将userid插入cache表
     *
     * @param value
     */
    public void insertUserIdToCacheData(String value) {
        String sql = "insert or replace into IM_Cache_Data(key,type, value" + ") values" +
                "(?,?, ?);";
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            stat.bindString(1, CacheDataType.USER_ID);
            stat.bindString(2, String.valueOf(CacheDataType.USER_ID_TYPE));
            stat.bindString(3, value);
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "insertUserIdToCacheData crashed.");
        } finally {
            db.endTransaction();
        }
    }


    public boolean getFocusSearch() {
        String sql = "select value from IM_Cache_Data where  type = " + CacheDataType.Focus_Search_TYPE;
        SQLiteDatabase db = helper.getReadableDatabase();
        boolean isFocus = false;
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                isFocus = Boolean.parseBoolean(cursor.getString(0));
            }
        } catch (Exception e) {
            isFocus = false;
            Logger.e(e, "getLatestGroupRMTime crashed.");
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return isFocus;
    }

    /**
     * 将新老焦点搜索插入cache表
     *
     * @param value
     */
    public void insertFocusSearchCacheData(String value) {
        String sql = "insert or replace into IM_Cache_Data(key,type, value" + ") values" +
                "(?,?, ?);";
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            stat.bindString(1, CacheDataType.Focus_Search_ID);
            stat.bindString(2, String.valueOf(CacheDataType.Focus_Search_TYPE));
            stat.bindString(3, value);
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "insertUserIdToCacheData crashed.");
        } finally {
            db.endTransaction();
        }
    }



    /**
     * 更新本地群的最新readmark时间
     *
     * @param time
     */
    public void updateGroupReadMarkTime(String time) {
        String sql = "insert or replace into IM_Cache_Data(key,type, value" + ") values" +
                "(?,?, ?);";
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            stat.bindString(1, CacheDataType.GROUP_READMARK);
            stat.bindString(2, String.valueOf(CacheDataType.GROUP_READMARK_TIME));
            stat.bindString(3, time);
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "insertUserIdToCacheData crashed.");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 获取本地最新的群readmark时间
     *
     * @return
     */
    public String getLatestGroupRMTime() {
        String sql = "select value from IM_Cache_Data where  type = " + CacheDataType.GROUP_READMARK_TIME;
        SQLiteDatabase db = helper.getReadableDatabase();
        String time = null;
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                time = cursor.getString(0);
            }
        } catch (Exception e) {
            Logger.e(e, "getLatestGroupRMTime crashed.");
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return time;
    }

    /**
     * 更新备注
     *
     * @param value
     */
    public void updateMarkupNames(String value) {
        String sql = "insert or replace into IM_Cache_Data(key, type, value" + ") values" +
                "(?, ?, ?);";
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            stat.bindString(1, Constants.SYS.MARKUP_NAME);
            stat.bindString(2, String.valueOf(CacheDataType.MARKUP_NAMES));
            stat.bindString(3, value);
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "updateConversationParams crashed.");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 查询备注信息
     *
     * @return
     */
    public LruCache<String,String> selectMarkupNames() {

        LruCache<String,String> markupNames = new LruCache<>(CurrentPreference.MAX_MARKUP_NAMES_COUNT);

        String sql = " select subkey,value from im_user_config where pkey=?";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{CacheDataType.kMarkupNames});
        try {
            while (cursor.moveToNext()) {
                markupNames.put(cursor.getString(0), cursor.getString(1));
            }
        } catch (Exception e) {
            Logger.e(e, "selectMarkupNames crashed.");
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return markupNames;
    }

    public String selectMarkupNameById(String xmppid){
        String name = "";
        String sql = " select value from im_user_config where pkey=? and subkey=? ";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{CacheDataType.kMarkupNames,xmppid});
        try {
            while (cursor.moveToNext()) {
                name = cursor.getString(0);
                if(!TextUtils.isEmpty(name))
                    CurrentPreference.getInstance().getMarkupNames().put(xmppid,name);
            }
        } catch (Exception e) {
            Logger.e(e, "selectMarkupNameById crashed.");
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return name;
    }

    /**
     * 更新qchat 个人配置 众包参数
     */
    public void updateConversationParams(Map<String, Object> map) {
        String sql = "insert or replace into IM_Cache_Data(key, type, value" + ") values" +
                "(?, ?, ?);";

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                stat.bindString(1, key);
                stat.bindString(2, String.valueOf(CacheDataType.CONVERSATION_PARAMS));
                stat.bindString(3, value);
                stat.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "updateConversationParams crashed.");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 查询qchat 众包对应key的params
     *
     * @param key
     */
    public JSONObject selectConversationParam(String key) {
        String sql = "select * from IM_Cache_Data where key = ? AND type = " + CacheDataType.CONVERSATION_PARAMS;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{key});
        try {
            while (cursor.moveToNext()) {
                String value = cursor.getString(2);
                if (!TextUtils.isEmpty(value)) {
                    try {
                        return new JSONObject(value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            Logger.e(e, "selectConversationParam crashed.");
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 查询qchat 个人配置众包 所有会话的参数
     *
     * @return
     */
    public JSONObject selectAllConversationParams() {
        JSONObject jsonObject = new JSONObject();
        String sql = "select * from IM_Cache_Data where  type = " + CacheDataType.CONVERSATION_PARAMS;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                String value = cursor.getString(2);
                if (!TextUtils.isEmpty(value)) {
                    try {
                        jsonObject.put(cursor.getString(0), value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            Logger.e(e, "selectAllConversationParams crashed.");
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return jsonObject;
    }

    /**
     * 设置置顶版本号
     *
     * @param version
     */
    public void setConversationTopVersion(int version) {
        String sql = "insert or replace into IM_Cache_Data (key,type,value) values" +
                "('topversion'," + CacheDataType.TOPVER + ",?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            db.beginTransactionNonExclusive();
            stat.bindString(1, version + "");
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 查找发现页配置
     */
    public FoundConfiguration SelectFoundConfiguration() {
        deleteJournal();
        String str = "";
        FoundConfiguration foundConfiguration = new FoundConfiguration();
        String sql = "select value from IM_Cache_Data where type = ? and key = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{CacheDataType.FoundConfigurationType + "", CacheDataType.FoundConfiguration});

        try {


            while (cursor.moveToNext()) {
                str = cursor.getString(0);
                foundConfiguration = JsonUtils.getGson().fromJson(str, FoundConfiguration.class);
                return foundConfiguration;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return foundConfiguration;
    }


    /**
     * 将发现页插入cache表
     *
     * @param value
     */
    public void insertFoundConfigurationToCacheData(String value) {
        String sql = "insert or replace into IM_Cache_Data(key,type, value" + ") values" +
                "(?,?, ?);";
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            stat.bindString(1, CacheDataType.FoundConfiguration);
            stat.bindString(2, String.valueOf(CacheDataType.FoundConfigurationType));
            stat.bindString(3, value);
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "insertUserIdToCacheData crashed.");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 查询置顶数
     *
     * @return
     */
    public int querryConversationTopCount() {
        String sql = "select count(1) from IM_Cache_Data where type = " + CacheDataType.TOP;
        Cursor cursor = null;
        int count = 0;
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return count;
        }
    }

    /**
     * 清除数据库内所有置顶信息
     */
    public void deleteConversationTopInfo() {
        String sql = "delete from IM_Cache_Data where type = " + CacheDataType.TOP;
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            db.beginTransactionNonExclusive();
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 登陆完成后, 批量获取当前置顶列表,
     * 更新版本号,清空本地置顶信息,重新插入
     *
     * @param list
     * @param version
     */
    public void setBulkConversationTopOrCancel(List<RecentConversation> list, String version) {
        setConversationTopVersion(Integer.parseInt(version));
        deleteConversationTopInfo();
        for (int i = 0; i < list.size(); i++) {
            setConversationTopOrCancel(list.get(i));
        }
    }

//    public void setBulkConversationTopOrCancel(List<RecentConversation> list, String version) {
//        setConversationTopVersion(Integer.parseInt(version));
//        deleteConversationTopInfo();
//        for (int i = 0; i < list.size(); i++) {
//            setConversationTopOrCancel(list.get(i));
//        }
//    }


    /**
     * 从数据库中删除message
     *
     * @param message
     */
    public void DeleteMessageByMessage(IMMessage message) {
        String sql = "Delete from IM_Message Where MsgId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            db.beginTransactionNonExclusive();
            stat.bindString(1, message.getMessageId());
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 删除一条会话根据id
     *
     * @param xmppId
     * @param realUserId
     */
    public void DeleteSessionAndMessageByXmppId(String xmppId, String realUserId) {
        String sql = "delete from IM_SessionList where XmppId = ? and RealJid = ?;";
        String deleteMessage = "Delete from IM_Message Where Xmppid = ? and RealJid = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        SQLiteStatement dstat = db.compileStatement(deleteMessage);
        try {
            db.beginTransactionNonExclusive();
            stat.bindString(1, xmppId);
            stat.bindString(2, realUserId);
            dstat.bindString(1, xmppId);
            dstat.bindString(2, realUserId);
            stat.executeUpdateDelete();
            dstat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 跟返回json历史数据匹配方法
     *
     * @param list
     * @param selfUser
     * @param isUpturn 是否是会话里面上翻拉历史
     * @return 成功 or 失败
     * @throws Exception
     */
    public boolean bulkInsertGroupHistoryFroJson(List<JSONMucHistorys.DataBean> list, String selfUser, boolean isUpturn) throws Exception {
        String sql = "insert or ignore into IM_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                "Platform, Type, State, Direction,LastUpdateTime,ReadedTag,MessageRaw,RealJid,ExtendedInfo) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?);";

        String revokeSql = "update IM_Message set Content = ?, Direction = ?,Type = ? where MsgId = ?";//撤销消息

        String ssql = "insert or replace into IM_SessionList (XmppId,RealJid,UserId,LastMessageId," +
                "LastUpdateTime,ChatType,ExtendedFlag) values(?,?,?,?,?,?,?);";


        String updateSql = "update IM_SessionList set LastMessageId = ?," +
                "LastUpdateTime = ? where XmppId = ?;";

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        boolean success = true;
        try {
            SQLiteStatement imstat = db.compileStatement(sql);
            SQLiteStatement revokeStat = db.compileStatement(revokeSql);
            SQLiteStatement insertSessionStat = db.compileStatement(ssql);

            SQLiteStatement updateSessionStat = db.compileStatement(updateSql);
            //遍历最外层
            int size = list.size();
            for (int i = 0; i < size; i++) {
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
                    imstat.bindString(1, msgId);
                    imstat.bindString(2, message.getTo());
                    imstat.bindString(3, message.getSendjid());
                    imstat.bindString(4, "");
                    imstat.bindString(5, body.getContent());
                    if (!TextUtils.isEmpty(message.getClient_type())) {
                        switch (message.getClient_type()) {
                            case "ClientTypeMac":
                                imstat.bindString(6, 1 + "");
                                break;
                            case "ClientTypeiOS":
                                imstat.bindString(6, 2 + "");
                                break;
                            case "ClientTypePC":
                                imstat.bindString(6, 3 + "");
                                break;
                            case "ClientTypeAndroid":
                                imstat.bindString(6, 4 + "");
                                break;
                            default:
                                imstat.bindString(6, 0 + "");
                                break;
                        }
                    } else if (!TextUtils.isEmpty(body.getMaType())) {
                        imstat.bindString(6, body.getMaType());
                    } else {
                        imstat.bindString(6, 0 + "");
                    }
                    String msgType = body.getMsgType();
                    imstat.bindString(7, msgType);
                    //批量获取的历史记录,消息状态应该都是正常的
                    imstat.bindString(8, String.valueOf(MessageStatus.LOCAL_STATUS_SUCCESS_PROCESSION));
                    //如果真实发送人等于自己,证明是自己发出的
                    //否则是其他人发出,根据这个条件判断方向
                    if (selfUser.equals(message.getRealfrom())) {
                        imstat.bindString(9, "1");
                    } else {
                        imstat.bindString(9, "0");
                    }
                    if ("-1".equals(body.getMsgType())) {
                        imstat.bindString(9, "2");
                        imstat.bindString(3, message.getFrom());
                        imstat.bindString(5, msg.getNick() + body.getContent());
                    }
                    if ("15".equals(body.getMsgType())) {
                        imstat.bindString(9, "2");
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
                    imstat.bindString(10, t);
                    //登录拉历史插成未读 会话上翻拉历史插已读
                    imstat.bindString(11, String.valueOf(isUpturn ? MessageStatus.REMOTE_STATUS_CHAT_READED : MessageStatus.REMOTE_STATUS_CHAT_DELIVERED));
                    imstat.bindString(12, JsonUtils.getGson().toJson(msg));
                    imstat.bindString(13, message.getTo());
                    if (!TextUtils.isEmpty(body.getExtendInfo())) {
                        imstat.bindString(14, body.getExtendInfo());
                    } else if (!TextUtils.isEmpty(body.getBackupinfo())) {
                        imstat.bindString(14, body.getBackupinfo());
                    } else {
                        imstat.bindString(14, "");
                    }

                    updateSessionStat.bindString(1, body.getId());
                    updateSessionStat.bindString(2, t);
                    updateSessionStat.bindString(3, message.getTo());


                    if (!isUpturn) {//上翻历史 不更新sessionlist
                        int count = updateSessionStat.executeUpdateDelete();
                        if (count <= 0) {
                            insertSessionStat.bindString(1, message.getTo());
                            insertSessionStat.bindString(2, message.getTo());
                            insertSessionStat.bindString(3, message.getTo());
                            insertSessionStat.bindString(4, body.getId());
                            insertSessionStat.bindString(5, t);
                            insertSessionStat.bindString(6, "1");
                            insertSessionStat.bindString(7, "7");
                            insertSessionStat.executeInsert();
                        }
                    }

                    long count = imstat.executeInsert();
                    if (count <= 0 && msgType.equals(String.valueOf(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE))) {//撤销消息 update body
                        revokeStat.bindString(1, body.getContent());
                        revokeStat.bindString(2, String.valueOf(IMMessage.DIRECTION_MIDDLE));
                        revokeStat.bindString(3, String.valueOf(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE));
                        revokeStat.bindString(4, msgId);
                        revokeStat.executeUpdateDelete();
                    }

                } catch (Exception e) {
                    success = false;
                    continue;

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {

            Logger.e(e, "bulkInsertMessage crashed.");
            throw e;
        } finally {
            db.endTransaction();
        }

        return success;
    }

    /******************************************EverNote&密码箱 start*****************************************************/
    public List<DailyMindMain> getDailyMain(int type, int offset, int number) {

        List<DailyMindMain> dailyMindMains = new ArrayList<>();
        if (helper == null) {
            return dailyMindMains;
        }
        String sql = "SELECT * FROM Daily_main WHERE state<>-1 and type=" + type + " ORDER BY version DESC Limit ?,?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{offset + "", number + ""});
        try {
            while (cursor.moveToNext()) {
                DailyMindMain dailyMindMain = new DailyMindMain();
                dailyMindMain.qid = cursor.getInt(0);
                dailyMindMain.version = cursor.getString(1);
                dailyMindMain.type = cursor.getInt(2);
                dailyMindMain.title = cursor.getString(3);
                dailyMindMain.desc = cursor.getString(4);
                dailyMindMain.content = cursor.getString(5);
                dailyMindMain.state = cursor.getInt(6);
                dailyMindMains.add(dailyMindMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return dailyMindMains;
    }

    public DailyMindMain getDailyMainByTitle() {
        if (helper == null) {
            return null;
        }
        List<DailyMindMain> dailyMindMains = new ArrayList<>();
        String sql = "SELECT * FROM Daily_main WHERE type =100 AND state<>-1 ORDER BY version DESC";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                DailyMindMain dailyMindMain = new DailyMindMain();
                dailyMindMain.qid = cursor.getInt(0);
                dailyMindMain.version = cursor.getString(1);
                dailyMindMain.type = cursor.getInt(2);
                dailyMindMain.title = cursor.getString(3);
                dailyMindMain.desc = cursor.getString(4);
                dailyMindMain.content = cursor.getString(5);
                dailyMindMain.state = cursor.getInt(6);
                dailyMindMains.add(dailyMindMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (dailyMindMains.size() > 0)
            return dailyMindMains.get(0);
        else return null;
    }

    public void insertDailyMain(DailyMindMain dailyMindMain) {
        String sql = "insert or replace into Daily_main(qid, version, type, title, desc, " +
                "content, state) values" +
                "(?, ?, ?, ?, ?, ?, ?);";

        if (helper == null) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        try {
            SQLiteStatement stat = db.compileStatement(sql);
            stat.bindString(1, dailyMindMain.qid + "");
            stat.bindString(2, dailyMindMain.version);
            stat.bindString(3, dailyMindMain.type + "");
            stat.bindString(4, dailyMindMain.title);
            stat.bindString(5, dailyMindMain.desc);
            stat.bindString(6, dailyMindMain.content);
            stat.bindString(7, dailyMindMain.state + "");

            stat.executeInsert();


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "insertDailyMain crashed.");

        } finally {
            db.endTransaction();
        }
    }

    public void insertMultiDailyMain(List<DailyMindMain> dailyMindMains) {
        String sql = "insert or IGNORE into Daily_main(qid, version, type, title, desc, " +
                "content, state) values" +
                "(?, ?, ?, ?, ?, ?, ?);";

        if (helper == null) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            int count = dailyMindMains == null ? 0 : dailyMindMains.size();
            for (int i = 0; i < count; i++) {
                DailyMindMain dailyMindMain = dailyMindMains.get(i);
                stat.bindString(1, dailyMindMain.qid + "");
                stat.bindString(2, dailyMindMain.version);
                stat.bindString(3, dailyMindMain.type + "");
                stat.bindString(4, dailyMindMain.title);
                stat.bindString(5, dailyMindMain.desc);
                stat.bindString(6, dailyMindMain.content == null ? "" : dailyMindMain.content);
                stat.bindString(7, dailyMindMain.state + "");
                stat.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "insertMultiDailyMain crashed.");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteDailyMain(String qid) {
        String sql = "delete from Daily_main where qid = ?;";
        if (helper == null) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            db.beginTransactionNonExclusive();
            stat.bindString(1, qid);
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void insertPasswordBoxSub(DailyMindSub dailyMindSub) {
        String sql = "insert or replace into Password_box_sub(qsid,qid, version, type, title, desc, " +
                "content, P,U,time,state) values" +
                "(?, ?, ?, ?, ?, ?, ?,?,?,?,?);";
        if (helper == null) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        try {
            SQLiteStatement stat = db.compileStatement(sql);
            stat.bindString(1, dailyMindSub.qsid + "");
            stat.bindString(2, dailyMindSub.qid + "");
            stat.bindString(3, dailyMindSub.version);
            stat.bindString(4, dailyMindSub.type + "");
            stat.bindString(5, dailyMindSub.title);
            stat.bindString(6, TextUtils.isEmpty(dailyMindSub.desc) ? "" : dailyMindSub.desc);
            stat.bindString(7, dailyMindSub.content);
            stat.bindString(8, TextUtils.isEmpty(dailyMindSub.P) ? "" : dailyMindSub.P);
            stat.bindString(9, TextUtils.isEmpty(dailyMindSub.U) ? "" : dailyMindSub.U);
            stat.bindString(10, TextUtils.isEmpty(dailyMindSub.time) ? "" : dailyMindSub.time);
            stat.bindString(11, dailyMindSub.state + "");

            stat.executeInsert();


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "insertPasswordBoxSub crashed.");

        } finally {
            db.endTransaction();
        }
    }

    public void updatePasswordBoxSub(DailyMindSub dailyMindSub) {
        String sql = "update Password_box_sub set version=?,title=?,desc=?,content=?,P=?,U=?,time=?,state=? where qsid =?";
        if (helper == null) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            db.beginTransactionNonExclusive();
            stat.bindString(1, dailyMindSub.version);
            stat.bindString(2, dailyMindSub.title);
            stat.bindString(3, TextUtils.isEmpty(dailyMindSub.desc) ? "" : dailyMindSub.desc);
            stat.bindString(4, dailyMindSub.content);
            stat.bindString(5, TextUtils.isEmpty(dailyMindSub.P) ? "" : dailyMindSub.P);
            stat.bindString(6, TextUtils.isEmpty(dailyMindSub.U) ? "" : dailyMindSub.U);
            stat.bindString(7, TextUtils.isEmpty(dailyMindSub.time) ? "" : dailyMindSub.time);
            stat.bindString(8, dailyMindSub.state + "");
            stat.bindString(9, dailyMindSub.qsid + "");
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<DailyMindSub> getPasswordBoxSub(int offset, int number, int qid) {
        List<DailyMindSub> dailyMindSubs = new ArrayList<>();
        String sql = "SELECT * FROM Password_box_sub WHERE qid=" + qid + " ORDER BY version DESC Limit ?,?";
        if (helper == null) {
            return dailyMindSubs;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{offset + "", number + ""});
        try {
            while (cursor.moveToNext()) {
                DailyMindSub dailyMindSub = new DailyMindSub();
                dailyMindSub.qsid = cursor.getInt(0);
                dailyMindSub.qid = cursor.getInt(1);
                dailyMindSub.version = cursor.getString(2);
                dailyMindSub.type = cursor.getInt(3);
                dailyMindSub.title = cursor.getString(4);
                dailyMindSub.desc = cursor.getString(5);
                dailyMindSub.content = cursor.getString(6);
                dailyMindSub.P = cursor.getString(7);
                dailyMindSub.U = cursor.getString(8);
                dailyMindSub.time = cursor.getString(9);
                dailyMindSub.state = cursor.getInt(10);
                dailyMindSubs.add(dailyMindSub);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return dailyMindSubs;
    }

    public void insertMultiPasswordBoxSub(List<DailyMindSub> dailyMindSubs) {
        String sql = "insert or IGNORE into Password_box_sub(qsid,qid, version, type, title, desc, " +
                "content, P,U,time,state) values" +
                "(?, ?, ?, ?, ?, ?, ?,?,?,?,?);";
        if (helper == null) {
            return;
        }
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            int count = dailyMindSubs == null ? 0 : dailyMindSubs.size();
            for (int i = 0; i < count; i++) {
                DailyMindSub dailyMindSub = dailyMindSubs.get(i);
                stat.bindString(1, dailyMindSub.qsid + "");
                stat.bindString(2, dailyMindSub.qid + "");
                stat.bindString(3, dailyMindSub.version);
                stat.bindString(4, dailyMindSub.type + "");
                stat.bindString(5, dailyMindSub.title);
                stat.bindString(6, TextUtils.isEmpty(dailyMindSub.desc) ? "" : dailyMindSub.desc);
                stat.bindString(7, dailyMindSub.content);
                stat.bindString(8, TextUtils.isEmpty(dailyMindSub.P) ? "" : dailyMindSub.P);
                stat.bindString(9, TextUtils.isEmpty(dailyMindSub.U) ? "" : dailyMindSub.U);
                stat.bindString(10, TextUtils.isEmpty(dailyMindSub.time) ? "" : dailyMindSub.time);
                stat.bindString(11, dailyMindSub.state + "");
                stat.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "insertMultiPasswordBoxSub crashed.");
        } finally {
            db.endTransaction();
        }
    }

    public DailyMindSub getPasswordBoxSubByTitle(String title, String qid) {
        List<DailyMindSub> dailyMindSubs = new ArrayList<>();
        String sql = "SELECT * FROM Password_box_sub WHERE title like '%" + title + "%' AND qid=" + qid + " ORDER BY version";
        if (helper == null) {
            return null;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                DailyMindSub dailyMindSub = new DailyMindSub();
                dailyMindSub.qsid = cursor.getInt(0);
                dailyMindSub.qid = cursor.getInt(1);
                dailyMindSub.version = cursor.getString(2);
                dailyMindSub.type = cursor.getInt(3);
                dailyMindSub.title = cursor.getString(4);
                dailyMindSub.desc = cursor.getString(5);
                dailyMindSub.content = cursor.getString(6);
                dailyMindSub.P = cursor.getString(7);
                dailyMindSub.U = cursor.getString(8);
                dailyMindSub.time = cursor.getString(9);
                dailyMindSub.state = cursor.getInt(10);
                dailyMindSubs.add(dailyMindSub);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (dailyMindSubs.size() > 0)
            return dailyMindSubs.get(0);
        else return null;
    }
    /******************************************EverNote&密码箱 end*****************************************************/

    /******************************************配置config start ****************************************************/

    public void updateConfig() {
        String sql = "insert or replace into Config(id, proFile, preference" + ") values" +
                "(?, ?, ?);";

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            String pre = JsonUtils.getGson().toJson(CurrentPreference.getInstance().getPreference());
            String pro = JsonUtils.getGson().toJson(CurrentPreference.getInstance().getProFile());
            stat.bindString(1, "0");
            stat.bindString(2, pro);
            stat.bindString(3, pre);

            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "inserConfig crashed.");
        } finally {
            db.endTransaction();
        }
    }

    public CurrentPreference.Preference getPreference() {
        CurrentPreference.Preference preference;

        String sql = "select * from Config";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                String p = cursor.getString(2);
                preference = JsonUtils.getGson().fromJson(p, CurrentPreference.Preference.class);
                return preference;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public CurrentPreference.ProFile getProFile() {
        CurrentPreference.ProFile proFile;

        String sql = "select * from Config";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                String p = cursor.getString(1);
                proFile = JsonUtils.getGson().fromJson(p, CurrentPreference.ProFile.class);
                return proFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 返回RN本地搜索数据
     *
     * @param key
     * @param start
     * @param len
     * @return
     */
    public List<RNSearchData> getLocalSearch(String key, int start, int len) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String outGroupStr = "select * from IM_Group where Name like ? and GroupId not like '%ost2'";
        String domain = QtalkNavicationService.getInstance().getXmppdomain();
        List<RNSearchData> result = new ArrayList<>();
        Cursor outGroupCursor = db.rawQuery(outGroupStr, new String[]{"%" + key + "%", "%" + domain});
        try {
            RNSearchData outGroup = new RNSearchData();
            outGroup.setHasMore(0);
            outGroup.setIsLoaclData(1);
            outGroup.setTodoType(1);
            outGroup.setGroupPriority(0);
            outGroup.setGroupLabel("外域群组");
            outGroup.setGroupId("Q04");
            outGroup.setDefaultportrait(IMLogicManager.getDefaultMucImage());
            List<RNSearchData.InfoBean> outGroupList = new ArrayList<>();
            while (outGroupCursor.moveToNext()) {
                RNSearchData.InfoBean outGroupInfo = new RNSearchData.InfoBean();
                //设置外域群名
                outGroupInfo.setLabel(outGroupCursor.getString(1));
                //设置外域群图标
                outGroupInfo.setIcon(outGroupCursor.getString(3));
                //设置外域群Id
                outGroupInfo.setUri(outGroupCursor.getString(0));
                //设置外域群Content
                outGroupInfo.setContent("");
                //将info放入list
                outGroupList.add(outGroupInfo);
            }
            //将外域list放入数据对象
            outGroup.setInfo(outGroupList);
            //将外域数据对象放入最终返回对象
            result.add(outGroup);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outGroupCursor != null) {
                outGroupCursor.close();
            }
        }


        //返回外域群组
        return result;
    }

    public List<RNSearchData.InfoBean> getLocalUser(String key, int start, int len) {
        List<RNSearchData.InfoBean> result = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from IM_User WHERE UserId like ? or Name like ? or SearchIndex like ? ORDER by Name DESC LIMIT ?,?";
        Cursor cursor = db.rawQuery(sql, new String[]{("%" + key + "%"), ("%" + key + "%"), ("%" + key + "%"), start + "", len + ""});
        try {


            while (cursor.moveToNext()) {
                RNSearchData.InfoBean info = new RNSearchData.InfoBean();
                info.setUserId(cursor.getString(0));
                info.setName(cursor.getString(2));
                info.setXmppId(cursor.getString(1));
                if (!TextUtils.isEmpty(cursor.getString(4))) {
                    info.setHeaderSrc(cursor.getString(4));
                }
                result.add(info);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<RNSearchData.InfoBean> getLocalGroup(String key, int start, int len) {
        List<RNSearchData.InfoBean> result = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String domain = QtalkNavicationService.getInstance().getXmppdomain();
        String sql = "select * from IM_Group where( Name like ? OR GroupId like ? )and GroupId like ? and Name NOTNULL ORDER BY Name LIMIT ?,?";
        Cursor cursor = db.rawQuery(sql, new String[]{("%" + key + "%"), ("%" + key + "%"), ("%" + domain), start + "", len + ""});
        try {
            while (cursor.moveToNext()) {
                RNSearchData.InfoBean info = new RNSearchData.InfoBean();
                info.setGroupId(cursor.getString(0));
                info.setName(cursor.getString(1));
                info.setIntroduce(cursor.getString(2));
                if (!TextUtils.isEmpty(cursor.getString(3))) {
                    info.setHeaderSrc(cursor.getString(3));
                }
                info.setLastUpdateTime(cursor.getInt(5));
                result.add(info);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public List<RNSearchData.InfoBean> getOutGroup(String key, int start, int len) {
        List<RNSearchData.InfoBean> result = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String domain = QtalkNavicationService.getInstance().getXmppdomain();
        String sql = "select * from IM_Group where( Name like ? OR GroupId like ? )and GroupId not like ? and Name NOTNULL ORDER BY Name LIMIT ?,?";
        Cursor cursor = db.rawQuery(sql, new String[]{("%" + key + "%"), ("%" + key + "%"), ("%" + domain), start + "", len + ""});
        try {
            while (cursor.moveToNext()) {
                RNSearchData.InfoBean info = new RNSearchData.InfoBean();
                info.setGroupId(cursor.getString(0));
                info.setName(cursor.getString(1));
                info.setIntroduce(cursor.getString(2));
                if (!TextUtils.isEmpty(cursor.getString(3))) {
                    info.setHeaderSrc(cursor.getString(3));
                }
//                info.setContent();
                info.setLastUpdateTime(cursor.getInt(5));
                result.add(info);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 查询绑定用户信息
     *
     * @return
     */
    public List<Nick> selectCollectionUser() {
        deleteJournal();
        List<Nick> result = new ArrayList<>();
        // String sql = "select *,(select count(1) from IM_Message_Collection as a  WHERE a.originto = " + "c.XmppId and " + MessageStatus.REMOTE_STATUS_CHAT_READED + "<> (a.Readedtag &" + MessageStatus.REMOTE_STATUS_CHAT_READED + ") ) as notNum from IM_Collection_User as c ORDER by XmppId ,BIND";
        String sql = "select *,(select count(1) from IM_Message_Collection as a left join IM_Message as b on a.msgid= b.msgid WHERE a.originto = c.XmppId and 0x02<> (b.Readedtag & 0x02) ) as notNum  from IM_Collection_User as c ORDER by XmppId ,BIND";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                Nick nick = new Nick();
                nick.setXmppId(cursor.getString(0));
                nick.setCollectionBind(cursor.getInt(1));
                nick.setCollectionUnReadCount(cursor.getInt(2));
                result.add(nick);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 查询全部代收消息
     *
     * @return
     */
    public List<CollectionConversation> SelectCollectionConversationList(String xmppid) {
        deleteJournal();
        List<CollectionConversation> list = new ArrayList<>();
        Cursor cursor = null;
        String sql = "select a.MsgId,a.Originfrom,a.Originto,a.Origintype,b.XmppId,b.'from', b.'to',b.content,b.type,b.State,b.Direction,b.ReadedTag,b.LastupdateTime,b.RealJid,(select count(1) from im_message as c,IM_Message_Collection as d where 0x02<>(c.readedtag&0x02) and c.msgid = d.msgId and d.originfrom = a.originfrom and d.Originto = a.Originto ) as uncount from IM_Message_Collection as a left join IM_Message as b on a.msgId = b.msgid where Originto = ? group by originfrom,originto ORDER by LastUpdateTime DESC";
        // String sql = "select a.MsgId,a.Originfrom,a.Originto,a.Origintype,b.XmppId,b.'from',b.'to',b.content,b.type,b.State,b.Direction,b.ReadedTag,b.LastupdateTime,b.RealJid,(select count(1) from IM_Message as c where " + MessageStatus.REMOTE_STATUS_CHAT_READED + "<>(b.ReadedTag&" + MessageStatus.REMOTE_STATUS_CHAT_READED + ")  and  c.'from' = a.originfrom and c.'to' = a.Originto ) as uncount from IM_Message_Collection as a left join IM_Message as b on a.msgId = b.msgid where Originto = ? group by originfrom,originto ORDER by LastUpdateTime DESC";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            cursor = db.rawQuery(sql, new String[]{xmppid});
            while (cursor.moveToNext()) {
                CollectionConversation rc = new CollectionConversation();
                rc.setMsgId(cursor.getString(0));


                rc.setOriginFrom(cursor.getString(1));
                rc.setOriginTo(cursor.getString(2));
                rc.setOriginType(cursor.getString(3));
                rc.setXmppId(cursor.getString(4));
                rc.setFrom(cursor.getString(5));
                rc.setTo(cursor.getString(6));
//                rc.setContent(cursor.getString(9));
                String msg = cursor.getString(7);
                int type = Integer.parseInt(cursor.getString(8));
                rc.setType(type + "");
                if (type != ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE
                        && type != ProtoMessageOuterClass.MessageType.MessageTypeGroupNotify_VALUE
                        && type != 12) {
                    switch (type) {
                        case ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE:
//                            rc.setLastMsg((chatType == 1) ? (from + ":" + msg) : msg);
                            rc.setContent(msg);
                            break;
                        case ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE:
                            rc.setContent("[文件]");
                            break;
                        case ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE:
                            rc.setContent("[语音]");
                            break;
                        case ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE:
                            rc.setContent("[视频]");
                            break;
                        case ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE:
                            rc.setContent("[图片]");
                            break;
                        case ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE:
                            rc.setContent("[位置]");
                            break;
                        case ProtoMessageOuterClass.MessageType.MessageTypeSystem_VALUE://系统消息
                            rc.setContent("[系统消息]");
                            break;
                        case ProtoMessageOuterClass.MessageType.MessageTypeNotice_VALUE://通知消息
                            rc.setContent("[通知消息]");
                            break;
                        case ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuVcard_VALUE://抢单消息
                        case ProtoMessageOuterClass.MessageType.MessageTypeGrabMenuResult_VALUE://抢单状态消息
                            rc.setContent("[抢单消息]");
                            break;
                        default:
//                            rc.setLastMsg((chatType == 1) ? (from + ":" + msg) : msg);
                            rc.setContent(msg);
                            break;
                    }

                } else {
                    rc.setContent(msg);
                }
                rc.setState(cursor.getString(9));
                rc.setDirection(cursor.getString(10));
                rc.setReadedTag(cursor.getString(11));
                rc.setLastUpdateTime(cursor.getString(12));
                rc.setRealJid(cursor.getString(13));
                rc.setUnCount(cursor.getInt(14));
                list.add(rc);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.i("SelectCollectionConversationList error", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        return null;
    }

    public List<IMMessage> SelectHistoryCollectionChatMessage(String of, String ot, int count, int size) {
        deleteJournal();
        List<IMMessage> imMessageList = new ArrayList<>();
        String sql = "select b.*,a.Originfrom,a.Originto from IM_Message_Collection AS a left join IM_Message AS B ON a.msgId = b.msgId WHERE Originfrom = ? and Originto = ?  ORDER by b.LastUpdateTime DESC LIMIT ?,?\n";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{of, ot, count + "", size + ""});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                imMessage.setCollection(true);

//            imMessage.setNick(new Gson().fromJson( IMLogicManager.getInstance().getUserInfoByUserId(target,IMLogicManager.getInstance().getMyself(),false).toString(),Nick.class));
                //设置消息id
                imMessage.setId(cursor.getString(0));
                imMessage.setMessageID(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                imMessage.setMaType(cursor.getString(2));
                String from = cursor.getString(16);
                imMessage.setoFromId(cursor.getString(16));
                imMessage.setoToId(cursor.getString(17));
                XMPPJID target = XMPPJID.parseJID(from);
                String to = cursor.getString(4);
                //to
                imMessage.setToID(to);
                //消息正文
                imMessage.setBody(cursor.getString(5));
                //消息类型2
                imMessage.setMsgType(cursor.getInt(6));
                int direction = cursor.getInt(8);
                //from
                imMessage.setFromID(from);
                imMessage.setRealfrom(from);
//                imMessage.setRealfrom(from);
                //设置方向
                imMessage.setDirection(direction);
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setTime(new Date(time));
                imMessage.setExt(cursor.getString(14));
//
                imMessage.setReadState(cursor.getInt(10));
                imMessage.setMessageState(cursor.getInt(7));
                imMessageList.add(imMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imMessageList;
    }

    public List<IMMessage> SelectHistoryCollectionGroupChatMessage(String of, String ot, int start, int firstLoadCount) {
        deleteJournal();
        List<IMMessage> imMessageList = new ArrayList<>();
        String sql = "select b.*,a.Originfrom,a.Originto from IM_Message_Collection AS a left join IM_Message AS B ON a.msgId = b.msgId WHERE Originfrom = ? and Originto = ?  ORDER by b.LastUpdateTime DESC LIMIT ?,?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{of, ot, start + "", firstLoadCount + ""});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                imMessage.setCollection(true);
                //设置消息id
                imMessage.setId(cursor.getString(0));
                imMessage.setMessageID(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                imMessage.setToID(cursor.getString(1));
                imMessage.setMaType(cursor.getString(2));
                String nickName = cursor.getString(3);
//                imMessage.setNickName(nickName);
                imMessage.setRealfrom(nickName);
                imMessage.setFromID(nickName);
                imMessage.setoFromId(cursor.getString(16));
                imMessage.setoToId(cursor.getString(17));
                //因为取得是历史群消息,有部分功能需要知道msg的消息类型
                imMessage.setType(ConversitionType.MSG_TYPE_GROUP);
////                //from
//                imMessage.setFromID(cursor.getString(3));
//                //to
//                imMessage.setToID(cursor.getString(4));
                //消息正文
                imMessage.setBody(cursor.getString(5));
                imMessage.setMsgType(cursor.getInt(6));
                //设置方向
                imMessage.setDirection(cursor.getInt(8));
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setExt(cursor.getString(14));
                imMessage.setTime(new Date(time));
                imMessage.setReadState(cursor.getInt(10));
                imMessage.setMessageState(cursor.getInt(7));
                imMessageList.add(imMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imMessageList;
    }

    /**
     * 插入代收用户名片
     *
     * @param data
     */
    public void InsertCollectionCard(List<CollectionCardData.DataBean> data) {
        String sql = "insert or REPLACE into IM_Collection_User_Card (UserId,XmppId,Name,HeaderSrc)values(?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            for (int i = 0; i < data.size(); i++) {
                CollectionCardData.DataBean d = data.get(i);
                stat.bindString(1, d.getUsername());
                stat.bindString(2, d.getUsername() + "@" + d.getDomain());
                stat.bindString(3, d.getUsernick());
                stat.bindString(4, d.getUrl());
                stat.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 插入代收群名片
     *
     * @param data
     */
    public void InsertCollectionMucCard(List<CollectionMucCardData.DataBean> data) {
        String sql = "insert or replace into IM_Collection_Group_Card (GroupId,Name,Introduce,HeaderSrc) values(?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            for (int i = 0; i < data.size(); i++) {
                CollectionMucCardData.DataBean d = data.get(i);
                stat.bindString(1, d.getMuc_name());
                stat.bindString(2, d.getShow_name());
                stat.bindString(3, d.getMuc_desc());
                stat.bindString(4, d.getMuc_pic());
                stat.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 查询代收单条会话未读数
     *
     * @param of
     * @param ot
     * @return
     */
    public int SelectCollectionUnReadCountByConvid(String of, String ot) {
        String sql = "select count() from im_message_collection as a left join im_message as b on a.msgid=b.msgid where a.originfrom =? and a.originto = ? and " + MessageStatus.REMOTE_STATUS_CHAT_READED + " <>(b.ReadedTag&" + MessageStatus.REMOTE_STATUS_CHAT_READED + ")";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{of, ot});
        try {
            while (cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * 获取所有收到的历史单聊消息未设置已送达的list
     *
     * @param xmppid
     * @param state
     * @return
     */
    public List<MessageStateSendJsonBean> getMessageStateSendNotXmppIdJson(String xmppid, String state) {
        List<MessageStateSendJsonBean> list = new ArrayList<>();
        String sql = "SELECT a.'From', GROUP_CONCAT(a.MsgId) as msgIdList  FROM IM_Message as a WHERE (" + MessageStatus.LOCAL_STATUS_PROCESSION + " & a.ReadedTag)<>" + MessageStatus.LOCAL_STATUS_PROCESSION + " and a.'From' <> ? GROUP By a.'From';";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{xmppid});
            while (cursor.moveToNext()) {
                MessageStateSendJsonBean bean = new MessageStateSendJsonBean();
                bean.setUserid(cursor.getString(0));
                String str = cursor.getString(1);
                String[] array = str.split(",");
                JSONArray ja = new JSONArray();
                for (int i = 0; i < array.length; i++) {
                    JSONObject jb = new JSONObject();
                    jb.put("id", array[i]);
                    ja.put(jb);
                }
                bean.setJsonArray(ja);
                list.add(bean);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public void updateMessageStateByJsonArray(JSONArray jsonArray) {
        String sql = "update IM_Message set  ReadedTag = (ReadedTag |" + MessageStatus.LOCAL_STATUS_PROCESSION + ") where MsgId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            SQLiteStatement stat = db.compileStatement(sql);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                stat.bindString(1, JSONUtils.getStringValue(jsonObject, "id"));
                stat.executeUpdateDelete();
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 更新代收消息已读
     *
     * @param of
     * @param ot
     */
    public void updateCollectionRead(String of, String ot) {
        //  String sql = "update IM_Message_Collection set ReadedTag= (ReadedTag |" + MessageStatus.REMOTE_STATUS_CHAT_READED + ")" + " Where Originfrom = ? AND ORIGINTO = ?";
        String sql = "update im_message set readedtag = (readedtag |" + MessageStatus.REMOTE_STATUS_CHAT_READED + ") WHERE MsgId in (SELECT MsgId From IM_Message_Collection Where Originfrom = ? AND ORIGINTO = ?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            stat.bindString(1, of);
            stat.bindString(2, ot);
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 插入当前用户绑定代收账号及状态
     *
     * @param data
     */
    public void InsertCollectionUserByData(CollectionUserBindData data) {
        String insertSql = "insert or replace into IM_Collection_User(XmppId,BIND) values (?,?)";
        List<CollectionUserBindData.DataBean> list = data.getData();
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(insertSql);
            for (int i = 0; i < list.size(); i++) {
                stat.bindString(1, list.get(i).getBindname() + "@" + list.get(i).getBindhost());
                stat.bindString(2, list.get(i).getAction() + "");
                stat.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 更新群阅读指针
     *
     * @param jsonReadMark
     */
    public void updateIMMessageMucRead(JSONReadMark jsonReadMark) {

        String sql1 = "select LastUpdateTime from IM_Message where XmppId = ? and ReadedTag&" + MessageStatus.REMOTE_STATUS_CHAT_READED + "=" + MessageStatus.REMOTE_STATUS_CHAT_READED + " order by LastUpdateTime desc LIMIT 1";

        String sql2 = "update IM_Message set ReadedTag = (ReadedTag|" + MessageStatus.REMOTE_STATUS_CHAT_READED + ") where XmppId = ? and LastUpdateTime <= ? and LastUpdateTime > ? ";

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat2 = db.compileStatement(sql2);

            for (int i = 0; i < jsonReadMark.getData().size(); i++) {

                JSONReadMark.DataBean data = jsonReadMark.getData().get(i);
                String date = data.getDate();
                if (data.getDate().equals("0")) {
                    continue;
                }
                long time = 0;
                String xmppid = data.getMuc_name() + "@" + data.getDomain();
                long start = System.currentTimeMillis();
                Cursor cursor = db.rawQuery(sql1, new String[]{xmppid});
                try {
                    while (cursor.moveToNext()) {
                        time = cursor.getLong(0);
                    }
                } catch (Exception e) {

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    Logger.i("query耗时：" + (System.currentTimeMillis() - start));
                }
                stat2.bindString(1, xmppid);
                stat2.bindLong(2, Long.parseLong(date));
                stat2.bindLong(3, time);

                int count = stat2.executeUpdateDelete();
                Logger.i("更新了" + count + "条已读");
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }
    }

    public boolean updateChatHistoryStateForJson(List<NewReadStateByJson.DataBean> list) {
        boolean success = true;
        String newReadSql = "update IM_Message set ReadedTag = (ReadedTag|?) where MsgId = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();

        try {
            SQLiteStatement nrs = db.compileStatement(newReadSql);
            int size = list.size();
            for (int i = 0; i < size; i++) {
                String id = list.get(i).getMsgid() + "";
                String state = list.get(i).getReadflag() + "";
                nrs.bindString(1, state);
                nrs.bindString(2, id);
                nrs.executeUpdateDelete();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }


    /**
     * 插入单聊历史数据,根据json
     *
     * @param list
     * @param selfId
     * @param isUpdown
     */
    public boolean bulkInsertChatHistoryFroJson(List<JSONChatHistorys.DataBean> list, String selfId, boolean isUpdown) {
        String imsql = "insert or ignore into IM_Message(MsgId, XmppId, \"From\", \"To\", Content, " +
                "Platform, Type, State, Direction,LastUpdateTime,ReadedTag,MessageRaw,RealJid,ExtendedInfo) values" +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?);";
        String revokeSql = "update IM_Message set Content = ?, Direction = ?,Type = ? where MsgId = ?";//撤销消息
        String imcsql = "insert or ignore into IM_Message_Collection(MsgId,Originfrom,Originto,Origintype) values" +
                "(?,?,?,?)";
        String icusql = "insert or ignore into IM_Collection_User (XmppId,BIND) values (?,?)";

        String issql = "insert or replace into IM_SessionList (XmppId,RealJid,UserId,LastMessageId," +
                "LastUpdateTime,ChatType,ExtendedFlag) values(?,?,?,?,?,?,?);";

        String updateSql = "update IM_SessionList set LastMessageId = ?," +
                "LastUpdateTime = ? where XmppId = ? and RealJid =?;";


        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        boolean success = true;
        try {
            SQLiteStatement imstat = db.compileStatement(imsql);
            SQLiteStatement revokeStat = db.compileStatement(revokeSql);
            SQLiteStatement imcstat = db.compileStatement(imcsql);
            SQLiteStatement icustat = db.compileStatement(icusql);
            SQLiteStatement isstat = db.compileStatement(issql);
            SQLiteStatement updatestat = db.compileStatement(updateSql);
            int size = list.size();
            for (int i = 0; i < size; ++i) {
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
                    updatestat.bindString(1, msgId);
                    updatestat.bindString(3, from.equals(selfId) ? to : from);
                    updatestat.bindString(4, from.equals(selfId) ? to : from);

                    isstat.bindString(1, from.equals(selfId) ? to : from);//普通情况sessionlist表的xmppid
                    isstat.bindString(2, from.equals(selfId) ? to : from);//偶同情况的realjid
                    isstat.bindString(3, from.equals(selfId) ? to : from);
                    isstat.bindString(4, body.getId());
                    isstat.bindString(6, "0");//默认情况下窗口类型是0
                    isstat.bindString(7, "6");

                    imstat.bindString(1, body.getId());
                    imstat.bindString(2, from.equals(selfId) ? to : from);//普通情况me的xmppid
                    imstat.bindString(3, from);
                    imstat.bindString(4, to);
                    imstat.bindString(5, body.getContent());
                    if (!TextUtils.isEmpty(message.getClient_type())) {
                        switch (message.getClient_type()) {
                            case "ClientTypeMac":
                                imstat.bindString(6, 1 + "");
                                break;
                            case "ClientTypeiOS":
                                imstat.bindString(6, 2 + "");
                                break;
                            case "ClientTypePC":
                                imstat.bindString(6, 3 + "");
                                break;
                            case "ClientTypeAndroid":
                                imstat.bindString(6, 4 + "");
                                break;
                            default:
                                imstat.bindString(6, 0 + "");
                                break;
                        }
                    } else if (!TextUtils.isEmpty(body.getMaType())) {
                        imstat.bindString(6, body.getMaType());
                    } else {
                        imstat.bindString(6, 0 + "");
                    }
                    String msgType = body.getMsgType();
                    imstat.bindString(7, msgType);

                    imstat.bindString(8, String.valueOf(MessageStatus.LOCAL_STATUS_SUCCESS));

                    if ("-1".equals(body.getMsgType())
                            || "15".equals(body.getMsgType())
                            || (ProtoMessageOuterClass.MessageType.MessageTypeRobotTurnToUser_VALUE + "").equals(body.getMsgType())) {
                        imstat.bindString(9, "2");
                    } else {
                        imstat.bindString(9, from.equals(selfId) ? "1" : "0");
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
                    imstat.bindString(10, t);
                    isstat.bindString(5, t);//sessionlist表的消息时间

                    updatestat.bindString(2, t);
                    imstat.bindString(11, String.valueOf(data.getRead_flag()));
                    imstat.bindString(12, JsonUtils.getGson().toJson(data));
                    imstat.bindString(13, from.equals(selfId) ? to : from);
                    //// TODO: 2017/12/6 少一个强化消息字段
                    imstat.bindString(14, TextUtils.isEmpty(body.getExtendInfo()) ? "" : body.getExtendInfo());

                    //代收表数据绑定
                    if ("collection".equals(message.getType())) {
                        //代收数据方向肯定在左边故写死
                        isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_COLLECTION));
                        imstat.bindString(9, "0");
                        imcstat.bindString(1, body.getId());
                        imcstat.bindString(2, ofrom);
                        imcstat.bindString(3, oto);
                        if ("chat".equals(message.getOrigintype())) {
                            imcstat.bindString(4, 0 + "");
                        } else if ("groupchat".equals(message.getOrigintype())) {
                            //代收群组类型消息 message表from存realJid
                            imstat.bindString(3, realFrom);
                            imcstat.bindString(4, 1 + "");
                        }else{
                            imstat.bindString(4,0+"");
                        }
                        imcstat.executeInsert();
                        icustat.bindString(1, oto);
                        icustat.bindString(2, "1");
                        icustat.executeInsert();
                    }
                    if ("consult".equals(message.getType())) {

                        if (from.equals(selfId)) {//我发送的的情况
                            if ("5".equals(message.getQchatid())) {//证明我是客服 在进行回复
                                imstat.bindString(13, realTo);
                                isstat.bindString(2, realTo);//consult消息情况下的 sessionlist表的realJid
                                updatestat.bindString(4, realTo);
                                isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER));//consult消息情况下 窗口类型
                            } else {//证明我是咨询者,默认咨询者,因为这类用户多
                                isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_CONSULT));
                            }
                        } else {//我接收的情况
                            if ("5".equals(message.getQchatid())) {//证明我是咨询者
                                isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_CONSULT));//consult消息情况下 窗口类型
                            } else {//其他情况,证明我是客服,
                                imstat.bindString(13, realFrom);
                                updatestat.bindString(4, realFrom);
                                isstat.bindString(2, realFrom);//consult消息情况下的 sessionlist表的realJid
                                isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER));
                            }
                        }
                    }
                    if ("headline".equals(message.getType())) {
                        imstat.bindString(2, Constants.SYS.SYSTEM_MESSAGE);
                        imstat.bindString(3, "history");
                        imstat.bindString(4, selfId);
                        imstat.bindString(9, "0");
                        imstat.bindString(13, Constants.SYS.SYSTEM_MESSAGE);
                        isstat.bindString(1, Constants.SYS.SYSTEM_MESSAGE);
                        updatestat.bindString(3, Constants.SYS.SYSTEM_MESSAGE);
                        updatestat.bindString(4, Constants.SYS.SYSTEM_MESSAGE);
                        isstat.bindString(2, Constants.SYS.SYSTEM_MESSAGE);
                        isstat.bindString(3, Constants.SYS.SYSTEM_MESSAGE);
                        isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_HEADLINE));
                    }
                    if ("subscription".equals(message.getType())) {
                        isstat.bindString(6, String.valueOf(ConversitionType.MSG_TYPE_SUBSCRIPT));
                    }

                    if (!isUpdown) {//不是上翻历史(因为上翻历史更新session的话 会导致最后一条消息&时间展示不对)
                        int count = updatestat.executeUpdateDelete();
                        if (count <= 0) {//不存在再插入
                            isstat.executeInsert();
                        }
                    } else {//暂时未处理 可能导致 不在会话列表的会话上翻历史无法在会话列表展示

                    }
                    long count = imstat.executeInsert();
                    if (count <= 0 && msgType.equals(String.valueOf(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE))) {//撤销消息 update body
                        revokeStat.bindString(1, body.getContent());
                        revokeStat.bindString(2, String.valueOf(IMMessage.DIRECTION_MIDDLE));
                        revokeStat.bindString(3, String.valueOf(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE));
                        revokeStat.bindString(4, msgId);
                        revokeStat.executeUpdateDelete();
                    }
                } catch (Exception e) {
                    success = false;
                    continue;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "bulkInsertMessage crashed.");
            throw e;
        } finally {
            db.endTransaction();
        }
        return success;
    }

    public void clearIMMessage() {
        String sql = "delete from IM_Message";
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Nick> SelectGroupListBySearchText(String searchText, int limit) {
        deleteJournal();
        String sql = "select * from IM_Group where Name like ? limit ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{"%" + searchText + "%", limit + ""});
        List<Nick> list = new ArrayList<>();
        int id = 0;
        try {


            while (cursor.moveToNext()) {
                Nick nick = new Nick();
                id++;
                nick.setId(id);
                nick.setGroupId(cursor.getString(0));
                nick.setName(cursor.getString(1));
                nick.setIntroduce(cursor.getString(2));
                nick.setHeaderSrc(cursor.getString(3));
                nick.setTopic(cursor.getString(4));
                list.add(nick);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 所有消息已读
     */
    public void ALLMessageRead() {

    }

    /**
     * 获取推送相关状态 key 为pushState 固定
     * type 为500 固定
     *
     * @param pushName
     * @return
     */
    public boolean getPushStateBy(int pushName) {
        deleteJournal();
        boolean state = false;
        String sql = "select ((value&" + pushName + ")<>0) as a from IM_Cache_Data where key='" + CacheDataType.pushState + "' and type=" + CacheDataType.PushStateType;
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{});

        try {


            while (cursor.moveToNext()) {
                state = cursor.getInt(0) > 0 ? true : false;
            }
        } catch (Exception e) {
            state = false;
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return state;
    }

    /**
     * 设置push开关操作
     *
     * @param pushIndex 设置 第几位开关
     * @param state     设置开关状态
     */
    public void setPushState(int pushIndex, int state) {
        String sql = "update IM_Cache_Data set value = case when 1==" + state + "  then value | " + pushIndex + "  else   ~(~(value)|" + pushIndex + ") end where key='" + CacheDataType.pushState + "' and type=" + CacheDataType.PushStateType;

        SQLiteDatabase db = helper.getWritableDatabase();
        try {

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransactionNonExclusive();
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }


    }

    public void updatePushSettingAllState(int push_flag) {
//        String sql = "update IM_Cache_Data set value = " + push_flag + " where key='" + CacheDataType.pushState + "' and type=" + CacheDataType.PushStateType;
        String sql = "insert or replace into IM_Cache_Data (key,type,value) values(?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            stat.bindString(1, CacheDataType.pushState);
            stat.bindString(2, CacheDataType.PushStateType + "");
            stat.bindString(3, push_flag + "");
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 根据会话Id删除该会话的所有消息
     *
     * @param xmppId
     */
    public void deleteIMmessageByXmppId(String xmppId) {

        String sql = "delete from IM_Message where XmppId = ?";
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL(sql, new Object[]{xmppId});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新指定人头像
     *
     * @param jid
     * @param url
     */
    public void updateUserImage(String jid, String url) {

        String sql = "update IM_User set HeaderSrc = ? where XmppId=?";


        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            stat.bindString(1, url);
            stat.bindString(2, jid);

            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 删除好友
     *
     * @param delete
     */
    public void deleteFriend(Nick delete) {
        String sql = "delete from im_friend_list where xmppid = ?";
        try {
            SQLiteDatabase db = helper.getWritableDatabase();

            db.execSQL(sql, new Object[]{delete.getXmppId()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFriend(Nick add) {
        String sql = "insert or replace into im_friend_list(UserId,XmppId) values(?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            stat.bindString(1, QtalkStringUtils.parseBareJid(add.getXmppId()));
            stat.bindString(2, add.getXmppId());
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 全部已读
     */
    public void updateAllRead() {
        String sql = "update IM_Message set ReadedTag = (ReadedTag|" + MessageStatus.REMOTE_STATUS_CHAT_READED + ");";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql);
    }


    /**
     * 更新用户配置批量,本方法只更新 isdel 不会更新value数据
     *
     * @param userConfigData
     */
    public void updateUserConfigBatch(UserConfigData userConfigData) {
        String sql = "update im_user_config set isdel = ? where pkey =? and subkey=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        int isdel = 0;
        switch (userConfigData.getType()) {
            case CacheDataType.set:
                isdel = 0;
                break;
            case CacheDataType.cancel:
                isdel = 1;
                break;

        }


        try {
            for (int i = 0; i < userConfigData.getBatchProcess().size(); i++) {
                UserConfigData.Info info = userConfigData.getBatchProcess().get(i);
                stat.bindString(1, isdel + "");
                stat.bindString(2, info.getKey());
                stat.bindString(3, info.getSubkey());
                stat.executeUpdateDelete();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 新版本config查询最大version
     */
    public int selectUserConfigVersion() {
        deleteJournal();
        int version = 0;
        String sql = "select value from IM_Cache_Data where type = ? and key = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{CacheDataType.userConfigVersionType + "", CacheDataType.userConfigVersion});

        try {


            while (cursor.moveToNext()) {
                version = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return version;
    }

    /**
     * 新版本config查询最大version
     */
    public long selectUserTripVersion() {
        deleteJournal();
        long version = 0;
        String sql = "select value from IM_Cache_Data where type = ? and key = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{CacheDataType.userTripVersionType + "", CacheDataType.userTripVersion});

        try {


            while (cursor.moveToNext()) {
                version = cursor.getLong(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return version;
    }

    public void insertUserConfigVersion(int version) {
        String sql = "INSERT OR REPLACE INTO IM_Cache_Data (key,type,value) VALUES (?, ?, ?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            stat.bindString(1, CacheDataType.userConfigVersion);
            stat.bindString(2, CacheDataType.userConfigVersionType + "");
            stat.bindString(3, version + "");
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void insertUserTripVersion(long version) {
        String sql = "INSERT OR REPLACE INTO IM_Cache_Data (key,type,value) VALUES (?, ?, ?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            stat.bindString(1, CacheDataType.userTripVersion);
            stat.bindString(2, CacheDataType.userTripVersionType + "");
            stat.bindString(3, version + "");
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 插入一条用户漫游配置数据
     *
     * @param data
     */
    public void insertUserConfigVersion(UserConfigData data) {
        String sql = "INSERT OR REPLACE INTO IM_USER_CONFIG(pkey,subkey,value,isdel) VALUES (?,?,?,?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            stat.bindString(1, data.getKey());
            stat.bindString(2, data.getSubkey());
            stat.bindString(3, data.getValue());
// stat.bindString(3,data.getVersion()+"");
            stat.bindString(4, data.getIsdel() + "");
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 置顶 重新插入,并根据当前字段决定是0 或1
     * 同时创建会话
     */
    public void setConversationTopSession(NewRemoteConfig.DataBean.ClientConfigInfosBean data) {
// TODO: 2017/9/25 继续消息zhiding !!!!!!!!
// String insertSql = "INSERT or REPLACE into IM_Cache_Data (key,type,value) values " +
// "(?," + CacheDataType.TOP + ",(CASE WHEN (select value from " +
// "IM_Cache_Data where key =? and type = " + CacheDataType.TOP + ")" +
// " = 1 THEN 0 ELSE 1 END))";
        String selectSql = "update IM_SessionList set chattype = ? where xmppid||'<>'||realjid = ?";
        String insertTopConv = "insert or IGNORE into IM_SessionList (XmppId,RealJid,UserId,ChatType) values(?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
// SQLiteStatement stat = db.compileStatement(insertSql);
        db.beginTransactionNonExclusive();
        SQLiteStatement stattc = db.compileStatement(insertTopConv);
        SQLiteStatement updateStat = db.compileStatement(selectSql);
        try {
            for (NewRemoteConfig.DataBean.ClientConfigInfosBean.InfosBean i : data.getInfos()) {

                if (i.getIsdel() == CacheDataType.Y) {
                    continue;
                }
// db.rawQuery(selectSql,new String[]{i.getSubkey()})
                UserConfigData.TopInfo topInfo = null;
                try {
                    topInfo = JsonUtils.getGson().fromJson(i.getConfiginfo(), UserConfigData.TopInfo.class);
                } catch (Exception e) {
                    continue;
                }
                if (topInfo == null || TextUtils.isEmpty(topInfo.getChatType())) {
                    continue;
                }
                updateStat.bindString(1, topInfo.getChatType());
                updateStat.bindString(2, i.getSubkey());
                int count = updateStat.executeUpdateDelete();
                if (!(count > 0)) {
                    String[] strs = i.getSubkey().split("<>");
                    stattc.bindString(1, strs[0]);
                    stattc.bindString(2, strs[1]);
                    stattc.bindString(3, strs[0]);
                    stattc.bindString(4, topInfo.getChatType());
                    stattc.executeInsert();
                }
            }
// stat.bindString(1, rc.getId() + "-" + rc.getRealUser());
// stat.bindString(2, rc.getId() + "-" + rc.getRealUser());
// stat.executeInsert();
// stattc.bindString(1, rc.getId());
// stattc.bindString(2, rc.getId());
// stattc.bindString(3, rc.getId());
// String type = "";
//// if (rc.getId().startsWith("collection_rbt")) {
//// type = "8";
//// } else {
// type = rc.getId().contains("conference") ? "1" : "0";
//// }
// stattc.bindString(4, type);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 新版本config查询置顶类型数据
     */
    public List<UserConfigData> selectUserConfigValueInString(UserConfigData data) {
        deleteJournal();
        List<UserConfigData> list = new ArrayList<>();
        String sql = "select * from im_user_config where pkey =?  and isdel=" + CacheDataType.N;
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{data.getKey()});

        try {


            while (cursor.moveToNext()) {
                UserConfigData userConfigData = new UserConfigData();
                userConfigData.setKey(cursor.getString(0));
                userConfigData.setSubkey(cursor.getString(1));
                userConfigData.setValue(cursor.getString(2));
                userConfigData.setIsdel(cursor.getInt(4));
                list.add(userConfigData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }


    /**
     * 新版本config查询置顶类型数据
     */
    public UserConfigData selectUserConfigValueForKey(UserConfigData data) {
        deleteJournal();
        UserConfigData userConfigData = null;
        String sql = "select * from im_user_config where pkey =? and subkey= ? and isdel=" + CacheDataType.N;
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{data.getKey(), data.getSubkey()});

        try {


            while (cursor.moveToNext()) {
                userConfigData = new UserConfigData();
                userConfigData.setKey(cursor.getString(0));
                userConfigData.setSubkey(cursor.getString(1));
                userConfigData.setValue(cursor.getString(2));
                userConfigData.setIsdel(cursor.getInt(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userConfigData;
    }


    /**
     * 批量插入用户配置
     *
     * @param newRemoteConfigs
     */
    public void bulkUserConfig(NewRemoteConfig newRemoteConfigs) {
        List<NewRemoteConfig.DataBean.ClientConfigInfosBean> list = newRemoteConfigs.getData().getClientConfigInfos();
        for (int i = 0; i < list.size(); i++) {
            UserConfigData data = new UserConfigData();
            NewRemoteConfig.DataBean.ClientConfigInfosBean item = list.get(i);
            data.setKey(item.getKey());
            List<NewRemoteConfig.DataBean.ClientConfigInfosBean.InfosBean> infos = item.getInfos();
            for (int j = 0; j < infos.size(); j++) {
                NewRemoteConfig.DataBean.ClientConfigInfosBean.InfosBean info = infos.get(j);
                data.setSubkey(info.getSubkey());
                data.setValue(info.getConfiginfo());
                data.setIsdel(info.getIsdel());
                IMDatabaseManager.getInstance().insertUserConfigVersion(data);
            }

        }
    }


    /**
     * 新版本config查询置顶类型数据
     */
    public UserConfigData.TopInfo selectSessionChatType(UserConfigData data) {
        deleteJournal();
        UserConfigData.TopInfo topInfo = null;
        String sql = "select chattype from IM_SessionList where xmppid||'<>'||realjid = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{data.getSubkey()});

        try {


            while (cursor.moveToNext()) {
                topInfo = new UserConfigData.TopInfo();
                topInfo.setChatType(cursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (topInfo == null) {
            topInfo = new UserConfigData.TopInfo();
            if (data.getSubkey().contains("conference")) {
                topInfo.setChatType(1 + "");
            } else {
                topInfo.setChatType(0 + "");
            }
        }
        return topInfo;
    }

    /**
     * 清空用户配置表
     */
    public void deleteUserConfig() {

        String delete = "Delete from IM_USER_CONFIG";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            SQLiteStatement dmstat = db.compileStatement(delete);
            dmstat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<CalendarTrip.DataBean.TripsBean> SelectTripByYearMonth(String date) {
        deleteJournal();
        List<CalendarTrip.DataBean.TripsBean> tripsBeanList = new ArrayList<>();
        String sql = "select * from IM_TRIP_INFO where (tripDate Between ? and  ?) and canceled = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{date + "-01", date + "-31", "false"});

        try {

//            List<CalendarTrip.DataBean.TripsBean>
            while (cursor.moveToNext()) {
                CalendarTrip.DataBean.TripsBean bean = new CalendarTrip.DataBean.TripsBean();
                bean.setTripId(cursor.getString(0));
                bean.setTripName(cursor.getString(1));
                bean.setTripDate(cursor.getString(2));
                bean.setTripType(cursor.getString(3));
                bean.setTripIntr(cursor.getString(4));
                bean.setTripInviter(cursor.getString(5));
                bean.setBeginTime(cursor.getString(6));
                bean.setEndTime(cursor.getString(7));
                bean.setScheduleTime(cursor.getString(8));
                bean.setAppointment(cursor.getString(9));
                bean.setTripLocale(cursor.getString(10));
                bean.setTripLocaleNumber(cursor.getString(11));
                bean.setTripRoom(cursor.getString(12));
                bean.setTripRoomNumber(cursor.getString(13));
                bean.setMemberList((List<CalendarTrip.DataBean.TripsBean.MemberListBean>) JsonUtils.getGson().fromJson(cursor.getString(14), new TypeToken<List<CalendarTrip.DataBean.TripsBean.MemberListBean>>() {
                }.getType()));
                bean.setTripRemark(cursor.getString(15));
                tripsBeanList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tripsBeanList;
    }


    public List<CityLocal.DataBean> getCityList() {

        deleteJournal();
        List<CityLocal.DataBean> cityList = new ArrayList<>();
        String sql = "select * from IM_TRIP_CITY ";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{});

        try {

//            List<CalendarTrip.DataBean.TripsBean>
            while (cursor.moveToNext()) {
                CityLocal.DataBean bean = new CityLocal.DataBean();
                bean.setId(Integer.parseInt(cursor.getString(0)));
                bean.setCityName(cursor.getString(1));

                cityList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return cityList;

    }

    public List<AreaLocal.DataBean.ListBean> getAreaList() {
        deleteJournal();
        List<AreaLocal.DataBean.ListBean> areaList = new ArrayList<>();
        String sql = "select * from IM_TRIP_AREA where Enable =1";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{});

        try {

//            List<CalendarTrip.DataBean.TripsBean>
            while (cursor.moveToNext()) {
                AreaLocal.DataBean.ListBean bean = new AreaLocal.DataBean.ListBean();
                bean.setAreaID(Integer.parseInt(cursor.getString(0)));
                bean.setEnable(Integer.parseInt(cursor.getString(1)));
                bean.setAreaName(cursor.getString(2));
                bean.setMorningStarts(cursor.getString(3));
                bean.setEveningEnds(cursor.getString(4));
                bean.setDescription(cursor.getString(5));
                areaList.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return areaList;
    }


    public void DeleteCity() {
        String delete = "Delete from IM_TRIP_CITY";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            SQLiteStatement dmstat = db.compileStatement(delete);
            dmstat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void InsertCity(CityLocal areaLocal) {

        DeleteCity();

        String sql = "INSERT OR REPLACE INTO IM_TRIP_CITY" +
                "(id,cityName) VALUES (?,?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);

        try {
            for (int i = 0; i < areaLocal.getData().size(); i++) {
                CityLocal.DataBean data = areaLocal.getData().get(i);
                stat.bindString(1, String.valueOf(data.getId()));
                stat.bindString(2, data.getCityName());
                stat.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void InsertArea(AreaLocal areaLocal) {


        String sql = "INSERT OR REPLACE INTO IM_TRIP_AREA" +
                "(areaId,areaName,Enable,MorningStarts,EveningEnds," +
                "Description) VALUES (?,?,?,?,?,?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);

        try {
            for (int i = 0; i < areaLocal.getData().getList().size(); i++) {
                AreaLocal.DataBean.ListBean data = areaLocal.getData().getList().get(i);
                stat.bindString(1, String.valueOf(data.getAreaID()));
                stat.bindString(2, data.getAreaName());
                stat.bindString(3, String.valueOf(data.getEnable()));
                stat.bindString(4, data.getMorningStarts());
                stat.bindString(5, data.getEveningEnds());
                stat.bindString(6, data.getDescription());
                stat.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 插入会议行程
     *
     * @param calendarTrip
     */
    public void InsertTrip(CalendarTrip calendarTrip) {

        String sql = "INSERT OR REPLACE INTO IM_TRIP_INFO" +
                "(tripId,tripName,tripDate,tripType,tripIntr," +
                "tripInviter,beginTime,endTime,scheduleTime," +
                "appointment,tripLocale,tripLocaleNumber,tripRoom," +
                "tripRoomNumber,memberList,tripRemark,canceled) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);

        try {
            for (int i = 0; i < calendarTrip.getData().getTrips().size(); i++) {
                CalendarTrip.DataBean.TripsBean data = calendarTrip.getData().getTrips().get(i);
                stat.bindString(1, data.getTripId());
                stat.bindString(2, data.getTripName());
                stat.bindString(3, data.getTripDate());
                stat.bindString(4, data.getTripType());
                stat.bindString(5, data.getTripIntr());
                stat.bindString(6, data.getTripInviter());
                stat.bindString(7, data.getBeginTime());
                stat.bindString(8, data.getEndTime());
                stat.bindString(9, data.getScheduleTime());
                stat.bindString(10, TextUtils.isEmpty(data.getAppointment()) ? data.getTripLocale() + "-" + data.getTripRoom() : data.getAppointment());
                stat.bindString(11, data.getTripLocale());
                stat.bindString(12, data.getTripLocaleNumber());
                stat.bindString(13, data.getTripRoom());
                stat.bindString(14, data.getTripRoomNumber());
                stat.bindString(15, JsonUtils.getGson().toJson(data.getMemberList()));
                stat.bindString(16, data.getTripRemark() == null ? "" : data.getTripRemark());
                stat.bindString(17, String.valueOf(data.getCanceled()));
                stat.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

//    String sql = "insert or replace into IM_SessionList (XmppId,RealJid,UserId,LastMessageId," +
//            "LastUpdateTime,ChatType,ExtendedFlag) values(?,?,?,?,?,?,?);";
//    SQLiteDatabase db = helper.getWritableDatabase();
//
//        db.beginTransactionNonExclusive();
//
//        try {
//        SQLiteStatement stat = db.compileStatement(sql);
//
//        for (Map.Entry<String, JSONObject> entry : map.entrySet()) {
//            JSONObject jsonObject = entry.getValue();
//            stat.bindString(1, jsonObject.optString("XmppId"));
//            stat.bindString(2, TextUtils.isEmpty(jsonObject.optString("RealJid")) ? jsonObject.optString("XmppId") : jsonObject.optString("RealJid"));
//            stat.bindString(3, jsonObject.optString("XmppId"));
//            stat.bindString(4, jsonObject.optString("MsgId"));
//            stat.bindString(5, jsonObject.optString("MsgDateTime"));
//            String ct = chatType;
//            if ("consult".equals(jsonObject.optString("messageType"))) {
////                    if(){
////
////                    }
////                    ct = jsonObject.optString("qchatid");
//                ct = jsonObject.optString("chatType");
//            } else if ("subscription".equals(jsonObject.optString("messageType"))) {
//                ct = String.valueOf(ConversitionType.MSG_TYPE_SUBSCRIPT);
//            } else if ("headline".equals(jsonObject.optString("messageType"))) {
//                ct = String.valueOf(ConversitionType.MSG_TYPE_HEADLINE);
//            } else if ("collection".equals(jsonObject.optString("messageType"))) {
//                ct = String.valueOf(ConversitionType.MSG_TYPE_COLLECTION);
//            }
//            stat.bindString(6, ct);
//            stat.bindString(7, ef);
//            stat.executeInsert();
//        }


//select
//    public List<RecentConversation> SelectConversationList() {
//        deleteJournal();
//        List<RecentConversation> list = new ArrayList<>();
//        Cursor cursor = null;
//        Cursor c1 = null;
//        String sql = "Select a.XmppId,a.RealJid,a.UserId,a.chatType,b.Content,b.'From',b.Type,b.state,(Select count(1) From IM_Message as c Where a.XmppId = c.XmppId AND c.ReadedTag = 0 AND a.RealJid = c.RealJid) AS Count,CASE b.LastUpdateTime  When NULL THEN a.LastUpdateTime ELSE b.LastUpdateTime END as orderTime,(CASE WHEN (select value from IM_Cache_Data where key =(a.XmppId ||'-'||a.RealJid) and type = 1) = 1 THEN 1 ELSE 0 END) as top,(CASE WHEN (select value from IM_Cache_Data where key =(a.XmppId ||'-'||a.RealJid) and type = 2) = 1 THEN 1 ELSE 0 END) as remind  From IM_SessionList as a Left join IM_Message as b ON  b.MsgId = (SELECT MsgId FROM IM_Message WHERE XmppId = a.XmppId AND RealJid = a.RealJid Order by LastUpdateTime DESC LIMIT 1)Order By top DESC, orderTime DESC;";
//        SQLiteDatabase db = helper.getWritableDatabase();
//        if (!db.isOpen()) {
//            return new ArrayList<>();
//        }
//        cursor = db.rawQuery(sql, null);
//        try {
//            while (cursor.moveToNext()) {
//                RecentConversation rc = new RecentConversation();
//                //获取会话列表id
//                rc.setId(cursor.getString(0));
//                //设置置顶字段
//                rc.setTop(cursor.getInt(10));
//                //设置提醒字段
//                rc.setRemind(cursor.getInt(11));
//                XMPPJID target = XMPPJID.parseJID(rc.getId());
//                int chatType = cursor.getInt(3);
//                JSONObject jsonObject = null;
////            if(chatType==0){
////                jsonObject=  IMLogicManager.getInstance().getUserInfoByUserId(target,IMLogicManager.getInstance().getMyself(),false);
////            }else if(chatType == 1){
////                jsonObject=  IMLogicManager.getInstance().getMucInfoByGroupId(target,IMLogicManager.getInstance().getMyself(),false);
////            }
////
////            Nick nick = new Gson().fromJson(jsonObject.toString(),Nick.class);
////            rc.setNick(nick);
//                //获取真实id
//                rc.setRealUser(cursor.getString(1));
//                //获取列表类型0是单人1是多人4 5都是客服
//                rc.setConversationType(chatType);
//                //获取会话列表最后一条消息
////            String name = cursor.getString(5);
//                int type = cursor.getInt(6);
//                String from = cursor.getString(5);
//                rc.setLastFrom(from);
//                String msg = cursor.getString(4);
//                if (type != ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE
//                        && type != ProtoMessageOuterClass.MessageType.MessageTypeGroupNotify_VALUE
//                        && type != 12) {
//                    switch (type) {
//                        case ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE:
////                            rc.setLastMsg((chatType == 1) ? (from + ":" + msg) : msg);
//                            rc.setLastMsg(msg);
//                            break;
//                        case ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE:
//                            rc.setLastMsg("[文件]");
//                            break;
//                        case ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE:
//                            rc.setLastMsg("[语音]");
//                            break;
//                        case ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE:
//                            rc.setLastMsg("[视频]");
//                            break;
//                        case ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE:
//                            rc.setLastMsg("[图片]");
//                            break;
//                        case ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE:
//                            rc.setLastMsg("[位置]");
//                            break;
//                        case MessageType.MSG_TYPE_RBT_SYSTEM://系统消息
//                            rc.setLastMsg("[系统消息]");
//                            break;
//                        case MessageType.MSG_TYPE_RBT_NOTICE://通知消息
//                            rc.setLastMsg("[通知消息]");
//                            break;
//                        case MessageType.MSG_TYPE_ROB_ORDER://抢单消息
//                        case MessageType.MSG_TYPE_ROB_ORDER_RESPONSE://抢单状态消息
//                            rc.setLastMsg("[抢单消息]");
//                            break;
//                        default:
////                            rc.setLastMsg((chatType == 1) ? (from + ":" + msg) : msg);
//                            rc.setLastMsg(msg);
//                            break;
////                        if (type == ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE) {
////                            rc.setLastMsg((chatType == 1) ? (from + ":" + msg) : msg);
////                        } else if (type == ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE) {
////                            rc.setLastMsg((chatType == 1) ? (from + ":" + "[文件]") : "[文件]");
////                        } else if (type == ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE) {
////                            rc.setLastMsg((chatType == 1) ? (from + ":" + "[语音]") : "[语音]");
////                        } else if (type == ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE) {
////                            rc.setLastMsg((chatType == 1) ? (from + ":" + "[视频]") : "[视频]");
////                        } else if (type == ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE) {
////                            rc.setLastMsg((chatType == 1) ? (from + ":" + "[图片]") : "[图片]");
////                        } else if(type ==ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE){
////                            rc.setLastMsg((chatType == 1) ? (from + ":" + "[位置]") : "[位置]");
////                        }
//                    }
//
//                } else {
//                    rc.setLastMsg(cursor.getString(4));
//                }
//                //设置最后一条消息状态
//                rc.setLastState(cursor.getString(7));
//                rc.setMsgType(type);
//                //获取当前会话未读消息
//                rc.setUnread_msg_cont(cursor.getInt(8));
//                //获取会话列表最后一条消息时间
//                rc.setLastMsgTime(cursor.getLong(9));
//
//                if (chatType == 1) {
//                    //群消息查询是否有未读@消息
//                    String sql1 = "select Content,\"from\" from IM_Message where XmppId = ? and ReadedTag = ? order by LastUpdateTime";//DESC
//                    c1 = db.rawQuery(sql1, new String[]{cursor.getString(0), (0 + "")});
//                    try {
//                        if (c1 != null && c1.getCount() > 0) {
//                            int index = 0;
//                            while (c1.moveToNext()) {
//                                index++;
//                                String content = c1.getString(0);
//                                if ((content.contains("@all") || content.contains("@ALL") || content.contains("@All")) ||
//                                        ((!TextUtils.isEmpty(CurrentPreference.getInstance().getUserName())
//                                                && content.contains("@" + CurrentPreference.getInstance().getUserName())))) {
//                                    rc.setHasAtMsg(c1.getString(1));
//                                    //@消息在未读消息中的位置
//                                    rc.setAtMsgIndex(index);
//                                    break;
//                                }
//                            }
//                        }
//                    } finally {
//                        if (c1 != null) {
//                            c1.close();
//                        }
//                    }
//                }
//                list.add(rc);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            if (c1 != null) {
//                c1.close();
//            }
//        }
//        return list;
//    }

    /******************************************配置config end ****************************************************/
    /******************************************快捷回复 start ****************************************************/
    /**
     * 查询快捷回复组最大version
     */
    public int selectQuickReplyGroupMaxVersion() {
        deleteJournal();
        int version = 0;
        String sql = "select max(version) from IM_QUICK_REPLY_GROUP";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, null);

        try {
            while (cursor.moveToNext()) {
                version = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return version;
    }

    /**
     * 查询快捷回复内容最大version
     */
    public int selectQuickReplyContentMaxVersion() {
        deleteJournal();
        int version = 0;
        String sql = "select max(version) from IM_QUICK_REPLY_CONTENT";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, null);

        try {
            while (cursor.moveToNext()) {
                version = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return version;
    }

    /**
     * 获取快捷回复
     *
     * @return
     */
    public List<QuickReplyData> selectQuickReplies() {
        deleteJournal();
        List<QuickReplyData> list = new ArrayList<>();
        String sql = "select sid, groupname, groupseq from IM_QUICK_REPLY_GROUP order by groupseq";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, null);

        try {
            while (cursor.moveToNext()) {
                QuickReplyData quickReplyData = new QuickReplyData();
                quickReplyData.gid = cursor.getLong(0);
                quickReplyData.groupname = cursor.getString(1);
                quickReplyData.groupseq = cursor.getLong(2);

                String csql = "select sid, gid, content, contentseq from IM_QUICK_REPLY_CONTENT where gid = ? order by contentseq";
                Cursor ccursor = helper.getReadableDatabase().rawQuery(csql, new String[]{quickReplyData.gid + ""});
//                List<QuickReplyData.QuickReplyContent> contents = new ArrayList<>();
                List<String> contents = new ArrayList<>();
                try {
                    while (ccursor.moveToNext()) {
                        String content = ccursor.getString(2);
//                        QuickReplyData.QuickReplyContent content = new QuickReplyData.QuickReplyContent();
//                        content.cid = ccursor.getLong(0);
//                        content.gid = ccursor.getLong(1);
//                        content.content = cursor.getString(2);
//                        content.contentseq = cursor.getLong(3);
                        contents.add(content);
                    }
                } finally {
                    if (ccursor != null) {
                        ccursor.close();
                    }
                }
                quickReplyData.contents = contents;
                list.add(quickReplyData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 清空快捷回复表
     */
    public void deleteQuickReply() {
        String deleteGroup = "Delete from IM_QUICK_REPLY_GROUP";
        String deleteContent = "Delete from IM_QUICK_REPLY_CONTENT";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            SQLiteStatement dmstat = db.compileStatement(deleteGroup);
            SQLiteStatement dmstat1 = db.compileStatement(deleteContent);
            dmstat.executeUpdateDelete();
            dmstat1.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 批量插入快捷回复
     *
     * @param dataBean
     */
    public void batchInsertQuickReply(QuickReplyResult.DataBean dataBean) {
        if (dataBean != null) {
            if (dataBean.groupInfo != null) {
                List<QuickReplyResult.DataBean.GroupInfoBean.GroupsBean> groups = dataBean.groupInfo.groups;
                String sql = "insert or REPLACE into IM_QUICK_REPLY_GROUP(sid, groupname, groupseq, version" +
                        ") values" +
                        "(?, ?, ?, ?);";
                String deleteSql = "delete from IM_QUICK_REPLY_GROUP where sid = ?";
                if (helper != null) {
                    SQLiteDatabase db = helper.getWritableDatabase();

                    db.beginTransactionNonExclusive();
                    SQLiteStatement stat = db.compileStatement(sql);
                    SQLiteStatement deleteStat = db.compileStatement(deleteSql);
                    try {
                        for (int i = 0; i < groups.size(); i++) {
                            QuickReplyResult.DataBean.GroupInfoBean.GroupsBean groupsBean = groups.get(i);
                            if (groupsBean.isdel == 1) {
                                deleteStat.bindString(1, groupsBean.id + "");
                                deleteStat.executeUpdateDelete();
                            } else {
                                stat.bindString(1, groupsBean.id + "");
                                stat.bindString(2, groupsBean.groupname);
                                stat.bindString(3, groupsBean.groupseq + "");
                                stat.bindString(4, groupsBean.version + "");
                                stat.executeInsert();
                            }
                        }
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        Logger.e(e, "insertMultiDailyMain crashed.");
                    } finally {
                        db.endTransaction();
                    }
                }
            }
            if (dataBean.contentInfo != null) {
                List<QuickReplyResult.DataBean.ContentInfoBean.ContentsBean> contentsBeans = dataBean.contentInfo.contents;
                String sql = "insert or REPLACE into IM_QUICK_REPLY_CONTENT(sid, gid, content, contentseq, version" +
                        ") values" +
                        "(?, ?, ?, ?, ?);";
                String deleteSql = "delete from IM_QUICK_REPLY_CONTENT where sid = ?";
                if (helper != null) {
                    SQLiteDatabase db = helper.getWritableDatabase();

                    db.beginTransactionNonExclusive();
                    SQLiteStatement stat = db.compileStatement(sql);
                    SQLiteStatement deleteStat = db.compileStatement(deleteSql);
                    try {
                        for (int i = 0; i < contentsBeans.size(); i++) {
                            QuickReplyResult.DataBean.ContentInfoBean.ContentsBean contentsBean = contentsBeans.get(i);
                            if (contentsBean.isdel == 1) {
                                deleteStat.bindString(1, contentsBean.id + "");
                                deleteStat.executeUpdateDelete();
                            } else {
                                stat.bindString(1, contentsBean.id + "");
                                stat.bindString(2, contentsBean.groupid + "");
                                stat.bindString(3, contentsBean.content);
                                stat.bindString(4, contentsBean.contentseq + "");
                                stat.bindString(5, contentsBean.version + "");
                                stat.executeInsert();
                            }

                        }
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        Logger.e(e, "insertMultiDailyMain crashed.");
                    } finally {
                        db.endTransaction();
                    }
                }
            }

        }

    }

    /******************************************快捷回复 end ****************************************************/
    /******************************************热线列表 start ****************************************************/
    public void InsertHotlines(String hotlinejson) {
        String sql = "insert or replace into IM_Cache_Data(key, type, value" + ") values" +
                "(?, ?, ?);";

        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            stat.bindString(1, CacheDataType.HOTLINE_KEY);
            stat.bindString(2, String.valueOf(CacheDataType.HOTLINE_TYPE));
            stat.bindString(3, hotlinejson);
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public Map<String, String> searchHotlines() {
        String sql = "select value from IM_Cache_Data where type = " + CacheDataType.HOTLINE_TYPE + " and key='" + CacheDataType.HOTLINE_KEY + "'";
        Cursor cursor = null;
        String json = "";
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                json = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (TextUtils.isEmpty(json)) {
                return null;
            }
            final Map<String, String> results = JsonUtils.getGson().fromJson(json, new TypeToken<Map<String, String>>() {
            }.getType());
            return results;
        }
    }
    /******************************************热线列表 end ****************************************************/

    /******************************************本地搜索 start ****************************************************/

    /**
     * 根据关键词搜索某会话消息
     *
     * @param keyword
     * @param xmppid
     * @param realjid
     * @return
     */
    public JSONArray selectMessageByKeyWord(String keyword, String xmppid, String realjid) {
        String sql = "select a.'From',a.Content,a.LastUpdateTime,b.Name,b.HeaderSrc,a.MsgId from IM_Message as a left join IM_User as b on a.'from' = b.Xmppid  where a.Content like ? and a.XmppId = ? and a.RealJid = ? ORDER by a.LastUpdateTime desc limit 1000;";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{"%" + keyword + "%", xmppid, realjid});
        JSONArray jsonArray = new JSONArray();
        try {
            while (cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("time", DateTimeUtils.getTime(cursor.getLong(2), false));
                jsonObject.put("timeLong", String.valueOf(cursor.getLong(2)));
                jsonObject.put("content", cursor.getString(1));
                jsonObject.put("nickName", cursor.getString(3));
                jsonObject.put("headerUrl", cursor.getString(4));
                jsonObject.put("msgId", cursor.getString(5));
                jsonObject.put("from", cursor.getString(0));
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return jsonArray;
    }


    /**
     * 本地搜索 结果后 跳转会话页 显示的消息(chat)
     *
     * @param xmppid
     * @param realjid
     * @param t
     * @return
     */
    public List<IMMessage> selectChatMessageAfterSearch(String xmppid, String realjid, long t) {
        List<IMMessage> imMessageList = new ArrayList<>();
        String sql = "SELECT * FROM IM_MESSAGE where XmppId = ? and realJid = ? and LastUpdateTime >=? ORDER BY LastUpdateTime desc;";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{xmppid, realjid, String.valueOf(t)});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                //设置消息id
                imMessage.setId(cursor.getString(0));
                imMessage.setMessageID(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                imMessage.setMaType(cursor.getString(2));
                String from = cursor.getString(3);
                String to = cursor.getString(4);
                //to
                imMessage.setToID(to);
                //消息正文
                imMessage.setBody(cursor.getString(5));
                //消息类型2
                imMessage.setMsgType(cursor.getInt(6));
                int direction = cursor.getInt(8);
                //from
                imMessage.setFromID(from);
                imMessage.setRealfrom(cursor.getString(13));
//                imMessage.setRealfrom(from);
                //设置方向
                imMessage.setDirection(direction);
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setTime(new Date(time));
                imMessage.setExt(cursor.getString(14));
                imMessage.setReadState(cursor.getInt(10));
                imMessage.setMessageState(cursor.getInt(7));
                imMessageList.add(imMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imMessageList;
    }

    public List<IMMessage> selectGroupMessageAfterSearch(String xmppid, String realjid, long t) {
        List<IMMessage> imMessageList = new ArrayList<>();
        String sql = "SELECT * FROM IM_MESSAGE where XmppId = ? and RealJid = ? and LastUpdateTime >=? ORDER BY LastUpdateTime desc;";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{xmppid, realjid, String.valueOf(t)});
        try {
            while (cursor.moveToNext()) {
                IMMessage imMessage = new IMMessage();
                //设置消息id
                imMessage.setId(cursor.getString(0));
                imMessage.setMessageID(cursor.getString(0));
                //设置会话列表id
                imMessage.setConversationID(cursor.getString(1));
                imMessage.setToID(cursor.getString(1));
                imMessage.setMaType(cursor.getString(2));
                String nickName = cursor.getString(3);
//                imMessage.setNickName(nickName);
                imMessage.setRealfrom(nickName);
                imMessage.setFromID(nickName);
                //因为取得是历史群消息,有部分功能需要知道msg的消息类型
                imMessage.setType(ConversitionType.MSG_TYPE_GROUP);
                //消息正文
                imMessage.setBody(cursor.getString(5));
                imMessage.setMsgType(cursor.getInt(6));
                //设置方向
                imMessage.setDirection(cursor.getInt(8));
                //设置时间
                long time = cursor.getLong(11);
                imMessage.setExt(cursor.getString(14));
                imMessage.setTime(new Date(time));
                imMessage.setReadState(cursor.getInt(10));
                imMessage.setMessageState(cursor.getInt(7));
                imMessageList.add(imMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imMessageList;
    }


    /******************************************本地搜索 end ****************************************************/


    /**********************TEST*******************************************/

    public String queryMessageContent(String msgId) {
        String sql = "select Content from IM_Message where MsgId = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{msgId});
        String raw = "";
        try {
            while (cursor.moveToNext()) {
                raw = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return raw;
        }

    }

    public void resetUnreadCount() {
        String sql = "update IM_SessionList set UnreadCount = 0 where UnreadCount<0;";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            SQLiteStatement sqLiteStatement = db.compileStatement(sql);
            sqLiteStatement.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 获取当前用户配置
     *
     * @return
     */
    public CapabilityResult getCapability() {
        String sql = "select value from IM_Cache_Data where key = ? AND type = ?";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{CacheDataType.userCapabilityValue, CacheDataType.userCapabilityValueType + ""});
        CapabilityResult capabilityResult = null;
        try {
            while (cursor.moveToNext()) {
                String value = cursor.getString(0);
                if (!TextUtils.isEmpty(value)) {
                    capabilityResult = JsonUtils.getGson().fromJson(value, CapabilityResult.class);

                }
            }
        } catch (Exception e) {
            Logger.e("getCapability crashed." + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return capabilityResult;
    }

    /**
     * 用户加号功能配置信息插入
     *
     * @param str
     */
    public void InsertCapability(String str) {

        String sql = "insert or replace into IM_Cache_Data(key, type, value" + ") values" +
                "(?, ?, ?);";

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stat = db.compileStatement(sql);
        try {
            stat.bindString(1, CacheDataType.userCapabilityValue);
            stat.bindString(2, CacheDataType.userCapabilityValueType + "");
            stat.bindString(3, str);
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e, "updateCollectEmojConfig crashed.");
        } finally {
            db.endTransaction();
        }

    }


    public JSONArray searchLocalLinkMessageByXmppId(String xmppid, String realJid) {
        deleteJournal();
//        List<IMMessage> imMessageList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        if (xmppid == null || realJid == null) {//防止绑定crash
            return jsonArray;
        }
        String sql = "select a.'From',a.Content,a.LastUpdateTime,b.Name,b.HeaderSrc,a.MsgId,a.ExtendedInfo " +
                "from IM_Message as a left join IM_User as b on a.'from' = b.Xmppid  " +
                "where a.XmppId = ? and a.RealJid = ? and type =666 or type =777 ORDER by a.LastUpdateTime desc ;";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{realJid, xmppid});

        try {
            while (cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("time", DateTimeUtils.getTime(cursor.getLong(2), false));
                jsonObject.put("timeLong", String.valueOf(cursor.getLong(2)));
                String ext = "";
                if (TextUtils.isEmpty(cursor.getString(6))) {
                    ext = cursor.getString(1);
                } else {
                    ext = cursor.getString(6);
                }
                jsonObject.put("ext", ext);
                jsonObject.put("nickName", cursor.getString(3));
                jsonObject.put("headerUrl", cursor.getString(4));
                jsonObject.put("msgId", cursor.getString(5));
                jsonObject.put("from", cursor.getString(0));
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return jsonArray;
    }

    /**
     * 查询本地文件
     *
     * @param xmppid
     * @param realJid
     * @return
     */
    public JSONArray searchLocalFileMessageByXmppId(String xmppid, String realJid) {
        deleteJournal();
//        List<IMMessage> imMessageList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        if (xmppid == null || realJid == null) {//防止绑定crash
            return jsonArray;
        }
        String sql = "select a.'From',a.Content,a.LastUpdateTime,b.Name,b.HeaderSrc,a.MsgId " +
                "from IM_Message as a left join IM_User as b on a.'from' = b.Xmppid  " +
                "where a.XmppId = ? and a.RealJid = ? and type =5 ORDER by a.LastUpdateTime desc ;";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{realJid, xmppid});

        try {
            while (cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("time", DateTimeUtils.getTime(cursor.getLong(2), false));
                jsonObject.put("timeLong", String.valueOf(cursor.getLong(2)));
                jsonObject.put("ext", cursor.getString(1));
                jsonObject.put("nickName", cursor.getString(3));
                jsonObject.put("headerUrl", cursor.getString(4));
                jsonObject.put("msgId", cursor.getString(5));
                jsonObject.put("from", cursor.getString(0));
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return jsonArray;
    }

    /**
     * 插入奖章信息
     *
     * @param list
     */
    public void bulkInsertUserMedalsWithData(List<MedalsInfo> list) {

        String sql = "insert or Replace into IM_User_Medal(XmppId, Type, URL, URLDesc, LastUpdateTime) values(?, ?, ?, ?, ?);";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            for (int i = 0; i < list.size(); i++) {
                MedalsInfo data = list.get(i);

                stat.bindString(1, data.getUserId() + "@" + data.getHost());
                stat.bindString(2, data.getType());
                stat.bindString(3, data.getUrl());
                stat.bindString(4, data.getDesc());
                stat.bindString(5, data.getUpt());
                stat.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 获取用户奖章信息
     *
     * @param xmppId
     * @return
     */
    public List<MedalsInfo> getUserMedalsWithXmppId(String xmppId) {
        List<MedalsInfo> list = new ArrayList<>();
        String sql = "Select XmppId, Type, URL, URLDesc, LastUpdateTime From IM_User_Medal Where XmppId=? Order By LastUpdateTime Desc";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{xmppId});

        try {
            while (cursor.moveToNext()) {
                MedalsInfo info = new MedalsInfo();
                info.setXmppId(cursor.getString(0));
                info.setType(cursor.getString(1));
                info.setUrl(cursor.getString(2));
                info.setDesc(cursor.getString(3));
                info.setUpt(cursor.getString(4));
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }

    private Object query(String sql, String[] selectionArgs, IQuery iQuery) {
        return query(sql, selectionArgs, iQuery, DBDefalultTimeThreshold);
    }

    /**
     * 统一query 查询 方便记录日志
     *
     * @param sql
     * @param selectionArgs
     * @param iQuery
     * @param threshold     阀值（大于阀值才记录）
     * @return
     */
    private Object query(String sql, String[] selectionArgs, IQuery iQuery, long threshold) {
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            Cursor cursor = helper.getReadableDatabase().rawQuery(sql, selectionArgs);
            if (iQuery != null) {
                result = iQuery.onQuery(cursor);
            }
            long end = System.currentTimeMillis();
            if (!CurrentPreference.getInstance().isBack() && (end - start > threshold)) {
                //日志记录
                long time = end - start;
                LogInfo logInfo = QLog.build(LogConstans.LogType.COD, LogConstans.LogSubType.SQL)
                        .describtion("查询类sql耗时")
                        .costTime(time)
                        .method("sql_query");
                LogInfo.SQL s = new LogInfo.SQL();
                s.content = sql;
                s.time = time;
                if (selectionArgs != null) {
                    s.args = JsonUtils.getGsonEscaping().toJson(selectionArgs);
                }
                logInfo.getSql().add(s);
                LogService.getInstance().saveLog(logInfo);
            }
        } catch (Exception e) {
            Logger.e("query error " + e.getLocalizedMessage());
        } finally {
            return result;
        }
    }

    /*************************** For Update ************************/
    public boolean isTableExit(String tableName) {
        String sql = "select * from sqlite_master where type = ? and name = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{"table", tableName});
        boolean isExit = false;
        try {
            while (cursor.moveToNext()) {
                isExit = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return isExit;
        }
    }

    public boolean isTriggerExit(String triggerName) {
        String sql = "select * from sqlite_master where type = ? and name = ?";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{"trigger", triggerName});
        boolean isExit = false;
        try {
            while (cursor.moveToNext()) {
                isExit = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return isExit;
        }
    }

    public void update_DB_reduction() {
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            //校正未读数不准问题
            String sql = "update IM_SessionList set UnreadCount = 0 where UnreadCount<0;";
            sqLiteDatabase.execSQL(sql);

            //删除insert and update 触发器
//            String sql = "drop trigger if exists im_message_backup_insert;";
//            sqLiteDatabase.execSQL(sql);
//
//            sql = "drop trigger if exists im_message_backup_update;";
//            sqLiteDatabase.execSQL(sql);

//            //校正未读数不准问题
//            sql = "delete from im_Message";
//            sqLiteDatabase.execSQL(sql);

//            删除insert and update 触发器
//            String sql = "drop trigger if exists sessionlist_unread_insert;";
//            sqLiteDatabase.execSQL(sql);

//            sql = "drop trigger if exists sessionlist_unread_update;";
//            sql = "insert into im_message select * from im_message_backup";
//            sqLiteDatabase.execSQL(sql);

//            /**
//             * 更新消息阅读状态时的触发器，更新sessionlist表未读数
//             */
//
//            sql = "create trigger if not exists sessionlist_unread_update" +
//                    " after " +
//                    "update of ReadedTag on IM_Message " +
//                    "for each row " +
//                    "begin " +
//                    "update IM_SessionList set UnreadCount = " +
//                    "case when (new.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + ") =" + MessageStatus.REMOTE_STATUS_CHAT_READED + " and old.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + " <>" + MessageStatus.REMOTE_STATUS_CHAT_READED +
//                    " then (case when UnreadCount >0 then (unreadcount -1) else 0 end ) " +
//                    "when (new.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + ") <>" + MessageStatus.REMOTE_STATUS_CHAT_READED + " and old.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + " =" + MessageStatus.REMOTE_STATUS_CHAT_READED +
//                    " then UnreadCount + 1 " +
//                    "else UnreadCount " +
//                    "end " +
//                    "where XmppId||'-'||realjid = new.XmppId||'-'||new.realjid and new.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + "); " +
//                    "end;";
//            sqLiteDatabase.execSQL(sql);
//
//            sql = "create trigger if not exists sessionlist_unread_insert" +
//                    " after " +
//                    "insert on IM_Message " +
//                    "for each row " +
//                    "begin " +
//                    "update IM_SessionList set UnreadCount = " +
//                    "case when (new.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + " )<>" + MessageStatus.REMOTE_STATUS_CHAT_READED +
//                    " then UnreadCount+1 " +
//                    "else UnreadCount " +
//                    "end " +
//                    "where XmppId||'-'||realjid = new.XmppId||'-'||new.realjid and new.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + "); " +
//                    "end;";
//            sqLiteDatabase.execSQL(sql);

//            sql ="drop table im_message_backup";
//            sqLiteDatabase.execSQL(sql);


            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i("update_DB_version_20 crashed:" + e.getLocalizedMessage());
        } finally {
            sqLiteDatabase.endTransaction();
            DataUtils.getInstance(context).putPreferences("danIsUpdate", false);
        }

    }

    /**
     * IM_MessageState IM_MessageRead表合并到IM_Message 直接提升db version可能导致卡死无响应 所以在此处理
     */
    public void update_DB_version_20() {
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            //删除insert and update 触发器
            String sql = "drop trigger if exists sessionlist_unread_insert;";
            sqLiteDatabase.execSQL(sql);

            sql = "drop trigger if exists sessionlist_unread_update;";
            sqLiteDatabase.execSQL(sql);

            //校正未读数不准问题
            sql = "update IM_SessionList set UnreadCount = 0 where UnreadCount<0;";
            sqLiteDatabase.execSQL(sql);

            sql = "update IM_Message SET State = (select MessageState FROM IM_MessageState where IM_Message.MsgId = IM_MessageState.MsgId), ReadedTag = (select ReadState FROM IM_MessageRead where IM_Message.MsgId = IM_MessageRead.MsgId);";
            sqLiteDatabase.execSQL(sql);

            /**
             * 更新消息阅读状态时的触发器，更新sessionlist表未读数
             */

            sql = "create trigger if not exists sessionlist_unread_update" +
                    " after " +
                    "update of ReadedTag on IM_Message " +
                    "for each row " +
                    "begin " +
                    "update IM_SessionList set UnreadCount = " +
                    "case when (new.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + ") =" + MessageStatus.REMOTE_STATUS_CHAT_READED + " and old.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + " <>" + MessageStatus.REMOTE_STATUS_CHAT_READED +
                    " then (case when UnreadCount >0 then (unreadcount -1) else 0 end ) " +
                    "when (new.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + ") <>" + MessageStatus.REMOTE_STATUS_CHAT_READED + " and old.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + " =" + MessageStatus.REMOTE_STATUS_CHAT_READED +
                    " then UnreadCount + 1 " +
                    "else UnreadCount " +
                    "end " +
                    "where XmppId = new.XmppId and RealJid = new.realjid and new.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + "); " +
                    "end;";
            sqLiteDatabase.execSQL(sql);

            /**
             * 更新消息新插入时的触发器 更新sessionlist表未读数
             */


            sql = "create trigger if not exists sessionlist_unread_insert" +
                    " after " +
                    "insert on IM_Message " +
                    "for each row " +
                    "begin " +
                    "update IM_SessionList set UnreadCount = " +
                    "case when (new.ReadedTag & " + MessageStatus.REMOTE_STATUS_CHAT_READED + " )<>" + MessageStatus.REMOTE_STATUS_CHAT_READED +
                    " then UnreadCount+1 " +
                    "else UnreadCount " +
                    "end " +
                    "where XmppId = new.XmppId and RealJid = new.realjid and new.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + "); " +
                    "end;";
            sqLiteDatabase.execSQL(sql);

            sql = "drop table IM_MessageState;";
            sqLiteDatabase.execSQL(sql);

            sql = "drop table IM_MessageRead;";
            sqLiteDatabase.execSQL(sql);

            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i("update_DB_version_20 crashed:" + e.getLocalizedMessage());
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * 更新点赞状态
     *
     * @param setLikeDataResponse
     */
    public void UpdateWorkWorldLikeState(SetLikeDataResponse setLikeDataResponse) {
        String sql = "";
        if (setLikeDataResponse.getData().getOpType() == 1) {
            sql = "update im_work_world set isLike = " + setLikeDataResponse.getData().getLikeType() + ",likeNum= " + setLikeDataResponse.getData().getLikeNum() + " WHERE uuid = '" + setLikeDataResponse.getData().getPostId() + "'";
        } else {
            sql = "update im_work_world_comment set isLike = " + setLikeDataResponse.getData().getLikeType() + ",likeNum= " + setLikeDataResponse.getData().getLikeNum() + " WHERE commentUUID = '" + setLikeDataResponse.getData().getCommentId() + "'";
        }


//        String sql = "update IM_Work_World  set State = 0 where (State&" + MessageStatus.LOCAL_STATUS_PROCESSION + ")" +
//                "==" + MessageStatus.LOCAL_STATUS_PROCESSION + " and (State&" + MessageStatus.LOCAL_STATUS_SUCCESS + ")<>" + MessageStatus.LOCAL_STATUS_SUCCESS + "";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {

            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransactionNonExclusive();
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }





    /**
     * 删除所有驼圈
     *
     * @param worldDeleteResponse
     */
    public void DeleteWorkWorldDeleteByAll(String owner,String host) {
        String sql = "";
        if(!TextUtils.isEmpty(owner)&&!TextUtils.isEmpty(host)){
            sql = "update im_work_world set isDelete = ?  where owner = "+owner+" and owner_host = "+host;
        }else {
            sql = "update im_work_world set isDelete = ? ";
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();

        try {

                stat.bindString(1,  "1");
                stat.executeUpdateDelete();


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }



    /**
     * 更新点赞状态
     *
     * @param worldDeleteResponse
     */
    public void UpdateWorkWorldDeleteState(List<WorkWorldDeleteResponse.Data> list) {
        String sql = "";
        sql = "update im_work_world set isDelete = ? WHERE id = ?";


        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();

        try {

            for (int i = 0; i < list.size(); i++) {
                stat.bindString(1, list.get(i).getIsDelete() + "");
                stat.bindString(2, list.get(i).getId() + "");
                stat.executeUpdateDelete();
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }


    /**
     * 更新点赞状态
     */
    public void UpdateWorkWorldCommentDeleteState(WorkWorldDeleteResponse.CommentDeleteInfo data) {
        String sql = "";
        sql = "update im_work_world_comment set isDelete = ?,commentStatus = ? WHERE commentUUID = ?";


        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();

        try {

//            for (int i = 0; i < list.size(); i++) {
            stat.bindString(1, data.getIsDelete() + "");
            stat.bindString(2, data.getSuperParentStatus() + "");
            stat.bindString(3, data.getCommentUUID() + "");
            stat.executeUpdateDelete();
//            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }

    /**
     * 插入工作圈数据
     *
     * @param list
     */
    public void InsertWorkWorldByList(List<WorkWorldItem> list) {
        String sql = "INSERT or REPLACE INTO IM_Work_World (id,uuid,owner,owner_host, isAnonymous, anonymousName, anonymousPhoto," +
                " createTime, updateTime, content,atList, likeNum, commentsNum, review_status,isLike,isDelete,postType,attachCommentListString) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            List<WorkWorldOutCommentBean> clist = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                WorkWorldItem item = list.get(i);
                stat.bindString(1, TextUtils.isEmpty(item.getId()) ? "" : item.getId());
                stat.bindString(2, TextUtils.isEmpty(item.getUuid()) ? "" : item.getUuid());
                stat.bindString(3, TextUtils.isEmpty(item.getOwner()) ? "" : item.getOwner());
                stat.bindString(4, TextUtils.isEmpty(item.getOwnerHost()) ? "" : item.getOwnerHost());
                stat.bindString(5, TextUtils.isEmpty(item.getIsAnonymous()) ? "" : item.getIsAnonymous());
                stat.bindString(6, TextUtils.isEmpty(item.getAnonymousName()) ? "" : item.getAnonymousName());
                stat.bindString(7, TextUtils.isEmpty(item.getAnonymousPhoto()) ? "" : item.getAnonymousPhoto());
                stat.bindString(8, TextUtils.isEmpty(item.getCreateTime()) ? "0" : item.getCreateTime());
                stat.bindString(9, TextUtils.isEmpty(item.getUpdateTime()) ? "0" : item.getUpdateTime());
                stat.bindString(10, TextUtils.isEmpty(item.getContent()) ? "" : item.getContent());
                stat.bindString(11, TextUtils.isEmpty(item.getAtList()) ? "" : item.getAtList());
                stat.bindString(12, TextUtils.isEmpty(item.getLikeNum()) ? "" : item.getLikeNum());
                stat.bindString(13, TextUtils.isEmpty(item.getCommentsNum()) ? "" : item.getCommentsNum());
                stat.bindString(14, TextUtils.isEmpty(item.getReviewStatus()) ? "" : item.getReviewStatus());
                stat.bindString(15, TextUtils.isEmpty(item.getIsLike()) ? "" : item.getIsLike());
                stat.bindString(16, TextUtils.isEmpty(item.getIsDelete()) ? "" : item.getIsDelete());
                stat.bindString(17, TextUtils.isEmpty(item.getPostType()) ? "1" : item.getPostType());
                if (item.getAttachCommentList()!=null&&item.getAttachCommentList().size() > 0) {
                    String str = JsonUtils.getGson().toJson(item.getAttachCommentList());
                    stat.bindString(18, str);
                } else {
//                    attachCommentListString
                    stat.bindString(18, "");
                }

//                clist.addAll(item.getAttachCommentList());
//                DeleteWorkWorldCommentStateByPostUuid(item.getUuid());
                stat.executeInsert();
            }

//            InsertWorkWorldOutCommentDataByList(clist);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


    public List<WorkWorldItem> selectHistoryWorkWorldItem(int size, int limit, String searchId) {


        long start = System.currentTimeMillis();
        deleteJournal();
        List<WorkWorldItem> imMessageList = new ArrayList<>();
//
//        String sql = "SELECT * FROM IM_Work_World where review_status = 2 and isDelete = 0 ORDER BY createTime DESC Limit ?,?";
//        SQLiteDatabase db = helper.getWritableDatabase();
//        Cursor cursor = db.rawQuery(sql, new String[]{limit + "", size + ""});
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String sql = "";
        if (TextUtils.isEmpty(searchId)) {
            sql = "SELECT * FROM IM_Work_World where review_status = 2 and isDelete = 0 ORDER BY createTime DESC Limit ?,?";
            db = helper.getWritableDatabase();
            cursor = db.rawQuery(sql, new String[]{limit + "", size + ""});

        } else {
            String userId = QtalkStringUtils.parseId(searchId);
            String userDomain = QtalkStringUtils.parseDomain(searchId);

//            if(CurrentPreference.getInstance().getPreferenceUserId().equals(searchId)){
//                sql = "SELECT * FROM IM_Work_World where review_status = 2  and isDelete = 0 and owner = ? and owner_host = ? ORDER BY createTime DESC Limit ?,?";
//
//
//            }else{
            sql = "SELECT * FROM IM_Work_World where review_status = 2 and isAnonymous = 0 and isDelete = 0 and owner = ? and owner_host = ? ORDER BY createTime DESC Limit ?,?";

//            }

            db = helper.getWritableDatabase();
            cursor = db.rawQuery(sql, new String[]{userId, userDomain, limit + "", size + ""});

        }
        try {
            while (cursor.moveToNext()) {
                WorkWorldItem item = new WorkWorldItem();
                item.setId(cursor.getString(0));
                item.setUuid(cursor.getString(1));
                item.setOwner(cursor.getString(2));
                item.setOwnerHost(cursor.getString(3));
                item.setIsAnonymous(cursor.getString(4));
                item.setAnonymousName(cursor.getString(5));
                item.setAnonymousPhoto(cursor.getString(6));
                item.setCreateTime(cursor.getString(7));
                item.setUpdateTime(cursor.getString(8));
                item.setContent(cursor.getString(9));
                item.setAtList(cursor.getString(10));
                item.setLikeNum(cursor.getString(11));
                item.setCommentsNum(cursor.getString(12));
                item.setReviewStatus(cursor.getString(13));
                item.setIsDelete(cursor.getString(14));
                item.setIsLike(cursor.getString(15));
                item.setPostType(cursor.getString(16));
                item.setAttachCommentListString(cursor.getString(17));

                imMessageList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        long end = System.currentTimeMillis();
        Logger.i("SelectHistoryChatMessage" + (end - start));
        return imMessageList;
    }

    /**
     * 根据id查询帖子
     *
     * @return
     */
    public WorkWorldItem selectWorkWorldItemByUUID(String uuid) {
        long start = System.currentTimeMillis();
        deleteJournal();
        WorkWorldItem item = null;

        String sql = "SELECT * FROM IM_Work_World where uuid = ?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{uuid});
        try {
            while (cursor.moveToNext()) {
                item = new WorkWorldItem();
                item.setId(cursor.getString(0));
                item.setUuid(cursor.getString(1));
                item.setOwner(cursor.getString(2));
                item.setOwnerHost(cursor.getString(3));
                item.setIsAnonymous(cursor.getString(4));
                item.setAnonymousName(cursor.getString(5));
                item.setAnonymousPhoto(cursor.getString(6));
                item.setCreateTime(cursor.getString(7));
                item.setUpdateTime(cursor.getString(8));
                item.setContent(cursor.getString(9));
                item.setAtList(cursor.getString(10));
                item.setLikeNum(cursor.getString(11));
                item.setCommentsNum(cursor.getString(12));
                item.setReviewStatus(cursor.getString(13));
                item.setIsDelete(cursor.getString(14));
                item.setIsLike(cursor.getString(15));
                item.setPostType(cursor.getString(16));
                item.setAttachCommentListString(cursor.getString(17));

            }
        } catch (Exception e) {
            e.printStackTrace();
            item = null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        long end = System.currentTimeMillis();
        Logger.i("SelectHistoryChatMessage" + (end - start));
        return item;
    }


    /**
     * 插入工作圈数据
     *
     * @param list
     */
    public void InsertWorkWorldOutCommentDataByList(List<WorkWorldOutCommentBean> list) {
        String sql = "INSERT or REPLACE INTO IM_Work_World_OUT_Comment (anonymousName,anonymousPhoto,commentUUID,content, createTime, fromHost, fromUser," +
                " id, isAnonymous, isDelete,isLike, likeNum, parentCommentUUID, postUUID,reviewStatus,toHost,toUser,updateTime,toisAnonymous,toAnonymousName,toAnonymousPhoto,superParentUUID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            for (int i = 0; i < list.size(); i++) {
                WorkWorldNewCommentBean item = list.get(i);
                stat.bindString(1, TextUtils.isEmpty(item.getAnonymousName()) ? "" : item.getAnonymousName());
                stat.bindString(2, TextUtils.isEmpty(item.getAnonymousPhoto()) ? "" : item.getAnonymousPhoto());
                stat.bindString(3, TextUtils.isEmpty(item.getCommentUUID()) ? "" : item.getCommentUUID());
                stat.bindString(4, TextUtils.isEmpty(item.getContent()) ? "" : item.getContent());
                stat.bindString(5, TextUtils.isEmpty(item.getCreateTime()) ? "0" : item.getCreateTime());
                stat.bindString(6, TextUtils.isEmpty(item.getFromHost()) ? "" : item.getFromHost());
                stat.bindString(7, TextUtils.isEmpty(item.getFromUser()) ? "" : item.getFromUser());
                stat.bindString(8, TextUtils.isEmpty(item.getId()) ? "" : item.getId());
                stat.bindString(9, TextUtils.isEmpty(item.getIsAnonymous()) ? "" : item.getIsAnonymous());
                stat.bindString(10, TextUtils.isEmpty(item.getIsDelete()) ? "" : item.getIsDelete());
                stat.bindString(11, TextUtils.isEmpty(item.getIsLike()) ? "" : item.getIsLike());
                stat.bindString(12, TextUtils.isEmpty(item.getLikeNum()) ? "" : item.getLikeNum());
                stat.bindString(13, TextUtils.isEmpty(item.getParentCommentUUID()) ? "" : item.getParentCommentUUID());
                stat.bindString(14, TextUtils.isEmpty(item.getPostUUID()) ? "" : item.getPostUUID());
                stat.bindString(15, TextUtils.isEmpty(item.getReviewStatus()) ? "" : item.getReviewStatus());
                stat.bindString(16, TextUtils.isEmpty(item.getToHost()) ? "" : item.getToHost());
                stat.bindString(17, TextUtils.isEmpty(item.getToUser()) ? "" : item.getToUser());
                stat.bindString(18, TextUtils.isEmpty(item.getUpdateTime()) ? "0" : item.getUpdateTime());
                stat.bindString(19, TextUtils.isEmpty(item.getToisAnonymous()) ? "" : item.getToisAnonymous());
                stat.bindString(20, TextUtils.isEmpty(item.getToAnonymousName()) ? "" : item.getToAnonymousName());
                stat.bindString(21, TextUtils.isEmpty(item.getToAnonymousPhoto()) ? "" : item.getToAnonymousPhoto());
                stat.bindString(22, TextUtils.isEmpty(item.getSuperParentUUID()) ? "" : item.getSuperParentUUID());


                stat.executeInsert();
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


    /**
     * 插入工作圈数据
     *
     * @param list
     */
    public void InsertWorkWorldCommentDataByList(List<WorkWorldNewCommentBean> list) {
        String sql = "INSERT or REPLACE INTO IM_Work_World_Comment (anonymousName,anonymousPhoto,commentUUID,content, createTime, fromHost, fromUser," +
                " id, isAnonymous, isDelete,isLike, likeNum, parentCommentUUID, postUUID,reviewStatus,toHost,toUser,updateTime,toisAnonymous,toAnonymousName,toAnonymousPhoto,superParentUUID,newChildString," +
                "commentStatus,atList) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            for (int i = 0; i < list.size(); i++) {
                WorkWorldNewCommentBean item = list.get(i);
                stat.bindString(1, TextUtils.isEmpty(item.getAnonymousName()) ? "" : item.getAnonymousName());
                stat.bindString(2, TextUtils.isEmpty(item.getAnonymousPhoto()) ? "" : item.getAnonymousPhoto());
                stat.bindString(3, TextUtils.isEmpty(item.getCommentUUID()) ? "" : item.getCommentUUID());
                stat.bindString(4, TextUtils.isEmpty(item.getContent()) ? "" : item.getContent());
                stat.bindString(5, TextUtils.isEmpty(item.getCreateTime()) ? "0" : item.getCreateTime());
                stat.bindString(6, TextUtils.isEmpty(item.getFromHost()) ? "" : item.getFromHost());
                stat.bindString(7, TextUtils.isEmpty(item.getFromUser()) ? "" : item.getFromUser());
                stat.bindString(8, TextUtils.isEmpty(item.getId()) ? "" : item.getId());
                stat.bindString(9, TextUtils.isEmpty(item.getIsAnonymous()) ? "" : item.getIsAnonymous());
                stat.bindString(10, TextUtils.isEmpty(item.getIsDelete()) ? "" : item.getIsDelete());
                stat.bindString(11, TextUtils.isEmpty(item.getIsLike()) ? "" : item.getIsLike());
                stat.bindString(12, TextUtils.isEmpty(item.getLikeNum()) ? "" : item.getLikeNum());
                stat.bindString(13, TextUtils.isEmpty(item.getParentCommentUUID()) ? "" : item.getParentCommentUUID());
                stat.bindString(14, TextUtils.isEmpty(item.getPostUUID()) ? "" : item.getPostUUID());
                stat.bindString(15, TextUtils.isEmpty(item.getReviewStatus()) ? "" : item.getReviewStatus());
                stat.bindString(16, TextUtils.isEmpty(item.getToHost()) ? "" : item.getToHost());
                stat.bindString(17, TextUtils.isEmpty(item.getToUser()) ? "" : item.getToUser());
                stat.bindString(18, TextUtils.isEmpty(item.getUpdateTime()) ? "0" : item.getUpdateTime());
                stat.bindString(19, TextUtils.isEmpty(item.getToisAnonymous()) ? "" : item.getToisAnonymous());
                stat.bindString(20, TextUtils.isEmpty(item.getToAnonymousName()) ? "" : item.getToAnonymousName());
                stat.bindString(21, TextUtils.isEmpty(item.getToAnonymousPhoto()) ? "" : item.getToAnonymousPhoto());
                stat.bindString(22, TextUtils.isEmpty(item.getSuperParentUUID()) ? "" : item.getSuperParentUUID());
//                stat.bindString(23, TextUtils.isEmpty(item.getAtList()) ? "" : item.getAtList());



                if (item.getNewChild() != null && item.getNewChild().size() > 0) {
                    String str = JsonUtils.getGson().toJson(item.getNewChild());
                    stat.bindString(23, str);
                } else {
                    stat.bindString(23, "");
                }
                stat.bindString(24, TextUtils.isEmpty(item.getCommentStatus()) ? "" : item.getCommentStatus());
                stat.bindString(25, TextUtils.isEmpty(item.getAtList()) ? "" : item.getAtList());

                stat.executeInsert();
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


    /**
     * 更新点赞状态
     */
    public void UpdateWorkWorldCommentState(List<WorkWorldDetailsCommenData.DataBean.DeleteCommentsBean> list) {
        String sql = "";
        sql = "update IM_Work_World_Comment set isDelete = ? WHERE id = ?";


        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();

        try {

            for (int i = 0; i < list.size(); i++) {
                stat.bindString(1, list.get(i).getIsDelete() + "");
                stat.bindString(2, list.get(i).getId() + "");
                stat.executeUpdateDelete();
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }


    public List<WorkWorldNewCommentBean> selectHistoryWorkWorldCommentItem(int size, int limit) {
        long start = System.currentTimeMillis();
        deleteJournal();
        List<WorkWorldNewCommentBean> imMessageList = new ArrayList<>();

        String sql = "SELECT * FROM IM_Work_World_Comment where reviewStatus = 2 and isDelete = 0 ORDER BY createTime DESC Limit ?,?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{limit + "", size + ""});
        try {
            while (cursor.moveToNext()) {
                WorkWorldNewCommentBean item = new WorkWorldNewCommentBean();
//
                item.setAnonymousName(cursor.getString(0));
                item.setAnonymousPhoto(cursor.getString(1));
                item.setCommentUUID(cursor.getString(2));
                item.setContent(cursor.getString(3));
                item.setCreateTime(cursor.getString(4));
                item.setFromHost(cursor.getString(5));
                item.setFromUser(cursor.getString(6));
                item.setId(cursor.getString(7));
                item.setIsAnonymous(cursor.getString(8));
                item.setIsDelete(cursor.getString(9));
                item.setIsLike(cursor.getString(10));
                item.setLikeNum(cursor.getString(11));
                item.setParentCommentUUID(cursor.getString(12));
                item.setPostUUID(cursor.getString(13));
                item.setReviewStatus(cursor.getString(14));
                item.setToHost(cursor.getString(15));
                item.setToUser(cursor.getString(16));
                item.setUpdateTime(cursor.getString(17));
                item.setToisAnonymous(cursor.getString(18));
                item.setToAnonymousName(cursor.getString(19));
                item.setToAnonymousPhoto(cursor.getString(20));
                item.setSuperParentUUID(cursor.getString(21));
                item.setNewChildString(cursor.getString(22));
                item.setCommentStatus(cursor.getString(23));
                item.setAtList(cursor.getString(24));


                imMessageList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        long end = System.currentTimeMillis();
        Logger.i("SelectHistoryChatMessage" + (end - start));
        return imMessageList;
    }


    public List<WorkWorldNewCommentBean> selectHistoryWorkWorldNewCommentBean(int size, int limit, String uuid) {
        long start = System.currentTimeMillis();
        deleteJournal();
        List<WorkWorldNewCommentBean> imMessageList = new ArrayList<>();

        String sql = "SELECT * FROM IM_Work_World_Comment where reviewStatus = ? and isDelete = ? and postUUID = ? ORDER BY createTime DESC Limit ?,?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{0 + "", 0 + "", uuid, limit + "", size + ""});
        try {
            while (cursor.moveToNext()) {
                WorkWorldNewCommentBean item = new WorkWorldNewCommentBean();
//
                item.setAnonymousName(cursor.getString(0));
                item.setAnonymousPhoto(cursor.getString(1));
                item.setCommentUUID(cursor.getString(2));
                item.setContent(cursor.getString(3));
                item.setCreateTime(cursor.getString(4));
                item.setFromHost(cursor.getString(5));
                item.setFromUser(cursor.getString(6));
                item.setId(cursor.getString(7));
                item.setIsAnonymous(cursor.getString(8));
                item.setIsDelete(cursor.getString(9));
                item.setIsLike(cursor.getString(10));
                item.setLikeNum(cursor.getString(11));
                item.setParentCommentUUID(cursor.getString(12));
                item.setPostUUID(cursor.getString(13));
                item.setReviewStatus(cursor.getString(14));
                item.setToHost(cursor.getString(15));
                item.setToUser(cursor.getString(16));
                item.setUpdateTime(cursor.getString(17));
                item.setToisAnonymous(cursor.getString(18));
                item.setToAnonymousName(cursor.getString(19));
                item.setToAnonymousPhoto(cursor.getString(20));
                item.setSuperParentUUID(cursor.getString(21));
                item.setNewChildString(cursor.getString(22));
                item.setCommentStatus(cursor.getString(23));
                item.setAtList(cursor.getString(24));


                imMessageList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        long end = System.currentTimeMillis();
        Logger.i("SelectHistoryChatMessage" + (end - start));
        return imMessageList;
    }





    /**
     * 更新通知数据
     *
     * @param
     */
    public void DeleteWorkWorldNoticeByEventType(String eventType) {
        String sql = "";
        sql = "delete from IM_Work_World_Notice  WHERE eventType = ?";


        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();

        try {

                stat.bindString(1, eventType + "");
                stat.executeUpdateDelete();


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }


    /**
     * 更新通知数据
     *
     * @param
     */
    public void UpdateWorkWorldNoticeDeleteState(List<WorkWorldDetailsCommenData.DataBean.DeleteCommentsBean> list) {
        String sql = "";
        sql = "delete from IM_Work_World_Notice  WHERE uuid = ?";


        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();

        try {

            for (int i = 0; i < list.size(); i++) {
                stat.bindString(1, list.get(i).getUuid() + "");
                stat.executeUpdateDelete();
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }


    /**
     * 插入通知数据
     *
     * @param list
     * @param isRead
     */
    public void InsertWorkWorldNoticeByList(List<? extends WorkWorldNoticeItem> list, boolean isRead) {
//        String sql = "INSERT or REPLACE INTO IM_Work_World_Notice (eventType,userFrom,userFromHost,userTo,userToHost,fromIsAnyonous," +
//                "fromAnyonousName,fromAnyonousPhoto,toIsAnyonous,toAnyonousName,toAnyonousPhoto,content,postUUID,uuid,createTime," +
//                "readState,owner,owner_host,isAnonymous,anonymousName,anonymousPhoto) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql = "INSERT or IGNORE INTO IM_Work_World_Notice (eventType,userFrom,userFromHost,userTo,userToHost,fromIsAnyonous," +
                "fromAnyonousName,fromAnyonousPhoto,toIsAnyonous,toAnyonousName,toAnyonousPhoto,content,postUUID,uuid,createTime," +
                "readState) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            for (int i = 0; i < list.size(); i++) {
                WorkWorldNoticeItem item = list.get(i);
                stat.bindString(1, TextUtils.isEmpty(item.getEventType()) ? "0" : item.getEventType());
                stat.bindString(2, TextUtils.isEmpty(item.getUserFrom()) ? "" : item.getUserFrom());
                stat.bindString(3, TextUtils.isEmpty(item.getUserFromHost()) ? "" : item.getUserFromHost());
                stat.bindString(4, TextUtils.isEmpty(item.getUserTo()) ? "" : item.getUserTo());
                stat.bindString(5, TextUtils.isEmpty(item.getUserToHost()) ? "" : item.getUserToHost());
                stat.bindString(6, TextUtils.isEmpty(item.getFromIsAnonymous()) ? "" : item.getFromIsAnonymous());
                stat.bindString(7, TextUtils.isEmpty(item.getFromAnonymousName()) ? "" : item.getFromAnonymousName());
                stat.bindString(8, TextUtils.isEmpty(item.getFromAnonymousPhoto()) ? "" : item.getFromAnonymousPhoto());
                stat.bindString(9, TextUtils.isEmpty(item.getToIsAnonymous()) ? "" : item.getToIsAnonymous());
                stat.bindString(10, TextUtils.isEmpty(item.getToAnonymousName()) ? "" : item.getToAnonymousName());
                stat.bindString(11, TextUtils.isEmpty(item.getToAnonymousPhoto()) ? "" : item.getToAnonymousPhoto());
                stat.bindString(12, TextUtils.isEmpty(item.getContent()) ? "" : item.getContent());
                stat.bindString(13, TextUtils.isEmpty(item.getPostUUID()) ? "" : item.getPostUUID());
                stat.bindString(14, TextUtils.isEmpty(item.getUuid()) ? "" : item.getUuid());
                stat.bindString(15, TextUtils.isEmpty(item.getCreateTime()) ? "0" : item.getCreateTime());
                if(isRead){
                    stat.bindString(16, TextUtils.isEmpty(item.getReadState()) ? "1" : item.getReadState());
                }else {
                    stat.bindString(16, TextUtils.isEmpty(item.getReadState()) ? "0" : item.getReadState());
                }
//                stat.bindString(17, TextUtils.isEmpty(item.getOwner()) ? "" : item.getOwner());
//                stat.bindString(18, TextUtils.isEmpty(item.getOwnerHost()) ? "" : item.getOwnerHost());
//                stat.bindString(19, TextUtils.isEmpty(item.getIsAnyonous()) ? "" : item.getIsAnyonous());
//                stat.bindString(20, TextUtils.isEmpty(item.getAnyonousName()) ? "" : item.getAnyonousName());
//                stat.bindString(21, TextUtils.isEmpty(item.getAnyonousPhoto()) ? "" : item.getAnyonousPhoto());

                stat.executeInsert();
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


    public int selectWorkWorldNotice() {


        deleteJournal();
        int count = 0;
        String sql = "select count(1) from im_work_world_notice where readstate = 0 " +
                "and (eventType="+Constants.WorkWorldState.NOTICE+" " +
                "or eventType="+Constants.WorkWorldState.WORKWORLDATMESSAGE+" " +
                "or eventType="+Constants.WorkWorldState.COMMENTATMESSAGE+")";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;


    }


    public List<WorkWorldNoticeItem> selectHistoryWorkWorldNotice(int size, int limit) {
        long start = System.currentTimeMillis();
        deleteJournal();
        List<WorkWorldNoticeItem> imMessageList = new ArrayList<>();

        String sql = "SELECT * FROM im_work_world_notice where readstate = ? " +
                "and (eventType="+Constants.WorkWorldState.NOTICE+" " +
                "or eventType="+Constants.WorkWorldState.WORKWORLDATMESSAGE+" " +
                "or eventType="+Constants.WorkWorldState.COMMENTATMESSAGE+")" +
                " ORDER BY createTime DESC Limit ?,?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{0 + "", limit + "", size + ""});
        try {
            while (cursor.moveToNext()) {
                WorkWorldNoticeItem item = new WorkWorldNoticeItem();
//
//                sql ="CREATE TABLE IM_Work_World_Notice (" +
//                        "eventType INTEGER," +
//                        "userFrom TEXT," +
//                        "userFromHost TEXT," +
//                        "userTo TEXT," +
//                        "userToHost TEXT," +
//                        "fromIsAnyonous INTEGER," +
//                        "fromAnyonousName TEXT," +
//                        "fromAnyonousPhoto TEXT," +
//                        "toIsAnyonous INTEGER," +
//                        "toAnyonousName TEXT," +
//                        "toAnyonousPhoto TEXT," +
//                        "content TEXT," +
//                        "postUUID TEXT," +
//                        "uuid TEXT PRIMARY KEY," +
//                        "createTime INTEGER," +
//                        "readState INTEGER DEFAULT 0" +
//                        ")";
                item.setEventType(cursor.getString(0));
                item.setUserFrom(cursor.getString(1));
                item.setUserFromHost(cursor.getString(2));
                item.setUserTo(cursor.getString(3));
                item.setUserToHost(cursor.getString(4));
                item.setFromIsAnonymous(cursor.getString(5));
                item.setFromAnonymousName(cursor.getString(6));
                item.setFromAnonymousPhoto(cursor.getString(7));
                item.setToIsAnonymous(cursor.getString(8));
                item.setToAnonymousName(cursor.getString(9));
                item.setToAnonymousPhoto(cursor.getString(10));
                item.setContent(cursor.getString(11));
                item.setPostUUID(cursor.getString(12));
                item.setUuid(cursor.getString(13));
                item.setCreateTime(cursor.getString(14));
                item.setReadState(cursor.getString(15));
//                item.setOwner(cursor.getString(16));
//                item.setOwnerHost(cursor.getString(17));
//                item.setIsAnyonous(cursor.getString(18));
//                item.setAnyonousName(cursor.getString(19));
//                item.setAnyonousPhoto(cursor.getString(20));


                imMessageList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        long end = System.currentTimeMillis();
        Logger.i("SelectHistoryChatMessage" + (end - start));
        return imMessageList;
    }


    public List<? extends WorkWorldNoticeItem> selectHistoryWorkWorldNoticeByEventType(int size, int limit, List<String> typeList, boolean isAtShow) {
        long start = System.currentTimeMillis();
        deleteJournal();
        List<WorkWorldNoticeItem> imMessageList = new ArrayList<>();
        String sql = "SELECT * FROM im_work_world_notice where (";
        for (int i = 0; i < typeList.size(); i++) {
            sql+="eventType = "+typeList.get(i)+ "  or ";
        }
       sql =  sql.substring(0,(sql.lastIndexOf("or")));
        sql+=" ) ORDER BY createTime DESC Limit ?,?";
//        String sql = "SELECT * FROM im_work_world_notice where eventType = ? " +
//                " ORDER BY createTime DESC Limit ?,?";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{ limit + "", size + ""});
        try {
            while (cursor.moveToNext()) {
                WorkWorldNoticeItem item;
                if(isAtShow){
                    item = new WorkWorldAtShowItem();
                }else {
                 item=new WorkWorldNoticeItem();
                }
//
//                sql ="CREATE TABLE IM_Work_World_Notice (" +
//                        "eventType INTEGER," +
//                        "userFrom TEXT," +
//                        "userFromHost TEXT," +
//                        "userTo TEXT," +
//                        "userToHost TEXT," +
//                        "fromIsAnyonous INTEGER," +
//                        "fromAnyonousName TEXT," +
//                        "fromAnyonousPhoto TEXT," +
//                        "toIsAnyonous INTEGER," +
//                        "toAnyonousName TEXT," +
//                        "toAnyonousPhoto TEXT," +
//                        "content TEXT," +
//                        "postUUID TEXT," +
//                        "uuid TEXT PRIMARY KEY," +
//                        "createTime INTEGER," +
//                        "readState INTEGER DEFAULT 0" +
//                        ")";
                item.setEventType(cursor.getString(0));
                item.setUserFrom(cursor.getString(1));
                item.setUserFromHost(cursor.getString(2));
                item.setUserTo(cursor.getString(3));
                item.setUserToHost(cursor.getString(4));
                item.setFromIsAnonymous(cursor.getString(5));
                item.setFromAnonymousName(cursor.getString(6));
                item.setFromAnonymousPhoto(cursor.getString(7));
                item.setToIsAnonymous(cursor.getString(8));
                item.setToAnonymousName(cursor.getString(9));
                item.setToAnonymousPhoto(cursor.getString(10));
                item.setContent(cursor.getString(11));
                item.setPostUUID(cursor.getString(12));
                item.setUuid(cursor.getString(13));
                item.setCreateTime(cursor.getString(14));
                item.setReadState(cursor.getString(15));
//                item.setOwner(cursor.getString(16));
//                item.setOwnerHost(cursor.getString(17));
//                item.setIsAnyonous(cursor.getString(18));
//                item.setAnyonousName(cursor.getString(19));
//                item.setAnyonousPhoto(cursor.getString(20));


                imMessageList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        long end = System.currentTimeMillis();
        Logger.i("SelectHistoryChatMessage" + (end - start));
        return imMessageList;
    }






    /**
     * 获取最后一条消息时间,
     * 这条消息一定是正常状态,即为 1
     * 防止出现时间戳时间错乱,历史消息拿的有问题
     *
     * @return
     */
    public WorkWorldNoticeTimeData getLastestWorkWorldTime() {
        deleteJournal();
        String sql = "select createTime,uuid from im_work_world_notice " +
                "where (eventType = "+Constants.WorkWorldState.NOTICE+" " +
                "or eventType = "+Constants.WorkWorldState.WORKWORLDATMESSAGE+" " +
                "or eventType = "+Constants.WorkWorldState.COMMENTATMESSAGE+")" +
                " order by createTime desc limit 1";
        Object result = query(sql, null, new IQuery() {
            @Override
            public Object onQuery(Cursor cursor) {
                WorkWorldNoticeTimeData workWorldNoticeTimeDate = new WorkWorldNoticeTimeData();
                try {

                    while (cursor.moveToNext()) {
                        workWorldNoticeTimeDate.setCreateTime(cursor.getString(0));
                        workWorldNoticeTimeDate.setUuid(cursor.getString(1));
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    workWorldNoticeTimeDate.setCreateTime("0");
                    workWorldNoticeTimeDate.setUuid("");
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                return workWorldNoticeTimeDate;
            }
        });
        if (result == null) {
            return new WorkWorldNoticeTimeData();
        } else {
            return (WorkWorldNoticeTimeData) result;
        }

    }


    /**
     * 更新通知状态
     *
     * @param
     */
    public void UpdateWorkWorldNoticeReadTime(String time) {
        String sql = "";
        sql = "update IM_Work_World_notice set readstate = ? WHERE createTime<= ?";


        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();

        try {

            stat.bindString(1, 1 + "");
            stat.bindString(2, time);
            stat.executeUpdateDelete();


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }


    public void InsertWorkWorldRemind(boolean isopen){
        String sql = "insert or replace into IM_Cache_Data (key,type,value) values(?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            stat.bindString(1, CacheDataType.workWorldRemindValue);
            stat.bindString(2, CacheDataType.workWorldRemindType + "");
            stat.bindString(3, isopen + "");
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    public void InsertWorkWorldPremissions(Boolean aBoolean) {

        String sql = "insert or replace into IM_Cache_Data (key,type,value) values(?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        try {
            stat.bindString(1, CacheDataType.workWorldPermissionsValue);
            stat.bindString(2, CacheDataType.workWorldPermissionsType + "");
            stat.bindString(3, aBoolean + "");
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }


    }


    /**
     * 查询是否有未读数据
     *
     * @return
     */
    public boolean SelectWorkWorldUnRead() {

        long start = System.currentTimeMillis();
        deleteJournal();
        List<WorkWorldNoticeItem> imMessageList = new ArrayList<>();
        boolean haveUnRead = false;
        String sql = "SELECT * FROM IM_Work_World where review_status = 2 and isDelete = 0 Limit 1";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        try {
            if (cursor.moveToNext()) {
                haveUnRead = true;
            } else {
                haveUnRead = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            haveUnRead = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        long end = System.currentTimeMillis();
        Logger.i("SelectWorkWorldUnRead" + (end - start));
        return haveUnRead;
    }



    public boolean SelectWorkWorldRemind() {
        deleteJournal();
        String sql = "select value from im_cache_data  where key = ? and type = ?";

        boolean is = true;
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{CacheDataType.workWorldRemindValue, "" + CacheDataType.workWorldRemindType});
        try {


            while (cursor.moveToNext()) {
                is = Boolean.parseBoolean(cursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            is = true;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return is;
    }



    public boolean SelectWorkWorldPremissions() {
        deleteJournal();
        String sql = "select value from im_cache_data  where key = ? and type = ?";

        boolean is = true;
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{CacheDataType.workWorldPermissionsValue, "" + CacheDataType.workWorldPermissionsType});
        try {


            while (cursor.moveToNext()) {
                is = Boolean.parseBoolean(cursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            is = true;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return is;
    }


    public void DeleteWorkWorldCommentStateByPostUuid(String uuid) {
        String sql = "";

        sql = "delete from IM_Work_World_OUT_Comment where  postUUID  = ?";


        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();

        try {

//            for (int i = 0; i < list.size(); i++) {
            stat.bindString(1, uuid + "");
            stat.executeUpdateDelete();
//            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }


    public void UpdateWorkWorldCommentStateByCreateTime(String createTime, int flag) {
        String sql = "";

        sql = "update IM_Work_World_Comment set isDelete = ? WHERE createTime < ?";


        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();

        try {

//            for (int i = 0; i < list.size(); i++) {
            stat.bindString(1, flag + "");
            stat.bindString(2, createTime + "");
            stat.executeUpdateDelete();
//            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.i(e + "");
        } finally {
            db.endTransaction();
        }

    }

    public boolean selectHistoryWorkWorldItemIsHave(WorkWorldItem data) {
        deleteJournal();
        String sql = "select * from im_work_world where uuid = ?";

        boolean is = false;
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{data.getUuid()});
        try {


            if (cursor.moveToNext()) {
                is = true;
            } else {
                is = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            is = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return is;
    }


    public List<SearchKeyData> getLocalSearchKeyHistory(int type, int limit) {

        deleteJournal();
        String sql = "select * from IM_SearchHistory  where searchType = ? order by searchTime desc limit ?";

        boolean is = true;
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{type+"", "" + limit});
        List<SearchKeyData> list = new ArrayList<>();
        try {


            while (cursor.moveToNext()) {
                SearchKeyData data = new SearchKeyData();
                data.setSearchKey(cursor.getString(0));
                data.setSearchType(cursor.getString(1));
                data.setSearchTime(cursor.getString(2));
                list.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            is = true;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }

    public void updateLocalSearchKeyHistory(Map<String, String> paramsData) {


        String sql = "insert or replace into IM_SearchHistory (searchKey,searchType,searchTime) values(?,?,?)";
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransactionNonExclusive();
        String time = paramsData.get("searchTime");
        if(TextUtils.isEmpty(paramsData.get("searchTime"))||Long.parseLong(paramsData.get("searchTime"))==0){
            time = String.valueOf(System.currentTimeMillis());
        }
        try {
            stat.bindString(1, paramsData.get("searchKey"));
            stat.bindString(2, paramsData.get("searchType"));
            stat.bindString(3, time);
            stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void clearLocalSearchKeyHistory(int searchType) {
        String sql = "Delete from IM_SearchHistory Where searchType = ? ";
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            SQLiteStatement stat = db.compileStatement(sql);
            stat.bindString(1, searchType+"");
            stat.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}



