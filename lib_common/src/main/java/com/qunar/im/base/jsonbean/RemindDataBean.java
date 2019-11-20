package com.qunar.im.base.jsonbean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemindDataBean {
    private String title;
    private String url;
    private Map<String,String> params;//json串
    private List<Map<String,String>> keyValues = new ArrayList<>();


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public List<Map<String, String>> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(List<Map<String, String>> keyValues) {
        this.keyValues = keyValues;
    }
}
