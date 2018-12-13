package com.qunar.im.ui.view.recentView;

import android.content.Context;
import android.view.View;

import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.ui.R;

/**
 * Created by xinbo.wang on 2016-12-05.
 */
public class HeadLineRender implements IRecentRender {
    @Override
    public void render(CommonHolderView holder, RecentConversation data, final Context context) {
        holder.mLastMsgTextView.setText(data.getLastMsg());
    }
}
