package com.qunar.rn_service.util;

import com.qunar.im.base.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangyu.wang on 16/5/9.
 */
public class FileHelper {

    public static final int CACHED_SIZE = 2048;
    public static final String TAG = "FileHelper";

    public static void close(Closeable closeable) {
        try {
            if(closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            LogUtil.e(TAG, e.toString());
        } finally {

        }
    }


    public static boolean inputStreamToFile(InputStream inputStream, String filename, String tmpSuffix) throws IOException {

        BufferedInputStream bis = new BufferedInputStream(inputStream);
        // 存储加临时后缀,避免重名
        File file = new File(filename + tmpSuffix);
        File parent = file.getParentFile();
        if (parent != null && (!parent.exists())) {
            parent.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos, CACHED_SIZE);
        int count;
        byte data[] = new byte[CACHED_SIZE];
        while ((count = bis.read(data, 0, CACHED_SIZE)) != -1) {
            bos.write(data, 0, count);
        }
        bos.flush();
        bos.close();
        bis.close();

        return renameFile(file, filename);
    }

    public static boolean renameFile(File file, String destFilename) {

        final File finalFile = new File(destFilename);

        if(file.renameTo(finalFile)) {
            file.delete();
        }
        return true;
    }

    /**
     * the traditional io way
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(String filename) throws IOException{

        File f = new File(filename);
        if(!f.exists()){
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int)f.length());
        BufferedInputStream in = null;
        try{
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while(-1 != (len = in.read(buffer,0,buf_size))){
                bos.write(buffer,0,len);
            }
            return bos.toByteArray();
        }catch (IOException e) {
            LogUtil.e(TAG, e.toString());
            throw e;
        }finally{
            try{
                in.close();
            }catch (IOException e) {
                LogUtil.e(TAG, e.toString());
            }
            bos.close();
        }
    }

    public static byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

}
