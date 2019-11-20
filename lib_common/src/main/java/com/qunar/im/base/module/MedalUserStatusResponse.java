package com.qunar.im.base.module;

import java.util.List;

public class MedalUserStatusResponse {


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
        private List<UserMedalsBean> userMedals;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public List<UserMedalsBean> getUserMedals() {
            return userMedals;
        }

        public void setUserMedals(List<UserMedalsBean> userMedals) {
            this.userMedals = userMedals;
        }

        public static class UserMedalsBean {

            private int medalId;
            private String medalName;
            private String obtainCondition;
            private IconBean icon;
            private String userId;
            private String host;
            private int medalStatus;
            private int mappingVersion;
            private long updateTime;

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public int getMedalId() {
                return medalId;
            }

            public void setMedalId(int medalId) {
                this.medalId = medalId;
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

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public int getMedalStatus() {
                return medalStatus;
            }

            public void setMedalStatus(int medalStatus) {
                this.medalStatus = medalStatus;
            }

            public int getMappingVersion() {
                return mappingVersion;
            }

            public void setMappingVersion(int mappingVersion) {
                this.mappingVersion = mappingVersion;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }

            public static class IconBean {

                private String small;
                private String bigLight;
                private String bigGray;

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
