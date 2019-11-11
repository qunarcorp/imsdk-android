package com.qunar.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.ICloudRecordPresenter;
import com.qunar.im.ui.presenter.impl.CloudRecordPresent;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.Utils;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.adapter.ExtendChatViewAdapter;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/5/13.
 */
public class CloudChatRecordActivity extends SwipeActivity implements IChatView {

    private static final int MENU1 = 0x1;

    PullToRefreshListView recors_of_chat;

    ExtendChatViewAdapter adapter;
    ICloudRecordPresenter presenter;

    boolean isFromGroup;

    String fullName,toId;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_cloud_record);
        bindViews();
        injectExtras();
        presenter = new CloudRecordPresent();
        presenter.setView(this);
        initViews();
    }

    private void bindViews()
    {
        recors_of_chat = (PullToRefreshListView) findViewById(R.id.recors_of_chat);
    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey("isFromGroup")) {
                isFromGroup = extras_.getBoolean("isFromGroup");
            }
            if (extras_.containsKey("fullName")) {
                fullName = extras_.getString("fullName");
            }
            if (extras_.containsKey("toId")) {
                toId = extras_.getString("toId");
            }
        }
    }
    void initViews()
    {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        ILoadingLayout startLabels = recors_of_chat
                .getLoadingLayoutProxy();
        startLabels.setPullLabel(getText(R.string.atom_ui_tip_prelode_history));// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel(getText(R.string.atom_ui_tip_loding_history));// 刷新时
        startLabels.setReleaseLabel(getText(R.string.atom_ui_tip_release_load));// 下来达到一定距离时，显示的提示
        if(adapter == null)
        {
            adapter = new ExtendChatViewAdapter(this,toId,getHandler(),isFromGroup);
            adapter.setGravatarHandler(new ChatViewAdapter.GravatarHandler() {
                @Override
                public void requestGravatarEvent(String jid, String imageSrc, SimpleDraweeView view) {

                }

//                @Override
//                public void requestGravatarEvent(final String nickOrUid, final SimpleDraweeView view) {
//                    ProfileUtils.displayGravatarByFullname(nickOrUid,view);
//                }
            });
            adapter.setContextMenuRegister(new ChatViewAdapter.ContextMenuRegister() {
                @Override
                public void registerContextMenu(View v) {
                    registerForContextMenu(v);
                }
            });
            recors_of_chat.getRefreshableView().setAdapter(adapter);
            recors_of_chat.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                    loadCloudRecords();
                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v instanceof LinearLayout) {
            IMMessage message = (IMMessage) v.getTag();
            Intent intent = new Intent();
            intent.putExtra(Constants.BundleKey.MESSAGE, message);
            if (message.getMsgType() != ProtoMessageOuterClass.MessageType.MessageTypeBurnAfterRead_VALUE) {
//                if (message.getReadState() == MessageStatus.STATUS_SUCCESS) {
                    menu.add(0, MENU1, 0, getText(R.string.atom_ui_menu_copy)).setIntent(intent);
//                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        IMMessage message = null;
        message = (IMMessage) item.getIntent().getSerializableExtra(Constants.BundleKey.MESSAGE);

        switch (item.getItemId()) {
            case MENU1:
                if (message != null && !TextUtils.isEmpty(message.getBody())) {
                    String content = ChatTextHelper.showContentType(message.getBody(), message.getMsgType());
                    Utils.dropIntoClipboard(content, this);
                    Toast.makeText(this, getText(R.string.atom_ui_tip_copied), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getText(R.string.atom_ui_tip_copy_no_content), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadCloudRecords();
    }

    void loadCloudRecords()
    {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                presenter.showMoreOldMsg(isFromGroup);
            }
        });
    }



    @Override
    public String getInputMsg() {
        return "";
    }

    @Override
    public void setInputMsg(String text) {

    }

    @Override
    public void setNewMsg2DialogueRegion(IMMessage newMsg) {

    }

    @Override
    public void showUnReadCount(int count) {

    }

    @Override
    public String getAutoReply() {
        return null;
    }

    @Override
    public String getOf() {
        return null;
    }

    @Override
    public String getOt() {
        return null;
    }

    @Override
    public String getRealJid() {
        return toId;
    }

    @Override
    public String getFromId() {
        return "";
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public String getToId() {
        return toId;
    }

    @Override
    public String getChatType() {
        return null;
    }

    @Override
    public String getUserId() {
        return fullName;
    }

    @Override
    public String getUploadImg() {
        return "";
    }

    @Override
    public String getTransferId() {
        return null;
    }

    @Override
    public void initActionBar() {

    }

    @Override
    public List<IMMessage> getSelMessages() {
        return null;
    }

    @Override
    public void refreshDataset() {

    }

    @Override
    public void setCurrentStatus(String status) {

    }

    @Override
    public void setHistoryMessage(final List<IMMessage> historyMessage,int unread) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                recors_of_chat.onRefreshComplete();
            }
        });
    }

    @Override
    public void addHistoryMessage(final List<IMMessage> list) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                recors_of_chat.onRefreshComplete();
                if(list!=null&&list.size()>0) {
                    adapter.addOldMsg(list);
                    recors_of_chat.getRefreshableView().setSelection(list.size());
                }
            }
        });
    }

    @Override
    public void addHistoryMessageLast(List<IMMessage> historyMessage) {

    }

    @Override
    public void showNoticePopupWindow(NoticeBean noticeBean) {

    }

    @Override
    public Map<String, String> getAtList() {
        return null;
    }

    @Override
    public void clearAndAddMsgs(List<IMMessage> historyMessage, int unread) {

    }

    @Override
    public void setTitle(String title) {
        if(mNewActionBar!=null){
            setActionBarTitle(title);
        }

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public int getListSize() {
        return 0;
    }

    @Override
    public void setTitleState(String stats) {

    }

    @Override
    public void revokeItem(IMMessage imMessage) {

    }

    @Override
    public void deleteItem(IMMessage imMessage) {

    }

    @Override
    public boolean isFromChatRoom() {
        return isFromGroup;
    }

    @Override
    public void replaceItem(IMMessage imMessage) {

    }

    @Override
    public void sendEditPic(String path) {

    }

    @Override
    public void closeActivity() {

    }

    @Override
    public void parseEncryptSignal(IMMessage message) {

    }

    @Override
    public void updateUploadProgress(IMMessage message, int progress, boolean isDone) {

    }

    @Override
    public void isEmotionAdd(boolean flag) {

    }

    @Override
    public void popNotice(NoticeBean noticeBean) {

    }

    @Override
    public void clearMessage() {

    }

    @Override
    public void sendRobotMsg(String msg) {

    }

//    @Override
//    public void setMsg(String msg) {
//
//    }

    @Override
    public String getBackupInfo() {
        return null;
    }

    @Override
    public IMMessage getListFirstItem() {
        return null;
    }

    @Override
    public IMMessage getListLastItem() {
        return null;
    }

    @Override
    public void onRefreshComplete() {

    }

    @Override
    public boolean getSearching() {
        return false;
    }

    @Override
    public void showToast(String str) {

    }

    @Override
    public boolean isMessageExit(String msgId) {
        return false;
    }

    @Override
    public int getUnreadMsgCount() {
        return 0;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public String getRefenceString() {
        return null;
    }

    @Override
    public void payRedEnvelopChioce(String type,String rid) {

    }

    @Override
    public void payOrder(String orderInfo) {

    }

    @Override
    public void payAuth(String authInfo) {

    }
}
