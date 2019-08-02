package com.qunar.im.ui.presenter.views;

import com.qunar.im.base.jsonbean.SeatStatusResult;

import java.util.List;

/**
 * Created by huayu.chen on 2016/7/19.
 */
public interface IServiceStateView {
    String getUerId();
    String getServiceState();
    String getSeatSid();
    void setServiceState(String state);
    void getSeatStates(List<SeatStatusResult.SeatStatus> seatStatuses);
}
