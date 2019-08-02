package com.qunar.im.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.ChatRoom;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.IChatRoomManagePresenter;
import com.qunar.im.ui.presenter.views.IChatRoomListView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.ChatroomInvitationActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.adapter.ChatRoomListAdapter;
import com.qunar.im.ui.view.indexlistview.IndexableListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhao.liu on 2014/8/21.
 */
public class ChatRoomFragment extends BaseFragment implements IChatRoomListView {
    IndexableListView group_list;

    TextView txt_create_chatroom;

    IChatRoomManagePresenter presenter;
    ChatRoomListAdapter groupsAdapter;

    // 消息转发
    boolean isSelectTransUser;
    Serializable transMsg;
    HandleChatRoom handleChatRoom = new HandleChatRoom();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(presenter == null)
        {

        }

        Bundle args = getArguments();
        if (null!=args) {
            isSelectTransUser = args.getBoolean(Constants.BundleKey.IS_TRANS, false);
            if (isSelectTransUser) {
                transMsg = args.getSerializable(Constants.BundleKey.TRANS_MSG);
            }
        }
        EventBus.getDefault().register(handleChatRoom);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_groups, container, false);
        group_list = (IndexableListView) view.findViewById(R.id.group_list);
        initViews();
        return view;
    }

    void initHeader()
    {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.atom_ui_layout_chatroom_header, null, false);
        txt_create_chatroom = (TextView) headerView.findViewById(R.id.txt_create_chatroom);
        group_list.addHeaderView(headerView, null, false);
    }

    void initViews()
    {
        initHeader();
        if(groupsAdapter== null)
        {
            groupsAdapter = new ChatRoomListAdapter(this.getActivity(),new ArrayList<ChatRoom>(),R.layout.atom_ui_group_child_item);//new ChatRoomListAdapter(this.getActivity());
        }
        group_list.setAdapter(groupsAdapter);
        group_list.alwaysShowScroll(true);
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                listMyGroups();
            }
        });
        txt_create_chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatroomInvitationActivity.class);
                intent.putExtra("roomId", "");
                intent.putExtra("userId", "");
                intent.putExtra("actionType", ChatroomInvitationActivity.ACTION_TYPE_CHREATE_GROUP);
                getActivity().startActivity(intent);
            }
        });
        group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getItemAtPosition(position);
                if(obj!=null) {
                    Nick chatRoom = (Nick) obj;
                    if(!TextUtils.isEmpty(chatRoom.getGroupId())) {
                        if (isSelectTransUser) {
                            EventBus.getDefault().post(new EventBusEvent.SendTransMsg(transMsg,
                                    chatRoom.getGroupId()));
                            getActivity().finish();
                        } else {
//                            if (chatRoom.getIsJoined() == ChatRoom.JOINED) {
                                Intent i = new Intent(getContext(), PbChatActivity.class);
                                i.putExtra(PbChatActivity.KEY_JID,chatRoom.getGroupId());
                                i.putExtra(PbChatActivity.KEY_IS_CHATROOM, true);
                                getActivity().startActivity(i);
//                            } else {
//                                Intent i = new Intent(getContext(), ChatroomInfoActivity.class);
//                                i.putExtra("roomId", chatRoom.getJid());
//                                getActivity().startActivity(i);
//                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(handleChatRoom);
        presenter.clearTemporaryRoom();
        super.onDestroy();
    }

    void listMyGroups()
    {
        presenter.listGroups();
    }

    @Override
    public void setGroupList(final SparseArray<List<Nick>> groups) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                groupsAdapter.setDatas(groups);
            }
        });
    }

    @Override
    public void resetListView() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                group_list.setSelection(0);
            }
        });
    }

    @Override
    public Context getContext() {
        return getActivity().getApplication();
    }

    class HandleChatRoom {
        public void onEventMainThread(EventBusEvent.RefreshChatroom refreshChatroom) {
            getActivity().finish();
        }
    }
}
