package com.qunar.im.base.jsonbean;

/**
 * Created by saber on 15-8-12.
 */
public class VideoMessageResult {
    public String ThumbUrl;
    public String ThumbName;
    public String FileName;
    public String Width;
    public String Height;
    public String FileSize;
    public String Duration;
    public String FileUrl;
    public String fileMd5;
    public String LocalVideoOutPath;
    public boolean newVideo;

    public static VideoMessageResult createInstance(ImgVideoBean bean){
        VideoMessageResult videoMessageResult = new VideoMessageResult();
        videoMessageResult.FileUrl = bean.url;
        videoMessageResult.ThumbUrl = bean.thumbUrl;
        videoMessageResult.FileName = bean.fileName;
        videoMessageResult.Duration = bean.Duration;
        videoMessageResult.FileSize = bean.fileSize;
        videoMessageResult.Width = bean.Width;
        videoMessageResult.Height = bean.Height;
        return  videoMessageResult;
    }
}
