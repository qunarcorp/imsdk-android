package com.qunar.im.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.ui.R;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.ui.view.emojiconTextView.EmojiconTextView;

import java.util.List;

/**
 * Created by saber on 15-7-7.
 */
public class SearchChatingAdapter extends CommonAdapter<IMMessage> {

    GravatarHandler gravatarHandler;

    public void setGravatarHandler(GravatarHandler gravatarHandler) {
        this.gravatarHandler = gravatarHandler;
    }

    public SearchChatingAdapter(Context cxt, List<IMMessage> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
    }

    public void setDatas(List<IMMessage> datas)
    {
        super.changeData(datas);
    }

    @Override
    public void convert(CommonViewHolder viewHolder, IMMessage item) {
        SimpleDraweeView gravatar = viewHolder.getView(R.id.conversation_gravatar);
        TextView nickName = viewHolder.getView(android.R.id.text1);
        EmojiconTextView message = viewHolder.getView(android.R.id.text2);
        ProfileUtils.loadNickName(item.getType() == ConversitionType.MSG_TYPE_CHAT?
                QtalkStringUtils.parseBareJid(item.getFromID()):item.getFromID(),nickName,false);
        message.setText(ChatTextHelper.showContentType(item.getBody(), item.getMsgType()));
        updateGravatar(gravatar,item.getFromID());
    }

    public void updateGravatar(final SimpleDraweeView imageView, String fromId) {
        String idOrName = fromId;
        if(!fromId.contains("@conference"))
        {
            idOrName = QtalkStringUtils.parseBareJid(fromId);
        }
        ProfileUtils.displayGravatarByFullname(idOrName,imageView);
    }
    public interface GravatarHandler
    {
        void requestGravatarEvent(final String jid, final SimpleDraweeView view);
    }

}
