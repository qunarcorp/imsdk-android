package com.qunar.im.utils;

import androidx.annotation.NonNull;

import com.qunar.im.base.util.LogUtil;
import com.qunar.im.core.services.FeedBackServcie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by zhaokai on 15-8-5.
 */
public class UnzipUtils {
    private static final String TAG = "UnzipUtils";
    private static final int BUFF = 1024;

    /***
     * @param dir        解压目的文件夹
     * @param file       原压缩文件
     * @param deleteFile 是否删除原文件
     * @return 返回解压后的根目录文件夹
     **/
    public static File unZipFile(@NonNull File dir, @NonNull File file, boolean deleteFile) {
        final String DIR_BASE = dir.getAbsolutePath() + File.separator;
        File resultDir = null;
        ZipFile zip;
        try {
            zip = new ZipFile(file);
            if (zip != null) {
                Enumeration e = zip.entries();
                while (e.hasMoreElements()) {
                    InputStream is = null;
                    OutputStream os = null;
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    try {
                        File f = new File(DIR_BASE + entry.getName());
                        if (entry.isDirectory()) {
                            if (!f.exists()) {
                                f.mkdirs();
                            }
                            if (resultDir == null) {
                                resultDir = f;
                            }
                        } else {
                            if(!f.getPath().contains("/"))
                            {
                                resultDir = dir;
                            }
                            //保证路径创建
                            File temp = f.getParentFile();
                            while (!temp.exists()){
                                temp.mkdirs();
                                temp = temp.getParentFile();
                            }
                            f.createNewFile();
                            is = new BufferedInputStream(zip.getInputStream(entry));
                            os = new BufferedOutputStream(new FileOutputStream(f));
                            int len;
                            byte[] buff = new byte[BUFF];
                            while ((len = is.read(buff, 0, buff.length)) != -1) {
                                os.write(buff, 0, len);
                            }
                        }
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                        if (os != null) {
                            os.close();
                        }
                    }
                }
            }
            //防止zip内文件夹没解析，导致没有根路径
            if(resultDir == null){
                Enumeration e = zip.entries();
                ZipEntry entry = (ZipEntry) e.nextElement();
                if(entry.getName().contains("/")){
                    resultDir = new File(DIR_BASE + entry.getName().substring(0, entry.getName().indexOf("/")));
                }
            }
        } catch (FileNotFoundException ex) {
            LogUtil.e(TAG, "File not found", ex);
        } catch (IOException e) {
            LogUtil.e(TAG, "IO Exception", e);
        }

        if (deleteFile) {
            if (file != null && file.exists()) {
                file.delete();
            }
        }

        return resultDir;
    }
    /**
     * 批量压缩文件（夹）
     *
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile 生成的压缩文件
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFiles(Collection<File> resFileList, File zipFile) throws IOException {
        final int BUFF_SIZE = 2048;
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        for (File resFile : resFileList) {
            zipFile(resFile, zipout, "");
        }
        zipout.close();
    }

    /**
     * 批量压缩文件（夹）
     *
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile 生成的压缩文件
     * @param comment 压缩文件的注释
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFiles(Collection<File> resFileList, File zipFile, String comment)
            throws IOException {
        zipFiles(resFileList,zipFile,comment,null);
    }

    public static void zipFiles(Collection<File> resFileList, File zipFile, String comment, FeedBackServcie.Callback callback)
            throws IOException {
        final int BUFF_SIZE = 2048;
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        int i = 0;
        for (File resFile : resFileList) {
            zipFile(resFile, zipout, "");
            if(callback != null){
                i++;
                callback.showFeedProgress(i,resFileList.size(), FeedBackServcie.FeedType.ZIP);
            }
        }
        zipout.setComment(comment);
        zipout.close();
    }
    /**
     * 压缩文件
     *
     * @param resFile 需要压缩的文件（夹）
     * @param zipout 压缩的目的文件
     * @param rootpath 压缩的文件路径
     * @throws FileNotFoundException 找不到文件时抛出
     * @throws IOException 当压缩过程出错时抛出
     */
    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
            throws FileNotFoundException, IOException {
            final int BUFF_SIZE = 2048;
        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
                + resFile.getName();
        rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipout, rootpath);
            }
        } else {
            byte buffer[] = new byte[BUFF_SIZE];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
                    BUFF_SIZE);
            zipout.putNextEntry(new ZipEntry(rootpath));
            int realLength;
            while ((realLength = in.read(buffer)) != -1) {
                zipout.write(buffer, 0, realLength);
            }
            in.close();
            zipout.flush();
            zipout.closeEntry();
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
                if(!files[i].getName().toLowerCase().endsWith("zip"))
                    filelist.add(files[i]);
            }
        }
        return filelist;
    }
}
