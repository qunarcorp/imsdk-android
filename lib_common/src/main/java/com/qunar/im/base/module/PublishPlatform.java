package com.qunar.im.base.module;

import com.qunar.im.base.util.Constants;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.Serializable;

/**
 * Created by saber on 15-9-10.
 * 已订阅公众号
 */
public class PublishPlatform extends BaseModel implements Serializable, Comparable<PublishPlatform> {

    //是否接受公众号推送
    public final static int RECEIVE_SWITCH_FLAG = 0x01;
    //是否单独显示在会话列表
    public static final int IMPORTANT_PLATFORM_MSG = 0x02;
    public static final int WEB_MSG = 0x08;
    //是否通知,只可接受消息,并默认关注
    public static final int NOTICE_MSG = 0x04;



    //系统公众号
    public static final String RBT_SYSTEM = "rbt-system@"+ Constants.Config.PUB_NET_XMPP_Domain;
    //通知公众号
    public static final String RBT_NOTICE = "rbt-notice@"+ Constants.Config.PUB_NET_XMPP_Domain;

    /**
     * robotEnName
     */
    private String id;
    /**
     * not used
     */
    private int publishPlatformType;
    /**
     * robotCnNane
     */
    private String name;
    /**
     * robotDesc
     */
    private String description;
    /**
     * headerurl
     */
    private String gravatarUrl;
    private String publishPlatformInfo;
    private int extentionFlag;
    private long latestUpdateTime;
    private String tag;
    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getId() {
        return QtalkStringUtils.userId2Jid(id);
    }

    public void setId(String id) {
        this.id = QtalkStringUtils.userId2Jid(id);
    }

    public int getPublishPlatformType() {
        return publishPlatformType;
    }

    public void setPublishPlatformType(int publishPlatformType) {
        this.publishPlatformType = publishPlatformType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGravatarUrl() {
        return gravatarUrl;
    }

    public void setGravatarUrl(String gravatarUrl) {
        this.gravatarUrl = gravatarUrl;
    }

    public String getPublishPlatformInfo() {
        return publishPlatformInfo;
    }

    public void setPublishPlatformInfo(String publishPlatformInfo) {
        this.publishPlatformInfo = publishPlatformInfo;
    }

    /**
     * 32bit, 1, 接受消息的开关
     */
    public int getExtentionFlag() {
        return extentionFlag;
    }

    public void setExtentionFlag(int extentionFlag) {
        this.extentionFlag = extentionFlag;
    }

    public long getLatestUpdateTime() {
        return latestUpdateTime;
    }

    public void setLatestUpdateTime(long latestUpdateTime) {
        this.latestUpdateTime = latestUpdateTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int compareTo(PublishPlatform another) {

        if(this.getId().equals(another.getId()))
        {
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object another)
    {
        return this.getId().equals(((PublishPlatform)another).getId());
    }
}
