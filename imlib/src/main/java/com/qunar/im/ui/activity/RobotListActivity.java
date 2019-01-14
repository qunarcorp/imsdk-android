package com.qunar.im.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.base.presenter.messageHandler.ConversitionType;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.adapter.BaseInfoAdapter;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.PublishPlatform;
import com.qunar.im.base.presenter.impl.RobotListPresenter;
import com.qunar.im.base.presenter.impl.SearchPublishPlatformPresenter;
import com.qunar.im.base.presenter.views.ISearchPresenter;
import com.qunar.im.ui.R;
import com.qunar.im.utils.QtalkStringUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.view.BaseInfoBinderable;
import com.qunar.im.ui.view.MySearchView;
import com.qunar.im.ui.view.QtSearchActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinbo.wang on 15-9-2.
 */
public class RobotListActivity extends SwipeBackActivity implements
        RobotListPresenter.UpdateableView, View.OnTouchListener {
    ListView list;

    private QtSearchActionBar searchActionBar;
    private BaseInfoAdapter adapter;
    private RobotListPresenter robotListPresenter;
    private ISearchPresenter searchPresenter;
    private TextView textView;

    private String selectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_robot_list);
        bindViews();
        adapter = new BaseInfoAdapter(this.getApplicationContext());
        robotListPresenter = new RobotListPresenter();
        searchPresenter = new SearchPublishPlatformPresenter();
        searchPresenter.setSearchView(this);
        initView();

        robotListPresenter.setIRobotListView(this);
        getRobotList();
    }

    private void bindViews()
    {
        list = (ListView) findViewById(R.id.list);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();

    }


    void getRobotList() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                robotListPresenter.loadRobotList();
            }
        });
    }

    void initView() {
        initSearchActionBar();
        adapter.setClickHandler(new BaseInfoAdapter.ViewClickHandler() {
            @Override
            public void ItemClickEvent(BaseInfoBinderable item) {
                selectId = item.id;
                robotListPresenter.selectRobot();
            }
        });
        textView = new TextView(this);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setText(R.string.atom_ui_search_more);
        textView.setVisibility(View.GONE);
        textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dipToPixels(this, 1)));
        textView.setPadding(Utils.dipToPixels(this, 16), 0, Utils.dipToPixels(this, 16), 0);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.atom_ui_text_size_extra_micro));
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.atom_ui_ic_follow_robot, 0, 0, 0);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robotListPresenter.searchRobot4mNet();
            }
        });
        textView.setVisibility(View.GONE);
        list.addFooterView(textView);
        list.setAdapter(adapter);
        list.setOnTouchListener(this);
        robotListPresenter.init();
    }

    @Override
    public void update(final List<PublishPlatform> platforms) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                List<BaseInfoBinderable> baseInfoBinderables
                        = new ArrayList<BaseInfoBinderable>(platforms.size());
                boolean isFirst = true;
                for(final PublishPlatform p:platforms)
                {
                    BaseInfoBinderable baseInfoBinderable = new BaseInfoBinderable();
                    baseInfoBinderable.id = p.getId();
                    baseInfoBinderable.name = p.getName();
                    baseInfoBinderable.desc = p.getDescription();
                    baseInfoBinderable.type = BaseInfoBinderable.PUBLISH_TYPE;
                    baseInfoBinderable.imageUrl = QtalkStringUtils.getGravatar(p.getGravatarUrl(),true);
                    if(isFirst)
                    {
                        baseInfoBinderable.hint =getString(R.string.atom_ui_contact_tab_public_number);
                        isFirst = false;
                    }
                    baseInfoBinderables.add(baseInfoBinderable);
                }
                adapter.update(baseInfoBinderables);
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        if (getTerm() != null && !getTerm().isEmpty()) {
                            textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    Utils.dipToPixels(RobotListActivity.this, 72)));
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    Utils.dipToPixels(RobotListActivity.this, 1)));
                            textView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void error(final String errmsg) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RobotListActivity.this, errmsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setSelRobotInfo(boolean isFollow, String jid, boolean rawHtml) {
        Intent intent = null;
        if (isFollow) {
            //已关注公众号
            if(rawHtml)
            {
                intent = new Intent(RobotListActivity.this, WebMsgActivity.class);
                intent.setData(Uri.fromFile(new File(this.getFilesDir(), Constants.TEMPLATE_FILE_NAME)));
            }
            else {
                intent = new Intent(RobotListActivity.this, RobotChatActivity.class);
                intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, ConversitionType.MSG_TYPE_SUBSCRIPT + "");
            }
            intent.putExtra(RobotChatActivity.ROBOT_ID_EXTRA,jid);
            intent.putExtra(PbChatActivity.KEY_REAL_JID,jid);

        } else {
            //没有关注公众号
            intent = new Intent(RobotListActivity.this, RobotInfoActivity.class);
            intent.putExtra(RobotChatActivity.ROBOT_ID_EXTRA, jid);
        }
        startActivity(intent);
    }

    @Override
    public String getSelId() {
        return selectId;
    }

    @Override
    public String getTerm() {
        return searchActionBar.getSearchView().getQuery().toString();
    }

    @Override
    public void setSearchResult(List<PublishPlatform> results) {
        update(results);
    }

    void doSearch() {
        if (searchPresenter != null) {
            BackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    searchPresenter.doSearch();
                }
            });
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            //imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return false;
    }

    private void initSearchActionBar() {
        searchActionBar = (QtSearchActionBar) this.findViewById(R.id.my_action_bar);
        setSupportActionBar(searchActionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        searchActionBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchActionBar.getSearchView().setOnQueryChangeListener(new MySearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doSearch();
                return true;
            }
        });
        searchActionBar.getSearchView().getEditFocus();
        searchActionBar.getSearchView().changeQueryHint("搜索公众号");
    }
}
