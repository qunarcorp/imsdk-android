package com.qunar.im.base.module;

import java.util.List;

public class MedalListResponse {


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

        private int version;
        private List<MedalListBean> medalList;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public List<MedalListBean> getMedalList() {
            return medalList;
        }

        public void setMedalList(List<MedalListBean> medalList) {
            this.medalList = medalList;
        }

        public static class MedalListBean {

            private int id;
            private String medalName;
            private String obtainCondition;
            private IconBean icon;
            private int status;

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getMedalName() {
                return medalName;
            }

            public void setMedalName(String medalName) {
                this.medalName = medalName;
            }

            public String getObtainCondition() {
                return obtainCondition;
            }

            public void setObtainCondition(String obtainCondition) {
                this.obtainCondition = obtainCondition;
            }

            public IconBean getIcon() {
                return icon;
            }

            public void setIcon(IconBean icon) {
                this.icon = icon;
            }

            public static class IconBean {

                private String small;
                private String bigLight;
                private String bigGray;
                private String bigLock;

                public String getBigLock() {
                    return bigLock;
                }

                public void setBigLock(String bigLock) {
                    this.bigLock = bigLock;
                }

                public String getSmall() {
                    return small;
                }

                public void setSmall(String small) {
                    this.small = small;
                }

                public String getBigLight() {
                    return bigLight;
                }

                public void setBigLight(String bigLight) {
                    this.bigLight = bigLight;
                }

                public String getBigGray() {
                    return bigGray;
                }

                public void setBigGray(String bigGray) {
                    this.bigGray = bigGray;
                }
            }
        }
    }
}
