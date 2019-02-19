package com.qunar.im.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.base.presenter.views.IOrganizationView;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.ui.R;
import com.qunar.im.ui.util.OrganizationTreeUtils;

/**
 * 新组织架构
 * Created by lihaibin.li on 2018/1/4.
 */

public class OrganizationFragment extends BaseFragment implements IOrganizationView,IMNotificaitonCenter.NotificationCenterDelegate{
    ViewGroup containerView;
    OrganizationTreeUtils organizationTreeUtils;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectionUtil.getInstance().addEvent(this,QtalkEvent.UPDATE_ORGANIZATION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.atom_ui_fragment_organization, null, false);
        containerView = (ViewGroup) rootView.findViewById(R.id.container);
        organizationTreeUtils = new OrganizationTreeUtils(getActivity());
        organizationTreeUtils.getView(this);
        return rootView;
    }

    @Override
    public void getView(View view) {
        if(view == null) return;
        containerView.removeAllViews();
        containerView.addView(view);
    }

    @Override
    public void didReceivedNotification(String key, Object... args) {
        switch (key){
            case QtalkEvent.UPDATE_ORGANIZATION:
                if(organizationTreeUtils!=null)
                    organizationTreeUtils.getView(this);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnectionUtil.getInstance().removeEvent(this,QtalkEvent.UPDATE_ORGANIZATION);
    }

    public void refresh(){
        organizationTreeUtils.refresh();
    }

}
