package com.qunar.im.base.module;

public class AvailableRoomRequest {


    /**
     * date : 2018-08-13
     * areaId : 6
     * startTime : 12:00
     * endTime : 13:00
     */

    private String date;
    private int areaId;
    private String startTime;
    private String endTime;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
