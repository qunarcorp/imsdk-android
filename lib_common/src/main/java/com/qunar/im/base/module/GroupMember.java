package com.qunar.im.base.module;

import android.text.TextUtils;

/**
 * Created by hubin on 2017/8/24.
 */

public class GroupMember {
    //管理权限
    public static final int OWNER = 0;
    public static final int ADMIN = 1;
    public static final int MEMBER = 2;
    public static final int NONE = 4;


    private String MemberId;//成员id 带@
    private String GroupId;//群id 带@
    private String MemberJid;//成员真是id 带@
    private String Name;//成员姓名
    private String Affiliation;//成员权限
    private String LastUpdateTime;//最后更新时间
    private String ExtendedFlag;//强化字段
    private String fuzzy;//搜索字段
    private String headerSrc;//成员头像
    private Nick nick;//名片

    public String getHeaderSrc() {
        return headerSrc;
    }

    public void setHeaderSrc(String headerSrc) {
        this.headerSrc = headerSrc;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getMemberJid() {
        return MemberJid;
    }

    public void setMemberJid(String memberJid) {
        MemberJid = memberJid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAffiliation() {
        return Affiliation;
    }

    public void setAffiliation(String affiliation) {
        Affiliation = affiliation;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    public String getExtendedFlag() {
        return ExtendedFlag;
    }

    public void setExtendedFlag(String extendedFlag) {
        ExtendedFlag = extendedFlag;
    }

    public String getFuzzy() {
        return fuzzy;
    }

    public void setFuzzy(String fuzzy) {
        this.fuzzy = fuzzy;
    }

    /**
     * 获取用户权限级别
     *
     * @return
     */
    public int getPowerLevel() {
        if (TextUtils.isEmpty(Affiliation)) {
            return NONE;
        }
        if (Affiliation.equals("m_user")) {
            return MEMBER;
        } else if (Affiliation.equals("admin")) {
            return ADMIN;
        } else if (Affiliation.equals("owner")) {
            return OWNER;
        } else if (Affiliation.equals("-1invalid")) {
            return -1;
        }else if(Affiliation.equals("-2invalid")){
            return -2;
        }else {
            return NONE;
        }
    }

    public void setPowerLevel(int powerLevel) {
        if (powerLevel == 0) {
            Affiliation = "owner";
        } else if (powerLevel == 1) {
            Affiliation = "admin";
        } else if (powerLevel == 2) {
            Affiliation = "m_user";
        } else if (powerLevel == -1) {
            Affiliation = "-1invalid";
        } else if (powerLevel == -2) {
            Affiliation = "-2invalid";
        } else {
            Affiliation = "";
        }
    }


}
