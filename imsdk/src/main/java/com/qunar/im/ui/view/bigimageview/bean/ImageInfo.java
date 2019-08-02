package com.qunar.im.ui.view.bigimageview.bean;

import java.io.Serializable;

/**
 * 图片信息
 */
public class ImageInfo implements Serializable {

    public String thumbnailUrl="";// 缩略图，质量很差
    public String originUrl="";// 原图或者高清图
    public String localPath="";//本地图片
    public int height=0;//图片高度
    public int width=0;//图片宽度

    private boolean returnLocal;//返回本地原图

    public boolean isReturnLocal() {
        return returnLocal;
    }

    public void setReturnLocal(boolean returnLocal) {
        this.returnLocal = returnLocal;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getThumbnailUrl() {
        if(returnLocal){
            return localPath;
        }else {
            return thumbnailUrl;
        }
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getOriginUrl() {
        if(returnLocal){
            return localPath;
        }else {
            return originUrl;
        }
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    @Override
    public String toString() {
        return "ImageInfo{" + "thumbnailUrl='" + thumbnailUrl + '\'' + ", originUrl='" + originUrl + '\'' + '}';
    }
}