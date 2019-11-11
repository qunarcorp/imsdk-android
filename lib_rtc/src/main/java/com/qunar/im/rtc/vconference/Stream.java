package com.qunar.im.rtc.vconference;

import org.webrtc.PeerConnection;

/**
 * Created by xinbo.wang on 2017-04-05.
 */
public class Stream {
    protected PeerConnection peerConnection;
    protected boolean localStream;
    protected IConferenceView view;
    protected boolean isDispose;
    public Stream(boolean isLocal,IConferenceView view)
    {
        this.localStream = isLocal;
        this.view = view;
    }

    public void updateMainSpeaker()
    {

    }

    public void dispose(){
        peerConnection.dispose();
        isDispose = true;
    }

    public boolean isDispose()
    {
        return isDispose;
    }

    public void subscribe(){

    }
}
