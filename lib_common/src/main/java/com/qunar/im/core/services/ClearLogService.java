package com.qunar.im.core.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 清理日志
 */
public class ClearLogService extends IntentService {
    public static void runClearLogService(Context context) {
        Intent intent = new Intent(context, ClearLogService.class);
        context.startService(intent);
    }

    public ClearLogService() {
        super(ClearLogService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String folder = MyDiskCache.CACHE_LOG_DIR;
        //遍历所有日志文件，大于7天的删除
        List<File> files = listFiles(folder);
        Logger.i("ClearLogService clearLogFile 日志文件数" + files.size() + "  当前时间" + DateTimeUtils.getTime(System.currentTimeMillis(), true, true));
        if(files != null && files.size() > 1){
            for(File file : files){
                Logger.i("ClearLogService clearLogFile 当前文件名" + file.getName() + "  当前文件时间" + DateTimeUtils.getTime(file.lastModified(),false, true));
                if(System.currentTimeMillis() - file.lastModified() > 7 * 24 * 60 * 60 * 1000){
                    file.delete();
                }
            }
        }
    }

    public static ArrayList<File> listFiles(String strPath) {
        return refreshFileList(strPath);
    }
    public static ArrayList<File> refreshFileList(String strPath) {
        ArrayList<File> filelist = new ArrayList<File>();
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files == null)
            return null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                refreshFileList(files[i].getAbsolutePath());
            } else {
                filelist.add(files[i]);
            }
        }
        return filelist;
    }
}
