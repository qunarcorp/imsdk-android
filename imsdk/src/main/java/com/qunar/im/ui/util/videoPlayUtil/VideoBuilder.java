package com.qunar.im.ui.util.videoPlayUtil;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.qunar.im.ui.activity.VideoPlayActivity;

import java.lang.ref.WeakReference;

public class VideoBuilder {

    private static VideoBuilder instance;
    private WeakReference<Activity> mActivity;
    private WeakReference<Fragment> mFragmentV;


    //私有构造函数，不允许外部调用，真正实例化通过静态方法实现
    private VideoBuilder(FragmentActivity activity) {
        mActivity = new WeakReference<Activity>(activity);
    }

    private VideoBuilder(Fragment fragment) {
        mFragmentV = new WeakReference<Fragment>(fragment);
    }


    /**
     * 内部处理视频播放的实例
     *
     * @param activity Activity的实例
     * @return AlbumBuilder EasyPhotos的实例
     */
    private static VideoBuilder with(FragmentActivity activity) {
        clear();
        instance = new VideoBuilder(activity);
        return instance;
    }

    private static VideoBuilder with(Fragment fragmentV) {
        clear();
        instance = new VideoBuilder(fragmentV);
        return instance;
    }

    /**
     * 创建视频启动器
     *
     * @param activity 上下文
     * @return
     */
    public static VideoBuilder createVideoBuilder(FragmentActivity activity) {
        return VideoBuilder.with(activity);
    }

    public static VideoBuilder createVideoBuilder(Fragment fragmentV) {
        return VideoBuilder.with(fragmentV);
    }


    /**
     * 设置播放地址
     *
     * @return
     */
    public VideoBuilder setPlayPath(String playPath) {
        VideoSetting.playPath = playPath;
        return VideoBuilder.this;
    }


    /**
     * 设置文件大小
     * @param fileSize
     * @return
     */
    public VideoBuilder setFileSize(String fileSize){
        VideoSetting.fileSize = fileSize;
        return VideoBuilder.this;
    }

    /**
     * 是否循环播放
     * @param isCycle
     * @return
     */
    public VideoBuilder setIsCycle(boolean isCycle){
        VideoSetting.looping = isCycle;
        return VideoBuilder.this;
    }


    /**
     * 设置静态图
     *
     * @param playThumb
     * @return
     */
    public VideoBuilder setPlayThumb(String playThumb) {
        VideoSetting.playThumb = playThumb;
        return VideoBuilder.this;
    }

    /**
     * 设置下载地址
     *
     * @param downloadPath
     * @return
     */
    public VideoBuilder setDownLoadPath(String downloadPath) {
        VideoSetting.downloadPath = downloadPath;
        return VideoBuilder.this;
    }

    /**
     * 设置是否自动播放
     *
     * @param autoPlay
     * @return
     */
    public VideoBuilder setAutoPlay(boolean autoPlay) {
        VideoSetting.autoPlay = autoPlay;
        return VideoBuilder.this;
    }

    /**
     * 设置是否显示分享及下载
     *
     * @param showShare
     * @return
     */
    public VideoBuilder setShowShare(boolean showShare) {
        VideoSetting.showShare = showShare;
        return VideoBuilder.this;
    }


    /**
     * 设置文件名称
     *
     * @param fileName
     * @return
     */
    public VideoBuilder setFileName(String fileName) {
        VideoSetting.fileName = fileName;
        return VideoBuilder.this;
    }

    /**
     * 设置是否展示全屏按钮
     *
     * @param showFull
     * @return
     */
    public VideoBuilder setShowFull(boolean showFull) {
        VideoSetting.showFull = showFull;
        return VideoBuilder.this;
    }

    /**
     * 是否只有下载才播放
     * @param onlyDownLoad
     * @return
     */
    public VideoBuilder setOnlyDownLoad(boolean onlyDownLoad){
        VideoSetting.onlyDownLoad = onlyDownLoad;
        return VideoBuilder.this;
    }

    /**
     * 设置下载监听
     *
     * @param videoDownLoadCallback
     * @return
     */
    public VideoBuilder setDownLoadLis(VideoDownLoadCallback videoDownLoadCallback) {
        VideoSetting.videoDownLoadCallback = videoDownLoadCallback;
        return VideoBuilder.this;

    }

    /**
     * 设置分享监听
     *
     * @param videoShareCallback
     * @return
     */
    public VideoBuilder setShareLis(VideoShareCallback videoShareCallback) {
        VideoSetting.videoShareCallback = videoShareCallback;
        return VideoBuilder.this;
    }


    /**
     * 启动浏览器
     */
    public void start(){
        if (null != mActivity && null != mActivity.get() && mActivity.get() instanceof FragmentActivity) {
//            EasyResult.get((FragmentActivity) mActivity.get()).startEasyPhoto(callback);
            VideoPlayActivity.start(mActivity.get());
            return;
        }
        if (null != mFragmentV && null != mFragmentV.get()) {
            VideoPlayActivity.start(mFragmentV.get());
//            EasyResult.get(mFragmentV.get()).startEasyPhoto(callback);
            return;
        }
        throw new RuntimeException("mActivity or mFragmentV maybe null, you can not use this method... ");
    }

    /**
     * 清除所有数据
     */
    private static void clear() {
        VideoSetting.clear();
        instance = null;
    }


}
