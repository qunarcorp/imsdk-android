package com.qunar.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.qunar.im.base.module.BuddyRequest;
import com.qunar.im.ui.presenter.views.IAnswerForResultView;
import com.qunar.im.ui.view.baseView.BuddyRequestView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaokai on 15-12-9.
 */
public class BuddyRequestAdapter extends BaseAdapter {

    private List<BuddyRequest> requests;
    private Context context;
    private IAnswerForResultView view;
    public BuddyRequestAdapter(Context context,IAnswerForResultView view) {
        requests = new ArrayList<>();
        this.context = context;
        this.view = view;
    }

    public void addRequest(BuddyRequest request) {
        requests.add(request);
        notifyDataSetChanged();
    }

    public void addRequests(List<BuddyRequest> request) {
        requests.addAll(request);
        notifyDataSetChanged();
    }

    public void setRequests(List<BuddyRequest> requests)
    {
        this.requests = requests;
        notifyDataSetChanged();
    }

    public void clearRequests(){
        requests.clear();
    }
    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int position) {
        return requests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BuddyRequest request = (BuddyRequest)getItem(position);
        BuddyRequestView buddyRequestView = null;
        if (convertView == null) {
            buddyRequestView =new BuddyRequestView(context);
            buddyRequestView.setHandler(new BuddyRequestView.ItemAcceptClickHandler() {
                @Override
                public void accept(Button button, BuddyRequest buddyRequest) {
                    button.setClickable(false);
                    view.doAnswerForResult(buddyRequest);
                }
            });
        } else {
            buddyRequestView = (BuddyRequestView) convertView;
        }
        buddyRequestView.bindData(request);
        return buddyRequestView;
    }
}
