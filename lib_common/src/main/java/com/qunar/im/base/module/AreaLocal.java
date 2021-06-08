package com.qunar.im.base.module;

import java.util.List;

public class AreaLocal {

    /**
     * ret : true
     * errcode : 0
     * errmsg :
     * data : {"list":[{"areaID":13,"enable":1,"areaName":"东升B2-4-A区","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"东升"},{"areaID":14,"enable":1,"areaName":"东升B2-4-B区","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"东升"},{"areaID":15,"enable":1,"areaName":"东升B2-4-C区","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"东升"},{"areaID":16,"enable":1,"areaName":"东升B2-4-D区","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"东升"},{"areaID":41,"enable":1,"areaName":"东升D1-1层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":""},{"areaID":42,"enable":1,"areaName":"东升D1-2层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":""},{"areaID":43,"enable":1,"areaName":"东升D1-3层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":""},{"areaID":44,"enable":1,"areaName":"东升D1-4层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":""},{"areaID":21,"enable":1,"areaName":"八爪鱼电话会议设备","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"设备"},{"areaID":22,"enable":1,"areaName":"投影仪预约","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"设备"},{"areaID":10,"enable":1,"areaName":"电子大厦17层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"电子大厦"},{"areaID":11,"enable":1,"areaName":"电子大厦19层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"电子大厦"},{"areaID":9,"enable":1,"areaName":"电子大厦3层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"电子大厦"},{"areaID":4,"enable":1,"areaName":"维亚大厦16层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"维亚大厦"},{"areaID":5,"enable":1,"areaName":"维亚大厦17层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"维亚大厦"},{"areaID":1,"enable":1,"areaName":"维亚大厦3层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"维亚大厦"},{"areaID":2,"enable":1,"areaName":"维亚大厦5层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"维亚大厦"},{"areaID":3,"enable":1,"areaName":"维亚大厦8层","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":"维亚大厦"},{"areaID":32,"enable":1,"areaName":"维亚大厦915会议区","morningStarts":"08:00:00","eveningEnds":"20:00:00","description":""}]}
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
        private List<ListBean> list;
        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * areaID : 13
             * enable : 1
             * areaName : 东升B2-4-A区
             * morningStarts : 08:00:00
             * eveningEnds : 20:00:00
             * description : 东升
             */

            private int areaID;
            private int enable;
            private String areaName;
            private String morningStarts;
            private String eveningEnds;
            private String description;

            public int getAreaID() {
                return areaID;
            }

            public void setAreaID(int areaID) {
                this.areaID = areaID;
            }

            public int getEnable() {
                return enable;
            }

            public void setEnable(int enable) {
                this.enable = enable;
            }

            public String getAreaName() {
                return areaName;
            }

            public void setAreaName(String areaName) {
                this.areaName = areaName;
            }

            public String getMorningStarts() {
                return morningStarts;
            }

            public void setMorningStarts(String morningStarts) {
                this.morningStarts = morningStarts;
            }

            public String getEveningEnds() {
                return eveningEnds;
            }

            public void setEveningEnds(String eveningEnds) {
                this.eveningEnds = eveningEnds;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }
}
