package com.qunar.im.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 日志上报db
 */
public class LogDatabaseHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "log.dat";

    public LogDatabaseHelper(Context context){
        super(context,  DB_NAME, null, DB_VERSION);
        getWritableDatabase().enableWriteAheadLogging();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransactionNonExclusive();
        try{
            String sql = "CREATE TABLE IF NOT EXISTS IM_Log(" +
                    "        type                TEXT," +
                    "        subType             TEXT," +
                    "        reportTime          TEXT," +
                    "        content             TEXT);";
            db.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IM_LOG_TYPE_SUBTYPE ON " +
                    "            IM_Log(type,subType);";
            db.execSQL(sql);

            sql = "CREATE INDEX IF NOT EXISTS IM_LOG_REPORTTIME ON " +
                    "            IM_Log(reportTime);";
            db.execSQL(sql);

            db.setTransactionSuccessful();
        }catch (Exception e){

        }finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
