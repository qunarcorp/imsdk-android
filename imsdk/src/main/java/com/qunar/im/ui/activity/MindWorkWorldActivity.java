package com.qunar.im.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.qunar.im.base.module.Nick;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.WorkWorldFragment;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.ConnectionUtil;

import static com.qunar.im.ui.fragment.WorkWorldFragment.WorkWordJID;

public class MindWorkWorldActivity extends SwipeBackActivity {

    protected QtNewActionBar qtNewActionBar;//头部导航

    private String jid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ui_fragment);
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.fragment_container,new WorkWorldFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();

        initData();
    }

    private void initData() {
        if (getIntent().hasExtra(WorkWordJID)) {
//            setActionBarTitle("用户动态");
            jid =getIntent().getStringExtra(WorkWordJID);
            if(TextUtils.isEmpty(jid)){
                jid = "未知";
            }
            ConnectionUtil.getInstance().getUserCard(jid, new IMLogicManager.NickCallBack() {
                @Override
                public void onNickCallBack(final Nick nick) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setActionBarTitle(nick.getName()+"的驼圈");
                        }
                    });
                }
            },false,false);
//            workWorldPresenter = new WorkWorldManagerPresenter(this, searchUserIdstr);
        } else {
//            setActionBarTitle("驼圈");
//            workWorldPresenter = new WorkWorldManagerPresenter(this);
            setActionBarTitle("用户驼圈");
        }
    }
}
