package com.qunar.im.rtc.webrtc;

public class PeerConnectionParameters {
    public final boolean videoCallEnabled;
    public final boolean loopback;
    public final boolean tracing;
    public final int videoWidth;
    public final int videoHeight;
    public final int videoFps;
    public final int videoMaxBitrate;
    public final String videoCodec;
    public final boolean videoCodecHwAcceleration;
    public final boolean videoFlexfecEnabled;
    public final int audioStartBitrate;
    public final String audioCodec;
    public final boolean noAudioProcessing;
    public final boolean aecDump;
    public final boolean useOpenSLES;
    public final boolean disableBuiltInAEC;
    public final boolean disableBuiltInAGC;
    public final boolean disableBuiltInNS;
    public final boolean enableLevelControl;
    private final DataChannelParameters dataChannelParameters;

    private PeerConnectionParameters(boolean videoCallEnabled, boolean loopback, boolean tracing,
                                    int videoWidth, int videoHeight, int videoFps, int videoMaxBitrate, String videoCodec,
                                    boolean videoCodecHwAcceleration, boolean videoFlexfecEnabled, int audioStartBitrate,
                                    String audioCodec, boolean noAudioProcessing, boolean aecDump, boolean useOpenSLES,
                                    boolean disableBuiltInAEC, boolean disableBuiltInAGC, boolean disableBuiltInNS,
                                    boolean enableLevelControl) {
        this(videoCallEnabled, loopback, tracing, videoWidth, videoHeight, videoFps, videoMaxBitrate,
                videoCodec, videoCodecHwAcceleration, videoFlexfecEnabled, audioStartBitrate, audioCodec,
                noAudioProcessing, aecDump, useOpenSLES, disableBuiltInAEC, disableBuiltInAGC,
                disableBuiltInNS, enableLevelControl, null);
    }

    public PeerConnectionParameters(boolean videoCallEnabled, boolean loopback, boolean tracing,
                                    int videoWidth, int videoHeight, int videoFps, int videoMaxBitrate, String videoCodec,
                                    boolean videoCodecHwAcceleration, boolean videoFlexfecEnabled, int audioStartBitrate,
                                    String audioCodec, boolean noAudioProcessing, boolean aecDump, boolean useOpenSLES,
                                    boolean disableBuiltInAEC, boolean disableBuiltInAGC, boolean disableBuiltInNS,
                                    boolean enableLevelControl,DataChannelParameters dataChannelParameters) {
        this.videoCallEnabled = videoCallEnabled;
        this.loopback = loopback;
        this.tracing = tracing;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoFps = videoFps;
        this.videoMaxBitrate = videoMaxBitrate;
        this.videoCodec = videoCodec;
        this.videoFlexfecEnabled = videoFlexfecEnabled;
        this.videoCodecHwAcceleration = videoCodecHwAcceleration;
        this.audioStartBitrate = audioStartBitrate;
        this.audioCodec = audioCodec;
        this.noAudioProcessing = noAudioProcessing;
        this.aecDump = aecDump;
        this.useOpenSLES = useOpenSLES;
        this.disableBuiltInAEC = disableBuiltInAEC;
        this.disableBuiltInAGC = disableBuiltInAGC;
        this.disableBuiltInNS = disableBuiltInNS;
        this.enableLevelControl = enableLevelControl;
        this.dataChannelParameters = dataChannelParameters;
    }

    /**
     * Peer connection parameters.
     */
    public static class DataChannelParameters {
        public final boolean ordered;
        public final int maxRetransmitTimeMs;
        public final int maxRetransmits;
        public final String protocol;
        public final boolean negotiated;
        public final int id;

        public DataChannelParameters(boolean ordered, int maxRetransmitTimeMs, int maxRetransmits,
                                     String protocol, boolean negotiated, int id) {
            this.ordered = ordered;
            this.maxRetransmitTimeMs = maxRetransmitTimeMs;
            this.maxRetransmits = maxRetransmits;
            this.protocol = protocol;
            this.negotiated = negotiated;
            this.id = id;
        }
    }

    public static class PCBuilder{
        private boolean videoCallEnabled;
        private boolean loopback;
        private boolean tracing;
        private int videoWidth;
        private int videoHeight;
        private int videoFps;
        private int videoMaxBitrate;
        private String videoCodec;
        private boolean videoCodecHwAcceleration;
        private boolean videoFlexfecEnabled;
        private int audioStartBitrate;
        private String audioCodec;
        private boolean noAudioProcessing;
        private boolean aecDump;
        private boolean useOpenSLES;
        private boolean disableBuiltInAEC;
        private boolean disableBuiltInAGC;
        private boolean disableBuiltInNS;
        private boolean enableLevelControl;
        private DataChannelParameters dataChannelParameters;

        public PCBuilder()
        {
            this.videoCallEnabled = true;
            this.loopback = false;
            this.tracing = false;
            this.videoWidth = 0;
            this.videoHeight = 0;
            this.videoFps = 24;
            this.videoMaxBitrate = 1500;
            this.videoCodec = "VP8";
            this.videoCodecHwAcceleration = true;
            this.videoFlexfecEnabled = false;
            this.audioStartBitrate = 16;
            this.audioCodec="opus";
            this.noAudioProcessing = false;
            this.aecDump = false;
            this.useOpenSLES = false;
            this.disableBuiltInNS=false;
            this.disableBuiltInAGC = false;
            this.disableBuiltInAEC = false;
            this.enableLevelControl=false;
            this.dataChannelParameters = null;
        }

        public PeerConnectionParameters build()
        {
            return new PeerConnectionParameters(this.videoCallEnabled,this.loopback,this.tracing,this.videoWidth,
                    this.videoHeight,this.videoFps,this.videoMaxBitrate,this.videoCodec,this.videoCodecHwAcceleration,
                    this.videoFlexfecEnabled,this.audioStartBitrate,this.audioCodec,this.noAudioProcessing,
                    this.aecDump,this.useOpenSLES,this.disableBuiltInAEC,this.disableBuiltInAGC,this.disableBuiltInNS,
                    this.enableLevelControl,this.dataChannelParameters);
        }

        public PCBuilder setDataChannelParameters(DataChannelParameters parameters)
        {
            this.dataChannelParameters = parameters;
            return this;
        }

        public PCBuilder enableLevelControl(boolean enableLevelControl)
        {
            this.enableLevelControl = enableLevelControl;
            return this;
        }
        public PCBuilder disableBuildInNS(boolean disableBuiltInNS)
        {
            this.disableBuiltInNS = disableBuiltInNS;
            return this;
        }
        public PCBuilder disableBuildInAGC(boolean disableBuiltInAGC)
        {
            this.disableBuiltInAGC = disableBuiltInAGC;
            return this;
        }
        public PCBuilder disableBuiltInAEC(boolean disableBuiltInAEC)
        {
            this.disableBuiltInAEC = disableBuiltInAEC;
            return this;
        }

        public PCBuilder useOpenSLES(boolean useOpenSLES)
        {
            this.useOpenSLES = useOpenSLES;
            return this;
        }

        public PCBuilder aecDump(boolean aecDump)
        {
            this.aecDump = aecDump;
            return this;
        }

        public PCBuilder noAudioProccessing(boolean noAudioProcessing)
        {
            this.noAudioProcessing = noAudioProcessing;
            return this;
        }
        public PCBuilder audioCodec(String audioCodec)
        {
            this.audioCodec = audioCodec;
            return this;
        }

        public PCBuilder audioStartBitrate(int audioStartBitrate)
        {
            this.audioStartBitrate = audioStartBitrate;
            return this;
        }

        public PCBuilder videoFlexfecEnabled(boolean videoFlexfecEnabled)
        {
            this.videoFlexfecEnabled = videoFlexfecEnabled;
            return this;
        }
        public PCBuilder videoCodecHwAcceleration(boolean videoCodecHwAcceleration)
        {
            this.videoCodecHwAcceleration = videoCodecHwAcceleration;
            return this;
        }
        public PCBuilder videoCodec(String videoCodec)
        {
            this.videoCodec = videoCodec;
            return this;
        }

        public PCBuilder videoMaxBitrate(int videoMaxBitrate)
        {
            this.videoMaxBitrate = videoMaxBitrate;
            return this;
        }

        public PCBuilder videoFps(int fps)
        {
            this.videoFps = fps;
            return this;
        }
        public PCBuilder videoHeight(int videoHeight)
        {
            this.videoHeight = videoHeight;
            return this;
        }

        public PCBuilder videoWidth(int videoWidth)
        {
            this.videoWidth = videoWidth;
            return this;
        }

        public PCBuilder videoEnable(boolean enable)
        {
            this.videoCallEnabled = enable;
            return this;
        }

        public PCBuilder lookback(boolean loopback)
        {
            this.loopback = loopback;
            return this;
        }

        public PCBuilder tracing(boolean tracing)
        {
            this.tracing = tracing;
            return this;
        }
    }

}