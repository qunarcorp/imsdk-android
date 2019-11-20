package com.qunar.im.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.MultiItemEntity;
import com.qunar.im.base.module.WorkWorldNoticeItem;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.RecycleViewDivider;
import com.qunar.im.ui.adapter.WorkWorldDetailsAdapter;
import com.qunar.im.ui.presenter.WorkWorldNoticePresenter;
import com.qunar.im.ui.presenter.impl.WorkWorldNoticeManagerPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldNoticeView;
import com.qunar.im.ui.view.recyclerview.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

public class WorkWorldNoticeFragment extends BaseFragment implements WorkWorldNoticeView {

    protected RecyclerView work_world_notice_rc;
    protected WorkWorldDetailsAdapter workWorldNoticeAdapter;
    protected WorkWorldNoticePresenter workWorldNoticePresenter ;
    protected SwipeRefreshLayout mSwipeRefreshLayout;//刷新组件

    private boolean isMindMessage = true;//我的消息情况或者是我的回复情况

    public static String isMindMessageState = "isMindMessageState";

    public static String NOTICE_COUNT = "NOTICE_CUONT";

    private int noticeCount= 0;

    public boolean canLoadMore = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_work_world_notice_activity, null, false);

        bindView(view);
        bindData();
        initAdapter();
        workWorldNoticePresenter.loadingHistory();
        return view;
    }

    private void initAdapter() {
        workWorldNoticeAdapter = new WorkWorldDetailsAdapter(new ArrayList<MultiItemEntity>(),getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.atom_ui_empty_work_world,null);
        TextView emptyText = view.findViewById(R.id.empty_tip);
        workWorldNoticeAdapter.setEmptyView(view);
            emptyText.setText("暂时没有动态");
        work_world_notice_rc.setAdapter(workWorldNoticeAdapter);
        work_world_notice_rc.addItemDecoration(new RecycleViewDivider(
                getContext(), LinearLayoutManager.VERTICAL, 3, getResources().getColor(R.color.atom_ui_light_gray_DD)));
        if(!isMindMessage){
            workWorldNoticeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    if(canLoadMore){
                        loadMore();
                    }

                }
            });
        }

        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(0, 202, 190));

    }

    private void loadMore() {
        workWorldNoticePresenter.loadingMore();
    }

    private void bindData() {
       if( getActivity().getIntent().hasExtra(isMindMessageState)){
           isMindMessage = getActivity().getIntent().getBooleanExtra(isMindMessageState,true);
        }
        workWorldNoticePresenter = new WorkWorldNoticeManagerPresenter();
        workWorldNoticePresenter.setView(this);
        noticeCount = getActivity().getIntent().getIntExtra(NOTICE_COUNT,0);
        work_world_notice_rc.setLayoutManager(new LinearLayoutManager(getContext()));
        if(!isMindMessage){
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
        }else{
            mSwipeRefreshLayout.setEnabled(false);
        }

    }

    private void bindView(View view ){
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        work_world_notice_rc = (RecyclerView) view.findViewById(R.id.work_world_notice_rc);

    }

    public void refresh() {
        Logger.i("进行了一次刷新");
        if(workWorldNoticeAdapter!=null){
            workWorldNoticeAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        }

        if(workWorldNoticePresenter!=null){
            workWorldNoticePresenter.startRefresh();
        }





    }



    @Override
    public void showNewData(List<? extends MultiItemEntity> list) {
//        if (list != null && list.size() > 0) {
////            workWorldAdapter.setNewData(list);
////            work_world_rc.scrollToPosition(0);
//            showNewDataHandle(list);
//        }

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

    public void showMoreDataHandle(final List<? extends MultiItemEntity> list) {
        if (work_world_notice_rc.isComputingLayout()) {
            work_world_notice_rc.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMoreDataHandle(list);
                }
            }, 500);
        } else {
            if(getActivity()!=null&&!getActivity().isFinishing()){
                getActivity().runOnUiThread(new Runnable() {
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

    }

    @Override
    public void showMoreData(List<? extends MultiItemEntity> list) {
        showMoreDataHandle(list);
    }



    public void showNewDataHandle(final List<? extends MultiItemEntity> list) {
//        if (work_world_notice_rc.isComputingLayout()) {
//            work_world_notice_rc.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    showNewDataHandle(list);
//                }
//            }, 500);
//        } else {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////
//                    workWorldNoticeAdapter.setNewData((List<MultiItemEntity>) list);
//                    work_world_notice_rc.scrollToPosition(0);
//                }
//            });
//
//        }


        if (work_world_notice_rc.isComputingLayout()) {
            work_world_notice_rc.postDelayed(new Runnable() {
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
                    workWorldNoticeAdapter.setNewData((List<MultiItemEntity>)list);
//                    work_world_rc.scrollToPosition(0);
//                    work_world_rc.scrollTo(0,0);
                    mSwipeRefreshLayout.setRefreshing(false);

                    if(!canLoadMore){
                        canLoadMore = true;
                    }
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
        if (workWorldNoticeAdapter.getData() != null && workWorldNoticeAdapter.getData().size() > 0) {
            return (WorkWorldNoticeItem) workWorldNoticeAdapter.getData().get(workWorldNoticeAdapter.getData().size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public boolean isMindMessage() {
        return isMindMessage;
    }

    @Override
    public void startRefresh() {

    }

    @Override
    public void closeRefresh() {
        if(getActivity()!=null&&!getActivity().isFinishing()){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }

    }

    @Override
    public int getListCount() {
        return workWorldNoticeAdapter.getItemCount();
    }
}
