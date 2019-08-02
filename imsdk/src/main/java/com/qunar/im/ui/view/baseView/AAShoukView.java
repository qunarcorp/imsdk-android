package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qunar.im.base.jsonbean.AAShoukContent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.IconView;

/**
 * Created by saber on 15-12-30.
 */
public class AAShoukView extends RelativeLayout {
    private TextView titleTextView, aashoukType, aashouk_persons, aashouk_total;
//    private ImageView aashoukaashouk_icon;
    private IconView aashoukaashouk_icon;

    public AAShoukView(Context context) {
        this(context, null);
    }

    public AAShoukView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AAShoukView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_item_aashouk, this, true);
        aashouk_persons = findViewById(R.id.aashouk_persons);
        titleTextView = findViewById(R.id.aashouk_title);
        aashouk_total = findViewById(R.id.aashouk_total);
        aashoukType = findViewById(R.id.tv_aashouk_type);
        aashoukaashouk_icon = findViewById(R.id.aashouk_icon);
    }

    public void bindView(AAShoukContent aaShoukContent) {
        if(!TextUtils.isEmpty(aaShoukContent.aa_type) && "single".equalsIgnoreCase(aaShoukContent.aa_type)) {
            titleTextView.setText("AA收款");
        }else {
            if(TextUtils.isEmpty(aaShoukContent.avg_money)) {
                titleTextView.setVisibility(GONE);
            } else {
                titleTextView.setVisibility(VISIBLE);
                titleTextView.setText(String.format("AA收款，每人%s元", aaShoukContent.avg_money));
            }
        }
        if(TextUtils.isEmpty(aaShoukContent.person_num)) {
            aashouk_persons.setVisibility(GONE);
        } else {
            aashouk_persons.setVisibility(VISIBLE);
            aashouk_persons.setText(String.format("%s人参与", aaShoukContent.person_num));
        }
        if(TextUtils.isEmpty(aaShoukContent.total_money)) {
            aashouk_total.setVisibility(GONE);
        } else {
            aashouk_total.setVisibility(VISIBLE);
            aashouk_total.setText(String.format("共%s元", aaShoukContent.total_money));
        }
        aashoukType.setText(aaShoukContent.type);
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setAashoukType(String type) {
        aashoukType.setText(type);
    }

    public void setIcon(int id)
    {
//        aashoukaashouk_icon.setImageResource(id);
    }
}
