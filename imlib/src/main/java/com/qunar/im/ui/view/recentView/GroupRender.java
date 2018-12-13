package com.qunar.im.ui.view.recentView;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.Nick;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.core.manager.IMLogicManager;

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
        String atFrom = getAtOwnMessage(data);//处理展示at自己的消息
        if (!TextUtils.isEmpty(atFrom)) {
            ConnectionUtil.getInstance().getUserCard(atFrom, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    SpannableStringBuilder ssb = new SpannableStringBuilder("[" + nick.getName() + "@你]");
                    ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
                    ssb.setSpan(redSpan, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append(text);
                    holder.mLastMsgTextView.setText(ssb);
                }
            },false,false);

        }else {
            holder.mLastMsgTextView.setText(builder);
        }
    }

    private String getAtOwnMessage(RecentConversation recentConversation){
        Map<String,String> AtMessageMap = ConnectionUtil.getInstance().getAtMessageMap();
        if(AtMessageMap == null){
            return null;
        }
        //处理at消息
        String atMessage = AtMessageMap.get(recentConversation.getId());
        if(!TextUtils.isEmpty(atMessage)){
            String[] values = atMessage.split(",");
            if(values!=null && values.length>0){
                String data = values[0];
                if(!TextUtils.isEmpty(data)){
                    String[] ss = data.split(":");
                    if(ss != null && ss.length>0){
                        return  ss[0];
                    }
                }
            }
        }
        return null;
    }
}
