package com.qunar.im.base.module;

public class VideoSetting {


    /**
     * data : {"videoFileSize":-4.706979010416546E7,"videoTimeLen":-4.7959055819485694E7}
     * errcode : -1.249823269986771E7
     * errmsg : in elit deserunt
     * ret : false
     */

    private DataBean data;
    private int errcode;
    private String errmsg;
    private boolean ret;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
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

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public static class DataBean {
        /**
         * videoFileSize : -4.706979010416546E7
         * videoTimeLen : -4.7959055819485694E7
         */

        private long videoFileSize;
        private long videoTimeLen;
        private boolean highDefinition;
        private boolean useAble;
        private long videoMaxTimeLen;

        public long getVideoMaxTimeLen() {
            return videoMaxTimeLen;
        }

        public void setVideoMaxTimeLen(long videoMaxTimeLen) {
            this.videoMaxTimeLen = videoMaxTimeLen;
        }

        public boolean isUseAble() {
            return useAble;
        }

        public void setUseAble(boolean useAble) {
            this.useAble = useAble;
        }

        public boolean isHighDefinition() {
            return highDefinition;
        }

        public void setHighDefinition(boolean highDefinition) {
            this.highDefinition = highDefinition;
        }

        public long getVideoFileSize() {
            return videoFileSize;
        }

        public void setVideoFileSize(long videoFileSize) {
            this.videoFileSize = videoFileSize;
        }

        public long getVideoTimeLen() {
            return videoTimeLen;
        }

        public void setVideoTimeLen(long videoTimeLen) {
            this.videoTimeLen = videoTimeLen;
        }
    }
}
