package com.qunar.im.ui.view.recentView;

import android.content.Context;
import android.view.View;

import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.ui.R;

/**
 * Created by xinbo.wang on 2016-12-05.
 */
public class RobotRender implements IRecentRender {
    @Override
    public void render(final CommonHolderView holder, final RecentConversation data, final Context context) {
        String id = data.getId();
        if(id == null) return;
        if(id.startsWith("rbt-system")){
            holder.mLastMsgTextView.setText("[系统消息]");
        }else if(id.startsWith("rbt-notice")){
            holder.mLastMsgTextView.setText("[公告消息]");
        }else if(id.startsWith("rbt-qiangdan")) {
            holder.mLastMsgTextView.setText("[抢单消息]");
        }
    }
}
