package com.qunar.im.ui.view.baseView.processor;

import androidx.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.bubbleLayout.BubbleLayout;

/**
 * Created by zhaokai on 15-8-14.
 */
public interface MessageProcessor {
    /**
     * @param progressBar 处理进度条
     **/
    void processProgressbar(ProgressBar progressBar, IMessageItem item);


    /**
     * @param timeTextView 时间框
     * @param adapter      当前view 所在的adapter 可以为空
     **/
    void processTimeText(TextView timeTextView, IMessageItem item, @Nullable ChatViewAdapter adapter);

    /**
     * @param errImgView 处理时间框
     **/
    void processErrorImageView(ImageView errImgView, IMessageItem item);

    /**
     * 处理进度条及发送失败情况
     * @param progressBar
     * @param errImgView
     * @param item
     */
    void processErrorSendingView(ProgressBar progressBar,ImageView errImgView, IMessageItem item);
    /**
     * @param parent 所有通过消息生成的View全部都add到这个ViewGroup中,将对消息处理复杂处理放在这个方法中
     **/
    void processChatView(ViewGroup parent, IMessageItem item);

    void processStatusView(TextView textView);

    /**
     * 处理消息背景
     * @param bubbleLayout
     * @param item
     */
    void processBubbleView(BubbleLayout bubbleLayout, IMessageItem item);

    /**
     * 消息阅读状态
     * @param textView
     * @param item
     */
    void processSendStatesView(TextView textView,IMessageItem item);
}