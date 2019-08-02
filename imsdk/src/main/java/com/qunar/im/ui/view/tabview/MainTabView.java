package com.qunar.im.ui.view.tabview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qunar.im.ui.R;

/**
 * Created by saber on 16-1-5.
 */
public class MainTabView extends RelativeLayout {
    TextView tabName,unreadCount;

    public MainTabView(Context context) {
        this(context, null);
    }

    public MainTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_tab_layout, this, true);
        tabName = findViewById(R.id.tab_name);
        unreadCount = findViewById(R.id.textView_new_msg);
    }

    public void setTitle(String title)
    {
        tabName.setText(title);
    }
    public void setUnreadCount(int count){
        if(count>0){
            unreadCount.setVisibility(VISIBLE);
            if(count>99){
                unreadCount.setText("99+");
            }else{
                unreadCount.setText(count+"");
            }
        }else{
            unreadCount.setVisibility(GONE);
        }
    }
    public TextView getUnreadView()
    {
        return unreadCount;
    }
}
