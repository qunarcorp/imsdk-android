package com.qunar.im.base.jsonbean;

/**
 * Created by saber on 15-9-11.
 */
public class RequestRobotInfo extends BaseResult {
    public String robot_name;
    public int version;

    public RequestRobotInfo(String robot_name, int version) {
        this.robot_name = robot_name;
        this.version = version;

    }
}
