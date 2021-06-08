package com.qunar.im.base.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.qunar.im.common.CommonConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xinbo.wang on 2015/3/26.
 */
public class FileUtils {
    public static final String savePath = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/";

    public static void saveFileToExtensionStorage(File sourceFile) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/qtalk");
        myDir.mkdirs();

        File file = new File(myDir, sourceFile.getName());
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            FileInputStream in = new FileInputStream(sourceFile);
            int length = (int) sourceFile.length();
            byte[] bytes = new byte[length];
            in.read(bytes);
            out.write(bytes);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap ResizeBitmap(Bitmap bitmap, int scale)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(1 / scale, 1 / scale);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }


    public static byte[] toByteArray(File file, int len) {
        FileInputStream fileInputStream = null;
        byte[] bFiles = new byte[len];
        if(!file.exists()) return bFiles;
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFiles, 0, len);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bFiles;
    }

    public static File getFilesDir(Context context) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        try {
            final File cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ?
                    getExternalFilesDir(context) : context.getFilesDir();
            return cachePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getExternalFilesDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File f= context.getExternalFilesDir(null);
            if(f!=null)
                return f;
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String dir = "/Android/data/" + context.getPackageName() + "/files/glide";
        File extFile = Environment.getExternalStorageDirectory();
        return new File(extFile.getPath() + dir);
    }

    /**
     * 获取访问SD卡中图片路径
     */
    public static String getPath(Uri uri, Context cxt) {
        if (uri != null) {
            Cursor cursor = cxt.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = cxt.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            return path;
        }
        return null;
    }

    public static String getRealPath(Uri fileUrl) {
        File file = new File(fileUrl.getPath());
        return file.getPath();
    }

    public static void removeDir(File dir) {
        if (dir.isFile()) {
            dir.delete();
            return;
        }
        File[] childFiles = dir.listFiles();
        if (childFiles != null && childFiles.length > 0) {
            for (File file : childFiles) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    removeDir(file);
                }
            }
        }
        dir.delete();
    }

    public static String getFormatFileSize(String file) {
        File f = new File(file);
        String fileSizeString = "0MB";
        if (f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                long fileS = fis.available();
                fileSizeString = getFormatSizeStr(fileS);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(fis!=null) try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileSizeString;
    }

    public static String getFormatSizeStr(long size)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public enum FileSizeUnit
    {
        B,K,M,G
    }

    public static int getFileSize(String file,FileSizeUnit unit)
    {
        File f = new File(file);
        int result = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                long fileS = fis.available();

                if(unit == FileSizeUnit.B)
                {
                    result = (int) Math.ceil(fileS);
                }else if(unit == FileSizeUnit.K){
                   result = (int) Math.ceil((double) fileS / 1024);
                }
                else if(unit == FileSizeUnit.G)
                {
                    result = (int) Math.ceil((double) fileS / 1073741824);
                }
                else
                {
                    result = (int) Math.ceil((double) fileS / 1048576);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(fis!=null) try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(result ==0) result = 1;
        return result;
    }

    /**
     * Get the md5 value of the io.File specified file
     *
     * @param file The filepath of the file
     * @return The md5 value
     */
    public static String fileToMD5(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file); // Create an FileInputStream instance according to the filepath
            byte[] buffer = new byte[1024]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("MD5"); // Get a MD5 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte[] md5Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(md5Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            return "";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Convert the hash bytes to hex digits string
     *
     * @param hashBytes
     * @return The converted hex digits string
     */
    private static String convertHashToString(byte[] hashBytes) {
        String returnVal = "";
        for (int i = 0; i < hashBytes.length; i++) {
            returnVal += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1);
        }
        return returnVal.toLowerCase();
    }

    public static void writeToFile(String str, String fileName, Context ctx, boolean delIfExists) {
        File dir = ctx.getFilesDir();
        File file = new File(dir, fileName);
        if (file.exists() && delIfExists) {
            file.delete();
        }
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file)));
            printWriter.println(str);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    public static void writeToFile(String str,File file, boolean delIfExists) {
        if (file.exists() && delIfExists) {
            file.delete();
        }
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file)));
            printWriter.println(str);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    /**
     * Reads the named file, translating {@link IOException} to a
     *
     * @param fileName {@code non-null;} name of the file to read
     * @return {@code non-null;} contents of the file
     */
    public static byte[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        return readFile(file);
    }

    /**
     * Reads the given file, translating {@link IOException} to a
     *
     * @param file {@code non-null;} the file to read
     * @return {@code non-null;} contents of the file
     * @throws IOException
     */
    public static byte[] readFile(File file) throws IOException {
        if (!file.exists()) {
            throw new RuntimeException(file + ": file not found");
        }

        if (!file.isFile()) {
            throw new RuntimeException(file + ": not a file");
        }

        if (!file.canRead()) {
            throw new RuntimeException(file + ": file not readable");
        }

        long longLength = file.length();
        int length = (int) longLength;
        if (length != longLength) {
            throw new RuntimeException(file + ": file too long");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(length);

        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[8192];
            int bytesRead = 0;
            while ((bytesRead = in.read(buffer)) > 0) {
                baos.write(buffer, 0, bytesRead);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    // ignored.
                }
            }
        }

        return baos.toByteArray();
    }

    public static byte[] readStream(InputStream is) throws IOException {
        return readStream(is, 32 * 1024);
    }

    public static byte[] readStream(InputStream is, int initSize) throws IOException {
        if (initSize <= 0) {
            initSize = 32 * 1024;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(initSize);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    public static void deleteFile(Context context, String fileName) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static String readFirstLine(String fileName, Context ctx) {
        File dir = ctx.getFilesDir();
        File file = new File(dir, fileName);
        BufferedReader reader = null;
        String line = "";
        if (!file.exists())
            return line;
        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }
    public static String readFirstLine(File file, Context ctx) {
        BufferedReader reader = null;
        String line = "";
        if (!file.exists())
            return line;
        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }


    /**
     * 获取存储视频的路径，包含文件名
     * @param prefix  文件前缀
     * @return 创建的文件 path/"VID_"+prefix+yyyyMMdd_HHmmss.mp4
     */
    public static File getOutputMediaFile(String prefix) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = CommonConfig.globalContext.getCacheDir();

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_" +prefix+ timeStamp + ".mp4");
        return mediaFile;
    }
    /**
     * 生成一个存放视频第一帧图片的路径
     * @param prefix
     * @return
     */
    public static File getOutputFrameFile(String prefix) {
        File mediaStorageDir = getFilesDir(CommonConfig.globalContext);//CommonConfig.globalContext.getCacheDir();
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "qtalk_" +prefix+ timeStamp + ".jpg");
        return mediaFile;
    }


    /**
     * 获取第一帧图片
     * @param fileName 视频文件路径
     * @return 第一帧图片的路径
     */
    public static String getFristFrameOfFile(String fileName){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(fileName);
            String filePath = saveImage(retriever.getFrameAtTime(1));
            return filePath;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

       return "";
    }

    /**
     * 保存图片
     * @param bmp 要保存的源文件
     * @return  保存后文件的地址
     */
    public static String saveImage(Bitmap bmp){
        String filePath =getOutputFrameFile("frame").getAbsolutePath();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmp.recycle();
        }
        return filePath;
    }

    /**
     * 计算某个文件或文件夹的大小
     */
    public static long getDirSize(File dir) {
        if (dir == null || !dir.exists()) {
            return 0;
        }
        long total = 0;
        if (dir.isFile()) {
            total += dir.length();
        } else if (dir.isDirectory()) {
            File[] getFiles = dir.listFiles();
            if (getFiles.length > 0) {
                for (File f : getFiles) {
                    total += getDirSize(f);
                }
            }
        }
        return total;
    }

    public static long calculateDiskSize(File dir) {
        StatFs statFs = new StatFs(dir.getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return statFs.getTotalBytes();
        }
        return statFs.getBlockSize() * statFs.getBlockCount();
    }

    public static long calculateDiskFree(File dir) {
        StatFs statFs = new StatFs(dir.getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            return statFs.getFreeBytes();
        }
        return statFs.getBlockSize() * statFs.getFreeBlocks();
    }

    public static Uri toUri(String path){
        return Uri.parse("file:///" + path);
    }

    public static boolean copy(File source, File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            bos = new BufferedOutputStream(new FileOutputStream(dest, false));

            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    @TargetApi(19)
    public static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)
                    &&Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    if(id != null && id.startsWith("raw:")){
                        return id.substring(id.indexOf("raw:") + 1);
                    }
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                String path = getDataColumn(context, uri, null, null);
                return path;
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        return uri.toString();
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;

        final String column = MediaStore.Images.Media.DATA;
        final String id = MediaStore.Images.Media._ID;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException e) {
            //华为scheme content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/
            String rootPre = File.separator + "root";
            return uri.getPath().startsWith(rootPre) ? uri.getPath().replace(rootPre, "") : uri.getPath();
        } catch (Exception e) {
            if (cursor != null)
                cursor.close();
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getIdColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;

        final String column = MediaStore.Images.Media._ID;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            if (cursor != null)
                cursor.close();
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String formatByteSize(long bytes) {
        StringBuilder sb = new StringBuilder();
        if (bytes == 0) {
           sb.append("0 Byte");
        } else {
            if (bytes < 1024) { //不到1KB
                sb.append(bytes).append(" ").append("bytes");
            } else if (bytes < 1024 * 1024) { //不到1 MB
                float f = 1.0f * bytes / 1024;
                sb.append(String.format("%.2f", f)).append(" ").append("KB");
            } else if (bytes < 1024 * 1024 * 1024) { // 不到 1 GB
                float f = 1.0f * bytes / 1024 / 1024;
                sb.append(String.format("%.2f", f)).append(" ").append("MB");
            } else { // 大于1GB
                float f = 1.0f * bytes / 1024 / 1024 / 1024;
                sb.append(String.format("%.2f", f)).append(" ").append("GB");
            }
        }
        return sb.toString();
    }
}
