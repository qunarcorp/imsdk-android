package com.qunar.im.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.base.module.RobotConversation;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.ResourceUtils;

import java.util.List;

/**
 * Created by xinbo.wang on 2015/2/9.
 */
public class RobotConvAdapter extends BaseAdapter {
    List<RobotConversation> recentConversationList;
    Context context;
    public RobotConvAdapter(Context cxt) {
        context = cxt;
    }

    public void setRecentConversationList(List<RobotConversation> recentConversationList) {
        this.recentConversationList = recentConversationList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (recentConversationList == null)
            return 0;
        return recentConversationList.size();
    }

    @Override
    public RobotConversation getItem(int position) {
        if (recentConversationList == null || position >= recentConversationList.size())
            return null;
        return recentConversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.atom_ui_item_robot_conv, parent, false);
            holder.mNameTextView = (TextView) convertView.findViewById(android.R.id.text1);
            holder.mImageView = (SimpleDraweeView) convertView.findViewById(R.id.conversation_gravantar);
            holder.mTimeTextView = (TextView) convertView.findViewById(R.id.textview_time);
            holder.mLastMsgTextView = (TextView) convertView.findViewById(android.R.id.text2);
            holder.mNotifyTextView = (TextView) convertView.findViewById(R.id.textView_new_msg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int fontSizeMode = com.qunar.im.protobuf.common.CurrentPreference.getInstance().getFontSizeMode();
        float text1FontSize=context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_large);
        float textViewTimeFontSize=context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_smaller);
        float text2FontSize=context.getResources().getDimensionPixelSize(R.dimen.atom_ui_text_size_small);
        switch (fontSizeMode)
        {
            case 1://small font size
                text1FontSize-=ResourceUtils.getFontSizeIntervalPX(context);
                textViewTimeFontSize-=ResourceUtils.getFontSizeIntervalPX(context);
                text2FontSize-=ResourceUtils.getFontSizeIntervalPX(context);
                break;
            case 2://middle font size
                break;
            case 3://big font size
                text1FontSize+= ResourceUtils.getFontSizeIntervalPX(context);
                textViewTimeFontSize+=ResourceUtils.getFontSizeIntervalPX(context);
                text2FontSize+=ResourceUtils.getFontSizeIntervalPX(context);
                break;
        }
        holder.mNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,text1FontSize);
        holder.mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textViewTimeFontSize);
        holder.mLastMsgTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,text2FontSize);
        final RobotConversation data = getItem(position);

        holder.mTimeTextView.setText(DateTimeUtils.getTime(data.lastMsgTime, false, true));
        String latestMsg = ChatTextHelper.showContentType(data.lastMsg, data.msgType);
        if (data.unread_msg_cont > 0) {
                if (data.unread_msg_cont < 100) {
                    holder.mNotifyTextView.setText(String.valueOf(data.unread_msg_cont));
                } else {
                    holder.mNotifyTextView.setText("99+");
                }
                holder.mNotifyTextView.setVisibility(View.VISIBLE);

        } else {
            holder.mNotifyTextView.setVisibility(View.GONE);
        }
        //处理消息
        holder.mLastMsgTextView.setText(latestMsg);
        holder.mNameTextView.setText(data.cnName);
        FacebookImageUtil.loadWithCache(data.gravatar, holder.mImageView);
        String draft = InternDatas.getDraft(QtalkStringUtils.parseBareJid(data.id));
        if (!TextUtils.isEmpty(draft)) {
            //草稿不为空
            draft = ChatTextHelper.showDraftContent(draft);
            SpannableStringBuilder sb = new SpannableStringBuilder("[草稿] ");
            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
            sb.setSpan(redSpan, 0, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(draft);
            holder.mLastMsgTextView.setText(sb);
        }
        return convertView;
    }


    class ViewHolder {
        SimpleDraweeView mImageView;
        TextView mNameTextView, mTimeTextView, mLastMsgTextView, mNotifyTextView;
    }
}