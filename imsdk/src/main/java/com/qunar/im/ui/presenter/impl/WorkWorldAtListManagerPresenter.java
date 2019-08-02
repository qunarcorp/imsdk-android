package com.qunar.im.ui.presenter.impl;

import com.qunar.im.ui.presenter.WorkWorldAtListPresenter;
import com.qunar.im.ui.presenter.views.WorkWorldAtListView;
import com.qunar.im.core.manager.IMDatabaseManager;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class WorkWorldAtListManagerPresenter implements WorkWorldAtListPresenter{
    private WorkWorldAtListView mView;

    @Override
    public void setView(WorkWorldAtListView view) {
        mView = view;
    }

    @Override
    public void startSearch() {
        String text = mView.getSearText();
        if(getStringLength(text)>=4){
               List<String> list =  IMDatabaseManager.getInstance().SelectAllUserXmppIdListBySearchText(text);
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).equals(mView.getJid())){
                    list.remove(i);
                    break;
                }
            }
           mView.showSearchUser(list);
        }else{
            mView.showToast("请在尝试多输入一点关键字吧!");
        }
    }

    public int getStringLength(String str){
//        String string = "phil安卓";
//        Logger.d(string.length());
        int i= 0;
        try {
            String newString = new String(str.getBytes("GB2312"), "ISO-8859-1");
//            Logger.d(newString.length());
            i= newString.length();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return i;

    }
}
