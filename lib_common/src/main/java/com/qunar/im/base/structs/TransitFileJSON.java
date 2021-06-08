package com.qunar.im.base.structs;

import com.qunar.im.base.jsonbean.BaseResult;

/**
 * Created by xinbo.wang on 2015/4/29.
 */
public class TransitFileJSON extends BaseResult {
    public String HttpUrl;
    public String FileName;
    public String FileSize;
    public String FILEID;
    public String FILEMD5;
    public String LocalFile;
    public boolean noMD5;

    public TransitFileJSON(){

    }

    public TransitFileJSON(String httpUrl,String fileName,String fileSize,String fid,String md5)
    {
        this.HttpUrl = httpUrl;
        this.FileName = fileName;
        this.FileSize = fileSize;
        this.FILEID = fid;
        this.FILEMD5 = md5;
    }
}
