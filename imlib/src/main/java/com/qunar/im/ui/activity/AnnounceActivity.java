package com.qunar.im.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qunar.im.base.common.BackgroundExecutor;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.presenter.IAnnouncePresenter;
import com.qunar.im.base.presenter.INoticePresenter;
import com.qunar.im.base.presenter.impl.AnnouncePresenter;
import com.qunar.im.base.presenter.impl.NoticePresenter;
import com.qunar.im.base.presenter.views.IAnnounceView;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ChatViewAdapter;
import com.qunar.im.ui.adapter.ExtendChatViewAdapter;
import com.qunar.im.ui.view.QtNewActionBar;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by xinbo.wang on 2015/6/8.
 */
public class AnnounceActivity extends IMBaseActivity implements IAnnounceView {

    PullToRefreshListView system_msg_list;

    ExtendChatViewAdapter adapter;
    String jid;
    IAnnouncePresenter presenter;

    HandleAnnounceEvent handleAnnounceEvent = new HandleAnnounceEvent();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.atom_ui_activity_announce);
        bindViews();
        injectExtras();

        if(jid == null) {
            presenter = new AnnouncePresenter();
        }else {
            presenter = new NoticePresenter(jid);
        }
        presenter.setAnnounceView(this);
        initViews();
    }

    private void injectExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey("jid")) {
                jid = extras_.getString("jid");
            }
        }
    }

    private void bindViews() {

        system_msg_list = (com.handmark.pulltorefresh.library.PullToRefreshListView) findViewById(R.id.system_msg_list);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.resetRecentConv();
        EventBus.getDefault().register(handleAnnounceEvent);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(handleAnnounceEvent);
    }

    void initViews() {
        QtNewActionBar actionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
//        setActionBar(actionBar);
        setNewActionBar(actionBar);
        adapter = new ExtendChatViewAdapter(this, Constants.Config.PUB_NET_XMPP_Domain, getHandler(),false);
        adapter.setGravatarHandler(new ChatViewAdapter.GravatarHandler() {
            @Override
            public void requestGravatarEvent(String jid, String imageSrc, SimpleDraweeView view) {

            }

//            @Override
//            public void requestGravatarEvent(final String nickOrUid, final SimpleDraweeView view) {
//                getHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        view.setImageResource(R.drawable.atom_ui_rbt_system);
//                    }
//                });
//            }
        });
        system_msg_list.setAdapter(adapter);
        system_msg_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
                loadSysMsg();
            }
        });
        presenter.initView();
        loadSysMsg();
    }

    void loadSysMsg() {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                presenter.loadAnnounce();
            }
        });
    }

    @Override
    public void setAnnounceList(final List<IMMessage> msgs) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                system_msg_list.onRefreshComplete();
                if (msgs != null) {
                    adapter.addOldMsg(msgs);
                }else{
                    presenter.loadMoreHistory(String.valueOf(adapter.getItem(0).getTime().getTime() - 1));
                }
                if (adapter.getCount() > 0) {
                    int selection = msgs == null ? 0:msgs.size();
                    system_msg_list.getRefreshableView().setSelection(selection);
                }
            }
        });
    }

    @Override
    public void setTitle(final String name) {
        if(!TextUtils.isEmpty(name)) {
//            myActionBar.getTitleTextview().setText(name);
            setActionBarTitle(name);
        }
    }
    //添加系统消息的历史记录
    @Override
    public void addHistoryMessage(final List<IMMessage> historyMessage) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                system_msg_list.onRefreshComplete();
                if (historyMessage == null || historyMessage.size() == 0) {
                    return;
                }
                if (historyMessage.size() == 1) {
                    if (TextUtils.isEmpty(historyMessage.get(0).getBody())) {
                        historyMessage.get(0).setBody(getString(R.string.atom_ui_cloud_record_prompt));
                        adapter.addOldMsg(historyMessage);
                        return;
                    }
                }
                adapter.addOldMsg(historyMessage);
                system_msg_list.getRefreshableView().setSelection(historyMessage.size());
            }
        });
    }

    class HandleAnnounceEvent {
        public void onEventMainThread(EventBusEvent.HandleOrderOperation event) {
            ((INoticePresenter)presenter).handleOrderMessage(event.message);
            adapter.notifyDataSetChanged();
        }
    }
}
