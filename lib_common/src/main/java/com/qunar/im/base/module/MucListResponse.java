package com.qunar.im.base.module;

import com.qunar.im.base.jsonbean.BaseJsonResult;

import java.util.List;

/**
 * Created by hubin on 2017/9/20.
 */

public class MucListResponse extends BaseJsonResult{

    private List<Data> data;

    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }


    public class Data {

        private String M;
        private String D;
        private String T;
        private String F;
        public void setM(String M) {
            this.M = M;
        }
        public String getM() {
            return M;
        }

        public void setD(String D) {
            this.D = D;
        }
        public String getD() {
            return D;
        }

        public void setT(String T) {
            this.T = T;
        }
        public String getT() {
            return T;
        }

        public void setF(String F) {
            this.F = F;
        }
        public String getF() {
            return F;
        }

    }
}
