package com.qunar.im.base.structs;

import com.qunar.im.base.jsonbean.BaseResult;

/**
 * Created by xinbo.wang on 2015/5/5.
 */
public class TransitSoundJSON extends BaseResult {
    public static final int PLAYED = 1;
    public String HttpUrl;
    public String FileName;
    public String FilePath;
    public int Seconds;
    public int s;

    public TransitSoundJSON(){}

    public TransitSoundJSON(String httpUrl,String fileName,int seconds, String filepath)
    {
        this.HttpUrl = httpUrl;
        this.FileName = fileName;
        this.Seconds = seconds;
        this.FilePath = filepath;
    }
}
