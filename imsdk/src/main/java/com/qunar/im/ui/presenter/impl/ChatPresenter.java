package com.qunar.im.ui.presenter.impl;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.module.VideoDataResponse;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.services.FileProgressRequestBody;
import com.qunar.im.other.CacheDataType;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.PhotoUtil;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.common.CommonDownloader;
import com.qunar.im.base.common.CommonUploader;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.common.QunarIMApp;
import com.qunar.im.base.jsonbean.DownloadImageResult;
import com.qunar.im.base.jsonbean.ExtendEmoImgInfo;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.jsonbean.HongbaoContent;
import com.qunar.im.base.jsonbean.NewRemoteConfig;
import com.qunar.im.base.jsonbean.QunarLocation;
import com.qunar.im.base.jsonbean.RemoteConfig;
import com.qunar.im.base.jsonbean.ShareLocation;
import com.qunar.im.base.jsonbean.ShareMessageEntity;
import com.qunar.im.base.jsonbean.SyncConversation;
import com.qunar.im.base.jsonbean.UploadImageResult;
import com.qunar.im.base.jsonbean.VideoMessageResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.UserConfigData;
import com.qunar.im.base.module.VideoDataResponse;
import com.qunar.im.base.protocol.ProgressRequestListener;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.TransitFileJSON;
import com.qunar.im.base.structs.TransitSoundJSON;
import com.qunar.im.base.transit.DownloadRequest;
import com.qunar.im.base.transit.IDownloadRequestComplete;
import com.qunar.im.base.transit.IUploadRequestComplete;
import com.qunar.im.base.transit.PbImageMessageQueue;
import com.qunar.im.base.transit.UploadImageRequest;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.DataCenter;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.FileUtils;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.MessageUtils;
import com.qunar.im.base.util.graphics.ImageUtils;
import com.qunar.im.base.util.graphics.MyDiskCache;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.core.manager.IMDatabaseManager;
import com.qunar.im.core.manager.IMNotificaitonCenter;
import com.qunar.im.core.services.FileProgressRequestBody;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.other.CacheDataType;
import com.qunar.im.protobuf.Event.QtalkEvent;
import com.qunar.im.protobuf.common.CurrentPreference;
import com.qunar.im.protobuf.common.ProtoMessageOuterClass;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.ui.presenter.IAddEmojiconPresenter;
import com.qunar.im.ui.presenter.IChatExtendMsg;
import com.qunar.im.ui.presenter.IChatingPresenter;
import com.qunar.im.ui.presenter.ISnapPresenter;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;
import com.qunar.im.ui.util.easyphoto.easyphotos.utils.PhotoUtil;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by xinbo.wang on 2016/6/22.
 */
public abstract class ChatPresenter implements IChatingPresenter, ISnapPresenter, IAddEmojiconPresenter, IChatExtendMsg, IMNotificaitonCenter.NotificationCenterDelegate {
    protected static final String TAG = ChatPresenter.class.getSimpleName();
    protected static final int MAX_UNREAD_MSG_LOAD_COUNT = 2000;
    protected IChatView chatView;
    //核心连接管理类
    protected ConnectionUtil connectionUtil;

    protected long historyTime = System.currentTimeMillis();
    protected boolean snapStatus;
    protected long latestTypingTime = 0;
    protected int curMsgNum = 0;
    protected int numPerPage = 20;
    protected boolean isFromChatRoom = false;

    protected CommonDialog.Builder commonDialog;

    public void onEvent(EventBusEvent.UpdateVoiceMessage updateVoiceMessage) {
        if (updateVoiceMessage != null && updateVoiceMessage.message != null) {
            String body = updateVoiceMessage.message.getBody();
            if (!TextUtils.isEmpty(body)) {
                TransitSoundJSON extJson = JsonUtils.getGson().fromJson(body, TransitSoundJSON.class);
                extJson.s = TransitSoundJSON.PLAYED;
                body = JsonUtils.getGson().toJson(extJson);
                updateVoiceMessage.message.setBody(body);
            }
        }
    }

    public void updateVoiceMessage(IMMessage message) {
        if (message != null) {
            String body = message.getBody();
            if (!TextUtils.isEmpty(body)) {
                TransitSoundJSON extJson = JsonUtils.getGson().fromJson(body, TransitSoundJSON.class);
                extJson.s = TransitSoundJSON.PLAYED;
                body = JsonUtils.getGson().toJson(extJson);
                message.setBody(body);
                chatView.replaceItem(message);
                connectionUtil.updateVoiceMessage(message);
//                messageRecordDataModel.insertMessage(updateVoiceMessage.message);
            }
        }
    }


    public void onEvent(EventBusEvent.UpdateFileMessage updateFileMessage) {

    }

    @Override
    public void setView(IChatView view) {
        chatView = view;
        connectionUtil = ConnectionUtil.getInstance();
        isFromChatRoom = chatView.isFromChatRoom();
        //挂载事件消息
        //挂载二人消息
        addEvent();
//        chatView.getContext()
//        EventBus.getDefault().register(this);
    }

    @Override
    public void sendMsg() {
        String msg = chatView.getInputMsg();
        String refenceString = chatView.getRefenceString();//引用消息内容
        //qchat 二人会话 众包参数处理
        String s = addParams2Url(msg);
        if (!TextUtils.isEmpty(refenceString)) {
            msg = refenceString + ChatTextHelper.textToHTML(s);
        } else {
            msg = ChatTextHelper.textToHTML(s);
        }
        send2Server(msg);
        latestTypingTime = 0;

    }

    protected abstract IMMessage send2Server(String msg);

    protected abstract String addParams2Url(String msg);

    protected void showUnReadCount() {
        //TODO 可能有泄漏不可预知风险 暂时注释（会话左上角暂不刷新未读数）
//        int total = connectionUtil.SelectUnReadCount();
//        chatView.showUnReadCount(total);
    }

    protected EmoticonEntity getEmotinEntry(Map<String, String> map) {
        String value = map.get("value");

        if (TextUtils.isEmpty(value)) {
            return null;
        }
        String ext = map.get("extra");
        String pkgId = "";
        if (ext != null && ext.contains("width")) {
            String[] str = ext.trim().split("\\s+");
            if (str.length > 1) {
                //处理width = 240.000000　问题
                pkgId = str[0].substring(str[0].indexOf("width") + 6);
            }
        }
        String shortcut = value.substring(1, value.length() - 1);
        EmoticonEntity emotionEntry = EmotionUtils.getEmoticionByShortCut(shortcut, pkgId, true);
        emotionEntry.pkgId = pkgId;
        return emotionEntry;
    }

    //生成 emoji showall为0 的extendinfo
    protected String getEmojiExtendInfo(EmoticonEntity emoticonEntity) {
        ExtendEmoImgInfo emoImgInfo = new ExtendEmoImgInfo();
        emoImgInfo.setUrl("");
        emoImgInfo.setPkgid(emoticonEntity.pkgId);
        emoImgInfo.setShortcut(emoticonEntity.shortCut);
        emoImgInfo.setWidth(0);
        emoImgInfo.setHeight(0);
        return JsonUtils.getGson().toJson(emoImgInfo);
    }

    //msgtype == 3 extendinfo
    protected String getImgExtendInfo(String url, int width, int height) {
        ExtendEmoImgInfo emoImgInfo = new ExtendEmoImgInfo();
        emoImgInfo.setUrl(url);
        emoImgInfo.setPkgid("");
        emoImgInfo.setShortcut("");
        emoImgInfo.setWidth(width);
        emoImgInfo.setHeight(height);
        return JsonUtils.getGson().toJson(emoImgInfo);
    }

    @Override
    public void checkAlipayAccount() {
        connectionUtil.checkAlipayAccount();
    }

    @Override
    public void close() {
        removeEvent();
    }

    @Override
    public void sendImage() {
        final IMMessage message = generateIMMessage();
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
        uploadAndSendImage(message, chatView.getUploadImg());
    }

    private void sendImageMessage(final IMMessage message, final String imgurl) {
        DispatchHelper.Async("sendImageMessage", new Runnable() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(imgurl)) return;
                    final String toid = chatView.getToId();

                    File imageFile = Glide.with(chatView.getContext())
                            .load(new MyGlideUrl(imgurl))
//                            .load(imgurl)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    BitmapFactory.Options option = ImageUtils.getImageSize(imageFile.getPath());
                    final int width = option.outWidth;
                    final int height = option.outHeight;

                    final PbImageMessageQueue.ImgMsgPacket packet = new PbImageMessageQueue.ImgMsgPacket();
                    packet.key = toid;
                    if (PbImageMessageQueue.packetMap.containsKey(toid)) {
                        PbImageMessageQueue.ImgMsgPacket header = PbImageMessageQueue.packetMap.get(toid);
                        while (header.next != null) {
                            header = header.next;
                        }
                        header.next = packet;
                    } else {
                        packet.isFirst = true;
                        PbImageMessageQueue.packetMap.put(packet.key, packet);
                    }

                    String origal = ChatTextHelper.textToImgHtml(imgurl, width, height);
                    message.setBody(origal);
                    message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);

                    HttpUtil.addEncryptMessageInfo(chatView.getToId(), message, ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);

                    packet.message = message;
//                    packet.bodyExtension = bodyExtension;
                    packet.approveSend();
                    if (snapStatus) {
                        HttpUtil.handleSnapMessage(message);
                    }
                    chatView.setNewMsg2DialogueRegion(message);
                    curMsgNum++;

                    IMDatabaseManager.getInstance().InsertChatMessage(message, false);
                    IMDatabaseManager.getInstance().InsertIMSessionList(message, false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void uploadAndSendImage(final IMMessage message, String filePath) {
        if (filePath.startsWith("http")) {
            //图片url直接发送
            sendImageMessage(message, filePath);
            return;
        }
        final String toid = chatView.getToId();
        final File origalFile = new File(filePath);

        BitmapFactory.Options option = ImageUtils.getImageSize(origalFile.getPath());
        final int width = option.outWidth;
        final int height = option.outHeight;

        final String img = ChatTextHelper.textToImgHtml("file://" + origalFile.getAbsolutePath(), width, height);
        DataCenter.localImageMessagePath.put(message.getMessageId(), img);
        message.setBody(img);
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
        if (snapStatus) {
            HttpUtil.handleSnapMessage(message);
        }
        HttpUtil.addEncryptMessageInfo(chatView.getToId(), message, ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
        chatView.setNewMsg2DialogueRegion(message);
        curMsgNum++;

        IMDatabaseManager.getInstance().InsertChatMessage(message, false);
        IMDatabaseManager.getInstance().InsertIMSessionList(message, false);

        final PbImageMessageQueue.ImgMsgPacket packet = new PbImageMessageQueue.ImgMsgPacket();
        packet.key = toid;
        if (PbImageMessageQueue.packetMap.containsKey(toid)) {
            PbImageMessageQueue.ImgMsgPacket header = PbImageMessageQueue.packetMap.get(toid);
            while (header.next != null) {
                header = header.next;
            }
            header.next = packet;
        } else {
            packet.isFirst = true;
            PbImageMessageQueue.packetMap.put(packet.key, packet);
        }
//        updateDbOnSuccess(message, true);
        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = origalFile.getPath();
        request.FileType = UploadImageRequest.IMAGE;
        request.id = message.getId();
        request.progressRequestListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
//                callback.updataProgress((int) (bytesWritten * 100 / contentLength), done);
                message.setProgress((int) (bytesWritten * 100 / contentLength));
//                if (done) {//不能根据这个判断是否消息发送成功 不准确
//                    message.setMessageState(MessageStatus.LOCAL_STATUS_SUCCESS);
//                }
                chatView.updateUploadProgress(message, (int) (bytesWritten * 100 / contentLength), done);
            }
        };
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {

                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    Logger.i("上传图片成功  msg url = " + result.httpUrl);
//                    IMMessage newMsg = BeanCloneUtil.cloneTo(message);
                    File file = MyDiskCache.getFile(QtalkStringUtils.addFilePathDomain(
                            result.httpUrl, true));
                    FileUtils.copy(origalFile, file);
                    String origal = ChatTextHelper.textToImgHtml(result.httpUrl, width, height);
                    message.setBody(origal);
                    message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
//                    message.setExt(getImgExtendInfo(result.httpUrl,width,height));

                    if (snapStatus) {
                        HttpUtil.handleSnapMessage(message);
                    }
                    HttpUtil.addEncryptMessageInfo(toid, message, ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE);
                    packet.message = message;
//                    packet.bodyExtension = bodyExtension;
                    packet.approveSend();
                } else {
                    Logger.i("上传图片失败  msg url = " + message.getId());
                    packet.removed();
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
                }
            }

            @Override
            public void onError(String msg) {
                Logger.i("上传图片失败  msg url = " + msg);
                packet.removed();
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                InternDatas.sendingLine.remove(message.getId());
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    protected abstract void handleSnapMessage(IMMessage message);

    protected abstract IMMessage generateIMMessage();

    @Override
    public void sendFile(final String file) {

        final String fileName = file.substring(file.lastIndexOf("/") + 1);
        final IMMessage message = generateIMMessage();
        message.setBody("发送文件:" + fileName);

        String fileSize = FileUtils.getFormatFileSize(file);
        TransitFileJSON json = new TransitFileJSON(file, fileName, fileSize, message.getId(), "");
        message.setBody(JsonUtils.getGson().toJson(json));
        message.setExt(JsonUtils.getGson().toJson(json));
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE);
//        final BodyExtension bodyExtension = new BodyExtension();
        uploadAndSendFile(message, file, fileName);
    }

    private void uploadAndSendFile(final IMMessage message, final String file, final String fileName) {
        if (snapStatus) {
            HttpUtil.handleSnapMessage(message);
        }
        HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE);

        chatView.setNewMsg2DialogueRegion(message);
        curMsgNum++;

        IMDatabaseManager.getInstance().InsertChatMessage(message, false);
        IMDatabaseManager.getInstance().InsertIMSessionList(message, false);

        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = file;
        request.id = message.getId();
        request.FileType = UploadImageRequest.FILE;
        request.progressRequestListener = (bytesWritten, contentLength, done) -> {
//                callback.updataProgress((int) (bytesWritten * 100 / contentLength), done);
            message.setProgress((int) (bytesWritten * 100 / contentLength));
//                if (done) {
//                    message.setMessageState(MessageStatus.LOCAL_STATUS_SUCCESS);
//                }
            chatView.updateUploadProgress(message, (int) (bytesWritten * 100 / contentLength), done);
        };
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    Logger.i("上传文件成功  msg url = " + result.httpUrl);
                    Uri uri = Uri.parse(result.httpUrl);
                    String fileMd5 = uri.getLastPathSegment();
                    if (fileMd5 != null && fileMd5.lastIndexOf(".") != -1) {//含后缀的md5需要截取
                        fileMd5 = fileMd5.substring(0, fileMd5.lastIndexOf("."));
                    }
                    String fileSize = FileUtils.getFormatFileSize(file);
                    TransitFileJSON json = new TransitFileJSON(result.httpUrl, fileName, fileSize, message.getId(), (fileMd5 == null ? "" : fileMd5));
                    json.LocalFile = file;
                    message.setBody(JsonUtils.getGson().toJson(json));
                    message.setExt(JsonUtils.getGson().toJson(json));
                    message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE);

                    if (snapStatus) {
                        HttpUtil.handleSnapMessage(message);
                    }

                    HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE);

                    if (isFromChatRoom) {

                        ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
                    } else {

                        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
                    }

                } else {
                    Logger.i("上传文件失败  msg = " + message.toString());
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
                }
            }

            @Override
            public void onError(String msg) {
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                InternDatas.sendingLine.remove(message.getId());
                Logger.i("上传文件失败  msg = " + msg);
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    protected abstract void updateDbOnSuccess(IMMessage message, boolean updateRc);

    /**
     * 上传视频的第一帧
     */
    @Override
    public void sendVideo(String file) {
        IMMessage message = generateIMMessage();
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);

//        uploadAndSendVideo(message, file);
        videoCheckAndSend(message, file);
    }

    @Override
    public void sendVideo(VideoMessageResult videoMessageResult) {
        IMMessage message = generateIMMessage();
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
        String jsonVideo = JsonUtils.getGson().toJson(videoMessageResult);
        message.setBody(jsonVideo);
        message.setExt(jsonVideo);
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);

        if (snapStatus) {
            HttpUtil.handleSnapMessage(message);
        }
        HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
        if (isFromChatRoom) {
            ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
        } else {
            ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
        }
    }


//    private void newUploadAndSendVideo(final IMMessage message, final String file){
//        final String firstFramPath = FileUtils.getFristFrameOfFile(file);
//
//    }

    private void uploadAndSendVideo(final IMMessage message, final String file) {
        final UploadImageRequest request = new UploadImageRequest();
        final String firstFramPath = FileUtils.getFristFrameOfFile(file);

        if (!TextUtils.isEmpty(firstFramPath)) {
            //获取video信息展示在页面，防止失败无法展示消息
            BitmapFactory.Options option = ImageUtils.getImageSize(firstFramPath);
            final int width = option.outWidth;
            final int height = option.outHeight;

            final String fileName = file.substring(file.lastIndexOf("/") + 1);
            final VideoMessageResult videoInfo = MessageUtils.getBasicVideoInfo(file);
            videoInfo.FileName = fileName;
            videoInfo.ThumbUrl = firstFramPath;
            videoInfo.FileUrl = file;
            videoInfo.Width = String.valueOf(width);
            videoInfo.Height = String.valueOf(height);

            message.setBody(firstFramPath);
            message.setExt(JsonUtils.getGson().toJson(videoInfo));
            message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
            if (snapStatus) {
                HttpUtil.handleSnapMessage(message);
            }
            HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
            //发送消息，更新页面
            chatView.setNewMsg2DialogueRegion(message);
            curMsgNum++;

            if (snapStatus) {
                HttpUtil.handleSnapMessage(message);
            }

            IMDatabaseManager.getInstance().InsertChatMessage(message, false);
            IMDatabaseManager.getInstance().InsertIMSessionList(message, false);

            request.filePath = firstFramPath;
            request.FileType = UploadImageRequest.IMAGE;
            request.id = message.getId();
            request.requestComplete = new IUploadRequestComplete() {
                @Override
                public void onRequestComplete(String id, UploadImageResult result) {
                    if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                        Logger.i("上传视频截图成功  msg url = " + result.httpUrl);
                        File targetFile = MyDiskCache.getFile(QtalkStringUtils.addFilePathDomain(result.httpUrl, true));
                        File sourceFile = new File(firstFramPath);
                        FileUtils.copy(sourceFile, targetFile);
                        sendVideoFile(file, message, result.httpUrl, width, height, isFromChatRoom);
                    } else {
                        Logger.i("上传视频第一帧失败  msg id = " + id);
                        message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                        IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    }
                }

                @Override
                public void onError(String msg) {
                    Logger.i("上传视频第一帧失败  msg url = " + msg);
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
//                    updateDbOnSuccess(message, false);
                }
            };
            CommonUploader.getInstance().setUploadImageRequest(request);
        } else {
            File f = new File(file);
            if (f.exists()) {
                sendVideoFile(file, message, "", 0, 0, isFromChatRoom);
            }
        }
    }

    /**
     * 上传视频的文件本身
     */
    protected void sendVideoFile(final String sourceFilePath, final IMMessage message, final String frameUrl, int videoW, int videoH, final boolean isFromChatRoom) {
        final String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1);
        final VideoMessageResult videoInfo = MessageUtils.getBasicVideoInfo(sourceFilePath);
        videoInfo.FileName = fileName;
        videoInfo.ThumbUrl = frameUrl;
        videoInfo.FileUrl = sourceFilePath;
        videoInfo.Height = String.valueOf(videoH);
        videoInfo.Width = String.valueOf(videoW);

        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = sourceFilePath;
        request.FileType = UploadImageRequest.FILE;
        request.id = message.getId();
        request.progressRequestListener = new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
//                callback.updataProgress((int) (bytesWritten / contentLength), done);
                chatView.updateUploadProgress(message, (int) (bytesWritten / contentLength), done);
            }
        };
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    Logger.i("上传视频文件成功  msg url = " + result.httpUrl);
                    videoInfo.FileUrl = result.httpUrl;

                    String jsonVideo = JsonUtils.getGson().toJson(videoInfo);
                    message.setBody(jsonVideo);
                    message.setExt(jsonVideo);
                    message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);

                    if (snapStatus) {
                        HttpUtil.handleSnapMessage(message);
                    }
                    HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
                    if (isFromChatRoom) {
                        ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
                    } else {
                        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
                    }
                } else {
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
                }
            }

            @Override
            public void onError(String msg) {
                Logger.i("上传视频文件失败  msg url = " + msg);
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());

                chatView.setNewMsg2DialogueRegion(message);
                curMsgNum++;

                InternDatas.sendingLine.remove(message.getId());
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }


    private void videoCheckAndSend(IMMessage message, String file) {
        boolean userAble = IMUserDefaults.getStandardUserDefaults().getBooleanValue(CommonConfig.globalContext,
                com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                        + QtalkNavicationService.getInstance().getXmppdomain()
                        + CommonConfig.isDebug
                        + "videoUseAble", false);
        if (userAble) {

                newUploadAndSendVideo(message, file);

        } else {

//                needTran = true;
            uploadAndSendVideo(message, file);


        }
    }

    private void newUploadAndSendVideo(final IMMessage message, final String file) {
        Photo photo = PhotoUtil.getPhoto(file);
        final String firstFramPath = FileUtils.getFristFrameOfFile(file);
        if (!TextUtils.isEmpty(firstFramPath)) {
            //获取video信息展示在页面，防止失败无法展示消息
            BitmapFactory.Options option = ImageUtils.getImageSize(firstFramPath);
            final int width = option.outWidth;
            final int height = option.outHeight;

            final String fileName = file.substring(file.lastIndexOf("/") + 1);
            final VideoMessageResult videoInfo = MessageUtils.getBasicVideoInfo(file);
            videoInfo.FileName = fileName;
            videoInfo.ThumbUrl = firstFramPath;
            videoInfo.FileUrl = file;
            videoInfo.Width = String.valueOf(width);
            videoInfo.Height = String.valueOf(height);
            videoInfo.LocalVideoOutPath = file;
            videoInfo.newVideo = true;

            message.setBody(firstFramPath);
            message.setExt(JsonUtils.getGson().toJson(videoInfo));
            message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
            if (snapStatus) {
                HttpUtil.handleSnapMessage(message);
            }
            HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
            //发送消息，更新页面
            chatView.setNewMsg2DialogueRegion(message);
            curMsgNum++;

            if (snapStatus) {
                HttpUtil.handleSnapMessage(message);
            }

            IMDatabaseManager.getInstance().InsertChatMessage(message, false);
            IMDatabaseManager.getInstance().InsertIMSessionList(message, false);
            boolean needTran = false;
            String time = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                    com.qunar.im.protobuf.common.CurrentPreference.getInstance().getUserid()
                            + QtalkNavicationService.getInstance().getXmppdomain()
                            + CommonConfig.isDebug
                            + "videoTime");
            if (TextUtils.isEmpty(time)) {
                time = (16 * 1000) + "";
            }
            if (photo.duration > Long.parseLong(time)) {
                needTran = false;
            } else {
                needTran = true;
            }
            boolean finalNeedTran = needTran;
            HttpUtil.videoCheckAndUpload(file, needTran, new FileProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
//                    Logger.i("对话框上传视频:bytesWritten:"+bytesWritten+",contentLength:"+contentLength+",done:"+done);
//                    chatView.updateUploadProgress(message, (int) (bytesWritten / contentLength), done);
                    if (done) {
                        Logger.i("对话框上传视频:bytesWritten:" + bytesWritten + ",contentLength:" + contentLength + ",done:" + done);
                    }
                }
            }, new ProtocolCallback.UnitCallback<VideoDataResponse>() {
                @Override
                public void onCompleted(VideoDataResponse videoDataResponse) {
                    if (videoDataResponse.getData().isReady()) {
                        Logger.i("新版上传视频文件成功  msg url = " + videoDataResponse.getData().getTransUrl());

                        if (finalNeedTran) {
                            videoInfo.FileUrl = videoDataResponse.getData().getTransUrl();
//                            videoInfo.ThumbUrl = videoDataResponse.getData().getFirstThumbUrl();
                            videoInfo.fileMd5 = videoDataResponse.getData().getTransFileMd5();
                            videoInfo.FileSize = String.valueOf(videoDataResponse.getData().getTransFileInfo().getVideoSize());
                            videoInfo.Duration = (videoDataResponse.getData().getTransFileInfo().getDuration() / 1000) + "";
                            videoInfo.FileName = videoDataResponse.getData().getTransFilename();
                            videoInfo.Height = videoDataResponse.getData().getTransFileInfo().getHeight();
                            videoInfo.Width = videoDataResponse.getData().getTransFileInfo().getWidth();
                            videoInfo.newVideo = true;
                        } else {
                            videoInfo.FileUrl = videoDataResponse.getData().getOriginUrl();

                            videoInfo.fileMd5 = videoDataResponse.getData().getOriginFileMd5();
                            videoInfo.FileSize = String.valueOf(videoDataResponse.getData().getOriginFileInfo().getVideoSize());

                            videoInfo.Duration = (videoDataResponse.getData().getOriginFileInfo().getDuration() / 1000) + "";
                            videoInfo.FileName = videoDataResponse.getData().getOriginFilename();
                            videoInfo.Height = videoDataResponse.getData().getOriginFileInfo().getHeight();
                            videoInfo.Width = videoDataResponse.getData().getOriginFileInfo().getWidth();
                            videoInfo.newVideo = false;
                        }
                        videoInfo.ThumbName = videoDataResponse.getData().getFirstThumb();
                        videoInfo.ThumbUrl = videoDataResponse.getData().getFirstThumbUrl();
                        videoInfo.LocalVideoOutPath = file;
                        String jsonVideo = JsonUtils.getGson().toJson(videoInfo);
                        message.setBody(jsonVideo);
                        message.setExt(jsonVideo);
                        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);

                        if (snapStatus) {
                            HttpUtil.handleSnapMessage(message);
                        }
                        HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE);
                        if (isFromChatRoom) {
                            ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
                        } else {
                            ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
                        }


                    } else {
                        Logger.i("新版上传视频失败1");
                        message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                        IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                        IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    }
                }

                @Override
                public void onFailure(String errMsg) {
                    Logger.i("新版上传视频失败2:" + errMsg);
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                }
            });
        } else {
            chatView.showToast("不可发布");
        }
    }


    @Override
    public void reset() {
        curMsgNum = 0;
    }

    @Override
    public void transferMessage() {
        IMMessage newMsg = generateIMMessage();
        List<IMMessage> selMessages = chatView.getSelMessages();
        if (selMessages == null || selMessages.size() == 0) return;
        IMMessage originMsg = selMessages.get(0);
        String toId = chatView.getTransferId();
        newMsg.setToID(toId);
        newMsg.setBody(originMsg.getBody());
        newMsg.setDirection(IMMessage.DIRECTION_SEND);
        newMsg.setConversationID(toId);
        newMsg.setMsgType(originMsg.getMsgType());
        newMsg.setExt(originMsg.getExt());

        if (toId.contains("@conference")) {
            newMsg.setType(ConversitionType.MSG_TYPE_GROUP);
            connectionUtil.sendGroupTextOrEmojiMessage(newMsg);
        } else {
            newMsg.setType(ConversitionType.MSG_TYPE_CHAT);
            connectionUtil.sendTextOrEmojiMessage(newMsg);
            newMsg.setFromID(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getPreferenceUserId()));
        }

        if (chatView.getToId().equals(chatView.getTransferId())) {
            curMsgNum++;
            chatView.setNewMsg2DialogueRegion(newMsg);
        }
    }

    @Override
    public void deleteMessge() {
        List<IMMessage> messages = chatView.getSelMessages();
        if (messages == null || messages.size() == 0) return;
        for (IMMessage message : messages) {
            chatView.deleteItem(message);
            connectionUtil.deleteMessage(message);
//            messageRecordDataModel.delSingleMessage(message, chatView.getToId());
            curMsgNum--;
        }
//        reloadMessages();
    }


    @Override
    public void resendMessage() {
        List<IMMessage> selMessages = chatView.getSelMessages();
        if (selMessages == null || selMessages.size() == 0) return;
        final IMMessage message = selMessages.get(0);

        if (message.getMessageState() == 0) {
            message.setTime(new Timestamp(System.currentTimeMillis()));
            message.setMessageState(MessageStatus.LOCAL_STATUS_PROCESSION);
            message.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);

            IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
            chatView.deleteItem(message);

            chatView.setNewMsg2DialogueRegion(message);
//            final BodyExtension bodyExtension = new BodyExtension();

            switch (message.getMsgType()) {
                case ProtoMessageOuterClass.MessageType.MessageTypePhoto_VALUE:
                    //重新上传图片
                    List<Map<String, String>> list = ChatTextHelper.getObjList(message.getBody());
                    String path = list.get(0).get("value").replaceFirst("file:\\/\\/", "");

                    uploadAndSendImage(message, path);

//                    HttpUtil.uploadAndSendImage(message,path, chatView.getToId(), new HttpUtil.SendCallback() {
//                        @Override
//                        public void send() {
//                            chatView.setNewMsg2DialogueRegion(message);
//                        }
//
//                        @Override
//                        public void updataProgress(int progress, boolean isDone) {
//                            message.setProgress(progress);
//                            if(isDone){
//                                message.setReadState(MessageStatus.STATUS_SUCCESS);
//                            }
//                            chatView.updateUploadProgress(message, progress, isDone);
//                        }
//
//                    });
                    break;
                case ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE:
                    final TransitFileJSON jsonObject = JsonUtils.getGson().fromJson(message.getExt(), TransitFileJSON.class);
                    //上传文件并发送
                    if (jsonObject == null) return;
                    uploadAndSendFile(message, jsonObject.HttpUrl, jsonObject.FileName);
//                    HttpUtil.uploadAndSendFile(message, jsonObject.HttpUrl, jsonObject.FileName, (message.getType() == ConversitionType.MSG_TYPE_GROUP), new HttpUtil.SendCallback() {
//                        @Override
//                        public void send() {
//
//                        }
//                        @Override
//                        public void updataProgress(int progress, boolean isDone) {
//                            message.setProgress(progress);
//                            if(isDone){
//                                message.setReadState(MessageStatus.STATUS_SUCCESS);
//                            }
//                            chatView.updateUploadProgress(message, progress, isDone);
//                        }
//                    });
                    break;
                case ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE:
//                    String file = message.getBody();
                    final VideoMessageResult videoMessageResult = JsonUtils.getGson().fromJson(message.getExt(), VideoMessageResult.class);
                    //上传并发送视频
                    videoCheckAndSend(message, videoMessageResult.FileUrl);
//                    HttpUtil.uploadAndSendVideo(videoMessageResult.FileUrl, message, new HttpUtil.SendCallback() {
//                        @Override
//                        public void send() {
//                            chatView.setNewMsg2DialogueRegion(message);
//                            curMsgNum++;
//                        }
//
//                        @Override
//                        public void updataProgress(int progress, boolean isDone) {
//                            chatView.updateUploadProgress(message, progress, isDone);
//                        }
//                    }, chatView.isFromChatRoom());
                    break;
                case ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE:
                    TransitSoundJSON json = JsonUtils.getGson().fromJson(message.getBody(), TransitSoundJSON.class);
                    uploadAndSendVoice(message, json);
//                    HttpUtil.uploadAndSendVoice(message, json.FilePath, json, isFromChatRoom);
                    break;
                case ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE:
                    if (chatView.isFromChatRoom()) {
                        connectionUtil.sendGroupTextOrEmojiMessage(message);
                    } else {
                        connectionUtil.sendTextOrEmojiMessage(message);
                    }
                    break;
                default:
                    if (chatView.isFromChatRoom()) {
                        connectionUtil.sendGroupTextOrEmojiMessage(message);
                    } else {
                        connectionUtil.sendTextOrEmojiMessage(message);
                    }
                    break;
            }


        }
    }

    @Override
    public void sendLocation(QunarLocation location) {

        //我在这里，点击查看：http://api.map.baidu.com/marker?location=39.985460,116.312695&title=我的位置&content=中国北京市海淀区海淀街道苏州街&output=html
        StringBuilder sb = new StringBuilder();
        sb.append("我在这里，点击查看：");
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://api.map.baidu.com/marker?location=");
        urlBuilder.append(location.latitude);
        urlBuilder.append(",");
        urlBuilder.append(location.longitude);
        urlBuilder.append("&title=我的位置&content=");
        urlBuilder.append(location.addressDesc);
        urlBuilder.append("&output=html");
        String urlString = ChatTextHelper.textToUrl(urlBuilder.toString());
        sb.append(urlString);
        sb.append(location.addressDesc);
        final ShareLocation jsonLocation = new ShareLocation();
        jsonLocation.latitude = Double.toString(location.latitude);
        jsonLocation.longitude = Double.toString(location.longitude);
        jsonLocation.adress = location.addressDesc;
        jsonLocation.fileUrl = "file://" + location.fileUrl;
        jsonLocation.name = location.name;
        String extInfo = JsonUtils.getGson().toJson(jsonLocation);
        final IMMessage message = generateIMMessage();
        message.setBody(sb.toString());
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE);
        message.setReadState(MessageStatus.REMOTE_STATUS_CHAT_SUCCESS);
        message.setExt(extInfo);

        HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE);

        curMsgNum++;
        if (snapStatus) {
            HttpUtil.handleSnapMessage(message);
        }
        chatView.setNewMsg2DialogueRegion(message);

        IMDatabaseManager.getInstance().InsertChatMessage(message, false);
        IMDatabaseManager.getInstance().InsertIMSessionList(message, false);

//        updateDbOnSuccess(message, true);
        final PbImageMessageQueue.ImgMsgPacket packet = new PbImageMessageQueue.ImgMsgPacket();
        packet.key = chatView.getToId();
        if (PbImageMessageQueue.packetMap.containsKey(chatView.getToId())) {
            PbImageMessageQueue.ImgMsgPacket header = PbImageMessageQueue.packetMap.get(chatView.getToId());
            while (header.next != null) {
                header = header.next;
            }
            header.next = packet;
        } else {
            packet.isFirst = true;
            PbImageMessageQueue.packetMap.put(packet.key, packet);
        }
        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = location.fileUrl;
        request.id = message.getId();
        request.FileType = UploadImageRequest.IMAGE;
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    File file = MyDiskCache.getFile(
                            QtalkStringUtils.addFilePathDomain(result.httpUrl,
                                    true));
                    File originFile = new File(jsonLocation.fileUrl);
                    FileUtils.copy(originFile, file);
                    originFile.delete();
                    jsonLocation.fileUrl = result.httpUrl;
                    String extInfo = JsonUtils.getGson().toJson(jsonLocation);
//                    bodyExtension.setExtendInfo(extInfo);
                    message.setExt(extInfo);
                    message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE);

                    if (snapStatus) {
                        HttpUtil.handleSnapMessage(message);
                    }
                    HttpUtil.addEncryptMessageInfo(chatView.getToId(), message, ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE);

                    packet.message = message;
//                    packet.bodyExtension = bodyExtension;
                    packet.approveSend();
//                    if (!sendMessage(message, bodyExtension)) {
//                        message.setReadState(MessageStatus.STATUS_FAILED);
//                        InternDatas.sendingLine.remove(message.getId());
//                        updateDbOnSuccess(message, false);
//                    }
                } else {
                    packet.removed();
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    InternDatas.sendingLine.remove(message.getId());
//                    updateDbOnSuccess(message, false);
                }
            }

            @Override
            public void onError(String msg) {
                packet.removed();
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                InternDatas.sendingLine.remove(message.getId());
//                updateDbOnSuccess(message, false);
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    @Override
    public void hongBaoMessage(HongbaoContent content) {
        String ext = JsonUtils.getGson().toJson(content);
        IMMessage message = generateIMMessage();
        message.setBody("[红包] 请升级最新版本查看此消息");
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeRedPack_VALUE);
        message.setExt(ext);
//        BodyExtension bodyExtension = new BodyExtension();
//        bodyExtension.setId(message.getId());
//        bodyExtension.setMsgType(String.valueOf(MessageType.MSG_HONGBAO_MESSAGE));
//        bodyExtension.setMaType(MachineType.MachineTypeAndroid);
//        bodyExtension.setExtendInfo(ext);
//        updateDbOnSuccess(message, true);
//        if (!sendMessage(message, bodyExtension)) {
//            message.setReadState(MessageStatus.STATUS_FAILED);
//            InternDatas.sendingLine.remove(message.getId());
//            updateDbOnSuccess(message, false);
//        }
        connectionUtil.sendTextOrEmojiMessage(message);
        chatView.setNewMsg2DialogueRegion(message);
    }


    @Override
    public void sendVoiceMessage(String voicePath, final int duration) {
        File file = new File(voicePath);
        String fileName = UUID.randomUUID().toString() + ".aar";
        final File targetFile = MyDiskCache.getVoiceFile(fileName);
        FileUtils.copy(file, targetFile);

        final TransitSoundJSON json = new TransitSoundJSON("", fileName, duration, voicePath);
        final IMMessage message = generateIMMessage();
        message.setBody(JsonUtils.getGson().toJson(json));
        message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE);

        Logger.d("voicePath:" + voicePath);
//        final BodyExtension bodyExtension = new BodyExtension();
        uploadAndSendVoice(message, json);
    }

    private void uploadAndSendVoice(final IMMessage message, final TransitSoundJSON json) {
        if (snapStatus) {
            HttpUtil.handleSnapMessage(message);
        }
        HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE);
        chatView.setNewMsg2DialogueRegion(message);
        curMsgNum++;
        IMDatabaseManager.getInstance().InsertChatMessage(message, false);
        IMDatabaseManager.getInstance().InsertIMSessionList(message, false);
//        chatView.isFromChatRoom();
        //上传语音文件并发送

//        HttpUtil.uploadAndSendVoice(message, targetFile.getAbsolutePath(), json, isFromChatRoom);

        final UploadImageRequest request = new UploadImageRequest();
        request.filePath = json.FilePath;
        request.FileType = UploadImageRequest.FILE;
        request.id = message.getId();
        request.requestComplete = new IUploadRequestComplete() {
            @Override
            public void onRequestComplete(String id, UploadImageResult result) {
                if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                    Logger.i("上传语音成功  msg url = " + result.httpUrl);
                    json.HttpUrl = result.httpUrl;
                    message.setBody(JsonUtils.getGson().toJson(json));
                    message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE);

                    if (snapStatus) {
                        HttpUtil.handleSnapMessage(message);
                    }
                    HttpUtil.addEncryptMessageInfo(message.getToID(), message, ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE);

                    // TODO: 2017/8/22  InternDatas.sendingLine这个缓存逻辑没细看
                    if (isFromChatRoom) {
                        ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
                    } else {
                        ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
                    }
                } else {
                    Logger.i("上传语音失败  msg id = " + message.getId());
                    message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                    IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                    IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                    InternDatas.sendingLine.remove(message.getId());
                }
            }

            @Override
            public void onError(String msg) {
                Logger.i("上传语音失败  msg id = " + msg);
                message.setMessageState(MessageStatus.LOCAL_STATUS_FAILED);
                IMDatabaseManager.getInstance().UpdateChatStateMessage(message, false);
                IMNotificaitonCenter.getInstance().postMainThreadNotificationName(QtalkEvent.Chat_Message_Send_Failed, message.getMessageId());
                InternDatas.sendingLine.remove(message.getId());
            }
        };
        CommonUploader.getInstance().setUploadImageRequest(request);
    }

    @Override
    public void changeSnapStatus(boolean status) {
        snapStatus = status;
    }

    String fileName = "";

    @Override
    public void addEmojicon() {

        final String url = chatView.getUploadImg();
        if (TextUtils.isEmpty(url)) {
            chatView.isEmotionAdd(false);
            return;
        }
        final UserConfigData userConfigData = new UserConfigData();
        userConfigData.setKey(CacheDataType.kCollectionCacheKey);

        userConfigData.setValue(url);
        userConfigData.setIsdel(CacheDataType.Y);
        userConfigData.setType(CacheDataType.set);

        try {
            URL u = new URL(url);
            String path = u.getPath();
            fileName = path.substring(path.lastIndexOf(File.separator) + 1);
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            userConfigData.setSubkey(fileName);

            UserConfigData cache = IMDatabaseManager.getInstance().selectUserConfigValueForKey(userConfigData);
            if (cache != null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpUtil.setUserConfig(userConfigData, new ProtocolCallback.UnitCallback<NewRemoteConfig>() {
            @Override
            public void onCompleted(NewRemoteConfig newRemoteConfig) {
                if (newRemoteConfig.getData().getClientConfigInfos().size() > 0) {
                    ConnectionUtil.getInstance().refreshTheConfig(newRemoteConfig);
                    chatView.isEmotionAdd(true);
                } else {
                    chatView.isEmotionAdd(false);
                }
            }

            @Override
            public void onFailure(String errMsg) {
                chatView.isEmotionAdd(false);
            }
        });
    }


    private void handleMyEmotion(RemoteConfig.ConfigItem item, String httpUrl, String fileName) {
        if (!TextUtils.isEmpty(item.value) && !TextUtils.isEmpty(fileName)) {
            //表情存储命名，区分用户，域名domain，导航配置
            File dir = EmotionUtils.getFavorEmoticonFileDir();
            fileName = fileName.replaceAll(File.separator, "");
            File file = new File(dir, fileName);
            if (file == null || file.exists()) {
                return;
            }
            DownloadRequest downloadProcess = new DownloadRequest();
            downloadProcess.url = QtalkStringUtils.addFilePathDomain(httpUrl, true);
            downloadProcess.savePath = file.getAbsolutePath();
            downloadProcess.requestComplete = new IDownloadRequestComplete() {
                @Override
                public void onRequestComplete(DownloadImageResult result) {
                    chatView.isEmotionAdd(true);
                }
            };
            CommonDownloader.getInsatnce().setDownloadRequest(downloadProcess);
            IMDatabaseManager.getInstance().updateCollectEmoConfig(item.value);
        }
    }

    @Override
    public void revoke() {
        List<IMMessage> selMessages = chatView.getSelMessages();
        if (selMessages == null || selMessages.size() == 0) return;
        IMMessage message = selMessages.get(0);
        if (System.currentTimeMillis() - message.getTime().getTime() > 1000 * 60 * 2) {
            IMMessage prompt = new IMMessage();
            prompt.setId(UUID.randomUUID().toString());
            prompt.setDirection(IMMessage.DIRECTION_MIDDLE);
            prompt.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE);
            prompt.setBody("超过2分钟的消息不可以撤销");
            chatView.setNewMsg2DialogueRegion(prompt);
        } else {
            if (TextUtils.isEmpty(message.getId())) {
                return;
            }
            IMMessage message1 = generateIMMessage();
            message1.setId(message.getId());

            if ("4".equals(chatView.getChatType()) || "5".equals(chatView.getChatType())) {
                message1.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeConsultRevoke_VALUE);
            } else {
                message1.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE);
            }
            message1.setType(ConversitionType.MSG_TYPE_REVOKE);
//            message1.setFromID(message.getFromID());
//            message1.setToID(message.getToID());
            message1.setNick(message.getNick());
            message1.setDirection(IMMessage.DIRECTION_MIDDLE);
            message1.setBody("{\"messageId\":\"" + message.getId() +
                    "\",\"message\":\"revoke a message\",\"fromId\":\"" +
                    chatView.getFromId() + "\"}");
            connectionUtil.sendRevokeMessage(message1);
            //把消息更新一下
            if ("4".equals(chatView.getChatType()) || "5".equals(chatView.getChatType())) {
                message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeConsultRevoke_VALUE);
            } else {
                message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeRevoke_VALUE);
            }
            message.setType(ConversitionType.MSG_TYPE_REVOKE);
            message.setBody(message.getNick().getName() + "撤回了一条消息");
            chatView.refreshDataset();
        }
    }

    @Override
    public void sendExtendMessage(ExtendMessageEntity entity) {
        String msg = entity.linkurl;
        if (!TextUtils.isEmpty(msg)) {
            String extStr = JsonUtils.getGson().toJson(entity);
            msg = ChatTextHelper.textToUrl(msg);
            IMMessage message = generateIMMessage();
            message.setBody(msg);
            message.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeCommonTrdInfo_VALUE);
            message.setExt(extStr);

//            BodyExtension bodyExtension = new BodyExtension();
//            bodyExtension.setId(message.getId());
//            bodyExtension.setMsgType(String.valueOf(MessageType.EXTEND_MSG));
//            bodyExtension.setMaType(MachineType.MachineTypeAndroid);
//            bodyExtension.setExtendInfo(extStr);

            curMsgNum++;

            if (isFromChatRoom) {
                ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(message);
            } else {
                ConnectionUtil.getInstance().sendTextOrEmojiMessage(message);
            }
//            updateDbOnSuccess(message, true);
//            if (!sendMessage(message, bodyExtension)) {
//                message.setReadState(MessageStatus.STATUS_FAILED);
//                InternDatas.sendingLine.remove(message.getId());
//                updateDbOnSuccess(message, false);
//            }
            chatView.setNewMsg2DialogueRegion(message);
        }
    }


    @Override
    public void sendInviteMsg() {

    }

    public void shareMessage(List<IMMessage> shareMsgs) {
        if (shareMsgs != null && shareMsgs.size() > 0) {
            final IMMessage newMsg = generateIMMessage();
            List<ShareMessageEntity> shareMessageEntities = new ArrayList<>();
            final String toId = chatView.getTransferId();
            for (IMMessage message : shareMsgs) {
                ShareMessageEntity messageEntity = new ShareMessageEntity();
                switch (message.getMsgType()) {
                    case ProtoMessageOuterClass.MessageType.MessageTypeText_VALUE:
                    case ProtoMessageOuterClass.MessageType.MessageTypeFile_VALUE:
                    case ProtoMessageOuterClass.MessageType.MessageTypeVoice_VALUE:
                        messageEntity.b = message.getBody();
                        break;
                    case ProtoMessageOuterClass.MessageType.MessageTypeSmallVideo_VALUE:
                    case ProtoMessageOuterClass.MessageType.MessageTypeCommonTrdInfo_VALUE:
                    case ProtoMessageOuterClass.MessageType.MessageTypeLocalShare_VALUE:
                        messageEntity.b = message.getExt();
                        break;
                    default:
                        messageEntity.b = message.getBody();
                        break;
                }
                messageEntity.t = message.getMsgType();
                messageEntity.s = message.getTime().getTime();
                messageEntity.d = message.getDirection() == IMMessage.DIRECTION_SEND ? 1 : 2;
//                messageEntity.n = ProfileUtils.getNickByKey(QtalkStringUtils.parseResource(message.getFromID()));
//                if (toId.contains("@conference")) {
//                    messageEntity.n = connectionUtil.getMucNickById(message.getFromID()).getName();
//                } else {
//                    messageEntity.n = connectionUtil.getNickById(message.getFromID()).getName();
//                }
                messageEntity.n = connectionUtil.getNickById(message.getFromID()).getName();
                shareMessageEntities.add(messageEntity);
            }
            final File file = new File(QunarIMApp.getContext().getFilesDir(), "curr_share_msg.json");
            FileUtils.writeToFile(JsonUtils.getGson().toJson(shareMessageEntities), file, true);
            UploadImageRequest request = new UploadImageRequest();
            request.filePath = file.getPath();
            request.FileType = UploadImageRequest.FILE;
            request.id = newMsg.getId();
            request.requestComplete = new IUploadRequestComplete() {
                @Override
                public void onRequestComplete(String id, UploadImageResult result) {
                    file.delete();
                    if (result != null && !TextUtils.isEmpty(result.httpUrl)) {
                        ExtendMessageEntity entity = new ExtendMessageEntity();
                        String base64Url = Base64.encodeToString(QtalkStringUtils.addFilePathDomain(result.httpUrl, true).getBytes(), Base64.NO_WRAP);
                        entity.auth = true;
                        entity.showbar = true;
                        if (chatView.getToId().contains("@conference")) {
                            entity.title = connectionUtil.getMucNickById(chatView.getToId()).getName() + "的聊天记录";
                        } else {
                            entity.title = connectionUtil.getNickById(chatView.getToId()).getName() + "的聊天记录";
                        }
//                        entity.title = "分享消息";
                        entity.desc = "点击查看全部";
                        String shareUrl = QtalkNavicationService.getInstance().getShareurl();
                        String params = "jdata=" + URLEncoder.encode(base64Url);
                        if (shareUrl != null && shareUrl.indexOf("?") == -1) {
                            entity.linkurl = shareUrl + "?" + params;
                        } else if (shareUrl != null) {
                            entity.linkurl = shareUrl + "&" + params;
                        } else {
                            entity.linkurl = "";
                        }
                        String extJson = JsonUtils.getGson().toJson(entity);
                        newMsg.setToID(toId);
                        newMsg.setBody(entity.linkurl);
                        newMsg.setDirection(IMMessage.DIRECTION_SEND);
                        newMsg.setConversationID(toId);
                        newMsg.setMsgType(ProtoMessageOuterClass.MessageType.MessageTypeCommonTrdInfo_VALUE);
                        newMsg.setExt(extJson);

//                        BodyExtension bodyExtension = new BodyExtension();
//                        bodyExtension.setId(newMsg.getId());
//                        bodyExtension.setMsgType(String.valueOf(MessageType.EXTEND_MSG));
//                        bodyExtension.setMaType(MachineType.MachineTypeAndroid);
//                        bodyExtension.setExtendInfo(extJson);
                        boolean sentRe = false;
                        if (toId.contains("@conference")) {
                            newMsg.setType(ConversitionType.MSG_TYPE_GROUP);
                            ConnectionUtil.getInstance().sendGroupTextOrEmojiMessage(newMsg);
                        } else {
                            newMsg.setType(ConversitionType.MSG_TYPE_CHAT);
                            newMsg.setFromID(QtalkStringUtils.userId2Jid(CurrentPreference.getInstance().getUserid()));
                            ConnectionUtil.getInstance().sendTextOrEmojiMessage(newMsg);
                        }
                    }
                }

                @Override
                public void onError(String msg) {
                    file.delete();
                }
            };

            CommonUploader.getInstance().setUploadImageRequest(request);
        }

    }

    @Override
    public void removeEventForSearch() {
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Text);
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Text_After_DB);
        connectionUtil.removeEvent(this, QtalkEvent.Group_Chat_Message_Text);
        connectionUtil.removeEvent(this, QtalkEvent.Group_Chat_Message_Text_After_DB);
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Input);
//        revoke = QtalremoveEvent();hat_Message_Revoke+chatView.getToId();
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Revoke);
        connectionUtil.removeEvent(this, QtalkEvent.LOGIN_EVENT);
        connectionUtil.removeEvent(this, QtalkEvent.Update_Voice_Message);
        connectionUtil.removeEvent(this, QtalkEvent.SEND_PHOTO_AFTER_EDIT);
        connectionUtil.removeEvent(this, QtalkEvent.CHAT_MESSAGE_ENCRYPT);
        connectionUtil.removeEvent(this, QtalkEvent.Remove_Session);
        connectionUtil.removeEvent(this, QtalkEvent.Destory_Muc);
        connectionUtil.removeEvent(this, QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION);
        connectionUtil.removeEvent(this, QtalkEvent.Message_Read_Mark);
        connectionUtil.removeEvent(this, QtalkEvent.Update_ReMind);
        connectionUtil.removeEvent(this, QtalkEvent.SPECIFYNOTICE);
        connectionUtil.removeEvent(this, QtalkEvent.REFRESH_NICK);
        connectionUtil.removeEvent(this, QtalkEvent.CLEAR_MESSAGE);
        connectionUtil.removeEvent(this, QtalkEvent.NOTIFY_RTCMSG);
    }

    @Override
    public void addEventForSearch() {
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Text);
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Text_After_DB);
        connectionUtil.addEvent(this, QtalkEvent.Group_Chat_Message_Text);
        connectionUtil.addEvent(this, QtalkEvent.Group_Chat_Message_Text_After_DB);
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Input);
//        revoke = QtalkEvent.Chat_Message_Revoke+chatView.getToId();
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Revoke);
        connectionUtil.addEvent(this, QtalkEvent.LOGIN_EVENT);
        connectionUtil.addEvent(this, QtalkEvent.Update_Voice_Message);
        connectionUtil.addEvent(this, QtalkEvent.SEND_PHOTO_AFTER_EDIT);
        connectionUtil.addEvent(this, QtalkEvent.CHAT_MESSAGE_ENCRYPT);
        connectionUtil.addEvent(this, QtalkEvent.Remove_Session);
        connectionUtil.addEvent(this, QtalkEvent.Destory_Muc);
        connectionUtil.addEvent(this, QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION);
        connectionUtil.addEvent(this, QtalkEvent.Message_Read_Mark);
        connectionUtil.addEvent(this, QtalkEvent.Update_ReMind);
        connectionUtil.addEvent(this, QtalkEvent.SPECIFYNOTICE);
        connectionUtil.addEvent(this, QtalkEvent.REFRESH_NICK);
        connectionUtil.addEvent(this, QtalkEvent.CLEAR_MESSAGE);
        connectionUtil.addEvent(this, QtalkEvent.NOTIFY_RTCMSG);
    }

    public void addEvent() {
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Text);
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Text_After_DB);
        connectionUtil.addEvent(this, QtalkEvent.Group_Chat_Message_Text);
        connectionUtil.addEvent(this, QtalkEvent.Group_Chat_Message_Text_After_DB);
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Input);
//        revoke = QtalkEvent.Chat_Message_Revoke+chatView.getToId();
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Revoke);
        connectionUtil.addEvent(this, QtalkEvent.LOGIN_EVENT);
        connectionUtil.addEvent(this, QtalkEvent.Update_Voice_Message);
        connectionUtil.addEvent(this, QtalkEvent.SEND_PHOTO_AFTER_EDIT);
        connectionUtil.addEvent(this, QtalkEvent.CHAT_MESSAGE_ENCRYPT);
        connectionUtil.addEvent(this, QtalkEvent.Remove_Session);
        connectionUtil.addEvent(this, QtalkEvent.Destory_Muc);
        connectionUtil.addEvent(this, QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION);
        connectionUtil.addEvent(this, QtalkEvent.Message_Read_Mark);
        connectionUtil.addEvent(this, QtalkEvent.Update_ReMind);
        connectionUtil.addEvent(this, QtalkEvent.SPECIFYNOTICE);
        connectionUtil.addEvent(this, QtalkEvent.REFRESH_NICK);
        connectionUtil.addEvent(this, QtalkEvent.CLEAR_MESSAGE);
        connectionUtil.addEvent(this, QtalkEvent.NOTIFY_RTCMSG);
        connectionUtil.addEvent(this, QtalkEvent.Chat_Message_Read_State);

        connectionUtil.addEvent(this, QtalkEvent.PAY_FAIL);
        connectionUtil.addEvent(this, QtalkEvent.PAY_AUTH);
        connectionUtil.addEvent(this, QtalkEvent.PAY_ORDER);
        connectionUtil.addEvent(this, QtalkEvent.PAY_RED_ENVELOP_CHOICE);
        connectionUtil.addEvent(this, QtalkEvent.SEND_MESSAGE_RENDER);
    }

    @Override
    public void removeEvent() {

        connectionUtil.removeEvent(this, QtalkEvent.Group_Chat_Message_Text);
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Text);
        connectionUtil.removeEvent(this, QtalkEvent.Group_Chat_Message_Text_After_DB);
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Text_After_DB);
//        connectionUtil.workworldremoveEvent(this, chatView.getToId());
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Input);
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Revoke);
        connectionUtil.removeEvent(this, QtalkEvent.LOGIN_EVENT);
        connectionUtil.removeEvent(this, QtalkEvent.Update_Voice_Message);
        connectionUtil.removeEvent(this, QtalkEvent.SEND_PHOTO_AFTER_EDIT);
        connectionUtil.removeEvent(this, QtalkEvent.CHAT_MESSAGE_ENCRYPT);
        connectionUtil.removeEvent(this, QtalkEvent.Remove_Session);
        connectionUtil.removeEvent(this, QtalkEvent.Destory_Muc);
        connectionUtil.removeEvent(this, QtalkEvent.CHAT_MESSAGE_SUBSCRIPTION);
        connectionUtil.removeEvent(this, QtalkEvent.Message_Read_Mark);
        connectionUtil.removeEvent(this, QtalkEvent.Update_ReMind);
        connectionUtil.removeEvent(this, QtalkEvent.SPECIFYNOTICE);
        connectionUtil.removeEvent(this, QtalkEvent.REFRESH_NICK);
        connectionUtil.removeEvent(this, QtalkEvent.CLEAR_MESSAGE);
        connectionUtil.removeEvent(this, QtalkEvent.NOTIFY_RTCMSG);
        connectionUtil.removeEvent(this, QtalkEvent.Chat_Message_Read_State);
        connectionUtil.removeEvent(this, QtalkEvent.SEND_MESSAGE_RENDER);

        connectionUtil.removeEvent(this, QtalkEvent.PAY_FAIL);
        connectionUtil.removeEvent(this, QtalkEvent.PAY_AUTH);
        connectionUtil.removeEvent(this, QtalkEvent.PAY_ORDER);
        connectionUtil.removeEvent(this, QtalkEvent.PAY_RED_ENVELOP_CHOICE);
    }

    @Override
    public void sendSyncConversation() {
        String userid = CurrentPreference.getInstance().getPreferenceUserId();
        SyncConversation syncConversation = new SyncConversation();
        syncConversation.id = chatView.getToId();
        syncConversation.timestamp = System.currentTimeMillis();
        if ("4".equals(chatView.getChatType()) || "5".equals(chatView.getChatType())) {
            syncConversation.realjid = chatView.getRealJid();
            syncConversation.qchatid = chatView.getChatType();
            syncConversation.type = "consult ";
        } else if (chatView.isFromChatRoom()) {
            syncConversation.type = "groupchat";
        } else {
            syncConversation.type = "chat";
        }
        connectionUtil.conversationSynchronizationMessage(JsonUtils.getGson().toJson(syncConversation), userid, userid);

    }
}
