package com.qunar.im.base.module;

import java.io.Serializable;

/**
 * Created by xingchao.song on 9/1/2015.
 */
public class FavouriteMessage extends BaseModel implements Serializable,Comparable<FavouriteMessage> {
    private String id;
    private String textContent;
    private String fromUserId;
    private String fromUserName ; //消息来源
    private String fromType ; //收藏来源   1 代表从聊天消息里获取
    private String type; //消息类型
    private String item;//item 搜索用
    private String tag;//标签
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int compareTo(FavouriteMessage another) {
        return 0;
    }
}
