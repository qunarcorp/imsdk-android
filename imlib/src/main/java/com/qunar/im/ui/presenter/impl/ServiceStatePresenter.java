package com.qunar.im.ui.presenter.impl;

import com.qunar.im.base.jsonbean.SeatStatusResult;
import com.qunar.im.ui.presenter.IServiceStatePresenter;
import com.qunar.im.ui.presenter.views.IServiceStateView;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.protocol.ThirdProviderAPI;

import java.util.List;

/**
 * Created by huayu.chen on 2016/7/19.
 */
public class ServiceStatePresenter implements IServiceStatePresenter {
    IServiceStateView serviceStateView;

    @Override
    public void setServiceStateView(IServiceStateView view) {
        serviceStateView = view;
    }

    @Override
    public void setServiceState() {
        final String state = serviceStateView.getServiceState();
        ThirdProviderAPI.setServiceStatus(serviceStateView.getUerId(),state ,serviceStateView.getSeatSid(), new ProtocolCallback.UnitCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean aBoolean) {
                if (aBoolean) {
                    serviceStateView.setServiceState(state);
                }
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    @Override
    public void getServiceState() {
        ThirdProviderAPI.getServiceStatus(serviceStateView.getUerId(), new ProtocolCallback.UnitCallback<List<SeatStatusResult.SeatStatus>>() {
            @Override
            public void onCompleted(List<SeatStatusResult.SeatStatus> seatStatuses) {
                serviceStateView.getSeatStates(seatStatuses);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }
}
