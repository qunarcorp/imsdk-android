package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;

/**
 * Created by zhaokai on 15-8-14.
 */
public abstract class DefaultMessageProcessor implements MessageProcessor {

    protected static final String TAG = DefaultMessageProcessor.class.getSimpleName();
//    protected TextView statusView;

    protected void sendNotify4Snap(Context context, IMMessage message) {
        MessageUtils.downloadAttachedComplete(context, message.getId());
    }

    @Override
    public void processProgressbar(ProgressBar progressBar, IMessageItem item) {
        if (progressBar != null) {
            if (item.getMessage().getMessageState() == MessageStatus.LOCAL_STATUS_PROCESSION) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void processErrorImageView(ImageView errImgView, IMessageItem item) {
        if (errImgView != null) {
            if (item.getMessage().getMessageState() == MessageStatus.LOCAL_STATUS_FAILED) {
                errImgView.setVisibility(View.VISIBLE);
            } else {
                errImgView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void processErrorSendingView(ProgressBar progressBar, ImageView errImgView, IMessageItem item) {
        if(progressBar!=null && errImgView !=null){

            if(MessageStatus.isExistStatus (item.getMessage().getMessageState(),MessageStatus.LOCAL_STATUS_SUCCESS )){
                progressBar.setVisibility(View.GONE);
                errImgView.setVisibility(View.GONE);
            }else if(MessageStatus.isExistStatus (item.getMessage().getMessageState(),MessageStatus.LOCAL_STATUS_PROCESSION )){
                progressBar.setVisibility(View.VISIBLE);
                errImgView.setVisibility(View.GONE);
            }else {
                progressBar.setVisibility(View.GONE);
                errImgView.setVisibility(View.VISIBLE);
            }

        }
    }


    @Override
    public void processSendStatesView(TextView textView, IMessageItem item) {
        if (textView != null) {

                int messageState = item.getMessage().getMessageState();

                if(MessageStatus.isExistStatus(messageState,MessageStatus.LOCAL_STATUS_SUCCESS)){
                    int readState = item.getMessage().getReadState();

                    if(  MessageStatus.isExistStatus(readState,MessageStatus.REMOTE_STATUS_CHAT_READED)){
                        textView.setText(R.string.atom_ui_new_message_read);
                        textView.setTextColor(Color.parseColor("#BFBFBF"));
                    }else if(  MessageStatus.isExistStatus(readState,MessageStatus.REMOTE_STATUS_CHAT_DELIVERED)){
                        textView.setText(R.string.atom_ui_new_message_unread);
                        textView.setTextColor(Color.parseColor("#00C1BA"));
                    }else{
                        textView.setText(R.string.atom_ui_new_message_unread);
                        textView.setTextColor(Color.parseColor("#00C1BA"));
                    }
                }else{
                    textView.setText("");
                }

//            int state = item.getMessage().getReadState();
//            if (state == MessageStatus.STATUS_SINGLE_DELIVERED) {
////                textView.setVisibility(View.VISIBLE);
//                textView.setText(R.string.atom_ui_no_read);
//                textView.setTextColor(ContextCompat.getColor(CommonConfig.globalContext, R.color.atom_ui_qchat_logo_color));
//            } else if (state == MessageStatus.STATUS_SUCCESS) {
////                textView.setVisibility(View.VISIBLE);
//
//            }
//            else if (state == MessageStatus.STATUS_DELIVERY) {
////                textView.setVisibility(View.VISIBLE);
//                textView.setText(R.string.atom_ui_no_read);
//                textView.setTextColor(ContextCompat.getColor(CommonConfig.globalContext, R.color.atom_ui_hongbao_bg));
//            }
//            else {
////                textView.setVisibility(View.GONE);
//
//            }
        }
    }

    @Override
    public void processTimeText(TextView timeTextView, IMessageItem item, ChatViewAdapter adapter) {
        boolean result = false;
        IMMessage message = item.getMessage();
        long currentTime = message.getTime().getTime();
        if (item.getPosition() == 0 && message.getDirection() != IMMessage.DIRECTION_MIDDLE) {
            result = true;
        } else if (item.getPosition() > 0) {
            if (currentTime <= 0) {
                currentTime = System.currentTimeMillis();
            }
            IMMessage premsg = adapter.getItem(item.getPosition() - 1);
            if (premsg.getDirection() == IMMessage.DIRECTION_MIDDLE) {
                result = true;
            } else {
                long previousTime = premsg.getTime().getTime();
                if (previousTime > 0 && (currentTime - previousTime) >= 60 * 5 * 1000) {
                    result = true;
                }
            }
        }
        if (result) {
            String date = DateTimeUtils.getTimeForSeesionAndChat(currentTime, true);
            timeTextView.setText(date);
            timeTextView.setVisibility(View.VISIBLE);
        } else {
            timeTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        TextView textView = ViewPool.getView(TextView.class, item.getContext());
        textView.setText(item.getMessage().getBody());
        parent.addView(textView);
    }

    @Override
    public void processStatusView(TextView textView) {
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item) {
        bubbleLayout.resetBubble(item.getMessage().getDirection()==IMMessage.DIRECTION_RECV);
    }
}