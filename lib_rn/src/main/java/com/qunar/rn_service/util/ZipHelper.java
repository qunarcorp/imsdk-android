package com.qunar.rn_service.util;

import com.qunar.im.base.util.LogUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by wangyu.wang on 16/5/9.
 */
public class ZipHelper {

    public static final String tmp = ".tmp";
    public static final String TAG = "ZipHelper";

    public static boolean UnzipFileByFilename(String filePath, String fileName) throws Exception{

        ZipFile zipFile = new ZipFile(filePath + fileName);
        Enumeration emu = zipFile.entries();
        while (emu.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) emu.nextElement();
            if (entry.isDirectory()) {
                new File(filePath + entry.getName()).mkdirs();
                continue;
            }

            FileHelper.inputStreamToFile(zipFile.getInputStream(entry), filePath + entry.getName(), tmp);
        }
        zipFile.close();

        return false;
    }

    public static boolean UnzipFileByInputStream(InputStream reader){
        return false;
    }

    public static void main(String args[]) {
        String zipFilename = "b.txt.zip";
        String filedir = "/Users/qitmac000005/code/ops/qchat_camelhelp/SRC/app/src/androidTest/java/com/qunar/im/camelhelp/utils/";
        try {
            UnzipFileByFilename(filedir, zipFilename);
            System.out.println("ok");
        } catch (Exception e) {
            LogUtil.e(TAG, e.toString());
        }
    }
}
