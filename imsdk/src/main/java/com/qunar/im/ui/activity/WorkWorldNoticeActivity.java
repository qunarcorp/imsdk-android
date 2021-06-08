package com.qunar.im.ui.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.ui.presenter.WorkWorldNoticePresenter;
import com.qunar.im.ui.presenter.impl.WorkWorldNoticeManagerPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldNoticeView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.RecycleViewDivider;
import com.qunar.im.ui.adapter.WorkWorldDetailsAdapter;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

public class WorkWorldNoticeActivity extends SwipeBackActivity implements WorkWorldNoticeView {


    protected QtNewActionBar qtNewActionBar;//头部导航
    protected RecyclerView work_world_notice_rc;
    protected WorkWorldDetailsAdapter workWorldNoticeAdapter;
    protected WorkWorldNoticePresenter workWorldNoticePresenter ;

    public static String NOTICE_COUNT = "NOTICE_CUONT";

    private int noticeCount= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_work_world_notice_activity);
        bindView();
        bindData();
        initAdapter();
        workWorldNoticePresenter.loadingHistory();

    }

    private void initAdapter() {
        workWorldNoticeAdapter = new WorkWorldDetailsAdapter(new ArrayList<MultiItemEntity>(),this);
        work_world_notice_rc.setAdapter(workWorldNoticeAdapter);
        work_world_notice_rc.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, 3, getResources().getColor(R.color.atom_ui_light_gray_DD)));

    }

    private void bindData() {
        setActionBarTitle("我的消息");
        workWorldNoticePresenter = new WorkWorldNoticeManagerPresenter();
        workWorldNoticePresenter.setView(this);
        noticeCount = getIntent().getIntExtra(NOTICE_COUNT,0);
        work_world_notice_rc.setLayoutManager(new LinearLayoutManager(this));
    }

    private void bindView() {
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        work_world_notice_rc = (RecyclerView) findViewById(R.id.work_world_notice_rc);

    }

    @Override
    public void showNewData(List<? extends MultiItemEntity> list) {
        if (list != null && list.size() > 0) {
//            workWorldAdapter.setNewData(list);
//            work_world_rc.scrollToPosition(0);
            showNewDataHandle(list);
        }
    }

    @Override
    public void showMoreData(List<? extends MultiItemEntity> list) {

    }

    public void showNewDataHandle(final List<? extends MultiItemEntity> list) {
        if (work_world_notice_rc.isComputingLayout()) {
            work_world_notice_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNewDataHandle(list);
                }
            }, 500);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//
                    workWorldNoticeAdapter.setNewData((List<MultiItemEntity>) list);
                    work_world_notice_rc.scrollToPosition(0);
                }
            });

        }

    }

    @Override
    public int getShowCount() {
        return noticeCount;
    }

    @Override
    public WorkWorldNoticeItem getLastItem() {
        return null;
    }

    @Override
    public boolean isMindMessage() {
        return false;
    }

    @Override
    public void startRefresh() {

    }

    @Override
    public void closeRefresh() {

    }

    @Override
    public int getListCount() {
        return 0;
    }
}
