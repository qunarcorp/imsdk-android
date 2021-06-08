package com.qunar.im.base.jsonbean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saber on 15-9-14.
 */
public class RobotInfoResult extends BaseJsonResult {
    public List<RobotItemResult> data;

    public static class RobotBody implements Serializable {
        public String robotEnName = "";
        public String robotCnName = "";
        public String headerurl = "";
        public String robotDesc = "";
        public String fromsource = "";
        public String tel = "";
        public boolean receiveswitch;
        public boolean replayable = true;
        public boolean rawhtml;
        public List<Action> actionlist;
    }

    public static class Action implements Serializable {
        public String mainaction;
        public ActionContent actioncontent;
        public List<SubAction> subactions;

        public static class ActionContent implements Serializable {
            public Object value;
            public String action;
        }

        public static class SubAction implements Serializable {
            public String subaction;
            public ActionContent actioncontent;
        }
    }

    public static class RobotItemResult implements Serializable {

        public String rbt_name;
        public RobotBody rbt_body;
        public int rbt_ver;
    }
}
