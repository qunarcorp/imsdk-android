package com.qunar.im.ui.view.recentView;

import android.content.Context;
import android.view.View;

import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.ui.R;

/**
 * Created by xinbo.wang on 2016-12-05.
 */
public class FriendRequestRender implements IRecentRender {
    @Override
    public void render(CommonHolderView holder, RecentConversation data, final Context context) {
        holder.mLastMsgTextView.setText(data.getLastMsg());
        if (data.isChan().indexOf("send") != -1) {
            holder.mNameTextView.setTag(null);
            holder.mNameTextView.setText(data.getFullname());
            holder.mConsultImageView.setVisibility(View.GONE);
        }
        FacebookImageUtil.loadFromResource(R.drawable.atom_ui_ic_friends_requests, holder.mImageView);
    }
}
