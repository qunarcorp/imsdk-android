package com.qunar.im.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.IFriendsManagePresenter;
import com.qunar.im.ui.presenter.impl.BuddyPresenter;
import com.qunar.im.ui.presenter.views.IFriendsManageView;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.view.multilLevelTreeView.Node;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.PersonalInfoActivity;
import com.qunar.im.ui.adapter.BuddyAdapter;
import com.qunar.im.ui.view.ContactHeaderView;
import com.qunar.im.ui.view.indexlistview.IndexableListView;

import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;


/**
 * 好友列表
 */
public class BuddiesFragment extends BaseFragment implements IFriendsManageView {
    IndexableListView indexableListView;

    TextView empty;

    IFriendsManagePresenter buddyPresenter;
    String rootName;

    BuddyAdapter mAdapter;
    HandleDeptEvent handleDeptEvent = new HandleDeptEvent();
    boolean canStartPresenceUpdate = false;
    ContactHeaderView headerView;

    @Override
    public void onStart() {
        super.onStart();
        buddyPresenter.setFriendsView(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initHeader();
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                buddyPresenter.updateContacts();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootName = getString(R.string.atom_ui_dept_root_name);
        buddyPresenter = new BuddyPresenter();
        EventBus.getDefault().register(handleDeptEvent);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(handleDeptEvent);
        super.onDestroy();
    }

    void initListViewHeader() {
        headerView = new ContactHeaderView(getActivity());
        indexableListView.addHeaderView(headerView, null, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_friends, container, false);
        indexableListView = (IndexableListView) view.findViewById(R.id.pull_to_refresh_listview);
        empty = (TextView) view.findViewById(R.id.empty);
        initViews();
        return view;
    }

    void initViews() {
        initListViewHeader();
        if (mAdapter == null) {
            mAdapter = new BuddyAdapter(getActivity(), null, R.layout.atom_ui_item_friends_member);
        }
        indexableListView.setAdapter(mAdapter);
//        indexableListView.setEmptyView(empty);
        indexableListView.alwaysShowScroll(true);
        indexableListView.setVisibility(View.VISIBLE);
        indexableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Nick node = (Nick) indexableListView.getAdapter().getItem(position);
                if (!TextUtils.isEmpty(node.getXmppId())) {
                    Intent intent = new Intent(getContext(), PersonalInfoActivity.class);
                    intent.putExtra("jid", node.getXmppId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void setFrineds(final Map<Integer, List<Node>> contacts) {

    }

    @Override
    public void setBuddyFrineds(final Map<Integer, List<Nick>> contacts) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
//                if (contacts.size() > 0) {
                if (mAdapter != null) {
                    canStartPresenceUpdate = true;
                    mAdapter.setNodes(contacts);
                    initHeader();
                }
//                }
            }
        });
    }

    @Override
    public boolean isTransfer() {
        return false;
    }

    @Override
    public void resetListView() {

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (indexableListView != null)
                    indexableListView.setSelection(0);
            }
        });
    }

    @Override
    public String getRootName() {
        return rootName;
    }

    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    private void initHeader()
    {
        headerView.changeChatRoomVisible(CommonConfig.showQchatGroup);
        if(CommonConfig.isQtalk){
            headerView.changeRobotVisible(true);
        }else {
            headerView.changeRobotVisible(false);
        }
        if (com.qunar.im.protobuf.common.CurrentPreference.getInstance().isMerchants()) {
            headerView.changeOrgVisible(true);
        }else if(CommonConfig.isQtalk){
            headerView.changeOrgVisible(QtalkNavicationService.getInstance().isShowOrganizational());
        }
//        else if(!com.qunar.im.protobuf.common.CurrentPreference.getInstance().isMerchants()&&
//                !CommonConfig.showQchatGroup){
//            headerView.setLayoutParams(new AbsListView.
//                    LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
//        }
    }

    class HandleDeptEvent {

        public void onEventMainThread(EventBusEvent.ShowGroupEvent showGroupEvent) {
            if(!CommonConfig.isQtalk)
            {
                headerView.changeChatRoomVisible(showGroupEvent.isShow);
//                if(showGroupEvent.isShow)
//                {
//                    headerView.setLayoutParams(new AbsListView.
//                            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                            Utils.dpToPx(getContext(),78)));
//                }
//                else {
//                    headerView.setLayoutParams(new AbsListView.
//                            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
//                }
            }
        }

        public void onEventMainThread(EventBusEvent.FriendsChange message) {
            if (message.result) {
                buddyPresenter.forceUpdateContacts();
            }
        }
    }


}
