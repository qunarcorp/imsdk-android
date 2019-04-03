package com.qunar.im.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.AddBuddyViewAdapter;
import com.qunar.im.base.jsonbean.SearchUserResult;
import com.qunar.im.ui.presenter.IFindBuddyPresenter;
import com.qunar.im.ui.presenter.impl.FindBuddyPresenter;
import com.qunar.im.ui.presenter.views.IFindBuddyView;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.List;


public class AddBuddyActivity extends IMBaseActivity implements View.OnClickListener,IFindBuddyView {
    Button btn_send_buddy;
    EditText et_buddy;
    TextView tv_error_msg;
    ListView search_results;

    AddBuddyViewAdapter addBuddyViewAdapter;

    IFindBuddyPresenter findBuddyPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_add_buddy);
        bindViews();
        findBuddyPresenter = new FindBuddyPresenter();
        init();
    }

    private void bindViews() {
        et_buddy = (EditText) findViewById(R.id.et_buddy);
        btn_send_buddy = (Button) findViewById(R.id.btn_send_buddy);
        tv_error_msg = (TextView) findViewById(R.id.tv_error_msg);
        search_results = (ListView) findViewById(R.id.search_results);
    }

    void init(){
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        setActionBarTitle(R.string.atom_ui_title_add_buddy_search);
        btn_send_buddy.setOnClickListener(this);
        search_results.setVisibility(View.GONE);
        addBuddyViewAdapter = new AddBuddyViewAdapter(this);
        search_results.setAdapter(addBuddyViewAdapter);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        findBuddyPresenter.setIFindBuddyView(this);
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send_buddy) {
            findBuddyPresenter.doSearch();

        }
    }

    @Override
    public String getKeyword() {
        return et_buddy.getText().toString().trim().toLowerCase();
    }

    @Override
    public void setSearchResults(final List<SearchUserResult.SearchUserInfo> results) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if(results==null||results.size() == 0)
                {
                    tv_error_msg.setVisibility(View.VISIBLE);
                    search_results.setVisibility(View.GONE);
                    tv_error_msg.setText(R.string.atom_ui_tip_add_buddy_not_found);
                }
                else {
                    tv_error_msg.setVisibility(View.GONE);
                    search_results.setVisibility(View.VISIBLE);
                    addBuddyViewAdapter.changeDatas(results);
                    addBuddyViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
