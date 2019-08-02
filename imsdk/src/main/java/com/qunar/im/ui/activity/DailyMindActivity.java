package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

/**
 * Qtalk evernote
 * Created by lihaibin.li on 2017/8/22.
 */

public class DailyMindActivity extends SwipeBackActivity implements View.OnClickListener {
    private TextView password_box, to_do_list, ever_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_daily_mind);

        initViews();
    }


    private void initViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_setting_carry_note);
//        myActionBar.getTitleTextview().setText(R.string.atom_ui_setting_carry_note);

        password_box = (TextView) findViewById(R.id.password_box);
        password_box.setOnClickListener(this);

        to_do_list = (TextView) findViewById(R.id.to_do_list);
        to_do_list.setOnClickListener(this);

        ever_note = (TextView) findViewById(R.id.ever_note);
        ever_note.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.password_box) {
            Intent intent = new Intent(this, DailyPasswordBoxMainActivity.class);
            startActivity(intent);
        }else if(id == R.id.to_do_list){
            startActivity(new Intent(this,DailyToDoListActivity.class));
        }else {
            startActivity(new Intent(this,DailyNoteListActivity.class));
        }
    }
}
