package com.qunar.im.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.ui.util.easyVideo.CustomMedia.JZMediaIjk;
import com.qunar.im.ui.util.easyVideo.JZDataSource;
import com.qunar.im.ui.util.easyVideo.Jzvd;
import com.qunar.im.ui.util.easyVideo.JzvdStd;
import com.qunar.im.ui.util.easyphoto.easyphotos.EasyPhotos;
import com.qunar.im.ui.util.easyphoto.easyphotos.setting.Setting;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.DownLoadFileResponse;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.BinaryUtil;
import com.qunar.im.core.services.FileProgressResponseBody;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.HttpProxyUtil;
import com.qunar.im.ui.util.videoPlayUtil.VideoSetting;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.bigimageview.tool.utility.common.NetworkUtil;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;



public class VideoPlayActivity extends AppCompatActivity {

    private JzvdStd mJzvdStd;

    //    private String playPath;
    private String downloadPath;
    public static String PLAYPATH = "play_path";
    public static String PLAYTHUMB = "play_thumb";
    public static String DOWNLOADPATH = "download_path";
    public static String AUTOPLAY = "play_auto";
    public static String OPENFULL = "open_full";
    public static String SHOWSHARE = "show_share";
    public static String FILENAME = "file_name";

    private String fileName;
    private String playPath;

    protected CommonDialog.Builder commonDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playPath = VideoSetting.playPath;

        if(!TextUtils.isEmpty(VideoSetting.fileSize)) {

            boolean flag = DataUtils.getInstance(this).getPreferences("notRemindVideoSize", false);
            if (flag||(!VideoSetting.playPath.startsWith("http"))) {
                initPlay();
            } else {
                if(NetworkUtil.isWiFi(this)){
                    initPlay();
                }else {
                    commonDialog = new CommonDialog.Builder(this);
                    String size = "";
                    try {
                        size = ((float) (Long.parseLong(VideoSetting.fileSize) * 100 / 1024 / 1024) / 100 + "mb");

//                    size = String.format("%.2f",size);
                    } catch (Exception e) {
                        size = VideoSetting.fileSize;
                    }
                    commonDialog.setMessage("视频大小为" + size + ",请问继续播放么");
                    commonDialog.setPositiveButton(getString(R.string.atom_ui_common_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            dialog.dismiss();
                            initPlay();
//                        HttpUtil.serverCloseSession(getRealJid(), getFromId(), getToId(), new ProtocolCallback.UnitCallback<String>() {
//                            @Override
//                            public void onCompleted(String s) {
//                                toast(s);
//                                finish();
//                            }
//
//                            @Override
//                            public void onFailure(String errMsg) {
//                                toast("结束会话失败");
//                            }
//                        });

                        }
                    });
                    commonDialog.setNegativeButton(getString(R.string.atom_ui_common_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();

                        }
                    });
                    commonDialog.setNeutralButton(getString(R.string.atom_ui_btn_not_remind), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            DataUtils.getInstance(VideoPlayActivity.this).putPreferences("notRemindVideoSize", true);
                            initPlay();
                        }
                    });
                    commonDialog.create().show();
                }
            }
        }else{
            initPlay();
        }





    }

    private void initPlay() {
        try {
            HttpProxyCacheServer httpProxyCacheServer = HttpProxyUtil.getProxy(this);

            setContentView(R.layout.activity_ui_video_play);
            Jzvd.WIFI_TIP_DIALOG_SHOWED = true;
            mJzvdStd = findViewById(R.id.videoplayer);
            if (TextUtils.isEmpty(VideoSetting.playPath)) {
                Toast.makeText(this, "未提供正确的播放路径", Toast.LENGTH_SHORT).show();
                VideoSetting.clear();
                JzvdStd.releaseAllVideos();
                finish();
            }
            String proxyUrl = "";



            if (VideoSetting.showShare) {
                mJzvdStd.shareAndDownLoadLayout.setVisibility(View.VISIBLE);

            } else {
                mJzvdStd.shareAndDownLoadLayout.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(VideoSetting.fileName)) {
                fileName = BinaryUtil.MD5(QtalkStringUtils.findRealUrl(VideoSetting.playPath)) + ".MP4";
            } else {
                fileName = VideoSetting.fileName;
            }





            if (VideoSetting.onlyDownLoad) {

                if (VideoSetting.playPath.startsWith("http")) {
//                    proxyUrl = httpProxyCacheServer.getProxyUrl(playPath);
                    initVideo(playPath, fileName);
                } else {
                    proxyUrl = playPath;
                    initSource(proxyUrl);
                }


            } else {
                if (VideoSetting.playPath.startsWith("http")) {
                    proxyUrl = httpProxyCacheServer.getProxyUrl(playPath);
                } else {
                    proxyUrl = playPath;
                }


                initSource(proxyUrl);
            }


//        mJzvdStd.setUp("/storage/emulated/0/20190730165813439_SbEIV4_Screenrecorder-2018-12-12-19-_trans_F.mp4","",JzvdStd.SCREEN_NORMAL);
            mJzvdStd.finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoSetting.clear();
                    JzvdStd.releaseAllVideos();
                    finish();

                }
            });

//        mJzvdStd.backButton.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(VideoSetting.playThumb)) {
                String thumbUrl = VideoSetting.playThumb;
                if (thumbUrl.startsWith("http")) {
                    Glide.with(this).load(new MyGlideUrl(thumbUrl)).into(mJzvdStd.thumbImageView);
                } else {
                    Glide.with(this).load(thumbUrl).into(mJzvdStd.thumbImageView);
                }
            }

//        if (getIntent().hasExtra(PLAYTHUMB)) {
//            String thumbUrl = getIntent().getStringExtra(PLAYTHUMB);
//            if (!TextUtils.isEmpty(thumbUrl)) {
//                if (thumbUrl.startsWith("http")) {
//                    Glide.with(this).load(new MyGlideUrl(thumbUrl)).into(mJzvdStd.thumbImageView);
//                } else {
//                    Glide.with(this).load(thumbUrl).into(mJzvdStd.thumbImageView);
//                }
////                mJzvdStd.thumbImageView.setVisibility(View.VISIBLE);
////                mJzvdStd.mRetryLayout.setVisibility(View.VISIBLE);
//
//            }
//
//        }
            if (VideoSetting.showFull) {
                mJzvdStd.fullscreenButton.setVisibility(View.VISIBLE);
            } else {
                mJzvdStd.fullscreenButton.setVisibility(View.INVISIBLE);
            }
//        if (getIntent().hasExtra(OPENFULL)) {
//            if (getIntent().getBooleanExtra(OPENFULL, false)) {
//                mJzvdStd.fullscreenButton.setVisibility(View.VISIBLE);
//            } else {
//                mJzvdStd.fullscreenButton.setVisibility(View.INVISIBLE);
//            }
//        }

//        if (getIntent().hasExtra(AUTOPLAY)) {
//            if (getIntent().getBooleanExtra(AUTOPLAY, false)) {
//                mJzvdStd.startVideo();
//            }
//        }

            if (TextUtils.isEmpty(VideoSetting.downloadPath)) {
                downloadPath = VideoSetting.playPath;
            } else {
                downloadPath = VideoSetting.downloadPath;
            }
//        if (getIntent().hasExtra(DOWNLOADPATH)) {
//            downloadPath = getIntent().getStringExtra(DOWNLOADPATH);
//        } else {
//            downloadPath = playPath;
//        }
            if(playPath.startsWith("http")){
                mJzvdStd.downLoadButton.setVisibility(View.VISIBLE);
            }else{
                mJzvdStd.downLoadButton.setVisibility(View.GONE);
            }
            if (VideoSetting.videoDownLoadCallback != null) {
                mJzvdStd.downLoadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoSetting.videoDownLoadCallback.onClickDownLoad(v,playPath, downloadPath, fileName);
                    }
                });
            }

            if (VideoSetting.videoShareCallback != null) {
                mJzvdStd.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoSetting.videoShareCallback.onClickShare(v,playPath, downloadPath, fileName);
                    }
                });
            }
        } catch (Exception e) {
            JzvdStd.releaseAllVideos();
            Setting.clear();
            finish();
        }
    }

    private void initSource(String proxyUrl) {

        if (playPath.startsWith("http")) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//                        File file=new File(savePath,getNameFromUrl(url));
            File file = new File(path, fileName);
            if (file.exists()) {
                proxyUrl = file.getAbsolutePath();
            }
        }


        JZDataSource source = new JZDataSource(proxyUrl);
        source.looping = VideoSetting.looping;//循环播放
//        mJzvdStd.setUp("file:///android_asset/video.html", "", JzvdStd.SCREEN_NORMAL);
        if (proxyUrl.startsWith("http")) {
            mJzvdStd.setUp(source, JzvdStd.SCREEN_NORMAL, JZMediaIjk.class);
        } else {
            mJzvdStd.setUp(source, JzvdStd.SCREEN_NORMAL);
        }

        if (VideoSetting.autoPlay) {
            mJzvdStd.startVideo();
//            mJzvdStd.reset();
        }
    }

    private void initVideo(String downloadPath, String fileName) {
        final String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        final File file = new File(filepath, fileName);
        if (file.exists()) {
            Logger.i("文件已经下载完成:" + file.getAbsolutePath());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("文件已下载完成");
                    playPath = file.getAbsolutePath();
                    initSourceAndPlay(playPath);
                }
            });

        } else {
//            showToast("请稍等,文件准备中...");
            mJzvdStd.startButton.setVisibility(View.GONE);
            HttpUtil.fileDownload(downloadPath, fileName, new FileProgressResponseBody.ProgressResponseListener() {
                @Override
                public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
                    Logger.i("activity文件下载进度:bytesRead:" + bytesRead + ",contentLength:" + contentLength + ",done:" + done);
//
//                    new ProcessBuilder().
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(done){
                                mJzvdStd.loading_text.setVisibility(View.GONE);
                                mJzvdStd.loadingProgressBar.setVisibility(View.GONE);
                            }else{
                                mJzvdStd.loading_text.setVisibility(View.VISIBLE);
                                int i = (int) ((((double)bytesRead)/((double)contentLength))*100);
                                mJzvdStd.loading_text.setText(i+"%");
                                mJzvdStd.loadingProgressBar.setVisibility(View.VISIBLE);
//                                mJzvdStd.loadingProgressBar.setTooltipText();
                            }
                        }
                    });

                }
            }, new ProtocolCallback.UnitCallback<DownLoadFileResponse>() {
                @Override
                public void onCompleted(final DownLoadFileResponse videoDataResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mJzvdStd.startButton.setVisibility(View.VISIBLE);
                            Logger.i("文件下载完成");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    showToast("下载完成");
                                    EasyPhotos.notifyMedia(VideoPlayActivity.this, videoDataResponse.getFilePath());
                                    playPath = videoDataResponse.getFilePath();
                                    initSourceAndPlay(playPath);
                                }
                            });
                        }
                    });



                }

                @Override
                public void onFailure(String errMsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            showToast("下载失败");
                        }
                    });
                }
            });
        }
    }

    private void initSourceAndPlay(String playPath) {
        initSource(playPath);
        mJzvdStd.startVideo();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //设置横屏
//            Toast.makeText(VideoPlayActivity.this,"当前屏幕为横屏",Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(VideoPlayActivity.this, "当前屏幕为竖屏", Toast.LENGTH_SHORT).show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏

        }
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        VideoSetting.clear();
        JzvdStd.releaseAllVideos();
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //home back
//        JzvdStd.goOnPlayOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //     Jzvd.clearSavedProgress(this, null);
        //home back
        JzvdStd.goOnPlayOnPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, VideoPlayActivity.class);
        activity.startActivity(intent);
    }

    public static void start(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), VideoPlayActivity.class);
        fragment.startActivity(intent);
    }

    public static void start(android.support.v4.app.Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), VideoPlayActivity.class);
        fragment.startActivity(intent);

    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

//    private static void externalShare(File file,Context context) {
//        Intent share_intent = new Intent();
//        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
//        share_intent.setType("video/*");  //设置分享内容的类型
//        Uri uri;
//        if (Build.VERSION.SDK_INT >= 24) {
//            share_intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            uri = FileProvider.getUriForFile(mContext, ProviderUtil.getFileProviderName(mContext), file);//android 7.0以上
//            uri = getImageContentUri(context, file);
//        } else {
////            uri = Uri.fromFile(file);
//            uri = getImageContentUri(context, file);
//        }
//        share_intent.putExtra(Intent.EXTRA_STREAM, uri);
//        //创建分享的Dialog
//        share_intent = Intent.createChooser(share_intent, "分享");
//        context.startActivity(share_intent);
//    }
}
