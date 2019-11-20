package com.qunar.im.base.module;

public class VideoDataResponse {


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

        private long createTime;
        private String firstThumb;
        private String firstThumbUrl;
        private String onlineUrl;
        private OriginFileInfoBean originFileInfo;
        private String originFileMd5;
        private String originFilename;
        private String originUrl;
        private boolean ready;
        private String resourceId;
        private TransFileInfoBean transFileInfo;
        private String transFileMd5;
        private String transFilename;
        private String transUrl;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getFirstThumb() {
            return firstThumb;
        }

        public void setFirstThumb(String firstThumb) {
            this.firstThumb = firstThumb;
        }

        public String getFirstThumbUrl() {
            return firstThumbUrl;
        }

        public void setFirstThumbUrl(String firstThumbUrl) {
            this.firstThumbUrl = firstThumbUrl;
        }

        public String getOnlineUrl() {
            return onlineUrl;
        }

        public void setOnlineUrl(String onlineUrl) {
            this.onlineUrl = onlineUrl;
        }

        public OriginFileInfoBean getOriginFileInfo() {
            return originFileInfo;
        }

        public void setOriginFileInfo(OriginFileInfoBean originFileInfo) {
            this.originFileInfo = originFileInfo;
        }

        public String getOriginFileMd5() {
            return originFileMd5;
        }

        public void setOriginFileMd5(String originFileMd5) {
            this.originFileMd5 = originFileMd5;
        }

        public String getOriginFilename() {
            return originFilename;
        }

        public void setOriginFilename(String originFilename) {
            this.originFilename = originFilename;
        }

        public String getOriginUrl() {
            return originUrl;
        }

        public void setOriginUrl(String originUrl) {
            this.originUrl = originUrl;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public TransFileInfoBean getTransFileInfo() {
            return transFileInfo;
        }

        public void setTransFileInfo(TransFileInfoBean transFileInfo) {
            this.transFileInfo = transFileInfo;
        }

        public String getTransFileMd5() {
            return transFileMd5;
        }

        public void setTransFileMd5(String transFileMd5) {
            this.transFileMd5 = transFileMd5;
        }

        public String getTransFilename() {
            return transFilename;
        }

        public void setTransFilename(String transFilename) {
            this.transFilename = transFilename;
        }

        public String getTransUrl() {
            return transUrl;
        }

        public void setTransUrl(String transUrl) {
            this.transUrl = transUrl;
        }

        public static class OriginFileInfoBean {
            /**
             * bitRate : 1629
             * duration : 5160
             * height : 720
             * videoFirstThumb :
             * videoMd5 : 27303a11dc2b3e3a31122a5d2c10b5b6
             * videoName : 20190730150137372_z46kL1_SampleVideo_1280x720_1mbb.flv
             * videoSize : 1051185
             * videoSuffix : flv
             * videoType : flv1
             * width : 1280
             */

            private String bitRate;
            private int duration;
            private String height;
            private String videoFirstThumb;
            private String videoMd5;
            private String videoName;
            private int videoSize;
            private String videoSuffix;
            private String videoType;
            private String width;

            public String getBitRate() {
                return bitRate;
            }

            public void setBitRate(String bitRate) {
                this.bitRate = bitRate;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getVideoFirstThumb() {
                return videoFirstThumb;
            }

            public void setVideoFirstThumb(String videoFirstThumb) {
                this.videoFirstThumb = videoFirstThumb;
            }

            public String getVideoMd5() {
                return videoMd5;
            }

            public void setVideoMd5(String videoMd5) {
                this.videoMd5 = videoMd5;
            }

            public String getVideoName() {
                return videoName;
            }

            public void setVideoName(String videoName) {
                this.videoName = videoName;
            }

            public int getVideoSize() {
                return videoSize;
            }

            public void setVideoSize(int videoSize) {
                this.videoSize = videoSize;
            }

            public String getVideoSuffix() {
                return videoSuffix;
            }

            public void setVideoSuffix(String videoSuffix) {
                this.videoSuffix = videoSuffix;
            }

            public String getVideoType() {
                return videoType;
            }

            public void setVideoType(String videoType) {
                this.videoType = videoType;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }
        }

        public static class TransFileInfoBean {
            /**
             * bitRate : 867
             * duration : 5160
             * height : 720
             * videoFirstThumb :
             * videoMd5 : 6fdbb90f1c0038231ff451b4c2984147
             * videoName : 20190730150137372_z46kL1_SampleVideo_1280x720_1mbb_trans_o.mp4
             * videoSize : 559745
             * videoSuffix : mp4
             * videoType : h264 (High) (avc1 / 0x31637661)
             * width : 1280
             */

            private String bitRate;
            private int duration;
            private String height;
            private String videoFirstThumb;
            private String videoMd5;
            private String videoName;
            private int videoSize;
            private String videoSuffix;
            private String videoType;
            private String width;

            public String getBitRate() {
                return bitRate;
            }

            public void setBitRate(String bitRate) {
                this.bitRate = bitRate;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getVideoFirstThumb() {
                return videoFirstThumb;
            }

            public void setVideoFirstThumb(String videoFirstThumb) {
                this.videoFirstThumb = videoFirstThumb;
            }

            public String getVideoMd5() {
                return videoMd5;
            }

            public void setVideoMd5(String videoMd5) {
                this.videoMd5 = videoMd5;
            }

            public String getVideoName() {
                return videoName;
            }

            public void setVideoName(String videoName) {
                this.videoName = videoName;
            }

            public int getVideoSize() {
                return videoSize;
            }

            public void setVideoSize(int videoSize) {
                this.videoSize = videoSize;
            }

            public String getVideoSuffix() {
                return videoSuffix;
            }

            public void setVideoSuffix(String videoSuffix) {
                this.videoSuffix = videoSuffix;
            }

            public String getVideoType() {
                return videoType;
            }

            public void setVideoType(String videoType) {
                this.videoType = videoType;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }
        }
    }
}
