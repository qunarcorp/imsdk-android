package com.qunar.im.base.jsonbean;

import android.text.TextUtils;

/**
 * Created by froyomu on 2019/5/9
 * <p>
 * Describe:
 */
public class AtInfo implements Comparable<AtInfo>{
    public String msgId;
    public String xmppid;
    public String from;
//    public String atcontent;
    public boolean isAtAll;

    @Override
    public int compareTo(AtInfo another) {
        return this.getMsgId().compareTo(another.getMsgId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(this.getMsgId() == null) return false;
        String msgId = ((AtInfo)o).getMsgId();
        if(TextUtils.isEmpty(msgId)) return false;
        return this.getMsgId().equals(msgId);
    }

    public String getMsgId() {
        return msgId;
    }
}
