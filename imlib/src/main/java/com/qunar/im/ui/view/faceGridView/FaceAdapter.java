package com.qunar.im.ui.view.faceGridView;

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
import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.view.faceGridView.EmoticionMap;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;

import java.io.File;

/**
 * Created by xinbo.wang on 2015/2/5.
 */
public class FaceAdapter extends BaseAdapter {
    private EmoticionMap data;
    Context context;
    int pos;
    int itemCount;

    public FaceAdapter(Context context,EmoticionMap data,int pos,int itemCount)
    {
        this.data = data;
        this.context = context;
        this.pos = pos;
        this.itemCount = itemCount;
    }

    @Override
    public int getCount() {

        if((pos+1)*itemCount> data.count)
        {
            return data.count - pos*itemCount;
        }
        return itemCount;
    }

    @Override
    public EmoticonEntity getItem(int position) {
        int index = pos*itemCount + position;
        return data.getEntity(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmoticonEntity emoji = getItem(position);
        GridViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new GridViewHolder();
            convertView = viewHolder.layoutView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GridViewHolder) convertView.getTag();
        }

        if (emoji.fileFiexd.startsWith("emoticons/") || emoji.fileFiexd.startsWith("Big_Emoticons/")) {
            if(data.showAll == 1)
                viewHolder.defaultSize();
            else viewHolder.extSize();
            FacebookImageUtil.loadWithCache("asset:///"+emoji.fileFiexd, viewHolder.faceIv,true);
        } else {
            if(data.showAll==1)
            {
                viewHolder.defaultSize();
            }
            else {
                viewHolder.extSize();
            }
            FacebookImageUtil.loadLocalImage(new File(emoji.fileFiexd), viewHolder.faceIv,0,0, true,FacebookImageUtil.EMPTY_CALLBACK);
        }

        return convertView;
    }

    class GridViewHolder {
        public LinearLayout layoutView;
        public SimpleDraweeView faceIv;

        public int faceSize = Utils.dipToPixels(context, 32);
        public int faceExtSize = Utils.dipToPixels(context,64);
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

        public void extSize()
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(faceExtSize, faceExtSize);
            faceIv.setLayoutParams(params);
            faceIv.invalidate();
        }

        public void defaultSize()
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(faceSize, faceSize);
            faceIv.setLayoutParams(params);
            faceIv.invalidate();
        }
    }
}
