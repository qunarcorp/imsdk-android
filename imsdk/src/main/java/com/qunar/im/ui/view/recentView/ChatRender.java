package com.qunar.im.ui.view.recentView;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.structs.MessageStatus;


/**
 * Created by xinbo.wang on 2016-12-05.
 */
public class ChatRender implements IRecentRender {
    @Override
    public void render(final CommonHolderView holder, final RecentConversation data, final Context context) {
        String lastmsg = data.getLastMsg();
        if(data.getRemind() > 0 && data.getUnread_msg_cont() > 0){
            lastmsg = "[" + data.getUnread_msg_cont() + "条]" + lastmsg;
        }
        SpannableStringBuilder builder = null;

        String messageState = data.getLastState();
        if(TextUtils.isEmpty(messageState)){
            builder = new SpannableStringBuilder(lastmsg);
        }else{
            if(MessageStatus.isExistStatus(Integer.parseInt(messageState),MessageStatus.LOCAL_STATUS_SUCCESS)){
                builder = new SpannableStringBuilder(lastmsg);
            }else if (MessageStatus.isExistStatus(Integer.parseInt(messageState),MessageStatus.LOCAL_STATUS_PROCESSION)){
                builder = new SpannableStringBuilder("<<-- ");
                ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.GRAY);
                builder.setSpan(redSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(lastmsg);
            }else {
                builder = new SpannableStringBuilder("！ ");
                ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
                builder.setSpan(redSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(lastmsg);
            }
        }


//        if(TextUtils.isEmpty(data.getLastState())){
//
//        }else if(Integer.valueOf(data.getLastState()) == MessageStatus.STATUS_FAILED){
//            builder = new SpannableStringBuilder("！ ");
//            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
//            builder.setSpan(redSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.append(lastmsg);
//        }else if(Integer.valueOf(data.getLastState()) == MessageStatus.STATUS_PROCESSION){
//            builder = new SpannableStringBuilder("<<-- ");
//            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.GRAY);
//            builder.setSpan(redSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.append(lastmsg);
//        }else{
//            builder = new SpannableStringBuilder(lastmsg);
//        }

        holder.mLastMsgTextView.setText(builder);
//        ProfileUtils.displayGravatarByUserId(data.getId(), holder.mImageView);
//        if (data.isChan().indexOf("send") != -1) {
//            ProfileUtils.loadNickName(QtalkStringUtils.parseBareJid(data.getId()),
//                    holder.mNameTextView,
//                    true );
//            holder.mConsultTextView.setVisibility(View.GONE);
//        }
    }
}
