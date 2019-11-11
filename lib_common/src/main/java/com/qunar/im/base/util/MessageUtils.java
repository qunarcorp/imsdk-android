package com.qunar.im.base.util;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;

import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.VideoMessageResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by saber on 15-8-21.
 */
public class MessageUtils {

    public static void downloadAttachedComplete(Context context, String messageId) {
        Intent intent = new Intent();
        intent.setAction(Constants.BroadcastFlag.MESSAGE_HAS_FULLY_RECEIVED);
        intent.putExtra(Constants.BundleKey.MESSAGE_ID, messageId);
        Utils.sendLocalBroadcast(intent, context);
    }

    public static IMMessage generateSingleIMMessage(String from, String to, String chatType, String realJid, String channelId) {
        IMMessage message = new IMMessage();
        Date time = Calendar.getInstance().getTime();
        time.setTime(time.getTime() + CommonConfig.divideTime);
        String id = UUID.randomUUID().toString();
        message.setId(id);
        message.setMessageID(id);
        message.setFromID(from);
        message.setUserId(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getPreferenceUserId());
        if (!TextUtils.isEmpty(chatType) && (chatType.equals(String.valueOf(ConversitionType.MSG_TYPE_CONSULT))
                || chatType.equals(String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER)))) {

//            if (CurrentPreference.getInstance().getIsIt()) {
            if (chatType.equals(String.valueOf(ConversitionType.MSG_TYPE_CONSULT))) {
                message.setQchatid("4");
                message.setType(ConversitionType.MSG_TYPE_CONSULT);
//                IMMessage.ConsultInfo ci = new IMMessage.ConsultInfo();
//                ci.setCn("consult");
//                ci.setD("send");
//                ci.setUserType("usr");
//                message.setConsultInfo(ci);
            } else if (chatType.equals(String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER))) {
                message.setQchatid("5");
                message.setType(ConversitionType.MSG_TYPE_CONSULT_SERVER);
//                IMMessage.ConsultInfo ci = new IMMessage.ConsultInfo();
//                ci.setCn("consult");
//                ci.setD("send");
//                ci.setUserType("common");
//                message.setConsultInfo(ci);
            }
            message.setChannelid("consult");
            message.setRealfrom(from);
            message.setRealto(realJid);

            IMMessage.ConsultInfo ci = new IMMessage.ConsultInfo();
            ci.setCn("consult");
            ci.setD("send");
            ci.setUserType("usr");
            message.setConsultInfo(ci);

        } else {
            message.setType(ConversitionType.MSG_TYPE_CHAT);
        }
//        if (!CommonConfig.isQtalk && ConversitionType.MSG_TYPE_CONSULT == message.getType()) {
//            message.setConversationID(CurrentPreference.getInstance().getItConnection());
//        } else {
        message.setConversationID(to);
//        }
        message.setToID(to);

        message.setTime(time);
        message.setDirection(IMMessage.DIRECTION_SEND);
        message.setIsRead(1);
        message.setMaType(4 + "");
//        message.setSignalType(6);
//        message.setIsRead(IMMessage.MSG_READ);
        //拼新消息 设置为发送中,默认认为消息阅读状态为发送到服务器状态
        message.setMessageState(MessageStatus.LOCAL_STATUS_PROCESSION);
        message.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);

        if (!TextUtils.isEmpty(channelId)) {
            message.channelId = channelId.replace("recv", "send");
        }
        return message;
    }

    public static IMMessage generateMucIMMessage(String from, String to) {
        IMMessage message = new IMMessage();
        Date time = Calendar.getInstance().getTime();
        time.setTime(time.getTime() + CommonConfig.divideTime);
        String id = UUID.randomUUID().toString();
        message.setId(id);
        message.setMessageID(id);
        message.setUserId(from);
        message.setType(ConversitionType.MSG_TYPE_GROUP);
        message.setMaType(4 + "");
//        message.setRealfrom(to);
        message.setRealfrom(from);
        message.setFromID(from);
        message.setToID(to);
        message.setNickName(com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserName());

        message.setTime(time);
        message.setDirection(IMMessage.DIRECTION_SEND);
        message.setIsRead(IMMessage.MSG_READ);
        //默认认为消息会发送成功,本地状态为发送中
        message.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
        message.setMessageState(MessageStatus.LOCAL_STATUS_PROCESSION);
        message.setConversationID(to);
        return message;
    }


    /**
     * 获取视频文件的基本信息
     *
     * @param fileName
     * @return
     */
    public static VideoMessageResult getBasicVideoInfo(String fileName) {
        VideoMessageResult videoMessageResult = new VideoMessageResult();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(fileName);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);
        long duration = timeInmillisec / 1000;
        videoMessageResult.Duration = duration + "";
        videoMessageResult.Width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        videoMessageResult.Height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        videoMessageResult.FileSize = FileUtils.getFormatFileSize(fileName);
        return videoMessageResult;
    }


    public static class ImageMsgParams {
        public int width;
        public int height;
        public String thumbUrl;
        public String sourceUrl;
        public String smallUrl;
        public File savedFilePath;
        public boolean origin;
    }


    public static void initImageUrl(ImageMsgParams params,Context context,boolean isBrowse){
        if(TextUtils.isEmpty(params.sourceUrl)){
            throw new IllegalArgumentException("需要传入原图地址");
        }

        File filePath;

        int maxWidth = (int) (Utils.getScreenWidth(QunarIMApp.getContext()) * 0.4);
        int minWidth = (int) (maxWidth * 0.5);
        //适配0的情况 默认宽高为minwidth
        if (params.width == 0 || params.height == 0) {
            params.width = minWidth;
            params.height = minWidth;
        }
        String  onlyUrl = QtalkStringUtils.UrlPage(params.sourceUrl);
        String thumbUrl = "";
        Map<String,String> map = QtalkStringUtils.URLRequest(params.sourceUrl);
        if(map.size()>0){
            onlyUrl+="?platform=touch";

            for (Map.Entry<String, String> entry : map.entrySet()) {
//            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                onlyUrl+=entry.getKey()+"="+entry.getValue()+"&";
            }
            thumbUrl = onlyUrl.substring(0,onlyUrl.length()-1);
        }



        params.thumbUrl = params.sourceUrl+"&imgtype=thumb";
    }

    public static void getDownloadFile(ImageMsgParams params, Context context, boolean isBrowse) {
        int maxWidth = (int) (Utils.getScreenWidth(QunarIMApp.getContext()) * 0.4);
        int minWidth = (int) (maxWidth * 0.5);
        //适配0的情况 默认宽高为minwidth
        if (params.width == 0 || params.height == 0) {
            params.width = minWidth;
            params.height = minWidth;
        }
        String imageUrl;
        String thumbUrl;
        File filePath;
        String smallUrl;
        int maxHeight = maxWidth;
        int minHeight = minWidth;
        int tempW;
        int tempH;
        if (params.width > params.height) {
            if (params.width > maxWidth) {
                tempW = maxWidth;
                tempH = Math.max((params.height * maxHeight / params.width), minWidth);
            } else if (params.width < minWidth) {
                tempW = minWidth;
                tempH = minWidth * params.height / params.width;
            } else {
                tempW = params.width;
                tempH = params.height;
            }
        } else {

            if (isBrowse && (((double)params.height / (double)params.width) > 2.5)) {
                tempH = (int)(params.height / (((double)params.width / (double)maxWidth)));
                tempW = maxWidth;
            } else {
                if (params.height > maxHeight) {
                    tempH = maxHeight;
                    tempW = Math.max((params.width * maxWidth / params.height), minWidth);
                } else if (params.height < minWidth) {
                    tempH = minHeight;
                    tempW = minHeight * params.width / params.height;
                } else {
                    tempW = params.width;
                    tempH = params.height;
                }
            }
        }
        params.width = tempW;//Math.max(tempW, minWidth);
        params.height = tempH;//Math.max(tempH, minWidth);
        if (params.origin) {
            imageUrl = params.sourceUrl;
            thumbUrl = String.format(imageUrl + "?w=%s&h=%s", params.width*0.7, params.height*0.7);
            smallUrl = String.format(imageUrl + "?w=%s&h=%s", params.width, params.height);//imageUrl;
            filePath = MyDiskCache.getFile(imageUrl);
        } else {
            if (params.sourceUrl.startsWith("file://") ||
                    params.sourceUrl.startsWith("http://") ||
                    params.sourceUrl.startsWith("https://")) {
                if( params.sourceUrl.startsWith("http://") ||
                        params.sourceUrl.startsWith("https://")){
                    imageUrl = QtalkStringUtils.addFilePathDomain(params.sourceUrl, true);
                    smallUrl = QtalkStringUtils.addFilePathDomain(params.sourceUrl, true);
                    thumbUrl = QtalkStringUtils.addFilePathDomain(params.sourceUrl, true);
                }else{
                    imageUrl =params.sourceUrl;
                    smallUrl = params.sourceUrl;
                    thumbUrl = params.sourceUrl;
                }

                if (params.sourceUrl.startsWith("file")) {
                    filePath = new File(Uri.decode(params.sourceUrl));
                } else {
                    String append = null;
                    String sappend = null;
                    if (params.width != 0 && params.height != 0) {
                        append = ChatTextHelper.generateQueryString4image(params.width, params.height,
                                smallUrl.indexOf("?") > 0);
                        int w = (int) (params.width*0.7);
                        int h = (int) (params.height*0.7);
                        sappend = ChatTextHelper.generateQueryString4image(w, h,
                                thumbUrl.indexOf("?") > 0);
                    }

                    if (append == null) {
                        if (smallUrl.indexOf("?") > 0) {
                            append = "&w=100&h=100";
                        } else {
                            append = "?w=100&h=100";
                        }
                    }

                    if (sappend == null) {
                        if (thumbUrl.indexOf("?") > 0) {
                            sappend = "&w=50&h=50";
                        } else {
                            sappend = "?w=50&h=50";
                        }
                    }
                    smallUrl = imageUrl + append;
                    thumbUrl = imageUrl + sappend;
                    filePath = MyDiskCache.getFile(params.sourceUrl);
                }
            } else {
                imageUrl = QtalkStringUtils.addFilePathDomain(params.sourceUrl, true);
                filePath = MyDiskCache.getFile(imageUrl);
                String append = null;
                String sappend = null;
                if (params.width != 0 && params.height != 0) {
                    append = ChatTextHelper.generateQueryString4image(params.width, params.height,
                            imageUrl.indexOf("?") > 0);
                    int w = (int) (params.width*0.7);
                    int h = (int) (params.height*0.7);
                    sappend = ChatTextHelper.generateQueryString4image(w, h,
                            imageUrl.indexOf("?") > 0);
                }

                if (append == null) {
                    if (imageUrl.indexOf("?") > 0) {
                        append = "&w=100&h=100";
                    } else {
                        append = "?w=100&h=100";
                    }
                }

                if (sappend == null) {
                    if (imageUrl.indexOf("?") > 0) {
                        sappend = "&w=50&h=50";
                    } else {
                        sappend = "?w=50&h=50";
                    }
                }

                smallUrl = imageUrl + append;
                thumbUrl = imageUrl + sappend;
                params.origin = filePath.exists() || NetworkUtils.isWifi(context);
                if (!params.origin) {
                    filePath = MyDiskCache.getFile(smallUrl);
                }
            }
        }
        params.savedFilePath = filePath;
        params.smallUrl = smallUrl;
        params.thumbUrl = thumbUrl;
        params.sourceUrl = imageUrl;



        String  onlyUrl = QtalkStringUtils.UrlPage(thumbUrl);

        Map<String,String> map = QtalkStringUtils.URLRequest(thumbUrl);
        if(map.size()>0){
            onlyUrl+="?platform=touch&";

            for (Map.Entry<String, String> entry : map.entrySet()) {
//            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                onlyUrl+=entry.getKey()+"="+entry.getValue()+"&";
            }
            thumbUrl = onlyUrl.substring(0,onlyUrl.length()-1);
        }


        if(thumbUrl.startsWith("http")){
            params.sourceUrl +=params.sourceUrl+"&webp=true";
            thumbUrl += thumbUrl+"&webp=true";
            params.thumbUrl = thumbUrl+"&imgtype=thumb";
            params.smallUrl = thumbUrl+"&imgtype=fuzzy";
        }

    }

    public static boolean filterHideMsg(IMMessage message) {
        return false;
        //return (message!=null&&!TextUtils.isEmpty(message.channelId)
        //       &&message.channelId.contains("ochat"));
    }
}
