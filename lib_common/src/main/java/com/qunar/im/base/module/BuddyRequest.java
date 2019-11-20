package com.qunar.im.base.module;

/**
 * Created by zhaokai on 15-12-8.
 */
public class BuddyRequest extends BaseModel {

    /**
     * 相关联的userId
     */
    private String id;
    /**
     * 当direction 是SEND的时候拒绝原因
     * 当direction 是RECEIVE的时候是验证消息
     */
    private String reason;
    /**
     * 最后一次更新时间
     */
    private long time;
    /**
     * 现在处于的状态
     *
     * @see BuddyRequest.Status
     */
    private int status;
    /**
     * 发出/收到
     *
     * @see com.qunar.im.base.module.BuddyRequest.Direction
     */
    private int direction;
    public String getId() {
        return id;
    }

    public void setId(String userId)
    {
        this.id = userId;
    }
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**
     * @see BuddyRequest.Status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @see BuddyRequest.Status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @see com.qunar.im.base.module.BuddyRequest.Direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     * @see com.qunar.im.base.module.BuddyRequest.Direction
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    public static final class Status {
        /**
         * 正在处理中
         */
        public static final int PENDING = 0;
        /**
         * 接受好友关系
         */
        public static final int ACCEPT = 1;
        /**
         * 拒绝好友关系
         */
        public static final int DENY = 2;
    }


    public static final class Direction {
        public static final int SEND = 0;
        public static final int RECEIVE = 1;
    }
}
