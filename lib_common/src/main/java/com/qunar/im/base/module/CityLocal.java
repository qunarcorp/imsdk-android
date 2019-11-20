package com.qunar.im.base.module;

import java.util.List;

public class CityLocal {


    /**
     * ret : true
     * errcode : 0
     * errmsg :
     * data : [{"cityName":"上海","id":6},{"cityName":"北京","id":1},{"cityName":"成都","id":4},{"cityName":"武汉","id":5}]
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
         * cityName : 上海
         * id : 6
         */

        private String cityName;
        private int id;

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
