package com.qunar.im.ui.activity;

import android.os.Bundle;

import com.qunar.im.ui.R;
import com.qunar.im.ui.fragment.WorkWorldNoticeFragment;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;

public class WorkWorldNoticeActivityV2 extends SwipeBackActivity {
    protected QtNewActionBar qtNewActionBar;//头部导航


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ui_fragment);
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.fragment_container,new WorkWorldNoticeFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();

        initData();
    }

    private void initData() {
        setActionBarTitle("我的消息");
//        if (getIntent().hasExtra(WorkWordJID)) {
////            setActionBarTitle("用户动态");
//            jid =getIntent().getStringExtra(WorkWordJID);
//            if(TextUtils.isEmpty(jid)){
//                jid = "未知";
//            }
//            ConnectionUtil.getInstance().getUserCard(jid, new IMLogicManager.NickCallBack() {
//                @Override
//                public void onNickCallBack(final Nick nick) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            setActionBarTitle(nick.getName()+"的驼圈");
//                        }
//                    });
//                }
//            },false,false);
////            workWorldPresenter = new WorkWorldManagerPresenter(this, searchUserIdstr);
//        } else {
////            setActionBarTitle("驼圈");
////            workWorldPresenter = new WorkWorldManagerPresenter(this);
//            setActionBarTitle("用户驼圈");
//        }
    }

}
