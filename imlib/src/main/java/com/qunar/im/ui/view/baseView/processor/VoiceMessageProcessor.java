package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.TransitSoundJSON;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.medias.play.PlayVoiceView;


/**
 * Created by zhaokai on 15-8-17.
 */
public class VoiceMessageProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        IMMessage message = item.getMessage();
        if (message.getDirection() == IMMessage.DIRECTION_RECV) {
            receiveVoice(parent, item);
        } else if (message.getDirection() == IMMessage.DIRECTION_SEND) {
            sendVoice(parent, item);
        }
    }


    /**
     * 处理发送的语音
     */
    private void sendVoice(final ViewGroup parent, final IMessageItem item) {
        final IMMessage message = item.getMessage();
        final ProgressBar progressBar = item.getProgressBar();
        final ImageView errorImage = item.getErrImageView();
        final Handler handler = item.getHandler();
        final Context context = item.getContext();
//        if (message.getMsgType() == MessageType.VOICE_MESSAGE) {
//        (!(MessageStatus.isExistStatus(message.getMessageState(),MessageStatus.LOCAL_STATUS_SUCCESS))&&(MessageStatus.isExistStatus(message.getMessageState(),MessageStatus.LOCAL_STATUS_PROCESSION))
            if ((MessageStatus.isProcession(message.getMessageState())&& progressBar!=null)) {
                progressBar.setVisibility(View.VISIBLE);
            } else if ((MessageStatus.isExistStatus(message.getMessageState(),MessageStatus.LOCAL_STATUS_SUCCESS) && progressBar!=null)) {
                progressBar.setVisibility(View.GONE);
            } else if (message.getMessageState()==0) {
                errorImage.setVisibility(View.VISIBLE);
            }
            TransitSoundJSON json = ChatTextHelper.turnText2SoundObj(message, true, new ChatTextHelper.DownloadVoiceCallback() {
                @Override
                public void onComplete(boolean isSuccess) {
                    if (isSuccess) {
                        message.setMessageState(MessageStatus.LOCAL_STATUS_SUCCESS);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(progressBar!=null){
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                        });

                    } else {
                        message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                errorImage.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                }
            });
            if (json == null) {
                TextView textView = new TextView(context);
                textView.setText(message.getBody());
                parent.addView(textView);
            } else {
                if (json.FileName != null && !json.FileName.equals("") && progressBar!=null)
                    progressBar.setVisibility(View.GONE);
                LogUtil.d("voice", json.FileName);
                PlayVoiceView view = ViewPool.getView(PlayVoiceView.class,context);
                view.init(json.FileName, json.Seconds, message, null);
                parent.addView(view);
            }
//        }
    }

    /**
     * 处理接收的语音
     */
    private void receiveVoice(final ViewGroup parent, final IMessageItem item) {
        final IMMessage message = item.getMessage();
        final ProgressBar progressBar = item.getProgressBar();
//        final ImageView errorImage = item.getErrImageView();
        final Handler handler = item.getHandler();
        final Context context = item.getContext();
        final TextView statusView = item.getStatusView();

//        if (message.getMsgType() == MessageType.VOICE_MESSAGE) {
            if(statusView != null){
                statusView.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(message.getFromID())) {
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
//                errorImage.setVisibility(View.VISIBLE);
                return;
            }

            if (MessageStatus.isProcession(message.getMessageState())) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (MessageStatus.isExistStatus(message.getMessageState(),MessageStatus.LOCAL_STATUS_SUCCESS)) {
                progressBar.setVisibility(View.GONE);
            } else if (message.getMessageState()==0) {
//                errorImage.setVisibility(View.VISIBLE);
            }
            TransitSoundJSON json = ChatTextHelper.turnText2SoundObj(message, false, new ChatTextHelper.DownloadVoiceCallback() {
                @Override
                public void onComplete(boolean isSuccess) {
                    if (isSuccess) {
                        message.setMessageState(MessageStatus.LOCAL_STATUS_SUCCESS);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                errorImage.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                errorImage.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            });
            if (json == null) {
                TextView textView = new TextView(context);
                textView.setText(message.getBody());
                parent.addView(textView);
            } else {
                LogUtil.d("voice", json.FileName);
                PlayVoiceView view = ViewPool.getView(PlayVoiceView.class,context);
                if (!TextUtils.isEmpty(json.FileName)) {
                    progressBar.setVisibility(View.GONE);
                }
                view.init(json.FileName, json.Seconds, message, null);
                view.initStatus(statusView,message);
                parent.addView(view);
            }
//        }
    }
}
