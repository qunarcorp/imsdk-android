package com.qunar.im.ui.view.medias.play.interfaces;

/**
 * Created by zhaokai on 4/22/15.
 */
public interface IMediaPlayerCallback {
    boolean doBeforeStartPlaying();
    boolean doAfterStartPlaying();
    boolean doCompletePlaying();
}
