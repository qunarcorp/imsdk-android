package com.qunar.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.base.view.faceGridView.EmoticionMap;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;

/**
 * Created by zhaokai on 16-1-19.
 */
public class FavoriteFaceAdapter extends BaseAdapter {

    Context context;
    int pos;
    int itemCount;
    private EmoticionMap data;

    /**
     * @param itemCount 每页有多少个
     * @param pos       第几页
     */
    public FavoriteFaceAdapter(Context context, EmoticionMap data, int pos, int itemCount) {
        this.data = data;
        this.context = context;
        this.pos = pos;
        this.itemCount = itemCount;
    }

    @Override
    public int getCount() {

        if ((pos + 1) * itemCount > data.count) {
            return data.count - pos * itemCount;
        }
        return itemCount;
    }

    @Override
    public EmoticonEntity getItem(int position) {
        int index = pos * itemCount + position;
        return data.getEntity(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmoticonEntity emoji = getItem(position);
        GridViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new GridViewHolder();
            convertView = viewHolder.layoutView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GridViewHolder) convertView.getTag();
        }
        viewHolder.extSize();
        if (EmotionUtils.FAVORITE_ID.equals(emoji.id)){
            //以特定的id 来标识添加表情的icon
            FacebookImageUtil.loadFromResource(R.drawable.atom_ui_ic_add_custom_emoji,viewHolder.faceIv);
            return convertView;
        }
        ProfileUtils.displayEmojiconByImageSrc((Activity) context, emoji.fileFiexd,  viewHolder.faceIv,
                context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), context.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
//        FacebookImageUtil.loadLocalImage(new File(emoji.fileFiexd), viewHolder.faceIv, 0, 0, true, FacebookImageUtil.EMPTY_CALLBACK);
        return convertView;
    }

    class GridViewHolder {
        public LinearLayout layoutView;
        public SimpleDraweeView faceIv;

        public int faceMargin = Utils.dipToPixels(context, 4);
        public int faceExtSize = Utils.dipToPixels(context, 64);

        public GridViewHolder() {
            faceIv = new SimpleDraweeView(context);
            layoutView = new LinearLayout(context);
            faceIv.setScaleType(ImageView.ScaleType.FIT_XY);
            layoutView.setOrientation(LinearLayout.VERTICAL);
            layoutView.setGravity(Gravity.CENTER);
            layoutView.setBackgroundColor(Color.TRANSPARENT);
            faceIv.setBackgroundColor(Color.TRANSPARENT);
            layoutView.addView(faceIv);
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT);
            layoutView.setLayoutParams(layoutParams);
        }

        public void extSize() {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(faceExtSize, faceExtSize);
            params.setMargins(0, faceMargin, 0, faceMargin);
            faceIv.setLayoutParams(params);
            faceIv.invalidate();
        }
    }
}