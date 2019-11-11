package com.qunar.im.ui.view.baseView.processor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.CodeActivty;
import com.qunar.im.ui.view.baseView.IMessageItem;

public class CodeMessageProcessor extends DefaultMessageProcessor {
    @Override
    public void processChatView(ViewGroup parent, IMessageItem item) {
        final IMMessage imMessage = item.getMessage();
        if(imMessage == null){
            return;
        }
        final String code = imMessage.getBody();
        final String msgId = imMessage.getId();
        final Context context = item.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.atom_ui_layout_code,null);
        ((TextView)view.findViewById(R.id.source_code)).setText(code);
        view.setOnClickListener((v) -> {
            Intent intent = new Intent(context, CodeActivty.class);
            intent.putExtra(Constants.BundleKey.MESSAGE_ID,msgId);
            context.startActivity(intent);
        });
        parent.addView(view);
    }
}
