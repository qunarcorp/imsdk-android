package com.qunar.im.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.RecentConversation;
import com.qunar.im.base.structs.EncryptMessageType;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.DataCenter;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.core.utils.GlobalConfigManager;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.BackgroundTipActivity;
import com.qunar.im.ui.activity.CollectionActivity;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.QunarWebActvity;
import com.qunar.im.ui.activity.RobotChatActivity;
import com.qunar.im.ui.activity.RobotExtendChatActivity;
import com.qunar.im.ui.activity.TabMainActivity;
import com.qunar.im.ui.adapter.RecentConvsAdapter;
import com.qunar.im.ui.presenter.IChatingPanelPresenter;
import com.qunar.im.ui.presenter.IConversationsManagePresenter;
import com.qunar.im.ui.presenter.impl.ChatingPanelPresenter;
import com.qunar.im.ui.presenter.impl.PbConversationManagePresenter;
import com.qunar.im.ui.presenter.views.IConversationListView;
import com.qunar.im.ui.presenter.views.IRefreshConversation;
import com.qunar.im.ui.presenter.views.ITopMeesageView;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.utils.ConnectionUtil;

import java.util.List;
import java.util.Map;


/**
 * Created by zhao.liu on 2014/8/21.
 */
public class ConversationFragment extends BaseFragment implements IConversationListView, IRefreshConversation {
    private static final String TAG = "ConversationFragment";
    // 为每个菜单定义一个标识
    final int MENU1 = 0x111;
    final int MENU2 = 0x112;
    final int MENU3 = 0x113;
    final int MENU4 = 0x114;
    final int MENU5 = 0x115;
    final int MENU6 = 0X116;
    ListView list;
    View header;
    TextView tvEmpty;
    private boolean isFirstShow = true;
    IConversationsManagePresenter convPresenter;
    IChatingPanelPresenter panelPresenter;
    RecentConvsAdapter recentConvsAdapter;
//    RVRecentConvsAdapter recentConvsAdapter;
    //    private boolean readAllConversations = true;
//    private ShakeEventHandler shakeEventHandler;
    private String XmppId;
    private String RealUserId;
    //    HandleConvEvent handleConvEvent = new HandleConvEvent();
//    IChatRoomManagePresenter chatRoomManagePresenter;
    private AlertDialog encryptDialog;
    private String currentJid;
    private boolean isOnlyUnRead = false;

    private RecentConversation mCurrentConv;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() == null) {
            return;
        }
        isOnlyUnRead = getArguments().getBoolean("isOnlyUnRead");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.atom_ui_fragment_conversation, null, false);
        list = (ListView) view.findViewById(R.id.list);
        tvEmpty = (TextView) view.findViewById(R.id.empty);
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        currentJid = null;
        convPresenter.showRecentConvs();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        convPresenter.removeEvent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        convPresenter = new PbConversationManagePresenter();

        convPresenter.setCoversationListView(this);
        panelPresenter = new ChatingPanelPresenter();
    }


    void initViews() {

        if (recentConvsAdapter == null) {
            recentConvsAdapter = new RecentConvsAdapter(getActivity());
        }
        list.setAdapter(recentConvsAdapter);

        list.setOnCreateContextMenuListener((menu, v, menuInfo) -> {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int position = info.position - list.getHeaderViewsCount();
            if(position < 0){
                return;
            }
            RecentConversation rc = recentConvsAdapter.getItem(position);
            if (rc.getConversationType() == 1 || rc.getConversationType() == 0 || rc.getConversationType() == 3) {


                if (rc.getTop() == 0) {
                    menu.add(0, MENU4, 0, R.string.atom_ui_menu_sticky_on_top);
                } else {
                    menu.add(0, MENU4, 0, R.string.atom_ui_menu_remove_from_top);
                }
            }
            //群才显示
            if (rc.getConversationType() == 1) {
                if (rc.getRemind() == 0) {
                    menu.add(0, MENU6, 0, R.string.atom_ui_menu_mute_notification);
                } else {
                    menu.add(0, MENU6, 0, R.string.atom_ui_menu_open_notification);
                }
            }
            if (rc.getUnread_msg_cont() == 0) {
//                    menu.add(0, MENU3, 0, R.string.atom_ui_menu_mark_asunread);
            } else {
                menu.add(0, MENU3, 0, R.string.atom_ui_menu_mark_asread);
            }
            menu.add(0, MENU2, 0, R.string.atom_ui_menu_delete_conversation);
        });

        list.setOnItemClickListener((parent, view, position, id) -> {
            if(position - list.getHeaderViewsCount() < 0){//防止点击header 数组越界crash
                return;
            }
            recentConvClick(recentConvsAdapter.getItem(position - list.getHeaderViewsCount()), view);
        });

        header = LayoutInflater.from(getActivity()).inflate(R.layout.atom_ui_header_file_sharing,null);
        header.findViewById(R.id.atom_ui_home_pc_layout).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BackgroundTipActivity.class);
            startActivity(intent);
        });
        header.findViewById(R.id.atom_ui_home_search_layout).setOnClickListener(v -> {
            if(getActivity() != null){
                ((TabMainActivity)getActivity()).startSearchActivity();
            }
        });
        ((TextView)header.findViewById(R.id.atom_ui_file_sharing_text)).setText(getString(R.string.atom_ui_desktop) + GlobalConfigManager.getAppName() + getString(R.string.atom_ui_logged));
        if(getActivity() instanceof TabMainActivity){//未读消息的activity不展示搜索header
            list.addHeaderView(header);
        }
    }


    void recentConvClick(final RecentConversation item, View view) {
//        readAllConversations = true;
        Intent intent = null;
        switch (item.getConversationType()) {

            case ConversitionType.MSG_TYPE_COLLECTION:
                intent = new Intent(getContext(), CollectionActivity.class);
                startActivity(intent);
                break;
            case ConversitionType.MSG_TYPE_CONSULT:
            case ConversitionType.MSG_TYPE_CONSULT_SERVER:
            case ConversitionType.MSG_TYPE_GROUP:
            case ConversitionType.MSG_TYPE_CHAT:

                intent = new Intent(getContext(), PbChatActivity.class);
                intent.putExtra(PbChatActivity.KEY_UNREAD_MSG_COUNT,item.getUnread_msg_cont());
                currentJid = item.getId();
                //设置jid 就是当前会话对象

                intent.putExtra(PbChatActivity.KEY_JID, item.getId());
                if (item.getConversationType() == 4) {

                } else if (item.getConversationType() == 5) {
                    intent.putExtra(PbChatActivity.KEY_REAL_JID, item.getRealUser());
                } else {
                    intent.putExtra(PbChatActivity.KEY_REAL_JID, item.getRealUser());
                }
                intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, item.getConversationType() + "");

                //设 置是否是群聊
                boolean isChatRoom = item.getConversationType() == ConversitionType.MSG_TYPE_GROUP;
                intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, isChatRoom);

                intent.putExtra(PbChatActivity.KEY_IS_REMIND,item.getRemind() == 0);

                startActivity(intent);
                break;
            case ConversitionType.MSG_TYPE_HEADLINE://qtalk的系统通知消息
                intent = new Intent(getContext(), RobotExtendChatActivity.class);
                intent.putExtra(PbChatActivity.KEY_JID, item.getId());
                //设置真实id
                intent.putExtra(PbChatActivity.KEY_REAL_JID, item.getRealUser());
                //设置是否是群聊
                intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);

                intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, item.getConversationType() + "");
                intent.putExtra(PbChatActivity.KEY_UNREAD_MSG_COUNT,item.getUnread_msg_cont());
                startActivity(intent);
                break;
            case ConversitionType.MSG_TYPE_SUBSCRIPT://qchat的系统通知公告抢单消息 和 qtalk的订阅号消息
                if (item.getId().contains("rbt-qiangdan")) {//qchat抢单消息、众包消息
                    intent = new Intent(getContext(), QunarWebActvity.class);
                    intent.setData(Uri.parse(QtalkNavicationService.getInstance().getqGrabOrder()));
                    intent.putExtra(QunarWebActvity.IS_HIDE_BAR, true);
                    startActivity(intent);
                    //抢单消息 特殊处理 点击后 设置成已读
                    ConnectionUtil.getInstance().sendSingleAllRead(item.getId(),item.getId(),MessageStatus.STATUS_SINGLE_READED + "");
                } else if (item.getId().contains("rbt-system") || item.getId().contains("rbt-notice")) {//qchat的系统通知消息
                    intent = new Intent(getContext(), RobotExtendChatActivity.class);
                    intent.putExtra(PbChatActivity.KEY_JID, item.getId());
                    //设置真实id
                    intent.putExtra(PbChatActivity.KEY_REAL_JID, item.getRealUser());
                    //设置是否是群聊
                    intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);

                    intent.putExtra(PbChatActivity.KEY_CHAT_TYPE, item.getConversationType() + "");
                    intent.putExtra(PbChatActivity.KEY_UNREAD_MSG_COUNT,item.getUnread_msg_cont());
                    startActivity(intent);
                } else {
                    intent = new Intent(getContext(), RobotChatActivity.class);
                    intent.putExtra(RobotChatActivity.ROBOT_ID_EXTRA, item.getId());
                    intent.putExtra(PbChatActivity.KEY_JID, item.getId());
                    //设置真实id
                    intent.putExtra(PbChatActivity.KEY_REAL_JID, item.getRealUser());
                    intent.putExtra(PbChatActivity.KEY_UNREAD_MSG_COUNT,item.getUnread_msg_cont());
                    startActivity(intent);
                }
                break;
            case ConversitionType.MSG_TYPE_FRIENDS_REQUEST:
                break;
            default:
                break;
        }

    }

    @Override
    public void setRecentConvList(final List<RecentConversation> convers) {
        super.getHandler().post(() -> {
            if (recentConvsAdapter != null)
                recentConvsAdapter.setRecentConversationList(convers);
        });

    }

    @Override
    public void setRecentConvListCache(List<RecentConversation> convers) {
//        recentConvsAdapter
    }

    @Override
    public String getXmppId() {
        return XmppId;
    }

    @Override
    public String getRealUserId() {
        return RealUserId;
    }

    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public boolean isOnlyUnRead() {
        return isOnlyUnRead;
    }

    @Override
    public void refresh() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (recentConvsAdapter != null)
                    recentConvsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean readAllConversations() {
        return false;
    }




    @Override
    public boolean onContextItemSelected(MenuItem mi) {
        AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo = (AdapterView.AdapterContextMenuInfo) mi.getMenuInfo();
        final RecentConversation rc = (RecentConversation) recentConvsAdapter.getItem(menuInfo.position - list.getHeaderViewsCount());
        XmppId = rc.getId();
        RealUserId = rc.getRealUser();
        mCurrentConv = rc;
        panelPresenter.setPanelView(new ITopMeesageView() {
            @Override
            public String getJid() {
                return rc.getId();
            }

            @Override
            public boolean getTop() {
                return rc.getTop() == 0;
            }

            @Override
            public boolean getReMind() {
                return rc.getRemind() == 0;
            }

            @Override
            public String getRJid() {
                return rc.getRealUser();
            }

            @Override
            public Context getContext() {
                return getActivity().getApplicationContext();
            }
        });
//        recentConvsAdapter.notifyItemChanged(menuInfo.position);
        switch (mi.getItemId()) {
            case MENU1:
                convPresenter.deleteChatRecord();
                break;
            case MENU2:
                convPresenter.deleteCoversation();
                break;
            case MENU5:
                BackgroundExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        convPresenter.allRead();
                    }
                });
                break;
            case MENU3:
                BackgroundExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        convPresenter.markReadById();
                    }
                });
                break;
            case MENU4:
                //设置置顶或者取消置顶
//                Toast.makeText(getActivity(),"尝试置顶",Toast.LENGTH_LONG);

                panelPresenter.setConversationTopOrCancel();
//                convPresenter.showRecentConvs(true);
                break;

            case MENU6:
                panelPresenter.setConversationReMindOrCancel();
                break;
        }

        return true;
    }

    public void MoveToUnread() {
        if (recentConvsAdapter == null) return;
        int count = recentConvsAdapter.getCount();
        int firstVisibleIndex = list.getFirstVisiblePosition();
        int lastVisibleIndex = list.getLastVisiblePosition();
        if(lastVisibleIndex == count){
            firstVisibleIndex = 0;
        }
        Logger.i("MoveToUnread:count=" + count + "firstVisibleIndex=:" + firstVisibleIndex + "lastVisibleIndex=:" + lastVisibleIndex);
        for (int i = firstVisibleIndex + 1; i < count; i++) {
            RecentConversation rc = recentConvsAdapter.getItem(i);
            //逻辑是判断是否有未读,并且没有设置不提醒
            if (rc.getUnread_msg_cont() > 0 && rc.getRemind() != 1) {
                list.setSelection(i + list.getHeaderViewsCount());

                break;
            }
            if(i == count -1){
                list.setSelection(recentConvsAdapter.getFirstUnreadIndex() + list.getHeaderViewsCount());
            }

        }
    }

    public void moveToTop() {
        if(list != null) list.smoothScrollToPosition(0);
    }

    @Override
    public void refreshConversation() {
//        if (!InternDatas.existKey(Constants.SYS.DND_LIST)) {
//            ConversationManagePresenter.getInstance().loadDoNotDisturbList();
//        }
//        convPresenter.showRecentConvs();
    }

    @Override
    public void showDialog(String msg) {
        if(commonDialog != null && getActivity() != null && !getActivity().isFinishing()){
            commonDialog.setMessage(msg);
            commonDialog.setTitle(R.string.atom_ui_common_prompt);
            commonDialog.setPositiveButton(R.string.atom_ui_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    commonDialog.dismiss();
                }
            });
            commonDialog.show();
        }
    }

    @Override
    public void parseEncryptMessage(final IMMessage message) {
        if (!message.getConversationID().equals(currentJid)) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (message.isCarbon()) {
                        dismissEncryptDialog();
                        Toast.makeText(getActivity(), R.string.atom_ui_tip_deal_other_client, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (message.getMsgType() == EncryptMessageType.BEGIN)
                        showEncryptSessionDialog(message);
                    else if (message.getMsgType() == EncryptMessageType.CANCEL) {
                        dismissEncryptDialog();
                        Toast.makeText(getActivity(), R.string.atom_ui_tip_encryp_cancel, Toast.LENGTH_SHORT).show();
                    } else if (message.getMsgType() == EncryptMessageType.CLOSE) {
                        dismissEncryptDialog();
                        Toast.makeText(getActivity(), R.string.atom_ui_tip_close_encrypted, Toast.LENGTH_SHORT).show();
                        DataCenter.encryptUsers.remove(message.getConversationID());
                    }

                }
            });
        }
    }

    @Override
    public void showFileSharing() {
       if(header != null && getActivity() != null && !getActivity().isFinishing()){
           header.findViewById(R.id.atom_ui_home_pc_layout).setVisibility(View.VISIBLE);
       }
    }

    @Override
    public void hidenFileSharing(){
        if(header != null && getActivity() != null && !getActivity().isFinishing()){
            header.findViewById(R.id.atom_ui_home_pc_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void loginState(final boolean isLogin) {

        if(header != null && getActivity() != null && !getActivity().isFinishing()){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    header.findViewById(R.id.atom_ui_un_connected).setVisibility(isLogin ? View.GONE : View.VISIBLE);
                }
            });
        }
    }

    @Override
    public RecentConversation getCurrentConv() {
        return mCurrentConv;
    }

    @Override
    public void CreadSession(Map<String, RecentConversation> sessionMap, List<RecentConversation> list, IMMessage message) {
        createSession(sessionMap, list, message);

    }

    public void createSession(Map<String, RecentConversation> sessionMap, List<RecentConversation> list, IMMessage message) {
        String conversationId = message.getConversationID();
        RecentConversation recentConversation = sessionMap.get(conversationId);
        if(recentConversation ==null){
            convPresenter.showRecentConvs();
            return;
        }
        int signaleType = message.getSignalType();
        if (signaleType == ProtoMessageOuterClass.SignalType.SignalTypeReadmark_VALUE) {//消息已读状态
            boolean isRead = MessageStatus.isExistStatus(message.getReadState(), MessageStatus.REMOTE_STATUS_CHAT_READED);
            if (recentConversation != null && isRead) {
                recentConversation.setUnread_msg_cont(0);
            }
        } else {
            int topCounts = ConnectionUtil.getInstance().querryConversationTopCount();
            if (message.getDirection() == IMMessage.DIRECTION_RECV) {
                int unReadCount = recentConversation.getUnread_msg_cont();
                recentConversation.setUnread_msg_cont(++unReadCount);
            }
            recentConversation.setLastFrom(message.getRealfrom());
            recentConversation.setLastMsgTime(message.getTime().getTime());
            recentConversation.setLastMsg(ConnectionUtil.getInstance().getLastMsg(message.getMsgType(), message.getBody()));

            list.remove(recentConversation);
            if (recentConversation.getTop() == 1) {//置顶排在第一个
                list.add(0, recentConversation);
            } else {
                list.add(topCounts, recentConversation);
            }
        }
        recentConvsAdapter.notifyDataSetChanged();

    }


    private void dismissEncryptDialog() {
        if (encryptDialog != null && encryptDialog.isShowing())
            encryptDialog.dismiss();
    }

    private void showEncryptSessionDialog(final IMMessage message) {
        if (getActivity().isFinishing()) return;
        dismissEncryptDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.atom_ui_dialog_encrypt_seesion, null, false);
        final TextView encrypt_text = (TextView) view.findViewById(R.id.encrypt_text);
        Button encrypt_button1 = (Button) view.findViewById(R.id.encrypt_button1);
        Button encrypt_button2 = (Button) view.findViewById(R.id.encrypt_button2);
        ProfileUtils.loadNickName(message.getConversationID(), true, new ProfileUtils.LoadNickNameCallback() {
            @Override
            public void finish(final String name) {
                if (!TextUtils.isEmpty(name))
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            encrypt_text.setText(name + getString(R.string.atom_ui_tip_request_encrypt));
                        }
                    });
            }
        });
        encrypt_button1.setText(R.string.atom_ui_btn_refuse_encrypt);
        encrypt_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissEncryptDialog();
                sendRefuseEncryptMsg(message.getToID(), message.getConversationID());
            }
        });
        encrypt_button2.setText(R.string.atom_ui_btn_open_encrypt);
        encrypt_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissEncryptDialog();
                Intent intent = new Intent(getActivity(), PbChatActivity.class);
                intent.putExtra(PbChatActivity.KEY_JID, message.getConversationID());
                intent.putExtra(PbChatActivity.KEY_IS_CHATROOM, false);
                intent.putExtra(PbChatActivity.KEY_ENCRYPT_BODY, message.getBody());
                startActivity(intent);
            }
        });
        builder.setView(view);
        encryptDialog = builder.show();
        encryptDialog.setCanceledOnTouchOutside(false);
        return;
    }

    //发送加密信令消息
    public void sendRefuseEncryptMsg(String fromId, String toId) {
        IMMessage message = MessageUtils.generateSingleIMMessage(fromId, toId, null, null, null);
        message.setType(ConversitionType.MSG_TYPE_ENCRYPT);
        message.setBody(getString(R.string.atom_ui_body_refuse_encrypt));
        message.setMsgType(EncryptMessageType.REFUSE);
        message.setDirection(IMMessage.DIRECTION_MIDDLE);
        ConnectionUtil.getInstance().sendEncryptSignalMessage(message);
    }
}
