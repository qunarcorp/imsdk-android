package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;

public class MedalClickRemindView extends LinearLayout {
    private TextView meetingTextView;
    private TextView click_to_detail;
    public MedalClickRemindView(Context context) {
        this(context, null);
    }

    public MedalClickRemindView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MedalClickRemindView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_meeting_remind, this, true);

        meetingTextView = findViewById(R.id.meeting_data);

        click_to_detail = findViewById(R.id.click_to_detail);
        click_to_detail.setText(Html.fromHtml("<u>"+"查看我的勋章>>"+"</u>"));
    }

    public void setData(String txt){
        if(meetingTextView != null){
            meetingTextView.setText(txt);
        }
    }

    public void setSB(CharSequence charSequence){
        if(meetingTextView != null){
            meetingTextView.setText(charSequence);
        }
    }
}

