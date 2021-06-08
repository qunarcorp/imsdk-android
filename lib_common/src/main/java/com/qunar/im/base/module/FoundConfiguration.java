package com.qunar.im.base.module;

import java.util.ArrayList;
import java.util.List;

public class FoundConfiguration {



    private int errorCode;
    private String errorMsg;
    private boolean ret;
    private List<DataBean> data = new ArrayList<>();

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private String groupName;
        private int groupId;
        private String groupIcon;
        private List<MembersBean> members = new ArrayList<>();

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public String getGroupIcon() {
            return groupIcon;
        }

        public void setGroupIcon(String groupIcon) {
            this.groupIcon = groupIcon;
        }

        public List<MembersBean> getMembers() {
            return members;
        }

        public void setMembers(List<MembersBean> members) {
            this.members = members;
        }

        public static class MembersBean {
            /**
             * memberName : 考勤查询
             * memberAction : https://qunar.it/webapp/index.html#/user/myAttendance
             * memberId : 0
             * memberIcon : https://ops-superoacp.qunarzz.com/ops_superoa_ops_superoa/prod/lee.guo/2018/12/24/14:52:25/%E8%80%83%E5%8B%A4.png
             */



            private String memberName;
            private String memberAction;
            private int memberId;
            private String memberIcon;
            /**
             * appType : 2
             * entrance : index.android
             * bundle : test
             * version : 1
             * properties : {"key1":"value1"}
             * bundleUrls : /startalk/management/file/download?key=9bb9d57758d886515f3dfbf317ad8934
             * module :
             * showNativeNav : true
             * navTitle : 演示demo
             */

            private int appType;
            private String entrance;
            private String bundle;
            private int version;
            private String properties;
            private String bundleUrls;
            private String module;
            private boolean showNativeNav;
            private String navTitle;

            public String getMemberName() {
                return memberName;
            }

            public void setMemberName(String memberName) {
                this.memberName = memberName;
            }

            public String getMemberAction() {
                return memberAction;
            }

            public void setMemberAction(String memberAction) {
                this.memberAction = memberAction;
            }

            public int getMemberId() {
                return memberId;
            }

            public void setMemberId(int memberId) {
                this.memberId = memberId;
            }

            public String getMemberIcon() {
                return memberIcon;
            }

            public void setMemberIcon(String memberIcon) {
                this.memberIcon = memberIcon;
            }

            public int getAppType() {
                return appType;
            }

            public void setAppType(int appType) {
                this.appType = appType;
            }

            public String getEntrance() {
                return entrance;
            }

            public void setEntrance(String entrance) {
                this.entrance = entrance;
            }

            public String getBundle() {
                return bundle;
            }

            public void setBundle(String bundle) {
                this.bundle = bundle;
            }

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }

            public String getProperties() {
                return properties;
            }

            public void setProperties(String properties) {
                this.properties = properties;
            }

            public String getBundleUrls() {
                return bundleUrls;
            }

            public void setBundleUrls(String bundleUrls) {
                this.bundleUrls = bundleUrls;
            }

            public String getModule() {
                return module;
            }

            public void setModule(String module) {
                this.module = module;
            }

            public boolean isShowNativeNav() {
                return showNativeNav;
            }

            public void setShowNativeNav(boolean showNativeNav) {
                this.showNativeNav = showNativeNav;
            }

            public String getNavTitle() {
                return navTitle;
            }

            public void setNavTitle(String navTitle) {
                this.navTitle = navTitle;
            }
        }
    }
}
