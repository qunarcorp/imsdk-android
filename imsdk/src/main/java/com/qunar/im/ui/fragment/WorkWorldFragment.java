package com.qunar.im.ui.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.ReleaseContentData;
import com.qunar.im.base.module.WorkWorldDeleteResponse;
import com.qunar.im.base.module.WorkWorldItem;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.WorkWorldNoticeActivityV2;
import com.qunar.im.ui.activity.WorkWorldReleaseCircleActivity;
import com.qunar.im.ui.activity.WorkWorldDetailsActivity;
import com.qunar.im.ui.adapter.RecycleViewDivider;
import com.qunar.im.ui.adapter.WorkWorldAdapter;
import com.qunar.im.ui.presenter.WorkWorldPresenter;
import com.qunar.im.ui.presenter.impl.WorkWorldManagerPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldView;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.WorkWorldSpannableTextView;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.qunar.im.ui.activity.WorkWorldDetailsActivity.WORK_WORLD_DETAILS_ITEM;
import static com.qunar.im.ui.activity.WorkWorldNoticeActivity.NOTICE_COUNT;

public class WorkWorldFragment extends BaseFragment implements WorkWorldView {

    protected static final int delete = 0x01;
    public static String ISSTARTREFRESH = "ISSTARTREFRESH";

    protected QtNewActionBar qtNewActionBar;//头部导航
    protected RecyclerView work_world_rc;
    protected SwipeRefreshLayout mSwipeRefreshLayout;//刷新组件
    protected WorkWorldAdapter workWorldAdapter;
    protected View workWorldHeadView;
    protected TextView headViewText;
    protected LinearLayout headViewLayout;
    protected FloatingActionButton floatbutton;
    private LinearLayoutManager mRcManager;
    private BottomSheetDialog bottomSheetDialog;
    private View focus;

    private OnRefresh onRefresh;

    private int noticeCount = 0;

    private String searchUserIdstr = "";

    private boolean startRefresh = false;

    public static final String WorkWordJID = "WorkWordJID";

    //    protected List<WorkWorldItem> datalist;
    public static final int ACTIVITY_RELEASE = 88;
    public static final int ACTIVITY_DETAILS = 89;
    public static final String WORK_WORLD_RESULT_DATA = "WORK_WORLD_RESULT_DATA";
    public static final String WORK_WORLD_RESULT_ITEM_BACK = "WORK_WORLD_RESULT_ITEM_BACK";


    private View.OnClickListener openDetailsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final WorkWorldItem item = (WorkWorldItem) v.getTag();
            Intent intent = new Intent(getActivity(), WorkWorldDetailsActivity.class);
            intent.putExtra(WORK_WORLD_DETAILS_ITEM, (Serializable) item);
            startActivityForResult(intent, ACTIVITY_DETAILS);
        }
    };

    //单击显示ContextMenu
    private View.OnClickListener onclick_one = new View.OnClickListener() {
        @Override
        public void onClick(final View cView) {
//            v.showContextMenu();
            final WorkWorldItem item = (WorkWorldItem) cView.getTag();

//
            String selfUserId = CurrentPreference.getInstance().getUserid();
            boolean showCopy = true;
            if (!(cView instanceof WorkWorldSpannableTextView)) {
                showCopy = false;
            }

            bottomSheetDialog = new BottomSheetDialog(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.atom_ui_work_world_special_popwindow, null);

//            TextView delete =
            TextView delete = view.findViewById(R.id.work_world_popwindow_delete);
            TextView reply = view.findViewById(R.id.work_world_popwindow_reply);
            TextView cancle = view.findViewById(R.id.work_world_popwindow_cancle);
            TextView copy = view.findViewById(R.id.work_world_popwindow_copy);

            if (!selfUserId.equals(item.getOwner())) {
                delete.setVisibility(View.GONE);
            }
            if (showCopy) {
                copy.setVisibility(View.VISIBLE);
            } else {
                copy.setVisibility(View.GONE);
            }
            reply.setVisibility(View.GONE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    workWorldPresenter.workworlddeleteWorkWorldItem(item);
                    Toast.makeText(getActivity(), "删除", Toast.LENGTH_LONG).show();
                    bottomSheetDialog.dismiss();
                }
            });
            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", ((WorkWorldSpannableTextView)cView).getText());
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(getActivity(), "复制", Toast.LENGTH_LONG).show();
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
            bottomSheetDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet)
                    .setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));


            bottomSheetDialog.show();


        }
    };

    private WorkWorldPresenter workWorldPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public void setOnRefresh(OnRefresh onRefresh) {
        this.onRefresh = onRefresh;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.atom_ui_work_world_activity, null, false);
//        setContentView(R.layout.atom_ui_work_world_activity);
        bindView(view);
        bindData();
        initAdapter();
        workWorldPresenter.workworldloadingHistory();
        workWorldPresenter.workworldloadingNoticeCount();
        return view;
    }

    @SuppressLint("ResourceType")
    private void bindView(View view) {
        workWorldHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.atom_ui_work_world_head_view, null);
        headViewText = workWorldHeadView.findViewById(R.id.head_text);
        headViewLayout = workWorldHeadView.findViewById(R.id.head_layout);
        headViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                workworldhiddenHeadView();
                Intent intent = new Intent(getActivity(), WorkWorldNoticeActivityV2.class);
                intent.putExtra(NOTICE_COUNT, noticeCount);
                startActivity(intent);
//                Toast.makeText(WorkWorldActivity.this, "查看通知", Toast.LENGTH_LONG).show();
            }
        });
//        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
//        setNewActionBar(qtNewActionBar);

        focus = view.findViewById(R.id.focus);

        floatbutton = view.findViewById(R.id.floatbutton);
        floatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkWorldReleaseCircleActivity.class);
                startActivityForResult(intent, ACTIVITY_RELEASE);
            }
        });

        work_world_rc = (RecyclerView) view.findViewById(R.id.work_world_rc);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(0, 202, 190));
        mRcManager = new LinearLayoutManager(getActivity());
        work_world_rc.setLayoutManager(mRcManager);
        work_world_rc.addItemDecoration(new RecycleViewDivider(
                getActivity(), LinearLayoutManager.VERTICAL, 1, R.color.atom_ui_primary_color));

        //设置刷新操作
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

//        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void bindData() {

//        datalist = new ArrayList<>();

        if (getActivity().getIntent().hasExtra(ISSTARTREFRESH)) {
//            setActionBarTitle("用户动态");
            startRefresh = getActivity().getIntent().getBooleanExtra(ISSTARTREFRESH, false);
//            searchUserIdstr = getActivity().getIntent().getStringExtra(PbChatActivity.KEY_JID);
//            workWorldPresenter = new WorkWorldManagerPresenter(this, searchUserIdstr);
        } else {
//            setActionBarTitle("驼圈");
//            workWorldPresenter = new WorkWorldManagerPresenter(this);
            startRefresh = false;
        }


//        mSwipeRefreshLayout.setRefreshing(true);//设置可开启
        if (getActivity().getIntent().hasExtra(WorkWordJID)) {
//            setActionBarTitle("用户动态");
            searchUserIdstr = getActivity().getIntent().getStringExtra(WorkWordJID);
            workWorldPresenter = new WorkWorldManagerPresenter(this, searchUserIdstr);
        } else {
//            setActionBarTitle("驼圈");
            workWorldPresenter = new WorkWorldManagerPresenter(this);
        }
//        qtNewActionBar.setFocusableInTouchMode(true);
//        qtNewActionBar.requestFocus();
        focus.setFocusableInTouchMode(true);
        focus.requestFocus();

        if (TextUtils.isEmpty(searchUserIdstr)) {
            floatbutton.setVisibility(View.VISIBLE);
//            setActionBarRightIcon(R.string.atom_ui_new_release);
//            setActionBarRightIconSize(34);
//            setActionBarRightIconColor(getResources().getColor(R.color.atom_ui_new_like_select));
//            setActionBarRigthClick(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), ReleaseCircleActivity.class);
//                    startActivityForResult(intent, ACTIVITY_RELEASE);
//                }
//            });
        } else {
            if (CurrentPreference.getInstance().getPreferenceUserId().equals(searchUserIdstr)) {
                floatbutton.setVisibility(View.VISIBLE);
            } else {
                floatbutton.setVisibility(View.GONE);
            }
        }


//        mNewActionBar.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
//            @Override
//            public void onDoubleClick() {
//                if (work_world_rc != null) {
//                    work_world_rc.scrollToPosition(0);
//                }
//
//            }
//
//            @Override
//            public void onSingleClick() {
//
//            }
//        }));

    }

    @Override
    public void scrollTop() {
        if (work_world_rc != null) {
            if(workWorldAdapter.getItemCount()>5){
                work_world_rc.scrollToPosition(5);
            }
            if(getActivity()!=null){
                LinearSmoothScroller smoothScroller = new WorkWorldDetailsActivity.TopSmoothScroller(getActivity());
                smoothScroller.setTargetPosition(0);
                mRcManager.startSmoothScroll(smoothScroller);
            }else{
                work_world_rc.smoothScrollToPosition(0);
            }

//
        }

    }

    private void initAdapter() {
        workWorldAdapter = new WorkWorldAdapter(getActivity(), work_world_rc);

        workWorldAdapter.setHeaderAndEmpty(true);
        workWorldAdapter.bindToRecyclerView(work_world_rc);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.atom_ui_empty_work_world, null);
        TextView emptyText = view.findViewById(R.id.empty_tip);
        workWorldAdapter.setEmptyView(view);
        if (TextUtils.isEmpty(searchUserIdstr)) {
            emptyText.setText("暂时没有动态");
        } else {
            emptyText.setText("暂时没有动态");
        }

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

    private void loadMore() {
        workWorldPresenter.workworldloadingMore();
    }

    public void refresh() {
        Logger.i("进行了一次刷新");
        if (workWorldAdapter != null) {
            workWorldAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        }

        if (workWorldPresenter != null) {
            workWorldPresenter.workworldstartRefresh();
        }


        if (onRefresh != null) {
            onRefresh.refreshTime(System.currentTimeMillis());
        }


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

    @Override
    public void workworldshowNewData(final List<WorkWorldItem> list) {
        Logger.i("拿到了刷新数据");

        if (list != null) {
            if (list.size() > 0) {
//            workWorldAdapter.setNewData(list);
//            work_world_rc.scrollToPosition(0);
                showNewDataHandle(list);
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        workWorldAdapter.setNewData(list);

                    }
                });
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);

                }
            });
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
            getActivity().runOnUiThread(new Runnable() {
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

    public void showMoreDataHandle(final List<WorkWorldItem> list) {
        if (work_world_rc.isComputingLayout()) {
            work_world_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMoreDataHandle(list);
                }
            }, 500);
        } else {
            getActivity().runOnUiThread(new Runnable() {
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
    public void workworldshowMoreData(List<WorkWorldItem> list) {
        showMoreDataHandle(list);
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
        getActivity().runOnUiThread(new Runnable() {
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

        if (mRcManager.findFirstCompletelyVisibleItemPosition() < 3) {
            work_world_rc.smoothScrollToPosition(0);
        }
    }

    @Override
    public void workworldhiddenHeadView() {
        workWorldAdapter.removeHeaderView(workWorldHeadView);
    }

    @Override
    public String workworldgetSearchId() {
        return searchUserIdstr;
    }

    @Override
    public void workworldcloseRefresh() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

        }

    }

    @Override
    public boolean workworldisStartRefresh() {
        return startRefresh;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACTIVITY_RELEASE:
                if (TextUtils.isEmpty(searchUserIdstr)) {
                    getDataShow(data);
                } else {
                    refresh();
                }

                break;
            case ACTIVITY_DETAILS:
                updateDataItem(data);
                break;
        }
    }


    private void updateDataItem(Intent data) {
        try {
            if (data != null) {
                WorkWorldItem item = (WorkWorldItem) data.getSerializableExtra(WORK_WORLD_RESULT_ITEM_BACK);
//                new String();
                for (int i = 0; i < workWorldAdapter.getData().size(); i++) {
                    if (workWorldAdapter.getData().get(i).getUuid().equals(item.getUuid())) {
                        workWorldAdapter.getData().remove(i);
                        workWorldAdapter.getData().add(i, item);


                        workWorldAdapter.notifyItemChanged(i + workWorldAdapter.getHeaderLayoutCount());
                    }
                }
            }
        } catch (Exception e) {
            Logger.i(e.getMessage());
        }
    }

    private void getDataShow(Intent data) {
        if (data != null) {
            //新版图片选择器
            ArrayList<WorkWorldItem> list = (ArrayList<WorkWorldItem>) data.getSerializableExtra(WORK_WORLD_RESULT_DATA);
            if (list != null && list.size() > 0) {
                workWorldAdapter.setNewData(list);
                work_world_rc.scrollToPosition(0);

            }
        }
    }


    public interface OnRefresh {
        void refreshTime(long time);
    }
}
