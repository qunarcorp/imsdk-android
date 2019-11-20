package com.qunar.im.base.jsonbean;

import java.util.Map;

public class ButtonMessageJson {

    private String content;
    private String middleContent;
    private String url;
    private Map<String,String> requestPost;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMiddleContent() {
        return middleContent;
    }

    public void setMiddleContent(String middleContent) {
        this.middleContent = middleContent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getRequestPost() {
        return requestPost;
    }

    public void setRequestPost(Map<String, String> requestPost) {
        this.requestPost = requestPost;
    }
}
