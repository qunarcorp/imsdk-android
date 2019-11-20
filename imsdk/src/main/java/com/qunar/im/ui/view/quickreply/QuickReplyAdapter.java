package com.qunar.im.ui.view.quickreply;

import androidx.annotation.Nullable;

import com.qunar.im.ui.R;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Lex lex on 2018/7/31.
 */
public class QuickReplyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public QuickReplyAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.quickreply_item_tv, item);
        helper.addOnClickListener(R.id.quickreply_item_tv);
    }
}
