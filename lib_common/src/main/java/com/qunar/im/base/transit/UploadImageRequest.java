package com.qunar.im.base.transit;


import com.qunar.im.base.protocol.ProgressRequestListener;

import java.util.Map;

/**
 * Created by xinbo.wang on 2015/3/9.
 */
public class UploadImageRequest implements Comparable<UploadImageRequest> {
    public final static int IMAGE = 0x1;
    public final static int FILE = 0x2;
    public final static int LOGO = 0x3;

    public String id;
    public String key="file";
    public String filePath;
    public int FileType;
    public String url;
    public long level;
    public Map<String,String> params;
    public IUploadRequestComplete requestComplete;
    public ProgressRequestListener progressRequestListener;


    @Override
    public int compareTo(UploadImageRequest another) {
        return this.level < another.level ? -1:
                (this.level == another.level ?0:1);
    }
}
