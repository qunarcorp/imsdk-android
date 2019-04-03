package com.qunar.im.ui.view.medias.play;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.IHandleVoiceMsgPresenter;
import com.qunar.im.ui.presenter.impl.HandleVoiceMsgPresenter;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.TransitSoundJSON;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.MediaUtils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.common.MyMediaPlayer;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.events.VoiceViewEvent;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by zhaokai on 15-4-22.
 */
public class MediaPlayerImpl {

    private static final String TAG = "MediaPlayerImpl";

    private MediaPlayer player = null;
    private String path = null;

    private boolean isPlaying;

    public String msgId="";

    private volatile static MediaPlayerImpl instance;

    private static synchronized void createInstance()
    {
        if(instance==null)
        {
            instance = new MediaPlayerImpl();
        }
    }
    public static MediaPlayerImpl getInstance() {
        if(instance == null)
        {
            createInstance();
        }
        return instance;
    }

    public void overPlay()
    {
        msgId = "";
        isPlaying = false;
    }


    private MediaPlayerImpl() {
        player = MyMediaPlayer.getInstance();
        player.setLooping(false);
        /*if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return;
        }

        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");

            Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});

            Object subtitleInstance = constructor.newInstance(QunarIMApp.getContext(), null, null);

            Field f = cSubtitleController.getDeclaredField("mHandler");

            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return;
            } finally {
                f.setAccessible(false);
            }

            Method setsubtitleanchor = player.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);

            setsubtitleanchor.invoke(player, subtitleInstance, null);
        } catch (Exception e) {
            LogUtil.e(TAG,"MediaPlayerImpl error at constructor");
        }*/
    }

    public void changeVoiceCall(Context context)
    {
        player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            MediaUtils.changeMode(AudioManager.MODE_IN_COMMUNICATION, context);
        else
            MediaUtils.changeMode(AudioManager.MODE_IN_CALL,context);

        ((Activity) context).setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        if(CommonConfig.isPlayVoice) {
            reset();
            startPlay();
        }
    }

    public void changeNormalCall(Context context)
    {
        player.setAudioStreamType(AudioManager.STREAM_SYSTEM);
        MediaUtils.changeMode(AudioManager.MODE_NORMAL, context);
        ((Activity)context).setVolumeControlStream(AudioManager.STREAM_SYSTEM);
        if(CommonConfig.isPlayVoice) {
            reset();
            startPlay();
        }
    }

    public void reset()
    {
        try {
            player.stop();
            player.setOnCompletionListener(null);
            player.reset();
            player.setOnCompletionListener(listener);
        }
        catch (Exception e)
        {
            LogUtil.e(TAG,"ERROR",e);
        }
    }


    public void startPlay() {
        try {
            CommonConfig.isPlayVoice = true;
            isPlaying= true;
            EventBus.getDefault().post(new VoiceViewEvent(msgId));
            player.setDataSource(path);
            player.prepare();
            player.start();

        } catch (Exception e) {
            /**
             * modify by xinbo.wang , 保证控件的稳定性，最好有exception，此处没有分类异常，更好的办法是分类异常。
             */
            LogUtil.e(TAG, "set data source exception", e);
        }
    }

    public void stopPlay() {
        if (player.isPlaying()) {
            try {
                CommonConfig.isPlayVoice = false;
                player.stop();
                isPlaying= false;
            } catch (Exception e) {
                LogUtil.e(TAG, "set data source exception", e);
            }
        }
    }

    public void release(){
        try
        {
            stopPlay();
            shutdown();
        }
        catch (Exception e)
        {
            LogUtil.e(TAG,"ERROR",e);
        }
    }

    public boolean isPlaying()
    {
        return isPlaying;
    }

    public void setResourceFilePath(String path,String id) {
        this.path = path;
        this.msgId = id;
    }

    public int getDuration() {
        int duration = -1;
        if (player != null) {
            if (isPlaying) {
                return player.getDuration();
            }
            player.reset();
            try {
                player.setDataSource(path);
                player.prepare();
                duration = player.getDuration();
            } catch (Exception e) {
                LogUtil.e(TAG, "set source exception", e);
            }
        }
        return duration;
    }
    MediaPlayer.OnCompletionListener listener = null;
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        LogUtil.d("PlayVoiceView", "setOnCompletionListener");
        this.listener = listener;
        player.setOnCompletionListener(listener);
    }

    IHandleVoiceMsgPresenter handleVoiceMsgPresenter = null;
    protected void shutdown()
    {
        BackgroundExecutor.cancelAll("playvoice_playlist",true);
        if(handleVoiceMsgPresenter!=null) {
            handleVoiceMsgPresenter.shutdown();
            handleVoiceMsgPresenter = null;
        }
    }
    protected void playList(long time, String convId, final Context context)
    {
        handleVoiceMsgPresenter = new HandleVoiceMsgPresenter();
        handleVoiceMsgPresenter.start(time-1,convId);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "link play");
                final IMMessage msg = handleVoiceMsgPresenter.next();
                if (msg == null) {
                    handleVoiceMsgPresenter.shutdown();
                    handleVoiceMsgPresenter = null;
                    return;
                }
                TransitSoundJSON json = ChatTextHelper.turnText2SoundObj(msg, false, new ChatTextHelper.DownloadVoiceCallback() {
                    @Override
                    public void onComplete(boolean isSuccess) {
                        if (isSuccess) {
                            msg.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);

                        } else {
                            msg.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
                        }
                    }
                });

                if (json == null || TextUtils.isEmpty(json.FileName)) {
                    player.reset();
                } else {

                    File file = new File(json.FileName);
                    while (!file.exists())
                    {
                        SystemClock.sleep(10);
                    }
                    reset();
                    setResourceFilePath(json.FileName, msg.getId());

                    startPlay();
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Update_Voice_Message,msg);
                    EventBus.getDefault().post(new EventBusEvent.UpdateVoiceMessage(msg));
                }

            }
        };
        listener = new MediaPlayer.OnCompletionListener() {//设置播放完成的监听器
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtil.d(TAG, "onCompletion");
                overPlay();
                CommonConfig.isPlayVoice = false;
                changeNormalCall(context);
                BackgroundExecutor.execute(runnable,"playvoice_playlist","");

            }
        };
        BackgroundExecutor.execute(runnable,"playvoice_playlist","");
    }
}