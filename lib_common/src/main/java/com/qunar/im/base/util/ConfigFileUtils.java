package com.qunar.im.base.util;

import android.content.Context;
import android.text.TextUtils;

import com.qunar.im.base.common.QunarIMApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by xingchao.song on 4/7/2016.
 */
public class ConfigFileUtils {
    private static final String TAG = ConfigFileUtils.class.getSimpleName();
    private static final String CONFIG_FILE_NAME = "config.txt";

    public static boolean isZh() {
        Locale locale = QunarIMApp.getContext().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language!=null&&language.endsWith("zh");
    }

    public static void copyConfig(Context context) {
        File f = new File(context.getFilesDir(), CONFIG_FILE_NAME);
        if (f.exists()) {
            return;
        }
        try {
            InputStream is = context.getAssets().open(CONFIG_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (IOException e) {
            LogUtil.e(TAG,"error",e);
        }
    }

    public static String loadNavConfig(Context context,String fileName) {
        String line = "";
        File configFile = new File(context.getFilesDir(), fileName);
        if (!configFile.exists()) {
            return line;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            line = reader.readLine();
            if(!TextUtils.isEmpty(line)){
               return   line;
            }
        } catch (IOException e) {
            LogUtil.e(TAG,"error",e);
        }
        return  line;
    }

    public static void saveNavConfig(String jsonContent, Context context,String fileName) {
        File configFile = new File(context.getFilesDir(), fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(configFile);
            fos.write(jsonContent.getBytes());
            fos.close();
        } catch (IOException e) {
            LogUtil.e(TAG,"error",e);
        }
    }

}
