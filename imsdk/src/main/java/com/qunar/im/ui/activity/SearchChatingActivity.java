package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.SearchChatingAdapter;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.ISearchChatingPresenter;
import com.qunar.im.ui.presenter.impl.SearchChatingPresenter;
import com.qunar.im.ui.presenter.views.ISearchChatingView;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.ui.view.MySearchView;
import com.qunar.im.ui.view.QtSearchActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinbo.wang on 15-7-7.
 */
public class SearchChatingActivity extends IMBaseActivity implements ISearchChatingView {
    PullToRefreshListView search_result;
    TextView emptyView;
    SearchChatingAdapter adapter;
    QtSearchActionBar actionBar;

    ISearchChatingPresenter searchChatingPresenter;
    String jid;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_search_chating);
        bindViews();
        injectExtras_();
        searchChatingPresenter = new SearchChatingPresenter();
        searchChatingPresenter.setSearchChatingView(this);
        initViews();
    }

    private void bindViews() {
        emptyView = (TextView) findViewById(R.id.emptyView);
        search_result = (com.handmark.pulltorefresh.library.PullToRefreshListView) findViewById(R.id.search_result);
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey("jid")) {
                jid = extras_.getString("jid");
            }
        }
    }

    void initViews() {
        actionBar= (QtSearchActionBar) this.findViewById(R.id.my_action_bar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        emptyView.setVisibility(View.GONE);
        search_result.setVisibility(View.VISIBLE);
        search_result.setAlpha(0.5f);
        search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getItemAtPosition(position);
                if(obj!=null)
                {
                    IMMessage message = (IMMessage) obj;
                    Intent intent = new Intent(SearchChatingActivity.this,LocalChatRecordActivity.class);
                    intent.putExtra("jid",jid);
                    intent.putExtra("selectedMsgTime",message.getTime().getTime());
                    startActivity(intent);
                    finish();
                }
            }
        });
        actionBar.getSearchView().setOnQueryChangeListener(new MySearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchChatingPresenter.doSearchChating();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchChatingPresenter.doSearchChating();
                return true;
            }
        });
        actionBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        actionBar.getSearchView().changeQueryHint("输入搜索内容");
        actionBar.getSearchView().getEditFocus();
        if (adapter == null) {
            adapter = new SearchChatingAdapter(this, new ArrayList<IMMessage>(), R.layout.atom_ui_item_search_chating);
            adapter.setGravatarHandler(new SearchChatingAdapter.GravatarHandler() {
                @Override
                public void requestGravatarEvent(final String fullName, final SimpleDraweeView view) {
                    ProfileUtils.displayGravatarByFullname(fullName,view);
                }
            });
            search_result.getRefreshableView().setAdapter(adapter);
        }
    }

    @Override
    public String getSearchTerm() {
        return actionBar.getSearchView().getQuery().toString();
    }

    @Override
    public void setSearchResult(List<IMMessage> results) {
        adapter.setDatas(results);
        adapter.notifyDataSetChanged();
        search_result.setVisibility(View.VISIBLE);
        if (!actionBar.getSearchView().getQuery().toString().isEmpty()) {
            //搜索栏不为空
            search_result.setAlpha(1);
            if (results == null || results.isEmpty()) {
                //搜索结果为空
                emptyView.setVisibility(View.VISIBLE);
                search_result.setVisibility(View.GONE);
            }
        } else {
            //搜索栏为空
            search_result.setAlpha(0.5f);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public String getSearchFrom() {
        return jid;
    }
}
