package com.qunar.im.base.module;

import java.io.Serializable;

/**
 * Created by saber on 15-9-10.
 * 公众号消息
 */
public class PublishPlatformNews extends BaseModel implements Serializable,Comparable<PublishPlatformNews> {
    public String id;
    public String platformXmppId;
    public String content;
    public int   msgType;
    public int state;
    public int direction;
    public int readTag;
    public long latestUpdateTime;
    public String extentionFlag;

    public int readState;
    public int messageState;

    @Override
    public int compareTo(PublishPlatformNews another) {
        return 0;
    }
}
