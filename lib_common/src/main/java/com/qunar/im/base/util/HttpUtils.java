package com.qunar.im.base.util;

import com.qunar.im.base.jsonbean.CheckFileResult;
import com.qunar.im.base.jsonbean.DownloadImageResult;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.protocol.HttpContinueDownloadCallback;
import com.qunar.im.base.protocol.HttpRequestCallback;
import com.qunar.im.base.protocol.HttpUrlConnectionHandler;
import com.qunar.im.base.protocol.Protocol;
import com.qunar.im.base.transit.DownloadRequest;
import com.qunar.im.base.transit.UploadImageRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaopeng.wu on 2014/5/14.
 */
public class HttpUtils {
    private static final String TAG = HttpUtils.class.getSimpleName();
    private static Map<String,DownloadRequest> downloadRequestMap = new HashMap<>();

	public static void getUploadImageUrl(final File file, String url,String fileName, final UploadImageRequest request) throws IOException {
        final UploadImageResult result = new UploadImageResult();
        HttpUrlConnectionHandler.executeUpload(url, file, request.key,fileName,request.progressRequestListener,
                request.params,new HttpRequestCallback() {
            @Override
            public void onComplete(InputStream response) {
                if (response!=null) {
                    String resultString = null;
                    try {
                        resultString = Protocol.parseStream(response);
                        LogUtil.d("debug", resultString);
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    }
                    try {
                        CheckFileResult result1 =
                                JsonUtils.getGson().fromJson(resultString, CheckFileResult.class);
                        if(result1 == null || !result1.ret){
                            request.requestComplete.onRequestComplete(request.id, result);
                        }else {

                            result.fileName = result1.data.
                                    substring(result1.data.lastIndexOf("/")+1);
                            result.httpUrl = result1.data.
                                    substring(result1.data.indexOf("file/"));
//                            result.httpUrl = result1.data.substring(result1.data.indexOf("file/"));
//                            if(!result.httpUrl.contains("?")) result.httpUrl+="?";
//                            if(!result.httpUrl.contains("name="))
//                            {
//                                String fname = result.httpUrl.substring(result.httpUrl.lastIndexOf("/") + 1);
//                                result.httpUrl = result.httpUrl+"?name="+fname
//                                        +"&file="+fname+"&fileName="+fname;
//                                result.httpUrl = result.httpUrl +"&name="+fname+"&file="+
//                                        fname+"&fileName="+fname;
//                                result.fileName = fname;
//                            }
//                            else {
//                                String name =
//                                        result.httpUrl.substring(result.httpUrl.indexOf("name=")+5);
//                                result.fileName = name;
//                                result.httpUrl =result.httpUrl +"&file="+
//                                        name+"&fileName="+name;
//                            }
                            request.requestComplete.onRequestComplete(request.id, result);
                        }
                    }
                    catch (Exception e)
                    {
//                        result.httpUrl = resultString;
                    }

                }
            }
            @Override
            public void onFailure(Exception e) {
                request.requestComplete.onError(e.getMessage());
            }
        });
	}

    public static void continueDownload(final String url, final File tmpFile, final File finalFile, final DownloadRequest request, final DownloadImageResult result )
    {
        long firstBytes =  tmpFile.length()+1;
        downloadRequestMap.put(url,request);
        HttpUrlConnectionHandler.excuteContinueDownload(url, request.progressListener, String.valueOf(firstBytes), new HttpContinueDownloadCallback() {
            @Override
            public void onComplete(InputStream response, boolean b) {
                LogUtil.d("debug", "download complete");
                if (response != null) {
                    FileOutputStream fileout = null;
                    try {
                        fileout = new FileOutputStream(tmpFile,b);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = response.read(buffer)) != -1) {
                            fileout.write(buffer,0,len);
                        }
                        fileout.flush();
                        //FileUtils.saveFileToExtensionStorage(finalFile);
                    } catch (Exception e) {
                        LogUtil.e(TAG,"error",e);
                    } finally {
                        if (fileout != null) {
                            try {
                                fileout.close();
                            } catch (IOException e) {
                                LogUtil.e(TAG,"error",e);
                            }
                        }
                        try {
                            response.close();
                        } catch (IOException e) {
                            LogUtil.e(TAG,"error",e);
                        }

                        boolean re = tmpFile.renameTo(finalFile);

                        result.setDownloadComplete(re);
                        if( request.requestComplete!=null)
                            request.requestComplete.onRequestComplete(result);
                        downloadRequestMap.remove(url);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                result.setDownloadComplete(false);
                if(request.requestComplete!=null)
                    request.requestComplete.onRequestComplete(result);
                downloadRequestMap.remove(url);
            }
        });
    }

    public static boolean checkDownloading(String url)
    {
        return downloadRequestMap.containsKey(url);
    }

    public static void getDownloadDrable(final String url,final String filePath,final DownloadRequest request) {
        final DownloadImageResult result = new DownloadImageResult();
        final File finalFile = new File(filePath);
        final File file = new File(filePath+".tmp");
        if (finalFile.exists()) {
            result.setDownloadComplete(true);
            if(request!=null&&request.requestComplete!=null)
                 request.requestComplete.onRequestComplete(result);
            return;
        }
        LogUtil.d("debug", "download");
        if(downloadRequestMap.containsKey(url))
        {
            downloadRequestMap.get(url).requestComplete = request.requestComplete;
            HttpUrlConnectionHandler.checkRunning(url,request.progressListener);
            return;
        }
        String basePath = file.getParent();
        File basePathFile = new File(basePath);
        if (!basePathFile.exists()) {
            basePathFile.mkdirs();
        }
        if (file.exists()) {
            continueDownload(url,file,finalFile,request,result);
            return;
        }
        try {
            downloadRequestMap.put(url,request);
            HttpUrlConnectionHandler.excuteDownload(url, request.progressListener,new HttpContinueDownloadCallback() {
                @Override
                public void onComplete(InputStream response,boolean append) {
                    LogUtil.d("debug", "download complete");
                    if (response != null) {
                        FileOutputStream fileout = null;
                        try {
                            if(!file.exists())file.createNewFile();
                            fileout = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = response.read(buffer)) != -1) {
                                fileout.write(buffer,0,len);
                            }
                            fileout.flush();
                            //FileUtils.saveFileToExtensionStorage(finalFile);
                        } catch (Exception e) {
                            LogUtil.e(TAG,"error",e);
                        } finally {
                            if (fileout != null) {
                                try {
                                    fileout.close();
                                } catch (IOException e) {
                                    LogUtil.e(TAG,"error",e);
                                }
                            }
                            try {
                                response.close();
                            } catch (IOException e) {
                                LogUtil.e(TAG,"error",e);
                            }

                            boolean re = file.renameTo(finalFile);

                            result.setDownloadComplete(re);
                            if( request.requestComplete!=null)
                                request.requestComplete.onRequestComplete(result);

                            downloadRequestMap.remove(url);
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    result.setDownloadComplete(false);
                    if(request.requestComplete!=null)
                        request.requestComplete.onRequestComplete(result);
                    downloadRequestMap.remove(url);
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG,"error",e);
            downloadRequestMap.remove(url);
        }
    }
}