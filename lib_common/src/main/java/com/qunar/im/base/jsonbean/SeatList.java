package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * 客服坐席
 */
public class SeatList extends BaseJsonResult {

    private List<Seat> data;

    public List<Seat> getData() {
        return data;
    }

    public void setData(List<Seat> data) {
        this.data = data;
    }

    public static class Seat{

        /**
         * id : 12220
         * qunarName : {"node":"qeyzvbi2011","domain":"ejabhost2","resource":null}
         * webName : 小泊
         * nickName : null
         * faceLink : null
         * supplierID : 323
         * priority : 4
         * createTime : 1530696573822
         * updateTime : 1532312956949
         * oldSupplierID : null
         * oldID : null
         * serviceStatus : 4
         * status : 1
         * maxServiceCount : 3
         * groupId : null
         */

        private int id;
        private QunarNameBean qunarName;
        private String webName;
        private Object nickName;
        private Object faceLink;
        private int supplierID;
        private int priority;
        private long createTime;
        private long updateTime;
        private Object oldSupplierID;
        private Object oldID;
        private int serviceStatus;
        private int status;
        private int maxServiceCount;
        private Object groupId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public QunarNameBean getQunarName() {
            return qunarName;
        }

        public void setQunarName(QunarNameBean qunarName) {
            this.qunarName = qunarName;
        }

        public String getWebName() {
            return webName;
        }

        public void setWebName(String webName) {
            this.webName = webName;
        }

        public Object getNickName() {
            return nickName;
        }

        public void setNickName(Object nickName) {
            this.nickName = nickName;
        }

        public Object getFaceLink() {
            return faceLink;
        }

        public void setFaceLink(Object faceLink) {
            this.faceLink = faceLink;
        }

        public int getSupplierID() {
            return supplierID;
        }

        public void setSupplierID(int supplierID) {
            this.supplierID = supplierID;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public Object getOldSupplierID() {
            return oldSupplierID;
        }

        public void setOldSupplierID(Object oldSupplierID) {
            this.oldSupplierID = oldSupplierID;
        }

        public Object getOldID() {
            return oldID;
        }

        public void setOldID(Object oldID) {
            this.oldID = oldID;
        }

        public int getServiceStatus() {
            return serviceStatus;
        }

        public void setServiceStatus(int serviceStatus) {
            this.serviceStatus = serviceStatus;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getMaxServiceCount() {
            return maxServiceCount;
        }

        public void setMaxServiceCount(int maxServiceCount) {
            this.maxServiceCount = maxServiceCount;
        }

        public Object getGroupId() {
            return groupId;
        }

        public void setGroupId(Object groupId) {
            this.groupId = groupId;
        }

        public static class QunarNameBean {
            /**
             * node : qeyzvbi2011
             * domain : ejabhost2
             * resource : null
             */

            private String node;
            private String domain;
            private Object resource;

            public String getNode() {
                return node;
            }

            public void setNode(String node) {
                this.node = node;
            }

            public String getDomain() {
                return domain;
            }

            public void setDomain(String domain) {
                this.domain = domain;
            }

            public Object getResource() {
                return resource;
            }

            public void setResource(Object resource) {
                this.resource = resource;
            }
        }
    }
}
