package com.qunar.im.ui.util.videoPlayUtil;

public class VideoSetting {

//    public static String PLAYPATH = "play_path";
//    public static String PLAYTHUMB ="play_thumb";
//    public static String DOWNLOADPATH="download_path";
//    public static String AUTOPLAY = "play_auto";
//    public static String OPENFULL = "open_full";
//    public static String SHOWSHARE="show_share";
//    public static String FILENAME = "file_name";

    public static String fileSize = "";
    public static String playPath = "";
    public static String playThumb = "";
    public static String downloadPath = "";
    public static boolean autoPlay = false;
    public static boolean showShare = false;
    public static String fileName = "";
    public static boolean showFull = false;
    public static boolean looping = false;
    public static boolean onlyDownLoad = false;
    public static VideoDownLoadCallback videoDownLoadCallback = null;
    public static VideoShareCallback videoShareCallback = null;


    public static void clear() {
        looping = false;
        onlyDownLoad = false;
        videoDownLoadCallback = null;
        videoShareCallback = null;
        playPath = "";
        playThumb = "";
        downloadPath = "";
        fileSize="";
        autoPlay = false;
        showShare = false;
        fileName = "";
        showFull = false;
    }
}
