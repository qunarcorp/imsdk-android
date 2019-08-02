package com.qunar.im.ui.util.atmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.qunar.im.base.module.Nick;
import com.qunar.im.core.manager.IMLogicManager;
import com.qunar.im.ui.activity.PbChatActivity;
import com.qunar.im.ui.activity.WorkWorldAtListActivity;
import com.qunar.im.utils.ConnectionUtil;

import java.util.List;

public class WorkWorldAtManager extends AtManager {


    private boolean showFirstAt;


    public WorkWorldAtManager(Context context, String jid) {
        super(context, jid);

    }

    @Override
    public void startAtList(boolean showAt) {
        // 启动@联系人界面
        Intent intent = new Intent(context, WorkWorldAtListActivity.class);
        intent.putExtra("jid", jid);
        showFirstAt = showAt;
        intent.putExtra("showFirstAt",showAt);
        ((Activity)context).startActivityForResult(intent, PbChatActivity.AT_MEMBER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PbChatActivity.AT_MEMBER && resultCode == Activity.RESULT_OK) {
            final List<String> list = data.getStringArrayListExtra("atList");
            for (int i = 0; i <list.size() ; i++) {
                final int finalI = i;
                ConnectionUtil.getInstance().getUserCard(list.get(i), new IMLogicManager.NickCallBack() {
                    @Override
                    public void onNickCallBack(Nick nick) {
                        String name,account;
                        if(nick!=null){


                         name = nick.getName();
                         account = nick.getXmppId();
                        }else{
                            name = list.get(finalI);
                            account = list.get(finalI);
                        }
                        boolean needAt = false;
                        if(finalI>0){
                            needAt = true;
                        }else{
                            if(showFirstAt){
                                needAt=true;
                            }else{
                                needAt = false;
                            }
                        }
                        insertAitMemberInner(account, name, curPos, needAt);
                    }
                },false,false);
            }

        }
//        super.onActivityResult(requestCode, resultCode, data);
    }
}
