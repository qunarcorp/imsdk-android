package com.qunar.im.log;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.qunar.im.base.jsonbean.LogInfo;
import com.qunar.im.base.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class LogDatabaseManager {

    private static LogDatabaseManager instance;
    private LogDatabaseHelper logDatabaseHelper;

    public static LogDatabaseManager getInstance(){
        synchronized (LogDatabaseManager.class){
            if(instance == null){
                instance = new LogDatabaseManager();
            }
        }
        return instance;
    }

    /**
     * 初始化DB
     * @param context
     */
    public void initDB(Context context){
        logDatabaseHelper = new LogDatabaseHelper(context);
    }

    /**
     * 日志插入
     */
    public void insertLog(LogInfo log){
        if(log == null){
            return;
        }
        String sql = "insert into IM_Log(type,subType,reportTime,content) values (?,?,?,?)";
        SQLiteDatabase db = logDatabaseHelper.getWritableDatabase();
        SQLiteStatement sqLiteStatement = db.compileStatement(sql);
        try{
            db.beginTransaction();
            sqLiteStatement.bindString(1,log.getType());
            sqLiteStatement.bindString(2,log.getSubType());
            sqLiteStatement.bindLong(3,log.getReportTime());
            sqLiteStatement.bindString(4,JsonUtils.getGsonEscaping().toJson(log));
            sqLiteStatement.executeInsert();
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }

    public List<LogInfo> queryAllLogs(long lastReportTime){
        List<LogInfo> lists = new ArrayList<>();
        String sql = "select * from IM_Log where reportTime<= ? order by reportTime desc limit 1000";
        SQLiteDatabase db = logDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(lastReportTime)});
        try{
            while (cursor.moveToNext()){
                String content = cursor.getString(3);
                LogInfo logInfo = JsonUtils.getGson().fromJson(content,LogInfo.class);
                lists.add(logInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
            return lists;
        }
    }

    public boolean clearLogs(long start,long end){
        boolean isScuess = false;
        String sql = "delete from IM_Log where reportTime<=? and reportTime>=? ";
        SQLiteDatabase db = logDatabaseHelper.getWritableDatabase();
        SQLiteStatement sqLiteStatement = db.compileStatement(sql);
        try{
            db.beginTransaction();
            sqLiteStatement.bindLong(1,start);
            sqLiteStatement.bindLong(2,end);
            sqLiteStatement.executeInsert();
            db.setTransactionSuccessful();
            isScuess = true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
            return isScuess;
        }
    }
}
