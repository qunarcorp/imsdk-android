package com.qunar.im.base.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.qunar.im.common.CommonConfig;

import java.io.IOException;

/**
 * Created by xinbo.wang on 2015/4/3.
 */
public class MediaUtils {
    static int MSM_SOUND_ID;
    static int SHAKE_SOUND_ID;
    static SoundPool soundPool;
    static MediaPlayer mediaPlayer;
    static AudioManager mAudioManager;
    static int MSG_VIDEO_TONE;
    static int WEBRTC_SOUND_ID;

    static int MAX_STREAMS = 3;

    static int MSG_SOUND_STREAM_ID;

    public static void loadNewMsgSound(Context context, int rid) {
        if(MSM_SOUND_ID == 0) {
            if (soundPool == null) {
                soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_RING, 5);
            }
            //加载短信声音
            MSM_SOUND_ID = soundPool.load(context, rid, 1);
            if (mAudioManager == null)
                mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        }
    }

    public static void loadShakeMsgSound(Context context,int resId){
        if(SHAKE_SOUND_ID == 0) {
            if (soundPool == null) {
                soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_RING, 5);
            }
            //加载短信声音
            SHAKE_SOUND_ID = soundPool.load(context, resId, 1);
            if (mAudioManager == null)
                mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        }
    }
    public static void playShakeMsgSound(Context context){
        if(SHAKE_SOUND_ID ==0)return;
        if (mAudioManager == null)
            mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        float cur = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        soundPool.play(SHAKE_SOUND_ID, cur, cur, 99, 0, 1);
    }

    public static void playNewMsgSound(Context context) {
        if(MSM_SOUND_ID==0) return;
        if(MSG_SOUND_STREAM_ID>0) soundPool.stop(MSG_SOUND_STREAM_ID);
        MSG_SOUND_STREAM_ID = 0;
        if(CommonConfig.isPlayVoice)
        {
            MSG_SOUND_STREAM_ID = soundPool.play(MSM_SOUND_ID, 1, 1, 1, 0, 1);
            return;
        }
        if (mAudioManager == null)
            mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        float cur = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        MSG_SOUND_STREAM_ID = soundPool.play(MSM_SOUND_ID, cur, cur, 99, 0, 1);
    }

    public static void unLoadNewMsgSound()
    {
        if(MSM_SOUND_ID >0) {
            soundPool.unload(MSM_SOUND_ID);
            MSM_SOUND_ID = 0;
        }
    }

    public static void loadRtcSound(Context context, int rid) {
        if(MSG_VIDEO_TONE == 0) {
            if (soundPool == null) {
                soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_RING, 5);
            }
            //加载短信声音
            MSG_VIDEO_TONE = soundPool.load(context, rid, 2);
            if (mAudioManager == null)
                mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        }
    }

    public static void playRtcTone(Context context)
    {
        if(MSG_VIDEO_TONE>0) {
            if (mAudioManager == null)
                mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
            float cur = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
            WEBRTC_SOUND_ID = soundPool.play(MSG_VIDEO_TONE, cur, cur, 99, -1, 1);
        }
    }

    public static void stopRtcTone()
    {
        if(WEBRTC_SOUND_ID >0) {
            soundPool.stop(WEBRTC_SOUND_ID);
            WEBRTC_SOUND_ID = 0;
        }
    }

    public static void playRtcSound(Context context, int rid) {
        if(mediaPlayer == null) {
//            mediaPlayer = MediaPlayer.create(context, rid);
//            ((Activity) context).setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            try {
                mediaPlayer = new MediaPlayer();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    mediaPlayer = new MediaPlayer();
                }
                AssetFileDescriptor file = context.getResources().openRawResourceFd(rid);
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                        file.getLength());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopRtcSound() {
        if(mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void unloadRtcTone()
    {
        if(MSG_VIDEO_TONE>0)
        {
            soundPool.unload(MSG_VIDEO_TONE);
            MSG_VIDEO_TONE = 0;
        }
    }

    public static void unLoadShakeSound()
    {
        if(SHAKE_SOUND_ID > 0) {
            soundPool.unload(SHAKE_SOUND_ID);
            SHAKE_SOUND_ID = 0;
        }
    }

    public static void changeMode(int mode,Context context)
    {
        if (mAudioManager == null)
            mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        if(mode == AudioManager.MODE_NORMAL) mAudioManager.setSpeakerphoneOn(true);
        else mAudioManager.setSpeakerphoneOn(false);
        mAudioManager.setMode(mode);
    }
}
