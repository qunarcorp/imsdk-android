package com.qunar.im.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.module.FavouriteMessage;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.DateTimeUtils;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.processor.MessageProcessor;
import com.qunar.im.ui.view.baseView.processor.ProcessorFactory;
import com.qunar.im.utils.QtalkStringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingchao.song on 9/2/2015.
 */
public class FavouriteMessageAdapter extends BaseAdapter {

    private Context mContext;
    private List<FavouriteMessage> mFavouriteMsgs= new ArrayList<>();

    public FavouriteMessageAdapter(Context context) {
        mContext = context;
    }

    public void setDatas(List<FavouriteMessage> msg)
    {
        if(msg!=null) {
            mFavouriteMsgs = msg;
            notifyDataSetChanged();
        }
        else {
            mFavouriteMsgs.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mFavouriteMsgs.size();
    }

    @Override
    public Object getItem(int position) {
        return mFavouriteMsgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FavouriteMessage fMsg = mFavouriteMsgs.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.atom_ui_favourite_activity_chat_item, parent, false);
            holder.tv_from_name = (TextView) convertView.findViewById(R.id.tv_from_name);
            holder.tv_save_time = (TextView) convertView.findViewById(R.id.tv_save_time);
            holder.ll_content = (LinearLayout) convertView.findViewById(R.id.ll_content);
            holder.iv_gravatar = (SimpleDraweeView) convertView.findViewById(R.id.iv_gravatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_save_time.setText(DateTimeUtils.getTime(Long.parseLong(fMsg.getTime()), true, true));

        //1 FOR GROUP
        if ("1".equals(fMsg.getFromType())) {

            String name = InternDatas.getName(QtalkStringUtils.parseBareJid(fMsg.getFromUserId()));
            if (TextUtils.isEmpty(name)) {
                name = "讨论组";
            }
            holder.tv_from_name.setText(name);
        }
        else {
            ProfileUtils.loadNickName(fMsg.getFromUserId(),holder.tv_from_name,false);
        }
        final IMMessage imMessage = JsonUtils.getGson().fromJson(fMsg.getTextContent(), IMMessage.class);
        updateGravatar(holder.iv_gravatar,
                imMessage.getFromID());
        MessageProcessor processor = ProcessorFactory.getProcessorMap().get(imMessage.getMsgType());
        if(processor == null)
        {
            processor = ProcessorFactory.getProcessorMap().get(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
        }
        holder.ll_content.removeAllViews();
        if (holder.ll_content != null) {
            processor.processChatView(holder.ll_content, new IMessageItem() {
                @Override
                public IMMessage getMessage() {
                    return imMessage;
                }

                @Override
                public int getPosition() {
                    return position;
                }

                @Override
                public Context getContext() {
                    return mContext;
                }

                @Override
                public Handler getHandler() {
                    return QunarIMApp.mainHandler;
                }

                @Override
                public ProgressBar getProgressBar() {
                    return null;
                }

                @Override
                public ImageView getErrImageView() {
                    return null;
                }

                @Override
                public TextView getStatusView() {
                    return null;
                }
            });
        }
        return convertView;
    }

    public void updateGravatar(final SimpleDraweeView imageView, String fromId) {
        String idOrName = fromId;
        if(!fromId.contains("@conference"))
        {
            idOrName = QtalkStringUtils.parseBareJid(fromId);
        }
        ProfileUtils.displayGravatarByFullname(idOrName,imageView);
    }

    private static class ViewHolder {
        TextView tv_from_name;
        TextView tv_save_time;
        LinearLayout ll_content;
        SimpleDraweeView iv_gravatar;
    }
}
