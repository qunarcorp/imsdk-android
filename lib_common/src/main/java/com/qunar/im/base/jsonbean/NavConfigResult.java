package com.qunar.im.base.jsonbean;

import java.io.Serializable;

/**
 * Created by xinbo.wang on 2015/4/2.
 */
public class NavConfigResult extends BaseResult {
    public String version;
    public Versions versions = new Versions();
    public AdContent ad = new AdContent();
    public Baseaddess baseaddess = new Baseaddess();
    public Login Login = new Login();
    public ImConfig imConfig = new ImConfig();
    public Ability ability = new Ability();
    public OPS ops = new OPS();
    public QcAdmin qcadmin = new QcAdmin();
    public RNAndroidAbility RNAndroidAbility = new RNAndroidAbility();
    public Video video = new Video();
    public String hosts;

    public class AdContent implements Serializable {
        public String adurl;
        public String adsec;
        public boolean shown;

        @Override
        public String toString() {
            return "AdContent{" +
                    "adurl='" + adurl + '\'' +
                    ", adsec='" + adsec + '\'' +
                    ", shown=" + shown +
                    '}';
        }
    }

    public class Baseaddess implements Serializable {
        public String xmpp;
        public int xmppmport;
        public int protobufPort;
        public String domain;
        public String javaurl;
        public String pubkey;
        public String apiurl;
        public String videourl;
        public String fileurl;
        public String checkurl;
        public String sms_token;
        public String sms_verify;
        public String hotcheckurl;
        public String checkconfig;
        public String simpleapiurl;
        public String httpurl;
        public String wikiurl;
        public String leaderurl;
        public String mobileurl;
        public String shareurl;
        public String domainhost;
        public String resetPwdUrl;
        public String appWeb;
        public String payurl;

        @Override
        public String toString() {
            return "Baseaddess{" +
                    "xmpp='" + xmpp + '\'' +
                    ", xmppmport=" + xmppmport +
                    ", protobufPort=" + protobufPort +
                    ", domain='" + domain + '\'' +
                    ", javaurl='" + javaurl + '\'' +
                    ", pubkey='" + pubkey + '\'' +
                    ", apiurl='" + apiurl + '\'' +
                    ", fileurl='" + fileurl + '\'' +
                    ", checkurl='" + checkurl + '\'' +
                    ", sms_token='" + sms_token + '\'' +
                    ", sms_verify='" + sms_verify + '\'' +
                    ", hotcheckurl='" + hotcheckurl + '\'' +
                    ", checkconfig='" + checkconfig + '\'' +
                    ", simpleapiurl='" + simpleapiurl + '\'' +
                    ", wikiurl='" + wikiurl + '\'' +
                    ", leaderurl='" + leaderurl + '\'' +
                    ", mobileurl='" + mobileurl + '\'' +
                    ", shareurl='" + shareurl + '\'' +
                    ", domainhost='" + domainhost + '\'' +
                    ", appWeb='" + appWeb + '\'' +
                    ", payurl='" + payurl + '\'' +
                    '}';
        }
    }

    public class Ability implements Serializable {
        public String qCloudHost;
        public String qcGrabOrder;
        public String qcZhongbao;
        //不知道什么
        public String resetpwd;
        //搜索地址
        public String searchurl;
        //一个配置的config
        public String mconfig;
        public boolean showmsgstat;
        public String new_searchurl;

        @Override
        public String toString() {
            return "Ability{" +
                    ", qCloudHost='" + qCloudHost + '\'' +
                    ", qcGrabOrder='" + qcGrabOrder + '\'' +
                    ", qcZhongbao='" + qcZhongbao + '\'' +
                    ", resetpwd='" + resetpwd + '\'' +
                    ", searchurl='" + searchurl + '\'' +
                    ", mconfig='" + mconfig + '\'' +
                    ", new_searchurl='" + new_searchurl + '\'' +
                    '}';
        }
    }

    public class OPS implements Serializable {
        public String host;
        public String checkversion;
        public String conf;
    }

    public class Login implements Serializable {
        public String loginType;

        @Override
        public String toString() {
            return "Login{" +
                    "loginType='" + loginType + '\'' +
                    '}';
        }
    }

    public class ImConfig implements Serializable {
        public String VideoHost;
        public String OpsAPI;
        public int RsaEncodeType;
        public boolean showOrganizational;
        public boolean showOA;
        public String uploadLog;
        public String mail;
        public String foundConfigUrl;
        public boolean isToC;

        @Override
        public String toString() {
            return "ImConfig{" +
                    "VideoHost='" + VideoHost + '\'' +
                    ", OpsAPI='" + OpsAPI + '\'' +
                    "showOrganizational='" + showOrganizational + '\'' +
                    ", showOA='" + showOA + '\'' +
                    ", uploadLog='" + uploadLog + '\'' +
                    ", RsaEncodeType=" + RsaEncodeType +
                    ", email=" + mail +
                    ", foundConfigUrl=" + foundConfigUrl +
                    ", isToC=" + isToC +
                    '}';
        }
    }

    public class Versions implements Serializable {
        public int checkconfig;

        @Override
        public String toString() {
            return "Versions{" +
                    "checkconfig=" + checkconfig +
                    '}';
        }
    }

    public class QcAdmin implements Serializable {
        public String host;

        @Override
        public String toString() {
            return "QcAdmin{" +
                    "host=" + host +
                    '}';
        }
    }


    public class RNAndroidAbility implements Serializable {


        public boolean RNMineView = true;
        public boolean RNSettingView = true;
        public boolean RNUserCardView = true;
        public boolean RNPublicNumberListView = false;
        public boolean RNContactView = true;
        public boolean RNAboutView = false;
        public boolean RNGroupCardView = false;
        public boolean RNGroupListView = false;


        @Override
        public String toString() {
            return "RNAbility{" +
                    "RNMineView=" + RNMineView +
                    ", RNSettingView=" + RNSettingView +
                    ", RNUserCardView=" + RNUserCardView +
                    ", RNPublicNumberListView=" + RNPublicNumberListView +
                    ", RNContactView=" + RNContactView +
                    ", RNAboutView=" + RNAboutView +
                    ", RNGroupCardView=" + RNGroupCardView +
                    ", RNGroupListView=" + RNGroupListView +
                    '}';
        }
    }

    public class Video implements Serializable {
        public String new_host;
    }

    @Override
    public String toString() {
        return "NavConfigResult{" +
                "version='" + version + '\'' +
                ", versions=" + versions +
                ", ad=" + ad +
                ", baseaddess=" + baseaddess +
                ", Login=" + Login +
                ", imConfig=" + imConfig +
                ", ability=" + ability +
                ", ops=" + ops +
                ", qcadmin=" + qcadmin +
                ", RNAndroidAbility=" + RNAndroidAbility +
                ", hosts='" + hosts + '\'' +
                '}';
    }
}