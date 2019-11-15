package com.qunar.im.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.RecycleViewDivider;
import com.qunar.im.ui.adapter.WorkWorldAtShowAdapter;
import com.qunar.im.ui.presenter.WorkWorldAtShowPresenter;
import com.qunar.im.ui.presenter.impl.WorkWorldSearchManagerPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldAtShowView;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.qunar.im.ui.activity.WorkWorldDetailsActivity.WORK_WORLD_DETAILS_ITEM;

public class WorkWorldSearchActivity extends IMBaseActivity implements WorkWorldAtShowView {

    protected QtNewActionBar qtNewActionBar;//头部导航
    protected RecyclerView work_world_notice_rc;
    protected SwipeRefreshLayout mSwipeRefreshLayout;//刷新组件

    protected WorkWorldAtShowAdapter workWorldNoticeAdapter;
    protected WorkWorldAtShowPresenter workWorldAtShowPresenter;
    protected  TextView emptyText;
    private static String tip="支持\"姓名\"或\"关键词\"搜索";

    private View.OnClickListener openDetailsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final WorkWorldItem item = (WorkWorldItem) v.getTag();
            Intent intent = new Intent(WorkWorldSearchActivity.this, WorkWorldDetailsActivity.class);
            intent.putExtra(WORK_WORLD_DETAILS_ITEM, (Serializable) item);
//            startActivityForResult(intent, ACTIVITY_DETAILS);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_search_workworld_activity);

        initView();
        initData();
    }

    private void initData() {
        workWorldAtShowPresenter = new WorkWorldSearchManagerPresenter();
        workWorldAtShowPresenter.setView(this);

    }

    private void initView() {
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        work_world_notice_rc = this.findViewById(R.id.work_world_notice_rc);
        work_world_notice_rc.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRefreshLayout = this.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setEnabled(false);
        setNewActionBar(qtNewActionBar);
        setShowSearchBar(true);
        setOnSearch(new OnSearch() {
            @Override
            public void onSearch(String str) {
                if(TextUtils.isEmpty(str)){
                    clearData();
//                    workWorldNoticeAdapter.setNewData(new ArrayList<MultiItemEntity>());
                    setEmptyText(tip);
                    return;
                }
                if(str.length()<2){
                    setEmptyText("请至少输入两个字符以开始搜索");
                    return;
                }
                workWorldAtShowPresenter.startSearch(str);
//                Toast.makeText(WorkWorldSearchActivity.this,str,Toast.LENGTH_LONG).show();
            }
        });
        setShowSearchBack(false);
        setSearchCancleClickLin(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        workWorldNoticeAdapter = new WorkWorldAtShowAdapter(new ArrayList<MultiItemEntity>(), this,work_world_notice_rc);
        View view = LayoutInflater.from(this).inflate(R.layout.atom_ui_empty_work_world,null);
       emptyText = view.findViewById(R.id.empty_tip);
        workWorldNoticeAdapter.setEmptyView(view);
        emptyText.setText(tip);
        workWorldNoticeAdapter.setOpenDetailsListener(openDetailsListener);
        work_world_notice_rc.setAdapter(workWorldNoticeAdapter);
        work_world_notice_rc.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, 3, getResources().getColor(R.color.atom_ui_light_gray_DD)));
        workWorldNoticeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                    loadMore();

            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(0, 202, 190));
    }


    private void loadMore() {
        workWorldAtShowPresenter.loadingMore();
    }


    @Override
    public void showNewData(List<? extends MultiItemEntity> list) {
        Logger.i("拿到了刷新数据");
        if (list != null) {
            if (list.size() > 0) {
//            workWorldAdapter.setNewData(list);
//            work_world_rc.scrollToPosition(0);
                showNewDataHandle(list);
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                workWorldNoticeAdapter.setNewData((List<MultiItemEntity>)list);
            }
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }

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
//                    datalist= list;
                    workWorldNoticeAdapter.setNewData((List<MultiItemEntity>) list);
//                    work_world_rc.scrollToPosition(0);
//                    work_world_rc.scrollTo(0,0);
                    mSwipeRefreshLayout.setRefreshing(false);
//                    if(!canLoadMore){
//                        canLoadMore = true;
//                    }
                }
            });

        }


    }

    @Override
    public void showMoreData(List<? extends MultiItemEntity> list) {
        showMoreDataHandle(list);
    }

    public void showMoreDataHandle(final List<? extends MultiItemEntity> list) {
        if (work_world_notice_rc.isComputingLayout()) {
            work_world_notice_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMoreDataHandle(list);
                }
            }, 500);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (list != null && list.size() > 0) {
//                        datalist.addAll(list);
                        workWorldNoticeAdapter.addData(list);
                        workWorldNoticeAdapter.loadMoreComplete();
                    } else {
                        workWorldNoticeAdapter.loadMoreEnd();
                    }
                }
            });

        }

    }

    @Override
    public int getShowCount() {
        return 0;
    }

    @Override
    public WorkWorldNoticeItem getLastItem() {
        if (workWorldNoticeAdapter.getData() != null && workWorldNoticeAdapter.getData().size() > 0) {
            return (WorkWorldNoticeItem) workWorldNoticeAdapter.getData().get(workWorldNoticeAdapter.getData().size() - 1);
        } else {
            return null;
        }
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

    }

    @Override
    public int getListCount() {
        return workWorldNoticeAdapter.getItemCount();
    }

    @Override
    public void setEmptyText(String string) {
        emptyText.setText(string);
    }

    @Override
    public void clearData() {
        workWorldNoticeAdapter.setNewData(new ArrayList<MultiItemEntity>());
    }
}
