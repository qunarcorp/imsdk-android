package com.qunar.im.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.presenter.ICloudRecordPresenter;
import com.qunar.im.ui.presenter.impl.MultipleSessionPresenter;
import com.qunar.im.ui.presenter.impl.SingleSessionPresenter;
import com.qunar.im.utils.ConnectionUtil;

import java.util.List;

public class PbChatSearchResultActivity extends PbChatActivity  {

    private int count = 20;

    public static final String KEY_START_TIME = "start_time";

    private long startTime;

    public static final String KEY_SEARCHING = "key_searching";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chat_region.setMode(PullToRefreshBase.Mode.BOTH);
        chat_region.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
//        searching = true;
    }

    @Override
    public void initViews() {

        super.initViews();
        chat_region.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMoreHistory();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMoreHistoryUp();
            }
        });

        edit_msg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    getIntent().removeExtra(PbChatSearchResultActivity.KEY_SEARCHING);
                    searching = false;
//                    chatingPresenter.addEventForSearch();
                    chatingPresenter.clearAndReloadMessages();
                }
            }
        });
    }

    public void loadMoreHistoryUp(){
        ((ICloudRecordPresenter) chatingPresenter).showMoreOldMsgUp(isFromChatRoom);
    }

    @Override
    public void setHistoryMessage(final List<IMMessage> historyMessage, final int unread) {
        if(searching){
            getHandler().post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void run() {
                    if(isFinishing()){
                        return;
                    }
                    unreadMsgCount.set(unread);
                    chat_region.onRefreshComplete();
                    if (historyMessage != null && historyMessage.size() > 0) {

                        pbChatViewAdapter.setMessages(historyMessage);
                        if (chat_region.getRefreshableView().getCount() > 0) {
                            chat_region.getRefreshableView().smoothScrollToPosition(0);
                        }
//                            chat_region.getRefreshableView().setSelection(chat_region.getRefreshableView().getCount() - 1);
                    }
                    // TODO: 2017/9/5 分享消息？
                    handlerReceivedData();
                    getAtOwnMsgIndexs();
                    if (unread > 5) {
                        showUnreadView(unread);
                        if (isFromChatRoom && atMsgIndex > 0) {
                            showAtmsgView();
                        }
                    }
                }
            });
        }else {
            super.setHistoryMessage(historyMessage, unread);
        }
    }

    @Override
    public void addHistoryMessageLast(final List<IMMessage> historyMessage) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                chat_region.onRefreshComplete();
                if (historyMessage == null || historyMessage.size() == 0) {
                    return;
                }
                if (historyMessage.size() == 1) {
                    if (TextUtils.isEmpty(historyMessage.get(0).getBody())) {
//                        historyMessage.get(0).setBody(getString(R.string.atom_ui_cloud_record_prompt));
                        pbChatViewAdapter.addNewMsgs(historyMessage);
                        return;
                    }
                    //已经拉取不到消息则不重复提示
                    if(historyMessage.get(0).getMsgType()==-99){
                        chat_region.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        searching = false;
                    }
//                    if (lastMessage != null && lastMessage.getMsgType() == MessageType.MSG_TYPE_NO_MORE_MESSAGE &&
//                            historyMessage.get(0).getMsgType() == MessageType.MSG_TYPE_NO_MORE_MESSAGE) {
//
//                        return;
//                    }
                }
                pbChatViewAdapter.addNewMsgs(historyMessage);
//                chat_region.getRefreshableView().setSelection(0);
                chat_region.getRefreshableView().smoothScrollToPosition(pbChatViewAdapter.getCount()-historyMessage.size()+1);
            }
        });
    }

    @Override
    protected void injectExtras(Intent intent) {
        searching = intent.getBooleanExtra(PbChatSearchResultActivity.KEY_SEARCHING,false);
        if(searching){
            super.injectExtras(intent);
            startTime = intent.getLongExtra(KEY_START_TIME,0);
            if (isFromChatRoom) {
                chatingPresenter = new MultipleSessionNetSearchPresenter();
            } else {
                chatingPresenter = new SingleSessionNetSearchPresenter();
            }
            chatingPresenter.setView(this);
//            chatingPresenter.removeEventForSearch();

        }else{
            super.injectExtras(intent);
        }

    }




    private class MultipleSessionNetSearchPresenter extends MultipleSessionPresenter{
        @Override
        public void propose() {
            if(chatView.getSearching()){
                ConnectionUtil.getInstance().SelectHistoryGroupChatMessageForNet(chatView.getToId(), chatView.getRealJid(), count, numPerPage, startTime, 1,true, new ConnectionUtil.HistoryMessage() {
                    @Override
                    public void onMessageResult(List<IMMessage> messageList) {
                        chatView.setHistoryMessage(messageList, 0);
//                    com.orhanobut.logger.Logger.i(JsonUtils.getGson().toJson(messageList));
                    }
                });
            }else {
            super.propose();
            }

        }

        @Override
        public void showMoreOldMsg(boolean isFromGroup) {
            if(chatView.getSearching()) {
                long time = chatView.getListFirstItem().getTime().getTime();
                ConnectionUtil.getInstance().SelectHistoryGroupChatMessageForNet(chatView.getToId(), chatView.getRealJid(), count, numPerPage, time, 0, false, new ConnectionUtil.HistoryMessage() {
                    @Override
                    public void onMessageResult(List<IMMessage> messageList) {
                        if (messageList.size() > 0) {
                            curMsgNum += messageList.size();
                            historyTime = messageList.get(0).getTime().getTime() - 1;
//                            Collections.reverse(messageList);
                        }
                        chatView.addHistoryMessage(messageList);

                    }
                });
            }else{
                super.showMoreOldMsg(isFromGroup);
            }
        }

        @Override
        public void showMoreOldMsgUp(boolean isFromGroup) {
            try {
                long time = chatView.getListLastItem().getTime().getTime();
                if (time == 0l) {
                    chatView.onRefreshComplete();
                } else {
                    ConnectionUtil.getInstance().SelectHistoryGroupChatMessageForNet(chatView.getToId(), chatView.getRealJid(), count, numPerPage, time, 1, false, new ConnectionUtil.HistoryMessage() {
                        @Override
                        public void onMessageResult(List<IMMessage> messageList) {
                            if (messageList.size() > 0) {
                                curMsgNum += messageList.size();
                                historyTime = messageList.get(0).getTime().getTime() - 1;
//                            Collections.reverse(messageList);// 不进行反转
//                            Collections.
                            }
                            chatView.addHistoryMessageLast(messageList);

                        }
                    });
                }
            }catch (Exception e){
                chatView.onRefreshComplete();
            }
        }



    }

    private class SingleSessionNetSearchPresenter extends SingleSessionPresenter{
        @Override
        public void propose() {
            if(chatView.getSearching()){

                ConnectionUtil.getInstance().SelectHistoryChatMessageForNet(chatView.getToId(), chatView.getRealJid(), count, numPerPage, startTime, 1, true, chatView.getChatType(), new ConnectionUtil.HistoryMessage() {
                    @Override
                    public void onMessageResult(List<IMMessage> messageList) {
                        chatView.setHistoryMessage(messageList, 0);
                    }
                });
//                ConnectionUtil.getInstance().SelectHistoryGroupChatMessageForNet(chatView.getToId(), chatView.getRealJid(), count, numPerPage, startTime, 0,true, new ConnectionUtil.HistoryMessage() {
//                    @Override
//                    public void onMessageResult(List<IMMessage> messageList) {
//                        chatView.setHistoryMessage(messageList, 0);
////                    com.orhanobut.logger.Logger.i(JsonUtils.getGson().toJson(messageList));
//                    }
//                });
            }else {
                super.propose();
            }
        }

        @Override
        public void showMoreOldMsg(boolean isFromGroup) {
            if(chatView.getSearching()) {
                long time = chatView.getListFirstItem().getTime().getTime();
                ConnectionUtil.getInstance().SelectHistoryChatMessageForNet(chatView.getToId(), chatView.getRealJid(), count, numPerPage, time, 0, false, chatView.getChatType(),new ConnectionUtil.HistoryMessage() {
                    @Override
                    public void onMessageResult(List<IMMessage> messageList) {
                        if (messageList.size() > 0) {
                            curMsgNum += messageList.size();
                            historyTime = messageList.get(0).getTime().getTime() - 1;
//                            Collections.reverse(messageList);
                        }
                        chatView.addHistoryMessage(messageList);

                    }
                });
            }else{
                super.showMoreOldMsg(isFromGroup);
            }
        }
        @Override
        public void showMoreOldMsgUp(boolean isFromGroup) {
            try {
                long time = chatView.getListLastItem().getTime().getTime();
                if (time == 0l) {
                    chatView.onRefreshComplete();
                } else {
                    ConnectionUtil.getInstance().SelectHistoryChatMessageForNet(chatView.getToId(), chatView.getRealJid(), count, numPerPage, time, 1, false,chatView.getChatType(), new ConnectionUtil.HistoryMessage() {
                        @Override
                        public void onMessageResult(List<IMMessage> messageList) {
                            if (messageList.size() > 0) {
                                curMsgNum += messageList.size();
                                historyTime = messageList.get(0).getTime().getTime() - 1;
//                            Collections.reverse(messageList);// 不进行反转
//                            Collections.
                            }
                            chatView.addHistoryMessageLast(messageList);

                        }
                    });
                }
            }catch (Exception e){
                chatView.onRefreshComplete();
            }
        }
    }

}
