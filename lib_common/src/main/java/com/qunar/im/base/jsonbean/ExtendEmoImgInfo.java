package com.qunar.im.base.jsonbean;

/**
 * emoji image extend info json
 * Created by lihaibin.li on 2018/2/5.
 */

public class ExtendEmoImgInfo {

    /**
     * url :
     * width : 100
     * height : 100
     * pkgid : qunar_camel
     * shortcut : 4600afd7a10bab5f3a0cd0b29171058b
     */

    private String url;
    private int width;
    private int height;
    private String pkgid;
    private String shortcut;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPkgid() {
        return pkgid;
    }

    public void setPkgid(String pkgid) {
        this.pkgid = pkgid;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }
}
