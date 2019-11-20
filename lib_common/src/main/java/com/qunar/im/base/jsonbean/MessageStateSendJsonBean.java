package com.qunar.im.base.jsonbean;

import org.json.JSONArray;

/**
 * Created by hubin on 2018/3/8.
 */

public class MessageStateSendJsonBean {
    String userid;
    JSONArray jsonArray;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}
