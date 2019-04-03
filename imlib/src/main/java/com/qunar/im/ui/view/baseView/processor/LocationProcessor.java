package com.qunar.im.ui.view.baseView.processor;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ShareLocationActivity;
import com.qunar.im.base.jsonbean.ShareLocationExtendInfo;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ViewPool;

/**
 * Created by zhaokai on 16-2-17.
 * 共享实时位置的Processor ,区别于发送位置的Processor
 */
public class LocationProcessor extends DefaultMessageProcessor {

    private final String PROMT = "发起了位置共享";

    @Override
    public void processChatView(ViewGroup parent, final IMessageItem item) {
        final TextView textView = ViewPool.getView(TextView.class, item.getContext());
        final IMMessage message = item.getMessage();
        if (message.getDirection() == IMMessage.DIRECTION_RECV) {
            String fromId = JsonUtils.getGson().fromJson(message.getExt(), ShareLocationExtendInfo.class).fromId;
            ProfileUtils.loadNickName(fromId, false, new ProfileUtils.LoadNickNameCallback() {
                @Override
                public void finish(String name) {
                    textView.setText(name + PROMT);
                }
            });
        } else if (message.getDirection() == IMMessage.DIRECTION_SEND) {
            textView.setText(item.getContext().getString(R.string.atom_ui_mine_title_me)+ PROMT);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(item.getContext(), ShareLocationActivity.class);
                String ext = message.getExt();
                ShareLocationExtendInfo info = JsonUtils.getGson().fromJson(ext, ShareLocationExtendInfo.class);
                intent.putExtra(ShareLocationActivity.SHARE_ID, info.shareId);
                intent.putExtra(ShareLocationActivity.FROM_ID, info.fromId);
                item.getContext().startActivity(intent);
            }
        });
        parent.addView(textView);
    }
}