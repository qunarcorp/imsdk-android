package com.qunar.im.ui.util.easyphoto.easyphotos.utils;

import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.media.DurationUtils;

import java.io.File;

public class PhotoUtil {

    public static Photo getPhoto(String filePath) {
        File file =null;
        if(TextUtils.isEmpty(filePath)){
            return null;
        }else{
            file = new File(filePath);
            if(!file.exists()){
                return null;
            }
        }
        boolean isVideo;
        int outWidth;
        int outHeight;
        String outMimeType;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(file.getAbsolutePath());
            outMimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            outWidth = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            outHeight = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            isVideo = true;
        } catch (Exception e) {
            e.printStackTrace();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            outMimeType = options.outMimeType;
            outWidth = options.outWidth;
            outHeight = options.outHeight;
            isVideo = false;
        }
        //if (code == Code.REQUEST_CAMERA) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss", Locale.getDefault());
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

//        String imageName;
//        if (isVideo) {
//            imageName = "VIDEO_%s" + suffix;
//        } else {
//            imageName = "IMG_%s" + suffix;
//        }

//        String filename = String.format(imageName, dateFormat.format(new Date()));
//        File reNameFile = new File(file.getParentFile(), filename);
//        if (!reNameFile.exists()) {
//            if (file.renameTo(reNameFile)) {
//                file = reNameFile;
//            }
//        }
        //}

        Photo photo = new Photo(file.getName(), file.getAbsolutePath(), file.lastModified() / 1000, outWidth, outHeight, file.length(), DurationUtils.getDuration(file.getAbsolutePath()), outMimeType);

        return photo;
    }
}
