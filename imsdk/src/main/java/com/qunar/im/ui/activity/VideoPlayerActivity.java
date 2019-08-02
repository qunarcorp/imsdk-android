package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.qunar.im.base.common.CommonDownloader;
import com.qunar.im.base.jsonbean.DownloadImageResult;
import com.qunar.im.base.protocol.ProgressResponseListener;
import com.qunar.im.base.transit.DownloadRequest;
import com.qunar.im.base.transit.IDownloadRequestComplete;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.NetworkUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.medias.video.MediaController;
import com.qunar.im.ui.view.medias.video.VideoView;
import com.qunar.im.ui.view.progressbarview.CircleView;

import java.io.File;
import java.lang.ref.WeakReference;

public class VideoPlayerActivity extends IMBaseActivity {

    private Uri mVideoUri;
    private String videoFIleName;
    public VideoView videoView;
    static MediaController mediaController;

    @SuppressLint("HandlerLeak")
    protected static class VideoHandler extends Handler
    {
        WeakReference<VideoPlayerActivity> weakReference;
        public VideoHandler(WeakReference<VideoPlayerActivity> w)
        {
            weakReference = w;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DOWNLOAD_FINISH:
                    VideoPlayerActivity activity = weakReference.get();
                    activity.pb_central.setVisibility(View.GONE);
                    String fileUri = msg.getData().getString(URL_KEY);
                    activity.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    activity.videoView.setVideoURI(FileUtils.toUri(fileUri));
                    activity.videoView.requestFocus();
                    break;
                case UPDATE_PROGRESS:
                    int progress = msg.getData().getInt(PROGRESS);
                    weakReference.get().pb_central.setProgress(progress);
                    break;
            }
        }
    }

    private Handler mHandler;
    private static final int DOWNLOAD_FINISH = 1000;
    private static final int UPDATE_PROGRESS = 1001;
    private static final String URL_KEY = "mNavUrl";
    private static final String PROGRESS = "progress";
    public CircleView pb_central;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_video_player_actvity);
        mVideoUri = getIntent().getData();
        videoFIleName = getIntent().getStringExtra(Constants.BundleKey.FILE_NAME);
        mHandler = new VideoHandler(new WeakReference<VideoPlayerActivity>(this));
        bindViews();
        playVideo();
    }

    private void bindViews()
    {
//        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
//        setNewActionBar(actionBar);
//        setActionBarTitle(R.string.atom_ui_play_video);
        videoView = (VideoView) this.findViewById(R.id.videoView);
        pb_central = (CircleView) this.findViewById(R.id.pb_central);
        findViewById(R.id.video_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoView);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(videoView!=null)
        {
            videoView.continuePlay();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(videoView!=null)
        {
            videoView.pause();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration)
    {
        super.onConfigurationChanged(configuration);
        if(configuration.orientation  ==Configuration.ORIENTATION_LANDSCAPE )
        {
            mNewActionBar.setVisibility(View.GONE);
            fullScreen();
        }
        else {
            mNewActionBar.setVisibility(View.VISIBLE);
            exitFullScreen();
        }
    }

    private void fullScreen()
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void exitFullScreen()
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void playVideo(){
        if(mVideoUri == null) return;
        pb_central.setVisibility(View.VISIBLE);

        String fileName = videoFIleName;
        if(TextUtils.isEmpty(fileName))
            fileName = mVideoUri.toString().
                    substring(mVideoUri.toString().lastIndexOf("/") + 1);
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES);
         final String savePath = path.getPath() +"/"+fileName;
        File file = new File(savePath);
        if(!file.exists()&& !NetworkUtils.isWifi(this))
        {
            commonDialog
                    .setMessage(R.string.atom_ui_wifi_prompt)
                    .setNegativeButton(R.string.atom_ui_common_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setPositiveButton(R.string.atom_ui_common_goon, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            downloadVideo(savePath);

                        }
                    })
                    .show();
//            dialog.show();
            return;
        }
        downloadVideo(savePath);
    }

    private void downloadVideo(final String savePath)
    {
        final DownloadRequest request = new DownloadRequest();
        request.savePath = savePath;
        request.url = mVideoUri.toString();
        request.requestComplete = new IDownloadRequestComplete() {
            @Override
            public void onRequestComplete(DownloadImageResult result) {
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_FINISH;
                Bundle b = new Bundle();
                b.putString(URL_KEY,savePath);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        };
        request.progressListener = new ProgressResponseListener() {
            @Override
            public void onResponseProgress(long bytewriten, long length, boolean complete) {
                int current = (int) (bytewriten*100/length);
                Message msg = mHandler.obtainMessage();
                msg.what = UPDATE_PROGRESS;
                Bundle b = new Bundle();
                b.putInt(PROGRESS,current);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        };
        CommonDownloader.getInsatnce().setDownloadRequest(request);
    }
}
