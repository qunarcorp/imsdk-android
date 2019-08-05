package com.qunar.im.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.IFriendsManagePresenter;
import com.qunar.im.ui.presenter.factory.FriendsManagerFactory;
import com.qunar.im.ui.presenter.views.IFriendsManageView;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.view.multilLevelTreeView.Node;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.SimpleTreeAdapter;
import com.qunar.im.ui.view.multilLevelTreeView.TreeListViewAdapter;
import com.qunar.im.base.protocol.NativeApi;

import java.util.List;
import java.util.Map;


/**
 * Created by zhao.liu on 2014/8/21.
 */
public class DeptFragment extends BaseFragment implements IFriendsManageView{
    private static final String TAG = DeptFragment.class.getSimpleName();
    PullToRefreshListView pullToRefreshView;
    TextView empty;

    IFriendsManagePresenter presenter;
    String rootName;

    TreeListViewAdapter mAdapter;
    boolean canStartPresenceUpdate = false;

    public static final String FROM_ACTION = "fromaction";
    public  int fromAction = 0; //1 表示会话转移

    @Override
    public void onStart() {
        super.onStart();
        getDeptFromURL();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootName = getString(R.string.atom_ui_dept_root_name);
        presenter = FriendsManagerFactory.getFriendManagerPresenter();
        presenter.setFriendsView(this);
        Bundle bundle = getArguments();
        if(bundle!=null){
            fromAction =bundle.getInt(FROM_ACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_contacts, container, false);
        pullToRefreshView = (PullToRefreshListView) view.findViewById(R.id.pull_to_refresh_listview);
        empty = (TextView) view.findViewById(R.id.empty);
        initViews();
        return view;
    }

    void initViews() {
        pullToRefreshView.setVisibility(View.VISIBLE);
        if (mAdapter == null) {
            try {
                mAdapter = new SimpleTreeAdapter<Node>(pullToRefreshView, getActivity());
                mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                    @Override
                    public void onClick(Node node, int position) {
                        if (!TextUtils.isEmpty(node.getKey())) {
                            if (fromAction == 1) {
                                Intent intent = new Intent();
                                intent.putExtra("userid", node.getKey());
                                getActivity().setResult(Activity.RESULT_OK, intent);
                                getActivity().finish();
                                return;
                            }
                            NativeApi.openUserCardVCByUserId(node.getKey());
                        }
                    }

                });
            } catch (IllegalAccessException e) {
                LogUtil.e(TAG,"ERROR",e);
            }

        }
        pullToRefreshView.setAdapter(mAdapter);
        pullToRefreshView.setEmptyView(empty);
        pullToRefreshView.setVisibility(View.VISIBLE);
        pullToRefreshView.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.forceUpdateContacts();
            }
        });
    }


    //从数据库获取组织架构
    void getDeptFromURL() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                presenter.updateContacts();
            }
        });

    }

    @Override
    public void setFrineds(final Map<Integer, List<Node>> contacts) {
        if (contacts.size() > 0) {
            LogUtil.d("debug", "set adapter");
            if (mAdapter != null) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        canStartPresenceUpdate = true;
                        mAdapter.setNodes(contacts);
                    }
                });
            }
        }
    }

    @Override
    public void setBuddyFrineds(Map<Integer, List<Nick>> contacts) {

    }

    @Override
    public boolean isTransfer() {
        return fromAction == 1;
    }

//    @Override
//    public void setFrineds(Map<Integer, List<Nick>> contacts) {
//
//    }

    @Override
    public void resetListView() {

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (pullToRefreshView != null)
                    pullToRefreshView.onRefreshComplete();
            }
        });
    }

    @Override
    public String getRootName() {
        return rootName;
    }

}
