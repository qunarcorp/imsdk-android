package com.qunar.im.base.jsonbean;

/**
 * Created by xinbo.wang on 2015/4/2.
 */
public class PublicVerifyResult extends BaseResult {
    public int status_id;
    public String status;
    public String msg;
    public TokenStruct data;

    static public class TokenStruct implements BaseData
    {
        public String token;
    }
}
