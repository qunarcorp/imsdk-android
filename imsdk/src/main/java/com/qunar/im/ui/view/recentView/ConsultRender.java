package com.qunar.im.ui.view.recentView;

import android.content.Context;
import android.text.Html;

import com.qunar.im.base.module.RecentConversation;

/**
 * Created by xinbo.wang on 2016-12-05.
 */
public class ConsultRender implements IRecentRender{
    @Override
    public void render(CommonHolderView holder, RecentConversation data, Context context) {
        String lastmsg = data.getLastMsg();
        if(data.getRemind() > 0 && data.getUnread_msg_cont() > 0){
            lastmsg = "[" + data.getUnread_msg_cont() + "条]" + lastmsg;
        }
        holder.mLastMsgTextView.setText(Html.fromHtml(lastmsg));
//        ProfileUtils.displayGravatarByUserId(data.getId(), holder.mImageView);
//        //限制身份进行热线通话
//        if (data.isChan().indexOf("recv") != -1) {
//            if (CurrentPreference.getInstance().getIsIt()) {
//                //消息列表的UI展示限制
//                if (data.getRealUser().indexOf(CurrentPreference.getInstance().getUserId()) != -1) {
//                    ProfileUtils.loadNickName(QtalkStringUtils.parseBareJid(data.getId()),
//                            holder.mNameTextView,
//                            true );
//                    holder.mConsultTextView.setVisibility(View.GONE);
//                }else {
//                    ProfileUtils.loadNickName(QtalkStringUtils.parseBareJid(data.getRealUser()),
//                            holder.mNameTextView,
//                            true);
//                    holder.mConsultTextView.setVisibility(View.VISIBLE);
//                }
//            }else {
//                ProfileUtils.loadNickName(QtalkStringUtils.parseBareJid(data.getId()),
//                        holder.mNameTextView,
//                        true );
//                holder.mConsultTextView.setVisibility(View.GONE);
//            }
//        }else {
//            ProfileUtils.loadNickName(QtalkStringUtils.parseBareJid(data.getId()),
//                    holder.mNameTextView,
//                    true );
//            holder.mConsultTextView.setVisibility(View.GONE);
//        }

    }
}
