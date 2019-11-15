package com.qunar.im.ui.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;

public class ShareUtil {


    public static final String TYPE_VIDEO = "video/*";
    public static final String TYPE_IMAGE = "image/*";

    public static void shareImage(Context context, File file, String title) {
        shareFile(context, file, TYPE_IMAGE, title);
    }

    public static void shareVideo(Context context, File file, String title) {
        shareFile(context, file, TYPE_VIDEO, title);
    }

    public static void shareFile(Context context, File file, String shareType, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (TYPE_VIDEO.equals(shareType)) {
                fileUri = getVideoContentUri(context, file);
            } else if (TYPE_IMAGE.equals(shareType)) {
                fileUri = getImageContentUri(context, file);
            } else {
                fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileUri = Uri.fromFile(file);
        }
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setType(shareType);
        Intent chooser = Intent.createChooser(intent, title);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        }
    }

    private static Uri getVideoContentUri(Context context, File file) {
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=?",
                new String[]{filePath}, null);
        Uri uri = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                Uri baseUri = Uri.parse("content://media/external/video/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }
            cursor.close();
        }

        //如果使用fileProvider获取失败，则使用此方法
        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        }

        if (uri == null) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        }

        return uri;
    }


    public static void imageExternalShare(File file, Context context) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("image/*");  //设置分享内容的类型
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            share_intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            uri = FileProvider.getUriForFile(mContext, ProviderUtil.getFileProviderName(mContext), file);//android 7.0以上
            uri = getImageContentUri(context, file);
        } else {
//            uri = Uri.fromFile(file);
            uri = getImageContentUri(context, file);
        }
        share_intent.putExtra(Intent.EXTRA_STREAM, uri);
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "分享");
        context.startActivity(share_intent);
    }


    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
