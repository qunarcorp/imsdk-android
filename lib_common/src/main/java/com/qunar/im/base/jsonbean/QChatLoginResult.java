package com.qunar.im.base.jsonbean;

import java.util.ArrayList;

/**
 * Created by saber on 15-10-12.
 */
public class QChatLoginResult extends BaseJsonResult {
    public ArrayList<QchatUserInfo> data;

    public static class QchatUserInfo extends QVTResponseResult.QVT {
        public String username;
        public String type;
    }
}
