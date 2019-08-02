package com.qunar.im.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.CalendarTrip;
import com.qunar.im.base.module.Nick;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.protobuf.common.CurrentPreference;

import java.text.ParseException;
import java.util.TimeZone;

public class CalendarReminderUtils {

    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "qunar";
//    private static String CALENDARS_ACCOUNT_NAME = "hubin.hu@qunar.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.qunar";
    private static String CALENDARS_DISPLAY_NAME = "qunar账户";

    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME          // 2
    };

    public static final String[] TRIP_PROJECTION = new String[] {
            CalendarContract.Events._ID,                           // 0
    };
    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    private static long checkAndAddCalendarAccount(Context context) {
        long oldId = checkCalendarAccount(context);
        if( oldId >= 0 ){
            return oldId;
        }else{
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }

    }

    @SuppressLint("MissingPermission")
    private static long checkEvents(Context context,CalendarTrip.DataBean.TripsBean bean){
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        long start = 0;
        long end = 0;
        String locan = "";
        try {
            locan = TextUtils.isEmpty(bean.getAppointment())?bean.getTripLocale()+"-"+bean.getTripRoom():bean.getAppointment();
            start = DateUtil.string2Time(bean.getBeginTime(), "yyyy-MM-dd HH:mm:ss").getTime();
            end = DateUtil.string2Time(bean.getEndTime(), "yyyy-MM-dd HH:mm:ss").getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String selection = "((" + CalendarContract.Events.ORIGINAL_SYNC_ID + " = ?))";
        String selection = "((" + CalendarContract.Events.TITLE + " = ? and "+CalendarContract.Events.DTSTART+ " = ? " +
                "and "+CalendarContract.Events.DTEND +"= ? and "+CalendarContract.Events.EVENT_LOCATION+" = ?))";
//        String accountName = CurrentPreference.getInstance().getUserid()+"@qunar.com";
//        String[] selectionArgs = new String[] {bean.getTripId()};
        String[] selectionArgs = new String[] {bean.getTripName(),start+"",end+"",locan};
        cur = cr.query(uri, TRIP_PROJECTION, selection, selectionArgs, null);
        try {
            if (cur == null) { //查询返回空值
                return -1;
            }
            while (cur.moveToNext()){
                long calId = cur.getLong(PROJECTION_ID_INDEX);
//                if(userCursor.getString(userCursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)).equals(CALENDARS_ACCOUNT_NAME)){
//                    return  userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
//                }
                return calId;

            }
            return  -1;
//            int count = userCursor.getCount();
//            for (int i = 0; i < userCursor.moveToNext(); i++) {
//
//            }

//            if (count > 0) { //存在现有账户，取第一个账户的id返回
//                userCursor.moveToFirst();
//                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
//            } else {
//                return -1;
//            }
        } finally {
            if (cur != null) {
                cur.close();
            }

        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    @SuppressLint("MissingPermission")
    private static long checkCalendarAccount(Context context) {

        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))";
        String accountName = CurrentPreference.getInstance().getUserid()+"@" + QtalkNavicationService.getInstance().getEmail();
        String[] selectionArgs = new String[] {accountName};
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

//        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (cur == null) { //查询返回空值
                return -1;
            }
            while (cur.moveToNext()){
                long calId = cur.getLong(PROJECTION_ID_INDEX);
//                if(userCursor.getString(userCursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)).equals(CALENDARS_ACCOUNT_NAME)){
//                    return  userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
//                }
                return calId;

            }
            return  -1;
//            int count = userCursor.getCount();
//            for (int i = 0; i < userCursor.moveToNext(); i++) {
//
//            }

//            if (count > 0) { //存在现有账户，取第一个账户的id返回
//                userCursor.moveToFirst();
//                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
//            } else {
//                return -1;
//            }
        } finally {
            if (cur != null) {
                cur.close();
            }

        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        String accountName = CurrentPreference.getInstance().getUserid()+"@" + QtalkNavicationService.getInstance().getEmail();
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    public static void AnalysisCalendarEvent(Context context, CalendarTrip.DataBean.TripsBean bean, int remindInAdvance){
        if (context == null) {
            return;
        }
        long calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            return;
        }

        long checkEventID = checkEvents(context,bean);
        if(checkEventID>0){
            updateCalendarEvent(context,bean,remindInAdvance,checkEventID,calId);
//            addCalendarEvent(context,bean,remindInAdvance,calId);
        }else{
            addCalendarEvent(context,bean,remindInAdvance,calId);
        }
    }

    @SuppressLint("MissingPermission")
    public static void updateCalendarEvent(Context context, CalendarTrip.DataBean.TripsBean bean, int remindInAdvance, long eventID, long calId){
        //添加日历事件
        long start = 0;
        long end = 0;
        try {
            start = DateUtil.string2Time(bean.getBeginTime(), "yyyy-MM-dd HH:mm:ss").getTime();
            end = DateUtil.string2Time(bean.getEndTime(), "yyyy-MM-dd HH:mm:ss").getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String locan = TextUtils.isEmpty(bean.getAppointment())?bean.getTripLocale()+"-"+bean.getTripRoom():bean.getAppointment();

        String der = "会议详情:\n"+bean.getTripIntr();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.TITLE, bean.getTripName());
        String inviterID = QtalkStringUtils.parseId(bean.getTripInviter())+"@" + QtalkNavicationService.getInstance().getEmail();
        event.put(CalendarContract.Events.ORGANIZER,inviterID);
        event.put(CalendarContract.Events.ORIGINAL_SYNC_ID,bean.getTripId());
        event.put(CalendarContract.Events.DESCRIPTION,der);
        event.put(CalendarContract.Events.EVENT_LOCATION,locan);
        event.put(CalendarContract.Events.CALENDAR_ID, calId); //插入账户的id
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");//这个是时区，必须有
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = context.getContentResolver().update(updateUri, event, null, null);
        Logger.i("更新日历数据行数:"+rows);
        for (int i = 0; i < bean.getMemberList().size(); i++) {
//            Nick nick = ConnectionUtil.getInstance().getNickById(bean.getMemberList().get(i).getMemberId());
//////            ContentResolver cr = context.getContentResolver();
//////            ContentValues values = new ContentValues();
//////            values.put(CalendarContract.Attendees.ATTENDEE_NAME, nick.getName());
//////            values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, nick.getUserId()+"@qunar.com");
//////            values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
//////            values.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_OPTIONAL);
//////            int status = 0;
//////            if(bean.getMemberList().get(i).getMemberState().equals("0")){
//////                status = CalendarContract.Attendees.ATTENDEE_STATUS_TENTATIVE;
//////            }else if(bean.getMemberList().get(i).getMemberState().equals("1")){
//////                status = CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED;
//////            }else if(bean.getMemberList().get(i).getMemberState().equals("2")){
//////                status = CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED;
//////            }
//////            values.put(CalendarContract.Attendees.ATTENDEE_STATUS, status);
//////            values.put(CalendarContract.Attendees.EVENT_ID, eventID);
//////
//////
//////
//////            Uri uri =  ContentUris.withAppendedId(CalendarContract.Attendees.CONTENT_URI,eventID);
//////
//////           int s =  cr.update(uri,values,null,null);


            Nick nick = ConnectionUtil.getInstance().getNickById(bean.getMemberList().get(i).getMemberId());
//                String name = nick.getName();
//                members+=(name+"\n");

            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Attendees.ATTENDEE_NAME, nick.getName());
            values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, nick.getUserId()+"@" + QtalkNavicationService.getInstance().getEmail());
            values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
            values.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_OPTIONAL);
            int status = 0;
            if(bean.getMemberList().get(i).getMemberState().equals("0")){
                status = CalendarContract.Attendees.ATTENDEE_STATUS_TENTATIVE;
            }else if(bean.getMemberList().get(i).getMemberState().equals("1")){
                status = CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED;
            }else if(bean.getMemberList().get(i).getMemberState().equals("2")){
                status = CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED;
            }
            values.put(CalendarContract.Attendees.ATTENDEE_STATUS, status);
            values.put(CalendarContract.Attendees.EVENT_ID, eventID);
            @SuppressLint("MissingPermission")
            Uri uri = cr.insert(CalendarContract.Attendees.CONTENT_URI, values);
//           Logger.i("更新联系人状态:"+s);


        }

    }

    /**
     * 添加日历事件
     */
    public static void addCalendarEvent(Context context, CalendarTrip.DataBean.TripsBean bean, int remindInAdvance,long calId) {


        //添加日历事件
        long start = 0;
        long end = 0;
        try {
            start = DateUtil.string2Time(bean.getBeginTime(), "yyyy-MM-dd HH:mm:ss").getTime();
            end = DateUtil.string2Time(bean.getEndTime(), "yyyy-MM-dd HH:mm:ss").getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String locan = TextUtils.isEmpty(bean.getAppointment())?bean.getTripLocale()+"-"+bean.getTripRoom():bean.getAppointment();

        String der = "会议详情:\n"+bean.getTripIntr();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.TITLE, bean.getTripName());
        String inviterID = QtalkStringUtils.parseId(bean.getTripInviter())+"@" + QtalkNavicationService.getInstance().getEmail();
        event.put(CalendarContract.Events.ORGANIZER,inviterID);
        event.put(CalendarContract.Events.ORIGINAL_SYNC_ID,bean.getTripId());
//        event.put(CalendarContract.Events._SYNC_ID,bean.getTripId());
        event.put(CalendarContract.Events.DESCRIPTION,der);
        event.put(CalendarContract.Events.EVENT_LOCATION,locan);
        event.put(CalendarContract.Events.CALENDAR_ID, calId); //插入账户的id
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");//这个是时区，必须有
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALENDER_EVENT_URL), event); //添加事件
        if (newEvent == null) { //添加日历事件失败直接返回
            return;
        }else{
            //添加会议与会人

            long eventID = Long.parseLong(newEvent.getLastPathSegment());
//            long eventID = 202;
            for (int i = 0; i < bean.getMemberList().size(); i++) {
                Nick nick = ConnectionUtil.getInstance().getNickById(bean.getMemberList().get(i).getMemberId());
//                String name = nick.getName();
//                members+=(name+"\n");

                ContentResolver cr = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Attendees.ATTENDEE_NAME, nick.getName());
                values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, nick.getUserId()+"@" + QtalkNavicationService.getInstance().getEmail());
                values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
                values.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_OPTIONAL);
                int status = 0;
                if(bean.getMemberList().get(i).getMemberState().equals("0")){
                    status = CalendarContract.Attendees.ATTENDEE_STATUS_TENTATIVE;
                }else if(bean.getMemberList().get(i).getMemberState().equals("1")){
                    status = CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED;
                }else if(bean.getMemberList().get(i).getMemberState().equals("2")){
                    status = CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED;
                }
                values.put(CalendarContract.Attendees.ATTENDEE_STATUS, status);
                values.put(CalendarContract.Attendees.EVENT_ID, eventID);
                @SuppressLint("MissingPermission")
                Uri uri = cr.insert(CalendarContract.Attendees.CONTENT_URI, values);

            }

        }


        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        values.put(CalendarContract.Reminders.MINUTES, remindInAdvance );// 提前previousDate天有提醒
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);
        if (uri == null) { //添加事件提醒失败直接返回
            return;
        }

    }

    /**
     * 删除日历事件
     */
    public static void deleteCalendarEvent(Context context,CalendarTrip.DataBean.TripsBean bean) {
        long checkEventID = checkEvents(context,bean);
        if(checkEventID<0){
            return;
        }
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, checkEventID);
        int rows = cr.delete(deleteUri, null, null);
//        Log.i(DEBUG_TAG, "Rows deleted: " + rows);
    }
}


