package com.qunar.im.base.transit;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.CheckFileResult;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.HttpUtils;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.NetworkUtils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.core.services.QtalkNavicationService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xinbo.wang on 2015/3/10.
 */
public class Uploader implements Runnable,Comparable<Uploader> {
    private static final String TAG = Uploader.class.getSimpleName();
    private static int counter = 0;
    private final int id = counter++;

    private int uploadRequestServed = 0;
    private UploadLine uploadImageRequests;
    private boolean servingRequestLine = true;

    public Uploader(UploadLine ul)
    {
        this.uploadImageRequests = ul;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                final UploadImageRequest request = uploadImageRequests.take();

                final File file = new File(request.filePath);
                final String fileName = file.getName();
                if(NetworkUtils.isConnection(QunarIMApp.getContext())
                        != NetworkUtils.ConnectStatus.connected)
                {
                    if(request.requestComplete!=null)
                    {
                        request.requestComplete.onError("net failed!");
                    }
                }
                else if(TextUtils.isEmpty(request.url)) {

                    final StringBuilder chkUrl = new StringBuilder(QtalkNavicationService.getInstance().getUploadCheckLink());
                    if(request.FileType==UploadImageRequest.FILE)
                    {
                        chkUrl.append("file");
                    }else if(request.FileType == UploadImageRequest.LOGO)
                    {
                        chkUrl.append("avatar");
                    }
                    else {
                        chkUrl.append("img");
                    }
                    final String md5Key = FileUtils.fileToMD5(new File(request.filePath));
                    final int fileSize = FileUtils.getFileSize(request.filePath, FileUtils.FileSizeUnit.M);
                    final String fname = generateUrl(request, md5Key, fileSize, fileName, file);
                    Protocol.addBasicParamsOnHead(chkUrl);
                    chkUrl.append("&key=");
                    chkUrl.append(md5Key);
                    chkUrl.append("&size=");
                    chkUrl.append(fileSize);
                    chkUrl.append("&name=");
                    chkUrl.append(fname);


                    HttpUrlConnectionHandler.executeGet(chkUrl.toString(), new HttpRequestCallback() {
                        @Override
                        public void onComplete(InputStream response) {
                            try {


                                String result = Protocol.parseStream(response);
                                Logger.i("检查返回接口:"+result);
                                com.orhanobut.logger.Logger.i("检查返回接口:"+result);
                                CheckFileResult checkFileResult =
                                        JsonUtils.getGson().fromJson(result, CheckFileResult.class);
                                if(checkFileResult==null||TextUtils.isEmpty(checkFileResult.data))
                                {
                                    Logger.i("检查失败:"+chkUrl);
                                    HttpUtils.getUploadImageUrl(file, request.url, fname, request);
                                }
                                else {
                                    Logger.i("检查成功:"+chkUrl);
                                    UploadImageResult result1 = new UploadImageResult();
                                    result1.fileName = checkFileResult.data.
                                            substring(checkFileResult.data.lastIndexOf("/")+1);
                                    result1.httpUrl = checkFileResult.data.
                                            substring(checkFileResult.data.indexOf("file/"));
//                                    if(!result1.httpUrl.contains("?")) result1.httpUrl+="?";
//                                    if(!result1.httpUrl.contains("name="))
//                                    {
//                                        result1.httpUrl = result1.httpUrl +"&name="+fname+"&file="+
//                                                fname+"&fileName="+fname;
//                                        result1.fileName = fname;
//                                    }
//                                    else {
//                                        String name =
//                                                result1.httpUrl.substring(result1.httpUrl.indexOf("name=")+5);
//                                        result1.fileName = name;
//                                        result1.httpUrl =result1.httpUrl +"&file="+
//                                                name+"&fileName="+name;
//                                    }
                                    request.requestComplete.onRequestComplete(request.id,result1);
                                }
                            } catch (IOException e) {
                                LogUtil.e(TAG,"error",e);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            try {
                                HttpUtils.getUploadImageUrl(file, request.url, fname, request);
                            } catch (IOException e1) {
                                LogUtil.e(TAG,"error",e);
                            }
                        }
                    });
                }else {
                    //end url
                    try {
                        HttpUtils.getUploadImageUrl(file, request.url, fileName, request);
                    } catch (IOException e) {
                        LogUtil.e(TAG,"error",e);
                    }
                }

                synchronized (this) {
                    uploadRequestServed++;
                    while (!servingRequestLine) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                LogUtil.e(TAG,"error",e);
            }
        }
    }

    private String generateUrl(UploadImageRequest request,String md5Key,int fileSize,String fileName,File file)
    {
        //构建URL
        StringBuilder url = new StringBuilder();
        url.append(QtalkNavicationService.getInstance().getUploadFile());
        if (request.FileType == UploadImageRequest.LOGO) {
            url.append("avatar");
        } else if (request.FileType == UploadImageRequest.IMAGE) {
            if (!fileName.contains(".")) {
                byte[] bytes = FileUtils.toByteArray(file, 4);
                ImageUtils.ImageType type = ImageUtils.adjustImageType(bytes);
                if (ImageUtils.ImageType.PNG == type) {
                    fileName += ".png";
                } else if (ImageUtils.ImageType.JPEG == type) {
                    fileName += ".jpg";
                } else if (ImageUtils.ImageType.GIF == type) {
                    fileName += ".gif";
                } else if ((ImageUtils.ImageType.BMP == type)) {
                    fileName += ".bmp";
                }
                else {
                    fileName += ".img";
                }
            }
            url.append("img");
        }
        else {
            url.append("file");
        }
        Protocol.addBasicParamsOnHead(url);
        url.append("&key=");
        url.append(md5Key);
        url.append("&size=");
        url.append(fileSize);
        url.append("&name=");
        url.append(fileName);
        request.url = url.toString();
        return fileName;
    }

    public synchronized void doSomethingElse()
    {
        uploadRequestServed = 0;
        servingRequestLine = false;
    }

    public synchronized void serveRequestLine()
    {
        servingRequestLine = true;
        notifyAll();
    }

    @Override
    public synchronized int compareTo(Uploader uploader)
    {
        return uploadRequestServed < uploader.uploadRequestServed ? -1:
                (uploadRequestServed == uploader.uploadRequestServed ?0:1);
    }
}
