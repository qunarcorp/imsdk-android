package com.qunar.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qunar.im.ui.presenter.impl.CollectionChatPresenter;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.module.Nick;
import com.qunar.im.core.manager.IMLogicManager;

/**
 * Created by hubin on 2017/11/27.
 */

public class CollectionChatActivity extends PbChatActivity {
    public static final String ORIGINFROM = "originfrom";
    public static final String ORIGINTO = "originto";
//    private static final String ORIGINTYPE = "origintype";


//    private String otype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatingPresenter = new CollectionChatPresenter();
        chatingPresenter.setView(this);
        edit_region.setVisibility(View.GONE);
//        myActionBar.getRightImageBtn().setVisibility(View.GONE);
        collectionInjectExtras(getIntent());
        initTitle();
        setActionBarRightIcon(0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initTitle();
    }

    public void initTitle() {
        if (isFromChatRoom) {
            ConnectionUtil.getInstance().getCollectionMucCard(getOf(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    setTitle(nick.getName());
                }
            }, false, false);
        } else {
            ConnectionUtil.getInstance().getCollectionUserCard(getOf(), new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(Nick nick) {
                    setTitle(nick.getName());
                }
            }, false, false);
        }
    }

    protected void collectionInjectExtras(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(ORIGINFROM)) {
                //这个做法是如果没有传入realjid 默认认为jid和realjid相同
                of = extras.getString(ORIGINFROM);
            }
            if (extras.containsKey(ORIGINTO)) {
                //这个做法是如果没有传入realjid 默认认为jid和realjid相同
                ot = extras.getString(ORIGINTO);
            }
//            if (extras.containsKey(ORIGINTYPE)) {
//                //这个做法是如果没有传入realjid 默认认为jid和realjid相同
//                otype = extras.getString(ORIGINTYPE);
//            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
//        chatingPresenter.removeEvent();
    }
}
