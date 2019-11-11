package com.qunar.im.core.helper;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.util.Constants;
import com.qunar.im.other.CacheDataType;
import com.qunar.im.base.structs.MessageStatus;

import org.sqlite.database.sqlite.SQLiteDatabase;
import org.sqlite.database.sqlite.SQLiteOpenHelper;

/**
 * Created by may on 2017/7/5.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 36;
    //    private static final int DB_VERSION = 29;
    private static final String DB_NAME = "data.dat";
    private String dbname = "";

    public DatabaseHelper(Context context, String name) {
        super(context, name + "", null, DB_VERSION);
        getWritableDatabase().enableWriteAheadLogging();
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.beginTransactionNonExclusive();

        try {

            String sql = "CREATE TABLE IF NOT EXISTS IM_User(" +
                    "        UserId                TEXT," +
                    "        XmppId                TEXT PRIMARY KEY," +
                    "        Name                  TEXT," +
                    "        DescInfo              TEXT," +
                    "        HeaderSrc             TEXT," +
                    "        SearchIndex           TEXT," +
                    "        mood                  TEXT," +
                    "        UserInfo              BLOB," +
                    "        LastUpdateTime        INTEGER," +
                    "        IncrementVersion      INTEGER," +
                    "        ExtendedFlag          BLOB," +
                    "        isVisible             INTEGER DEFAULT 1)";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_USER_USERID ON " +
                    "            IM_User(UserId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_USER_XMPPID ON " +
                    "        IM_User(XmppId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_USER_NAME ON " +
                    "        IM_User(Name)";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_Group(" +
                    "        GroupId TEXT" +
                    "        PRIMARY KEY," +
                    "        Name TEXT," +
                    "        Introduce TEXT," +
                    "        HeaderSrc TEXT," +
                    "        Topic TEXT," +
                    "        LastUpdateTime INTEGER," +
                    "        ExtendedFlag BLOB);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_GROUP_GROUPID ON " +
                    "            IM_Group(GroupId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_Group_Member(" +
                    "        MemberId TEXT," +
                    "        GroupId TEXT," +
                    "        MemberJid TEXT," +
                    "        Name TEXT," +
                    "        Affiliation TEXT," +
                    "        LastUpdateTime INTEGER," +
                    "        ExtendedFlag BLOB," +
                    "" +
                    "        primary key(MemberId, GroupId))";

            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_GROUP_MEMBER_MEMBERID ON " +
                    "            IM_Group_Member(MemberId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_GROUP_MEMBER_GROUPID ON " +
                    "            IM_Group_Member(GroupId);";
            sqLiteDatabase.execSQL(sql);
//           //由于主键自带索引,暂时不需要联合索引
//            sql = "CREATE unique index IF NOT EXISTS IX_IM_GROUP_MEMBER_GROUPID_JID_UINDEX ON " +
//                    "        IM_Group_Member(GroupId, MemberJid);";
//            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_SessionList(" +
                    "    XmppId TEXT," +//主键
                    "    RealJid TEXT," +//虚拟id
                    "    UserId TEXT," +//
                    "    LastMessageId TEXT," +
                    "    LastUpdateTime INTEGER," +
                    "    ChatType INTEGER," +
                    "    UnreadCount INTEGER DEFAULT 0," +
                    "    ExtendedFlag BLOB," +
                    "" +
                    "    primary key(XmppId, RealJid));";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_SESSION_MESSAGEID ON " +
                    "        IM_SessionList(LastMessageId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TEMPORARY TABLE t1_backup(" +
                    "            XmppId TEXT," +
                    "            RealJid TEXT," +
                    "            UserId TEXT," +
                    "            LastMessageId TEXT," +
                    "            LastUpdateTime INTEGER," +
                    "            ChatType INTEGER," +
                    "            ExtendedFlag BLOB," +
                    "            primary key (XmppId, RealJid));";
            sqLiteDatabase.execSQL(sql);


            sql = "CREATE TABLE IF NOT EXISTS IM_Cache_Data( " +
                    "            key TEXT, type int, " +
                    "            value TEXT,valueInt INTEGER DEFAULT 0, primary " +
                    "            key(key , type)  );";
            sqLiteDatabase.execSQL(sql);

            sql = "INSERT OR REPLACE INTO  IM_Cache_Data (key,type,value) VALUES('" + CacheDataType.pushState + "'," + CacheDataType.PushStateType + ",31)";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_Message(" +
                    "        MsgId TEXT" +
                    "        PRIMARY KEY," +
                    "        XmppId TEXT," +
                    "        Platform INTEGER," +
                    "        'From' TEXT," +
                    "        'To' TEXT," +
                    "        Content TEXT," +
                    "        Type INTEGER," +
                    "        State INTEGER," +
                    "        Direction INTEGER," +
                    "        ContentResolve TEXT," +
                    "        ReadedTag INTEGER" +
                    "        DEFAULT 0," +
                    "        LastUpdateTime INTEGER," +
                    "        MessageRaw TEXT," +
                    "        RealJid TEXT," +
                    "        ExtendedInfo TEXT," +
                    "        ExtendedFlag BLOB" +
                    "        );";
            sqLiteDatabase.execSQL(sql);


//            sql = "CREATE INDEX IF NOT EXISTS IX_IM_MESSAGE_XMPPID ON " +
//                    "            IM_Message(XmppId);";
//            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_MESSAGE_FROM ON " +
                    "            IM_Message('From');";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_MESSAGE_TO ON " +
                    "        IM_Message('To');";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_MESSAGE_STATE ON " +
                    "        IM_Message(State);";
            sqLiteDatabase.execSQL(sql);

//            sql = "CREATE INDEX IF NOT EXISTS IX_IM_MESSAGE_REALJID ON " +
//                    "        IM_Message(RealJid);";
//            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_Recent_Contacts(" +
                    "        XmppId TEXT" +
                    "        PRIMARY KEY," +
                    "        Type INTEGER," +
                    "        LastUpdateTime INTEGER," +
                    "        ExtendedFlag BLOB" +
                    "            );";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_Public_Number(" +
                    "        XmppId TEXT" +
                    "        PRIMARY KEY," +
                    "        PublicNumberId TEXT," +
                    "        PublicNumberType INTEGER," +
                    "        Name TEXT," +
                    "        DescInfo TEXT," +
                    "        HeaderSrc TEXT," +
                    "        SearchIndex TEXT," +
                    "        PublicNumberInfo BLOB," +
                    "        LastUpdateTime INTEGER," +
                    "        ExtendedFlag BLOB" +
                    "            );";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_PUBLIC_NUMBER_PNID ON " +
                    "            IM_Public_Number(PublicNumberId);";
            sqLiteDatabase.execSQL(sql);


            sql = "CREATE TABLE IF NOT EXISTS IM_Public_Number_Message(" +
                    "    MsgId TEXT" +
                    "    PRIMARY KEY," +
                    "    XmppId TEXT," +
                    "            'From'TEXT," +
                    "            'To'TEXT," +
                    "    Content TEXT," +
                    "    Type INTEGER," +
                    "    State INTEGER," +
                    "    Direction INTEGER," +
                    "    ReadedTag INTEGER" +
                    "    DEFAULT 0," +
                    "    LastUpdateTime INTEGER," +
                    "    ExtendedFlag BLOB);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IM_PUBLIC_NUMBER_MESSAGE_XMPPID ON " +
                    "        IM_Public_Number_Message(XmppId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IM_PUBLIC_NUMBER_MESSAGE_FROM ON " +
                    "        IM_Public_Number_Message('From');";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_PUBLIC_NUMBER_MESSAGE_TO ON " +
                    "    IM_Public_Number_Message('To');";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_Friendster_Message(" +
                    "        MsgId TEXT" +
                    "        PRIMARY KEY," +
                    "        XmppId TEXT," +
                    "        FromUser TEXT," +
                    "        ReplyMsgId TEXT," +
                    "        ReplyUser TEXT," +
                    "        Content Text," +
                    "        LastUpdateTime INTEGER," +
                    "        ExtendedFlag BLOB);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_FRIENDSTER_MESSAGE_XMPPID ON " +
                    "        IM_Friendster_Message(XmppId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_Friend_List(" +
                    "    UserId TEXT," +
                    "    XmppId TEXT" +
                    "    PRIMARY KEY," +
                    "    Name TEXT," +
                    "    DescInfo TEXT," +
                    "    HeaderSrc TEXT," +
                    "    SearchIndex TEXT," +
                    "    UserInfo BLOB," +
                    "    LastUpdateTime INTEGER," +
                    "    IncrementVersion INTEGER," +
                    "    ExtendedFlag BLOB);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_FRIEND_LIST_USERID ON " +
                    "        IM_Friend_List(UserId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_FRIEND_LIST_XMPPID ON " +
                    "        IM_Friend_List(XmppId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_FRIEND_LIST_NAME ON " +
                    "        IM_Friend_List(Name);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_Friend_Notify(" +
                    "    UserId TEXT," +
                    "    XmppId TEXT" +
                    "    PRIMARY KEY," +
                    "    Name TEXT," +
                    "    DescInfo TEXT," +
                    "    HeaderSrc TEXT," +
                    "    SearchIndex TEXT," +
                    "    UserInfo BLOB," +
                    "    State INTEGER," +
                    "    Version INTEGER," +
                    "    LastUpdateTime INTEGER," +
                    "    ExtendedFlag BLOB);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_FRIEND_NOTIFY_USERID ON " +
                    "        IM_Friend_Notify(UserId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_FRIEND_NOTIFY_XMPPID ON " +
                    "        IM_Friend_Notify(XmppId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_FRIEND_NOTIFY_NAME ON " +
                    "        IM_Friend_Notify(Name);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_Black_List(" +
                    "    XmppId TEXT" +
                    "    PRIMARY KEY," +
                    "    LastUpdateTime INTEGER," +
                    "    ExtendedFlag BLOB);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_FRIEND_NOTIFY_NAME ON " +
                    "        IM_Friend_Notify(Name);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_MESSAGE_XMPPID_LASTUPDATEIME ON " +
                    "        IM_Message(XmppId,LastUpdateTime);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_MESSAGE_XMPPID_REALJID ON " +
                    "        IM_Message(XmppId, RealJid);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS Daily_main(" +
                    "        qid INTEGER," +
                    "        version TEXT," +
                    "        type INTEGER," +
                    "        title TEXT," +
                    "        desc TEXT," +
                    "        content TEXT," +
                    "        state INTEGER" +
                    "        );";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE UNIQUE INDEX IF NOT EXISTS qid_index on Daily_main (qid)";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS Password_box_sub(" +
                    "        qsid INTEGER," +
                    "        qid INTEGER," +
                    "        version TEXT," +
                    "        type INTEGER," +
                    "        title TEXT," +
                    "        desc TEXT," +
                    "        content TEXT," +
                    "        P TEXT," +
                    "        U TEXT," +
                    "        time TEXT," +
                    "        state INTEGER" +
                    "        );";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE UNIQUE INDEX IF NOT EXISTS qsid_index on Password_box_sub (qsid)";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS Config(" +
                    "        id INTEGER" +
                    "    PRIMARY KEY," +
                    "        proFile TEXT," +
                    "        preference TEXT" +
                    "        );";
            sqLiteDatabase.execSQL(sql);

            //创建代收消息强化表
            sql = "CREATE TABLE IF NOT EXISTS IM_Message_Collection(" +
                    "    MsgId TEXT" +
                    "    PRIMARY KEY," +
                    "    State INTEGER," +
                    "    ReadedTag INTEGER," +
                    "    Originfrom TEXT," +
                    "    Originto TEXT," +
                    "    Origintype TEXT);";
            sqLiteDatabase.execSQL(sql);
            //创建代收消息索引
            sql = "CREATE INDEX IF NOT EXISTS IX_IM_Message_Collection_MSGID ON " +
                    "        IM_Message_Collection(MsgId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_Message_Collection_ORIGINTO ON " +
                    "        IM_Message_Collection(Originto);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_Message_Collection_ORIGINFROM ON " +
                    "        IM_Message_Collection(Originfrom);";
            sqLiteDatabase.execSQL(sql);

//            CREATE TABLE IF NOT EXISTS IM_Collection_User(XmppId Text PRIMARY KEY,BIND INTEGER)
//            CREATE INDEX IF NOT EXISTS IX_IM_Collection_User_XMPPID ON IM_Collection_User(XmppId)
            //创建代收用户表
            sql = "CREATE TABLE IF NOT EXISTS IM_Collection_User(" +
                    "        XmppId Text" +
                    "        PRIMARY KEY," +
                    "        BIND INTEGER);";
            sqLiteDatabase.execSQL(sql);
            //创建代收用户表索引
            sql = "CREATE INDEX IF NOT EXISTS IX_IM_Collection_User_XMPPID ON " +
                    "        IM_Collection_User(XmppId);";
            sqLiteDatabase.execSQL(sql);

            //代收用户名片及索引
            sql = "CREATE TABLE IF NOT EXISTS IM_Collection_User_Card(" +
                    "        UserId                TEXT," +
                    "        XmppId                TEXT PRIMARY KEY," +
                    "        Name                  TEXT," +
                    "        DescInfo              TEXT," +
                    "        HeaderSrc             TEXT," +
                    "        SearchIndex           TEXT," +
                    "        UserInfo              BLOB," +
                    "        LastUpdateTime        INTEGER," +
                    "        IncrementVersion      INTEGER," +
                    "        ExtendedFlag          BLOB);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_COLLECTION_USER_CARD_USERID ON " +
                    "            IM_Collection_User_Card(UserId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_COLLECTION_USER_CARD_XMPPID ON " +
                    "        IM_Collection_User_Card(XmppId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_COLLECTION_USER_CARD_NAME ON " +
                    "        IM_Collection_User_Card(Name)";
            sqLiteDatabase.execSQL(sql);
            //代收群名片及索引
            sql = "CREATE TABLE IF NOT EXISTS IM_Collection_Group_Card(" +
                    "        GroupId TEXT" +
                    "        PRIMARY KEY," +
                    "        Name TEXT," +
                    "        Introduce TEXT," +
                    "        HeaderSrc TEXT," +
                    "        Topic TEXT," +
                    "        LastUpdateTime INTEGER," +
                    "        ExtendedFlag BLOB);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_COLLECTION_GROUP_CARD_GROUPID ON " +
                    "            IM_Collection_Group_Card(GroupId);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_USER_CONFIG(" +
                    "pkey text," +
                    "subkey text," +
                    "value text," +
                    "version INTEGER default 0," +
                    "isdel INTEGER default 0,primary key (pkey,subkey) );";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_USER_CONFIG_PKEY ON " +
                    "            IM_USER_CONFIG(pkey);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_USER_CONFIG_SUBKEY ON " +
                    "            IM_USER_CONFIG(subkey);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_USER_CONFIG_ISDEL ON " +
                    "            IM_USER_CONFIG(isdel);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_TRIP_INFO (" +
                    "tripId TEXT PRIMARY KEY," +
                    "tripName TEXT," +
                    "tripDate TEXT," +
                    "tripType TEXT," +
                    "tripIntr TEXT," +
                    "tripInviter TEXT," +
                    "beginTime TEXT," +
                    "endTime TEXT," +
                    "scheduleTime TEXT," +
                    "appointment TEXT," +
                    "tripLocale TEXT," +
                    "tripLocaleNumber TEXT," +
                    "tripRoom TEXT," +
                    "tripRoomNumber TEXT," +
                    "memberList TEXT," +
                    "tripRemark TEXT," +
                    "canceled Text" +
                    ")";
            sqLiteDatabase.execSQL(sql);
            sql = "CREATE INDEX IF NOT EXISTS IX_IM_TRIP_INFO_canceled ON " +
                    "            IM_TRIP_INFO(canceled);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_TRIP_INFO_tripDate ON " +
                    "            IM_TRIP_INFO(tripDate);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_TRIP_INFO_beginTime ON " +
                    "            IM_TRIP_INFO(beginTime);";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_IM_TRIP_INFO_tripType ON " +
                    "            IM_TRIP_INFO(tripType);";
            sqLiteDatabase.execSQL(sql);

            //快捷回复
            sql = "CREATE TABLE IF NOT EXISTS IM_QUICK_REPLY_GROUP(" +
                    "sid Long," +
                    "groupname text," +
                    "groupseq Long," +
                    "version Long default 1," +
                    " primary key (sid)" +
                    ");";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_QUICK_REPLY_CONTENT(" +
                    "sid Long," +
                    "gid Long," +
                    "content text," +
                    "contentseq Long," +
                    "version Long default 1," +
                    " primary key (sid, gid)" +
                    ");";
            sqLiteDatabase.execSQL(sql);
            sql = "CREATE INDEX IF NOT EXISTS IX_IM_QUICK_REPLY_CONTENT_GID ON " +
                    "            IM_QUICK_REPLY_CONTENT(gid);";
            sqLiteDatabase.execSQL(sql);
            //快捷回复end
            sql = "CREATE TABLE IF NOT EXISTS  IM_TRIP_AREA (" +
                    "AreaID TEXT PRIMARY KEY," +
                    "Enable TEXT," +
                    "AreaName TEXT," +
                    "MorningStarts TEXT," +
                    "EveningEnds TEXT," +
                    "Description TEXT" +
                    ")";
            sqLiteDatabase.execSQL(sql);

            //创建用户勋章表
            sql = "CREATE TABLE IF NOT EXISTS IM_User_Medal (" +
                    "XmppId  TEXT," +
                    "Type TEXT," +
                    "URL TEXT," +
                    "URLDesc TEXT," +
                    "LastUpdateTime INTEGER DEFAULT 0," +
                    "PRIMARY KEY (XmppId,Type))";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IM_USER_MEDAL_XMPPID ON IM_User_Medal (XmppId)";
            sqLiteDatabase.execSQL(sql);

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

            sql = "insert or replace into IM_Cache_Data (key,type,valueInt) values (?,?,?)";
            sqLiteDatabase.execSQL(sql, new Object[]{CacheDataType.lastUpdateTimeValue, CacheDataType.lastUpdateTimeValueType, 0});

            sql = "create trigger if not exists lastupdatetime_insert" +
                    " after " +
                    "insert on IM_Message " +
                    "for each row " +
                    "begin " +
                    "update IM_Cache_Data set valueInt = " +
                    "case when (valueInt<new.LastUpdateTime and new.State&" + MessageStatus.LOCAL_STATUS_SUCCESS + "=" + MessageStatus.LOCAL_STATUS_SUCCESS + ")" +
                    " then new.LastUpdateTime " +
                    "else valueInt " +
                    "end " +
                    "where key='" + CacheDataType.lastUpdateTimeValue + "' and type=" + CacheDataType.lastUpdateTimeValueType + " ; " +
                    "end;";
            sqLiteDatabase.execSQL(sql);

            sql = "create trigger if not exists lastupdatetime_update" +
                    " after " +
                    "update of State on IM_Message " +
                    "for each row " +
                    "begin " +
                    "update IM_Cache_Data set valueInt = " +
                    "case when (valueInt<new.LastUpdateTime and old.State&" + MessageStatus.LOCAL_STATUS_SUCCESS + "<>" + MessageStatus.LOCAL_STATUS_SUCCESS + " and new.State&" + MessageStatus.LOCAL_STATUS_SUCCESS + "=" + MessageStatus.LOCAL_STATUS_SUCCESS + ")" +
                    " then new.LastUpdateTime " +
                    "else valueInt " +
                    "end " +
                    "where key='" + CacheDataType.lastUpdateTimeValue + "' and type=" + CacheDataType.lastUpdateTimeValueType + " ; " +
                    "end;";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IX_MESSAGE_TYPE ON " +
                    "        IM_Message(type);";
            sqLiteDatabase.execSQL(sql);


            //创建工作圈表
            sql = "CREATE TABLE IF NOT EXISTS IM_Work_World (" +
                    "id INTEGER," +
                    "uuid TEXT PRIMARY KEY," +
                    "owner TEXT," +
                    "owner_host TEXT," +
                    "isAnonymous INTEGER," +
                    "anonymousName TEXT," +
                    "anonymousPhoto TEXT," +
                    "createTime INTEGER DEFAULT 0," +
                    "updateTime INTEGER DEFAULT 0," +
                    "content INTEGER," +
                    "atList TEXT," +
                    "likeNum INTEGER," +
                    "commentsNum INTEGER," +
                    "review_status INTEGER," +
                    "isDelete INTEGER," +
                    "isLike INTEGER," +
                    "postType INTEGER DEFAULT 1," +
                    "attachCommentListString TEXT);";
            sqLiteDatabase.execSQL(sql);

            sqLiteDatabase.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS Ww_createTime_index ON IM_Work_World (createTime);";
            sqLiteDatabase.execSQL(sql);
            sql = "CREATE INDEX IF NOT EXISTS Ww_id_index ON IM_Work_World (id);";
            sqLiteDatabase.execSQL(sql);
            sql = "CREATE INDEX IF NOT EXISTS Ww_isAnonymous_index ON IM_Work_World (isAnonymous);";
            sqLiteDatabase.execSQL(sql);
            sql = "CREATE INDEX IF NOT EXISTS Ww_uuid_index ON IM_Work_World (uuid);";
            sqLiteDatabase.execSQL(sql);


            sqLiteDatabase.setTransactionSuccessful();

            //创建朋友圈评论表
            sql = "CREATE TABLE IF NOT EXISTS IM_Work_World_Comment (" +
                    "anonymousName TEXT," +
                    "anonymousPhoto TEXT," +
                    "commentUUID TEXT PRIMARY KEY," +
                    "content TEXT," +
                    "createTime INTEGER DEFAULT 0," +
                    "fromHost TEXT," +
                    "fromUser TEXT," +
                    "id INTEGER," +
                    "isAnonymous INTEGER," +
                    "isDelete INTEGER," +
                    "isLike INTEGER," +
                    "likeNum INTEGER," +
                    "parentCommentUUID TEXT," +
                    "postUUID TEXT," +
                    "reviewStatus INTEGER," +
                    "toHost TEXT," +
                    "toUser TEXT," +
                    "updateTime INTEGER DEFAULT 0," +
                    "toisAnonymous INTEGER," +
                    "toAnonymousName TEXT," +
                    "toAnonymousPhoto TEXT," +
                    "superParentUUID TEXT," +
                    "newChildString TEXT," +
                    "commentStatus INTEGER DEFAULT 0," +
                    "atList TEXT)";
            sqLiteDatabase.execSQL(sql);


            //创建朋友圈评论表
            sql = "CREATE TABLE IF NOT EXISTS IM_Work_World_OUT_Comment (" +
                    "anonymousName TEXT," +
                    "anonymousPhoto TEXT," +
                    "commentUUID TEXT PRIMARY KEY," +
                    "content TEXT," +
                    "createTime INTEGER DEFAULT 0," +
                    "fromHost TEXT," +
                    "fromUser TEXT," +
                    "id INTEGER," +
                    "isAnonymous INTEGER," +
                    "isDelete INTEGER," +
                    "isLike INTEGER," +
                    "likeNum INTEGER," +
                    "parentCommentUUID TEXT," +
                    "postUUID TEXT," +
                    "reviewStatus INTEGER," +
                    "toHost TEXT," +
                    "toUser TEXT," +
                    "updateTime INTEGER DEFAULT 0," +
                    "toisAnonymous INTEGER," +
                    "toAnonymousName TEXT," +
                    "toAnonymousPhoto TEXT," +
                    "superParentUUID TEXT," +
                    "commentStatus INTEGER DEFAULT 0)";
            sqLiteDatabase.execSQL(sql);


//            sql = "CREATE TABLE IF NOT EXISTS IM_Work_World_Notice (" +
//                    "eventType INTEGER," +
//                    "userFrom TEXT," +
//                    "userFromHost TEXT," +
//                    "userTo TEXT," +
//                    "userToHost TEXT," +
//                    "fromIsAnyonous INTEGER," +
//                    "fromAnyonousName TEXT," +
//                    "fromAnyonousPhoto TEXT," +
//                    "toIsAnyonous INTEGER," +
//                    "toAnyonousName TEXT," +
//                    "toAnyonousPhoto TEXT," +
//                    "content TEXT," +
//                    "postUUID TEXT," +
//                    "uuid TEXT PRIMARY KEY," +
//                    "createTime INTEGER DEFAULT 0," +
//                    "readState INTEGER DEFAULT 0," +
//                    "owner TEXT," +
//                    "owner_host TEXT," +
//                    "isAnonymous INTEGER," +
//                    "anonymousName TEXT," +
//                    "anonymousPhoto TEXT" +
//                    ")";

            sql = "CREATE TABLE IF NOT EXISTS IM_Work_World_Notice (" +
                    "eventType INTEGER," +
                    "userFrom TEXT," +
                    "userFromHost TEXT," +
                    "userTo TEXT," +
                    "userToHost TEXT," +
                    "fromIsAnyonous INTEGER," +
                    "fromAnyonousName TEXT," +
                    "fromAnyonousPhoto TEXT," +
                    "toIsAnyonous INTEGER," +
                    "toAnyonousName TEXT," +
                    "toAnyonousPhoto TEXT," +
                    "content TEXT," +
                    "postUUID TEXT," +
                    "uuid TEXT PRIMARY KEY," +
                    "createTime INTEGER DEFAULT 0," +
                    "readState INTEGER DEFAULT 0" +
                    ")";
            sqLiteDatabase.execSQL(sql);


            //会议室功能新增城市选择

            sql = "CREATE TABLE IF NOT EXISTS  IM_TRIP_CITY (" +
                    "id TEXT PRIMARY KEY," +
                    "cityName TEXT" +
                    ")";
            sqLiteDatabase.execSQL(sql);

            /**
             * 新增搜索关键字功能
             */
            sql = "CREATE TABLE IF NOT EXISTS IM_SearchHistory (" +
                    "searchKey  TEXT," +
                    "searchType INTEGER DEFAULT 0," +
                    "searchTime INTEGER DEFAULT 0," +
                    "primary key (searchKey, searchType))";
            sqLiteDatabase.execSQL(sql);

            /**
             * 新增勋章列表
             */
            sql = "CREATE TABLE IF NOT EXISTS IM_Medal_List (" +
                    "medalId  INTEGER," +
                    "medalName TEXT ," +
                    "obtainCondition TEXT ," +
                    "smallIcon TEXT," +
                    "bigLightIcon TEXT," +
                    "bigGrayIcon TEXT," +
                    "bigLockIcon TEXT," +
                    "status INTEGER," +
                    "primary key (medalId))";
            sqLiteDatabase.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS IM_User_Status_Medal(" +
                    "medalId  INTEGER," +
                    "userId TEXT," +
                    "host TEXT," +
                    "medalStatus INTEGER," +
                    "mappingVersion INTEGER ," +
                    "updateTime TEXT ," +
                    "primary key (medalId,userId))";
            sqLiteDatabase.execSQL(sql);


        } catch (Exception e) {
            Logger.e(e, "create table failed");

        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "";
        Logger.i("数据库更新：oldVersion = " + oldVersion + "   newVersion = " + newVersion);
        if (newVersion > oldVersion) {
            //DB_VERSION == 2  新增session cache表
            sql = "CREATE TABLE IF NOT EXISTS IM_Cache_Data( " +
                    "            key TEXT, type int, " +
                    "            value TEXT,valueInt INTEGER DEFAULT 0, primary " +
                    "            key(key , type)  );";
            sqLiteDatabase.execSQL(sql);
            //DB_VERSION == 5 新增用户设置表
            if (oldVersion < 6) {//更新了数据存储，需要清空表
                sql = "delete from IM_Message";
                sqLiteDatabase.execSQL(sql);
                sql = "delete from IM_SessionList";
                sqLiteDatabase.execSQL(sql);
                sql = "delete from IM_Message_Collection";
                sqLiteDatabase.execSQL(sql);
            }


            if (oldVersion < 9) {
                sql = "INSERT OR REPLACE INTO  IM_Cache_Data (key,type,value) VALUES('" + CacheDataType.pushState + "'," + CacheDataType.PushStateType + ",31)";
                sqLiteDatabase.execSQL(sql);
            }

            if (oldVersion < 14) {
                /**
                 * 新增UnreadCount列
                 */
                sql = "ALTER TABLE IM_SessionList ADD COLUMN UnreadCount INTEGER DEFAULT 0;";
                sqLiteDatabase.execSQL(sql);
                /**
                 * 进行老数据迁移
                 *
                 */
                sql = "update IM_SessionList set UnreadCount = (select count(1) from IM_Message as a left join IM_MessageRead as b on a.msgid= b.msgid  where 0x02<>(b.readstate&0x02) and a.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + ")  and IM_SessionList.xmppid ||'-'|| IM_SessionList.realjid = a.xmppid ||'-'|| a.realjid )";
                sqLiteDatabase.execSQL(sql);

                /**
                 * 新增
                 * 更新消息阅读状态表时的触发器，更新sessionlist表未读数
                 */

                sql = "create trigger if not exists sessionlist_unread_update" +
                        " after " +
                        "update of ReadState on IM_MessageRead " +
                        "for each row " +
                        "begin " +
                        "update IM_SessionList set UnreadCount = " +
                        "case when (new.ReadState & " + MessageStatus.REMOTE_STATUS_CHAT_READED + ") =" + MessageStatus.REMOTE_STATUS_CHAT_READED + " and old.ReadState & " + MessageStatus.REMOTE_STATUS_CHAT_READED + " <>" + MessageStatus.REMOTE_STATUS_CHAT_READED +
                        " then UnreadCount - 1 " +
                        "when (new.ReadState & " + MessageStatus.REMOTE_STATUS_CHAT_READED + ") <>" + MessageStatus.REMOTE_STATUS_CHAT_READED + " and old.ReadState & " + MessageStatus.REMOTE_STATUS_CHAT_READED + " =" + MessageStatus.REMOTE_STATUS_CHAT_READED +
                        " then UnreadCount + 1 " +
                        "else UnreadCount " +
                        "end " +
                        "where XmppId||'-'||realjid = (select XmppId||'-'||realjid from IM_Message where MsgId = new.MsgId and IM_Message.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + ")) ; " +
                        "end;";
                sqLiteDatabase.execSQL(sql);

                /**
                 * 更新消息新插入时的触发器 更新sessionlist表未读数
                 */


                sql = "create trigger if not exists sessionlist_unread_insert" +
                        " after " +
                        "insert on IM_MessageRead " +
                        "for each row " +
                        "begin " +
                        "update IM_SessionList set UnreadCount = " +
                        "case when (new.ReadState & " + MessageStatus.REMOTE_STATUS_CHAT_READED + " )<>" + MessageStatus.REMOTE_STATUS_CHAT_READED +
                        " then UnreadCount+1 " +
                        "else UnreadCount " +
                        "end " +
                        "where XmppId||'-'||realjid = (select XmppId||'-'||realjid from IM_Message where MsgId = new.MsgId and IM_Message.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + ")) ; " +
                        "end;";
                sqLiteDatabase.execSQL(sql);

            }

            if (oldVersion < 15) {
                sql = "CREATE TABLE IF NOT EXISTS IM_USER_CONFIG(" +
                        "pkey text," +
                        "subkey text," +
                        "value text," +
                        "version INTEGER default 0," +
                        "isdel INTEGER default 0,primary key (pkey,subkey) );";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS IX_IM_USER_CONFIG_PKEY ON " +
                        "            IM_USER_CONFIG(pkey);";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS IX_IM_USER_CONFIG_SUBKEY ON " +
                        "            IM_USER_CONFIG(subkey);";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS IX_IM_USER_CONFIG_ISDEL ON " +
                        "            IM_USER_CONFIG(isdel);";
                sqLiteDatabase.execSQL(sql);
            }


            if (oldVersion < 16) {
                //快捷回复
                sql = "CREATE TABLE IF NOT EXISTS IM_QUICK_REPLY_GROUP(" +
                        "sid Long," +
                        "groupname text," +
                        "groupseq Long," +
                        "version Long default 1," +
                        " primary key (groupname)" +
                        ");";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE TABLE IF NOT EXISTS IM_QUICK_REPLY_CONTENT(" +
                        "sid Long," +
                        "gid Long," +
                        "content text," +
                        "contentseq Long," +
                        "version Long default 1," +
                        " primary key (gid, content)" +
                        ");";

                sqLiteDatabase.execSQL(sql);
                sql = "CREATE INDEX IF NOT EXISTS IX_IM_QUICK_REPLY_CONTENT_GID ON " +
                        "            IM_QUICK_REPLY_CONTENT(gid);";
                sqLiteDatabase.execSQL(sql);
                //快捷回复end
            }

            if (oldVersion < 17) {
                sql = "CREATE TABLE IF NOT EXISTS IM_TRIP_INFO (" +
                        "tripId TEXT PRIMARY KEY," +
                        "tripName TEXT," +
                        "tripDate TEXT," +
                        "tripType TEXT," +
                        "tripIntr TEXT," +
                        "tripInviter TEXT," +
                        "beginTime TEXT," +
                        "endTime TEXT," +
                        "scheduleTime TEXT," +
                        "appointment TEXT," +
                        "tripLocale TEXT," +
                        "tripLocaleNumber TEXT," +
                        "tripRoom TEXT," +
                        "tripRoomNumber TEXT," +
                        "memberList TEXT," +
                        "tripRemark TEXT," +
                        "canceled Text" +
                        ")";
                sqLiteDatabase.execSQL(sql);
                sql = "CREATE INDEX IF NOT EXISTS IX_IM_TRIP_INFO_canceled ON " +
                        "            IM_TRIP_INFO(canceled);";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS IX_IM_TRIP_INFO_tripDate ON " +
                        "            IM_TRIP_INFO(tripDate);";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS IX_IM_TRIP_INFO_beginTime ON " +
                        "            IM_TRIP_INFO(beginTime);";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS IX_IM_TRIP_INFO_tripType ON " +
                        "            IM_TRIP_INFO(tripType);";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE TABLE IF NOT EXISTS  IM_TRIP_AREA (" +
                        "AreaID TEXT PRIMARY KEY," +
                        "Enable TEXT," +
                        "AreaName TEXT," +
                        "MorningStarts TEXT," +
                        "EveningEnds TEXT," +
                        "Description TEXT" +
                        ")";
                sqLiteDatabase.execSQL(sql);
            }
            //just for 未读数不准问题
            if (oldVersion < 19) {
                sql = "update IM_SessionList set UnreadCount = 0 where UnreadCount<0;";
                sqLiteDatabase.execSQL(sql);
            }

            if (oldVersion < 21) {
                //创建用户勋章表
                sql = "CREATE TABLE IF NOT EXISTS IM_User_Medal (" +
                        "XmppId  TEXT," +
                        "Type TEXT," +
                        "URL TEXT," +
                        "URLDesc TEXT," +
                        "LastUpdateTime INTEGER DEFAULT 0," +
                        "PRIMARY KEY (XmppId,Type))";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS IM_USER_MEDAL_XMPPID ON IM_User_Medal (XmppId)";
                sqLiteDatabase.execSQL(sql);

                sql = "DROP INDEX IX_IM_MESSAGE_XMPPID";
                sqLiteDatabase.execSQL(sql);

                sql = "DROP INDEX IX_IM_MESSAGE_REALJID";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS IX_MESSAGE_XMPPID_REALJID ON " +
                        "        IM_Message(XmppId, RealJid);";
                sqLiteDatabase.execSQL(sql);
            }

            if (oldVersion < 22) {
                sql = "drop trigger if exists sessionlist_unread_insert;";
                sqLiteDatabase.execSQL(sql);

                sql = "drop trigger if exists sessionlist_unread_update;";
                sqLiteDatabase.execSQL(sql);

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
                        "where XmppId||'-'||realjid = new.XmppId||'-'||new.realjid and new.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + "); " +
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
                        "where XmppId||'-'||realjid = new.XmppId||'-'||new.realjid and new.'from' <> (select value from IM_Cache_Data where key ='" + CacheDataType.USER_ID + "' and type = " + CacheDataType.USER_ID_TYPE + "); " +
                        "end;";
                sqLiteDatabase.execSQL(sql);
            }
            if (oldVersion < 24) {
                sql = "drop trigger if exists sessionlist_unread_insert;";
                sqLiteDatabase.execSQL(sql);

                sql = "drop trigger if exists sessionlist_unread_update;";
                sqLiteDatabase.execSQL(sql);

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
            }

            if (oldVersion < 25) {
                sql = "drop index IX_MESSAGE_LASTUPDATEIME_XMPPID";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS IX_MESSAGE_XMPPID_LASTUPDATEIME ON " +
                        "        IM_Message(XmppId, LastUpdateTime);";
                sqLiteDatabase.execSQL(sql);
            }

            if (oldVersion < 26) {

                sql = "delete from IM_Cache_Data where type=" + CacheDataType.REMIND;
                sqLiteDatabase.execSQL(sql);

                sql = "ALTER TABLE IM_Cache_Data ADD COLUMN valueInt INTEGER DEFAULT 0;";
                sqLiteDatabase.execSQL(sql);

                sql = "insert or replace into IM_Cache_Data (key,type,valueInt) values (?,?,?)";
                sqLiteDatabase.execSQL(sql, new Object[]{CacheDataType.lastUpdateTimeValue, CacheDataType.lastUpdateTimeValueType, 0});

                sql = "update IM_Cache_Data set valueInt=(select max(LastUpdateTime) from IM_Message where State&" + MessageStatus.LOCAL_STATUS_SUCCESS + "=" + MessageStatus.LOCAL_STATUS_SUCCESS + ") where key=? and type=?;";
                sqLiteDatabase.execSQL(sql, new Object[]{CacheDataType.lastUpdateTimeValue, CacheDataType.lastUpdateTimeValueType});

                sql = "create trigger if not exists lastupdatetime_insert" +
                        " after " +
                        "insert on IM_Message " +
                        "for each row " +
                        "begin " +
                        "update IM_Cache_Data set valueInt = " +
                        "case when (valueInt<new.LastUpdateTime and new.State&" + MessageStatus.LOCAL_STATUS_SUCCESS + "=" + MessageStatus.LOCAL_STATUS_SUCCESS + ")" +
                        " then new.LastUpdateTime " +
                        "else valueInt " +
                        "end " +
                        "where key='" + CacheDataType.lastUpdateTimeValue + "' and type=" + CacheDataType.lastUpdateTimeValueType + " ; " +
                        "end;";
                sqLiteDatabase.execSQL(sql);

                sql = "create trigger if not exists lastupdatetime_update" +
                        " after " +
                        "update of State on IM_Message " +
                        "for each row " +
                        "begin " +
                        "update IM_Cache_Data set valueInt = " +
                        "case when (valueInt<new.LastUpdateTime and old.State&" + MessageStatus.LOCAL_STATUS_SUCCESS + "<>" + MessageStatus.LOCAL_STATUS_SUCCESS + " and new.State&" + MessageStatus.LOCAL_STATUS_SUCCESS + "=" + MessageStatus.LOCAL_STATUS_SUCCESS + ")" +
                        " then new.LastUpdateTime " +
                        "else valueInt " +
                        "end " +
                        "where key='" + CacheDataType.lastUpdateTimeValue + "' and type=" + CacheDataType.lastUpdateTimeValueType + " ; " +
                        "end;";
                sqLiteDatabase.execSQL(sql);


                sql = "CREATE INDEX IF NOT EXISTS IX_MESSAGE_TYPE ON " +
                        "        IM_Message(type);";
                sqLiteDatabase.execSQL(sql);
            }

            if (oldVersion < 28) {

                sql = "CREATE TABLE IF NOT EXISTS IM_Work_World (" +
                        "id INTEGER," +
                        "uuid TEXT PRIMARY KEY," +
                        "owner TEXT," +
                        "owner_host TEXT," +
                        "isAnonymous INTEGER," +
                        "anonymousName TEXT," +
                        "anonymousPhoto TEXT," +
                        "createTime INTEGER DEFAULT 0," +
                        "updateTime INTEGER DEFAULT 0," +
                        "content INTEGER," +
                        "atList TEXT," +
                        "likeNum INTEGER," +
                        "commentsNum INTEGER," +
                        "review_status INTEGER," +
                        "isDelete INTEGER," +
                        "isLike INTEGER);";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE INDEX IF NOT EXISTS Ww_createTime_index ON IM_Work_World (createTime);";
                sqLiteDatabase.execSQL(sql);
                sql = "CREATE INDEX IF NOT EXISTS Ww_id_index ON IM_Work_World (id);";
                sqLiteDatabase.execSQL(sql);
                sql = "CREATE INDEX IF NOT EXISTS Ww_isAnonymous_index ON IM_Work_World (isAnonymous);";
                sqLiteDatabase.execSQL(sql);
                sql = "CREATE INDEX IF NOT EXISTS Ww_uuid_index ON IM_Work_World (uuid);";
                sqLiteDatabase.execSQL(sql);

                sql = "CREATE TABLE IF NOT EXISTS IM_Work_World_Comment (" +
                        "anonymousName TEXT," +
                        "anonymousPhoto TEXT," +
                        "commentUUID TEXT PRIMARY KEY," +
                        "content TEXT," +
                        "createTime INTEGER DEFAULT 0," +
                        "fromHost TEXT," +
                        "fromUser TEXT," +
                        "id INTEGER," +
                        "isAnonymous INTEGER," +
                        "isDelete INTEGER," +
                        "isLike INTEGER," +
                        "likeNum INTEGER," +
                        "parentCommentUUID TEXT," +
                        "postUUID TEXT," +
                        "reviewStatus INTEGER," +
                        "toHost TEXT," +
                        "toUser TEXT," +
                        "updateTime INTEGER DEFAULT 0," +
                        "toisAnonymous INTEGER," +
                        "toAnonymousName TEXT," +
                        "toAnonymousPhoto TEXT)";
                sqLiteDatabase.execSQL(sql);


//                sql = "CREATE TABLE IF NOT EXISTS IM_Work_World_Notice (" +
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
//                        "createTime INTEGER DEFAULT 0," +
//                        "readState INTEGER DEFAULT 0," +
//                        "owner TEXT," +
//                        "owner_host TEXT," +
//                        "isAnonymous INTEGER," +
//                        "anonymousName TEXT," +
//                        "anonymousPhoto TEXT" +
//                        ")";

                sql = "CREATE TABLE IF NOT EXISTS IM_Work_World_Notice (" +
                        "eventType INTEGER," +
                        "userFrom TEXT," +
                        "userFromHost TEXT," +
                        "userTo TEXT," +
                        "userToHost TEXT," +
                        "fromIsAnyonous INTEGER," +
                        "fromAnyonousName TEXT," +
                        "fromAnyonousPhoto TEXT," +
                        "toIsAnyonous INTEGER," +
                        "toAnyonousName TEXT," +
                        "toAnyonousPhoto TEXT," +
                        "content TEXT," +
                        "postUUID TEXT," +
                        "uuid TEXT PRIMARY KEY," +
                        "createTime INTEGER DEFAULT 0," +
                        "readState INTEGER DEFAULT 0" +
                        ")";
                sqLiteDatabase.execSQL(sql);

            }
            if (oldVersion < 29) {
                /**
                 * 新增UnreadCount列
                 */
                sql = "ALTER TABLE IM_Work_World_Comment ADD COLUMN superParentUUID TEXT ;";
                sqLiteDatabase.execSQL(sql);
//            }
//            if(oldVersion < 33){
                /**
                 * 新增postType列
                 */
                sql = "ALTER TABLE IM_Work_World ADD COLUMN postType INTEGER DEFAULT 1;";
                sqLiteDatabase.execSQL(sql);
//            }
//
//            if(oldVersion <34){

                //创建朋友圈评论表
                sql = "CREATE TABLE IF NOT EXISTS IM_Work_World_OUT_Comment (" +
                        "anonymousName TEXT," +
                        "anonymousPhoto TEXT," +
                        "commentUUID TEXT PRIMARY KEY," +
                        "content TEXT," +
                        "createTime INTEGER DEFAULT 0," +
                        "fromHost TEXT," +
                        "fromUser TEXT," +
                        "id INTEGER," +
                        "isAnonymous INTEGER," +
                        "isDelete INTEGER," +
                        "isLike INTEGER," +
                        "likeNum INTEGER," +
                        "parentCommentUUID TEXT," +
                        "postUUID TEXT," +
                        "reviewStatus INTEGER," +
                        "toHost TEXT," +
                        "toUser TEXT," +
                        "updateTime INTEGER DEFAULT 0," +
                        "toisAnonymous INTEGER," +
                        "toAnonymousName TEXT," +
                        "toAnonymousPhoto TEXT," +
                        "superParentUUID TEXT," +
                        "commentStatus INTEGER DEFAULT 0)";
                sqLiteDatabase.execSQL(sql);
//            }
//
//
//            if(oldVersion <35){
                /**
                 * 新增attachCommentListString列
                 */
                sql = "ALTER TABLE IM_Work_World ADD COLUMN attachCommentListString TEXT;";
                sqLiteDatabase.execSQL(sql);
//            }
//
//            if(oldVersion<36){
                /**
                 * 新增newChildString列
                 */
                sql = "ALTER TABLE IM_Work_World_Comment ADD COLUMN newChildString TEXT ;";
                sqLiteDatabase.execSQL(sql);
//            }
//
//            if(oldVersion<37){


                /**
                 * 新增newChildString列
                 */
                sql = "ALTER TABLE IM_Work_World_Comment ADD COLUMN commentStatus INTEGER DEFAULT 0;";
                sqLiteDatabase.execSQL(sql);
//                /**
//                 * 新增newChildString列
//                 */
//                sql = "ALTER TABLE IM_Work_World_OUT_Comment ADD COLUMN commentStatus INTEGER DEFAULT 0;";
//                sqLiteDatabase.execSQL(sql);
////            }
//
//            if(oldVersion < 29){
                sql = "ALTER TABLE IM_User ADD COLUMN mood TEXT;";
                sqLiteDatabase.execSQL(sql);
            }

            if (oldVersion < 30) {
                //会议室功能新增城市选择

                sql = "CREATE TABLE IF NOT EXISTS  IM_TRIP_CITY (" +
                        "id TEXT PRIMARY KEY," +
                        "cityName TEXT" +
                        ")";
                sqLiteDatabase.execSQL(sql);
            }

            if (oldVersion < 31) {
                /**
                 * 新增newChildString列
                 */
                sql = "ALTER TABLE IM_Work_World_Comment ADD COLUMN atList TEXT;";
                sqLiteDatabase.execSQL(sql);

            }

            if (oldVersion < 32) {
                /**
                 * 新增搜索关键字功能
                 */
                sql = "CREATE TABLE IF NOT EXISTS IM_SearchHistory (" +
                        "searchKey  TEXT," +
                        "searchType INTEGER DEFAULT 0," +
                        "searchTime INTEGER DEFAULT 0," +
                        "primary key (searchKey, searchType))";
                sqLiteDatabase.execSQL(sql);
            }

            /**
             * 删除之前热线缓存
             */
            if(oldVersion < 33){
                sql = "DELETE from IM_Cache_Data where key = '" + CacheDataType.HOTLINE_KEY + "' and type = " + CacheDataType.HOTLINE_TYPE;
                sqLiteDatabase.execSQL(sql);
            }

            /**
             * IM_User新增是否显示字段
             */
            if(oldVersion < 34){
                sql = "ALTER TABLE IM_User ADD COLUMN isVisible INTEGER DEFAULT 1;";
                sqLiteDatabase.execSQL(sql);

            }

            /**
             * fix 系统消息点不开的bug
             */
            if(oldVersion < 35){
                sql = "update IM_SessionList set ChatType = " + ConversitionType.MSG_TYPE_HEADLINE + " where XmppId = '" + Constants.SYS.SYSTEM_MESSAGE + "';";
                sqLiteDatabase.execSQL(sql);
            }

            if (oldVersion < 36) {
                /**
                 * 新增勋章列表
                 */
                sql = "CREATE TABLE IF NOT EXISTS IM_Medal_List (" +
                        "medalId  INTEGER," +
                        "medalName TEXT ," +
                        "obtainCondition TEXT ," +
                        "smallIcon TEXT," +
                        "bigLightIcon TEXT," +
                        "bigGrayIcon TEXT," +
                        "bigLockIcon TEXT," +
                        "status INTEGER," +
                        "primary key (medalId))";
                sqLiteDatabase.execSQL(sql);

//
                /**
                 * 新增勋章列表
                 */


                sql = "CREATE TABLE IF NOT EXISTS IM_User_Status_Medal(" +
                        "medalId  INTEGER," +
                        "userId TEXT," +
                        "host TEXT," +
                        "medalStatus INTEGER," +
                        "mappingVersion INTEGER ," +
                        "updateTime TEXT ," +
                        "primary key (medalId,userId))";
                sqLiteDatabase.execSQL(sql);
            }



        }
    }

}
