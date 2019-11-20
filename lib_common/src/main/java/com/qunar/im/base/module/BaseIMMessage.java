package com.qunar.im.base.module;

/**
 * Created by xinbo.wang on 2015/5/8.
 */
public class BaseIMMessage extends BaseModel {
    public static final int PREPARE = 0;
    public static final int READ = 1;
    public static final int READING = 2;
    public static final int LEFT = 1;
    public static final int MIDDLE = 2;
    public static final int RIGHT = 4;
    public int position;
    public String backupInfo;
    public String channelId;
    public String channelid;
    public String realto;
    public String qchatid;
    public String realfrom;
    public String userInfo;
    private int readStatus;

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public class Chan{
        private String cn;
        private String d;
        private String usrType;

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public String getUsrType() {
            return usrType;
        }

        public void setUsrType(String usrType) {
            this.usrType = usrType;
        }
    }
}
