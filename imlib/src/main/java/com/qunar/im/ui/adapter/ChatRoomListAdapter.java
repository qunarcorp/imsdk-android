package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/2/2.
 */
public class ChatRoomListAdapter extends CommonAdapter<Nick> implements SectionIndexer {
    private final int nodeHeight;
    private final int rootHeight;
    private Context context;

    public ChatRoomListAdapter(Context cxt, List datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
        this.context = cxt;
        if(datas==null) super.mDatas = new ArrayList<>();
        rootHeight = Utils.dipToPixels(mContext, 18);
        nodeHeight = Utils.dipToPixels(mContext,64);
    }

    String[] sections = new String[]{"^","A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"};
    private Map<Integer,Integer> index = new HashMap<>();

    public void setDatas(final SparseArray<List<Nick>> datas) {
        for(int i=0;i<28;i++)
        {
            if(i==0) {
                index.put(i,0);
                continue;
            }
            index.put(i,1+super.mDatas.size());
            List<Nick> list = datas.get(i);
            if(list==null)
            {
                index.put(i,index.get(i-1));
            }
            else {
                Nick node = new Nick();
                node.setName(sections[i]);
                super.mDatas.add(node);
                super.mDatas.addAll(list);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void convert(CommonViewHolder viewHolder, final Nick item) {
        final TextView name =  viewHolder.getView(R.id.group_name);
        final SimpleDraweeView gpic =  viewHolder.getView(R.id.group_pic);
        if(TextUtils.isEmpty(item.getGroupId())) {
            gpic.setVisibility(View.GONE);
            viewHolder.getConvertView().setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rootHeight));
            viewHolder.getConvertView().setBackgroundResource(R.color.atom_ui_light_gray_ee);
            name.setText(item.getName());
        }else {
            viewHolder.getConvertView().setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, nodeHeight));
            viewHolder.getConvertView().setBackgroundResource(R.color.atom_ui_white);
            gpic.setVisibility(View.VISIBLE);
            ConnectionUtil.getInstance().getMucCard(item.getGroupId(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(nick!=null){
                                name.setText(nick.getName());
                                //old
//                                ProfileUtils.displayGravatarByImageSrc(nick.getGroupId(),nick.getHeaderSrc(),gpic);
                                //new
                                ProfileUtils.displayGravatarByImageSrc((Activity) context, item.getHeaderSrc(), gpic,
                                        context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                            }else{
                                name.setText(item.getGroupId());
                            }

                        }
                    });

//                    ProfileUtils.setGroupPicture(gpic, R.drawable.atom_ui_ic_my_chatroom, mContext, item.getGroupId());
                }
            },false,true);

        }
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int idx = 0;
        if(index.containsKey(sectionIndex)) {
            idx = index.get(sectionIndex);
        }
        return idx;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
