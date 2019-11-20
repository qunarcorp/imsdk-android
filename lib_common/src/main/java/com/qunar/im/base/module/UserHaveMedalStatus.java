package com.qunar.im.base.module;

public class UserHaveMedalStatus {

    private int medalId;
    private String medalName;
    private String obtainCondition;
    private String smallIcon;
    private String bigLightIcon;
    private String bigGrayIcon;
    private String bigLockIcon;
    private int status;//勋章状态
    private String medalUserId;
    private String medalHost;
    private int medalUserStatus;//勋章佩戴状态
    private int userCount;//拥有用户数量


    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public String getMedalHost() {
        return medalHost;
    }

    public void setMedalHost(String medalHost) {
        this.medalHost = medalHost;
    }

    public String getBigLockIcon() {
        return bigLockIcon;
    }

    public void setBigLockIcon(String bigLockIcon) {
        this.bigLockIcon = bigLockIcon;
    }

    public int getMedalId() {
        return medalId;
    }

    public void setMedalId(int medalId) {
        this.medalId = medalId;
    }

    public String getMedalName() {
        return medalName;
    }

    public void setMedalName(String medalName) {
        this.medalName = medalName;
    }

    public String getObtainCondition() {
        return obtainCondition;
    }

    public void setObtainCondition(String obtainCondition) {
        this.obtainCondition = obtainCondition;
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getBigLightIcon() {
        return bigLightIcon;
    }

    public void setBigLightIcon(String bigLightIcon) {
        this.bigLightIcon = bigLightIcon;
    }

    public String getBigGrayIcon() {
        return bigGrayIcon;
    }

    public void setBigGrayIcon(String bigGrayIcon) {
        this.bigGrayIcon = bigGrayIcon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMedalUserId() {
        return medalUserId;
    }

    public void setMedalUserId(String medalUserId) {
        this.medalUserId = medalUserId;
    }

    public int getMedalUserStatus() {
        return medalUserStatus;
    }

    public void setMedalUserStatus(int medalUserStatus) {
        this.medalUserStatus = medalUserStatus;
    }
}
