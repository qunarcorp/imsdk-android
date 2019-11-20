package com.qunar.im.base.jsonbean;

/**
 * 抢单json
 * Created by lihaibin.li on 2017/10/25.
 */

public class RobOrderMsgJson {

    private String title;
    private DetailBean detail;
    private String dealId;
    private String dealUrl;
    private String status;
    private String msgId;
    private String btnDisplay;//按钮上展示的文字

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getDealUrl() {
        return dealUrl;
    }

    public void setDealUrl(String dealUrl) {
        this.dealUrl = dealUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getBtnDisplay() {
        return btnDisplay;
    }

    public void setBtnDisplay(String btnDisplay) {
        this.btnDisplay = btnDisplay;
    }

    public static class DetailBean {
        /**
         * budgetinfo : 人均预算：3422元-3444元
         * OrderTime : 下单时间：2017-09-2114: 20: 31
         * Remarks : 补充说明：希望直飞不需要中转
         */

        private String budgetInfo;
        private String orderTime;
        private String remarks;

        public String getBudgetInfo() {
            return budgetInfo;
        }

        public void setBudgetInfo(String budgetInfo) {
            this.budgetInfo = budgetInfo;
        }

        public String getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(String orderTime) {
            this.orderTime = orderTime;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}

