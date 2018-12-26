package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.base.util.ProfileUtils;

/**
 * Created by saber on 16-3-23.
 */
public class TransferMsgView extends LinearLayout {
    TextView from_server,transfer_reason,history_url,to_customer,start_conv;

    public TransferMsgView(Context context) {
        this(context, null);
    }

    public TransferMsgView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransferMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_server_transfer, this, true);
        from_server = findViewById(R.id.from_server);
        transfer_reason = findViewById(R.id.transfer_reason);
        history_url = findViewById(R.id.history_url);
        to_customer = findViewById(R.id.to_customer);
        start_conv = findViewById(R.id.start_conv);
    }

    public void initServer(String from,String r, final String history, final String to)
    {
        transfer_reason.setVisibility(VISIBLE);
        history_url.setVisibility(VISIBLE);
        history_url.setMovementMethod(LinkMovementMethod.getInstance());
        final String s =
                getContext().getResources().getString(R.string.atom_ui_transfer_server_template);
        String reason = getContext().getResources().getString(R.string.atom_ui_transfer_reason);
        String h = getContext().getResources().getString(R.string.atom_ui_history_url);
        String historyText = h+history;
        final String c = getContext().getResources().getString(R.string.atom_ui_transfer_customer);
        ProfileUtils.loadNickName(from, true, new ProfileUtils.LoadNickNameCallback() {
            @Override
            public void finish(String name) {

                from_server.setText(String.format(s, name));
            }
        });

        transfer_reason.setText(String.format(reason, r));
        URLSpan span = new URLSpan(history) {
            @Override
            public void onClick(View widget) {

                View v = (View) widget.getParent();
                if (v.getTag(R.string.atom_ui_voice_hold_to_talk) != null) {
                    v.setTag(R.string.atom_ui_voice_hold_to_talk,null);
                    return;
                }
                Intent intent = new Intent(getContext(), QunarWebActvity.class);
                intent.setData(Uri.parse(history));
                getContext().startActivity(intent);
            }
        };
        SpannableString spannableString = new SpannableString(historyText);
        spannableString.setSpan(span, h.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        history_url.setText(spannableString);
        ProfileUtils.loadNickName(to, true, new ProfileUtils.LoadNickNameCallback() {
            @Override
            public void finish(String name) {
                to_customer.setText(c + " " +name);
            }
        });
        start_conv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PbChatActivity.class);
                i.putExtra("jid",to);
                i.putExtra("isFromChatRoom", false);
                getContext().startActivity(i);
            }
        });
    }

    public void initCustomer(String from, final String to)
    {
        transfer_reason.setVisibility(GONE);
        history_url.setVisibility(GONE);
        from_server.setText(R.string.atom_ui_transfer_customer_template);
        final String s = getContext().getResources().getString(R.string.atom_ui_transfer_name);
        to_customer.setText(String.format(s,to));
//        ProfileUtils.loadNickName(to, true, new ProfileUtils.LoadNickNameCallback() {
//            @Override
//            public void finish(String name) {
//
//            }
//        });
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PbChatActivity.class);
                i.putExtra("jid", to);
                i.putExtra("isFromChatRoom", false);
                getContext().startActivity(i);
            }
        });
    }

}
