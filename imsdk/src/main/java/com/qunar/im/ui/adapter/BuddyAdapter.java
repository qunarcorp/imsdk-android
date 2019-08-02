package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
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
 * 好友列表适配器
 */
public class BuddyAdapter extends CommonAdapter<Nick> implements SectionIndexer {
    private final int rootHeight;
    private final int nodeHeight;
    public List<Nick> mBuddies = new ArrayList<>();
    private Map<Integer, Integer> index = new HashMap<>();
    private Context context;

    String[] sections = new String[]{"^", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    public BuddyAdapter(Context cxt, List<Nick> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
        this.context = cxt;
        rootHeight = Utils.dipToPixels(mContext, 18);
        nodeHeight = Utils.dipToPixels(mContext, 48);
    }

    @Override
    public int getCount() {
        return mBuddies.size();
    }

    @Override
    public Nick getItem(int position) {
        return mBuddies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void convert(CommonViewHolder viewHolder, final Nick item) {
        final TextView tv_name = viewHolder.getView(R.id.m_name);
        final SimpleDraweeView gravantar = viewHolder.getView(R.id.m_gravatar);
        if (item.isRoot()) {
            viewHolder.getConvertView().setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rootHeight));
            viewHolder.getConvertView().setBackgroundResource(R.color.atom_ui_light_gray_ee);
            gravantar.setVisibility(View.GONE);
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tv_name.setText(item.getName());
        } else {
//            if (TextUtils.isEmpty(item.getName())) {
//                ProfileUtils.loadNickName(item.getKey(), tv_name, true);
//            } else {

//            }
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            gravantar.setVisibility(View.VISIBLE);
            //old
//            ProfileUtils.displayGravatarByImageSrc(item.getXmppId(), item.getHeaderSrc(), gravantar);
            //new
            ConnectionUtil.getInstance().getUserCard(item.getXmppId(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {

                    ProfileUtils.displayGravatarByImageSrc((Activity) context, nick.getHeaderSrc(), gravantar,
                            context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_name.setText(TextUtils.isEmpty(nick.getName())?item.getXmppId():nick.getName());
                        }
                    });
                }
            },false,false);

//            ProfileUtils.displayGravatarByUserId(item.getKey(), gravantar);
            viewHolder.getConvertView().setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, nodeHeight));
            viewHolder.getConvertView().setBackgroundResource(R.color.atom_ui_white);
        }
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int idx = 0;
        if (index.containsKey(sectionIndex)) {
            idx = index.get(sectionIndex);
        }
        return idx;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public void setNodes(Map<Integer, List<Nick>> contacts) {
        mBuddies.clear();
        for (int i = 0; i < 28; i++) {
            if (i == 0) {
                index.put(i, 0);
                continue;
            }
            index.put(i, 1 + mBuddies.size());
            List<Nick> list = contacts.get(i);
            if (list == null) {
                index.put(i, index.get(i - 1));
            } else {
                Nick node = new Nick();
                node.setName(sections[i]);
                node.setRoot(true);
                mBuddies.add(node);
                mBuddies.addAll(list);
            }
        }
        changeData(mBuddies);
        notifyDataSetChanged();
    }
}
