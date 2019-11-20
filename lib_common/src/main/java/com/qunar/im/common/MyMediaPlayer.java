package com.qunar.im.common;

import android.media.MediaPlayer;


/**
 * Created by xinbo.wang on 16-9-21
 */
public class MyMediaPlayer extends MediaPlayer {
    private volatile static MyMediaPlayer instance;

    protected MyMediaPlayer()
    {
    }



    public static MyMediaPlayer getInstance() {
        if(instance == null)
        {
            instance = createInstance();
        }
        return instance;
    }

    protected static synchronized MyMediaPlayer createInstance()
    {
        if(instance==null)
        {
            instance = new MyMediaPlayer();
        }
        return instance;
    }
}