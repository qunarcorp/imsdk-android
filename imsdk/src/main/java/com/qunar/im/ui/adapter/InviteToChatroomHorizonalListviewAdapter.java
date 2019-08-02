package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.view.multilLevelTreeView.Node;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ChatroomInvitationActivity;

import java.util.List;

public class InviteToChatroomHorizonalListviewAdapter extends BaseAdapter {
    private List<Node> mSelectedNodes;
    private Context mContext;
    ChatroomInvitationActivity.ICheckboxClickedListener mListener;
    private List<String> mNoChangeIds;

    public InviteToChatroomHorizonalListviewAdapter(Context context, ChatroomInvitationActivity.ICheckboxClickedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public List<Node> getSelectedNodes() {
        return mSelectedNodes;
    }
    public void setSelectedNodes(List<Node> mSelectedNodes) {
        this.mSelectedNodes = mSelectedNodes;
    }

    public void setNoChangeIds(List<String> mNoChangeIds) {
        this.mNoChangeIds = mNoChangeIds;
    }

    @Override
    public int getCount() {
        return mSelectedNodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mSelectedNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHodler mHolder;
        if (convertView == null) {
            mHolder = new ViewHodler();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.atom_ui_item_gravatar_adapter, parent, false);
            mHolder.mGravatar = (SimpleDraweeView) convertView.findViewById(R.id.iv_head);
            mHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHodler) convertView.getTag();
        }
        Logger.i("选中的人的id:"+mSelectedNodes.get(position).getKey());
        ConnectionUtil.getInstance().getUserCard(mSelectedNodes.get(position).getKey(), new IMLogicManager.NickCallBack() {
            @Override
            public void onNickCallBack(Nick nick) {
                if(nick!=null){
                    //old
//                    ProfileUtils.displayGravatarByImageSrc(nick.getXmppId(),nick.getHeaderSrc(),mHolder.mGravatar);
                    //new
                    ProfileUtils.displayGravatarByImageSrc((Activity) mContext, nick.getHeaderSrc(), mHolder.mGravatar,
                            mContext.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), mContext.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                    mHolder.name.setText(nick.getName());
//                    ProfileUtils.displayGravatarByUserId(nick.getHeaderSrc(),
//                            mHolder.mGravatar);
                }
            }
        },false,false);
//        ProfileUtils.displayGravatarByUserId( mSelectedNodes.get(position).getKey(),
//                mHolder.mGravatar);
        mHolder.mGravatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNoChangeIds.contains(getSelectedNodes().get(position).getKey())){
                    return;
                }
                mSelectedNodes.remove(position);
                mListener.CheckobxClicked();
            }
        });
        return convertView;
    }

    static class ViewHodler {
        SimpleDraweeView mGravatar;
        TextView name;
    }
}