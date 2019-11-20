package com.qunar.im.base.jsonbean;

/**
 * Created by hubin on 2018/3/5.
 */

public class QchatCousltIdBean {


    /**
     * ret : true
     * msg : null
     * data : {"switchOn":true,"supplier":{"id":323,"name":"去哪儿旗舰店","bType":1,"welcomes":null,"shopId":"shop_323","logoUrl":null,"busiSupplierId":"3803042451","busiName":null},"seat":{"id":2,"qunarName":"dujia_robot","webName":"度假小拿","nickName":null,"faceLink":null,"createTime":null,"priority":null,"supplierId":323,"businessId":1,"supplierName":null,"isrobot":true,"serviceStatus":4,"pid":null,"customerName":null,"onlineState":"ONLINE"},"onlineState":"online","lastStartTime":null}
     * errcode : null
     */

    private boolean ret;
    private Object msg;
    private DataBean data;
    private Object errcode;

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public Object getErrcode() {
        return errcode;
    }

    public void setErrcode(Object errcode) {
        this.errcode = errcode;
    }

    public static class DataBean {
        /**
         * switchOn : true
         * supplier : {"id":323,"name":"去哪儿旗舰店","bType":1,"welcomes":null,"shopId":"shop_323","logoUrl":null,"busiSupplierId":"3803042451","busiName":null}
         * seat : {"id":2,"qunarName":"dujia_robot","webName":"度假小拿","nickName":null,"faceLink":null,"createTime":null,"priority":null,"supplierId":323,"businessId":1,"supplierName":null,"isrobot":true,"serviceStatus":4,"pid":null,"customerName":null,"onlineState":"ONLINE"}
         * onlineState : online
         * lastStartTime : null
         */

        private boolean switchOn;
        private SupplierBean supplier;
        private SeatBean seat;
        private String onlineState;
        private Object lastStartTime;

        public boolean isSwitchOn() {
            return switchOn;
        }

        public void setSwitchOn(boolean switchOn) {
            this.switchOn = switchOn;
        }

        public SupplierBean getSupplier() {
            return supplier;
        }

        public void setSupplier(SupplierBean supplier) {
            this.supplier = supplier;
        }

        public SeatBean getSeat() {
            return seat;
        }

        public void setSeat(SeatBean seat) {
            this.seat = seat;
        }

        public String getOnlineState() {
            return onlineState;
        }

        public void setOnlineState(String onlineState) {
            this.onlineState = onlineState;
        }

        public Object getLastStartTime() {
            return lastStartTime;
        }

        public void setLastStartTime(Object lastStartTime) {
            this.lastStartTime = lastStartTime;
        }

        public static class SupplierBean {
            /**
             * id : 323
             * name : 去哪儿旗舰店
             * bType : 1
             * welcomes : null
             * shopId : shop_323
             * logoUrl : null
             * busiSupplierId : 3803042451
             * busiName : null
             */

            private int id;
            private String name;
            private int bType;
            private Object welcomes;
            private String shopId;
            private Object logoUrl;
            private String busiSupplierId;
            private Object busiName;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getBType() {
                return bType;
            }

            public void setBType(int bType) {
                this.bType = bType;
            }

            public Object getWelcomes() {
                return welcomes;
            }

            public void setWelcomes(Object welcomes) {
                this.welcomes = welcomes;
            }

            public String getShopId() {
                return shopId;
            }

            public void setShopId(String shopId) {
                this.shopId = shopId;
            }

            public Object getLogoUrl() {
                return logoUrl;
            }

            public void setLogoUrl(Object logoUrl) {
                this.logoUrl = logoUrl;
            }

            public String getBusiSupplierId() {
                return busiSupplierId;
            }

            public void setBusiSupplierId(String busiSupplierId) {
                this.busiSupplierId = busiSupplierId;
            }

            public Object getBusiName() {
                return busiName;
            }

            public void setBusiName(Object busiName) {
                this.busiName = busiName;
            }
        }

        public static class SeatBean {
            /**
             * id : 2
             * qunarName : dujia_robot
             * webName : 度假小拿
             * nickName : null
             * faceLink : null
             * createTime : null
             * priority : null
             * supplierId : 323
             * businessId : 1
             * supplierName : null
             * isrobot : true
             * serviceStatus : 4
             * pid : null
             * customerName : null
             * onlineState : ONLINE
             */

            private int id;
            private String qunarName;
            private String webName;
            private Object nickName;
            private Object faceLink;
            private Object createTime;
            private Object priority;
            private int supplierId;
            private int businessId;
            private Object supplierName;
            private boolean isrobot;
            private int serviceStatus;
            private Object pid;
            private Object customerName;
            private String onlineState;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getQunarName() {
                return qunarName;
            }

            public void setQunarName(String qunarName) {
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

            public Object getCreateTime() {
                return createTime;
            }

            public void setCreateTime(Object createTime) {
                this.createTime = createTime;
            }

            public Object getPriority() {
                return priority;
            }

            public void setPriority(Object priority) {
                this.priority = priority;
            }

            public int getSupplierId() {
                return supplierId;
            }

            public void setSupplierId(int supplierId) {
                this.supplierId = supplierId;
            }

            public int getBusinessId() {
                return businessId;
            }

            public void setBusinessId(int businessId) {
                this.businessId = businessId;
            }

            public Object getSupplierName() {
                return supplierName;
            }

            public void setSupplierName(Object supplierName) {
                this.supplierName = supplierName;
            }

            public boolean isIsrobot() {
                return isrobot;
            }

            public void setIsrobot(boolean isrobot) {
                this.isrobot = isrobot;
            }

            public int getServiceStatus() {
                return serviceStatus;
            }

            public void setServiceStatus(int serviceStatus) {
                this.serviceStatus = serviceStatus;
            }

            public Object getPid() {
                return pid;
            }

            public void setPid(Object pid) {
                this.pid = pid;
            }

            public Object getCustomerName() {
                return customerName;
            }

            public void setCustomerName(Object customerName) {
                this.customerName = customerName;
            }

            public String getOnlineState() {
                return onlineState;
            }

            public void setOnlineState(String onlineState) {
                this.onlineState = onlineState;
            }
        }
    }
}
