package com.qunar.im.base.module;

import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.jsonbean.VideoMessageResult;

import java.util.ArrayList;
import java.util.List;

public class ReleaseContentData {
    private int type;//消息类型  //1 图片类型  2 连接类型 //3 视频类型
    private String exContent;//消息内容
    private String content="";
    private List<ImageItemWorkWorldItem> imgList=new ArrayList<>();
    private ExtendMessageEntity linkContent;//用于显示连接卡片类型
    private VideoMessageResult videoContent;//用于展示视频类型


    public VideoMessageResult getVideoContent() {
        return videoContent;
    }

    public void setVideoContent(VideoMessageResult videoContent) {
        this.videoContent = videoContent;
    }

    public ExtendMessageEntity getLinkContent() {
        return linkContent;
    }

    public void setLinkContent(ExtendMessageEntity linkContent) {
        this.linkContent = linkContent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getExContent() {
        return exContent;
    }

    public void setExContent(String exContent) {
        this.exContent = exContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ImageItemWorkWorldItem> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImageItemWorkWorldItem> imgList) {
        this.imgList = imgList;

    }
}
