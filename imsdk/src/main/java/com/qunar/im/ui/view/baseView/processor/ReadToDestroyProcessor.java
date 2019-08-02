package com.qunar.im.ui.view.baseView.processor;

import android.view.ViewGroup;

import com.qunar.im.ui.view.baseView.IMessageItem;
import com.qunar.im.ui.view.baseView.ReadToDestroyView;
import com.qunar.im.ui.view.baseView.ViewPool;

/**
 * Created by zhaokai on 15-8-20.
 */
public class ReadToDestroyProcessor extends DefaultMessageProcessor {

    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {

        ReadToDestroyView view = ViewPool.getView(ReadToDestroyView.class,item.getContext());
        view.chanageContext(item.getContext());
        view.init(item.getMessage());
        parent.addView(view);
    }
}
