package com.qunar.im.base.module;

import java.util.List;

public class CalendarTrip {


    /**
     * ret : true
     * errcode : 0
     * errmsg :
     * data : {"trips":[{"tripIntr":"我想开个会","tripRemark":"可以写点自己对此会的备注","scheduleTime":"2018-08-03 10:00:00+08","operateType":null,"updateTime":null,"appointment":"预约会面","tripId":"49bc0bec2c5f40c5b963d0a66f99d7f7","tripRoom":"酸梅汤","tripLocaleNumber":"2","tripLocale":"维亚大厦","tripType":"0","tripInviter":"hubin.hu@ejabhost1","memberList":[{"memberId":"liufan.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"helen.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"hubin.hu@ejabhost1","memberState":"0","memberStateDescribe":""}],"tripRoomNumber":"1","tripDate":"2018-08-03","tripName":"RN大神邀请会","beginTime":"2018-08-03 14:00:00+08","endTime":"2018-08-03 15:00:00+08"},{"tripIntr":"我想开个会","tripRemark":"可以写点自己对此会的备注","scheduleTime":"2018-08-03 10:00:00+08","operateType":null,"updateTime":null,"appointment":"预约会面","tripId":"5fa779e6ecb744e3a2573094a2ae3572","tripRoom":"酸梅汤","tripLocaleNumber":"2","tripLocale":"维亚大厦","tripType":"0","tripInviter":"hubin.hu@ejabhost1","memberList":[{"memberId":"liufan.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"helen.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"hubin.hu@ejabhost1","memberState":"0","memberStateDescribe":""}],"tripRoomNumber":"1","tripDate":"2018-08-03","tripName":"RN大神邀请会","beginTime":"2018-08-03 14:00:00+08","endTime":"2018-08-03 15:00:00+08"}],"updateTime":null}
     */

    private boolean ret;
    private int errcode;
    private String errmsg;
    private DataBean data;

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * trips : [{"tripIntr":"我想开个会","tripRemark":"可以写点自己对此会的备注","scheduleTime":"2018-08-03 10:00:00+08","operateType":null,"updateTime":null,"appointment":"预约会面","tripId":"49bc0bec2c5f40c5b963d0a66f99d7f7","tripRoom":"酸梅汤","tripLocaleNumber":"2","tripLocale":"维亚大厦","tripType":"0","tripInviter":"hubin.hu@ejabhost1","memberList":[{"memberId":"liufan.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"helen.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"hubin.hu@ejabhost1","memberState":"0","memberStateDescribe":""}],"tripRoomNumber":"1","tripDate":"2018-08-03","tripName":"RN大神邀请会","beginTime":"2018-08-03 14:00:00+08","endTime":"2018-08-03 15:00:00+08"},{"tripIntr":"我想开个会","tripRemark":"可以写点自己对此会的备注","scheduleTime":"2018-08-03 10:00:00+08","operateType":null,"updateTime":null,"appointment":"预约会面","tripId":"5fa779e6ecb744e3a2573094a2ae3572","tripRoom":"酸梅汤","tripLocaleNumber":"2","tripLocale":"维亚大厦","tripType":"0","tripInviter":"hubin.hu@ejabhost1","memberList":[{"memberId":"liufan.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"helen.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"hubin.hu@ejabhost1","memberState":"0","memberStateDescribe":""}],"tripRoomNumber":"1","tripDate":"2018-08-03","tripName":"RN大神邀请会","beginTime":"2018-08-03 14:00:00+08","endTime":"2018-08-03 15:00:00+08"}]
         * updateTime : null
         */

        private String updateTime;
        private List<TripsBean> trips;

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public List<TripsBean> getTrips() {
            return trips;
        }

        public void setTrips(List<TripsBean> trips) {
            this.trips = trips;
        }

        public static class TripsBean {
            /**
             * tripIntr : 我想开个会
             * tripRemark : 可以写点自己对此会的备注
             * scheduleTime : 2018-08-03 10:00:00+08
             * operateType : null
             * updateTime : null
             * appointment : 预约会面
             * tripId : 49bc0bec2c5f40c5b963d0a66f99d7f7
             * tripRoom : 酸梅汤
             * tripLocaleNumber : 2
             * tripLocale : 维亚大厦
             * tripType : 0
             * tripInviter : hubin.hu@ejabhost1
             * memberList : [{"memberId":"liufan.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"helen.liu@ejabhost1","memberState":"0","memberStateDescribe":""},{"memberId":"hubin.hu@ejabhost1","memberState":"0","memberStateDescribe":""}]
             * tripRoomNumber : 1
             * tripDate : 2018-08-03
             * tripName : RN大神邀请会
             * beginTime : 2018-08-03 14:00:00+08
             * endTime : 2018-08-03 15:00:00+08
             */

            private String checkId;//为检查用户是否冲突所用字段, 与创建会议字段无关
            private String tripIntr;
            private String tripRemark;
            private String scheduleTime;
            private String operateType;
            private String updateTime;
            private String appointment;
            private String tripId;
            private String tripRoom;
            private String tripLocaleNumber;
            private String tripLocale;
            private String tripType;
            private String tripInviter;
            private String tripRoomNumber;
            private String tripDate;
            private String tripName;
            private String beginTime;
            private String endTime;
            private List<MemberListBean> memberList;
            private boolean canceled;

            public boolean getCanceled() {
                return canceled;
            }

            public void setCanceled(boolean canceled) {
                this.canceled = canceled;
            }

            public String getCheckId() {
                return checkId;
            }

            public void setCheckId(String checkId) {
                this.checkId = checkId;
            }

            public String getTripIntr() {
                return tripIntr;
            }

            public void setTripIntr(String tripIntr) {
                this.tripIntr = tripIntr;
            }

            public String getTripRemark() {
                return tripRemark;
            }

            public void setTripRemark(String tripRemark) {
                this.tripRemark = tripRemark;
            }

            public String getScheduleTime() {
                return scheduleTime;
            }

            public void setScheduleTime(String scheduleTime) {
                this.scheduleTime = scheduleTime;
            }

            public String getOperateType() {
                return operateType;
            }

            public void setOperateType(String operateType) {
                this.operateType = operateType;
            }

            public String getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(String updateTime) {
                this.updateTime = updateTime;
            }

            public String getAppointment() {
                return appointment;
            }

            public void setAppointment(String appointment) {
                this.appointment = appointment;
            }

            public String getTripId() {
                return tripId;
            }

            public void setTripId(String tripId) {
                this.tripId = tripId;
            }

            public String getTripRoom() {
                return tripRoom;
            }

            public void setTripRoom(String tripRoom) {
                this.tripRoom = tripRoom;
            }

            public String getTripLocaleNumber() {
                return tripLocaleNumber;
            }

            public void setTripLocaleNumber(String tripLocaleNumber) {
                this.tripLocaleNumber = tripLocaleNumber;
            }

            public String getTripLocale() {
                return tripLocale;
            }

            public void setTripLocale(String tripLocale) {
                this.tripLocale = tripLocale;
            }

            public String getTripType() {
                return tripType;
            }

            public void setTripType(String tripType) {
                this.tripType = tripType;
            }

            public String getTripInviter() {
                return tripInviter;
            }

            public void setTripInviter(String tripInviter) {
                this.tripInviter = tripInviter;
            }

            public String getTripRoomNumber() {
                return tripRoomNumber;
            }

            public void setTripRoomNumber(String tripRoomNumber) {
                this.tripRoomNumber = tripRoomNumber;
            }

            public String getTripDate() {
                return tripDate;
            }

            public void setTripDate(String tripDate) {
                this.tripDate = tripDate;
            }

            public String getTripName() {
                return tripName;
            }

            public void setTripName(String tripName) {
                this.tripName = tripName;
            }

            public String getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(String beginTime) {
                this.beginTime = beginTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public List<MemberListBean> getMemberList() {
                return memberList;
            }

            public void setMemberList(List<MemberListBean> memberList) {
                this.memberList = memberList;
            }

            public static class MemberListBean {
                /**
                 * memberId : liufan.liu@ejabhost1
                 * memberState : 0
                 * memberStateDescribe :
                 */

                private String memberId;
                private String memberState;
                private String memberStateDescribe;

                public String getMemberId() {
                    return memberId;
                }

                public void setMemberId(String memberId) {
                    this.memberId = memberId;
                }

                public String getMemberState() {
                    return memberState;
                }

                public void setMemberState(String memberState) {
                    this.memberState = memberState;
                }

                public String getMemberStateDescribe() {
                    return memberStateDescribe;
                }

                public void setMemberStateDescribe(String memberStateDescribe) {
                    this.memberStateDescribe = memberStateDescribe;
                }
            }
        }
    }
}
