package com.qunar.im.ui.view.recentView;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.qunar.im.base.jsonbean.AtInfo;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.structs.MessageStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2016-12-05.
 */
public class GroupRender implements IRecentRender {
    @Override
    public void render(final CommonHolderView holder, final RecentConversation data, final Context context) {
        String lastmsg;
        if(TextUtils.isEmpty(data.getFullname())){
            lastmsg = data.getLastMsg();
        }else{
            lastmsg = data.getFullname()+":"+data.getLastMsg();
        }
        if(data.getRemind() > 0 && data.getUnread_msg_cont() > 0){
            lastmsg = "[" + data.getUnread_msg_cont() + "条]" + lastmsg;
        }
        String messageState = data.getLastState();
        SpannableStringBuilder builder = null;

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
        final String text = builder.toString();
        String atShow = getAtOwnMessage(data);//处理展示at自己的消息
        if (!TextUtils.isEmpty(atShow)) {
//            ConnectionUtil.getInstance().getUserCard(atFrom, new IMLogicManager.NickCallBack() {
//                @Override
//                public void onNickCallBack(Nick nick) {
//                    SpannableStringBuilder ssb = new SpannableStringBuilder("[" + nick.getName() + "@你]");
//                    ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
//                    ssb.setSpan(redSpan, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    ssb.append(text);
//                    holder.mLastMsgTextView.setText(ssb);
//                }
//            },false,false);

            SpannableStringBuilder ssb = new SpannableStringBuilder(atShow);
            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor("#EB524A"));
            ssb.setSpan(redSpan, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(text);
            holder.mLastMsgTextView.setText(ssb);

        }else {
            holder.mLastMsgTextView.setText(builder);
        }
    }

    private String getAtOwnMessage(RecentConversation recentConversation){
        Map<String,List<AtInfo>> AtMessageMap = ConnectionUtil.getInstance().getAtMessageMap();
        if(AtMessageMap == null){
            return null;
        }
        //处理at消息
        List<AtInfo> atMessages = AtMessageMap.get(recentConversation.getId());
        if(!ListUtil.isEmpty(atMessages)){
            for(AtInfo atInfo : atMessages){
                if(!atInfo.isAtAll){
                    return "[有人@我]";
                }
            }
            return "[@all]";
        }else {
            return null;
        }
    }
}
