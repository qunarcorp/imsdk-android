package com.qunar.im.base.module;

import java.util.List;

public class AvailableRoomResponse {

    /**
     * ret : true
     * errcode : 0
     * errmsg :
     * data : [{"description":"白板、会服物资","roomName":"印度洋","capacity":8,"roomId":13,"canUse":0},{"description":"白板、会服物资","roomName":"太平洋","capacity":8,"roomId":12,"canUse":0},{"description":"白板、会服物资","roomName":"北冰洋","capacity":6,"roomId":11,"canUse":0}]
     */

    private boolean ret;
    private int errcode;
    private String errmsg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * description : 白板、会服物资
         * roomName : 印度洋
         * capacity : 8
         * roomId : 13
         * canUse : 0
         */

        private String description;
        private String roomName;
        private int capacity;
        private int roomId;
        private int canUse;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getRoomId() {
            return roomId;
        }

        public void setRoomId(int roomId) {
            this.roomId = roomId;
        }

        public int getCanUse() {
            return canUse;
        }

        public void setCanUse(int canUse) {
            this.canUse = canUse;
        }
    }
}
