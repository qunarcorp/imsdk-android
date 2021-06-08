package com.qunar.im.base.module;

/**
 * Created by hubin on 2017/9/11.
 */

public class IMGroup {
    //群id
    private String GroupId;
    //群名称
    private String Name;
    //群公告
    private String Introduce;
    //群头像
    private String HeaderSrc;
    //群说明
    private String Topic;
    //更新次数
    private String LastUpdateTime;
    //强化消息字段
    private String ExtendedFlag;

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIntroduce() {
        return Introduce;
    }

    public void setIntroduce(String introduce) {
        Introduce = introduce;
    }

    public String getHeaderSrc() {
        return HeaderSrc;
    }

    public void setHeaderSrc(String headerSrc) {
        HeaderSrc = headerSrc;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    public String getExtendedFlag() {
        return ExtendedFlag;
    }

    public void setExtendedFlag(String extendedFlag) {
        ExtendedFlag = extendedFlag;
    }
}
