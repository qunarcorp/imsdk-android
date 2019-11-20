package com.qunar.im.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.presenter.ILocalChatRecordPresenter;
import com.qunar.im.ui.presenter.impl.LocalChatRecordPresenter;
import com.qunar.im.ui.presenter.impl.ShowSearchDetailsPresenter;
import com.qunar.im.ui.presenter.views.ILocalChatRecordView;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.adapter.ExtendChatViewAdapter;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.List;

/**
 * Created by saber on 15-7-7.
 */
public class LocalChatRecordActivity extends IMBaseActivity implements ILocalChatRecordView {
    public static final String KEY_JID = "jid";
    public static final String KEY_SELECTED_TIME = "selectedMsgTime";
    public static final String KEY_FROM_CLOUD = "fromCloud";
    public static final String KEY_MSG_ID = "MSGID";

    PullToRefreshListView recors_of_chat;

    ExtendChatViewAdapter adapter;

    ILocalChatRecordPresenter localChatRecordPresenter;

    boolean isInit = true;

    String jid;
    long selectedMsgTime;
    boolean fromCloud;
    String selectedMsgId;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_local_chat_record);
        recors_of_chat = (PullToRefreshListView) findViewById(R.id.recors_of_chat);
        injectExtras();
        if (fromCloud) {
            localChatRecordPresenter = new ShowSearchDetailsPresenter();
        } else {
            localChatRecordPresenter = new LocalChatRecordPresenter();
        }
        localChatRecordPresenter.setLocalChatRecordView(this);
        iniViews();
    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey(KEY_JID)) {
                jid = extras_.getString(KEY_JID);
            }
            if (extras_.containsKey(KEY_SELECTED_TIME)) {
                selectedMsgTime = extras_.getLong(KEY_SELECTED_TIME);
            }
            if (extras_.containsKey(KEY_FROM_CLOUD)) {
                fromCloud = extras_.getBoolean(KEY_FROM_CLOUD);
            }
            if (extras_.containsKey(KEY_MSG_ID)) {
                selectedMsgId = extras_.getString(KEY_MSG_ID);
            }
        }
    }

    void iniViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(actionBar);
        if (adapter == null) {
            adapter = new ExtendChatViewAdapter(this, jid, getHandler(), true);
            adapter.animateMsgId = selectedMsgId;
            adapter.setGravatarHandler(new ChatViewAdapter.GravatarHandler() {

                @Override
                public void requestGravatarEvent(String jid, String imageSrc, SimpleDraweeView view) {
                    //old
//                    ProfileUtils.displayGravatarByImageSrc(jid,imageSrc,view);
                    //new
                    ProfileUtils.displayGravatarByImageSrc(LocalChatRecordActivity.this, imageSrc, view,
                            LocalChatRecordActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size), LocalChatRecordActivity.this.getResources().getDimensionPixelSize(R.dimen.atom_ui_image_mid_size));
                }

//                @Override
//                public void requestGravatarEvent(final String nickOrUid, final SimpleDraweeView view) {
//                    ProfileUtils.displayGravatarByFullname(nickOrUid,view);
//                }

            });
            recors_of_chat.setAdapter(adapter);
            recors_of_chat.setMode(PullToRefreshBase.Mode.BOTH);
            recors_of_chat.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                    localChatRecordPresenter.loadOldderMsg();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                    localChatRecordPresenter.loadNewerMsg();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        localChatRecordPresenter.loadOldderMsg();
        if (jid.contains("@conference")) {
            ConnectionUtil.getInstance().getMucCard(jid, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (nick != null) {
                                setActionBarTitle(nick.getName());
                            }
                        }
                    });
                }
            }, false, false);
//            myActionBar.getTitleTextview().setText(jid);
        } else {
            ConnectionUtil.getInstance().getUserCard(jid, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (nick != null) {
                                setActionBarTitle(nick.getName());
                            }
                        }
                    });
                }
            }, false, false);

        }
    }

    @Override
    public String getFromId() {
        return jid;
    }

    @Override
    public String getUserId() {
        return CurrentPreference.getInstance().getUserid();
    }

    @Override
    public void addHistoryMessage(final List<IMMessage> historyMessage) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                recors_of_chat.onRefreshComplete();
                if (historyMessage.size() > 0) {
                    adapter.addNewMsgs(historyMessage);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public long getCurrentMsgRecTime() {
        return selectedMsgTime;
    }

    @Override
    public void insertHistory2Head(final List<IMMessage> list) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                recors_of_chat.onRefreshComplete();
                if (list.size() > 0) {
                    String str = JsonUtils.getGson().toJson(list);
                    adapter.addOldMsg(list);
                    adapter.notifyDataSetChanged();
                }
                if (isInit) {
                    recors_of_chat.getRefreshableView().setSelection(adapter.getCount() - 1);
                    isInit = false;
                }
            }
        });
    }
}