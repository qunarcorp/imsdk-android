package com.qunar.im.ui.view.chatExtFunc;

import android.content.Context;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.base.common.FacebookImageUtil;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.CommonAdapter;
import com.qunar.im.ui.adapter.CommonViewHolder;

import java.util.List;

/**
 * Created by xinbo.wang on 2016/5/19.
 */
public class GridFuncAdapter extends CommonAdapter<FuncItem> {

    public GridFuncAdapter(Context cxt, List<FuncItem> datas, int itemLayoutId) {
        super(cxt, datas, itemLayoutId);
    }

    @Override
    public void convert(CommonViewHolder viewHolder, FuncItem item) {
        final TextView desc =  viewHolder.getView(R.id.ItemText);
        final SimpleDraweeView btnPic =  viewHolder.getView(R.id.ItemImage);
        desc.setText(item.textId);
        FacebookImageUtil.loadWithCache(item.icon,btnPic);
    }
}
