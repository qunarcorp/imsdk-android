package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qunar.im.base.jsonbean.SearchUserResult;
import com.qunar.im.ui.view.AddBuddyItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saber on 16-2-3.
 */
public class AddBuddyViewAdapter extends BaseAdapter {
    Context context;
    public AddBuddyViewAdapter(Context cxt) {
        context = cxt;
    }

    List<SearchUserResult.SearchUserInfo> userInfos = new ArrayList<>();

    public void changeDatas(List<SearchUserResult.SearchUserInfo> datas)
    {
        userInfos = datas;
    }

    @Override
    public int getCount() {
        return userInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddBuddyItemView itemView;
        if (convertView == null) {
            itemView = new AddBuddyItemView(context);
        } else {
            itemView = (AddBuddyItemView) convertView;
        }
        SearchUserResult.SearchUserInfo info;
        if(position<userInfos.size()) {
            info = (SearchUserResult.SearchUserInfo) getItem(position);
        }
        else {
            info = new SearchUserResult.SearchUserInfo();
            info.isFriends = true;
        }
        itemView.bindData(info);
        return itemView;
    }
}
