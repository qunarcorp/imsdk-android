package com.qunar.im.base.util.graphics;

import android.text.TextUtils;

import com.facebook.common.util.SecureHashUtil;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.common.CommonConfig;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by xinbo.wang on 2015/5/4.
 */
public class MyDiskCache
{
    private static final String DEFAULT_DISK_STORAGE_VERSION_PREFIX = "v2";

    /*
     * We use sharding to avoid Samsung's RFS problem, and to avoid having one big directory
     * containing thousands of files.
     * This number of directories is large enough based on the following reasoning:
     * - high usage: 150 photos per day
     * - such usage will hit Samsung's 6,500 photos cap in 43 days
     * - 100 buckets will extend that period to 4,300 days which is 11.78 years
     */
    private static final int SHARDING_BUCKET_COUNT = 100;
    private static final String CONTENT_FILE_EXTENSION = ".cnt";

    public static final String TEMP_VOICE_FILE_NAME = "qvoice.tmp";

    public static final String CACHE_LOG_DIR = MyDiskCache.getDirectory().getAbsolutePath() + "/logs/" + (CommonConfig.isQtalk?"qtalk":"qchat");

    public static File getDirectory() {
        File file = FileUtils.getFilesDir(QunarIMApp.getContext());
        file = new File(file,"qtalk_cache");
        if(!file.exists())
        {
            file.mkdirs();
        }
        return file;
    }

    public static File getFile(String imageUri)
    {
        if(TextUtils.isEmpty(imageUri))
            return new File(UUID.randomUUID().toString()+".qtmp");
        File dir = getDirectory();
        String fileName = null;

        try {
            String resourceId= SecureHashUtil.makeSHA1HashBase64(imageUri.getBytes("UTF-8"));
            dir = new File(dir,getSubdirectory(resourceId));
            if(!dir.exists()){
                dir.mkdirs();
            }
            fileName = getFileNameByURL(resourceId);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e("MyDisk","error",e);
            fileName = "empty.tmp";
        }
        File file = new File(dir, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static String getFileNameByURL(String resourceId) throws UnsupportedEncodingException {
        /*String fileName = null;
        int len = imageUri.length();
        if(imageUri.indexOf("file=")>0)
        {

            if(imageUri.indexOf("&")>0)
            {
                len = imageUri.indexOf("&");
            }
            fileName = imageUri.substring(imageUri.indexOf("=") + 1,len);
        }
        else {
            if(imageUri.indexOf("?")>0)
            {
                len = imageUri.indexOf("?");
            }
            fileName = imageUri.substring(imageUri.lastIndexOf("/")+1,len);
        }
        int idx = fileName.indexOf("/");
        if(idx>0)
        {
            fileName = fileName.substring(idx+1,fileName.length());
        }*/
        String fileName = resourceId +CONTENT_FILE_EXTENSION; //BinaryUtil.MD5(imageUri);
        return fileName;
    }

    private static String getSubdirectory(String resourceId) {
        String subdirectory = String.valueOf(Math.abs(resourceId.hashCode() % SHARDING_BUCKET_COUNT));
        return  getVersionSubdirectoryName(100)+"/"+subdirectory;
    }

    static String getVersionSubdirectoryName(int version) {
        return String.format(
                (Locale) null,
                "%s.ols%d.%d",
                DEFAULT_DISK_STORAGE_VERSION_PREFIX,
                SHARDING_BUCKET_COUNT,
                version);
    }

    public static File getSmallDirectory() {
        File file = FileUtils.getFilesDir(QunarIMApp.getContext());
        file = new File(file,"small_icon");
        if(!file.exists())
        {
            file.mkdirs();
        }
        return file;
    }

    public static File getSmallFile(String imgUrl)
    {
        File dir =getSmallDirectory();
        String fileName = null;

        try {
            String resourceId= SecureHashUtil.makeSHA1HashBase64(imgUrl.getBytes("UTF-8"));
            dir = new File(dir,getSubdirectory(resourceId));
            fileName = getFileNameByURL(resourceId);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e("MyDisk","error",e);
            fileName = "empty.tmp";
        }
        return new File(dir, fileName);
    }

    public static File getVoiceDir()
    {
        File internalDir = QunarIMApp.getContext().getFilesDir();
        internalDir = new File(internalDir,"qvoice");
        if(!internalDir.exists())
        {
            internalDir.mkdirs();
        }
        return internalDir;
    }

    public static File getTempDir()
    {
        File tempDir = FileUtils.getFilesDir(QunarIMApp.getContext());
        tempDir = new File(tempDir,"qtemp");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        return tempDir;
    }

    public static File getVoiceFile(String fileName)
    {
        return new File(getVoiceDir().getAbsolutePath()+File.separator+fileName);
    }

    public static File getTempFile(String fileName)
    {
        return new File(getTempDir(),fileName);
    }

    public static File[] getAllCacheDir()
    {
        File[] files = new File[2];
        files[0] = getDirectory();
        files[1] = getVoiceDir();
        return files;
    }
}
