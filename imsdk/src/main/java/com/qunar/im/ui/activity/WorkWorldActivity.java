package com.qunar.im.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.ui.presenter.impl.WorkWorldManagerPresenter;
import com.qunar.im.ui.presenter.WorkWorldPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.RecycleViewDivider;
import com.qunar.im.ui.adapter.WorkWorldAdapter;
import com.qunar.im.ui.view.OnDoubleClickListener;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.qunar.im.ui.activity.WorkWorldDetailsActivity.WORK_WORLD_DETAILS_ITEM;
import static com.qunar.im.ui.activity.WorkWorldNoticeActivity.NOTICE_COUNT;

public class WorkWorldActivity extends SwipeBackActivity implements WorkWorldView {


    protected static final int delete = 0x01;

    protected QtNewActionBar qtNewActionBar;//头部导航
    protected RecyclerView work_world_rc;
    protected SwipeRefreshLayout mSwipeRefreshLayout;//刷新组件
    protected WorkWorldAdapter workWorldAdapter;
    protected View workWorldHeadView;
    protected TextView headViewText;
    protected LinearLayout headViewLayout;
    private BottomSheetDialog bottomSheetDialog;

    private int noticeCount = 0;

    private String searchUserIdstr = "";

    //    protected List<WorkWorldItem> datalist;
    public static final int ACTIVITY_RELEASE = 88;
    public static final int ACTIVITY_DETAILS=89;
    public static final String WORK_WORLD_RESULT_DATA = "WORK_WORLD_RESULT_DATA";
    public static final String WORK_WORLD_RESULT_ITEM_BACK= "WORK_WORLD_RESULT_ITEM_BACK";

    //
    private View.OnClickListener openDetailsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final WorkWorldItem item = (WorkWorldItem) v.getTag();
            Intent intent = new Intent(WorkWorldActivity.this, WorkWorldDetailsActivity.class);
            intent.putExtra(WORK_WORLD_DETAILS_ITEM, (Serializable) item);
            startActivityForResult(intent,ACTIVITY_DETAILS);
        }
    };

    //单击显示ContextMenu
    private View.OnClickListener onclick_one = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            v.showContextMenu();
            final WorkWorldItem item = (WorkWorldItem) v.getTag();

//


            bottomSheetDialog = new BottomSheetDialog(WorkWorldActivity.this);
            View view = LayoutInflater.from(WorkWorldActivity.this).inflate(R.layout.atom_ui_work_world_special_popwindow, null);
//            TextView delete =
            TextView delete = view.findViewById(R.id.work_world_popwindow_delete);
            TextView reply = view.findViewById(R.id.work_world_popwindow_reply);
            TextView cancle = view.findViewById(R.id.work_world_popwindow_cancle);
            reply.setVisibility(View.GONE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    workWorldPresenter.workworlddeleteWorkWorldItem(item);
                    Toast.makeText(WorkWorldActivity.this, "删除", Toast.LENGTH_LONG).show();
                    bottomSheetDialog.dismiss();
                }
            });
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });
//


            bottomSheetDialog.setContentView(view);
//
            //给布局设置透明背景色
            bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet)
                    .setBackgroundColor(WorkWorldActivity.this.getResources().getColor(android.R.color.transparent));


            bottomSheetDialog.show();


        }
    };

    private WorkWorldPresenter workWorldPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_work_world_activity);
        bindView();
        bindData();
        initAdapter();
        workWorldPresenter.workworldloadingHistory();
        workWorldPresenter.workworldloadingNoticeCount();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initAdapter() {
        workWorldAdapter = new WorkWorldAdapter(this, work_world_rc);


        workWorldAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        });
        work_world_rc.setAdapter(workWorldAdapter);
//        work_world_rc.addOnItemTouchListener(new OnItemClickListener() {
//            @Override
//            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//                Toast.makeText(WorkWorldActivity.this, Integer.toString(position), Toast.LENGTH_LONG).show();
//            }
//        });
        workWorldAdapter.setOnClickListener(onclick_one);
        workWorldAdapter.setOpenDetailsListener(openDetailsListener);


    }

    @SuppressLint("ResourceType")
    private void bindView() {
        workWorldHeadView = LayoutInflater.from(this).inflate(R.layout.atom_ui_work_world_head_view, null);
        headViewText = workWorldHeadView.findViewById(R.id.head_text);
        headViewLayout = workWorldHeadView.findViewById(R.id.head_layout);
        headViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                workworldhiddenHeadView();
                Intent intent = new Intent(WorkWorldActivity.this, WorkWorldNoticeActivityV2.class);
                intent.putExtra(NOTICE_COUNT, noticeCount);
                startActivity(intent);
//                Toast.makeText(WorkWorldActivity.this, "查看通知", Toast.LENGTH_LONG).show();
            }
        });
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);


        work_world_rc = (RecyclerView) findViewById(R.id.work_world_rc);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(0, 202, 190));
        work_world_rc.setLayoutManager(new LinearLayoutManager(this));
        work_world_rc.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, 1, R.color.atom_ui_primary_color));

        //设置刷新操作
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void refresh() {
        workWorldAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        workWorldPresenter.workworldstartRefresh();

//        new Request(1, new RequestCallBack() {
//
//
//            @Override
//            public void success(List<WorkWorldItem> data) {
//                workWorldAdapter.setNewData(data);
//                workWorldAdapter.setEnableLoadMore(true);
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//
//            @Override
//            public void fail(Exception e) {
//                workWorldAdapter.setEnableLoadMore(true);
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        }).start();
    }

    private void loadMore() {
        workWorldPresenter.workworldloadingMore();
    }


    private void bindData() {

//        datalist = new ArrayList<>();

//        mSwipeRefreshLayout.setRefreshing(true);//设置可开启
        if (getIntent().hasExtra(PbChatActivity.KEY_JID)) {
            setActionBarTitle("用户动态");
            searchUserIdstr = getIntent().getStringExtra(PbChatActivity.KEY_JID);
            workWorldPresenter = new WorkWorldManagerPresenter(this, searchUserIdstr);
        } else {
            setActionBarTitle("驼圈");
            workWorldPresenter = new WorkWorldManagerPresenter(this);
        }
        qtNewActionBar.setFocusableInTouchMode(true);
        qtNewActionBar.requestFocus();


        if (TextUtils.isEmpty(searchUserIdstr)) {
            setActionBarRightIcon(R.string.atom_ui_new_release);
            setActionBarRightIconSize(34);
            setActionBarRightIconColor(getResources().getColor(R.color.atom_ui_new_like_select));
            setActionBarRigthClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WorkWorldActivity.this, WorkWorldReleaseCircleActivity.class);
                    startActivityForResult(intent, ACTIVITY_RELEASE);
                }
            });
        }


        mNewActionBar.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if (work_world_rc != null) {
                    work_world_rc.scrollToPosition(0);
                }

            }

            @Override
            public void onSingleClick() {

            }
        }));

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        WorkWorldItem item = (WorkWorldItem) v.getTag();
        Intent intent = new Intent();
        intent.putExtra(Constants.BundleKey.WORK_WORLD_ITEM, (Serializable) item);
        menu.add(0, delete, 0, R.string.atom_ui_common_delete).setIntent(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACTIVITY_RELEASE:
                getDataShow(data);
                break;
            case ACTIVITY_DETAILS:
                updateDataItem(data);
                break;
        }
    }


    private void updateDataItem(Intent data){
        try{
            if(data!=null){
                WorkWorldItem item = (WorkWorldItem) data.getSerializableExtra(WORK_WORLD_RESULT_ITEM_BACK);
//                new String();
                for (int i = 0; i < workWorldAdapter.getData().size(); i++) {
                    if(workWorldAdapter.getData().get(i).getUuid().equals(item.getUuid())){
                        workWorldAdapter.getData().remove(i);
                        workWorldAdapter.getData().add(i,item);


                        workWorldAdapter.notifyItemChanged(i+workWorldAdapter.getHeaderLayoutCount());
                    }
                }
            }
        }catch (Exception e){
            Logger.i(e.getMessage());
        }
    }

    private void getDataShow(Intent data) {
        try {
            if (data != null) {
                //新版图片选择器
                ArrayList<WorkWorldItem> list = (ArrayList<WorkWorldItem>) data.getSerializableExtra(WORK_WORLD_RESULT_DATA);
                if (list != null && list.size() > 0) {
                    workWorldAdapter.setNewData(list);
//
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        work_world_rc.scrollToPosition(0);
                                    }
                                });
                            } catch (Exception e) {

                            }

                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(700);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        work_world_rc.scrollToPosition(0);
                                    }
                                });
                            } catch (Exception e) {

                            }

                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        work_world_rc.scrollToPosition(0);
                                    }
                                });
                            } catch (Exception e) {

                            }

                        }
                    }).start();

                }

            }
        } catch (Exception e) {
        }
    }

    @Override
    public void workworldshowNewData(List<WorkWorldItem> list) {
        if (list != null) {
            if (list.size() > 0) {
//            workWorldAdapter.setNewData(list);
//            work_world_rc.scrollToPosition(0);
                showNewDataHandle(list);
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    public void showNewDataHandle(final List<WorkWorldItem> list) {
        if (work_world_rc.isComputingLayout()) {
            work_world_rc.postDelayed(new Runnable() {
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
                    workWorldAdapter.setNewData(list);
//                    work_world_rc.scrollToPosition(0);
//                    work_world_rc.scrollTo(0,0);
                    mSwipeRefreshLayout.setRefreshing(false);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(500);
//                                work_world_rc.scrollToPosition(0);
//                            }catch (Exception e){
//
//                            }
//
//                        }
//                    }).start();
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(700);
//                                work_world_rc.scrollToPosition(0);
//                            }catch (Exception e){
//
//                            }
//
//                        }
//                    }).start();
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(1000);
//                                work_world_rc.scrollToPosition(0);
//                            }catch (Exception e){
//
//                            }
//
//                        }
//                    }).start();
                }
            });

        }

    }

    @Override
    public void workworldshowMoreData(List<WorkWorldItem> list) {

        showMoreDataHandle(list);
//            work_world_rc.scrollToPosition(0);


    }

    public void showMoreDataHandle(final List<WorkWorldItem> list) {
        if (work_world_rc.isComputingLayout()) {
            work_world_rc.postDelayed(new Runnable() {
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
                        workWorldAdapter.addData(list);
                        workWorldAdapter.loadMoreComplete();
                    } else {
                        workWorldAdapter.loadMoreEnd();
                    }
                }
            });

        }

    }


    @Override
    public int workworldgetListCount() {
        return workWorldAdapter.getItemCount();
    }

    @Override
    public void workworldstartRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public WorkWorldItem workworldgetLastItem() {

        if (workWorldAdapter.getData() != null && workWorldAdapter.getData().size() > 0) {
            return workWorldAdapter.getData().get(workWorldAdapter.getData().size() - 1);
        } else {
            return null;
        }

    }

    @Override
    public void workworldremoveWorkWorldItem(final WorkWorldDeleteResponse worldDeleteResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < workWorldAdapter.getData().size(); i++) {
                    if (workWorldAdapter.getData().get(i).getId().equals(worldDeleteResponse.getData().getId() + "")) {
                        workWorldAdapter.remove(i);
                    }
                }
            }
        });

    }

    @Override
    public void workworldshowHeadView(int count) {
        if (!TextUtils.isEmpty(searchUserIdstr)) {
            return;
        }
        if (!(workWorldAdapter.getHeaderViewsCount() > 0)) {
//            workWorldAdapter.removeAllHeaderView();
            workWorldAdapter.addHeaderView(workWorldHeadView);
        }
        noticeCount = count;
        headViewText.setText(count + "条消息");
    }

    @Override
    public String workworldgetSearchId() {
        return searchUserIdstr;
    }

    @Override
    public void workworldcloseRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean workworldisStartRefresh() {
        return false;
    }

    @Override
    public void scrollTop() {

    }

    @Override
    public void workworldhiddenHeadView() {
        workWorldAdapter.removeHeaderView(workWorldHeadView);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            final WorkWorldItem workWorldItem = (WorkWorldItem) item.getIntent().getSerializableExtra(Constants.BundleKey.WORK_WORLD_ITEM);
            switch (item.getItemId()) {
                //转发
                case delete:
                    workWorldPresenter.workworlddeleteWorkWorldItem(workWorldItem);
                    Toast.makeText(WorkWorldActivity.this, "删除", Toast.LENGTH_LONG).show();
//                KPSwitchConflictUtil.hidePanelAndKeyboard(mPanelRoot);
//                Intent selUser = new Intent(this, SearchUserActivity.class);
//                selUser.putExtra(Constants.BundleKey.IS_TRANS, true);
//                selUser.putExtra(Constants.BundleKey.TRANS_MSG, message);
//                startActivity(selUser);
                    break;
                //重发


            }
        } catch (Exception e) {

        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (workWorldPresenter != null) {
            workWorldPresenter.workworldremoveEvent();
        }
    }
}
