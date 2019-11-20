package com.qunar.im.base.jsonbean;

import java.util.List;

/**
 * Created by hubin on 2017/11/7.
 */
public class RNSearchData {


    private int hasMore;
    private int isLoaclData;
    private int todoType;
    private int groupPriority;
    private String groupLabel;
    private String groupId;
    private String defaultportrait;
    private List<InfoBean> info;

    public int getHasMore() {
        return hasMore;
    }

    public void setHasMore(int hasMore) {
        this.hasMore = hasMore;
    }

    public int getIsLoaclData() {
        return isLoaclData;
    }

    public void setIsLoaclData(int isLoaclData) {
        this.isLoaclData = isLoaclData;
    }

    public int getTodoType() {
        return todoType;
    }

    public void setTodoType(int todoType) {
        this.todoType = todoType;
    }

    public int getGroupPriority() {
        return groupPriority;
    }

    public void setGroupPriority(int groupPriority) {
        this.groupPriority = groupPriority;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDefaultportrait() {
        return defaultportrait;
    }

    public void setDefaultportrait(String defaultportrait) {
        this.defaultportrait = defaultportrait;
    }

    public List<InfoBean> getInfo() {
        return info;
    }

    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }

    public static class InfoBean {
        /**
         * label : lee李露lucas,nigotuu7479
         * content :
         * icon :
         * uri : 0d436780d3ec47cea4bc392539da3298@conference.ejabhost2
         * UserId : lee.guo
         * Name : 郭立lee
         * HeaderSrc : lee.guo@ejabhost1.jpg
         * XmppId : lee.guo@ejabhost1
         * ExtendedFlag : true
         * GroupId : ops群@conference.ejabhost1
         * LastUpdateTime : 10
         * Introduce : 欢迎加入...
         */

        private String label;
        private String content;
        private String icon;
        private String uri;
        private String UserId;
        private String Name;
        private String HeaderSrc;
        private String XmppId;
        private boolean ExtendedFlag;
        private String GroupId;
        private int LastUpdateTime;
        private String Introduce;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getHeaderSrc() {
            return HeaderSrc;
        }

        public void setHeaderSrc(String HeaderSrc) {
            this.HeaderSrc = HeaderSrc;
        }

        public String getXmppId() {
            return XmppId;
        }

        public void setXmppId(String XmppId) {
            this.XmppId = XmppId;
        }

        public boolean isExtendedFlag() {
            return ExtendedFlag;
        }

        public void setExtendedFlag(boolean ExtendedFlag) {
            this.ExtendedFlag = ExtendedFlag;
        }

        public String getGroupId() {
            return GroupId;
        }

        public void setGroupId(String GroupId) {
            this.GroupId = GroupId;
        }

        public int getLastUpdateTime() {
            return LastUpdateTime;
        }

        public void setLastUpdateTime(int LastUpdateTime) {
            this.LastUpdateTime = LastUpdateTime;
        }

        public String getIntroduce() {
            return Introduce;
        }

        public void setIntroduce(String Introduce) {
            this.Introduce = Introduce;
        }
    }
}
