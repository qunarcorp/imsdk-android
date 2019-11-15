package com.qunar.im.ui.util;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.qunar.im.ui.util.easyphoto.easyphotos.EasyPhotos;
import com.qunar.im.ui.util.easyphoto.easyphotos.callback.SelectCallback;
import com.qunar.im.ui.util.easyphoto.easyphotos.callback.VideoPlayCallback;
import com.qunar.im.ui.util.easyphoto.easyphotos.constant.Type;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;
import com.qunar.im.base.module.ImageItem;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.PhotoUtil;
import com.qunar.im.ui.util.videoPlayUtil.VideoPlayUtil;

public class ImageSelectUtil {


    private static long ltime;
    private static long lsize;
    private static long maxlTime;


    public static void initVideoSetting() {
        String time = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + "videoTime");

        String size = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + "videoSize");

        String maxTime = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + "videoMaxTime");

        if (!TextUtils.isEmpty(time)) {
            ltime = (Long.parseLong(time) / 1000);
        } else {
            ltime = 16;
        }

        if (!TextUtils.isEmpty(maxTime)) {
            maxlTime = (Long.parseLong(maxTime) / 1000);
        } else {
            maxlTime = 300;
        }


        if (!TextUtils.isEmpty(size)) {
            lsize = Long.parseLong(size);
        } else {
            lsize = 30 * 1024 * 1024;
        }

    }

    public static boolean checkVideo(String url){
        initVideoSetting();
        boolean check = true;
       Photo p = PhotoUtil.getPhoto(url);
       if(p!=null){
           if(p.size>lsize){
               check = false;
           }
           if((p.duration/1000)>ltime){
               check =false;
           }
       }else{
           check=false;
       }

       return check;
    }


    public static void startSelectPhotos(final FragmentActivity mActivity, SelectCallback selectCallback) {

        initVideoSetting();
        EasyPhotos.createAlbum(mActivity, false, ImageSelectGlideEngine.getInstance())
                .setCount(9)
                .setGif(true)
                .filter(Type.all())
                .setVideoPlayModule(new VideoPlayCallback() {
                    @Override
                    public void onVideoPlayResult(View v, String path, String type) {
                        VideoPlayUtil.openLocalVideo(mActivity, path, "", path);
//                        Intent intent = new Intent(mActivity, VideoPlayActivity.class);
//                        intent.putExtra(VideoPlayActivity.PLAYPATH,path);
//                        intent.putExtra(VideoPlayActivity.PLAYTHUMB,path);
//                        intent.putExtra(VideoPlayActivity.AUTOPLAY,true);
//                        intent.putExtra(VideoPlayActivity.OPENFULL,false);
//
//                        mActivity.startActivity(intent);
                    }
                })
//                .setVideoMaxSecond((int) ltime)
//                .setMaxFileSize(500*1024*1024)
                .start(selectCallback);
    }

    public static void startSelectWorkWorld(FragmentActivity mActivity, SelectCallback selectCallback) {
        initVideoSetting();
        EasyPhotos.createAlbum(mActivity, false, ImageSelectGlideEngine.getInstance())
//                .setCount(9)
//                .setPictureCount(9)
//                .setVideoCount(1)
//                .setisSeparate(true)
                .setGif(true)
//                .setIsVideoDone(true)
                .filter(Type.all())
//                .setSelectMutualExclusion(true)
                .start(selectCallback);
    }

    public static void startSelectWorkWorld(final FragmentActivity mActivity, int count, boolean openVideo, SelectCallback selectCallback) {
        initVideoSetting();
        if (openVideo) {
            EasyPhotos.createAlbum(mActivity, false, ImageSelectGlideEngine.getInstance())
//                .setCount(count)
                    .setPictureCount(count)
                    .setVideoCount(1)
//                .setCount(9)
//                .setPictureCount(9)
//                .setVideoCount(1)
//                .setisSeparate(true)
                    .setGif(true)
                    .setVideoMaxSecond((int) maxlTime)
//                    .setMaxFileSize(lsize)
//                .setIsVideoDone(true)
                    .filter(Type.all())
                    .setSelectMutualExclusion(true)
                    .setIsDistinguish(true)
                    .setVideoPlayModule(new VideoPlayCallback() {
                        @Override
                        public void onVideoPlayResult(View v, String path, String type) {
                            VideoPlayUtil.openLocalVideo(mActivity, path, "", path);
//                            Intent intent = new Intent(mActivity, VideoPlayActivity.class);
//                            intent.putExtra(VideoPlayActivity.PLAYPATH,path);
//                            intent.putExtra(VideoPlayActivity.PLAYTHUMB,path);
//                            intent.putExtra(VideoPlayActivity.AUTOPLAY,true);
//                            intent.putExtra(VideoPlayActivity.OPENFULL,false);
//                            mActivity.startActivity(intent);
                        }
                    })
                    .start(selectCallback);
        } else {
            EasyPhotos.createAlbum(mActivity, false, ImageSelectGlideEngine.getInstance())
                    .setCount(count)
                    .setGif(true)
                    .setMaxFileSize(30 * 1024 * 1024)
                    .filter(Type.image())
                    .start(selectCallback);
        }

    }

    public static void startSelectOnlyPhotos(int count, Activity mActivity, SelectCallback selectCallback) {
//        EasyPhotos.createAlbum(mActivity,false,ImageSelectGlideEngine.getInstance())
//                .setCount(count)
//                .setGif(true)
//                .start(selectCallback);
    }

    public static void startSelectOnlyVideo(Activity mActivity, SelectCallback selectCallback) {
//        EasyPhotos.createAlbum(mActivity,false,ImageSelectGlideEngine.getInstance())
//                .setCount(1)
//                .setVideo(true)
//                .filter(Type.VIDEO)
//                .start(selectCallback);
    }

    public static ImageItem parseImageItemForPhotos(Photo photo) {
        ImageItem imageItem = new ImageItem();
        imageItem.mimeType = photo.type;
        imageItem.name = photo.name;
        imageItem.path = photo.path;
        imageItem.size = photo.size;
        imageItem.height = photo.height;
        imageItem.width = photo.width;

        return imageItem;
    }

}
